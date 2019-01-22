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
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.Script;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArg;
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
 * Tests for {@link ExecutableScriptRepository}.
 *
 * @author volsch
 */
public class ExecutableScriptRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private ScriptRepository scriptRepository;

    @Autowired
    private ScriptArgRepository scriptArgRepository;

    @Autowired
    private ExecutableScriptRepository executableScriptRepository;

    @Test
    public void createExecutableScript() throws Exception
    {
        final Script script = loadScript( "TRANSFORM_FHIR_OB_BODY_WEIGHT" );
        final String scriptId = script.getId().toString();
        final String dataElementScriptArgId = loadScriptArg( script, "dataElement" ).getId().toString();
        final String roundScriptArgId = loadScriptArg( script, "round" ).getId().toString();

        final ConstrainedFields fields = new ConstrainedFields( ExecutableScript.class, constraintDescriptionResolver );
        final String request = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createExecutableScript.json", StandardCharsets.UTF_8 )
            .replace( "$scriptId", API_BASE_URI + "/script/" + scriptId )
            .replace( "$dataElementArgId", API_BASE_URI + "/scriptArgs/" + dataElementScriptArgId )
            .replace( "$roundArgId", API_BASE_URI + "/scriptArgs/" + roundScriptArgId );
        final String location = docMockMvc.perform( post( "/api/executableScripts" ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( request ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for executable script creation" ) ),
                fields.withPath( "script" ).description( "The reference to the script resource that is executed by this resource definition." ).type( JsonFieldType.STRING ),
                fields.withPath( "name" ).description( "The name of the executable script." ).type( JsonFieldType.STRING ),
                fields.withPath( "code" ).description( "The code of the executable script." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description of the purpose of the executable script." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "overrideArguments" ).description( "The overridden arguments of the script resource. If the script resource defined mandatory arguments with null values, these must be specified for the executable script. " +
                    "Otherwise the script cannot be executed. If no argument value should be overridden, this field need not to be specified." ).type( JsonFieldType.ARRAY ).optional(),
                fields.withPath( "baseExecutableScript" ).description( "Link to another executable script from which arguments are inherited." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "overrideArguments[].argument" ).description( "Reference to the script argument resource for which the value should be overridden." ).type( JsonFieldType.STRING ),
                fields.withPath( "overrideArguments[].overrideValue" ).description( "The value that should be used for the argument when executing the script. The value must match the data type of the argument." ).type( JsonFieldType.STRING ),
                fields.withPath( "overrideArguments[].enabled" ).description( "Specifies if the override argument is enabled. If the override argument is not enabled, the value of the script argument itself is used." ).type( JsonFieldType.BOOLEAN )
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "name", is( "CP: Baby Birth Weight" ) ) )
            .andExpect( jsonPath( "code", is( "CP_BABY_BIRTH_WEIGHT" ) ) )
            .andExpect( jsonPath( "overrideArguments.length()", is( 2 ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readExecutableScript() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( ExecutableScript.class, constraintDescriptionResolver );
        final String executableScriptId = loadExecutableScript( "CP_OPV_DOSE" ).getId().toString();
        docMockMvc.perform( get( "/api/executableScripts/{executableScriptId}", executableScriptId ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "executableScript" ).description( "Link to this resource itself." ),
                linkWithRel( "script" ).description( "Link to the script to which the resource belongs to." ),
                linkWithRel( "baseExecutableScript" ).description( "Link to another executable script from which arguments are inherited." ).optional() ), responseFields(
                attributes( key( "title" ).value( "Fields for executable script reading" ) ),
                fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                fields.withPath( "name" ).description( "The name of the executable script." ).type( JsonFieldType.STRING ),
                fields.withPath( "code" ).description( "The code of the executable script." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description of the purpose of the executable script." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "overrideArguments" ).description( "The overridden arguments of the script resource. If the script resource defined mandatory arguments with null values, these must be specified for the executable script. " +
                    "Otherwise the script cannot be executed. If no argument value should be overridden, this field need not to be specified." ).type( JsonFieldType.ARRAY ).optional(),
                fields.withPath( "overrideArguments[].overrideValue" ).description( "The value that should be used for the argument when executing the script. The value must match the data type of the argument." ).type( JsonFieldType.STRING ),
                fields.withPath( "overrideArguments[].enabled" ).description( "Specifies if the override argument is enabled. If the override argument is not enabled, the value of the script argument itself is used." ).type( JsonFieldType.BOOLEAN ),
                subsectionWithPath( "_links" ).description( "Links to other resources" ),
                subsectionWithPath( "overrideArguments[]._links" ).description( "Links to other resources" )
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
    protected ScriptArg loadScriptArg( @Nonnull Script script, @Nonnull String name )
    {
        final Script exampleScript = new Script();
        exampleScript.setId( script.getId() );
        final ScriptArg example = new ScriptArg();
        example.setScript( exampleScript );
        example.setName( name );
        return scriptArgRepository.findOne( Example.of( example, ExampleMatcher.matching().withIgnorePaths( "arrayValue", "mandatory" ) ) )
            .orElseThrow( () -> new AssertionError( "Script argument does not exist: " + name ) );
    }

    @Nonnull
    protected ExecutableScript loadExecutableScript( @Nonnull String code )
    {
        final ExecutableScript executableScript = new ExecutableScript();
        executableScript.setCode( code );
        return executableScriptRepository.findOne( Example.of( executableScript ) ).orElseThrow( () -> new AssertionError( "Executable script does not exist: " + code ) );
    }
}