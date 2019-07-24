package org.dhis2.fhir.adapter.fhir.transform.dhis.model;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * The mutable DHIS request that caused the transformation from DHIS2 to FHIR resource.
 *
 * @author volsch
 */
public class WritableDhisRequest implements DhisRequest, Serializable
{
    private static final long serialVersionUID = 6482108680860344148L;

    private final boolean dhisFhirId;

    private boolean completeTransformation;

    private boolean includeReferences;

    private DhisResourceType resourceType;

    private ZonedDateTime lastUpdated;

    public WritableDhisRequest( boolean dhisFhirId, boolean completeTransformation, boolean includeReferences )
    {
        this.dhisFhirId = dhisFhirId;
        this.completeTransformation = completeTransformation;
        this.includeReferences = includeReferences;
    }

    @Override
    public boolean isDhisFhirId()
    {
        return dhisFhirId;
    }

    @Override
    public boolean isCompleteTransformation()
    {
        return completeTransformation;
    }

    public void setCompleteTransformation( boolean completeTransformation )
    {
        this.completeTransformation = completeTransformation;
    }

    @Override
    public boolean isIncludeReferences()
    {
        return includeReferences;
    }

    public void setIncludeReferences( boolean includeReferences )
    {
        this.includeReferences = includeReferences;
    }

    @Override
    public DhisResourceType getResourceType()
    {
        return resourceType;
    }

    public void setResourceType( DhisResourceType resourceType )
    {
        this.resourceType = resourceType;
    }

    @Nullable
    @Override
    public ZonedDateTime getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( ZonedDateTime lastUpdated )
    {
        this.lastUpdated = lastUpdated;
    }


}
