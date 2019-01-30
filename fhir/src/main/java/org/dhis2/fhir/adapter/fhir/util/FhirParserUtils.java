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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * FHIR parser utilities for parsing JSON and XML.
 *
 * @author volsch
 */
public abstract class FhirParserUtils
{
    @Nonnull
    public static IBaseResource parse( @Nonnull FhirContext fhirContext, @Nonnull String resource, @Nullable String contentType ) throws FhirParserException
    {
        boolean xml;
        if ( contentType == null )
        {
            int xmlIndex = resource.indexOf( '<' );
            if ( xmlIndex < 0 )
            {
                xmlIndex = Integer.MAX_VALUE;
            }
            int jsonIndex = resource.indexOf( '{' );
            if ( jsonIndex < 0 )
            {
                jsonIndex = Integer.MAX_VALUE;
            }
            xml = xmlIndex < jsonIndex;
        }
        else
        {
            xml = contentType.toLowerCase().contains( "xml" );
        }

        try
        {
            if ( xml )
            {
                return fhirContext.newXmlParser().parseResource( resource );
            }
            else
            {
                return fhirContext.newJsonParser().parseResource( resource );
            }
        }
        catch ( DataFormatException e )
        {
            throw new FhirParserException( "Could not parse FHIR resource: " + e.getMessage(), e );
        }
    }

    private FhirParserUtils()
    {
        super();
    }
}
