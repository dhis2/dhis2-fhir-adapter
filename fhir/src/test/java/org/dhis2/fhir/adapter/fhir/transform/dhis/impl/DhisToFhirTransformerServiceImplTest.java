package org.dhis2.fhir.adapter.fhir.transform.dhis.impl;

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
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractFhirResourceDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisResource;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.dhis2.fhir.adapter.spring.StaticObjectProvider;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseMetaType;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests of {@link DhisToFhirTransformerServiceImpl}.
 *
 * @author volsch
 */
public class DhisToFhirTransformerServiceImplTest
{
    @Mock
    private LockManager lockManager;

    @Mock
    private FhirClientResourceRepository fhirClientResourceRepository;

    @Mock
    private FhirClientSystemRepository fhirClientSystemRepository;

    @Mock
    private OrganizationUnitService organizationUnitService;

    @Mock
    private RuleRepository ruleRepository;

    @Mock
    private RequestCacheService requestCacheService;

    @Mock
    private ScriptExecutor scriptExecutor;

    private DhisToFhirTransformerServiceImpl service;

    @Mock
    private FhirClient fhirClient;

    @Mock
    private ScriptedDhisResource scriptedDhisResource;

    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @Mock
    private DhisToFhirTransformerRequestImpl transformerRequest;

    private AbstractFhirResourceDhisToFhirTransformerUtils resourceTransformerUtils;

    @Mock
    private DhisToFhirTransformerContext context;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp()
    {
        resourceTransformerUtils = Mockito.mock( AbstractFhirResourceDhisToFhirTransformerUtils.class, Mockito.CALLS_REAL_METHODS );
        Mockito.doReturn( FhirVersion.R4_ONLY ).when( resourceTransformerUtils ).getFhirVersions();

        service = new DhisToFhirTransformerServiceImpl(
            lockManager, fhirClientResourceRepository, fhirClientSystemRepository, organizationUnitService,
            ruleRepository, requestCacheService,
            new StaticObjectProvider<>( new ArrayList<>() ),
            new StaticObjectProvider<>( new ArrayList<>() ),
            new StaticObjectProvider<>( new ArrayList<>() ),
            new StaticObjectProvider<>( Collections.singletonList( resourceTransformerUtils ) ),
            scriptExecutor, scriptExecutionContext );
        service = Mockito.spy( service );
    }

    @Test
    public void resolveFhirReferenceSimpleId()
    {
        final TrackedEntityRule trackedEntityRule = new TrackedEntityRule();
        trackedEntityRule.setFhirResourceType( FhirResourceType.PATIENT );
        trackedEntityRule.setSimpleFhirId( true );

        Mockito.doReturn( transformerRequest ).when( service ).createTransformerRequest( Mockito.same( fhirClient ), Mockito.any(), Mockito.same( scriptedDhisResource ), Mockito.any() );
        Mockito.when( scriptedDhisResource.getId() ).thenReturn( "a1234567890" );
        Mockito.when( transformerRequest.isSimpleFhirIdRule() ).thenReturn( true );
        Mockito.when( transformerRequest.getInput() ).thenReturn( scriptedDhisResource );
        Mockito.when( scriptedDhisResource.getId() ).thenReturn( "a1234567890" );
        Mockito.when( transformerRequest.getContext() ).thenReturn( context );
        Mockito.when( context.getFhirVersion() ).thenReturn( FhirVersion.R4 );
        Mockito.doReturn( new RuleInfo<>( trackedEntityRule, Collections.emptyList() ) ).when( transformerRequest ).nextRule();
        Mockito.doReturn( new TestResource() ).when( resourceTransformerUtils ).createResource( Mockito.eq( "Patient" ) );
        Mockito.doAnswer( invocation -> new TestReference( ( (IBaseResource) invocation.getArgument( 0 ) ).getIdElement().toString() ) ).when( resourceTransformerUtils ).createReference( Mockito.any() );

        final List<IBaseReference> references = service.resolveFhirReferences( fhirClient, scriptedDhisResource, Collections.singleton( FhirResourceType.PATIENT ), 10 );
        Assert.assertEquals( 1, references.size() );
        Assert.assertEquals( "Patient/a1234567890", references.get( 0 ).toString() );
    }

    @Test
    public void resolveFhirReference()
    {
        final TrackedEntityRule trackedEntityRule = new TrackedEntityRule();
        trackedEntityRule.setFhirResourceType( FhirResourceType.PATIENT );
        trackedEntityRule.setSimpleFhirId( true );

        final IBaseResource resource = new TestResource();
        resource.setId( new IdDt( "Patient", "a1234567890" ) );

        final DhisToFhirTransformOutcome<IBaseResource> outcome = new DhisToFhirTransformOutcome<>( trackedEntityRule, resource, false );

        Mockito.when( fhirClient.getFhirVersion() ).thenReturn( FhirVersion.R4 );
        Mockito.doReturn( transformerRequest ).when( service ).createTransformerRequest( Mockito.same( fhirClient ), Mockito.any(), Mockito.same( scriptedDhisResource ), Mockito.any() );
        Mockito.doReturn( outcome ).when( service ).transform( Mockito.same( transformerRequest ) );
        Mockito.when( scriptedDhisResource.getId() ).thenReturn( "a1234567890" );
        Mockito.when( transformerRequest.isSimpleFhirIdRule() ).thenReturn( false );
        Mockito.doAnswer( invocation -> new TestReference( ( (IBaseResource) invocation.getArgument( 0 ) ).getIdElement().toString() ) ).when( resourceTransformerUtils ).createReference( Mockito.same( resource ) );

        final List<IBaseReference> references = service.resolveFhirReferences( fhirClient, scriptedDhisResource, Collections.singleton( FhirResourceType.PATIENT ), 10 );
        Assert.assertEquals( 1, references.size() );
        Assert.assertEquals( "Patient/a1234567890", references.get( 0 ).toString() );
    }

    public static class TestResource implements IBaseResource
    {
        private static final long serialVersionUID = -2782382154208526668L;

        private IIdType id;

        @Override
        public IBaseMetaType getMeta()
        {
            return null;
        }

        @Override
        public IIdType getIdElement()
        {
            return id;
        }

        @Override
        public IBaseResource setId( String theId )
        {
            id = new IdDt( theId );
            return this;
        }

        @Override
        public IBaseResource setId( IIdType theId )
        {
            id = theId;
            return this;
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

    public static class TestReference implements IBaseReference
    {
        private static final long serialVersionUID = -3963810347197066321L;

        private String reference;

        public TestReference( String reference )
        {
            this.reference = reference;
        }

        @Override
        public IBaseResource getResource()
        {
            return null;
        }

        @Override
        public void setResource( IBaseResource theResource )
        {
            // nothing to be done
        }

        @Override
        public IIdType getReferenceElement()
        {
            return null;
        }

        @Override
        public IBaseReference setReference( String theReference )
        {
            reference = theReference;
            return this;
        }

        @Override
        public IBase setDisplay( String theValue )
        {
            return null;
        }

        @Override
        public IPrimitiveType<String> getDisplayElement()
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

        @Override
        public String toString()
        {
            return reference;
        }
    }
}
