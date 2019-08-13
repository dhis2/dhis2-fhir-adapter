package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.r4;

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
import org.dhis2.fhir.adapter.fhir.repository.HierarchicallyFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecution;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.hamcrest.Matchers;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Location;
import org.hl7.fhir.r4.model.Reference;
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
 * Unit tests for {@link R4LocationFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class R4LocationFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private FhirClientResourceRepository fhirClientResourceRepository;

    @Mock
    private FhirResourceRepository fhirResourceRepository;

    @Mock
    private HierarchicallyFhirResourceRepository hierarchicallyFhirResourceRepository;

    @Mock
    private FhirToDhisTransformerContext context;

    @Mock
    private FhirRequest request;

    @Mock
    private ScriptExecution scriptExecution;

    @Mock
    private Map<String, Object> variables;

    @InjectMocks
    private R4LocationFhirToDhisTransformerUtils utils;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void findHierarchy()
    {
        final ClientFhirEndpoint clientFhirEndpoint = new ClientFhirEndpoint();
        final FhirClient fhirClient = new FhirClient();
        fhirClient.setFhirEndpoint( clientFhirEndpoint );
        final FhirContext fhirContext = FhirContext.forR4();
        final UUID fhirClientResourceId = UUID.randomUUID();
        final FhirClientResource fhirClientResource = new FhirClientResource();
        fhirClientResource.setFhirClient( fhirClient );
        final ResourceSystem resourceSystem = new ResourceSystem( FhirResourceType.LOCATION, "http://test.com", "OT_", null, null, false );
        Mockito.doReturn( scriptExecution ).when( scriptExecutionContext ).getScriptExecution();
        Mockito.doReturn( variables ).when( scriptExecution ).getVariables();
        Mockito.doReturn( context ).when( variables ).get( Mockito.eq( "context" ) );
        Mockito.doReturn( request ).when( context ).getFhirRequest();
        Mockito.doReturn( fhirClientResourceId ).when( request ).getFhirClientResourceId();
        Mockito.doReturn( FhirVersion.R4 ).when( request ).getVersion();
        Mockito.doReturn( Optional.of( resourceSystem ) ).when( request ).getOptionalResourceSystem( FhirResourceType.LOCATION );
        Mockito.doReturn( Optional.of( fhirClientResource ) ).when( fhirClientResourceRepository ).findOneByIdCached( Mockito.eq( fhirClientResourceId ) );
        Mockito.doReturn( Optional.of( fhirContext ) ).when( fhirResourceRepository ).findFhirContext( Mockito.eq( FhirVersion.R4 ) );

        final Location org1 = (Location) new Location().setId( new IdType( "Location", "1" ) );
        final Location org2 = (Location) new Location().setId( new IdType( "2" ) );
        final Location org3 = (Location) new Location().setId( new IdType( ("3") ) );
        final Location org4 = (Location) new Location().setId( new IdType( "Location", "4" ) );

        Mockito.doAnswer( invocation -> {
            final Function<IBaseResource, IBaseReference> parentReferenceFunction = invocation.getArgument( 6 );
            Assert.assertEquals( org3.getPartOf(), parentReferenceFunction.apply( org3 ) );
            Assert.assertEquals( org4.getPartOf(), parentReferenceFunction.apply( org4 ) );
            return new Bundle()
                .addEntry( new Bundle.BundleEntryComponent().setResource( org3 ) )
                .addEntry( new Bundle.BundleEntryComponent().setResource( org4 ) );
        } )
            .when( hierarchicallyFhirResourceRepository )
            .findWithParents( Mockito.eq( fhirClientResourceId ), Mockito.eq( FhirVersion.R4 ), Mockito.same( clientFhirEndpoint ),
                Mockito.eq( "Location" ), Mockito.eq( "3" ), Mockito.eq( "locationPartOf" ), Mockito.any() );

        final Reference org2Ref = new Reference( org2.getIdElement() );
        org2Ref.setResource( org2 );
        org1.setPartOf( org2Ref );
        org2.setPartOf( new Reference( org3.getIdElement() ) );
        org3.setPartOf( new Reference( org4.getIdElement() ) );

        final Reference org1Ref = new Reference( org1.getIdElement() );
        org1Ref.setResource( org1 );

        final List<? extends IBaseResource> hierarchy = utils.findHierarchy( org1Ref );
        Assert.assertThat( hierarchy, Matchers.contains( org1, org2 ) );
    }
}