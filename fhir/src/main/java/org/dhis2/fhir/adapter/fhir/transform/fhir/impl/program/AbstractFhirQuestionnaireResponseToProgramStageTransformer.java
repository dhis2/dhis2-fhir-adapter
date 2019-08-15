package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program;

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
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.IdentifiedTrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.TrackedEntityRuleRepository;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisDeleteTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.AbstractFhirToDhisTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEvent;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.spring.StaticObjectProvider;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Abstract implementation of a transformer that transforms from FHIR Questionnaire Response to a DHIS2 event.
 *
 * @author volsch
 */
public abstract class AbstractFhirQuestionnaireResponseToProgramStageTransformer extends AbstractFhirToDhisTransformer<Event, ProgramStageRule>
{
    protected final EventService eventService;

    protected final TrackedEntityMetadataService trackedEntityMetadataService;

    protected final TrackedEntityRuleRepository trackedEntityRuleRepository;

    protected final ScriptExecutionContext scriptExecutionContext;

    protected final ValueConverter valueConverter;

    protected final ProgramMetadataService programMetadataService;

    protected final ProgramStageMetadataService programStageMetadataService;

    protected final FhirResourceMappingRepository resourceMappingRepository;

    public AbstractFhirQuestionnaireResponseToProgramStageTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull TrackedEntityRuleRepository trackedEntityRuleRepository, @Nonnull OrganizationUnitService organizationUnitService,
        @Nonnull ProgramMetadataService programMetadataService, @Nonnull ProgramStageMetadataService programStageMetadataService, @Nonnull EventService eventService,
        @Nonnull FhirResourceMappingRepository resourceMappingRepository, @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutor, organizationUnitService, new StaticObjectProvider<>( trackedEntityMetadataService ), new StaticObjectProvider<>( trackedEntityService ),
            fhirDhisAssignmentRepository, scriptExecutionContext, valueConverter );

        this.programMetadataService = programMetadataService;
        this.programStageMetadataService = programStageMetadataService;
        this.eventService = eventService;
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.trackedEntityRuleRepository = trackedEntityRuleRepository;
        this.resourceMappingRepository = resourceMappingRepository;
        this.scriptExecutionContext = scriptExecutionContext;
        this.valueConverter = valueConverter;
    }

    @Override
    @Nonnull
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.PROGRAM_STAGE_EVENT;
    }

    @Override
    @Nonnull
    public Class<Event> getDhisResourceClass()
    {
        return Event.class;
    }

    @Override
    @Nonnull
    public Class<ProgramStageRule> getRuleClass()
    {
        return ProgramStageRule.class;
    }

    @Override
    public int getPriority()
    {
        return 10;
    }

    @Override
    protected boolean isSyncRequired( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return context.getFhirRequest().isSync();
    }

    @Override
    @Nonnull
    protected Optional<Event> getResourceById( String id ) throws TransformerException
    {
        return id == null ? Optional.empty() : eventService.findOneById( id );
    }

    @Override
    @Nonnull
    protected Optional<Event> findResourceById( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull String id, @Nonnull Map<String, Object> scriptVariables )
    {
        return eventService.findOneById( id );
    }

    @Nonnull
    @Override
    protected Optional<Event> getActiveResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, boolean sync, boolean refreshed ) throws TransformerException
    {
        return Optional.empty();
    }

    @Override
    protected Event createResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, String id, @Nonnull Map<String, Object> scriptVariables, boolean sync, boolean refreshed ) throws TransformerException
    {
        final Event event = new Event( true );
        event.setStatus( EventStatus.ACTIVE );

        return event;
    }

    @Override
    public FhirToDhisTransformOutcome<Event> transform( @Nonnull FhirClientResource fhirClientResource, @Nonnull FhirToDhisTransformerContext context,
        @Nonnull IBaseResource input, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( ruleInfo.getRule().getFhirResourceType() != FhirResourceType.QUESTIONNAIRE_RESPONSE || ruleInfo.getRule().getProgramStage() != null )
        {
            return null;
        }

        final Map<String, Object> variables = new HashMap<>( scriptVariables );

        addBasicScriptVariables( context, ruleInfo, variables, input );

        final RuleInfo<TrackedEntityRule> trackedEntityRuleInfo = getTrackedEntityRuleInfo( context, ruleInfo, scriptVariables,
            TransformerUtils.getScriptVariable( variables, ScriptVariable.PROGRAM, Program.class ),
            TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class ) );
        final FhirResourceMapping resourceMapping = getResourceMapping( ruleInfo, trackedEntityRuleInfo.getRule().getFhirResourceType() );
        final IdentifiedTrackedEntityInstance trackedEntityInstance = getTrackedEntityInstance( context, trackedEntityRuleInfo, resourceMapping, variables, false )
            .orElseThrow( () -> new TransformerDataException( "Tracked entity instance could not be found." ) );

        addScriptVariables( context, variables, ruleInfo, trackedEntityRuleInfo, trackedEntityInstance );

        final Event event = getResource( fhirClientResource, context, ruleInfo, variables )
            .orElseThrow( () -> new TransformerDataException( "Event could not be determined." ) );
        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class );
        final Program program = TransformerUtils.getScriptVariable( variables, ScriptVariable.PROGRAM, Program.class );
        final ProgramStage programStage = TransformerUtils.getScriptVariable( variables, ScriptVariable.PROGRAM_STAGE, ProgramStage.class );

        final WritableScriptedEvent scriptedEvent = new WritableScriptedEvent( program, programStage, event, scriptedTrackedEntityInstance, scriptExecutionContext, valueConverter );
        variables.put( ScriptVariable.OUTPUT.getVariableName(), scriptedEvent );

        transformInternal( context, ruleInfo, scriptVariables, resourceMapping,
            program, programStage, input, scriptedTrackedEntityInstance, scriptedEvent );
        if ( !transform( context, ruleInfo, variables ) )
        {
            throw new TransformerDataException( "Care plan data could not be transformed to enrollment." );
        }

        if ( event.isModified() )
        {
            scriptedEvent.validate();
        }

        return new FhirToDhisTransformOutcome<>( ruleInfo.getRule(), event, event.isNewResource() );
    }

    protected void transformInternal( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nonnull FhirResourceMapping fhirResourceMapping,
        @Nonnull Program program, @Nonnull ProgramStage programStage, @Nonnull IBaseResource questionnaireResponse, @Nonnull ScriptedTrackedEntityInstance scriptedTrackedEntityInstance, @Nonnull WritableScriptedEvent scriptedEvent ) throws TransformerException
    {
        // method can be overridden
    }

    @Override
    public FhirToDhisDeleteTransformOutcome<Event> transformDeletion( @Nonnull FhirClientResource fhirClientResource, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull DhisFhirResourceId dhisFhirResourceId ) throws TransformerException
    {
        if ( ruleInfo.getRule().getFhirResourceType() != FhirResourceType.QUESTIONNAIRE_RESPONSE || ruleInfo.getRule().getProgramStage() != null )
        {
            return null;
        }

        final Event event = new Event();
        event.setId( dhisFhirResourceId.getId() );

        return new FhirToDhisDeleteTransformOutcome<>( ruleInfo.getRule(), event, true );
    }

    protected void addBasicScriptVariables( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nonnull IBaseResource questionnaireResponse ) throws TransformerException
    {
        final ProgramStage programStage = getProgramStage( context, ruleInfo, scriptVariables, questionnaireResponse );
        scriptVariables.put( ScriptVariable.PROGRAM_STAGE.getVariableName(), programStage );

        final Program program = getProgram( context, ruleInfo, scriptVariables, programStage );
        scriptVariables.put( ScriptVariable.PROGRAM.getVariableName(), program );

        final TrackedEntityAttributes attributes = trackedEntityMetadataService.getAttributes();
        scriptVariables.put( ScriptVariable.TRACKED_ENTITY_ATTRIBUTES.getVariableName(), attributes );

        final TrackedEntityType trackedEntityType = getTrackedEntityType( context, ruleInfo, scriptVariables, program );
        scriptVariables.put( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName(), trackedEntityType );
    }

    protected void addScriptVariables( @Nonnull FhirToDhisTransformerContext context, @Nonnull Map<String, Object> variables, @Nonnull RuleInfo<ProgramStageRule> ruleInfo,
        @Nonnull RuleInfo<TrackedEntityRule> trackedEntityRuleRuleInfo, @Nonnull IdentifiedTrackedEntityInstance trackedEntityInstance ) throws TransformerException
    {
        variables.put( ScriptVariable.TRACKED_ENTITY_RESOURCE_TYPE.getVariableName(), trackedEntityRuleRuleInfo.getRule().getFhirResourceType() );
        addTrackedEntityScriptVariables( context, ruleInfo, trackedEntityInstance, variables );
    }

    @Nonnull
    protected FhirResourceMapping getResourceMapping( @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull FhirResourceType trackedEntityFhirResourceType )
    {
        return resourceMappingRepository.findOneByFhirResourceType( ruleInfo.getRule().getFhirResourceType(), trackedEntityFhirResourceType )
            .orElseThrow( () -> new TransformerMappingException( "No FHIR resource mapping has been defined for " + ruleInfo.getRule().getFhirResourceType() +
                " and tracked entity FHIR resource " + trackedEntityFhirResourceType + "." ) );
    }

    @Override
    protected boolean isAlwaysActiveResource( @Nonnull RuleInfo<ProgramStageRule> ruleInfo )
    {
        return false;
    }

    @Nonnull
    protected TrackedEntityType getTrackedEntityType( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull Program program )
    {
        return trackedEntityMetadataService.findTypeByReference( Reference.createIdReference( program.getTrackedEntityTypeId() ) )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity type " + program.getTrackedEntityTypeId() + " of tracker program " + program.getId() + " does not exist." ) );
    }

    @Nonnull
    protected RuleInfo<TrackedEntityRule> getTrackedEntityRuleInfo( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull Program program, @Nonnull TrackedEntityType trackedEntityType )
    {
        return trackedEntityRuleRepository.findByTypeRefs( trackedEntityType.getAllReferences() )
            .stream().findFirst().map( r -> new RuleInfo<>( r, Collections.emptyList() ) )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity type " + program.getTrackedEntityTypeId() + " is not associated with a tracked entity rule." ) );
    }

    @Nonnull
    protected abstract Reference getProgramStageRef( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nonnull IBaseResource questionnaireResponse );

    @Nonnull
    protected ProgramStage getProgramStage( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nonnull IBaseResource questionnaireResponse )
    {
        final Reference programStageRef = getProgramStageRef( context, ruleInfo, scriptVariables, questionnaireResponse );

        return programStageMetadataService.findMetadataByReference( programStageRef )
            .orElseThrow( () -> new TransformerDataException( "Tracker program stage \"" + programStageRef + "\" does not exist." ) );
    }

    @Nonnull
    protected Program getProgram( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<ProgramStageRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nonnull ProgramStage programStage )
    {
        final Program program = programMetadataService.findMetadataByReference( Reference.createIdReference( programStage.getProgramId() ) )
            .orElseThrow( () -> new TransformerDataException( "Tracker program \"" + programStage.getProgramId() + "\" does not exist." ) );

        if ( !program.isRegistration() )
        {
            throw new TransformerDataException( "Tracker program \"" + programStage.getProgramId() + "\" does not require registration." );
        }

        return program;
    }
}
