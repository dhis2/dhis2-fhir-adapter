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
import org.dhis2.fhir.adapter.cache.RequestCacheContext;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.cache.impl.RequestCacheServiceImpl;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisResourceRepositoryContainer;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisResourceRepositoryTemplate;
import org.dhis2.fhir.adapter.dhis.local.impl.LocalDhisResourceRepositoryContainerImpl;
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
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

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

        requestCacheService = new RequestCacheServiceImpl();

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
        Assert.assertEquals( EnrollmentStatus.ACTIVE, ou.get().getStatus() );
        Assert.assertEquals( ZonedDateTime.of( 2016, 10, 27, 12, 43, 46, 663000000, ZoneId.systemDefault() ), ou.get().getIncidentDate() );
        Assert.assertEquals( ZonedDateTime.of( 2016, 10, 30, 12, 42, 46, 663000000, ZoneId.systemDefault() ), ou.get().getEnrollmentDate() );
        Assert.assertEquals( new Location( -70.2433, 30.34323 ), ou.get().getCoordinate() );
    }

    @Test
    public void getLatestActiveLocal() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/enrollments.json?program=ur1Edk5Oe2n&programStatus=ACTIVE&trackedEntityInstance=tPeQKGUIowE&ouMode=ACCESSIBLE&fields=:all&order=lastUpdated:desc&pageSize=1" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/enrollments.json" ), MediaType.APPLICATION_JSON ) );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME,
                new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( Enrollment.class ) ) );

            Optional<? extends Enrollment> ou = service.findLatestActive( "ur1Edk5Oe2n", "tPeQKGUIowE", false );
            Assert.assertTrue( ou.isPresent() );

            Assert.assertEquals( "N4cVHaUjfJO", ou.get().getId() );
            Assert.assertEquals( "pMEnu7BjqMz", ou.get().getOrgUnitId() );
            Assert.assertEquals( "ur1Edk5Oe2n", ou.get().getProgramId() );
            Assert.assertEquals( "tPeQKGUIowE", ou.get().getTrackedEntityInstanceId() );
            Assert.assertEquals( EnrollmentStatus.ACTIVE, ou.get().getStatus() );
            Assert.assertEquals( ZonedDateTime.of( 2016, 10, 27, 12, 43, 46, 663000000, ZoneId.systemDefault() ), ou.get().getIncidentDate() );
            Assert.assertEquals( ZonedDateTime.of( 2016, 10, 30, 12, 42, 46, 663000000, ZoneId.systemDefault() ), ou.get().getEnrollmentDate() );
            Assert.assertEquals( new Location( -70.2433, 30.34323 ), ou.get().getCoordinate() );

            ou = service.findLatestActive( "ur1Edk5Oe2n", "tPeQKGUIowE", false );
            Assert.assertTrue( ou.isPresent() );

            Assert.assertEquals( "N4cVHaUjfJO", ou.get().getId() );
            Assert.assertEquals( "pMEnu7BjqMz", ou.get().getOrgUnitId() );
            Assert.assertEquals( "ur1Edk5Oe2n", ou.get().getProgramId() );
            Assert.assertEquals( "tPeQKGUIowE", ou.get().getTrackedEntityInstanceId() );
            Assert.assertEquals( EnrollmentStatus.ACTIVE, ou.get().getStatus() );
            Assert.assertEquals( ZonedDateTime.of( 2016, 10, 27, 12, 43, 46, 663000000, ZoneId.systemDefault() ), ou.get().getIncidentDate() );
            Assert.assertEquals( ZonedDateTime.of( 2016, 10, 30, 12, 42, 46, 663000000, ZoneId.systemDefault() ), ou.get().getEnrollmentDate() );
            Assert.assertEquals( new Location( -70.2433, 30.34323 ), ou.get().getCoordinate() );
        }

        mockServer.verify();
    }

    @Test
    public void getLatestActiveLocalProgramDiffer() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/enrollments.json?program=ur1Edk5Oe2n&programStatus=ACTIVE&trackedEntityInstance=tPeQKGUIowE&ouMode=ACCESSIBLE&fields=:all&order=lastUpdated:desc&pageSize=1" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/enrollments.json" ), MediaType.APPLICATION_JSON ) );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME,
                new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( Enrollment.class ) ) );

            Optional<? extends Enrollment> ou = service.findLatestActive( "ur1Edk5Oe2n", "tPeQKGUIowE", false );
            Assert.assertTrue( ou.isPresent() );

            ou = service.findLatestActive( "ar1Edk5Oe2n", "tPeQKGUIowE", true );
            Assert.assertFalse( ou.isPresent() );
        }

        mockServer.verify();
    }

    @Test
    public void getLatestActiveLocalTeiDiffer() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/enrollments.json?program=ur1Edk5Oe2n&programStatus=ACTIVE&trackedEntityInstance=tPeQKGUIowE&ouMode=ACCESSIBLE&fields=:all&order=lastUpdated:desc&pageSize=1" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/enrollments.json" ), MediaType.APPLICATION_JSON ) );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME,
                new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( Enrollment.class ) ) );

            Optional<? extends Enrollment> ou = service.findLatestActive( "ur1Edk5Oe2n", "tPeQKGUIowE", false );
            Assert.assertTrue( ou.isPresent() );

            ou = service.findLatestActive( "ur1Edk5Oe2n", "aPeQKGUIowE", true );
            Assert.assertFalse( ou.isPresent() );
        }

        mockServer.verify();
    }

    @Test
    public void getLatestActiveLocalOnly()
    {
        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME,
                new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( Enrollment.class ) ) );

            Optional<? extends Enrollment> ou = service.findLatestActive( "ur1Edk5Oe2n", "tPeQKGUIowE", true );
            Assert.assertFalse( ou.isPresent() );
        }

        mockServer.verify();
    }

    @Test
    public void findOneById() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/enrollments/N4cVHaUjfJO.json" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/enrollment.json" ), MediaType.APPLICATION_JSON ) );

        Optional<? extends Enrollment> ou = service.findOneById( "N4cVHaUjfJO" );
        Assert.assertTrue( ou.isPresent() );

        Assert.assertEquals( "N4cVHaUjfJO", ou.get().getId() );
        Assert.assertEquals( "pMEnu7BjqMz", ou.get().getOrgUnitId() );
        Assert.assertEquals( "ur1Edk5Oe2n", ou.get().getProgramId() );
        Assert.assertEquals( "tPeQKGUIowE", ou.get().getTrackedEntityInstanceId() );
        Assert.assertEquals( EnrollmentStatus.ACTIVE, ou.get().getStatus() );
        Assert.assertEquals( ZonedDateTime.of( 2016, 10, 27, 12, 43, 46, 663000000, ZoneId.systemDefault() ), ou.get().getIncidentDate() );
        Assert.assertEquals( ZonedDateTime.of( 2016, 10, 30, 12, 42, 46, 663000000, ZoneId.systemDefault() ), ou.get().getEnrollmentDate() );
        Assert.assertEquals( new Location( -70.2433, 30.34323 ), ou.get().getCoordinate() );
    }

    @Test
    public void findOneByIdNotFound()
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/enrollments/N4cVHaUjfJO.json" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withStatus( HttpStatus.NOT_FOUND ).contentType( MediaType.APPLICATION_JSON ) );

        Optional<? extends Enrollment> ou = service.findOneById( "N4cVHaUjfJO" );
        Assert.assertFalse( ou.isPresent() );
    }

    @Test
    public void create() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/enrollments.json?strategy=CREATE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/createEnrollment.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.POST ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/createEnrollment-response.json" ), MediaType.APPLICATION_JSON ) );

        final WritableDataValue dataValue1 = new WritableDataValue();
        dataValue1.setDataElementId( "dsf84sfsdf" );
        dataValue1.setProvidedElsewhere( true );
        dataValue1.setValue( "Test 1" );

        final WritableDataValue dataValue2 = new WritableDataValue();
        dataValue2.setDataElementId( "dsfdfj98js" );
        dataValue2.setProvidedElsewhere( true );
        dataValue2.setValue( "Test 2" );

        final Event event = new Event();
        event.setId( "jShdkweusi2" );
        event.setEventDate( ZonedDateTime.of( 2018, 10, 5, 22, 12, 34, 998000000, ZoneId.systemDefault() ) );
        event.setDueDate( ZonedDateTime.of( 2018, 10, 6, 22, 12, 34, 998000000, ZoneId.systemDefault() ) );
        event.setStatus( EventStatus.ACTIVE );
        event.setTrackedEntityInstanceId( "jdhshdfj" );
        event.setProgramId( "kgdyunhUgg" );
        event.setProgramStageId( "gjddShhdfgh" );
        event.setOrgUnitId( "jhgtJgrygffg" );
        event.setCoordinate( new Location( 64.89767, -86.78866 ) );
        event.setDataValues( Arrays.asList( dataValue1, dataValue2 ) );

        final Enrollment enrollment = new Enrollment( true );
        enrollment.setStatus( EnrollmentStatus.ACTIVE );
        enrollment.setId( "Jskdsjeua1s" );
        enrollment.setTrackedEntityInstanceId( "hsdgshdjSa2" );
        enrollment.setOrgUnitId( "pMEnu7BjqMz" );
        enrollment.setProgramId( "ur1Edk5Oe2n" );
        enrollment.setEvents( Collections.singletonList( event ) );

        final Enrollment createdEnrollment = service.createOrUpdate( enrollment );
        Assert.assertSame( enrollment, createdEnrollment );
        Assert.assertFalse( createdEnrollment.isNewResource() );
        Assert.assertNotNull( createdEnrollment.getLastUpdated() );

        mockServer.verify();
    }

    @Test
    public void update() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/enrollments/Jskdsjeua1s.json?mergeMode=MERGE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/updateEnrollment.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.PUT ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/updateEnrollment-response.json" ), MediaType.APPLICATION_JSON ) );

        final Event event = new Event();
        event.setId( "jShdkweusi2" );
        event.setEventDate( ZonedDateTime.of( 2018, 10, 5, 22, 12, 34, 998000000, ZoneId.systemDefault() ) );
        event.setDueDate( ZonedDateTime.of( 2018, 10, 6, 22, 12, 34, 998000000, ZoneId.systemDefault() ) );
        event.setStatus( EventStatus.ACTIVE );
        event.setTrackedEntityInstanceId( "jdhshdfj" );
        event.setProgramId( "kgdyunhUgg" );
        event.setProgramStageId( "gjddShhdfgh" );
        event.setOrgUnitId( "jhgtJgrygffg" );
        event.setCoordinate( new Location( 64.89767, -86.78866 ) );
        event.setDataValues( Collections.emptyList() );

        final Enrollment enrollment = new Enrollment( false );
        enrollment.setStatus( EnrollmentStatus.ACTIVE );
        enrollment.setId( "Jskdsjeua1s" );
        enrollment.setTrackedEntityInstanceId( "hsdgshdjSa2" );
        enrollment.setOrgUnitId( "pMEnu7BjqMz" );
        enrollment.setProgramId( "ur1Edk5Oe2n" );
        enrollment.setEvents( Collections.singletonList( event ) );

        final Enrollment createdEnrollment = service.createOrUpdate( enrollment );
        Assert.assertSame( enrollment, createdEnrollment );
        Assert.assertFalse( createdEnrollment.isNewResource() );

        mockServer.verify();
    }

    @Test
    public void persistCreateUpdate() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/enrollments.json?strategy=CREATE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/createEnrollments.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.POST ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/createEnrollments-response.json" ), MediaType.APPLICATION_JSON ) );
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/enrollments.json?strategy=UPDATE&mergeMode=MERGE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/updateEnrollments.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.POST ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/updateEnrollments-response.json" ), MediaType.APPLICATION_JSON ) );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            final LocalDhisResourceRepositoryContainer repositoryContainer = new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( Enrollment.class ) );
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME, repositoryContainer );

            final Event event = new Event();
            event.setId( "jShdkweusi2" );
            event.setStatus( EventStatus.ACTIVE );
            event.setTrackedEntityInstanceId( "jdhshdfj" );
            event.setProgramId( "kgdyunhUgg" );
            event.setProgramStageId( "gjddShhdfgh" );
            event.setOrgUnitId( "jhgtJgrygffg" );
            event.setCoordinate( new Location( 64.89767, -86.78866 ) );

            final Enrollment newEnrollment = new Enrollment( true );
            newEnrollment.setStatus( EnrollmentStatus.ACTIVE );
            newEnrollment.setId( "Jskdsjeua1s" );
            newEnrollment.setTrackedEntityInstanceId( "hsdgshdjSa2" );
            newEnrollment.setOrgUnitId( "pMEnu7BjqMz" );
            newEnrollment.setProgramId( "ur1Edk5Oe2n" );
            newEnrollment.setEvents( Collections.singletonList( event ) );

            final Enrollment existingEnrollment = new Enrollment( false );
            existingEnrollment.setStatus( EnrollmentStatus.ACTIVE );
            existingEnrollment.setId( "Jskdsjeua2s" );
            existingEnrollment.setTrackedEntityInstanceId( "hsdgshdjSa2" );
            existingEnrollment.setOrgUnitId( "pMEnu7BjqMz" );
            existingEnrollment.setProgramId( "ur1Edk5Oe2n" );
            existingEnrollment.setEvents( Collections.emptyList() );

            final Enrollment createdEnrollment = service.createOrUpdate( newEnrollment );
            Assert.assertSame( newEnrollment, createdEnrollment );
            Assert.assertFalse( createdEnrollment.isNewResource() );
            Assert.assertNotNull( createdEnrollment.getLastUpdated() );

            final Enrollment updatedEnrollment = service.createOrUpdate( existingEnrollment );
            Assert.assertSame( existingEnrollment, updatedEnrollment );
            Assert.assertFalse( createdEnrollment.isNewResource() );

            final List<String> persistedIds = new ArrayList<>();
            repositoryContainer.apply( ( resource, resourceKey, result ) -> persistedIds.add( resource.getId() ) );
            Assert.assertThat( persistedIds, Matchers.containsInAnyOrder( "Jskdsjeua1s", "Jskdsjeua2s" ) );
        }

        mockServer.verify();
    }

    @Test
    public void persistDelete() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/enrollments.json?strategy=DELETE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/deleteEnrollments.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.POST ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/deleteEnrollments-response.json" ), MediaType.APPLICATION_JSON ) );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            final LocalDhisResourceRepositoryContainer repositoryContainer = new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( Enrollment.class ) );
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME, repositoryContainer );

            Assert.assertTrue( service.delete( "Jskdsjeua1s" ) );
            Assert.assertTrue( service.delete( "Jskdsjeua2s" ) );

            final List<String> persistedIds = new ArrayList<>();
            repositoryContainer.apply( ( resource, resourceKey, result ) -> persistedIds.add( resource.getId() ) );
            Assert.assertThat( persistedIds, Matchers.containsInAnyOrder( "Jskdsjeua1s", "Jskdsjeua2s" ) );
        }

        mockServer.verify();
    }

    @Test
    public void delete()
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/enrollments/Jskdsjeua1s" ) )
            .andExpect( method( HttpMethod.DELETE ) ).andRespond( withNoContent() );

        Assert.assertTrue( service.delete( "Jskdsjeua1s" ) );

        mockServer.verify();
    }

    @Test
    public void deleteNotFound()
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/enrollments/Jskdsjeua1s" ) )
            .andExpect( method( HttpMethod.DELETE ) ).andRespond( withStatus( HttpStatus.NOT_FOUND ).contentType( MediaType.APPLICATION_JSON ) );

        Assert.assertFalse( service.delete( "Jskdsjeua1s" ) );

        mockServer.verify();
    }
}