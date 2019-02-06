package org.dhis2.fhir.adapter.fhir.repository.impl;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionFhirEndpoint;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.HierarchicallyFhirResourceRepository;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * Implementation of {@link HierarchicallyFhirResourceRepository}.
 *
 * @author volsch
 */
@Component
@CacheConfig( cacheNames = "hierarchicallyFhirResources", cacheManager = "fhirCacheManager" )
public class HierarchicallyFhirResourceRepositoryImpl implements HierarchicallyFhirResourceRepository
{
    private final FhirResourceRepository fhirResourceRepository;

    private final Map<FhirVersion, AbstractFhirResourceRepositorySupport> supports = new HashMap<>();

    public HierarchicallyFhirResourceRepositoryImpl( @Nonnull FhirResourceRepository fhirResourceRepository, @Nonnull ObjectProvider<List<AbstractFhirResourceRepositorySupport>> supports )
    {
        this.fhirResourceRepository = fhirResourceRepository;
        supports.getIfAvailable( Collections::emptyList ).forEach( s -> s.getFhirVersions().forEach( v -> HierarchicallyFhirResourceRepositoryImpl.this.supports.put( v, s ) ) );
    }

    @Nonnull
    @Cacheable( key = "{#fhirServerId, #fhirVersion, #resourceType, #resourceId, #hierarchyType}" )
    @Override
    public IBaseBundle findWithParents( @Nonnull UUID fhirServerId, @Nonnull FhirVersion fhirVersion, @Nonnull SubscriptionFhirEndpoint fhirEndpoint,
        @Nonnull String resourceType, @Nullable String resourceId, @Nonnull String hierarchyType,
        @Nonnull Function<IBaseResource, IBaseReference> parentReferenceFunction )
    {
        final AbstractFhirResourceRepositorySupport support = supports.get( fhirVersion );
        if ( resourceId == null )
        {
            return support.createBundle( Collections.emptyList() );
        }

        IBaseResource child = fhirResourceRepository.find( fhirServerId, fhirVersion, fhirEndpoint, resourceType, resourceId ).orElse( null );
        if ( child == null )
        {
            return support.createBundle( Collections.emptyList() );
        }

        final Set<IBaseResource> processedResources = new HashSet<>();
        final Set<String> processedResourceIds = new HashSet<>();
        processedResourceIds.add( NameUtils.toClassName( resourceType ) + "/" + resourceId );
        final List<IBaseResource> result = new ArrayList<>();
        result.add( child );

        IBaseReference parentReference;
        while ( (child != null) && ((parentReference = parentReferenceFunction.apply( child )) != null) && !parentReference.isEmpty() && parentReference.getReferenceElement().hasIdPart() )
        {
            if ( parentReference.getResource() == null )
            {
                final String childResourceType = parentReference.getReferenceElement().hasResourceType() ?
                    parentReference.getReferenceElement().getResourceType() : Objects.requireNonNull( NameUtils.toClassName( resourceType ) );
                if ( !processedResourceIds.add( childResourceType + "/" + parentReference.getReferenceElement().getIdPart() ) )
                {
                    // there is a dependency loop and search must be interrupted
                    break;
                }
                child = fhirResourceRepository.find( fhirServerId, fhirVersion, fhirEndpoint,
                    childResourceType, parentReference.getReferenceElement().getIdPart() ).orElse( null );
            }
            else
            {
                child = parentReference.getResource();
            }
            if ( child != null )
            {
                if ( !processedResources.add( child ) )
                {
                    // there is a dependency loop and search must be interrupted
                    break;
                }
                result.add( child );
            }
        }

        return support.createBundle( result );
    }
}
