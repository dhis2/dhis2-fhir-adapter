package org.dhis2.fhir.adapter.fhir.server;

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

import ca.uhn.fhir.rest.server.RestfulServer;
import org.dhis2.fhir.adapter.auth.AuthorizationResetFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;

/**
 * Provides the configuration to run the FHIR restful client and enables the authorization reset filter for the restful
 * client. The configuration may support to run multiple restful servers in the future (to serve multiple FHIR versions).
 *
 * @author volsch
 */
public abstract class AbstractFhirServerConfig
{
    private final String fhirVersionString;

    protected AbstractFhirServerConfig( @Nonnull String fhirVersionString )
    {
        this.fhirVersionString = fhirVersionString;
    }

    @Nonnull
    protected ServletRegistrationBean<RestfulServer> fhirServerRegistrationBean( @Nonnull RestfulServer restfulServer )
    {
        final ServletRegistrationBean<RestfulServer> registration = new ServletRegistrationBean<>( restfulServer, "/fhir/" + fhirVersionString + "/*" );
        registration.setName( "DHIS2 FHIR Adapter Server " + fhirVersionString );
        registration.setLoadOnStartup( 10 );
        return registration;
    }

    @Nonnull
    protected FilterRegistrationBean<AuthorizationResetFilter> fhirAuthorizationResetFilter(
        @Nonnull ServletRegistrationBean<RestfulServer> fhirServerRegistrationBean, @Nonnull AuthorizationResetFilter authorizationResetFilter )
    {
        final FilterRegistrationBean<AuthorizationResetFilter> registration = new FilterRegistrationBean<>( authorizationResetFilter, fhirServerRegistrationBean );
        registration.setName( "DHIS2 FHIR Adapter Authorization Reset Filter " + fhirVersionString );
        return registration;
    }

    @Nonnull
    protected FilterRegistrationBean corsFilter( @Nonnull ServletRegistrationBean<RestfulServer> fhirServerRegistrationBean )
    {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins( Collections.singletonList( "*" ) );
        config.setAllowedMethods( Arrays.asList( HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.PATCH.name(), HttpMethod.DELETE.name(), HttpMethod.HEAD.name(), HttpMethod.OPTIONS.name() ) );
        config.setAllowedHeaders( Arrays.asList( HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, HttpHeaders.CACHE_CONTROL, HttpHeaders.ACCEPT, HttpHeaders.ACCEPT_LANGUAGE, HttpHeaders.CONTENT_LANGUAGE ) );
        config.setExposedHeaders( Arrays.asList( HttpHeaders.CACHE_CONTROL, HttpHeaders.CONTENT_LANGUAGE, HttpHeaders.CONTENT_TYPE, HttpHeaders.EXPIRES,
            HttpHeaders.LAST_MODIFIED, HttpHeaders.PRAGMA, HttpHeaders.CONTENT_LENGTH, HttpHeaders.LOCATION, HttpHeaders.ETAG, HttpHeaders.WWW_AUTHENTICATE ) );
        config.setAllowCredentials( true );
        source.registerCorsConfiguration( "/**", config );

        final FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>( new CorsFilter( source ), fhirServerRegistrationBean );
        bean.setName( "DHIS2 FHIR Interfaces CORS Filter " + fhirVersionString );
        bean.setOrder( 0 );
        return bean;
    }
}
