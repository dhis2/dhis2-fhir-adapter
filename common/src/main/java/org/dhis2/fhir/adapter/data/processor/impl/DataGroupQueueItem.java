package org.dhis2.fhir.adapter.data.processor.impl;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhis2.fhir.adapter.data.model.DataGroupId;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * A data group that has been queued for processing. The instance
 * will be serialized and de-serialized to and from JSON.
 *
 * @param <I> the concrete type of the data group.
 * @author volsch
 */
public class DataGroupQueueItem<I extends DataGroupId> implements Serializable
{
    private static final long serialVersionUID = -7911324825049826913L;

    private I dataGroupId;

    private ZonedDateTime receivedAt;

    public DataGroupQueueItem()
    {
        super();
    }

    public DataGroupQueueItem( @Nonnull I dataGroupId, @Nonnull ZonedDateTime receivedAt )
    {
        this.dataGroupId = dataGroupId;
        this.receivedAt = receivedAt;
    }

    @JsonProperty
    public I getDataGroupId()
    {
        return dataGroupId;
    }

    public void setDataGroupId( I dataGroupId )
    {
        this.dataGroupId = dataGroupId;
    }

    @JsonProperty
    public ZonedDateTime getReceivedAt()
    {
        return receivedAt;
    }

    public void setReceivedAt( ZonedDateTime receivedAt )
    {
        this.receivedAt = receivedAt;
    }
}
