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
 * Defines a constant that can be substituted by its value in rules and transformations. E.g. the code value for a
 * gender that is defined in DHIS2 can be defined as a constant.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_constant" )
public class Constant implements Serializable
{
    private static final long serialVersionUID = -4219974054617859678L;

    private UUID id;
    private Long version;
    private LocalDateTime createdAt;
    private String lastUpdatedBy;
    private LocalDateTime lastUpdatedAt;
    private String name;
    private String description;
    private ConstantCategory category;
    private String code;
    private DataType dataType;
    private String value;

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

    @Basic @Column( name = "description", nullable = true, length = -1 ) public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @Basic @Column( name = "category", nullable = true, length = 30 ) public ConstantCategory getCategory()
    {
        return category;
    }

    public void setCategory( ConstantCategory category )
    {
        this.category = category;
    }

    @Basic @Column( name = "code", nullable = false, length = 50 ) public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @Basic @Column( name = "data_type", nullable = false, length = 30 ) public DataType getDataType()
    {
        return dataType;
    }

    public void setDataType( DataType dataType )
    {
        this.dataType = dataType;
    }

    @Basic @Column( name = "value", nullable = true, length = 250 ) public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
    }
}
