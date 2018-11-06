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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.dhis2.fhir.adapter.jackson.PersistentBagConverter;
import org.springframework.data.rest.core.annotation.RestResource;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Contains the definition of an executable {@linkplain Script script} where values of all mandatory arguments must
 * have been specified. Also default values of arguments can be overriden.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_executable_script" )
public class ExecutableScript extends VersionedBaseMetadata implements Serializable
{
    private static final long serialVersionUID = -2006842064596779970L;

    public static final int MAX_NAME_LENGTH = 230;

    public static final int MAX_CODE_LENGTH = 100;

    private UUID id;
    private Script script;
    private String name;
    private String code;
    private String description;
    private List<ExecutableScriptArg> overrideArguments;

    @ManyToOne
    @JoinColumn( name = "script_id", referencedColumnName = "id", nullable = false )
    @JsonIgnore
    public Script getScript()
    {
        return script;
    }

    public void setScript( Script script )
    {
        this.script = script;
    }

    @Basic
    @Column( name = "name", nullable = false )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Basic
    @Column( name = "code", nullable = false )
    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @Basic
    @Column( name = "description" )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @OneToMany( mappedBy = "script", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER )
    @OrderBy( "id" )
    @RestResource
    @JsonSerialize( converter = PersistentBagConverter.class )
    public List<ExecutableScriptArg> getOverrideArguments()
    {
        return overrideArguments;
    }

    public void setOverrideArguments( List<ExecutableScriptArg> overrideArguments )
    {
        this.overrideArguments = overrideArguments;
    }
}
