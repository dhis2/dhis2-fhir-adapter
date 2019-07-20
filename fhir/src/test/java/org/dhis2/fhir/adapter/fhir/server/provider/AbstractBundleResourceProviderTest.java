package org.dhis2.fhir.adapter.fhir.server.provider;

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

import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import org.dhis2.fhir.adapter.cache.RequestCacheContext;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisResourceRepositoryContainer;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisResourceRepositoryTemplate;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientSystem;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirBatchRequest;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperation;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperationResult;
import org.dhis2.fhir.adapter.fhir.repository.FhirOperationType;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryOperation;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryOperationOutcome;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryOperationType;
import org.hamcrest.Matchers;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseMetaType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient.FHIR_REST_INTERFACE_DSTU3_ID;

/**
 * Unit tests for {@link AbstractBundleResourceProvider}.
 *
 * @author volsch
 */
public class AbstractBundleResourceProviderTest
{
    private AbstractBundleResourceProvider<IBaseBundle> bundleResourceProvider;

    @Mock
    private IBaseBundle baseBundle;

    @Mock
    private IBaseBundle resultBaseBundle;

    @Mock
    private IBaseResource baseResource1;

    @Mock
    private IBaseResource baseResource2;

    @Mock
    private IBaseResource baseResource3;

    @Mock
    private IBaseResource baseResource4;

    @Mock
    private IBaseResource baseResource5;

    @Mock
    private IBaseResource baseResource6;

    @Mock
    private IBaseResource baseResource7;

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

    @Mock
    private RequestCacheContext requestCacheContext;

    @Mock
    private RequestDetails requestDetails;

    private FhirClient fhirClient = new FhirClient();

    private FhirClientResource patientClientResource = new FhirClientResource();

    private FhirClientResource observationClientResource = new FhirClientResource();

    private FhirClientSystem patientClientSystem;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    @SuppressWarnings( { "unchecked" } )
    public void setUp()
    {
        bundleResourceProvider = Mockito.mock( AbstractBundleResourceProvider.class, Mockito.withSettings()
            .useConstructor( fhirClientResourceRepository, fhirClientSystemRepository, fhirRepository, dhisRepository, requestCacheService )
            .defaultAnswer( Mockito.CALLS_REAL_METHODS ) );

        fhirClient.setId( FhirClient.getIdByFhirVersion( FhirVersion.DSTU3 ) );
        patientClientResource.setFhirClient( fhirClient );
        observationClientResource.setFhirClient( fhirClient );

        patientClientSystem = new FhirClientSystem();
        patientClientSystem.setSystem( new System( "http://test.com/patient" ) );
    }

    @Test( expected = InvalidRequestException.class )
    public void processInternalNull()
    {
        bundleResourceProvider.processInternal( requestDetails, null );
    }

