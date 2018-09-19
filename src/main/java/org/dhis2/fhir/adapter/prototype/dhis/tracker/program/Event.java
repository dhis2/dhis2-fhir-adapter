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
import org.dhis2.fhir.adapter.prototype.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.prototype.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.prototype.dhis.model.WritableDataValue;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Event implements DhisResource, Serializable, Comparable<Event>
{
    private static final long serialVersionUID = 4966183580394235575L;

    @JsonIgnore
    private boolean newResource;

    @JsonProperty( "event" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String id;

    @JsonProperty( "orgUnit" )
    private String orgUnitId;

    @JsonProperty( "program" )
    private String programId;

    @JsonProperty( "enrollment" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String enrollmentId;

    @JsonIgnore
    private Enrollment enrollment;

    @JsonProperty( "trackedEntityInstance" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String trackedEntityInstanceId;

    @JsonProperty( "programStage" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String programStageId;

    private EventStatus status;

    private ZonedDateTime eventDate;

    private ZonedDateTime dueDate;

    private List<WritableDataValue> dataValues;

    @JsonIgnore
    private boolean modified;

    public Event()
    {
        super();
    }

    public Event( boolean newResource )
    {
        this.newResource = newResource;
        this.modified = newResource;
        this.dataValues = new ArrayList<>();
    }

    @JsonIgnore
    @Nonnull
    @Override
    public DhisResourceType getResourceType()
    {
        return DhisResourceType.EVENT;
    }

    @Override
    public boolean isNewResource()
    {
        return newResource;
    }

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

    public Enrollment getEnrollment()
    {
        return enrollment;
    }

    public void setEnrollment( Enrollment enrollment )
    {
        this.enrollment = enrollment;
        setEnrollmentId( (enrollment == null) ? null : enrollment.getId() );
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

    public ZonedDateTime getEventDate()
    {
        return eventDate;
    }

    public void setEventDate( ZonedDateTime eventDate )
    {
        this.eventDate = eventDate;
    }

    public ZonedDateTime getDueDate()
    {
        return dueDate;
    }

    public void setDueDate( ZonedDateTime dueDate )
    {
        this.dueDate = dueDate;
    }

    public List<? extends DataValue> getDataValues()
    {
        return dataValues;
    }

    public void setDataValues( List<WritableDataValue> dataValues )
    {
        this.dataValues = dataValues;
    }

    public boolean isModified()
    {
        return modified;
    }

    public void setModified( boolean modified )
    {
        this.modified = modified;
    }

    @Nonnull
    public WritableDataValue getDataValue( @Nonnull String dataElementId )
    {
        if ( dataValues == null )
        {
            dataValues = new ArrayList<>();
        }
        WritableDataValue dataValue = dataValues.stream().filter(
            dv -> Objects.equals( dataElementId, dv.getDataElementId() ) ).findFirst().orElse( null );
        if ( dataValue == null )
        {
            dataValue = new WritableDataValue( dataElementId, true );
            dataValues.add( dataValue );
        }
        return dataValue;
    }


    @Override
    public int compareTo( @Nonnull Event o )
    {
        int v = getStatus().ordinal() - o.getStatus().ordinal();
        if ( v != 0 )
        {
            return v;
        }
        return o.getEventDate().compareTo( getEventDate() );
    }
}
