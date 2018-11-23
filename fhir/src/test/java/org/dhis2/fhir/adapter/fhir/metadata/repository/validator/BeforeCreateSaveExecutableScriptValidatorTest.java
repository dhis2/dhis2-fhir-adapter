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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.AbstractJpaRepositoryTest;
import org.dhis2.fhir.adapter.fhir.metadata.model.DataType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScriptArg;
import org.dhis2.fhir.adapter.fhir.metadata.model.Script;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArg;
import org.hamcrest.Matchers;
import org.hibernate.Hibernate;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Validator tests for the corresponding repository.
 *
 * @author volsch
 */
@SuppressWarnings( "JpaQlInspection" )
public class BeforeCreateSaveExecutableScriptValidatorTest extends AbstractJpaRepositoryTest
{
    public static final String RESOURCE_PATH = "/api/executableScripts";

    public static final String AUTHORIZATION_HEADER_VALUE = CODE_MAPPING_AUTHORIZATION_HEADER_VALUE;

    private Script script;

    private ScriptArg intArg;

    private ScriptArg intArrayArg;

    private List<ExecutableScriptArg> overrideArguments;

    private ExecutableScript entity;

    @Before
    public void before()
    {
        final TransactionStatus status = platformTransactionManager.getTransaction( new DefaultTransactionDefinition() );
        try
        {
            script = entityManager.createQuery( "SELECT e FROM Script e WHERE e.code=:code", Script.class )
                .setParameter( "code", "TEST" ).getSingleResult();
            Hibernate.initialize( script.getArguments() );

            ScriptArg arg = new ScriptArg();
            arg.setName( createUnique() );
            arg.setDataType( DataType.STRING );
            arg.setMandatory( true );
            arg.setScript( script );
            entityManager.persist( arg );
            script.getArguments().add( arg );

            arg = new ScriptArg();
            arg.setName( createUnique() );
            arg.setDataType( DataType.INTEGER );
            arg.setMandatory( false );
            arg.setScript( script );
            entityManager.persist( arg );
            script.getArguments().add( arg );
            intArg = arg;

            arg = new ScriptArg();
            arg.setName( createUnique() );
            arg.setDataType( DataType.INTEGER );
            arg.setMandatory( false );
            arg.setScript( script );
            arg.setArray( true );
            entityManager.persist( arg );
            script.getArguments().add( arg );
            intArrayArg = arg;

            script = entityManager.merge( script );
            entityManager.detach( script );
        }
        finally
        {
            platformTransactionManager.commit( status );
        }

        entity = new ExecutableScript();
        entity.setName( createUnique() );
        entity.setCode( createUnique() );
        entity.setDescription( createUnique() );
        overrideArguments = script.getArguments().stream()
            .filter( a -> a.isMandatory() && (a.getDefaultValue() == null) ).map( a -> {
                a.setScript( script );
                final ExecutableScriptArg executableScriptArg = new ExecutableScriptArg();
                executableScriptArg.setArgument( a );
                executableScriptArg.setOverrideValue( "b" );
                return executableScriptArg;
            } ).collect( Collectors.toList() );
        script.setArguments( null );
        script.setVariables( null );
        script.setSources( null );
    }

