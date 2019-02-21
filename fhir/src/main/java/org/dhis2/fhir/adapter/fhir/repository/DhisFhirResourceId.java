package org.dhis2.fhir.adapter.fhir.repository;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Includes the ID for a DHIS2 resource that is used by FHIR.
 *
 * @author volsch
 */
public class DhisFhirResourceId implements Serializable
{
    private static final long serialVersionUID = 2783682126270684036L;

    private static final Pattern UUID_PATTERN = Pattern.compile( "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})" );

    private final DhisResourceType type;

    private final String id;

    private final UUID ruleId;

    public DhisFhirResourceId( @Nonnull DhisResourceType type, @Nonnull String id, @Nonnull UUID ruleId )
    {
        this.type = type;
        this.id = id;
        this.ruleId = ruleId;
    }

    @Nonnull
    public DhisResourceType getType()
    {
        return type;
    }

    @Nonnull
    public String getId()
    {
        return id;
    }

    @Nonnull
    public UUID getRuleId()
    {
        return ruleId;
    }

    @Nonnull
    public DhisResourceId getDhisResourceId()
    {
        return new DhisResourceId( getType(), getId() );
    }

    @Override
    @Nonnull
    public String toString()
    {
        return toString( type, id, ruleId );
    }

    @Nonnull
    public static String toString( @Nonnull DhisResourceType type, @Nonnull String id, @Nonnull UUID ruleId )
    {
        return type.getAbbreviation() + "-" + id + "-" + StringUtils.remove( ruleId.toString(), '-' );
    }

    @Nonnull
    public static DhisFhirResourceId parse( @Nonnull String value ) throws IllegalArgumentException
    {
        final int firstIndex = value.indexOf( '-' );
        if ( firstIndex <= 0 )
        {
            throw new IllegalArgumentException( "Invalid DHIS FHIR ID: " + value );
        }
        final int secondIndex = value.indexOf( '-', firstIndex + 1 );
        if ( secondIndex < 0 )
        {
            throw new IllegalArgumentException( "Invalid DHIS FHIR ID: " + value );
        }

        final DhisResourceType type = DhisResourceType.getByAbbreviation( value.substring( 0, firstIndex ) );
        if ( type == null )
        {
            throw new IllegalArgumentException( "Invalid DHIS FHIR ID: " + value );
        }
        final String id = value.substring( firstIndex + 1, secondIndex );
        if ( StringUtils.isBlank( id ) || !StringUtils.isAlphanumeric( id ) )
        {
            throw new IllegalArgumentException( "Invalid DHIS FHIR ID: " + value );
        }
        final UUID ruleId;
        try
        {
            final Matcher matcher = UUID_PATTERN.matcher( value.substring( secondIndex + 1 ) );
            if ( !matcher.matches() )
            {
                throw new IllegalArgumentException( "Invalid DHIS FHIR ID: " + value );
            }
            ruleId = UUID.fromString( matcher.replaceAll( "$1-$2-$3-$4-$5" ) );
        }
        catch ( IllegalArgumentException e )
        {
            throw new IllegalArgumentException( "Invalid DHIS FHIR ID: " + value );
        }
        return new DhisFhirResourceId( type, id, ruleId );
    }
}
