package org.dhis2.fhir.adapter.fhir.transform.dhis.impl;

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
import org.dhis2.fhir.adapter.fhir.metadata.model.AvailableFhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
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
import java.util.HashMap;
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

    private final FhirClient fhirClient;

    private final Map<FhirResourceType, AvailableFhirClientResource> availableResourcesByType;

    private final Map<FhirResourceType, ResourceSystem> resourceSystemsByType;

    private final Map<String, Object> attributes = new HashMap<>();

    public DhisToFhirTransformerContextImpl( @Nonnull DhisRequest dhisRequest, @Nonnull FhirClient fhirClient,
        @Nonnull Map<FhirResourceType, ResourceSystem> resourceSystemsByType, @Nonnull Collection<AvailableFhirClientResource> availableResources )
    {
        this.dhisRequest = dhisRequest;
        this.fhirClient = fhirClient;
        this.resourceSystemsByType = resourceSystemsByType;
        this.availableResourcesByType = availableResources.stream().collect( Collectors.toMap( AvailableFhirClientResource::getResourceType, a -> a ) );
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
        return fhirClient.getFhirVersion();
    }

    @Nonnull
    @Override
    public UUID getFhirClientId()
    {
        return fhirClient.getId();
    }

    @Nonnull
    @Override
    public String getFhirClientCode()
    {
        return fhirClient.getCode();
    }

    @Nonnull
    @Override
    public ZonedDateTime now()
    {
        return ZonedDateTime.now();
    }

    @Nonnull
    @Override
    public FhirVersion getFhirVersion()
    {
        return fhirClient.getFhirVersion();
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
    public AvailableFhirClientResource getAvailableResource( @Nonnull FhirResourceType resourceType )
    {
        return availableResourcesByType.get( resourceType );
    }

    @Nonnull
    @Override
    public Optional<AvailableFhirClientResource> getOptionalAvailableResource( @Nonnull FhirResourceType resourceType )
    {
        return Optional.ofNullable( getAvailableResource( resourceType ) );
    }

    @Override
    public boolean isUseAdapterIdentifier()
    {
        return fhirClient.isUseAdapterIdentifier();
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

    @Nonnull
    @Override
    public String getDhisUsername()
    {
        return fhirClient.getDhisEndpoint().getUsername();
    }

    @Override
    public void setAttribute( @Nonnull String name, @Nullable Object value )
    {
        attributes.put( name, value );
    }

    @Nullable
    @Override
    public Object getAttribute( @Nonnull String name )
    {
        return attributes.get( name );
    }
}
