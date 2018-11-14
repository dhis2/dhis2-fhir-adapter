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
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Contains the value of the {@linkplain CodeSet code set} which can additionally be
 * enabled and disabled.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_code_set_value" )
public class CodeSetValue implements Serializable, FhirAdapterMetadata<UUID>
{
    private static final long serialVersionUID = 8365594386802303061L;

    private UUID id;

    private CodeSet codeSet;

    @NotNull
    private Code code;

    private boolean enabled = true;

    @Override
    @GeneratedValue( generator = "uuid2" )
    @GenericGenerator( name = "uuid2", strategy = "uuid2" )
    @Id
    @Column( name = "id", nullable = false )
    public UUID getId()
    {
        return id;
    }

    public void setId( UUID id )
    {
        this.id = id;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "code_set_id", nullable = false )
    @JsonIgnore
    public CodeSet getCodeSet()
    {
        return codeSet;
    }

    public void setCodeSet( CodeSet codeSet )
    {
        this.codeSet = codeSet;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "code_id", nullable = false )
    public Code getCode()
    {
        return code;
    }

    public void setCode( Code code )
    {
        this.code = code;
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

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        CodeSetValue that = (CodeSetValue) o;
        return Objects.equals( (codeSet == null) ? null : codeSet.getId(), (that.codeSet == null) ? null : that.codeSet.getId() ) &&
            Objects.equals( (code == null) ? null : code.getId(), (that.code == null) ? null : that.code.getId() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( (codeSet == null) ? null : codeSet.getId(), (code == null) ? null : code.getId() );
    }

    @Override
    public String toString()
    {
        return "[" + "codeSetId=" + ((codeSet == null) ? "" : codeSet.getId()) + ", codeId=" + ((code == null) ? "" : code.getId()) + ']';
    }
}
