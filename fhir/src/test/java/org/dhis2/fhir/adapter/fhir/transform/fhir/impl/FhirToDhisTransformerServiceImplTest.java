package org.dhis2.fhir.adapter.fhir.transform.fhir.impl;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RuleRepository;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.dhis2.fhir.adapter.spring.StaticObjectProvider;
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
import java.util.Optional;
import java.util.UUID;

/**
 * Unit tests for {@link FhirToDhisTransformerServiceImpl}.
 *
 * @author volsch
 */
public class FhirToDhisTransformerServiceImplTest
{
    @Mock
    private LockManager lockManager;

    @Mock
    private FhirResourceRepository fhirResourceRepository;

    @Mock
    private RuleRepository ruleRepository;

    @Mock
    private ScriptExecutor scriptExecutor;

    @Mock
    private FhirClientResource fhirClientResource;

    private FhirToDhisTransformerServiceImpl service;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp()
    {
        service = new FhirToDhisTransformerServiceImpl( lockManager, fhirResourceRepository, ruleRepository,
            new StaticObjectProvider<>( new ArrayList<>() ), new StaticObjectProvider<>( new ArrayList<>() ),
            scriptExecutor );
        service = Mockito.spy( service );
    }

    @Test
    public void getDeleteRuleInfo()
    {
        final UUID ruleId = UUID.randomUUID();
        final RuleInfo<AbstractRule> ruleInfo = new RuleInfo<>( new TrackedEntityRule(), Collections.emptyList() );

        Mockito.doReturn( FhirResourceType.PATIENT ).when( fhirClientResource ).getFhirResourceType();
        Mockito.doReturn( Optional.of( ruleInfo ) ).when( ruleRepository ).findOneImpByDhisFhirInputData( Mockito.eq( FhirResourceType.PATIENT ),
            Mockito.eq( DhisResourceType.TRACKED_ENTITY ), Mockito.eq( ruleId ) );

        final RuleInfo<? extends AbstractRule> result = service.getDeleteRuleInfo( fhirClientResource, new DhisFhirResourceId( DhisResourceType.TRACKED_ENTITY, "a0123456789", ruleId ) );
        Assert.assertSame( ruleInfo, result );
    }

    @Test
    public void getDeleteRuleInfoSimpleId()
    {
        final UUID ruleId = UUID.randomUUID();
        final RuleInfo<AbstractRule> ruleInfo = new RuleInfo<>( new TrackedEntityRule(), Collections.emptyList() );
        ruleInfo.getRule().setSimpleFhirId( true );

        Mockito.doReturn( FhirResourceType.PATIENT ).when( fhirClientResource ).getFhirResourceType();
        Mockito.doReturn( Optional.of( ruleInfo ) ).when( ruleRepository ).findOneExpByDhisFhirInputData( Mockito.eq( FhirResourceType.PATIENT ) );

        final RuleInfo<? extends AbstractRule> result = service.getDeleteRuleInfo( fhirClientResource, new DhisFhirResourceId( "a0123456789" ) );
        Assert.assertSame( ruleInfo, result );
    }

    @Test
    public void getDeleteRuleInfoNonSimpleId()
    {
        final UUID ruleId = UUID.randomUUID();
        final RuleInfo<AbstractRule> ruleInfo = new RuleInfo<>( new TrackedEntityRule(), Collections.emptyList() );
        ruleInfo.getRule().setSimpleFhirId( false );

        Mockito.doReturn( FhirResourceType.PATIENT ).when( fhirClientResource ).getFhirResourceType();
        Mockito.doReturn( Optional.of( ruleInfo ) ).when( ruleRepository ).findOneExpByDhisFhirInputData( Mockito.eq( FhirResourceType.PATIENT ) );

        final RuleInfo<? extends AbstractRule> result = service.getDeleteRuleInfo( fhirClientResource, new DhisFhirResourceId( "a0123456789" ) );
        Assert.assertNull( result );
    }
}