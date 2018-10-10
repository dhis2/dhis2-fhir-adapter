package org.dhis2.fhir.adapter.util;

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

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

/**
 * Utility class for handling date and times.
 *
 * @author volsch
 */
public abstract class DateTimeUtils
{
    private static final Pattern DATE_TIME_OFFSET_PATTERN = Pattern.compile( ".+T.+[Z+-].*" );

    /**
     * Checks if an ISO formatted date and time contains a zone offset.
     *
     * @param dateTime the date and time that should be checked.
     * @return <code>true</code> if the ISO formatted date and time contains an offset, <code>false</code> otherwise.
     */
    public static boolean containsDateTimeOffset( @Nonnull String dateTime )
    {
        return DATE_TIME_OFFSET_PATTERN.matcher( dateTime ).matches();
    }

    /**
     * Checks if the specified date/time is on the next day or even later.
     *
     * @param dateTime the date/time that should be checked.
     * @return <code>true</code> if the specified date/time is on the next day or even later, <code>false</code> otherwise.
     */
    public static boolean isFutureDate( @Nonnull ZonedDateTime dateTime )
    {
        return dateTime.toLocalDateTime().truncatedTo( ChronoUnit.DAYS ).isAfter( LocalDateTime.now().truncatedTo( ChronoUnit.DAYS ) );
    }

    public DateTimeUtils()
    {
        super();
    }
}
