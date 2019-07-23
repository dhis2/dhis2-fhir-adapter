package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.trackedentity;

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
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttribute;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProvider;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProviderException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirSearchResult;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirSearchState;
import org.dhis2.fhir.adapter.fhir.transform.dhis.PreparedDhisToFhirSearch;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.AbstractDhisToFhirDataProvider;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Implementation of {@link DhisToFhirDataProvider} for DHIS2 Tracked Entities.
 *
 * @author volsch
 */
@Component
public class TrackedEntityToFhirDataProvider extends AbstractDhisToFhirDataProvider<TrackedEntityRule>
{
    private final TrackedEntityMetadataService metadataService;

    private final TrackedEntityService service;

    public TrackedEntityToFhirDataProvider( @Nonnull ScriptExecutor scriptExecutor, @Nonnull TrackedEntityMetadataService metadataService, @Nonnull TrackedEntityService service )
    {
        super( scriptExecutor, true );
        this.metadataService = metadataService;
        this.service = service;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.TRACKED_ENTITY;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.ALL;
    }

    @Nonnull
    @Override
    protected Class<TrackedEntityRule> getRuleClass()
    {
        return TrackedEntityRule.class;
    }

    @Nullable
    @Override
    public DhisResource findByDhisFhirIdentifier( @Nonnull FhirClient fhirClient, @Nonnull RuleInfo<TrackedEntityRule> ruleInfo, @Nonnull String identifier )
    {
        final TrackedEntityType type = metadataService.findTypeByReference( ruleInfo.getRule().getTrackedEntity().getTrackedEntityReference() )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity type does not exist: " + ruleInfo.getRule().getTrackedEntity().getTrackedEntityReference() ) );
        final TrackedEntityAttributes trackedEntityAttributes = metadataService.getAttributes();
        final TrackedEntityAttribute identifierAttribute = trackedEntityAttributes.getOptional( ruleInfo.getRule().getTrackedEntity().getTrackedEntityIdentifierReference() )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity identifier attribute does not exist: " + ruleInfo.getRule().getTrackedEntity().getTrackedEntityIdentifierReference() ) );

        final Collection<TrackedEntityInstance> result = service.findByAttrValueRefreshed( type.getId(), identifierAttribute.getId(), identifier, 1 );
        if ( result.size() > 1 )
        {
            return null;
        }
        return result.stream().findFirst().orElse( null );
    }

    @Nonnull
    @Override
    public PreparedDhisToFhirSearch prepareSearch( @Nonnull FhirVersion fhirVersion, @Nonnull List<RuleInfo<TrackedEntityRule>> ruleInfos, @Nullable Map<String, List<String>> filter, @Nullable DateRangeParam lastUpdatedDateRange, int count ) throws DhisToFhirDataProviderException
    {
        final Set<Reference> typeRefs = ruleInfos.stream().filter( ri -> ri.getRule().getTrackedEntity().isEnabled() && ri.getRule().getTrackedEntity().isExpEnabled() )
            .map( ri -> ri.getRule().getTrackedEntity().getTrackedEntityReference() ).collect( Collectors.toCollection( TreeSet::new ) );
        final List<TrackedEntityType> trackedEntityTypes = typeRefs.stream().map( typeRef -> metadataService.findTypeByReference( typeRef )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity type does not exist: " + typeRef ) ) ).collect( Collectors.toList() );
        return new PreparedTrackedEntityToFhirSearch( fhirVersion, ruleInfos, filter, lastUpdatedDateRange, count, trackedEntityTypes );
    }

    @Nullable
    @Override
    public DhisToFhirSearchResult<? extends DhisResource> search( @Nonnull PreparedDhisToFhirSearch preparedSearch, @Nullable DhisToFhirSearchState state, int max )
    {
        final PreparedTrackedEntityToFhirSearch ps = (PreparedTrackedEntityToFhirSearch) preparedSearch;
        final TrackedEntityToFhirSearchState ss = (TrackedEntityToFhirSearchState) state;

        final TrackedEntityType type;
        final int from;
        if ( (ss == null) || !ss.isMore() )
        {
            type = ps.getNextType( ( ss == null ) ? null : ss.getTrackedEntityType() );

            if ( type == null )
            {
                return null;
            }

            from = 0;

            // may be filtered by ID of tracked entity type
            ps.setUriFilterApplier( apply( ps.getFhirVersion(), ps.getRuleInfos(), ps.createSearchFilterCollector( type ) ) );
        }
        else
        {
            type = ss.getTrackedEntityType();
            from = ss.getFrom();
        }

        final DhisResourceResult<TrackedEntityInstance> result;
        try
        {
            result = service.find( type.getId(), ps, from, max );
        }
        catch ( DhisFindException e )
        {
            throw new DhisToFhirDataProviderException( e.getMessage(), e );
        }

        return new DhisToFhirSearchResult<>( result.getResources(),
            new TrackedEntityToFhirSearchState( type, from + result.getResources().size(),
                !result.getResources().isEmpty() && result.isMore() ) );
    }
}
