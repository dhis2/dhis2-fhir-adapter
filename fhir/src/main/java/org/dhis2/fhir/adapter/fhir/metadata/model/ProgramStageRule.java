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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * AbstractRule for program stage that defines also if and how to enroll into a program.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_program_stage_rule" )
@DiscriminatorValue( "PROGRAM_STAGE_EVENT" )
public class ProgramStageRule extends AbstractRule
{
    private static final long serialVersionUID = 3376410603952222321L;

    private boolean updateEventDate;
    private MappedTrackerProgramStage programStage;
    private EventPeriodDayType beforePeriodDayType;
    private Integer beforePeriodDays;
    private EventPeriodDayType afterPeriodDayType;
    private Integer afterPeriodDays;
    private ApplicableEnrollmentStatus applicableEnrollmentStatus;
    private ApplicableEventStatus applicableEventStatus;
    private EventStatusUpdate eventStatusUpdate;

    @Basic
    @Column( name = "update_event_date", nullable = false )
    public boolean isUpdateEventDate()
    {
        return updateEventDate;
    }

    public void setUpdateEventDate( boolean updateEventDate )
    {
        this.updateEventDate = updateEventDate;
    }

    @ManyToOne( optional = false )
    @JoinColumn( name = "program_stage_id", referencedColumnName = "id", nullable = false )
    public MappedTrackerProgramStage getProgramStage()
    {
        return programStage;
    }

    public void setProgramStage( MappedTrackerProgramStage programStage )
    {
        this.programStage = programStage;
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
    public Integer getBeforePeriodDays()
    {
        return beforePeriodDays;
    }

    public void setBeforePeriodDays( Integer beforePeriodDays )
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
    @Column( name = "after_period_days" )
    public Integer getAfterPeriodDays()
    {
        return afterPeriodDays;
    }

    public void setAfterPeriodDays( Integer afterPeriodDays )
    {
        this.afterPeriodDays = afterPeriodDays;
    }

    @Embedded
    public ApplicableEnrollmentStatus getApplicableEnrollmentStatus()
    {
        return applicableEnrollmentStatus;
    }

    public void setApplicableEnrollmentStatus( ApplicableEnrollmentStatus applicableEnrollmentStatus )
    {
        this.applicableEnrollmentStatus = applicableEnrollmentStatus;
    }

    @Embedded
    public ApplicableEventStatus getApplicableEventStatus()
    {
        return applicableEventStatus;
    }

    public void setApplicableEventStatus( ApplicableEventStatus applicableEventStatus )
    {
        this.applicableEventStatus = applicableEventStatus;
    }

    @Embedded
    public EventStatusUpdate getEventStatusUpdate()
    {
        return eventStatusUpdate;
    }

    public void setEventStatusUpdate( EventStatusUpdate eventStatusUpdate )
    {
        this.eventStatusUpdate = eventStatusUpdate;
    }

    @JsonIgnore
    @Transient
    public EventPeriodDayType getResultingBeforePeriodDayType()
    {
        return (getBeforePeriodDayType() == null) ? getProgramStage().getBeforePeriodDayType() : getBeforePeriodDayType();
    }

    @JsonIgnore
    @Transient
    public int getResultingBeforePeriodDays()
    {
        return (getBeforePeriodDays() == null) ? getProgramStage().getBeforePeriodDays() : getBeforePeriodDays();
    }

    @JsonIgnore
    @Transient
    public EventPeriodDayType getResultingAfterPeriodDayType()
    {
        return (getAfterPeriodDayType() == null) ? getProgramStage().getAfterPeriodDayType() : getAfterPeriodDayType();
    }

    @JsonIgnore
    @Transient
    public int getResultingAfterPeriodDays()
    {
        return (getAfterPeriodDays() == null) ? getProgramStage().getAfterPeriodDays() : getAfterPeriodDays();
    }
}