    @Test
    public void processInternal() throws Exception
    {
        final AtomicReference<LocalDhisResourceRepositoryContainer> repositoryContainerReference = new AtomicReference<>();

        Mockito.when( bundleResourceProvider.getFhirVersion() ).thenReturn( FhirVersion.DSTU3 );
        Mockito.when( requestCacheService.createRequestCacheContext() ).thenReturn( requestCacheContext );
        Mockito.doAnswer( invocation -> {
            repositoryContainerReference.set( invocation.getArgument( 1 ) );

            return null;
        } ).when( requestCacheContext ).setAttribute( Mockito.eq( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME ), Mockito.any() );

        final FhirOperation invalidDeleteOperation = new FhirOperation( FhirOperationType.DELETE, FhirResourceType.PATIENT, null, null, null, null );
        invalidDeleteOperation.getResult().badRequest( "Invalid data included" );

        final FhirOperation invalidPostOperation = new FhirOperation( FhirOperationType.POST, FhirResourceType.PATIENT, null, null, null, null );
        invalidPostOperation.getResult().badRequest( "Invalid data included" );

        final FhirOperation invalidPutOperation = new FhirOperation( FhirOperationType.PUT, FhirResourceType.PATIENT, null, null, null, null );
        invalidPutOperation.getResult().badRequest( "Invalid data included" );

        final List<FhirOperation> operations = new ArrayList<>();
        operations.add( new FhirOperation( FhirOperationType.PUT, FhirResourceType.PATIENT, patientClientResource, "ldXIdLNUNE1", baseResource1, null ) );
        operations.add( new FhirOperation( FhirOperationType.DELETE, FhirResourceType.PATIENT, patientClientResource, "ldXIdLNUNE4", null, null ) );
        operations.add( new FhirOperation( FhirOperationType.POST, FhirResourceType.OBSERVATION, observationClientResource, null, baseResource2, null ) );
        operations.add( new FhirOperation( FhirOperationType.PUT, FhirResourceType.PATIENT, patientClientResource, "ldXIdLNUNE2", baseResource3,
            new URI( "Patient?identifier=http%3A%2F%2Ftest.com%2Fpatient%7C8972" ) ) );
        operations.add( new FhirOperation( FhirOperationType.PUT, FhirResourceType.PATIENT, patientClientResource, null, baseResource4,
            new URI( "Patient?identifier=http%3A%2F%2Ftest.com%2Fpatient%7C8973" ) ) );
        operations.add( new FhirOperation( FhirOperationType.DELETE, FhirResourceType.PATIENT, patientClientResource, "ldXIdLNUNE2", null, null ) );
        operations.add( new FhirOperation( FhirOperationType.POST, FhirResourceType.PATIENT, patientClientResource, "ldXIdLNUN12", baseResource5, null ) );
        operations.add( new FhirOperation( FhirOperationType.PUT, FhirResourceType.PATIENT, patientClientResource, "ldXIdLNUN22", baseResource6, null ) );

        operations.add( invalidDeleteOperation );
        operations.add( invalidPostOperation );
        operations.add( invalidPutOperation );

        operations.add( new FhirOperation( FhirOperationType.PUT, FhirResourceType.PATIENT, patientClientResource, null, baseResource7, null ) );

        final FhirBatchRequest batchRequest = new FhirBatchRequest( operations, false );

        Mockito.when( requestDetails.getTenantId() ).thenReturn( "default" );
        Mockito.when( bundleResourceProvider.createBatchRequest( Mockito.same( baseBundle ) ) ).thenReturn( batchRequest );
        Mockito.when( fhirClientSystemRepository.findOneByFhirClientResourceType( Mockito.eq( FHIR_REST_INTERFACE_DSTU3_ID ), Mockito.eq( FhirResourceType.PATIENT ) ) )
            .thenReturn( Optional.of( patientClientSystem ) );
        Mockito.when( bundleResourceProvider.createBatchResponse( Mockito.same( batchRequest ) ) ).thenReturn( resultBaseBundle );

        Mockito.when( fhirRepository.save( Mockito.same( patientClientResource ), Mockito.same( baseResource1 ), Mockito.eq( new FhirRepositoryOperation( FhirRepositoryOperationType.UPDATE ) ) ) )
            .thenReturn( new FhirRepositoryOperationOutcome( "ldXIdLNUNE1", false ) );
        Mockito.when( fhirRepository.delete( Mockito.same( patientClientResource ), Mockito.eq( DhisFhirResourceId.parse( "ldXIdLNUNE4" ) ) ) )
            .thenReturn( true );
        Mockito.when( fhirRepository.save( Mockito.same( observationClientResource ), Mockito.same( baseResource2 ), Mockito.eq( new FhirRepositoryOperation( FhirRepositoryOperationType.CREATE ) ) ) )
            .thenReturn( new FhirRepositoryOperationOutcome( "ldXIdLNUNE9", true ) );
        Mockito.when( dhisRepository.readByIdentifier( Mockito.same( patientClientResource.getFhirClient() ), Mockito.eq( FhirResourceType.PATIENT ), Mockito.eq( "8972" ) ) )
            .thenReturn( Optional.empty() );
        Mockito.when( fhirRepository.save( Mockito.same( patientClientResource ), Mockito.same( baseResource3 ), Mockito.eq( new FhirRepositoryOperation( FhirRepositoryOperationType.CREATE ) ) ) )
            .thenReturn( new FhirRepositoryOperationOutcome( "ldXIdLNUNE8", true ) );
        Mockito.when( baseResource4.getIdElement() ).thenReturn( new IdDt( "ldXIdLNUNE7" ) );
        Mockito.when( dhisRepository.readByIdentifier( Mockito.same( patientClientResource.getFhirClient() ), Mockito.eq( FhirResourceType.PATIENT ), Mockito.eq( "8973" ) ) )
            .thenReturn( Optional.of( baseResource4 ) );
        Mockito.when( fhirRepository.save( Mockito.same( patientClientResource ), Mockito.same( baseResource4 ), Mockito.eq( new FhirRepositoryOperation( FhirRepositoryOperationType.UPDATE ) ) ) )
            .thenReturn( new FhirRepositoryOperationOutcome( "ldXIdLNUNE4", false ) );
        Mockito.when( fhirRepository.save( Mockito.same( patientClientResource ), Mockito.same( baseResource7 ), Mockito.eq( new FhirRepositoryOperation( FhirRepositoryOperationType.CREATE_OR_UPDATE ) ) ) )
            .thenReturn( new FhirRepositoryOperationOutcome( "lxXIdLNUNE4", false ) );
        Mockito.when( fhirRepository.delete( Mockito.same( patientClientResource ), Mockito.eq( DhisFhirResourceId.parse( "ldXIdLNUNE2" ) ) ) )
            .thenReturn( false );

        Assert.assertSame( resultBaseBundle, bundleResourceProvider.processInternal( requestDetails, baseBundle ) );

        Assert.assertNull( operations.get( 0 ).getResult().getIssue() );
        Assert.assertEquals( FhirOperationResult.OK_STATUS_CODE, operations.get( 0 ).getResult().getStatusCode() );
        Assert.assertNull( operations.get( 0 ).getResult().getId() );

        Assert.assertNull( operations.get( 1 ).getResult().getIssue() );
        Assert.assertEquals( FhirOperationResult.NO_CONTENT_STATUS_CODE, operations.get( 1 ).getResult().getStatusCode() );
        Assert.assertNull( operations.get( 1 ).getResult().getId() );

        Assert.assertNull( operations.get( 2 ).getResult().getIssue() );
        Assert.assertEquals( FhirOperationResult.CREATED_STATUS_CODE, operations.get( 2 ).getResult().getStatusCode() );
        Assert.assertEquals( new IdDt( "ldXIdLNUNE9" ), operations.get( 2 ).getResult().getId() );

        Assert.assertNull( operations.get( 3 ).getResult().getIssue() );
        Assert.assertEquals( FhirOperationResult.CREATED_STATUS_CODE, operations.get( 3 ).getResult().getStatusCode() );
        Assert.assertEquals( new IdDt( "ldXIdLNUNE8" ), operations.get( 3 ).getResult().getId() );

        Assert.assertNull( operations.get( 4 ).getResult().getIssue() );
        Assert.assertEquals( FhirOperationResult.OK_STATUS_CODE, operations.get( 4 ).getResult().getStatusCode() );
        Assert.assertNull( operations.get( 4 ).getResult().getId() );

        Assert.assertNotNull( operations.get( 5 ).getResult().getIssue() );
        Assert.assertEquals( FhirOperationResult.NOT_FOUND_STATUS_CODE, operations.get( 5 ).getResult().getStatusCode() );
        Assert.assertNull( operations.get( 5 ).getResult().getId() );

        Assert.assertNotNull( operations.get( 6 ).getResult().getIssue() );
        Assert.assertThat( operations.get( 6 ).getResult().getIssue().toString(), Matchers.containsString( "Could not find a rule that matches" ) );

        Assert.assertNotNull( operations.get( 7 ).getResult().getIssue() );
        Assert.assertThat( operations.get( 7 ).getResult().getIssue().toString(), Matchers.containsString( "Could not find a rule that matches" ) );

        Assert.assertNotNull( operations.get( 8 ).getResult().getIssue() );
        Assert.assertThat( operations.get( 8 ).getResult().getIssue().toString(), Matchers.containsString( "Invalid data included" ) );

        Assert.assertNotNull( operations.get( 9 ).getResult().getIssue() );
        Assert.assertThat( operations.get( 9 ).getResult().getIssue().toString(), Matchers.containsString( "Invalid data included" ) );

        Assert.assertNotNull( operations.get( 10 ).getResult().getIssue() );
        Assert.assertThat( operations.get( 10 ).getResult().getIssue().toString(), Matchers.containsString( "Invalid data included" ) );

        Assert.assertNull( operations.get( 11 ).getResult().getIssue() );
        Assert.assertEquals( FhirOperationResult.OK_STATUS_CODE, operations.get( 11 ).getResult().getStatusCode() );
        Assert.assertEquals( new IdDt( "lxXIdLNUNE4" ), operations.get( 11 ).getResult().getId() );

        Assert.assertNotNull( repositoryContainerReference.get() );
        Mockito.verify( requestCacheContext, Mockito.times( 9 ) ).setAttribute( Mockito.eq( LocalDhisResourceRepositoryTemplate.RESOURCE_KEY_REQUEST_CACHE_ATTRIBUTE_NAME ),
            Mockito.any( FhirOperation.class ) );
    }

