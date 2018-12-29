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
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceAttributeConverter;
import org.dhis2.fhir.adapter.jackson.JsonCacheIgnore;
import org.dhis2.fhir.adapter.jackson.JsonCachePropertyFilter;
import org.dhis2.fhir.adapter.model.VersionedBaseMetadata;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * The reference of a rule to the data elements and attributes on
 * DHIS2.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_rule_dhis_data_ref" )
@JsonFilter( JsonCachePropertyFilter.FILTER_NAME )
public class RuleDhisDataReference extends VersionedBaseMetadata implements Serializable
{
    private static final long serialVersionUID = -2784006479143123933L;

    private AbstractRule rule;

    private Reference dataReference;

    private String scriptArgName;

    private String description;

    private boolean required;

    @JsonCacheIgnore
    @ManyToOne( optional = false )
    @JoinColumn( name = "rule_id", referencedColumnName = "id", nullable = false )
    public AbstractRule getRule()
    {
        return rule;
    }

    public void setRule( AbstractRule rule )
    {
        this.rule = rule;
    }

    @Basic
    @Column( name = "data_ref", nullable = false, length = 230 )
    @Convert( converter = ReferenceAttributeConverter.class )
    public Reference getDataReference()
    {
        return dataReference;
    }

    public void setDataReference( Reference dataReference )
    {
        this.dataReference = dataReference;
    }

    @Basic
    @Column( name = "script_arg_name", length = ScriptArg.MAX_NAME_LENGTH )
    public String getScriptArgName()
    {
        return scriptArgName;
    }

    public void setScriptArgName( String scriptArgName )
    {
        this.scriptArgName = scriptArgName;
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
    @Column( name = "required", columnDefinition = "BOOLEAN DEFAULT TRUE NOT NULL", nullable = false )
    public boolean isRequired()
    {
        return required;
    }

    public void setRequired( boolean required )
    {
        this.required = required;
    }
}
