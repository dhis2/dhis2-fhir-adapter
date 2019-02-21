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

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.model.SingleFhirVersionRestricted;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.fhir.security.AdapterSystemAuthenticationToken;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Abstract base class of all resource providers for FHIR interface. This
 * abstract implementation exposes read-only interfaces only.
 *
 * @param <T> the concrete type of the resource.
 * @author volsch
 */
public abstract class AbstractReadOnlyResourceProvider<T extends IBaseResource> implements IResourceProvider, SingleFhirVersionRestricted
{
    private final Class<T> resourceClass;

    private final FhirRepository fhirRepository;

    private final DhisRepository dhisRepository;

    private final FhirClientResourceRepository fhirClientResourceRepository;

    private final FhirResourceType fhirResourceType;

    private volatile FhirClientResource fhirClientResource;

    public AbstractReadOnlyResourceProvider( @Nonnull Class<T> resourceClass,
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull FhirRepository fhirRepository, @Nonnull DhisRepository dhisRepository )
    {
        this.resourceClass = resourceClass;
        this.fhirClientResourceRepository = fhirClientResourceRepository;
        this.fhirRepository = fhirRepository;
        this.dhisRepository = dhisRepository;
        this.fhirResourceType = FhirResourceType.getByResourceType( resourceClass );

        if ( fhirResourceType == null )
        {
            throw new AssertionError( "Unhandled FHIR resource type: " + resourceClass );
        }
    }

    @Override
    @Nonnull
    public Class<? extends IBaseResource> getResourceType()
    {
        return resourceClass;
    }

    @Read
    @Nullable
    public T getResourceById( @IdParam IIdType id )
    {
        return executeInSecurityContext( () -> resourceClass.cast( getDhisRepository().read( getFhirClientResource().getFhirClient(), getFhirResourceType(), extractDhisFhirResourceId( id ) ).orElse( null ) ) );
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
    protected FhirResourceType getFhirResourceType()
    {
        return fhirResourceType;
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
    protected FhirClientResource getFhirClientResource()
    {
        if ( fhirClientResource == null )
        {
            final UUID fhirClientId = FhirClient.getIdByFhirVersion( getFhirVersion() );
            fhirClientResource = fhirClientResourceRepository.findFirstCached( fhirClientId, fhirResourceType )
                .orElseThrow( () -> new InvalidRequestException( "FHIR resource " + fhirResourceType +
                    " is not supported for FHIR version " + getFhirVersion() + "." ) );
        }
        return fhirClientResource;
    }

    protected final <T> T executeInSecurityContext( @Nonnull Supplier<T> supplier )
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
}
