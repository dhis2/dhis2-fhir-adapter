package org.dhis2.fhir.adapter.dhis.model;

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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.validator.EnumValue;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * A reference of an entity that is stored in DHIS. The reference can be by a unique
 * code, a unique name (unique in scope of a specific entity) or a unique ID
 * (unique in scope of a specific entity).
 *
 * @author volsch
 */
public class Reference implements Serializable
{
    private static final long serialVersionUID = 6049184293580457755L;

    public static final int MAX_LENGTH = 230;

    public static final int MAX_VALUE_LENGTH = 200;

    @NotBlank
    @Size( max = MAX_VALUE_LENGTH )
    private final String value;

    @NotNull
    @EnumValue( ReferenceType.class )
    private final ReferenceType type;

    @Nonnull
    public static Reference createIdReference( @Nonnull String name )
    {
        return new Reference( name, ReferenceType.ID );
    }

    /**
     * @param name the code or name to which the reference refers to.
     * @param type the type of the reference (either a reference by code or by name).
     */
    @JsonCreator
    public Reference( @Nonnull @JsonProperty( "value" ) String name, @Nonnull @JsonProperty( "type" ) ReferenceType type )
    {
        this.value = name;
        this.type = type;
    }

    /**
     * @return the code or name to which the reference refers to.
     */
    @Nonnull
    public String getValue()
    {
        return value;
    }

    @Nonnull
    public ReferenceType getType()
    {
        return type;
    }

    @JsonIgnore
    public boolean isValid()
    {
        return StringUtils.isNotBlank( getValue() ) && (StringUtils.length( getValue() ) <= MAX_VALUE_LENGTH);
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Reference reference = (Reference) o;
        return Objects.equals( value, reference.value ) &&
            type == reference.type;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( value, type );
    }

    @Override
    public String toString()
    {
        return type + ":" + value;
    }
}
