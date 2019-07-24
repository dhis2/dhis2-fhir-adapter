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

import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
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
public class WritableScriptedOrganizationUnit extends WritableScriptedDhisMetadata implements ScriptedOrganizationUnit, Serializable
{
    private static final long serialVersionUID = -9043373621936561310L;

    public WritableScriptedOrganizationUnit( @Nonnull OrganizationUnit organizationUnit, @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( organizationUnit, scriptExecutionContext );
    }

    @Nonnull
    @Override
    protected OrganizationUnit getManagedResource()
    {
        return (OrganizationUnit) resource;
    }

    @Nullable
    @Override
    public String getShortName()
    {
        return getManagedResource().getShortName();
    }

    @Nullable
    @Override
    public String getDisplayName()
    {
        return getManagedResource().getDisplayName();
    }

    @Override
    public boolean isLeaf()
    {
        return getManagedResource().isLeaf();
    }

    @Override
    public int getLevel()
    {
        return getManagedResource().getLevel();
    }

    @Nullable
    @Override
    public ZonedDateTime getOpeningDate()
    {
        return getManagedResource().getOpeningDate();
    }

    @Nullable
    @Override
    public ZonedDateTime getClosedDate()
    {
        return getManagedResource().getClosedDate();
    }

    @Nullable
    @Override
    public String getParentId()
    {
        return getManagedResource().getParentId();
    }

    @Nullable
    @Override
    public String getCoordinates()
    {
        return getManagedResource().getCoordinates();
    }
}
