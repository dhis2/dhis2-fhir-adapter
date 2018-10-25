package org.dhis2.fhir.adapter.fhir.data.model;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity that contains if currently for {@linkplain RemoteSubscriptionResource remote subscription resource}
 * there is a pending queued processing request.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_queued_remote_subscription_request" )
public class QueuedRemoteSubscriptionRequest implements Serializable
{
    private static final long serialVersionUID = 4304414115903803395L;

    private UUID id;

    private RemoteSubscriptionResource subscriptionResource;

    private String requestId;

    private LocalDateTime queuedAt;

    @Id
    @Column( name = "id", nullable = false )
    public UUID getId()
    {
        return id;
    }

    public void setId( UUID id )
    {
        this.id = id;
    }

    @OneToOne( optional = false )
    @JoinColumn( name = "id", nullable = false )
    @MapsId
    public RemoteSubscriptionResource getSubscriptionResource()
    {
        return subscriptionResource;
    }

    public void setSubscriptionResource( RemoteSubscriptionResource subscriptionResource )
    {
        this.subscriptionResource = subscriptionResource;
    }

    @Basic
    @Column( name = "request_id", nullable = false )
    public String getRequestId()
    {
        return requestId;
    }

    public void setRequestId( String requestId )
    {
        this.requestId = requestId;
    }

    @Column( name = "queued_at", nullable = false )
    public LocalDateTime getQueuedAt()
    {
        return queuedAt;
    }

    public void setQueuedAt( LocalDateTime queuedAt )
    {
        this.queuedAt = queuedAt;
    }
}
