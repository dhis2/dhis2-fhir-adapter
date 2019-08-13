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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Immutable scripted event.
 *
 * @author volsch
 */
@Scriptable
public class ImmutableScriptedEvent extends ImmutableScriptedDhisResource implements ScriptedEvent, ImmutableDhisObject, Serializable
{
    private static final long serialVersionUID = -3248712035742910069L;

    public ImmutableScriptedEvent( @Nonnull WritableScriptedEvent delegate )
    {
        super( delegate );
    }

    @Nonnull
    protected ScriptedEvent getInternalDelegate()
    {
        return (ScriptedEvent) super.getDelegate();
    }

    @Nullable
    @Override
    public String getEnrollmentId()
    {
        return getInternalDelegate().getEnrollmentId();
    }

    @Override
    @Nonnull
    public Program getProgram()
    {
        return getInternalDelegate().getProgram();
    }

    @Nonnull
    @Override
    public ProgramStage getProgramStage()
    {
        return getInternalDelegate().getProgramStage();
    }

    @Nullable
    @Override
    public ScriptedTrackedEntityInstance getTrackedEntityInstance()
    {
        if ( getInternalDelegate().getTrackedEntityInstance() instanceof ImmutableDhisObject )
        {
            return getInternalDelegate().getTrackedEntityInstance();
        }

        return new ImmutableScriptedTrackedEntityInstance( Objects.requireNonNull( getInternalDelegate().getTrackedEntityInstance() ) );
    }

    @Nullable
    @Override
    public ZonedDateTime getEventDate()
    {
        return getInternalDelegate().getEventDate();
    }

    @Override
    @Nullable
    public ZonedDateTime getDueDate()
    {
        return getInternalDelegate().getDueDate();
    }

    @Nullable
    @Override
    public EventStatus getStatus()
    {
        return getInternalDelegate().getStatus();
    }

    @Override
    @Nullable
    public Location getCoordinate()
    {
        return getInternalDelegate().getCoordinate();
    }

    @Nullable
    @Override
    public Object getValue( @Nonnull Reference dataElementReference )
    {
        return getInternalDelegate().getValue( dataElementReference );
    }

    @Nullable
    @Override
    public Boolean getBooleanValue( @Nonnull Reference dataElementReference )
    {
        return getInternalDelegate().getBooleanValue( dataElementReference );
    }

    @Nullable
    @Override
    public Integer getIntegerValue( @Nonnull Reference dataElementReference )
    {
        return getInternalDelegate().getIntegerValue( dataElementReference );
    }

    @Nullable
    @Override
    public BigDecimal getBigDecimalValue( @Nonnull Reference dataElementReference )
    {
        return getInternalDelegate().getBigDecimalValue( dataElementReference );
    }

    @Nullable
    @Override
    public ZonedDateTime getDateTimeValue( @Nonnull Reference dataElementReference )
    {
        return getInternalDelegate().getDateTimeValue( dataElementReference );
    }

    @Nullable
    @Override
    public LocalDate getDateValue( @Nonnull Reference dataElementReference )
    {
        return getInternalDelegate().getDateValue( dataElementReference );
    }

    @Nullable
    @Override
    public String getStringValue( @Nonnull Reference dataElementReference )
    {
        return getInternalDelegate().getStringValue( dataElementReference );
    }

    @Nullable
    @Override
    public Integer getIntegerOptionValue( @Nonnull Reference dataElementReference, int valueBase, @Nullable Pattern optionValuePattern )
    {
        return getInternalDelegate().getIntegerOptionValue( dataElementReference, valueBase, optionValuePattern );
    }

    @Override
    public boolean isProvidedElsewhere( @Nonnull Reference dataElementReference )
    {
        return getInternalDelegate().isProvidedElsewhere( dataElementReference );
    }

    @Override
    public void validate() throws TransformerException
    {
        getInternalDelegate().validate();
    }
}
