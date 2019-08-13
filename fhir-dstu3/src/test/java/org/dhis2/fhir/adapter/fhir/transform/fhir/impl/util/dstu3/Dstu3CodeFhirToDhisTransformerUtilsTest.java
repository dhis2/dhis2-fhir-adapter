package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.dstu3;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecution;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Observation;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Unit tests for {@link Dstu3CodeFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class Dstu3CodeFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private CodeRepository codeRepository;

    @Mock
    private SystemCodeRepository systemCodeRepository;

    @Mock
    private FhirToDhisTransformerContext context;

    @Mock
    private FhirRequest request;

    @Mock
    private ScriptExecution scriptExecution;

    @Mock
    private Map<String, Object> variables;

    @InjectMocks
    private Dstu3CodeFhirToDhisTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void getSystemCodeValuesNull()
    {
        Assert.assertTrue( utils.getSystemCodeValues( null ).isEmpty() );
    }

    @Test
    public void getCodeSystemNull()
    {
        Assert.assertNull( utils.getCode( null, "http://test.com/1" ) );
    }

    @Test
    public void getCodeNotFound()
    {
        final CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding( new Coding().setSystem( "http://test.com/1" ).setCode( "C1" ) )
            .addCoding( new Coding().setSystem( "http://test.com/2" ).setCode( "C2" ) );
        Assert.assertNull( utils.getCode( codeableConcept, "http://test.com/3" ) );
    }

    @Test
    public void getCode()
    {
        final CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding( new Coding().setSystem( "http://test.com/1" ).setCode( "C1" ) )
            .addCoding( new Coding().setSystem( "http://test.com/2" ).setCode( "C2" ) );
        Assert.assertEquals( "C2", utils.getCode( codeableConcept, "http://test.com/2" ) );
    }

    @Test
    public void getSystemCodeValues()
    {
        final CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding( new Coding().setSystem( "http://test.com/1" ).setCode( "C1" ) )
            .addCoding( new Coding().setSystem( "http://test.com/2" ).setCode( "C2" ) );
        final List<SystemCodeValue> systemCodeValues = utils.getSystemCodeValues( codeableConcept );
        Assert.assertEquals( new SystemCodeValue( "http://test.com/1", "C1" ), systemCodeValues.get( 0 ) );
        Assert.assertEquals( new SystemCodeValue( "http://test.com/2", "C2" ), systemCodeValues.get( 1 ) );
    }

    @Test
    public void containsMappingCodeNull()
    {
        Assert.assertFalse( utils.containsMappingCode( null, new Object[0] ) );
    }

    @Test
    public void containsMappingCodeEmptyArray()
    {
        Assert.assertFalse( utils.containsMappingCode( new CodeableConcept().addCoding(), new Object[0] ) );
    }

    @Test
    public void containsMappingCodeFound()
    {
        final System system1 = new System();
        system1.setSystemUri( "http://test.com/3" );
        final SystemCode systemCode1 = new SystemCode();
        systemCode1.setSystemCode( "C3" );
        systemCode1.setSystem( system1 );
        systemCode1.setCode( new Code() );

        final System system2 = new System();
        system2.setSystemUri( "http://test.com/2" );
        final SystemCode systemCode2 = new SystemCode();
        systemCode2.setSystemCode( "C2" );
        systemCode2.setSystem( system2 );
        systemCode2.setCode( new Code() );

        Mockito.doReturn( Arrays.asList( systemCode1, systemCode2 ) ).when( systemCodeRepository )
            .findAllByCodes( Mockito.eq( Arrays.asList( "X_20", "X_21" ) ) );
        Assert.assertTrue( utils.containsMappingCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://test.com/1" ).setCode( "C1" ) )
            .addCoding( new Coding().setSystem( "http://test.com/2" ).setCode( "C2" ) ), new Object[]{ "X_20", "X_21" } ) );
    }

    @Test
    public void containsMappingCodeNotFound()
    {
        final System system1 = new System();
        system1.setSystemUri( "http://test.com/3" );
        final SystemCode systemCode1 = new SystemCode();
        systemCode1.setSystemCode( "C3" );
        systemCode1.setSystem( system1 );
        systemCode1.setCode( new Code() );

        final System system2 = new System();
        system2.setSystemUri( "http://test.com/4" );
        final SystemCode systemCode2 = new SystemCode();
        systemCode2.setSystemCode( "C4" );
        systemCode2.setSystem( system2 );
        systemCode2.setCode( new Code() );

        Mockito.doReturn( Arrays.asList( systemCode1, systemCode2 ) ).when( systemCodeRepository )
            .findAllByCodes( Mockito.eq( Arrays.asList( "X_20", "X_21" ) ) );
        Assert.assertFalse( utils.containsMappingCode( new CodeableConcept().addCoding( new Coding().setSystem( "http://test.com/1" ).setCode( "C1" ) )
            .addCoding( new Coding().setSystem( "http://test.com/2" ).setCode( "C2" ) ), new Object[]{ "X_20", "X_21" } ) );
    }

    @Test
    public void containsCodeNull()
    {
        Assert.assertFalse( utils.containsCode( null, "http://test.com/2", "C2" ) );
    }

    @Test
    public void containsCode()
    {
        final CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding( new Coding().setSystem( "http://test.com/1" ).setCode( "C1" ) )
            .addCoding( new Coding().setSystem( "http://test.com/2" ).setCode( "C2" ) );
        Assert.assertTrue( utils.containsCode( codeableConcept, "http://test.com/2", "C2" ) );
    }

    @Test
    public void containsCodeNot()
    {
        final CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding( new Coding().setSystem( "http://test.com/1" ).setCode( "C1" ) )
            .addCoding( new Coding().setSystem( "http://test.com/2" ).setCode( "C2" ) );
        Assert.assertFalse( utils.containsCode( codeableConcept, "http://test.com/3", "C3" ) );
    }

    @Test
    public void containsAnyCode()
    {
        final CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding( new Coding().setSystem( "http://test.com/1" ).setCode( "C1" ) )
            .addCoding( new Coding().setSystem( "http://test.com/2" ).setCode( "C2" ) );
        final List<SystemCodeValue> systemCodeValues = Arrays.asList(
            new SystemCodeValue( "http://test.com/3", "C3" ),
            new SystemCodeValue( "http://test.com/2", "C2" )
        );
        Assert.assertTrue( utils.containsAnyCode( codeableConcept, systemCodeValues ) );
    }

    @Test
    public void containsAnyCodeNot()
    {
        final CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding( new Coding().setSystem( "http://test.com/1" ).setCode( "C1" ) )
            .addCoding( new Coding().setSystem( "http://test.com/2" ).setCode( "C2" ) );
        final List<SystemCodeValue> systemCodeValues = Arrays.asList(
            new SystemCodeValue( "http://test.com/3", "C3" ),
            new SystemCodeValue( "http://test.com/4", "C4" )
        );
        Assert.assertFalse( utils.containsAnyCode( codeableConcept, systemCodeValues ) );
    }

    @Test
    public void containsAnyCodeConceptNull()
    {
        final List<SystemCodeValue> systemCodeValues = Arrays.asList(
            new SystemCodeValue( "http://test.com/3", "C3" ),
            new SystemCodeValue( "http://test.com/4", "C4" )
        );
        Assert.assertFalse( utils.containsAnyCode( null, systemCodeValues ) );
    }

    @Test
    public void containsAnyCodeValuesNull()
    {
        final CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding( new Coding().setSystem( "http://test.com/1" ).setCode( "C1" ) )
            .addCoding( new Coding().setSystem( "http://test.com/2" ).setCode( "C2" ) );
        Assert.assertFalse( utils.containsAnyCode( codeableConcept, null ) );
    }

    @Test
    public void getSystemCodeValuesByMappingCodesNull()
    {
        Assert.assertTrue( utils.getSystemCodeValuesByMappingCodes( null ).isEmpty() );
    }

    @Test
    public void getSystemCodeValuesByMappingCodesEmpty()
    {
        Assert.assertTrue( utils.getSystemCodeValuesByMappingCodes( new Object[0] ).isEmpty() );
    }

    @Test
    public void getSystemCodeValuesByMappingCodes()
    {
        final System system1 = new System();
        system1.setSystemUri( "http://test.com/3" );
        final Code code1 = new Code();
        code1.setCode( "X_20" );
        final SystemCode systemCode1 = new SystemCode();
        systemCode1.setSystemCode( "C3" );
        systemCode1.setSystem( system1 );
        systemCode1.setCode( code1 );

        final System system2 = new System();
        system2.setSystemUri( "http://test.com/4" );
        final Code code2 = new Code();
        code2.setCode( "X_21" );
        final SystemCode systemCode2 = new SystemCode();
        systemCode2.setSystemCode( "C4" );
        systemCode2.setSystem( system2 );
        systemCode2.setCode( code2 );

        final System system3 = new System();
        system2.setSystemUri( "http://test.com/5" );
        final Code code3 = new Code();
        code3.setCode( "X_21" );
        final SystemCode systemCode3 = new SystemCode();
        systemCode3.setSystemCode( "C5" );
        systemCode3.setSystem( system2 );
        systemCode3.setCode( code3 );

        Mockito.doReturn( Arrays.asList( systemCode1, systemCode2, systemCode3 ) ).when( systemCodeRepository )
            .findAllByCodes( Mockito.eq( Arrays.asList( "X_20", "X_21" ) ) );
        final Map<String, List<SystemCodeValue>> result =
            utils.getSystemCodeValuesByMappingCodes( new Object[]{ "X_20", "X_21" } );

        Assert.assertEquals( 2, result.size() );
        Assert.assertNotNull( result.get( "X_20" ) );
        Assert.assertEquals( 1, result.get( "X_20" ).size() );
        Assert.assertEquals( systemCode1.getCalculatedSystemCodeValue(), result.get( "X_20" ).get( 0 ) );
        Assert.assertNotNull( result.get( "X_21" ) );
        Assert.assertEquals( 2, result.get( "X_21" ).size() );
        Assert.assertEquals( systemCode2.getCalculatedSystemCodeValue(), result.get( "X_21" ).get( 0 ) );
        Assert.assertEquals( systemCode3.getCalculatedSystemCodeValue(), result.get( "X_21" ).get( 1 ) );
    }

    @Test
    public void getResourceCodes()
    {
        final CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding( new Coding().setSystem( "http://test.com/1" ).setCode( "C1" ) )
            .addCoding( new Coding().setSystem( "http://test.com/2" ).setCode( "C2" ) );
        final Observation observation = new Observation();
        observation.setCode( codeableConcept );
        final List<SystemCodeValue> systemCodeValues = utils.getResourceCodes( observation );
        Assert.assertNotNull( systemCodeValues );
        Assert.assertEquals( 2, systemCodeValues.size() );
        Assert.assertEquals( new SystemCodeValue( "http://test.com/1", "C1" ), systemCodeValues.get( 0 ) );
        Assert.assertEquals( new SystemCodeValue( "http://test.com/2", "C2" ), systemCodeValues.get( 1 ) );
    }

    @Test
    public void getMappedCodeNotFound()
    {
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "http://test.com/2", null, null, null, false );

        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.ORGANIZATION );

        Assert.assertNull( utils.getMappedCode( "TEST_1", "ORGANIZATION" ) );
    }

    @Test
    public void getMappedCodeFound()
    {
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "http://test.com/2", null, null, null, false );

        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.ORGANIZATION );

        final Code code = new Code();
        code.setCode( "ABC_1" );
        Mockito.doReturn( Collections.singletonList( code ) ).when( codeRepository ).findAllBySystemCodes( Mockito.eq( Collections.singleton( "http://test.com/2|TEST_1" ) ) );

        Assert.assertEquals( "ABC_1", utils.getMappedCode( "TEST_1", "ORGANIZATION" ) );
    }

    @Test
    public void getMappedCodeFoundMapped()
    {
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "http://test.com/2", null, null, null, false );

        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.ORGANIZATION );

        final Code code = new Code();
        code.setCode( "ABC_1" );
        code.setMappedCode( "CBA_1" );
        Mockito.doReturn( Collections.singletonList( code ) ).when( codeRepository ).findAllBySystemCodes( Mockito.eq( Collections.singleton( "http://test.com/2|TEST_1" ) ) );

        Assert.assertEquals( "CBA_1", utils.getMappedCode( "TEST_1", "ORGANIZATION" ) );
    }
}