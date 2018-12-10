package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program;

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

import org.dhis2.fhir.adapter.dhis.model.Option;
import org.dhis2.fhir.adapter.dhis.model.OptionSet;
import org.dhis2.fhir.adapter.fhir.transform.scripted.TransformerScriptException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for transforming FHIR values to DHIS option set values.
 *
 * @author volsch
 */
public abstract class FhirToDhisOptionSetUtils
{
    private static final Pattern ALL = Pattern.compile( "(.*)" );

    @Nullable
    public static String getIntegerOptionCode( @Nullable String code, @Nullable Pattern pattern )
    {
        if ( (code == null) || (pattern == null) )
        {
            return code;
        }

        final Matcher matcher = pattern.matcher( code );
        if ( !matcher.matches() )
        {
            return null;
        }
        if ( matcher.groupCount() != 1 )
        {
            throw new TransformerScriptException( "Pattern to resolveRule integer options must have exactly one group: " + pattern.pattern() );
        }
        return matcher.group( 1 );
    }

    @Nonnull
    public static List<String> resolveIntegerOptionCodes( @Nonnull OptionSet optionSet, @Nullable Pattern pattern ) throws TransformerScriptException
    {
        final Pattern resultingPattern = (pattern == null) ? ALL : pattern;
        final SortedMap<Integer, String> options = new TreeMap<>();
        for ( final Option option : optionSet.getOptions() )
        {
            final String code, value;
            if ( pattern == null )
            {
                code = value = option.getCode();
            }
            else
            {
                code = option.getCode();
                value = getIntegerOptionCode( code, resultingPattern );
            }
            if ( value != null )
            {
                try
                {
                    if ( options.put( Integer.parseInt( value ), code ) != null )
                    {
                        throw new TransformerScriptException( "Pattern to resolveRule integer options results in duplicate integer value " + value + ": " + resultingPattern.pattern() );
                    }
                }
                catch ( NumberFormatException e )
                {
                    throw new TransformerScriptException( "Pattern to resolveRule integer options results in non-integer value \"" + value + "\": " + resultingPattern.pattern() );
                }
            }
        }

        validateAscendingIntegerOptions( options, resultingPattern );
        return new ArrayList<>( options.values() );
    }

    private static void validateAscendingIntegerOptions( @Nonnull SortedMap<Integer, String> options, @Nonnull Pattern pattern ) throws TransformerScriptException
    {
        if ( options.isEmpty() )
        {
            throw new TransformerScriptException( "Pattern to resolveRule integer options does not result in any option: " + pattern.pattern() );
        }

        int expectedValue = options.firstKey();
        for ( final int value : options.keySet() )
        {
            if ( value != expectedValue )
            {
                throw new TransformerScriptException( "Pattern to resolveRule integer options does not result subsequent ordered integer values: " + pattern.pattern() );
            }
            expectedValue++;
        }
    }

    private FhirToDhisOptionSetUtils()
    {
        super();
    }
}
