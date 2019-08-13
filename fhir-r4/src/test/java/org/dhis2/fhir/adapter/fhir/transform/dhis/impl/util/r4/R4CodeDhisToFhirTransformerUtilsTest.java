package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.r4;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSet;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValues;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecution;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Observation;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Unit tests for {@link R4CodeDhisToFhirTransformerUtils}.
 *
 * @author volsch
 */
public class R4CodeDhisToFhirTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private ScriptExecution scriptExecution;

    @Mock
    private SystemCodeRepository systemCodeRepository;

    @Mock
    private DhisToFhirTransformerContext context;

    @InjectMocks
    private R4CodeDhisToFhirTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void setRuleCodeableConceptNoCodeSet()
    {
        final ProgramStageRule programStageRule = new ProgramStageRule();
        programStageRule.setName( "test rule name" );

        final RuleInfo<?> ruleInfo = new RuleInfo<>( programStageRule, Collections.emptyList() );
        final Observation observation = new Observation();

        Assert.assertTrue( utils.setRuleCodeableConcept( ruleInfo, observation ) );

        Assert.assertEquals( "test rule name", observation.getCode().getText() );
        Assert.assertEquals( 0, observation.getCode().getCoding().size() );
    }

    @Test
    public void setRuleCodeableConcept()
    {
        final CodeSet codeSet = new CodeSet();
        codeSet.setId( UUID.randomUUID() );
        codeSet.setCode( "MY_TEST_CODE_SET" );
        codeSet.setName( "My test code set name" );
        final ProgramStageRule programStageRule = new ProgramStageRule();
        programStageRule.setApplicableCodeSet( codeSet );

        final SystemCodeValues systemCodeValues = new SystemCodeValues( "testText",
            Arrays.asList( new SystemCodeValue( "SNOMED CT", "837823" ),
                new SystemCodeValue( "LOINC", "84878973-8", "Test Text" ) ) );
        Mockito.when( systemCodeRepository.findPreferredByCodeSetId( Mockito.eq( codeSet.getId() ) ) )
            .thenReturn( systemCodeValues );

        final RuleInfo<?> ruleInfo = new RuleInfo<>( programStageRule, Collections.emptyList() );
        final Immunization immunization = new Immunization();

        Assert.assertTrue( utils.setRuleCodeableConcept( ruleInfo, immunization ) );

        CodeableConcept codeableConcept = immunization.getVaccineCode();
        Assert.assertEquals( "testText", codeableConcept.getText() );
        Assert.assertEquals( 3, codeableConcept.getCoding().size() );
        Assert.assertEquals( "SNOMED CT", codeableConcept.getCoding().get( 0 ).getSystem() );
        Assert.assertEquals( "837823", codeableConcept.getCoding().get( 0 ).getCode() );
        Assert.assertNull( codeableConcept.getCoding().get( 0 ).getDisplay() );
        Assert.assertEquals( "LOINC", codeableConcept.getCoding().get( 1 ).getSystem() );
        Assert.assertEquals( "84878973-8", codeableConcept.getCoding().get( 1 ).getCode() );
        Assert.assertEquals( "Test Text", codeableConcept.getCoding().get( 1 ).getDisplay() );
        Assert.assertEquals( "http://www.dhis2.org/dhis2-fhir-adapter/systems/code-set", codeableConcept.getCoding().get( 2 ).getSystem() );
        Assert.assertEquals( "MY_TEST_CODE_SET", codeableConcept.getCoding().get( 2 ).getCode() );
        Assert.assertEquals( "My test code set name", codeableConcept.getCoding().get( 2 ).getDisplay() );
    }

    @Test
    public void getRuleCodeableConceptNull()
    {
        final ProgramStageRule programStageRule = new ProgramStageRule();

        final Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put( TransformerUtils.RULE_VAR_NAME, new RuleInfo<>( programStageRule, Collections.emptyList() ) );

        Mockito.when( scriptExecutionContext.getScriptExecution() ).thenReturn( scriptExecution );
        Mockito.when( scriptExecution.getContextVariables() ).thenReturn( contextVariables );

        Assert.assertNull( utils.getRuleCodeableConcept() );
    }

    @Test
    public void getRuleCodeableConcept()
    {
        final CodeSet codeSet = new CodeSet();
        codeSet.setId( UUID.randomUUID() );
        final ProgramStageRule programStageRule = new ProgramStageRule();
        programStageRule.setApplicableCodeSet( codeSet );

        final Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put( TransformerUtils.RULE_VAR_NAME, new RuleInfo<>( programStageRule, Collections.emptyList() ) );

        final SystemCodeValues systemCodeValues = new SystemCodeValues( "testText",
            Arrays.asList( new SystemCodeValue( "SNOMED CT", "837823" ),
                new SystemCodeValue( "LOINC", "84878973-8", "Test Text" ) ) );

        Mockito.when( scriptExecutionContext.getScriptExecution() ).thenReturn( scriptExecution );
        Mockito.when( scriptExecution.getContextVariables() ).thenReturn( contextVariables );
        Mockito.when( systemCodeRepository.findPreferredByCodeSetId( Mockito.eq( codeSet.getId() ) ) )
            .thenReturn( systemCodeValues );

        final CodeableConcept codeableConcept = (CodeableConcept) Objects.requireNonNull( utils.getRuleCodeableConcept() );
        Assert.assertEquals( "testText", codeableConcept.getText() );
        Assert.assertEquals( 2, codeableConcept.getCoding().size() );
        Assert.assertEquals( "SNOMED CT", codeableConcept.getCoding().get( 0 ).getSystem() );
        Assert.assertEquals( "837823", codeableConcept.getCoding().get( 0 ).getCode() );
        Assert.assertNull( codeableConcept.getCoding().get( 0 ).getDisplay() );
        Assert.assertEquals( "LOINC", codeableConcept.getCoding().get( 1 ).getSystem() );
        Assert.assertEquals( "84878973-8", codeableConcept.getCoding().get( 1 ).getCode() );
        Assert.assertEquals( "Test Text", codeableConcept.getCoding().get( 1 ).getDisplay() );
    }

    @Test
    public void getByMappedCodeNull()
    {
        Assert.assertNull( utils.getByMappedCode( null, FhirResourceType.ORGANIZATION ) );
    }

    @Test
    public void getByMappedCode()
    {
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "TestSystem", null, null, null, false );
        final SystemCode systemCode = new SystemCode();
        systemCode.setSystemCode( "837232" );

        final Map<String, Object> variables = new HashMap<>();
        variables.put( ScriptVariable.CONTEXT.getVariableName(), context );

        final SystemCodeValues systemCodeValues = new SystemCodeValues( "testText",
            Arrays.asList( new SystemCodeValue( "SNOMED CT", "837823" ),
                new SystemCodeValue( "LOINC", "84878973-8", "Test Text" ) ) );

        Mockito.when( scriptExecutionContext.getScriptExecution() ).thenReturn( scriptExecution );
        Mockito.when( scriptExecution.getVariables() ).thenReturn( variables );
        Mockito.when( context.getOptionalResourceSystem( Mockito.eq( FhirResourceType.ORGANIZATION ) ) )
            .thenReturn( Optional.of( resourceSystem ) );
        Mockito.when( systemCodeRepository.findOneByMappedCode( Mockito.eq( "TestSystem" ), Mockito.eq( "TEST_1" ) ) )
            .thenReturn( Optional.of( systemCode ) );

        Assert.assertEquals( "837232", utils.getByMappedCode( "TEST_1", FhirResourceType.ORGANIZATION ) );
    }

    @Test
    public void getByMappedCodeNotFound()
    {
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "TestSystem", null, null, null, false );

        final Map<String, Object> variables = new HashMap<>();
        variables.put( ScriptVariable.CONTEXT.getVariableName(), context );

        final SystemCodeValues systemCodeValues = new SystemCodeValues( "testText",
            Arrays.asList( new SystemCodeValue( "SNOMED CT", "837823" ),
                new SystemCodeValue( "LOINC", "84878973-8", "Test Text" ) ) );

        Mockito.when( scriptExecutionContext.getScriptExecution() ).thenReturn( scriptExecution );
        Mockito.when( scriptExecution.getVariables() ).thenReturn( variables );
        Mockito.when( context.getOptionalResourceSystem( Mockito.eq( FhirResourceType.ORGANIZATION ) ) )
            .thenReturn( Optional.of( resourceSystem ) );
        Mockito.when( systemCodeRepository.findOneByMappedCode( Mockito.eq( "TestSystem" ), Mockito.eq( "TEST_1" ) ) )
            .thenReturn( Optional.empty() );

        Assert.assertNull( utils.getByMappedCode( "TEST_1", FhirResourceType.ORGANIZATION ) );
    }

    @Test
    public void getCodeWithoutPrefixPrefix()
    {
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "TestSystem", "SE_", null, null, false );

        final Map<String, Object> variables = new HashMap<>();
        variables.put( ScriptVariable.CONTEXT.getVariableName(), context );

        Mockito.when( scriptExecutionContext.getScriptExecution() ).thenReturn( scriptExecution );
        Mockito.when( scriptExecution.getVariables() ).thenReturn( variables );
        Mockito.when( context.getOptionalResourceSystem( Mockito.eq( FhirResourceType.ORGANIZATION ) ) )
            .thenReturn( Optional.of( resourceSystem ) );

        Assert.assertEquals( "TEST3", utils.getCodeWithoutPrefix( "SE_TEST3", FhirResourceType.ORGANIZATION ) );
    }

    @Test
    public void getCodeWithoutPrefixNoPrefix()
    {
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "TestSystem", "SE_", null, null, false );

        final Map<String, Object> variables = new HashMap<>();
        variables.put( ScriptVariable.CONTEXT.getVariableName(), context );

        Mockito.when( scriptExecutionContext.getScriptExecution() ).thenReturn( scriptExecution );
        Mockito.when( scriptExecution.getVariables() ).thenReturn( variables );
        Mockito.when( context.getOptionalResourceSystem( Mockito.eq( FhirResourceType.ORGANIZATION ) ) )
            .thenReturn( Optional.of( resourceSystem ) );

        Assert.assertEquals( "TEST3", utils.getCodeWithoutPrefix( "TEST3", FhirResourceType.ORGANIZATION ) );
    }

    @Test
    public void getCodeWithoutPrefixNoPrefixSystem()
    {
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "TestSystem", null, null, null, false );

        final Map<String, Object> variables = new HashMap<>();
        variables.put( ScriptVariable.CONTEXT.getVariableName(), context );

        Mockito.when( scriptExecutionContext.getScriptExecution() ).thenReturn( scriptExecution );
        Mockito.when( scriptExecution.getVariables() ).thenReturn( variables );
        Mockito.when( context.getOptionalResourceSystem( Mockito.eq( FhirResourceType.ORGANIZATION ) ) )
            .thenReturn( Optional.of( resourceSystem ) );

        Assert.assertEquals( "SE_TEST3", utils.getCodeWithoutPrefix( "SE_TEST3", FhirResourceType.ORGANIZATION ) );
    }

    @Test
    public void getCodeWithoutPrefixNull()
    {
        Assert.assertNull( utils.getCodeWithoutPrefix( null, FhirResourceType.ORGANIZATION ) );
    }
}