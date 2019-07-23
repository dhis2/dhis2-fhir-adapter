package org.dhis2.fhir.adapter.dhis.orgunit;

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
import org.dhis2.fhir.adapter.dhis.model.AbstractDhisMetadata;
import org.dhis2.fhir.adapter.dhis.model.DhisMetadata;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.model.Id;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Contains the required information of a DHIS2 organization unit.
 *
 * @author volsch
 */
public class OrganizationUnit extends AbstractDhisMetadata implements DhisResource, DhisMetadata, Serializable
{
    private static final long serialVersionUID = 3976508569865955265L;

    private ZonedDateTime lastUpdated;

    private String id;

    private String code;

    private String name;

    private String shortName;

    private String displayName;

    private boolean leaf;

    private int level;

    private ZonedDateTime openingDate;

    private ZonedDateTime closedDate;

    private Id parent;

    private String coordinates;

    @JsonIgnore
    @Nonnull
    @Override
    public DhisResourceType getResourceType()
    {
        return DhisResourceType.ORGANIZATION_UNIT;
    }

    @JsonIgnore
    @Override
    public boolean isNewResource()
    {
        return false;
    }

    @Override
    public void resetNewResource()
    {
        // nothing to be done
    }

    @JsonIgnore
    @Override
    public boolean isLocal()
    {
        return false;
    }

    @JsonIgnore
    @Override
    public boolean isDeleted()
    {
        return false;
    }

    @JsonIgnore
    @Override
    public DhisResourceId getResourceId()
    {
        return new DhisResourceId( DhisResourceType.ORGANIZATION_UNIT, getId() );
    }

    @Override
    public ZonedDateTime getLastUpdated()
    {
        return lastUpdated;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    @JsonIgnore
    @Override
    public String getOrgUnitId()
    {
        return getId();
    }

    public boolean isLeaf()
    {
        return leaf;
    }

    public void setLeaf( boolean leaf )
    {
        this.leaf = leaf;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel( int level )
    {
        this.level = level;
    }

    public ZonedDateTime getOpeningDate()
    {
        return openingDate;
    }

    public void setOpeningDate( ZonedDateTime openingDate )
    {
        this.openingDate = openingDate;
    }

    public ZonedDateTime getClosedDate()
    {
        return closedDate;
    }

    public void setClosedDate( ZonedDateTime closedDate )
    {
        this.closedDate = closedDate;
    }

    public Id getParent()
    {
        return parent;
    }

    public void setParent( Id parent )
    {
        this.parent = parent;
    }

    @JsonIgnore
    public String getParentId()
    {
        return (parent == null) ? null : parent.getId();
    }

    public void setParentId( String parentId )
    {
        this.parent = (parent == null) ? null : new Id( parentId );
    }

    public String getCoordinates()
    {
        return coordinates;
    }

    public void setCoordinates( String coordinates )
    {
        this.coordinates = coordinates;
    }
}
