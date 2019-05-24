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
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageDataElement;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ApplicableEnrollmentStatus;
import org.dhis2.fhir.adapter.fhir.metadata.model.ApplicableEventStatus;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeCategory;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSet;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSetValue;
import org.dhis2.fhir.adapter.fhir.metadata.model.EventStatusUpdate;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgram;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgramStage;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleDhisDataReference;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeCategoryRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeSetRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ExecutableScriptRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackerProgramRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackerProgramStageRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ProgramStageRuleRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetLocation;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessage;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessageCollector;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessageSeverity;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Processes the metadata import of rules from a sheet.<br>
 *
 * <b>This metadata sheet import tool is just a temporary solution
 * and may be removed in the future completely.</b>
 *
 * @author volsch
 */
@Component
public class MetadataSheetRuleImportProcessor extends AbstractMetadataSheetImportProcessor
{
    public static final String RULES_SHEET_NAME = "Rules";

    public static final int PROGRAM_STAGE_REF_COL = 0;

    public static final int DHIS_RESOURCE_TYPE_COL = 1;

    public static final int FHIR_RESOURCE_TYPE_COL = 2;

    public static final int DATA_ELEMENT_REFS_COL = 3;

    public static final int CODE_SET_CODE_COL = 4;

    public static final int CODE_SET_DISPLAY_NAME_COL = 5;

    public static final int CODE_SET_PREFERRED_COL = 6;

    public static final int CODE_SET_CODES_COL = 7;

    public static final int F2D_APPLICABLE_SCRIPT_CODE_COL = 12;

    public static final int F2D_TRANSFORM_SCRIPT_CODE_COL = 13;

    public static final int D2F_APPLICABLE_SCRIPT_CODE_COL = 14;

    public static final int D2F_TRANSFORM_SCRIPT_CODE_COL = 15;

    public static final UUID DEFAULT_OBSERVATION_SEARCH_FILTER_SCRIPT_ID = UUID.fromString( "a97e64a7-81d1-4f62-84f2-da9a003f9d0b" );

    public static final UUID DEFAULT_IMMUNIZATION_SEARCH_FILTER_SCRIPT_ID = UUID.fromString( "2e3c191f-8cf1-4a90-9876-4e9b0b6e4e8c" );

    public static final UUID DEFAULT_CONDITION_SEARCH_FILTER_SCRIPT_ID = UUID.fromString( "14bacf9c-2427-4d06-8d1b-7cea735f0089" );

    public static final UUID DEFAULT_MEDICATION_REQUEST_SEARCH_FILTER_SCRIPT_ID = UUID.fromString( "339bdfa6-0e73-4f26-a7f5-b53c7aa580e2" );

    private final ProgramMetadataService programMetadataService;

    private final CodeCategoryRepository codeCategoryRepository;

    private final CodeSetRepository codeSetRepository;

    private final SystemCodeRepository systemCodeRepository;

    private final MappedTrackerProgramRepository mappedTrackerProgramRepository;

    private final MappedTrackerProgramStageRepository mappedTrackerProgramStageRepository;

    private final ProgramStageRuleRepository programStageRuleRepository;

    private final ExecutableScriptRepository executableScriptRepository;

    public MetadataSheetRuleImportProcessor( @Nonnull ProgramMetadataService programMetadataService, @Nonnull CodeCategoryRepository codeCategoryRepository,
        @Nonnull CodeSetRepository codeSetRepository, @Nonnull SystemCodeRepository systemCodeRepository,
        @Nonnull MappedTrackerProgramRepository mappedTrackerProgramRepository, @Nonnull MappedTrackerProgramStageRepository mappedTrackerProgramStageRepository,
        @Nonnull ProgramStageRuleRepository programStageRuleRepository, @Nonnull ExecutableScriptRepository executableScriptRepository )
    {
        this.programMetadataService = programMetadataService;
        this.codeCategoryRepository = codeCategoryRepository;
        this.codeSetRepository = codeSetRepository;
        this.systemCodeRepository = systemCodeRepository;
        this.mappedTrackerProgramRepository = mappedTrackerProgramRepository;
        this.mappedTrackerProgramStageRepository = mappedTrackerProgramStageRepository;
        this.programStageRuleRepository = programStageRuleRepository;
        this.executableScriptRepository = executableScriptRepository;
    }

