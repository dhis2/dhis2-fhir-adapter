package org.dhis2.fhir.adapter.fhir.transform.scripted.program;

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
import org.dhis2.fhir.adapter.dhis.orgunit.OrganisationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganisationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.dhis2.fhir.adapter.dhis.tracker.program.ImmutableProgram;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.ImmutableTrackedEntityType;
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
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.scripted.AbstractFhirToDhisTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.trackedentity.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.scripted.trackedentity.WritableScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.spring.StaticObjectProvider;
import org.dhis2.fhir.adapter.util.DateTimeUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class FhirToProgramStageTransformer extends AbstractFhirToDhisTransformer<Event, ProgramStageRule>
{
    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final ProgramMetadataService programMetadataService;

    private final EnrollmentService enrollmentService;

    private final EventService eventService;

    private final FhirResourceMappingRepository resourceMappingRepository;

    private final ValueConverter valueConverter;

    public FhirToProgramStageTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull OrganisationUnitService organisationUnitService,
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull ProgramMetadataService programMetadataService, @Nonnull EnrollmentService enrollmentService, @Nonnull EventService eventService,
        @Nonnull FhirResourceMappingRepository resourceMappingRepository, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutor, organisationUnitService, new StaticObjectProvider<>( trackedEntityService ) );
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
            rule.getProgramStage().getProgram().getTrackedEntityRule(), resourceMapping, variables ).orElse( null );
        if ( trackedEntityInstance == null )
        {
            return null;
        }

        addScriptVariables( variables, rule, trackedEntityInstance );
        final Optional<Event> event = getResource( context, rule, variables );
        if ( !event.isPresent() )
        {
            return null;
        }

        final Program program = getScriptVariable( variables, ScriptVariable.PROGRAM, Program.class );
        final ProgramStage programStage = getScriptVariable( variables, ScriptVariable.PROGRAM_STAGE, ProgramStage.class );
        final WritableScriptedEnrollment scriptedEnrollment = new WritableScriptedEnrollment( program, event.get().getEnrollment(), valueConverter );
        variables.put( ScriptVariable.ENROLLMENT.getVariableName(), scriptedEnrollment );

        final WritableScriptedEvent scriptedEvent = new WritableScriptedEvent( programStage, event.get(), valueConverter );
        variables.put( ScriptVariable.OUTPUT.getVariableName(), scriptedEvent );

        if ( !transform( context, rule, variables ) )
        {
            return null;
        }
        updateCoordinates( context, resourceMapping,
            program, scriptedEnrollment, programStage, scriptedEvent, variables );

        if ( trackedEntityInstance.isModified() )
        {
            getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class ).validate();
        }
        event.get().getEnrollment().setTrackedEntityInstance( trackedEntityInstance );
        if ( event.get().getEnrollment().isModified() )
        {
            scriptedEvent.validate();
        }
        event.get().setTrackedEntityInstance( trackedEntityInstance );
        scriptedEvent.validate();

        return new FhirToDhisTransformOutcome<>( event.get().getId(), event.get() );
    }

    protected void addBasicScriptVariables( @Nonnull Map<String, Object> variables, @Nonnull ProgramStageRule rule ) throws TransformerException
    {
        final Program program = programMetadataService.getProgram( rule.getProgramStage().getProgram().getProgramReference() ).map( ImmutableProgram::new )
            .orElseThrow( () -> new TransformerMappingException( "Mapping " + rule + " requires program \"" +
                rule.getProgramStage().getProgram().getProgramReference() + "\" that does not exist." ) );
        variables.put( ScriptVariable.PROGRAM.getVariableName(), program );

        final TrackedEntityAttributes attributes = trackedEntityMetadataService.getAttributes();
        final TrackedEntityType trackedEntityType = trackedEntityMetadataService.getTypeById( program.getTrackedEntityTypeId() )
            .orElseThrow( () -> new TransformerMappingException( "Program \"" + program.getName() +
                "\" references tracked entity type " + program.getTrackedEntityTypeId() + " that does not exist." ) );
        variables.put( ScriptVariable.TRACKED_ENTITY_ATTRIBUTES.getVariableName(), attributes );
        variables.put( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName(), new ImmutableTrackedEntityType( trackedEntityType ) );
    }

    protected void addScriptVariables( @Nonnull Map<String, Object> variables, @Nonnull ProgramStageRule rule, @Nonnull TrackedEntityInstance trackedEntityInstance ) throws TransformerException
    {
        variables.put( ScriptVariable.TRACKED_ENTITY_INSTANCE.getVariableName(), new WritableScriptedTrackedEntityInstance(
            getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_ATTRIBUTES, TrackedEntityAttributes.class ),
            getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class ),
            trackedEntityInstance, valueConverter ) );

        final Program program = getScriptVariable( variables, ScriptVariable.PROGRAM, Program.class );
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

    @Nonnull
    @Override
    protected Optional<Event> getResourceById( @Nullable String id ) throws TransformerException
    {
        // resolving resources by technical ID is not supported
        return Optional.empty();
    }

    @Nonnull
    @Override
    protected Optional<Event> getResourceByIdentifier( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule,
        @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        // resolving resources by business identifier is not supported currently
        return Optional.empty();
    }


    @Nonnull
    @Override
    protected Optional<Event> getActiveResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule mapping,
        @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return getEventInfo( FhirToProgramStageTransformer::isEditableEvent, scriptVariables ).getEvent();
    }

    @Nullable
    @Override
    protected Event createResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull ProgramStageRule rule,
        @Nullable String id, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( !rule.getProgramStage().isCreationEnabled() )
        {
            return null;
        }

        final EventInfo eventInfo = getEventInfo( e -> e.getStatus() != EventStatus.COMPLETED, scriptVariables );
        // repeated events are not supported currently
        if ( eventInfo.getEvent().isPresent() || eventInfo.isCompletedEvents() || eventInfo.getProgramStage().isRepeatable() )
        {
            logger.info( "Creation of repeated program stage instance \"{}\" is not yet supported.", eventInfo.getProgramStage().getName() );
            return null;
        }

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

        final Event event = new Event( true );
        event.setEnrollment( enrollment );
        event.setOrgUnitId( orgUnit.get().getId() );
        event.setProgramId( eventInfo.getProgram().getId() );
        event.setProgramStageId( eventInfo.getProgramStage().getId() );
        event.setTrackedEntityInstanceId( eventInfo.getTrackedEntityInstance().getId() );

        final Program program = getScriptVariable( scriptVariables, ScriptVariable.PROGRAM, Program.class );
        final WritableScriptedEnrollment scriptedEnrollment = new WritableScriptedEnrollment( program, enrollment, valueConverter );
        final WritableScriptedEvent scriptedEvent = new WritableScriptedEvent( eventInfo.getProgramStage(), event, valueConverter );
        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        variables.put( ScriptVariable.ENROLLMENT.getVariableName(), scriptedEnrollment );
        variables.put( ScriptVariable.EVENT.getVariableName(), scriptedEvent );

        final ZonedDateTime eventDate = getEventDate( context, resourceMapping, enrollment, eventInfo.getProgramStage(), variables );
        if ( eventDate == null )
        {
            return null;
        }
        event.setEventDate( eventDate );

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

        if ( enrollment.isModified() )
        {
            scriptedEnrollment.validate();
        }
        scriptedEvent.validate();
        return event;
    }

    private ZonedDateTime getEventDate( @Nonnull FhirToDhisTransformerContext context, @Nonnull FhirResourceMapping resourceMapping, @Nonnull Enrollment enrollment,
        @Nonnull ProgramStage programStage, @Nonnull Map<String, Object> variables )
    {
        ZonedDateTime eventDate;
        if ( programStage.isGeneratedByEnrollmentDate() )
        {
            eventDate = enrollment.getEnrollmentDate();
        }
        else
        {
            eventDate = valueConverter.convert( getScriptExecutor().execute( resourceMapping.getEventDateLookupScript(),
                context.getFhirRequest().getVersion(), variables, Object.class ), ValueType.DATETIME, ZonedDateTime.class );
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
        final MappedTrackerProgram mappedProgram = rule.getProgramStage().getProgram();
        if ( !mappedProgram.isCreationEnabled() )
        {
            logger.info( "Creation of program stage has not been enabled." );
            return null;
        }

        final Program program = getScriptVariable( scriptVariables, ScriptVariable.PROGRAM, Program.class );
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

        final Enrollment enrollment = new Enrollment( true );
        enrollment.setProgramId( program.getId() );
        enrollment.setTrackedEntityInstanceId( getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class ).getId() );
        enrollment.setOrgUnitId( organisationUnit.get().getId() );
        enrollment.setEnrollmentDate( enrollmentDate );

        final WritableScriptedEnrollment scriptedEnrollment = new WritableScriptedEnrollment( program, enrollment, valueConverter );
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
        if ( enrollment.getStatus() == null )
        {
            enrollment.setStatus( EnrollmentStatus.ACTIVE );
        }

        scriptedEnrollment.validate();
        return enrollment;
    }

    @Nullable
    private ZonedDateTime getEnrollmentDate( @Nonnull FhirToDhisTransformerContext context, @Nonnull FhirResourceMapping resourceMapping, Program program, @Nonnull Map<String, Object> scriptVariables )
    {
        ZonedDateTime enrollmentDate = valueConverter.convert( getScriptExecutor().execute( resourceMapping.getEnrollmentDateLookupScript(),
            context.getFhirRequest().getVersion(), scriptVariables, Object.class ), ValueType.DATETIME, ZonedDateTime.class );
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

    private boolean updateIncidentDate( @Nonnull Program program, @Nonnull Enrollment enrollment )
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
        return resourceMappingRepository.getByFhirResourceType( rule.getFhirResourceType() )
            .orElseThrow( () -> new FatalTransformerException( "No FHIR resource mapping has been defined for " + rule.getFhirResourceType() + "." ) );
    }

    @Nonnull
    protected Optional<OrganisationUnit> getEventOrgUnit( @Nonnull FhirToDhisTransformerContext context, @Nonnull FhirResourceMapping resourceMapping,
        @Nonnull Enrollment enrollment, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final Program program = getScriptVariable( scriptVariables, ScriptVariable.PROGRAM, Program.class );
        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        variables.put( ScriptVariable.ENROLLMENT.getVariableName(),
            new ImmutableScriptedEnrollment( new WritableScriptedEnrollment( program, enrollment, valueConverter ) ) );
        return getOrgUnit( context, resourceMapping.getEventOrgLookupScript(), variables );
    }

    @Nonnull
    private EventInfo getEventInfo( @Nonnull Predicate<Event> predicate, @Nonnull Map<String, Object> scriptVariables )
    {
        final Program program = getScriptVariable( scriptVariables, ScriptVariable.PROGRAM, Program.class );
        final ProgramStage programStage = getScriptVariable( scriptVariables, ScriptVariable.PROGRAM_STAGE, ProgramStage.class );
        final TrackedEntityType trackedEntityType = getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class );
        final ScriptedTrackedEntityInstance trackedEntityInstance = getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_INSTANCE, ScriptedTrackedEntityInstance.class );

        // automatic enrollment is not available currently/
        final Enrollment enrollment = enrollmentService.getLatestActive( program.getId(),
            Objects.requireNonNull( trackedEntityInstance.getId() ) ).orElse( null );
        Event event = null;
        boolean completedEvents = false;
        if ( enrollment != null )
        {
            final List<Event> events = eventService.find( program.getId(), programStage.getId(), enrollment.getId(), trackedEntityInstance.getId() );
            event = events.stream().filter( predicate ).sorted().findFirst().map( e -> {
                e.setEnrollment( enrollment );
                return e;
            } ).orElse( null );
            completedEvents = events.stream().anyMatch( e -> e.getStatus() == EventStatus.COMPLETED );
        }
        return new EventInfo( program, programStage, trackedEntityType, trackedEntityInstance, enrollment, event, completedEvents );
    }

    private static boolean isEditableEvent( @Nonnull Event e )
    {
        return (e.getStatus() == EventStatus.ACTIVE) || (e.getStatus() == EventStatus.SCHEDULE) || (e.getStatus() == EventStatus.OVERDUE) || (e.getStatus() == EventStatus.VISITED);
    }
}
