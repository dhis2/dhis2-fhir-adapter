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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.fhir.data.model.FhirDhisAssignment;
import org.dhis2.fhir.adapter.fhir.data.repository.CustomFhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.util.SqlExceptionUtils;
import org.hl7.fhir.instance.model.api.IIdType;
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
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.time.Instant;

/**
 * Implementation of {@link CustomFhirDhisAssignmentRepository}.
 *
 * @author volsch
 */
public class CustomFhirDhisAssignmentRepositoryImpl implements CustomFhirDhisAssignmentRepository
{
    private final PlatformTransactionManager platformTransactionManager;

    @PersistenceContext
    private final EntityManager entityManager;

    private final PersistenceExceptionTranslator persistenceExceptionTranslator;

    public CustomFhirDhisAssignmentRepositoryImpl( @Nonnull PlatformTransactionManager platformTransactionManager,
        @Nonnull EntityManager entityManager, @Nonnull @Qualifier( "&entityManagerFactory" ) PersistenceExceptionTranslator persistenceExceptionTranslator )
    {
        this.platformTransactionManager = platformTransactionManager;
        this.entityManager = entityManager;
        this.persistenceExceptionTranslator = persistenceExceptionTranslator;
    }

    @Nullable
    @Override
    public String findFirstDhisResourceId( @Nonnull AbstractRule rule, @Nonnull FhirClient subscription, @Nonnull IIdType fhirResourceId )
    {
        return findFirstDhisResourceId( rule, subscription, fhirResourceId, false );
    }

    protected String findFirstDhisResourceId( @Nonnull AbstractRule rule, @Nonnull FhirClient subscription, @Nonnull IIdType fhirResourceId, boolean locked )
    {
        return entityManager.createNamedQuery( FhirDhisAssignment.FIND_FIRST_RULED_ID_BY_FHIR_NAMED_QUERY, String.class )
            .setLockMode( locked ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE )
            .setParameter( "ruleId", rule.getId() ).setParameter( "subscriptionId", subscription.getId() )
            .setParameter( "fhirResourceId", fhirResourceId.getIdPart() ).getResultList().stream().findFirst().orElse( null );
    }

    @Nullable
    @Override
    public String findFirstDhisResourceId( @Nonnull FhirClient fhirClient, @Nonnull IIdType fhirResourceId )
    {
        return entityManager.createNamedQuery( FhirDhisAssignment.FIND_FIRST_ID_BY_FHIR_NAMED_QUERY, String.class )
            .setParameter( "fhirClientId", fhirClient.getId() ).setParameter( "fhirResourceId", fhirResourceId.getIdPart() ).getResultList().stream().findFirst().orElse( null );
    }

    @Nullable
    @Override
    public String findFirstFhirResourceId( @Nonnull AbstractRule rule, @Nonnull FhirClient subscription, @Nonnull DhisResourceId dhisResourceId )
    {
        return entityManager.createNamedQuery( FhirDhisAssignment.FIND_FIRST_RULED_ID_BY_DHIS_NAMED_QUERY, String.class )
            .setParameter( "ruleId", rule.getId() ).setParameter( "subscriptionId", subscription.getId() )
            .setParameter( "dhisResourceId", dhisResourceId.getId() ).getResultList().stream().findFirst().orElse( null );
    }

    @Nullable
    @Override
    public String findFirstFhirResourceId( @Nonnull FhirClient fhirClient, @Nonnull DhisResourceId dhisResourceId )
    {
        return entityManager.createNamedQuery( FhirDhisAssignment.FIND_FIRST_ID_BY_DHIS_NAMED_QUERY, String.class )
            .setParameter( "fhirClientId", fhirClient.getId() ).setParameter( "dhisResourceId", dhisResourceId.getId() ).getResultList().stream().findFirst().orElse( null );
    }

