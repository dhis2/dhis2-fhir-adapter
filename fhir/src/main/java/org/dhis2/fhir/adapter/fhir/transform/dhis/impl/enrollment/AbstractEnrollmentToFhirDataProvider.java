package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.enrollment;

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
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
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
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.metadata.DhisMetadataToFhirSearchState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link DhisToFhirDataProvider} for DHIS2 Enrollments.
 *
 * @author volsch
 */
public abstract class AbstractEnrollmentToFhirDataProvider extends AbstractDhisToFhirDataProvider<EnrollmentRule>
{
    private final EnrollmentService enrollmentService;

    public AbstractEnrollmentToFhirDataProvider( @Nonnull ScriptExecutor scriptExecutor, @Nonnull EnrollmentService enrollmentService )
    {
        super( scriptExecutor, false );

        this.enrollmentService = enrollmentService;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.ENROLLMENT;
    }

    @Nonnull
    @Override
    protected Class<EnrollmentRule> getRuleClass()
    {
        return EnrollmentRule.class;
    }

    @Nullable
    @Override
    public DhisResource findByDhisFhirIdentifier( @Nonnull FhirClient fhirClient, @Nonnull RuleInfo<EnrollmentRule> ruleInfo, @Nonnull String identifier )
    {
        // finding by identifier is not supported
        return null;
    }

    @Nonnull
    @Override
    public PreparedDhisToFhirSearch prepareSearch( @Nonnull FhirVersion fhirVersion, @Nonnull List<RuleInfo<EnrollmentRule>> ruleInfos, @Nullable Map<String, List<String>> filter, @Nullable DateRangeParam lastUpdatedDateRange, int count ) throws DhisToFhirDataProviderException
    {
        final PreparedEnrollmentToFhirSearch ps = new PreparedEnrollmentToFhirSearch( fhirVersion, ruleInfos, filter, lastUpdatedDateRange, count );
        ps.setUriFilterApplier( apply( fhirVersion, ruleInfos, ps.createSearchFilterCollector( null ) ) );

        return ps;
    }

    @Nullable
    @Override
    public DhisToFhirSearchResult<? extends DhisResource> search( @Nonnull PreparedDhisToFhirSearch preparedSearch, @Nullable DhisToFhirSearchState state, int max )
    {
        final PreparedEnrollmentToFhirSearch ps = (PreparedEnrollmentToFhirSearch) preparedSearch;
        final DhisMetadataToFhirSearchState ss = (DhisMetadataToFhirSearchState) state;

        if ( ss != null && !ss.isMore() )
        {
            return null;
        }

        final int from = ( ss == null ) ? 0 : ss.getFrom();
        final DhisResourceResult<Enrollment> result;

        try
        {
            result = enrollmentService.find( ps, from, max );
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
}
