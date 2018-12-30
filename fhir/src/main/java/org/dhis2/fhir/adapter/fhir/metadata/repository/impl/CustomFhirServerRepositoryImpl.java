package org.dhis2.fhir.adapter.fhir.metadata.repository.impl;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServer;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerResourceUpdate;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CustomFhirServerRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.event.AutoCreatedFhirServerResourceEvent;
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
 * Implementation of {@link CustomFhirServerRepository}.
 *
 * @author volsch
 */
public class CustomFhirServerRepositoryImpl implements CustomFhirServerRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    private ApplicationEventPublisher applicationEventPublisher;

    public CustomFhirServerRepositoryImpl( @Nonnull EntityManager entityManager, @Nonnull ApplicationEventPublisher applicationEventPublisher )
    {
        this.entityManager = entityManager;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Nonnull
    @Cacheable( key = "{#root.methodName}", cacheManager = "metadataCacheManager", cacheNames = "fhirServer" )
    public Optional<FhirServer> findOnly()
    {
        final List<FhirServer> result = entityManager.createNamedQuery( FhirServer.ALL_REMOTE_SUBSCRIPTIONS_NAMED_QUERY, FhirServer.class )
            .setMaxResults( 2 ).getResultList();
        if ( (result.isEmpty()) || (result.size() > 1) )
        {
            return Optional.empty();
        }
        return Optional.of( result.get( 0 ) );
    }

    @Override
    @Nonnull
    @CachePut( key = "#a0.id", cacheManager = "metadataCacheManager", cacheNames = "fhirServer" )
    @CacheEvict( allEntries = true, cacheManager = "metadataCacheManager", cacheNames = "fhirServerResource" )
    @Transactional
    public <S extends FhirServer> S save( @Nonnull S entity )
    {
        final List<FhirServerResource> autoCreatedSubscriptionResources;
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
            autoCreatedSubscriptionResources.forEach( r -> applicationEventPublisher.publishEvent( new AutoCreatedFhirServerResourceEvent( r ) ) );
        }

        return result;
    }

    @Override
    @Nonnull
    @CachePut( key = "#a0.id", cacheManager = "metadataCacheManager", cacheNames = "fhirServer" )
    @CacheEvict( allEntries = true, cacheManager = "metadataCacheManager", cacheNames = "fhirServerResource" )
    @Transactional
    public <S extends FhirServer> S saveAndFlush( @Nonnull S entity )
    {
        final S result = save( entity );
        entityManager.flush();
        return result;
    }

    protected List<FhirServerResource> createAutoCreatedSubscriptionResources( @Nonnull FhirServer fhirServer )
    {
        final List<FhirServerResource> autoCreatedFhirServerResources = new ArrayList<>();
        if ( fhirServer.getAutoCreatedSubscriptionResources() != null )
        {
            for ( final FhirResourceType resourceType : fhirServer.getAutoCreatedSubscriptionResources() )
            {
                if ( resourceType != null )
                {
                    if ( fhirServer.getResources() == null )
                    {
                        fhirServer.setResources( new ArrayList<>() );
                    }
                    if ( fhirServer.getResources().stream().noneMatch( r -> resourceType.equals( r.getFhirResourceType() ) ) )
                    {
                        final FhirServerResource rsr = new FhirServerResource();
                        rsr.setFhirServer( fhirServer );
                        rsr.setFhirResourceType( resourceType );
                        rsr.setDescription( "Automatically created subscription for FHIR Resource " + resourceType.getResourceTypeName() + "." );
                        fhirServer.getResources().add( rsr );

                        final FhirServerResourceUpdate resourceUpdate = new FhirServerResourceUpdate( Instant.now() );
                        resourceUpdate.setGroup( rsr );
                        rsr.setResourceUpdate( resourceUpdate );

                        autoCreatedFhirServerResources.add( rsr );
                    }
                }
            }
        }
        return autoCreatedFhirServerResources;
    }
}
