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
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceResult;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.OrganizationUnitRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProvider;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProviderException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirSearchResult;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirSearchState;
import org.dhis2.fhir.adapter.fhir.transform.dhis.PreparedDhisToFhirSearch;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.AbstractDhisToFhirDataProvider;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link DhisToFhirDataProvider} for DHIS2 Organisation Units.
 *
 * @author volsch
 */
@Component
public class OrganizationUnitToFhirDataProvider extends AbstractDhisToFhirDataProvider<OrganizationUnitRule>
{
    private final OrganizationUnitService organizationUnitService;

    public OrganizationUnitToFhirDataProvider( @Nonnull ScriptExecutor scriptExecutor, @Nonnull OrganizationUnitService organizationUnitService )
    {
        super( scriptExecutor );
        this.organizationUnitService = organizationUnitService;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.ORGANIZATION_UNIT;
    }

    @Nonnull
    @Override
    protected Class<OrganizationUnitRule> getRuleClass()
    {
        return OrganizationUnitRule.class;
    }

    @Nullable
    @Override
    public DhisResource findByDhisFhirIdentifier( @Nonnull FhirClient fhirClient, @Nonnull RuleInfo<OrganizationUnitRule> ruleInfo, @Nonnull String identifier )
    {
        return organizationUnitService.findOneRefreshedByReference( new Reference( identifier, ReferenceType.CODE ) ).orElse( null );
    }

    @Nonnull
    @Override
    public PreparedDhisToFhirSearch prepareSearch( @Nonnull FhirVersion fhirVersion, @Nonnull List<RuleInfo<OrganizationUnitRule>> ruleInfos, @Nullable Map<String, List<String>> filter, @Nullable DateRangeParam lastUpdatedDateRange, int count ) throws DhisToFhirDataProviderException
    {
        final PreparedOrganizationUnitDhisToFhirSearch ps = new PreparedOrganizationUnitDhisToFhirSearch( fhirVersion, ruleInfos, filter, lastUpdatedDateRange, count );
        ps.setUriFilterApplier( apply( fhirVersion, ruleInfos, ps.createSearchFilterCollector() ) );
        return ps;
    }

    @Nullable
    @Override
    public DhisToFhirSearchResult<OrganizationUnit> search( @Nonnull PreparedDhisToFhirSearch preparedSearch, @Nullable DhisToFhirSearchState state, int max )
    {
        final PreparedOrganizationUnitDhisToFhirSearch ps = (PreparedOrganizationUnitDhisToFhirSearch) preparedSearch;
        final OrganizationUnitToFhirSearchState ss = (OrganizationUnitToFhirSearchState) state;
        if ( (ss != null) && !ss.isMore() )
        {
            return null;
        }

        final int from = (ss == null) ? 0 : ss.getFrom();
        final DhisResourceResult<OrganizationUnit> result;
        try
        {
            result = organizationUnitService.find( ps, from, max );
        }
        catch ( DhisFindException e )
        {
            throw new DhisToFhirDataProviderException( e.getMessage(), e );
        }
        if ( result.getResources().isEmpty() )
        {
            return null;
        }
        return new DhisToFhirSearchResult<>( result.getResources(),
            new OrganizationUnitToFhirSearchState( from + result.getResources().size(),
                !result.getResources().isEmpty() && result.isMore() ) );
    }
}
