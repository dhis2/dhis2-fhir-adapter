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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Session cookie request template that stores for a specific authorization header value
 * the corresponding session cookie in a cookie store.
 *
 * @author volsch
 */
public abstract class AbstractSessionCookieRestTemplate extends RestTemplate
{
    protected static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    protected static final String LOGIN_PAGE_HEADER_NAME = "Login-Page";

    private static final String[] SESSION_COOKIE_NAMES = new String[]{
        "JSESSIONID",
        "SESSION"
    };

    private final RestTemplateCookieStore cookieStore;

    private volatile String sessionCookieName;

    protected AbstractSessionCookieRestTemplate( @Nonnull RestTemplateCookieStore cookieStore )
    {
        this.cookieStore = cookieStore;
    }

    @Nonnull
    protected abstract String getAuthorizationHeaderValue();

    @Override
    protected <T> T doExecute( URI url, HttpMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor ) throws RestClientException
    {
        SessionCookieRequestCallback sessionCookieRequestCallback = null;
        if ( requestCallback != null )
        {
            sessionCookieRequestCallback = new SessionCookieRequestCallback( requestCallback );
        }
        return super.doExecute( url, method, sessionCookieRequestCallback, responseExtractor );
    }

    @Nonnull
    @Override
    protected ClientHttpRequest createRequest( @Nonnull URI url, @Nonnull HttpMethod method ) throws IOException
    {
        return new SessionCookieClientHttpRequest( super.createRequest( url, method ) );
    }

    protected boolean isUnauthorizedResponse( @Nonnull ClientHttpResponse response ) throws IOException
    {
        final HttpStatus httpStatus = response.getStatusCode();
        if ( httpStatus == HttpStatus.UNAUTHORIZED )
        {
            return true;
        }
        if ( httpStatus == HttpStatus.OK )
        {
            if ( Boolean.parseBoolean( response.getHeaders().getFirst( LOGIN_PAGE_HEADER_NAME ) ) )
            {
                return true;
            }
            return MediaType.TEXT_HTML.isCompatibleWith( response.getHeaders().getContentType() );
        }
        return false;
    }

    protected static class SessionCookieRequestCallback implements RequestCallback
    {
        private final RequestCallback requestCallback;

        public SessionCookieRequestCallback( @Nonnull RequestCallback requestCallback )
        {
            this.requestCallback = requestCallback;
        }

        @Override
        public void doWithRequest( @Nonnull ClientHttpRequest request ) throws IOException
        {
            ((SessionCookieClientHttpRequest) request).setRequestCallback( requestCallback );
            requestCallback.doWithRequest( request );
        }
    }

    protected class SessionCookieClientHttpRequest implements ClientHttpRequest
    {
        private final ClientHttpRequest clientHttpRequest;

        private RequestCallback requestCallback;

        public SessionCookieClientHttpRequest( @Nonnull ClientHttpRequest clientHttpRequest )
        {
            this.clientHttpRequest = clientHttpRequest;
        }

        public void setRequestCallback( RequestCallback requestCallback )
        {
            this.requestCallback = requestCallback;
        }

        @Override
        @Nonnull
        public ClientHttpResponse execute() throws IOException
        {
            String sessionCookieName = AbstractSessionCookieRestTemplate.this.sessionCookieName;

            final String authorizationHeaderValue = getAuthorizationHeaderValue();
            final String sessionCookieValue;
            if ( sessionCookieName == null )
            {
                sessionCookieValue = null;
            }
            else
            {
                sessionCookieValue = cookieStore.get( authorizationHeaderValue );
            }

            if ( sessionCookieValue == null )
            {
                clientHttpRequest.getHeaders().set( AUTHORIZATION_HEADER_NAME, authorizationHeaderValue );
            }
            else
            {
                clientHttpRequest.getHeaders().set( HttpHeaders.COOKIE,
                    new HttpCookie( sessionCookieName, sessionCookieValue ).toString() );
            }

            boolean ok = false;
            ClientHttpResponse response = clientHttpRequest.execute();
            try
            {
                if ( !isUnauthorizedResponse( response ) || (sessionCookieValue == null) )
                {
                    if ( sessionCookieValue == null )
                    {
                        rememberSessionCookie( authorizationHeaderValue, response );
                    }
                    ok = true;
                    return response;
                }
            }
            finally
            {
                if ( !ok )
                {
                    response.close();
                }
            }
            cookieStore.remove( authorizationHeaderValue, sessionCookieValue );

            final ClientHttpRequest request = getRequestFactory().createRequest(
                clientHttpRequest.getURI(), Objects.requireNonNull( clientHttpRequest.getMethod() ) );
            if ( requestCallback != null )
            {
                requestCallback.doWithRequest( request );
            }
            request.getHeaders().set( AUTHORIZATION_HEADER_NAME, authorizationHeaderValue );

            ok = false;
            response = request.execute();
            try
            {
                if ( !isUnauthorizedResponse( response ) )
                {
                    rememberSessionCookie( authorizationHeaderValue, response );
                }
                ok = true;
            }
            finally
            {
                if ( !ok )
                {
                    response.close();
                }
            }
            return response;
        }

        private void rememberSessionCookie( @Nonnull String authorizationHeaderValue, @Nonnull ClientHttpResponse response ) throws IOException
        {
            final HttpStatus httpStatus = response.getStatusCode();
            if ( httpStatus.is2xxSuccessful() || httpStatus.is3xxRedirection() )
            {
                if ( AbstractSessionCookieRestTemplate.this.sessionCookieName == null )
                {
                    final String cookieHeader = response.getHeaders().getFirst( HttpHeaders.SET_COOKIE );
                    if ( cookieHeader != null )
                    {
                        final Map<String, HttpCookie> cookiesByName = HttpCookie.parse( cookieHeader )
                            .stream().collect( Collectors.toMap( HttpCookie::getName, c -> c ) );
                        Arrays.stream( SESSION_COOKIE_NAMES ).filter( cookiesByName::containsKey ).findFirst()
                            .ifPresent( sessionCookieName -> AbstractSessionCookieRestTemplate.this.sessionCookieName = sessionCookieName );
                    }
                }

                if ( AbstractSessionCookieRestTemplate.this.sessionCookieName != null )
                {
                    final String cookieHeader = response.getHeaders().getFirst( HttpHeaders.SET_COOKIE );
                    if ( cookieHeader != null )
                    {
                        final String sessionCookieName = AbstractSessionCookieRestTemplate.this.sessionCookieName;
                        HttpCookie.parse( cookieHeader )
                            .stream().filter( c -> sessionCookieName.equals( c.getName() ) ).findFirst()
                            .ifPresent( cookie -> cookieStore.add( authorizationHeaderValue, cookie.getValue() ) );
                    }
                }
            }
        }

        @Override
        @Nonnull
        public OutputStream getBody() throws IOException
        {
            return clientHttpRequest.getBody();
        }

        @Override
        @Nonnull
        public String getMethodValue()
        {
            return clientHttpRequest.getMethodValue();
        }

        @Override
        @Nonnull
        public URI getURI()
        {
            return clientHttpRequest.getURI();
        }

        @Override
        @Nonnull
        public HttpHeaders getHeaders()
        {
            return clientHttpRequest.getHeaders();
        }
    }
}
