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

import org.dhis2.fhir.adapter.dhis.model.DhisResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Repository that stores DHIS2 resources locally.
 *
 * @param <T> the concrete type of the resource.
 * @author volsch
 */
public interface LocalDhisResourceRepository<T extends DhisResource>
{
    @Nonnull
    T save( @Nonnull T resource, @Nullable Object resourceKey );

    boolean deleteById( @Nonnull String id, @Nullable Object resourceKey, @Nonnull Function<String, T> prototypeFunction );

    boolean containsCollectionKey( @Nonnull String key );

    @Nonnull
    Collection<T> found( @Nonnull Collection<T> resources, @Nonnull String key );

    @Nonnull
    Optional<T> findOneById( @Nonnull String id );

    @Nonnull
    Collection<T> find( @Nonnull Predicate<T> filter );

    @Nonnull
    Collection<T> find( @Nonnull String trackedEntityInstanceId, @Nonnull Predicate<T> filter );

    void applySaves( @Nonnull LocalDhisRepositoryPersistCallback<T> persistCallback,
        @Nonnull LocalDhisRepositoryResultCallback resultCallback );

    void applyDeletes( @Nonnull LocalDhisRepositoryPersistCallback<T> persistCallback,
        @Nonnull LocalDhisRepositoryResultCallback resultCallback );
}
