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
import org.dhis2.fhir.adapter.fhir.data.repository.CustomQueuedRemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.data.repository.IgnoredSubscriptionResourceException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import java.time.Instant;
import java.util.UUID;

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

    public CustomQueuedRemoteFhirResourceRepositoryImpl( @Nonnull EntityManager entityManager )
    {
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    @SuppressWarnings( "unchecked" )
    public boolean enqueue( @Nonnull UUID subscriptionResourceId, @Nonnull String fhirResourceId, @Nonnull String requestId )
    {
        final Query query = entityManager.createNativeQuery( "INSERT INTO fhir_queued_remote_resource(remote_subscription_resource_id,fhir_resource_id,request_id,queued_at) " +
            "VALUES (:subscriptionResourceId,:fhirResourceId,:requestId,:queuedAt) ON CONFLICT ON CONSTRAINT fhir_queued_remote_resource_pk DO NOTHING RETURNING request_id" )
            .setParameter( "subscriptionResourceId", subscriptionResourceId ).setParameter( "fhirResourceId", fhirResourceId )
            .setParameter( "requestId", requestId ).setParameter( "queuedAt", Instant.now() );
        // avoid invalidation of complete 2nd level cache
        query.unwrap( NativeQuery.class ).addSynchronizedEntityClass( QueuedRemoteSubscriptionRequest.class );

        try
        {
            return query.getResultList().stream().anyMatch( requestId::equals );
        }
        catch ( PersistenceException e )
        {
            if ( e.getCause() instanceof ConstraintViolationException )
            {
                logger.error( "Could not process enqueue request for subscription resource {} and FHIR resource {} due to constraint violation: {}",
                    subscriptionResourceId, fhirResourceId, e.getCause().getMessage() );
                throw new IgnoredSubscriptionResourceException( "Subscription resource " + subscriptionResourceId + " does no longer exist.", e );
            }
            throw e;
        }
    }

    @Transactional
    @Override
    public boolean dequeued( @Nonnull UUID subscriptionResourceId, @Nonnull String fhirResourceId )
    {
        // First an enqueue must be tried. There may still be a pending not committed enqueue.
        // This must be deleted. The pending enqueue will block this enqueue until it has been committed.
        enqueue( subscriptionResourceId, fhirResourceId, "?" );

        final Query query = entityManager.createQuery( "DELETE FROM QueuedRemoteFhirResource " +
            "WHERE id.remoteSubscriptionResource.id=:subscriptionResourceId AND id.fhirResourceId=:fhirResourceId" )
            .setParameter( "subscriptionResourceId", subscriptionResourceId ).setParameter( "fhirResourceId", fhirResourceId );
        return (query.executeUpdate() > 0);
    }
}
