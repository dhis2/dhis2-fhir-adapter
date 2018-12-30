package org.dhis2.fhir.adapter.auth;

/*
 * Copyright (c) 2004-2018, University of Oslo
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

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Extension of REST Template that accesses the current authorization header from the
 * {@linkplain AuthorizationContext} and uses it as authorization header for the current
 * request. This is useful when using the incoming authentication as well for requests
 * to a dependent system (which must use the same authentication).
 *
 * @author volsch
 */
public class AuthorizedRestTemplate extends RestTemplate
{
    protected static final String AUTHORIZATION_HEADER_NAME = "Authorization";

    protected static final String WWW_AUTHENTICATE_HEADER_NAME = "WWW-Authenticate";

    private final AuthorizationContext authorizationContext;

    private final List<WwwAuthenticate> wwwAuthenticates;

    /**
     * @param authorizationContext the authorization context that returns the authorization that should be used in the current scope.
     */
    public AuthorizedRestTemplate( @Nonnull AuthorizationContext authorizationContext )
    {
        this( authorizationContext, Collections.emptyList() );
    }

    /**
     * @param authorizationContext the authorization context that returns the authorization that should be used in the current scope.
     * @param wwwAuthenticates     the WWW authenticate headers that should be included in thrown {@link UnauthorizedException} in case of such a server failure.
     */
    public AuthorizedRestTemplate( @Nonnull AuthorizationContext authorizationContext, @Nonnull List<WwwAuthenticate> wwwAuthenticates )
    {
        this.authorizationContext = authorizationContext;
        this.wwwAuthenticates = wwwAuthenticates;
    }

    @Nonnull
    @Override
    protected ClientHttpRequest createRequest( @Nonnull URI url, @Nonnull HttpMethod method ) throws IOException
    {
        final ClientHttpRequest request = super.createRequest( url, method );
        final Authorization authorization = authorizationContext.getAuthorization();
        if ( authorization.getAuthorization() == null )
        {
            throw new UnauthorizedException( "Authentication has failed.",
                wwwAuthenticates.stream().map( WwwAuthenticate::toString ).collect( Collectors.toList() ) );
        }
        request.getHeaders().set( AUTHORIZATION_HEADER_NAME, authorization.getAuthorization() );
        return request;
    }

    @Override
    protected void handleResponse( @Nonnull URI url, @Nonnull HttpMethod method, @Nonnull ClientHttpResponse response ) throws IOException
    {
        final HttpStatus httpStatus = response.getStatusCode();
        if ( httpStatus == HttpStatus.UNAUTHORIZED )
        {
            final List<String> wwwAuthenticates = response.getHeaders().get( WWW_AUTHENTICATE_HEADER_NAME );
            if ( (wwwAuthenticates != null) && !wwwAuthenticates.isEmpty() )
            {
                throw new UnauthorizedException( "Authentication has failed.", wwwAuthenticates );
            }
            else
            {
                throw new UnauthorizedException( "Authentication has failed.",
                    this.wwwAuthenticates.stream().map( WwwAuthenticate::toString ).collect( Collectors.toList() ) );
            }
        }
        else if ( httpStatus == HttpStatus.FORBIDDEN )
        {
            throw new ForbiddenException( "Access to requested resource is forbidden." );
        }

        super.handleResponse( url, method, response );
    }
}
