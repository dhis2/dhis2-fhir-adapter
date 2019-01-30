package org.dhis2.fhir.adapter.fhir.repository;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.data.model.UuidDataGroupId;
import org.dhis2.fhir.adapter.data.processor.DataItemQueueItem;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.UUID;

/**
 * Processing request for a server FHIR resource that is enqueued and dequeued.
 * The class must support the initial legacy serialized format.
 * <b>The class must not be moved to a different package since the full qualified class name is used in JMS messages.</b>
 *
 * @author volsch
 */
public class FhirResource extends DataItemQueueItem<UuidDataGroupId> implements Serializable
{
    private static final long serialVersionUID = 1642564911249098319L;

    private boolean persistedDataItem;

    public FhirResource()
    {
        super();
    }

    public FhirResource( @Nonnull UuidDataGroupId dataGroupId, @Nonnull ProcessedItemInfo processedItemInfo, boolean persistedDataItem )
    {
        super( dataGroupId, processedItemInfo );
        this.persistedDataItem = persistedDataItem;
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
    public UUID getFhirServerResourceId()
    {
        return (getDataGroupId() == null) ? null : getDataGroupId().getId();
    }

    public void setFhirServerResourceId( UUID fhirServerResourceId )
    {
        super.setDataGroupId( (fhirServerResourceId == null) ? null : new UuidDataGroupId( fhirServerResourceId ) );
    }

    @JsonProperty( "fhirResourceId" )
    @Override
    public String getId()
    {
        return super.getId();
    }

    @Override
    public void setId( String id )
    {
        super.setId( id );
    }

    @JsonProperty( "fhirResourceVersion" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    @Override
    public String getVersion()
    {
        return super.getVersion();
    }

    @Override
    public void setVersion( String version )
    {
        super.setVersion( version );
    }

    public boolean isPersistedDataItem()
    {
        return persistedDataItem;
    }

    public void setPersistedDataItem( boolean persistedDataItem )
    {
        this.persistedDataItem = persistedDataItem;
    }

    @JsonIgnore
    public String getIdPart()
    {
        final String id = getId();
        if ( id == null )
        {
            return null;
        }
        final int index = id.indexOf( '/' );
        if ( index < 0 )
        {
            return id;
        }
        return id.substring( index + 1 );
    }
}
