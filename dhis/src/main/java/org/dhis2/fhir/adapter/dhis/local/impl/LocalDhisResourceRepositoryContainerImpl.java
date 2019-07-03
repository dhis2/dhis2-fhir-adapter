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

import com.google.common.collect.Lists;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisRepositoryPersistCallback;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisRepositoryResultCallback;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisResourceRepository;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisResourceRepositoryContainer;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Container with resource type specific repositories. Instances of this class
 * are not thread safe and must not be used by multiple threads at the same time.
 *
 * @author volsch
 */
public class LocalDhisResourceRepositoryContainerImpl implements LocalDhisResourceRepositoryContainer
{
    private final Set<Class<? extends DhisResource>> supportedResourceClasses;

    private final Map<Class<? extends DhisResource>, RepositoryItem<? extends DhisResource>> repositoryItems = new HashMap<>();

    public LocalDhisResourceRepositoryContainerImpl( @Nonnull Set<Class<? extends DhisResource>> supportedResourceClasses )
    {
        this.supportedResourceClasses = new LinkedHashSet<>( supportedResourceClasses );
    }

    @Nonnull
    @Override
    @SuppressWarnings( "unchecked" )
    public <T extends DhisResource> LocalDhisResourceRepository<T> getRepository(
        @Nonnull Class<T> resourceClass, @Nonnull LocalDhisRepositoryPersistCallback<T> persistCallback )
    {
        if ( !supportedResourceClasses.contains( resourceClass ) )
        {
            throw new IllegalArgumentException( "Resource class is not supported: " + resourceClass );
        }

        final RepositoryItem<? extends DhisResource> repositoryItem = repositoryItems.computeIfAbsent( resourceClass,
            rc -> new RepositoryItem<>( new LocalDhisResourceRepositoryImpl<>( resourceClass ), persistCallback ) );

        return (LocalDhisResourceRepository<T>) repositoryItem.getResourceRepository();
    }

    @Override
    public void apply( @Nonnull LocalDhisRepositoryResultCallback resultCallback )
    {
        supportedResourceClasses.stream().map( repositoryItems::get ).filter( Objects::nonNull )
            .forEach( ri -> ri.applySaves( resultCallback ) );

        // deletion should be made in reverse order in order to honor constraints between resources
        Lists.reverse( new ArrayList<>( supportedResourceClasses ) ).stream().map( repositoryItems::get ).filter( Objects::nonNull )
            .forEach( ri -> ri.applyDeletes( resultCallback ) );
    }

    protected static class RepositoryItem<T extends DhisResource>
    {
        private final LocalDhisResourceRepository<T> resourceRepository;

        private LocalDhisRepositoryPersistCallback<T> persistCallback;

        public RepositoryItem( @Nonnull LocalDhisResourceRepository<T> resourceRepository, @Nonnull LocalDhisRepositoryPersistCallback<T> persistCallback )
        {
            this.resourceRepository = resourceRepository;
            this.persistCallback = persistCallback;
        }

        @Nonnull
        public LocalDhisResourceRepository<T> getResourceRepository()
        {
            return resourceRepository;
        }

        @Nonnull
        public LocalDhisRepositoryPersistCallback<T> getPersistCallback()
        {
            return persistCallback;
        }

        public void applySaves( @Nonnull LocalDhisRepositoryResultCallback resultCallback )
        {
            resourceRepository.applySaves( persistCallback, resultCallback );
        }

        public void applyDeletes( @Nonnull LocalDhisRepositoryResultCallback resultCallback )
        {
            resourceRepository.applyDeletes( persistCallback, resultCallback );
        }
    }
}
