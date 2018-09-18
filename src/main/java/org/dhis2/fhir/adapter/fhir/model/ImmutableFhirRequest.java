package org.dhis2.fhir.adapter.fhir.model;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ImmutableFhirRequest implements FhirRequest, Serializable
{
    private static final long serialVersionUID = 8079249171843824509L;

    private final FhirRequest delegate;

    public ImmutableFhirRequest( @Nonnull FhirRequest delegate )
    {
        this.delegate = delegate;
    }

    @Nullable @Override public FhirRequestMethod getRequestMethod()
    {
        return delegate.getRequestMethod();
    }

    @Nullable @Override public FhirResourceType getResourceType()
    {
        return delegate.getResourceType();
    }

    @Nullable @Override public String getResourceId()
    {
        return delegate.getResourceId();
    }

    @Override public boolean containsRequestParameters()
    {
        return delegate.containsRequestParameters();
    }

    @Override public boolean containsRequestParameter( @Nonnull String name )
    {
        return delegate.containsRequestParameter( name );
    }

    @Nonnull @Override public Set<String> getParameterNames()
    {
        return Collections.unmodifiableSet( delegate.getParameterNames() );
    }

    @Nullable @Override public List<String> getParameterValues( @Nonnull String name )
    {
        final List<String> values = delegate.getParameterValues( name );
        return (values == null) ? null : Collections.unmodifiableList( values );
    }

    @Nonnull @Override public FhirVersion getVersion()
    {
        return delegate.getVersion();
    }
}
