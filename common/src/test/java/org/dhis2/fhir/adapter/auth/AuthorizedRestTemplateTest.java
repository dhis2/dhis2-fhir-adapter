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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Unit tests of {@link AuthorizedRestTemplate}.
 *
 * @author volsch
 */
public class AuthorizedRestTemplateTest
{
    @Mock
    private Authorization authorization;

    @Mock
    private AuthorizationContext authorizationContext;

    @Mock
    private ClientHttpRequest request;

    @Mock
    private ClientHttpResponse response;

    @Mock
    private HttpHeaders requestHeaders;

    @Mock
    private HttpHeaders responseHeaders;

    @Mock
    private ClientHttpRequestFactory requestFactory;

    private List<WwwAuthenticate> authenticates = Arrays.asList(
        new WwwAuthenticate( "Basic", "test1" ), new WwwAuthenticate( "Bearer", "test2" ) );

    private AuthorizedRestTemplate template;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void before() throws IOException
    {
        Mockito.when( authorizationContext.getAuthorization() ).thenReturn( authorization );
        template = new AuthorizedRestTemplate( authorizationContext, authenticates );
        Mockito.when( requestFactory.createRequest( Mockito.any(), Mockito.any() ) ).thenReturn( request );
        template.setRequestFactory( requestFactory );
        Mockito.when( request.getHeaders() ).thenReturn( requestHeaders );
        Mockito.when( request.execute() ).thenReturn( response );
        Mockito.when( response.getHeaders() ).thenReturn( responseHeaders );
    }

    @Test( expected = UnauthorizedException.class )
    public void createRequestWithoutAuthorization()
    {
        try
        {
            template.postForObject( "http://127.0.0.1/test", null, String.class );
        }
        catch ( UnauthorizedException e )
        {
            Assert.assertEquals( authenticates.stream().map( WwwAuthenticate::toString ).collect( Collectors.toList() ),
                e.getWwwAuthenticates() );
            throw e;
        }
    }

    @Test
    public void createRequest() throws IOException
    {
        Mockito.when( authorization.getAuthorization() ).thenReturn( "Bearer 123" );
        Mockito.when( response.getStatusCode() ).thenReturn( HttpStatus.OK );
        Mockito.when( response.getRawStatusCode() ).thenReturn( HttpStatus.OK.value() );
        Mockito.when( responseHeaders.get( Mockito.eq( "Content-Length" ) ) ).thenReturn( Collections.singletonList( "0" ) );

        template.postForObject( "http://127.0.0.1/test", null, String.class );

        Mockito.verify( requestHeaders ).set( Mockito.eq( "Authorization" ), Mockito.eq( "Bearer 123" ) );
    }

    @Test( expected = ForbiddenException.class )
    public void handleResponseForbidden() throws IOException
    {
        Mockito.when( authorization.getAuthorization() ).thenReturn( "Bearer 123" );
        Mockito.when( response.getStatusCode() ).thenReturn( HttpStatus.FORBIDDEN );
        Mockito.when( response.getRawStatusCode() ).thenReturn( HttpStatus.FORBIDDEN.value() );

        template.postForObject( "http://127.0.0.1/test", null, String.class );
    }

    @Test( expected = UnauthorizedException.class )
    public void handleResponseUnauthorizedWithoutExternalHeaders() throws IOException
    {
        Mockito.when( authorization.getAuthorization() ).thenReturn( "Bearer 123" );
        Mockito.when( response.getStatusCode() ).thenReturn( HttpStatus.UNAUTHORIZED );
        Mockito.when( response.getRawStatusCode() ).thenReturn( HttpStatus.UNAUTHORIZED.value() );

        try
        {
            template.postForObject( "http://127.0.0.1/test", null, String.class );
        }
        catch ( UnauthorizedException e )
        {
            Assert.assertEquals( authenticates.stream().map( WwwAuthenticate::toString ).collect( Collectors.toList() ),
                e.getWwwAuthenticates() );
            throw e;
        }
    }

    @Test( expected = UnauthorizedException.class )
    public void handleResponseUnauthorizedWithExternalHeaders() throws IOException
    {
        Mockito.when( authorization.getAuthorization() ).thenReturn( "Bearer 123" );
        Mockito.when( response.getStatusCode() ).thenReturn( HttpStatus.UNAUTHORIZED );
        Mockito.when( response.getRawStatusCode() ).thenReturn( HttpStatus.UNAUTHORIZED.value() );
        Mockito.when( responseHeaders.get( Mockito.eq( "WWW-Authenticate" ) ) ).thenReturn( Collections.singletonList( "Basic other" ) );

        try
        {
            template.postForObject( "http://127.0.0.1/test", null, String.class );
        }
        catch ( UnauthorizedException e )
        {
            Assert.assertEquals( Collections.singletonList( "Basic other" ), e.getWwwAuthenticates() );
            throw e;
        }
    }

    @Test( expected = HttpServerErrorException.class )
    public void handleOtherError() throws IOException
    {
        Mockito.when( authorization.getAuthorization() ).thenReturn( "Bearer 123" );
        Mockito.when( response.getStatusCode() ).thenReturn( HttpStatus.INTERNAL_SERVER_ERROR );
        Mockito.when( response.getRawStatusCode() ).thenReturn( HttpStatus.INTERNAL_SERVER_ERROR.value() );

        template.postForObject( "http://127.0.0.1/test", null, String.class );
    }
}