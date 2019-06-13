package org.dhis2.fhir.adapter.model;

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

import org.dhis2.fhir.adapter.security.AdapterSecurityUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Base metadata class that contains the base metadata properties.
 *
 * @author volsch
 */
@MappedSuperclass
public class VersionedBaseMetadata implements Serializable, UuidIdentifiable, Metadata
{
    private static final long serialVersionUID = 7500268787032387101L;

    private UUID id;
    private Long version;
    private Instant createdAt;
    private String lastUpdatedBy;
    private Instant lastUpdatedAt;

    @Override
    @GeneratedValue( generator = "custom-uuid2" )
    @GenericGenerator( name = "custom-uuid2", strategy = "org.dhis2.fhir.adapter.hibernate.CustomUuidGenerator" )
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

    @Version
    @Column( name = "version", nullable = false, columnDefinition = "BIGINT DEFAULT 0 NOT NULL" )
    public Long getVersion()
    {
        return version;
    }

    public void setVersion( Long version )
    {
        this.version = version;
    }

    @Basic
    @Column( name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP(3) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP() NOT NULL" )
    public Instant getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt( Instant createdAt )
    {
        this.createdAt = createdAt;
    }

    @Basic
    @Column( name = "last_updated_by", length = 11 )
    public String getLastUpdatedBy()
    {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy( String lastUpdatedBy )
    {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Basic
    @Column( name = "last_updated_at", nullable = false, columnDefinition = "TIMESTAMP(3) WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP() NOT NULL" )
    @LastModifiedDate
    public Instant getLastUpdatedAt()
    {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt( Instant lastUpdatedAt )
    {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @PrePersist
    protected void onPrePersist()
    {
        setCreatedAt( Instant.now() );
        setLastUpdatedAt( getCreatedAt() );
        setLastUpdatedBy( AdapterSecurityUtils.getCurrentUsername() );
    }

    @PreUpdate
    protected void onPreUpdate()
    {
        setLastUpdatedAt( Instant.now() );
        setLastUpdatedBy( AdapterSecurityUtils.getCurrentUsername() );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        VersionedBaseMetadata that = (VersionedBaseMetadata) o;

        return Objects.equals( getId(), that.getId() ) && getId() != null;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getId() );
    }
}
