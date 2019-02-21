package org.dhis2.fhir.adapter.dhis.config;

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

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.dhis2.fhir.adapter.auth.Authorization;
import org.dhis2.fhir.adapter.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.jackson.JsonCachePropertyFilter;
import org.dhis2.fhir.adapter.jackson.SecuredPropertyFilter;
import org.dhis2.fhir.adapter.jackson.ZonedDateTimeDeserializer;
import org.dhis2.fhir.adapter.jackson.ZonedDateTimeSerializer;
import org.dhis2.fhir.adapter.rest.AbstractSessionCookieRestTemplate;
import org.dhis2.fhir.adapter.rest.AuthorizedRestTemplate;
import org.dhis2.fhir.adapter.rest.CaffeineRestTemplateCookieStore;
import org.dhis2.fhir.adapter.rest.RestTemplateCookieStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import static com.fasterxml.jackson.databind.SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

/**
 * Configuration for access of DHIS2 endpoints.
 *
 * @author volsch
 */
@Configuration
public class DhisConfig
{
    @Bean
    @Nonnull
    public ClientHttpRequestFactory dhisClientHttpRequestFactory()
    {
        final HttpClient httpClient = HttpClientBuilder.create()
            .useSystemProperties()
            .disableCookieManagement()
            .disableAuthCaching()
            .build();
        return new HttpComponentsClientHttpRequestFactory( httpClient );
    }

    @Bean
    @Nonnull
    public RestTemplateCookieStore dhisCookieStore()
    {
        return new CaffeineRestTemplateCookieStore();
    }

    /**
     * Creates a REST template that connects to DHIS2 with the authentication that is provided by {@link AuthorizationContext}
     * in the current execution scope of the request.
     *
     * @param builder              the rest template builder to be used.
     * @param endpointConfig       the endpoint configuration of the DHIS2 endpoint.
     * @param authorizationContext the authorization context from which the REST template gets its authorization information dynamically.
     * @param cookieStore          the cookie store in which session cookies of DHIS2 are stored.
     * @return the generated user rest template that uses the specified authorization context for authorization.
     */
    @Bean
    @Nonnull
    public RestTemplate userDhis2RestTemplate( @Nonnull RestTemplateBuilder builder, @Nonnull DhisEndpointConfig endpointConfig, @Nonnull AuthorizationContext authorizationContext,
        @Nonnull @Qualifier( "dhisClientHttpRequestFactory" ) ClientHttpRequestFactory clientHttpRequestFactory, @Nonnull @Qualifier( "dhisCookieStore" ) RestTemplateCookieStore cookieStore )
    {
        return builder.requestFactory( () -> clientHttpRequestFactory )
            .rootUri( getRootUri( endpointConfig, false ) )
            .setConnectTimeout( endpointConfig.getConnectTimeout() ).setReadTimeout( endpointConfig.getReadTimeout() )
            .configure( new AuthorizedRestTemplate( authorizationContext, endpointConfig.getWwwAuthenticates(), cookieStore ) );
    }

    /**
     * Creates a REST template that connects to DHIS2 with the authentication that is included in the specified endpoint configuration.
     *
     * @param builder        the rest template builder to be used.
     * @param endpointConfig the endpoint configuration of the DHIS2 endpoint.
     * @param cookieStore          the cookie store in which session cookies of DHIS2 are stored.
     * @return the generated system rest template that uses the authorization that is included in the specified endpoint configuration
     */
    @Bean
    @Nonnull
    public RestTemplate systemDhis2RestTemplate( @Nonnull RestTemplateBuilder builder, @Nonnull DhisEndpointConfig endpointConfig,
        @Nonnull @Qualifier( "dhisClientHttpRequestFactory" ) ClientHttpRequestFactory clientHttpRequestFactory, @Nonnull @Qualifier( "dhisCookieStore" ) RestTemplateCookieStore cookieStore )
    {
        final String basicAuthHeaderValue = createBasicAuthHeaderValue( endpointConfig.getSystemAuthentication().getUsername(), endpointConfig.getSystemAuthentication().getPassword() );
        return builder.requestFactory( () -> clientHttpRequestFactory )
            .rootUri( getRootUri( endpointConfig, false ) )
            .setConnectTimeout( endpointConfig.getConnectTimeout() ).setReadTimeout( endpointConfig.getReadTimeout() )
            .configure( new AbstractSessionCookieRestTemplate( cookieStore )
            {
                @Nonnull
                @Override
                protected String getAuthorizationHeaderValue()
                {
                    return basicAuthHeaderValue;
                }
            } );
    }

    /**
     * Returns the system authorization.
     *
     * @param endpointConfig the endpoint configuration of the DHIS2 endpoint.
     * @return the system authorization.
     */
    @Bean
    @Nonnull
    public Authorization systemDhis2Authorization( @Nonnull DhisEndpointConfig endpointConfig )
    {
        return new Authorization( createBasicAuthHeaderValue(
            endpointConfig.getSystemAuthentication().getUsername(), endpointConfig.getSystemAuthentication().getPassword() ) );
    }

    /**
     * Creates a Redis serializer that ignores one to many relationships.
     *
     * @return the Redis serializer.
     */
    @Bean
    public GenericJackson2JsonRedisSerializer cacheRedisSerializer()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping( ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY );
        mapper.setFilterProvider( new SimpleFilterProvider()
            .addFilter( JsonCachePropertyFilter.FILTER_NAME, new JsonCachePropertyFilter() )
            .addFilter( SecuredPropertyFilter.FILTER_NAME, new SimpleBeanPropertyFilter()
            {
            } ) );
        mapper.disable( FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS );
        mapper.disable( WRITE_DATES_AS_TIMESTAMPS );

        final SimpleModule module = new SimpleModule();
        module.addSerializer( InstantSerializer.INSTANCE );
        module.addSerializer( LocalDateTimeSerializer.INSTANCE );
        module.addSerializer( new ZonedDateTimeSerializer() );
        module.addSerializer( new LocalDateSerializer( DateTimeFormatter.ISO_LOCAL_DATE ) );
        module.addDeserializer( Instant.class, InstantDeserializer.INSTANT );
        module.addDeserializer( LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE );
        module.addDeserializer( ZonedDateTime.class, new ZonedDateTimeDeserializer() );
        module.addDeserializer( LocalDate.class, new LocalDateDeserializer( DateTimeFormatter.ISO_LOCAL_DATE ) );
        mapper.registerModule( module );

        return new GenericJackson2JsonRedisSerializer( mapper );
    }

    @Nonnull
    public static String getRootUri( @Nonnull DhisEndpointConfig endpointConfig, boolean withoutVersion )
    {
        if ( withoutVersion )
        {
            return endpointConfig.getUrl() + "/api";
        }
        return endpointConfig.getUrl() + "/api/" + endpointConfig.getApiVersion();
    }

    @Nonnull
    public static String createBasicAuthHeaderValue( @Nonnull String username, @Nonnull String password )
    {
        return "Basic " + Base64.getEncoder().encodeToString( (username + ":" + password).getBytes( StandardCharsets.UTF_8 ) );
    }
}
