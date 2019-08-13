package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.r4;

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

import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.r4.R4ValueTypeFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.model.ValueType;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.TimeType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Unit tests for {@link R4ValueTypeFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class R4ValueTypeFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @InjectMocks
    private R4ValueTypeFhirToDhisTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void getFhirVersions()
    {
        Assert.assertEquals( FhirVersion.R4_ONLY, utils.getFhirVersions() );
    }

    @Test
    public void convertNull()
    {
        Assert.assertNull( utils.convert( null, ValueType.TEXT ) );
    }

    @Test
    public void convertText()
    {
        Assert.assertEquals( "Das ist ein Test!", utils.convert( new StringType( "Das ist ein Test!" ), "text" ) );
    }

    @Test
    public void convertTextCode()
    {
        Assert.assertEquals( "test1", utils.convert( new Coding().setCode( "test1" ), "Text" ) );
    }

    @Test( expected = TransformerDataException.class )
    public void convertTextCodeWithSystem()
    {
        utils.convert( new CodeableConcept().addCoding( new Coding().setSystem( "abc" ).setCode( "test1" ) ), "text" );
    }

    @Test
    public void convertInteger()
    {
        Assert.assertEquals( "736384", utils.convert( new IntegerType( 736384 ), "integer" ) );
    }

    @Test
    public void convertIntegerCode()
    {
        Assert.assertEquals( "736384", utils.convert( new Coding().setCode( "736384" ), "integer" ) );
    }

    @Test( expected = TransformerDataException.class )
    public void convertIntegerInvalid()
    {
        utils.convert( new TimeType( "12:47:21" ), "integer" );
    }

    @Test
    public void convertNumber()
    {
        Assert.assertEquals( "736384.5", utils.convert( new DecimalType( 736384.5 ), "number" ) );
    }

    @Test( expected = TransformerDataException.class )
    public void convertNumberInvalid()
    {
        utils.convert( new TimeType( "12:47:21" ), "Number" );
    }

    @Test
    public void convertDateTime()
    {
        final LocalDateTime expected = ZonedDateTime.of( 2019, 7, 21, 16, 48, 12, 0, ZoneId.ofOffset( "UTC", ZoneOffset.ofHours( 7 ) ) )
            .withZoneSameInstant( ZoneId.systemDefault() ).toLocalDateTime();

        Assert.assertEquals( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( expected.atZone( ZoneId.systemDefault() ) ),
            utils.convert( new DateTimeType( "2019-07-21T16:48:12+07:00" ), "datetime" ) );
    }

    @Test( expected = TransformerDataException.class )
    public void convertDateTimeInvalid()
    {
        utils.convert( new TimeType( "12:47:21" ), "datetime" );
    }

    @Test
    public void convertTime()
    {
        Assert.assertEquals( "12:47:21", utils.convert( new TimeType( "12:47:21" ), "time" ) );
    }

    @Test( expected = TransformerDataException.class )
    public void convertTimeInvalid()
    {
        utils.convert( new DateTimeType( new Date() ), "time" );
    }

    @Test
    public void convertBoolean()
    {
        Assert.assertEquals( "true", utils.convert( new BooleanType( true ), "boolean" ) );
    }

    @Test( expected = TransformerDataException.class )
    public void convertBooleanInvalid()
    {
        utils.convert( new DateTimeType( new Date() ), "boolean" );
    }

    @Test( expected = TransformerDataException.class )
    public void convertUnsupportedValueType()
    {
        utils.convert( new DateTimeType( new Date() ), "image" );
    }
}