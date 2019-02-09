package org.dhis2.fhir.adapter.dhis.security;

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

import org.dhis2.fhir.adapter.dhis.config.DhisConfig;
import org.dhis2.fhir.adapter.dhis.config.DhisEndpointConfig;
import org.dhis2.fhir.adapter.model.Id;
import org.dhis2.fhir.adapter.rest.AbstractSessionCookieRestTemplate;
import org.dhis2.fhir.adapter.rest.RestTemplateCookieStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Authentication provider that authenticates the user by establishing a connection
 * to DHIS2.
 *
 * @author volsch
 */
public class DhisWebApiAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider
{
    protected static final String USER_ACCOUNT_URL = "/me/user-account.json";

    protected static final String AUTHORIZATION_URL = "/me/authorization.json";

    private final RestTemplateBuilder restTemplateBuilder;

    private final SecurityConfig securityConfig;

    private final RestTemplateCookieStore cookieStore;

    public DhisWebApiAuthenticationProvider( @Nonnull RestTemplateBuilder restTemplateBuilder, @Nonnull DhisEndpointConfig endpointConfig, @Nonnull SecurityConfig securityConfig,
        @Nonnull @Qualifier( "dhisClientHttpRequestFactory" ) ClientHttpRequestFactory clientHttpRequestFactory, @Nonnull @Qualifier( "dhisCookieStore" ) RestTemplateCookieStore cookieStore )
    {
        this.restTemplateBuilder = restTemplateBuilder.requestFactory( () -> clientHttpRequestFactory )
            .rootUri( DhisConfig.getRootUri( endpointConfig, true ) )
            .setConnectTimeout( endpointConfig.getConnectTimeout() ).setReadTimeout( endpointConfig.getReadTimeout() );
        this.securityConfig = securityConfig;
        this.cookieStore = cookieStore;
    }

    @Override
    protected void additionalAuthenticationChecks( UserDetails userDetails, UsernamePasswordAuthenticationToken authentication ) throws AuthenticationException
    {
        // all authentication checks have been performed by DHIS2
    }

    @Override
    protected UserDetails retrieveUser( String username, UsernamePasswordAuthenticationToken authentication ) throws AuthenticationException
    {
        final RestTemplate restTemplate = restTemplateBuilder.configure( new AbstractSessionCookieRestTemplate( cookieStore )
        {
            @Nonnull
            @Override
            protected String getAuthorizationHeaderValue()
            {
                return DhisConfig.createBasicAuthHeaderValue( username, String.valueOf( authentication.getCredentials() ) );
            }
        } );
        final ResponseEntity<Id> idResponse;
        final ResponseEntity<List<String>> authorizationResponse;
        try
        {
            idResponse = restTemplate.getForEntity( USER_ACCOUNT_URL, Id.class );
            authorizationResponse = restTemplate.exchange( AUTHORIZATION_URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>()
            {
            } );
        }
        catch ( HttpClientErrorException e )
        {
            if ( HttpStatus.UNAUTHORIZED.equals( e.getStatusCode() ) )
            {
                throw new BadCredentialsException( "Invalid username or password." );
            }
            throw new AuthenticationServiceException( "An error has occurred when authenticating the user.", e );
        }
        catch ( HttpServerErrorException e )
        {
            throw new AuthenticationServiceException( "An error has occurred when authenticating the user.", e );
        }

        final Set<String> dhisAuthorities = new HashSet<>( Objects.requireNonNull( authorizationResponse.getBody() ) );
        final Set<GrantedAuthority> grantedAuthorities = securityConfig.createGrantedAuthorities( dhisAuthorities );
        return new AdapterUser( Objects.requireNonNull( idResponse.getBody() ).getId(), username, grantedAuthorities );
    }
}
