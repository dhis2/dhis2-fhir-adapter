package org.dhis2.fhir.adapter.dhis.tracker.trackedentity.impl;

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
import org.dhis2.fhir.adapter.dhis.metadata.model.DhisSyncGroup;
import org.dhis2.fhir.adapter.dhis.sync.StoredDhisResourceService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.WritableTrackedEntityType;
import org.dhis2.fhir.adapter.jackson.ZonedDateTimeDeserializer;
import org.dhis2.fhir.adapter.jackson.ZonedDateTimeSerializer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Unit tests for {@link TrackedEntityServiceImpl}.
 *
 * @author volsch
 */
public class TrackedEntityServiceImplTest
{
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private TrackedEntityService service;

    @Mock
    private TrackedEntityMetadataService metadataService;

    @Mock
    private StoredDhisResourceService storedDhisResourceService;

    private RequestCacheService requestCacheService;

    private WritableTrackedEntityType trackedEntityType;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Before
    public void setUp()
    {
        final MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        final SimpleModule testModule = new SimpleModule( "TestModule" );
        testModule.addSerializer( new ZonedDateTimeSerializer() );
        testModule.addDeserializer( ZonedDateTime.class, new ZonedDateTimeDeserializer() );
        messageConverter.getObjectMapper().registerModule( testModule );

        requestCacheService = new RequestCacheServiceImpl();

        trackedEntityType = new WritableTrackedEntityType();
        trackedEntityType.setAttributes( Collections.emptyList() );

        restTemplate = new RestTemplateBuilder().rootUri( "http://localhost:8080/api" ).messageConverters( messageConverter ).build();
        mockServer = MockRestServiceServer.createServer( restTemplate );
        service = new TrackedEntityServiceImpl( restTemplate, requestCacheService, metadataService, storedDhisResourceService );

        Mockito.doReturn( Optional.of( new DhisSyncGroup() ) ).when( storedDhisResourceService ).findSyncGroupById( Mockito.eq( DhisSyncGroup.DEFAULT_ID ) );
    }

