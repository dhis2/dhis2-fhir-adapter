package org.dhis2.fhir.adapter.dhis.tracker.trackedentity.impl;

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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhis2.fhir.adapter.dhis.model.AttributeValue;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.WritableTrackedEntityType;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class DhisTrackedEntityType implements Serializable
{
    private static final long serialVersionUID = 5150630816129625111L;

    private String id;

    private String name;

    @JsonProperty( "trackedEntityTypeAttributes" )
    private List<DhisTrackedEntityTypeAttribute> typeAttributes;

    private List<AttributeValue> attributeValues;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public List<DhisTrackedEntityTypeAttribute> getTypeAttributes()
    {
        return typeAttributes;
    }

    public void setTypeAttributes( List<DhisTrackedEntityTypeAttribute> typeAttributes )
    {
        this.typeAttributes = typeAttributes;
    }

    public List<AttributeValue> getAttributeValues()
    {
        return attributeValues;
    }

    public void setAttributeValues( List<AttributeValue> attributeValues )
    {
        this.attributeValues = attributeValues;
    }

    public Optional<AttributeValue> getAttributeValue( String code )
    {
        return Optional.ofNullable( attributeValues ).orElse( Collections.emptyList() ).stream().filter( av -> Objects.equals( code, av.getAttribute().getCode() ) ).min( ( av1, av2 ) -> av2.getLastUpdated().compareTo( av1.getLastUpdated() ) );
    }

    public WritableTrackedEntityType toModel()
    {
        return new WritableTrackedEntityType( getId(), getName(), Optional.ofNullable( typeAttributes ).orElse( Collections.emptyList() ).stream().map( DhisTrackedEntityTypeAttribute::toModel ).collect( toList() ) );
    }
}
