package org.dhis2.fhir.adapter.dhis.config;

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

import org.dhis2.fhir.adapter.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.auth.AuthorizedRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;

/**
 * Configuration for access of DHIS2 endpoints.
 *
 * @author volsch
 */
@Configuration
public class DhisConfig
{
    /**
     * Creates a REST template that connects to DHIS2 with the authentication that is provided by {@link AuthorizationContext}
     * in the current execution scope of the request.
     *
     * @param builder              the rest template builder to be used.
     * @param endpointConfig       the endpoint configuration of the DHIS2 endpoint.
     * @param authorizationContext the authorization context from which the REST template gets its authorization information dynamically.
     * @return the generated user rest template that uses the specified authorization context for authorization.
     */
    @Bean
    @Nonnull
    public RestTemplate userDhis2RestTemplate( @Nonnull RestTemplateBuilder builder, @Nonnull DhisEndpointConfig endpointConfig, @Nonnull AuthorizationContext authorizationContext )
    {
        return builder.rootUri( getRootUri( endpointConfig ) ).configure( new AuthorizedRestTemplate( authorizationContext ) );
    }

    /**
     * Creates a REST template that connects to DHIS2 with the authentication that is included in the specified endpoint configuration.
     *
     * @param builder        the rest template builder to be used.
     * @param endpointConfig the endpoint configuration of the DHIS2 endpoint.
     * @return the generated system rest template that uses the authorization that is included in the specified endpoint configuration
     */
    @Bean
    @Nonnull
    public RestTemplate systemDhis2RestTemplate( @Nonnull RestTemplateBuilder builder, @Nonnull DhisEndpointConfig endpointConfig )
    {
        return builder.rootUri( getRootUri( endpointConfig ) ).basicAuthorization(
            endpointConfig.getSystemAuthentication().getUsername(), endpointConfig.getSystemAuthentication().getPassword() ).build();
    }

    @Nonnull
    private String getRootUri( @Nonnull DhisEndpointConfig endpointConfig )
    {
        return endpointConfig.getUrl() + "/api/" + endpointConfig.getApiVersion();
    }
}
