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

import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecution;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.ReferenceFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.util.FhirIdentifierUtils;
import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collection;
import java.util.Collections;

/**
 * Unit tests for {@link Dstu3FhirResourceFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class Dstu3FhirResourceFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private ScriptExecution scriptExecution;

    @Mock
    private ReferenceFhirToDhisTransformerUtils referenceUtils;

    @Mock
    private SystemCodeRepository systemCodeRepository;

    private final FhirIdentifierUtils fhirIdentifierUtils = new FhirIdentifierUtils();

    @Mock
    private FhirToDhisTransformerContext context;

    @Mock
    private FhirRequest request;

    @InjectMocks
    private Dstu3FhirResourceFhirToDhisTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule().silent();

    @Before
    public void setUp()
    {
        utils = new Dstu3FhirResourceFhirToDhisTransformerUtils( scriptExecutionContext, referenceUtils, systemCodeRepository, fhirIdentifierUtils );
    }

    @Test
    public void getIdentifierReferenceNull()
    {
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();
        Assert.assertNull( utils.getIdentifiedReference( null, "Patient" ) );
    }

    @Test
    public void getCanonicalReferenceNull()
    {
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();
        Assert.assertNull( utils.getCanonicalAdapterReference( null, "MEASURE" ) );
    }

    @Test
    public void getCanonicalAdapterReferenceFqDhisFhirId()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();
        Mockito.doReturn( "1234" ).when( context ).extractDhisId( Mockito.eq( "1234" ) );

        final org.dhis2.fhir.adapter.dhis.model.Reference adapterReference = utils.getCanonicalAdapterReference( new Reference( "Patient/1234" ), "Patient" );

        Assert.assertNotNull( adapterReference );
        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "1234", ReferenceType.ID ), adapterReference );
    }

    @Test
    public void getCanonicalAdapterReferenceNonFqDhisFhirId()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();
        Mockito.doReturn( "1234" ).when( context ).extractDhisId( Mockito.eq( "1234" ) );

        final org.dhis2.fhir.adapter.dhis.model.Reference adapterReference = utils.getCanonicalAdapterReference( new Reference( "1234" ), "Patient" );

        Assert.assertNotNull( adapterReference );
        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "1234", ReferenceType.ID ), adapterReference );
    }

    @Test
    public void getIdentifierReferenceDhisFhirIdOnly()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();

        final Reference reference = (Reference) utils.getIdentifiedReference( new Reference().setReference( "Patient/1234" ), "Patient" );

        Assert.assertNotNull( reference );
        Assert.assertTrue( new Reference( "Patient/1234" ).equalsDeep( reference ) );
        Assert.assertNull( reference.getResource() );
    }

    @Test
    public void getIdentifierReferenceDhisFhirId()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();

        final Reference reference = (Reference) utils.getIdentifiedReference( new Reference().setReference( "Patient/1234" )
            .setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "5678" ) ), "Patient" );

        Assert.assertNotNull( reference );
        Assert.assertTrue( new Reference( "Patient/1234" ).setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "5678" ) ).equalsDeep( reference ) );
        Assert.assertNull( reference.getResource() );
    }

    @Test
    public void getIdentifierReferenceDhisFhirIdUri()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();

        final Reference reference = (Reference) utils.getIdentifiedReference( new Reference()
            .setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "5678" ) ), "Patient" );

        Assert.assertNotNull( reference );
        Assert.assertNull( reference.getReference() );
        Assert.assertTrue( new Reference().setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "5678" ) ).equalsDeep( reference ) );
        Assert.assertNull( reference.getResource() );
    }

    @Test
    public void getIdentifierReferenceDhisFhirIdCodeUri()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();

        final Reference reference = (Reference) utils.getIdentifiedReference( new Reference()
            .setIdentifier( new Identifier().setSystem( "National ID" ).setValue( "5678" ) ), "Patient" );

        Assert.assertNotNull( reference );
        Assert.assertNull( reference.getReference() );
        Assert.assertTrue( new Reference().setIdentifier( new Identifier().setSystem( "National ID" ).setValue( "5678" ) ).equalsDeep( reference ) );
        Assert.assertNull( reference.getResource() );
    }

    @Test
    public void getIdentifierReferenceNonDhisFhirIdNull()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( false ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();

        final Reference reference = (Reference) utils.getIdentifiedReference( new Reference().setReference( "Patient/1234" ), "Patient" );

        Assert.assertNull( reference );
    }

    @Test
    public void getIdentifierReferenceNonDhisFhirId()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( false ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();

        final Reference reference = (Reference) utils.getIdentifiedReference( new Reference().setReference( "Patient/1234" )
            .setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "5678" ) ), "Patient" );

        Assert.assertNotNull( reference );
        Assert.assertTrue( new Reference( "Patient/1234" ).setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "5678" ) ).equalsDeep( reference ) );
        Assert.assertNull( reference.getResource() );
    }

    @SuppressWarnings( "unchecked" )
    @Test
    public void getIdentifierReferenceOtherSystem()
    {
        final System otherSystem = new System();
        otherSystem.setSystemUri( "National ID" );

        final SystemCode systemCode = new SystemCode();
        systemCode.setSystem( otherSystem );
        systemCode.setSystemCode( "4711" );

        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( false ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();
        Mockito.doReturn( Collections.singletonList( systemCode ) ).when( systemCodeRepository ).findAllInternalBySystemCodeValues(
            (Collection<String>) MockitoHamcrest.argThat( Matchers.containsInAnyOrder( System.DHIS2_FHIR_IDENTIFIER_URI, "National ID" ) ),
            (Collection<String>) MockitoHamcrest.argThat( Matchers.containsInAnyOrder( "otherSystem|5678" ) ) );

        final Reference reference = (Reference) utils.getIdentifiedReference( new Reference()
            .setIdentifier( new Identifier().setSystem( "otherSystem" ).setValue( "5678" ) ), "Patient" );

        Assert.assertNotNull( reference );
        Assert.assertTrue( new Reference().setIdentifier( new Identifier().setSystem( "National ID" ).setValue( "4711" ) ).equalsDeep( reference ) );
        Assert.assertNull( reference.getResource() );
    }

    @Test
    public void getIdentifierReferenceNonDhisFhirIdUri()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( false ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();

        final Reference reference = (Reference) utils.getIdentifiedReference( new Reference()
            .setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "5678" ) ), "Patient" );

        Assert.assertNotNull( reference );
        Assert.assertNull( reference.getReference() );
        Assert.assertTrue( new Reference().setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "5678" ) ).equalsDeep( reference ) );
        Assert.assertNull( reference.getResource() );
    }

    @Test
    public void getIdentifierReferenceNonDhisFhirIdCodeUri()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( false ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();

        final Reference reference = (Reference) utils.getIdentifiedReference( new Reference()
            .setIdentifier( new Identifier().setSystem( "National ID" ).setValue( "5678" ) ), "Patient" );

        Assert.assertNotNull( reference );
        Assert.assertNull( reference.getReference() );
        Assert.assertTrue( new Reference().setIdentifier( new Identifier().setSystem( "National ID" ).setValue( "5678" ) ).equalsDeep( reference ) );
        Assert.assertNull( reference.getResource() );
    }

    @Test
    public void getIdentifierResourceDhisFhirIdCode()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();

        final Resource resource = (Resource) utils.getIdentifiedResource( new Reference().setReference( "Patient/1234" )
            .setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "5678" ) ), "Patient" );

        Assert.assertNotNull( resource );
        Assert.assertTrue( resource instanceof Patient );
        final Patient patient = (Patient) resource;

        Assert.assertTrue( new IdType( "Patient/1234" ).equalsDeep( resource.getIdElement() ) );
        Assert.assertEquals( 1, patient.getIdentifier().size() );
        Assert.assertTrue( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "5678" ).equalsDeep( patient.getIdentifier().get( 0 ) ) );
    }

    @Test
    public void getIdentifierResourceNonDhisFhirIdCode()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( false ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();

        final Resource resource = (Resource) utils.getIdentifiedResource( new Reference().setReference( "Patient/1234" )
            .setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "5678" ) ), "Patient" );

        Assert.assertNotNull( resource );
        Assert.assertTrue( resource instanceof Patient );
        final Patient patient = (Patient) resource;

        Assert.assertTrue( new IdType( "Patient/1234" ).equalsDeep( resource.getIdElement() ) );
        Assert.assertEquals( 1, patient.getIdentifier().size() );
        Assert.assertTrue( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "5678" ).equalsDeep( patient.getIdentifier().get( 0 ) ) );
    }

    @Test
    public void getIdentifierResourceNonDhisFhirIdCodeLoaded()
    {
        final Reference reference = new Reference().setReference( "Patient/1234" );
        final Patient returnedPatient = new Patient();

        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( false ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();
        Mockito.doReturn( returnedPatient ).when( referenceUtils ).getResource( Mockito.same( reference ), Mockito.eq( "Patient" ) );

        final Resource resource = (Resource) utils.getIdentifiedResource( reference, "Patient" );

        Assert.assertNotNull( resource );
        Assert.assertTrue( resource instanceof Patient );
        final Patient patient = (Patient) resource;

        Assert.assertSame( returnedPatient, patient );
    }

    @Test
    public void getAdapterReferenceDhisFhirId()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();
        Mockito.doReturn( "12345678901" ).when( context ).extractDhisId( Mockito.eq( "12345678901" ) );

        final org.dhis2.fhir.adapter.dhis.model.Reference adapterReference = utils.getAdapterReference( new Reference().setReference( "Patient/12345678901" )
            .setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "56781234567" ) ), "Patient" );

        Assert.assertNotNull( adapterReference );
        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "12345678901", ReferenceType.ID ), adapterReference );
    }

    @Test
    public void getAdapterReferenceDhisFhirIdentifier()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();
        Mockito.doReturn( "abcdef123456" ).when( context ).extractDhisId( "l2C0N9u2e5q" );

        final org.dhis2.fhir.adapter.dhis.model.Reference adapterReference = utils.getAdapterReference( new Reference()
            .setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "l2C0N9u2e5q" ) ), "Patient" );

        Assert.assertNotNull( adapterReference );
        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "abcdef123456", ReferenceType.ID ), adapterReference );
    }

    @Test
    public void getAdapterReferenceDhisFhirIdentifierCode()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();

        final org.dhis2.fhir.adapter.dhis.model.Reference adapterReference = utils.getAdapterReference( new Reference()
            .setIdentifier( new Identifier().setSystem( "National ID" ).setValue( "5678" ) ), "Patient" );

        Assert.assertNotNull( adapterReference );
        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "5678", ReferenceType.CODE ), adapterReference );
    }

    @Test
    public void getAdapterReferenceDhisFhirIdCode()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID", null, null, null, true ) )
            .when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();

        final org.dhis2.fhir.adapter.dhis.model.Reference adapterReference = utils.getAdapterReference( new Reference( "Patient/5678" ), "PATIENT" );

        Assert.assertNotNull( adapterReference );
        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "5678", ReferenceType.CODE ), adapterReference );
    }

    @Test
    public void getAdapterReferenceDhisFhirIdOnly()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();
        Mockito.doReturn( "1234" ).when( context ).extractDhisId( Mockito.eq( "1234" ) );

        final org.dhis2.fhir.adapter.dhis.model.Reference adapterReference = utils.getAdapterReference( new Reference().setReference( "Patient/1234" ), "Patient" );

        Assert.assertNotNull( adapterReference );
        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "1234", ReferenceType.ID ), adapterReference );
    }

    @Test
    public void getAdapterReferenceResourceNonDhisFhirIdCode()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( false ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();
        Mockito.doReturn( "12345678912" ).when( context ).extractDhisId( "12345678912" );

        final org.dhis2.fhir.adapter.dhis.model.Reference adapterReference = utils.getAdapterReference( new Reference().setReference( "Patient/1234" )
            .setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "12345678912" ) ), "Patient" );

        Assert.assertNotNull( adapterReference );
        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "12345678912", ReferenceType.ID ), adapterReference );
    }

    @Test
    public void getResourceAdapterReferenceDhisFhirId()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();
        Mockito.doReturn( "1234" ).when( context ).extractDhisId( Mockito.eq( "1234" ) );

        final Patient patient = new Patient();
        patient.setIdElement( new IdType( "Patient/1234" ) );
        patient.addIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "5678" ) );

        final org.dhis2.fhir.adapter.dhis.model.Reference adapterReference = utils.getResourceAdapterReference( patient, "Patient" );

        Assert.assertNotNull( adapterReference );
        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "1234", ReferenceType.ID ), adapterReference );
    }

    @Test
    public void getResourceAdapterReferenceDhisFhirIdentifier()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();
        Mockito.doReturn( "abcdef123456" ).when( context ).extractDhisId( "l2C0N9u2e5q" );

        final Patient patient = new Patient();
        patient.addIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "l2C0N9u2e5q" ) );

        final org.dhis2.fhir.adapter.dhis.model.Reference adapterReference = utils.getResourceAdapterReference( patient, "Patient" );

        Assert.assertNotNull( adapterReference );
        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "abcdef123456", ReferenceType.ID ), adapterReference );
    }

    @Test
    public void getResourceAdapterReferenceDhisFhirIdentifierCode()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();

        final Patient patient = new Patient();
        patient.addIdentifier( new Identifier().setSystem( "National ID" ).setValue( "5678" ) );

        final org.dhis2.fhir.adapter.dhis.model.Reference adapterReference = utils.getResourceAdapterReference( patient, "Patient" );

        Assert.assertNotNull( adapterReference );
        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "5678", ReferenceType.CODE ), adapterReference );
    }

    @Test
    public void getResourceAdapterReferenceDhisFhirIdOnly()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( true ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();
        Mockito.doReturn( "1234" ).when( context ).extractDhisId( Mockito.eq( "1234" ) );

        final Patient patient = new Patient();
        patient.setIdElement( new IdType( "Patient/1234" ) );

        final org.dhis2.fhir.adapter.dhis.model.Reference adapterReference = utils.getResourceAdapterReference( patient, "Patient" );

        Assert.assertNotNull( adapterReference );
        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "1234", ReferenceType.ID ), adapterReference );
    }

    @Test
    public void getResourceAdapterReferenceResourceNonDhisFhirIdCode()
    {
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( false ).when( request ).isDhisFhirId();
        Mockito.doReturn( new ResourceSystem( FhirResourceType.PATIENT, "National ID" ) ).when( request ).getResourceSystem( Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( Collections.singletonMap( ScriptVariable.CONTEXT.getVariableName(), context ) ).when( scriptExecution ).getVariables();
        Mockito.doReturn( "12345678912" ).when( context ).extractDhisId( "12345678912" );

        final Patient patient = new Patient();
        patient.setIdElement( new IdType( "Patient/1234" ) );
        patient.addIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "12345678912" ) );

        final org.dhis2.fhir.adapter.dhis.model.Reference adapterReference = utils.getResourceAdapterReference( patient, "Patient" );

        Assert.assertNotNull( adapterReference );
        Assert.assertEquals( new org.dhis2.fhir.adapter.dhis.model.Reference( "12345678912", ReferenceType.ID ), adapterReference );
    }
}
