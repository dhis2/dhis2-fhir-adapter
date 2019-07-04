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
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.WritableTrackedEntityType;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Unit tests for {@link LocalDhisResourceRepositoryImpl}.
 *
 * @author volsch
 */
public class LocalDhisResourceRepositoryImplTest
{
    @Test
    public void saveWithoutId()
    {
        final LocalDhisResourceRepositoryImpl<TrackedEntityInstance> resourceRepository = new LocalDhisResourceRepositoryImpl<>( TrackedEntityInstance.class );
        final WritableTrackedEntityType trackedEntityType = new WritableTrackedEntityType();

        trackedEntityType.setAttributes( Collections.emptyList() );
        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance( trackedEntityType, null, true );

        Assert.assertSame( trackedEntityInstance, resourceRepository.save( trackedEntityInstance, null ) );

        final List<TrackedEntityInstance> found = new ArrayList<>( resourceRepository.find( r -> true ) );
        Assert.assertEquals( 1, found.size() );
        Assert.assertSame( trackedEntityInstance, found.get( 0 ) );
        Assert.assertNotNull( trackedEntityInstance.getId() );
        Assert.assertTrue( trackedEntityInstance.isLocal() );
        Assert.assertFalse( trackedEntityInstance.isNewResource() );
        Assert.assertFalse( trackedEntityInstance.isModified() );
    }

    @Test
    public void saveWithId()
    {
        final LocalDhisResourceRepositoryImpl<TrackedEntityInstance> resourceRepository = new LocalDhisResourceRepositoryImpl<>( TrackedEntityInstance.class );
        final WritableTrackedEntityType trackedEntityType = new WritableTrackedEntityType();

        trackedEntityType.setAttributes( Collections.emptyList() );
        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance( trackedEntityType, "a1234567890", true );

        Assert.assertSame( trackedEntityInstance, resourceRepository.save( trackedEntityInstance, null ) );

        final List<TrackedEntityInstance> found = new ArrayList<>( resourceRepository.find( r -> true ) );
        Assert.assertEquals( 1, found.size() );
        Assert.assertSame( trackedEntityInstance, found.get( 0 ) );
        Assert.assertEquals( "a1234567890", trackedEntityInstance.getId() );
        Assert.assertTrue( trackedEntityInstance.isLocal() );
        Assert.assertFalse( trackedEntityInstance.isNewResource() );
        Assert.assertFalse( trackedEntityInstance.isModified() );
    }

    @Test
    public void findOneById()
    {
        final LocalDhisResourceRepositoryImpl<TrackedEntityInstance> resourceRepository = new LocalDhisResourceRepositoryImpl<>( TrackedEntityInstance.class );
        final WritableTrackedEntityType trackedEntityType = new WritableTrackedEntityType();

        trackedEntityType.setAttributes( Collections.emptyList() );
        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance( trackedEntityType, "a1234567890", true );

        Assert.assertSame( trackedEntityInstance, resourceRepository.save( trackedEntityInstance, null ) );

        final TrackedEntityInstance found = resourceRepository.findOneById( "a1234567890" ).orElse( null );
        Assert.assertSame( trackedEntityInstance, found );
        Assert.assertEquals( "a1234567890", trackedEntityInstance.getId() );
    }

    @Test
    public void findOneByIdNotFound()
    {
        final LocalDhisResourceRepositoryImpl<TrackedEntityInstance> resourceRepository = new LocalDhisResourceRepositoryImpl<>( TrackedEntityInstance.class );
        final WritableTrackedEntityType trackedEntityType = new WritableTrackedEntityType();

        trackedEntityType.setAttributes( Collections.emptyList() );
        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance( trackedEntityType, "a1234567890", true );

        Assert.assertSame( trackedEntityInstance, resourceRepository.save( trackedEntityInstance, null ) );

        final TrackedEntityInstance found = resourceRepository.findOneById( "a1234567891" ).orElse( null );
        Assert.assertNull( found );
    }

