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

import ca.uhn.fhir.rest.param.DateRangeParam;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProvider;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProviderException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.PreparedDhisToFhirSearch;
import org.dhis2.fhir.adapter.fhir.transform.dhis.search.SearchFilter;
import org.dhis2.fhir.adapter.fhir.transform.dhis.search.SearchFilterCollector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Abstract implementation of data provider for specific DHIS resources.
 *
 * @param <R> the concrete type of the rule.
 * @author volsch
 */
public abstract class AbstractDhisToFhirDataProvider<R extends AbstractRule> implements DhisToFhirDataProvider<R>
{
    private final ScriptExecutor scriptExecutor;

    private final boolean onlyStringContains;

    protected AbstractDhisToFhirDataProvider( @Nonnull ScriptExecutor scriptExecutor, boolean onlyStringContains )
    {
        this.scriptExecutor = scriptExecutor;
        this.onlyStringContains = onlyStringContains;
    }

    @Nonnull
    protected abstract Class<R> getRuleClass();

    @Nonnull
    protected SearchFilterCollector apply( @Nonnull FhirVersion fhirVersion, @Nonnull List<RuleInfo<R>> ruleInfos, @Nonnull SearchFilterCollector searchFilterCollector )
    {
        ruleInfos.forEach( ruleInfo -> {
            final SearchFilter searchFilter = new SearchFilter( searchFilterCollector, onlyStringContains );
            initSearchFilter( fhirVersion, ruleInfo, searchFilter );

            if ( ruleInfo.getRule().getFilterScript() != null )
            {
                final Boolean result = scriptExecutor.execute( ruleInfo.getRule().getFilterScript(), fhirVersion,
                    Collections.singletonMap( ScriptVariable.SEARCH_FILTER.getVariableName(), searchFilter ),
                    Collections.emptyMap(), Boolean.class );

                if ( !Boolean.TRUE.equals( result ) )
                {
                    throw new DhisToFhirDataProviderException( "Search parameter filter could not be applied." );
                }
            }
        } );

        return searchFilterCollector;
    }

    protected void initSearchFilter( @Nonnull FhirVersion fhirVersion, @Nonnull RuleInfo<R> ruleInfo, @Nonnull SearchFilter searchFilter )
    {
        // method may be overridden
    }

    @Nullable
    @Override
    public DhisResource findByDhisFhirIdentifierCasted( @Nonnull FhirClient fhirClient, @Nonnull RuleInfo<? extends AbstractRule> ruleInfo, @Nonnull String identifier )
    {
        return findByDhisFhirIdentifier( fhirClient, new RuleInfo<>( getRuleClass().cast( ruleInfo.getRule() ), ruleInfo.getDhisDataReferences() ), identifier );
    }

    @Nonnull
    @Override
    public PreparedDhisToFhirSearch prepareSearchCasted( @Nonnull FhirVersion fhirVersion, @Nonnull List<RuleInfo<? extends AbstractRule>> ruleInfos, @Nullable Map<String, List<String>> filter, @Nullable DateRangeParam lastUpdatedDateRange, int count ) throws DhisToFhirDataProviderException
    {
        final Class<R> ruleClass = getRuleClass();
        final List<RuleInfo<R>> castedRuleInfos = ruleInfos.stream().map( r -> new RuleInfo<>( getRuleClass().cast( r.getRule() ), r.getDhisDataReferences() ) ).collect( Collectors.toList() );

        return prepareSearch( fhirVersion, castedRuleInfos, filter, lastUpdatedDateRange, count );
    }
}
