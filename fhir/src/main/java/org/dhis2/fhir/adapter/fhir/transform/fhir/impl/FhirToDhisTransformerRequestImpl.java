package org.dhis2.fhir.adapter.fhir.transform.fhir.impl;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.FhirToDhisTransformerUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link FhirToDhisTransformerContext}.
 *
 * @author volsch
 */
public class FhirToDhisTransformerRequestImpl implements FhirToDhisTransformerRequest
{
    private static final long serialVersionUID = 4181923310602004074L;

    private final FhirToDhisTransformerContext context;

    private final IBaseResource input;

    private final Map<String, FhirToDhisTransformerUtils> transformerUtils;

    private final List<? extends AbstractRule> rules;

    private int ruleIndex;

    public FhirToDhisTransformerRequestImpl( @Nonnull FhirToDhisTransformerContext context, @Nonnull IBaseResource input, @Nonnull Map<String, FhirToDhisTransformerUtils> transformerUtils, @Nonnull List<? extends AbstractRule> rules )
    {
        this.context = context;
        this.input = input;
        this.transformerUtils = transformerUtils;
        this.rules = rules;
    }

    @Nonnull
    @Override
    public FhirToDhisTransformerContext getContext()
    {
        return context;
    }

    @Nonnull
    @Override
    public IBaseResource getInput()
    {
        return input;
    }

    @Nonnull
    public Map<String, FhirToDhisTransformerUtils> getTransformerUtils()
    {
        return transformerUtils;
    }

    @Nonnull
    public List<? extends AbstractRule> getRules()
    {
        return rules;
    }

    public boolean isFirstRule()
    {
        return (ruleIndex == 0);
    }

    public boolean isLastRule()
    {
        return (ruleIndex >= rules.size());
    }

    @Nullable
    public AbstractRule nextRule()
    {
        if ( ruleIndex >= rules.size() )
        {
            return null;
        }
        return rules.get( ruleIndex++ );
    }
}