    @Test
    public void processInternalReferenceNotFound() throws Exception
    {
        Mockito.when( bundleResourceProvider.getFhirVersion() ).thenReturn( FhirVersion.DSTU3 );
        Mockito.when( requestCacheService.createRequestCacheContext() ).thenReturn( requestCacheContext );

        final List<FhirOperation> operations = new ArrayList<>();
        operations.add( new FhirOperation( FhirOperationType.DELETE, FhirResourceType.PATIENT, patientClientResource, null, null,
            new URI( "Patient?identifier=http%3A%2F%2Ftest.com%2Fpatient%7C8972" ) ) );

        final FhirBatchRequest batchRequest = new FhirBatchRequest( operations, false );

        Mockito.when( requestDetails.getTenantId() ).thenReturn( "default" );
        Mockito.when( bundleResourceProvider.createBatchRequest( Mockito.same( baseBundle ) ) ).thenReturn( batchRequest );
        Mockito.when( fhirClientSystemRepository.findOneByFhirClientResourceType( Mockito.eq( FHIR_REST_INTERFACE_DSTU3_ID ), Mockito.eq( FhirResourceType.PATIENT ) ) )
            .thenReturn( Optional.of( patientClientSystem ) );
        Mockito.when( bundleResourceProvider.createBatchResponse( Mockito.same( batchRequest ) ) ).thenReturn( resultBaseBundle );

        Assert.assertSame( resultBaseBundle, bundleResourceProvider.processInternal( requestDetails, baseBundle ) );

        Assert.assertNotNull( operations.get( 0 ).getResult().getIssue() );
        Assert.assertEquals( FhirOperationResult.NOT_FOUND_STATUS_CODE, operations.get( 0 ).getResult().getStatusCode() );
        Assert.assertNull( operations.get( 0 ).getResult().getId() );
    }

