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

import org.dhis2.fhir.adapter.fhir.AbstractJpaRepositoryTest;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Validator tests for the corresponding repository.
 *
 * @author volsch
 */
@Rollback
public class BeforeCreateSaveFhirResourceMappingValidatorTest extends AbstractJpaRepositoryTest
{
    public static final String RESOURCE_PATH = "/api/fhirResourceMappings";

    public static final String AUTHORIZATION_HEADER_VALUE = DATA_MAPPING_AUTHORIZATION_HEADER_VALUE;

    private FhirResourceMapping entity;

    private ExecutableScript impTeiLookupScript;

    private ExecutableScript impEnrollmentOrgLookupScript;

    private ExecutableScript impEventOrgLookupScript;

    private ExecutableScript impEnrollmentDateLookupScript;

    private ExecutableScript impEventDateLookupScript;

    private ExecutableScript impEnrollmentGeoLookupScript;

    private ExecutableScript impEventGeoLookupScript;

    private ExecutableScript impEffectiveDateLookupScript;

    private ExecutableScript impTransformationScript;

    private ExecutableScript impProgramStageRefLookupScript;

    private ExecutableScript impBooleanScript;

    private ExecutableScript impOtherDateLookupScript;

    private ExecutableScript impOtherOrgUnitLookupScript;

    private ExecutableScript impOtherGeoLookupScript;

