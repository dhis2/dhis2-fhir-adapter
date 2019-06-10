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
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.TokenOrListParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientSystem;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Abstract base class of all resource providers for FHIR interface. This
 * abstract implementation exposes read-only interfaces only.
 *
 * @param <T> the concrete type of the resource.
 * @author volsch
 */
public abstract class AbstractReadOnlyResourceProvider<T extends IBaseResource> extends AbstractResourceProvider<T>
{
    public static final String SP_LAST_UPDATED = "_lastUpdated";

    public AbstractReadOnlyResourceProvider( @Nonnull Class<T> resourceClass,
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull FhirClientSystemRepository fhirClientSystemRepository,
        @Nonnull FhirRepository fhirRepository, @Nonnull DhisRepository dhisRepository )
    {
        super( resourceClass, fhirClientResourceRepository, fhirClientSystemRepository, fhirRepository, dhisRepository );
    }

    @Override
    @Nonnull
    public Class<? extends IBaseResource> getResourceType()
    {
        return resourceClass;
    }

    @Read
    @Nullable
    public T getResourceById( @Nonnull RequestDetails requestDetails, @IdParam IIdType id )
    {
        validateUseCase( requestDetails );
        return executeInSecurityContext( () -> resourceClass.cast( getDhisRepository().read(
            getFhirClientResource().getFhirClient(), getFhirResourceType(), extractDhisFhirResourceId( id ) ).orElse( null ) ) );
    }

    @Search
    @Nonnull
    public List<T> searchByIdentifier( @Nonnull RequestDetails requestDetails, @Nonnull @RequiredParam( name = SP_IDENTIFIER ) TokenParam identifierParam )
    {
        validateUseCase( requestDetails );
        return executeInSecurityContext( () -> {
            final FhirClientSystem fhirClientSystem = getFhirClientSystem();
            if ( !fhirClientSystem.getSystem().getSystemUri().equals( identifierParam.getSystem() ) ||
                StringUtils.isBlank( identifierParam.getValue() ) )
            {
                return Collections.emptyList();
            }
            final T result = resourceClass.cast( getDhisRepository().readByIdentifier(
                getFhirClientResource().getFhirClient(), getFhirResourceType(), identifierParam.getValue() ).orElse( null ) );
            return (result == null) ? Collections.emptyList() : Collections.singletonList( result );
        } );
    }

    @Nonnull
    protected IBundleProvider search( @Nonnull RequestDetails requestDetails, @Nullable Integer count, @Nullable TokenOrListParam filteredCodes, @Nullable DateRangeParam lastUpdatedDateRange, @Nullable Map<String, List<String>> filter )
    {
        validateUseCase( requestDetails );

        final Set<SystemCodeValue> convertedFilteredCodes;
        if ( ( filteredCodes == null ) || filteredCodes.getValuesAsQueryTokens().isEmpty() )
        {
            convertedFilteredCodes = null;
        }
        else
        {
            convertedFilteredCodes = new HashSet<>();
            filteredCodes.getValuesAsQueryTokens().forEach( tp ->
                convertedFilteredCodes.add( new SystemCodeValue( StringUtils.defaultString( tp.getSystem() ), StringUtils.defaultString( tp.getValue() ) ) ) );
        }

        return executeInSecurityContext( () ->
            getDhisRepository().search( getFhirClientResource().getFhirClient(), getFhirResourceType(),
                count, false, convertedFilteredCodes, filter, lastUpdatedDateRange ) );
    }
}
