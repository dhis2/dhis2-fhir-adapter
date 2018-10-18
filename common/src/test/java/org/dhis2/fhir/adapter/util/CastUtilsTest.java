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

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Unit tests for {@link CastUtils}.
 *
 * @author volsch
 */
public class CastUtilsTest
{
    @Test
    public void cast2WithNull()
    {
        Assert.assertNull( CastUtils.cast( null,
            ZonedDateTime.class, ZonedDateTime::toLocalDate,
            Date.class, v -> LocalDate.from( v.toInstant().atZone( ZoneId.systemDefault() ) ) ) );
    }

    @Test
    public void cast2With1()
    {
        final ZonedDateTime input = ZonedDateTime.now();
        final LocalDate localDate = CastUtils.cast( input,
            ZonedDateTime.class, ZonedDateTime::toLocalDate,
            Date.class, v -> LocalDate.from( v.toInstant().atZone( ZoneId.systemDefault() ) ) );
        Assert.assertEquals( input.toLocalDate(), localDate );
    }

    @Test
    public void cast2With2()
    {
        final Date input = new Date();
        final LocalDate localDate = CastUtils.cast( input,
            ZonedDateTime.class, ZonedDateTime::toLocalDate,
            Date.class, v -> LocalDate.from( v.toInstant().atZone( ZoneId.systemDefault() ) ) );
        Assert.assertEquals( LocalDate.from( input.toInstant().atZone( ZoneId.systemDefault() ) ), localDate );
    }

    @Test( expected = ClassCastException.class )
    public void cast2WithInvalid()
    {
        CastUtils.cast( "Test",
            ZonedDateTime.class, ZonedDateTime::toLocalDate,
            Date.class, v -> LocalDate.from( v.toInstant().atZone( ZoneId.systemDefault() ) ) );
    }

    @Test
    public void cast3WithNull()
    {
        Assert.assertNull( CastUtils.cast( null,
            ZonedDateTime.class, ZonedDateTime::toLocalDate,
            Date.class, v -> LocalDate.from( v.toInstant().atZone( ZoneId.systemDefault() ) ),
            Calendar.class, c -> LocalDate.from( c.toInstant().atZone( ZoneId.systemDefault() ) ) ) );
    }

    @Test
    public void cast3With1()
    {
        final ZonedDateTime input = ZonedDateTime.now();
        final LocalDate localDate = CastUtils.cast( input,
            ZonedDateTime.class, ZonedDateTime::toLocalDate,
            Date.class, v -> LocalDate.from( v.toInstant().atZone( ZoneId.systemDefault() ) ),
            Calendar.class, c -> LocalDate.from( c.toInstant().atZone( ZoneId.systemDefault() ) ) );
        Assert.assertEquals( input.toLocalDate(), localDate );
    }

    @Test
    public void cast3With2()
    {
        final Date input = new Date();
        final LocalDate localDate = CastUtils.cast( input,
            ZonedDateTime.class, ZonedDateTime::toLocalDate,
            Date.class, v -> LocalDate.from( v.toInstant().atZone( ZoneId.systemDefault() ) ),
            Calendar.class, c -> LocalDate.from( c.toInstant().atZone( ZoneId.systemDefault() ) ) );
        Assert.assertEquals( LocalDate.from( input.toInstant().atZone( ZoneId.systemDefault() ) ), localDate );
    }

    @Test
    public void cast3With3()
    {
        final Calendar input = new GregorianCalendar();
        final LocalDate localDate = CastUtils.cast( input,
            ZonedDateTime.class, ZonedDateTime::toLocalDate,
            Date.class, v -> LocalDate.from( v.toInstant().atZone( ZoneId.systemDefault() ) ),
            Calendar.class, c -> LocalDate.from( c.toInstant().atZone( ZoneId.systemDefault() ) ) );
        Assert.assertEquals( LocalDate.from( input.toInstant().atZone( ZoneId.systemDefault() ) ), localDate );
    }
}