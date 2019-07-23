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
import org.dhis2.fhir.adapter.dhis.model.ItemContainerType;
import org.dhis2.fhir.adapter.dhis.model.UriFilterApplier;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProviderException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.PreparedDhisToFhirSearch;
import org.dhis2.fhir.adapter.fhir.transform.dhis.search.SearchFilterCollector;
import org.springframework.web.util.UriBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Abstract implementation of {@link PreparedDhisToFhirSearch}.
 *
 * @param <T> the concrete type of the rule.
 * @author volsch
 */
public abstract class AbstractPreparedDhisToFhirSearch<T extends AbstractRule> implements PreparedDhisToFhirSearch, UriFilterApplier
{
    private final FhirVersion fhirVersion;

    private final List<RuleInfo<T>> ruleInfos;

    private final Map<String, List<String>> filter;

    private final DateRangeParam lastUpdatedDateRange;

    private final int count;

    private UriFilterApplier uriFilterApplier;

    protected AbstractPreparedDhisToFhirSearch( @Nonnull FhirVersion fhirVersion, @Nonnull List<RuleInfo<T>> ruleInfos,
        @Nullable Map<String, List<String>> filter, @Nullable DateRangeParam lastUpdatedDateRange, int count )
    {
        this.fhirVersion = fhirVersion;
        this.ruleInfos = ruleInfos;
        this.filter = filter;
        this.lastUpdatedDateRange = lastUpdatedDateRange;
        this.count = count;
    }

    @Nonnull
    @Override
    public FhirVersion getFhirVersion()
    {
        return fhirVersion;
    }

    @Nonnull
    public List<RuleInfo<T>> getRuleInfos()
    {
        return ruleInfos;
    }

    @Nullable
    public DateRangeParam getLastUpdatedDateRange()
    {
        return lastUpdatedDateRange;
    }

    public int getCount()
    {
        return count;
    }

    public void setUriFilterApplier( @Nonnull UriFilterApplier uriFilterApplier )
    {
        this.uriFilterApplier = uriFilterApplier;
    }

    public SearchFilterCollector createSearchFilterCollector( @Nullable ItemContainerType itemContainerType )
    {
        return new SearchFilterCollector( itemContainerType, filter );
    }

    @Nonnull
    @Override
    public <U extends UriBuilder> U add( @Nonnull U uriBuilder, @Nonnull List<String> variables )
    {
        if ( uriFilterApplier == null )
        {
            throw new IllegalStateException( "URI filter applier has not been set" );
        }

        if ( lastUpdatedDateRange != null && !lastUpdatedDateRange.isEmpty() )
        {
            throw new DhisToFhirDataProviderException( "Search parameter to filter last updated date range is not yet supported." );
        }

        return uriFilterApplier.add( uriBuilder, variables );
    }

    @Override
    public boolean containsQueryParam( @Nonnull String name )
    {
        return uriFilterApplier.containsQueryParam( name );
    }
}
