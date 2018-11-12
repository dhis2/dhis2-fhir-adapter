package org.dhis2.fhir.adapter.fhir.metadata.repository;

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

import org.apache.commons.io.IOUtils;
import org.dhis2.fhir.adapter.fhir.AbstractJpaRepositoryRestDocsTest;
import org.dhis2.fhir.adapter.fhir.ConstrainedFields;
import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
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
 * Tests for {@link SystemCodeRepository}.
 *
 * @author volsch
 */
public class SystemCodeRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private SystemCodeRepository systemCodeRepository;

    @Test
    public void createSystemCode() throws Exception
    {
        final String systemId = loadSystem( "SYSTEM_SL_ORGANIZATION" ).getId().toString();
        final String codeId = loadCode( "OU_FT_CH" ).getId().toString();
        final ConstrainedFields fields = new ConstrainedFields( SystemCode.class, constraintDescriptionResolver );
        final String json = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createSystemCode.json", StandardCharsets.UTF_8 )
            .replace( "$systemId", API_BASE_URI + "/systems/" + systemId ).replace( "$codeId", API_BASE_URI + "/codes/" + codeId );
        final String location = docMockMvc.perform( post( "/api/systemCodes" ).header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( json ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for system code creation" ) ),
                fields.withPath( "system" ).description( "The reference to the system to which the code belongs to." ).type( JsonFieldType.STRING ),
                fields.withPath( "systemCode" ).description( "The code of the system." ).type( JsonFieldType.STRING ),
                fields.withPath( "code" ).description( "The reference to the internal code that is used for the system specific code." ).type( JsonFieldType.STRING )
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "systemCode", is( "982783729" ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readSystemCode() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( SystemCode.class, constraintDescriptionResolver );
        final String systemCodeId = loadSystemCode( "c513935c-9cd2-4357-a679-60f0c79bfacb" ).getId().toString();
        docMockMvc.perform( get( "/api/systemCodes/{systemCodeId}", systemCodeId ).header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "systemCode" ).description( "Link to this resource itself." ),
                linkWithRel( "system" ).description( "Link to the system resource to which the code belongs to." ),
                linkWithRel( "code" ).description( "Link to the internal code that is mapped to the system specific code." ) ), responseFields(
                attributes( key( "title" ).value( "Fields for system code reading" ) ),
                fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                fields.withPath( "systemCode" ).description( "The code of the system." ).type( JsonFieldType.STRING ),
                fields.withPath( "systemCodeValue" ).description( "The combination of system URI and code separated by a pipe character (generated, cannot be updated)." ).type( JsonFieldType.STRING ),
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

    @Nonnull
    protected Code loadCode( @Nonnull String code )
    {
        final Code example = new Code();
        example.setCode( code );
        return codeRepository.findOne( Example.of( example ) ).orElseThrow( () -> new AssertionError( "Code does not exist: " + code ) );
    }

    @Nonnull
    protected SystemCode loadSystemCode( @Nonnull String id )
    {
        return systemCodeRepository.findById( UUID.fromString( id ) ).orElseThrow( () -> new AssertionError( "System code does not exist: " + id ) );
    }
}