    @Test
    public void find()
    {
        final LocalDhisResourceRepositoryImpl<TrackedEntityInstance> resourceRepository = new LocalDhisResourceRepositoryImpl<>( TrackedEntityInstance.class );
        final WritableTrackedEntityType trackedEntityType = new WritableTrackedEntityType();

        trackedEntityType.setAttributes( Collections.emptyList() );
        final TrackedEntityInstance trackedEntityInstance1 = new TrackedEntityInstance( trackedEntityType, "a1234567890", true );
        final TrackedEntityInstance trackedEntityInstance2 = new TrackedEntityInstance( trackedEntityType, "a1234567891", true );
        final TrackedEntityInstance trackedEntityInstance3 = new TrackedEntityInstance( trackedEntityType, "a1234567892", true );

        resourceRepository.save( trackedEntityInstance1, null );
        resourceRepository.save( trackedEntityInstance2, null );
        resourceRepository.save( trackedEntityInstance3, null );

        final List<TrackedEntityInstance> found = new ArrayList<>( resourceRepository.find( r -> !"a1234567891".equals( r.getId() ) ) );
        Assert.assertThat( found, Matchers.containsInAnyOrder( trackedEntityInstance1, trackedEntityInstance3 ) );
    }

    @Test
    public void findByTei()
    {
        final LocalDhisResourceRepositoryImpl<Enrollment> resourceRepository = new LocalDhisResourceRepositoryImpl<>( Enrollment.class );
        final WritableTrackedEntityType trackedEntityType = new WritableTrackedEntityType();

        trackedEntityType.setAttributes( Collections.emptyList() );
        final Enrollment enrollment1 = new Enrollment( true );
        enrollment1.setId( "b1234567890" );
        enrollment1.setTrackedEntityInstanceId( "a1234567890" );
        final Enrollment enrollment2 = new Enrollment( true );
        enrollment2.setId( "b1234567891" );
        enrollment2.setTrackedEntityInstanceId( "a1234567890" );
        final Enrollment enrollment3 = new Enrollment( true );
        enrollment3.setId( "b1234567892" );
        enrollment3.setTrackedEntityInstanceId( "a1234567890" );
        final Enrollment enrollment4 = new Enrollment( true );
        enrollment4.setId( "b1234567893" );
        enrollment4.setTrackedEntityInstanceId( "a1234567891" );

        resourceRepository.save( enrollment1, null );
        resourceRepository.save( enrollment2, null );
        resourceRepository.save( enrollment3, null );
        resourceRepository.save( enrollment4, null );

        final List<Enrollment> found = new ArrayList<>( resourceRepository.find( "a1234567890", r -> !"b1234567891".equals( r.getId() ) ) );
        Assert.assertThat( found, Matchers.containsInAnyOrder( enrollment1, enrollment3 ) );
    }

    @Test
    public void findByTeiNotFound()
    {
        final LocalDhisResourceRepositoryImpl<Enrollment> resourceRepository = new LocalDhisResourceRepositoryImpl<>( Enrollment.class );
        final WritableTrackedEntityType trackedEntityType = new WritableTrackedEntityType();

        trackedEntityType.setAttributes( Collections.emptyList() );
        final Enrollment enrollment4 = new Enrollment( true );
        enrollment4.setId( "b1234567893" );
        enrollment4.setTrackedEntityInstanceId( "a1234567891" );

        resourceRepository.save( enrollment4, null );

        final List<Enrollment> found = new ArrayList<>( resourceRepository.find( "a1234567890", r -> !"b1234567891".equals( r.getId() ) ) );
        Assert.assertTrue( found.isEmpty() );
    }

