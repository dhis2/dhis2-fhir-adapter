package org.dhis2.fhir.adapter.dhis.sync;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.data.model.UuidDataGroupId;
import org.dhis2.fhir.adapter.data.processor.DataItemQueueItem;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.UUID;

/**
 * Processing request for a DHIS2 resource synchronization that is enqueued and dequeued.
 * <b>The class must not be moved to a different package since the full qualified class name is used in JMS messages.</b>
 *
 * @author volsch
 */
public class DhisResourceQueueItem extends DataItemQueueItem<UuidDataGroupId> implements Serializable
{
    private static final long serialVersionUID = 1642564911249098319L;

    public DhisResourceQueueItem()
    {
        super();
    }

    public DhisResourceQueueItem( @Nonnull UuidDataGroupId dataGroupId, @Nonnull ProcessedItemInfo processedItemInfo )
    {
        super( dataGroupId, processedItemInfo );
    }

    @JsonIgnore
    @Override
    public UuidDataGroupId getDataGroupId()
    {
        return super.getDataGroupId();
    }

    @Override
    public void setDataGroupId( UuidDataGroupId dataGroupId )
    {
        super.setDataGroupId( dataGroupId );
    }

    @JsonProperty
    public UUID getRemoteSubscriptionResourceId()
    {
        return (getDataGroupId() == null) ? null : getDataGroupId().getId();
    }

    public void setRemoteSubscriptionResourceId( UUID remoteSubscriptionResourceId )
    {
        super.setDataGroupId( (remoteSubscriptionResourceId == null) ? null : new UuidDataGroupId( remoteSubscriptionResourceId ) );
    }
}
