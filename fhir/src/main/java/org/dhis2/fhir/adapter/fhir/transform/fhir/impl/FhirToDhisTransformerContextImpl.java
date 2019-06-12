package org.dhis2.fhir.adapter.fhir.transform.fhir.impl;

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
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ImmutableFhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.hl7.fhir.instance.model.api.IIdType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link FhirToDhisTransformerContext}.
 *
 * @author volsch
 */
public class FhirToDhisTransformerContextImpl implements FhirToDhisTransformerContext
{
    private final FhirRequest fhirRequest;

    private final boolean creationDisabled;

    private final Map<String, Object> attributes = new HashMap<>();

    public FhirToDhisTransformerContextImpl( @Nonnull FhirRequest fhirRequest, boolean creationDisabled )
    {
        this.fhirRequest = new ImmutableFhirRequest( fhirRequest );
        this.creationDisabled = creationDisabled;
    }

    @Nonnull
    @Override
    public FhirRequest getFhirRequest()
    {
        return fhirRequest;
    }

    @Nullable
    @Override
    public Reference createReference( @Nullable String value, @Nonnull Object referenceType )
    {
        return TransformerUtils.createReference( value, referenceType );
    }

    @Nullable
    @Override
    public String extractDhisId( @Nullable Object idElement )
    {
        if ( idElement == null )
        {
            return null;
        }

        final String id;

        if ( idElement instanceof IIdType )
        {
            final IIdType idType = (IIdType) idElement;

            if ( idType.isLocal() )
            {
                return null;
            }

            id = idType.getIdPart();
        }
        else
        {
            id = idElement.toString();
        }

        if ( id == null )
        {
            return null;
        }

        if ( DhisResourceId.isValidId( id ) )
        {
            return id;
        }

        try
        {
            return DhisFhirResourceId.parse( id ).getId();
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerDataException( "Not a valid DHIS2 FHIR ID: " + id, e );
        }
    }

    @Nonnull
    @Override
    public ZonedDateTime now()
    {
        return ZonedDateTime.now();
    }

    @Override
    public boolean isCreationDisabled()
    {
        return creationDisabled;
    }

    @Override
    public void fail( @Nonnull String message ) throws TransformerDataException
    {
        throw new TransformerDataException( message );
    }

    @Nullable
    @Override
    public String getDhisUsername()
    {
        return getFhirRequest().getDhisUsername();
    }

    @Nonnull
    @Override
    public FhirVersion getFhirVersion()
    {
        return getFhirRequest().getVersion();
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
