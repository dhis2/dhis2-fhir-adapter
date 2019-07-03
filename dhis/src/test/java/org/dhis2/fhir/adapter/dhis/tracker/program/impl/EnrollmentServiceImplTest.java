package org.dhis2.fhir.adapter.dhis.tracker.program.impl;

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

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.io.IOUtils;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.dhis.model.WritableDataValue;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.dhis2.fhir.adapter.geo.Location;
import org.dhis2.fhir.adapter.jackson.ZonedDateTimeDeserializer;
import org.dhis2.fhir.adapter.jackson.ZonedDateTimeSerializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Unit tests of {@link EnrollmentServiceImpl}.
 *
 * @author volsch
 */
public class EnrollmentServiceImplTest
{
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private EnrollmentService service;

    @Mock
    private EventService eventService;

    @Mock
    private RequestCacheService requestCacheService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp()
    {
        final MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        final SimpleModule testModule = new SimpleModule( "TestModule" );
        testModule.addSerializer( new ZonedDateTimeSerializer() );
        testModule.addDeserializer( ZonedDateTime.class, new ZonedDateTimeDeserializer() );
        messageConverter.getObjectMapper().registerModule( testModule );

        restTemplate = new RestTemplateBuilder().rootUri( "http://localhost:8080/api" ).messageConverters( messageConverter ).build();
        mockServer = MockRestServiceServer.createServer( restTemplate );
        service = new EnrollmentServiceImpl( restTemplate, eventService, requestCacheService );
    }

    @Test
    public void getLatestActive() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/enrollments.json?program=93783&programStatus=ACTIVE&trackedEntityInstance=88737&ouMode=ACCESSIBLE&fields=:all&order=lastUpdated:desc&pageSize=1" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/enrollments.json" ), MediaType.APPLICATION_JSON ) );

        Optional<? extends Enrollment> ou = service.findLatestActive( "93783", "88737", false );
        Assert.assertTrue( ou.isPresent() );

        Assert.assertEquals( "N4cVHaUjfJO", ou.get().getId() );
        Assert.assertEquals( "pMEnu7BjqMz", ou.get().getOrgUnitId() );
        Assert.assertEquals( "ur1Edk5Oe2n", ou.get().getProgramId() );
        Assert.assertEquals( "tPeQKGUIowE", ou.get().getTrackedEntityInstanceId() );
        Assert.assertEquals( EnrollmentStatus.COMPLETED, ou.get().getStatus() );
        Assert.assertEquals( ZonedDateTime.of( 2016, 10, 27, 12, 43, 46, 663000000, ZoneId.systemDefault() ), ou.get().getIncidentDate() );
        Assert.assertEquals( ZonedDateTime.of( 2016, 10, 30, 12, 42, 46, 663000000, ZoneId.systemDefault() ), ou.get().getEnrollmentDate() );
        Assert.assertEquals( new Location( -70.2433, 30.34323 ), ou.get().getCoordinate() );
    }

    public void create()
    {
        final WritableDataValue dataValue1 = new WritableDataValue();
        dataValue1.setDataElementId( "dsf84sfsdf" );
        dataValue1.setProvidedElsewhere( true );
        dataValue1.setValue( "Test 1" );

        final WritableDataValue dataValue2 = new WritableDataValue();
        dataValue2.setDataElementId( "dsfdfj98js" );
        dataValue2.setProvidedElsewhere( true );
        dataValue2.setValue( "Test 2" );

        final Event event = new Event();
        event.setEventDate( ZonedDateTime.of( 2018, 10, 5, 22, 12, 34, 998000000, ZoneId.systemDefault() ) );
        event.setDueDate( ZonedDateTime.of( 2018, 10, 6, 22, 12, 34, 998000000, ZoneId.systemDefault() ) );
        event.setStatus( EventStatus.ACTIVE );
        event.setTrackedEntityInstanceId( "jdhshdfj" );
        event.setProgramId( "kgdyunhUgg" );
        event.setProgramStageId( "gjddShhdfgh" );
        event.setOrgUnitId( "jhgtJgrygffg" );
        event.setCoordinate( new Location( 64.89767, -86.78866 ) );
        event.setEnrollmentId( "jghhffJgfjjhfg" );
        event.setDataValues( Arrays.asList( dataValue1, dataValue2 ) );
    }

}