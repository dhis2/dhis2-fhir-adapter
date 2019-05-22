package org.dhis2.fhir.adapter.fhir.server.provider.dstu3;

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

import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirBatchRequest;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperation;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperationIssueSeverity;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperationIssueType;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperationType;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Patient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Unit tests for {@link Dstu3BundleResourceProvider}.
 *
 * @author volsch
 */
public class Dstu3BundleResourceProviderTest
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
    private RequestCacheService requestCacheService;

    @InjectMocks
    private Dstu3BundleResourceProvider provider;

    private FhirClient fhirClient = new FhirClient();

    private FhirClientResource patientClientResource = new FhirClientResource();

    private FhirClientResource observationClientResource = new FhirClientResource();

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void setUp()
    {
        fhirClient.setId( FhirClient.getIdByFhirVersion( FhirVersion.DSTU3 ) );
        patientClientResource.setFhirClient( fhirClient );
        observationClientResource.setFhirClient( fhirClient );
    }

    @Test
    public void getFhirVersion()
    {
        Assert.assertEquals( FhirVersion.DSTU3, provider.getFhirVersion() );
    }

    @Test( expected = InvalidRequestException.class )
    public void transactionBundle()
    {
        final Bundle bundle = new Bundle();
        bundle.setType( Bundle.BundleType.TRANSACTION );
        provider.createBatchRequest( bundle );
    }

    @Test
    public void createBatchResponseCreated()
    {
        final List<FhirOperation> operations = new ArrayList<>();
        operations.add( new FhirOperation( FhirOperationType.POST, FhirResourceType.PATIENT, patientClientResource, null, new Patient(), null ) );
        operations.get( 0 ).getResult().created( new IdDt( "123" ) );
        final FhirBatchRequest batchRequest = new FhirBatchRequest( operations, false );

        final Bundle bundle = provider.createBatchResponse( batchRequest );
        Assert.assertEquals( Bundle.BundleType.BATCHRESPONSE, bundle.getType() );
        Assert.assertEquals( 1, bundle.getEntry().size() );

        Assert.assertEquals( "Patient/123", bundle.getEntry().get( 0 ).getResponse().getLocation() );
        Assert.assertEquals( "201 Created", bundle.getEntry().get( 0 ).getResponse().getStatus() );
        Assert.assertNull( bundle.getEntry().get( 0 ).getResponse().getOutcome() );
    }

    @Test
    public void createBatchResponseOk()
    {
        final List<FhirOperation> operations = new ArrayList<>();
        operations.add( new FhirOperation( FhirOperationType.PUT, FhirResourceType.PATIENT, patientClientResource, "4711", new Patient(), null ) );
        operations.get( 0 ).getResult().ok();
        final FhirBatchRequest batchRequest = new FhirBatchRequest( operations, false );

        final Bundle bundle = provider.createBatchResponse( batchRequest );
        Assert.assertEquals( Bundle.BundleType.BATCHRESPONSE, bundle.getType() );
        Assert.assertEquals( 1, bundle.getEntry().size() );

        Assert.assertNull( bundle.getEntry().get( 0 ).getResponse().getLocation() );
        Assert.assertEquals( "200 OK", bundle.getEntry().get( 0 ).getResponse().getStatus() );
        Assert.assertNull( bundle.getEntry().get( 0 ).getResponse().getOutcome() );
    }

    @Test
    public void createBatchResponseError()
    {
        final List<FhirOperation> operations = new ArrayList<>();
        operations.add( new FhirOperation( FhirOperationType.PUT, FhirResourceType.PATIENT, patientClientResource, "4711", new Patient(), null ) );
        operations.get( 0 ).getResult().unprocessableEntity( "Invalid data included" );
        final FhirBatchRequest batchRequest = new FhirBatchRequest( operations, false );

        final Bundle bundle = provider.createBatchResponse( batchRequest );
        Assert.assertEquals( Bundle.BundleType.BATCHRESPONSE, bundle.getType() );
        Assert.assertEquals( 1, bundle.getEntry().size() );

        Assert.assertNull( bundle.getEntry().get( 0 ).getResponse().getLocation() );
        Assert.assertEquals( "422 Unprocessable entity", bundle.getEntry().get( 0 ).getResponse().getStatus() );
        Assert.assertNotNull( bundle.getEntry().get( 0 ).getResponse().getOutcome() );
        Assert.assertNotNull( ( (OperationOutcome) bundle.getEntry().get( 0 ).getResponse().getOutcome() ).getIssue() );
        Assert.assertEquals( 1, ( (OperationOutcome) bundle.getEntry().get( 0 ).getResponse().getOutcome() ).getIssue().size() );
        Assert.assertEquals( "Invalid data included", ( (OperationOutcome) bundle.getEntry().get( 0 ).getResponse().getOutcome() ).getIssueFirstRep().getDiagnostics() );
    }

    @Test
    public void createBatchResponseNotProcessed()
    {
        final List<FhirOperation> operations = new ArrayList<>();
        operations.add( new FhirOperation( FhirOperationType.PUT, FhirResourceType.PATIENT, patientClientResource, "4711", new Patient(), null ) );
        final FhirBatchRequest batchRequest = new FhirBatchRequest( operations, false );

        final Bundle bundle = provider.createBatchResponse( batchRequest );
        Assert.assertEquals( Bundle.BundleType.BATCHRESPONSE, bundle.getType() );
        Assert.assertEquals( 1, bundle.getEntry().size() );

        Assert.assertNull( bundle.getEntry().get( 0 ).getResponse().getLocation() );
        Assert.assertEquals( "500 Internal server error", bundle.getEntry().get( 0 ).getResponse().getStatus() );
        Assert.assertNotNull( bundle.getEntry().get( 0 ).getResponse().getOutcome() );
        Assert.assertNotNull( ( (OperationOutcome) bundle.getEntry().get( 0 ).getResponse().getOutcome() ).getIssue() );
        Assert.assertEquals( 1, ( (OperationOutcome) bundle.getEntry().get( 0 ).getResponse().getOutcome() ).getIssue().size() );
        Assert.assertEquals( "Operation has not been processed.", ( (OperationOutcome) bundle.getEntry().get( 0 ).getResponse().getOutcome() ).getIssueFirstRep().getDiagnostics() );
    }

    @Test
    public void createBatchRequest()
    {
        Mockito.when( fhirClientResourceRepository.findFirstCached( Mockito.eq( fhirClient.getId() ), Mockito.eq( FhirResourceType.PATIENT ) ) )
            .thenReturn( Optional.of( patientClientResource ) );

        final Bundle bundle = new Bundle();
        bundle.setType( Bundle.BundleType.BATCH );
        bundle.addEntry().setResource( new Patient() ).getRequest().setMethod( Bundle.HTTPVerb.POST );
        bundle.addEntry().setResource( new Patient() ).getRequest().setMethod( Bundle.HTTPVerb.POST );

        final FhirBatchRequest batchRequest = provider.createBatchRequest( bundle );
        Assert.assertFalse( batchRequest.isTransactional() );
        Assert.assertEquals( 2, batchRequest.getOperations().size() );

        Assert.assertFalse( batchRequest.getOperations().get( 0 ).isProcessed() );
        Assert.assertNotNull( batchRequest.getOperations().get( 0 ).getResource() );
        Assert.assertEquals( FhirOperationType.POST, batchRequest.getOperations().get( 0 ).getOperationType() );

        Assert.assertFalse( batchRequest.getOperations().get( 1 ).isProcessed() );
        Assert.assertNotNull( batchRequest.getOperations().get( 1 ).getResource() );
        Assert.assertEquals( FhirOperationType.POST, batchRequest.getOperations().get( 1 ).getOperationType() );
    }

    @Test
    public void createBatchRequestIfMatch()
    {
        final Bundle bundle = new Bundle();
        bundle.setType( Bundle.BundleType.BATCH );
        bundle.addEntry().setResource( new Patient() ).getRequest().setMethod( Bundle.HTTPVerb.POST ).setIfMatch( "xyz" );

        final FhirBatchRequest batchRequest = provider.createBatchRequest( bundle );
        Assert.assertFalse( batchRequest.isTransactional() );
        Assert.assertEquals( 1, batchRequest.getOperations().size() );

        Assert.assertTrue( batchRequest.getOperations().get( 0 ).isProcessed() );
        Assert.assertNotNull( batchRequest.getOperations().get( 0 ).getResult().getIssue() );
    }

    @Test
    public void createBatchRequestIfModifiedSince()
    {
        final Bundle bundle = new Bundle();
        bundle.setType( Bundle.BundleType.BATCH );
        bundle.addEntry().setResource( new Patient() ).getRequest().setMethod( Bundle.HTTPVerb.POST ).setIfModifiedSince( new Date() );

        final FhirBatchRequest batchRequest = provider.createBatchRequest( bundle );
        Assert.assertFalse( batchRequest.isTransactional() );
        Assert.assertEquals( 1, batchRequest.getOperations().size() );

        Assert.assertTrue( batchRequest.getOperations().get( 0 ).isProcessed() );
        Assert.assertNotNull( batchRequest.getOperations().get( 0 ).getResult().getIssue() );
    }

    @Test
    public void createBatchRequestIfNoneExist()
    {
        final Bundle bundle = new Bundle();
        bundle.setType( Bundle.BundleType.BATCH );
        bundle.addEntry().setResource( new Patient() ).getRequest().setMethod( Bundle.HTTPVerb.POST ).setIfNoneExist( "xyz" );

        final FhirBatchRequest batchRequest = provider.createBatchRequest( bundle );
        Assert.assertFalse( batchRequest.isTransactional() );
        Assert.assertEquals( 1, batchRequest.getOperations().size() );

        Assert.assertTrue( batchRequest.getOperations().get( 0 ).isProcessed() );
        Assert.assertNotNull( batchRequest.getOperations().get( 0 ).getResult().getIssue() );
    }

    @Test
    public void createBatchRequestIfNoneMatch()
    {
        final Bundle bundle = new Bundle();
        bundle.setType( Bundle.BundleType.BATCH );
        bundle.addEntry().setResource( new Patient() ).getRequest().setMethod( Bundle.HTTPVerb.POST ).setIfNoneExist( "xyz" );

        final FhirBatchRequest batchRequest = provider.createBatchRequest( bundle );
        Assert.assertFalse( batchRequest.isTransactional() );
        Assert.assertEquals( 1, batchRequest.getOperations().size() );

        Assert.assertTrue( batchRequest.getOperations().get( 0 ).isProcessed() );
        Assert.assertNotNull( batchRequest.getOperations().get( 0 ).getResult().getIssue() );
    }

    @Test
    public void testConvertInvalid()
    {
        Assert.assertEquals( OperationOutcome.IssueType.INVALID, provider.convert( FhirOperationIssueType.INVALID ) );
    }

    @Test
    public void testConvertProcessing()
    {
        Assert.assertEquals( OperationOutcome.IssueType.PROCESSING, provider.convert( FhirOperationIssueType.PROCESSING ) );
    }

    @Test
    public void testConvertTransient()
    {
        Assert.assertEquals( OperationOutcome.IssueType.TRANSIENT, provider.convert( FhirOperationIssueType.TRANSIENT ) );
    }

    @Test
    public void testConvertInformational()
    {
        Assert.assertEquals( OperationOutcome.IssueType.INFORMATIONAL, provider.convert( FhirOperationIssueType.INFORMATIONAL ) );
    }

    @Test
    public void testConvertInformation()
    {
        Assert.assertEquals( OperationOutcome.IssueSeverity.INFORMATION, provider.convert( FhirOperationIssueSeverity.INFO ) );
    }

    @Test
    public void testConvertWarning()
    {
        Assert.assertEquals( OperationOutcome.IssueSeverity.WARNING, provider.convert( FhirOperationIssueSeverity.WARN ) );
    }

    @Test
    public void testConvertError()
    {
        Assert.assertEquals( OperationOutcome.IssueSeverity.ERROR, provider.convert( FhirOperationIssueSeverity.ERROR ) );
    }

    @Test
    public void testConvertFatal()
    {
        Assert.assertEquals( OperationOutcome.IssueSeverity.FATAL, provider.convert( FhirOperationIssueSeverity.FATAL ) );
    }
}