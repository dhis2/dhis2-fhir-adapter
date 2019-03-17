package org.dhis2.fhir.adapter.fhir.transform.dhis.search;

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

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.parser.DataFormatException;
import org.dhis2.fhir.adapter.converter.DateToIsoDateStringConverter;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProviderException;

import javax.annotation.Nullable;
import java.util.regex.Pattern;

/**
 * Specifies the different types of FHIR search parameters.
 *
 * @author volsch
 */
public enum SearchParamType
{
    NUMBER( false, true )
        {
            private final Pattern NUMBER_PATTERN = Pattern.compile( "\\d+(\\.\\d+)([eE]\\d{1,2})?" );

            @Nullable
            @Override
            public String convertToDhis( @Nullable String value ) throws DhisToFhirDataProviderException
            {
                if ( ( value != null ) && !NUMBER_PATTERN.matcher( value ).matches() )
                {
                    throw new DhisToFhirDataProviderException( "Search parameter value is not a number: " + value );
                }
                return value;
            }
        },
    DATE( false, true )
        {
            private final DateToIsoDateStringConverter converter = new DateToIsoDateStringConverter();

            @Nullable
            @Override
            public String convertToDhis( @Nullable String value ) throws DhisToFhirDataProviderException
            {
                if ( value == null )
                {
                    return null;
                }
                try
                {
                    final DateTimeDt dateTimeDt = new DateTimeDt();
                    dateTimeDt.setPrecision( TemporalPrecisionEnum.MILLI );
                    dateTimeDt.setValueAsString( value );
                    return converter.doConvert( dateTimeDt.getValue() );
                }
                catch ( DataFormatException e )
                {
                    throw new DhisToFhirDataProviderException( "Search parameter is not a valid date: " + value );
                }
            }
        },
    STRING( true, false )
        {
            @Nullable
            @Override
            public String convertToDhis( @Nullable String value ) throws DhisToFhirDataProviderException
            {
                return value;
            }
        },
    REFERENCE( false, false )
        {
            @Nullable
            @Override
            public String convertToDhis( @Nullable String value ) throws DhisToFhirDataProviderException
            {
                throw new IllegalStateException( "Reference values cannot be converted to DHIS" );
            }
        },
    TOKEN( true, false )
        {
            @Nullable
            @Override
            public String convertToDhis( @Nullable String value ) throws DhisToFhirDataProviderException
            {
                return value;
            }
        };

    private final boolean modifierAllowed;

    private final boolean prefixAllowed;

    SearchParamType( boolean modifierAllowed, boolean prefixAllowed )
    {
        this.modifierAllowed = modifierAllowed;
        this.prefixAllowed = prefixAllowed;
    }

    public boolean isModifierAllowed()
    {
        return modifierAllowed;
    }

    public boolean isPrefixAllowed()
    {
        return prefixAllowed;
    }

    @Nullable
    public abstract String convertToDhis( @Nullable String value )
        throws DhisToFhirDataProviderException;
}
