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

import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import java.io.Serializable;
import java.util.Objects;

/**
 * The combination of system URI and code.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "SystemCodeValue", description = "Contains the combination of system URI and code." )
public class SystemCodeValue implements Serializable
{
    private static final long serialVersionUID = -4751270623619799949L;

    public static final String SEPARATOR = "|";

    private final String system;

    private final String code;

    public SystemCodeValue( String system, String code )
    {
        this.system = system;
        this.code = code;
    }

    @ScriptMethod( description = "Returns the system URI." )
    public String getSystem()
    {
        return system;
    }

    @ScriptMethod( description = "Returns the code." )
    public String getCode()
    {
        return code;
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
    @ScriptMethod( description = "Returns the string representation of system URI and code. The string representation contains the system URI and the code separated by a pipe character (as used by FHIR searches)." )
    public String toString()
    {
        return (system == null) ? code : (system + SEPARATOR + code);
    }
}
