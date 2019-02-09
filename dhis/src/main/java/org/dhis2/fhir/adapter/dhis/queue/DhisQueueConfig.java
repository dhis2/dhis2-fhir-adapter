package org.dhis2.fhir.adapter.dhis.queue;

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
import org.dhis2.fhir.adapter.dhis.sync.DhisResourceQueueItem;
import org.dhis2.fhir.adapter.dhis.sync.impl.DhisSyncConfig;
import org.dhis2.fhir.adapter.dhis.sync.impl.DhisSyncRequestQueueItem;
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
public class DhisQueueConfig
{
    private final DhisSyncConfig syncConfig;

    public DhisQueueConfig( @Nonnull DhisSyncConfig syncConfig )
    {
        this.syncConfig = syncConfig;
    }

    @Bean
    @Nonnull
    protected ArtemisConfigurationCustomizer dhisArtemisConfigurationCustomizer()
    {
        syncConfig.getSyncRequestQueue().getEmbeddedAddressSettings().setLastValueQueue( true );
        syncConfig.getDhisResourceQueue().getEmbeddedAddressSettings().setLastValueQueue( true );

        return configuration -> {
            configuration.addAddressesSetting( syncConfig.getSyncRequestQueue().getQueueName(), syncConfig.getSyncRequestQueue().getEmbeddedAddressSettings() );
            configuration.addAddressesSetting( syncConfig.getDhisResourceQueue().getQueueName(), syncConfig.getDhisResourceQueue().getEmbeddedAddressSettings() );
            configuration.addAddressesSetting( syncConfig.getDhisResourceDlQueue().getQueueName(), syncConfig.getDhisResourceDlQueue().getEmbeddedAddressSettings() );
        };
    }

    @Bean
    @Nonnull
    protected JMSQueueConfiguration syncRequestQueueConfiguration()
    {
        final JMSQueueConfiguration queueConfiguration = new JMSQueueConfigurationImpl();
        queueConfiguration.setName( syncConfig.getSyncRequestQueue().getQueueName() );
        queueConfiguration.setDurable( true );
        return queueConfiguration;
    }

    @Bean
    @Nonnull
    protected JMSQueueConfiguration dhisResourceQueueConfiguration()
    {
        final JMSQueueConfiguration queueConfiguration = new JMSQueueConfigurationImpl();
        queueConfiguration.setName( syncConfig.getDhisResourceQueue().getQueueName() );
        queueConfiguration.setDurable( true );
        return queueConfiguration;
    }

    @Bean
    @Nonnull
    protected JMSQueueConfiguration dhisResourceDlQueueConfiguration()
    {
        final JMSQueueConfiguration queueConfiguration = new JMSQueueConfigurationImpl();
        queueConfiguration.setName( syncConfig.getDhisResourceDlQueue().getQueueName() );
        queueConfiguration.setDurable( true );
        return queueConfiguration;
    }

    @Bean
    @Nonnull
    protected JmsTemplate dhisSyncRequestQueueJmsTemplate( @Nonnull ConnectionFactory connectionFactory, @Nonnull MessageConverter jmsMessageConverter )
    {
        final JmsTemplate jmsTemplate = new JmsTemplate( connectionFactory );
        jmsTemplate.setDefaultDestinationName( syncConfig.getSyncRequestQueue().getQueueName() );
        jmsTemplate.setMessageConverter( jmsMessageConverter );
        return jmsTemplate;
    }

    @Bean
    @Nonnull
    protected JmsTemplate dhisResourceQueueJmsTemplate( @Nonnull ConnectionFactory connectionFactory, @Nonnull MessageConverter jmsMessageConverter )
    {
        final JmsTemplate jmsTemplate = new JmsTemplate( connectionFactory );
        jmsTemplate.setDefaultDestinationName( syncConfig.getDhisResourceQueue().getQueueName() );
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
                    "dhisSyncRequest", DhisSyncRequestQueueItem.class,
                    "dhisResource", DhisResourceQueueItem.class );
            }
        };
    }
}