    @Test
    public void found()
    {
        final LocalDhisResourceRepositoryImpl<TrackedEntityInstance> resourceRepository = new LocalDhisResourceRepositoryImpl<>( TrackedEntityInstance.class );
        final WritableTrackedEntityType trackedEntityType = new WritableTrackedEntityType();

        trackedEntityType.setAttributes( Collections.emptyList() );
        final TrackedEntityInstance trackedEntityInstance1 = new TrackedEntityInstance( trackedEntityType, "a1234567890", false );
        final TrackedEntityInstance trackedEntityInstance2 = new TrackedEntityInstance( trackedEntityType, "a1234567891", false );
        final TrackedEntityInstance trackedEntityInstance3 = new TrackedEntityInstance( trackedEntityType, "a1234567892", false );
        final TrackedEntityInstance savedTrackedEntityInstance3 = new TrackedEntityInstance( trackedEntityType, "a1234567892", false );

        resourceRepository.save( savedTrackedEntityInstance3, null );

        Assert.assertFalse( resourceRepository.containsCollectionKey( "xyz" ) );
        final Collection<TrackedEntityInstance> result = resourceRepository.found( Arrays.asList( trackedEntityInstance1, trackedEntityInstance2, trackedEntityInstance3 ), "xyz" );
        Assert.assertTrue( resourceRepository.containsCollectionKey( "xyz" ) );

        Assert.assertEquals( 3, result.size() );
        Assert.assertFalse( result.contains( trackedEntityInstance3 ) );
        Assert.assertThat( result, Matchers.containsInAnyOrder( trackedEntityInstance1, trackedEntityInstance2, savedTrackedEntityInstance3 ) );
    }

    @Test
    public void saveFound()
    {
        final LocalDhisResourceRepositoryImpl<TrackedEntityInstance> resourceRepository = new LocalDhisResourceRepositoryImpl<>( TrackedEntityInstance.class );
        final WritableTrackedEntityType trackedEntityType = new WritableTrackedEntityType();

        trackedEntityType.setAttributes( Collections.emptyList() );
        final TrackedEntityInstance trackedEntityInstance1 = new TrackedEntityInstance( trackedEntityType, "a1234567890", false );
        final TrackedEntityInstance trackedEntityInstance2 = new TrackedEntityInstance( trackedEntityType, "a1234567891", false );
        final TrackedEntityInstance trackedEntityInstance3 = new TrackedEntityInstance( trackedEntityType, "a1234567892", false );
        final TrackedEntityInstance savedTrackedEntityInstance3 = new TrackedEntityInstance( trackedEntityType, "a1234567892", false );

        resourceRepository.found( Arrays.asList( trackedEntityInstance1, trackedEntityInstance2, trackedEntityInstance3 ), "xyz" );

        Assert.assertSame( savedTrackedEntityInstance3, resourceRepository.save( savedTrackedEntityInstance3, null ) );
    }

    @Test
    public void foundEmpty()
    {
        final LocalDhisResourceRepositoryImpl<TrackedEntityInstance> resourceRepository = new LocalDhisResourceRepositoryImpl<>( TrackedEntityInstance.class );

        Assert.assertFalse( resourceRepository.containsCollectionKey( "xyz" ) );
        final Collection<TrackedEntityInstance> result = resourceRepository.found( Collections.emptyList(), "xyz" );
        Assert.assertTrue( resourceRepository.containsCollectionKey( "xyz" ) );

        Assert.assertEquals( 0, result.size() );
    }

    @Test
    public void findFound()
    {
        final LocalDhisResourceRepositoryImpl<TrackedEntityInstance> resourceRepository = new LocalDhisResourceRepositoryImpl<>( TrackedEntityInstance.class );
        final WritableTrackedEntityType trackedEntityType = new WritableTrackedEntityType();

        trackedEntityType.setAttributes( Collections.emptyList() );
        final TrackedEntityInstance trackedEntityInstance1 = new TrackedEntityInstance( trackedEntityType, "a1234567890", false );
        final TrackedEntityInstance trackedEntityInstance2 = new TrackedEntityInstance( trackedEntityType, "a1234567891", false );
        final TrackedEntityInstance trackedEntityInstance3 = new TrackedEntityInstance( trackedEntityType, "a1234567892", false );
        final TrackedEntityInstance savedTrackedEntityInstance3 = new TrackedEntityInstance( trackedEntityType, "a1234567892", false );

        resourceRepository.save( savedTrackedEntityInstance3, null );

        Assert.assertFalse( resourceRepository.containsCollectionKey( "xyz" ) );
        resourceRepository.found( Arrays.asList( trackedEntityInstance1, trackedEntityInstance2, trackedEntityInstance3 ), "xyz" );
        Assert.assertTrue( resourceRepository.containsCollectionKey( "xyz" ) );

        final List<TrackedEntityInstance> found = new ArrayList<>( resourceRepository.find( r -> !"a1234567891".equals( r.getId() ) ) );
        Assert.assertThat( found, Matchers.containsInAnyOrder( trackedEntityInstance1, savedTrackedEntityInstance3 ) );
    }

