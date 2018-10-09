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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

    private Reference programStageReference;
    private MappedTrackerProgram program;
    private boolean enrollmentEnabled;
    private MappedEnrollment enrollment;
    private boolean creationEnabled;
    private ExecutableScript creationApplicableScript;
    private ExecutableScript creationScript;
    private ExecutableScript finalScript;

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

    @ManyToOne
    @JoinColumn( name = "enrollment_id", referencedColumnName = "id" )
    public MappedEnrollment getEnrollment()
    {
        return enrollment;
    }

    public void setEnrollment( MappedEnrollment enrollment )
    {
        this.enrollment = enrollment;
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

    @ManyToOne
    @JoinColumn( name = "final_script_id", referencedColumnName = "id" )
    public ExecutableScript getFinalScript()
    {
        return finalScript;
    }

    public void setFinalScript( ExecutableScript finalScript )
    {
        this.finalScript = finalScript;
    }

    @Basic
    @Column( name = "enrollment_enabled" )
    public boolean isEnrollmentEnabled()
    {
        return enrollmentEnabled;
    }

    public void setEnrollmentEnabled( boolean enrollmentEnabled )
    {
        this.enrollmentEnabled = enrollmentEnabled;
    }

    @Basic
    @Column( name = "creation_enabled" )
    public boolean isCreationEnabled()
    {
        return creationEnabled;
    }

    public void setCreationEnabled( boolean creationEnabled )
    {
        this.creationEnabled = creationEnabled;
    }
}
