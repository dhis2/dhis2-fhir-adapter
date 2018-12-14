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
import org.dhis2.fhir.adapter.auth.Authorization;
import org.dhis2.fhir.adapter.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.auth.AuthorizedRestTemplate;
import org.dhis2.fhir.adapter.jackson.SecuredPropertyFilter;
import org.dhis2.fhir.adapter.jackson.ToAnyPropertyFilter;
import org.dhis2.fhir.adapter.jackson.ToManyPropertyFilter;
import org.dhis2.fhir.adapter.jackson.ToOnePropertyFilter;
import org.dhis2.fhir.adapter.jackson.ZonedDateTimeDeserializer;
import org.dhis2.fhir.adapter.jackson.ZonedDateTimeSerializer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
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
        return builder.rootUri( getRootUri( endpointConfig, false ) )
            .setConnectTimeout( endpointConfig.getConnectTimeout() ).setReadTimeout( endpointConfig.getReadTimeout() )
            .configure( new AuthorizedRestTemplate( authorizationContext ) );
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
        return builder.rootUri( getRootUri( endpointConfig, false ) )
            .setConnectTimeout( endpointConfig.getConnectTimeout() ).setReadTimeout( endpointConfig.getReadTimeout() )
            .basicAuthorization( endpointConfig.getSystemAuthentication().getUsername(), endpointConfig.getSystemAuthentication().getPassword() ).build();
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
        return new Authorization( "Basic " + Base64.getEncoder().encodeToString(
            (endpointConfig.getSystemAuthentication().getUsername() + ":" + endpointConfig.getSystemAuthentication().getPassword())
                .getBytes( StandardCharsets.UTF_8 ) ) );
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
            .addFilter( ToManyPropertyFilter.FILTER_NAME, new ToManyPropertyFilter() )
            .addFilter( ToOnePropertyFilter.FILTER_NAME, new ToOnePropertyFilter() )
            .addFilter( ToAnyPropertyFilter.FILTER_NAME, new ToAnyPropertyFilter() )
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
}
