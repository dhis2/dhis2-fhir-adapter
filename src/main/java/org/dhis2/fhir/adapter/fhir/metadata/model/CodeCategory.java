package org.dhis2.fhir.adapter.fhir.metadata.model;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Contains the category of a {@linkplain Code code}. E.g. there may be multiple codes for the category "vaccine".
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_code_category" )
public class CodeCategory implements Serializable
{
    private static final long serialVersionUID = 2265589184934267879L;

    private UUID id;
    private Long version;
    private LocalDateTime createdAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
    private String name;
    private String code;
    private String description;

    @GeneratedValue( generator = "uuid2" )
    @GenericGenerator( name = "uuid2", strategy = "uuid2" )
    @Id @Column( name = "id", nullable = false ) public UUID getId()
    {
        return id;
    }

    public void setId( UUID id )
    {
        this.id = id;
    }

    @Basic @Column( name = "version", nullable = false ) public Long getVersion()
    {
        return version;
    }

    public void setVersion( Long version )
    {
        this.version = version;
    }

    @Basic @Column( name = "created_at", nullable = false ) public LocalDateTime getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt( LocalDateTime createdAt )
    {
        this.createdAt = createdAt;
    }

    @Basic @Column( name = "last_updated_by", nullable = true, length = 11 ) public String getLastUpdatedBy()
    {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy( String lastUpdatedBy )
    {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Basic @Column( name = "last_updated_at", nullable = false ) public LocalDateTime getLastUpdatedAt()
    {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt( LocalDateTime lastUpdatedAt )
    {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @Basic @Column( name = "name", nullable = false, length = 230 ) public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Basic @Column( name = "code", nullable = false, length = 50 )
    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @Basic @Column( name = "description", nullable = true, length = -1 ) public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }
}
