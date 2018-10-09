package org.dhis2.fhir.adapter.jackson;

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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Unit tests for {@link ZonedDateTimeSerializer}
 */
public class ZonedDateTimeSerializerTest
{
    @Mock
    private JsonGenerator jsonGenerator;

    @Mock
    private SerializerProvider serializerProvider;

    private ZonedDateTimeSerializer serializer = new ZonedDateTimeSerializer();

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void serializeNull() throws IOException
    {
        serializer.serialize( null, jsonGenerator, serializerProvider );

        Mockito.verify( jsonGenerator ).writeNull();
        Mockito.verifyNoMoreInteractions( jsonGenerator );
    }

    @Test
    public void serialize() throws IOException
    {
        final ZonedDateTime dateTime = ZonedDateTime.of( 2018, 8, 19, 17, 22, 33, 321000000, ZoneId.ofOffset( "UTC", ZoneOffset.ofHours( 4 ) ) );

        serializer.serialize( dateTime, jsonGenerator, serializerProvider );

        Mockito.verify( jsonGenerator ).writeString( Mockito.eq( "2018-08-19T17:22:33.321+04:00" ) );
        Mockito.verifyNoMoreInteractions( jsonGenerator );
    }
}