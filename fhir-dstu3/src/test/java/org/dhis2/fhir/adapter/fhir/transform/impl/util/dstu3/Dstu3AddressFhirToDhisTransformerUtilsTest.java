package org.dhis2.fhir.adapter.fhir.transform.impl.util.dstu3;

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

import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Period;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Unit tests for {@link Dstu3AddressFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class Dstu3AddressFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @InjectMocks
    private Dstu3DateTimeFhirToDhisTransformerUtils dateTimeFhirToDhisTransformerUtils;

    private Dstu3AddressFhirToDhisTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void before()
    {
        utils = new Dstu3AddressFhirToDhisTransformerUtils( scriptExecutionContext, dateTimeFhirToDhisTransformerUtils );
    }

    @Test
    public void primaryExpired()
    {
        final List<Address> addresses = new ArrayList<>();
        addresses.add( new Address().setType( Address.AddressType.PHYSICAL ).setUse( Address.AddressUse.HOME )
            .setPeriod( new Period().setEnd( Date.from( ZonedDateTime.now().minusDays( 1 ).toInstant() ) ) ) );
        addresses.add( new Address().setType( Address.AddressType.POSTAL ).setUse( Address.AddressUse.OLD ) );
        Assert.assertSame( addresses.get( 1 ), utils.getPrimaryAddress( addresses ) );
    }

    @Test
    public void primaryTemp()
    {
        final List<Address> addresses = new ArrayList<>();
        addresses.add( new Address().setType( Address.AddressType.PHYSICAL ).setUse( Address.AddressUse.TEMP ) );
        addresses.add( new Address().setType( Address.AddressType.PHYSICAL ).setUse( Address.AddressUse.OLD ) );
        Assert.assertSame( addresses.get( 0 ), utils.getPrimaryAddress( addresses ) );
    }

    @Test
    public void primaryWork()
    {
        final List<Address> addresses = new ArrayList<>();
        addresses.add( new Address().setType( Address.AddressType.PHYSICAL ).setUse( Address.AddressUse.HOME ) );
        addresses.add( new Address().setType( Address.AddressType.PHYSICAL ).setUse( Address.AddressUse.WORK ) );
        Assert.assertSame( addresses.get( 0 ), utils.getPrimaryAddress( addresses ) );
    }

    @Test
    public void primaryNull()
    {
        final List<Address> addresses = new ArrayList<>();
        addresses.add( new Address().setType( Address.AddressType.PHYSICAL ).setUse( Address.AddressUse.NULL ) );
        addresses.add( new Address().setType( Address.AddressType.PHYSICAL ).setUse( Address.AddressUse.WORK ) );
        Assert.assertSame( addresses.get( 0 ), utils.getPrimaryAddress( addresses ) );
    }

    @Test
    public void primaryUnspecified()
    {
        final List<Address> addresses = new ArrayList<>();
        addresses.add( new Address().setType( Address.AddressType.PHYSICAL ) );
        addresses.add( new Address().setType( Address.AddressType.PHYSICAL ).setUse( Address.AddressUse.WORK ) );
        Assert.assertSame( addresses.get( 0 ), utils.getPrimaryAddress( addresses ) );
    }

    @Test
    public void primaryPhysical()
    {
        final List<Address> addresses = new ArrayList<>();
        addresses.add( new Address().setType( Address.AddressType.PHYSICAL ).setUse( Address.AddressUse.HOME ) );
        addresses.add( new Address().setType( Address.AddressType.POSTAL ).setUse( Address.AddressUse.HOME ) );
        Assert.assertSame( addresses.get( 0 ), utils.getPrimaryAddress( addresses ) );
    }

    @Test
    public void primaryBoth()
    {
        final List<Address> addresses = new ArrayList<>();
        addresses.add( new Address().setType( Address.AddressType.POSTAL ).setUse( Address.AddressUse.HOME ) );
        addresses.add( new Address().setType( Address.AddressType.BOTH ).setUse( Address.AddressUse.HOME ) );
        Assert.assertSame( addresses.get( 1 ), utils.getPrimaryAddress( addresses ) );
    }

    @Test
    public void primaryEmpty()
    {
        final List<Address> addresses = new ArrayList<>();
        Assert.assertNotNull( utils.getPrimaryAddress( addresses ) );
    }

    @Test
    public void getSingleLine()
    {
        final Address address = new Address().addLine( "Lower Road 20" ).addLine( "Apartment 19" );
        Assert.assertEquals( "Lower Road 20 Apartment 19", utils.getSingleLine( address ) );
    }

    @Test
    public void getSingleLineOneLine()
    {
        final Address address = new Address().addLine( "Lower Road 20" );
        Assert.assertEquals( "Lower Road 20", utils.getSingleLine( address ) );
    }

    @Test
    public void getConstructedText()
    {
        final Address address = new Address().addLine( "Lower Road 20" ).addLine( "Apartment 19" )
            .setCity( "Freetown" ).setPostalCode( "2009" ).setState( "Other State" ).setCountry( "Sierra Leone" );
        Assert.assertEquals( "Lower Road 20 / Apartment 19 / 2009 Freetown / Other State", utils.getConstructedText( address ) );
    }

    @Test
    public void getConstructedTextWithoutPostcode()
    {
        final Address address = new Address().addLine( "Lower Road 20" ).addLine( "Apartment 19" )
            .setCity( "Freetown" ).setState( "Other State" ).setCountry( "Sierra Leone" );
        Assert.assertEquals( "Lower Road 20 / Apartment 19 / Freetown / Other State", utils.getConstructedText( address ) );
    }

    @Test
    public void getText()
    {
        final Address address = new Address().addLine( "Lower Road 20" ).addLine( "Apartment 19" )
            .setCity( "Freetown" ).setState( "Other State" ).setCountry( "Sierra Leone" ).setText( "Summary Address" );
        Assert.assertEquals( "Summary Address", utils.getText( address ) );
    }
}