    @Test
    public void find() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/trackedEntityInstances.json?trackedEntityType=ur1Edk5Oe2n&ouMode=ACCESSIBLE&filter=tPeQKGUIowE:EQ:PT_72983&pageSize=2&fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit," +
            "coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/trackedEntityInstances.json" ), MediaType.APPLICATION_JSON ) );

        Collection<TrackedEntityInstance> trackedEntityInstances = service.findByAttrValue( "ur1Edk5Oe2n", "tPeQKGUIowE", "PT_72983", 2 );
        Assert.assertEquals( 1, trackedEntityInstances.size() );
    }

    @Test
    public void findLocal() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/trackedEntityInstances.json?trackedEntityType=ur1Edk5Oe2n&ouMode=ACCESSIBLE&filter=tPeQKGUIowE:EQ:PT_72983&pageSize=2&fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit," +
            "coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/trackedEntityInstances.json" ), MediaType.APPLICATION_JSON ) );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME,
                new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( TrackedEntityInstance.class ) ) );

            Collection<TrackedEntityInstance> trackedEntityInstances = service.findByAttrValue( "ur1Edk5Oe2n", "tPeQKGUIowE", "PT_72983", 2 );
            Assert.assertEquals( 1, trackedEntityInstances.size() );

            trackedEntityInstances = service.findByAttrValue( "ur1Edk5Oe2n", "tPeQKGUIowE", "PT_72983", 2 );
            Assert.assertEquals( 1, trackedEntityInstances.size() );
        }

        mockServer.verify();
    }

    @Test
    public void isLocal() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/trackedEntityInstances.json?strategy=CREATE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/createTrackedEntityInstance.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.POST ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/createTrackedEntityInstance-response.json" ), MediaType.APPLICATION_JSON ) );

        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance( trackedEntityType, "Jskdsjeua1s", true );
        trackedEntityInstance.setOrgUnitId( "pMEnu7BjqMz" );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME,
                new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( TrackedEntityInstance.class ) ) );

            final TrackedEntityInstance createdTrackedEntityInstance = service.createOrUpdate( trackedEntityInstance );
            Assert.assertTrue( service.isLocal( "Jskdsjeua1s" ) );
        }
    }

    @Test
    public void isLocalNot()
    {
        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            Assert.assertFalse( service.isLocal( "Jskdsjeua1s" ) );
        }
    }

    @Test
    public void isLocalNoContextNot()
    {
        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            Assert.assertFalse( service.isLocal( "Jskdsjeua1s" ) );
        }
    }

    @Test
    public void findOneById() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/trackedEntityInstances/Jskdsjeua1s.json?fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/trackedEntityInstance.json" ), MediaType.APPLICATION_JSON ) );

        Optional<? extends TrackedEntityInstance> ou = service.findOneById( "Jskdsjeua1s" );
        Assert.assertTrue( ou.isPresent() );

        Assert.assertEquals( "Jskdsjeua1s", ou.get().getId() );
        Assert.assertEquals( "pMEnu7BjqMz", ou.get().getOrgUnitId() );
    }

    @Test
    public void findOneByIdNotFound()
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/trackedEntityInstances/N4cVHaUjfJO.json?fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withStatus( HttpStatus.NOT_FOUND ).contentType( MediaType.APPLICATION_JSON ) );

        Optional<? extends TrackedEntityInstance> ou = service.findOneById( "N4cVHaUjfJO" );
        Assert.assertFalse( ou.isPresent() );
    }

    @Test
    public void create() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/trackedEntityInstances.json?strategy=CREATE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/createTrackedEntityInstance.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.POST ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/createTrackedEntityInstance-response.json" ), MediaType.APPLICATION_JSON ) );

        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance( trackedEntityType, "Jskdsjeua1s", true );
        trackedEntityInstance.setOrgUnitId( "pMEnu7BjqMz" );

        final TrackedEntityInstance createdTrackedEntityInstance = service.createOrUpdate( trackedEntityInstance );
        Assert.assertSame( trackedEntityInstance, createdTrackedEntityInstance );
        Assert.assertFalse( createdTrackedEntityInstance.isNewResource() );
        Assert.assertNotNull( createdTrackedEntityInstance.getLastUpdated() );

        mockServer.verify();
    }

    @Test
    public void update() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/trackedEntityInstances/Jskdsjeua1s.json?mergeMode=MERGE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/updateTrackedEntityInstance.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.PUT ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/updateTrackedEntityInstance-response.json" ), MediaType.APPLICATION_JSON ) );

        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance( trackedEntityType, "Jskdsjeua1s", false );
        trackedEntityInstance.setOrgUnitId( "pMEnu7BjqMz" );

        final TrackedEntityInstance createdTrackedEntityInstance = service.createOrUpdate( trackedEntityInstance );
        Assert.assertSame( trackedEntityInstance, createdTrackedEntityInstance );
        Assert.assertFalse( createdTrackedEntityInstance.isNewResource() );

        mockServer.verify();
    }

    @Test
    public void persistCreateUpdate() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/trackedEntityInstances.json?strategy=CREATE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/createTrackedEntityInstances.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.POST ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/createTrackedEntityInstances-response.json" ), MediaType.APPLICATION_JSON ) );
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/trackedEntityInstances.json?strategy=UPDATE&mergeMode=MERGE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/updateTrackedEntityInstances.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.POST ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/updateTrackedEntityInstances-response.json" ), MediaType.APPLICATION_JSON ) );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            final LocalDhisResourceRepositoryContainer repositoryContainer = new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( TrackedEntityInstance.class ) );
            cacheContext.setAttribute( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME, repositoryContainer );

            final TrackedEntityInstance newTrackedEntityInstance = new TrackedEntityInstance( trackedEntityType, "Jskdsjeua1s", true );
            newTrackedEntityInstance.setOrgUnitId( "pMEnu7BjqMz" );

            final TrackedEntityInstance existingTrackedEntityInstance = new TrackedEntityInstance( trackedEntityType, "Jskdsjeua2s", false );
            existingTrackedEntityInstance.setOrgUnitId( "pMEnu7BjqMz" );

            final TrackedEntityInstance createdTrackedEntityInstance = service.createOrUpdate( newTrackedEntityInstance );
            Assert.assertSame( newTrackedEntityInstance, createdTrackedEntityInstance );
            Assert.assertFalse( createdTrackedEntityInstance.isNewResource() );
            Assert.assertNotNull( createdTrackedEntityInstance.getLastUpdated() );

            final TrackedEntityInstance updatedTrackedEntityInstance = service.createOrUpdate( existingTrackedEntityInstance );
            Assert.assertSame( existingTrackedEntityInstance, updatedTrackedEntityInstance );
            Assert.assertFalse( createdTrackedEntityInstance.isNewResource() );

            final List<String> persistedIds = new ArrayList<>();
            repositoryContainer.apply( ( resource, resourceKey, result ) -> persistedIds.add( resource.getId() ) );
            Assert.assertThat( persistedIds, Matchers.containsInAnyOrder( "Jskdsjeua1s", "Jskdsjeua2s" ) );
        }

        mockServer.verify();
    }

    @Test
    public void persistDelete() throws IOException
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/trackedEntityInstances.json?strategy=DELETE" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/deleteTrackedEntityInstances.json", StandardCharsets.UTF_8 ) ) )
            .andExpect( method( HttpMethod.POST ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/deleteTrackedEntityInstances-response.json" ), MediaType.APPLICATION_JSON ) );

        try ( final RequestCacheContext cacheContext = requestCacheService.createRequestCacheContext() )
        {
            final LocalDhisResourceRepositoryContainer repositoryContainer = new LocalDhisResourceRepositoryContainerImpl( Collections.singleton( TrackedEntityInstance.class ) );
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
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/trackedEntityInstances/Jskdsjeua1s" ) )
            .andExpect( method( HttpMethod.DELETE ) ).andRespond( withNoContent() );

        Assert.assertTrue( service.delete( "Jskdsjeua1s" ) );

        mockServer.verify();
    }

    @Test
    public void deleteNotFound()
    {
        mockServer.expect( ExpectedCount.once(), requestTo( "http://localhost:8080/api/trackedEntityInstances/Jskdsjeua1s" ) )
            .andExpect( method( HttpMethod.DELETE ) ).andRespond( withStatus( HttpStatus.NOT_FOUND ).contentType( MediaType.APPLICATION_JSON ) );

        Assert.assertFalse( service.delete( "Jskdsjeua1s" ) );

        mockServer.verify();
    }
}