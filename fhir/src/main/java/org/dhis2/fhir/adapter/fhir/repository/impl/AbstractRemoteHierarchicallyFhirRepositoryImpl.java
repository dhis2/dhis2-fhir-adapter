package org.dhis2.fhir.adapter.fhir.repository.impl;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionFhirEndpoint;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirRepository;
import org.dhis2.fhir.adapter.fhir.repository.RemoteHierarchicallyFhirRepository;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

/**
 * Implementation of {@link RemoteHierarchicallyFhirRepository}.
 *
 * @author volsch
 */
@CacheConfig( cacheNames = "hierarchicallyFhirResources", cacheManager = "fhirCacheManager" )
public abstract class AbstractRemoteHierarchicallyFhirRepositoryImpl implements RemoteHierarchicallyFhirRepository
{
    private final RemoteFhirRepository remoteFhirRepository;

    public AbstractRemoteHierarchicallyFhirRepositoryImpl( @Nonnull RemoteFhirRepository remoteFhirRepository )
    {
        this.remoteFhirRepository = remoteFhirRepository;
    }

    @Nonnull
    @Cacheable( key = "{#remoteSubscriptionId, #fhirVersion, #resourceType, #resourceId, #hierarchyType}" )
    @Override
    public IBaseBundle findWithParents( @Nonnull UUID remoteSubscriptionId, @Nonnull FhirVersion fhirVersion, @Nonnull SubscriptionFhirEndpoint fhirEndpoint,
        @Nonnull String resourceType, @Nullable String resourceId, @Nonnull String hierarchyType,
        @Nonnull Function<IBaseResource, IBaseReference> parentReferenceFunction )
    {
        if ( resourceId == null )
        {
            return createBundle( Collections.emptyList() );
        }

        IBaseResource child = remoteFhirRepository.find( remoteSubscriptionId, fhirVersion, fhirEndpoint, resourceType, resourceId ).orElse( null );
        if ( child == null )
        {
            return createBundle( Collections.emptyList() );
        }

        final Set<String> processedResourceIds = new HashSet<>();
        processedResourceIds.add( resourceType + "/" + resourceId );
        final List<IBaseResource> result = new ArrayList<>();
        result.add( child );

        IBaseReference parentReference;
        while ( (child != null) && ((parentReference = parentReferenceFunction.apply( child )) != null) && !parentReference.isEmpty() && parentReference.getReferenceElement().hasIdPart() )
        {
            final String childResourceType = parentReference.getReferenceElement().hasResourceType() ?
                parentReference.getReferenceElement().getResourceType() : resourceType;
            if ( !processedResourceIds.add( childResourceType + "/" + parentReference.getReferenceElement().getIdPart() ) )
            {
                // there is a dependency loop and search must be interrupted
                break;
            }
            child = remoteFhirRepository.find( remoteSubscriptionId, fhirVersion, fhirEndpoint,
                childResourceType, parentReference.getReferenceElement().getIdPart() ).orElse( null );
            if ( child != null )
            {
                result.add( child );
            }
        }

        return createBundle( result );
    }

    @Nonnull
    protected abstract IBaseBundle createBundle( @Nonnull List<? extends IBaseResource> resources );
}
