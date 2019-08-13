package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util;

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

import ca.uhn.fhir.context.FhirContext;
import org.dhis2.fhir.adapter.fhir.metadata.model.ClientFhirEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecution;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Unit tests for {@link ReferenceFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class ReferenceFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private FhirClientResourceRepository fhirClientResourceRepository;

    @Mock
    private FhirResourceRepository fhirResourceRepository;

    @Mock
    private FhirToDhisTransformerContext context;

    @Mock
    private FhirRequest request;

    @Mock
    private ScriptExecution scriptExecution;

    @Mock
    private Map<String, Object> variables;

    @Mock
    private IBaseReference reference;

    @InjectMocks
    private ReferenceFhirToDhisTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void getResourceNull()
    {
        Assert.assertNull( utils.getResource( null, "PATIENT", false ) );
    }

    @Test
    public void getIncludedResource()
    {
        final Patient resource = new Patient();
        final IIdType id = new IdType( "Patient", "123" );
        Mockito.doReturn( resource ).when( reference ).getResource();
        Assert.assertSame( resource, utils.getResource( reference, "PATIENT", false ) );
    }

    @Test
    public void getIncludedResourceNoType()
    {
        final Patient resource = new Patient();
        final IIdType id = new IdType( null, "123" );
        Mockito.doReturn( resource ).when( reference ).getResource();
        Assert.assertSame( resource, utils.getResource( reference, "PATIENT", false ) );
    }

    @Test
    public void getIncludedResourceNoRequestType()
    {
        final Patient resource = new Patient();
        final IIdType id = new IdType( "Patient", "123" );
        Mockito.doReturn( resource ).when( reference ).getResource();
        Assert.assertSame( resource, utils.getResource( reference, null, false ) );
    }

    @Test
    public void getResource()
    {
        final ClientFhirEndpoint clientFhirEndpoint = new ClientFhirEndpoint();
        final UUID fhirClientId = UUID.randomUUID();
        final FhirClient fhirClient = new FhirClient();
        fhirClient.setId( fhirClientId );
        fhirClient.setFhirVersion( FhirVersion.R4 );
        fhirClient.setFhirEndpoint( clientFhirEndpoint );
        final FhirContext fhirContext = FhirContext.forR4();
        final UUID fhirClientResourceId = UUID.randomUUID();
        final FhirClientResource fhirClientResource = new FhirClientResource();
        fhirClientResource.setId( fhirClientResourceId );
        fhirClientResource.setFhirClient( fhirClient );
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "http://test.com", "OT_", null, null, false );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( fhirClientResourceId ).when( request ).getFhirClientResourceId();
        Mockito.doReturn( FhirVersion.R4 ).when( request ).getVersion();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.ORGANIZATION );
        Mockito.doReturn( Optional.of( fhirClientResource ) ).when( fhirClientResourceRepository ).findOneByIdCached( Mockito.eq( fhirClientResourceId ) );
        Mockito.doReturn( Optional.of( fhirContext ) ).when( fhirResourceRepository ).findFhirContext( Mockito.eq( FhirVersion.R4 ) );

        final Patient resource = new Patient();
        final IIdType id = new IdType( "Patient", "123" );
        resource.setId( id );
        final Reference reference = new Reference( id );

        Mockito.doReturn( Optional.of( resource ) ).when( fhirResourceRepository )
            .find( Mockito.eq( fhirClientId ), Mockito.eq( FhirVersion.R4 ),
                Mockito.same( clientFhirEndpoint ), Mockito.eq( "Patient" ), Mockito.eq( "123" ) );

        final Resource result = (Resource) utils.getResource( reference, null, false );
        Assert.assertTrue( result instanceof Patient );
        Assert.assertEquals( id, result.getIdElement() );
        Assert.assertNotSame( resource, result );

        Mockito.verify( fhirResourceRepository ).find( Mockito.eq( fhirClientId ), Mockito.eq( FhirVersion.R4 ),
            Mockito.same( clientFhirEndpoint ), Mockito.eq( "Patient" ), Mockito.eq( "123" ) );
    }

    @Test
    public void getResourceRefreshed()
    {
        final ClientFhirEndpoint clientFhirEndpoint = new ClientFhirEndpoint();
        final UUID fhirClientId = UUID.randomUUID();
        final FhirClient fhirClient = new FhirClient();
        fhirClient.setId( fhirClientId );
        fhirClient.setFhirVersion( FhirVersion.R4 );
        fhirClient.setFhirEndpoint( clientFhirEndpoint );
        final FhirContext fhirContext = FhirContext.forR4();
        final UUID fhirClientResourceId = UUID.randomUUID();
        final FhirClientResource fhirClientResource = new FhirClientResource();
        fhirClientResource.setId( fhirClientResourceId );
        fhirClientResource.setFhirClient( fhirClient );
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "http://test.com", "OT_", null, null, false );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( fhirClientResourceId ).when( request ).getFhirClientResourceId();
        Mockito.doReturn( FhirVersion.R4 ).when( request ).getVersion();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.ORGANIZATION );
        Mockito.doReturn( Optional.of( fhirClientResource ) ).when( fhirClientResourceRepository ).findOneByIdCached( Mockito.eq( fhirClientResourceId ) );
        Mockito.doReturn( Optional.of( fhirContext ) ).when( fhirResourceRepository ).findFhirContext( Mockito.eq( FhirVersion.R4 ) );

        final Patient resource = new Patient();
        final IIdType id = new IdType( "Patient", "123" );
        resource.setId( id );
        final Reference reference = new Reference( id );

        Mockito.doReturn( Optional.of( resource ) ).when( fhirResourceRepository )
            .findRefreshed( Mockito.eq( fhirClientId ), Mockito.eq( FhirVersion.R4 ),
                Mockito.same( clientFhirEndpoint ), Mockito.eq( "Patient" ), Mockito.eq( "123" ) );

        final Resource result = (Resource) utils.getResource( reference, null, true );
        Assert.assertTrue( result instanceof Patient );
        Assert.assertEquals( id, result.getIdElement() );
        Assert.assertNotSame( resource, result );

        Mockito.verify( fhirResourceRepository ).findRefreshed( Mockito.eq( fhirClientId ), Mockito.eq( FhirVersion.R4 ),
            Mockito.same( clientFhirEndpoint ), Mockito.eq( "Patient" ), Mockito.eq( "123" ) );
    }

    @Test
    public void initReference()
    {
        final ClientFhirEndpoint clientFhirEndpoint = new ClientFhirEndpoint();
        final UUID fhirClientId = UUID.randomUUID();
        final FhirClient fhirClient = new FhirClient();
        fhirClient.setId( fhirClientId );
        fhirClient.setFhirVersion( FhirVersion.R4 );
        fhirClient.setFhirEndpoint( clientFhirEndpoint );
        final FhirContext fhirContext = FhirContext.forR4();
        final UUID fhirClientResourceId = UUID.randomUUID();
        final FhirClientResource fhirClientResource = new FhirClientResource();
        fhirClientResource.setId( fhirClientResourceId );
        fhirClientResource.setFhirClient( fhirClient );
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "http://test.com", "OT_", null, null, false );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( fhirClientResourceId ).when( request ).getFhirClientResourceId();
        Mockito.doReturn( FhirVersion.R4 ).when( request ).getVersion();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.ORGANIZATION );
        Mockito.doReturn( Optional.of( fhirClientResource ) ).when( fhirClientResourceRepository ).findOneByIdCached( Mockito.eq( fhirClientResourceId ) );
        Mockito.doReturn( Optional.of( fhirContext ) ).when( fhirResourceRepository ).findFhirContext( Mockito.eq( FhirVersion.R4 ) );

        final Patient resource = new Patient();
        final IIdType id = new IdType( "Patient", "123" );
        resource.setId( id );
        final Reference reference = new Reference( id );

        Mockito.doReturn( Optional.of( resource ) ).when( fhirResourceRepository )
            .find( Mockito.eq( fhirClientId ), Mockito.eq( FhirVersion.R4 ),
                Mockito.same( clientFhirEndpoint ), Mockito.eq( "Patient" ), Mockito.eq( "123" ) );

        utils.initReference( reference, "PATIENT" );
        Assert.assertTrue( reference.getResource() instanceof Patient );
        Assert.assertEquals( id, reference.getResource().getIdElement() );
        Assert.assertNotSame( resource, reference.getResource() );

        Mockito.verify( fhirResourceRepository ).find( Mockito.eq( fhirClientId ), Mockito.eq( FhirVersion.R4 ),
            Mockito.same( clientFhirEndpoint ), Mockito.eq( "Patient" ), Mockito.eq( "123" ) );
    }
}