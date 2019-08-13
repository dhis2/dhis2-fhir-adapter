package org.dhis2.fhir.adapter.fhir.util;

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

import ca.uhn.fhir.model.primitive.IdDt;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.hl7.fhir.instance.model.api.IIdType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class that handles FHIR URIs.
 *
 * @author volsch
 */
public abstract class FhirUriUtils
{
    protected static final Pattern CANONICAL_PATTERN = Pattern.compile( "(?:([^/]+)/)?([^/]+)" );

    @Nonnull
    public static IIdType createIdFromUri( @Nonnull String uri, @Nullable FhirResourceType fhirResourceType ) throws IllegalArgumentException
    {
        final Matcher matcher = CANONICAL_PATTERN.matcher( uri );

        if ( !matcher.matches() )
        {
            throw new IllegalArgumentException( "URI is invalid: " + uri );
        }

        String resourceTypeName = matcher.group( 1 );
        final String id = matcher.group( 2 );

        if ( resourceTypeName == null && fhirResourceType != null )
        {
            resourceTypeName = fhirResourceType.getResourceTypeName();
        }

        if ( resourceTypeName == null )
        {
            throw new IllegalArgumentException( "URI must include a valid FHIR resource type: " + uri );
        }

        if ( fhirResourceType != null && !fhirResourceType.getResourceTypeName().equals( resourceTypeName ) )
        {
            throw new IllegalArgumentException( "Resource type in URI differs from expected resource type " + fhirResourceType + ": " + uri );
        }

        return new IdDt( resourceTypeName, id );
    }

    private FhirUriUtils()
    {
        super();
    }
}
