package org.dhis2.fhir.adapter.cache;

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

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Cache manager that stores data in a request level cache and at the same time
 * in another cache manager. When a cache value is retrieved, first the request
 * level scope cache (if any) is checked for a value, afterwards the another
 * cache manager.
 *
 * @author volsch
 */
public class RequestCacheManager implements CacheManager
{
    private final String cacheManagerName;

    private final RequestCacheService requestCacheService;

    private final CacheManager defaultCacheManager;

    public RequestCacheManager( @Nonnull String cacheManagerName, @Nonnull RequestCacheService requestCacheService, @Nonnull CacheManager defaultCacheManager )
    {
        this.cacheManagerName = cacheManagerName;
        this.requestCacheService = requestCacheService;
        this.defaultCacheManager = defaultCacheManager;
    }

    @Nullable
    @Override
    public Cache getCache( @Nonnull String name )
    {
        final Cache defaultCache = defaultCacheManager.getCache( name );
        if ( defaultCache == null )
        {
            return null;
        }

        final RequestCacheContext requestCacheContext = requestCacheService.getCurrentRequestCacheContext();
        if ( requestCacheContext != null )
        {
            final Cache requestCache = Objects.requireNonNull(
                requestCacheContext.getCacheManager( cacheManagerName ).getCache( name ) );
            return new RequestCache( requestCache, defaultCache );
        }
        return defaultCache;
    }

    @Nonnull
    @Override
    public Collection<String> getCacheNames()
    {
        final HashSet<String> names = new HashSet<>( defaultCacheManager.getCacheNames() );
        final RequestCacheContext requestCacheContext = requestCacheService.getCurrentRequestCacheContext();
        if ( requestCacheContext != null )
        {
            names.addAll( requestCacheContext.getCacheManager( cacheManagerName ).getCacheNames() );
        }
        return names;
    }
}
