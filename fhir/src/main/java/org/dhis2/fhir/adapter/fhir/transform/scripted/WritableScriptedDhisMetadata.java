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

import org.dhis2.fhir.adapter.dhis.model.DhisMetadata;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Implementation of writable scripted metadata.
 *
 * @param <M> concrete metadata class.
 * @author volsch
 */
@Scriptable
public class WritableScriptedDhisMetadata<M extends DhisResource & DhisMetadata> implements ScriptedDhisMetadata, Serializable
{
    private static final long serialVersionUID = 8245822986881397171L;

    protected final M metadata;

    public WritableScriptedDhisMetadata( @Nonnull M metadata )
    {
        this.metadata = metadata;
    }

    @Nullable
    @Override
    public String getCode()
    {
        return metadata.getCode();
    }

    @Nullable
    @Override
    public String getName()
    {
        return metadata.getName();
    }

    @Nullable
    @Override
    public String getId()
    {
        return metadata.getId();
    }

    @Nonnull
    @Override
    public DhisResourceType getResourceType()
    {
        return metadata.getResourceType();
    }

    @Nullable
    @Override
    public DhisResourceId getResourceId()
    {
        return metadata.getResourceId();
    }

    @Override
    public boolean isNewResource()
    {
        return metadata.isNewResource();
    }

    @Override
    public boolean isLocal()
    {
        return metadata.isLocal();
    }

    @Override
    public boolean isDeleted()
    {
        return metadata.isDeleted();
    }

    @Nullable
    @Override
    public ZonedDateTime getLastUpdated()
    {
        return metadata.getLastUpdated();
    }

    @Nullable
    @Override
    public String getOrganizationUnitId()
    {
        return metadata.getOrgUnitId();
    }

    @Nullable
    @Override
    public ScriptedTrackedEntityInstance getTrackedEntityInstance()
    {
        return null;
    }

    @Override
    public void validate() throws TransformerException
    {
        // nothing to be done
    }
}