    @Override
    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    public boolean saveDhisResourceId( @Nonnull AbstractRule rule, @Nonnull FhirClient fhirClient, @Nonnull IIdType fhirResourceId, @Nonnull DhisResourceId dhisResourceId )
    {
        boolean updated = false;
        final TransactionStatus transactionStatus = platformTransactionManager.getTransaction( new DefaultTransactionDefinition() );
        try
        {
            final String existingId = findFirstDhisResourceId( rule, fhirClient, fhirResourceId, true );
            if ( existingId == null )
            {
                updated = persist( rule, fhirClient, fhirResourceId, dhisResourceId );
            }
            else if ( !existingId.equals( dhisResourceId.getId() ) )
            {
                final FhirDhisAssignment assignment = entityManager.createNamedQuery( FhirDhisAssignment.FIND_FIRST_BY_FHIR_NAMED_QUERY, FhirDhisAssignment.class )
                    .setParameter( "ruleId", rule.getId() ).setParameter( "subscriptionId", fhirClient.getId() )
                    .setParameter( "fhirResourceId", fhirResourceId.getIdPart() ).setLockMode( LockModeType.PESSIMISTIC_WRITE ).getSingleResult();
                assignment.setDhisResourceId( dhisResourceId.getId() );
                updated = true;
            }
        }
        finally
        {
            finalizeTransaction( transactionStatus );
        }
        return updated;
    }

    @Override
    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    public boolean saveFhirResourceId( @Nonnull AbstractRule rule, @Nonnull FhirClient subscription, @Nonnull DhisResourceId dhisResourceId, @Nonnull IIdType fhirResourceId )
    {
        boolean updated = false;
        final TransactionStatus transactionStatus = platformTransactionManager.getTransaction( new DefaultTransactionDefinition() );
        try
        {
            final String existingId = findFirstFhirResourceId( rule, subscription, dhisResourceId );
            if ( existingId == null )
            {
                updated = persist( rule, subscription, fhirResourceId, dhisResourceId );
            }
            else if ( !existingId.equals( fhirResourceId.getIdPart() ) )
            {
                final FhirDhisAssignment assignment = entityManager.createNamedQuery( FhirDhisAssignment.FIND_FIRST_BY_DHIS_NAMED_QUERY, FhirDhisAssignment.class )
                    .setParameter( "ruleId", rule.getId() ).setParameter( "subscriptionId", subscription.getId() )
                    .setParameter( "dhisResourceId", dhisResourceId.getId() ).setLockMode( LockModeType.PESSIMISTIC_WRITE ).getSingleResult();
                assignment.setFhirResourceId( fhirResourceId.getIdPart() );
                updated = true;
            }
        }
        finally
        {
            finalizeTransaction( transactionStatus );
        }
        return updated;
    }

    @Override
    @Transactional
    public boolean deleteFhirResourceId( @Nonnull AbstractRule rule, @Nonnull FhirClient subscription, @Nonnull IIdType fhirResourceId )
    {
        final FhirDhisAssignment assignment = entityManager.createNamedQuery( FhirDhisAssignment.FIND_FIRST_BY_FHIR_NAMED_QUERY, FhirDhisAssignment.class )
            .setParameter( "ruleId", rule.getId() ).setParameter( "subscriptionId", subscription.getId() )
            .setParameter( "fhirResourceId", fhirResourceId.getIdPart() ).setLockMode( LockModeType.PESSIMISTIC_WRITE ).getResultList().stream().findFirst().orElse( null );
        if ( assignment != null )
        {
            entityManager.remove( assignment );
            return true;
        }
        return false;
    }

    private boolean persist( @Nonnull AbstractRule rule, @Nonnull FhirClient subscription, @Nonnull IIdType fhirResourceId, @Nonnull DhisResourceId dhisResourceId )
    {
        final FhirDhisAssignment assignment = new FhirDhisAssignment();
        assignment.setCreatedAt( Instant.now() );
        assignment.setRule( entityManager.getReference( AbstractRule.class, rule.getId() ) );
        assignment.setFhirClient( entityManager.getReference( FhirClient.class, subscription.getId() ) );
        assignment.setFhirResourceId( fhirResourceId.getIdPart() );
        assignment.setDhisResourceId( dhisResourceId.getId() );

        try
        {
            entityManager.persist( assignment );
            entityManager.flush();
            return true;
        }
        catch ( PersistenceException e )
        {
            final RuntimeException runtimeException = DataAccessUtils.translateIfNecessary(
                e, persistenceExceptionTranslator );
            if ( runtimeException instanceof DataIntegrityViolationException )
            {
                final DataIntegrityViolationException dataIntegrityViolationException =
                    (DataIntegrityViolationException) runtimeException;
                if ( !SqlExceptionUtils.isUniqueKeyViolation( dataIntegrityViolationException.getMostSpecificCause() ) )
                {
                    throw runtimeException;
                }
            }
            else
            {
                throw runtimeException;
            }
        }
        return false;
    }

    private void finalizeTransaction( @Nonnull TransactionStatus transactionStatus )
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
