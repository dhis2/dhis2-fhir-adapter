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

import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerRequest;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.DhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of {@link DhisToFhirTransformerRequest}.
 *
 * @author volsch
 */
public class DhisToFhirTransformerRequestImpl implements DhisToFhirTransformerRequest
{
    private static final long serialVersionUID = 4181923310602004074L;

    private final DhisToFhirTransformerContext context;

    private ScriptedDhisResource input;

    private final FhirClient fhirClient;

    private final List<RuleInfo<? extends AbstractRule>> rules;

    private final Map<String, DhisToFhirTransformerUtils> transformerUtils;

    private int ruleIndex;

    private final boolean simpleFhirIdRule;

    public DhisToFhirTransformerRequestImpl( @Nonnull DhisToFhirTransformerContext context, @Nullable ScriptedDhisResource input, @Nonnull FhirClient fhirClient, @Nonnull List<RuleInfo<? extends AbstractRule>> rules,
        @Nonnull Map<String, DhisToFhirTransformerUtils> transformerUtils )
    {
        this.context = context;
        this.input = input;
        this.fhirClient = fhirClient;
        this.rules = rules;
        this.transformerUtils = transformerUtils;

        this.simpleFhirIdRule = rules.size() == 1 && rules.get( 0 ).getRule().isSimpleFhirId();
    }

    @Nonnull
    @Override
    public DhisToFhirTransformerContext getContext()
    {
        return context;
    }

    @Nonnull
    @Override
    public ScriptedDhisResource getInput()
    {
        if ( input == null )
        {
            throw new FatalTransformerException( "Transformer input has not yet been set." );
        }

        return input;
    }

    public void setInput( @Nonnull ScriptedDhisResource input )
    {
        this.input = input;
    }

    @Nonnull
    @Override
    public FhirClient getFhirClient()
    {
        return fhirClient;
    }

    @Nonnull
    public List<RuleInfo<? extends AbstractRule>> getRules()
    {
        return rules;
    }

    @Nonnull
    public Map<String, DhisToFhirTransformerUtils> getTransformerUtils()
    {
        return transformerUtils;
    }

    @Nonnull
    @Override
    public UUID getRuleId()
    {
        if ( ruleIndex >= rules.size() )
        {
            throw new FatalTransformerException( "Rule ID is not available." );
        }

        return rules.get( ruleIndex ).getRule().getId();
    }

    @Override
    public boolean isSimpleFhirIdRule()
    {
        return simpleFhirIdRule;
    }

    public final boolean isFirstRule()
    {
        return (ruleIndex == 0);
    }

    public final boolean isLastRule()
    {
        return (ruleIndex >= rules.size());
    }

    public final boolean isSingleRule()
    {
        return (rules.size() == 1);
    }

    @Nullable
    public RuleInfo<? extends AbstractRule> nextRule()
    {
        if ( ruleIndex >= rules.size() )
        {
            return null;
        }

        final RuleInfo<? extends AbstractRule> ruleInfo = rules.get( ruleIndex++ );

        if ( input != null && ruleInfo.getRule().getDhisResourceType() != input.getResourceType() )
        {
            input = null;
        }

        return ruleInfo;
    }
}
