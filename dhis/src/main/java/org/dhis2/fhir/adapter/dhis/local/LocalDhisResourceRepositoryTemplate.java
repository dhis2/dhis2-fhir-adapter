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
import org.dhis2.fhir.adapter.dhis.model.DhisResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Template to access the local DHIS2 resource repository. If an request cache
 * is available, operations on DHIS2 resources will be cached in this request cache
 * in order to be applied later.
 *
 * @param <T> the concrete type of the DHIS2 resource.
 * @author volsch
 */
public class LocalDhisResourceRepositoryTemplate<T extends DhisResource>
{
    public static final String CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME = LocalDhisResourceRepositoryContainer.class.getSimpleName();

    public static final String RESOURCE_KEY_REQUEST_CACHE_ATTRIBUTE_NAME = LocalDhisResourceRepositoryTemplate.class.getSimpleName().concat( ".resourceKey" );

    private final Class<T> resourceClass;

    private final RequestCacheService requestCacheService;

    private final LocalDhisRepositoryPersistCallback<T> persistCallback;

    public LocalDhisResourceRepositoryTemplate( @Nonnull Class<T> resourceClass, @Nonnull RequestCacheService requestCacheService,
        @Nonnull LocalDhisRepositoryPersistCallback<T> persistCallback )
    {
        this.resourceClass = resourceClass;
        this.requestCacheService = requestCacheService;
        this.persistCallback = persistCallback;
    }

    @Nonnull
    public T save( @Nonnull T resource )
    {
        return save( resource, null );
    }

    @Nonnull
    public T save( @Nonnull T resource, @Nullable Consumer<T> localResourceConsumer )
    {
        final RequestCacheContext context = requestCacheService.getCurrentRequestCacheContext();
        final Optional<LocalDhisResourceRepository<T>> repository = getRepository( context );

        return repository.map( localDhisResourceRepository -> {
            final T savedResource = localDhisResourceRepository.save( resource, getResourceKey( context ) );

            if ( localResourceConsumer != null )
            {
                localResourceConsumer.accept( savedResource );
            }

            return savedResource;
        } ).orElseGet( () -> persistCallback.persistSave( resource ) );
    }

    public boolean deleteById( @Nonnull String id, @Nonnull Function<String, T> prototypeFunction )
    {
        final RequestCacheContext context = requestCacheService.getCurrentRequestCacheContext();
        final Optional<LocalDhisResourceRepository<T>> repository = getRepository( context );

        return repository.map( tLocalDhisResourceRepository -> tLocalDhisResourceRepository.deleteById( id, getResourceKey( context ), prototypeFunction ) )
            .orElseGet( () -> persistCallback.persistDeleteById( id ) );
    }

    @Nonnull
    public Optional<T> findOneById( @Nonnull String id, @Nonnull Function<String, T> callback )
    {
        final RequestCacheContext context = requestCacheService.getCurrentRequestCacheContext();
        final Optional<LocalDhisResourceRepository<T>> repository = getRepository( context );

        T result = null;

        if ( repository.isPresent() )
        {
            result = repository.get().findOneById( id ).orElse( null );
        }

        if ( result == null )
        {
            result = callback.apply( id );
        }

        return Optional.ofNullable( result );
    }

    public boolean isLocal( @Nonnull String id )
    {
        final RequestCacheContext context = requestCacheService.getCurrentRequestCacheContext();
        final Optional<LocalDhisResourceRepository<T>> repository = getRepository( context );

        return repository.map( r -> r.findOneById( id ).map( DhisResource::isLocal ).orElse( false ) ).orElse( false );

    }

    @Nonnull
    public Collection<T> find( @Nonnull Predicate<T> filter, @Nonnull Supplier<Collection<T>> collectionSupplier, boolean localOnly, @Nonnull String methodName, @Nonnull Object... args )
    {
        return find( null, filter, collectionSupplier, localOnly, methodName, args );
    }

    @Nonnull
    public Collection<T> find( @Nullable String trackedEntityInstanceId, @Nonnull Predicate<T> filter, @Nonnull Supplier<Collection<T>> collectionSupplier, boolean localOnly, @Nonnull String methodName, @Nonnull Object... args )
    {
        final RequestCacheContext context = requestCacheService.getCurrentRequestCacheContext();
        final Optional<LocalDhisResourceRepository<T>> repository = getRepository( context );
        final String key = createCollectionKey( methodName, args );

        Collection<T> result = null;

        if ( repository.isPresent() && ( localOnly || repository.get().containsCollectionKey( key ) ) )
        {
            if ( trackedEntityInstanceId == null )
            {
                result = repository.get().find( filter );
            }
            else
            {
                result = repository.get().find( trackedEntityInstanceId, filter );
            }
        }

        if ( result == null && !localOnly )
        {
            result = collectionSupplier.get();

            if ( repository.isPresent() )
            {
                result = repository.get().found( result, key );
            }
        }

        return result == null ? Collections.emptyList() : result;
    }

    @Nonnull
    private Optional<LocalDhisResourceRepository<T>> getRepository( @Nullable RequestCacheContext context )
    {
        if ( context == null )
        {
            return Optional.empty();
        }

        final LocalDhisResourceRepositoryContainer container = context.getAttribute(
            CONTAINER_REQUEST_CACHE_ATTRIBUTE_NAME, LocalDhisResourceRepositoryContainer.class );

        if ( container == null )
        {
            return Optional.empty();
        }

        return Optional.of( container.getRepository( resourceClass, persistCallback ) );
    }

    @Nullable
    private Object getResourceKey( @Nullable RequestCacheContext context )
    {
        if ( context == null )
        {
            return null;
        }

        return context.getAttribute( RESOURCE_KEY_REQUEST_CACHE_ATTRIBUTE_NAME, Object.class );
    }

    @Nonnull
    private String createCollectionKey( @Nonnull String methodName, @Nonnull Object... args )
    {
        final StringBuilder key = new StringBuilder( methodName );

        for ( final Object arg : args )
        {
            key.append( (char) 0 );

            if ( arg == null )
            {
                key.append( (char) 0 );
            }
            else
            {
                key.append( arg );
            }
        }

        return key.toString();
    }
}
