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

import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.TrackedEntityDhisResource;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Repository that stores DHIS 2 resources locally.
 *
 * @param <T> the concrete type of the resource.
 * @author volsch
 */
public class LocalDhisResourceRepository<T extends DhisResource>
{
    private final Map<String, LocalDhisResource<T>> resourcesById = new HashMap<>();

    private final Map<String, Map<String, LocalDhisResource<T>>> resourcesByTeiId = new HashMap<>();

    private final Set<String> collectionKeys = new HashSet<>();

    private final Class<T> resourceClass;

    public LocalDhisResourceRepository( @Nonnull Class<T> resourceClass )
    {
        this.resourceClass = resourceClass;
    }

    @Nonnull
    public Class<T> getResourceClass()
    {
        return resourceClass;
    }

    public void save( @Nonnull T resource )
    {
        LocalDhisResource<T> local = resourcesById.get( resource.getId() );

        if ( local == null )
        {
            local = new LocalDhisResource<>( resource, resource.isNewResource() ? LocalDhisResourceState.SAVED_NEW : LocalDhisResourceState.SAVED_EXISTING );
            resourcesById.put( resource.getId(), local );

            if ( resource instanceof TrackedEntityDhisResource )
            {
                final TrackedEntityDhisResource trackedEntityDhisResource = (TrackedEntityDhisResource) resource;

                if ( trackedEntityDhisResource.getTrackedEntityInstanceId() != null )
                {
                    resourcesByTeiId.computeIfAbsent( resource.getId(), k -> new HashMap<>() )
                        .put( trackedEntityDhisResource.getTrackedEntityInstanceId(), local );
                }
            }

            local.setState( resource.isNewResource() ? LocalDhisResourceState.SAVED_NEW : LocalDhisResourceState.SAVED_EXISTING );
        }
        else
        {
            if ( TrackedEntityDhisResource.class.isAssignableFrom( resourceClass ) && !Objects.equals(
                ( (TrackedEntityDhisResource) local.getResource() ).getTrackedEntityInstanceId(), ( (TrackedEntityDhisResource) resource ).getTrackedEntityInstanceId() ) )
            {
                throw new IllegalArgumentException( "Tracked entity instance ID must not change" );
            }

            if ( local.getState() == LocalDhisResourceState.FOUND )
            {
                local.setState( LocalDhisResourceState.SAVED_EXISTING );
            }
        }

        // after persisting resource, resource is no longer new and no longer modified
        resource.resetNewResource();
    }

    public void deleteById( @Nonnull String id )
    {
        final LocalDhisResource<T> local = resourcesById.remove( id );

        if ( local != null )
        {
            local.setDeleted();
        }
    }

    @Nonnull
    public Optional<T> findOneById( @Nonnull String id )
    {
        final LocalDhisResource<T> local = resourcesById.get( id );

        return local == null ? Optional.empty() : Optional.of( local.getResource() );
    }

    public boolean containsCollectionKey( @Nonnull String key )
    {
        return collectionKeys.contains( key );
    }

    @Nonnull
    public Collection<T> find( @Nonnull Predicate filter )
    {
        return null;
    }

    @Nonnull
    public Collection<T> find( @Nonnull String trackedEntityInstanceId, @Nonnull Predicate filter )
    {
        if ( !TrackedEntityDhisResource.class.isAssignableFrom( resourceClass ) )
        {
            throw new IllegalArgumentException( "Filtering by tracked entity instance ID is not supported on " + resourceClass.getName() );
        }

        return null;
    }
}
