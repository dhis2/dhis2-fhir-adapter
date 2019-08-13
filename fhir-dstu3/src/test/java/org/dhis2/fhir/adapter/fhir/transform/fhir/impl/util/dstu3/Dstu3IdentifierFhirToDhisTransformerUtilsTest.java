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

import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecution;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.ReferenceFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.util.FhirIdentifierUtils;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Map;
import java.util.Optional;

/**
 * Unit tests for {@link Dstu3IdentifierFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class Dstu3IdentifierFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private ReferenceFhirToDhisTransformerUtils referenceFhirToDhisTransformerUtils;

    @Mock
    private FhirToDhisTransformerContext context;

    @Mock
    private FhirRequest request;

    @Mock
    private ScriptExecution scriptExecution;

    @Mock
    private Map<String, Object> variables;

    private final FhirIdentifierUtils fhirIdentifierUtils = new FhirIdentifierUtils();

    private Dstu3IdentifierFhirToDhisTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void before()
    {
        utils = new Dstu3IdentifierFhirToDhisTransformerUtils( scriptExecutionContext, fhirIdentifierUtils, referenceFhirToDhisTransformerUtils );
    }

    @Test
    public void getReferenceIdentifierWithSystemNull()
    {
        Assert.assertNull( utils.getReferenceIdentifier( null, "PATIENT", "http://test.com" ) );
    }

    @Test
    public void getReferenceIdentifierWithSystem()
    {
        final Patient patient = new Patient();
        patient.addIdentifier().setSystem( "http://test.com" ).setValue( "ABC_123" );
        final Reference reference = new Reference( new IdType( "Patient", "5671" ) );
        Mockito.doAnswer( invocation -> {
            ((Reference) invocation.getArgument( 0 )).setResource( patient );
            return null;
        } ).when( referenceFhirToDhisTransformerUtils ).initReference( Mockito.same( reference ), Mockito.eq( "PATIENT" ) );
        Assert.assertEquals( "ABC_123", utils.getReferenceIdentifier( reference, "PATIENT", "http://test.com" ) );
        Mockito.verify( referenceFhirToDhisTransformerUtils ).initReference( Mockito.same( reference ), Mockito.eq( "PATIENT" ) );
    }

    @Test
    public void getReferenceIdentifierWithDifferentSystem()
    {
        final Patient patient = new Patient();
        patient.addIdentifier().setSystem( "http://test.com" ).setValue( "ABC_123" );
        final Reference reference = new Reference( new IdType( "Patient", "5671" ) );
        Mockito.doAnswer( invocation -> {
            ((Reference) invocation.getArgument( 0 )).setResource( patient );
            return null;
        } ).when( referenceFhirToDhisTransformerUtils ).initReference( Mockito.same( reference ), Mockito.eq( "PATIENT" ) );
        Assert.assertNull( utils.getReferenceIdentifier( reference, "PATIENT", "http://test2.com" ) );
        Mockito.verify( referenceFhirToDhisTransformerUtils ).initReference( Mockito.same( reference ), Mockito.eq( "PATIENT" ) );
    }

    @Test
    public void getReferenceIdentifierWithDefaultSystem()
    {
        final Patient patient = new Patient();
        patient.addIdentifier().setSystem( "http://test.com" ).setValue( "ABC_123" );

        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.PATIENT, "http://test.com", null, null, null, false );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.PATIENT );

        final Reference reference = new Reference( new IdType( "Patient", "5671" ) );
        Mockito.doAnswer( invocation -> {
            ((Reference) invocation.getArgument( 0 )).setResource( patient );
            return null;
        } ).when( referenceFhirToDhisTransformerUtils ).initReference( Mockito.same( reference ), Mockito.eq( "PATIENT" ) );
        Assert.assertEquals( "ABC_123", utils.getReferenceIdentifier( reference, "PATIENT" ) );
        Mockito.verify( referenceFhirToDhisTransformerUtils ).initReference( Mockito.same( reference ), Mockito.eq( "PATIENT" ) );
    }

    @Test
    public void getResourceIdentifierWithDefaultSystem()
    {
        final Patient patient = new Patient();
        patient.addIdentifier().setSystem( "http://test.com" ).setValue( "ABC_123" );

        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.PATIENT, "http://test.com", null, null, null, false );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.PATIENT );

        Assert.assertEquals( "ABC_123", utils.getResourceIdentifier( patient, "PATIENT" ) );
    }

    @Test
    public void getIncludedReferenceIdentifierWithSystem()
    {
        final Patient patient = new Patient();
        patient.addIdentifier().setSystem( "http://test.com" ).setValue( "ABC_123" );

        final Reference reference = new Reference( new IdType( "Patient", "5671" ) ).setIdentifier( new Identifier().setSystem( "http://test.com" ).setValue( "ABC_123" ) );
        Assert.assertEquals( "ABC_123", utils.getReferenceIdentifier( reference, "PATIENT", "http://test.com" ) );
        Mockito.verifyZeroInteractions( referenceFhirToDhisTransformerUtils );
    }

    @Test
    public void getIncludedReferenceIdentifierWithDifferentSystem()
    {
        final Patient patient = new Patient();
        patient.addIdentifier().setSystem( "http://test.com" ).setValue( "ABC_123" );

        final Reference reference = new Reference( new IdType( "Patient", "5671" ) ).setIdentifier( new Identifier().setSystem( "http://test.com" ).setValue( "ABC_123" ) );
        Assert.assertNull( utils.getReferenceIdentifier( reference, "PATIENT", "http://test2.com" ) );
    }

    @Test
    public void getIncludedReferenceIdentifierWithDefaultSystem()
    {
        final Patient patient = new Patient();
        patient.addIdentifier().setSystem( "http://test.com" ).setValue( "ABC_123" );

        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.PATIENT, "http://test.com", null, null, null, false );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.PATIENT );

        final Reference reference = new Reference( new IdType( "Patient", "5671" ) ).setIdentifier( new Identifier().setSystem( "http://test.com" ).setValue( "ABC_123" ) );
        Assert.assertEquals( "ABC_123", utils.getReferenceIdentifier( reference, "PATIENT" ) );
        Mockito.verifyZeroInteractions( referenceFhirToDhisTransformerUtils );
    }
}