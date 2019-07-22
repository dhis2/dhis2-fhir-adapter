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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;

import javax.annotation.Nonnull;
import javax.persistence.Transient;

/**
 * Abstract implementation of a rule that has some default implementations for
 * special cases.
 *
 * @author volsch
 */
public abstract class AbstractSimpleRule extends AbstractRule
{
    private static final long serialVersionUID = 1545627424069944659L;

    public AbstractSimpleRule( @Nonnull DhisResourceType dhisResourceType )
    {
        super( dhisResourceType );
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirCreateEnable()
    {
        return isExpEnabled() && isFhirCreateEnabled();
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirUpdateEnable()
    {
        return isExpEnabled() && isFhirUpdateEnabled();
    }

    @Override
    @Transient
    @JsonIgnore
    public boolean isEffectiveFhirDeleteEnable()
    {
        return isExpEnabled() && isFhirDeleteEnabled();
    }

    @Override
    public boolean coversExecutedRule( @Nonnull AbstractRule executedRule )
    {
        return false;
    }
}
