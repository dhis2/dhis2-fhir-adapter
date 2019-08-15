package org.dhis2.fhir.adapter.dhis.tracker.program;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.dhis2.fhir.adapter.dhis.model.DataValue;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.TrackedEntityDhisResource;
import org.dhis2.fhir.adapter.dhis.model.WritableDataValue;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.geo.Location;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Contains a DHIS2 Program Stage Instance (aka event).
 *
 * @author volsch
 */
public class Event implements TrackedEntityDhisResource, Serializable, Comparable<Event>
{
    private static final long serialVersionUID = 4966183580394235575L;

    @JsonIgnore
    private boolean newResource;

    @JsonIgnore
    private boolean local;

    @JsonProperty( access = JsonProperty.Access.WRITE_ONLY )
    private boolean deleted;

    @JsonProperty( "event" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String id;

    @JsonProperty( access = JsonProperty.Access.WRITE_ONLY )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private ZonedDateTime lastUpdated;

    @JsonProperty( "orgUnit" )
    private String orgUnitId;

    @JsonProperty( "program" )
    private String programId;

    @JsonProperty( "enrollment" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String enrollmentId;

    @JsonIgnore
    private transient Enrollment enrollment;

    @JsonIgnore
    private transient TrackedEntityInstance trackedEntityInstance;

    @JsonProperty( "trackedEntityInstance" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String trackedEntityInstanceId;

    @JsonProperty( "programStage" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String programStageId;

    private EventStatus status;

    private ZonedDateTime eventDate;

    private ZonedDateTime dueDate;

    private Location coordinate;

    private List<WritableDataValue> dataValues;

    @JsonIgnore
    private boolean modified;

    public Event()
    {
        super();
    }

    public Event( @Nonnull String id )
    {
        this.id = id;
    }

    public Event( boolean newResource )
    {
        this.newResource = newResource;
        this.modified = newResource;
        this.local = newResource;
        this.dataValues = new ArrayList<>();
    }

    @JsonIgnore
    @Override
    public DhisResourceId getResourceId()
    {
        return (getId() == null) ? null : new DhisResourceId( DhisResourceType.PROGRAM_STAGE_EVENT, getId() );
    }

    @JsonIgnore
    @Nonnull
    @Override
    public DhisResourceType getResourceType()
    {
        return DhisResourceType.PROGRAM_STAGE_EVENT;
    }

    @Override
    public boolean isNewResource()
    {
        return newResource;
    }

    @Override
    public void resetNewResource()
    {
        this.newResource = false;
        this.modified = false;

        if ( lastUpdated == null )
        {
            lastUpdated = ZonedDateTime.now();
        }
    }

    @Override
    public boolean isLocal()
    {
        return local;
    }

    public void setLocal( boolean local )
    {
        this.local = local;
    }

    public boolean isDeleted()
    {
        return deleted;
    }

    public void setDeleted( boolean deleted )
    {
        this.deleted = deleted;
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
        if ( enrollment != null )
        {
            setEnrollmentId( enrollment.getId() );
        }
    }

    public TrackedEntityInstance getTrackedEntityInstance()
    {
        return trackedEntityInstance;
    }

    public void setTrackedEntityInstance( TrackedEntityInstance trackedEntityInstance )
    {
        this.trackedEntityInstance = trackedEntityInstance;
        if ( trackedEntityInstance != null )
        {
            this.trackedEntityInstanceId = trackedEntityInstance.getId();
        }
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

    public Location getCoordinate()
    {
        return coordinate;
    }

    public void setCoordinate( Location coordinate )
    {
        this.coordinate = coordinate;
    }

    @Override
    public ZonedDateTime getLastUpdated()
    {
        return lastUpdated;
    }

    public void setLastUpdated( ZonedDateTime lastUpdated )
    {
        this.lastUpdated = lastUpdated;
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

    public void setModified()
    {
        this.modified = true;
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

    @JsonIgnore
    public boolean isAnyDataValueModified()
    {
        return (getDataValues() != null) && getDataValues().stream().anyMatch( DataValue::isModified );
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
