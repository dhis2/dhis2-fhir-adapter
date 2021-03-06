package org.dhis2.fhir.adapter.queue;

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

import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Configuration of a single queue that may run as an embedded queue.
 *
 * @author volsch
 */
@Validated
public class QueueConfig implements Serializable
{
    private static final long serialVersionUID = -2505291312383786207L;

    @NotBlank
    private String queueName;

    @NestedConfigurationProperty
    @Valid
    private QueueListenerConfig listener = new QueueListenerConfig();

    @NestedConfigurationProperty
    @Valid
    private AddressSettings embeddedAddressSettings = new AddressSettings();

    public String getQueueName()
    {
        return queueName;
    }

    public void setQueueName( String queueName )
    {
        this.queueName = queueName;
    }

    public QueueListenerConfig getListener()
    {
        return listener;
    }

    public void setListener( QueueListenerConfig listener )
    {
        this.listener = listener;
    }

    public AddressSettings getEmbeddedAddressSettings()
    {
        return embeddedAddressSettings;
    }

    public void setEmbeddedAddressSettings( AddressSettings embeddedAddressSettings )
    {
        this.embeddedAddressSettings = embeddedAddressSettings;
    }
}
