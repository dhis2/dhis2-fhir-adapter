package org.dhis2.fhir.adapter.fhir.transform.dhis;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * The outcome of the transformation between a DHIS2 resource and a FHIR resource.
 *
 * @param <R> the concrete type of the returned FHIR resource.
 * @author volsch
 */
public class DhisToFhirTransformOutcome<R extends IBaseResource> implements Serializable
{
    private static final long serialVersionUID = -1022009827965716982L;

    private final AbstractRule rule;

    private final R resource;

    private final boolean delete;

    private final DhisToFhirTransformerRequest nextTransformerRequest;

    public DhisToFhirTransformOutcome( @Nonnull AbstractRule rule, @Nullable R resource )
    {
        this( rule, resource, false );
    }

    public DhisToFhirTransformOutcome( @Nonnull AbstractRule rule, @Nullable R resource, boolean delete )
    {
        if ( delete && (resource == null) )
        {
            throw new IllegalArgumentException( "Resource must be specified when it should be deleted" );
        }
        this.rule = rule;
        this.resource = resource;
        this.delete = delete;
        this.nextTransformerRequest = null;
    }

    public DhisToFhirTransformOutcome( @Nonnull DhisToFhirTransformOutcome<R> outcome, @Nullable DhisToFhirTransformerRequest nextTransformerRequest )
    {
        this.rule = outcome.getRule();
        this.resource = outcome.getResource();
        this.delete = outcome.isDelete();
        this.nextTransformerRequest = nextTransformerRequest;
    }

    @Nonnull
    public AbstractRule getRule()
    {
        return rule;
    }

    @Nullable
    public R getResource()
    {
        return resource;
    }

    public boolean isDelete()
    {
        return delete;
    }

    @Nullable
    public DhisToFhirTransformerRequest getNextTransformerRequest()
    {
        return nextTransformerRequest;
    }
}
