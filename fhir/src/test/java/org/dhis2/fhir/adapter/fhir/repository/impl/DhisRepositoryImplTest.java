package org.dhis2.fhir.adapter.fhir.repository.impl;

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

import org.dhis2.fhir.adapter.auth.Authorization;
import org.dhis2.fhir.adapter.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.sync.DhisResourceRepository;
import org.dhis2.fhir.adapter.dhis.sync.StoredDhisResourceService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.transform.config.FhirRestInterfaceConfig;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerRequest;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerService;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

/**
 * Unit tests for {@link DhisRepositoryImpl}.
 *
 * @author volsch
 */
public class DhisRepositoryImplTest
{
    @Mock
    private AuthorizationContext authorizationContext;

    @Mock
    private Authorization systemDhis2Authorization;

    @Mock
    private LockManager lockManager;

    @Mock
    private RequestCacheService requestCacheService;

    @Mock
    private StoredDhisResourceService storedItemService;

    @Mock
    private DhisResourceRepository dhisResourceRepository;

    @Mock
    private DhisToFhirTransformerService dhisToFhirTransformerService;

    @Mock
    private FhirResourceRepository fhirResourceRepository;

    @Mock
    private FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    @Mock
    private FhirRestInterfaceConfig fhirRestInterfaceConfig;

    @Mock
    private FhirClient fhirClient;

    private final UUID ruleId = UUID.randomUUID();

    private TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();

    @Mock
    private DhisToFhirTransformerRequest transformerRequest;

    private final TrackedEntityRule rule = new TrackedEntityRule();

    @Mock
    private IBaseResource resultResource;

    @InjectMocks
    private DhisRepositoryImpl repository;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void readQualifiedId()
    {
        Mockito.doReturn( Optional.of( trackedEntityInstance ) ).when( dhisResourceRepository )
            .findRefreshed( Mockito.eq( new DhisResourceId( DhisResourceType.TRACKED_ENTITY, "a1234567890" ) ) );
        Mockito.doReturn( transformerRequest ).when( dhisToFhirTransformerService )
            .createTransformerRequest( Mockito.same( fhirClient ), Mockito.any(), Mockito.same( trackedEntityInstance ), Mockito.eq( FhirResourceType.PATIENT ), Mockito.eq( ruleId ) );
        Mockito.doReturn( ruleId ).when( transformerRequest ).getRuleId();
        Mockito.doReturn( new DhisToFhirTransformOutcome<>( rule, resultResource ) ).when( dhisToFhirTransformerService ).transform( Mockito.same( transformerRequest ) );

        final Optional<IBaseResource> result = repository.read( fhirClient, FhirResourceType.PATIENT, new DhisFhirResourceId( DhisResourceType.TRACKED_ENTITY, "a1234567890", ruleId ) );

        Assert.assertTrue( result.isPresent() );
        Assert.assertSame( resultResource, result.get() );
    }

    @Test
    public void readNonQualifiedIdNoRule()
    {
        Mockito.doReturn( null ).when( dhisToFhirTransformerService ).findSingleRule( Mockito.same( fhirClient ), Mockito.eq( FhirResourceType.PATIENT ) );

        final Optional<IBaseResource> result = repository.read( fhirClient, FhirResourceType.PATIENT, new DhisFhirResourceId( "a1234567890" ) );

        Assert.assertFalse( result.isPresent() );
    }

    @Test
    public void readNonQualifiedIdNoSimpleIdRule()
    {
        rule.setId( ruleId );
        rule.setSimpleFhirId( false );

        Mockito.doReturn( new RuleInfo<>( rule, Collections.emptyList() ) ).when( dhisToFhirTransformerService ).findSingleRule( Mockito.same( fhirClient ), Mockito.eq( FhirResourceType.PATIENT ) );

        final Optional<IBaseResource> result = repository.read( fhirClient, FhirResourceType.PATIENT, new DhisFhirResourceId( "a1234567890" ) );

        Assert.assertFalse( result.isPresent() );
    }

    @Test
    public void readNonQualifiedId()
    {
        rule.setId( ruleId );
        rule.setSimpleFhirId( true );

        Mockito.doReturn( new RuleInfo<>( rule, Collections.emptyList() ) ).when( dhisToFhirTransformerService ).findSingleRule( Mockito.same( fhirClient ), Mockito.eq( FhirResourceType.PATIENT ) );
        Mockito.doReturn( Optional.of( trackedEntityInstance ) ).when( dhisResourceRepository )
            .findRefreshed( Mockito.eq( new DhisResourceId( DhisResourceType.TRACKED_ENTITY, "a1234567890" ) ) );
        Mockito.doReturn( transformerRequest ).when( dhisToFhirTransformerService )
            .createTransformerRequest( Mockito.same( fhirClient ), Mockito.any(), Mockito.same( trackedEntityInstance ), Mockito.eq( FhirResourceType.PATIENT ), Mockito.eq( ruleId ) );
        Mockito.doReturn( ruleId ).when( transformerRequest ).getRuleId();
        Mockito.doReturn( new DhisToFhirTransformOutcome<>( rule, resultResource ) ).when( dhisToFhirTransformerService ).transform( Mockito.same( transformerRequest ) );

        final Optional<IBaseResource> result = repository.read( fhirClient, FhirResourceType.PATIENT, new DhisFhirResourceId( "a1234567890" ) );

        Assert.assertTrue( result.isPresent() );
        Assert.assertSame( resultResource, result.get() );
    }
}