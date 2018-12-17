package org.dhis2.fhir.adapter.dhis.converter;

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
import org.dhis2.fhir.adapter.converter.ObjectToZonedDateTimeConverter;
import org.dhis2.fhir.adapter.converter.StringToIntegerConverter;
import org.dhis2.fhir.adapter.converter.StringToZonedDateTimeConverter;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.spring.StaticObjectProvider;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.convert.ConversionService;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * Unit tests for {@link ValueConverter}.
 *
 * @author volsch
 */
public class ValueConverterTest
{
    @Mock
    private ConversionService conversionService;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testSameSourceClassSuperClassBefore()
    {
        final ObjectToZonedDateTimeConverter converter1 = new ObjectToZonedDateTimeConverter();
        final StringToZonedDateTimeConverter converter2 = new StringToZonedDateTimeConverter();
        final ValueConverter valueConverter = new ValueConverter( new StaticObjectProvider<>( Arrays.asList( converter1, converter2 ) ), conversionService );
        testSameSourceClass( valueConverter );
    }

    @Test
    public void testSameSourceClassSuperClassAfter()
    {
        final ObjectToZonedDateTimeConverter converter1 = new ObjectToZonedDateTimeConverter();
        final StringToZonedDateTimeConverter converter2 = new StringToZonedDateTimeConverter();
        final ValueConverter valueConverter = new ValueConverter( new StaticObjectProvider<>( Arrays.asList( converter2, converter1 ) ), conversionService );
        testSameSourceClass( valueConverter );
    }

    @Test
    public void testDifferentType()
    {
        final StringToIntegerConverter converter1 = new StringToIntegerConverter();
        final StringToZonedDateTimeConverter converter2 = new StringToZonedDateTimeConverter();
        final ValueConverter valueConverter = new ValueConverter( new StaticObjectProvider<>( Arrays.asList( converter1, converter2 ) ), conversionService );

        Assert.assertNotNull( valueConverter.convert( "2017-08-04T17:28:43Z", ValueType.DATETIME, ZonedDateTime.class ) );
        Assert.assertEquals( (Integer) 12, valueConverter.convert( "12", ValueType.INTEGER_POSITIVE, Integer.class ) );
    }

    @Test
    public void testNull()
    {
        final StringToIntegerConverter converter1 = new StringToIntegerConverter();
        final ValueConverter valueConverter = new ValueConverter( new StaticObjectProvider<>( Collections.singletonList( converter1 ) ), conversionService );
        Assert.assertNull( valueConverter.convert( null, ValueType.INTEGER_POSITIVE, Integer.class ) );
    }

    @Test
    public void testSameClass()
    {
        final StringToIntegerConverter converter1 = new StringToIntegerConverter();
        final ValueConverter valueConverter = new ValueConverter( new StaticObjectProvider<>( Collections.singletonList( converter1 ) ), conversionService );
        Assert.assertEquals( "Test", valueConverter.convert( "Test", ValueType.INTEGER_POSITIVE, String.class ) );
    }

    @Test( expected = ConversionException.class )
    public void testNoConverter()
    {
        final StringToIntegerConverter converter1 = new StringToIntegerConverter();
        final ValueConverter valueConverter = new ValueConverter( new StaticObjectProvider<>( Collections.singletonList( converter1 ) ), conversionService );
        valueConverter.convert( new Date(), ValueType.DATETIME, String.class );
    }

    private void testSameSourceClass( ValueConverter valueConverter )
    {
        Assert.assertNotNull( valueConverter.convert( "2017-08-04T17:28:43Z", ValueType.DATETIME, ZonedDateTime.class ) );
        Assert.assertNotNull( valueConverter.convert( new Value( "xyz2017-08-04T17:28:43Zabc" ), ValueType.DATETIME, ZonedDateTime.class ) );
        try
        {
            valueConverter.convert( "xyz2017-08-04T17:28:43Zabc", ValueType.DATETIME, ZonedDateTime.class );
            Assert.fail( "Exception expected" );
        }
        catch ( ConversionException e )
        {
            // expected exception
        }
    }

    protected static class Value
    {
        private final String value;

        public Value( String value )
        {
            this.value = value;
        }

        @Override
        public String toString()
        {
            return value;
        }
    }
}