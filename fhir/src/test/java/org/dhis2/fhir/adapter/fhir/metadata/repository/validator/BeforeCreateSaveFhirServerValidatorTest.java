package org.dhis2.fhir.adapter.fhir.metadata.repository.validator;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.AbstractJpaRepositoryTest;
import org.dhis2.fhir.adapter.fhir.metadata.model.AuthenticationMethod;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServer;
import org.dhis2.fhir.adapter.fhir.metadata.model.RequestHeader;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionAdapterEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionDhisEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionFhirEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionType;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Validator tests for the corresponding repository.
 *
 * @author volsch
 */
public class BeforeCreateSaveFhirServerValidatorTest extends AbstractJpaRepositoryTest
{
    public static final String RESOURCE_PATH = "/api/fhirServers";

    public static final String AUTHORIZATION_HEADER_VALUE = CODE_MAPPING_AUTHORIZATION_HEADER_VALUE;

    private FhirServer entity;

    @Before
    public void before()
    {
        entity = new FhirServer();
        entity.setName( createUnique() );
        entity.setCode( createUnique() );
        entity.setFhirVersion( FhirVersion.DSTU3 );
        entity.setToleranceMillis( 5000 );
        entity.setDescription( createUnique() );

        final SubscriptionAdapterEndpoint adapterEndpoint = new SubscriptionAdapterEndpoint();
        adapterEndpoint.setBaseUrl( "http://localhost:8081" );
        adapterEndpoint.setAuthorizationHeader( "Bearer abcdef1234567890" );
        adapterEndpoint.setSubscriptionType( SubscriptionType.REST_HOOK );
        entity.setAdapterEndpoint( adapterEndpoint );

        final SubscriptionDhisEndpoint dhisEndpoint = new SubscriptionDhisEndpoint();
        dhisEndpoint.setUsername( "admin" );
        dhisEndpoint.setPassword( "district" );
        dhisEndpoint.setAuthenticationMethod( AuthenticationMethod.BASIC );
        entity.setDhisEndpoint( dhisEndpoint );

        final SubscriptionFhirEndpoint fhirEndpoint = new SubscriptionFhirEndpoint();
        fhirEndpoint.setBaseUrl( "http://localhost:8082/baseDstu3" );
        fhirEndpoint.setHeaders( Collections.singletonList( new RequestHeader( "Authentication", "Bearer 987654321", false ) ) );
        entity.setFhirEndpoint( fhirEndpoint );
    }

