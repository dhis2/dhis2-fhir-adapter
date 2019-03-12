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
import org.springframework.cache.support.SimpleValueWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * Cache that delegates cache requests to up to two caches. The first cache must
 * be a failsafe request scope cache that is stored in memory.
 *
 * @author volsch
 */
public class RequestCache implements Cache
{
    private final Cache requestCache;

    private final Cache otherCache;

    public RequestCache( @Nonnull Cache requestCache, @Nonnull Cache otherCache )
    {
        this.requestCache = requestCache;
        this.otherCache = otherCache;
    }

    @Nonnull
    @Override
    public String getName()
    {
        return otherCache.getName();
    }

    @Nonnull
    @Override
    public Object getNativeCache()
    {
        return otherCache.getNativeCache();
    }

    @Override
    public ValueWrapper get( @Nonnull Object key )
    {
        ValueWrapper valueWrapper = requestCache.get( key );
        if ( valueWrapper != null )
        {
            return valueWrapper;
        }
        valueWrapper = otherCache.get( key );
        if ( valueWrapper != null )
        {
            requestCache.put( key, valueWrapper.get() );
        }
        return valueWrapper;
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <T> T get( @Nonnull Object key, Class<T> type )
    {
        final ValueWrapper valueWrapper = get( key );
        if ( valueWrapper == null )
        {
            return null;
        }
        if ( type == null )
        {
            return (T) valueWrapper.get();
        }
        return type.cast( valueWrapper.get() );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public <T> T get( @Nonnull Object key, @Nonnull Callable<T> valueLoader )
    {
        final ValueWrapper valueWrapper = get( key );
        if ( valueWrapper != null )
        {
            return (T) valueWrapper.get();
        }

        final T value;
        try
        {
            value = valueLoader.call();
        }
        catch ( Throwable ex )
        {
            throw new ValueRetrievalException( key, valueLoader, ex );
        }

        put( key, value );
        return value;
    }

    @Override
    public void put( @Nonnull Object key, Object value )
    {
        requestCache.put( key, value );
        otherCache.put( key, value );
    }

    @Override
    public ValueWrapper putIfAbsent( @Nonnull Object key, Object value )
    {
        requestCache.putIfAbsent( key, value );
        return toValueWrapper( otherCache.putIfAbsent( key, value ) );
    }

    @Override
    public void evict( @Nonnull Object key )
    {
        requestCache.evict( key );
        otherCache.evict( key );
    }

    @Override
    public void clear()
    {
        requestCache.clear();
        otherCache.clear();
    }

    @Nullable
    protected ValueWrapper toValueWrapper( @Nullable Object value )
    {
        return (value == null) ? null : new SimpleValueWrapper( value );
    }
}
