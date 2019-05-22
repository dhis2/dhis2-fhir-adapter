package org.dhis2.fhir.adapter.fhir.server.provider;

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

import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientSystem;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.model.SingleFhirVersionRestricted;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.fhir.security.AdapterSystemAuthenticationToken;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Abstract base class of all FHIR resource providers for FHIR interface (also plain
 * FHIR resource providers).
 *
 * @author volsch
 */
public abstract class AbstractUntypedResourceProvider implements FhirResourceProvider, SingleFhirVersionRestricted
{
    public static final String DEFAULT_USE_CASE_TENANT = "default";

    public static final String SP_IDENTIFIER = "identifier";

    private final FhirRepository fhirRepository;

    private final DhisRepository dhisRepository;

    private final FhirClientResourceRepository fhirClientResourceRepository;

    private final FhirClientSystemRepository fhirClientSystemRepository;

    private volatile Map<FhirResourceType, FhirClientResource> fhirClientResources = new HashMap<>();

    private volatile Map<FhirResourceType, FhirClientSystem> fhirClientSystems = new HashMap<>();

    public AbstractUntypedResourceProvider(
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull FhirClientSystemRepository fhirClientSystemRepository,
        @Nonnull FhirRepository fhirRepository, @Nonnull DhisRepository dhisRepository )
    {
        this.fhirClientResourceRepository = fhirClientResourceRepository;
        this.fhirClientSystemRepository = fhirClientSystemRepository;
        this.fhirRepository = fhirRepository;
        this.dhisRepository = dhisRepository;
    }

    @Nonnull
    protected FhirClientResourceRepository getFhirClientResourceRepository()
    {
        return fhirClientResourceRepository;
    }

    @Nonnull
    protected FhirRepository getFhirRepository()
    {
        return fhirRepository;
    }

    @Nonnull
    protected DhisRepository getDhisRepository()
    {
        return dhisRepository;
    }

    @Nonnull
    protected DhisFhirResourceId extractDhisFhirResourceId( @Nonnull IIdType idType ) throws UnprocessableEntityException
    {
        try
        {
            return DhisFhirResourceId.parse( idType.getIdPart() );
        }
        catch ( IllegalArgumentException e )
        {
            throw new UnprocessableEntityException( "Invalid DHIS2 FHIR ID: " + idType.getIdPart() );
        }
    }

    @Nonnull
    protected FhirClientResource getFhirClientResource( @Nonnull FhirResourceType fhirResourceType )
    {
        FhirClientResource fhirClientResource = fhirClientResources.get( fhirResourceType );

        if ( fhirClientResource != null )
        {
            return fhirClientResource;
        }

        final UUID fhirClientId = FhirClient.getIdByFhirVersion( getFhirVersion() );
        fhirClientResource = fhirClientResourceRepository.findFirstCached( fhirClientId, fhirResourceType )
            .orElseThrow( () -> new InvalidRequestException( "FHIR resource " + fhirResourceType +
                " is not supported for FHIR version " + getFhirVersion() + "." ) );

        final Map<FhirResourceType, FhirClientResource> copiedMap = new HashMap<>( fhirClientResources );
        copiedMap.put( fhirResourceType, fhirClientResource );
        fhirClientResources = copiedMap;

        return fhirClientResource;
    }

    @Nonnull
    protected FhirClientSystem getFhirClientSystem( @Nonnull FhirResourceType fhirResourceType )
    {
        FhirClientSystem fhirClientSystem = fhirClientSystems.get( fhirResourceType );

        if ( fhirClientSystem != null )
        {
            return fhirClientSystem;
        }


        final UUID fhirClientId = FhirClient.getIdByFhirVersion( getFhirVersion() );
        fhirClientSystem = fhirClientSystemRepository.findOneByFhirClientResourceType( fhirClientId, fhirResourceType )
            .orElseThrow( () -> new InvalidRequestException( "FHIR resource " + fhirResourceType +
                " has not assigned system URI for FHIR version " + getFhirVersion() + "." ) );

        final Map<FhirResourceType, FhirClientSystem> copiedMap = new HashMap<>( fhirClientSystems );
        copiedMap.put( fhirResourceType, fhirClientSystem );
        fhirClientSystems = copiedMap;

        return fhirClientSystem;
    }

    protected void validateUseCase( @Nonnull RequestDetails theRequestDetails )
    {
        final String tenantId = theRequestDetails.getTenantId();

        if ( !DEFAULT_USE_CASE_TENANT.equals( tenantId ) )
        {
            throw new InvalidRequestException( "Selected use case must be default (received " + tenantId + ")." );
        }
    }

    protected final <R> R executeInSecurityContext( @Nonnull Supplier<R> supplier )
    {
        SecurityContextHolder.getContext().setAuthentication( new AdapterSystemAuthenticationToken() );
        try
        {
            return supplier.get();
        }
        finally
        {
            SecurityContextHolder.clearContext();
        }
    }

    protected TokenParam parseTokenParam( @Nonnull String param )
    {
        final int index = param.indexOf( '|' );

        if ( index < 0 )
        {
            return new TokenParam( null, param );
        }

        return new TokenParam( param.substring( 0, index ), param.substring( index + 1 ) );
    }
}
