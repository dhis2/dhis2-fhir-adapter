package org.dhis2.fhir.adapter.metadata.sheet.processor;

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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ApplicableEnrollmentStatus;
import org.dhis2.fhir.adapter.fhir.metadata.model.ApplicableEventStatus;
import org.dhis2.fhir.adapter.fhir.metadata.model.EventStatusUpdate;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackedEntity;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgram;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgramStage;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ExecutableScriptRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackedEntityRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackerProgramRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackerProgramStageRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ProgramStageRuleRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetLocation;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessage;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessageCollector;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessageSeverity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Processes the metadata import of program and its program stages from a sheet.<br>
 *
 * <b>This metadata sheet import tool is just a temporary solution
 * and may be removed in the future completely.</b>
 *
 * @author volsch
 */
@Component
public class MetadataSheetProgramImportProcessor extends AbstractMetadataSheetImportProcessor
{
    public static final String PROGRAM_STAGES_SHEET_NAME = "Program Stages";

    public static final int AUTOMATIC_ENROLLMENT_APPLICABLE_SCRIPT_ROW = 1;

    public static final int ENROLLMENT_DATE_INCIDENT_DATE_ROW = 2;

    public static final int ENROLLMENT_SCRIPT_ROW = 4;

    public static final int VALUE_COL = 1;

    public static final int PROGRAM_STAGE_REF_COL = 0;

    public static final int PROGRAM_STAGE_BEFORE_SCRIPT_CODE_COL = 2;

    public static final int PROGRAM_STAGE_CREATE_SCRIPT_CODE_COL = 3;

    public static final UUID DEFAULT_ENCOUNTER_SEARCH_FILTER_SCRIPT_ID = UUID.fromString( "bae38b4e-5d16-46e9-b579-a9a0900edb72" );

    public static final UUID DEFAULT_ENCOUNTER_TRANSFORM_EXP_SCRIPT_ID = UUID.fromString( "5eab76a9-0ff4-43b0-a7d0-5a6e726ca80e" );

    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final ProgramMetadataService programMetadataService;

    private final MappedTrackedEntityRepository mappedTrackedEntityRepository;

    private final MappedTrackerProgramRepository mappedTrackerProgramRepository;

    private final MappedTrackerProgramStageRepository mappedTrackerProgramStageRepository;

    private final ProgramStageRuleRepository programStageRuleRepository;

    private final TrackedEntityRuleRepository trackedEntityRuleRepository;

    private final ExecutableScriptRepository executableScriptRepository;

    public MetadataSheetProgramImportProcessor( @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull ProgramMetadataService programMetadataService,
        @Nonnull MappedTrackerProgramRepository mappedTrackerProgramRepository, @Nonnull MappedTrackedEntityRepository mappedTrackedEntityRepository, @Nonnull MappedTrackerProgramStageRepository mappedTrackerProgramStageRepository,
        @Nonnull ProgramStageRuleRepository programStageRuleRepository, @Nonnull TrackedEntityRuleRepository trackedEntityRuleRepository, @Nonnull ExecutableScriptRepository executableScriptRepository )
    {
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.programMetadataService = programMetadataService;
        this.mappedTrackedEntityRepository = mappedTrackedEntityRepository;
        this.mappedTrackerProgramRepository = mappedTrackerProgramRepository;
        this.mappedTrackerProgramStageRepository = mappedTrackerProgramStageRepository;
        this.programStageRuleRepository = programStageRuleRepository;
        this.trackedEntityRuleRepository = trackedEntityRuleRepository;
        this.executableScriptRepository = executableScriptRepository;
    }

    @Nonnull
    @Transactional
    public MetadataSheetMessageCollector process( @Nonnull Workbook workbook )
    {
        final MetadataSheetMessageCollector messageCollector = new MetadataSheetMessageCollector();
        final Sheet sheet = workbook.getSheet( PROGRAM_SHEET_NAME );

        if ( sheet == null )
        {
            messageCollector.addMessage( new MetadataSheetMessage(
                MetadataSheetMessageSeverity.ERROR, "Sheet '" + PROGRAM_SHEET_NAME + "' is not included." ) );

            return messageCollector;
        }

        final String programRefValue = getString( sheet, PROGRAM_REF_ROW, PROGRAM_REF_COL );

        if ( programRefValue == null )
        {
            messageCollector.addMessage( new MetadataSheetMessage(
                MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_SHEET_NAME, PROGRAM_REF_ROW, PROGRAM_REF_COL ),
                "Program reference has not been defined." ) );

            return messageCollector;
        }

