package org.dhis2.fhir.adapter;

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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientRequestor;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.management.ManagementHelper;
import org.apache.activemq.artemis.jms.client.ActiveMQSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.security.AdapterSystemAuthenticationToken;
import org.hl7.fhir.instance.model.api.IIdType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Abstract base class for application tests. All interfaces will be setup.
 *
 * @author volsch
 */
@RunWith( Dhis2SpringRunner.class )
@SpringBootTest( classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
@TestPropertySource( "classpath:test.properties" )
@AutoConfigureMockMvc
public abstract class AbstractAppTest
{
    public static final MediaType FHIR_JSON_MEDIA_TYPE = MediaType.parseMediaType( "application/fhir+json;charset=UTF-8" );

    public static final MediaType FHIR_XML_MEDIA_TYPE = MediaType.parseMediaType( "application/fhir+xml;charset=UTF-8" );

    public static final long MAX_COMPLETED_POLL_TIME = 10 * 60_000;

    private static final String RESOURCE_DL_QUEUE_NAME = "jms.queue.remoteFhirResourceDlQueue";

    @LocalServerPort
    protected int localPort;

    @Autowired
    protected TestRestTemplate localRestTemplate;

    /**
     * The DHIS2 client that is accessed to retrieve metadata.
     */
    protected MockRestServiceServer systemDhis2Server;

    /**
     * The DHIS2 client that is accessed to retrieve non-metadata
     * as user that is assigned to the FHIR client service.
     */
    protected MockRestServiceServer userDhis2Server;

    @Value( "${dhis2.fhir-adapter.endpoint.url}" )
    protected String dhis2BaseUrl;

    @Value( "${dhis2.fhir-adapter.endpoint.api-version}" )
    protected String dhis2ApiVersion;

    @Autowired
    @Qualifier( "fhirMockServer" )
    protected WireMockServer fhirMockServer;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected TestConfiguration testConfiguration;

    /**
     * The REST template that is used to connect to the DHIS2 metadata services.
     */
    @Autowired
    @Qualifier( "systemDhis2RestTemplate" )
    private RestTemplate systemDhis2RestTemplate;

    /**
     * The REST template that is used to connect to the DHIS2 non-metadata services
     * as user that is assigned to the FHIR client service.
     */
    @Autowired
    @Qualifier( "userDhis2RestTemplate" )
    private RestTemplate userDhis2RestTemplate;

    private StubMapping previousResourceSearchStubMapping;

    @Autowired
    @Qualifier( "metadataCacheManager" )
    private CacheManager metadataCacheManager;

    @Autowired
    @Qualifier( "dhisCacheManager" )
    private CacheManager dhisCacheManager;

    @Autowired
    @Qualifier( "fhirCacheManager" )
    private CacheManager fhirCacheManager;

    @Autowired
    @Qualifier( "fhirRestHookRequestQueueJmsTemplate" )
    private JmsTemplate fhirRestHookRequestQueueJmsTemplate;

    @Autowired
    @Qualifier( "fhirResourceQueueJmsTemplate" )
    private JmsTemplate fhirResourceQueueJmsTemplate;

    @Autowired
    protected EntityManager entityManager;

    @Autowired
    protected PlatformTransactionManager transactionManager;

    @Autowired
    protected FhirDhisAssignmentRepository assignmentRepository;

    @Autowired
    protected RuleRepository ruleRepository;

    @Autowired
    protected FhirClientRepository fhirClientRepository;

    private long resourceDlQueueCount;

    @Nonnull
    protected abstract FhirVersion getFhirVersion();

    @Nonnull
    protected String getBaseFhirContext()
    {
        switch ( getFhirVersion() )
        {
            case DSTU3:
                return TestConfiguration.BASE_DSTU3_CONTEXT;
            case R4:
                return TestConfiguration.BASE_R4_CONTEXT;
            default:
                throw new AssertionError( "Unhandled FHIR version: " + getFhirVersion() );
        }
    }

    @Nonnull
    protected FhirContext getFhirContext()
    {
        final FhirContext ctx;
        switch ( getFhirVersion() )
        {
            case DSTU3:
                ctx = FhirContext.forDstu3();
                break;
            case R4:
                ctx = FhirContext.forR4();
                break;
            default:
                throw new AssertionError( "Unhandled FHIR version: " + getFhirVersion() );
        }
        ctx.getRestfulClientFactory().setSocketTimeout( 300 * 1000 );
        return ctx;
    }

    @Nonnull
    protected IGenericClient createGenericClient()
    {
        return getFhirContext().newRestfulGenericClient( "http://localhost:" + localPort + "/fhir" + getFhirVersionPath() + "/default" );
    }

    @Nonnull
    protected String getFhirVersionPath()
    {
        switch ( getFhirVersion() )
        {
            case DSTU3:
                return TestConfiguration.DSTU3_VERSION_PATH;
            case R4:
                return TestConfiguration.R4_VERSION_PATH;
            default:
                throw new AssertionError( "Unhandled FHIR version: " + getFhirVersion() );
        }
    }

    @Nonnull
    protected String getResourceDir()
    {
        switch ( getFhirVersion() )
        {
            case DSTU3:
                return "dstu3";
            case R4:
                return "r4";
            default:
                throw new AssertionError( "Unhandled FHIR version: " + getFhirVersion() );
        }
    }

    @Nonnull
    protected HttpHeaders createDefaultHeaders()
    {
        final HttpHeaders headers = new HttpHeaders();
        headers.setDate( System.currentTimeMillis() );

        return headers;
    }

    protected void createAssignment( @Nonnull UUID ruleId, @Nonnull UUID fhirClientId, @Nonnull DhisResourceId dhisResourceId, @Nonnull IIdType fhirResourceId )
    {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityContextHolder.getContext().setAuthentication( new AdapterSystemAuthenticationToken() );
        try
        {
            assignmentRepository.saveFhirResourceId(
                ruleRepository.findById( ruleId ).orElseGet( () -> {
                    Assert.fail( "Rule could not be found: " + ruleId );
                    return null;
                } ),
                fhirClientRepository.findById( fhirClientId ).orElseGet( () -> {
                    Assert.fail( "FHIR client could not be found: " + fhirClientId );
                    return null;
                } ),
                dhisResourceId, fhirResourceId );
        }
        finally
        {
            SecurityContextHolder.getContext().setAuthentication( authentication );
        }
    }

    protected void notifyResource( @Nonnull FhirResourceType resourceType, @Nullable String resourceSearchResponse,
        @Nullable String resourceId, @Nullable String resourceResponse, boolean payload ) throws Exception
    {
        if ( previousResourceSearchStubMapping != null )
        {
            fhirMockServer.removeStub( previousResourceSearchStubMapping );
            previousResourceSearchStubMapping = null;
        }

        if ( StringUtils.isNotBlank( resourceSearchResponse ) )
        {
            final UrlPattern urlPattern = urlMatching( getBaseFhirContext() +
                "/" + resourceType.getResourceTypeName() +
                "\\?_sort=_lastUpdated&_count=10000&_lastUpdated=ge[^&]+&_elements=meta\\%2[cC]id" );
            previousResourceSearchStubMapping = fhirMockServer.stubFor(
                WireMock.get( urlPattern ).willReturn( aResponse()
                .withHeader( "Content-Type", "application/fhir+json" )
                .withBody( resourceSearchResponse ) ) );
        }

        if ( StringUtils.isNotBlank( resourceId ) && StringUtils.isNotBlank( resourceResponse ) )
        {
            fhirMockServer.stubFor( WireMock.get( urlEqualTo( getBaseFhirContext() +
                "/" + resourceType.getResourceTypeName() + "/" + resourceId ) ).willReturn( aResponse()
                .withHeader( "Content-Type", "application/fhir+json" )
                .withBody( resourceResponse ) ) );
        }

        if ( payload )
        {
            Assert.assertNotNull( resourceId );
            Assert.assertNotNull( resourceResponse );
            mockMvc.perform( MockMvcRequestBuilders.put( "/remote-fhir-rest-hook/{subscriptionId}/{fhirClientResourceId}/{resourceType}/{resourceId}",
                testConfiguration.getFhirClientId( getFhirVersion() ), testConfiguration.getFhirClientResourceId( getFhirVersion(), resourceType ),
                resourceType.getResourceTypeName(), resourceId ).content( resourceResponse ).contentType( FHIR_JSON_MEDIA_TYPE )
                .header( "Authorization", TestConfiguration.ADAPTER_AUTHORIZATION ) )
                .andExpect( status().isOk() );
        }
        else
        {
            mockMvc.perform( MockMvcRequestBuilders.post( "/remote-fhir-rest-hook/{subscriptionId}/{fhirClientResourceId}",
                testConfiguration.getFhirClientId( getFhirVersion() ), testConfiguration.getFhirClientResourceId( getFhirVersion(), resourceType ) )
                .header( "Authorization", TestConfiguration.ADAPTER_AUTHORIZATION ) )
                .andExpect( status().isOk() );
        }
    }

    protected void waitForEmptyResourceQueue() throws Exception
    {
        final long begin = System.currentTimeMillis();
        long messageCount;
        do
        {
            messageCount = getQueueMessageCount( fhirRestHookRequestQueueJmsTemplate, null ) +
                getQueueMessageCount( fhirResourceQueueJmsTemplate, null );

            if ( messageCount > 0 )
            {
                if ( (System.currentTimeMillis() - begin) > MAX_COMPLETED_POLL_TIME )
                {
                    Assert.fail( "Waited more than " + MAX_COMPLETED_POLL_TIME + " ms, but there are still " + messageCount + " messages in the queues." );
                }

                Thread.sleep( 100L );
            }
        }
        while ( messageCount > 0 );

        Assert.assertEquals( "Resource dead letter queue contains messages.",
            resourceDlQueueCount, getQueueMessageCount( fhirResourceQueueJmsTemplate, RESOURCE_DL_QUEUE_NAME ) );
    }

    private long getQueueMessageCount( @Nonnull JmsTemplate jmsTemplate, @Nullable String queueName )
    {
        return Objects.requireNonNull( jmsTemplate.execute( session -> {
            try
            {
                final ClientSession clientSession = ((ActiveMQSession) session).getCoreSession();
                final ClientRequestor req = new ClientRequestor( clientSession, "activemq.management" );
                clientSession.start();
                try
                {
                    final ClientMessage message = clientSession.createMessage( false );
                    ManagementHelper.putAttribute( message, "queue." +
                        ((queueName == null) ? jmsTemplate.getDefaultDestinationName() : queueName), "messageCount" );
                    final ClientMessage reply = req.request( message );
                    return ((Number) ManagementHelper.getResult( reply )).longValue();
                }
                finally
                {
                    clientSession.stop();
                }
            }
            catch ( Exception e )
            {
                throw new UncategorizedJmsException( e );
            }
        } ) );
    }

    @Before
    public void beforeAbstractAppTest() throws Exception
    {
        testConfiguration.init( fhirMockServer );

        fhirMockServer.resetAll();
        WireMock.configureFor( fhirMockServer.port() );
        previousResourceSearchStubMapping = null;

        systemDhis2Server = MockRestServiceServer.bindTo( systemDhis2RestTemplate ).ignoreExpectOrder( true ).build();
        userDhis2Server = MockRestServiceServer.bindTo( userDhis2RestTemplate ).build();

        fhirMockServer.stubFor(
            WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/metadata" ) ).willReturn( aResponse()
                .withHeader( "Content-Type", "application/fhir+json" )
                .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/metadata.json", StandardCharsets.UTF_8 ) ) ) );

        clearCache( metadataCacheManager );
        clearCache( dhisCacheManager );
        clearCache( fhirCacheManager );

        final TransactionStatus transactionStatus = transactionManager.getTransaction( new DefaultTransactionDefinition() );
        try
        {
            entityManager.createQuery( "DELETE FROM ProcessedDhisResource" ).executeUpdate();
            entityManager.createQuery( "DELETE FROM StoredDhisResource" ).executeUpdate();
            entityManager.createQuery( "DELETE FROM ProcessedFhirResource" ).executeUpdate();
            entityManager.createQuery( "DELETE FROM StoredFhirResource" ).executeUpdate();
            entityManager.createQuery( "DELETE FROM FhirDhisAssignment" ).executeUpdate();
            entityManager.createQuery( "DELETE FROM SubscriptionFhirResource" ).executeUpdate();
        }
        finally
        {
            transactionManager.commit( transactionStatus );
        }

        resourceDlQueueCount = getQueueMessageCount( fhirResourceQueueJmsTemplate, RESOURCE_DL_QUEUE_NAME );

        TestSecurityContextHolder.clearContext();
    }

    private void clearCache( @Nonnull CacheManager cacheManager )
    {
        for ( String name : cacheManager.getCacheNames() )
        {
            final Cache cache = cacheManager.getCache( name );
            if ( cache != null )
            {
                cache.clear();
            }
        }
    }
}
