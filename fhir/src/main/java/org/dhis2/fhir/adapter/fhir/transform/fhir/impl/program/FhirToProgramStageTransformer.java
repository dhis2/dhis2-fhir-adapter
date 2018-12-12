package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.program;

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
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganisationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganisationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramTrackedEntityAttribute;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgram;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirResourceMappingRepository;
import org.dhis2.fhir.adapter.fhir.model.EventDecisionType;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.AbstractFhirToDhisTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ImmutableScriptedEnrollment;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedEnrollment;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEnrollment;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedEvent;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.spring.StaticObjectProvider;
import org.dhis2.fhir.adapter.util.DateTimeUtils;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FhirToProgramStageTransformer extends AbstractFhirToDhisTransformer<Event, ProgramStageRule>
{
    private final LockManager lockManager;

    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final ProgramMetadataService programMetadataService;

    private final EnrollmentService enrollmentService;

    private final EventService eventService;

    private final FhirResourceMappingRepository resourceMappingRepository;

    private final ValueConverter valueConverter;

    private final ZoneId zoneId = ZoneId.systemDefault();

    public FhirToProgramStageTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull OrganisationUnitService organisationUnitService, @Nonnull LockManager lockManager,
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull ProgramMetadataService programMetadataService, @Nonnull EnrollmentService enrollmentService, @Nonnull EventService eventService,
        @Nonnull FhirResourceMappingRepository resourceMappingRepository, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutor, organisationUnitService, new StaticObjectProvider<>( trackedEntityService ) );
        this.lockManager = lockManager;
        this.programMetadataService = programMetadataService;
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.enrollmentService = enrollmentService;
        this.eventService = eventService;
        this.resourceMappingRepository = resourceMappingRepository;
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

    @Nullable
    @Override
    public FhirToDhisTransformOutcome<Event> transform( @Nonnull FhirToDhisTransformerContext context, @Nonnull IBaseResource input,
        @Nonnull ProgramStageRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( !rule.getProgramStage().isEnabled() || !rule.getProgramStage().getProgram().isEnabled() )
        {
            logger.debug( "Ignoring not enabled program stage \"{}\" of program \"{}\".",
                rule.getProgramStage().getName(), rule.getProgramStage().getProgram().getName() );
            return null;
        }

        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        addBasicScriptVariables( variables, rule );
        final FhirResourceMapping resourceMapping = getResourceMapping( rule );
        final TrackedEntityInstance trackedEntityInstance = getTrackedEntityInstance( context,
            rule.getProgramStage().getProgram().getTrackedEntityRule(), resourceMapping, variables, false ).orElse( null );
        if ( trackedEntityInstance == null )
        {
            return null;
        }

        addScriptVariables( context, variables, rule, trackedEntityInstance );
        final Event event = getResource( context, rule, variables ).orElse( null );
        if ( event == null )
        {
            return null;
        }
        rule.getEventStatusUpdate().update( event );

        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class );
        final ProgramStage programStage = TransformerUtils.getScriptVariable( variables, ScriptVariable.PROGRAM_STAGE, ProgramStage.class );
        final List<WritableScriptedEvent> scriptedProgramStageEvents = createScriptedProgramStageEvents( context, programStage, event.getEnrollment().getEvents(), scriptedTrackedEntityInstance );
        variables.put( ScriptVariable.PROGRAM_STAGE_EVENTS.getVariableName(), scriptedProgramStageEvents );

        final Program program = TransformerUtils.getScriptVariable( variables, ScriptVariable.PROGRAM, Program.class );
        final WritableScriptedEnrollment scriptedEnrollment = new WritableScriptedEnrollment( program, event.getEnrollment(), scriptedTrackedEntityInstance, valueConverter );
        variables.put( ScriptVariable.ENROLLMENT.getVariableName(), scriptedEnrollment );
        if ( !event.isNewResource() && !beforeEnrollmentEvent( context, rule, programStage, event.getEnrollment(), scriptVariables ) )
        {
            return null;
        }

        if ( !isStatusApplicable( rule, event ) )
        {
            return null;
        }
        if ( !isEffectiveDateApplicable( context, resourceMapping, rule, programStage, event, variables ) )
        {
            return null;
        }

        final WritableScriptedEvent scriptedEvent = new WritableScriptedEvent( context, programStage, event, scriptedTrackedEntityInstance, valueConverter );
        variables.put( ScriptVariable.OUTPUT.getVariableName(), scriptedEvent );

        updateEventDate( context, rule, resourceMapping, event.getEnrollment(), programStage, event, variables );
        if ( !transform( context, rule, variables ) )
        {
            return null;
        }
        updateCoordinates( context, resourceMapping, program, scriptedEnrollment, programStage, scriptedEvent, variables );

        if ( !afterEvent( context, rule, programStage, scriptVariables ) ||
            !afterEnrollmentEvent( context, rule, programStage, scriptVariables ) )
        {
            return null;
        }

        if ( trackedEntityInstance.isModified() )
        {
            TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class ).validate();
        }
        event.getEnrollment().setTrackedEntityInstance( trackedEntityInstance );
        if ( event.getEnrollment().isModified() )
        {
            scriptedEnrollment.validate();
        }
        event.setTrackedEntityInstance( trackedEntityInstance );
        scriptedProgramStageEvents.stream().filter( se -> se.isNewResource() || se.isModified() || se.isAnyDataValueModified() ).forEach( WritableScriptedEvent::validate );

        return new FhirToDhisTransformOutcome<>( event );
    }

    protected void addBasicScriptVariables( @Nonnull Map<String, Object> variables, @Nonnull ProgramStageRule rule ) throws TransformerException
    {
        final Program program = programMetadataService.findProgramByReference( rule.getProgramStage().getProgram().getProgramReference() )
            .orElseThrow( () -> new TransformerMappingException( "Mapping " + rule + " requires program \"" +
                rule.getProgramStage().getProgram().getProgramReference() + "\" that does not exist." ) );
        variables.put( ScriptVariable.PROGRAM.getVariableName(), program );

        final TrackedEntityAttributes attributes = trackedEntityMetadataService.getAttributes();
        final TrackedEntityType trackedEntityType = trackedEntityMetadataService
            .findTypeByReference( new Reference( program.getTrackedEntityTypeId(), ReferenceType.ID ) )
            .orElseThrow( () -> new TransformerMappingException( "Program \"" + program.getName() +
                "\" references tracked entity type " + program.getTrackedEntityTypeId() + " that does not exist." ) );
        variables.put( ScriptVariable.TRACKED_ENTITY_ATTRIBUTES.getVariableName(), attributes );
        variables.put( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName(), trackedEntityType );
    }

    protected void addScriptVariables( @Nonnull FhirToDhisTransformerContext context, @Nonnull Map<String, Object> variables, @Nonnull ProgramStageRule rule,
        @Nonnull TrackedEntityInstance trackedEntityInstance ) throws TransformerException
    {
        if ( trackedEntityInstance.getIdentifier() == null )
        {
            throw new FatalTransformerException( "Identifier of tracked entity instance has not yet been set." );
        }
        variables.put( ScriptVariable.TRACKED_ENTITY_INSTANCE.getVariableName(), new WritableScriptedTrackedEntityInstance(
            TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_ATTRIBUTES, TrackedEntityAttributes.class ),
            TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class ),
            trackedEntityInstance, valueConverter ) );

        final Program program = TransformerUtils.getScriptVariable( variables, ScriptVariable.PROGRAM, Program.class );
        final ProgramStage programStage =
            program.getOptionalStage( rule.getProgramStage().getProgramStageReference() ).orElseThrow( () -> new TransformerMappingException( "Rule " + rule + " requires program stage \"" +
                rule.getProgramStage().getProgramStageReference() + "\" that is not included in program \"" + rule.getProgramStage().getProgram().getName() + "\"." ) );
        variables.put( ScriptVariable.PROGRAM_STAGE.getVariableName(), programStage );
    }

    protected void updateCoordinates( @Nonnull FhirToDhisTransformerContext context, @Nonnull FhirResourceMapping resourceMapping,
        @Nonnull Program program, @Nonnull WritableScriptedEnrollment scriptedEnrollment,
        @Nonnull ProgramStage programStage, @Nonnull WritableScriptedEvent scriptedEvent, @Nonnull Map<String, Object> scriptVariables )
    {
        if ( program.isCaptureCoordinates() && (scriptedEnrollment.getCoordinate() == null) )
        {
            scriptedEnrollment.setCoordinate( getCoordinate( context, resourceMapping.getEnrollmentLocationLookupScript(), scriptVariables ) );
        }
        if ( programStage.isCaptureCoordinates() && (scriptedEvent.getCoordinate() == null) )
        {
            scriptedEvent.setCoordinate( getCoordinate( context, resourceMapping.getEventLocationLookupScript(), scriptVariables ) );
        }
    }

    @Override
    protected boolean isSyncRequired( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return true;
    }

    @Nonnull
    @Override
    protected Optional<Event> getResourceById( @Nullable String id ) throws TransformerException
    {
        // resolving resources by technical ID is not supported
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected Optional<Event> getActiveResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule,
        @Nonnull Map<String, Object> scriptVariables, boolean sync ) throws TransformerException
    {
        final EventInfo eventInfo = getEventInfo( scriptVariables, sync );
        if ( eventInfo.getEvents().isEmpty() )
        {
            return Optional.empty();
        }

        final Event event = eventInfo.getEvents().stream().findFirst().orElseThrow( () -> new IllegalStateException( "Events have not been populated." ) );
        if ( rule.getProgramStage().getBeforeScript() != null )
        {
            final Enrollment enrollment = eventInfo.getEnrollment().orElseThrow(
                () -> new TransformerMappingException( "Enrolled events do not have an enrollment." ) );

            final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class );
            final Map<String, Object> variables = createResourceVariables( context, scriptVariables, eventInfo, enrollment );

            final FhirResourceMapping resourceMapping = getResourceMapping( rule );
            final Optional<OrganisationUnit> orgUnit = getEventOrgUnit( context, resourceMapping, enrollment, variables );
            variables.put( ScriptVariable.ORGANIZATION_UNIT_ID.getVariableName(), orgUnit.map( OrganisationUnit::getId ).orElse( null ) );

            final EventDecisionType eventDecisionType = getScriptExecutor().execute( rule.getProgramStage().getBeforeScript(), context.getFhirRequest().getVersion(),
                variables, EventDecisionType.class );
            if ( (eventDecisionType == null) || (eventDecisionType == EventDecisionType.BREAK) )
            {
                logger.info( "Processing of event for program stage \"{}\" has been cancelled by event script after execution. " +
                    "It will be tried to create a new one.", eventInfo.getProgramStage().getName() );
                return Optional.empty();
            }
            if ( (eventDecisionType == EventDecisionType.NEW_EVENT) && eventInfo.getProgramStage().isRepeatable() )
            {
                // it will be tried to create a new one
                return Optional.empty();
            }

            if ( enrollment.isModified() )
            {
                TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.ENROLLMENT, ScriptedEnrollment.class ).validate();
            }
        }
        return Optional.of( event );
    }

    @Nullable
    @Override
    protected Event createResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule,
        @Nullable String id, @Nonnull Map<String, Object> scriptVariables, boolean sync ) throws TransformerException
    {
        if ( context.isCreationDisabled() || !rule.isEventCreationEnabled() || !rule.getProgramStage().isCreationEnabled() )
        {
            return null;
        }

        final EventInfo eventInfo = getEventInfo( scriptVariables, sync );
        // creation of event may not be applicable
        if ( (rule.getProgramStage().getCreationApplicableScript() != null) && !Boolean.TRUE.equals( getScriptExecutor().execute(
            rule.getProgramStage().getCreationApplicableScript(), context.getFhirRequest().getVersion(), scriptVariables, Boolean.class ) ) )
        {
            logger.info( "Creation of program stage instance \"{}\" is not applicable.", eventInfo.getProgramStage().getName() );
            return null;
        }

        final FhirResourceMapping resourceMapping = getResourceMapping( rule );
        final Enrollment enrollment = eventInfo.getEnrollment().orElseGet( () -> createEnrollment( context, rule, resourceMapping, scriptVariables ) );
        if ( enrollment == null )
        {
            return null;
        }

        // without an organization unit no event can be created
        final Optional<OrganisationUnit> orgUnit = getEventOrgUnit( context, resourceMapping, enrollment, scriptVariables );
        if ( !orgUnit.isPresent() )
        {
            return null;
        }

        final Map<String, Object> variables = createResourceVariables( context, scriptVariables, eventInfo, enrollment );
        if ( !beforeEnrollmentEvent( context, rule, eventInfo.getProgramStage(), enrollment, scriptVariables ) )
        {
            return null;
        }

        final Event event = new Event( true );
        event.setStatus( rule.getProgramStage().getCreationStatus() );
        event.setEnrollment( enrollment );
        event.setOrgUnitId( orgUnit.get().getId() );
        event.setProgramId( eventInfo.getProgram().getId() );
        event.setProgramStageId( eventInfo.getProgramStage().getId() );
        event.setTrackedEntityInstanceId( eventInfo.getTrackedEntityInstance().getId() );

        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class );
        final WritableScriptedEvent scriptedEvent = new WritableScriptedEvent( context, eventInfo.getProgramStage(), event, scriptedTrackedEntityInstance, valueConverter );
        variables.put( ScriptVariable.EVENT.getVariableName(), scriptedEvent );

        final ZonedDateTime eventDate;
        if ( rule.getProgramStage().isEventDateIsIncident() && !eventInfo.getProgramStage().isGeneratedByEnrollmentDate() )
        {
            eventDate = enrollment.getIncidentDate().plusDays( eventInfo.getProgramStage().getMinDaysFromStart() );
        }
        else
        {
            eventDate = getEventDate( context, rule, resourceMapping, enrollment, eventInfo.getProgramStage(), variables );
            if ( eventDate == null )
            {
                return null;
            }
        }
        event.setEventDate( eventDate );
        event.setDueDate( Stream.of( enrollment.getIncidentDate().plusDays( eventInfo.getProgramStage().getMinDaysFromStart() ), eventDate )
            .max( Comparator.naturalOrder() ).get() );

        if ( (rule.getProgramStage().getCreationScript() != null) && !Boolean.TRUE.equals( getScriptExecutor().execute(
            rule.getProgramStage().getCreationScript(), context.getFhirRequest().getVersion(), variables, Boolean.class ) ) )
        {
            logger.info( "Creation of program stage instance \"{}\" has been cancelled by creation script.",
                eventInfo.getProgramStage().getName() );
            return null;
        }
        if ( event.getStatus() == null )
        {
            event.setStatus( EventStatus.ACTIVE );
        }

        final List<Event> events = new ArrayList<>( enrollment.getEvents() );
        events.add( event );
        enrollment.setEvents( events );

        if ( enrollment.isModified() )
        {
            TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.ENROLLMENT, ScriptedEnrollment.class ).validate();
        }
        scriptedEvent.validate();
        return event;
    }

    @Nonnull
    private Map<String, Object> createResourceVariables( @Nonnull FhirToDhisTransformerContext context, @Nonnull Map<String, Object> scriptVariables, @Nonnull EventInfo eventInfo, @Nonnull Enrollment enrollment )
    {
        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class );
        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        final Program program = eventInfo.getProgram();
        final ProgramStage programStage = eventInfo.getProgramStage();
        final WritableScriptedEnrollment scriptedEnrollment = new WritableScriptedEnrollment( program, enrollment, scriptedTrackedEntityInstance, valueConverter );
        final List<WritableScriptedEvent> scriptedProgramStageEvents = createScriptedProgramStageEvents( context, programStage, enrollment.getEvents(), scriptedTrackedEntityInstance );
        variables.put( ScriptVariable.ENROLLMENT.getVariableName(), scriptedEnrollment );
        variables.put( ScriptVariable.PROGRAM_STAGE_EVENTS.getVariableName(), scriptedProgramStageEvents );
        return variables;
    }

    protected void updateEventDate( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule, @Nonnull FhirResourceMapping resourceMapping, @Nonnull Enrollment enrollment,
        @Nonnull ProgramStage programStage, @Nonnull Event event, @Nonnull Map<String, Object> variables )
    {
        if ( rule.isUpdateEventDate() && !programStage.isGeneratedByEnrollmentDate() )
        {
            final ZonedDateTime eventDate = getEventDate( context, rule, resourceMapping, enrollment, programStage, variables );
            if ( (eventDate != null) && !Objects.equals( event.getEventDate(), eventDate ) )
            {
                event.setEventDate( eventDate );
                event.setModified( true );
                logger.info( "Updated event date to {}.", eventDate );
            }
        }
    }

    private ZonedDateTime getEventDate( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule, @Nonnull FhirResourceMapping resourceMapping,
        @Nonnull Enrollment enrollment, @Nonnull ProgramStage programStage, @Nonnull Map<String, Object> variables )
    {
        ZonedDateTime eventDate;
        if ( programStage.isGeneratedByEnrollmentDate() )
        {
            eventDate = enrollment.getEnrollmentDate().plusDays( programStage.getMinDaysFromStart() );
        }
        else
        {
            eventDate = valueConverter.convert( getScriptExecutor().execute( resourceMapping.getEventDateLookupScript(),
                context.getFhirRequest().getVersion(), variables, Object.class ), ValueType.DATETIME, ZonedDateTime.class );
        }
        if ( eventDate == null )
        {
            final IAnyResource resource = TransformerUtils.getScriptVariable( variables, ScriptVariable.INPUT, IAnyResource.class );
            if ( (resource.getMeta() != null) && (resource.getMeta().getLastUpdated() != null) )
            {
                logger.info( "Event date of program stage instance \"{}\" has not been returned " +
                    "by event date script (using last updated timestamp).", programStage.getName() );
                eventDate = ZonedDateTime.ofInstant( resource.getMeta().getLastUpdated().toInstant(), zoneId );
            }
        }
        if ( eventDate == null )
        {
            logger.info( "Event date of program stage instance \"{}\" has not been returned " +
                "by event date script (using current timestamp).", programStage.getName() );
            eventDate = ZonedDateTime.now();
        }
        return eventDate;
    }

    @Nullable
    protected Enrollment createEnrollment( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule, @Nonnull FhirResourceMapping resourceMapping,
        @Nonnull Map<String, Object> scriptVariables )
    {
        if ( context.isCreationDisabled() || !rule.isEnrollmentCreationEnabled() )
        {
            return null;
        }

        final MappedTrackerProgram mappedProgram = rule.getProgramStage().getProgram();
        if ( !mappedProgram.isCreationEnabled() )
        {
            logger.info( "Creation of program stage has not been enabled." );
            return null;
        }

        final Program program = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.PROGRAM, Program.class );
        if ( !program.isRegistration() )
        {
            throw new TransformerMappingException( "Cannot create enrollment into program \"" + program.getName() +
                "\" since program is without registration." );
        }

        final ZonedDateTime enrollmentDate = getEnrollmentDate( context, resourceMapping, program, scriptVariables );
        if ( enrollmentDate == null )
        {
            return null;
        }

        // creation of event may not be applicable
        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        variables.put( ScriptVariable.DATE_TIME.getVariableName(), enrollmentDate );
        if ( (mappedProgram.getCreationApplicableScript() != null) && !Boolean.TRUE.equals( getScriptExecutor().execute(
            mappedProgram.getCreationApplicableScript(), context.getFhirRequest().getVersion(), variables, Boolean.class ) ) )
        {
            logger.info( "Creation of program instance \"{}\" is not applicable.", mappedProgram.getName() );
            return null;
        }

        final Optional<OrganisationUnit> organisationUnit = getOrgUnit( context, resourceMapping.getEnrollmentOrgLookupScript(), variables );
        if ( !organisationUnit.isPresent() )
        {
            return null;
        }

        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class );
        final Enrollment enrollment = new Enrollment( true );
        enrollment.setProgramId( program.getId() );
        enrollment.setTrackedEntityInstanceId( scriptedTrackedEntityInstance.getId() );
        enrollment.setOrgUnitId( organisationUnit.get().getId() );
        enrollment.setEnrollmentDate( enrollmentDate );

        final WritableScriptedEnrollment scriptedEnrollment = new WritableScriptedEnrollment( program, enrollment, scriptedTrackedEntityInstance, valueConverter );
        variables.put( ScriptVariable.ENROLLMENT.getVariableName(), scriptedEnrollment );

        if ( (mappedProgram.getCreationScript() != null) && !Boolean.TRUE.equals( getScriptExecutor().execute(
            mappedProgram.getCreationScript(), context.getFhirRequest().getVersion(), variables, Boolean.class ) ) )
        {
            logger.info( "Creation of program instance \"{}\" has been cancelled by creation script.", program.getName() );
            return null;
        }

        if ( !updateIncidentDate( program, enrollment ) )
        {
            return null;
        }
        if ( rule.getProgramStage().getProgram().isEnrollmentDateIsIncident() )
        {
            enrollment.setEnrollmentDate( enrollment.getIncidentDate() );
        }
        if ( enrollment.getStatus() == null )
        {
            enrollment.setStatus( EnrollmentStatus.ACTIVE );
        }

        // enrollment may require more tracked entity attributes
        if ( !initAndValidateTrackedEntity( program, variables ) )
        {
            return null;
        }

        scriptedEnrollment.validate();
        return enrollment;
    }

    protected boolean beforeEnrollmentEvent( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule, @Nonnull ProgramStage programStage, @Nonnull Enrollment enrollment, @Nonnull Map<String, Object> scriptVariables )
    {
        if ( enrollment.isNewResource() )
        {
            return true;
        }
        if ( (rule.getProgramStage().getProgram().getBeforeScript() != null) && !Boolean.TRUE.equals( getScriptExecutor().execute(
            rule.getProgramStage().getProgram().getBeforeScript(), context.getFhirRequest().getVersion(), scriptVariables, Boolean.class ) ) )
        {
            logger.info( "Processing of event for program stage \"{}\" has been cancelled by enrollment script before execution.", programStage.getName() );
            return false;
        }
        return true;
    }

    protected boolean afterEvent( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule, @Nonnull ProgramStage programStage, @Nonnull Map<String, Object> scriptVariables )
    {
        if ( (rule.getProgramStage().getAfterScript() != null) && !Boolean.TRUE.equals( getScriptExecutor().execute(
            rule.getProgramStage().getAfterScript(), context.getFhirRequest().getVersion(), scriptVariables, Boolean.class ) ) )
        {
            logger.info( "Processing of event for program stage \"{}\" has been cancelled by event script after execution.", programStage.getName() );
            return false;
        }
        return true;
    }

    protected boolean afterEnrollmentEvent( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule, @Nonnull ProgramStage programStage, @Nonnull Map<String, Object> scriptVariables )
    {
        if ( (rule.getProgramStage().getProgram().getAfterScript() != null) && !Boolean.TRUE.equals( getScriptExecutor().execute(
            rule.getProgramStage().getProgram().getAfterScript(), context.getFhirRequest().getVersion(), scriptVariables, Boolean.class ) ) )
        {
            logger.info( "Processing of event for program stage \"{}\" has been cancelled by enrollment script after execution.", programStage.getName() );
            return false;
        }
        return true;
    }

    @Nonnull
    protected List<WritableScriptedEvent> createScriptedProgramStageEvents( @Nonnull FhirToDhisTransformerContext transformerContext, @Nonnull ProgramStage programStage, @Nonnull List<Event> events,
        @Nonnull ScriptedTrackedEntityInstance scriptedTrackedEntityInstance )
    {
        return events.stream().filter( e -> programStage.getId().equals( e.getProgramStageId() ) ).sorted( new EventComparator() )
            .map( e -> new WritableScriptedEvent( transformerContext, programStage, e, scriptedTrackedEntityInstance, valueConverter ) ).collect( Collectors.toList() );
    }

    protected boolean initAndValidateTrackedEntity( @Nonnull Program program, @Nonnull Map<String, Object> variables )
    {
        final WritableScriptedTrackedEntityInstance trackedEntityInstance = TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_INSTANCE, WritableScriptedTrackedEntityInstance.class );
        for ( final ProgramTrackedEntityAttribute attribute : program.getTrackedEntityAttributes() )
        {
            trackedEntityInstance.initValue( Reference.createIdReference( attribute.getAttributeId() ) );
            if ( !attribute.isAllowFutureDate() && ValueType.DATE_TYPES.contains( attribute.getValueType() ) )
            {
                final Temporal temporal = valueConverter.convert( trackedEntityInstance.getValue( Reference.createIdReference( attribute.getAttributeId() ) ), attribute.getValueType(), Temporal.class );
                if ( (temporal != null) && DateTimeUtils.isFutureDate( temporal ) )
                {
                    logger.info( "Tracked entity attribute \"" + attribute.getAttribute().getName() + "\" contains future date, which is not allowed by program \"" + program.getName() + "\"." );
                    return false;
                }
            }
            if ( attribute.isMandatory() && (trackedEntityInstance.getValue( Reference.createIdReference( attribute.getAttributeId() ) ) == null) )
            {
                logger.info( "Tracked entity attribute \"" + attribute.getAttribute().getName() + "\" is mandatory but not set, which is not allowed by program \"" + program.getName() + "\"." );
                return false;
            }
        }
        return true;
    }

    @Nullable
    protected ZonedDateTime getEnrollmentDate( @Nonnull FhirToDhisTransformerContext context, @Nonnull FhirResourceMapping resourceMapping, @Nonnull Program program, @Nonnull Map<String, Object> scriptVariables )
    {
        ZonedDateTime enrollmentDate = valueConverter.convert( getScriptExecutor().execute( resourceMapping.getEnrollmentDateLookupScript(),
            context.getFhirRequest().getVersion(), scriptVariables, Object.class ), ValueType.DATETIME, ZonedDateTime.class );
        if ( enrollmentDate == null )
        {
            final IAnyResource resource = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, IAnyResource.class );
            if ( (resource.getMeta() != null) && (resource.getMeta().getLastUpdated() != null) )
            {
                logger.info( "Enrollment date of program instance \"{}\" has not been returned by " +
                    "enrollment date script (using last updated timestamp).", program.getName() );
                enrollmentDate = ZonedDateTime.ofInstant( resource.getMeta().getLastUpdated().toInstant(), zoneId );
            }
        }
        if ( enrollmentDate == null )
        {
            logger.info( "Enrollment date of program instance \"{}\" has not been returned by " +
                "enrollment date script (using current timestamp).", program.getName() );
            enrollmentDate = ZonedDateTime.now();
        }
        if ( !program.isSelectEnrollmentDatesInFuture() && DateTimeUtils.isFutureDate( enrollmentDate ) )
        {
            logger.info( "Enrollment date of of program instance \"{}\" is in the future and program does not allow dates in the future.", program.getName() );
            return null;
        }
        return enrollmentDate;
    }

    protected boolean updateIncidentDate( @Nonnull Program program, @Nonnull Enrollment enrollment )
    {
        if ( enrollment.getIncidentDate() == null )
        {
            if ( program.isDisplayIncidentDate() )
            {
                logger.info( "Incident date of program instance \"{}\" has not been returned by " +
                    "event date script (using current timestamp).", program.getName() );
            }
            enrollment.setIncidentDate( ZonedDateTime.now() );
        }
        else if ( !program.isSelectIncidentDatesInFuture() && DateTimeUtils.isFutureDate( enrollment.getIncidentDate() ) )
        {
            logger.info( "Incident date of of program instance \"{}\" is in the future and program does not allow dates in the future.", program.getName() );
            return false;
        }
        return true;
    }

    @Nonnull
    protected FhirResourceMapping getResourceMapping( @Nonnull ProgramStageRule rule )
    {
        return resourceMappingRepository.findByFhirResourceType( rule.getFhirResourceType() )
            .orElseThrow( () -> new FatalTransformerException( "No FHIR resource mapping has been defined for " + rule.getFhirResourceType() + "." ) );
    }

    @Nonnull
    protected Optional<OrganisationUnit> getEventOrgUnit( @Nonnull FhirToDhisTransformerContext context, @Nonnull FhirResourceMapping resourceMapping,
        @Nonnull Enrollment enrollment, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final Program program = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.PROGRAM, Program.class );
        final ScriptedTrackedEntityInstance scriptedTrackedEntityInstance = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class );
        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        variables.put( ScriptVariable.ENROLLMENT.getVariableName(), new ImmutableScriptedEnrollment( new WritableScriptedEnrollment( program, enrollment, scriptedTrackedEntityInstance, valueConverter ) ) );
        return getOrgUnit( context, resourceMapping.getEventOrgLookupScript(), variables );
    }

    @Nonnull
    protected EventInfo getEventInfo( @Nonnull Map<String, Object> scriptVariables, boolean sync )
    {
        final Program program = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.PROGRAM, Program.class );
        final ProgramStage programStage = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.PROGRAM_STAGE, ProgramStage.class );
        final TrackedEntityType trackedEntityType = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class );
        final ScriptedTrackedEntityInstance trackedEntityInstance = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class );

        // Since the lock is required for modifying events, the lock must not be created conditionally for the enrollment.
        // Creating this lock conditionally may result in a deadlock (due to possible reverse order of locking).
        lockManager.getCurrentLockContext().orElseThrow( () -> new FatalTransformerException( "No lock context available." ) )
            .lock( "in-te:" + trackedEntityInstance.getId() );

        final Enrollment enrollment;
        if ( sync )
        {
            enrollment = enrollmentService.findLatestActiveRefreshed( program.getId(), Objects.requireNonNull( trackedEntityInstance.getId() ) ).orElse( null );
        }
        else
        {
            enrollment = enrollmentService.findLatestActive( program.getId(), Objects.requireNonNull( trackedEntityInstance.getId() ) ).orElse( null );
        }

        List<Event> events = Collections.emptyList();
        if ( enrollment != null )
        {
            // find method must not return cached data (if it would, data must be refreshed when invoked in a synchronized call)
            final List<Event> foundEvents = eventService.find( program.getId(), programStage.getId(), enrollment.getId(), trackedEntityInstance.getId() );
            events = foundEvents.stream().peek( e -> e.setEnrollment( enrollment ) ).sorted( Collections.reverseOrder( new EventComparator() ) ).collect( Collectors.toList() );
            enrollment.setEvents( events );
        }
        return new EventInfo( program, programStage, trackedEntityType, trackedEntityInstance, enrollment, events );
    }

    protected boolean isEffectiveDateApplicable( @Nonnull FhirToDhisTransformerContext context, @Nonnull FhirResourceMapping resourceMapping, @Nonnull ProgramStageRule rule,
        @Nonnull ProgramStage programStage, @Nonnull Event event, @Nonnull Map<String, Object> variables )
    {
        if ( (rule.getResultingBeforePeriodDayType() == null) && (rule.getResultingAfterPeriodDayType() == null) )
        {
            return true;
        }

        final LocalDate effectiveDate = getEffectiveDate( context, resourceMapping, variables ).toLocalDate();
        if ( rule.getResultingBeforePeriodDayType() != null )
        {
            final ZonedDateTime periodDay = rule.getResultingBeforePeriodDayType().getDate( programStage, event );
            final int days = (int) Period.between( effectiveDate, periodDay.toLocalDate() ).get( ChronoUnit.DAYS );
            if ( days > rule.getResultingBeforePeriodDays() )
            {
                logger.debug( "Effective date {} is {} days before {}.", effectiveDate, days, periodDay );
                return false;
            }
        }
        if ( rule.getResultingAfterPeriodDayType() != null )
        {
            final ZonedDateTime periodDay = rule.getResultingAfterPeriodDayType().getDate( programStage, event );
            final int days = (int) Period.between( periodDay.toLocalDate(), effectiveDate ).get( ChronoUnit.DAYS );
            if ( days > rule.getResultingAfterPeriodDays() )
            {
                logger.debug( "Effective date {} is {} days after {}.", effectiveDate, days, periodDay );
                return false;
            }
        }
        return true;
    }

    @Nonnull
    protected ZonedDateTime getEffectiveDate( @Nonnull FhirToDhisTransformerContext context, @Nonnull FhirResourceMapping resourceMapping, @Nonnull Map<String, Object> variables )
    {
        ZonedDateTime effectiveDate;
        effectiveDate = valueConverter.convert( getScriptExecutor().execute( resourceMapping.getEffectiveDateLookupScript(),
            context.getFhirRequest().getVersion(), variables, Object.class ), ValueType.DATETIME, ZonedDateTime.class );
        if ( effectiveDate == null )
        {
            final IAnyResource resource = TransformerUtils.getScriptVariable( variables, ScriptVariable.INPUT, IAnyResource.class );
            if ( (resource.getMeta() != null) && (resource.getMeta().getLastUpdated() != null) )
            {
                effectiveDate = ZonedDateTime.ofInstant( resource.getMeta().getLastUpdated().toInstant(), zoneId );
            }
        }
        if ( effectiveDate == null )
        {
            effectiveDate = ZonedDateTime.now();
        }
        return effectiveDate;
    }

    protected boolean isStatusApplicable( @Nonnull ProgramStageRule rule, @Nonnull Event event )
    {
        if ( !rule.getApplicableEnrollmentStatus().isApplicable( event.getEnrollment().getStatus() ) )
        {
            logger.info( "Enrollment status {} is not applicable for rule {}.", event.getEnrollment().getStatus(), rule );
            return false;
        }
        if ( !rule.getApplicableEventStatus().isApplicable( event.getStatus() ) )
        {
            logger.info( "Event status {} is not applicable for rule {}.", event.getStatus(), rule );
            return false;
        }
        return true;
    }
}
