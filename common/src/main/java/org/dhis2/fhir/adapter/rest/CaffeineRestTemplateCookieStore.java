package org.dhis2.fhir.adapter.rest;

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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine cache based implementation of the rest template cookie store.
 *
 * @author voksch
 */
public class CaffeineRestTemplateCookieStore implements RestTemplateCookieStore
{
    public static final long DEFAULT_MAX_COOKIE_LIFETIME_MILLIS = 30L * 60L * 1_000L;

    private volatile String cookieName;

    private final Cache<String, String> cookieStore;

    public CaffeineRestTemplateCookieStore()
    {
        this.cookieStore = Caffeine.newBuilder()
            .expireAfterWrite( DEFAULT_MAX_COOKIE_LIFETIME_MILLIS, TimeUnit.MILLISECONDS )
            .maximumSize( 1000L ).build();
    }

    @Nullable
    @Override
    public String getCookieName()
    {
        return cookieName;
    }

    @Override
    public void setCookieName( @Nonnull String cookieName )
    {
        this.cookieName = cookieName;
    }

    @Override
    public void add( @Nonnull String authorizationHeaderValue, @Nonnull String cookieValue )
    {
        cookieStore.put( authorizationHeaderValue, cookieValue );
    }

    @Nullable
    @Override
    public String get( @Nonnull String authorizationHeaderValue )
    {
        return cookieStore.getIfPresent( authorizationHeaderValue );
    }

    @Override
    public boolean remove( @Nonnull String authorizationHeaderValue, @Nonnull String cookieValue )
    {
        final String currentCookieValue = cookieStore.getIfPresent( authorizationHeaderValue );
        // may have been changed again, but cache does not provide operations to make thread safe operations
        if ( (currentCookieValue != null) && currentCookieValue.equals( cookieValue ) )
        {
            cookieStore.invalidate( authorizationHeaderValue );
            return true;
        }
        return false;
    }
}