    @Before
    public void before()
    {
        impTeiLookupScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "OBSERVATION_TEI_LOOKUP" ).getSingleResult();
        impEnrollmentOrgLookupScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "EXTRACT_FHIR_PATIENT_DHIS_ORG_UNIT_CODE" ).getSingleResult();
        impEventOrgLookupScript = impEnrollmentOrgLookupScript;
        impEnrollmentGeoLookupScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "EXTRACT_FHIR_OBSERVATION_GEO_LOCATION" ).getSingleResult();
        impEventGeoLookupScript = impEnrollmentGeoLookupScript;
        impEnrollmentDateLookupScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "OBSERVATION_DATE_LOOKUP" ).getSingleResult();
        impEventDateLookupScript = impEnrollmentDateLookupScript;
        impEffectiveDateLookupScript = impEnrollmentDateLookupScript;

        impTransformationScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "CP_OPV_DOSE" ).getSingleResult();
        impBooleanScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "TRUE" ).getSingleResult();
        impOtherDateLookupScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "OTHER_DATE_LOOKUP" ).getSingleResult();
        impOtherOrgUnitLookupScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "OTHER_ORG_UNIT_LOOKUP" ).getSingleResult();
        impOtherGeoLookupScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "OTHER_GEO_LOOKUP" ).getSingleResult();

        impProgramStageRefLookupScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "QR_PROGRAM_STAGE_REF_LOOKUP" ).getSingleResult();

        entity = new FhirResourceMapping();
        entity.setFhirResourceType( FhirResourceType.ENCOUNTER );
        entity.setTrackedEntityFhirResourceType( FhirResourceType.PATIENT );
    }

    @Test
    public void testFhirResourceTypeNull() throws Exception
    {
        entity.setFhirResourceType( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEnrollmentDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "fhirResourceType" ) ) );
    }

    @Test
    public void testTrackedEntityFhirResourceTypeNull() throws Exception
    {
        entity.setTrackedEntityFhirResourceType( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEnrollmentDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "trackedEntityFhirResourceType" ) ) );
    }

    @Test
    public void testImpEffectiveDateLookupScriptNull() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEnrollmentDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testImpEffectiveDateLookupScriptTransform() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impTransformationScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEnrollmentDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEffectiveDateLookupScript" ) ) );
    }

    @Test
    public void testImpEffectiveDateLookupScriptType() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impOtherDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEnrollmentDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEffectiveDateLookupScript" ) ) );
    }

    @Test
    public void testImpEffectiveDateLookupScriptBoolean() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEnrollmentDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEffectiveDateLookupScript" ) ) );
    }

    @Test
    public void testImpEnrollmentDateLookupScriptNull() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testImpEnrollmentDateLookupScriptTransform() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impTransformationScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEnrollmentDateLookupScript" ) ) );
    }

    @Test
    public void testImpEnrollmentDateLookupScriptType() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impOtherDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEnrollmentDateLookupScript" ) ) );
    }

    @Test
    public void testImpEnrollmentDateLookupScriptBoolean() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEnrollmentDateLookupScript" ) ) );
    }

    @Test
    public void testImpEventDateLookupScriptNull() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEnrollmentDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testImpEventDateLookupScriptTransform() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impTransformationScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEventDateLookupScript" ) ) );
    }

    @Test
    public void testImpEventDateLookupScriptType() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impOtherDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEventDateLookupScript" ) ) );
    }

    @Test
    public void testImpEventDateLookupScriptBoolean() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEventDateLookupScript" ) ) );
    }

    @Test
    public void testImpTeiLookupScriptNull() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEnrollmentDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testImpTeiLookupScriptTransform() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTransformationScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impTeiLookupScript" ) ) );
    }

    @Test
    public void testImpEnrollmentOrgLookupScript() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEnrollmentDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testImpEnrollmentOrgLookupScriptTransform() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impTransformationScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEnrollmentOrgLookupScript" ) ) );
    }

    @Test
    public void testImpTeiLookupScriptBoolean() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impTeiLookupScript" ) ) );
    }

    @Test
    public void testImpTeiLookupScriptType() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impOtherOrgUnitLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impTeiLookupScript" ) ) );
    }

    @Test
    public void testImpEventOrgLookupScript() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEnrollmentDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testImpEventOrgLookupScriptTransform() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impTransformationScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEventOrgLookupScript" ) ) );
    }

    @Test
    public void testImpEventOrgLookupScriptBoolean() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEventOrgLookupScript" ) ) );
    }

    @Test
    public void testImpEventOrgLookupScriptType() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impOtherOrgUnitLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEventOrgLookupScript" ) ) );
    }

    @Test
    public void testImpEnrollmentGeoLookupScript() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEnrollmentDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testImpEnrollmentGeoLookupScriptTransform() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impTransformationScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEnrollmentGeoLookupScript" ) ) );
    }

    @Test
    public void testImpEnrollmentGeoLookupScriptBoolean() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impBooleanScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEnrollmentGeoLookupScript" ) ) );
    }

    @Test
    public void testImpEnrollmentGeoLookupScriptType() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impOtherGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impEventGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEnrollmentGeoLookupScript" ) ) );
    }

    @Test
    public void testImpEventGeoLookupScript() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEnrollmentDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testImpProgramStageRefLookupScriptInvalid() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEnrollmentDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEnrollmentOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impProgramStageRefLookupScript", "executableScripts", impProgramStageRefLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impProgramStageRefLookupScript" ) ) );
    }

    @Test
    public void testImpProgramStageRefLookupScriptOk() throws Exception
    {
        entity.setFhirResourceType( FhirResourceType.QUESTIONNAIRE_RESPONSE );
        entity.setTrackedEntityFhirResourceType( FhirResourceType.PRACTITIONER );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impProgramStageRefLookupScript", "executableScripts", impProgramStageRefLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testImpEventGeoLookupScriptTransform() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impTransformationScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEventGeoLookupScript" ) ) );
    }

    @Test
    public void testImpEventGeoLookupScriptBoolean() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impBooleanScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEventGeoLookupScript" ) ) );
    }

    @Test
    public void testImpEventGeoLookupScriptType() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "impEffectiveDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentDateLookupScript", "executableScripts", impEffectiveDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventDateLookupScript", "executableScripts", impEventDateLookupScript.getId().toString() ),
                JsonEntityValue.create( "impTeiLookupScript", "executableScripts", impTeiLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventOrgLookupScript", "executableScripts", impEventOrgLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEnrollmentGeoLookupScript", "executableScripts", impEnrollmentGeoLookupScript.getId().toString() ),
                JsonEntityValue.create( "impEventGeoLookupScript", "executableScripts", impOtherGeoLookupScript.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "impEventGeoLookupScript" ) ) );
    }
}