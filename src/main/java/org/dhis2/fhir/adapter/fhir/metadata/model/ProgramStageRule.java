package org.dhis2.fhir.adapter.fhir.metadata.model;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Rule for program stage that defines also if and how to enroll into a program.
 *
 * @author volsch
 */
@Entity
@Table( name = "fhir_program_stage_rule" )
@DiscriminatorValue( "ENROLLMENT" )
public class ProgramStageRule extends Rule
{
    private static final long serialVersionUID = 3376410603952222321L;

    private String programStageReference;
    private MappedProgram program;
    private MappedEnrollment enrollment;
    private ExecutableScript creationApplicableScript;
    private ExecutableScript creationTransformScript;
    private ExecutableScript finalScript;

    @Basic @Column( name = "program_stage_ref", nullable = false, length = 230 ) public String getProgramStageReference()
    {
        return programStageReference;
    }

    public void setProgramStageReference( String programStageReference )
    {
        this.programStageReference = programStageReference;
    }

    @ManyToOne @JoinColumn( name = "program_id", referencedColumnName = "id", nullable = false ) public MappedProgram getProgram()
    {
        return program;
    }

    public void setProgram( MappedProgram program )
    {
        this.program = program;
    }

    @ManyToOne @JoinColumn( name = "enrollment_id", referencedColumnName = "id" ) public MappedEnrollment getEnrollment()
    {
        return enrollment;
    }

    public void setEnrollment( MappedEnrollment enrollment )
    {
        this.enrollment = enrollment;
    }

    @ManyToOne @JoinColumn( name = "creation_applicable_script_id", referencedColumnName = "id" ) public ExecutableScript getCreationApplicableScript()
    {
        return creationApplicableScript;
    }

    public void setCreationApplicableScript( ExecutableScript creationApplicableScript )
    {
        this.creationApplicableScript = creationApplicableScript;
    }

    @ManyToOne @JoinColumn( name = "creation_transform_script_id", referencedColumnName = "id" ) public ExecutableScript getCreationTransformScript()
    {
        return creationTransformScript;
    }

    public void setCreationTransformScript( ExecutableScript creationTransformScript )
    {
        this.creationTransformScript = creationTransformScript;
    }

    @ManyToOne @JoinColumn( name = "final_script_id", referencedColumnName = "id" ) public ExecutableScript getFinalScript()
    {
        return finalScript;
    }

    public void setFinalScript( ExecutableScript finalScript )
    {
        this.finalScript = finalScript;
    }
}
