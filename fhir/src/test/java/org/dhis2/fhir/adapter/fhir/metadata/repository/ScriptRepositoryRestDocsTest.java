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
import org.dhis2.fhir.adapter.fhir.metadata.model.Script;
import org.hamcrest.Matchers;
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
 * Tests for {@link ScriptRepository}.
 *
 * @author volsch
 */
public class ScriptRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private ScriptRepository scriptRepository;

    @Test
    public void createScript() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( Script.class, constraintDescriptionResolver );
        final String request = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createScript.json", StandardCharsets.UTF_8 );
        final String location = docMockMvc.perform( post( "/api/scripts" ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( request ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for script creation" ) ),
                fields.withPath( "name" ).description( "The unique name of the script." ).type( JsonFieldType.STRING ),
                fields.withPath( "code" ).description( "The unique code of the script." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description that describes for which purpose the script is used." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "scriptType" ).description( "The the of the script that describes its purpose." ).type( JsonFieldType.STRING ),
                fields.withPath( "returnType" ).description( "The data type of the value that is returned by the script." ).type( JsonFieldType.STRING ),
                fields.withPath( "inputType" ).description( "The required data type of the transformation input." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "outputType" ).description( "The required data type of the transformation output." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "variables" ).description( "The variables that are required for the script execution." ).type( JsonFieldType.ARRAY ).optional()
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "name", is( "Transforms FHIR Immunization to Y/N data element" ) ) )
            .andExpect( jsonPath( "code", is( "TRANSFORM_FHIR_IMMUNIZATION_YN" ) ) )
            .andExpect( jsonPath( "description", is( "Transforms FHIR Immunization to Y/N data element." ) ) )
            .andExpect( jsonPath( "scriptType", is( "TRANSFORM_TO_DHIS" ) ) )
            .andExpect( jsonPath( "returnType", is( "BOOLEAN" ) ) )
            .andExpect( jsonPath( "inputType", is( "FHIR_IMMUNIZATION" ) ) )
            .andExpect( jsonPath( "outputType", is( "DHIS_EVENT" ) ) )
            .andExpect( jsonPath( "variables", Matchers.contains( "CONTEXT", "INPUT", "OUTPUT" ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readScript() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( Script.class, constraintDescriptionResolver );
        final String scriptId = loadScript( "TRANSFORM_FHIR_IMMUNIZATION_OS" ).getId().toString();
        docMockMvc.perform( get( "/api/scripts/{scriptId}", scriptId ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "script" ).description( "Link to this resource itself." ),
                linkWithRel( "arguments" ).description( "Link to the arguments that are provided as variable args (contains a map with all argument values)." ),
                linkWithRel( "sources" ).description( "Link to the source codes of the scripts (multiple scripts for different FHIR versions)." ) ), responseFields(
                attributes( key( "title" ).value( "Fields for script reading" ) ),
                fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                fields.withPath( "name" ).description( "The unique name of the script." ).type( JsonFieldType.STRING ),
                fields.withPath( "code" ).description( "The unique code of the script." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description that describes for which purpose the script is used." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "scriptType" ).description( "The the of the script that describes its purpose." ).type( JsonFieldType.STRING ),
                fields.withPath( "returnType" ).description( "The data type of the value that is returned by the script." ).type( JsonFieldType.STRING ),
                fields.withPath( "inputType" ).description( "The required data type of the transformation input." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "outputType" ).description( "The required data type of the transformation output." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "variables" ).description( "The variables that are required for the script execution." ).type( JsonFieldType.ARRAY ).optional(),
                subsectionWithPath( "_links" ).description( "Links to other resources" )
            ) ) );
    }

    @Nonnull
    protected Script loadScript( @Nonnull String code )
    {
        final Script example = new Script();
        example.setCode( code );
        return scriptRepository.findOne( Example.of( example ) ).orElseThrow( () -> new AssertionError( "Script does not exist: " + code ) );
    }
}