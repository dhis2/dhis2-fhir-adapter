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
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

/**
 * The arguments of an {@linkplain ExecutableScript executable script}. All arguments of the underlying
 * {@linkplain Script script} may be overridden by executable arguments.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_executable_script_argument" )
public class ExecutableScriptArg implements Serializable, FhirAdapterMetadata<UUID>
{
    private static final long serialVersionUID = 487628755797899218L;

    private UUID id;

    private String overrideValue;

    @NotNull
    private ExecutableScript script;

    @NotNull
    private ScriptArg argument;

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

    @Basic
    @Column( name = "override_value", length = 230 )
    public String getOverrideValue()
    {
        return overrideValue;
    }

    public void setOverrideValue( String overrideValue )
    {
        this.overrideValue = overrideValue;
    }

    @ManyToOne
    @JoinColumn( name = "executable_script_id", referencedColumnName = "id", nullable = false )
    @JsonIgnore
    @RestResource( exported = false )
    public ExecutableScript getScript()
    {
        return script;
    }

    public void setScript( ExecutableScript script )
    {
        this.script = script;
    }

    @ManyToOne
    @JoinColumn( name = "script_argument_id", referencedColumnName = "id", nullable = false )
    public ScriptArg getArgument()
    {
        return argument;
    }

    public void setArgument( ScriptArg argument )
    {
        this.argument = argument;
    }

    @Basic
    @Column( name = "enabled", nullable = false )
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }
}
