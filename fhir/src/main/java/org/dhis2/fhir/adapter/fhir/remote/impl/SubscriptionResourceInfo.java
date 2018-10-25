package org.dhis2.fhir.adapter.fhir.remote.impl;

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

import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * The information about a queried subscription resource. The last updated timestamp
 * and the version may not be returned by the server.
 *
 * @author volsch
 */
public class SubscriptionResourceInfo implements Serializable
{
    private static final long serialVersionUID = 1808470990206683252L;

    private final String id;

    private final ZonedDateTime lastUpdated;

    private final String version;

    public SubscriptionResourceInfo( @Nonnull String id, @Nullable ZonedDateTime lastUpdated, @Nullable String version )
    {
        this.lastUpdated = lastUpdated;
        this.id = id;
        this.version = version;
    }

    @Nonnull
    public String getId()
    {
        return id;
    }

    @Nullable
    public ZonedDateTime getLastUpdated()
    {
        return lastUpdated;
    }

    @Nullable
    public String getVersion()
    {
        return version;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        SubscriptionResourceInfo that = (SubscriptionResourceInfo) o;
        return Objects.equals( id, that.id ) &&
            Objects.equals( lastUpdated, that.lastUpdated ) &&
            Objects.equals( version, that.version );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, lastUpdated, version );
    }

    @Nonnull
    public String toVersionString( @Nonnull ZonedDateTime defaultLastUpdated )
    {
        return getId() + "|" + StringUtils.defaultString( getVersion(), "?" ) + "|" +
            ((getLastUpdated() == null) ? ("X" + defaultLastUpdated.toInstant().toEpochMilli()) : getLastUpdated().toInstant().toEpochMilli());
    }
}
