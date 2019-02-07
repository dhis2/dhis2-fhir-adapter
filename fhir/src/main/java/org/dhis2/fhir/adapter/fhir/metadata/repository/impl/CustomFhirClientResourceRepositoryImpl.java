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

import org.dhis2.fhir.adapter.fhir.metadata.model.AvailableFhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CustomFhirClientResourceRepository;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link CustomFhirClientResourceRepository}.
 *
 * @author volsch
 */
public class CustomFhirClientResourceRepositoryImpl implements CustomFhirClientResourceRepository
{
    @PersistenceContext
    private EntityManager entityManager;

    public CustomFhirClientResourceRepositoryImpl( @Nonnull EntityManager entityManager )
    {
        this.entityManager = entityManager;
    }

    @Nonnull
    @Override
    @Cacheable( key = "#a0", cacheManager = "metadataCacheManager", cacheNames = "fhirClientResource" )
    @Transactional( readOnly = true )
    public Optional<FhirClientResource> findOneByIdCached( @Nonnull UUID id )
    {
        final FhirClientResource rsr = entityManager.find( FhirClientResource.class, id );
        if ( rsr == null )
        {
            return Optional.empty();
        }

        Hibernate.initialize( rsr.getFhirClient().getFhirEndpoint().getHeaders() );
        return Optional.of( rsr );
    }

    @Nonnull
    @Override
    @Cacheable( key = "{#root.methodName, #a0, #a1}", cacheManager = "metadataCacheManager", cacheNames = "fhirClientResource" )
    @Transactional( readOnly = true )
    public Optional<FhirClientResource> findFirstCached( @Nonnull UUID fhirClientId, @Nonnull FhirResourceType fhirResourceType )
    {
        final FhirClientResource rsr = entityManager.createNamedQuery( FhirClientResource.FIND_FIRST_CACHED_NAMED_QUERY, FhirClientResource.class )
            .setParameter( "fhirClientId", fhirClientId )
            .setParameter( "fhirResourceType", fhirResourceType )
            .setMaxResults( 1 ).getResultList().stream().findFirst().orElse( null );
        if ( rsr == null )
        {
            return Optional.empty();
        }

        Hibernate.initialize( rsr.getFhirClient().getFhirEndpoint().getHeaders() );
        return Optional.of( rsr );
    }

    @Nonnull
    @Override
    @Cacheable( key = "{#root.methodName, #a0.id}", cacheManager = "metadataCacheManager", cacheNames = "fhirClientResource" )
    public Collection<AvailableFhirClientResource> findAllAvailable( @Nonnull FhirClient fhirClient )
    {
        final Map<FhirResourceType, AvailableFhirClientResource> result = new HashMap<>();
        entityManager.createNamedQuery( FhirClientResource.FIND_ALL_BY_SUBSCRIPTION_NAMED_QUERY, AvailableFhirClientResource.class )
            .setParameter( "subscription", fhirClient ).getResultList()
            .forEach( a -> result.compute( a.getResourceType(), ( fhirResourceType, availableFhirClientResource ) -> a.merge( availableFhirClientResource ) ) );
        return new ArrayList<>( result.values() );
    }
}
