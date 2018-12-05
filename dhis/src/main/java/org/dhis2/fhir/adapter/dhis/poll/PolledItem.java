package org.dhis2.fhir.adapter.dhis.poll;

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

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Item that is polled from DHIS2. It contains the ID of the item, the last updated
 * timestamp and the user that has updated the item last time.
 *
 * @author volsch
 */
public class PolledItem implements Serializable
{
    private static final long serialVersionUID = 5775458316433704977L;

    private String id;

    private Instant lastUpdated;

    private String storedBy;

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
    public Instant getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( Instant lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    @JsonProperty
    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        PolledItem that = (PolledItem) o;
        return Objects.equals( getId(), that.getId() ) &&
            Objects.equals( getLastUpdated(), that.getLastUpdated() ) &&
            Objects.equals( getStoredBy(), that.getStoredBy() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getId(), getLastUpdated(), getStoredBy() );
    }
}
