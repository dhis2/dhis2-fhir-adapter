package org.dhis2.fhir.adapter.fhir.metadata.repository.validator;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.fhir.AbstractJpaRepositoryTest;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSet;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.OrganizationUnitRule;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Validator tests for the corresponding repository.
 *
 * @author volsch
 */
public class BeforeCreateSaveOrganizationUnitRuleValidatorTest extends AbstractJpaRepositoryTest
{
    public static final String RESOURCE_PATH = "/api/organizationUnitRules";

    public static final String AUTHORIZATION_HEADER_VALUE = CODE_MAPPING_AUTHORIZATION_HEADER_VALUE;

    private ExecutableScript identifierLookupScript;

    private ExecutableScript otherTransformInScript;

    private CodeSet applicableCodeSet;

    private OrganizationUnitRule entity;

    @Before
    public void before()
    {
        identifierLookupScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "DHIS_ORG_UNIT_IDENTIFIER_LOC" ).getSingleResult();
        otherTransformInScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "CP_OPV_DOSE" ).getSingleResult();

        entity = new OrganizationUnitRule();
        entity.setEnabled( false );
        entity.setEvaluationOrder( Integer.MIN_VALUE );
        entity.setName( createUnique() );
        entity.setFhirResourceType( FhirResourceType.ORGANIZATION );
        entity.setDescription( createUnique() );
    }

    @Test
    public void testNameBlank() throws Exception
    {
        entity.setName( "    " );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "identifierLookupScript", "executableScripts", identifierLookupScript.getId().toString() ),
                JsonEntityValue.create( "managingOrgIdentifierLookupScript", "executableScripts", identifierLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "name" ) ) );
    }

    @Test
    public void testNameLength() throws Exception
    {
        entity.setName( StringUtils.repeat( 'a', OrganizationUnitRule.MAX_NAME_LENGTH + 1 ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "identifierLookupScript", "executableScripts", identifierLookupScript.getId().toString() ),
                JsonEntityValue.create( "managingOrgIdentifierLookupScript", "executableScripts", identifierLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "name" ) ) );
    }

    @Test
    public void testFhirResourceTypeNull() throws Exception
    {
        entity.setFhirResourceType( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "identifierLookupScript", "executableScripts", identifierLookupScript.getId().toString() ),
                JsonEntityValue.create( "managingOrgIdentifierLookupScript", "executableScripts", identifierLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "fhirResourceType" ) ) );
    }

    @Test
    public void testIdentifierLookupNull() throws Exception
    {
        entity.setIdentifierLookupScript( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "managingOrgIdentifierLookupScript", "executableScripts", identifierLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "identifierLookupScript" ) ) );
    }

    @Test
    public void testManagingOrgIdentifierLookupNull() throws Exception
    {
        entity.setManagingOrgIdentifierLookupScript( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "identifierLookupScript", "executableScripts", identifierLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testIdentifierLookupInvalid() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "identifierLookupScript", "executableScripts", otherTransformInScript.getId().toString() ),
                JsonEntityValue.create( "managingOrgIdentifierLookupScript", "executableScripts", identifierLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "identifierLookupScript" ) ) );
    }

    @Test
    public void testManagingOrgIdentifierLookupInvalid() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "identifierLookupScript", "executableScripts", identifierLookupScript.getId().toString() ),
                JsonEntityValue.create( "managingOrgIdentifierLookupScript", "executableScripts", otherTransformInScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "managingOrgIdentifierLookupScript" ) ) );
    }
}