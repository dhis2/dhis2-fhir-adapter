package org.dhis2.fhir.adapter.dhis.local.impl;

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

import org.dhis2.fhir.adapter.dhis.local.LocalDhisRepositoryPersistCallback;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisRepositoryPersistResult;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisRepositoryPersistStatus;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisResourceRepository;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;

/**
 * Unit tests for {@link LocalDhisResourceRepositoryContainerImpl}.
 *
 * @author volsch
 */
public class LocalDhisResourceRepositoryContainerImplTest
{
    private LocalDhisResourceRepositoryContainerImpl container;

    @Mock
    private LocalDhisRepositoryPersistCallback<TrackedEntityInstance> teiLocalDhisRepositoryPersistCallback;

    @Mock
    private LocalDhisRepositoryPersistCallback<Enrollment> enrollmentLocalDhisRepositoryPersistCallback;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp()
    {
        container = new LocalDhisResourceRepositoryContainerImpl( new LinkedHashSet<>( Arrays.asList( TrackedEntityInstance.class, Enrollment.class, Event.class ) ) );
    }

    @Test
    public void getRepository()
    {
        final LocalDhisResourceRepository<?> r1 = container.getRepository( TrackedEntityInstance.class, teiLocalDhisRepositoryPersistCallback );
        final LocalDhisResourceRepository<?> r2 = container.getRepository( TrackedEntityInstance.class, teiLocalDhisRepositoryPersistCallback );
        final LocalDhisResourceRepository<?> r3 = container.getRepository( Enrollment.class, enrollmentLocalDhisRepositoryPersistCallback );

        Assert.assertSame( r1, r2 );
        Assert.assertNotSame( r1, r3 );
    }

    @Test
    public void apply()
    {
        final LocalDhisResourceRepository<TrackedEntityInstance> r1 = container.getRepository( TrackedEntityInstance.class, teiLocalDhisRepositoryPersistCallback );
        final LocalDhisResourceRepository<Enrollment> r2 = container.getRepository( Enrollment.class, enrollmentLocalDhisRepositoryPersistCallback );

        Mockito.doAnswer( invocation -> {
            final Collection<? extends DhisResource> resources = invocation.getArgument( 0 );
            final Consumer<LocalDhisRepositoryPersistResult> consumer = invocation.getArgument( 2 );
            resources.forEach( r -> consumer.accept( new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.SUCCESS, r.getId() ) ) );

            return null;
        } ).when( teiLocalDhisRepositoryPersistCallback ).persistSave( Mockito.anyCollection(), Mockito.anyBoolean(), Mockito.any() );

        Mockito.doAnswer( invocation -> {
            final Collection<String> ids = invocation.getArgument( 0 );
            final Consumer<LocalDhisRepositoryPersistResult> consumer = invocation.getArgument( 1 );
            ids.forEach( id -> consumer.accept( new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.SUCCESS, id ) ) );

            return null;
        } ).when( teiLocalDhisRepositoryPersistCallback ).persistDeleteById( Mockito.anyCollection(), Mockito.any() );

        Mockito.doAnswer( invocation -> {
            final Collection<? extends DhisResource> resources = invocation.getArgument( 0 );
            final Consumer<LocalDhisRepositoryPersistResult> consumer = invocation.getArgument( 2 );
            resources.forEach( r -> consumer.accept( new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.SUCCESS, r.getId() ) ) );

            return null;
        } ).when( enrollmentLocalDhisRepositoryPersistCallback ).persistSave( Mockito.anyCollection(), Mockito.anyBoolean(), Mockito.any() );

        Mockito.doAnswer( invocation -> {
            final Collection<String> ids = invocation.getArgument( 0 );
            final Consumer<LocalDhisRepositoryPersistResult> consumer = invocation.getArgument( 1 );
            ids.forEach( id -> consumer.accept( new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.SUCCESS, id ) ) );

            return null;
        } ).when( enrollmentLocalDhisRepositoryPersistCallback ).persistDeleteById( Mockito.anyCollection(), Mockito.any() );

        final TrackedEntityInstance trackedEntityInstance1 = new TrackedEntityInstance();
        trackedEntityInstance1.setId( "A1234567890" );
        r1.save( trackedEntityInstance1, null );
        r1.deleteById( "A1234567891", null, TrackedEntityInstance::new );

        final Enrollment enrollment1 = new Enrollment();
        enrollment1.setId( "B1234567890" );
        r2.save( enrollment1, null );
        r2.deleteById( "B1234567891", null, Enrollment::new );

        final List<String> ids = new ArrayList<>();
        container.apply( ( resource, resourceKey, result ) -> ids.add( resource.getId() ) );

        Assert.assertThat( ids, Matchers.contains( "A1234567890", "B1234567890", "B1234567891", "A1234567891" ) );
    }
}