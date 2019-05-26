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

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeCategory;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeCategoryAware;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSet;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSetValue;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeCategoryRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeSetRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ExecutableScriptRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackerProgramRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.MappedTrackerProgramStageRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ProgramStageRuleRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetLocation;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessage;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessageCollector;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessageSeverity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Processes the metadata import of codes from a sheet.<br>
 *
 * <b>This metadata sheet import tool is just a temporary solution
 * and may be removed in the future completely.</b>
 *
 * @author volsch
 */
@Component
public class MetadataSheetCodeImportProcessor extends AbstractMetadataSheetImportProcessor
{
    public static final String CODES_SHEET_NAME = "Codes";

    public static final int SYSTEM_URI_COL = 0;

    public static final int GENERIC_CODE_COL = 1;

    public static final int CODE_COL = 2;

    public static final int DISPLAY_NAME_COL = 3;

    public static final int DESCRIPTION_COL = 4;

    public static final int CODE_SET_CODE_COL = 5;

    public static final int CODE_SET_DISPLAY_NAME_COL = 6;

    public static final int CODE_SET_MAPPED_CODE_COL = 7;

    public static final int CODE_SET_PREFERRED_COL = 8;

    public static final String LOINC_SYSTEM_URI = "http://loinc.org";

    public static final String CVX_SYSTEM_URI = "http://hl7.org/fhir/sid";

    private final ProgramMetadataService programMetadataService;

    private final CodeCategoryRepository codeCategoryRepository;

    private final CodeSetRepository codeSetRepository;

    private final CodeRepository codeRepository;

    private final SystemRepository systemRepository;

    private final SystemCodeRepository systemCodeRepository;

    private final MappedTrackerProgramRepository mappedTrackerProgramRepository;

    private final MappedTrackerProgramStageRepository mappedTrackerProgramStageRepository;

    private final ProgramStageRuleRepository programStageRuleRepository;

    private final ExecutableScriptRepository executableScriptRepository;

    public MetadataSheetCodeImportProcessor( @Nonnull ProgramMetadataService programMetadataService, @Nonnull CodeCategoryRepository codeCategoryRepository,
        @Nonnull CodeSetRepository codeSetRepository, @Nonnull CodeRepository codeRepository, @Nonnull SystemRepository systemRepository, @Nonnull SystemCodeRepository systemCodeRepository,
        @Nonnull MappedTrackerProgramRepository mappedTrackerProgramRepository, @Nonnull MappedTrackerProgramStageRepository mappedTrackerProgramStageRepository,
        @Nonnull ProgramStageRuleRepository programStageRuleRepository, @Nonnull ExecutableScriptRepository executableScriptRepository )
    {
        this.programMetadataService = programMetadataService;
        this.codeCategoryRepository = codeCategoryRepository;
        this.codeSetRepository = codeSetRepository;
        this.codeRepository = codeRepository;
        this.systemRepository = systemRepository;
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
        final Sheet sheet = workbook.getSheet( CODES_SHEET_NAME );

        if ( sheet == null )
        {
            messageCollector.addMessage( new MetadataSheetMessage(
                MetadataSheetMessageSeverity.ERROR, "Sheet '" + CODES_SHEET_NAME + "' is not included." ) );

            return messageCollector;
        }

        final Set<String> genericCodes = new HashSet<>();
        final Set<String> displayNames = new HashSet<>();
        final Map<String, CodeSet> codeSets = new HashMap<>();
        final int lastRowNum = sheet.getLastRowNum();

        // skip first header column
        for ( int rowNum = 1; rowNum <= lastRowNum; rowNum++ )
        {
            final Row row = sheet.getRow( rowNum );

            if ( row != null && notEmpty( row, CODE_COL ) )
            {
                final String systemUri = getString( row, SYSTEM_URI_COL );
                final String code = getString( row, CODE_COL );
                final String displayName = getString( row, DISPLAY_NAME_COL );
                final String description = getString( row, DESCRIPTION_COL );
                final String codeSetCode = StringUtils.upperCase( getString( row, CODE_SET_CODE_COL ) );
                final String codeSetDisplayName = getString( row, CODE_SET_DISPLAY_NAME_COL );
                final String codeSetMappedCode = getString( row, CODE_SET_MAPPED_CODE_COL );
                final Boolean codeSetPreferred = getBoolean( sheet, rowNum, CODE_SET_PREFERRED_COL );
                System system = null;
                String genericCode = getString( row, GENERIC_CODE_COL );
                CodeSet codeSet;
                CodeSetValue codeSetValue = null;

                if ( CVX_SYSTEM_URI.equals( systemUri ) )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.INFO, new MetadataSheetLocation( CODES_SHEET_NAME, rowNum, SYSTEM_URI_COL ),
                        "CVX codes are ignore." ) );

                    continue;
                }

