package org.dhis2.fhir.adapter.fhir.metadata.repository.impl;

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

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

/**
 * The configurer for the the REST repository that sets up mainly CORS.
 *
 * @author volsch
 */
@Configuration
public class FhirRepositoryRestConfigurer extends RepositoryRestConfigurerAdapter
{
    @Override
    public void configureRepositoryRestConfiguration( RepositoryRestConfiguration config )
    {
        config.getCorsRegistry().addMapping( "/api/**" )
            .allowedOrigins( "*" )
            .allowedMethods( HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.PATCH.name(), HttpMethod.DELETE.name(), HttpMethod.HEAD.name(), HttpMethod.OPTIONS.name() )
            .allowedHeaders( HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE, HttpHeaders.CACHE_CONTROL, HttpHeaders.ACCEPT, HttpHeaders.ACCEPT_LANGUAGE, HttpHeaders.CONTENT_LANGUAGE, HttpHeaders.IF_MATCH, HttpHeaders.IF_MODIFIED_SINCE,
                HttpHeaders.IF_NONE_MATCH, HttpHeaders.IF_UNMODIFIED_SINCE )
            .exposedHeaders( HttpHeaders.CACHE_CONTROL, HttpHeaders.CONTENT_LANGUAGE, HttpHeaders.CONTENT_TYPE, HttpHeaders.EXPIRES, HttpHeaders.LAST_MODIFIED, HttpHeaders.PRAGMA, HttpHeaders.CONTENT_LENGTH,
                HttpHeaders.LOCATION, HttpHeaders.ETAG, HttpHeaders.WWW_AUTHENTICATE )
            .allowCredentials( true );
    }
}
