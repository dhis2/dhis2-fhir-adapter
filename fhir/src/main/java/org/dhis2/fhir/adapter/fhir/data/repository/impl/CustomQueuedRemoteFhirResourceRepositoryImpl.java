package org.dhis2.fhir.adapter.fhir.data.repository.impl;

/*
 * Copyright (c) 2004-2018, University of Oslo
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

import org.dhis2.fhir.adapter.fhir.data.model.QueuedRemoteSubscriptionRequest;
import org.dhis2.fhir.adapter.fhir.data.repository.AlreadyQueuedException;
import org.dhis2.fhir.adapter.fhir.data.repository.CustomQueuedRemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.data.repository.IgnoredSubscriptionResourceException;
import org.dhis2.fhir.adapter.util.SqlExceptionUtils;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

/**
 * Implementation of {@link CustomQueuedRemoteFhirResourceRepository}.
 *
 * @author volsch
 */
public class CustomQueuedRemoteFhirResourceRepositoryImpl implements CustomQueuedRemoteFhirResourceRepository
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @PersistenceContext
    private EntityManager entityManager;

    private PlatformTransactionManager platformTransactionManager;

    private PersistenceExceptionTranslator persistenceExceptionTranslator;

    public CustomQueuedRemoteFhirResourceRepositoryImpl( @Nonnull EntityManager entityManager,
        @Nonnull PlatformTransactionManager platformTransactionManager, @Nonnull @Qualifier( "&entityManagerFactory" ) PersistenceExceptionTranslator persistenceExceptionTranslator )
    {
        this.entityManager = entityManager;
        this.platformTransactionManager = platformTransactionManager;
        this.persistenceExceptionTranslator = persistenceExceptionTranslator;
    }

    @Transactional( rollbackFor = AlreadyQueuedException.class )
    @Override
    public void enqueue( @Nonnull UUID subscriptionResourceId, @Nonnull String fhirResourceId, @Nonnull String requestId ) throws AlreadyQueuedException
    {
        final Query query = entityManager.createNativeQuery( "INSERT INTO fhir_queued_remote_resource(remote_subscription_resource_id,fhir_resource_id,request_id,queued_at) " +
            "VALUES (:subscriptionResourceId,:fhirResourceId,:requestId,:queuedAt)" )
            .setParameter( "subscriptionResourceId", subscriptionResourceId ).setParameter( "fhirResourceId", fhirResourceId )
            .setParameter( "requestId", requestId ).setParameter( "queuedAt", Instant.now() );
        // avoid invalidation of complete 2nd level cache
        query.unwrap( NativeQuery.class ).addSynchronizedEntityClass( QueuedRemoteSubscriptionRequest.class );

        try
        {
            query.executeUpdate();
        }
        catch ( PersistenceException e )
        {
            final RuntimeException runtimeException = DataAccessUtils.translateIfNecessary( e, persistenceExceptionTranslator );
            if ( runtimeException instanceof DataIntegrityViolationException )
            {
                final DataIntegrityViolationException dataIntegrityViolationException =
                    (DataIntegrityViolationException) runtimeException;
                if ( SqlExceptionUtils.isUniqueKeyViolation( dataIntegrityViolationException.getMostSpecificCause() ) )
                {
                    throw new AlreadyQueuedException();
                }
                if ( SqlExceptionUtils.isForeignKeyViolation( dataIntegrityViolationException.getMostSpecificCause() ) )
                {
                    logger.error( "Could not process enqueue request for subscription resource {} and FHIR resource {} due to constraint violation: {}",
                        subscriptionResourceId, fhirResourceId, e.getCause().getMessage() );
                    throw new IgnoredSubscriptionResourceException( "Subscription resource " + subscriptionResourceId + " does no longer exist.", e );
                }
            }
            throw runtimeException;
        }
    }

    @Override
    public boolean dequeued( @Nonnull UUID subscriptionResourceId, @Nonnull String fhirResourceId )
    {
        // First an enqueue must be tried. There may still be a pending not committed enqueue.
        // This must be deleted. The pending enqueue will block this enqueue until it has been committed.
        TransactionStatus transactionStatus = platformTransactionManager
            .getTransaction( new DefaultTransactionDefinition( PROPAGATION_REQUIRES_NEW ) );
        try
        {
            enqueue( subscriptionResourceId, fhirResourceId, "?" );
        }
        catch ( AlreadyQueuedException e )
        {
            // can be ignored
        }
        finally
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

        transactionStatus = platformTransactionManager
            .getTransaction( new DefaultTransactionDefinition() );
        try
        {
            final Query query = entityManager.createQuery( "DELETE FROM QueuedRemoteFhirResource " +
                "WHERE id.remoteSubscriptionResource.id=:subscriptionResourceId AND id.fhirResourceId=:fhirResourceId" )
                .setParameter( "subscriptionResourceId", subscriptionResourceId ).setParameter( "fhirResourceId", fhirResourceId );
            return (query.executeUpdate() > 0);
        }
        finally
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
}