    @Test
    public void applySaves()
    {
        final LocalDhisResourceRepositoryImpl<TrackedEntityInstance> resourceRepository = new LocalDhisResourceRepositoryImpl<>( TrackedEntityInstance.class );
        final WritableTrackedEntityType trackedEntityType = new WritableTrackedEntityType();

        trackedEntityType.setAttributes( Collections.emptyList() );
        final TrackedEntityInstance trackedEntityInstance1 = new TrackedEntityInstance( trackedEntityType, "a1234567890", true );
        final Object resourceKey1 = new Object();
        final TrackedEntityInstance trackedEntityInstance2 = new TrackedEntityInstance( trackedEntityType, "a1234567891", true );
        final Object resourceKey2 = new Object();
        final TrackedEntityInstance trackedEntityInstance3 = new TrackedEntityInstance( trackedEntityType, "a1234567892", false );
        final Object resourceKey3 = new Object();
        final TrackedEntityInstance trackedEntityInstance4 = new TrackedEntityInstance( trackedEntityType, "a1234567893", true );
        final Object resourceKey4 = new Object();
        final Object resourceKey4_2 = new Object();
        final TrackedEntityInstance trackedEntityInstance5 = new TrackedEntityInstance( trackedEntityType, "a1234567894", false );
        final Object resourceKey5 = new Object();
        final TrackedEntityInstance trackedEntityInstance6 = new TrackedEntityInstance( trackedEntityType, "a1234567896", true );
        final Object resourceKey6 = new Object();
        final Object resourceKey6_2 = new Object();

        resourceRepository.save( trackedEntityInstance1, resourceKey1 );
        resourceRepository.save( trackedEntityInstance2, resourceKey2 );
        resourceRepository.save( trackedEntityInstance3, resourceKey3 );
        resourceRepository.save( trackedEntityInstance4, resourceKey4 );
        resourceRepository.save( trackedEntityInstance5, resourceKey5 );
        resourceRepository.save( trackedEntityInstance6, resourceKey6 );
        resourceRepository.deleteById( "a1234567893", resourceKey4_2, TrackedEntityInstance::new );
        resourceRepository.deleteById( "a1234567894", null, TrackedEntityInstance::new );
        resourceRepository.deleteById( "a1234567895", resourceKey5, TrackedEntityInstance::new );
        resourceRepository.deleteById( "a1234567896", null, TrackedEntityInstance::new );
        resourceRepository.save( trackedEntityInstance6, resourceKey6_2 );


        final List<DhisResource> persistedResources = new ArrayList<>();
        final List<Object> persistedResourceKeys = new ArrayList<>();
        resourceRepository.applySaves( new LocalDhisRepositoryPersistCallback<TrackedEntityInstance>()
        {
            @Override
            public void persistSave( @Nonnull Collection<TrackedEntityInstance> resources, boolean create, @Nullable Consumer<LocalDhisRepositoryPersistResult> resultConsumer )
            {
                if ( create )
                {
                    Assert.assertThat( resources, Matchers.containsInAnyOrder( trackedEntityInstance1, trackedEntityInstance2, trackedEntityInstance6 ) );
                }
                else
                {
                    Assert.assertThat( resources, Matchers.containsInAnyOrder( trackedEntityInstance3 ) );
                }
                resources.forEach( resource -> {
                    assert resultConsumer != null;
                    resultConsumer.accept( new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.SUCCESS, resource.getId() ) );
                } );
            }

            @Nonnull
            @Override
            public TrackedEntityInstance persistSave( @Nonnull TrackedEntityInstance resource )
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public void persistDeleteById( @Nonnull Collection<String> ids, @Nullable Consumer<LocalDhisRepositoryPersistResult> resultConsumer )
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean persistDeleteById( @Nonnull String id )
            {
                throw new UnsupportedOperationException();
            }
        }, ( resource, resourceKey, result ) -> {
            Assert.assertEquals( LocalDhisRepositoryPersistStatus.SUCCESS, result.getStatus() );
            persistedResources.add( resource );
            persistedResourceKeys.add( resourceKey );
        } );

