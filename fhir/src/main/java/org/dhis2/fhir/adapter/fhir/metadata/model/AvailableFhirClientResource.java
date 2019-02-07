package org.dhis2.fhir.adapter.fhir.metadata.model;

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
import java.util.Objects;

/**
 * Immutable information about an available FHIR client resource.
 *
 * @author volsch
 */
public class AvailableFhirClientResource implements Serializable
{
    private static final long serialVersionUID = -3209909347076287930L;

    private final FhirResourceType resourceType;

    private final boolean virtual;

    @JsonCreator
    public AvailableFhirClientResource( @JsonProperty( "resourceType" ) @Nonnull FhirResourceType resourceType, @JsonProperty( "virtual" ) boolean virtual )
    {
        this.resourceType = resourceType;
        this.virtual = virtual;
    }

    @Nonnull
    public FhirResourceType getResourceType()
    {
        return resourceType;
    }

    public boolean isVirtual()
    {
        return virtual;
    }

    @Nonnull
    public AvailableFhirClientResource merge( @Nullable AvailableFhirClientResource a )
    {
        // non virtual overrides virtual
        if ( (a != null) && isVirtual() && !a.isVirtual() )
        {
            return a;
        }
        return this;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        AvailableFhirClientResource that = (AvailableFhirClientResource) o;
        return virtual == that.virtual &&
            resourceType == that.resourceType;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( resourceType, virtual );
    }

    @Override
    public String toString()
    {
        return "AvailableFhirClientResource{" + "resourceType=" + resourceType + ", virtual=" + virtual + '}';
    }
}
