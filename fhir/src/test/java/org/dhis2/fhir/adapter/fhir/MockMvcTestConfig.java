package org.dhis2.fhir.adapter.fhir;

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
import org.dhis2.fhir.adapter.cache.CacheBasePackage;
import org.dhis2.fhir.adapter.converter.ZonedDateTimeToDateConverter;
import org.dhis2.fhir.adapter.fhir.data.DataBasePackage;
import org.dhis2.fhir.adapter.fhir.metadata.MetadataBasePackage;
import org.dhis2.fhir.adapter.rest.RestBasePackage;
import org.dhis2.fhir.adapter.script.ScriptEvaluator;
import org.dhis2.fhir.adapter.script.impl.ScriptEvaluatorImpl;
import org.dhis2.fhir.adapter.validator.EnumValue;
import org.dhis2.fhir.adapter.validator.EnumValueValidator;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.netflix.hystrix.HystrixAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.format.FormatterRegistry;
import org.springframework.restdocs.constraints.Constraint;
import org.springframework.restdocs.constraints.ConstraintDescriptionResolver;
import org.springframework.restdocs.constraints.ResourceBundleConstraintDescriptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

/**
 * Test configuration.
 *
 * @author volsch
 */
@Configuration
@EnableAutoConfiguration( exclude = { RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class, ArtemisAutoConfiguration.class, HystrixAutoConfiguration.class } )
@ComponentScan( basePackageClasses = { CacheBasePackage.class, RestBasePackage.class, DataBasePackage.class, MetadataBasePackage.class } )
public class MockMvcTestConfig
{
    @Nonnull
    @Bean
    protected ScriptEvaluator scriptEvaluator()
    {
        return new ScriptEvaluatorImpl( "nashorn", Arrays.asList( "-doe", "--no-java", "--no-syntax-extensions" ), 3600, 1000 );
    }

    @Bean
    @Nonnull
    public WebMvcConfigurer mvcConfigurer()
    {
        return new WebMvcConfigurer()
        {
            @Override
            public void addFormatters( @Nonnull FormatterRegistry registry )
            {
                registry.addConverter( ZonedDateTime.class, Date.class, new ZonedDateTimeToDateConverter() );
            }
        };
    }

    @Nonnull
    @Bean
    protected GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer()
    {
        return new GenericJackson2JsonRedisSerializer();
    }

    @Nonnull
    @Bean
    protected RepositoryRestConfigurer repositoryRestConfigurer()
    {
        return new RepositoryRestConfigurerAdapter()
        {
            @Override
            public void configureRepositoryRestConfiguration( RepositoryRestConfiguration config )
            {
                config.setRepositoryDetectionStrategy( RepositoryDetectionStrategy.RepositoryDetectionStrategies.ANNOTATED );
                config.setBasePath( "/api" );
            }
        };
    }

    @Nonnull
    @Primary
    @Bean
    protected ConstraintDescriptionResolver constraintDescriptionResolver()
    {
        return new ResourceBundleConstraintDescriptionResolver()
        {
            @Override
            public String resolveDescription( Constraint constraint )
            {
                if ( EnumValue.class.getName().equals( constraint.getName() ) )
                {
                    @SuppressWarnings( "unchecked" ) final Class<Enum<?>> value = (Class<Enum<?>>) constraint.getConfiguration().get( "value" );
                    final String[] unsupported = (String[]) constraint.getConfiguration().get( "unsupported" );
                    final String[] supported = (String[]) constraint.getConfiguration().get( "supported" );
                    if ( value != null )
                    {
                        return "Supported values are " + StringUtils.join( EnumValueValidator.getSupported( value, unsupported, supported ), ", " );
                    }
                }
                return super.resolveDescription( constraint );
            }
        };
    }

    @Nonnull
    @Bean
    protected BuildProperties buildProperties()
    {
        return new BuildProperties( new Properties() );
    }
}
