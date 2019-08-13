package org.dhis2.fhir.adapter.fhir.model;

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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * List of ordered system code values with an optional text representation.
 *
 * @author volsch
 */
public class SystemCodeValues implements Serializable
{
    private static final long serialVersionUID = 8065532674239620314L;

    private final String text;

    private final List<SystemCodeValue> systemCodeValues;

    @JsonCreator
    public SystemCodeValues( @JsonProperty( "text" ) @Nullable String text, @JsonProperty( "systemCodeValues" ) @Nonnull List<SystemCodeValue> systemCodeValues )
    {
        this.text = text;
        this.systemCodeValues = new ArrayList<>( systemCodeValues );
    }

    public SystemCodeValues( @Nonnull SystemCodeValues systemCodeValues )
    {
        this.text = systemCodeValues.getText();
        this.systemCodeValues = new ArrayList<>( systemCodeValues.getSystemCodeValues() );
    }

    public SystemCodeValues( @Nonnull List<SystemCodeValue> systemCodeValues )
    {
        this.text = null;
        this.systemCodeValues = new ArrayList<>( systemCodeValues );
    }

    public SystemCodeValues()
    {
        this.text = null;
        this.systemCodeValues = new ArrayList<>();
    }

    @Nullable
    public String getText()
    {
        return text;
    }

    @Nonnull
    public List<SystemCodeValue> getSystemCodeValues()
    {
        return systemCodeValues;
    }
}
