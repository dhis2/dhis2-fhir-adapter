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

import com.github.tomakehurst.wiremock.WireMockServer;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.security.SecurityConfig;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionType;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.security.AdapterSystemAuthenticationToken;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.dhis2.fhir.adapter.lock.impl.EmbeddedLockManagerImpl;
import org.dhis2.fhir.adapter.setup.FhirClientSetup;
import org.dhis2.fhir.adapter.setup.OrganizationCodeSetup;
import org.dhis2.fhir.adapter.setup.Setup;
import org.dhis2.fhir.adapter.setup.SetupResult;
import org.dhis2.fhir.adapter.setup.SetupService;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Configuration that must be executed before any test is run. It setups the
 * database with a basic system setup.
 *
 * @author volsch
 */
@Configuration
@AutoConfigureAfter( { HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class, ArtemisAutoConfiguration.class } )
public class TestConfiguration
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    public static final String ADAPTER_AUTHORIZATION = "Bearer 836ef9274abc828728746";

    public static final String DHIS2_USERNAME = "fhir_user";

    public static final String DHIS2_PASSWORD = "fhir_user_123";

    public static final String FHIR_SERVICE_HEADER_NAME = "Authorization";

    public static final String FHIR_SERVICE_HEADER_VALUE = "Basic Zmhpcjp0ZXN0";

    public static final String BASE_DSTU3_CONTEXT = "/baseDstu3";

    public static final String BASE_R4_CONTEXT = "/baseR4";

    public static final String DSTU3_VERSION_PATH = "/dstu3";

    public static final String R4_VERSION_PATH = "/r4";

    @Value( "${dhis2.fhir-adapter.endpoint.system-authentication.username}" )
    private String dhis2SystemAuthenticationUsername;

    @Value( "${dhis2.fhir-adapter.endpoint.system-authentication.password}" )
    private String dhis2SystemAuthenticationPassword;

    @Autowired
    private SetupService setupService;

    private UUID fhirClientId;

    private Map<FhirResourceType, UUID> fhirClientResourceIds;

    private UUID fhirClientIdR4;

    private Map<FhirResourceType, UUID> fhirClientResourceIdsR4;

    private boolean initialized;

    @Nonnull
    public UUID getFhirClientId( @Nonnull FhirVersion fhirVersion )
    {
        switch ( fhirVersion )
        {
            case DSTU3:
                return fhirClientId;
            case R4:
                return fhirClientIdR4;
            default:
                throw new AssertionError( "Unhandled FHIR version: " + fhirVersion );
        }
    }

    @Nonnull
    public UUID getFhirClientResourceId( @Nonnull FhirVersion fhirVersion, @Nonnull FhirResourceType resourceType )
    {
        final UUID resourceId;
        switch ( fhirVersion )
        {
            case DSTU3:
                resourceId = fhirClientResourceIds.get( resourceType );
                break;
            case R4:
                resourceId = fhirClientResourceIdsR4.get( resourceType );
                break;
            default:
                throw new AssertionError( "Unhandled FHIR version: " + fhirVersion );
        }
        Assert.assertNotNull( "FHIR client resource for " + resourceType + " could not be found.", resourceId );
        return resourceId;
    }

    @Nonnull
    public String getDhis2SystemAuthorization()
    {
        return "Basic " + Base64.getEncoder().encodeToString(
            (dhis2SystemAuthenticationUsername + ":" + dhis2SystemAuthenticationPassword).getBytes( StandardCharsets.UTF_8 ) );
    }

    @Nonnull
    public String getDhis2UserAuthorization()
    {
        return "Basic " + Base64.getEncoder().encodeToString(
            (DHIS2_USERNAME + ":" + DHIS2_PASSWORD).getBytes( StandardCharsets.UTF_8 ) );
    }

    @Nonnull
    @Bean
    @Primary
    protected AbstractUserDetailsAuthenticationProvider testDhisWebApiAuthenticationProvider( @Nonnull SecurityConfig securityConfig )
    {
        return new TestDhisWebApiAuthenticationProvider( securityConfig );
    }

    @Nonnull
    @Bean
    @Primary
    protected LockManager embeddedLockManager()
    {
        return new EmbeddedLockManagerImpl();
    }

    @Nonnull
    @Bean( destroyMethod = "stop" )
    protected WireMockServer fhirMockServer()
    {
        final WireMockServer fhirMockServer = new WireMockServer( wireMockConfig().dynamicPort() );
        fhirMockServer.start();
        logger.info( "Started WireMock client for FHIR requests on port {}.", fhirMockServer.port() );
        return fhirMockServer;
    }

    public void init( @Nonnull WireMockServer fhirMockServer )
    {
        if ( initialized )
        {
            return;
        }

        final Set<FhirResourceType> additionalFhirResourceTypes = new HashSet<>();
        additionalFhirResourceTypes.add( FhirResourceType.OBSERVATION );
        additionalFhirResourceTypes.add( FhirResourceType.CARE_PLAN );
        additionalFhirResourceTypes.add( FhirResourceType.QUESTIONNAIRE_RESPONSE );

        final Setup setup = new Setup();

        setup.setFhirRestInterfaceOnly( false );
        setup.setFhirClientSetup( new FhirClientSetup( false ) );
        setup.getFhirClientSetup().getAdapterSetup().setBaseUrl( "http://localhost:8081" );
        setup.getFhirClientSetup().getAdapterSetup().setAuthorizationHeaderValue( ADAPTER_AUTHORIZATION );

        setup.getFhirClientSetup().getDhisSetup().setUsername( DHIS2_USERNAME );
        setup.getFhirClientSetup().getDhisSetup().setPassword( DHIS2_PASSWORD );

        setup.getFhirClientSetup().getFhirSetup().setBaseUrl( fhirMockServer.baseUrl() + BASE_DSTU3_CONTEXT );
        setup.getFhirClientSetup().getFhirSetup().setHeaderName( FHIR_SERVICE_HEADER_NAME );
        setup.getFhirClientSetup().getFhirSetup().setHeaderValue( FHIR_SERVICE_HEADER_VALUE );
        setup.getFhirClientSetup().getFhirSetup().setSubscriptionType( SubscriptionType.REST_HOOK );
        setup.getFhirClientSetup().getFhirSetup().setSupportsRelatedPerson( true );

        setup.getFhirClientSetup().getSystemUriSetup().setOrganizationSystemUri( "http://example.sl/organizations" );
        setup.getFhirClientSetup().getSystemUriSetup().setOrganizationCodePrefix( "OU_" );
        setup.getFhirClientSetup().getSystemUriSetup().setPatientSystemUri( "http://example.sl/patients" );
        setup.getFhirClientSetup().getSystemUriSetup().setPatientCodePrefix( "PT_" );

        setup.setOrganizationCodeSetup( new OrganizationCodeSetup() );
        setup.getOrganizationCodeSetup().setFallback( true );
        setup.getOrganizationCodeSetup().setDefaultDhisCode( "OU_4567" );
        setup.getOrganizationCodeSetup().setMappings( "9876 OU_1234 \n  8765, OU_2345" );

        setup.getTrackedEntitySetup().getFirstName().setReferenceType( ReferenceType.CODE );
        setup.getTrackedEntitySetup().getFirstName().setReferenceValue( "FIRST_NAME" );
        setup.getTrackedEntitySetup().getLastName().setReferenceType( ReferenceType.NAME );
        setup.getTrackedEntitySetup().getLastName().setReferenceValue( "Last Name" );
        setup.getTrackedEntitySetup().getUniqueId().setEnabled( true );
        setup.getTrackedEntitySetup().getMiddleName().setEnabled( false );

        final Setup setupR4 = new Setup();

        setupR4.setFhirClientSetup( new FhirClientSetup( false ) );
        setupR4.getFhirClientSetup().getAdapterSetup().setBaseUrl( "http://localhost:8081" );
        setupR4.getFhirClientSetup().getAdapterSetup().setAuthorizationHeaderValue( ADAPTER_AUTHORIZATION );

        setupR4.getFhirClientSetup().getDhisSetup().setUsername( DHIS2_USERNAME );
        setupR4.getFhirClientSetup().getDhisSetup().setPassword( DHIS2_PASSWORD );

        setupR4.getFhirClientSetup().getFhirSetup().setBaseUrl( fhirMockServer.baseUrl() + BASE_R4_CONTEXT );
        setupR4.getFhirClientSetup().getFhirSetup().setHeaderName( FHIR_SERVICE_HEADER_NAME );
        setupR4.getFhirClientSetup().getFhirSetup().setHeaderValue( FHIR_SERVICE_HEADER_VALUE );
        setupR4.getFhirClientSetup().getFhirSetup().setSubscriptionType( SubscriptionType.REST_HOOK );
        setupR4.getFhirClientSetup().getFhirSetup().setSupportsRelatedPerson( true );

        setupR4.getFhirClientSetup().getSystemUriSetup().setOrganizationSystemUri( "http://example.sl/organizations" );
        setupR4.getFhirClientSetup().getSystemUriSetup().setOrganizationCodePrefix( "OU_" );
        setupR4.getFhirClientSetup().getSystemUriSetup().setPatientSystemUri( "http://example.sl/patients" );
        setupR4.getFhirClientSetup().getSystemUriSetup().setPatientCodePrefix( "PT_" );

        final SetupResult setupResult;
        final SetupResult setupResultR4;
        SecurityContextHolder.getContext().setAuthentication( new AdapterSystemAuthenticationToken() );
        try
        {
            Assert.assertFalse( setupService.hasCompletedSetup() );
            setupResult = setupService.apply( setup, additionalFhirResourceTypes, true, false, false );
            setupResultR4 = setupService.createFhirClient( setupR4.getFhirClientSetup(), FhirVersion.R4, "_R4",
                setupResult.getOrganizationSystem(), setupResult.getPatientSystem(), additionalFhirResourceTypes, false );
            Assert.assertTrue( setupService.hasCompletedSetup() );
        }
        finally
        {
            SecurityContextHolder.clearContext();
        }

        this.fhirClientId = setupResult.getFhirClientId();
        this.fhirClientResourceIds = setupResult.getFhirClientResourceIds();
        this.fhirClientIdR4 = setupResultR4.getFhirClientId();
        this.fhirClientResourceIdsR4 = setupResultR4.getFhirClientResourceIds();

        initialized = true;
    }
}
