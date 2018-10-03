package org.dhis2.fhir.adapter.prototype.fhir.config;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import ca.uhn.fhir.rest.server.RestfulServer;
import org.dhis2.fhir.adapter.prototype.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.prototype.auth.AuthorizationResetFilter;
import org.dhis2.fhir.adapter.prototype.auth.ThreadLocalAuthorizationContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FhirConfig
{
    public static final String FHIR_URL_MAPPING = "/fhir/*";

    public static final String FHIR_SERVLET_NAME = "DHIS2 FHIR Adapter Server";

    public static final String FHIR_AUTHORIZATION_RESET_FILTER_NAME = "DHIS2 FHIR Adapter Authorization Reset Filter";

    @Bean
    public ThreadLocalAuthorizationContext requestedAuthenticationContext()
    {
        return new ThreadLocalAuthorizationContext();
    }

    public ServletRegistrationBean<RestfulServer> fhirServerRegistrationBean( RestfulServer restfulServer )
    {
        final ServletRegistrationBean<RestfulServer> registration = new ServletRegistrationBean<>( restfulServer, FHIR_URL_MAPPING );
        registration.setName( FHIR_SERVLET_NAME );
        registration.setLoadOnStartup( 10 );
        return registration;
    }

    public AuthorizationResetFilter fhirAuthorizationResetFilter( AuthorizationContext authorizationContext )
    {
        return new AuthorizationResetFilter( authorizationContext );
    }

    public FilterRegistrationBean<AuthorizationResetFilter> fhirAuthorizationResetFilter( @Qualifier( "fhirServerRegistrationBean" ) ServletRegistrationBean<RestfulServer> fhirServerRegistrationBean, AuthorizationResetFilter authorizationResetFilter )
    {
        final FilterRegistrationBean<AuthorizationResetFilter> registration = new FilterRegistrationBean<>( authorizationResetFilter, fhirServerRegistrationBean );
        registration.setName( FHIR_AUTHORIZATION_RESET_FILTER_NAME );
        return registration;
    }
}
