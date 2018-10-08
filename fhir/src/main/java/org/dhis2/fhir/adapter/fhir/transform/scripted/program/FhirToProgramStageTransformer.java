package org.dhis2.fhir.adapter.fhir.transform.scripted.program;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.ImmutableProgram;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.ImmutableTrackedEntityType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.scripted.AbstractFhirToDhisTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.trackedentity.ImmutableScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.trackedentity.WritableScriptedTrackedEntityInstance;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class FhirToProgramStageTransformer extends AbstractFhirToDhisTransformer<Event, ProgramStageRule>
{
    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final ProgramMetadataService programMetadataService;

    private final TrackedEntityService trackedEntityService;

    private final ValueConverter valueConverter;

    private final FhirResourceMappingRepository fhirResourceMappingRepository;

    public FhirToProgramStageTransformer( @Nonnull ScriptExecutor scriptExecutor,
        @Nonnull ProgramMetadataService programMetadataService, @Nonnull TrackedEntityMetadataService trackedEntityMetadataService,
        @Nonnull TrackedEntityService trackedEntityService, @Nonnull ValueConverter valueConverter, @Nonnull FhirResourceMappingRepository fhirResourceMappingRepository )
    {
        super( scriptExecutor );
        this.programMetadataService = programMetadataService;
        this.trackedEntityService = trackedEntityService;
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.valueConverter = valueConverter;
        this.fhirResourceMappingRepository = fhirResourceMappingRepository;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.PROGRAM_STAGE_EVENT;
    }

    @Nonnull
    @Override
    public Class<Event> getDhisResourceClass()
    {
        return Event.class;
    }

    @Nonnull
    @Override
    public Class<ProgramStageRule> getRuleClass()
    {
        return ProgramStageRule.class;
    }

    @Nonnull
    @Override
    protected Optional<Event> getResourceById( @Nullable String id ) throws TransformerException
    {
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected Optional<Event> getResourceByIdentifier( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected Optional<Event> getActiveResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return Optional.empty();
    }

    @Nullable
    @Override
    protected Event createResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule, @Nullable String id, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return null;
    }


    @Nullable
    @Override
    public FhirToDhisTransformOutcome<Event> transform( @Nonnull FhirToDhisTransformerContext context, @Nonnull IBaseResource input,
        @Nonnull ProgramStageRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        if ( !addScriptVariables( variables, context, rule ) )
        {
            return null;
        }

        final Optional<Event> event = getResource( context, rule, scriptVariables );
        if ( !event.isPresent() )
        {
            return null;
        }
//        // if rule does not allow automatic creation of events of this type, transformation cannot be performed
//        if ( !mapping.isGenerateEvent() && event.get().isNewResource() )
//        {
//            return null;
//        }
//
//        final ProgramStage programStage = getProgramStage( scriptVariables );
//        arguments.put( ScriptVariable.ENROLLMENT.getVariableName(),
//            new ImmutableScriptedEnrollment( new WritableScriptedEnrollment( event.get().getEnrollment(), valueConverter ) ) );
//
//        final WritableScriptedEvent scriptedEvent = new WritableScriptedEvent( programStage, event.get(), valueConverter );
//        arguments.put( ScriptVariable.OUTPUT.getVariableName(), scriptedEvent );
//
//        if ( !transform( mapping, arguments ) )
//        {
//            return null;
//        }
//        scriptedEvent.validate();
//
//        // as soon as data values have been entered an event date must be set
//        if ( scriptedEvent.isAnyDataValueModified() && (scriptedEvent.getEventDate() == null) )
//        {
//            event.get().setStatus( EventStatus.ACTIVE );
//            event.get().setEventDate( getEventDate( getFhirResourceMapping( mapping ), event.get().getEnrollment(), arguments ) );
//            event.get().setModified( true );
//        }
//
//        return new FhirToDhisTransformOutcome<>( event.get().getId(), event.get() );
        return null;
    }

    protected boolean addScriptVariables( @Nonnull Map<String, Object> variables, @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule ) throws TransformerException
    {
        final FhirResourceMapping fhirResourceMapping = getFhirResourceMapping( rule );

        final TrackedEntityInstance trackedEntityInstance = getTrackedEntityInstance( context, fhirResourceMapping, variables );
        if ( trackedEntityInstance == null )
        {
            // without a tracked entity instance transformation cannot continue
            return false;
        }
        final TrackedEntityType trackedEntityType = trackedEntityMetadataService.getTypeById( trackedEntityInstance.getTypeId() )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity instance " + trackedEntityInstance.getId() + " references type " + trackedEntityInstance.getTypeId() + " that does not exist." ) );
        variables.put( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName(), new ImmutableTrackedEntityType( trackedEntityType ) );
        variables.put( ScriptVariable.TRACKED_ENTITY_INSTANCE.getVariableName(),
            new ImmutableScriptedTrackedEntityInstance( new WritableScriptedTrackedEntityInstance( trackedEntityType, trackedEntityInstance, valueConverter ) ) );

        final Program program = programMetadataService.getProgram( rule.getProgram().getProgramReference() ).map( ImmutableProgram::new )
            .orElseThrow( () -> new TransformerMappingException( "Mapping " + rule + " requires program \"" + rule.getProgram().getProgramReference() + "\" that does not exist." ) );
        if ( !trackedEntityInstance.getTypeId().equals( program.getTrackedEntityTypeId() ) )
        {
            // referenced tracked entity instance type must match type that is required by program
            return false;
        }
        variables.put( ScriptVariable.PROGRAM.getVariableName(), program );

        final ProgramStage programStage =
            program.getOptionalStage( rule.getProgramStageReference() ).orElseThrow( () -> new TransformerMappingException( "Mapping " + rule + " requires program stage \"" +
                rule.getProgramStageReference() + "\" that is not included in program \"" + rule.getProgramStageReference() + "\"." ) );
        variables.put( ScriptVariable.PROGRAM_STAGE.getVariableName(), programStage );

        // is applicable for further processing
        return true;
    }

//    @Nonnull
//    @Override
//    protected Optional<Event> getResourceById( @Nullable String id ) throws TransformerException
//    {
//        // since only immunizations are supported at the moment, resolving resources by ID is not supported
//        return Optional.empty();
//    }
//
//    @Nonnull
//    @Override
//    protected Optional<Event> getResourceByIdentifier(
//        @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule mapping, @Nullable String identifier, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
//    {
//        // since only immunizations are supported at the moment, resolving resources by ID is not supported
//        return Optional.empty();
//    }
//
//    @Nonnull
//    @Override
//    protected Optional<Event> getActiveResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule mapping, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
//    {
//        return getEventInfo( FhirToProgramStageTransformer::isEditableEvent, scriptVariables ).getEvent();
//    }
//
//    @Nullable
//    @Override
//    protected Event createResource(
//        @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule mapping, @Nullable String id, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
//    {
//        final EventInfo eventInfo = getEventInfo( e -> e.getStatus() != EventStatus.COMPLETED, scriptVariables );
//
//        // repeated events are not supported currently
//        if ( eventInfo.getEvent().isPresent() || eventInfo.isCompletedEvents() )
//        {
//            return null;
//        }
//
//        final FhirResourceMapping fhirResourceMapping = getFhirResourceMapping( mapping );
//        Enrollment enrollment = eventInfo.getEnrollment().orElse( null );
//        if ( enrollment == null )
//        {
//            if ( mapping.getAutomatedEnrollment() == null )
//            {
//                return null;
//            }
//
//            if ( !transform( mapping.getAutomatedEnrollment().getApplicableScript(), scriptVariables ) )
//            {
//                return null;
//            }
//
//            enrollment = new Enrollment( true );
//            enrollment.setProgramId( eventInfo.getProgram().getId() );
//            enrollment.setTrackedEntityInstanceId( eventInfo.getTrackedEntityInstance().getId() );
//            enrollment.setStatus( EnrollmentStatus.ACTIVE );
//
//            final WritableScriptedEnrollment scriptedEnrollment = new WritableScriptedEnrollment( enrollment, valueConverter );
//            final Map<String, Object> arguments = new HashMap<>( scriptVariables );
//            arguments.put( TransformerScriptConstants.OUTPUT_ATTR_NAME, scriptedEnrollment );
//            if ( !transform( mapping.getAutomatedEnrollment().getTransformScript(), arguments ) )
//            {
//                return null;
//            }
//            scriptedEnrollment.validate();
//        }
//
//        final Event event = new Event( true );
//        event.setEnrollment( enrollment );
//        event.setOrgUnitId( getEnrolledOrgUnitId( fhirResourceMapping, enrollment, scriptVariables ) );
//        event.setProgramId( eventInfo.getProgram().getId() );
//        event.setProgramStageId( eventInfo.getProgramStage().getId() );
//        event.setTrackedEntityInstanceId( eventInfo.getTrackedEntityInstance().getId() );
//        event.setStatus( EventStatus.ACTIVE );
//        event.setEventDate( getEventDate( fhirResourceMapping, enrollment, scriptVariables ) );
//        return event;
//    }

    protected FhirResourceMapping getFhirResourceMapping( @Nonnull ProgramStageRule rule )
    {
        return fhirResourceMappingRepository.getByFhirResourceType( rule.getFhirResourceType() )
            .orElseThrow( () -> new FatalTransformerException( "No FHIR resource mapping has been defined for " + rule.getFhirResourceType() + "." ) );
    }

//    @Nonnull protected
//    TrackedEntityType getTrackedEntityType( @Nonnull Map<String, Object> scriptVariables ) throws FatalTransformerException
//    {
//        final TrackedEntityType trackedEntityType = (TrackedEntityType) scriptVariables.get( ScriptVariable.TRACKED_ENTITY_TYPE );
//        if ( trackedEntityType == null )
//        {
//            throw new FatalTransformerException( "Tracked entity type is not included as script variables." );
//        }
//        return trackedEntityType;
//    }

    @Nullable
    protected TrackedEntityInstance getTrackedEntityInstance( @Nonnull FhirToDhisTransformerContext context, @Nonnull FhirResourceMapping fhirResourceMapping, @Nonnull Map<String, Object> variables )
    {
        final String trackedEntityInstanceId = getTrackedEntityInstanceId( context, fhirResourceMapping, variables );
        if ( trackedEntityInstanceId == null )
        {
            return null;
        }
        return trackedEntityService.getById( trackedEntityInstanceId ).orElse( null );
    }

    @Nullable
    protected String getTrackedEntityInstanceId( @Nonnull FhirToDhisTransformerContext context, @Nonnull FhirResourceMapping resourceMapping, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return getScriptExecutor().execute( resourceMapping.getTeiIdLookupScript(), context.getFhirRequest().getVersion(), scriptVariables, String.class );
    }

    //    @Nullable protected
//    String getEnrolledOrgUnitId( @Nonnull FhirResourceMapping resourceMapping, @Nonnull Enrollment enrollment, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
//    {
//        final Map<String, Object> arguments = new HashMap<>( scriptVariables );
//        arguments.put( ScriptVariable.ENROLLMENT.getVariableName(),
//            new ImmutableScriptedEnrollment( new WritableScriptedEnrollment( enrollment, valueConverter ) ) );
//
//        try
//        {
//            final Object result = getScriptExecutor().evaluate( new StaticScriptSource(
//                resourceMapping.getEnrollmentOrgLookupScript() ), new HashMap<>( arguments ) );
//            return (result == null) ? enrollment.getId() : result.toString();
//        }
//        catch ( ScriptCompilationException e )
//        {
//            throw new TransformerScriptException( "Enrolled organization unit ID lookup script of FHIR resource mapping " +
//                resourceMapping.getFhirResourceType() + " caused an error: " + e.getMessage(), e );
//        }
//    }
//
//    private @Nonnull
//    EventInfo getEventInfo( @Nonnull Predicate<Event> predicate, @Nonnull Map<String, Object> scriptVariables )
//    {
//        final Program program = (Program) scriptVariables.get( ScriptVariable.PROGRAM.getVariableName() );
//        if ( program == null )
//        {
//            throw new FatalTransformerException( "MappedTrackerProgram is not included as script argument." );
//        }
//        final ProgramStage programStage = getProgramStage( scriptVariables );
//
//        final TrackedEntityType trackedEntityType = (TrackedEntityType) scriptVariables.get( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName() );
//        if ( trackedEntityType == null )
//        {
//            throw new FatalTransformerException( "Tracked entity type is not included as script argument." );
//        }
//        final ScriptedTrackedEntityInstance trackedEntityInstance = (ScriptedTrackedEntityInstance) scriptVariables.get( ScriptVariable.TRACKED_ENTITY_INSTANCE.getVariableName() );
//        if ( trackedEntityInstance == null )
//        {
//            throw new FatalTransformerException( "Tracked entity instance is not included as script argument." );
//        }
//
//        // automatic enrollment is not available currently
//        final Enrollment enrollment = enrollmentService.getLatestActive( program.getId(),
//            Objects.requireNonNull( trackedEntityInstance.getId() ) ).orElse( null );
//        Event event = null;
//        boolean completedEvents = false;
//        if ( enrollment != null )
//        {
//            final List<Event> events = eventService.find( program.getId(), programStage.getId(), enrollment.getId(), trackedEntityInstance.getId() );
//            event = events.stream().filter( predicate ).sorted().findFirst().map( e -> {
//                e.setEnrollment( enrollment );
//                return e;
//            } ).orElse( null );
//            completedEvents = events.stream().anyMatch( e -> e.getStatus() == EventStatus.COMPLETED );
//        }
//        return new EventInfo( program, programStage, trackedEntityType, trackedEntityInstance, enrollment, event, completedEvents );
//    }
//
//    protected boolean transform( @Nonnull String script, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
//    {
//        try
//        {
//            final Object result = getScriptExecutor().evaluate( new StaticScriptSource( script ), new HashMap<>( scriptVariables ) );
//            if ( !(result instanceof Boolean) )
//            {
//                throw new TransformerScriptException( "Script did not return a boolean value." );
//            }
//            return (boolean) result;
//        }
//        catch ( ScriptCompilationException e )
//        {
//            throw new TransformerScriptException( "Script caused an error: " + e.getMessage(), e );
//        }
//    }
//
    @Nonnull
    private static ProgramStage getProgramStage( @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final ProgramStage programStage = (ProgramStage) scriptVariables.get( ScriptVariable.PROGRAM_STAGE.getVariableName() );
        if ( programStage == null )
        {
            throw new FatalTransformerException( "MappedTrackerProgram stage is not included as script variables." );
        }
        return programStage;
    }

//    private static boolean isEditableEvent( @Nonnull Event e )
//    {
//        return (e.getStatus() == EventStatus.ACTIVE) || (e.getStatus() == EventStatus.SCHEDULE) || (e.getStatus() == EventStatus.OVERDUE) || (e.getStatus() == EventStatus.VISITED);
//    }
}
