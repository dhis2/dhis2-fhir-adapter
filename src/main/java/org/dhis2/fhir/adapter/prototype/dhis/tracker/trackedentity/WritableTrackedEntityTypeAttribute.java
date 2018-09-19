package org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity;

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

import org.dhis2.fhir.adapter.prototype.dhis.model.ValueType;

import java.io.Serializable;

public class WritableTrackedEntityTypeAttribute implements TrackedEntityTypeAttribute, Serializable
{
    private static final long serialVersionUID = 9074338111048865188L;

    private String id;

    private String name;

    private ValueType valueType;

    private boolean mandatory;

    private boolean generated;

    private WritableTrackedEntityAttribute attribute;

    public WritableTrackedEntityTypeAttribute()
    {
        super();
    }

    public WritableTrackedEntityTypeAttribute( String id, String name, ValueType valueType, boolean mandatory, boolean generated, WritableTrackedEntityAttribute attribute )
    {
        this.id = id;
        this.name = name;
        this.valueType = valueType;
        this.mandatory = mandatory;
        this.generated = generated;
        this.attribute = attribute;
    }

    @Override
    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
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
    public ValueType getValueType()
    {
        return valueType;
    }

    public void setValueType( ValueType valueType )
    {
        this.valueType = valueType;
    }

    @Override
    public boolean isMandatory()
    {
        return mandatory;
    }

    public void setMandatory( boolean mandatory )
    {
        this.mandatory = mandatory;
    }

    @Override
    public boolean isGenerated()
    {
        return generated;
    }

    public void setGenerated( boolean generated )
    {
        this.generated = generated;
    }

    @Override
    public WritableTrackedEntityAttribute getAttribute()
    {
        return attribute;
    }

    public void setAttribute( WritableTrackedEntityAttribute attribute )
    {
        this.attribute = attribute;
    }

    @Override
    public String getAttributeId()
    {
        return (getAttribute() == null) ? null : getAttribute().getId();
    }
}