        final Reference programRef = getReference( programRefValue );

        if ( programRef == null )
        {
            messageCollector.addMessage( new MetadataSheetMessage(
                MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_SHEET_NAME, PROGRAM_REF_ROW, PROGRAM_REF_COL ),
                "Program reference is invalid: " + programRefValue ) );

            return messageCollector;
        }

        final Program program = programMetadataService.findMetadataRefreshedByReference( programRef ).orElse( null );
        TrackedEntityRule trackedEntityRule = null;

        if ( program == null )
        {
            messageCollector.addMessage( new MetadataSheetMessage(
                MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_SHEET_NAME, PROGRAM_REF_ROW, PROGRAM_REF_COL ),
                "Program does not exist in DHIS2: " + programRef ) );
        }
        else
        {
            final TrackedEntityType trackedEntityType = trackedEntityMetadataService.findTypeByReference( Reference.createIdReference( program.getTrackedEntityTypeId() ) ).orElse( null );

            if ( trackedEntityType == null )
            {
                messageCollector.addMessage( new MetadataSheetMessage(
                    MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_SHEET_NAME, PROGRAM_REF_ROW, PROGRAM_REF_COL ),
                    "Tracked entity type of program does not exist in DHIS2: " + program.getTrackedEntityTypeId() ) );
            }
            else
            {
                final MappedTrackedEntity mappedTrackedEntity = mappedTrackedEntityRepository.findOneByTrackedEntityReference( trackedEntityType.getAllReferences() ).orElse( null );

                if ( mappedTrackedEntity == null )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_SHEET_NAME, PROGRAM_REF_ROW, PROGRAM_REF_COL ),
                        "Tracked entity type of program has not been mapped: " + program.getTrackedEntityTypeId() ) );
                }
                else
                {
                    trackedEntityRule = trackedEntityRuleRepository.findFirstByTrackedEntity( FhirResourceType.PATIENT, mappedTrackedEntity ).orElse( null );

                    if ( trackedEntityRule == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_SHEET_NAME, PROGRAM_REF_ROW, PROGRAM_REF_COL ),
                            "Tracked entity type of program has no enabled rule: " + program.getTrackedEntityTypeId() ) );
                    }
                }
            }
        }

        final String automaticEnrollmentApplicableScriptCode = getString( sheet, AUTOMATIC_ENROLLMENT_APPLICABLE_SCRIPT_ROW, VALUE_COL );
        final String enrollmentScriptCode = getString( sheet, ENROLLMENT_SCRIPT_ROW, VALUE_COL );
        final Boolean enrollmentDateIncidentDate = getBoolean( sheet, ENROLLMENT_DATE_INCIDENT_DATE_ROW, VALUE_COL );
        ExecutableScript automaticEnrollmentApplicableScript = null;
        ExecutableScript enrollmentScript = null;

        if ( automaticEnrollmentApplicableScriptCode == null )
        {
            messageCollector.addMessage( new MetadataSheetMessage(
                MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_SHEET_NAME, AUTOMATIC_ENROLLMENT_APPLICABLE_SCRIPT_ROW, VALUE_COL ),
                "Automatic enrollment applicable script code must be specified." ) );
        }
        else
        {
            automaticEnrollmentApplicableScript = executableScriptRepository.findOneByCode( automaticEnrollmentApplicableScriptCode ).orElse( null );

            if ( automaticEnrollmentApplicableScript == null )
            {
                messageCollector.addMessage( new MetadataSheetMessage(
                    MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_SHEET_NAME, AUTOMATIC_ENROLLMENT_APPLICABLE_SCRIPT_ROW, VALUE_COL ),
                    "Automatic enrollment applicable script does not exist: " + automaticEnrollmentApplicableScriptCode ) );
            }
        }

        if ( enrollmentScriptCode != null )
        {
            enrollmentScript = executableScriptRepository.findOneByCode( enrollmentScriptCode ).orElse( null );

            if ( enrollmentScript == null )
            {
                messageCollector.addMessage( new MetadataSheetMessage(
                    MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_SHEET_NAME, ENROLLMENT_SCRIPT_ROW, VALUE_COL ),
                    "Enrollment script does not exist: " + enrollmentScriptCode ) );
            }
        }

        if ( enrollmentDateIncidentDate == null )
        {
            messageCollector.addMessage( new MetadataSheetMessage(
                MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_SHEET_NAME, ENROLLMENT_DATE_INCIDENT_DATE_ROW, VALUE_COL ),
                "Yes or no must be specified." ) );
        }

        if ( messageCollector.isOk() )
        {
            final MappedTrackerProgram mappedTrackerProgram = mappedTrackerProgramRepository.findOneByProgramReference(
                Objects.requireNonNull( program ).getAllReferences() ).orElseGet( MappedTrackerProgram::new );

            mappedTrackerProgram.setName( StringUtils.left( Objects.requireNonNull( program ).getName(), MappedTrackerProgram.MAX_NAME_LENGTH ) );
            mappedTrackerProgram.setDescription( Objects.requireNonNull( program ).getName() );
            mappedTrackerProgram.setEnabled( true );
            mappedTrackerProgram.setCreationEnabled( true );
            mappedTrackerProgram.setCreationApplicableScript( automaticEnrollmentApplicableScript );
            mappedTrackerProgram.setCreationScript( enrollmentScript );
            mappedTrackerProgram.setEnrollmentDateIsIncident( Objects.requireNonNull( enrollmentDateIncidentDate ) );
            mappedTrackerProgram.setProgramReference( getReference( Objects.requireNonNull( program ).getAllReferences() ) );
            mappedTrackerProgram.setTrackedEntityFhirResourceType( FhirResourceType.PATIENT );
            mappedTrackerProgram.setTrackedEntityRule( trackedEntityRule );
            mappedTrackerProgram.setExpEnabled( true );
            mappedTrackerProgram.setFhirCreateEnabled( true );
            mappedTrackerProgram.setFhirUpdateEnabled( true );
            mappedTrackerProgram.setFhirDeleteEnabled( true );

            mappedTrackerProgramRepository.save( mappedTrackerProgram );

            messageCollector.add( processStages( workbook, program, mappedTrackerProgram ) );
        }

        return messageCollector;
    }

    @Nonnull
    protected MetadataSheetMessageCollector processStages( @Nonnull Workbook workbook, @Nonnull Program program, @Nonnull MappedTrackerProgram mappedTrackerProgram )
    {
        final MetadataSheetMessageCollector messageCollector = new MetadataSheetMessageCollector();
        final Sheet sheet = workbook.getSheet( PROGRAM_STAGES_SHEET_NAME );

        if ( sheet == null )
        {
            messageCollector.addMessage( new MetadataSheetMessage(
                MetadataSheetMessageSeverity.ERROR, "Sheet '" + PROGRAM_STAGES_SHEET_NAME + "' is not included." ) );

            return messageCollector;
        }

        final Set<Reference> processedReferences = new HashSet<>();
        final int lastRowNum = sheet.getLastRowNum();

        // skip first header column
        for ( int rowNum = 1; rowNum <= lastRowNum; rowNum++ )
        {
            final Row row = sheet.getRow( rowNum );

            if ( row != null && notEmpty( row, PROGRAM_STAGE_REF_COL ) )
            {
                final String programStageRefValue = getString( row, PROGRAM_STAGE_REF_COL );
                final String beforeScriptCode = getString( row, PROGRAM_STAGE_BEFORE_SCRIPT_CODE_COL );
                final String createScriptCode = getString( row, PROGRAM_STAGE_CREATE_SCRIPT_CODE_COL );
                final Reference programStageRef = getReference( programStageRefValue );
                ExecutableScript beforeScript = null;
                ExecutableScript createScript = null;

                if ( programStageRef == null )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_STAGES_SHEET_NAME, rowNum, PROGRAM_STAGE_REF_COL ),
                        "Program stage reference is invalid: " + programStageRef ) );

                    return messageCollector;
                }

                final ProgramStage programStage = program.getOptionalStage( programStageRef ).orElse( null );

                if ( programStage == null )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_STAGES_SHEET_NAME, rowNum, PROGRAM_STAGE_REF_COL ),
                        "Program stage does not exist in DHIS2: " + programStageRef ) );
                }
                else if ( !processedReferences.addAll( programStage.getAllReferences() ) )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_STAGES_SHEET_NAME, rowNum, PROGRAM_STAGE_REF_COL ),
                        "Program stage is not unique in sheet: " + programStageRef ) );
                }

                if ( beforeScriptCode != null )
                {
                    beforeScript = executableScriptRepository.findOneByCode( beforeScriptCode ).orElse( null );

                    if ( beforeScript == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_STAGES_SHEET_NAME, rowNum, PROGRAM_STAGE_BEFORE_SCRIPT_CODE_COL ),
                            "Before script does not exist: " + beforeScriptCode ) );
                    }
                }

                if ( createScriptCode != null )
                {
                    createScript = executableScriptRepository.findOneByCode( createScriptCode ).orElse( null );

                    if ( createScript == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( PROGRAM_STAGES_SHEET_NAME, rowNum, PROGRAM_STAGE_CREATE_SCRIPT_CODE_COL ),
                            "Create script does not exist: " + createScriptCode ) );
                    }
                }

                if ( messageCollector.isOk() )
                {
                    final MappedTrackerProgramStage mappedTrackerProgramStage = mappedTrackerProgramStageRepository.findOneByProgramAndProgramStageReference(
                        mappedTrackerProgram, Objects.requireNonNull( programStage ).getAllReferences() ).orElseGet( MappedTrackerProgramStage::new );

                    mappedTrackerProgramStage.setProgram( mappedTrackerProgram );
                    mappedTrackerProgramStage.setName( StringUtils.left( Objects.requireNonNull( programStage ).getName(), MappedTrackerProgramStage.MAX_NAME_LENGTH ) );
                    mappedTrackerProgramStage.setDescription( Objects.requireNonNull( programStage ).getName() );
                    mappedTrackerProgramStage.setProgramStageReference( getReference( programStage.getAllReferences() ) );
                    mappedTrackerProgramStage.setBeforeScript( beforeScript );
                    mappedTrackerProgramStage.setCreationScript( createScript );
                    mappedTrackerProgramStage.setEnabled( true );
                    mappedTrackerProgramStage.setEventDateIsIncident( false );
                    mappedTrackerProgramStage.setCreationEnabled( true );
                    mappedTrackerProgramStage.setExpEnabled( true );
                    mappedTrackerProgramStage.setFhirCreateEnabled( true );
                    mappedTrackerProgramStage.setFhirUpdateEnabled( true );
                    mappedTrackerProgramStage.setFhirDeleteEnabled( true );

                    mappedTrackerProgramStageRepository.save( mappedTrackerProgramStage );
                    createEncounterRule( mappedTrackerProgramStage );
                }
            }
        }

        return messageCollector;
    }

    protected void createEncounterRule( @Nonnull MappedTrackerProgramStage mappedTrackerProgramStage )
    {
        final ExecutableScript expTransformExecutableScript = executableScriptRepository.findById( DEFAULT_ENCOUNTER_TRANSFORM_EXP_SCRIPT_ID )
            .orElseThrow( () -> new MetadataSheetImportException( "Could not find default encounter export transformation script." ) );
        final ExecutableScript filterExecutableScript = executableScriptRepository.findById( DEFAULT_ENCOUNTER_SEARCH_FILTER_SCRIPT_ID )
            .orElseThrow( () -> new MetadataSheetImportException( "Could not find default encounter filter script." ) );

        final ProgramStageRule rule = programStageRuleRepository.findFirstByProgramStageFhirResource( mappedTrackerProgramStage, FhirResourceType.ENCOUNTER )
            .orElseGet( ProgramStageRule::new );

        rule.setProgramStage( mappedTrackerProgramStage );
        rule.setFhirResourceType( FhirResourceType.ENCOUNTER );
        rule.setName( StringUtils.left( mappedTrackerProgramStage.getName() + ": Encounter", ProgramStageRule.MAX_NAME_LENGTH ) );
        rule.setDescription( mappedTrackerProgramStage.getName() + ": Encounter" );
        rule.setEvaluationOrder( 1_000_000 );
        rule.setGrouping( true );
        rule.setEnabled( true );
        rule.setImpEnabled( false );
        rule.setExpEnabled( true );
        rule.setTransformExpScript( expTransformExecutableScript );
        rule.setFilterScript( filterExecutableScript );
        rule.setApplicableEnrollmentStatus( new ApplicableEnrollmentStatus() );
        rule.setApplicableEventStatus( new ApplicableEventStatus() );
        rule.setEventStatusUpdate( new EventStatusUpdate() );

        if ( rule.getDhisDataReferences() == null )
        {
            rule.setDhisDataReferences( new ArrayList<>() );
        }

        rule.getDhisDataReferences().clear();

        programStageRuleRepository.save( rule );
    }
}
