package org.dhis2.fhir.adapter.prototype.fhir.transform.scripted;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.dhis2.fhir.adapter.prototype.fhir.model.FhirRequest;
import org.dhis2.fhir.adapter.prototype.fhir.model.ImmutableFhirRequest;
import org.dhis2.fhir.adapter.prototype.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.prototype.fhir.transform.TransformException;
import org.dhis2.fhir.adapter.prototype.fhir.transform.TransformMappingException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

public class FhirToDhisTransformerContextImpl implements FhirToDhisTransformerContext, Serializable
{
    private final FhirRequest fhirRequest;

    public FhirToDhisTransformerContextImpl( @Nonnull FhirRequest fhirRequest )
    {
        this.fhirRequest = new ImmutableFhirRequest( fhirRequest );
    }

    @Nonnull
    @Override
    public FhirRequest getFhirRequest()
    {
        return fhirRequest;
    }

    @Nonnull
    @Override
    public <T> T failIfNull( @Nonnull String message, @Nullable T value ) throws TransformException
    {
        if ( value == null )
        {
            throw new TransformMappingException( message );
        }
        return value;
    }


}
