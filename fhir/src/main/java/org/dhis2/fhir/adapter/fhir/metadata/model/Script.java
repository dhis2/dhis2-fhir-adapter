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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.dhis2.fhir.adapter.fhir.metadata.model.jackson.ScriptVariablePersistentSortedSetConverter;
import org.dhis2.fhir.adapter.jackson.ToManyPropertyFilter;
import org.dhis2.fhir.adapter.validator.EnumValue;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.SortedSet;

/**
 * Defines a script that may be implemented for several FHIR versions and in various programming languages. The script
 * may not be executable without specifying mandatory arguments of the script for which no default values have been
 * provided.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_script" )
@JsonFilter( ToManyPropertyFilter.FILTER_NAME )
public class Script extends VersionedBaseMetadata implements Serializable
{
    private static final long serialVersionUID = 2166269559735726192L;

    public static final int MAX_NAME_LENGTH = 230;

    public static final int MAX_CODE_LENGTH = 50;

    @NotBlank
    @Size( max = MAX_NAME_LENGTH )
    private String name;

    private String description;

    @NotBlank
    @Size( max = MAX_CODE_LENGTH )
    private String code;

    @NotNull
    @EnumValue( ScriptType.class )
    private ScriptType scriptType;

    @NotNull
    @EnumValue( DataType.class )
    private DataType returnType;

    @EnumValue( TransformDataType.class )
    private TransformDataType inputType;

    @EnumValue( TransformDataType.class )
    private TransformDataType outputType;

    private List<ScriptArg> arguments;

    private SortedSet<ScriptVariable> variables;

    private List<ScriptSource> sources;

    @Basic
    @Column( name = "name", nullable = false, length = 230 )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Basic
    @Column( name = "description", columnDefinition = "TEXT" )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @Basic
    @Column( name = "code", nullable = false, length = 50, unique = true )
    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @Basic
    @Column( name = "script_type", nullable = false, length = 30 )
    @Enumerated( EnumType.STRING )
    public ScriptType getScriptType()
    {
        return scriptType;
    }

    public void setScriptType( ScriptType scriptType )
    {
        this.scriptType = scriptType;
    }

    @Basic
    @Column( name = "return_type", nullable = false, length = 30 )
    @Enumerated( EnumType.STRING )
    public DataType getReturnType()
    {
        return returnType;
    }

    public void setReturnType( DataType returnType )
    {
        this.returnType = returnType;
    }

    @Basic
    @Column( name = "input_type", length = 30 )
    @Enumerated( EnumType.STRING )
    public TransformDataType getInputType()
    {
        return inputType;
    }

    public void setInputType( TransformDataType inputType )
    {
        this.inputType = inputType;
    }

    @Basic
    @Column( name = "output_type", length = 30 )
    @Enumerated( EnumType.STRING )
    public TransformDataType getOutputType()
    {
        return outputType;
    }

    public void setOutputType( TransformDataType outputType )
    {
        this.outputType = outputType;
    }

    @OneToMany( mappedBy = "script" )
    @OrderBy( "id" )
    public List<ScriptArg> getArguments()
    {
        return arguments;
    }

    public void setArguments( List<ScriptArg> scriptVariables )
    {
        this.arguments = scriptVariables;
    }

    @SuppressWarnings( "JpaAttributeTypeInspection" )
    @ElementCollection( fetch = FetchType.EAGER )
    @CollectionTable( name = "fhir_script_variable", joinColumns = @JoinColumn( name = "script_id" ) )
    @Column( name = "variable" )
    @Enumerated( EnumType.STRING )
    @OrderBy
    @JsonSerialize( converter = ScriptVariablePersistentSortedSetConverter.class )
    public SortedSet<ScriptVariable> getVariables()
    {
        return variables;
    }

    public void setVariables( SortedSet<ScriptVariable> variables )
    {
        this.variables = variables;
    }

    @OneToMany( mappedBy = "script", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    @OrderBy( "id" )
    @JsonIgnore
    public List<ScriptSource> getSources()
    {
        return sources;
    }

    public void setSources( List<ScriptSource> sources )
    {
        this.sources = sources;
    }
}
