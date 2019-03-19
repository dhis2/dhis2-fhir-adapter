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
import org.dhis2.fhir.adapter.fhir.converter.dstu3.Dstu3StringToAdministrativeGenderConverter;
import org.dhis2.fhir.adapter.fhir.metadata.model.Constant;
import org.dhis2.fhir.adapter.fhir.metadata.model.ConstantResolver;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.spring.StaticObjectProvider;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.Collections;
import java.util.Optional;

/**
 * Unit tests for {@link Dstu3AdministrativeGenderDhisToFhirTransformerUtils}.
 *
 * @author volsch
 */
public class Dstu3AdministrativeGenderDhisToFhirTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private ConstantResolver constantResolver;

    private Dstu3AdministrativeGenderDhisToFhirTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void before()
    {
        final ConversionService conversionService = new DefaultConversionService();
        final ValueConverter valueConverter = new ValueConverter( new StaticObjectProvider<>(
            Collections.singletonList( new Dstu3StringToAdministrativeGenderConverter( constantResolver ) ) ),
            conversionService );
        utils = new Dstu3AdministrativeGenderDhisToFhirTransformerUtils( scriptExecutionContext, valueConverter );
    }

    @Test
    public void getAdministrativeGenderMale()
    {
        final Constant maleConstant = new Constant();
        maleConstant.setCode( "GENDER_MALE" );
        maleConstant.setValue( "Male" );

        final Constant femaleConstant = new Constant();
        femaleConstant.setCode( "GENDER_FEMALE" );
        femaleConstant.setValue( "Female" );

        Mockito.when( constantResolver.findOneByCode( Mockito.eq( "GENDER_MALE" ) ) ).thenReturn( Optional.of( maleConstant ) );
        Mockito.when( constantResolver.findOneByCode( Mockito.eq( "GENDER_FEMALE" ) ) ).thenReturn( Optional.of( femaleConstant ) );

        Assert.assertEquals( Enumerations.AdministrativeGender.MALE, utils.getAdministrativeGender( "male" ) );
    }

    @Test
    public void getAdministrativeGenderFemale()
    {
        final Constant maleConstant = new Constant();
        maleConstant.setCode( "GENDER_MALE" );
        maleConstant.setValue( "Male" );

        final Constant femaleConstant = new Constant();
        femaleConstant.setCode( "GENDER_FEMALE" );
        femaleConstant.setValue( "Female" );

        Mockito.when( constantResolver.findOneByCode( Mockito.eq( "GENDER_MALE" ) ) ).thenReturn( Optional.of( maleConstant ) );
        Mockito.when( constantResolver.findOneByCode( Mockito.eq( "GENDER_FEMALE" ) ) ).thenReturn( Optional.of( femaleConstant ) );

        Assert.assertEquals( Enumerations.AdministrativeGender.FEMALE, utils.getAdministrativeGender( "FEMALE" ) );
    }
}