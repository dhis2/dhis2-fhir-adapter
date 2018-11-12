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
import org.dhis2.fhir.adapter.fhir.metadata.model.Constant;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
 * Tests for {@link ConstantRepository}.
 *
 * @author volsch
 */
public class ConstantRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private ConstantRepository constantRepository;

    @Test
    public void createConstant() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( Constant.class, constraintDescriptionResolver );
        final String request = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createConstant.json", StandardCharsets.UTF_8 );
        final String location = docMockMvc.perform( post( "/api/constants" ).header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( request ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for constant creation" ) ),
                fields.withPath( "name" ).description( "The unique name of the constant." ).type( JsonFieldType.STRING ),
                fields.withPath( "code" ).description( "The unique code of the constant." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description that describes for which purpose the constant is used." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "category" ).description( "The constant category to which the constant belongs to." ).type( JsonFieldType.STRING ),
                fields.withPath( "dataType" ).description( "The data type of the constant value." ).type( JsonFieldType.STRING ),
                fields.withPath( "value" ).description( "The value of the constant (must have the specified data type)." ).type( JsonFieldType.STRING ).optional()
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "name", is( "Gender Female" ) ) )
            .andExpect( jsonPath( "code", is( "GENDER_FEMALE" ) ) )
            .andExpect( jsonPath( "description", is( "Constant for Gender option value as it is used by DHIS2." ) ) )
            .andExpect( jsonPath( "category", is( "GENDER" ) ) )
            .andExpect( jsonPath( "dataType", is( "STRING" ) ) )
            .andExpect( jsonPath( "value", is( "Female" ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readConstant() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( Constant.class, constraintDescriptionResolver );
        final String constantId = loadConstant( "GENDER_MALE" ).getId().toString();
        docMockMvc.perform( get( "/api/constants/{constantId}", constantId ).header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "constant" ).description( "Link to this resource itself." ) ), responseFields(
                attributes( key( "title" ).value( "Fields for constant reading" ) ),
                fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                fields.withPath( "name" ).description( "The unique name of the constant." ).type( JsonFieldType.STRING ),
                fields.withPath( "code" ).description( "The unique code of the constant." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description that describes for which purpose the constant is used." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "category" ).description( "The constant category to which the constant belongs to." ).type( JsonFieldType.STRING ),
                fields.withPath( "dataType" ).description( "The data type of the constant value." ).type( JsonFieldType.STRING ),
                fields.withPath( "value" ).description( "The data type of the constant value (must have the specified data type)." ).type( JsonFieldType.STRING ),
                subsectionWithPath( "_links" ).description( "Links to other resources" )
            ) ) );
    }

    @Nonnull
    protected Constant loadConstant( @Nonnull String code )
    {
        final Constant example = new Constant();
        example.setCode( code );
        return constantRepository.findOne( Example.of( example ) ).orElseThrow( () -> new AssertionError( "Constant does not exist: " + code ) );
    }
}