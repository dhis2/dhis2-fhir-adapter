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
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.DhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisResource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests {@link DhisToFhirTransformerRequestImpl}.
 *
 * @author volsch
 */
public class DhisToFhirTransformerRequestImplTest
{
    @Mock
    private DhisToFhirTransformerContext context;

    @Mock
    private ScriptedDhisResource input;

    @Mock
    private FhirClient fhirClient;

    private final Map<String, DhisToFhirTransformerUtils> transformerUtils = new HashMap<>();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void isSimpleFhirId()
    {
        final List<RuleInfo<? extends AbstractRule>> ruleInfos = new ArrayList<>();

        final TrackedEntityRule trackedEntityRule = new TrackedEntityRule();
        trackedEntityRule.setSimpleFhirId( true );
        ruleInfos.add( new RuleInfo<>( trackedEntityRule, Collections.emptyList() ) );

        final DhisToFhirTransformerRequestImpl transformerRequest = new DhisToFhirTransformerRequestImpl(
            context, input, fhirClient, ruleInfos, transformerUtils );

        Assert.assertTrue( transformerRequest.isSimpleFhirIdRule() );
    }

    @Test
    public void isNotSimpleFhirId()
    {
        final List<RuleInfo<? extends AbstractRule>> ruleInfos = new ArrayList<>();

        final TrackedEntityRule trackedEntityRule = new TrackedEntityRule();
        trackedEntityRule.setSimpleFhirId( false );
        ruleInfos.add( new RuleInfo<>( trackedEntityRule, Collections.emptyList() ) );

        final DhisToFhirTransformerRequestImpl transformerRequest = new DhisToFhirTransformerRequestImpl(
            context, input, fhirClient, ruleInfos, transformerUtils );

        Assert.assertFalse( transformerRequest.isSimpleFhirIdRule() );
    }

    @Test
    public void isNotSimpleFhirIdMultiple()
    {
        final List<RuleInfo<? extends AbstractRule>> ruleInfos = new ArrayList<>();

        final TrackedEntityRule trackedEntityRule1 = new TrackedEntityRule();
        trackedEntityRule1.setSimpleFhirId( false );

        final TrackedEntityRule trackedEntityRule2 = new TrackedEntityRule();
        trackedEntityRule2.setSimpleFhirId( false );

        ruleInfos.add( new RuleInfo<>( trackedEntityRule1, Collections.emptyList() ) );
        ruleInfos.add( new RuleInfo<>( trackedEntityRule2, Collections.emptyList() ) );

        final DhisToFhirTransformerRequestImpl transformerRequest = new DhisToFhirTransformerRequestImpl(
            context, input, fhirClient, ruleInfos, transformerUtils );

        Assert.assertFalse( transformerRequest.isSimpleFhirIdRule() );
    }
}
