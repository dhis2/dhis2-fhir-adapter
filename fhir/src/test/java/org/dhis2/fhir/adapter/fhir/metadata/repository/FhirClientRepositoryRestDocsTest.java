package org.dhis2.fhir.adapter.fhir.metadata.repository;

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

import org.apache.commons.io.IOUtils;
import org.dhis2.fhir.adapter.fhir.AbstractJpaRepositoryRestDocsTest;
import org.dhis2.fhir.adapter.fhir.ConstrainedFields;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for {@link FhirClientRepository}.
 *
 * @author volsch
 */
public class FhirClientRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private FhirClientRepository fhirClientRepository;

    @Test
    public void createFhirClient() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( FhirClient.class, constraintDescriptionResolver );
        final String request = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createFhirClient.json", StandardCharsets.UTF_8 );
        final String location = docMockMvc.perform( post( "/api/fhirClients" ).header( AUTHORIZATION_HEADER_NAME, ADMINISTRATION_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( request ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for FHIR client creation" ) ),
                fields.withPath( "name" ).description( "The name of the FHIR client." ).type( JsonFieldType.STRING ),
                fields.withPath( "code" ).description( "The code of the FHIR client." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description of the purpose of the FHIR client." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "enabled" ).description( "Specifies if this FHIR client has been enabled. " +
                    "If the FHIR client has not been enabled, no subscription notifications are processed from the corresponding FHIR service." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "locked" ).description( "Specifies if this FHIR client has been locked. " +
                    "If the FHIR client has been locked (i.e. by automatic processes), no subscription notifications are processed from the corresponding FHIR service." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "outEnabled" ).description( "Specifies if output transformation from DHIS to FHIR for this FHIR client is enabled (by default false)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "useAdapterIdentifier" ).description( "Specifies if the adapter should add an adapter specific identifier to created and updated resources (by default true). Using such identifiers makes it easier to map resources between " +
                    "FHIR and DHIS2." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirVersion" ).description( "The FHIR version that should be used when communicating with the client FHIR service." ).type( JsonFieldType.STRING ),
                fields.withPath( "toleranceMillis" ).description( "The number of milli-seconds to subtract from the last updated timestamp when searching for created and updated resources." ).type( JsonFieldType.NUMBER ),
                fields.withPath( "autoCreatedSubscriptionResources" ).description( "Subscription resources for which the subscriptions should be created automatically when creating the subscription resource. This value will not be returned and can only " +
                    "be used when creating and updating the entity." ).type( JsonFieldType.ARRAY ).optional(),
                fields.withPath( "remoteSyncEnabled" ).description( "Specifies if a FHIR notification can be simulated by a remote FHIR client by invoking a read request on a FHIR resource." )
                    .type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "adapterEndpoint" ).description( "Specifies FHIR client settings that are relevant for the adapter." ).type( JsonFieldType.OBJECT ),
                fields.withPath( "adapterEndpoint.baseUrl" ).description( "The base URL of the adapter that is used to register the subscription on the FHIR service. " +
                    "If the FHIR service runs on a different client, the URL must not contain localhost. If this URL is not specified it is calculated automatically." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "adapterEndpoint.subscriptionType" ).description( "The subscription type that is used to register the subscription on FHIR service. " +
                    "A normal REST hook subscription is sufficient (without any payload). If this causes issues on the FHIR service also a subscription with payload can be used." )
                    .type( JsonFieldType.STRING ),
                fields.withPath( "adapterEndpoint.authorizationHeader" ).description( "The authorization header value that is expected by the adapter when it receives a subscription notification from the FHIR service. " +
                    "This should include a bearer token." ).type( JsonFieldType.STRING ),
                fields.withPath( "dhisEndpoint" ).description( "Specifies FHIR client settings that are relevant for the connection to DHIS2." ).type( JsonFieldType.OBJECT ),
                fields.withPath( "dhisEndpoint.authenticationMethod" ).description( "The authentication method that should be used when connecting to DHIS2." ).type( JsonFieldType.STRING ),
                fields.withPath( "dhisEndpoint.username" ).description( "The username that is used to connect to DHIS2 when handling data of this FHIR client." ).type( JsonFieldType.STRING ),
                fields.withPath( "dhisEndpoint.password" ).description( "The password that is used to connect to DHIS2 when handling data of this FHIR client. " +
                    "This value will not be returned and will be set using the original value when performing an update without this value." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "fhirEndpoint" ).description( "Specifies FHIR client settings that are relevant for the connection to FHIR." ).type( JsonFieldType.OBJECT ),
                fields.withPath( "fhirEndpoint.baseUrl" ).description( "The base URL of the FHIR endpoints on the FHIR service." ).type( JsonFieldType.STRING ),
                fields.withPath( "fhirEndpoint.useRemote" ).description( "Specifies if connections to the remote FHIR endpoint are enabled (true by default). Otherwise no connection attempt will be made and default values will be used." )
                    .type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirEndpoint.useJsonFormat" ).description( "Specifies if JSON format instead of XML should be used when communicating with this FHIR client (by default false)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirEndpoint.sortSupported" ).description( "Specifies if client does support sorting (by default true)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirEndpoint.headers" ).description( "The headers that are sent to the client FHIR service when connecting to the FHIR endpoints." ).type( JsonFieldType.ARRAY ),
                fields.withPath( "fhirEndpoint.headers[].name" ).description( "The name of the header for which the value will be sent (e.g. Authorization)." ).type( JsonFieldType.STRING ),
                fields.withPath( "fhirEndpoint.headers[].value" ).description( "The value of the header for which the value will be sent (e.g. a bearer token). If the value of the header is marked as secure, " +
                    "the value will not be returned and will be set using the original value when performing an update without this value." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "fhirEndpoint.headers[].secure" ).description( "Specifies if the value of the header is secure and should not be returned " +
                    "(e.g. when it contains authentication information)." ).type( JsonFieldType.BOOLEAN )
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, ADMINISTRATION_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "name", is( "Main Subscription" ) ) )
            .andExpect( jsonPath( "code", is( "MAIN_SUBSCRIPTION" ) ) )
            .andExpect( jsonPath( "description", is( "Main FHIR service on which the adapter has subscriptions." ) ) )
            .andExpect( jsonPath( "enabled", is( true ) ) )
            .andExpect( jsonPath( "locked", is( false ) ) )
            .andExpect( jsonPath( "expEnabled", is( false ) ) )
            .andExpect( jsonPath( "useAdapterIdentifier", is( true ) ) )
            .andExpect( jsonPath( "autoCreatedSubscriptionResources" ).doesNotExist() )
            .andExpect( jsonPath( "adapterEndpoint.baseUrl", is( "http://localhist:8081" ) ) )
            .andExpect( jsonPath( "adapterEndpoint.subscriptionType", is( "REST_HOOK" ) ) )
            .andExpect( jsonPath( "adapterEndpoint.authorizationHeader", is( "Bearer 98a7558102b7bdc4da5c8f74ca63958c498b4bd9231bd3b0cc" ) ) )
            .andExpect( jsonPath( "dhisEndpoint.authenticationMethod", is( "BASIC" ) ) )
            .andExpect( jsonPath( "dhisEndpoint.username", is( "admin" ) ) )
            .andExpect( jsonPath( "dhisEndpoint.password" ).doesNotExist() )
            .andExpect( jsonPath( "fhirEndpoint.baseUrl", is( "http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3" ) ) )
            .andExpect( jsonPath( "fhirEndpoint.headers[0].name", is( "Authorization" ) ) )
            .andExpect( jsonPath( "fhirEndpoint.headers[0].value" ).doesNotExist() )
            .andExpect( jsonPath( "fhirEndpoint.headers[0].secure", is( true ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readFhirClient() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( FhirClient.class, constraintDescriptionResolver );
        final String fhirClientId = loadFhirClient( "DEFAULT_SUBSCRIPTION" ).getId().toString();
        docMockMvc.perform( get( "/api/fhirClients/{fhirClientId}", fhirClientId ).header( AUTHORIZATION_HEADER_NAME, ADMINISTRATION_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "fhirClient" ).description( "Link to this resource itself." ),
                linkWithRel( "systems" ).description( "Link to the system URI resources that belong to this FHIR client." ),
                linkWithRel( "resources" ).description( "Link to the subscribed FHIR resources that belong to this FHIR client." ) ), responseFields(
                attributes( key( "title" ).value( "Fields for FHIR client reading" ) ),
                fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                fields.withPath( "name" ).description( "The name of the FHIR client." ).type( JsonFieldType.STRING ),
                fields.withPath( "code" ).description( "The code of the FHIR client." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description of the purpose of the FHIR client." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "enabled" ).description( "Specifies if this FHIR client has been enabled. " +
                    "If the FHIR client has not been enabled, no subscription notifications are processed from the corresponding FHIR service." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "locked" ).description( "Specifies if this FHIR client has been locked. " +
                    "If the FHIR client has been locked (i.e. by automatic processes), no subscription notifications are processed from the corresponding FHIR service." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "expEnabled" ).description( "Specifies if output transformation from DHIS to FHIR for this FHIR client is enabled." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "useAdapterIdentifier" ).description( "Specifies if the adapter should add an adapter specific identifier to created and updated resources (by default true). Using such identifiers makes it easier to map resources " +
                    "between FHIR and DHIS2." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirVersion" ).description( "The FHIR version that should be used when communicating with the client FHIR service." ).type( JsonFieldType.STRING ),
                fields.withPath( "toleranceMillis" ).description( "The number of milli-seconds to subtract from the last updated timestamp when searching for created and updated resources." ).type( JsonFieldType.NUMBER ),
                fields.withPath( "autoCreatedSubscriptionResources" ).description( "Subscription resources for which the subscriptions should be created automatically when creating the subscription resource. This value will not be returned and can only " +
                    "be used when creating and updating the entity." ).type( JsonFieldType.ARRAY ).optional(),
                fields.withPath( "remoteSyncEnabled" ).description( "Specifies if a FHIR notification can be simulated by a remote FHIR client by invoking a read request on a FHIR resource." )
                    .type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "adapterEndpoint" ).description( "Specifies FHIR client settings that are relevant for the adapter." ).type( JsonFieldType.OBJECT ),
                fields.withPath( "adapterEndpoint.baseUrl" ).description( "The base URL of the adapter that is used to register the subscription on the FHIR service. " +
                    "If the FHIR service runs on a different client, the URL must not contain localhost. If this URL is not specified it is calculated automatically." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "adapterEndpoint.subscriptionType" ).description( "The subscription type that is used to register the subscription on FHIR service. " +
                    "A normal REST hook subscription is sufficient (without any payload). If this causes issues on the FHIR service also a subscription with payload can be used." )
                    .type( JsonFieldType.STRING ),
                fields.withPath( "adapterEndpoint.authorizationHeader" ).description( "The authorization header value that is expected by the adapter when it receives a subscription notification from the FHIR service. " +
                    "This should include a bearer token." ).type( JsonFieldType.STRING ),
                fields.withPath( "dhisEndpoint" ).description( "Specifies FHIR client settings that are relevant for the connection to DHIS2." ).type( JsonFieldType.OBJECT ),
                fields.withPath( "dhisEndpoint.authenticationMethod" ).description( "The authentication method that should be used when connecting to DHIS2." ).type( JsonFieldType.STRING ),
                fields.withPath( "dhisEndpoint.username" ).description( "The username that is used to connect to DHIS2 when handling data of this FHIR client." ).type( JsonFieldType.STRING ),
                fields.withPath( "dhisEndpoint.password" ).description( "The password that is used to connect to DHIS2 when handling data of this FHIR client. " +
                    "This value will not be returned and will be set using the original value when performing an update without this value." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "fhirEndpoint" ).description( "Specifies FHIR client settings that are relevant for the connection to FHIR." ).type( JsonFieldType.OBJECT ),
                fields.withPath( "fhirEndpoint.baseUrl" ).description( "The base URL of the FHIR endpoints on the FHIR service." ).type( JsonFieldType.STRING ),
                fields.withPath( "fhirEndpoint.useRemote" ).description( "Specifies if connections to the remote FHIR endpoint are enabled (true by default). Otherwise no connection attempt will be made and default values will be used." )
                    .type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirEndpoint.useJsonFormat" ).description( "Specifies if JSON format instead of XML should be used when communicating with this FHIR client (by default false)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirEndpoint.sortSupported" ).description( "Specifies if client does support sorting (by default true)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirEndpoint.logging" ).description( "Specifies if basic logging should be enabled when communicating with the FHIR endpoints of this FHIR service." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirEndpoint.verboseLogging" ).description( "Specifies if verbose logging (includes complete payload) should be enabled when communicating with the FHIR endpoints of this FHIR service. " +
                    "Enabling verbose logging may log confidential patient data. This could violate data protection laws and regulations." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirEndpoint.headers" ).description( "The headers that are sent to the client FHIR service when connecting to the FHIR endpoints." ).type( JsonFieldType.ARRAY ),
                fields.withPath( "fhirEndpoint.headers[].name" ).description( "The name of the header for which the value will be sent (e.g. Authorization)." ).type( JsonFieldType.STRING ),
                fields.withPath( "fhirEndpoint.headers[].value" ).description( "The value of the header for which the value will be sent (e.g. a bearer token). If the value of the header is marked as secure, " +
                    "the value will not be returned and will be set using the original value when performing an update without this value." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "fhirEndpoint.headers[].secure" ).description( "Specifies if the value of the header is secure and should not be returned " +
                    "(e.g. when it contains authentication information)." ).type( JsonFieldType.BOOLEAN ),
                subsectionWithPath( "_links" ).description( "Links to other resources" )
            ) ) );
    }

    @Nonnull
    protected FhirClient loadFhirClient( @Nonnull String code )
    {
        final FhirClient fhirClient = new FhirClient();
        fhirClient.setCode( code );
        return fhirClientRepository.findOne( Example.of( fhirClient,
            ExampleMatcher.matching().withIgnorePaths( "toleranceMillis", "logging", "verboseLogging", "enabled", "locked" ) ) ).orElseThrow( () -> new AssertionError( "FHIR client does not exist: " + code ) );
    }
}