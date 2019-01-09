package org.dhis2.fhir.adapter.data.processor;

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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhis2.fhir.adapter.data.model.DataGroupId;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.time.Instant;

/**
 * Contains information about a data item that should be processed. The instance
 * will be serialized and de-serialized to and from JSON.
 *
 * @param <I> the concrete type of the data group ID.
 */
public class DataItemQueueItem<I extends DataGroupId> implements Serializable
{
    private static final long serialVersionUID = -5179772435529403495L;

    private I dataGroupId;

    private String id;

    private String version;

    private Instant lastUpdated;

    private boolean deleted;

    public DataItemQueueItem()
    {
        super();
    }

    public DataItemQueueItem( @Nonnull I dataGroupId, @Nonnull ProcessedItemInfo processedItemInfo )
    {
        this.dataGroupId = dataGroupId;
        this.id = processedItemInfo.getId();
        this.version = processedItemInfo.getVersion();
        this.lastUpdated = processedItemInfo.getLastUpdated();
        this.deleted = processedItemInfo.isDeleted();
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
    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    @JsonProperty
    @JsonInclude( JsonInclude.Include.NON_NULL )
    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

    @JsonProperty
    @JsonInclude( JsonInclude.Include.NON_NULL )
    public Instant getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( Instant lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }

    @JsonProperty
    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted( boolean deleted )
    {
        this.deleted = deleted;
    }
}
