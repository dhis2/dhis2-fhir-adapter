package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.program;

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
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageId;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
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
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Implementation of {@link DhisToFhirDataProvider} for DHIS2 Program Stages.
 *
 * @author volsch
 */
public abstract class AbstractProgramStageToFhirDataProvider extends AbstractDhisToFhirDataProvider<ProgramStageRule>
{
    private final ProgramMetadataService metadataService;

    private final EventService eventService;

    public AbstractProgramStageToFhirDataProvider( @Nonnull ScriptExecutor scriptExecutor, @Nonnull ProgramMetadataService metadataService, @Nonnull EventService eventService )
    {
        super( scriptExecutor, false );

        this.metadataService = metadataService;
        this.eventService = eventService;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.PROGRAM_STAGE_EVENT;
    }

    @Nonnull
    @Override
    protected Class<ProgramStageRule> getRuleClass()
    {
        return ProgramStageRule.class;
    }

    @Nullable
    @Override
    public DhisResource findByDhisFhirIdentifier( @Nonnull FhirClient fhirClient, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull String identifier )
    {
        // finding by identifier is not supported
        return null;
    }

    @Nonnull
    @Override
    public PreparedDhisToFhirSearch prepareSearch( @Nonnull FhirVersion fhirVersion, @Nonnull List<RuleInfo<ProgramStageRule>> ruleInfos, @Nullable Map<String, List<String>> filter, @Nullable DateRangeParam lastUpdatedDateRange, int count ) throws DhisToFhirDataProviderException
    {
        List<ProgramStageId> programStageIds = null;

        if ( ruleInfos.stream().noneMatch( ri -> ri.getRule().getProgramStage() == null ) )
        {
            final Set<ReferenceTuple> refs = ruleInfos.stream()
                .filter( ri -> ri.getRule().getProgramStage().isEnabled() && ri.getRule().getProgramStage().isExpEnabled() && ri.getRule().getProgramStage().getProgram().isEnabled() && ri.getRule().getProgramStage().getProgram().isExpEnabled() )
                .map( ri -> new ReferenceTuple( ri.getRule().getProgramStage().getProgram().getProgramReference(), ri.getRule().getProgramStage().getProgramStageReference() ) )
                .collect( Collectors.toCollection( TreeSet::new ) );

            programStageIds = refs.stream().map( ref -> {
                final Program program = metadataService.findMetadataByReference( ref.getProgramReference() )
                    .orElseThrow( () -> new TransformerMappingException( "Program does not exist: " + ref.getProgramReference() ) );
                final ProgramStage programStage = program.getOptionalStage( ref.getProgramStageReference() )
                    .orElseThrow( () -> new TransformerMappingException( "Program stage does not exist: " + ref.getProgramStageReference() ) );

                return new ProgramStageId( program.getId(), programStage.getId() );
            } ).distinct().collect( Collectors.toList() );
        }

        return new PreparedProgramStageToFhirSearch( fhirVersion, ruleInfos, filter, lastUpdatedDateRange, count, programStageIds );
    }

    @Nullable
    @Override
    public DhisToFhirSearchResult<? extends DhisResource> search( @Nonnull PreparedDhisToFhirSearch preparedSearch, @Nullable DhisToFhirSearchState state, int max )
    {
        final PreparedProgramStageToFhirSearch ps = (PreparedProgramStageToFhirSearch) preparedSearch;
        final ProgramStageToFhirSearchState ss = (ProgramStageToFhirSearchState) state;

        final ProgramStageId programStageId;
        final int from;

        if ( ss == null || !ss.isMore() )
        {
            if ( ps.isProgramStageRestricted() )
            {
                programStageId = ps.getNextProgramStageId( ss == null ? null : ss.getProgramStageId() );

                if ( programStageId == null )
                {
                    return null;
                }
            }
            else if ( ss == null )
            {
                programStageId = null;
            }
            else
            {
                return null;
            }

            from = 0;

            // may be filtered by ID of program stage
            ps.setUriFilterApplier( apply( ps.getFhirVersion(), ps.getRuleInfos(), ps.createSearchFilterCollector( null ) ) );
        }
        else
        {
            programStageId = ss.getProgramStageId();
            from = ss.getFrom();
        }

        final DhisResourceResult<Event> result;
        try
        {
            result = eventService.find(
                programStageId == null ? null : programStageId.getProgramId(),
                programStageId == null ? null : programStageId.getProgramStageId(),
                ps, from, max );
        }
        catch ( DhisFindException e )
        {
            throw new DhisToFhirDataProviderException( e.getMessage(), e );
        }

        return new DhisToFhirSearchResult<>( result.getResources(), new ProgramStageToFhirSearchState( programStageId, from + result.getResources().size(),
            !result.getResources().isEmpty() && result.isMore() ) );
    }

    public static class ReferenceTuple implements Comparable<ReferenceTuple>
    {
        private final Reference programReference;

        private final Reference programStageReference;

        public ReferenceTuple( @Nonnull Reference programReference, @Nonnull Reference programStageReference )
        {
            this.programReference = programReference;
            this.programStageReference = programStageReference;
        }

        @Nonnull
        public Reference getProgramReference()
        {
            return programReference;
        }

        @Nonnull
        public Reference getProgramStageReference()
        {
            return programStageReference;
        }

        @Override
        public boolean equals( Object o )
        {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;
            ReferenceTuple that = (ReferenceTuple) o;
            return getProgramReference().equals( that.getProgramReference() ) &&
                getProgramStageReference().equals( that.getProgramStageReference() );
        }

        @Override
        public int hashCode()
        {
            return Objects.hash( getProgramReference(), getProgramStageReference() );
        }

        @Override
        public int compareTo( @Nonnull ReferenceTuple o )
        {
            int value = programReference.compareTo( o.programReference );

            if ( value != 0 )
            {
                return value;
            }

            return programStageReference.compareTo( o.programStageReference );
        }
    }
}
