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
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.dhis.model.AbstractDhisMetadata;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WritableTrackedEntityType extends AbstractDhisMetadata implements TrackedEntityType, Serializable
{
    private static final long serialVersionUID = 797154293863611491L;

    private String id;

    private String name;

    @JsonProperty( "trackedEntityTypeAttributes" )
    private List<WritableTrackedEntityTypeAttribute> attributes;

    @JsonIgnore
    private transient volatile Map<String, WritableTrackedEntityTypeAttribute> attributesByName;

    @JsonIgnore
    private transient volatile Map<String, WritableTrackedEntityTypeAttribute> attributesByCode;

    @JsonIgnore
    private transient volatile Map<String, WritableTrackedEntityTypeAttribute> attributesById;

    public WritableTrackedEntityType()
    {
        super();
    }

    public WritableTrackedEntityType( String id, String name, List<WritableTrackedEntityTypeAttribute> typeAttributes )
    {
        this.id = id;
        this.name = name;
        this.attributes = typeAttributes;
    }

    @Override
    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @JsonIgnore
    @Override
    public String getCode()
    {
        // tracked entity types do no have a code
        return null;
    }

    @JsonIgnore
    @Override
    public String getOrgUnitId()
    {
        return null;
    }

    @JsonIgnore
    @Override
    public DhisResourceId getResourceId()
    {
        return getId() == null ? null : new DhisResourceId( getResourceType(), getId() );
    }

    @JsonIgnore
    @Override
    public boolean isDeleted()
    {
        return false;
    }

    @JsonIgnore
    @Override
    public ZonedDateTime getLastUpdated()
    {
        return null;
    }

    @JsonIgnore
    @Nonnull
    @Override
    public DhisResourceType getResourceType()
    {
        return DhisResourceType.TRACKED_ENTITY_TYPE;
    }

    @JsonIgnore
    @Override
    public boolean isLocal()
    {
        return false;
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

    @Override
    public List<WritableTrackedEntityTypeAttribute> getAttributes()
    {
        return attributes;
    }

    public void setAttributes( List<WritableTrackedEntityTypeAttribute> attributes )
    {
        this.attributes = attributes;
        this.attributesByName = null;
        this.attributesByCode = null;
        this.attributesById = null;
    }

    @Nonnull
    @Override
    public Optional<? extends TrackedEntityTypeAttribute> getOptionalTypeAttribute( @Nonnull Reference reference )
    {
        switch ( reference.getType() )
        {
            case CODE:
                return getOptionalTypeAttributeByCode( reference.getValue() );
            case NAME:
                return getOptionalTypeAttributeByName( reference.getValue() );
            case ID:
                return getOptionalTypeAttributeById( reference.getValue() );
            default:
                throw new AssertionError( "Unhandled reference type: " + reference.getType() );
        }
    }

    @Nullable
    @Override
    public TrackedEntityTypeAttribute getTypeAttribute( @Nonnull Reference reference )
    {
        return getOptionalTypeAttribute( reference ).orElse( null );
    }

    @Nonnull
    public Optional<WritableTrackedEntityTypeAttribute> getOptionalTypeAttributeByCode( @Nonnull String code )
    {
        if ( attributes == null )
        {
            return Optional.empty();
        }
        Map<String, WritableTrackedEntityTypeAttribute> attributesByCode = this.attributesByCode;
        if ( attributesByCode == null )
        {
            this.attributesByCode = attributesByCode = attributes.stream().filter( a -> (a.getAttribute() != null) && StringUtils.isNotBlank( a.getAttribute().getCode() ) ).collect( Collectors.toMap( a -> a.getAttribute().getCode(), a -> a ) );
        }
        return Optional.ofNullable( attributesByCode.get( code ) );
    }

    @Nonnull
    public Optional<WritableTrackedEntityTypeAttribute> getOptionalTypeAttributeByName( @Nonnull String name )
    {
        if ( attributes == null )
        {
            return Optional.empty();
        }
        Map<String, WritableTrackedEntityTypeAttribute> attributesByName = this.attributesByName;
        if ( attributesByName == null )
        {
            this.attributesByName = attributesByName = attributes.stream().filter( a -> (a.getAttribute() != null) ).collect( Collectors.toMap( a -> a.getAttribute().getName(), a -> a ) );
        }
        return Optional.ofNullable( attributesByName.get( name ) );
    }

    @Nonnull
    public Optional<WritableTrackedEntityTypeAttribute> getOptionalTypeAttributeById( @Nonnull String id )
    {
        if ( attributes == null )
        {
            return Optional.empty();
        }
        Map<String, WritableTrackedEntityTypeAttribute> attributesById = this.attributesById;
        if ( attributesById == null )
        {
            this.attributesById = attributesById = attributes.stream().filter( a -> (a.getAttribute() != null) ).collect( Collectors.toMap( a -> a.getAttribute().getId(), a -> a ) );
        }
        return Optional.ofNullable( attributesById.get( id ) );
    }

    @Nullable
    @Override
    public String getItemId( @Nullable Reference reference )
    {
        if ( reference == null )
        {
            return null;
        }

        return getOptionalTypeAttribute( reference ).map( TrackedEntityTypeAttribute::getAttributeId ).orElse( null );
    }
}
