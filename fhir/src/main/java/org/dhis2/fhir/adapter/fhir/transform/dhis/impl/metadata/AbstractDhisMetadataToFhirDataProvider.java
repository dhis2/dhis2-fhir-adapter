package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata;

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
import org.dhis2.fhir.adapter.dhis.DhisFindException;
import org.dhis2.fhir.adapter.dhis.model.DhisMetadata;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceResult;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.model.UriFilterApplier;
import org.dhis2.fhir.adapter.dhis.service.DhisMetadataService;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProvider;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProviderException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirSearchResult;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirSearchState;
import org.dhis2.fhir.adapter.fhir.transform.dhis.PreparedDhisToFhirSearch;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.AbstractDhisToFhirDataProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link DhisToFhirDataProvider} for DHIS2 metadata resources.
 *
 * @param <T> the concrete type of the DHIS2 resource.
 * @param <U> the concrete type of the rule that performs the transformation.
 * @author volsch
 */
public abstract class AbstractDhisMetadataToFhirDataProvider<T extends DhisResource & DhisMetadata, U extends AbstractRule> extends AbstractDhisToFhirDataProvider<U>
{
    private final DhisMetadataService<T> metadataService;

    public AbstractDhisMetadataToFhirDataProvider( @Nonnull ScriptExecutor scriptExecutor, @Nonnull DhisMetadataService<T> metadataService )
    {
        super( scriptExecutor, false );
        this.metadataService = metadataService;
    }

    @Nonnull
    @Override
    public PreparedDhisToFhirSearch prepareSearch( @Nonnull FhirVersion fhirVersion, @Nonnull List<RuleInfo<U>> ruleInfos, @Nullable Map<String, List<String>> filter, @Nullable DateRangeParam lastUpdatedDateRange, int count ) throws DhisToFhirDataProviderException
    {
        final PreparedDhisMetadataToFhirSearch<U> ps = new PreparedDhisMetadataToFhirSearch<>( fhirVersion, ruleInfos, filter, lastUpdatedDateRange, count );
        ps.setUriFilterApplier( apply( fhirVersion, ruleInfos, ps.createSearchFilterCollector( null ) ) );

        return ps;
    }

    @Nullable
    @Override
    public DhisToFhirSearchResult<T> search( @Nonnull PreparedDhisToFhirSearch preparedSearch, @Nullable DhisToFhirSearchState state, int max )
    {
        final PreparedDhisMetadataToFhirSearch ps = (PreparedDhisMetadataToFhirSearch) preparedSearch;
        final DhisMetadataToFhirSearchState ss = (DhisMetadataToFhirSearchState) state;

        if ( (ss != null) && !ss.isMore() )
        {
            return null;
        }

        final int from = (ss == null) ? 0 : ss.getFrom();
        final DhisResourceResult<T> result;

        try
        {
            result = find( ps, from, max );
        }
        catch ( DhisFindException e )
        {
            throw new DhisToFhirDataProviderException( e.getMessage(), e );
        }

        if ( result.getResources().isEmpty() )
        {
            return null;
        }

        return new DhisToFhirSearchResult<>( result.getResources(), new DhisMetadataToFhirSearchState( from + result.getResources().size(),
                !result.getResources().isEmpty() && result.isMore() ) );
    }

    @Nullable
    @Override
    public DhisResource findByDhisFhirIdentifier( @Nonnull FhirClient fhirClient, @Nonnull RuleInfo<U> ruleInfo, @Nonnull String identifier )
    {
        return metadataService.findOneByReference( new Reference( identifier, ReferenceType.CODE ) ).orElse( null );
    }

    @Nonnull
    protected DhisResourceResult<T> find( @Nonnull UriFilterApplier uriFilterApplier, int from, int max )
    {
        return metadataService.find( uriFilterApplier, from, max );
    }
}
