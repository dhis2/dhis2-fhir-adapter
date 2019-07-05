package org.dhis2.fhir.adapter.dhis.tracker.trackedentity;

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
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Representation of a DHIS2 tracked entity instance resource.
 *
 * @author volsch
 */
public class TrackedEntityInstance implements DhisResource, Serializable
{
    private static final long serialVersionUID = -1707916238115298513L;

    @JsonIgnore
    private boolean newResource;

    @JsonIgnore
    private boolean local;

    @JsonProperty( access = JsonProperty.Access.WRITE_ONLY )
    private boolean deleted;

    @JsonProperty( "trackedEntityInstance" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String id;

    @JsonProperty( access = JsonProperty.Access.WRITE_ONLY )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private ZonedDateTime lastUpdated;

    @JsonIgnore
    private String identifier;

    @JsonProperty( "trackedEntityType" )
    private String typeId;

    @JsonProperty( "orgUnit" )
    private String orgUnitId;

    private String coordinates;

    private Collection<TrackedEntityAttributeValue> attributes;

    @JsonIgnore
    private boolean modified;

    public TrackedEntityInstance()
    {
        super();
    }

    public TrackedEntityInstance( @Nonnull String id )
    {
        this.id = id;
    }

    public TrackedEntityInstance( @Nonnull TrackedEntityType type, @Nullable String id, boolean newResource )
    {
        this.typeId = type.getId();
        this.id = id;
        this.newResource = newResource;
        this.local = newResource;
        this.modified = newResource;

        this.attributes = new ArrayList<>();

        if ( newResource )
        {
            for ( final TrackedEntityTypeAttribute typeAttribute : type.getAttributes() )
            {
                this.attributes.add( new TrackedEntityAttributeValue( typeAttribute.getAttributeId() ) );
            }
        }
    }

    @JsonIgnore
    @Override
    public DhisResourceId getResourceId()
    {
        return (getId() == null) ? null : new DhisResourceId( DhisResourceType.TRACKED_ENTITY, getId() );
    }

    @JsonIgnore
    @Nonnull
    @Override
    public DhisResourceType getResourceType()
    {
        return DhisResourceType.TRACKED_ENTITY;
    }

    @Override
    public ZonedDateTime getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( ZonedDateTime lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean isNewResource()
    {
        return newResource;
    }

    public void setNewResource( boolean newResource )
    {
        this.newResource = newResource;
        this.modified = true;
    }

    @Override
    public void resetNewResource()
    {
        this.newResource = false;
        this.modified = false;

        if ( lastUpdated == null )
        {
            lastUpdated = ZonedDateTime.now();
        }
    }

    @Override
    public boolean isLocal()
    {
        return local;
    }

    public void setLocal( boolean local )
    {
        this.local = local;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted( boolean deleted )
    {
        this.deleted = deleted;
    }

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier( String identifier )
    {
        this.identifier = identifier;
    }

    public String getTypeId()
    {
        return typeId;
    }

    public void setTypeId( String typeId )
    {
        this.typeId = typeId;
    }

    public String getOrgUnitId()
    {
        return orgUnitId;
    }

    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    public String getCoordinates()
    {
        return coordinates;
    }

    public void setCoordinates( String coordinates )
    {
        this.coordinates = coordinates;
    }

    public Collection<TrackedEntityAttributeValue> getAttributes()
    {
        return attributes;
    }

    public void setAttributes( Collection<TrackedEntityAttributeValue> attributes )
    {
        this.attributes = attributes;
    }

    public boolean containsAttribute( @Nonnull String attributeId )
    {
        return getAttributes().stream().anyMatch( a -> Objects.equals( attributeId, a.getAttributeId() ) );
    }

    public boolean containsAttribute( @Nonnull String attributeId, @Nonnull String value )
    {
        return getAttributes().stream().anyMatch( a -> Objects.equals( attributeId, a.getAttributeId() ) && a.getValue() != null && Objects.equals( String.valueOf( a.getValue() ), value ) );
    }

    public boolean containsAttributeWithValue( @Nonnull String attributeId )
    {
        return getAttributes().stream().filter( a -> (a.getValue() != null) )
            .anyMatch( a -> Objects.equals( attributeId, a.getAttributeId() ) );
    }

    public boolean isModified()
    {
        return modified;
    }

    public void setModified( boolean modified )
    {
        this.modified = modified;
    }

    @Nonnull
    public TrackedEntityAttributeValue getAttribute( @Nonnull String attributeId )
    {
        if ( getAttributes() == null )
        {
            setAttributes( new ArrayList<>() );
        }
        TrackedEntityAttributeValue attributeValue = getAttributes().stream().filter( a -> Objects.equals( attributeId, a.getAttributeId() ) ).findFirst().orElse( null );
        if ( attributeValue == null )
        {
            attributeValue = new TrackedEntityAttributeValue( attributeId );
            getAttributes().add( attributeValue );
        }
        return attributeValue;
    }
}
