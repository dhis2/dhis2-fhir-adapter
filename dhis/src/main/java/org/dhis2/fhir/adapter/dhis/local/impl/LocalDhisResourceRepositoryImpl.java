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
import org.dhis2.fhir.adapter.dhis.local.LocalDhisRepositoryResultCallback;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisResourceRepository;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.TrackedEntityDhisResource;
import org.dhis2.fhir.adapter.dhis.util.CodeGenerator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Repository that stores DHIS2 resources locally.
 *
 * @param <T> the concrete type of the resource.
 * @author volsch
 */
public class LocalDhisResourceRepositoryImpl<T extends DhisResource> implements LocalDhisResourceRepository<T>
{
    private final Map<String, LocalDhisResource<T>> resourcesById = new HashMap<>();

    private final Map<String, Map<String, LocalDhisResource<T>>> resourcesByTeiId = new HashMap<>();

    private final Set<String> collectionKeys = new HashSet<>();

    private final Class<T> resourceClass;

    public LocalDhisResourceRepositoryImpl( @Nonnull Class<T> resourceClass )
    {
        this.resourceClass = resourceClass;
    }

    @Override
    @Nonnull
    public T save( @Nonnull T resource, @Nullable Object resourceKey )
    {
        if ( resource.getId() == null )
        {
            resource.setId( CodeGenerator.generateUid() );
        }

        return store( resource, resourceKey, false );
    }

    @Override
    public boolean deleteById( @Nonnull String id, @Nullable Object resourceKey, @Nonnull Function<String, T> prototypeFunction )
    {
        LocalDhisResource<T> local = resourcesById.get( id );

        if ( local == null )
        {
            local = new LocalDhisResource<>( prototypeFunction.apply( id ) );
            resourcesById.put( id, local );
        }

        if ( resourceKey != null )
        {
            local.setResourceKey( resourceKey );
        }

        local.setDeleted();

        return true;
    }

    @Override
    public boolean containsCollectionKey( @Nonnull String key )
    {
        return collectionKeys.contains( key );
    }

    @Override
    @Nonnull
    public Collection<T> found( @Nonnull Collection<T> resources, @Nonnull String key )
    {
        collectionKeys.add( key );

        if ( resources.isEmpty() )
        {
            return Collections.emptyList();
        }

        final List<T> result = new ArrayList<>( resources.size() );
        resources.forEach( r -> result.add( store( r, null, true ) ) );

        return result;
    }

    @Override
    @Nonnull
    public Optional<T> findOneById( @Nonnull String id )
    {
        final LocalDhisResource<T> local = resourcesById.get( id );

        return local == null ? Optional.empty() : Optional.of( local.getResource() );
    }

    @Override
    @Nonnull
    public Collection<T> find( @Nonnull Predicate<T> filter )
    {
        return resourcesById.values().stream().filter( lr -> !lr.getState().isDeleted() && filter.test( lr.getResource() ) )
            .map( LocalDhisResource::getResource ).collect( Collectors.toList() );
    }

    @Override
    @Nonnull
    public Collection<T> find( @Nonnull String trackedEntityInstanceId, @Nonnull Predicate<T> filter )
    {
        if ( !TrackedEntityDhisResource.class.isAssignableFrom( resourceClass ) )
        {
            throw new IllegalArgumentException( "Filtering by tracked entity instance ID is not supported on " + resourceClass.getName() );
        }

        final Map<String, LocalDhisResource<T>> resourcesById = resourcesByTeiId.get( trackedEntityInstanceId );

        if ( resourcesById == null )
        {
            return Collections.emptyList();
        }

        return resourcesById.values().stream().filter( lr -> !lr.getState().isDeleted() && filter.test( lr.getResource() ) )
            .map( LocalDhisResource::getResource ).collect( Collectors.toList() );
    }

    @Override
    public void applySaves( @Nonnull LocalDhisRepositoryPersistCallback<T> persistCallback, @Nonnull LocalDhisRepositoryResultCallback resultCallback )
    {
        applySaves( persistCallback, resultCallback, Collections.singleton( LocalDhisResourceState.SAVED_NEW ), true );
        applySaves( persistCallback, resultCallback, Collections.singleton( LocalDhisResourceState.SAVED_EXISTING ), false );
    }

