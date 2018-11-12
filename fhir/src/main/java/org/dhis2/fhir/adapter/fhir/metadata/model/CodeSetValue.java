package org.dhis2.fhir.adapter.fhir.metadata.model;

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
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirAdapterMetadata;

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;

/**
 * Contains the value of the {@linkplain CodeSet code set} which can additionally be
 * enabled and disabled.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_code_set_value" )
public class CodeSetValue implements Serializable, Comparable<CodeSetValue>, FhirAdapterMetadata<CodeSetValueId>
{
    private static final long serialVersionUID = 8365594386802303061L;

    private CodeSetValueId id;

    private boolean enabled = true;

    @EmbeddedId
    public CodeSetValueId getId()
    {
        return id;
    }

    @Column( name = "enabled", nullable = false )
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    public void setId( CodeSetValueId id )
    {
        this.id = id;
    }

    @JsonIgnore
    @Transient
    public CodeSet getCodeSet()
    {
        return (id == null) ? null : id.getCodeSet();
    }

    public void setCodeSet( CodeSet codeSet )
    {
        if ( id == null )
        {
            id = new CodeSetValueId();
        }
        id.setCodeSet( codeSet );
    }

    @JsonIgnore
    @Transient
    public Code getCode()
    {
        return (id == null) ? null : id.getCode();
    }

    public void setCode( Code code )
    {
        if ( id == null )
        {
            id = new CodeSetValueId();
        }
        id.setCode( code );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        CodeSetValue that = (CodeSetValue) o;
        return Objects.equals( id, that.id );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id );
    }

    @Override
    public int compareTo( @Nonnull CodeSetValue o )
    {
        int value = ((getCodeSet() == null) && (o.getCodeSet() == null)) ? 0 :
            getCodeSet().getId().compareTo( o.getCodeSet().getId() );
        if ( value != 0 )
        {
            return value;
        }
        return getCode().getId().compareTo( o.getCode().getId() );
    }
}
