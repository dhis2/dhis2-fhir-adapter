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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.jackson.AdapterBeanPropertyFilter;
import org.dhis2.fhir.adapter.jackson.JsonCacheId;
import org.springframework.hateoas.core.Relation;

import javax.annotation.Nonnull;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Objects;

/**
 * AbstractRule for program stage that defines also if and how to enroll into a program.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_program_stage_rule" )
@DiscriminatorValue( "PROGRAM_STAGE_EVENT" )
@NamedQueries( {
    @NamedQuery( name = ProgramStageRule.FIND_ALL_EXP_NAMED_QUERY,
        query = "SELECT psr FROM ProgramStageRule psr " +
            "JOIN psr.programStage ps ON (ps.enabled=true AND ps.expEnabled=true AND (ps.fhirCreateEnabled=true OR ps.fhirUpdateEnabled=true) AND ps.programStageReference IN (:programStageReferences)) " +
            "JOIN ps.program p ON (p.enabled=true AND p.expEnabled=true AND (p.fhirCreateEnabled=true OR p.fhirUpdateEnabled=true) AND p.programReference IN (:programReferences)) WHERE " +
            "psr.enabled=true AND psr.expEnabled=true AND (psr.fhirCreateEnabled=true OR psr.fhirUpdateEnabled=true)" ),
    @NamedQuery( name = ProgramStageRule.FIND_ALL_EXP_WILDCARD_NAMED_QUERY,
        query = "SELECT psr FROM ProgramStageRule psr WHERE psr.programStage IS NULL AND " +
            "psr.enabled=true AND psr.expEnabled=true AND (psr.fhirCreateEnabled=true OR psr.fhirUpdateEnabled=true)" ),
    @NamedQuery( name = ProgramStageRule.FIND_ALL_EXP_BY_DATA_REF_NAMED_QUERY,
        query = "SELECT psr FROM ProgramStageRule psr " +
            "JOIN psr.programStage ps ON (ps.enabled=true AND ps.expEnabled=true AND (ps.fhirCreateEnabled=true OR ps.fhirUpdateEnabled=true) AND ps.programStageReference IN (:programStageReferences)) " +
            "JOIN ps.program p ON (p.enabled=true AND p.expEnabled=true AND (p.fhirCreateEnabled=true OR p.fhirUpdateEnabled=true) AND p.programReference IN (:programReferences)) WHERE " +
            "psr.enabled=true AND psr.expEnabled=true AND (psr.fhirCreateEnabled=true OR psr.fhirUpdateEnabled=true) AND " +
            "EXISTS (SELECT 1 FROM RuleDhisDataReference edr WHERE edr.rule=psr AND edr.dataReference IN (:dataReferences))" )
} )
@Relation( value = "rule", collectionRelation = "rules" )
@JsonFilter( value = AdapterBeanPropertyFilter.FILTER_NAME )
public class ProgramStageRule extends AbstractRule
{
    private static final long serialVersionUID = 3376410603952222321L;

    public static final String FIND_ALL_EXP_NAMED_QUERY = "ProgramStageRule.findAllExportedWithoutDataRef";

    public static final String FIND_ALL_EXP_WILDCARD_NAMED_QUERY = "ProgramStageRule.findAllExportedWithoutProgramStageAndDataRef";

    public static final String FIND_ALL_EXP_BY_DATA_REF_NAMED_QUERY = "ProgramStageRule.findAllExportedByDataRef";

    private MappedTrackerProgramStage programStage;

    private boolean enrollmentCreationEnabled;

    private boolean eventCreationEnabled;

    private boolean updateEventDate;

    private EventPeriodDayType beforePeriodDayType;

    private Integer beforePeriodDays;

    private EventPeriodDayType afterPeriodDayType;

    private Integer afterPeriodDays;

    private ApplicableEnrollmentStatus applicableEnrollmentStatus;

    private ApplicableEventStatus applicableEventStatus;

    private EventStatusUpdate eventStatusUpdate;

    private ExecutableScript expDeleteEvaluateScript;

    private boolean expEnabled;

    private boolean fhirCreateEnabled = true;

    private boolean fhirUpdateEnabled;

    public ProgramStageRule()
    {
        super( DhisResourceType.PROGRAM_STAGE_EVENT );
    }

    @Basic
    @Column( name = "enrollment_creation_enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isEnrollmentCreationEnabled()
    {
        return enrollmentCreationEnabled;
    }

    public void setEnrollmentCreationEnabled( boolean enrollmentCreationEnabled )
    {
        this.enrollmentCreationEnabled = enrollmentCreationEnabled;
    }

    @Basic
    @Column( name = "event_creation_enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isEventCreationEnabled()
    {
        return eventCreationEnabled;
    }

    public void setEventCreationEnabled( boolean eventCreationEnabled )
    {
        this.eventCreationEnabled = eventCreationEnabled;
    }

    @Basic
    @Column( name = "update_event_date", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE NOT NULL" )
    public boolean isUpdateEventDate()
    {
        return updateEventDate;
    }

    public void setUpdateEventDate( boolean updateEventDate )
    {
        this.updateEventDate = updateEventDate;
    }

    @ManyToOne
    @JoinColumn( name = "program_stage_id", referencedColumnName = "id" )
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
    @Column( name = "before_period_days", nullable = false, columnDefinition = "INTEGER DEFAULT 0 NOT NULL" )
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
    @Column( name = "after_period_days", columnDefinition = "INTEGER DEFAULT 0 NOT NULL" )
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

    @JsonCacheId
    @ManyToOne
    @JoinColumn( name = "exp_delete_evaluate_script_id", referencedColumnName = "id" )
    public ExecutableScript getExpDeleteEvaluateScript()
    {
        return expDeleteEvaluateScript;
    }

    public void setExpDeleteEvaluateScript( ExecutableScript expDeleteEvaluateScript )
    {
        this.expDeleteEvaluateScript = expDeleteEvaluateScript;
    }

    @JsonIgnore
    @Transient
    public EventPeriodDayType getResultingBeforePeriodDayType()
    {
        return getBeforePeriodDayType() == null && getProgramStage() != null ? getProgramStage().getBeforePeriodDayType() : getBeforePeriodDayType();
    }

    @JsonIgnore
    @Transient
    public int getResultingBeforePeriodDays()
    {
        return getBeforePeriodDays() == null && getProgramStage() != null ? getProgramStage().getBeforePeriodDays() : getBeforePeriodDays();
    }

    @JsonIgnore
    @Transient
    public EventPeriodDayType getResultingAfterPeriodDayType()
    {
        return getAfterPeriodDayType() == null && getProgramStage() != null ? getProgramStage().getAfterPeriodDayType() : getAfterPeriodDayType();
    }

    @JsonIgnore
    @Transient
    public int getResultingAfterPeriodDays()
    {
        return getAfterPeriodDays() == null && getProgramStage() != null ? getProgramStage().getAfterPeriodDays() : getAfterPeriodDays();
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirCreateEnable()
    {
        return isExpEnabled() && isFhirCreateEnabled() && ( getProgramStage() == null || ( getProgramStage().isExpEnabled() && getProgramStage().isEffectiveFhirCreateEnabled() ) );
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirUpdateEnable()
    {
        return isExpEnabled() && isFhirUpdateEnabled() && ( getProgramStage() == null || ( getProgramStage().isExpEnabled() && getProgramStage().isEffectiveFhirUpdateEnabled() ) );
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirDeleteEnable()
    {
        return isExpEnabled() && isFhirDeleteEnabled() && ( getProgramStage() == null || ( getProgramStage().isExpEnabled() && getProgramStage().isEffectiveFhirDeleteEnabled() ) );
    }

    @Override
    public boolean coversExecutedRule( @Nonnull AbstractRule executedRule )
    {
        return executedRule instanceof ProgramStageRule && Objects.equals(
            ( (ProgramStageRule) executedRule ).getProgramStage() == null ? null : ( (ProgramStageRule) executedRule ).getProgramStage().getId(),
            getProgramStage() == null ? null : getProgramStage().getId() );
    }
}
