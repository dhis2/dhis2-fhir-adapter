package org.dhis2.fhir.adapter.fhir.model;

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
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;

/**
 * The combination of system URI and code.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "SystemCodeValue", description = "Contains the combination of system URI and code." )
public class SystemCodeValue implements Serializable, Comparable<SystemCodeValue>
{
    private static final long serialVersionUID = -4751270623619799949L;

    public static final String SEPARATOR = "|";

    public static final int MAX_LENGTH = 230;

    private final String system;

    private final String code;

    private final String displayName;

    @Nullable
    public static SystemCodeValue parse( @Nullable String value ) throws IllegalArgumentException
    {
        if ( value == null )
        {
            return null;
        }

        final int index = value.indexOf( SEPARATOR );
        if ( index < 0 )
        {
            return new SystemCodeValue( null, value );
        }
        if ( index == 0 )
        {
            return new SystemCodeValue( null, value.substring( 1 ) );
        }
        if ( index + 1 == value.length() )
        {
            throw new IllegalArgumentException( "Value does not include a code: " + value );
        }
        return new SystemCodeValue( value.substring( 0, index ), value.substring( index + 1 ), null );
    }

    @JsonCreator
    public SystemCodeValue( @Nullable String system, @Nonnull String code, @Nullable String displayName )
    {
        this.system = system;
        this.code = code;
        this.displayName = displayName;
    }

    public SystemCodeValue( @Nullable String system, @Nonnull String code )
    {
        this( system, code, null );
    }

    @Nullable
    @ScriptMethod( description = "Returns the system URI." )
    public String getSystem()
    {
        return system;
    }

    @Nonnull
    @ScriptMethod( description = "Returns the code." )
    public String getCode()
    {
        return code;
    }

    @Nullable
    @ScriptMethod( description = "Returns the display name that is not available in all contexts." )
    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    @ScriptMethod
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        SystemCodeValue that = (SystemCodeValue) o;
        return Objects.equals( system, that.system ) &&
            Objects.equals( code, that.code );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( system, code );
    }

    @Override
    public int compareTo( @Nonnull SystemCodeValue o )
    {
        int v = StringUtils.compare( system, o.system );
        if ( v != 0 )
        {
            return v;
        }
        return code.compareTo( o.code );
    }

    @Override
    @ScriptMethod( description = "Returns the string representation of system URI and code. The string representation contains the system URI and the code separated by a pipe character (as used by FHIR searches)." )
    public String toString()
    {
        return (system == null) ? code : (system + SEPARATOR + code);
    }
}
