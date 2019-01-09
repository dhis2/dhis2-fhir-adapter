package org.dhis2.fhir.adapter.data.model;

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

import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * The information about a processed item. The last updated timestamp
 * and the version are optional.
 *
 * @author volsch
 */
public class ProcessedItemInfo implements Serializable
{
    private static final long serialVersionUID = 1808470990206683252L;

    private final String id;

    private final long lastUpdated;

    private final String version;

    private final boolean deleted;

    public ProcessedItemInfo( @Nonnull String id, @Nullable Instant lastUpdated, boolean deleted )
    {
        this( id, lastUpdated, null, deleted );
    }

    public ProcessedItemInfo( @Nonnull String id, @Nullable Instant lastUpdated, @Nullable String version, boolean deleted )
    {
        this.id = id;
        this.lastUpdated = (lastUpdated == null) ? 0 : lastUpdated.toEpochMilli();
        this.version = version;
        this.deleted = deleted;
    }

    @Nonnull
    public String getId()
    {
        return id;
    }

    @Nullable
    public Instant getLastUpdated()
    {
        return (lastUpdated == 0) ? null : Instant.ofEpochMilli( lastUpdated );
    }

    @Nullable
    public String getVersion()
    {
        return version;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        ProcessedItemInfo that = (ProcessedItemInfo) o;
        return Objects.equals( id, that.id ) &&
            (lastUpdated == that.lastUpdated) &&
            Objects.equals( version, that.version );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, lastUpdated, version );
    }

    @Nonnull
    public String toIdString( @Nonnull Instant defaultLastUpdated )
    {
        final StringBuilder stringId = new StringBuilder();
        if ( StringUtils.isBlank( getVersion() ) )
        {
            stringId.append( getId() ).append( "|?|" ).append( (lastUpdated == 0) ? defaultLastUpdated.toEpochMilli() : lastUpdated );
        }
        else
        {
            stringId.append( getId() ).append( '|' ).append( getVersion() );
        }
        if ( deleted )
        {
            stringId.append( "|X" );
        }
        return stringId.toString();
    }
}
