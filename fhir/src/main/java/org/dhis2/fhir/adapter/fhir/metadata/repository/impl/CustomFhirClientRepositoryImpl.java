package org.dhis2.fhir.adapter.fhir.metadata.repository.impl;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResourceUpdate;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CustomFhirClientRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.event.AutoCreatedFhirClientResourceEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link CustomFhirClientRepository}.
 *
 * @author volsch
 */
public class CustomFhirClientRepositoryImpl implements CustomFhirClientRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    private ApplicationEventPublisher applicationEventPublisher;

    public CustomFhirClientRepositoryImpl( @Nonnull EntityManager entityManager, @Nonnull ApplicationEventPublisher applicationEventPublisher )
    {
        this.entityManager = entityManager;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Nonnull
    @Cacheable( key = "{#root.methodName}", cacheManager = "metadataCacheManager", cacheNames = "fhirClient" )
    public Optional<FhirClient> findOnly()
    {
        final List<FhirClient> result = entityManager.createNamedQuery( FhirClient.ALL_REMOTE_SUBSCRIPTIONS_NAMED_QUERY, FhirClient.class )
            .setMaxResults( 2 ).getResultList();
        if ( (result.isEmpty()) || (result.size() > 1) )
        {
            return Optional.empty();
        }
        return Optional.of( result.get( 0 ) );
    }

    @Override
    @Nonnull
    @CachePut( key = "#a0.id", cacheManager = "metadataCacheManager", cacheNames = "fhirClient" )
    @CacheEvict( allEntries = true, cacheManager = "metadataCacheManager", cacheNames = "fhirClientResource" )
    @Transactional
    public <S extends FhirClient> S save( @Nonnull S entity )
    {
        final List<FhirClientResource> autoCreatedSubscriptionResources;
        S result;
        if ( entity.getVersion() == null )
        {
            autoCreatedSubscriptionResources = createAutoCreatedSubscriptionResources( entity );
            entityManager.persist( entity );
            result = entity;
        }
        else
        {
            result = entityManager.merge( entity );
            autoCreatedSubscriptionResources = createAutoCreatedSubscriptionResources( result );
            if ( !autoCreatedSubscriptionResources.isEmpty() )
            {
                result = entityManager.merge( result );
            }
        }

        if ( !autoCreatedSubscriptionResources.isEmpty() )
        {
            // prevent that transaction may fail afterwards
            entityManager.flush();
            autoCreatedSubscriptionResources.forEach( r -> applicationEventPublisher.publishEvent( new AutoCreatedFhirClientResourceEvent( r ) ) );
        }

        return result;
    }

    @Override
    @Nonnull
    @CachePut( key = "#a0.id", cacheManager = "metadataCacheManager", cacheNames = "fhirClient" )
    @CacheEvict( allEntries = true, cacheManager = "metadataCacheManager", cacheNames = "fhirClientResource" )
    @Transactional
    public <S extends FhirClient> S saveAndFlush( @Nonnull S entity )
    {
        final S result = save( entity );
        entityManager.flush();
        return result;
    }

    protected List<FhirClientResource> createAutoCreatedSubscriptionResources( @Nonnull FhirClient fhirClient )
    {
        final List<FhirClientResource> autoCreatedFhirClientResources = new ArrayList<>();
        if ( fhirClient.getAutoCreatedSubscriptionResources() != null )
        {
            for ( final FhirResourceType resourceType : fhirClient.getAutoCreatedSubscriptionResources() )
            {
                if ( resourceType != null )
                {
                    if ( fhirClient.getResources() == null )
                    {
                        fhirClient.setResources( new ArrayList<>() );
                    }
                    if ( fhirClient.getResources().stream().noneMatch( r -> resourceType.equals( r.getFhirResourceType() ) ) )
                    {
                        final FhirClientResource rsr = new FhirClientResource();
                        rsr.setFhirClient( fhirClient );
                        rsr.setFhirResourceType( resourceType );
                        rsr.setDescription( "Automatically created subscription for FHIR Resource " + resourceType.getResourceTypeName() + "." );
                        fhirClient.getResources().add( rsr );

                        final FhirClientResourceUpdate resourceUpdate = new FhirClientResourceUpdate( Instant.now() );
                        resourceUpdate.setGroup( rsr );
                        rsr.setResourceUpdate( resourceUpdate );

                        autoCreatedFhirClientResources.add( rsr );
                    }
                }
            }
        }
        return autoCreatedFhirClientResources;
    }
}