    @Test
    public void createOperationUrlWithoutResourceType()
    {
        Mockito.when( bundleResourceProvider.getFhirVersion() ).thenReturn( FhirVersion.DSTU3 );
        Mockito.when( fhirClientResourceRepository.findFirstCached( Mockito.eq( fhirClient.getId() ), Mockito.eq( FhirResourceType.PATIENT ) ) )
            .thenReturn( Optional.of( patientClientResource ) );

        final FhirOperation fhirOperation = bundleResourceProvider.createOperation( "http://xyz.com/Patient/4811", null, "delete", "4711" );

        Assert.assertFalse( fhirOperation.isProcessed() );
        Assert.assertEquals( FhirOperationType.DELETE, fhirOperation.getOperationType() );
        Assert.assertEquals( FhirResourceType.PATIENT, fhirOperation.getFhirResourceType() );
        Assert.assertEquals( "4811", fhirOperation.getResourceId() );
    }

    @Test
    public void createOperationUrlWithResourceType()
    {
        Mockito.when( bundleResourceProvider.getFhirVersion() ).thenReturn( FhirVersion.DSTU3 );
        Mockito.when( fhirClientResourceRepository.findFirstCached( Mockito.eq( fhirClient.getId() ), Mockito.eq( FhirResourceType.PATIENT ) ) )
            .thenReturn( Optional.of( patientClientResource ) );

        final FhirOperation fhirOperation = bundleResourceProvider.createOperation( "http://xyz.com/Observation/4811", null, "delete", "Patient/4711" );

        Assert.assertFalse( fhirOperation.isProcessed() );
        Assert.assertEquals( FhirOperationType.DELETE, fhirOperation.getOperationType() );
        Assert.assertEquals( FhirResourceType.PATIENT, fhirOperation.getFhirResourceType() );
        Assert.assertEquals( "4711", fhirOperation.getResourceId() );
    }

