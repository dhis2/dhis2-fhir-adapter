package org.dhis2.fhir.adapter.fhir.transform.dhis.impl;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Key to store transformer outcomes for single rule transformer requests.
 *
 * @author volsch
 */
public class TransformerRequestKey implements Serializable
{
    private static final long serialVersionUID = -2361879658238098797L;

    private final UUID ruleId;

    private final DhisResourceId dhisResourceId;

    public TransformerRequestKey( @Nonnull UUID ruleId, @Nonnull DhisResourceId dhisResourceId )
    {
        this.ruleId = ruleId;
        this.dhisResourceId = dhisResourceId;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        TransformerRequestKey that = (TransformerRequestKey) o;

        return ruleId.equals( that.ruleId ) && dhisResourceId.equals( that.dhisResourceId );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( ruleId, dhisResourceId );
    }

    @Override
    @Nonnull
    public String toString()
    {
        return "[ruleId=" + ruleId + ", dhisResourceId=" + dhisResourceId + ']';
    }
}
