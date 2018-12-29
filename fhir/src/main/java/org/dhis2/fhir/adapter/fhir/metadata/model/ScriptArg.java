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
import org.dhis2.fhir.adapter.jackson.JsonCacheIgnore;
import org.dhis2.fhir.adapter.jackson.JsonCachePropertyFilter;
import org.dhis2.fhir.adapter.model.VersionedBaseMetadata;
import org.dhis2.fhir.adapter.validator.EnumValue;

import javax.annotation.Nullable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * Defines a single argument of a script. Arguments of a script have names (are not referenced by their position) and
 * may be mandatory. A default value can be given also for mandatory arguments.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_script_argument" )
@JsonFilter( JsonCachePropertyFilter.FILTER_NAME )
public class ScriptArg extends VersionedBaseMetadata implements Serializable
{
    private static final long serialVersionUID = -5052962742547037363L;

    public static final int MAX_NAME_LENGTH = 30;

    public static final int MAX_DEFAULT_VALUE_LENGTH = 230;

    public static final String ARRAY_SEPARATOR = "|";

    protected static final String ARRAY_SEPARATOR_REGEXP = Pattern.quote( ARRAY_SEPARATOR );

    @NotBlank
    @Size( max = MAX_NAME_LENGTH )
    private String name;

    @NotNull
    @EnumValue( DataType.class )
    private DataType dataType;

    private boolean mandatory;

    private boolean array;

    private String defaultValue;

    private String description;

    @NotNull
    private Script script;

    @Basic
    @Column( name = "name", nullable = false, length = 30 )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Basic
    @Column( name = "data_type", nullable = false, length = 30 )
    @Enumerated( EnumType.STRING )
    public DataType getDataType()
    {
        return dataType;
    }

    public void setDataType( DataType dataType )
    {
        this.dataType = dataType;
    }

    @Basic
    @Column( name = "mandatory", nullable = false )
    public boolean isMandatory()
    {
        return mandatory;
    }

    public void setMandatory( boolean mandatory )
    {
        this.mandatory = mandatory;
    }

    @Basic
    @Column( name = "array_value", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isArray()
    {
        return array;
    }

    public void setArray( boolean array )
    {
        this.array = array;
    }

    @Basic
    @Column( name = "default_value", length = 230 )
    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue( String defaultValue )
    {
        this.defaultValue = defaultValue;
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

    @JsonCacheIgnore
    @ManyToOne( optional = false )
    @JoinColumn( name = "script_id", referencedColumnName = "id", nullable = false )
    public Script getScript()
    {
        return script;
    }

    public void setScript( Script script )
    {
        this.script = script;
    }

    @Nullable
    public static String[] splitArrayValues( @Nullable String values )
    {
        if ( values == null )
        {
            return null;
        }
        return values.split( ARRAY_SEPARATOR_REGEXP );
    }
}