    @Test
    public void createOperationResourceOnly()
    {
        Mockito.when( bundleResourceProvider.getFhirVersion() ).thenReturn( FhirVersion.DSTU3 );
        Mockito.when( fhirClientResourceRepository.findFirstCached( Mockito.eq( fhirClient.getId() ), Mockito.eq( FhirResourceType.PATIENT ) ) )
            .thenReturn( Optional.of( patientClientResource ) );

        final FhirOperation fhirOperation = bundleResourceProvider.createOperation( null, new Patient(), "POST", null );

        Assert.assertFalse( fhirOperation.isProcessed() );
        Assert.assertEquals( FhirOperationType.POST, fhirOperation.getOperationType() );
        Assert.assertEquals( FhirResourceType.PATIENT, fhirOperation.getFhirResourceType() );
        Assert.assertNull( fhirOperation.getResourceId() );
    }

    @Test
    public void createOperationUnhandledResource()
    {
        Mockito.when( bundleResourceProvider.getFhirVersion() ).thenReturn( FhirVersion.DSTU3 );

        final FhirOperation fhirOperation = bundleResourceProvider.createOperation( null, new MeasureReport(), "POST", null );

        Assert.assertTrue( fhirOperation.isProcessed() );
        Assert.assertEquals( FhirOperationType.POST, fhirOperation.getOperationType() );
        Assert.assertEquals( FhirResourceType.MEASURE_REPORT, fhirOperation.getFhirResourceType() );
        Assert.assertNull( fhirOperation.getResourceId() );
    }

    @Test
    public void createOperationUnknownHttpVerb()
    {
        Mockito.when( bundleResourceProvider.getFhirVersion() ).thenReturn( FhirVersion.DSTU3 );
        Mockito.when( fhirClientResourceRepository.findFirstCached( Mockito.eq( fhirClient.getId() ), Mockito.eq( FhirResourceType.PATIENT ) ) )
            .thenReturn( Optional.of( patientClientResource ) );

        final FhirOperation fhirOperation = bundleResourceProvider.createOperation( null, new Patient(), "xPOST", null );

        Assert.assertTrue( fhirOperation.isProcessed() );
        Assert.assertEquals( FhirOperationResult.BAD_REQUEST_STATUS_CODE, fhirOperation.getResult().getStatusCode() );
        Assert.assertEquals( FhirOperationType.UNKNOWN, fhirOperation.getOperationType() );
    }

    @Test
    public void createOperationUndefinedResourceType()
    {
        final FhirOperation fhirOperation = bundleResourceProvider.createOperation( null, null, "delete", "4711" );

        Assert.assertTrue( fhirOperation.isProcessed() );
        Assert.assertEquals( FhirOperationResult.BAD_REQUEST_STATUS_CODE, fhirOperation.getResult().getStatusCode() );
        Assert.assertEquals( FhirOperationType.DELETE, fhirOperation.getOperationType() );
    }

