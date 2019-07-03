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
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Writable scripted tracked entity instance that is used in evaluation and transformation
 * scripts and prevents accesses to the tracked entity instance domain object.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "OrganizationUnit", transformDataType = "DHIS_ORGANIZATION_UNIT", description = "Organization unit." )
public class WritableScriptedOrganizationUnit implements ScriptedOrganizationUnit, Serializable
{
    private static final long serialVersionUID = -9043373621936561310L;

    private final OrganizationUnit organizationUnit;

    public WritableScriptedOrganizationUnit( OrganizationUnit organizationUnit )
    {
        this.organizationUnit = organizationUnit;
    }

    @Override
    public boolean isNewResource()
    {
        return false;
    }

    @Override
    public boolean isLocal()
    {
        return organizationUnit.isLocal();
    }

    @Override
    public boolean isDeleted()
    {
        return false;
    }

    @Nullable
    @Override
    public String getId()
    {
        return organizationUnit.getId();
    }

    @Nonnull
    @Override
    public DhisResourceType getResourceType()
    {
        return organizationUnit.getResourceType();
    }

    @Nullable
    @Override
    public DhisResourceId getResourceId()
    {
        return organizationUnit.getResourceId();
    }

    @Nullable
    @Override
    @ScriptMethod( description = "Returns the date and time when the resource has been updated the last time or null if this is a new resource." )
    public ZonedDateTime getLastUpdated()
    {
        return organizationUnit.getLastUpdated();
    }

    @Nullable
    @Override
    public String getOrganizationUnitId()
    {
        return organizationUnit.getId();
    }

    @Nullable
    @Override
    public ScriptedTrackedEntityInstance getTrackedEntityInstance()
    {
        return null;
    }

    @Nullable
    @Override
    public String getCode()
    {
        return organizationUnit.getCode();
    }

    @Nullable
    @Override
    public String getName()
    {
        return organizationUnit.getName();
    }

    @Nullable
    @Override
    public String getShortName()
    {
        return organizationUnit.getShortName();
    }

    @Nullable
    @Override
    public String getDisplayName()
    {
        return organizationUnit.getDisplayName();
    }

    @Override
    public boolean isLeaf()
    {
        return organizationUnit.isLeaf();
    }

    @Override
    public int getLevel()
    {
        return organizationUnit.getLevel();
    }

    @Nullable
    @Override
    public ZonedDateTime getOpeningDate()
    {
        return organizationUnit.getOpeningDate();
    }

    @Nullable
    @Override
    public ZonedDateTime getClosedDate()
    {
        return organizationUnit.getClosedDate();
    }

    @Nullable
    @Override
    public String getParentId()
    {
        return organizationUnit.getParentId();
    }

    @Nullable
    @Override
    public String getCoordinates()
    {
        return organizationUnit.getCoordinates();
    }

    @Override
    public void validate() throws TransformerException
    {
        // nothing to be done since instance will not be modified currently
    }
}
