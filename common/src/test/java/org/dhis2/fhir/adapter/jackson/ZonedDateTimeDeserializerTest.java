package org.dhis2.fhir.adapter.jackson;

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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class ZonedDateTimeDeserializerTest
{
    @Mock
    private JsonParser jsonParser;

    @Mock
    private DeserializationContext deserializationContext;

    private ZonedDateTimeDeserializer deserializer = new ZonedDateTimeDeserializer();

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void deserializeEmpty() throws IOException
    {
        Mockito.when( jsonParser.getText() ).thenReturn( "  " );

        Assert.assertNull( deserializer.deserialize( jsonParser, deserializationContext ) );

        Mockito.verify( jsonParser ).getText();
        Mockito.verifyNoMoreInteractions( jsonParser );
    }

    @Test
    public void deserialize() throws IOException
    {
        Mockito.when( jsonParser.getText() ).thenReturn( "2018-09-05T17:18:23.123-04:00" );

        final ZonedDateTime zonedDateTime = deserializer.deserialize( jsonParser, deserializationContext );
        Assert.assertNotNull( zonedDateTime );
        Assert.assertEquals( 2018, zonedDateTime.getYear() );
        Assert.assertEquals( 9, zonedDateTime.getMonth().getValue() );
        Assert.assertEquals( 5, zonedDateTime.getDayOfMonth() );
        Assert.assertEquals( 17, zonedDateTime.getHour() );
        Assert.assertEquals( 18, zonedDateTime.getMinute() );
        Assert.assertEquals( 23, zonedDateTime.getSecond() );
        Assert.assertEquals( 123000000, zonedDateTime.getNano() );
        Assert.assertEquals( ZoneOffset.ofHours( -4 ), zonedDateTime.getOffset() );

        Mockito.verify( jsonParser ).getText();
        Mockito.verifyNoMoreInteractions( jsonParser );
    }

    @Test
    public void deserializeSystemDefaultZone() throws IOException
    {
        Mockito.when( jsonParser.getText() ).thenReturn( "2018-09-05T17:18:23.123" );

        final ZoneOffset zoneOffset = OffsetDateTime.now().getOffset();
        final ZonedDateTime zonedDateTime = deserializer.deserialize( jsonParser, deserializationContext );
        Assert.assertNotNull( zonedDateTime );
        Assert.assertEquals( 2018, zonedDateTime.getYear() );
        Assert.assertEquals( 9, zonedDateTime.getMonth().getValue() );
        Assert.assertEquals( 5, zonedDateTime.getDayOfMonth() );
        Assert.assertEquals( 17, zonedDateTime.getHour() );
        Assert.assertEquals( 18, zonedDateTime.getMinute() );
        Assert.assertEquals( 23, zonedDateTime.getSecond() );
        Assert.assertEquals( 123000000, zonedDateTime.getNano() );
        Assert.assertEquals( zoneOffset, zonedDateTime.getOffset() );

        Mockito.verify( jsonParser ).getText();
        Mockito.verifyNoMoreInteractions( jsonParser );
    }

    @Test
    public void deserializeUtc() throws IOException
    {
        Mockito.when( jsonParser.getText() ).thenReturn( "2018-09-05T17:18:23.123Z" );

        final ZonedDateTime zonedDateTime = deserializer.deserialize( jsonParser, deserializationContext );
        Assert.assertNotNull( zonedDateTime );
        Assert.assertEquals( 2018, zonedDateTime.getYear() );
        Assert.assertEquals( 9, zonedDateTime.getMonth().getValue() );
        Assert.assertEquals( 5, zonedDateTime.getDayOfMonth() );
        Assert.assertEquals( 17, zonedDateTime.getHour() );
        Assert.assertEquals( 18, zonedDateTime.getMinute() );
        Assert.assertEquals( 23, zonedDateTime.getSecond() );
        Assert.assertEquals( 123000000, zonedDateTime.getNano() );
        Assert.assertEquals( 0, zonedDateTime.getOffset().getTotalSeconds() );

        Mockito.verify( jsonParser ).getText();
        Mockito.verifyNoMoreInteractions( jsonParser );
    }
}