    @Test
    public void createOperationInvalidUri()
    {
        Mockito.when( bundleResourceProvider.getFhirVersion() ).thenReturn( FhirVersion.DSTU3 );
        Mockito.when( fhirClientResourceRepository.findFirstCached( Mockito.eq( fhirClient.getId() ), Mockito.eq( FhirResourceType.PATIENT ) ) )
            .thenReturn( Optional.of( patientClientResource ) );

        final FhirOperation fhirOperation = bundleResourceProvider.createOperation( null, new Patient(), "post", ":%727" );

        Assert.assertTrue( fhirOperation.isProcessed() );
        Assert.assertEquals( FhirOperationResult.BAD_REQUEST_STATUS_CODE, fhirOperation.getResult().getStatusCode() );
        Assert.assertEquals( FhirOperationType.POST, fhirOperation.getOperationType() );
    }

    @Test
    public void createOperationPostWithoutResource()
    {
        final FhirOperation fhirOperation = bundleResourceProvider.createOperation( null, null, "post", null );

        Assert.assertTrue( fhirOperation.isProcessed() );
        Assert.assertEquals( FhirOperationResult.BAD_REQUEST_STATUS_CODE, fhirOperation.getResult().getStatusCode() );
        Assert.assertEquals( FhirOperationType.POST, fhirOperation.getOperationType() );
    }

    @Test
    public void createOperationPutWithoutResource()
    {
        Mockito.when( bundleResourceProvider.getFhirVersion() ).thenReturn( FhirVersion.DSTU3 );
        Mockito.when( fhirClientResourceRepository.findFirstCached( Mockito.eq( fhirClient.getId() ), Mockito.eq( FhirResourceType.PATIENT ) ) )
            .thenReturn( Optional.of( patientClientResource ) );

        final FhirOperation fhirOperation = bundleResourceProvider.createOperation( null, null, "post", "Patient/4711" );

        Assert.assertTrue( fhirOperation.isProcessed() );
        Assert.assertEquals( FhirOperationResult.BAD_REQUEST_STATUS_CODE, fhirOperation.getResult().getStatusCode() );
        Assert.assertEquals( FhirOperationType.POST, fhirOperation.getOperationType() );
    }

    @Test
    public void createOperationPostWithConditionalReference()
    {
        Mockito.when( bundleResourceProvider.getFhirVersion() ).thenReturn( FhirVersion.DSTU3 );
        Mockito.when( fhirClientResourceRepository.findFirstCached( Mockito.eq( fhirClient.getId() ), Mockito.eq( FhirResourceType.PATIENT ) ) )
            .thenReturn( Optional.of( patientClientResource ) );

        final FhirOperation fhirOperation = bundleResourceProvider.createOperation( null, new Patient(), "post", "Patient?identifier=832784" );

        Assert.assertTrue( fhirOperation.isProcessed() );
        Assert.assertEquals( FhirOperationResult.BAD_REQUEST_STATUS_CODE, fhirOperation.getResult().getStatusCode() );
        Assert.assertEquals( FhirOperationType.POST, fhirOperation.getOperationType() );
    }

    @Test
    public void lookupConditionalReferenceUrlUnsupportedSystem() throws Exception
    {
        Mockito.when( bundleResourceProvider.getFhirVersion() ).thenReturn( FhirVersion.DSTU3 );
        Mockito.when( fhirClientSystemRepository.findOneByFhirClientResourceType( Mockito.eq( FHIR_REST_INTERFACE_DSTU3_ID ), Mockito.eq( FhirResourceType.PATIENT ) ) )
            .thenReturn( Optional.of( patientClientSystem ) );

        final FhirOperation fhirOperation = new FhirOperation( FhirOperationType.POST, FhirResourceType.PATIENT, patientClientResource, null, baseResource3,
            new URI( "Patient?identifier=http%3A%2F%2Ftes.com%2Fpatient%7C8972" ) );
        Assert.assertNull( bundleResourceProvider.lookupConditionalReferenceUrl( fhirOperation ) );
        Assert.assertFalse( fhirOperation.isProcessed() );

        Mockito.verifyZeroInteractions( dhisRepository );
        Mockito.verifyZeroInteractions( fhirRepository );
    }

