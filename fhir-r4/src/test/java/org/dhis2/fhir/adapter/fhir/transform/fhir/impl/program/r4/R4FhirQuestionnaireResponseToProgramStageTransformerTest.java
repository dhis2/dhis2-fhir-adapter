package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program.r4;

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
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.model.WritableDataElement;
import org.dhis2.fhir.adapter.dhis.model.WritableDataValue;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
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
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.r4.R4ValueTypeFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AssignmentFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEvent;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.model.ValueType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceFactory;
import org.hl7.fhir.r4.model.StringType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for {@link R4FhirQuestionnaireResponseToProgramStageTransformer}.
 *
 * @author volsch
 */
public class R4FhirQuestionnaireResponseToProgramStageTransformerTest
{
    @Mock
    private ScriptExecutor scriptExecutor;

    @Mock
    private TrackedEntityMetadataService trackedEntityMetadataService;

    @Mock
    private TrackedEntityService trackedEntityService;

    @Mock
    private TrackedEntityRuleRepository trackedEntityRuleRepository;

    @Mock
    private OrganizationUnitService organizationUnitService;

    @Mock
    private ProgramMetadataService programMetadataService;

    @Mock
    private ProgramStageMetadataService programStageMetadataService;

    @Mock
    private EnrollmentService enrollmentService;

    @Mock
    private FhirResourceMappingRepository resourceMappingRepository;

    @Mock
    private FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private ValueConverter valueConverter;

    @Mock
    private FhirToDhisTransformerContext transformerContext;

    @Mock
    private FhirResourceMapping fhirResourceMapping;

    @Mock
    private TrackedEntityType trackedEntityType;

    @Mock
    private TrackedEntityAttributes trackedEntityAttributes;

    @Mock
    private ExecutableScript orgUnitExecutableScript;

    @Mock
    private AssignmentFhirToDhisTransformerUtils assignmentUtils;

    @InjectMocks
    private R4FhirQuestionnaireResponseToProgramStageTransformer transformer;

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
        program.setId( "x72638dsydu" );
        program.setName( "Test Program" );
        program.setDescription( "Test Description" );
        program.setWithoutRegistration( false );
        program.setTrackedEntityTypeId( "a1234567890" );
        program.setStages( new ArrayList<>() );

        final WritableProgramStage programStage = new WritableProgramStage();
        programStage.setId( "x12638bsyda" );
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

        programStageDataElement = new WritableProgramStageDataElement();
        dataElement = new WritableDataElement();
        dataElement.setId( "f7123456789" );
        dataElement.setName( "Value 3" );
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
        event.setDataValues( dataValues );

        final QuestionnaireResponse fhirQuestionnaireResponse = new QuestionnaireResponse();
        fhirQuestionnaireResponse.setAuthored( Date.from( ZonedDateTime.parse( "2011-12-03T10:15:30+01:00" ).toInstant() ) );
        fhirQuestionnaireResponse.setQuestionnaire( "Questionnaire/x72638dsydu" );
        fhirQuestionnaireResponse.addBasedOn().setReference( "CarePlan/a73dhsjxhs8" );
        fhirQuestionnaireResponse.setStatus( QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED );
        EventStatusExtensionUtils.setValue( fhirQuestionnaireResponse, EventStatus.OVERDUE, ResourceFactory::createType );
        LocationExtensionUtils.setValue( fhirQuestionnaireResponse, new Reference( "Location/j82jdy28dusx" ) );
        DueDateExtensionUtils.setValue( fhirQuestionnaireResponse, Date.from( ZonedDateTime.parse( "2011-12-08T10:11:30+01:00" ).toInstant() ), ResourceFactory::createType );

        fhirQuestionnaireResponse.addItem().setLinkId( "d0123456789" ).addAnswer().setValue( new StringType( "New Value 1x" ) );
        fhirQuestionnaireResponse.addItem().setLinkId( "f7123456789" ).addAnswer().setValue( new StringType( "Additional Value 3x" ) );

        final Map<String, Object> scriptVariables = new HashMap<>();
        scriptVariables.put( "assignmentUtils", assignmentUtils );
        scriptVariables.put( "valueTypeUtils", new R4ValueTypeFhirToDhisTransformerUtils( scriptExecutionContext ) );

