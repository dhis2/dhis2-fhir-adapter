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
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.geo.Location;
import org.dhis2.fhir.adapter.geo.StringToLocationConverter;
import org.dhis2.fhir.adapter.spring.StaticObjectProvider;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.DecimalType;
import org.hl7.fhir.dstu3.model.Location.LocationPositionComponent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Objects;

/**
 * Unit tests for {@link Dstu3GeoDhisToFhirTransformerUtils}.
 *
 * @author volsch
 */
public class Dstu3GeoDhisToFhirTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    private Dstu3GeoDhisToFhirTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void before()
    {
        final ConversionService conversionService = new DefaultConversionService();
        final ValueConverter valueConverter = new ValueConverter( new StaticObjectProvider<>(
            Collections.singletonList( new StringToLocationConverter() ) ) );
        utils = new Dstu3GeoDhisToFhirTransformerUtils( scriptExecutionContext, valueConverter );
    }

    @Test
    public void createPositionNull()
    {
        Assert.assertNull( utils.createPosition( null ) );
    }

    @Test
    public void createPosition()
    {
        final Location location = new Location( 47.1, -23.2 );
        final LocationPositionComponent position = (LocationPositionComponent) utils.createPosition( location );
        Assert.assertEquals( 47.1, Objects.requireNonNull( position ).getLongitude().doubleValue(), 0.0 );
        Assert.assertEquals( -23.2, Objects.requireNonNull( position ).getLatitude().doubleValue(), 0.0 );
    }

    @Test
    public void updateAddressLocationNull()
    {
        final Address address = new Address();
        utils.updateAddressLocation( address, null );
        Assert.assertEquals( 0, address.getExtensionsByUrl( "http://hl7.org/fhir/StructureDefinition/geolocation" ).size() );
    }

    @Test
    public void updateAddressLocationRemoved()
    {
        final Address address = new Address();
        address.addExtension().setUrl( "http://hl7.org/fhir/StructureDefinition/geolocation" );
        utils.updateAddressLocation( address, null );
        Assert.assertEquals( 0, address.getExtensionsByUrl( "http://hl7.org/fhir/StructureDefinition/geolocation" ).size() );
    }

    @Test
    public void updateAddressLocationAddress()
    {
        final Address address = new Address();
        final Location location = new Location( 47.1, -23.2 );
        utils.updateAddressLocation( address, location );
        Assert.assertEquals( 1, address.getExtensionsByUrl( "http://hl7.org/fhir/StructureDefinition/geolocation" ).size() );
        Assert.assertEquals( 1, address.getExtensionsByUrl( "http://hl7.org/fhir/StructureDefinition/geolocation" ).get( 0 )
            .getExtensionsByUrl( "longitude" ).size() );
        Assert.assertEquals( BigDecimal.valueOf( 47.1 ), ( (DecimalType) address.getExtensionsByUrl( "http://hl7.org/fhir/StructureDefinition/geolocation" ).get( 0 )
            .getExtensionsByUrl( "longitude" ).get( 0 ).getValue() ).getValueAsNumber() );
        Assert.assertEquals( 1, address.getExtensionsByUrl( "http://hl7.org/fhir/StructureDefinition/geolocation" ).get( 0 )
            .getExtensionsByUrl( "latitude" ).size() );
        Assert.assertEquals( BigDecimal.valueOf( -23.2 ), ( (DecimalType) address.getExtensionsByUrl( "http://hl7.org/fhir/StructureDefinition/geolocation" ).get( 0 )
            .getExtensionsByUrl( "latitude" ).get( 0 ).getValue() ).getValueAsNumber() );
    }
}
