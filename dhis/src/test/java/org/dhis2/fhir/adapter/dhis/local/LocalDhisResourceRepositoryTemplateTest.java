package org.dhis2.fhir.adapter.dhis.local;

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

import org.dhis2.fhir.adapter.cache.RequestCacheContext;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * Unit tests for {@link LocalDhisResourceRepositoryTemplate}.
 *
 * @author volsch
 */
public class LocalDhisResourceRepositoryTemplateTest
{
    private LocalDhisResourceRepositoryTemplate<TrackedEntityInstance> template;

    @Mock
    private RequestCacheService requestCacheService;

    @Mock
    private RequestCacheContext requestCacheContext;

    @Mock
    private LocalDhisRepositoryPersistCallback<TrackedEntityInstance> persistCallback;

    @Mock
    private LocalDhisResourceRepositoryContainer resourceRepositoryContainer;

    @Mock
    private LocalDhisResourceRepository<TrackedEntityInstance> resourceRepository;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setUp()
    {
        template = new LocalDhisResourceRepositoryTemplate<>( TrackedEntityInstance.class, requestCacheService, persistCallback );
    }

    @Test
    public void saveNoLocal()
    {
        final TrackedEntityInstance tei = new TrackedEntityInstance();

        Mockito.doReturn( tei ).when( persistCallback ).persistSave( Mockito.same( tei ) );

        Assert.assertSame( tei, template.save( tei ) );

        Mockito.verify( persistCallback ).persistSave( Mockito.same( tei ) );
    }

