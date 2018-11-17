package org.dhis2.fhir.adapter.setup;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Contains the setup of a single reference.
 *
 * @author volsch
 */
public class ReferenceSetup implements Serializable
{
    private static final long serialVersionUID = 767218823240343127L;

    private boolean enabled;

    @NotNull
    private ReferenceType referenceType;

    @NotBlank
    @Size( max = Reference.MAX_VALUE_LENGTH )
    private String referenceValue;

    public ReferenceSetup()
    {
        super();
    }

    public ReferenceSetup( @NotNull ReferenceType referenceType, @NotBlank @Size( max = Reference.MAX_VALUE_LENGTH ) String referenceValue )
    {
        this.referenceType = referenceType;
        this.referenceValue = referenceValue;
        this.enabled = true;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }

    public ReferenceType getReferenceType()
    {
        return referenceType;
    }

    public void setReferenceType( ReferenceType referenceType )
    {
        this.referenceType = referenceType;
    }

    public String getReferenceValue()
    {
        return referenceValue;
    }

    public void setReferenceValue( String referenceValue )
    {
        this.referenceValue = referenceValue;
    }

    @Nonnull
    public Reference getReference()
    {
        return new Reference( StringUtils.trimToNull( getReferenceValue() ), getReferenceType() );
    }

    @Nonnull
    public String getMandatoryRefVal()
    {
        return getReference().toString();
    }

    @Nullable
    public String getOptionalRefVal()
    {
        return isEnabled() ? getMandatoryRefVal() : null;
    }
}
