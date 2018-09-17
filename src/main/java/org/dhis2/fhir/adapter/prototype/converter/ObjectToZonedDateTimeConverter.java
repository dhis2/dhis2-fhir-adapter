package org.dhis2.fhir.adapter.prototype.converter;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectToZonedDateTimeConverter extends TypedConverter<Object, ZonedDateTime>
{
    private final Pattern dateTimePattern = Pattern.compile( ".*(\\d{4}-\\d{2}-\\d{2}T.*Z).*" );

    private final ZoneId zoneId = ZoneId.systemDefault();

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME.withZone( zoneId );

    public ObjectToZonedDateTimeConverter()
    {
        super( Object.class, ZonedDateTime.class );
    }

    @Override public @Nullable ZonedDateTime doConvert( @Nonnull Object source )
    {
        final Matcher matcher = dateTimePattern.matcher( source.toString() );
        if ( !matcher.matches() )
        {
            throw new ConversionException( "Could not parse ISO formatted local date in string: " + source );
        }
        final String value = matcher.group( 1 );
        try
        {
            return ZonedDateTime.from( formatter.parse( value ) );
        }
        catch ( DateTimeParseException e )
        {
            throw new ConversionException( "Could not parse ISO formatted local date in string: " + source, e );
        }
    }
}
