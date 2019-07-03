package org.dhis2.fhir.adapter.fhir.transform.scripted;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.ImmutableDhisObject;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Immutable scripted organization unit.
 *
 * @author volsch
 */
@Scriptable
public class ImmutableScriptedOrganizationUnit implements ScriptedOrganizationUnit, ImmutableDhisObject, Serializable
{
    private static final long serialVersionUID = 3106142635120155470L;

    private final ScriptedOrganizationUnit delegate;

    public ImmutableScriptedOrganizationUnit( ScriptedOrganizationUnit delegate )
    {
        this.delegate = delegate;
    }

    @Override
    @Nonnull
    public DhisResourceType getResourceType()
    {
        return delegate.getResourceType();
    }

    @Override
    @Nullable
    public String getCode()
    {
        return delegate.getCode();
    }

    @Override
    @Nullable
    public String getName()
    {
        return delegate.getName();
    }

    @Nullable
    @Override
    public String getShortName()
    {
        return delegate.getShortName();
    }

    @Override
    @Nullable
    public String getDisplayName()
    {
        return delegate.getDisplayName();
    }

    @Override
    public boolean isLocal()
    {
        return delegate.isLocal();
    }

    @Override
    public boolean isDeleted()
    {
        return delegate.isDeleted();
    }

    @Override
    public boolean isLeaf()
    {
        return delegate.isLeaf();
    }

    @Override
    public int getLevel()
    {
        return delegate.getLevel();
    }

    @Override
    @Nullable
    public ZonedDateTime getOpeningDate()
    {
        return delegate.getOpeningDate();
    }

    @Override
    @Nullable
    public ZonedDateTime getClosedDate()
    {
        return delegate.getClosedDate();
    }

    @Override
    @Nullable
    public String getParentId()
    {
        return delegate.getParentId();
    }

    @Override
    @Nullable
    public String getId()
    {
        return delegate.getId();
    }

    @Override
    @Nullable
    public DhisResourceId getResourceId()
    {
        return delegate.getResourceId();
    }

    @Override
    public boolean isNewResource()
    {
        return delegate.isNewResource();
    }

    @Override
    @Nullable
    public ZonedDateTime getLastUpdated()
    {
        return delegate.getLastUpdated();
    }

    @Override
    @Nullable
    public String getOrganizationUnitId()
    {
        return delegate.getOrganizationUnitId();
    }

    @Override
    @Nullable
    public ScriptedTrackedEntityInstance getTrackedEntityInstance()
    {
        return delegate.getTrackedEntityInstance();
    }

    @Nullable
    @Override
    public String getCoordinates()
    {
        return delegate.getCoordinates();
    }

    @Override
    public void validate() throws TransformerException
    {
        delegate.validate();
    }
}
