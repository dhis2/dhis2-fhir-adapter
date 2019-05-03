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
import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeCategory;
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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for {@link CodeRepository}.
 *
 * @author volsch
 */
public class CodeRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private CodeCategoryRepository codeCategoryRepository;

    @Autowired
    private CodeRepository codeRepository;

    @Test
    public void createCode() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( Code.class, constraintDescriptionResolver );
        final String codeCategoryId = loadCodeCategory( "ORGANIZATION_UNIT" ).getId().toString();
        final String request = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createCode.json", StandardCharsets.UTF_8 )
            .replace( "$codeCategory", API_BASE_URI + "/codeCategories/" + codeCategoryId );
        final String location = docMockMvc.perform( post( "/api/codes" ).header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( request ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for code creation" ) ),
                fields.withPath( "name" ).description( "The unique name of the code." ).type( JsonFieldType.STRING ),
                fields.withPath( "enabled" ).description( "Specifies if the code can be used (by default true)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "code" ).description( "The unique code of the code." ).type( JsonFieldType.STRING ),
                fields.withPath( "mappedCode" ).description( "The optional mapped code (e.g. organization unit code as it exists on DHIS2). If this is not specified the code itself is used." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "description" ).description( "The detailed description that describes for which purpose the code is used." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "codeCategory" ).description( "The reference to the code category to which this code belongs to." ).type( JsonFieldType.STRING )
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "name", is( "Test Code" ) ) )
            .andExpect( jsonPath( "enabled", is( true ) ) )
            .andExpect( jsonPath( "code", is( "TEST_CODE" ) ) )
            .andExpect( jsonPath( "mappedCode", is( "MAPPED_TEST_CODE" ) ) )
            .andExpect( jsonPath( "description", is( "This is a test code." ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) )
            .andExpect( jsonPath( "_links.codeCategory.href", is( location + "/codeCategory" ) ) );
    }

    @Test
    public void readCode() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( Code.class, constraintDescriptionResolver );
        final String codeId = loadCode( "OU_FT_CH" ).getId().toString();
        docMockMvc.perform( get( "/api/codes/{codeId}", codeId ).header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "code" ).description( "Link to this resource itself." ),
                linkWithRel( "codeCategory" ).description( "Link to this resource itself." ) ), responseFields(
                attributes( key( "title" ).value( "Fields for code reading" ) ),
                fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                fields.withPath( "name" ).description( "The unique name of the code." ).type( JsonFieldType.STRING ),
                fields.withPath( "enabled" ).description( "Specifies if the code can be used." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "code" ).description( "The unique code of the code." ).type( JsonFieldType.STRING ),
                fields.withPath( "mappedCode" ).description( "The optional mapped code (e.g. organization unit code as it exists on DHIS2). If this is not specified the code itself is used." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "description" ).description( "The detailed description that describes for which purpose the code is used." ).type( JsonFieldType.STRING ).optional(),
                subsectionWithPath( "_links" ).description( "Links to other resources" )
            ) ) );
    }

    @Test
    public void readCodesPaged() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( Code.class, constraintDescriptionResolver );
        docMockMvc.perform( get( "/api/codes" ).param( "size", "3" ).header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "first" ).description( "Link to the first page of the paged resource." ),
                linkWithRel( "prev" ).description( "Link to the previous page (if any) of the paged resource." ).optional(),
                linkWithRel( "next" ).description( "Link to the next page (if any) of the paged resource." ).optional(),
                linkWithRel( "last" ).description( "Link to the last page of the paged resource." ),
                linkWithRel( "profile" ).description( "Link to the profile of this resource." ) ), responseFields(
                attributes( key( "title" ).value( "Fields for paged code reading" ) ),
                fieldWithPath( "page" ).description( "The paging information." ).type( JsonFieldType.OBJECT ),
                fieldWithPath( "page.size" ).description( "The used page size (may be less than the specified amount)." ).type( JsonFieldType.NUMBER ),
                fieldWithPath( "page.totalElements" ).description( "The total amount of elements." ).type( JsonFieldType.NUMBER ),
                fieldWithPath( "page.totalPages" ).description( "The total number of pages." ).type( JsonFieldType.NUMBER ),
                fieldWithPath( "page.number" ).description( "The current page number." ).type( JsonFieldType.NUMBER ),
                subsectionWithPath( "_embedded" ).ignored(),
                subsectionWithPath( "_links" ).description( "Links to other resources" )
                )
            ) );
    }

    @Test
    public void readCodesSortedPaged() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( Code.class, constraintDescriptionResolver );
        docMockMvc.perform( get( "/api/codes" ).param( "page", "0" ).param( "size", "3" ).param( "sort", "lastUpdatedAt,desc" )
            .header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "first" ).description( "Link to the first page of the paged resource." ),
                linkWithRel( "prev" ).description( "Link to the previous page (if any) of the paged resource." ).optional(),
                linkWithRel( "next" ).description( "Link to the next page (if any) of the paged resource." ).optional(),
                linkWithRel( "last" ).description( "Link to the last page of the paged resource." ),
                linkWithRel( "profile" ).description( "Link to the profile of this resource." ) ), requestParameters(
                parameterWithName( "page" ).description( "The current page number to display." ).optional(),
                parameterWithName( "size" ).description( "The number of elements to display one one page." ).optional(),
                parameterWithName( "sort" ).description( "The sort that should be applied with format propertyName[,asc|desc]" ).optional()
                ), responseFields(
                attributes( key( "title" ).value( "Fields for paged code reading" ) ),
                fieldWithPath( "page" ).description( "The paging information." ).type( JsonFieldType.OBJECT ),
                fieldWithPath( "page.size" ).description( "The used page size (may be less than the specified amount)." ).type( JsonFieldType.NUMBER ),
                fieldWithPath( "page.totalElements" ).description( "The total amount of elements." ).type( JsonFieldType.NUMBER ),
                fieldWithPath( "page.totalPages" ).description( "The total number of pages." ).type( JsonFieldType.NUMBER ),
                fieldWithPath( "page.number" ).description( "The current page number." ).type( JsonFieldType.NUMBER ),
                subsectionWithPath( "_embedded" ).ignored(),
                subsectionWithPath( "_links" ).description( "Links to other resources" )
                )
            ) );
    }

    @Test
    public void readCodesFiltered() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( Code.class, constraintDescriptionResolver );
        docMockMvc.perform( get( "/api/codes" ).param( "name", "DTaP" )
            .header( AUTHORIZATION_HEADER_NAME, CODE_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document() );
    }

    @Nonnull
    protected Code loadCode( @Nonnull String code )
    {
        final Code example = new Code();
        example.setCode( code );
        return codeRepository.findOne( Example.of( example ) ).orElseThrow( () -> new AssertionError( "Code does not exist: " + code ) );
    }

    @Nonnull
    protected CodeCategory loadCodeCategory( @Nonnull String code )
    {
        final CodeCategory example = new CodeCategory();
        example.setCode( code );
        return codeCategoryRepository.findOne( Example.of( example ) ).orElseThrow( () -> new AssertionError( "Code category does not exist: " + code ) );
    }
}