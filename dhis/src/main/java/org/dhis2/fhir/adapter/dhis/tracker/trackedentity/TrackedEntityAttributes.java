package org.dhis2.fhir.adapter.dhis.tracker.trackedentity;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.dhis.model.Reference;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrackedEntityAttributes implements Serializable
{
    private static final long serialVersionUID = -7879360551610569923L;

    @JsonProperty( "trackedEntityAttributes" )
    private Collection<WritableTrackedEntityAttribute> attributes;

    @JsonIgnore
    private transient volatile Map<String, TrackedEntityAttribute> attributesById;

    @JsonIgnore
    private transient volatile Map<String, TrackedEntityAttribute> attributesByName;

    @JsonIgnore
    private transient volatile Map<String, TrackedEntityAttribute> attributesByCode;

    public TrackedEntityAttributes()
    {
        this.attributes = Collections.emptyList();
    }

    public TrackedEntityAttributes( @Nonnull Collection<WritableTrackedEntityAttribute> attributes )
    {
        this.attributes = attributes;
    }

    @Nonnull
    public Optional<TrackedEntityAttribute> getOptional( @Nonnull Reference reference )
    {
        switch ( reference.getType() )
        {
            case CODE:
                return getOptionalByCode( reference.getValue() );
            case NAME:
                return getOptionalByName( reference.getValue() );
            case ID:
                return getOptionalById( reference.getValue() );
            default:
                throw new AssertionError( "Unhandled reference type: " + reference.getType() );
        }
    }

    @Nonnull
    public Optional<TrackedEntityAttribute> getOptionalById( @Nonnull String id )
    {
        Map<String, TrackedEntityAttribute> attributesById = this.attributesById;
        if ( attributesById == null )
        {
            this.attributesById = attributesById = attributes.stream().collect( Collectors.toMap( TrackedEntityAttribute::getId, a -> a ) );
        }
        return Optional.ofNullable( attributesById.get( id ) );
    }

    @Nonnull
    public Optional<TrackedEntityAttribute> getOptionalByCode( @Nonnull String code )
    {
        Map<String, TrackedEntityAttribute> attributesByCode = this.attributesByCode;
        if ( attributesByCode == null )
        {
            this.attributesByCode = attributesByCode = attributes.stream().filter( a -> StringUtils.isNotBlank( a.getCode() ) ).collect( Collectors.toMap( TrackedEntityAttribute::getCode, a -> a ) );
        }
        return Optional.ofNullable( attributesByCode.get( code ) );
    }

    @Nonnull
    public Optional<TrackedEntityAttribute> getOptionalByName( @Nonnull String name )
    {
        Map<String, TrackedEntityAttribute> attributesByName = this.attributesByName;
        if ( attributesByName == null )
        {
            this.attributesByName = attributesByName = attributes.stream().collect( Collectors.toMap( TrackedEntityAttribute::getName, a -> a ) );
        }
        return Optional.ofNullable( attributesByName.get( name ) );
    }
}
