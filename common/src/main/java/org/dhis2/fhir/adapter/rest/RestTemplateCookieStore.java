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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Cookie store to be used with Spring rest template. The session cookie is stored
 * together with the authorization header value.
 *
 * @author volsch
 */
public interface RestTemplateCookieStore
{
    /**
     * @return the name of the cookie that contains the session cookie or
     * <code>null</code> if this is not yet known.
     */
    @Nullable
    String getCookieName();

    /**
     * @param cookieName he name of the cookie that contains the session cookie.
     */
    void setCookieName( @Nonnull String cookieName );

    /**
     * Replaces the existing cookie for the specified authorization header.
     *
     * @param authorizationHeaderValue the authorization header value to which the cookie value should be associated.
     * @param cookieValue              the session cookie value that should be bound to the authorization header.
     */
    void add( @Nonnull String authorizationHeaderValue, @Nonnull String cookieValue );

    /**
     * @param authorizationHeaderValue the value of the authorization header for which the cookie should be returned.
     * @return the session cookie value that belongs to the authorization header.
     */
    @Nullable
    String get( @Nonnull String authorizationHeaderValue );

    /**
     * Removes the combination of authorization header and session cookie from the store.
     *
     * @param authorizationHeaderValue the authorization header value.
     * @param cookieValue              the session cookie value.
     * @return <code>true</code> if anything has been removed.
     */
    boolean remove( @Nonnull String authorizationHeaderValue, @Nonnull String cookieValue );
}