        Mockito.doReturn( "a73dhsjxhs8" ).when( assignmentUtils ).getMappedDhisId( Mockito.same( transformerContext ), Mockito.any(), Mockito.any() );
        Mockito.doReturn( orgUnitExecutableScript ).when( fhirResourceMapping )
            .getImpEnrollmentOrgLookupScript();
        Mockito.doReturn( org.dhis2.fhir.adapter.dhis.model.Reference.createIdReference( "j82jdy28dusx" ) )
            .when( scriptExecutor ).execute( Mockito.same( orgUnitExecutableScript ), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any() );

        transformer.transformInternal( transformerContext, new RuleInfo<>( new ProgramStageRule(), Collections.emptyList() ), scriptVariables, fhirResourceMapping,
            program, programStage, fhirQuestionnaireResponse, scriptedTrackedEntityInstance, new WritableScriptedEvent( program, programStage, event, scriptedTrackedEntityInstance, scriptExecutionContext, valueConverter ) );

        Assert.assertEquals( "x72638dsydu", event.getProgramId() );
        Assert.assertEquals( "j82jdy28dusx", event.getOrgUnitId() );
        Assert.assertEquals( "lyau3syaixys", event.getTrackedEntityInstanceId() );
        Assert.assertEquals( "a73dhsjxhs8", event.getEnrollmentId() );
        Assert.assertEquals( EventStatus.OVERDUE, event.getStatus() );
        Assert.assertEquals( ZonedDateTime.parse( "2011-12-03T10:15:30+01:00" ).toLocalDateTime(), event.getEventDate().toLocalDateTime() );
        Assert.assertEquals( ZonedDateTime.parse( "2011-12-08T10:11:30+01:00" ).toLocalDateTime(), event.getDueDate().toLocalDateTime() );

        Assert.assertEquals( 2, event.getDataValues().size() );
        Assert.assertEquals( "d0123456789", event.getDataValues().get( 0 ).getDataElementId() );
        Assert.assertEquals( "New Value 1x", event.getDataValues().get( 0 ).getValue() );
        Assert.assertEquals( "f7123456789", event.getDataValues().get( 1 ).getDataElementId() );
        Assert.assertEquals( "Additional Value 3x", event.getDataValues().get( 1 ).getValue() );
    }

    @Test
    public void getProgramStageRef()
    {
        final QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
        questionnaireResponse.setQuestionnaire( "Questionnaire/sj87dhyui82" );

        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "sj87dhyui82", ReferenceType.ID ),
            transformer.getProgramStageRef( transformerContext, new RuleInfo<>( new ProgramStageRule(), Collections.emptyList() ), new HashMap<>(), questionnaireResponse ) );
    }

    @Test
    public void getProgramRefWithoutType()
    {
        final QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
        questionnaireResponse.setQuestionnaire( "sj87dhyui82" );

        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "sj87dhyui82", ReferenceType.ID ),
            transformer.getProgramStageRef( transformerContext, new RuleInfo<>( new ProgramStageRule(), Collections.emptyList() ), new HashMap<>(), questionnaireResponse ) );
    }

    @Test( expected = TransformerDataException.class )
    public void getProgramRefNull()
    {
        final QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
        transformer.getProgramStageRef( transformerContext, new RuleInfo<>( new ProgramStageRule(), Collections.emptyList() ), new HashMap<>(), questionnaireResponse );
    }

    @Test
    public void convertStatusCompleted()
    {
        final QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
        questionnaireResponse.setStatus( QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED );

        Assert.assertEquals( EventStatus.COMPLETED, transformer.convertStatus( questionnaireResponse ) );
    }

    @Test
    public void convertStatusInProgress()
    {
        final QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
        questionnaireResponse.setStatus( QuestionnaireResponse.QuestionnaireResponseStatus.INPROGRESS );

        Assert.assertEquals( EventStatus.ACTIVE, transformer.convertStatus( questionnaireResponse ) );
    }

    @Test
    public void convertStatusEventOverdue()
    {
        final QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
        questionnaireResponse.setStatus( QuestionnaireResponse.QuestionnaireResponseStatus.COMPLETED );
        EventStatusExtensionUtils.setValue( questionnaireResponse, EventStatus.OVERDUE, ResourceFactory::createType );

        Assert.assertEquals( EventStatus.OVERDUE, transformer.convertStatus( questionnaireResponse ) );
    }

    @Test( expected = TransformerDataException.class )
    public void convertStatusNull()
    {
        final QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
        transformer.convertStatus( questionnaireResponse );
    }
}