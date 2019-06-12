package org.dhis2.fhir.adapter.dhis.model;

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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * The unique ID of a DHIS2 resource including its resource type.
 *
 * @author volsch
 */
public class DhisResourceId implements Serializable
{
    private static final long serialVersionUID = -2135456188739405510L;

    protected static final char SEPARATOR = '/';

    protected static final Pattern ID_PATTERN = Pattern.compile( "[a-zA-Z0-9]{11,13}" );

    private final DhisResourceType type;

    private final String id;

    public static boolean isValidId( @Nullable String id )
    {
        if ( id == null )
        {
            return false;
        }

        return ID_PATTERN.matcher( id ).matches();
    }

    @Nullable
    public static DhisResourceId parse( @Nullable String resourceId )
    {
        if ( resourceId == null )
        {
            return null;
        }
        final int index = resourceId.indexOf( SEPARATOR );
        if ( (index <= 0) || (index + 1 == resourceId.length()) )
        {
            throw new IllegalArgumentException( "Invalid DHIS resource ID syntax: " + resourceId );
        }
        final DhisResourceType type = DhisResourceType.getByTypeName( resourceId.substring( 0, index ) );
        if ( type == null )
        {
            throw new IllegalArgumentException( "Invalid DHIS resource type in resource ID: " + resourceId );
        }
        return new DhisResourceId( type, resourceId.substring( index + 1 ) );
    }

    @Nonnull
    public static String toString( @Nonnull DhisResourceType type, @Nonnull String id )
    {
        return type.getTypeName() + SEPARATOR + id;
    }

    @JsonCreator
    public DhisResourceId( @JsonProperty( "type" ) @Nonnull DhisResourceType type, @JsonProperty( "id" ) @Nonnull String id )
    {
        this.type = type;
        this.id = id;
    }

    public DhisResourceType getType()
    {
        return type;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        DhisResourceId that = (DhisResourceId) o;
        return getType() == that.getType() &&
            Objects.equals( getId(), that.getId() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getType(), getId() );
    }

    @Override
    public String toString()
    {
        return toString( type, id );
    }
}
