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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.cache.AbstractSimpleCacheConfig;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.AbstractCacheResolver;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

/**
 * Cache configuration for FHIR Resources.
 *
 * @author volsch
 */
@Configuration
@Component
@ConfigurationProperties( "dhis2.fhir-adapter.cache.dhis" )
@Validated
public class DhisMetadataCacheConfig extends AbstractSimpleCacheConfig
{
    private static final long serialVersionUID = 3060542002074294407L;

    @Nonnull
    @Override
    protected String getCacheManagerName()
    {
        return "dhisCacheManager";
    }

    @Primary
    @Bean
    @Nonnull
    protected CacheManager dhisCacheManager( @Nonnull RequestCacheService requestCacheService, @Nonnull ObjectProvider<RedisConnectionFactory> redisConnectionFactoryProvider, @Nonnull GenericJackson2JsonRedisSerializer redisSerializer )
    {
        return createCacheManager( requestCacheService, redisConnectionFactoryProvider, redisSerializer );
    }

    @Nonnull
    @Bean
    protected CacheResolver dhisMetadataCacheResolver( @Nonnull @Qualifier( "dhisCacheManager" ) CacheManager cacheManager )
    {
        return new AbstractCacheResolver( cacheManager )
        {
            @Override
            protected Collection<String> getCacheNames( @Nonnull CacheOperationInvocationContext<?> context )
            {
                String name = context.getTarget().getClass().getSimpleName();

                final int index = name.indexOf( "Service" );

                if ( index > 0 )
                {
                    name = name.substring( 0, index );
                }

                return Collections.singletonList( StringUtils.uncapitalize( name ) );
            }
        };
    }
}
