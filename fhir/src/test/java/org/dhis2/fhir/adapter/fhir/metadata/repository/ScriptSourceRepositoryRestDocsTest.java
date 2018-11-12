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
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptSource;
import org.hamcrest.Matchers;
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
 * Tests for {@link ScriptSourceRepository}.
 *
 * @author volsch
 */
public class ScriptSourceRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired( required = false )
    private ScriptRepository scriptRepository;

    @Autowired( required = false )
    private ScriptSourceRepository scriptSourceRepository;

    @Test
    public void createScriptSource() throws Exception
    {
        final String scriptId = loadScript( "TRANSFORM_FHIR_OB_BODY_WEIGHT" ).getId().toString();
        final ConstrainedFields fields = new ConstrainedFields( ScriptSource.class, constraintDescriptionResolver );
        final String request = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createScriptSource.json", StandardCharsets.UTF_8 ).replace( "$scriptId", API_BASE_URI + "/script/" + scriptId );
        final String location = docMockMvc.perform( post( "/api/scriptSources" ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( request ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for script source creation" ) ),
                fields.withPath( "script" ).description( "The reference to the script to which this source belongs to." ).type( JsonFieldType.STRING ),
                fields.withPath( "sourceType" ).description( "The type of the source code (the programming language)." ).type( JsonFieldType.STRING ),
                fields.withPath( "sourceText" ).description( "The code of the script in the configured programming language (source type)." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "fhirVersions" ).description( "The FHIR versions that are supported by this script source." ).type( JsonFieldType.ARRAY )
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827e" ) ) )
            .andExpect( jsonPath( "sourceText", is( "output.setValue(args['dataElement'], vitalSignUtils.getWeight(input.value, args['weightUnit'], args['round']), null, args['override'], context.getFhirRequest().getLastUpdated())" ) ) )
            .andExpect( jsonPath( "sourceType", is( "JAVASCRIPT" ) ) )
            .andExpect( jsonPath( "fhirVersions", Matchers.contains( "DSTU3" ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readScriptSource() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( ScriptSource.class, constraintDescriptionResolver );
        final String scriptSourceId = loadScriptSource( "081c4642-bb83-44ab-b90f-aa206ad347aa" ).getId().toString();
        docMockMvc.perform( get( "/api/scriptSources/{scriptSourceId}", scriptSourceId ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "scriptSource" ).description( "Link to this resource itself." ),
                linkWithRel( "script" ).description( "Link to the script to which the resource belongs to." ) ), responseFields(
                attributes( key( "title" ).value( "Fields for script source reading" ) ),
                fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                fields.withPath( "sourceType" ).description( "The type of the source code (the programming language)." ).type( JsonFieldType.STRING ),
                fields.withPath( "sourceText" ).description( "The code of the script in the configured programming language (source type)." ).type( JsonFieldType.STRING ),
                fields.withPath( "fhirVersions" ).description( "The FHIR versions that are supported by this script source." ).type( JsonFieldType.ARRAY ),
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
    protected ScriptSource loadScriptSource( @Nonnull String scriptId )
    {
        return scriptSourceRepository.findById( UUID.fromString( scriptId ) ).orElseThrow( () -> new AssertionError( "Script source does not exist: " + scriptId ) );
    }
}