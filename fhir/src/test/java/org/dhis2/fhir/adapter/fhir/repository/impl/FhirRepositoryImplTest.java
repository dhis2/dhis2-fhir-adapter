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

import org.dhis2.fhir.adapter.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.dhis.sync.DhisResourceRepository;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.client.StoredFhirResourceService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.data.repository.SubscriptionFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryOperation;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryOperationType;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerService;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequestMethod;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.UUID;

/**
 * Unit tests for {@link FhirResourceRepositoryImpl}.
 *
 * @author volsch
 */
public class FhirRepositoryImplTest
{
    @Mock
    private AuthorizationContext authorizationContext;

    @Mock
    private LockManager lockManager;

    @Mock
    private RequestCacheService requestCacheService;

    @Mock
    private FhirClientSystemRepository fhirClientSystemRepository;

    @Mock
    private FhirClientResourceRepository fhirClientResourceRepository;

    @Mock
    private SubscriptionFhirResourceRepository subscriptionFhirResourceRepository;

    @Mock
    private StoredFhirResourceService storedItemService;

    @Mock
    private FhirResourceRepository fhirResourceRepository;

    @Mock
    private FhirToDhisTransformerService fhirToDhisTransformerService;

    @Mock
    private DhisResourceRepository dhisResourceRepository;

    @Mock
    private FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    @InjectMocks
    private FhirRepositoryImpl repository;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void getRequestMethodCreate()
    {
        final FhirRepositoryOperation operation = new FhirRepositoryOperation( FhirRepositoryOperationType.CREATE );
        Assert.assertEquals( FhirRequestMethod.CREATE, repository.getRequestMethod( operation ) );
    }

    @Test
    public void getRequestMethodUpdate()
    {
        final FhirRepositoryOperation operation = new FhirRepositoryOperation( FhirRepositoryOperationType.UPDATE );
        Assert.assertEquals( FhirRequestMethod.UPDATE, repository.getRequestMethod( operation ) );
    }

    @Test
    public void getRequestMethodCreateOrUpdate()
    {
        final FhirRepositoryOperation operation = new FhirRepositoryOperation( FhirRepositoryOperationType.CREATE_OR_UPDATE );
        Assert.assertEquals( FhirRequestMethod.CREATE_OR_UPDATE, repository.getRequestMethod( operation ) );
    }

    @Test
    public void createDhisFhirResourceIdNonSimple()
    {
        final TrackedEntityRule rule = new TrackedEntityRule();
        rule.setId( UUID.fromString( "1f989050-500c-4eaa-b6dd-0bef9fef7023" ) );

        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
        trackedEntityInstance.setId( "b1234567890" );

        final FhirToDhisTransformOutcome<TrackedEntityInstance> outcome = new FhirToDhisTransformOutcome<>( rule, trackedEntityInstance, true );

        Assert.assertEquals( "te-b1234567890-1f989050500c4eaab6dd0bef9fef7023", repository.createDhisFhirResourceId( outcome, trackedEntityInstance ) );
    }

    @Test
    public void createDhisFhirResourceIdSimple()
    {
        final TrackedEntityRule rule = new TrackedEntityRule();
        rule.setId( UUID.fromString( "1f989050-500c-4eaa-b6dd-0bef9fef7023" ) );
        rule.setSimpleFhirId( true );

        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance();
        trackedEntityInstance.setId( "b1234567890" );

        final FhirToDhisTransformOutcome<TrackedEntityInstance> outcome = new FhirToDhisTransformOutcome<>( rule, trackedEntityInstance, true );

        Assert.assertEquals( "b1234567890", repository.createDhisFhirResourceId( outcome, trackedEntityInstance ) );
    }
}