package org.dhis2.fhir.adapter.cache;

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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisElementReader;
import org.springframework.data.redis.serializer.RedisElementWriter;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nonnull;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Abstract simple cache configuration that can be extended multiple times for specific use cases.
 *
 * @author volsch
 */
@Validated
public abstract class AbstractSimpleCacheConfig implements Serializable
{
    private static final long serialVersionUID = 3060542002074294407L;

    @NotNull
    private SimpleCacheType type = SimpleCacheType.NONE;

    @Valid
    @NestedConfigurationProperty
    @NotNull
    private final SimpleCaffeineCacheConfig caffeine = new SimpleCaffeineCacheConfig();

    @Valid
    @NestedConfigurationProperty
    @NotNull
    private final SimpleRedisCacheConfig redis = new SimpleRedisCacheConfig();

    @Nonnull
    public SimpleCacheType getType()
    {
        return type;
    }

    public void setType( @Nonnull SimpleCacheType type )
    {
        this.type = type;
    }

    @Nonnull
    public SimpleCaffeineCacheConfig getCaffeine()
    {
        return caffeine;
    }

    @Nonnull
    public SimpleRedisCacheConfig getRedis()
    {
        return redis;
    }

    @Nonnull
    protected abstract String getCacheManagerName();

    @Nonnull
    protected <R> CacheManager createCacheManager( @Nonnull RequestCacheService requestCacheService,
        @Nonnull ObjectProvider<RedisConnectionFactory> redisConnectionFactoryProvider, @Nonnull RedisSerializer<R> redisSerializer )
    {
        final CacheManager defaultCacheManager;
        switch ( getType() )
        {
            case NONE:
                defaultCacheManager = new NoOpCacheManager();
                break;
            case CAFFEINE:
                final CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
                caffeineCacheManager.setCacheSpecification( caffeine.getSpec() );
                defaultCacheManager = caffeineCacheManager;
                break;
            case REDIS:
                defaultCacheManager = RedisCacheManager.builder( redisConnectionFactoryProvider.getObject() ).cacheDefaults( createRedisCacheConfiguration( redisSerializer ) ).build();
                break;
            default:
                throw new AssertionError( "Unhandled cache type: " + getType() );
        }
        return new RequestCacheManager( getCacheManagerName(), requestCacheService, defaultCacheManager );
    }

    @Nonnull
    protected <R> RedisCacheConfiguration createRedisCacheConfiguration( @Nonnull RedisSerializer<R> redisSerializer )
    {
        return RedisCacheConfiguration.defaultCacheConfig().computePrefixWith( getRedis().getCacheKeyPrefix() )
            .entryTtl( getRedis().getTimeToLive() ).serializeValuesWith( new RedisSerializationContext.SerializationPair<R>()
            {
                @Override
                @Nonnull
                public RedisElementReader<R> getReader()
                {
                    return RedisElementReader.from( redisSerializer );
                }

                @Override
                @Nonnull
                public RedisElementWriter<R> getWriter()
                {
                    return RedisElementWriter.from( redisSerializer );
                }
            } );
    }
}
