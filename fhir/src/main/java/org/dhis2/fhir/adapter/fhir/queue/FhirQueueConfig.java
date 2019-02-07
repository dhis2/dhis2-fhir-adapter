package org.dhis2.fhir.adapter.fhir.queue;

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

import com.google.common.collect.ImmutableMap;
import org.apache.activemq.artemis.jms.server.config.JMSQueueConfiguration;
import org.apache.activemq.artemis.jms.server.config.impl.JMSQueueConfigurationImpl;
import org.dhis2.fhir.adapter.dhis.queue.JmsJsonTypeIdMapping;
import org.dhis2.fhir.adapter.fhir.repository.FhirResource;
import org.dhis2.fhir.adapter.fhir.repository.impl.RepositoryConfig;
import org.dhis2.fhir.adapter.fhir.server.impl.FhirClientConfig;
import org.dhis2.fhir.adapter.fhir.server.impl.FhirClientRestHookRequest;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nonnull;
import javax.jms.ConnectionFactory;
import java.util.Map;

/**
 * Configuration of queues that are used by the adapter.
 *
 * @author volsch
 */
@Configuration
@Validated
public class FhirQueueConfig
{
    private final FhirClientConfig fhirClientConfig;

    private final RepositoryConfig repositoryConfig;

    public FhirQueueConfig( @Nonnull FhirClientConfig fhirClientConfig, @Nonnull RepositoryConfig repositoryConfig )
    {
        this.fhirClientConfig = fhirClientConfig;
        this.repositoryConfig = repositoryConfig;
    }

    @Bean
    @Nonnull
    protected ArtemisConfigurationCustomizer artemisConfigurationCustomizer()
    {
        return configuration -> {
            configuration.addAddressesSetting( fhirClientConfig.getRestHookRequestQueue().getQueueName(), fhirClientConfig.getRestHookRequestQueue().getEmbeddedAddressSettings() );
            configuration.addAddressesSetting( repositoryConfig.getFhirResourceQueue().getQueueName(), repositoryConfig.getFhirResourceQueue().getEmbeddedAddressSettings() );
            configuration.addAddressesSetting( repositoryConfig.getFhirResourceDlQueue().getQueueName(), repositoryConfig.getFhirResourceDlQueue().getEmbeddedAddressSettings() );
        };
    }

    @Bean
    @Nonnull
    protected JMSQueueConfiguration webHookRequestQueueConfiguration()
    {
        final JMSQueueConfiguration queueConfiguration = new JMSQueueConfigurationImpl();
        queueConfiguration.setName( fhirClientConfig.getRestHookRequestQueue().getQueueName() );
        queueConfiguration.setDurable( true );
        return queueConfiguration;
    }

    @Bean
    @Nonnull
    protected JMSQueueConfiguration fhirResourceQueueConfiguration()
    {
        final JMSQueueConfiguration queueConfiguration = new JMSQueueConfigurationImpl();
        queueConfiguration.setName( repositoryConfig.getFhirResourceQueue().getQueueName() );
        queueConfiguration.setDurable( true );
        return queueConfiguration;
    }

    @Bean
    @Nonnull
    protected JMSQueueConfiguration fhirResourceDlQueueConfiguration()
    {
        final JMSQueueConfiguration queueConfiguration = new JMSQueueConfigurationImpl();
        queueConfiguration.setName( repositoryConfig.getFhirResourceDlQueue().getQueueName() );
        queueConfiguration.setDurable( true );
        return queueConfiguration;
    }

    @Bean
    @Nonnull
    protected JmsTemplate fhirRestHookRequestQueueJmsTemplate( @Nonnull ConnectionFactory connectionFactory, @Nonnull MessageConverter jmsMessageConverter )
    {
        final JmsTemplate jmsTemplate = new JmsTemplate( connectionFactory );
        jmsTemplate.setDefaultDestinationName( fhirClientConfig.getRestHookRequestQueue().getQueueName() );
        jmsTemplate.setMessageConverter( jmsMessageConverter );
        return jmsTemplate;
    }

    @Bean
    @Nonnull
    protected JmsTemplate fhirResourceQueueJmsTemplate( @Nonnull ConnectionFactory connectionFactory, @Nonnull MessageConverter jmsMessageConverter )
    {
        final JmsTemplate jmsTemplate = new JmsTemplate( connectionFactory );
        jmsTemplate.setDefaultDestinationName( repositoryConfig.getFhirResourceQueue().getQueueName() );
        jmsTemplate.setMessageConverter( jmsMessageConverter );
        return jmsTemplate;
    }

    @Bean
    @Nonnull
    protected JmsJsonTypeIdMapping dhisJmsJsonTypeIdMapping()
    {
        return new JmsJsonTypeIdMapping()
        {
            @Nonnull
            @Override
            public Map<String, Class<?>> getTypeIdMappings()
            {
                return ImmutableMap.of(
                    "fhirRestHookRequest", FhirClientRestHookRequest.class,
                    "fhirResource", FhirResource.class );
            }
        };
    }
}
