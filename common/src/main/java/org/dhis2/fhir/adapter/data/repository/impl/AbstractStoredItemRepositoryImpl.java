package org.dhis2.fhir.adapter.data.repository.impl;

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

import org.dhis2.fhir.adapter.data.model.DataGroup;
import org.dhis2.fhir.adapter.data.model.StoredItem;
import org.dhis2.fhir.adapter.data.model.StoredItemId;
import org.dhis2.fhir.adapter.data.repository.StoredItemRepository;
import org.dhis2.fhir.adapter.util.SqlExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of a repository that stores already stored items.
 *
 * @param <T> the concrete type of the stored item.
 * @param <I> the concrete type of the ID of the stored item.
 * @param <G> the group of the ID that is constant for a specific use case.
 * @author volsch
 */
public abstract class AbstractStoredItemRepositoryImpl<T extends StoredItem<I, G>, I extends StoredItemId<G>, G extends DataGroup> implements StoredItemRepository<T, I, G>
{
    @PersistenceContext
    private EntityManager entityManager;

    private final PlatformTransactionManager platformTransactionManager;

    private final PersistenceExceptionTranslator persistenceExceptionTranslator;

    protected AbstractStoredItemRepositoryImpl( @Nonnull EntityManager entityManager, @Nonnull PlatformTransactionManager platformTransactionManager, @Nonnull PersistenceExceptionTranslator persistenceExceptionTranslator )
    {
        this.entityManager = entityManager;
        this.platformTransactionManager = platformTransactionManager;
        this.persistenceExceptionTranslator = persistenceExceptionTranslator;
    }

    @Override
    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    public boolean stored( @Nonnull G prefix, @Nonnull String storedId )
    {
        final TransactionStatus transactionStatus = platformTransactionManager.getTransaction( new DefaultTransactionDefinition() );
        try
        {
            entityManager.persist( createStoredItem( prefix, storedId ) );
            entityManager.flush();
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
                    return false;
                }
            }
            throw runtimeException;
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
        return true;
    }

    @Override
    public boolean contains( @Nonnull G prefix, @Nonnull String storedId )
    {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaDelete<T> criteriaDelete = cb.createCriteriaDelete( getStoredItemClass() );
        final Root<T> root = criteriaDelete.from( getStoredItemClass() );
        return !entityManager.createQuery( criteriaDelete.where(
            cb.equal( root.get( "id" ).get( "group" ), prefix ),
            cb.equal( root.get( "id" ).get( "storedId" ), storedId ) ) )
            .getResultList().isEmpty();
    }

    @Override
    @Nonnull
    public Set<String> findProcessedIds( @Nonnull G prefix, @Nonnull Collection<String> processedIds )
    {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<String> criteria = cb.createQuery( String.class );
        final Root<T> root = criteria.from( getStoredItemClass() );
        return new HashSet<>( entityManager.createQuery( criteria.select( root.get( "id" ).get( "storedId" ) )
            .where( cb.equal( root.get( "id" ).get( "group" ), prefix ),
                root.get( "id" ).get( "storedId" ).in( processedIds ) ) )
            .setHint( "org.hibernate.fetchSize", 1000 ).getResultList() );
    }

    @Override
    @Transactional
    public int deleteOldest( @Nonnull G prefix, @Nonnull Instant timestamp )
    {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaDelete<T> criteriaDelete = cb.createCriteriaDelete( getStoredItemClass() );
        final Root<T> root = criteriaDelete.from( getStoredItemClass() );
        return entityManager.createQuery( criteriaDelete.where(
            cb.equal( root.get( "id" ).get( "group" ), prefix ),
            cb.lessThan( root.get( "storedAt" ), timestamp ) ) ).executeUpdate();
    }

    @Nonnull
    protected abstract Class<T> getStoredItemClass();

    @Nonnull
    protected abstract T createStoredItem( @Nonnull G prefix, @Nonnull String storedId );
}
