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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Unit tests of {@link EventServiceImpl}.
 *
 * @author volsch
 */
public class EventServiceImplTest
{
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private EventService service;

    private RequestCacheService requestCacheService;

    @Mock
    private PolledProgramRetriever polledProgramRetriever;

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
        service = new EventServiceImpl( restTemplate, requestCacheService, polledProgramRetriever );
    }

    @Test
    public void find() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/events.json?program=kgdyunhUgg&trackedEntityInstance=jdhshdfj&ouMode=ACCESSIBLE&fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status," +
            "eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D&skipPaging=true" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/events.json" ), MediaType.APPLICATION_JSON ) );

        Collection<Event> events = service.find( "kgdyunhUgg", "gjddShhdfgh", "Jskdsjeua1s", "jdhshdfj", false );
        Assert.assertEquals( 2, events.size() );

        mockServer.verify();
    }

    @Test
    public void findLocal() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/events.json?program=kgdyunhUgg&trackedEntityInstance=jdhshdfj&ouMode=ACCESSIBLE&fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status," +
            "eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D&skipPaging=true" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/events.json" ), MediaType.APPLICATION_JSON ) );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME,
                new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( Event.class ) ) );

            Collection<Event> events = service.find( "kgdyunhUgg", "gjddShhdfgh", "Jskdsjeua1s", "jdhshdfj", false );
            Assert.assertEquals( 2, events.size() );

            events = service.find( "kgdyunhUgg", "gjddShhdfgh", "Jskdsjeua1s", "jdhshdfj", false );
            Assert.assertEquals( 2, events.size() );
        }

        mockServer.verify();
    }

    @Test
    public void findLocalProgramDiffer() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/events.json?program=kgdyunhUgg&trackedEntityInstance=jdhshdfj&ouMode=ACCESSIBLE&fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status," +
            "eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D&skipPaging=true" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/events.json" ), MediaType.APPLICATION_JSON ) );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME,
                new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( Event.class ) ) );

            Collection<Event> events = service.find( "kgdyunhUgg", "gjddShhdfgh", "Jskdsjeua1s", "jdhshdfj", false );
            Assert.assertEquals( 2, events.size() );

            events = service.find( "kgdyunhUga", "gjddShhdfgh", "Jskdsjeua1s", "jdhshdfj", true );
            Assert.assertEquals( 0, events.size() );
        }

        mockServer.verify();
    }

    @Test
    public void findLocalProgramStageDiffer() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/events.json?program=kgdyunhUgg&trackedEntityInstance=jdhshdfj&ouMode=ACCESSIBLE&fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status," +
            "eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D&skipPaging=true" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/events.json" ), MediaType.APPLICATION_JSON ) );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME,
                new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( Event.class ) ) );

            Collection<Event> events = service.find( "kgdyunhUgg", "gjddShhdfgh", "Jskdsjeua1s", "jdhshdfj", false );
            Assert.assertEquals( 2, events.size() );

            events = service.find( "kgdyunhUgg", "ajddShhdfgh", "Jskdsjeua1s", "jdhshdfj", true );
            Assert.assertEquals( 0, events.size() );
        }

        mockServer.verify();
    }

    @Test
    public void findLocalTeiDiffer() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/events.json?program=kgdyunhUgg&trackedEntityInstance=jdhshdfj&ouMode=ACCESSIBLE&fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status," +
            "eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D&skipPaging=true" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/events.json" ), MediaType.APPLICATION_JSON ) );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME,
                new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( Event.class ) ) );

            Collection<Event> events = service.find( "kgdyunhUgg", "gjddShhdfgh", "Jskdsjeua1s", "jdhshdfj", false );
            Assert.assertEquals( 2, events.size() );

            events = service.find( "kgdyunhUgg", "gjddShhdfgh", "Jskdsjeua1s", "adhshdfj", true );
            Assert.assertEquals( 0, events.size() );
        }

        mockServer.verify();
    }

    @Test
    public void findLocalOnly()
    {
        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME,
                new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( Event.class ) ) );

            Collection<Event> events = service.find( "kgdyunhUgg", "gjddShhdfgh", "Jskdsjeua1s", "jdhshdfj", true );
            Assert.assertEquals( 0, events.size() );
        }

        mockServer.verify();
    }

    @Test
    public void findOneById() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/events/jShdkweusi2.json?fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated," +
            "dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/event.json" ), MediaType.APPLICATION_JSON ) );

        Optional<? extends Event> ou = service.findOneById( "jShdkweusi2" );
        Assert.assertTrue( ou.isPresent() );

        Assert.assertEquals( "jShdkweusi2", ou.get().getId() );
        Assert.assertEquals( "Jskdsjeua1s", ou.get().getEnrollmentId() );
        Assert.assertEquals( ZonedDateTime.of( 2018, 10, 5, 22, 12, 34, 998000000, ZoneId.systemDefault() ).toLocalDateTime(), ou.get().getEventDate().toLocalDateTime() );
        Assert.assertEquals( ZonedDateTime.of( 2018, 10, 6, 22, 12, 34, 998000000, ZoneId.systemDefault() ).toLocalDateTime(), ou.get().getDueDate().toLocalDateTime() );
        Assert.assertEquals( EventStatus.ACTIVE, ou.get().getStatus() );
        Assert.assertEquals( "jdhshdfj", ou.get().getTrackedEntityInstanceId() );
        Assert.assertEquals( "kgdyunhUgg", ou.get().getProgramId() );
        Assert.assertEquals( "gjddShhdfgh", ou.get().getProgramStageId() );
        Assert.assertEquals( "jhgtJgrygffg", ou.get().getOrgUnitId() );
        Assert.assertEquals( new Location( 64.89767, -86.78866 ), ou.get().getCoordinate() );
    }

    @Test
    public void findOneByIdNotFound()
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/events/N4cVHaUjfJO.json?fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate," +
            "coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withStatus( HttpStatus.NOT_FOUND ).contentType( MediaType.APPLICATION_JSON ) );

        Optional<? extends Event> ou = service.findOneById( "N4cVHaUjfJO" );
        Assert.assertFalse( ou.isPresent() );
    }

    @Test
    public void create() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/events.json?strategy=CREATE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/createEvent.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.POST ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/createEvent-response.json" ), MediaType.APPLICATION_JSON ) );

        final WritableDataValue dataValue1 = new WritableDataValue();
        dataValue1.setDataElementId( "dsf84sfsdf" );
        dataValue1.setProvidedElsewhere( true );
        dataValue1.setValue( "Test 1" );

        final WritableDataValue dataValue2 = new WritableDataValue();
        dataValue2.setDataElementId( "dsfdfj98js" );
        dataValue2.setProvidedElsewhere( true );
        dataValue2.setValue( "Test 2" );

        final Event event = new Event( true );
        event.setId( "jShdkweusi2" );
        event.setEnrollmentId( "Jskdsjeua1s" );
        event.setEventDate( ZonedDateTime.of( 2018, 10, 5, 22, 12, 34, 998000000, ZoneId.systemDefault() ) );
        event.setDueDate( ZonedDateTime.of( 2018, 10, 6, 22, 12, 34, 998000000, ZoneId.systemDefault() ) );
        event.setStatus( EventStatus.ACTIVE );
        event.setTrackedEntityInstanceId( "jdhshdfj" );
        event.setProgramId( "kgdyunhUgg" );
        event.setProgramStageId( "gjddShhdfgh" );
        event.setOrgUnitId( "jhgtJgrygffg" );
        event.setCoordinate( new Location( 64.89767, -86.78866 ) );
        event.setDataValues( Arrays.asList( dataValue1, dataValue2 ) );

        final Event createdEvent = service.createOrMinimalUpdate( event );
        Assert.assertSame( event, createdEvent );
        Assert.assertFalse( createdEvent.isNewResource() );
        Assert.assertNotNull( createdEvent.getLastUpdated() );

        mockServer.verify();
    }

    @Test
    public void update() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/events/jShdkweusi2.json?mergeMode=MERGE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/updateEvent.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.PUT ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/updateEvent-response.json" ), MediaType.APPLICATION_JSON ) );

        final Event event = new Event();
        event.setModified();
        event.setId( "jShdkweusi2" );
        event.setEnrollmentId( "Jskdsjeua1s" );
        event.setEventDate( ZonedDateTime.of( 2018, 10, 5, 22, 12, 34, 998000000, ZoneId.systemDefault() ) );
        event.setDueDate( ZonedDateTime.of( 2018, 10, 6, 22, 12, 34, 998000000, ZoneId.systemDefault() ) );
        event.setStatus( EventStatus.ACTIVE );
        event.setTrackedEntityInstanceId( "jdhshdfj" );
        event.setProgramId( "kgdyunhUgg" );
        event.setProgramStageId( "gjddShhdfgh" );
        event.setOrgUnitId( "jhgtJgrygffg" );
        event.setCoordinate( new Location( 64.89767, -86.78866 ) );
        event.setDataValues( Collections.emptyList() );

        final Event createdEvent = service.createOrMinimalUpdate( event );
        Assert.assertSame( event, createdEvent );
        Assert.assertFalse( createdEvent.isNewResource() );

        mockServer.verify();
    }

    @Test
    public void persistCreateUpdate() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/events.json?strategy=CREATE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/createEvents.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.POST ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/createEvents-response.json" ), MediaType.APPLICATION_JSON ) );
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/events.json?strategy=UPDATE&mergeMode=MERGE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/updateEvents.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.POST ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/updateEvents-response.json" ), MediaType.APPLICATION_JSON ) );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            final LocalDhisResourceRepositoryContainer repositoryContainer = new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( Event.class ) );
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME, repositoryContainer );

            final Event newEvent = new Event( true );
            newEvent.setId( "jShdkweusi2" );
            newEvent.setEnrollmentId( "Jskdsjeua2s" );
            newEvent.setStatus( EventStatus.ACTIVE );
            newEvent.setTrackedEntityInstanceId( "jdhshdfj" );
            newEvent.setProgramId( "kgdyunhUgg" );
            newEvent.setProgramStageId( "gjddShhdfgh" );
            newEvent.setOrgUnitId( "jhgtJgrygffg" );
            newEvent.setCoordinate( new Location( 64.89767, -86.78866 ) );
            newEvent.setDataValues( Collections.emptyList() );

            final Event existingEvent = new Event();
            existingEvent.setId( "jShdkweusi3" );
            existingEvent.setEnrollmentId( "Jskdsjeua2s" );
            existingEvent.setStatus( EventStatus.ACTIVE );
            existingEvent.setTrackedEntityInstanceId( "jdhshdfj" );
            existingEvent.setProgramId( "kgdyunhUgg" );
            existingEvent.setProgramStageId( "gjddShhdfgh" );
            existingEvent.setOrgUnitId( "jhgtJgrygffg" );
            existingEvent.setCoordinate( new Location( 64.89767, -86.78866 ) );
            existingEvent.setDataValues( Collections.emptyList() );

            final Event createdEvent = service.createOrMinimalUpdate( newEvent );
            Assert.assertSame( newEvent, createdEvent );
            Assert.assertFalse( createdEvent.isNewResource() );
            Assert.assertNotNull( createdEvent.getLastUpdated() );

            final Event updatedEvent = service.createOrMinimalUpdate( existingEvent );
            Assert.assertSame( existingEvent, updatedEvent );
            Assert.assertFalse( updatedEvent.isNewResource() );

            final List<String> persistedIds = new ArrayList<>();
            repositoryContainer.apply( ( resource, resourceKey, result ) -> persistedIds.add( resource.getId() ) );
            Assert.assertThat( persistedIds, Matchers.containsInAnyOrder( "jShdkweusi2", "jShdkweusi3" ) );
        }

        mockServer.verify();
    }

    @Test
    public void persistDelete() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/events.json?strategy=DELETE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/deleteEvents.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.POST ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/deleteEvents-response.json" ), MediaType.APPLICATION_JSON ) );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            final LocalDhisResourceRepositoryContainer repositoryContainer = new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( Event.class ) );
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
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/events/eskdsjeua1s" ) )
            .andExpect( method( HttpMethod.DELETE ) ).andRespond( withNoContent() );

        Assert.assertTrue( service.delete( "eskdsjeua1s" ) );

        mockServer.verify();
    }

    @Test
    public void deleteNotFound()
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/events/eskdsjeua1s" ) )
            .andExpect( method( HttpMethod.DELETE ) ).andRespond( withStatus( HttpStatus.NOT_FOUND ).contentType( MediaType.APPLICATION_JSON ) );

        Assert.assertFalse( service.delete( "eskdsjeua1s" ) );

        mockServer.verify();
    }
}