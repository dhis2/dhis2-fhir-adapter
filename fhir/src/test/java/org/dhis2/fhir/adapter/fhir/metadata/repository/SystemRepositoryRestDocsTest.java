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
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import javax.annotation.Nonnull;
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
 * Tests for {@link SystemRepository}.
 *
 * @author volsch
 */
public class SystemRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private SystemRepository systemRepository;

    @Test
    public void createSystem() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( System.class, constraintDescriptionResolver );
        final String location = docMockMvc.perform( post( "/api/systems" ).header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createSystem.json" ) ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for system creation" ) ),
                fields.withPath( "name" ).description( "The unique name of the system." ).type( JsonFieldType.STRING ),
                fields.withPath( "code" ).description( "The unique code of the system." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description that describes for which purpose the system is used." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "systemUri" ).description( "The system URI of the system." ).type( JsonFieldType.STRING ),
                fields.withPath( "enabled" ).description( "Specifies if this system and its code are enabled." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "descriptionProtected" ).description( "Specifies if the description contains license information that must not be changed." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirDisplayName" ).description( "Display name of the system and its assigned identifiers and codes that is used when storing data on FHIR clients." ).type( JsonFieldType.STRING ).optional()
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "name", is( "Sierra Leone Patient" ) ) )
            .andExpect( jsonPath( "code", is( "SYSTEM_SL_PATIENT" ) ) )
            .andExpect( jsonPath( "description", is( "All Sierra Leone patients." ) ) )
            .andExpect( jsonPath( "systemUri", is( "http://example.sl/patients" ) ) )
            .andExpect( jsonPath( "enabled", is( true ) ) )
            .andExpect( jsonPath( "descriptionProtected", is( false ) ) )
            .andExpect( jsonPath( "fhirDisplayName", is( "National Patients" ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readSystem() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( System.class, constraintDescriptionResolver );
        final String systemId = loadSystem( "SYSTEM_SL_ORGANIZATION" ).getId().toString();
        docMockMvc.perform( get( "/api/systems/{systemId}", systemId ).header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "system" ).description( "Link to this resource itself." ) ), responseFields(
                attributes( key( "title" ).value( "Fields for system reading" ) ),
                fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                fields.withPath( "name" ).description( "The unique name of the system." ).type( JsonFieldType.STRING ),
                fields.withPath( "code" ).description( "The unique code of the system." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description that describes for which purpose the system is used." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "systemUri" ).description( "The system URI of the system." ).type( JsonFieldType.STRING ),
                fields.withPath( "enabled" ).description( "Specifies if this system and its code are enabled." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "fhirDisplayName" ).description( "Display name of the system and its assigned identifiers and codes that is used when storing data on FHIR clients." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "descriptionProtected" ).description( "Specifies if the description contains license information that must not be changed." ).type( JsonFieldType.BOOLEAN ).optional(),
                subsectionWithPath( "_links" ).description( "Links to other resources" )
            ) ) );
    }

    @Nonnull
    protected System loadSystem( @Nonnull String code )
    {
        final System example = new System();
        example.setCode( code );
        return systemRepository.findOne( Example.of( example, ExampleMatcher.matching().withIgnorePaths( "enabled" ) ) ).orElseThrow( () -> new AssertionError( "System does not exist: " + code ) );
    }
}