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

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Contains the ID of the program stage and its program.
 *
 * @author volsch
 */
public class ProgramStageId implements Comparable<ProgramStageId>, Serializable
{
    private static final long serialVersionUID = 6571859445969896545L;

    private final String programId;

    private final String programStageId;

    public ProgramStageId( @Nonnull String programId, @Nonnull String programStageId )
    {
        this.programId = programId;
        this.programStageId = programStageId;
    }

    @Nonnull
    public String getProgramId()
    {
        return programId;
    }

    @Nonnull
    public String getProgramStageId()
    {
        return programStageId;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        ProgramStageId that = (ProgramStageId) o;
        return getProgramId().equals( that.getProgramId() ) &&
            getProgramStageId().equals( that.getProgramStageId() );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( getProgramId(), getProgramStageId() );
    }

    @Override
    public int compareTo( @Nonnull ProgramStageId o )
    {
        int value = programId.compareTo( o.programId );
        if ( value != 0 )
        {
            return value;
        }
        return programStageId.compareTo( o.programStageId );
    }
}
