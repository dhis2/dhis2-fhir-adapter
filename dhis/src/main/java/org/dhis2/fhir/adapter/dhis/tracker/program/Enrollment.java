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
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.TrackedEntityDhisResource;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.geo.Location;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains a DHIS2 Program Instance (aka enrollment).
 *
 * @author volsch
 */
public class Enrollment implements TrackedEntityDhisResource, Serializable
{
    private static final long serialVersionUID = 6528591138270821481L;

    @JsonIgnore
    private boolean newResource;

    @JsonIgnore
    private boolean modified;

    @JsonIgnore
    private boolean local;

    @JsonProperty( "enrollment" )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String id;

    @JsonProperty( access = JsonProperty.Access.WRITE_ONLY )
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private ZonedDateTime lastUpdated;

    @JsonProperty( "orgUnit" )
    private String orgUnitId;

    @JsonProperty( "program" )
    private String programId;

    @JsonIgnore
    private TrackedEntityInstance trackedEntityInstance;

    @JsonProperty( "trackedEntityInstance" )
    private String trackedEntityInstanceId;

    @JsonInclude( JsonInclude.Include.NON_NULL )
    private EnrollmentStatus status;

    private ZonedDateTime enrollmentDate;

    private ZonedDateTime incidentDate;

    private Location coordinate;

    @JsonProperty
    @JsonInclude( JsonInclude.Include.NON_NULL )
    private List<Event> events;

    public Enrollment()
    {
        super();
    }

    public Enrollment( @Nonnull String id )
    {
        this.id = id;
    }

    public Enrollment( boolean newResource )
    {
        this.newResource = newResource;
        this.modified = newResource;
        this.local = newResource;
        this.events = new ArrayList<>();
    }

    @JsonIgnore
    @Override
    public DhisResourceId getResourceId()
    {
        return (getId() == null) ? null : new DhisResourceId( DhisResourceType.ENROLLMENT, getId() );
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

    @Nonnull
    @JsonIgnore
    @Override
    public DhisResourceType getResourceType()
    {
        return DhisResourceType.ENROLLMENT;
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

    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    @JsonIgnore
    @Override
    public boolean isDeleted()
    {
        return false;
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

    public TrackedEntityInstance getTrackedEntityInstance()
    {
        return trackedEntityInstance;
    }

    public void setTrackedEntityInstance( TrackedEntityInstance trackedEntityInstance )
    {
        this.trackedEntityInstance = trackedEntityInstance;
        this.trackedEntityInstanceId = (trackedEntityInstance == null) ? null : trackedEntityInstance.getId();
    }

    public String getTrackedEntityInstanceId()
    {
        return trackedEntityInstanceId;
    }

    public void setTrackedEntityInstanceId( String trackedEntityInstanceId )
    {
        this.trackedEntityInstanceId = trackedEntityInstanceId;
    }

    public EnrollmentStatus getStatus()
    {
        return status;
    }

    public void setStatus( EnrollmentStatus status )
    {
        this.status = status;
    }

    public ZonedDateTime getEnrollmentDate()
    {
        return enrollmentDate;
    }

    public void setEnrollmentDate( ZonedDateTime enrollmentDate )
    {
        this.enrollmentDate = enrollmentDate;
    }

    public ZonedDateTime getIncidentDate()
    {
        return incidentDate;
    }

    public void setIncidentDate( ZonedDateTime incidentDate )
    {
        this.incidentDate = incidentDate;
    }

    public Location getCoordinate()
    {
        return coordinate;
    }

    public void setCoordinate( Location coordinate )
    {
        this.coordinate = coordinate;
    }

    public List<Event> getEvents()
    {
        return events;
    }

    public void setEvents( List<Event> events )
    {
        this.events = events;
    }

    public boolean isModified()
    {
        return modified;
    }

    public void setModified()
    {
        this.modified = true;
    }

    public void resetModified()
    {
        this.modified = false;
    }
}
