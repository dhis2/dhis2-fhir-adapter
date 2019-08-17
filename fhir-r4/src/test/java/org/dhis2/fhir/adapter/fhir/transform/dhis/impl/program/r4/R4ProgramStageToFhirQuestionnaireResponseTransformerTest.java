package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.program.r4;

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

import ca.uhn.fhir.model.primitive.IdDt;
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.WritableDataElement;
import org.dhis2.fhir.adapter.dhis.model.WritableDataValue;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgram;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgramStageDataElement;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.extension.DueDateExtensionUtils;
import org.dhis2.fhir.adapter.fhir.extension.EventStatusExtensionUtils;
import org.dhis2.fhir.adapter.fhir.extension.LocationExtensionUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractAssignmentDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.r4.R4ValueTypeDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ImmutableScriptedEvent;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEvent;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.dhis2.fhir.adapter.model.ValueType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Unit tests for {@link R4ProgramStageToFhirQuestionnaireResponseTransformer}.
 *
 * @author volsch
 */
public class R4ProgramStageToFhirQuestionnaireResponseTransformerTest
{
    @Mock
    private ScriptExecutor scriptExecutor;

    @Mock
    private LockManager lockManager;

    @Mock
    private SystemRepository systemRepository;

    @Mock
    private FhirResourceRepository fhirResourceRepository;

    @Mock
    private FhirResourceMappingRepository resourceMappingRepository;

    @Mock
    private FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    @Mock
    private TrackedEntityMetadataService trackedEntityMetadataService;

    @Mock
    private TrackedEntityRuleRepository trackedEntityRuleRepository;

    @Mock
    private ProgramMetadataService programMetadataService;

    @Mock
    private ProgramStageMetadataService programStageMetadataService;

    @Mock
    private FhirClient fhirClient;

    @Mock
    private DhisToFhirTransformerContext context;

    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private TrackedEntityService trackedEntityService;

    @Mock
    private TrackedEntityAttributes trackedEntityAttributes;

    @Mock
    private TrackedEntityType trackedEntityType;

    @Mock
    private ValueConverter valueConverter;

    @Mock
    private AbstractAssignmentDhisToFhirTransformerUtils assignmentUtils;

    @InjectMocks
    private R4ProgramStageToFhirQuestionnaireResponseTransformer transformer;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void getFhirVersions()
    {
        Assert.assertEquals( FhirVersion.R4_ONLY, transformer.getFhirVersions() );
    }