    @Test
    public void saveLocal()
    {
        final TrackedEntityInstance tei = new TrackedEntityInstance();
        final Object resourceKey = new Object();

        Mockito.doReturn( requestCacheContext ).when( requestCacheService ).getCurrentRequestCacheContext();
        Mockito.doReturn( resourceRepositoryContainer ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME ),
            Mockito.eq( LocalDhisResourceRepositoryContainer.class ) );
        Mockito.doReturn( resourceKey ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.RESOURCE_KEY_REQUEST_CACHE_ATTRIBUTE_NAME ), Mockito.eq( Object.class ) );
        Mockito.doReturn( resourceRepository ).when( resourceRepositoryContainer ).getRepository( Mockito.eq( TrackedEntityInstance.class ), Mockito.same( persistCallback ) );
        Mockito.doReturn( tei ).when( resourceRepository ).save( Mockito.same( tei ), Mockito.same( resourceKey ) );

        Assert.assertSame( tei, template.save( tei ) );

        Mockito.verify( resourceRepository ).save( Mockito.same( tei ), Mockito.same( resourceKey ) );
        Mockito.verifyNoMoreInteractions( resourceRepository );
        Mockito.verifyZeroInteractions( persistCallback );
    }

    @Test
    public void saveLocalConsumer()
    {
        final TrackedEntityInstance tei = new TrackedEntityInstance();
        final Object resourceKey = new Object();

        Mockito.doReturn( requestCacheContext ).when( requestCacheService ).getCurrentRequestCacheContext();
        Mockito.doReturn( resourceRepositoryContainer ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME ),
            Mockito.eq( LocalDhisResourceRepositoryContainer.class ) );
        Mockito.doReturn( resourceKey ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.RESOURCE_KEY_REQUEST_CACHE_ATTRIBUTE_NAME ), Mockito.eq( Object.class ) );
        Mockito.doReturn( resourceRepository ).when( resourceRepositoryContainer ).getRepository( Mockito.eq( TrackedEntityInstance.class ), Mockito.same( persistCallback ) );
        Mockito.doReturn( tei ).when( resourceRepository ).save( Mockito.same( tei ), Mockito.same( resourceKey ) );

        final AtomicReference<TrackedEntityInstance> consumer = new AtomicReference<>();
        Assert.assertSame( tei, template.save( tei, consumer::set ) );
        Assert.assertSame( tei, consumer.get() );

        Mockito.verify( resourceRepository ).save( Mockito.same( tei ), Mockito.same( resourceKey ) );
        Mockito.verifyNoMoreInteractions( resourceRepository );
        Mockito.verifyZeroInteractions( persistCallback );
    }

    @Test
    public void deleteByIdNoLocal()
    {
        final TrackedEntityInstance tei = new TrackedEntityInstance();

        Mockito.doReturn( true ).when( persistCallback ).persistDeleteById( Mockito.eq( "h1234567890" ) );

        Assert.assertTrue( template.deleteById( "h1234567890", TrackedEntityInstance::new ) );

        Mockito.verify( persistCallback ).persistDeleteById( Mockito.eq( "h1234567890" ) );
    }

    @Test
    public void deleteByIdLocal()
    {
        final TrackedEntityInstance tei = new TrackedEntityInstance();
        final Object resourceKey = new Object();

        Mockito.doReturn( requestCacheContext ).when( requestCacheService ).getCurrentRequestCacheContext();
        Mockito.doReturn( resourceRepositoryContainer ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME ),
            Mockito.eq( LocalDhisResourceRepositoryContainer.class ) );
        Mockito.doReturn( resourceKey ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.RESOURCE_KEY_REQUEST_CACHE_ATTRIBUTE_NAME ), Mockito.eq( Object.class ) );
        Mockito.doReturn( resourceRepository ).when( resourceRepositoryContainer ).getRepository( Mockito.eq( TrackedEntityInstance.class ), Mockito.same( persistCallback ) );
        Mockito.doReturn( true ).when( resourceRepository ).deleteById( Mockito.eq( "h1234567890" ), Mockito.same( resourceKey ), Mockito.any() );

        Assert.assertTrue( template.deleteById( "h1234567890", TrackedEntityInstance::new ) );

        Mockito.verify( resourceRepository ).deleteById( Mockito.eq( "h1234567890" ), Mockito.same( resourceKey ), Mockito.any() );
        Mockito.verifyNoMoreInteractions( resourceRepository );
        Mockito.verifyZeroInteractions( persistCallback );
    }

    @Test
    public void isLocal()
    {
        final TrackedEntityInstance tei = new TrackedEntityInstance();
        final Object resourceKey = new Object();

        tei.setLocal( true );

        Mockito.doReturn( requestCacheContext ).when( requestCacheService ).getCurrentRequestCacheContext();
        Mockito.doReturn( resourceRepositoryContainer ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME ),
            Mockito.eq( LocalDhisResourceRepositoryContainer.class ) );
        Mockito.doReturn( resourceKey ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.RESOURCE_KEY_REQUEST_CACHE_ATTRIBUTE_NAME ), Mockito.eq( Object.class ) );
        Mockito.doReturn( resourceRepository ).when( resourceRepositoryContainer ).getRepository( Mockito.eq( TrackedEntityInstance.class ), Mockito.same( persistCallback ) );
        Mockito.doReturn( Optional.of( tei ) ).when( resourceRepository ).findOneById( Mockito.eq( "h1234567890" ) );

        Assert.assertTrue( template.isLocal( "h1234567890" ) );

        Mockito.verifyZeroInteractions( persistCallback );
    }

    @Test
    public void isLocalIncludedNot()
    {
        final TrackedEntityInstance tei = new TrackedEntityInstance();
        final Object resourceKey = new Object();

        Mockito.doReturn( requestCacheContext ).when( requestCacheService ).getCurrentRequestCacheContext();
        Mockito.doReturn( resourceRepositoryContainer ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME ),
            Mockito.eq( LocalDhisResourceRepositoryContainer.class ) );
        Mockito.doReturn( resourceKey ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.RESOURCE_KEY_REQUEST_CACHE_ATTRIBUTE_NAME ), Mockito.eq( Object.class ) );
        Mockito.doReturn( resourceRepository ).when( resourceRepositoryContainer ).getRepository( Mockito.eq( TrackedEntityInstance.class ), Mockito.same( persistCallback ) );
        Mockito.doReturn( Optional.of( tei ) ).when( resourceRepository ).findOneById( Mockito.eq( "h1234567890" ) );

        Assert.assertFalse( template.isLocal( "h1234567890" ) );

        Mockito.verifyZeroInteractions( persistCallback );
    }

    @Test
    public void isLocalNotIncluded()
    {
        final TrackedEntityInstance tei = new TrackedEntityInstance();
        final Object resourceKey = new Object();

        Mockito.doReturn( requestCacheContext ).when( requestCacheService ).getCurrentRequestCacheContext();
        Mockito.doReturn( resourceRepositoryContainer ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME ),
            Mockito.eq( LocalDhisResourceRepositoryContainer.class ) );
        Mockito.doReturn( resourceKey ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.RESOURCE_KEY_REQUEST_CACHE_ATTRIBUTE_NAME ), Mockito.eq( Object.class ) );
        Mockito.doReturn( resourceRepository ).when( resourceRepositoryContainer ).getRepository( Mockito.eq( TrackedEntityInstance.class ), Mockito.same( persistCallback ) );
        Mockito.doReturn( Optional.empty() ).when( resourceRepository ).findOneById( Mockito.eq( "h1234567890" ) );

        Assert.assertFalse( template.isLocal( "h1234567890" ) );

        Mockito.verifyZeroInteractions( persistCallback );
    }

    @Test
    public void findOneByIdNoLocal()
    {
        final TrackedEntityInstance tei = new TrackedEntityInstance();

        Assert.assertSame( tei, template.findOneById( "h1234567890", id -> tei ).orElse( null ) );

        Mockito.verifyZeroInteractions( resourceRepository );
        Mockito.verifyZeroInteractions( persistCallback );
    }

    @Test
    public void findOneByIdLocal()
    {
        final TrackedEntityInstance tei = new TrackedEntityInstance();
        final TrackedEntityInstance tei2 = new TrackedEntityInstance();

        Mockito.doReturn( requestCacheContext ).when( requestCacheService ).getCurrentRequestCacheContext();
        Mockito.doReturn( resourceRepositoryContainer ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME ),
            Mockito.eq( LocalDhisResourceRepositoryContainer.class ) );
        Mockito.doReturn( resourceRepository ).when( resourceRepositoryContainer ).getRepository( Mockito.eq( TrackedEntityInstance.class ), Mockito.same( persistCallback ) );
        Mockito.doReturn( Optional.of( tei ) ).when( resourceRepository ).findOneById( Mockito.eq( "h1234567890" ) );

        Assert.assertSame( tei, template.findOneById( "h1234567890", id -> tei2 ).orElse( null ) );
    }

    @Test
    public void findOneByIdLocalNotFound()
    {
        final TrackedEntityInstance tei = new TrackedEntityInstance();

        Mockito.doReturn( requestCacheContext ).when( requestCacheService ).getCurrentRequestCacheContext();
        Mockito.doReturn( resourceRepositoryContainer ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME ),
            Mockito.eq( LocalDhisResourceRepositoryContainer.class ) );
        Mockito.doReturn( resourceRepository ).when( resourceRepositoryContainer ).getRepository( Mockito.eq( TrackedEntityInstance.class ), Mockito.same( persistCallback ) );
        Mockito.doReturn( Optional.empty() ).when( resourceRepository ).findOneById( Mockito.eq( "h1234567890" ) );

        Assert.assertSame( tei, template.findOneById( "h1234567890", id -> tei ).orElse( null ) );
    }

    @Test
    public void findNoLocal()
    {
        final Predicate<TrackedEntityInstance> filter = tei -> true;
        final List<TrackedEntityInstance> result = Arrays.asList( new TrackedEntityInstance(), new TrackedEntityInstance() );

        final Collection<TrackedEntityInstance> r = template.find( filter, () -> result, false, "test", "val1", null, "val2" );
        Assert.assertThat( r, Matchers.containsInAnyOrder( result.get( 0 ), result.get( 1 ) ) );
    }

    @Test
    public void findLocal()
    {
        final Predicate<TrackedEntityInstance> filter = tei -> true;
        final List<TrackedEntityInstance> result = Arrays.asList( new TrackedEntityInstance(), new TrackedEntityInstance() );
        final List<TrackedEntityInstance> otherResult = Collections.singletonList( new TrackedEntityInstance() );

        final TrackedEntityInstance tei = new TrackedEntityInstance();

        Mockito.doReturn( requestCacheContext ).when( requestCacheService ).getCurrentRequestCacheContext();
        Mockito.doReturn( resourceRepositoryContainer ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME ),
            Mockito.eq( LocalDhisResourceRepositoryContainer.class ) );
        Mockito.doReturn( resourceRepository ).when( resourceRepositoryContainer ).getRepository( Mockito.eq( TrackedEntityInstance.class ), Mockito.same( persistCallback ) );
        Mockito.doReturn( result ).when( resourceRepository ).find( Mockito.same( filter ) );
        Mockito.doReturn( true ).when( resourceRepository ).containsCollectionKey( Mockito.eq( "test\u0000val1\u0000\u0000\u0000val2" ) );

        final Collection<TrackedEntityInstance> r = template.find( filter, () -> otherResult, false, "test", "val1", null, "val2" );
        Assert.assertThat( r, Matchers.containsInAnyOrder( result.get( 0 ), result.get( 1 ) ) );
    }

    @Test
    public void findLocalTrackedEntity()
    {
        final Predicate<TrackedEntityInstance> filter = tei -> true;
        final List<TrackedEntityInstance> result = Arrays.asList( new TrackedEntityInstance(), new TrackedEntityInstance() );
        final List<TrackedEntityInstance> otherResult = Collections.singletonList( new TrackedEntityInstance() );

        final TrackedEntityInstance tei = new TrackedEntityInstance();

        Mockito.doReturn( requestCacheContext ).when( requestCacheService ).getCurrentRequestCacheContext();
        Mockito.doReturn( resourceRepositoryContainer ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME ),
            Mockito.eq( LocalDhisResourceRepositoryContainer.class ) );
        Mockito.doReturn( resourceRepository ).when( resourceRepositoryContainer ).getRepository( Mockito.eq( TrackedEntityInstance.class ), Mockito.same( persistCallback ) );
        Mockito.doReturn( result ).when( resourceRepository ).find( Mockito.eq( "yeudhdgdh" ), Mockito.same( filter ) );
        Mockito.doReturn( true ).when( resourceRepository ).containsCollectionKey( Mockito.eq( "test\u0000val1\u0000\u0000\u0000val2" ) );

        final Collection<TrackedEntityInstance> r = template.find( "yeudhdgdh", filter, () -> otherResult, false, "test", "val1", null, "val2" );
        Assert.assertThat( r, Matchers.containsInAnyOrder( result.get( 0 ), result.get( 1 ) ) );
    }

    @Test
    public void findLocalNotFound()
    {
        final Predicate<TrackedEntityInstance> filter = tei -> true;
        final List<TrackedEntityInstance> result = Arrays.asList( new TrackedEntityInstance(), new TrackedEntityInstance() );
        final List<TrackedEntityInstance> otherResult = Collections.singletonList( new TrackedEntityInstance() );

        final TrackedEntityInstance tei = new TrackedEntityInstance();

        Mockito.doReturn( requestCacheContext ).when( requestCacheService ).getCurrentRequestCacheContext();
        Mockito.doReturn( resourceRepositoryContainer ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME ),
            Mockito.eq( LocalDhisResourceRepositoryContainer.class ) );
        Mockito.doReturn( resourceRepository ).when( resourceRepositoryContainer ).getRepository( Mockito.eq( TrackedEntityInstance.class ), Mockito.same( persistCallback ) );
        Mockito.doReturn( result ).when( resourceRepository ).find( Mockito.same( filter ) );
        Mockito.doReturn( true ).when( resourceRepository ).containsCollectionKey( Mockito.eq( "test\u0000val1\u0000\u0000\u0000val2" ) );

        final Collection<TrackedEntityInstance> r = template.find( filter, () -> otherResult, false, "test", "val1", null, "val2" );
        Assert.assertThat( r, Matchers.containsInAnyOrder( result.get( 0 ), result.get( 1 ) ) );
    }

    @Test
    public void findLocalNotFoundNoPrevious()
    {
        final Predicate<TrackedEntityInstance> filter = tei -> true;
        final List<TrackedEntityInstance> result = Arrays.asList( new TrackedEntityInstance(), new TrackedEntityInstance() );
        final List<TrackedEntityInstance> otherResult = Collections.singletonList( new TrackedEntityInstance() );
        final List<TrackedEntityInstance> otherResultMerged = Collections.singletonList( new TrackedEntityInstance() );

        final TrackedEntityInstance tei = new TrackedEntityInstance();

        Mockito.doReturn( requestCacheContext ).when( requestCacheService ).getCurrentRequestCacheContext();
        Mockito.doReturn( resourceRepositoryContainer ).when( requestCacheContext ).getAttribute(
            Mockito.eq( LocalDhisResourceRepositoryTemplate.CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME ),
            Mockito.eq( LocalDhisResourceRepositoryContainer.class ) );
        Mockito.doReturn( resourceRepository ).when( resourceRepositoryContainer ).getRepository( Mockito.eq( TrackedEntityInstance.class ), Mockito.same( persistCallback ) );
        Mockito.doReturn( false ).when( resourceRepository ).containsCollectionKey( Mockito.eq( "test\u0000val1\u0000\u0000\u0000val2" ) );
        Mockito.doReturn( otherResultMerged ).when( resourceRepository ).found( Mockito.eq( otherResult ), Mockito.eq( "test\u0000val1\u0000\u0000\u0000val2" ) );

        final Collection<TrackedEntityInstance> r = template.find( filter, () -> otherResult, false, "test", "val1", null, "val2" );
        Assert.assertThat( r, Matchers.containsInAnyOrder( otherResultMerged.get( 0 ) ) );
    }
}
