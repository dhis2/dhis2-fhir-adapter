package org.dhis2.fhir.adapter.cache.impl;

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
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of {@link RequestCacheContext}.
 * <b>Request level cache is not thread-safe.</b>
 *
 * @author volsch
 */
public class RequestCacheContextImpl implements RequestCacheContext
{
    private final RequestCacheServiceImpl service;

    private Map<String, Object> attributes;

    private Map<String, CacheManager> cacheManagers;

    public RequestCacheContextImpl( RequestCacheServiceImpl service )
    {
        this.service = service;
    }

    @Override
    public void setAttribute( @Nonnull String name, Object value )
    {
        if ( attributes == null )
        {
            attributes = new HashMap<>();
        }

        attributes.put( name, value );
    }

    @Override
    public <T> T getAttribute( @Nonnull String name, @Nonnull Class<? extends T> valueClass )
    {
        if ( attributes == null )
        {
            return null;
        }

        return valueClass.cast( attributes.get( name ) );
    }

    @Override
    public void removeAttribute( @Nonnull String name )
    {
        if ( attributes != null )
        {
            attributes.remove( name );
        }
    }

    @Nonnull
    @Override
    public CacheManager getCacheManager( @Nonnull String name )
    {
        if ( cacheManagers == null )
        {
            cacheManagers = new HashMap<>();
        }

        CacheManager cacheManager = cacheManagers.get( name );

        if ( cacheManager == null )
        {
            cacheManager = new ConcurrentMapCacheManager()
            {

            };

            cacheManagers.put( name, cacheManager );
        }

        return cacheManager;
    }

    @Override
    public void close()
    {
        service.remove( this );
    }
}
