package org.dhis2.fhir.adapter.fhir.server.r4;

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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.IServerInterceptor;
import org.dhis2.fhir.adapter.auth.AuthorizationResetFilter;
import org.dhis2.fhir.adapter.fhir.server.AbstractFhirServerConfig;
import org.dhis2.fhir.adapter.fhir.server.FhirRestfulServer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * R4 specific implementation of {@link AbstractFhirServerConfig}.
 *
 * @author volsch
 */
@Configuration
@ConditionalOnProperty( "dhis2.fhir-adapter.fhir-rest-interfaces.r4-enabled" )
public class R4FhirServerConfig extends AbstractFhirServerConfig
{
    public R4FhirServerConfig()
    {
        super( "r4" );
    }

    @Bean
    @Nonnull
    protected FhirRestfulServer fhirRestfulServerR4( @Nonnull @Qualifier( "fhirContextR4" ) FhirContext fhirContext,
        @Nonnull ObjectProvider<List<IResourceProvider>> resourceProviders,
        @Nonnull ObjectProvider<List<IServerInterceptor>> interceptors )
    {
        return new FhirRestfulServer( fhirContext, resourceProviders, interceptors );
    }

    @Bean
    @Nonnull
    protected ServletRegistrationBean<RestfulServer> fhirServerRegistrationBeanR4( @Nonnull @Qualifier( "fhirRestfulServerR4" ) RestfulServer restfulServer )
    {
        return fhirServerRegistrationBean( restfulServer );
    }

    @Bean
    @Nonnull
    protected FilterRegistrationBean<AuthorizationResetFilter> fhirAuthorizationResetFilterR4(
        @Nonnull @Qualifier( "fhirServerRegistrationBeanR4" ) ServletRegistrationBean<RestfulServer> fhirServerRegistrationBean,
        @Nonnull AuthorizationResetFilter authorizationResetFilter )
    {
        return fhirAuthorizationResetFilter( fhirServerRegistrationBean, authorizationResetFilter );
    }

    @Bean
    @Nonnull
    protected FilterRegistrationBean corsFilterR4( @Nonnull @Qualifier( "fhirServerRegistrationBeanR4" ) ServletRegistrationBean<RestfulServer> fhirServerRegistrationBean )
    {
        return super.corsFilter( fhirServerRegistrationBean );
    }
}
