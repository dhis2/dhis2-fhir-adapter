package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.dstu3;

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

import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.WritableOption;
import org.dhis2.fhir.adapter.dhis.model.WritableOptionSet;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.hl7.fhir.dstu3.model.Base;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.DecimalType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.TimeType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;

/**
 * Unit tests for {@link Dstu3ValueTypeDhisToFhirTransformerUtils}.
 *
 * @author volsch
 */
public class Dstu3ValueTypeDhisToFhirTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private ValueConverter valueConverter;

    @InjectMocks
    private Dstu3ValueTypeDhisToFhirTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().silent();

    @Before
    public void setUp()
    {
        Mockito.when( valueConverter.convert( Mockito.any(), Mockito.any(), Mockito.eq( String.class ) ) )
            .thenAnswer( invocation -> invocation.getArgument( 0 ) );
    }

    @Test
    public void getFhirVersions()
    {
        Assert.assertEquals( FhirVersion.DSTU3_ONLY, utils.getFhirVersions() );
    }

    @Test
    public void convertNull()
    {
        Assert.assertNull( utils.convert( null, "text", null ) );
    }

    @Test
    public void convertText()
    {
        Assert.assertTrue( new StringType( "This is a test" ).equalsDeep( (Base) utils.convert( "This is a test", "text", null ) ) );

        Mockito.verify( valueConverter ).convert( Mockito.any(), Mockito.any(), Mockito.eq( String.class ) );
    }

    @Test( expected = TransformerDataException.class )
    public void convertUnsupportedType()
    {
        utils.convert( "This is a test", "image", null );

        Mockito.verify( valueConverter ).convert( Mockito.any(), Mockito.any(), Mockito.eq( String.class ) );
    }

    @Test
    public void convertInteger()
    {
        Assert.assertTrue( new IntegerType( 29837 ).equalsDeep( (Base) utils.convert( "29837", "Integer", null ) ) );

        Mockito.verify( valueConverter ).convert( Mockito.any(), Mockito.any(), Mockito.eq( String.class ) );
    }

    @Test( expected = TransformerDataException.class )
    public void convertIntegerDataFormat()
    {
        utils.convert( "298x37", "Integer", null );
    }

    @Test
    public void convertNumber()
    {
        Assert.assertTrue( new DecimalType( 29837.5 ).equalsDeep( (Base) utils.convert( "29837.5", "Number", null ) ) );

        Mockito.verify( valueConverter ).convert( Mockito.any(), Mockito.any(), Mockito.eq( String.class ) );
    }

    @Test
    public void convertBoolean()
    {
        Assert.assertTrue( new BooleanType( true ).equalsDeep( (Base) utils.convert( "true", "BOOLEAN", null ) ) );

        Mockito.verify( valueConverter ).convert( Mockito.any(), Mockito.any(), Mockito.eq( String.class ) );
    }

    @Test
    public void convertTime()
    {
        Assert.assertTrue( new TimeType( "17:48:12" ).equalsDeep( (Base) utils.convert( "17:48:12", "Time", null ) ) );

        Mockito.verify( valueConverter ).convert( Mockito.any(), Mockito.any(), Mockito.eq( String.class ) );
    }

    @Test
    public void convertDateTime()
    {
        Assert.assertTrue( new DateTimeType( Date.from( ZonedDateTime.of( 2018, 7, 21, 17, 48, 12, 0, ZoneId.systemDefault() ).toInstant() ) )
            .equalsDeep( (Base) utils.convert( "2018-07-21T17:48:12", "datetime", null ) ) );

        Mockito.verify( valueConverter ).convert( Mockito.any(), Mockito.any(), Mockito.eq( String.class ) );
    }

    @Test
    public void convertOption()
    {
        final WritableOptionSet optionSet = new WritableOptionSet();
        optionSet.setOptions( Collections.singletonList( new WritableOption( "test1", "Test 1" ) ) );

        Assert.assertTrue( new Coding().setCode( "test1" ).setDisplay( "Test 1" )
            .equalsDeep( (Base) utils.convert( "test1", "text", optionSet ) ) );

        Mockito.verify( valueConverter ).convert( Mockito.any(), Mockito.any(), Mockito.eq( String.class ) );
    }

    @Test
    public void convertOptionNotFound()
    {
        final WritableOptionSet optionSet = new WritableOptionSet();
        optionSet.setOptions( Collections.singletonList( new WritableOption( "test1", "Test 1" ) ) );

        Assert.assertTrue( new Coding().setCode( "test2" ).setDisplay( "test2" )
            .equalsDeep( (Base) utils.convert( "test2", "text", optionSet ) ) );

        Mockito.verify( valueConverter ).convert( Mockito.any(), Mockito.any(), Mockito.eq( String.class ) );
    }
}