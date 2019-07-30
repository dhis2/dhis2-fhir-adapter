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
import org.dhis2.fhir.adapter.dhis.model.AbstractDhisMetadata;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.model.Id;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class WritableProgram extends AbstractDhisMetadata implements Program, Serializable
{
    private static final long serialVersionUID = -4906529875383953995L;

    private ZonedDateTime lastUpdated;

    @JsonInclude( JsonInclude.Include.NON_NULL )
    private String id;

    private String name;

    private String code;

    private String description;

    private boolean selectIncidentDatesInFuture;

    private boolean selectEnrollmentDatesInFuture;

    private boolean displayIncidentDate;

    private boolean registration;

    private boolean withoutRegistration;

    private boolean captureCoordinates;

    @JsonProperty
    private Id trackedEntityType;

    @JsonProperty( "programTrackedEntityAttributes" )
    private List<WritableProgramTrackedEntityAttribute> trackedEntityAttributes;

    @JsonProperty( "programStages" )
    private List<WritableProgramStage> stages;

    @JsonIgnore
    private transient volatile Map<String, WritableProgramStage> stagesByName;

    @JsonIgnore
    private transient volatile Map<String, WritableProgramStage> stagesByCode;

    @JsonIgnore
    private transient volatile Map<String, WritableProgramStage> stagesById;

    @Override
    public String getId()
    {
        return id;
    }

    public void setId( String id )
    {
        this.id = id;
    }

    @Override
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Override
    public String getCode()
    {
        return code;
    }

    public void setCode( String code )
    {
        this.code = code;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @JsonIgnore
    @Override
    public String getTrackedEntityTypeId()
    {
        return trackedEntityType == null ? null : trackedEntityType.getId();
    }

    public void setTrackedEntityTypeId( String trackedEntityTypeId )
    {
        this.trackedEntityType = trackedEntityTypeId == null ? null : new Id( trackedEntityTypeId );
    }

    @Override
    public boolean isSelectIncidentDatesInFuture()
    {
        return selectIncidentDatesInFuture;
    }

    public void setSelectIncidentDatesInFuture( boolean selectIncidentDatesInFuture )
    {
        this.selectIncidentDatesInFuture = selectIncidentDatesInFuture;
    }

    @Override
    public boolean isSelectEnrollmentDatesInFuture()
    {
        return selectEnrollmentDatesInFuture;
    }

    public void setSelectEnrollmentDatesInFuture( boolean selectEnrollmentDatesInFuture )
    {
        this.selectEnrollmentDatesInFuture = selectEnrollmentDatesInFuture;
    }

    @Override
    public boolean isDisplayIncidentDate()
    {
        return displayIncidentDate;
    }

    public void setDisplayIncidentDate( boolean displayIncidentDate )
    {
        this.displayIncidentDate = displayIncidentDate;
    }

    @Override
    public boolean isRegistration()
    {
        return registration;
    }

    public void setRegistration( boolean registration )
    {
        this.registration = registration;
    }

    @Override
    public boolean isWithoutRegistration()
    {
        return withoutRegistration;
    }

    public void setWithoutRegistration( boolean withoutRegistration )
    {
        this.withoutRegistration = withoutRegistration;
    }

    @Override
    public boolean isCaptureCoordinates()
    {
        return captureCoordinates;
    }

    public void setCaptureCoordinates( boolean captureCoordinates )
    {
        this.captureCoordinates = captureCoordinates;
    }

    @Override
    public List<WritableProgramTrackedEntityAttribute> getTrackedEntityAttributes()
    {
        return trackedEntityAttributes;
    }

    public void setTrackedEntityAttributes( List<WritableProgramTrackedEntityAttribute> trackedEntityAttributes )
    {
        this.trackedEntityAttributes = trackedEntityAttributes;
    }

    @Override
    public List<WritableProgramStage> getStages()
    {
        return stages;
    }

    public void setStages( List<WritableProgramStage> stages )
    {
        this.stages = stages;
    }

    @Nonnull
    public Optional<WritableProgramStage> getOptionalStage( @Nonnull Reference reference )
    {
        switch ( reference.getType() )
        {
            case NAME:
                return Optional.ofNullable( getStageByName( reference.getValue() ) );
            case ID:
                return Optional.ofNullable( getStageById( reference.getValue() ) );
            case CODE:
                // program stages do not have a code
                return Optional.empty();
            default:
                throw new AssertionError( "Unhandled reference type: " + reference.getType() );
        }
    }

    @Nullable
    public WritableProgramStage getStageByName( @Nonnull String name )
    {
        Map<String, WritableProgramStage> stagesByName = this.stagesByName;
        if ( stagesByName == null )
        {
            this.stagesByName = stagesByName = stages.stream().collect( Collectors.toMap( WritableProgramStage::getName, s -> s ) );
        }
        return stagesByName.get( name );
    }

    @Nullable
    public WritableProgramStage getStageById( @Nonnull String id )
    {
        Map<String, WritableProgramStage> stagesById = this.stagesById;
        if ( stagesById == null )
        {
            this.stagesById = stagesById = stages.stream().collect( Collectors.toMap( WritableProgramStage::getId, s -> s ) );
        }
        return stagesById.get( id );
    }

    @Nonnull
    @Override
    public DhisResourceType getResourceType()
    {
        return DhisResourceType.PROGRAM_METADATA;
    }

    @Override
    public DhisResourceId getResourceId()
    {
        return new DhisResourceId( DhisResourceType.PROGRAM_METADATA, getId() );
    }

    @Override
    public boolean isDeleted()
    {
        return false;
    }

    @Override
    public ZonedDateTime getLastUpdated()
    {
        return lastUpdated;
    }

    @Override
    public boolean isLocal()
    {
        return false;
    }

    @Override
    public boolean isNewResource()
    {
        return false;
    }

    @Override
    public void resetNewResource()
    {
        // nothing to be done, read-only
    }

    @Override
    public String getOrgUnitId()
    {
        return null;
    }
}
