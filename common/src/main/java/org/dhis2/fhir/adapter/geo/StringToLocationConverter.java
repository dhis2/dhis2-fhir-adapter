package org.dhis2.fhir.adapter.geo;

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

import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.converter.ConvertedValueTypes;
import org.dhis2.fhir.adapter.converter.TypedConverter;
import org.dhis2.fhir.adapter.model.ValueType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts a string to a {@link Location}. The format of the string <code>[10.92,-24.212]</code> where the first
 * value is the longitude and the second value is the latitude.
 *
 * @author volsch
 */
@Component
@ConvertedValueTypes( types = ValueType.COORDINATE )
public class StringToLocationConverter extends TypedConverter<String, Location>
{
    private static final Pattern LOCATION_PATTERN = Pattern.compile( "\\s*\\[\\s*([+\\-\\d.eE]+)\\s*,\\s*([+\\-\\d.eE]+)\\s*]\\s*" );

    public StringToLocationConverter()
    {
        super( String.class, Location.class );
    }

    @Nullable
    @Override
    public Location doConvert( @Nonnull String source ) throws ConversionException
    {
        final Matcher matcher = LOCATION_PATTERN.matcher( source );
        if ( !matcher.matches() )
        {
            throw new ConversionException( "Not a valid location: " + source );
        }
        try
        {
            return new Location( Double.valueOf( matcher.group( 1 ) ), Double.valueOf( matcher.group( 2 ) ) );
        }
        catch ( NumberFormatException e )
        {
            throw new ConversionException( "Not a valid location: " + source );
        }
    }

    /**
     * Returns if the specified source is the string representation of a single location. No strict
     * validation of the longitude and latitude values is made.
     *
     * @param source the string that should be checked.
     * @return <code>true</code> if the specified string is a representation of a single location
     * or <code>null</code>, <code>false</code> otherwise.
     */
    public static boolean isLocation( @Nullable String source )
    {
        return (source == null) || LOCATION_PATTERN.matcher( source ).matches();
    }
}
