package org.dhis2.fhir.adapter.prototype.fhir.model;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.google.common.collect.ListMultimap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class WritableFhirRequest implements FhirRequest, Serializable
{
    private static final long serialVersionUID = 6482108680860344148L;

    private FhirRequestMethod requestMethod;

    private FhirResourceType resourceType;

    private ListMultimap<String, String> parameters;

    private FhirVersion version;

    @Override public FhirRequestMethod getRequestMethod()
    {
        return requestMethod;
    }

    public void setRequestMethod( FhirRequestMethod requestMethod )
    {
        this.requestMethod = requestMethod;
    }

    @Override public FhirResourceType getResourceType()
    {
        return resourceType;
    }

    public void setResourceType( FhirResourceType resourceType )
    {
        this.resourceType = resourceType;
    }

    public ListMultimap<String, String> getParameters()
    {
        return parameters;
    }

    public void setParameters( ListMultimap<String, String> parameters )
    {
        this.parameters = parameters;
    }

    @Override public boolean containsRequestParameter( @Nonnull String name )
    {
        return (getParameters() != null) && getParameters().containsKey( name );
    }

    @Nonnull @Override public Set<String> getParameterNames()
    {
        return (getParameters() == null) ? Collections.emptySet() : getParameters().keys().elementSet();
    }

    @Nullable @Override public List<String> getParameterValues( @Nonnull String name )
    {
        if ( (getParameters() == null) || !getParameters().containsKey( name ) )
        {
            return null;
        }
        return getParameters().get( name );
    }

    @Override public FhirVersion getVersion()
    {
        return version;
    }

    public void setVersion( FhirVersion version )
    {
        this.version = version;
    }
}
