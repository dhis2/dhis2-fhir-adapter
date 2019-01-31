package org.dhis2.fhir.adapter.fhir.data.repository.impl;

/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.dhis2.fhir.adapter.data.repository.TooManyPersistRetriesException;
import org.dhis2.fhir.adapter.fhir.data.model.SubscriptionFhirResource;
import org.dhis2.fhir.adapter.fhir.data.repository.CustomSubscriptionFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerResource;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.util.SqlExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.time.Instant;
import java.util.Optional;

/**
 * Implementation of {@link CustomSubscriptionFhirResourceRepository}.
 *
 * @author volsch
 */
public class CustomSubscriptionFhirResourceRepositoryImpl implements CustomSubscriptionFhirResourceRepository
{
    private final Logger logger = LoggerFactory.getLogger( CustomSubscriptionFhirResourceRepositoryImpl.class );

    protected static final int MAX_TRY_COUNT = 3;

    @PersistenceContext
    private EntityManager entityManager;

    private PlatformTransactionManager platformTransactionManager;

    private PersistenceExceptionTranslator persistenceExceptionTranslator;

    public CustomSubscriptionFhirResourceRepositoryImpl( @Nonnull EntityManager entityManager,
        @Nonnull PlatformTransactionManager platformTransactionManager,
        @Nonnull @Qualifier( "&entityManagerFactory" ) PersistenceExceptionTranslator persistenceExceptionTranslator )
    {
        this.entityManager = entityManager;
        this.platformTransactionManager = platformTransactionManager;
        this.persistenceExceptionTranslator = persistenceExceptionTranslator;
    }

    @Override
    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    public void enqueue( @Nonnull FhirServerResource fhirServerResource, @Nullable String contentType, @Nonnull FhirVersion fhirVersion, @Nonnull String fhirResourceId, @Nonnull String fhirResource )
    {
        enqueue( fhirServerResource, contentType, fhirVersion, fhirResourceId, fhirResource, 1 );
    }

    protected void enqueue( @Nonnull FhirServerResource fhirServerResource, @Nullable String contentType, @Nonnull FhirVersion fhirVersion, @Nonnull String fhirResourceId, @Nonnull String fhirResource, int tryCount )
    {
        SubscriptionFhirResource subscriptionFhirResource = new SubscriptionFhirResource();
        subscriptionFhirResource.setCreatedAt( Instant.now() );
        subscriptionFhirResource.setFhirServerResource( fhirServerResource );
        subscriptionFhirResource.setContentType( contentType );
        subscriptionFhirResource.setFhirVersion( fhirVersion );
        subscriptionFhirResource.setFhirResourceId( fhirResourceId );
        subscriptionFhirResource.setFhirResource( fhirResource );

        TransactionStatus transactionStatus = platformTransactionManager.getTransaction( new DefaultTransactionDefinition() );
        try
        {
            // FHIR server resource may be not a valid entity and must be reloaded (cached before)
            subscriptionFhirResource.setFhirServerResource( entityManager.getReference( FhirServerResource.class, fhirServerResource.getId() ) );

            entityManager.persist( subscriptionFhirResource );
            entityManager.flush();
            return;
        }
        catch ( PersistenceException e )
        {
            // may have been inserted already and not been processed and needs to be updated
            final RuntimeException runtimeException = DataAccessUtils.translateIfNecessary( e, persistenceExceptionTranslator );
            if ( !(runtimeException instanceof DataIntegrityViolationException) ||
                !SqlExceptionUtils.isUniqueKeyViolation( ((DataIntegrityViolationException) runtimeException).getMostSpecificCause() ) )
            {
                throw runtimeException;
            }
            logger.debug( "FHIR Server Resource " + fhirServerResource.getId() + " contains already FHIR Resource " + fhirResourceId, e );
        }
        finally
        {
            completeTransaction( transactionStatus );
        }

        boolean retry = false;
        transactionStatus = platformTransactionManager.getTransaction( new DefaultTransactionDefinition() );
        try
        {
            subscriptionFhirResource = entityManager.createNamedQuery( SubscriptionFhirResource.RESOURCE_NAMED_QUERY, SubscriptionFhirResource.class )
                .setLockMode( LockModeType.PESSIMISTIC_WRITE )
                .setParameter( "fhirServerResource", fhirServerResource )
                .setParameter( "fhirResourceId", fhirResourceId ).getSingleResult();
            subscriptionFhirResource.setCreatedAt( Instant.now() );
            subscriptionFhirResource.setContentType( contentType );
            subscriptionFhirResource.setFhirVersion( fhirVersion );
            subscriptionFhirResource.setFhirResource( fhirResource );
        }
        catch ( NoResultException e )
        {
            // may have been deleted again in the meantime (dequeued)
            retry = true;
        }
        finally
        {
            completeTransaction( transactionStatus );
        }

        if ( retry )
        {
            if ( tryCount + 1 > MAX_TRY_COUNT )
            {
                throw new TooManyPersistRetriesException( "Storing subscription FHIR resource has been retried too many times." );
            }
            logger.debug( "FHIR Server Resource " + fhirServerResource.getId() + " does no longer contain FHIR Resource " + fhirResourceId );
            enqueue( fhirServerResource, contentType, fhirVersion, fhirResourceId, fhirResource, tryCount + 1 );
        }
    }

    @Override
    @Transactional
    public boolean deleteEnqueued( @Nonnull SubscriptionFhirResource subscriptionFhirResource )
    {
        final SubscriptionFhirResource sfr = entityManager.find( SubscriptionFhirResource.class, subscriptionFhirResource.getId(), LockModeType.PESSIMISTIC_WRITE );
        if ( sfr == null )
        {
            return false;
        }
        if ( !sfr.getCreatedAt().equals( subscriptionFhirResource.getCreatedAt() ) )
        {
            // has been updated in the meantime and will be reused for further processing
            return false;
        }
        entityManager.remove( sfr );
        return true;
    }

    @Nonnull
    @Override
    public Optional<SubscriptionFhirResource> findResource( @Nonnull FhirServerResource fhirServerResource, @Nonnull String fhirResourceId )
    {
        try
        {
            return Optional.of( entityManager.createNamedQuery( SubscriptionFhirResource.RESOURCE_NAMED_QUERY, SubscriptionFhirResource.class )
                .setParameter( "fhirServerResource", fhirServerResource )
                .setParameter( "fhirResourceId", fhirResourceId ).getSingleResult() );
        }
        catch ( NoResultException e )
        {
            return Optional.empty();
        }
    }

    private void completeTransaction( @Nonnull TransactionStatus transactionStatus )
    {
        if ( transactionStatus.isRollbackOnly() )
        {
            platformTransactionManager.rollback( transactionStatus );
        }
        else
        {
            platformTransactionManager.commit( transactionStatus );
        }
    }
}
