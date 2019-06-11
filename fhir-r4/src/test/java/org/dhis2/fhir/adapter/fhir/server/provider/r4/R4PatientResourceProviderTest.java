package org.dhis2.fhir.adapter.fhir.server.provider.r4;

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

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Observation;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Unit tests for {@link R4PatientResourceProvider}.
 *
 * @author volsch
 */
public class R4PatientResourceProviderTest
{
    @Mock
    private FhirClientResourceRepository fhirClientResourceRepository;

    @Mock
    private FhirClientSystemRepository fhirClientSystemRepository;

    @Mock
    private FhirRepository fhirRepository;

    @Mock
    private DhisRepository dhisRepository;

    @Mock
    private RequestDetails requestDetails;

    @InjectMocks
    private R4PatientResourceProvider provider;

    @Mock
    private FhirClient fhirClient;

    @Mock
    private FhirClientResource observationClientResource;

    @Mock
    private FhirClientResource conditionClientResource;

    @Mock
    private FhirClientResource immunizationClientResource;

    @Mock
    private FhirClientResource encounterClientResource;

    @Mock
    private FhirClientResource observationResultClientResource;

    @Mock
    private FhirClientResource diagnosticReportClientResource;

    @Mock
    private FhirClientResource medicationRequestClientResource;

    @Mock
    private Observation observation;

    @Mock
    private Condition condition;

    @Mock
    private Immunization immunization;

    @Mock
    private Encounter encounter;

    @Mock
    private DiagnosticReport diagnosticReport;

    @Mock
    private MedicationRequest medicationRequest;

    @Mock
    private IBundleProvider observationBundleProvider;

    @Mock
    private IBundleProvider conditionBundleProvider;

    @Mock
    private IBundleProvider immunizationBundleProvider;

    @Mock
    private IBundleProvider encounterBundleProvider;

    @Mock
    private IBundleProvider diagnosticReportBundleProvider;

