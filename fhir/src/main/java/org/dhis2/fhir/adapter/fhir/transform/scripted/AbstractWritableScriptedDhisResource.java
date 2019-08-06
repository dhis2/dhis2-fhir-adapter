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

import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;

/**
 * Abstract implementation of writable scripted resource.
 *
 * @author volsch
 */
@Scriptable
public abstract class AbstractWritableScriptedDhisResource
{
    private final DhisResourceType resourceType;

    private final String resourceId;

    protected DhisResource resource;

    protected final ScriptExecutionContext scriptExecutionContext;

    protected AbstractWritableScriptedDhisResource( @Nonnull DhisResourceType resourceType, @Nonnull String resourceId, @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.scriptExecutionContext = scriptExecutionContext;
    }

    protected AbstractWritableScriptedDhisResource( @Nonnull DhisResource resource, @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        this.resourceType = resource.getResourceType();
        this.resourceId = resource.getId();
        this.resource = resource;
        this.scriptExecutionContext = scriptExecutionContext;
    }

    @Nonnull
    protected DhisResource getInternalResource()
    {
        load();

        return resource;
    }

    @Nullable
    @ScriptMethod( description = "Returns the ID of the DHIS2 resource. Return null if the instance is new." )
    public String getId()
    {
        return resourceId;
    }

    @Nonnull
    public DhisResourceType getResourceType()
    {
        return resourceType;
    }

    @Nullable
    public DhisResourceId getResourceId()
    {
        return resourceId == null ? null : new DhisResourceId( resourceType, resourceId );
    }

    @ScriptMethod( description = "Returns if the DHIS2 resource is new and has not yet been saved on DHIS2." )
    public boolean isNewResource()
    {
        return getInternalResource().isNewResource();
    }

    public boolean isLocal()
    {
        return getInternalResource().isLocal();
    }

    public boolean isDeleted()
    {
        return getInternalResource().isDeleted();
    }

    @Nullable
    public ZonedDateTime getLastUpdated()
    {
        return getInternalResource().getLastUpdated();
    }

    @Nullable
    public String getOrganizationUnitId()
    {
        return getInternalResource().getOrgUnitId();
    }

    @Nullable
    public ScriptedTrackedEntityInstance getTrackedEntityInstance()
    {
        return null;
    }

    public void validate() throws TransformerException
    {
        // nothing to be done
    }

    /**
     * @return <code>true</code> if the resource itself has already been loaded.
     */
    protected boolean isLoaded()
    {
        return resource != null;
    }

    /**
     * Method may be overridden to load associated DHIS resource. After invocation of the method
     * the resource must have been loaded. If the resource cannot be loaded, an exception must
     * be thrown.
     */
    protected void load()
    {
        if ( !isLoaded() )
        {
            throw new IllegalStateException( "Resource has not yet been loaded and load method has not been overridden." );
        }
    }
}