        Assert.assertThat( persistedResources, Matchers.containsInAnyOrder( trackedEntityInstance1, trackedEntityInstance2, trackedEntityInstance3, trackedEntityInstance6 ) );
        Assert.assertThat( persistedResourceKeys, Matchers.containsInAnyOrder( resourceKey1, resourceKey2, resourceKey3, resourceKey6_2 ) );
    }

    @Test
    public void applyDeleted()
    {
        final LocalDhisResourceRepositoryImpl<TrackedEntityInstance> resourceRepository = new LocalDhisResourceRepositoryImpl<>( TrackedEntityInstance.class );
        final WritableTrackedEntityType trackedEntityType = new WritableTrackedEntityType();

        trackedEntityType.setAttributes( Collections.emptyList() );
        final TrackedEntityInstance trackedEntityInstance1 = new TrackedEntityInstance( trackedEntityType, "a1234567890", true );
        final Object resourceKey1 = new Object();
        final TrackedEntityInstance trackedEntityInstance2 = new TrackedEntityInstance( trackedEntityType, "a1234567891", true );
        final Object resourceKey2 = new Object();
        final TrackedEntityInstance trackedEntityInstance3 = new TrackedEntityInstance( trackedEntityType, "a1234567892", false );
        final Object resourceKey3 = new Object();
        final TrackedEntityInstance trackedEntityInstance4 = new TrackedEntityInstance( trackedEntityType, "a1234567893", true );
        final Object resourceKey4 = new Object();
        final TrackedEntityInstance trackedEntityInstance5 = new TrackedEntityInstance( trackedEntityType, "a1234567894", false );
        final Object resourceKey5 = new Object();
        final Object resourceKey6 = new Object();

        resourceRepository.save( trackedEntityInstance1, resourceKey1 );
        resourceRepository.save( trackedEntityInstance2, resourceKey2 );
        resourceRepository.save( trackedEntityInstance3, resourceKey3 );
        resourceRepository.save( trackedEntityInstance4, resourceKey4 );
        resourceRepository.save( trackedEntityInstance5, resourceKey5 );
        resourceRepository.deleteById( "a1234567893", null, TrackedEntityInstance::new );
        resourceRepository.deleteById( "a1234567894", null, TrackedEntityInstance::new );
        resourceRepository.deleteById( "a1234567895", resourceKey6, TrackedEntityInstance::new );

        final List<String> persistedIds = new ArrayList<>();
        final List<Object> persistedResourceKeys = new ArrayList<>();
        resourceRepository.applyDeletes( new LocalDhisRepositoryPersistCallback<TrackedEntityInstance>()
        {
            @Override
            public void persistSave( @Nonnull Collection<TrackedEntityInstance> resources, boolean create, @Nullable Consumer<LocalDhisRepositoryPersistResult> resultConsumer )
            {
                throw new UnsupportedOperationException();
            }

            @Nonnull
            @Override
            public TrackedEntityInstance persistSave( @Nonnull TrackedEntityInstance resource )
            {
                throw new UnsupportedOperationException();
            }

            @Override
            public void persistDeleteById( @Nonnull Collection<String> ids, @Nullable Consumer<LocalDhisRepositoryPersistResult> resultConsumer )
            {
                ids.forEach( id -> {
                    assert resultConsumer != null;
                    resultConsumer.accept( new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.SUCCESS, id ) );
                } );
            }

            @Override
            public boolean persistDeleteById( @Nonnull String id )
            {
                throw new UnsupportedOperationException();
            }
        }, ( resource, resourceKey, result ) -> {
            Assert.assertEquals( LocalDhisRepositoryPersistStatus.SUCCESS, result.getStatus() );
            persistedIds.add( resource.getId() );
            persistedResourceKeys.add( resourceKey );
        } );

        Assert.assertThat( persistedIds, Matchers.containsInAnyOrder( "a1234567894", "a1234567895" ) );
        Assert.assertThat( persistedResourceKeys, Matchers.containsInAnyOrder( resourceKey5, resourceKey6 ) );
    }
}