    @Test
    public void testNameBlank() throws Exception
    {
        entity.setName( "    " );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "name" ) ) );
    }

    @Test
    public void testNameLength() throws Exception
    {
        entity.setName( StringUtils.repeat( 'a', FhirServer.MAX_NAME_LENGTH + 1 ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "name" ) ) );
    }

    @Test
    public void testCodeBlank() throws Exception
    {
        entity.setCode( "    " );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "code" ) ) );
    }

    @Test
    public void testCodeLength() throws Exception
    {
        entity.setCode( StringUtils.repeat( 'a', FhirServer.MAX_CODE_LENGTH + 1 ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "code" ) ) );
    }

    @Test
    public void testFhirVersionNull() throws Exception
    {
        entity.setFhirVersion( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "fhirVersion" ) ) );
    }

    @Test
    public void testToleranceMillisNegative() throws Exception
    {
        entity.setToleranceMillis( -1 );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "toleranceMillis" ) ) );
    }

    @Test
    public void testAdapterEndpointNull() throws Exception
    {
        entity.setAdapterEndpoint( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "adapterEndpoint" ) ) );
    }

    @Test
    public void testAdapterEndpointUrlInvalid() throws Exception
    {
        entity.getAdapterEndpoint().setBaseUrl( "http//localhost:8081" );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "adapterEndpoint.baseUrl" ) ) );
    }

    @Test
    public void testAdapterEndpointUrlSchema() throws Exception
    {
        entity.getAdapterEndpoint().setBaseUrl( "ftp://localhost:8081" );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "adapterEndpoint.baseUrl" ) ) );
    }

    @Test
    public void testAdapterEndpointUrlLength() throws Exception
    {
        entity.getAdapterEndpoint().setBaseUrl( "http://" + StringUtils.repeat( 'a', SubscriptionAdapterEndpoint.MAX_BASE_URL_LENGTH - 6 ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "adapterEndpoint.baseUrl" ) ) );
    }

    @Test
    public void testAdapterEndpointAuthorizationHeaderBlank() throws Exception
    {
        entity.getAdapterEndpoint().setAuthorizationHeader( "    " );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "adapterEndpoint.authorizationHeader" ) ) );
    }

    @Test
    public void testAdapterEndpointAuthorizationHeaderLength() throws Exception
    {
        entity.getAdapterEndpoint().setAuthorizationHeader( StringUtils.repeat( 'a', SubscriptionAdapterEndpoint.MAX_AUTHORIZATION_HEADER_LENGTH + 1 ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "adapterEndpoint.authorizationHeader" ) ) );
    }

    @Test
    public void testAdapterEndpointSubscriptionTypeNull() throws Exception
    {
        entity.getAdapterEndpoint().setSubscriptionType( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "adapterEndpoint.subscriptionType" ) ) );
    }

    @Test
    public void testDhisEndpointNull() throws Exception
    {
        entity.setDhisEndpoint( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "dhisEndpoint" ) ) );
    }

    @Test
    public void testDhisEndpointUsernameBlank() throws Exception
    {
        entity.getDhisEndpoint().setUsername( "  " );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "dhisEndpoint.username" ) ) );
    }

    @Test
    public void testDhisEndpointUsernameLength() throws Exception
    {
        entity.getDhisEndpoint().setUsername( StringUtils.repeat( 'a', SubscriptionDhisEndpoint.MAX_USERNAME_LENGTH + 1 ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "dhisEndpoint.username" ) ) );
    }

    @Test
    public void testDhisEndpointPasswordBlank() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "   " ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "dhisEndpoint.password" ) ) );
    }

    @Test
    public void testDhisEndpointPasswordLength() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, StringUtils.repeat( 'a', SubscriptionDhisEndpoint.MAX_PASSWORD_LENGTH + 1 ) ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "dhisEndpoint.password" ) ) );
    }

    @Test
    public void testDhisEndpointAuthenticationMethodNull() throws Exception
    {
        entity.getDhisEndpoint().setAuthenticationMethod( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "dhisEndpoint.authenticationMethod" ) ) );
    }

    @Test
    public void testFhirEndpointNull() throws Exception
    {
        entity.setFhirEndpoint( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "fhirEndpoint" ) ) );
    }

    @Test
    public void testFhirEndpointUrlInvalid() throws Exception
    {
        entity.getFhirEndpoint().setBaseUrl( "http//localhost:8081" );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "fhirEndpoint.baseUrl" ) ) );
    }

    @Test
    public void testFhirEndpointUrlSchema() throws Exception
    {
        entity.getFhirEndpoint().setBaseUrl( "ftp://localhost:8081" );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "fhirEndpoint.baseUrl" ) ) );
    }

    @Test
    public void testFhirEndpointUrlLength() throws Exception
    {
        entity.getFhirEndpoint().setBaseUrl( "http://" + StringUtils.repeat( 'a', SubscriptionFhirEndpoint.MAX_BASE_URL_LENGTH - 6 ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "fhirEndpoint.baseUrl" ) ) );
    }

    @Test
    public void testFhirEndpointHeadersNull() throws Exception
    {
        entity.getFhirEndpoint().setHeaders( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testFhirEndpointHeaderNameBlank() throws Exception
    {
        entity.getFhirEndpoint().setHeaders( Collections.singletonList( new RequestHeader( "   ", "Bearer 987654321", false ) ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "fhirEndpoint.headers[0].name" ) ) );
    }

    @Test
    public void testFhirEndpointHeaderNameLength() throws Exception
    {
        entity.getFhirEndpoint().setHeaders( Collections.singletonList( new RequestHeader( StringUtils.repeat( 'a', RequestHeader.MAX_NAME_LENGTH + 1 ), "Bearer 987654321", false ) ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "fhirEndpoint.headers[0].name" ) ) );
    }

    @Test
    public void testFhirEndpointHeaderValueBlank() throws Exception
    {
        entity.getFhirEndpoint().setHeaders( Collections.singletonList( new RequestHeader( "Authorization", "   ", false ) ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "fhirEndpoint.headers[0].value" ) ) );
    }

    @Test
    public void testFhirEndpointHeaderValueLength() throws Exception
    {
        entity.getFhirEndpoint().setHeaders( Collections.singletonList( new RequestHeader( "Authorization", StringUtils.repeat( 'a', RequestHeader.MAX_VALUE_LENGTH + 1 ), false ) ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "dhisEndpoint/password", null, "district" ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "fhirEndpoint.headers[0].value" ) ) );
    }
}