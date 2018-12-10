package org.dhis2.fhir.adapter.dhis.queue;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dhis2.fhir.adapter.queue.QueueListenerErrorHandler;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.jms.JmsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nonnull;
import javax.jms.ConnectionFactory;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * General configuration of queues that are used by the adapter.
 *
 * @author volsch
 */
@Configuration
@Validated
public class QueueConfig
{
    @Primary
    @Bean
    @Nonnull
    protected MessageConverter jacksonJmsMessageConverter( @Nonnull ObjectMapper objectMapper, @Nonnull Collection<JmsJsonTypeIdMapping> typeIdMappings )
    {
        final Map<String, Class<?>> resultingMappings = new HashMap<>();
        typeIdMappings.forEach( tim -> resultingMappings.putAll( tim.getTypeIdMappings() ) );

        final MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper( objectMapper );
        converter.setTargetType( MessageType.TEXT );
        converter.setTypeIdPropertyName( "_type" );
        converter.setTypeIdMappings( resultingMappings );
        return converter;
    }

    @SuppressWarnings( "ConstantConditions" )
    @Bean
    @Nonnull
    protected DefaultJmsListenerContainerFactory jmsListenerContainerFactory( @Nonnull ConnectionFactory connectionFactory, @Nonnull DefaultJmsListenerContainerFactoryConfigurer configurer )
    {
        final DefaultJmsListenerContainerFactory listenerFactory = new DefaultJmsListenerContainerFactory();
        configurer.configure( listenerFactory, connectionFactory );
        listenerFactory.setSessionTransacted( false );
        listenerFactory.setTransactionManager( null );
        listenerFactory.setSessionAcknowledgeMode( JmsProperties.AcknowledgeMode.CLIENT.getMode() );
        listenerFactory.setErrorHandler( new QueueListenerErrorHandler() );
        return listenerFactory;
    }
}
