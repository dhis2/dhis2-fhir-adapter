package org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity;

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

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WritableTrackedEntityType implements TrackedEntityType, Serializable
{
    private static final long serialVersionUID = 797154293863611491L;

    private String id;

    private String name;

    private List<WritableTrackedEntityTypeAttribute> attributes;

    private transient volatile Map<String, WritableTrackedEntityTypeAttribute> attributesByName;

    private transient volatile Map<String, WritableTrackedEntityTypeAttribute> attributesByCode;

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

    @Override public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    @Override public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Override public List<WritableTrackedEntityTypeAttribute> getAttributes()
    {
        return attributes;
    }

    public void setAttributes( List<WritableTrackedEntityTypeAttribute> attributes )
    {
        this.attributes = attributes;
        this.attributesByName = null;
        this.attributesByCode = null;
    }

    @Override public Optional<WritableTrackedEntityTypeAttribute> getOptionalTypeAttributeByCode( @Nonnull String code )
    {
        if ( attributes == null )
        {
            return Optional.empty();
        }
        Map<String, WritableTrackedEntityTypeAttribute> attributesByCode = this.attributesByCode;
        if ( attributesByCode == null )
        {
            this.attributesByCode = attributesByCode = attributes.stream().filter( a -> StringUtils.isNotBlank( a.getAttribute().getCode() ) ).collect( Collectors.toMap( a -> a.getAttribute().getCode(), a -> a ) );
        }
        return Optional.ofNullable( attributesByCode.get( code ) );
    }

    @Nullable @Override public TrackedEntityTypeAttribute getTypeAttributeByCode( @Nonnull String code )
    {
        return getOptionalTypeAttributeByCode( code ).orElse( null );
    }

    @Override public Optional<WritableTrackedEntityTypeAttribute> getOptionalTypeAttributeByName( @Nonnull String name )
    {
        if ( attributes == null )
        {
            return Optional.empty();
        }
        Map<String, WritableTrackedEntityTypeAttribute> attributesByName = this.attributesByName;
        if ( attributesByName == null )
        {
            this.attributesByName = attributesByName = attributes.stream().collect( Collectors.toMap( a -> a.getAttribute().getName(), a -> a ) );
        }
        return Optional.ofNullable( attributesByName.get( name ) );
    }

    @Nullable @Override public TrackedEntityTypeAttribute getTypeAttributeByName( @Nonnull String name )
    {
        return getOptionalTypeAttributeByName( name ).orElse( null );
    }
}
