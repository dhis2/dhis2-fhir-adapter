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
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Default implementation of {@link RequestCacheService}.
 *
 * @author volsch
 */
@Service
public class RequestCacheServiceImpl implements RequestCacheService
{
    private final ThreadLocal<RequestCacheContext> threadLocal = new ThreadLocal<>();

    @Nonnull
    @Override
    public RequestCacheContext createRequestCacheContext()
    {
        return createRequestCacheContext( false );
    }

    @Nonnull
    @Override
    public RequestCacheContext createRequestCacheContext( boolean useExisting )
    {
        final RequestCacheContext existingContext = threadLocal.get();
        if ( existingContext != null )
        {
            if ( useExisting )
            {
                return new ExistingRequestCacheContextDelegate( existingContext );
            }
            else
            {
                throw new IllegalStateException( "There is already a request cache context bound to the current thread." );
            }
        }

        final RequestCacheContextImpl context = new RequestCacheContextImpl( this );
        threadLocal.set( context );
        return context;
    }

    @Nullable
    @Override
    public RequestCacheContext getCurrentRequestCacheContext()
    {
        return threadLocal.get();
    }

    void remove( @Nonnull RequestCacheContextImpl context )
    {
        if ( threadLocal.get() != context )
        {
            throw new IllegalStateException( "A different request cache context has been bound to the thread." );
        }
        threadLocal.remove();
    }

    protected static class ExistingRequestCacheContextDelegate implements RequestCacheContext
    {
        private final RequestCacheContext requestCacheContext;

        private boolean closed;

        public ExistingRequestCacheContextDelegate( @Nonnull RequestCacheContext requestCacheContext )
        {
            this.requestCacheContext = requestCacheContext;
        }

        @Override
        public void setAttribute( @Nonnull String name, Object value )
        {
            if ( closed )
            {
                throw new IllegalStateException( "Request cache context has already been closed." );
            }

            requestCacheContext.setAttribute( name, value );
        }

        @Override
        public <T> T getAttribute( @Nonnull String name, @Nonnull Class<? extends T> valueClass )
        {
            if ( closed )
            {
                throw new IllegalStateException( "Request cache context has already been closed." );
            }

            return requestCacheContext.getAttribute( name, valueClass );
        }

        @Override
        public void removeAttribute( @Nonnull String name )
        {
            if ( closed )
            {
                throw new IllegalStateException( "Request cache context has already been closed." );
            }

            requestCacheContext.removeAttribute( name );
        }

        @Nonnull
        @Override
        public CacheManager getCacheManager( @Nonnull String name )
        {
            if ( closed )
            {
                throw new IllegalStateException( "Request cache context has already been closed." );
            }

            return requestCacheContext.getCacheManager( name );
        }

        @Override
        public void close()
        {
            if ( closed )
            {
                throw new IllegalStateException( "Request cache context has already been closed." );
            }

            closed = true;
        }
    }
}