    @Test
    public void transformInternal()
    {
        final WritableProgram program = new WritableProgram();
        program.setName( "Test Program" );
        program.setDescription( "Test Description" );
        program.setWithoutRegistration( false );
        program.setTrackedEntityTypeId( "a1234567890" );
        program.setStages( new ArrayList<>() );

        final WritableProgramStage programStage = new WritableProgramStage();
        programStage.setId( "b1234567890" );
        programStage.setName( "Test Stage 1" );
        programStage.setDescription( "Test Description 1" );
        programStage.setRepeatable( false );
        programStage.setDataElements( new ArrayList<>() );

        WritableProgramStageDataElement programStageDataElement = new WritableProgramStageDataElement();
        WritableDataElement dataElement = new WritableDataElement();
        dataElement.setId( "d0123456789" );
        dataElement.setName( "Value 1" );
        dataElement.setValueType( ValueType.TEXT );
        programStageDataElement.setElement( dataElement );
        programStage.getDataElements().add( programStageDataElement );

        programStageDataElement = new WritableProgramStageDataElement();
        dataElement = new WritableDataElement();
        dataElement.setId( "d7123456789" );
        dataElement.setName( "Value 2" );
        dataElement.setValueType( ValueType.TEXT );
        programStageDataElement.setElement( dataElement );
        programStage.getDataElements().add( programStageDataElement );

        final Patient patient = new Patient();
        patient.setId( new IdDt( "Patient/lyau3syaixys" ) );

        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = new WritableScriptedTrackedEntityInstance( trackedEntityMetadataService, trackedEntityService, trackedEntityAttributes, trackedEntityType, "lyau3syaixys",
            scriptExecutionContext, valueConverter );

        final ArrayList<WritableDataValue> dataValues = new ArrayList<>();
        dataValues.add( new WritableDataValue( "d0123456789", "Test Value 1x" ) );
        dataValues.add( new WritableDataValue( "d7123456789", "Test Value 2x" ) );

        final Event event = new Event();
        event.setProgramId( "x72638dsydu" );
        event.setProgramStageId( "x12638bsyda" );
        event.setOrgUnitId( "j82jdy28dusx" );
        event.setEnrollmentId( "a73dhsjxhs8" );
        event.setTrackedEntityInstanceId( "lyau3syaixys" );
        event.setStatus( EventStatus.OVERDUE );
        event.setEventDate( ZonedDateTime.parse( "2011-12-03T10:15:30+01:00" ) );
        event.setDueDate( ZonedDateTime.parse( "2011-12-08T10:11:30+01:00" ) );
        event.setDataValues( dataValues );

        final QuestionnaireResponse fhirQuestionnaireResponse = new QuestionnaireResponse();

        final Map<String, Object> scriptVariables = new HashMap<>();
        scriptVariables.put( "assignmentUtils", assignmentUtils );
        scriptVariables.put( "valueTypeUtils", new R4ValueTypeDhisToFhirTransformerUtils( scriptExecutionContext, valueConverter ) );

        Mockito.doAnswer( invocation -> invocation.getArgument( 0 ) ).when( valueConverter )
            .convert( Mockito.any(), Mockito.any(), Mockito.eq( String.class ) );

        Mockito.doReturn( new Reference( "Location/j82jdy28dusx" ) ).when( assignmentUtils ).getMappedFhirId( Mockito.same( context ), Mockito.any(),
            Mockito.eq( DhisResourceType.ORGANIZATION_UNIT ), Mockito.eq( "j82jdy28dusx" ), Mockito.eq( FhirResourceType.LOCATION ) );
        Mockito.doReturn( new Reference( "Questionnaire/x12638bsyda" ) ).when( assignmentUtils ).getMappedFhirId( Mockito.same( context ), Mockito.any(),
            Mockito.eq( DhisResourceType.PROGRAM_STAGE_METADATA ), Mockito.eq( "x12638bsyda" ), Mockito.eq( FhirResourceType.QUESTIONNAIRE ) );
        Mockito.doReturn( new Reference( "CarePlan/a73dhsjxhs8" ) ).when( assignmentUtils ).getMappedFhirId( Mockito.same( context ), Mockito.any(),
            Mockito.eq( DhisResourceType.ENROLLMENT ), Mockito.eq( "a73dhsjxhs8" ), Mockito.eq( FhirResourceType.CARE_PLAN ) );

        transformer.transformInternal( fhirClient, context, new RuleInfo<>( new ProgramStageRule(), Collections.emptyList() ), scriptVariables,
            new ImmutableScriptedEvent( new WritableScriptedEvent( program, programStage, event, scriptedTrackedEntityInstance, scriptExecutionContext, valueConverter ) ),
            fhirQuestionnaireResponse, patient );

        Assert.assertEquals( "CarePlan/a73dhsjxhs8", fhirQuestionnaireResponse.getBasedOnFirstRep().getReference() );
        Assert.assertEquals( "Questionnaire/x12638bsyda", fhirQuestionnaireResponse.getQuestionnaire() );
        Assert.assertEquals( QuestionnaireResponse.QuestionnaireResponseStatus.INPROGRESS, fhirQuestionnaireResponse.getStatus() );
        Assert.assertEquals( EventStatus.OVERDUE, EventStatusExtensionUtils.getValue( fhirQuestionnaireResponse ) );

        Assert.assertEquals( "Patient/lyau3syaixys", fhirQuestionnaireResponse.getSubject().getReference() );
        Assert.assertEquals( "Location/j82jdy28dusx",
            Objects.requireNonNull( LocationExtensionUtils.getValue( fhirQuestionnaireResponse ) ).getReferenceElement().getValue() );

        Assert.assertEquals( Date.from( ZonedDateTime.parse( "2011-12-03T10:15:30+01:00" ).toInstant() ), fhirQuestionnaireResponse.getAuthored() );
        Assert.assertEquals( Date.from( ZonedDateTime.parse( "2011-12-08T10:11:30+01:00" ).toInstant() ), DueDateExtensionUtils.getValue( fhirQuestionnaireResponse ) );

        Assert.assertEquals( 2, fhirQuestionnaireResponse.getItem().size() );
        Assert.assertEquals( "d0123456789", fhirQuestionnaireResponse.getItem().get( 0 ).getLinkId() );
        Assert.assertEquals( "Test Value 1x", fhirQuestionnaireResponse.getItem().get( 0 ).getAnswerFirstRep().getValue().primitiveValue() );
        Assert.assertEquals( "d7123456789", fhirQuestionnaireResponse.getItem().get( 1 ).getLinkId() );
        Assert.assertEquals( "Test Value 2x", fhirQuestionnaireResponse.getItem().get( 1 ).getAnswerFirstRep().getValue().primitiveValue() );
    }
}