package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata.r4;

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

import org.dhis2.fhir.adapter.dhis.model.WritableDataElement;
import org.dhis2.fhir.adapter.dhis.model.WritableOption;
import org.dhis2.fhir.adapter.dhis.model.WritableOptionSet;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgramStageDataElement;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.extension.ValueTypeExtensionUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageMetadataRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedDhisMetadata;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.dhis2.fhir.adapter.model.ValueType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.StringType;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Unit tests for {@link R4ProgramStageMetadataToFhirQuestionnaireTransformer}.
 *
 * @author volsch
 */
public class R4ProgramStageMetadataToFhirQuestionnaireTransformerTest
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
    private FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    @Mock
    private OrganizationUnitService organizationUnitService;

    @Mock
    private DhisToFhirTransformerContext context;

    @Mock
    private FhirClient fhirClient;

    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @InjectMocks
    private R4ProgramStageMetadataToFhirQuestionnaireTransformer transformer;

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
        programStageDataElement.setCompulsory( false );
        programStageDataElement.setElement( dataElement );
        programStage.getDataElements().add( programStageDataElement );

        WritableOptionSet optionSet = new WritableOptionSet();
        optionSet.setOptions( new ArrayList<>() );
        optionSet.getOptions().add( new WritableOption( "5", "Test Value 1" ) );
        optionSet.getOptions().add( new WritableOption( "7", "Test Value 2" ) );

        programStageDataElement = new WritableProgramStageDataElement();
        dataElement = new WritableDataElement();
        dataElement.setId( "d1123456789" );
        dataElement.setName( "Value 2" );
        dataElement.setValueType( ValueType.INTEGER );
        dataElement.setOptionSetValue( true );
        dataElement.setOptionSet( optionSet );
        programStageDataElement.setCompulsory( true );
        programStageDataElement.setElement( dataElement );
        programStage.getDataElements().add( programStageDataElement );

        final Questionnaire fhirQuestionnaire = new Questionnaire();

        transformer.transformInternal( fhirClient, context, new RuleInfo<>( new ProgramStageMetadataRule(), Collections.emptyList() ), new HashMap<>(),
            new WritableScriptedDhisMetadata( programStage, scriptExecutionContext ), fhirQuestionnaire );

        Assert.assertEquals( "b1234567890", fhirQuestionnaire.getUrl() );
        Assert.assertEquals( "Test Stage 1", fhirQuestionnaire.getTitle() );
        Assert.assertEquals( "Test Description 1", fhirQuestionnaire.getDescription() );
        Assert.assertEquals( 2, fhirQuestionnaire.getItem().size() );

        Assert.assertEquals( "d0123456789", fhirQuestionnaire.getItem().get( 0 ).getLinkId() );
        Assert.assertEquals( "Value 1", fhirQuestionnaire.getItem().get( 0 ).getText() );
        Assert.assertEquals( 0, fhirQuestionnaire.getItem().get( 0 ).getAnswerOption().size() );
        Assert.assertFalse( fhirQuestionnaire.getItem().get( 0 ).getRequired() );

        Assert.assertEquals( 1, fhirQuestionnaire.getItem().get( 0 ).getExtension().size() );
        Assert.assertEquals( ValueTypeExtensionUtils.URL, fhirQuestionnaire.getItem().get( 0 ).getExtension().get( 0 ).getUrl() );
        Assert.assertTrue( fhirQuestionnaire.getItem().get( 0 ).getExtension().get( 0 ).getValue() instanceof StringType );
        Assert.assertEquals( "TEXT", ( (StringType) fhirQuestionnaire.getItem().get( 0 ).getExtension().get( 0 ).getValue() ).getValue() );

        Assert.assertEquals( "d1123456789", fhirQuestionnaire.getItem().get( 1 ).getLinkId() );
        Assert.assertEquals( "Value 2", fhirQuestionnaire.getItem().get( 1 ).getText() );
        Assert.assertEquals( 2, fhirQuestionnaire.getItem().get( 1 ).getAnswerOption().size() );
        Assert.assertTrue( fhirQuestionnaire.getItem().get( 1 ).getAnswerOption().get( 0 ).getValue() instanceof Coding );
        Assert.assertEquals( "5", ( (Coding) fhirQuestionnaire.getItem().get( 1 ).getAnswerOption().get( 0 ).getValue() ).getCode() );
        Assert.assertEquals( "Test Value 1", ( (Coding) fhirQuestionnaire.getItem().get( 1 ).getAnswerOption().get( 0 ).getValue() ).getDisplay() );
        Assert.assertEquals( "7", ( (Coding) fhirQuestionnaire.getItem().get( 1 ).getAnswerOption().get( 1 ).getValue() ).getCode() );
        Assert.assertEquals( "Test Value 2", ( (Coding) fhirQuestionnaire.getItem().get( 1 ).getAnswerOption().get( 1 ).getValue() ).getDisplay() );
        Assert.assertTrue( fhirQuestionnaire.getItem().get( 1 ).getRequired() );

        Assert.assertEquals( 1, fhirQuestionnaire.getItem().get( 1 ).getExtension().size() );
        Assert.assertEquals( ValueTypeExtensionUtils.URL, fhirQuestionnaire.getItem().get( 1 ).getExtension().get( 0 ).getUrl() );
        Assert.assertTrue( fhirQuestionnaire.getItem().get( 1 ).getExtension().get( 0 ).getValue() instanceof StringType );
        Assert.assertEquals( "INTEGER", ( (StringType) fhirQuestionnaire.getItem().get( 1 ).getExtension().get( 0 ).getValue() ).getValue() );
    }
}
