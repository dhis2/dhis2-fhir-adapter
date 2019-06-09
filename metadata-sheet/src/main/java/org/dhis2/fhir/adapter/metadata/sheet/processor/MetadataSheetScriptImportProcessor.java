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

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dhis2.fhir.adapter.fhir.metadata.model.DataType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.Script;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptSource;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptSourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ExecutableScriptRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ScriptRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ScriptSourceRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetLocation;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessage;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessageCollector;
import org.dhis2.fhir.adapter.metadata.sheet.model.MetadataSheetMessageSeverity;
import org.dhis2.fhir.adapter.script.ScriptCompilationException;
import org.dhis2.fhir.adapter.script.ScriptEvaluator;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Processes the metadata import of scripts from an Excel sheet.<br>
 *
 * <b>This metadata sheet import tool is just a temporary solution
 * and may be removed in the future completely.</b>
 *
 * @author volsch
 */
@Component
public class MetadataSheetScriptImportProcessor extends AbstractMetadataSheetImportProcessor
{
    public static final String SHEET_NAME = "Scripts";

    public static final int SCRIPT_CODE_COL = 0;

    public static final int SCRIPT_NAME_COL = 1;

    public static final int SCRIPT_TYPE_COL = 2;

    public static final int SCRIPT_RETURN_TYPE_COL = 3;

    public static final int SCRIPT_VERSIONS_COL = 4;

    public static final int SCRIPT_SOURCE_COL = 5;

    private ScriptEvaluator scriptEvaluator;

    private ScriptRepository scriptRepository;

    private ScriptSourceRepository scriptSourceRepository;

    private ExecutableScriptRepository executableScriptRepository;

    public MetadataSheetScriptImportProcessor( @Nonnull ScriptEvaluator scriptEvaluator,
        @Nonnull ScriptRepository scriptRepository, @Nonnull ScriptSourceRepository scriptSourceRepository,
        @Nonnull ExecutableScriptRepository executableScriptRepository )
    {
        this.scriptEvaluator = scriptEvaluator;
        this.scriptRepository = scriptRepository;
        this.scriptSourceRepository = scriptSourceRepository;
        this.executableScriptRepository = executableScriptRepository;
    }

