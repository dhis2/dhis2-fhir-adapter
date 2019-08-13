package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.dstu3;

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
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedEvent;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.geo.Location;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Unit tests for {@link Dstu3ProgramStageFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
public class Dstu3ProgramStageFhirToDhisTransformerUtilsTest
{
    @Mock
    private ScriptExecutionContext scriptExecutionContext;

    @InjectMocks
    private Dstu3ProgramStageFhirToDhisTransformerUtils utils;

    private List<ScriptedEvent> events;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void before()
    {
        events = new ArrayList<>();
        Event event = new Event();
        event.setId( "1" );
        event.setEventDate( ZonedDateTime.now().minusDays( 1L ) );
        events.add( new TestScriptedEvent( event ) );
        event = new Event();
        event.setId( "2" );
        event.setEventDate( ZonedDateTime.now().minusDays( 2L ) );
        events.add( new TestScriptedEvent( event ) );
        event = new Event();
        event.setId( "3" );
        event.setEventDate( ZonedDateTime.now().minusDays( 3L ) );
        events.add( new TestScriptedEvent( event ) );
    }

    @Test
    public void containsEventDayBefore()
    {
        Assert.assertFalse( utils.containsEventDay( events, ZonedDateTime.now().minusDays( 4 ) ) );
    }

    @Test
    public void containsEventDayAfter()
    {
        Assert.assertFalse( utils.containsEventDay( events, new Date() ) );
    }

    @Test
    public void containsEventDay()
    {
        Assert.assertTrue( utils.containsEventDay( events, new DateTimeType( Date.from( ZonedDateTime.now().minusDays( 2 ).toInstant() ) ) ) );
    }

    protected static class TestScriptedEvent implements ScriptedEvent
    {
        private final Event event;

        public TestScriptedEvent( Event event )
        {
            this.event = event;
        }

        @Nonnull
        @Override
        public DhisResource getDhisResource()
        {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings( "ConstantConditions" )
        @Nonnull
        @Override
        public Program getProgram()
        {
            return null;
        }

        @SuppressWarnings( "ConstantConditions" )
        @Nonnull
        @Override
        public ProgramStage getProgramStage()
        {
            return null;
        }

        @Nullable
        @Override
        public EventStatus getStatus()
        {
            return null;
        }

        @Nullable
        @Override
        public ZonedDateTime getEventDate()
        {
            return event.getEventDate();
        }

        @Nullable
        @Override
        public ZonedDateTime getDueDate()
        {
            return null;
        }

        @Nullable
        @Override
        public Location getCoordinate()
        {
            return null;
        }

        @Nullable
        @Override
        public String getEnrollmentId()
        {
            return null;
        }

        @Nullable
        @Override
        public Object getValue( @Nonnull Reference dataElementReference )
        {
            return null;
        }

        @Nullable
        @Override
        public Boolean getBooleanValue( @Nonnull Reference dataElementReference )
        {
            return null;
        }

        @Nullable
        @Override
        public Integer getIntegerValue( @Nonnull Reference dataElementReference )
        {
            return null;
        }

        @Nullable
        @Override
        public String getStringValue( @Nonnull Reference dataElementReference )
        {
            return null;
        }

        @Nullable
        @Override
        public BigDecimal getBigDecimalValue( @Nonnull Reference dataElementReference )
        {
            return null;
        }

        @Nullable
        @Override
        public Integer getIntegerOptionValue( @Nonnull Reference dataElementReference, int valueBase, @Nullable Pattern optionValuePattern )
        {
            return null;
        }

        @Nullable
        @Override
        public ZonedDateTime getDateTimeValue( @Nonnull Reference dataElementReference )
        {
            return null;
        }

        @Nullable
        @Override
        public LocalDate getDateValue( @Nonnull Reference dataElementReference )
        {
            return null;
        }

        @Override
        public boolean isProvidedElsewhere( @Nonnull Reference attributeReference )
        {
            return false;
        }

        @Nullable
        @Override
        public String getId()
        {
            return null;
        }

        @SuppressWarnings( "ConstantConditions" )
        @Nonnull
        @Override
        public DhisResourceType getResourceType()
        {
            return null;
        }

        @Nullable
        @Override
        public DhisResourceId getResourceId()
        {
            return null;
        }

        @Override
        public boolean isNewResource()
        {
            return false;
        }

        @Override
        public boolean isDeleted()
        {
            return false;
        }

        @Override
        public boolean isLocal()
        {
            return false;
        }

        @Nullable
        @Override
        public ZonedDateTime getLastUpdated()
        {
            return null;
        }

        @Nullable
        @Override
        public String getOrganizationUnitId()
        {
            return null;
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
}