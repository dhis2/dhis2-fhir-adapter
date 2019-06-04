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
import org.dhis2.fhir.adapter.fhir.metadata.model.Script;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArg;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
 * Tests for {@link ScriptArg}.
 *
 * @author volsch
 */
public class ScriptArgRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    private ScriptArgRepository scriptArgRepository;

    @Test
    public void createScriptArg() throws Exception
    {
        final String scriptId = loadScript( "TRANSFORM_FHIR_OB_BODY_WEIGHT" ).getId().toString();
        final ConstrainedFields fields = new ConstrainedFields( ScriptArg.class, constraintDescriptionResolver );
        final String request = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createScriptArg.json", StandardCharsets.UTF_8 )
            .replace( "$scriptId", API_BASE_URI + "/script/" + scriptId );
        final String location = docMockMvc.perform( post( "/api/scriptArgs" ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( request ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for script argument creation" ) ),
                fields.withPath( "script" ).description( "The reference to the script resource to which this argument belongs to." ).type( JsonFieldType.STRING ),
                fields.withPath( "name" ).description( "The name of the script argument. This is also used inside the script source to access the argument value." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description of the purpose of the argument." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "dataType" ).description( "The data type of the argument value." ).type( JsonFieldType.STRING ),
                fields.withPath( "array" ).description( "Specifies if the argument contains an array of values. The values must be separated by a pipe character." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "mandatory" ).description( "Specifies if the argument is mandatory and cannot be null when the script is executed." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "defaultValue" ).description( "The default value of the argument. This may be overridden by the executable script. The value must be convertible to the specified data type. " +
                    "If the argument is an array, the array values must be separated by pipe characters." ).type( JsonFieldType.STRING ).optional()
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "name", is( "otherWeightUnit" ) ) )
            .andExpect( jsonPath( "description", is( "The resulting weight unit in which the value will be set on the data element." ) ) )
            .andExpect( jsonPath( "dataType", is( "WEIGHT_UNIT" ) ) )
            .andExpect( jsonPath( "array", is( false ) ) )
            .andExpect( jsonPath( "mandatory", is( true ) ) )
            .andExpect( jsonPath( "defaultValue", is( "KILO_GRAM" ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readScriptArg() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( ScriptArg.class, constraintDescriptionResolver );
        final String scriptArgId = loadScriptArg( "d8cd0e7d-7780-45d1-8094-b448b480e6b8" ).getId().toString();
        docMockMvc.perform( get( "/api/scriptArgs/{scriptArgId}", scriptArgId ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "scriptArg" ).description( "Link to this resource itself." ),
                linkWithRel( "script" ).description( "Link to the script to which the resource belongs to." ) ), responseFields(
                attributes( key( "title" ).value( "Fields for script argument reading" ) ),
                fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                fields.withPath( "name" ).description( "The name of the script argument. This is also used inside the script source to access the argument value." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description of the purpose of the argument." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "dataType" ).description( "The data type of the argument value." ).type( JsonFieldType.STRING ),
                fields.withPath( "array" ).description( "Specifies if the argument contains an array of values. The values must be separated by a pipe character." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "mandatory" ).description( "Specifies if the argument is mandatory and cannot be null when the script is executed." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "defaultValue" ).description( "Specifies if the argument is mandatory and cannot be null when the script is executed." ).type( JsonFieldType.STRING ).optional(),
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

    @Nonnull
    protected ScriptArg loadScriptArg( @Nonnull String scriptId )
    {
        return scriptArgRepository.findById( UUID.fromString( scriptId ) ).orElseThrow( () -> new AssertionError( "Script source does not exist: " + scriptId ) );
    }
}