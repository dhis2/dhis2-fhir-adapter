package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.dstu3;

/*
 * Copyright (c) 2004-2018, University of Oslo
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
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganisationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganisationUnitService;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscription;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionFhirEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionResourceRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.RemoteHierarchicallyFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecution;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Unit tests for {@link Dstu3OrganizationFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class Dstu3OrganizationFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private OrganisationUnitService organisationUnitService;

    @Mock
    private RemoteSubscriptionResourceRepository subscriptionResourceRepository;

    @Mock
    private RemoteFhirResourceRepository remoteFhirResourceRepository;

    @Mock
    private RemoteHierarchicallyFhirResourceRepository remoteHierarchicallyFhirResourceRepository;

    @Mock
    private FhirToDhisTransformerContext context;

    @Mock
    private FhirRequest request;

    @Mock
    private ScriptExecution scriptExecution;

    @Mock
    private Map<String, Object> variables;

    @InjectMocks
    private Dstu3OrganizationFhirToDhisTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void exists()
    {
        Mockito.doReturn( Optional.of( new OrganisationUnit() ) ).when( organisationUnitService ).findOneByReference( Mockito.eq( new Reference( "ABC_123", ReferenceType.CODE ) ) );
        Assert.assertTrue( utils.exists( "ABC_123" ) );
        Mockito.verify( organisationUnitService ).findOneByReference( Mockito.eq( new Reference( "ABC_123", ReferenceType.CODE ) ) );
    }

    @Test
    public void existsNot()
    {
        Mockito.doReturn( Optional.empty() ).when( organisationUnitService ).findOneByReference( Mockito.eq( new Reference( "ABC_123", ReferenceType.CODE ) ) );
        Assert.assertFalse( utils.exists( "ABC_123" ) );
        Mockito.verify( organisationUnitService ).findOneByReference( Mockito.eq( new Reference( "ABC_123", ReferenceType.CODE ) ) );
    }

    @Test
    public void existsWithPrefix()
    {
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "http://test.com", "OT_" );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.ORGANIZATION );


        Mockito.doReturn( Optional.of( new OrganisationUnit() ) ).when( organisationUnitService ).findOneByReference( Mockito.eq( new Reference( "OT_ABC_123", ReferenceType.CODE ) ) );
        Assert.assertEquals( "OT_ABC_123", utils.existsWithPrefix( "ABC_123" ) );
        Mockito.verify( organisationUnitService ).findOneByReference( Mockito.eq( new Reference( "OT_ABC_123", ReferenceType.CODE ) ) );
    }

    @Test
    public void existsNotWithPrefix()
    {
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "http://test.com", "OT_" );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.ORGANIZATION );

        Mockito.doReturn( Optional.empty() ).when( organisationUnitService ).findOneByReference( Mockito.eq( new Reference( "OT_ABC_123", ReferenceType.CODE ) ) );
        Assert.assertNull( utils.existsWithPrefix( "ABC_123" ) );
        Mockito.verify( organisationUnitService ).findOneByReference( Mockito.eq( new Reference( "OT_ABC_123", ReferenceType.CODE ) ) );
    }

    @Test
    public void findHierarchy()
    {
        final SubscriptionFhirEndpoint subscriptionFhirEndpoint = new SubscriptionFhirEndpoint();
        final RemoteSubscription remoteSubscription = new RemoteSubscription();
        remoteSubscription.setFhirEndpoint( subscriptionFhirEndpoint );
        final FhirContext fhirContext = FhirContext.forDstu3();
        final UUID remoteSubscriptionResourceId = UUID.randomUUID();
        final RemoteSubscriptionResource remoteSubscriptionResource = new RemoteSubscriptionResource();
        remoteSubscriptionResource.setRemoteSubscription( remoteSubscription );
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.ORGANIZATION, "http://test.com", "OT_" );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( remoteSubscriptionResourceId ).when( request ).getRemoteSubscriptionResourceId();
        Mockito.doReturn( FhirVersion.DSTU3 ).when( request ).getVersion();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.ORGANIZATION );
        Mockito.doReturn( Optional.of( remoteSubscriptionResource ) ).when( subscriptionResourceRepository ).findByIdCached( Mockito.eq( remoteSubscriptionResourceId ) );
        Mockito.doReturn( Optional.of( fhirContext ) ).when( remoteFhirResourceRepository ).findFhirContext( Mockito.eq( FhirVersion.DSTU3 ) );

        final Organization org1 = (Organization) new Organization().setId( new IdType( "Organization", "1" ) );
        final Organization org2 = (Organization) new Organization().setId( new IdType( "2" ) );
        final Organization org3 = (Organization) new Organization().setId( new IdType( ("3") ) );
        final Organization org4 = (Organization) new Organization().setId( new IdType( "Organization", "4" ) );

        Mockito.doAnswer( invocation -> {
            final Function<IBaseResource, IBaseReference> parentReferenceFunction = invocation.getArgument( 6 );
            Assert.assertEquals( org3.getPartOf(), parentReferenceFunction.apply( org3 ) );
            Assert.assertEquals( org4.getPartOf(), parentReferenceFunction.apply( org4 ) );
            return new Bundle()
                .addEntry( new Bundle.BundleEntryComponent().setResource( org3 ) )
                .addEntry( new Bundle.BundleEntryComponent().setResource( org4 ) );
        } )
            .when( remoteHierarchicallyFhirResourceRepository )
            .findWithParents( Mockito.eq( remoteSubscriptionResourceId ), Mockito.eq( FhirVersion.DSTU3 ), Mockito.same( subscriptionFhirEndpoint ),
                Mockito.eq( "Organization" ), Mockito.eq( "3" ), Mockito.eq( "organizationPartOf" ), Mockito.any() );

        final org.hl7.fhir.dstu3.model.Reference org2Ref = new org.hl7.fhir.dstu3.model.Reference( org2.getIdElement() );
        org2Ref.setResource( org2 );
        org1.setPartOf( org2Ref );
        org2.setPartOf( new org.hl7.fhir.dstu3.model.Reference( org3.getIdElement() ) );
        org3.setPartOf( new org.hl7.fhir.dstu3.model.Reference( org4.getIdElement() ) );

        final org.hl7.fhir.dstu3.model.Reference org1Ref = new org.hl7.fhir.dstu3.model.Reference( org1.getIdElement() );
        org1Ref.setResource( org1 );

        final List<? extends IBaseResource> hierarchy = utils.findHierarchy( org1Ref );

    }
}