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
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
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
public class BeforeCreateSaveEnrollmentRuleValidatorTest extends AbstractJpaRepositoryTest
{
    public static final String RESOURCE_PATH = "/api/enrollmentRules";

    public static final String AUTHORIZATION_HEADER_VALUE = DATA_MAPPING_AUTHORIZATION_HEADER_VALUE;

    private ExecutableScript programRefLookupScript;

    private ExecutableScript transformImpScript;

    private ExecutableScript otherTransformInScript;

    private EnrollmentRule entity;

    @Before
    public void before()
    {
        programRefLookupScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "CARE_PLAN_PROGRAM_REF_LOOKUP" ).getSingleResult();
        transformImpScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "DEFAULT_CARE_PLAN_F2D" ).getSingleResult();
        otherTransformInScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "CP_OPV_DOSE" ).getSingleResult();

        entity = new EnrollmentRule();
        entity.setEnabled( false );
        entity.setEvaluationOrder( Integer.MIN_VALUE );
        entity.setName( createUnique() );
        entity.setFhirResourceType( FhirResourceType.CARE_PLAN );
        entity.setDescription( createUnique() );
    }

    @Test
    public void testNameBlank() throws Exception
    {
        entity.setName( "    " );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "programRefLookupScript", "executableScripts", programRefLookupScript.getId().toString() ),
                JsonEntityValue.create( "transformImpScript", "executableScripts", transformImpScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "name" ) ) );
    }

    @Test
    public void testNameLength() throws Exception
    {
        entity.setName( StringUtils.repeat( 'a', TrackedEntityRule.MAX_NAME_LENGTH + 1 ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "programRefLookupScript", "executableScripts", programRefLookupScript.getId().toString() ),
                JsonEntityValue.create( "transformImpScript", "executableScripts", transformImpScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "name" ) ) );
    }

    @Test
    public void testFhirResourceTypeNull() throws Exception
    {
        entity.setFhirResourceType( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "programRefLookupScript", "executableScripts", programRefLookupScript.getId().toString() ),
                JsonEntityValue.create( "transformImpScript", "executableScripts", transformImpScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "fhirResourceType" ) ) );
    }

    @Test
    public void testTransformInScriptNull() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "programRefLookupScript", "executableScripts", programRefLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testMinimal() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "programRefLookupScript", "executableScripts", programRefLookupScript.getId().toString() ),
                JsonEntityValue.create( "transformImpScript", "executableScripts", transformImpScript.getId().toString() ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testProframRefLookupTransformation() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "programRefLookupScript", "executableScripts", transformImpScript.getId().toString() ),
                JsonEntityValue.create( "transformImpScript", "executableScripts", transformImpScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "programRefLookupScript" ) ) );
    }

    @Test
    public void testApplicableInScriptTransformation() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "programRefLookupScript", "executableScripts", programRefLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableImpScript", "executableScripts", transformImpScript.getId().toString() ),
                JsonEntityValue.create( "transformImpScript", "executableScripts", transformImpScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "applicableImpScript" ) ) );
    }

    @Test
    public void testTransformInScriptEvaluate() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "programRefLookupScript", "executableScripts", programRefLookupScript.getId().toString() ),
                JsonEntityValue.create( "transformImpScript", "executableScripts", programRefLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "transformImpScript" ) ) );
    }

    @Test
    public void testTransformInScriptResourceType() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "programRefLookupScript", "executableScripts", programRefLookupScript.getId().toString() ),
                JsonEntityValue.create( "transformImpScript", "executableScripts", otherTransformInScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "transformImpScript" ) ) );
    }
}