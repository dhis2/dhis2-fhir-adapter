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

import org.dhis2.fhir.adapter.fhir.AbstractJpaRepositoryTest;
import org.dhis2.fhir.adapter.fhir.metadata.model.Script;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptSource;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptSourceType;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.TreeSet;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Validator tests for the corresponding repository.
 *
 * @author volsch
 */
public class BeforeCreateSaveScriptSourceValidatorTest extends AbstractJpaRepositoryTest
{
    public static final String RESOURCE_PATH = "/api/scriptSources";

    public static final String AUTHORIZATION_HEADER_VALUE = CODE_MAPPING_AUTHORIZATION_HEADER_VALUE;

    private Script script;

    private ScriptSource entity;

    @Before
    public void before()
    {
        script = entityManager.createQuery( "SELECT e FROM Script e WHERE e.code=:code", Script.class )
            .setParameter( "code", "TEST" ).getSingleResult();

        final TreeSet<FhirVersion> versions = new TreeSet<>();
        versions.add( FhirVersion.DSTU3 );

        entity = new ScriptSource();
        entity.setScript( script );
        entity.setFhirVersions( versions );
        entity.setSourceType( ScriptSourceType.JAVASCRIPT );
        entity.setSourceText( "true" );
    }

    @Test
    public void testScriptNull() throws Exception
    {
        entity.setScript( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( objectMapper.writeValueAsString( entity ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "script" ) ) );
    }

    @Test
    public void testFhirVersionsNull() throws Exception
    {
        entity.setScript( null );
        entity.setFhirVersions( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "script", "scripts", script.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "fhirVersions" ) ) );
    }

    @Test
    public void testSourceTypeNull() throws Exception
    {
        entity.setScript( null );
        entity.setSourceType( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "script", "scripts", script.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "sourceType" ) ) );
    }

    @Test
    public void testSourceBlank() throws Exception
    {
        entity.setScript( null );
        entity.setSourceText( "     " );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "script", "scripts", script.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "sourceText" ) ) );
    }

    @Test
    public void testSourceSyntax() throws Exception
    {
        entity.setScript( null );
        entity.setSourceText( "x += y +* 20; true" );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "script", "scripts", script.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "sourceText" ) ) );
    }
}