    @Nonnull
    @Transactional
    public MetadataSheetMessageCollector process( @Nonnull Workbook workbook )
    {
        final MetadataSheetMessageCollector messageCollector = new MetadataSheetMessageCollector();
        final Reference programRef = Objects.requireNonNull( getProgramRef( workbook ) );
        final Sheet sheet = workbook.getSheet( RULES_SHEET_NAME );

        final ExecutableScript observationFilterExecutableScript = executableScriptRepository.findById( DEFAULT_OBSERVATION_SEARCH_FILTER_SCRIPT_ID )
            .orElseThrow( () -> new MetadataSheetImportException( "Could not find default observation filter script." ) );
        final ExecutableScript immunizationFilterExecutableScript = executableScriptRepository.findById( DEFAULT_IMMUNIZATION_SEARCH_FILTER_SCRIPT_ID )
            .orElseThrow( () -> new MetadataSheetImportException( "Could not find default immunization filter script." ) );
        final ExecutableScript conditionFilterExecutableScript = executableScriptRepository.findById( DEFAULT_CONDITION_SEARCH_FILTER_SCRIPT_ID )
            .orElseThrow( () -> new MetadataSheetImportException( "Could not find default condition filter script." ) );
        final ExecutableScript medicationRequestFilterExecutableScript = executableScriptRepository.findById( DEFAULT_MEDICATION_REQUEST_SEARCH_FILTER_SCRIPT_ID )
            .orElseThrow( () -> new MetadataSheetImportException( "Could not find default medication request filter script." ) );

        if ( sheet == null )
        {
            messageCollector.addMessage( new MetadataSheetMessage(
                MetadataSheetMessageSeverity.ERROR, "Sheet '" + RULES_SHEET_NAME + "' is not included." ) );

            return messageCollector;
        }

        final Program program = programMetadataService.findProgramByReference( programRef ).orElse( null );

        if ( program == null )
        {
            messageCollector.addMessage( new MetadataSheetMessage(
                MetadataSheetMessageSeverity.ERROR, "Program has not been defined in DHIS2: " + programRef ) );

            return messageCollector;
        }

        final MappedTrackerProgram mappedTrackerProgram = mappedTrackerProgramRepository.findOneByProgramReference( program.getAllReferences() ).orElse( null );

        if ( mappedTrackerProgram == null )
        {
            messageCollector.addMessage( new MetadataSheetMessage(
                MetadataSheetMessageSeverity.ERROR, "There is no mapping for program: " + programRef ) );

            return messageCollector;
        }

        programStageRuleRepository.deleteAllByProgram( mappedTrackerProgram );

        final Set<ProgramStageDataElementKey> unprocessedKeys = new HashSet<>();
        program.getStages().forEach( ps -> ps.getDataElements().forEach( de -> unprocessedKeys.add( new ProgramStageDataElementKey( ps, de.getElement() ) ) ) );

        final Map<String, AtomicInteger> dataElementUsages = new HashMap<>();
        final Set<String> processedCodeSetCodes = new HashSet<>();
        final Map<String, CodeSet> codeSets = new HashMap<>();
        final Set<String> codeSetDisplayNames = new HashSet<>();
        final int lastRowNum = sheet.getLastRowNum();

        // skip first header column
        for ( int rowNum = 1; rowNum <= lastRowNum; rowNum++ )
        {
            final Row row = sheet.getRow( rowNum );

            if ( row != null && notEmpty( row, DATA_ELEMENT_REFS_COL ) )
            {
                final Reference programStageRef = getReference( getString( row, PROGRAM_STAGE_REF_COL ) );
                final String dhisResourceTypeValue = getString( row, DHIS_RESOURCE_TYPE_COL );
                final String fhirResourceTypeValue = getString( row, FHIR_RESOURCE_TYPE_COL );
                final Set<Reference> dataElementRefs = getAllReferences( getString( row, DATA_ELEMENT_REFS_COL ) );
                final String f2dApplicableScriptCode = getString( row, F2D_APPLICABLE_SCRIPT_CODE_COL );
                final String f2dTransformScriptCode = getString( row, F2D_TRANSFORM_SCRIPT_CODE_COL );
                final String d2fApplicableScriptCode = getString( row, D2F_APPLICABLE_SCRIPT_CODE_COL );
                final String d2fTransformScriptCode = getString( row, D2F_TRANSFORM_SCRIPT_CODE_COL );
                final Set<ProgramStageDataElement> dataElements = new LinkedHashSet<>();

                String codeSetCode = StringUtils.upperCase( getString( row, CODE_SET_CODE_COL ) );
                String codeSetDisplayName = getString( row, CODE_SET_DISPLAY_NAME_COL );
                Boolean codeSetPreferred = getBoolean( sheet, rowNum, CODE_SET_PREFERRED_COL );
                List<String> codeSetCodes = getStringList( getString( row, CODE_SET_CODES_COL ) );

                DhisResourceType dhisResourceType = null;
                FhirResourceType fhirResourceType = null;
                ProgramStage programStage = null;
                MappedTrackerProgramStage mappedTrackerProgramStage = null;
                boolean newCodeSetCode = false;
                CodeSet codeSet = null;
                ExecutableScript f2dApplicableScript = null;
                ExecutableScript f2dTransformScript = null;
                ExecutableScript d2fApplicableScript = null;
                ExecutableScript d2fTransformScript = null;

                if ( programStageRef == null )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, PROGRAM_STAGE_REF_COL ),
                        "Program stage reference is invalid." ) );
                }
                else
                {
                    programStage = program.getOptionalStage( programStageRef ).orElse( null );

                    if ( programStage == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, PROGRAM_STAGE_REF_COL ),
                            "Program stage does not exist in DHIS2: " + programStageRef ) );
                    }
                    else
                    {
                        mappedTrackerProgramStage = mappedTrackerProgramStageRepository.findOneByProgramAndProgramStageReference( mappedTrackerProgram, programStage.getAllReferences() ).orElse( null );

                        if ( mappedTrackerProgramStage == null )
                        {
                            messageCollector.addMessage( new MetadataSheetMessage(
                                MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, PROGRAM_STAGE_REF_COL ),
                                "Program stage has not been mapped: " + programStageRef ) );
                        }
                    }
                }

                if ( dhisResourceTypeValue == null )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, DHIS_RESOURCE_TYPE_COL ),
                        "DHIS resource type is invalid." ) );
                }
                else
                {
                    try
                    {
                        dhisResourceType = NameUtils.toEnumValue( DhisResourceType.class, dhisResourceTypeValue );
                    }
                    catch ( IllegalArgumentException e )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, DHIS_RESOURCE_TYPE_COL ),
                            "DHIS resource type is invalid: " + dhisResourceTypeValue ) );
                    }

                    if ( dhisResourceType != null && dhisResourceType != DhisResourceType.PROGRAM_STAGE_EVENT )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, DHIS_RESOURCE_TYPE_COL ),
                            "Only program stage DHIS resource type is supported currently." ) );
                    }
                }

                if ( fhirResourceTypeValue == null )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, FHIR_RESOURCE_TYPE_COL ),
                        "FHIR resource type is invalid." ) );
                }
                else
                {
                    try
                    {
                        fhirResourceType = NameUtils.toEnumValue( FhirResourceType.class, fhirResourceTypeValue );
                    }
                    catch ( IllegalArgumentException e )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, FHIR_RESOURCE_TYPE_COL ),
                            "FHIR resource type is invalid: " + fhirResourceTypeValue ) );
                    }
                }

                if ( dataElementRefs == null )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, DATA_ELEMENT_REFS_COL ),
                        "Data element references is invalid." ) );
                }
                else if ( programStage != null )
                {
                    final ProgramStage finalProgramStage = programStage;
                    final int finalRowNum = rowNum;
                    final Set<Reference> processedDataElementRefs = new HashSet<>();

                    dataElementRefs.forEach( ref -> {
                        final ProgramStageDataElement dataElement = finalProgramStage.getDataElement( ref );

                        if ( dataElement == null )
                        {
                            messageCollector.addMessage( new MetadataSheetMessage(
                                MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, finalRowNum, DATA_ELEMENT_REFS_COL ),
                                "Data element '" + ref + "' does not exist in program stage '" + programStageRef + "'." ) );
                        }
                        else if ( processedDataElementRefs.addAll( dataElement.getElement().getAllReferences() ) )
                        {
                            dataElements.add( dataElement );
                            unprocessedKeys.remove( new ProgramStageDataElementKey( finalProgramStage, dataElement.getElement() ) );
                        }
                    } );
                }

                if ( codeSetCode == null )
                {
                    if ( codeSetCodes == null )
                    {
                        if ( !dataElements.isEmpty() )
                        {
                            final ProgramStageDataElement dataElement = dataElements.stream().findFirst().get();

                            // if no codes have been specified the first data element will be used to create an empty code set
                            codeSetCode = createCode( "DE_" + StringUtils.defaultIfBlank( dataElement.getElement().getCode(), dataElement.getElement().getName() ) );

                            if ( processedCodeSetCodes.add( codeSetCode ) )
                            {
                                codeSetDisplayName = dataElement.getElement().getName();
                                codeSetPreferred = Boolean.FALSE;
                                codeSetCodes = Collections.emptyList();
                                newCodeSetCode = true;

                                if ( !codeSetDisplayNames.add( codeSetDisplayName ) )
                                {
                                    messageCollector.addMessage( new MetadataSheetMessage(
                                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, CODE_SET_CODE_COL ),
                                        "Code set display name has already been used for a different code set: " + codeSetDisplayName ) );
                                }
                            }
                        }
                    }
                    else
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, CODE_SET_CODE_COL ),
                            "Code set code is invalid." ) );
                    }
                }
                else if ( processedCodeSetCodes.add( codeSetCode ) )
                {
                    if ( codeSetDisplayName == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, CODE_SET_DISPLAY_NAME_COL ),
                            "Code set display name is invalid." ) );
                    }
                    else if ( !codeSetDisplayNames.add( codeSetDisplayName ) )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, CODE_SET_CODE_COL ),
                            "Code set display name has already been used for a different code set: " + codeSetDisplayName ) );
                    }

                    if ( codeSetPreferred == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, CODE_SET_DISPLAY_NAME_COL ),
                            "Code set preferred is invalid." ) );
                    }

                    newCodeSetCode = true;
                }

                if ( f2dApplicableScriptCode != null )
                {
                    f2dApplicableScript = executableScriptRepository.findOneByCode( f2dApplicableScriptCode ).orElse( null );

                    if ( f2dApplicableScript == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, F2D_APPLICABLE_SCRIPT_CODE_COL ),
                            "FHIR to DHIS2 applicable script does not exist: " + f2dApplicableScriptCode ) );
                    }
                }

                if ( f2dTransformScriptCode != null )
                {
                    f2dTransformScript = executableScriptRepository.findOneByCode( f2dTransformScriptCode ).orElse( null );

                    if ( f2dTransformScript == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, F2D_APPLICABLE_SCRIPT_CODE_COL ),
                            "FHIR to DHIS 2 transform script does not exist: " + f2dApplicableScriptCode ) );
                    }
                }

                if ( d2fApplicableScriptCode != null )
                {
                    d2fApplicableScript = executableScriptRepository.findOneByCode( d2fApplicableScriptCode ).orElse( null );

                    if ( d2fApplicableScript == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, D2F_APPLICABLE_SCRIPT_CODE_COL ),
                            "DHIS2 to FHIR applicable script does not exist: " + d2fApplicableScriptCode ) );
                    }
                }

                if ( d2fTransformScriptCode != null )
                {
                    d2fTransformScript = executableScriptRepository.findOneByCode( d2fTransformScriptCode ).orElse( null );

                    if ( d2fTransformScript == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, D2F_APPLICABLE_SCRIPT_CODE_COL ),
                            "FHIR to DHIS 2 transform script does not exist: " + d2fApplicableScriptCode ) );
                    }
                }

                if ( messageCollector.isOk() )
                {
                    if ( newCodeSetCode )
                    {
                        codeSet = createCodeSet( messageCollector, rowNum,
                            codeSetCode, Objects.requireNonNull( codeSetDisplayName ), codeSetPreferred, Objects.requireNonNull( codeSetCodes ) );

                        if ( codeSet != null )
                        {
                            codeSets.put( codeSetCode, codeSet );
                        }
                    }
                    else
                    {
                        codeSet = Objects.requireNonNull( codeSets.get( codeSetCode ), "No code set with code: " + codeSetCode );
                    }
                }

                if ( messageCollector.isOk() )
                {
                    final ProgramStageRule rule = new ProgramStageRule();
                    final ProgramStageDataElement dataElement = Objects.requireNonNull( dataElements.stream().findFirst().orElse( null ) );
                    final int usageCount = dataElementUsages.computeIfAbsent( dataElement.getElementId(), k -> new AtomicInteger() ).incrementAndGet();

                    rule.setName( StringUtils.left( program.getId() + ": " + usageCount + " " + dataElement.getElement().getName(), AbstractRule.MAX_NAME_LENGTH ) );
                    rule.setDescription( program.getName() + ": " + programStage.getName() + ": " + usageCount + " " + dataElement.getElement().getName() );
                    rule.setProgramStage( mappedTrackerProgramStage );
                    rule.setFhirResourceType( fhirResourceType );
                    rule.setEnrollmentCreationEnabled( true );
                    rule.setEventCreationEnabled( true );
                    rule.setContainedAllowed( false );
                    rule.setEnabled( true );
                    rule.setImpEnabled( true );
                    rule.setExpEnabled( true );
                    rule.setFhirCreateEnabled( true );
                    rule.setFhirUpdateEnabled( true );
                    rule.setFhirDeleteEnabled( true );
                    rule.setGrouping( false );
                    rule.setApplicableImpScript( f2dApplicableScript );
                    rule.setTransformImpScript( f2dTransformScript );
                    rule.setApplicableExpScript( d2fApplicableScript );
                    rule.setTransformExpScript( d2fTransformScript );
                    rule.setApplicableCodeSet( codeSet );
                    rule.setDhisDataReferences( new ArrayList<>() );
                    rule.setApplicableEnrollmentStatus( new ApplicableEnrollmentStatus() );
                    rule.setApplicableEventStatus( new ApplicableEventStatus() );
                    rule.setEventStatusUpdate( new EventStatusUpdate() );

                    switch ( Objects.requireNonNull( fhirResourceType ) )
                    {
                        case OBSERVATION:
                            rule.setFilterScript( observationFilterExecutableScript );
                            break;
                        case IMMUNIZATION:
                            rule.setFilterScript( immunizationFilterExecutableScript );
                            break;
                        case CONDITION:
                            rule.setFilterScript( conditionFilterExecutableScript );
                            break;
                        case MEDICATION_REQUEST:
                            rule.setFilterScript( medicationRequestFilterExecutableScript );
                            break;
                    }

                    dataElements.forEach( de -> {
                        final RuleDhisDataReference dataReference = new RuleDhisDataReference();

                        dataReference.setRule( rule );
                        dataReference.setDataReference( getReference( de.getElement().getAllReferences() ) );
                        dataReference.setDescription( de.getElement().getName() );
                        dataReference.setScriptArgName( "dataElement" +
                            ( rule.getDhisDataReferences().isEmpty() ? "" : ( rule.getDhisDataReferences().size() + 1 ) ) );
                        dataReference.setRequired( true );

                        rule.getDhisDataReferences().add( dataReference );
                    } );

                    programStageRuleRepository.save( rule );
                }
            }
        }

        unprocessedKeys.forEach( uk -> messageCollector.addMessage(
            new MetadataSheetMessage( MetadataSheetMessageSeverity.WARN, new MetadataSheetLocation( RULES_SHEET_NAME ),
                "Data element has not been mapped: " + uk.getDataElement().getId() + " (" + uk.getDataElement().getName() + ")" ) ) );

        return messageCollector;
    }

    @Nullable
    protected CodeSet createCodeSet( @Nonnull MetadataSheetMessageCollector messageCollector, int rowNum,
        @Nonnull String codeSetCode, @Nonnull String codeSetDisplayName, boolean codeSetPreferred, @Nonnull List<String> codeSetCodes )
    {
        final Collection<SystemCode> systemCodes;
        final String preferredCodeSetCode = codeSetCodes.stream().findFirst().orElse( null );
        boolean ok = true;

        if ( codeSetCodes.isEmpty() )
        {
            systemCodes = Collections.emptyList();
        }
        else
        {
            systemCodes = systemCodeRepository.findAllBySystemCodeValues( codeSetCodes );

            for ( final String systemCodeValue : codeSetCodes )
            {
                final SystemCode systemCode = systemCodes.stream().filter( sc -> systemCodeValue.equals( sc.getSystemCodeValue() ) ).findFirst().orElse( null );

                if ( systemCode == null )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( RULES_SHEET_NAME, rowNum, CODE_SET_CODES_COL ),
                        "Code set code has not been defined: " + systemCodeValue ) );
                    ok = false;
                }
            }
        }

        if ( !ok )
        {
            return null;
        }

        final CodeSet codeSet = codeSetRepository.findOneByCode( StringUtils.left( codeSetCode, CodeSet.MAX_CODE_LENGTH ) ).orElseGet( CodeSet::new );
        final Set<String> processedCodes = new HashSet<>();

        codeSet.setCode( StringUtils.left( codeSetCode, CodeSet.MAX_CODE_LENGTH ) );
        codeSet.setName( StringUtils.left( codeSetDisplayName, CodeSet.MAX_NAME_LENGTH ) );
        codeSet.setDescription( codeSetDisplayName );

        if ( codeSet.getCodeCategory() == null )
        {
            final CodeCategory unspecifiedCodeCategory = codeCategoryRepository.findById( CodeCategory.UNSPECIFIED_CATEGORY_ID ).orElse( null );

            if ( unspecifiedCodeCategory == null )
            {
                messageCollector.addMessage( new MetadataSheetMessage( MetadataSheetMessageSeverity.ERROR,
                    "Code category for unspecified items could not be found." ) );

                return null;
            }

            codeSet.setCodeCategory( unspecifiedCodeCategory );
        }

        if ( codeSet.getCodeSetValues() == null )
        {
            codeSet.setCodeSetValues( new ArrayList<>() );
        }

        codeSet.getCodeSetValues().clear();

        systemCodes.forEach( sc -> {
            if ( processedCodes.add( sc.getCode().getCode() ) )
            {
                final CodeSetValue codeSetValue = new CodeSetValue();

                codeSetValue.setCodeSet( codeSet );
                codeSetValue.setCode( sc.getCode() );
                codeSetValue.setEnabled( true );
                codeSetValue.setPreferredExport( codeSetPreferred && sc.getSystemCodeValue().equals( preferredCodeSetCode ) );
            }
        } );

        codeSetRepository.save( codeSet );

        return codeSet;
    }
}
