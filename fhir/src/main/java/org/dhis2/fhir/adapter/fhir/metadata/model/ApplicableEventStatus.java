package org.dhis2.fhir.adapter.fhir.metadata.model;

/*
 * Copyright (c) 2004-2018, University of Oslo
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;

import javax.annotation.Nullable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;

@Embeddable
public class ApplicableEventStatus implements Serializable
{
    private static final long serialVersionUID = -753167424963912832L;

    private boolean overdue;
    private boolean active;
    private boolean schedule;
    private boolean visited;
    private boolean completed;
    private boolean skipped;

    @Basic
    @Column( name = "overdue_applicable", nullable = false )
    public boolean isOverdue()
    {
        return overdue;
    }

    public void setOverdue( boolean overdue )
    {
        this.overdue = overdue;
    }

    @Basic
    @Column( name = "active_applicable", nullable = false )
    public boolean isActive()
    {
        return active;
    }

    public void setActive( boolean active )
    {
        this.active = active;
    }

    @Basic
    @Column( name = "schedule_applicable", nullable = false )
    public boolean isSchedule()
    {
        return schedule;
    }

    public void setSchedule( boolean schedule )
    {
        this.schedule = schedule;
    }

    @Basic
    @Column( name = "visited_applicable", nullable = false )
    public boolean isVisited()
    {
        return visited;
    }

    public void setVisited( boolean visited )
    {
        this.visited = visited;
    }

    @Basic
    @Column( name = "completed_applicable", nullable = false )
    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted( boolean completed )
    {
        this.completed = completed;
    }

    @Basic
    @Column( name = "skipped_applicable", nullable = false )
    public boolean isSkipped()
    {
        return skipped;
    }

    public void setSkipped( boolean skipped )
    {
        this.skipped = skipped;
    }

    @JsonIgnore
    @Transient
    public boolean isApplicable( @Nullable EventStatus status )
    {
        if ( status == null )
        {
            return false;
        }
        switch ( status )
        {
            case ACTIVE:
                return isActive();
            case COMPLETED:
                return isCompleted();
            case OVERDUE:
                return isOverdue();
            case SCHEDULE:
                return isSchedule();
            case SKIPPED:
                return isSkipped();
            case VISITED:
                return isVisited();
            default:
                throw new AssertionError( "Unhandled event status: " + status );
        }
    }
}
