package org.dhis2.fhir.adapter.prototype.fhir.transform.scripted.program;

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

import org.dhis2.fhir.adapter.prototype.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.prototype.fhir.transform.scripted.trackedentity.ScriptedTrackedEntityInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

class EventInfo
{
    private final Program program;

    private final ProgramStage programStage;

    private final TrackedEntityType trackedEntityType;

    private final ScriptedTrackedEntityInstance trackedEntityInstance;

    private final Enrollment enrollment;

    private final Event event;

    private final boolean completedEvents;

    public EventInfo( @Nonnull Program program, @Nonnull ProgramStage programStage,
        @Nonnull TrackedEntityType trackedEntityType, @Nonnull ScriptedTrackedEntityInstance trackedEntityInstance,
        @Nullable Enrollment enrollment, @Nullable Event event, boolean completedEvents )
    {
        this.program = program;
        this.programStage = programStage;
        this.trackedEntityType = trackedEntityType;
        this.trackedEntityInstance = trackedEntityInstance;
        this.enrollment = enrollment;
        this.event = event;
        this.completedEvents = completedEvents;
    }

    public @Nonnull Program getProgram()
    {
        return program;
    }

    public @Nonnull ProgramStage getProgramStage()
    {
        return programStage;
    }

    public @Nonnull TrackedEntityType getTrackedEntityType()
    {
        return trackedEntityType;
    }

    public @Nonnull ScriptedTrackedEntityInstance getTrackedEntityInstance()
    {
        return trackedEntityInstance;
    }

    public @Nonnull Optional<Enrollment> getEnrollment()
    {
        return Optional.ofNullable( enrollment );
    }

    public @Nonnull Optional<Event> getEvent()
    {
        return Optional.ofNullable( event );
    }

    public boolean isCompletedEvents()
    {
        return completedEvents;
    }
}