    @Nonnull
    @Transactional
    public MetadataSheetMessageCollector process( @Nonnull Workbook workbook )
    {
        final MetadataSheetMessageCollector messageCollector = new MetadataSheetMessageCollector();
        final Sheet sheet = workbook.getSheet( SHEET_NAME );

        if ( sheet == null )
        {
            messageCollector.addMessage( new MetadataSheetMessage(
                MetadataSheetMessageSeverity.ERROR, "Sheet '" + SHEET_NAME + "' is not included." ) );

            return messageCollector;
        }

        final int lastRowNum = sheet.getLastRowNum();

        // skip first header column
        for ( int rowNum = 1; rowNum <= lastRowNum; rowNum++ )
        {
            final Row row = sheet.getRow( rowNum );

            if ( row != null && notEmpty( row, SCRIPT_CODE_COL ) )
            {
                if ( row.getLastCellNum() < SCRIPT_SOURCE_COL )
                {
                    messageCollector.addMessage( new MetadataSheetMessage(
                        MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( SHEET_NAME, rowNum ), "Too less columns." ) );
                }
                else
                {
                    final String code = getString( row, SCRIPT_CODE_COL );
                    final String name = getString( row, SCRIPT_NAME_COL );
                    final String scriptType = getString( row, SCRIPT_TYPE_COL );
                    final String returnType = getString( row, SCRIPT_RETURN_TYPE_COL );
                    final String versions = getString( row, SCRIPT_VERSIONS_COL );
                    final String source = getString( row, SCRIPT_SOURCE_COL );
                    ScriptType convertedScriptType = null;
                    List<FhirVersion> convertedVersions = null;
                    DataType convertedReturnType = null;

                    if ( StringUtils.length( code ) > Script.MAX_CODE_LENGTH )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( SHEET_NAME, rowNum, SCRIPT_CODE_COL ),
                            "Code must not be longer than " + Script.MAX_CODE_LENGTH + " characters." ) );
                    }

                    if ( name == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( SHEET_NAME, rowNum, SCRIPT_NAME_COL ),
                            "Name is required." ) );
                    }

                    if ( StringUtils.length( name ) > Script.MAX_NAME_LENGTH )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( SHEET_NAME, rowNum, SCRIPT_NAME_COL ),
                            "Name must not be longer than " + Script.MAX_NAME_LENGTH + " characters." ) );
                    }

                    if ( scriptType == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( SHEET_NAME, rowNum, SCRIPT_TYPE_COL ),
                            "Type is required." ) );
                    }
                    else
                    {
                        try
                        {
                            convertedScriptType = NameUtils.toEnumValue( ScriptType.class, scriptType );
                        }
                        catch ( IllegalArgumentException e )
                        {
                            messageCollector.addMessage( new MetadataSheetMessage(
                                MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( SHEET_NAME, rowNum, SCRIPT_TYPE_COL ),
                                "Script type is invalid: " + scriptType ) );
                        }
                    }

                    if ( returnType == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( SHEET_NAME, rowNum, SCRIPT_RETURN_TYPE_COL ),
                            "Script return type is required." ) );
                    }
                    else
                    {
                        try
                        {
                            convertedReturnType = NameUtils.toEnumValue( DataType.class, returnType );
                        }
                        catch ( IllegalArgumentException e )
                        {
                            messageCollector.addMessage( new MetadataSheetMessage(
                                MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( SHEET_NAME, rowNum, SCRIPT_RETURN_TYPE_COL ),
                                "Script return type is invalid: " + returnType ) );
                        }
                    }

                    if ( versions == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( SHEET_NAME, rowNum, SCRIPT_VERSIONS_COL ),
                            "Script versions are required." ) );
                    }
                    else
                    {
                        convertedVersions = getEnums( FhirVersion.class, versions );

                        if ( convertedVersions == null )
                        {
                            messageCollector.addMessage( new MetadataSheetMessage(
                                MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( SHEET_NAME, rowNum, SCRIPT_VERSIONS_COL ),
                                "Script versions are invalid: " + versions ) );
                        }
                    }

                    if ( source == null )
                    {
                        messageCollector.addMessage( new MetadataSheetMessage(
                            MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( SHEET_NAME, rowNum, SCRIPT_SOURCE_COL ),
                            "Script source is required." ) );
                    }
                    else
                    {
                        try
                        {
                            scriptEvaluator.compile( source );
                        }
                        catch ( ScriptCompilationException e )
                        {
                            messageCollector.addMessage( new MetadataSheetMessage(
                                MetadataSheetMessageSeverity.ERROR, new MetadataSheetLocation( SHEET_NAME, rowNum, SCRIPT_SOURCE_COL ),
                                "Script source is invalid: " + e.getMessage() ) );
                        }
                    }

                    if ( messageCollector.isOk() )
                    {
                        final Script script = scriptRepository.findOneByCode( Objects.requireNonNull( code ) ).orElseGet( Script::new );

                        script.setCode( code );
                        script.setName( name );
                        script.setScriptType( convertedScriptType );
                        script.setReturnType( convertedReturnType );
                        script.setDescription( name );

                        if ( script.getSources() == null )
                        {
                            script.setSources( new ArrayList<>() );
                        }

                        ScriptSource scriptSource = script.getSources().isEmpty() ? null : script.getSources().get( 0 );

                        if ( scriptSource == null )
                        {
                            scriptSource = new ScriptSource();
                            script.getSources().add( scriptSource );
                        }

                        scriptSource.setScript( script );
                        scriptSource.setFhirVersions( new TreeSet<>( Objects.requireNonNull( convertedVersions ) ) );
                        scriptSource.setSourceType( ScriptSourceType.JAVASCRIPT );
                        scriptSource.setSourceText( source );

                        scriptRepository.save( script );
                        scriptSourceRepository.save( scriptSource );

                        final ExecutableScript executableScript = executableScriptRepository.findOneByCode( code ).orElseGet( ExecutableScript::new );

                        executableScript.setScript( script );
                        executableScript.setCode( code );
                        executableScript.setName( name );
                        executableScript.setDescription( name );

                        executableScriptRepository.save( executableScript );
                    }
                }
            }
        }

        return messageCollector;
    }
}