    @Test
    public void lookupConditionalReferenceUrlUnsupportedParameter() throws Exception
    {
        final FhirOperation fhirOperation = new FhirOperation( FhirOperationType.POST, FhirResourceType.PATIENT, patientClientResource, null, baseResource3,
            new URI( "Patient?identifie=http%3A%2F%2Ftest.com%2Fpatient%7C8972" ) );
        Assert.assertNull( bundleResourceProvider.lookupConditionalReferenceUrl( fhirOperation ) );
        Assert.assertTrue( fhirOperation.isProcessed() );

        Mockito.verifyZeroInteractions( dhisRepository );
        Mockito.verifyZeroInteractions( fhirRepository );
    }

    @Test
    public void getOperationTypeNull()
    {
        Assert.assertEquals( FhirOperationType.UNKNOWN, bundleResourceProvider.getOperationType( null ) );
    }

    @Test
    public void getOperationTypeOther()
    {
        Assert.assertEquals( FhirOperationType.UNKNOWN, bundleResourceProvider.getOperationType( "xpost" ) );
    }

    @Test
    public void getOperationTypePost()
    {
        Assert.assertEquals( FhirOperationType.POST, bundleResourceProvider.getOperationType( "PoSt" ) );
    }

    @Test
    public void getOperationTypePut()
    {
        Assert.assertEquals( FhirOperationType.PUT, bundleResourceProvider.getOperationType( "PuT" ) );
    }

    @Test
    public void getOperationTypeDelete()
    {
        Assert.assertEquals( FhirOperationType.DELETE, bundleResourceProvider.getOperationType( "DELete" ) );
    }

    @Test
    public void parseUriNull()
    {
        Assert.assertNull( bundleResourceProvider.parseUri( null ) );
    }

    @Test
    public void parseUriInvalid()
    {
        Assert.assertNull( bundleResourceProvider.parseUri( ":%727" ) );
    }

    @Test
    public void parseUri() throws Exception
    {
        Assert.assertEquals( new URI( "myTest" ), bundleResourceProvider.parseUri( "myTest" ) );
    }

    @Test
    public void getIdFromFullUrlNull()
    {
        Assert.assertNull( bundleResourceProvider.getIdFromFullUrl( null ) );
    }

    @Test
    public void getIdFromFullUrl()
    {
        final IIdType id = Objects.requireNonNull( bundleResourceProvider.getIdFromFullUrl( "Patient/471192" ) );

        Assert.assertEquals( new IdDt( "Patient/471192" ), id );
        Assert.assertEquals( "Patient", id.getResourceType() );
        Assert.assertEquals( "471192", id.getIdPart() );
    }

    @Test
    public void getIdFromFullUrlFull()
    {
        final IIdType id = Objects.requireNonNull( bundleResourceProvider.getIdFromFullUrl( "http://test.com/xyz/Patient/471192" ) );

        Assert.assertEquals( new IdDt( "http://test.com/xyz/Patient/471192" ), id );
        Assert.assertEquals( "Patient", id.getResourceType() );
        Assert.assertEquals( "471192", id.getIdPart() );
    }

    @Test
    public void getIdFromRequestUrl()
    {
        final IIdType id = bundleResourceProvider.getIdFromRequestUrl( "Patient/471192" );

        Assert.assertEquals( new IdDt( "Patient/471192" ), id );
        Assert.assertEquals( "Patient", id.getResourceType() );
        Assert.assertEquals( "471192", id.getIdPart() );
    }