    @Test
    public void testScript() throws Exception
    {
        entity.setName( "    " );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "script" ) ) );
    }

    @Test
    public void testNameBlank() throws Exception
    {
        script.setArguments( null );
        script.setVariables( null );
        script.setSources( null );

        final List<String> ids = overrideArguments.stream().map( oa -> oa.getArgument().getId().toString() ).collect( Collectors.toList() );
        entity.setOverrideArguments( overrideArguments );

        entity.setName( "    " );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityReference.create( "script", "scripts", script.getId().toString() ),
                JsonEntityReference.create( "overrideArguments/argument", "scriptArgs", ids ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "name" ) ) );
    }

    @Test
    public void testNameLength() throws Exception
    {
        final List<String> ids = overrideArguments.stream().map( oa -> oa.getArgument().getId().toString() ).collect( Collectors.toList() );
        entity.setOverrideArguments( overrideArguments );

        entity.setName( StringUtils.repeat( 'a', ExecutableScript.MAX_NAME_LENGTH + 1 ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityReference.create( "script", "scripts", script.getId().toString() ),
                JsonEntityReference.create( "overrideArguments/argument", "scriptArgs", ids ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "name" ) ) );
    }

    @Test
    public void testCodeBlank() throws Exception
    {
        script.setArguments( null );
        script.setVariables( null );
        script.setSources( null );

        final List<String> ids = overrideArguments.stream().map( oa -> oa.getArgument().getId().toString() ).collect( Collectors.toList() );
        entity.setOverrideArguments( overrideArguments );

        entity.setCode( "    " );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityReference.create( "script", "scripts", script.getId().toString() ),
                JsonEntityReference.create( "overrideArguments/argument", "scriptArgs", ids ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "code" ) ) );
    }

    @Test
    public void testCodeLength() throws Exception
    {
        script.setArguments( null );
        script.setVariables( null );
        script.setSources( null );

        final List<String> ids = overrideArguments.stream().map( oa -> oa.getArgument().getId().toString() ).collect( Collectors.toList() );
        entity.setOverrideArguments( overrideArguments );

        entity.setCode( StringUtils.repeat( 'a', ExecutableScript.MAX_CODE_LENGTH + 1 ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityReference.create( "script", "scripts", script.getId().toString() ),
                JsonEntityReference.create( "overrideArguments/argument", "scriptArgs", ids ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "code" ) ) );
    }

    @Test
    public void testOverrideValueLength() throws Exception
    {
        final List<String> ids = overrideArguments.stream().map( oa -> oa.getArgument().getId().toString() ).collect( Collectors.toList() );
        overrideArguments.get( 0 ).setOverrideValue( StringUtils.repeat( 'a', ScriptArg.MAX_DEFAULT_VALUE_LENGTH + 1 ) );
        entity.setOverrideArguments( overrideArguments );

        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityReference.create( "script", "scripts", script.getId().toString() ),
                JsonEntityReference.create( "overrideArguments/argument", "scriptArgs", ids ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "overrideArguments[0].overrideValue" ) ) );
    }

    @Test
    public void testOverrideValueNull() throws Exception
    {
        final List<String> ids = overrideArguments.stream().map( oa -> oa.getArgument().getId().toString() ).collect( Collectors.toList() );
        overrideArguments.get( 0 ).setOverrideValue( null );
        entity.setOverrideArguments( overrideArguments );

        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityReference.create( "script", "scripts", script.getId().toString() ),
                JsonEntityReference.create( "overrideArguments/argument", "scriptArgs", ids ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "overrideArguments[0].overrideValue" ) ) );
    }

    @Test
    public void testOverrideValueDisabled() throws Exception
    {
        final List<String> ids = overrideArguments.stream().map( oa -> oa.getArgument().getId().toString() ).collect( Collectors.toList() );
        overrideArguments.get( 0 ).setEnabled( false );
        entity.setOverrideArguments( overrideArguments );

        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityReference.create( "script", "scripts", script.getId().toString() ),
                JsonEntityReference.create( "overrideArguments/argument", "scriptArgs", ids ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "overrideArguments" ) ) );
    }

    @Test
    public void testOverrideValueDuplicate() throws Exception
    {
        final List<ExecutableScriptArg> overrideArguments = new ArrayList<>( this.overrideArguments );
        overrideArguments.add( 0, overrideArguments.get( 0 ) );
        final List<String> ids = overrideArguments.stream().map( oa -> oa.getArgument().getId().toString() ).collect( Collectors.toList() );
        entity.setOverrideArguments( overrideArguments );

        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityReference.create( "script", "scripts", script.getId().toString() ),
                JsonEntityReference.create( "overrideArguments/argument", "scriptArgs", ids ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "overrideArguments[1]" ) ) );
    }

    @Test
    public void testOverrideValueInvalidInt() throws Exception
    {
        final ExecutableScriptArg arg = new ExecutableScriptArg();
        arg.setArgument( intArg );
        arg.setOverrideValue( "zzz" );

        final List<ExecutableScriptArg> overrideArguments = new ArrayList<>( this.overrideArguments );
        overrideArguments.add( 0, arg );
        final List<String> ids = overrideArguments.stream().map( oa -> oa.getArgument().getId().toString() ).collect( Collectors.toList() );
        entity.setOverrideArguments( overrideArguments );

        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityReference.create( "script", "scripts", script.getId().toString() ),
                JsonEntityReference.create( "overrideArguments/argument", "scriptArgs", ids ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "overrideArguments[0].overrideValue" ) ) );
    }

    @Test
    public void testOverrideValueInvalidIntArray() throws Exception
    {
        final ExecutableScriptArg arg = new ExecutableScriptArg();
        arg.setArgument( intArrayArg );
        arg.setOverrideValue( "123|zzz" );

        final List<ExecutableScriptArg> overrideArguments = new ArrayList<>( this.overrideArguments );
        overrideArguments.add( 0, arg );
        final List<String> ids = overrideArguments.stream().map( oa -> oa.getArgument().getId().toString() ).collect( Collectors.toList() );
        entity.setOverrideArguments( overrideArguments );

        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityReference.create( "script", "scripts", script.getId().toString() ),
                JsonEntityReference.create( "overrideArguments/argument", "scriptArgs", ids ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "overrideArguments[0].overrideValue" ) ) );
    }
}