                if ( systemUri == null )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( CODES_SHEET_NAME, rowNum, SYSTEM_URI_COL ),
                        "System URI must be specified." ) );
                }
                else
                {
                    system = systemRepository.findOneBySystemUri( systemUri ).orElse( null );

                    if ( system == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( CODES_SHEET_NAME, rowNum, SYSTEM_URI_COL ),
                            "System URI has not been configured: " + systemUri ) );
                    }
                }

                if ( code == null )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( CODES_SHEET_NAME, rowNum, SYSTEM_URI_COL ),
                        "Code must be specified." ) );
                }
                else if ( genericCode == null )
                {
                    if ( LOINC_SYSTEM_URI.equals( systemUri ) )
                    {
                        genericCode = "LOINC_" + code;
                    }
                    else
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( CODES_SHEET_NAME, rowNum, SYSTEM_URI_COL ),
                            "Generic code must be specified if system is not LOINC." ) );
                    }
                }

                if ( genericCode != null && !genericCodes.add( genericCode ) )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( CODES_SHEET_NAME, rowNum, GENERIC_CODE_COL ),
                        "Generic code is not unique within sheet." ) );
                }

                if ( displayName == null )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( CODES_SHEET_NAME, rowNum, DISPLAY_NAME_COL ),
                        "Code must be specified." ) );
                }
                else if ( !displayNames.add( displayName ) )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( CODES_SHEET_NAME, rowNum, DISPLAY_NAME_COL ),
                        "Display name is not unique within sheet." ) );
                }

                if ( description == null )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( CODES_SHEET_NAME, rowNum, DESCRIPTION_COL ),
                        "Description must be specified." ) );
                }

                if ( codeSetCode != null )
                {
                    if ( codeSetMappedCode == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( CODES_SHEET_NAME, rowNum, CODE_SET_MAPPED_CODE_COL ),
                            "Code set mapped code must be specified." ) );
                    }

                    if ( codeSetPreferred == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( CODES_SHEET_NAME, rowNum, CODE_SET_PREFERRED_COL ),
                            "Value must be specified." ) );
                    }

                    if ( messageCollector.isOk() )
                    {
                        codeSet = codeSets.get( codeSetCode );

                        if ( codeSet == null )
                        {
                            if ( codeSetDisplayName == null )
                            {
                                messageCollector.addMessage( new MetadataSheetMessage(
                                    MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( CODES_SHEET_NAME, rowNum, CODE_SET_DISPLAY_NAME_COL ),
                                    "Code set display name must be specified." ) );
                            }
                            else
                            {
                                codeSet = codeSetRepository.findOneByCode( codeSetCode ).orElseGet( CodeSet::new );
                                codeSet.setCode( StringUtils.left( codeSetCode, CodeSet.MAX_CODE_LENGTH ) );
                                codeSet.setName( StringUtils.left( codeSetDisplayName, CodeSet.MAX_NAME_LENGTH ) );
                                codeSet.setDescription( codeSetDisplayName );
                                updateCodeCategory( messageCollector, rowNum, codeSet );

                                if ( codeSet.getCodeSetValues() == null )
                                {
                                    codeSet.setCodeSetValues( new ArrayList<>() );
                                }

                                codeSet.getCodeSetValues().clear();

                                codeSets.put( codeSetCode, codeSet );
                            }
                        }

                        if ( codeSet != null )
                        {
                            codeSetValue = new CodeSetValue();
                            codeSetValue.setCodeSet( codeSet );
                            codeSetValue.setEnabled( true );
                            codeSetValue.setPreferredExport( Objects.requireNonNull( codeSetPreferred ) );
                            codeSet.getCodeSetValues().add( codeSetValue );
                        }
                    }
                }

                if ( messageCollector.isOk() )
                {
                    final SystemCode systemCode = systemCodeRepository.findOneBySystemAndSystemCode(
                        Objects.requireNonNull( system ), Objects.requireNonNull( code ) ).orElseGet( SystemCode::new );

                    final Code resultingCode = ObjectUtils.defaultIfNull( systemCode.getCode(), new Code() );
                    resultingCode.setEnabled( true );
                    resultingCode.setCode( StringUtils.left( StringUtils.defaultIfBlank( genericCode, code ), Code.MAX_CODE_LENGTH ) );
                    resultingCode.setMappedCode( codeSetMappedCode );
                    resultingCode.setName( StringUtils.left( displayName, SystemCode.MAX_DISPLAY_NAME_LENGTH ) );
                    resultingCode.setDescription( description );

                    updateCodeCategory( messageCollector, rowNum, resultingCode );

                    if ( messageCollector.isOk() )
                    {
                        codeRepository.save( resultingCode );

                        systemCode.setCode( resultingCode );
                        systemCode.setSystem( system );
                        systemCode.setSystemCode( StringUtils.left( code, SystemCode.MAX_SYSTEM_CODE_LENGTH ) );
                        systemCode.setEnabled( true );
                        systemCode.setDisplayName( StringUtils.left( displayName, SystemCode.MAX_DISPLAY_NAME_LENGTH ) );
                        systemCodeRepository.save( systemCode );

                        if ( codeSetValue != null )
                        {
                            codeSetValue.setCode( resultingCode );
                        }
                    }
                }
            }
        }

        if ( messageCollector.isOk() && !codeSets.isEmpty() )
        {
            codeSetRepository.saveAll( codeSets.values() );
        }

        return messageCollector;
    }

    protected void updateCodeCategory( @Nonnull MetadataSheetMessageCollector messageCollector, int rowNum, @Nonnull CodeCategoryAware codeCategoryAware )
    {
        if ( codeCategoryAware.getCodeCategory() == null )
        {
            final CodeCategory unspecifiedCodeCategory = codeCategoryRepository
                .findById( CodeCategory.UNSPECIFIED_CATEGORY_ID ).orElse( null );

            if ( unspecifiedCodeCategory == null )
            {
                messageCollector.addMessage( new MetadataSheetMessage( MetadataSheetMessageSeverity.ERROR,
                    "Code category for unspecified items could not be found." ) );
            }
            else
            {
                codeCategoryAware.setCodeCategory( unspecifiedCodeCategory );
            }
        }
    }
}
