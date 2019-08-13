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
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientSystem;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

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
 * Tests for {@link FhirClientSystem}.
 *
 * @author volsch
 */
public class FhirClientSystemRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private FhirClientRepository fhirClientRepository;

    @Autowired
    private FhirClientSystemRepository fhirClientSystemRepository;

    @Test
    public void createFhirClientSystem() throws Exception
    {
        final String systemId = loadSystem( "SYSTEM_SL_LOCATION" ).getId().toString();
        final String fhirClientId = loadFhirClient( "DEFAULT_SUBSCRIPTION" ).getId().toString();
        final ConstrainedFields fields = new ConstrainedFields( FhirClientSystem.class, constraintDescriptionResolver );
        final String request = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createFhirClientSystem.json", StandardCharsets.UTF_8 )
            .replace( "$systemId", API_BASE_URI + "/systems/" + systemId ).replace( "$fhirClientId", API_BASE_URI + "/fhirClients/" + fhirClientId );
        final String location = docMockMvc.perform( post( "/api/fhirClientSystems" ).header( AUTHORIZATION_HEADER_NAME, ADMINISTRATION_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( request ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for script source creation" ) ),
                fields.withPath( "fhirClient" ).description( "The reference to the FHIR client to which this resource belongs to." ).type( JsonFieldType.STRING ),
                fields.withPath( "system" ).description( "The reference to the system URI that should be mapped to the FHIR client." ).type( JsonFieldType.STRING ),
                fields.withPath( "fhirResourceType" ).description( "The FHIR resource type to which the system URI should be mapped." ).type( JsonFieldType.STRING ),
                fields.withPath( "codePrefix" ).description( "The prefix that should be added to the codes when mapping them to DHIS2." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "defaultValue" ).description( "The default value that may be used when FHIR resource does not contain the corresponding value." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "fhirId" ).description( "Specifies if the FHIR ID should be taken as identifier value is no identifier has been specified." ).type( JsonFieldType.BOOLEAN ).optional()
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, ADMINISTRATION_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "fhirResourceType", is( "LOCATION" ) ) )
            .andExpect( jsonPath( "codePrefix", is( "LOC_" ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readFhirClientSystem() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( FhirClientSystem.class, constraintDescriptionResolver );
        final String fhirClientSystemId = loadFhirClientSystem( "ea9804a3-9e82-4d0d-9cd2-e417b32b1c0c" ).getId().toString();
        docMockMvc.perform( get( "/api/fhirClientSystems/{fhirClientSystemId}", fhirClientSystemId ).header( AUTHORIZATION_HEADER_NAME, ADMINISTRATION_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "fhirClientSystem" ).description( "Link to this resource itself." ),
                linkWithRel( "fhirClient" ).description( "The reference to the FHIR client to which this resource belongs to." ),
                linkWithRel( "system" ).description( "The reference to the system URI that should be mapped to the FHIR client." ) ), responseFields(
                attributes( key( "title" ).value( "Fields for FHIR client system reading" ) ),
                fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                fields.withPath( "fhirResourceType" ).description( "The FHIR resource type to which the system URI should be mapped." ).type( JsonFieldType.STRING ),
                fields.withPath( "codePrefix" ).description( "The prefix that should be added to the codes when mapping them to DHIS2." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "defaultValue" ).description( "The default value that may be used when FHIR resource does not contain the corresponding value." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "fhirId" ).description( "Specifies if the FHIR ID should be taken as identifier value is no identifier has been specified." ).type( JsonFieldType.BOOLEAN ).optional(),
                subsectionWithPath( "_links" ).description( "Links to other resources" )
            ) ) );
    }

    @Nonnull
    protected System loadSystem( @Nonnull String code )
    {
        final System system = new System();
        system.setCode( code );
        return systemRepository.findOne( Example.of( system, ExampleMatcher.matching().withIgnorePaths( "enabled" ) ) )
            .orElseThrow( () -> new AssertionError( "System does not exist: " + code ) );
    }

    @Nonnull
    protected FhirClient loadFhirClient( @Nonnull String code )
    {
        final FhirClient fhirClient = new FhirClient();
        fhirClient.setCode( code );
        return fhirClientRepository.findOne( Example.of( fhirClient,
            ExampleMatcher.matching().withIgnorePaths( "toleranceMillis", "logging", "verboseLogging", "enabled", "locked" ) ) ).orElseThrow( () -> new AssertionError( "FHIR client does not exist: " + code ) );
    }

    @Nonnull
    protected FhirClientSystem loadFhirClientSystem( @Nonnull String id )
    {
        return fhirClientSystemRepository.findById( UUID.fromString( id ) ).orElseThrow( () -> new AssertionError( "FHIR client system does not exist: " + id ) );
    }
}