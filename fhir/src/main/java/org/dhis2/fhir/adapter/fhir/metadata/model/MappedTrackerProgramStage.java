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

import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceAttributeConverter;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table( name = "fhir_tracker_program_stage" )
public class MappedTrackerProgramStage implements Serializable
{
    private static final long serialVersionUID = 7561285892767275117L;

    private UUID id;
    private Long version;
    private Timestamp createdAt;
    private String lastUpdatedBy;
    private Timestamp lastUpdatedAt;
    private String name;
    private String description;
    private Reference programStageReference;
    private boolean enabled;
    private MappedTrackerProgram program;
    private boolean creationEnabled;
    private ExecutableScript creationApplicableScript;
    private ExecutableScript creationScript;
    private EventStatus creationStatus;
    private boolean eventDateIsIncident;
    private EventPeriodDayType beforePeriodDayType;
    private int beforePeriodDays;
    private EventPeriodDayType afterPeriodDayType;
    private int afterPeriodDays;

    @GeneratedValue( generator = "uuid2" )
    @GenericGenerator( name = "uuid2", strategy = "uuid2" )
    @Id
    @Column( name = "id", nullable = false )
    public UUID getId()
    {
        return id;
    }

    public void setId( UUID id )
    {
        this.id = id;
    }

    @Column( name = "version", nullable = false )
    @Version
    public Long getVersion()
    {
        return version;
    }

    public void setVersion( Long version )
    {
        this.version = version;
    }

    @Basic
    @Column( name = "created_at", nullable = false )
    public Timestamp getCreatedAt()
    {
        return createdAt;
    }

    public void setCreatedAt( Timestamp createdAt )
    {
        this.createdAt = createdAt;
    }

    @Basic
    @Column( name = "last_updated_by", length = 11 )
    public String getLastUpdatedBy()
    {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy( String lastUpdatedBy )
    {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Basic
    @Column( name = "last_updated_at", nullable = false )
    public Timestamp getLastUpdatedAt()
    {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt( Timestamp lastUpdatedAt )
    {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @Basic
    @Column( name = "name", nullable = false, length = 230 )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @Basic
    @Column( name = "description", length = -1 )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    @Basic
    @Column( name = "program_stage_ref", nullable = false, length = 230 )
    @Convert( converter = ReferenceAttributeConverter.class )
    public Reference getProgramStageReference()
    {
        return programStageReference;
    }

    public void setProgramStageReference( Reference programStageReference )
    {
        this.programStageReference = programStageReference;
    }

    @Basic
    @Column( name = "enabled", nullable = false )
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "program_id", referencedColumnName = "id", nullable = false )
    public MappedTrackerProgram getProgram()
    {
        return program;
    }

    public void setProgram( MappedTrackerProgram program )
    {
        this.program = program;
    }

    @Basic
    @Column( name = "creation_enabled", nullable = false )
    public boolean isCreationEnabled()
    {
        return creationEnabled;
    }

    public void setCreationEnabled( boolean creationEnabled )
    {
        this.creationEnabled = creationEnabled;
    }

    @ManyToOne
    @JoinColumn( name = "creation_applicable_script_id", referencedColumnName = "id" )
    public ExecutableScript getCreationApplicableScript()
    {
        return creationApplicableScript;
    }

    public void setCreationApplicableScript( ExecutableScript creationApplicableScript )
    {
        this.creationApplicableScript = creationApplicableScript;
    }

    @ManyToOne
    @JoinColumn( name = "creation_script_id", referencedColumnName = "id" )
    public ExecutableScript getCreationScript()
    {
        return creationScript;
    }

    public void setCreationScript( ExecutableScript creationScript )
    {
        this.creationScript = creationScript;
    }

    @Column( name = "creation_status" )
    @Enumerated( EnumType.STRING )
    public EventStatus getCreationStatus()
    {
        return creationStatus;
    }

    public void setCreationStatus( EventStatus creationStatus )
    {
        this.creationStatus = creationStatus;
    }

    @Basic
    @Column( name = "event_date_is_incident", nullable = false )
    public boolean isEventDateIsIncident()
    {
        return eventDateIsIncident;
    }

    public void setEventDateIsIncident( boolean eventDateIsIncident )
    {
        this.eventDateIsIncident = eventDateIsIncident;
    }

    @Column( name = "before_period_day_type" )
    @Enumerated( EnumType.STRING )
    public EventPeriodDayType getBeforePeriodDayType()
    {
        return beforePeriodDayType;
    }

    public void setBeforePeriodDayType( EventPeriodDayType beforePeriodDayType )
    {
        this.beforePeriodDayType = beforePeriodDayType;
    }

    @Basic
    @Column( name = "before_period_days", nullable = false )
    public int getBeforePeriodDays()
    {
        return beforePeriodDays;
    }

    public void setBeforePeriodDays( int beforePeriodDays )
    {
        this.beforePeriodDays = beforePeriodDays;
    }

    @Column( name = "after_period_day_type" )
    @Enumerated( EnumType.STRING )
    public EventPeriodDayType getAfterPeriodDayType()
    {
        return afterPeriodDayType;
    }

    public void setAfterPeriodDayType( EventPeriodDayType afterPeriodDayType )
    {
        this.afterPeriodDayType = afterPeriodDayType;
    }

    @Basic
    @Column( name = "after_period_days", nullable = false )
    public int getAfterPeriodDays()
    {
        return afterPeriodDays;
    }

    public void setAfterPeriodDays( int afterPeriodDays )
    {
        this.afterPeriodDays = afterPeriodDays;
    }
}
