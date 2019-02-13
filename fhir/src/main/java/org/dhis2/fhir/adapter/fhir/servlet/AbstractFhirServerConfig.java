package org.dhis2.fhir.adapter.fhir.servlet;

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

import javax.annotation.Nonnull;

/**
 * Provides the configuration to run the FHIR restful server and enables the authorization reset filter for the restful
 * server. The configuration may support to run multiple restful servers in the future (to serve multiple FHIR versions).
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
}
