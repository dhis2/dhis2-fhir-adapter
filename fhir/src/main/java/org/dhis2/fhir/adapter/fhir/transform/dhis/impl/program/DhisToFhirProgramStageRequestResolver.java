package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.program;

/*
 * Copyright (c) 2004-2018, University of Oslo
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
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.repository.ProgramStageRuleRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionRepository;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.AbstractDhisToFhirRequestResolver;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.DhisToFhirRequestResolver;
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
public class DhisToFhirProgramStageRequestResolver extends AbstractDhisToFhirRequestResolver
{
    private final ProgramMetadataService programMetadataService;

    private final ProgramStageRuleRepository ruleRepository;

    private final TrackedEntityService trackedEntityService;

    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final ValueConverter valueConverter;

    public DhisToFhirProgramStageRequestResolver(
        @Nonnull RemoteSubscriptionRepository remoteSubscriptionRepository,
        @Nonnull ProgramMetadataService programMetadataService,
        @Nonnull ProgramStageRuleRepository ruleRepository,
        @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService,
        @Nonnull ValueConverter valueConverter )
    {
        super( remoteSubscriptionRepository );
        this.programMetadataService = programMetadataService;
        this.ruleRepository = ruleRepository;
        this.trackedEntityService = trackedEntityService;
        this.trackedEntityMetadataService = trackedEntityMetadataService;
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
        return ruleRepository.findAllExp( event.getProgram().getAllReferences(), event.getProgramStage().getAllReferences(), null ).stream()
            .sorted().collect( Collectors.toList() );
    }

    @Nonnull
    @Override
    public ScriptedDhisResource convert( @Nonnull DhisResource dhisResource )
    {
        final Event event = (Event) dhisResource;

        final TrackedEntityInstance tei = trackedEntityService.findOneById( event.getTrackedEntityInstanceId() )
            .orElseThrow( () -> new TransformerDataException( "Tracked entity instance " + event.getTrackedEntityInstanceId() +
                " of event " + event.getId() + " could not be found." ) );
        final TrackedEntityType trackedEntityType = trackedEntityMetadataService.findTypeByReference( new Reference( tei.getTypeId(), ReferenceType.ID ) )
            .orElseThrow( () -> new TransformerDataException( "Tracked entity type " + tei.getTypeId() + " of tracked entity instance " +
                tei.getId() + " could not be found." ) );
        final TrackedEntityAttributes trackedEntityAttributes = trackedEntityMetadataService.getAttributes();
        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = new ImmutableScriptedTrackedEntityInstance(
            new WritableScriptedTrackedEntityInstance( trackedEntityAttributes, trackedEntityType, tei, valueConverter ) );

        final Program program = programMetadataService.findProgramByReference( new Reference( event.getProgramId(), ReferenceType.ID ) )
            .orElseThrow( () -> new TransformerDataException( "Program " + event.getProgramId() + " of event " + event.getId() + " could not be found." ) );
        final ProgramStage programStage = program.getOptionalStage( new Reference( event.getProgramStageId(), ReferenceType.ID ) )
            .orElseThrow( () -> new TransformerDataException( "Program stage " + event.getProgramStageId() + " of event " + event.getId() +
                " could not be found as part of program " + event.getProgramId() + "." ) );
        return new ImmutableScriptedEvent( new WritableScriptedEvent( program, programStage, event,
            scriptedTrackedEntityInstance, valueConverter ) );
    }
}
