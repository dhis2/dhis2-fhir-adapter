package org.dhis2.fhir.adapter.fhir.transform.dhis.impl;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.fhir.metadata.model.AvailableRemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscription;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.MissingDhisResourceException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.DhisRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link DhisToFhirTransformerContext}.
 *
 * @author volsch
 */
public class DhisToFhirTransformerContextImpl implements DhisToFhirTransformerContext, Serializable
{
    private static final long serialVersionUID = -3205126998737677714L;

    private final DhisRequest dhisRequest;

    private final RemoteSubscription remoteSubscription;

    private final Map<FhirResourceType, AvailableRemoteSubscriptionResource> availableResourcesByType;

    private final Map<FhirResourceType, ResourceSystem> resourceSystemsByType;

    public DhisToFhirTransformerContextImpl( @Nonnull DhisRequest dhisRequest, @Nonnull RemoteSubscription remoteSubscription,
        @Nonnull Map<FhirResourceType, ResourceSystem> resourceSystemsByType, @Nonnull Collection<AvailableRemoteSubscriptionResource> availableResources )
    {
        this.dhisRequest = dhisRequest;
        this.remoteSubscription = remoteSubscription;
        this.resourceSystemsByType = resourceSystemsByType;
        this.availableResourcesByType = availableResources.stream().collect( Collectors.toMap( AvailableRemoteSubscriptionResource::getResourceType, a -> a ) );
    }

    @Nonnull
    @Override
    public DhisRequest getDhisRequest()
    {
        return dhisRequest;
    }

    @Nonnull
    @Override
    public FhirVersion getVersion()
    {
        return remoteSubscription.getFhirVersion();
    }

    @Nonnull
    @Override
    public UUID getRemoteSubscriptionId()
    {
        return remoteSubscription.getId();
    }

    @Nonnull
    @Override
    public String getRemoteSubscriptionCode()
    {
        return remoteSubscription.getCode();
    }

    @Nonnull
    @Override
    public ZonedDateTime now()
    {
        return ZonedDateTime.now();
    }

    @Nullable
    @Override
    public ResourceSystem getResourceSystem( @Nonnull FhirResourceType resourceType )
    {
        return resourceSystemsByType.get( resourceType );
    }

    @Nonnull
    @Override
    public Optional<ResourceSystem> getOptionalResourceSystem( @Nonnull FhirResourceType resourceType )
    {
        return Optional.ofNullable( getResourceSystem( resourceType ) );
    }

    @Nullable
    @Override
    public AvailableRemoteSubscriptionResource getAvailableResource( @Nonnull FhirResourceType resourceType )
    {
        return availableResourcesByType.get( resourceType );
    }

    @Nonnull
    @Override
    public Optional<AvailableRemoteSubscriptionResource> getOptionalAvailableResource( @Nonnull FhirResourceType resourceType )
    {
        return Optional.ofNullable( getAvailableResource( resourceType ) );
    }

    @Override
    public boolean isUseAdapterIdentifier()
    {
        return remoteSubscription.isUseAdapterIdentifier();
    }

    @Override
    public void missingDhisResource( @Nonnull DhisResourceId dhisResourceId ) throws MissingDhisResourceException
    {
        throw new MissingDhisResourceException( dhisResourceId );
    }

    @Override
    public void fail( @Nonnull String message ) throws TransformerDataException
    {
        throw new TransformerDataException( message );
    }
}