    protected void applySaves( @Nonnull LocalDhisRepositoryPersistCallback<T> persistCallback, @Nonnull LocalDhisRepositoryResultCallback resultCallback, @Nonnull Set<LocalDhisResourceState> states, boolean create )
    {
        final Map<String, LocalDhisResource<T>> localResources = resourcesById.values().stream()
            .filter( lr -> states.contains( lr.getState() ) ).collect( Collectors.toMap( lr -> lr.getResource().getId(), lr -> lr ) );

        if ( !localResources.isEmpty() )
        {
            persistCallback.persistSave( localResources.values().stream().map( LocalDhisResource::getResource ).collect( Collectors.toList() ), create, result -> {
                final LocalDhisResource<T> localDhisResource = Objects.requireNonNull( localResources.get( result.getResourceId() ),
                    () -> "No request for resource with ID " + result.getResourceId() );

                resultCallback.persisted( localDhisResource.getResource(), localDhisResource.getResourceKey(), result );
            } );
        }
    }

    @Override
    public void applyDeletes( @Nonnull LocalDhisRepositoryPersistCallback<T> persistCallback, @Nonnull LocalDhisRepositoryResultCallback resultCallback )
    {
        final Map<String, LocalDhisResource<T>> localResources = resourcesById.values().stream()
            .filter( lr -> lr.getState() == LocalDhisResourceState.DELETED_EXISTING ).collect( Collectors.toMap( lr -> lr.getResource().getId(), lr -> lr ) );

        if ( !localResources.isEmpty() )
        {
            persistCallback.persistDeleteById( localResources.values().stream().map( lr -> lr.getResource().getId() ).collect( Collectors.toList() ), result -> {
                final LocalDhisResource<T> localDhisResource = Objects.requireNonNull( localResources.get( result.getResourceId() ),
                    () -> "No request for resource with ID " + result.getResourceId() );

                resultCallback.persisted( localDhisResource.getResource(), localDhisResource.getResourceKey(), result );
            } );
        }
    }

    @Nonnull
    protected T store( @Nonnull T resource, @Nullable Object resourceKey, boolean found )
    {
        LocalDhisResource<T> local = resourcesById.get( resource.getId() );

        if ( local == null )
        {
            local = new LocalDhisResource<>( resource, resourceKey, found ? LocalDhisResourceState.FOUND :
                ( resource.isNewResource() ? LocalDhisResourceState.SAVED_NEW : LocalDhisResourceState.SAVED_EXISTING ) );
            resourcesById.put( resource.getId(), local );

            if ( resource instanceof TrackedEntityDhisResource )
            {
                final TrackedEntityDhisResource trackedEntityDhisResource = (TrackedEntityDhisResource) resource;

                if ( trackedEntityDhisResource.getTrackedEntityInstanceId() != null )
                {
                    resourcesByTeiId.computeIfAbsent( trackedEntityDhisResource.getTrackedEntityInstanceId(), k -> new HashMap<>() )
                        .put( resource.getId(), local );
                }
            }
        }
        else if ( found )
        {
            // the resource that is included in local repository must be returned
            return local.getResource();
        }
        else
        {
            if ( TrackedEntityDhisResource.class.isAssignableFrom( resourceClass ) && !Objects.equals(
                ( (TrackedEntityDhisResource) local.getResource() ).getTrackedEntityInstanceId(), ( (TrackedEntityDhisResource) resource ).getTrackedEntityInstanceId() ) )
            {
                throw new IllegalArgumentException( "Tracked entity instance ID must not change" );
            }

            if ( resourceKey != null )
            {
                local.setResourceKey( resourceKey );
            }

            local.setResource( resource );

            if ( local.getState() == LocalDhisResourceState.DELETED_NEW )
            {
                local.setState( LocalDhisResourceState.SAVED_NEW );
            }
            else if ( local.getState() != LocalDhisResourceState.SAVED_NEW )
            {
                local.setState( LocalDhisResourceState.SAVED_EXISTING );
            }
        }

        // after persisting resource, resource is no longer new and no longer modified
        resource.resetNewResource();

        return resource;
    }
}
