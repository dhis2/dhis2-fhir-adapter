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
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.fhir.AbstractJpaRepositoryTest;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgram;
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
public class BeforeCreateSaveTrackerProgramValidatorTest extends AbstractJpaRepositoryTest
{
    public static final String RESOURCE_PATH = "/api/trackerPrograms";

    public static final String AUTHORIZATION_HEADER_VALUE = DATA_MAPPING_AUTHORIZATION_HEADER_VALUE;

    private MappedTrackerProgram entity;

    private ExecutableScript impBooleanScript;

    private ExecutableScript impOtherDateLookupScript;

    private TrackedEntityRule trackedEntityRule;

    @Before
    public void before()
    {
        impBooleanScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "TRUE" ).getSingleResult();
        impOtherDateLookupScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "OTHER_DATE_LOOKUP" ).getSingleResult();
        trackedEntityRule = entityManager.createQuery( "SELECT e FROM TrackedEntityRule e WHERE e.name=:name", TrackedEntityRule.class )
            .setParameter( "name", "FHIR Patient to Person" ).getSingleResult();

        entity = new MappedTrackerProgram();
        entity.setName( createUnique() );
        entity.setProgramReference( Reference.createIdReference( "abc" ) );
        entity.setTrackedEntityFhirResourceType( FhirResourceType.PATIENT );
    }

    @Test
    public void testNameBlank() throws Exception
    {
        entity.setName( "   " );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntityRule", "rules", trackedEntityRule.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "name" ) ) );
    }

    @Test
    public void testNameLength() throws Exception
    {
        entity.setName( StringUtils.repeat( 'a', MappedTrackerProgram.MAX_NAME_LENGTH + 1 ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntityRule", "rules", trackedEntityRule.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "name" ) ) );
    }

    @Test
    public void testTrackedEntityFhirResourceType() throws Exception
    {
        entity.setTrackedEntityFhirResourceType( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntityRule", "rules", trackedEntityRule.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "trackedEntityFhirResourceType" ) ) );
    }

    @Test
    public void testTrackedEntityRuleNull() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "trackedEntityRule" ) ) );
    }

    @Test
    public void testProgramStageReferenceNull() throws Exception
    {
        entity.setProgramReference( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntityRule", "rules", trackedEntityRule.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "programReference" ) ) );
    }

    @Test
    public void testProgramStageReferenceInvalid() throws Exception
    {
        entity.setProgramReference( new Reference( StringUtils.repeat( 'a', 250 ), ReferenceType.ID ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntityRule", "rules", trackedEntityRule.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "programReference" ) ) );
    }

    @Test
    public void testOk() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntityRule", "rules", trackedEntityRule.getId().toString() ),
                JsonEntityValue.create( "creationApplicableScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "creationScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "beforeScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "afterScript", "executableScripts", impBooleanScript.getId().toString() ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testCreationApplicableScriptInvalid() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntityRule", "rules", trackedEntityRule.getId().toString() ),
                JsonEntityValue.create( "creationApplicableScript", "executableScripts", impOtherDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "creationScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "beforeScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "afterScript", "executableScripts", impBooleanScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "creationApplicableScript" ) ) );
    }

    @Test
    public void testCreationScriptInvalid() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntityRule", "rules", trackedEntityRule.getId().toString() ),
                JsonEntityValue.create( "creationApplicableScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "creationScript", "executableScripts", impOtherDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "beforeScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "afterScript", "executableScripts", impBooleanScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "creationScript" ) ) );
    }

    @Test
    public void testBeforeScriptInvalid() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntityRule", "rules", trackedEntityRule.getId().toString() ),
                JsonEntityValue.create( "creationApplicableScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "creationScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "beforeScript", "executableScripts", impOtherDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "afterScript", "executableScripts", impBooleanScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "beforeScript" ) ) );
    }

    @Test
    public void testAfterScriptInvalid() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntityRule", "rules", trackedEntityRule.getId().toString() ),
                JsonEntityValue.create( "creationApplicableScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "creationScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "beforeScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "afterScript", "executableScripts", impOtherDateLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "afterScript" ) ) );
    }
}