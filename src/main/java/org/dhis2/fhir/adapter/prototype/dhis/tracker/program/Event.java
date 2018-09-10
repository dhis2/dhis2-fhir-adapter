package org.dhis2.fhir.adapter.prototype.dhis.tracker.program;

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
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhis2.fhir.adapter.prototype.dhis.model.DataValue;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class Event implements Serializable
{
    private static final long serialVersionUID = 4966183580394235575L;

    @JsonIgnore
    private String id;

    @JsonProperty( "orgUnit" )
    private String orgUnitId;

    @JsonProperty( "program" )
    private String programId;

    @JsonProperty( "enrollment" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String enrollmentId;

    @JsonProperty( "trackedEntityInstance" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String trackedEntityInstanceId;

    @JsonProperty( "programStage" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String programStageId;

    private EventStatus status;

    private LocalDate dueDate;

    private LocalDate eventDate;

    private List<DataValue> dataValues;

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    public String getOrgUnitId()
    {
        return orgUnitId;
    }

    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    public String getProgramId()
    {
        return programId;
    }

    public void setProgramId( String programId )
    {
        this.programId = programId;
    }

    public String getEnrollmentId()
    {
        return enrollmentId;
    }

    public void setEnrollmentId( String enrollmentId )
    {
        this.enrollmentId = enrollmentId;
    }

    public String getTrackedEntityInstanceId()
    {
        return trackedEntityInstanceId;
    }

    public void setTrackedEntityInstanceId( String trackedEntityInstanceId )
    {
        this.trackedEntityInstanceId = trackedEntityInstanceId;
    }

    public String getProgramStageId()
    {
        return programStageId;
    }

    public void setProgramStageId( String programStageId )
    {
        this.programStageId = programStageId;
    }

    public EventStatus getStatus()
    {
        return status;
    }

    public void setStatus( EventStatus status )
    {
        this.status = status;
    }

    public LocalDate getDueDate()
    {
        return dueDate;
    }

    public void setDueDate( LocalDate dueDate )
    {
        this.dueDate = dueDate;
    }

    public LocalDate getEventDate()
    {
        return eventDate;
    }

    public void setEventDate( LocalDate eventDate )
    {
        this.eventDate = eventDate;
    }

    public List<DataValue> getDataValues()
    {
        return dataValues;
    }

    public void setDataValues( List<DataValue> dataValues )
    {
        this.dataValues = dataValues;
    }
}
