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
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSet;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackedEntity;
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
public class BeforeCreateSaveTrackedEntityRuleValidatorTest extends AbstractJpaRepositoryTest
{
    public static final String RESOURCE_PATH = "/api/trackedEntityRules";

    public static final String AUTHORIZATION_HEADER_VALUE = CODE_MAPPING_AUTHORIZATION_HEADER_VALUE;

    private MappedTrackedEntity mappedTrackedEntity;

    private ExecutableScript teiLookupScript;

    private ExecutableScript locationLookupScript;

    private ExecutableScript orgLookupScript;

    private ExecutableScript applicableInScript;

    private ExecutableScript transformInScript;

    private ExecutableScript otherTransformInScript;

    private CodeSet applicableCodeSet;

    private TrackedEntityRule entity;

    @Before
    public void before()
    {
        mappedTrackedEntity = entityManager.createQuery( "SELECT e FROM MappedTrackedEntity e WHERE e.name=:name", MappedTrackedEntity.class )
            .setParameter( "name", "Person" ).getSingleResult();
        teiLookupScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "PATIENT_TEI_LOOKUP" ).getSingleResult();
        locationLookupScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "EXTRACT_FHIR_PATIENT_GEO_LOCATION" ).getSingleResult();
        orgLookupScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "EXTRACT_FHIR_PATIENT_DHIS_ORG_UNIT_CODE" ).getSingleResult();
        applicableInScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "TRUE" ).getSingleResult();
        transformInScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "TRANSFORM_FHIR_PATIENT_DHIS_PERSON" ).getSingleResult();
        otherTransformInScript = entityManager.createQuery( "SELECT e FROM ExecutableScript e WHERE e.code=:code", ExecutableScript.class )
            .setParameter( "code", "CP_OPV_DOSE" ).getSingleResult();
        applicableCodeSet = entityManager.createQuery( "SELECT e FROM CodeSet e WHERE e.code=:code", CodeSet.class )
            .setParameter( "code", "ALL_DTP_DTAP" ).getSingleResult();

        entity = new TrackedEntityRule();
        entity.setEnabled( false );
        entity.setEvaluationOrder( Integer.MIN_VALUE );
        entity.setName( createUnique() );
        entity.setFhirResourceType( FhirResourceType.PATIENT );
        entity.setDescription( createUnique() );
    }

    @Test
    public void testNameBlank() throws Exception
    {
        entity.setName( "    " );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "teiLookupScript", "executableScripts", teiLookupScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", locationLookupScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", orgLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "name" ) ) );
    }

    @Test
    public void testNameLength() throws Exception
    {
        entity.setName( StringUtils.repeat( 'a', TrackedEntityRule.MAX_NAME_LENGTH + 1 ) );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "teiLookupScript", "executableScripts", teiLookupScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", locationLookupScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", orgLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "name" ) ) );
    }

    @Test
    public void testFhirResourceTypeNull() throws Exception
    {
        entity.setFhirResourceType( null );
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "teiLookupScript", "executableScripts", teiLookupScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", locationLookupScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", orgLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "fhirResourceType" ) ) );
    }

    @Test
    public void testTrackedEntityNull() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "teiLookupScript", "executableScripts", teiLookupScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", locationLookupScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", orgLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "trackedEntity" ) ) );
    }

    @Test
    public void testTransformInScriptNull() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "teiLookupScript", "executableScripts", teiLookupScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", locationLookupScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", orgLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "transformInScript" ) ) );
    }

    @Test
    public void testMinimal() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", transformInScript.getId().toString() ) ) ) )
            .andExpect( status().isCreated() );
    }

    @Test
    public void testTeiLookupTransformation() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "teiLookupScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", locationLookupScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", orgLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "teiLookupScript" ) ) );
    }

    @Test
    public void testTeiLookupBoolean() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "teiLookupScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", locationLookupScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", orgLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "teiLookupScript" ) ) );
    }

    @Test
    public void testLocationLookupTransformation() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "teiLookupScript", "executableScripts", teiLookupScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", orgLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "locationLookupScript" ) ) );
    }

    @Test
    public void testLocationLookupBoolean() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "teiLookupScript", "executableScripts", teiLookupScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", orgLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "locationLookupScript" ) ) );
    }

    @Test
    public void testOrgLookupScriptTransformation() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "teiLookupScript", "executableScripts", teiLookupScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", locationLookupScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "orgUnitLookupScript" ) ) );
    }

    @Test
    public void testOrgLookupScriptBoolean() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "teiLookupScript", "executableScripts", teiLookupScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", locationLookupScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "orgUnitLookupScript" ) ) );
    }

    @Test
    public void testApplicableInScriptTransformation() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "teiLookupScript", "executableScripts", teiLookupScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", locationLookupScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", orgLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "applicableInScript" ) ) );
    }

    @Test
    public void testApplicableInScriptLocation() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "teiLookupScript", "executableScripts", teiLookupScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", locationLookupScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", orgLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", locationLookupScript.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", transformInScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "applicableInScript" ) ) );
    }

    @Test
    public void testTransformInScriptEvaluate() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "teiLookupScript", "executableScripts", teiLookupScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", locationLookupScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", orgLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", teiLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "transformInScript" ) ) );
    }

    @Test
    public void testTransformInScriptResourceType() throws Exception
    {
        mockMvc.perform( post( RESOURCE_PATH ).header( AUTHORIZATION_HEADER_NAME, AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( replaceJsonEntityReferences( entity,
                JsonEntityValue.create( "trackedEntity", "trackedEntities", mappedTrackedEntity.getId().toString() ),
                JsonEntityValue.create( "teiLookupScript", "executableScripts", teiLookupScript.getId().toString() ),
                JsonEntityValue.create( "locationLookupScript", "executableScripts", locationLookupScript.getId().toString() ),
                JsonEntityValue.create( "orgUnitLookupScript", "executableScripts", orgLookupScript.getId().toString() ),
                JsonEntityValue.create( "applicableInScript", "executableScripts", applicableInScript.getId().toString() ),
                JsonEntityValue.create( "transformInScript", "executableScripts", otherTransformInScript.getId().toString() ),
                JsonEntityValue.create( "applicableCodeSet", "codeSets", applicableCodeSet.getId().toString() ) ) ) )
            .andExpect( status().isBadRequest() ).andExpect( jsonPath( "errors[0].property", Matchers.is( "transformInScript" ) ) );
    }
}