    @Mock
    private IBundleProvider medicationRequestBundleProvider;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void patientInstanceEverything()
    {
        Mockito.when( requestDetails.getServerBaseForRequest() ).thenReturn( "http://test.org/fhir" );
        Mockito.when( requestDetails.getTenantId() ).thenReturn( "default" );

        Mockito.when( observation.getIdElement() ).thenReturn( new IdType() );
        Mockito.when( condition.getIdElement() ).thenReturn( new IdType() );
        Mockito.when( immunization.getIdElement() ).thenReturn( new IdType() );
        Mockito.when( encounter.getIdElement() ).thenReturn( new IdType() );
        Mockito.when( diagnosticReport.getIdElement() ).thenReturn( new IdType() );
        Mockito.when( medicationRequest.getIdElement() ).thenReturn( new IdType() );

        final UUID fhirClientId = FhirClient.getIdByFhirVersion( FhirVersion.R4 );

        Mockito.doReturn( Optional.of( observationClientResource ) ).when( fhirClientResourceRepository ).findFirstCached( Mockito.eq( fhirClientId ), Mockito.eq( FhirResourceType.OBSERVATION ) );
        Mockito.doReturn( Optional.of( conditionClientResource ) ).when( fhirClientResourceRepository ).findFirstCached( Mockito.eq( fhirClientId ), Mockito.eq( FhirResourceType.CONDITION ) );
        Mockito.doReturn( Optional.of( immunizationClientResource ) ).when( fhirClientResourceRepository ).findFirstCached( Mockito.eq( fhirClientId ), Mockito.eq( FhirResourceType.IMMUNIZATION ) );
        Mockito.doReturn( Optional.of( encounterClientResource ) ).when( fhirClientResourceRepository ).findFirstCached( Mockito.eq( fhirClientId ), Mockito.eq( FhirResourceType.ENCOUNTER ) );
        Mockito.doReturn( Optional.of( diagnosticReportClientResource ) ).when( fhirClientResourceRepository ).findFirstCached( Mockito.eq( fhirClientId ), Mockito.eq( FhirResourceType.DIAGNOSTIC_REPORT ) );
        Mockito.doReturn( Optional.of( medicationRequestClientResource ) ).when( fhirClientResourceRepository ).findFirstCached( Mockito.eq( fhirClientId ), Mockito.eq( FhirResourceType.MEDICATION_REQUEST ) );

        Mockito.doReturn( fhirClient ).when( observationClientResource ).getFhirClient();
        Mockito.doReturn( fhirClient ).when( conditionClientResource ).getFhirClient();
        Mockito.doReturn( fhirClient ).when( immunizationClientResource ).getFhirClient();
        Mockito.doReturn( fhirClient ).when( encounterClientResource ).getFhirClient();
        Mockito.doReturn( fhirClient ).when( diagnosticReportClientResource ).getFhirClient();
        Mockito.doReturn( fhirClient ).when( medicationRequestClientResource ).getFhirClient();

        Mockito.doAnswer( invocation -> {
            final Map<String, List<String>> filter = invocation.getArgument( 5 );

            Assert.assertEquals( filter.size(), 1 );
            Assert.assertTrue( filter.containsKey( "patient" ) );
            Assert.assertEquals( Collections.singletonList( "Patient/4711" ), filter.get( "patient" ) );

            return observationBundleProvider;
        } ).when( dhisRepository ).search( Mockito.same( fhirClient ), Mockito.eq( FhirResourceType.OBSERVATION ), Mockito.isNull(), Mockito.eq( true ),
            Mockito.isNull(), Mockito.isNotNull(), Mockito.isNull() );

        Mockito.doAnswer( invocation -> {
            final Map<String, List<String>> filter = invocation.getArgument( 5 );

            Assert.assertEquals( filter.size(), 1 );
            Assert.assertTrue( filter.containsKey( "patient" ) );
            Assert.assertEquals( Collections.singletonList( "Patient/4711" ), filter.get( "patient" ) );

            return conditionBundleProvider;
        } ).when( dhisRepository ).search( Mockito.same( fhirClient ), Mockito.eq( FhirResourceType.CONDITION ), Mockito.isNull(), Mockito.eq( true ),
            Mockito.isNull(), Mockito.isNotNull(), Mockito.isNull() );

        Mockito.doAnswer( invocation -> {
            final Map<String, List<String>> filter = invocation.getArgument( 5 );

            Assert.assertEquals( filter.size(), 1 );
            Assert.assertTrue( filter.containsKey( "patient" ) );
            Assert.assertEquals( Collections.singletonList( "Patient/4711" ), filter.get( "patient" ) );

            return immunizationBundleProvider;
        } ).when( dhisRepository ).search( Mockito.same( fhirClient ), Mockito.eq( FhirResourceType.IMMUNIZATION ), Mockito.isNull(), Mockito.eq( true ),
            Mockito.isNull(), Mockito.isNotNull(), Mockito.isNull() );

        Mockito.doAnswer( invocation -> {
            final Map<String, List<String>> filter = invocation.getArgument( 5 );

            Assert.assertEquals( filter.size(), 1 );
            Assert.assertTrue( filter.containsKey( "patient" ) );
            Assert.assertEquals( Collections.singletonList( "Patient/4711" ), filter.get( "patient" ) );

            return encounterBundleProvider;
        } ).when( dhisRepository ).search( Mockito.same( fhirClient ), Mockito.eq( FhirResourceType.ENCOUNTER ), Mockito.isNull(), Mockito.eq( true ),
            Mockito.isNull(), Mockito.isNotNull(), Mockito.isNull() );

        Mockito.doAnswer( invocation -> {
            final Map<String, List<String>> filter = invocation.getArgument( 5 );

            Assert.assertEquals( filter.size(), 1 );
            Assert.assertTrue( filter.containsKey( "patient" ) );
            Assert.assertEquals( Collections.singletonList( "Patient/4711" ), filter.get( "patient" ) );

            return diagnosticReportBundleProvider;
        } ).when( dhisRepository ).search( Mockito.same( fhirClient ), Mockito.eq( FhirResourceType.DIAGNOSTIC_REPORT ), Mockito.isNull(), Mockito.eq( true ),
            Mockito.isNull(), Mockito.isNotNull(), Mockito.isNull() );

        Mockito.doAnswer( invocation -> {
            final Map<String, List<String>> filter = invocation.getArgument( 5 );

            Assert.assertEquals( filter.size(), 1 );
            Assert.assertTrue( filter.containsKey( "patient" ) );
            Assert.assertEquals( Collections.singletonList( "Patient/4711" ), filter.get( "patient" ) );

            return medicationRequestBundleProvider;
        } ).when( dhisRepository ).search( Mockito.same( fhirClient ), Mockito.eq( FhirResourceType.MEDICATION_REQUEST ), Mockito.isNull(), Mockito.eq( true ),
            Mockito.isNull(), Mockito.isNotNull(), Mockito.isNull() );

        Mockito.doReturn( Collections.singletonList( observation ) ).when( observationBundleProvider ).getResources( Mockito.eq( 0 ), Mockito.eq( Integer.MAX_VALUE ) );
        Mockito.doReturn( Collections.singletonList( immunization ) ).when( immunizationBundleProvider ).getResources( Mockito.eq( 0 ), Mockito.eq( Integer.MAX_VALUE ) );
        Mockito.doReturn( Collections.singletonList( condition ) ).when( conditionBundleProvider ).getResources( Mockito.eq( 0 ), Mockito.eq( Integer.MAX_VALUE ) );
        Mockito.doReturn( Collections.singletonList( diagnosticReport ) ).when( diagnosticReportBundleProvider ).getResources( Mockito.eq( 0 ), Mockito.eq( Integer.MAX_VALUE ) );
        Mockito.doReturn( Collections.singletonList( medicationRequest ) ).when( medicationRequestBundleProvider ).getResources( Mockito.eq( 0 ), Mockito.eq( Integer.MAX_VALUE ) );
        Mockito.doReturn( Collections.singletonList( encounter ) ).when( encounterBundleProvider ).getResources( Mockito.eq( 0 ), Mockito.eq( Integer.MAX_VALUE ) );

        final Bundle result = provider.patientInstanceEverything( requestDetails, new IdType( "4711" ) );

        Assert.assertEquals( 6, result.getEntry().size() );
        Assert.assertTrue( result.getEntry().stream().anyMatch( e -> e.getResource() == observation ) );
        Assert.assertTrue( result.getEntry().stream().anyMatch( e -> e.getResource() == immunization ) );
        Assert.assertTrue( result.getEntry().stream().anyMatch( e -> e.getResource() == condition ) );
        Assert.assertTrue( result.getEntry().stream().anyMatch( e -> e.getResource() == diagnosticReport ) );
        Assert.assertTrue( result.getEntry().stream().anyMatch( e -> e.getResource() == medicationRequest ) );
        Assert.assertTrue( result.getEntry().stream().anyMatch( e -> e.getResource() == encounter ) );
    }
}