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

import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ProgramStageRuleRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.AbstractDhisToFhirRequestResolver;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.DhisToFhirRequestResolver;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.DhisRequest;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ImmutableScriptedEvent;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ImmutableScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisResource;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedEvent;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEvent;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedTrackedEntityInstance;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link DhisToFhirRequestResolver} for program stage based events.
 *
 * @author volsch
 */
@Component
public class ProgramStageToFhirRequestResolver extends AbstractDhisToFhirRequestResolver
{
    private final ProgramMetadataService programMetadataService;

    private final ProgramStageRuleRepository programStageRuleRepository;

    private final TrackedEntityService trackedEntityService;

    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final ScriptExecutionContext scriptExecutionContext;

    private final ValueConverter valueConverter;

    public ProgramStageToFhirRequestResolver(
        @Nonnull FhirClientRepository fhirClientRepository,
        @Nonnull ProgramMetadataService programMetadataService,
        @Nonnull ProgramStageRuleRepository programStageRuleRepository,
        @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService,
        @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull ValueConverter valueConverter )
    {
        super( fhirClientRepository );

        this.programMetadataService = programMetadataService;
        this.programStageRuleRepository = programStageRuleRepository;
        this.trackedEntityService = trackedEntityService;
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.scriptExecutionContext = scriptExecutionContext;
        this.valueConverter = valueConverter;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.PROGRAM_STAGE_EVENT;
    }

    @Nonnull
    @Override
    public List<RuleInfo<? extends AbstractRule>> resolveRules( @Nonnull ScriptedDhisResource dhisResource )
    {
        final ScriptedEvent event = (ScriptedEvent) dhisResource;

        return programStageRuleRepository.findAllExp( event.getProgram().getAllReferences(), event.getProgramStage().getAllReferences(), null )
            .stream().sorted().collect( Collectors.toList() );
    }

    @Nonnull
    @Override
    public List<RuleInfo<? extends AbstractRule>> filterRules( @Nonnull ScriptedDhisResource dhisResource, @Nonnull List<RuleInfo<? extends AbstractRule>> rules )
    {
        final ScriptedEvent event = (ScriptedEvent) dhisResource;
        final Program program = event.getProgram();
        final ProgramStage programStage = event.getProgramStage();

        return rules.stream().map( ri -> new RuleInfo<>( (ProgramStageRule) ri.getRule(), ri.getDhisDataReferences() ) )
            .filter( ri -> ri.getRule().getProgramStage() == null || (
                program.isReference( ri.getRule().getProgramStage().getProgram().getProgramReference() ) &&
                programStage.isReference( ri.getRule().getProgramStage().getProgramStageReference() ) &&
                ri.getRule().getProgramStage().isEnabled() && ri.getRule().getProgramStage().isExpEnabled() &&
                    ri.getRule().getProgramStage().getProgram().isEnabled() && ri.getRule().getProgramStage().getProgram().isExpEnabled() ) )
            .sorted().collect( Collectors.toList() );
    }

    @Nonnull
    @Override
    public ScriptedDhisResource convert( @Nonnull DhisResource dhisResource, @Nonnull DhisRequest dhisRequest )
    {
        final Event event = (Event) dhisResource;

        final Program program = programMetadataService.findMetadataByReference( new Reference( event.getProgramId(), ReferenceType.ID ) )
            .orElseThrow( () -> new TransformerDataException( "Program " + event.getProgramId() + " of event " + event.getId() + " could not be found." ) );
        final ProgramStage programStage = program.getOptionalStage( new Reference( event.getProgramStageId(), ReferenceType.ID ) )
            .orElseThrow( () -> new TransformerDataException( "Program stage " + event.getProgramStageId() + " of event " + event.getId() +
                " could not be found as part of program " + event.getProgramId() + "." ) );
        final TrackedEntityType trackedEntityType = trackedEntityMetadataService.findTypeByReference( Reference.createIdReference( program.getTrackedEntityTypeId() ) )
            .orElseThrow( () -> new TransformerDataException( "Tracked entity type " + program.getTrackedEntityTypeId() + " of program " + program.getId() + "." ) );

        final TrackedEntityAttributes trackedEntityAttributes = trackedEntityMetadataService.getAttributes();
        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = new ImmutableScriptedTrackedEntityInstance( new WritableScriptedTrackedEntityInstance(
            trackedEntityMetadataService, trackedEntityService, trackedEntityAttributes, trackedEntityType, event.getTrackedEntityInstanceId(), scriptExecutionContext, valueConverter ) );

        return new ImmutableScriptedEvent( new WritableScriptedEvent( program, programStage, event, scriptedTrackedEntityInstance, scriptExecutionContext, valueConverter ) );
    }
}
