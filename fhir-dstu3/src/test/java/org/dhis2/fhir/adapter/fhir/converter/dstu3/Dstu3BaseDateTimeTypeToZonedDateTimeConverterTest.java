package org.dhis2.fhir.adapter.fhir.converter.dstu3;

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

import org.hl7.fhir.dstu3.model.DateTimeType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.sql.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Unit tests for {@link Dstu3BaseDateTimeTypeToZonedDateTimeConverter}.
 *
 * @author volsch
 */
public class Dstu3BaseDateTimeTypeToZonedDateTimeConverterTest
{
    @InjectMocks
    private Dstu3BaseDateTimeTypeToZonedDateTimeConverter converter;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void fullTimestamp()
    {
        final ZonedDateTime zonedDateTime = ZonedDateTime.of( 2018, 7, 27, 15, 2, 43, 123000000, ZoneId.of( "CET" ) );
        final ZonedDateTime result = converter.doConvert( new DateTimeType( Date.from( zonedDateTime.toInstant() ) ) );
        Assert.assertNotNull( result );
        Assert.assertEquals( zonedDateTime.toLocalDateTime(), result.toLocalDateTime() );
    }
}