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

import org.dhis2.fhir.adapter.fhir.metadata.model.Constant;
import org.dhis2.fhir.adapter.fhir.metadata.model.ConstantResolver;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Optional;

/**
 * Unit tests for {@link Dstu3AdministrativeGenderToStringConverter}.
 *
 * @author volsch
 */
public class Dstu3AdministrativeGenderToStringConverterTest
{
    @Mock
    private ConstantResolver constantResolver;

    @InjectMocks
    private Dstu3AdministrativeGenderToStringConverter converter;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void testMale()
    {
        final Constant constant = new Constant();
        constant.setValue( "MaLe" );
        Mockito.doReturn( Optional.of( constant ) ).when( constantResolver ).findOneByCode( Mockito.eq( "GENDER_MALE" ) );
        Assert.assertEquals( "MaLe", converter.convert( Enumerations.AdministrativeGender.MALE ) );
    }

    @Test
    public void testFemale()
    {
        final Constant constant = new Constant();
        constant.setValue( "FeMaLe" );
        Mockito.doReturn( Optional.of( constant ) ).when( constantResolver ).findOneByCode( Mockito.eq( "GENDER_FEMALE" ) );
        Assert.assertEquals( "FeMaLe", converter.convert( Enumerations.AdministrativeGender.FEMALE ) );
    }

    @Test
    public void testNull()
    {
        Assert.assertNull( converter.convert( Enumerations.AdministrativeGender.NULL ) );
    }

    @Test
    public void testUnknown()
    {
        Assert.assertNull( converter.convert( Enumerations.AdministrativeGender.UNKNOWN ) );
    }

    @Test
    public void testOther()
    {
        Assert.assertNull( converter.convert( Enumerations.AdministrativeGender.OTHER ) );
    }

    @Test
    public void testNullValue()
    {
        Assert.assertNull( converter.convert( null ) );
    }
}