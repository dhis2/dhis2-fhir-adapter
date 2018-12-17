package org.dhis2.fhir.adapter.fhir.transform.fhir.impl;

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

import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ImmutableFhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.scripted.TransformerScriptException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Implementation of {@link FhirToDhisTransformerContext}.
 *
 * @author volsch
 */
public class FhirToDhisTransformerContextImpl implements FhirToDhisTransformerContext, Serializable
{
    private static final long serialVersionUID = -3205126998737677714L;

    private final FhirRequest fhirRequest;

    private final boolean creationDisabled;

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
        final ReferenceType rt;
        try
        {
            rt = ReferenceType.valueOf( referenceType.toString() );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerScriptException( "Not a valid reference type: " + referenceType, e );
        }

        if ( value == null )
        {
            return null;
        }
        return new Reference( value, rt );
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
}
