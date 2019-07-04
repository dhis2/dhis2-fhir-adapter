package org.dhis2.fhir.adapter.dhis.local.impl;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The local DHIS2 resource and its state.
 *
 * @param <T> the concrete type of the local DHIS2 resource.
 * @author volsch
 */
public class LocalDhisResource<T extends DhisResource>
{
    private T resource;

    private Object resourceKey;

    private LocalDhisResourceState state;

    public LocalDhisResource( @Nonnull T resource )
    {
        this.resource = resource;
        this.state = LocalDhisResourceState.FOUND;
    }

    public LocalDhisResource( @Nonnull T resource, @Nullable Object resourceKey, @Nonnull LocalDhisResourceState state )
    {
        this.resource = resource;
        this.resourceKey = resourceKey;
        this.state = state;
    }

    @Nonnull
    public T getResource()
    {
        return resource;
    }

    @Nullable
    public Object getResourceKey()
    {
        return resourceKey;
    }

    public void setResource( @Nonnull T resource )
    {
        this.resource = resource;
    }

    public void setResourceKey( Object resourceKey )
    {
        this.resourceKey = resourceKey;
    }

    @Nonnull
    public LocalDhisResourceState getState()
    {
        return state;
    }

    public void setState( @Nonnull LocalDhisResourceState state )
    {
        this.state = state;
    }

    public void setDeleted()
    {
        switch ( getState() )
        {
            case SAVED_NEW:
            case DELETED_NEW:
                setState( LocalDhisResourceState.DELETED_NEW );
                break;
            default:
                setState( LocalDhisResourceState.DELETED_EXISTING );
        }
    }
}
