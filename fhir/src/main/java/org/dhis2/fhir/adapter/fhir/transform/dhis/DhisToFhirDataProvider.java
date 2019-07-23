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

import ca.uhn.fhir.rest.param.DateRangeParam;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.model.FhirVersionRestricted;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Data provider for specific DHIS resources.
 *
 * @param <R> the concrete type of the rule.
 * @author volsch
 */
public interface DhisToFhirDataProvider<R extends AbstractRule> extends FhirVersionRestricted
{
    @Nonnull
    DhisResourceType getDhisResourceType();

    @Nullable
    DhisResource findByDhisFhirIdentifierCasted( @Nonnull FhirClient fhirClient, @Nonnull RuleInfo<? extends AbstractRule> ruleInfo, @Nonnull String identifier );

    @Nullable
    DhisResource findByDhisFhirIdentifier( @Nonnull FhirClient fhirClient, @Nonnull RuleInfo<R> ruleInfo, @Nonnull String identifier );

    @Nonnull
    PreparedDhisToFhirSearch prepareSearchCasted( @Nonnull FhirVersion fhirVersion, @Nonnull List<RuleInfo<? extends AbstractRule>> ruleInfos, @Nullable Map<String, List<String>> filter, @Nullable DateRangeParam lastUpdatedDateRange, int count ) throws DhisToFhirDataProviderException;

    @Nonnull
    PreparedDhisToFhirSearch prepareSearch( @Nonnull FhirVersion fhirVersion, @Nonnull List<RuleInfo<R>> ruleInfos, @Nullable Map<String, List<String>> filter, @Nullable DateRangeParam lastUpdatedDateRange, int count ) throws DhisToFhirDataProviderException;

    @Nullable
    DhisToFhirSearchResult<? extends DhisResource> search( @Nonnull PreparedDhisToFhirSearch preparedSearch, @Nullable DhisToFhirSearchState state, int max );
}
