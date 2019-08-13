package org.dhis2.fhir.adapter.fhir.transform.fhir.model;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Contains the system that is used for a specific FHIR resource type.
 *
 * @author volsch
 */
public class ResourceSystem implements Serializable
{
    private static final long serialVersionUID = 2691242272673918578L;

    private final FhirResourceType fhirResourceType;

    private final String system;

    private final String codePrefix;

    private final String defaultValue;

    private final String fhirDisplayName;

    private final boolean fhirId;

    public ResourceSystem( @Nonnull FhirResourceType fhirResourceType, @Nonnull String system, @Nullable String codePrefix, @Nullable String defaultValue, @Nullable String fhirDisplayName, boolean fhirId )
    {
        this.fhirResourceType = fhirResourceType;
        this.system = system;
        this.codePrefix = codePrefix;
        this.defaultValue = defaultValue;
        this.fhirDisplayName = fhirDisplayName;
        this.fhirId = fhirId;
    }

    public ResourceSystem( @Nonnull FhirResourceType fhirResourceType, @Nonnull String system )
    {
        this.fhirResourceType = fhirResourceType;
        this.system = system;
        this.codePrefix = null;
        this.defaultValue = null;
        this.fhirDisplayName = null;
        this.fhirId = false;
    }

    @Nonnull
    public FhirResourceType getFhirResourceType()
    {
        return fhirResourceType;
    }

    @Nonnull
    public String getSystem()
    {
        return system;
    }

    @Nullable
    public String getCodePrefix()
    {
        return codePrefix;
    }

    @Nullable
    public String getDefaultValue()
    {
        return defaultValue;
    }

    @Nullable
    public String getFhirDisplayName()
    {
        return fhirDisplayName;
    }

    public boolean isFhirId()
    {
        return fhirId;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        ResourceSystem that = (ResourceSystem) o;
        return fhirResourceType == that.fhirResourceType && Objects.equals( system, that.system );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( fhirResourceType, system );
    }
}
