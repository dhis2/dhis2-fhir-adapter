package org.dhis2.fhir.adapter.fhir.metadata.model;

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

import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;

import javax.annotation.Nonnull;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class EventStatusUpdate implements Serializable
{
    private static final long serialVersionUID = 9142123598988442688L;

    private boolean overdueToActive;
    private boolean scheduleToActive;
    private boolean completedToActive;

    @Basic
    @Column( name = "overdue_to_active_update", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isOverdueToActive()
    {
        return overdueToActive;
    }

    public void setOverdueToActive( boolean overdueToActive )
    {
        this.overdueToActive = overdueToActive;
    }

    @Basic
    @Column( name = "schedule_to_active_update", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isScheduleToActive()
    {
        return scheduleToActive;
    }

    public void setScheduleToActive( boolean scheduleToActive )
    {
        this.scheduleToActive = scheduleToActive;
    }

    @Basic
    @Column( name = "completed_to_active_update", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isCompletedToActive()
    {
        return completedToActive;
    }

    public void setCompletedToActive( boolean completedToActive )
    {
        this.completedToActive = completedToActive;
    }

    public void update( @Nonnull Event event )
    {
        if ( event.getStatus() != null )
        {
            EventStatus resulting = null;
            switch ( event.getStatus() )
            {
                case OVERDUE:
                    if ( isOverdueToActive() )
                    {
                        resulting = EventStatus.ACTIVE;
                    }
                    break;
                case SCHEDULE:
                    if ( isScheduleToActive() )
                    {
                        resulting = EventStatus.ACTIVE;
                    }
                    break;
                case COMPLETED:
                    if ( isCompletedToActive() )
                    {
                        resulting = EventStatus.ACTIVE;
                    }
                    break;
            }
            if ( resulting != null )
            {
                if ( event.getStatus() != resulting )
                {
                    event.setStatus( resulting );
                    event.setModified();
                }
            }
        }
    }
}