    @Test
    public void getIdFromRequestUrlFull()
    {
        final IIdType id = bundleResourceProvider.getIdFromRequestUrl( "http://test.com/xyz/Patient/471192?identfier=827637" );

        Assert.assertEquals( new IdDt( "http://test.com/xyz/Patient/471192" ), id );
        Assert.assertEquals( "Patient", id.getResourceType() );
        Assert.assertEquals( "471192", id.getIdPart() );
    }

    @Test
    public void getStatusCodeOk()
    {
        Assert.assertEquals( "200 OK", bundleResourceProvider.getStatus( new FhirOperationResult( FhirOperationResult.OK_STATUS_CODE ) ) );
    }

    @Test
    public void getStatusCodeCreated()
    {
        Assert.assertEquals( "201 Created", bundleResourceProvider.getStatus( new FhirOperationResult( FhirOperationResult.CREATED_STATUS_CODE ) ) );
    }

    @Test
    public void getStatusCodeBadRequest()
    {
        Assert.assertEquals( "400 Bad request", bundleResourceProvider.getStatus( new FhirOperationResult( FhirOperationResult.BAD_REQUEST_STATUS_CODE ) ) );
    }

    @Test
    public void getStatusCodeUnauthorized()
    {
        Assert.assertEquals( "401 Unauthorized", bundleResourceProvider.getStatus( new FhirOperationResult( FhirOperationResult.UNAUTHORIZED_STATUS_CODE ) ) );
    }

    @Test
    public void getStatusCodeForbidden()
    {
        Assert.assertEquals( "403 Forbidden", bundleResourceProvider.getStatus( new FhirOperationResult( FhirOperationResult.FORBIDDEN_STATUS_CODE ) ) );
    }

    @Test
    public void getStatusCodeNotFound()
    {
        Assert.assertEquals( "404 Not found", bundleResourceProvider.getStatus( new FhirOperationResult( FhirOperationResult.NOT_FOUND_STATUS_CODE ) ) );
    }

    @Test
    public void getStatusCodeInternalServerError()
    {
        Assert.assertEquals( "500 Internal server error", bundleResourceProvider.getStatus( new FhirOperationResult( FhirOperationResult.INTERNAL_SERVER_ERROR_STATUS_CODE ) ) );
    }

    @Test
    public void getStatusCodeUnknown()
    {
        Assert.assertEquals( "533 Unknown", bundleResourceProvider.getStatus( new FhirOperationResult( 533 ) ) );
    }

    public static class Patient implements IBaseResource
    {
        private static final long serialVersionUID = 6079331283234508350L;

        @Override
        public IBaseMetaType getMeta()
        {
            return null;
        }

        @Override
        public IIdType getIdElement()
        {
            return null;
        }

        @Override
        public IBaseResource setId( String theId )
        {
            return null;
        }

        @Override
        public IBaseResource setId( IIdType theId )
        {
            return null;
        }

        @Override
        public FhirVersionEnum getStructureFhirVersionEnum()
        {
            return null;
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

        @Override
        public boolean hasFormatComment()
        {
            return false;
        }

        @Override
        public List<String> getFormatCommentsPre()
        {
            return null;
        }

        @Override
        public List<String> getFormatCommentsPost()
        {
            return null;
        }
    }

    public static class MeasureReport implements IBaseResource
    {
        private static final long serialVersionUID = 6079331283234508350L;

        @Override
        public IBaseMetaType getMeta()
        {
            return null;
        }

        @Override
        public IIdType getIdElement()
        {
            return null;
        }

        @Override
        public IBaseResource setId( String theId )
        {
            return null;
        }

        @Override
        public IBaseResource setId( IIdType theId )
        {
            return null;
        }

        @Override
        public FhirVersionEnum getStructureFhirVersionEnum()
        {
            return null;
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

        @Override
        public boolean hasFormatComment()
        {
            return false;
        }

        @Override
        public List<String> getFormatCommentsPre()
        {
            return null;
        }

        @Override
        public List<String> getFormatCommentsPost()
        {
            return null;
        }
    }
}