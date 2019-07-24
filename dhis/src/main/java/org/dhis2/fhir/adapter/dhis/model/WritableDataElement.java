package org.dhis2.fhir.adapter.dhis.model;

/*
 * Copyright (c) 2004-2019, University of Oslo
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
import org.dhis2.fhir.adapter.model.ValueType;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Writable implementation of {@link DataElement} that can also be used for
 * JSON serialization and deserialization.
 *
 * @author volsch
 */
public class WritableDataElement extends AbstractDhisMetadata implements DataElement, Serializable
{
    private static final long serialVersionUID = -3933887626350878763L;

    private String id;

    private String name;

    private String code;

    private String formName;

    private ValueType valueType;

    private boolean optionSetValue;

    private WritableOptionSet optionSet;

    @Override
    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    @JsonIgnore
    @Override
    public String getOrgUnitId()
    {
        return null;
    }

    @JsonIgnore
    @Override
    public DhisResourceId getResourceId()
    {
        return getId() == null ? null : new DhisResourceId( getResourceType(), getId() );
    }

    @JsonIgnore
    @Override
    public boolean isDeleted()
    {
        return false;
    }

    @JsonIgnore
    @Override
    public ZonedDateTime getLastUpdated()
    {
        return null;
    }

    @JsonIgnore
    @Nonnull
    @Override
    public DhisResourceType getResourceType()
    {
        return DhisResourceType.TRACKED_ENTITY_TYPE;
    }

    @JsonIgnore
    @Override
    public boolean isLocal()
    {
        return false;
    }

    @JsonIgnore
    @Override
    public boolean isNewResource()
    {
        return false;
    }

    @Override
    public void resetNewResource()
    {
        // nothing to be done
    }

    @Override
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Override
    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @Override
    public String getFormName()
    {
        return formName;
    }

    public void setFormName( String formName )
    {
        this.formName = formName;
    }

    @Override
    public ValueType getValueType()
    {
        return valueType;
    }

    public void setValueType( ValueType valueType )
    {
        this.valueType = valueType;
    }

    @Override
    public boolean isOptionSetValue()
    {
        return optionSetValue;
    }

    public void setOptionSetValue( boolean optionSetValue )
    {
        this.optionSetValue = optionSetValue;
    }

    @Override
    public WritableOptionSet getOptionSet()
    {
        return optionSet;
    }

    public void setOptionSet( WritableOptionSet optionSet )
    {
        this.optionSet = optionSet;
    }
}
