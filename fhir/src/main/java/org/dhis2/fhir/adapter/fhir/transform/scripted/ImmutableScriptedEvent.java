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
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.geo.Location;
import org.dhis2.fhir.adapter.scriptable.Scriptable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Immutable scripted event.
 *
 * @author volsch
 */
@Scriptable
public class ImmutableScriptedEvent implements ScriptedEvent, ImmutableDhisObject, Serializable
{
    private static final long serialVersionUID = -3248712035742910069L;

    private final ScriptedEvent delegate;

    public ImmutableScriptedEvent( @Nonnull ScriptedEvent delegate )
    {
        this.delegate = delegate;
    }

    @Override
    public boolean isNewResource()
    {
        return delegate.isNewResource();
    }

    @Override
    public boolean isDeleted()
    {
        return delegate.isDeleted();
    }

    @Nullable
    @Override
    public String getId()
    {
        return delegate.getId();
    }

    @Override
    @Nonnull
    public DhisResourceType getResourceType()
    {
        return delegate.getResourceType();
    }

    @Nullable
    @Override
    public DhisResourceId getResourceId()
    {
        return delegate.getResourceId();
    }

    @Nullable
    @Override
    public ZonedDateTime getLastUpdated()
    {
        return delegate.getLastUpdated();
    }

    @Nullable
    @Override
    public String getOrganizationUnitId()
    {
        return delegate.getOrganizationUnitId();
    }

    @Nullable
    @Override
    public String getEnrollmentId()
    {
        return delegate.getEnrollmentId();
    }

    @Override
    @Nonnull
    public Program getProgram()
    {
        return delegate.getProgram();
    }

    @Nonnull
    @Override
    public ProgramStage getProgramStage()
    {
        return delegate.getProgramStage();
    }

    @Nullable
    @Override
    public ScriptedTrackedEntityInstance getTrackedEntityInstance()
    {
        if ( delegate.getTrackedEntityInstance() instanceof ImmutableDhisObject )
        {
            return delegate.getTrackedEntityInstance();
        }
        return new ImmutableScriptedTrackedEntityInstance( Objects.requireNonNull( delegate.getTrackedEntityInstance() ) );
    }

    @Nullable
    @Override
    public ZonedDateTime getEventDate()
    {
        return delegate.getEventDate();
    }

    @Override
    @Nullable
    public ZonedDateTime getDueDate()
    {
        return delegate.getDueDate();
    }

    @Nullable
    @Override
    public EventStatus getStatus()
    {
        return delegate.getStatus();
    }

    @Override
    @Nullable
    public Location getCoordinate()
    {
        return delegate.getCoordinate();
    }

    @Nullable
    @Override
    public Object getValue( @Nonnull Reference dataElementReference )
    {
        return delegate.getValue( dataElementReference );
    }

    @Nullable
    @Override
    public Boolean getBooleanValue( @Nonnull Reference dataElementReference )
    {
        return delegate.getBooleanValue( dataElementReference );
    }

    @Nullable
    @Override
    public Integer getIntegerValue( @Nonnull Reference dataElementReference )
    {
        return delegate.getIntegerValue( dataElementReference );
    }

    @Nullable
    @Override
    public String getStringValue( @Nonnull Reference dataElementReference )
    {
        return delegate.getStringValue( dataElementReference );
    }

    @Nullable
    @Override
    public Integer getIntegerOptionValue( @Nonnull Reference dataElementReference, int valueBase, @Nullable Pattern optionValuePattern )
    {
        return delegate.getIntegerOptionValue( dataElementReference, valueBase, optionValuePattern );
    }

    @Override
    public boolean isProvidedElsewhere( @Nonnull Reference dataElementReference )
    {
        return delegate.isProvidedElsewhere( dataElementReference );
    }

    @Override
    public void validate() throws TransformerException
    {
        delegate.validate();
    }
}
