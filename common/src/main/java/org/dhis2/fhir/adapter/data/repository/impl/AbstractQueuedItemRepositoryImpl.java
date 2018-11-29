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
import org.dhis2.fhir.adapter.data.model.QueuedItem;
import org.dhis2.fhir.adapter.data.model.QueuedItemId;
import org.dhis2.fhir.adapter.data.repository.AlreadyQueuedException;
import org.dhis2.fhir.adapter.data.repository.IgnoredQueuedItemException;
import org.dhis2.fhir.adapter.data.repository.QueuedItemRepository;
import org.dhis2.fhir.adapter.util.SqlExceptionUtils;
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
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW;

/**
 * Abstract implementation that enqueues and dequeues items.
 *
 * @param <T> the concrete type of the queued item.
 * @param <G> the concrete type of the data group.
 * @param <I> the ID class of the queued items.
 * @author volsch
 */
public abstract class AbstractQueuedItemRepositoryImpl<T extends QueuedItem<I, G>, I extends QueuedItemId<G>, G extends DataGroup> implements QueuedItemRepository<I, G>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    @PersistenceContext
    private EntityManager entityManager;

    private PlatformTransactionManager platformTransactionManager;

    private PersistenceExceptionTranslator persistenceExceptionTranslator;

    protected AbstractQueuedItemRepositoryImpl( @Nonnull EntityManager entityManager,
        @Nonnull PlatformTransactionManager platformTransactionManager,
        @Nonnull @Qualifier( "&entityManagerFactory" ) PersistenceExceptionTranslator persistenceExceptionTranslator )
    {
        this.entityManager = entityManager;
        this.platformTransactionManager = platformTransactionManager;
        this.persistenceExceptionTranslator = persistenceExceptionTranslator;
    }

    @Transactional( rollbackFor = AlreadyQueuedException.class )
    public void enqueue( @Nonnull I id ) throws AlreadyQueuedException
    {
        try
        {
            entityManager.persist( createQueuedItem( id ) );
            entityManager.flush();
        }
        catch ( EntityNotFoundException e )
        {
            logger.error( "Could not process enqueue request for {} due to constraint violation: {}", id, e.getCause().getMessage() );
            throw new IgnoredQueuedItemException( "Queued item " + id + " does no longer exist.", e );
        }
        catch ( PersistenceException e )
        {
            final RuntimeException runtimeException = DataAccessUtils.translateIfNecessary(
                e, persistenceExceptionTranslator );
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
                    logger.error( "Could not process enqueue request for {} due to constraint violation: {}",
                        id, e.getCause().getMessage() );
                    throw new IgnoredQueuedItemException( "Queued item " + id + " does no longer exist.", e );
                }
            }
            throw runtimeException;
        }
    }

    public boolean dequeued( @Nonnull I id )
    {
        // First an enqueue must be tried. There may still be a pending not committed enqueue.
        // This must be deleted. The pending enqueue will block this enqueue until it has been committed.
        TransactionStatus transactionStatus = platformTransactionManager
            .getTransaction( new DefaultTransactionDefinition( PROPAGATION_REQUIRES_NEW ) );
        try
        {
            enqueue( id );
        }
        catch ( AlreadyQueuedException e )
        {
            // can be ignored
        }
        finally
        {
            finalizeTransaction( transactionStatus );
        }

        transactionStatus = platformTransactionManager.getTransaction( new DefaultTransactionDefinition() );
        try
        {
            final T reference = entityManager.getReference( getQueuedItemClass(), id );
            entityManager.remove( reference );
            entityManager.flush();
        }
        catch ( EntityNotFoundException e )
        {
            return false;
        }
        finally
        {
            finalizeTransaction( transactionStatus );
        }
        return true;
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

    @Nonnull
    protected EntityManager getEntityManager()
    {
        return entityManager;
    }

    @Nonnull
    protected abstract Class<T> getQueuedItemClass();

    @Nonnull
    protected abstract T createQueuedItem( @Nonnull I id );
}
