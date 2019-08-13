package org.dhis2.fhir.adapter.dhis.tracker.program.impl;

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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.dhis2.fhir.adapter.auth.UnauthorizedException;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.dhis.DhisConflictException;
import org.dhis2.fhir.adapter.dhis.DhisFindException;
import org.dhis2.fhir.adapter.dhis.DhisImportUnsuccessfulException;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisRepositoryPersistCallback;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisRepositoryPersistResult;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisRepositoryPersistStatus;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisResourceRepositoryTemplate;
import org.dhis2.fhir.adapter.dhis.metadata.model.DhisSyncGroup;
import org.dhis2.fhir.adapter.dhis.model.DataValue;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceComparator;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceResult;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.ImportStatus;
import org.dhis2.fhir.adapter.dhis.model.ImportSummariesWebMessage;
import org.dhis2.fhir.adapter.dhis.model.ImportSummary;
import org.dhis2.fhir.adapter.dhis.model.Status;
import org.dhis2.fhir.adapter.dhis.model.UriFilterApplier;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.dhis2.fhir.adapter.dhis.util.CodeGenerator;
import org.dhis2.fhir.adapter.dhis.util.DhisPagingQuery;
import org.dhis2.fhir.adapter.dhis.util.DhisPagingUtils;
import org.dhis2.fhir.adapter.rest.RestTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Implementation of {@link EventService}.
 *
 * @author volsch
 */
@Service
public class EventServiceImpl implements EventService, LocalDhisRepositoryPersistCallback<Event>
{
    protected static final String FIELDS =
        "deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated," +
            "dataValues[dataElement,value,providedElsewhere,lastUpdated,storedBy]";

    protected static final String ID_URI = "/events/{id}.json";

    protected static final String FIND_ID_URI = "/events/{id}.json?fields=" + FIELDS;

    protected static final String CREATE_URI = "/events.json?strategy=CREATE";

    protected static final String CREATES_URI = "/events.json?strategy=CREATE";

    protected static final String UPDATE_URI = ID_URI + "?mergeMode=MERGE";

    protected static final String UPDATES_URI = "/events.json?strategy=UPDATE&mergeMode=MERGE";

    protected static final String UPDATE_DATA_VALUE_URI = "/events/{id}/{dataElementId}.json?mergeMode=MERGE";

    protected static final String DELETES_URI = "/events.json?strategy=DELETE";

    protected static final String FIND_URI = "/events.json?" +
        "program={programId}&trackedEntityInstance={trackedEntityInstanceId}&ouMode=ACCESSIBLE&" +
        "fields=" + FIELDS + "&skipPaging=true";

    protected static final String FIND_DELETED_ID_URI = "/events.json?" +
        "event={eventId}&includeDeleted=true&fields=" + FIELDS + "&skipPaging=true";

    private final RestTemplate restTemplate;

    private final PolledProgramRetriever polledProgramRetriever;

    private final ZoneId zoneId = ZoneId.systemDefault();

    private final LocalDhisResourceRepositoryTemplate<Event> resourceRepositoryTemplate;

    @Autowired
    public EventServiceImpl( @Nonnull @Qualifier( "userDhis2RestTemplate" ) RestTemplate restTemplate, @Nonnull RequestCacheService requestCacheService, @Nonnull PolledProgramRetriever polledProgramRetriever )
    {
        this.restTemplate = restTemplate;
        this.polledProgramRetriever = polledProgramRetriever;

        this.resourceRepositoryTemplate = new LocalDhisResourceRepositoryTemplate<>( Event.class, requestCacheService, this );
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.PROGRAM_STAGE_EVENT;
    }

    @HystrixCommand( ignoreExceptions = { DhisConflictException.class, UnauthorizedException.class } )
    @Override
    public boolean delete( @Nonnull String eventId )
    {
        return resourceRepositoryTemplate.deleteById( eventId, Event::new );
    }

    protected boolean _delete( @Nonnull String eventId )
    {
        Event instance;

        try
        {
            restTemplate.delete( "/events/{id}", eventId );
        }
        catch ( HttpClientErrorException e )
        {
            if ( RestTemplateUtils.isNotFound( e ) )
            {
                return false;
            }

            throw e;
        }

        return true;
    }

    @Override
    public void persistDeleteById( @Nonnull Collection<String> ids, @Nullable Consumer<LocalDhisRepositoryPersistResult> resultConsumer )
    {
        if ( ids.isEmpty() )
        {
            return;
        }

        final List<Event> Events = ids.stream().map( Event::new ).sorted( DhisResourceComparator.INSTANCE ).collect( Collectors.toList() );
        final ResponseEntity<ImportSummariesWebMessage> response =
            restTemplate.postForEntity( DELETES_URI, new DhisEvents( Events ), ImportSummariesWebMessage.class );

        final ImportSummariesWebMessage result = Objects.requireNonNull( response.getBody() );
        final int size = Events.size();

        if ( result.getStatus() != Status.OK || result.getResponse() == null || result.getResponse().getImportSummaries().size() != size )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful deletion of event." );
        }

        if ( resultConsumer != null )
        {
            for ( int i = 0; i < size; i++ )
            {
                final ImportSummary importSummary = result.getResponse().getImportSummaries().get( i );
                final Event event = Events.get( i );
                final LocalDhisRepositoryPersistResult persistResult;

                if ( importSummary.getStatus() == ImportStatus.ERROR )
                {
                    persistResult = new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.ERROR, event.getId(),
                        StringUtils.defaultIfBlank( importSummary.getDescription(), "Failed to delete event." ) );
                }
                else if ( importSummary.getImportCount().getDeleted() == 0 )
                {
                    persistResult = new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.NOT_FOUND, event.getId(),
                        StringUtils.defaultIfBlank( importSummary.getDescription(), "Could not find event." ) );
                }
                else
                {
                    persistResult = new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.SUCCESS, event.getId() );
                }

                resultConsumer.accept( persistResult );
            }
        }
    }

    @Override
    public boolean persistDeleteById( @Nonnull String id )
    {
        return _delete( id );
    }

    @HystrixCommand( ignoreExceptions = { DhisConflictException.class, UnauthorizedException.class } )
    @Nonnull
    @Override
    @CacheEvict( key = "{'find', #a0.programId, #a0.programStageId, #a0.enrollmentId, #a0.trackedEntityInstanceId}", cacheManager = "dhisCacheManager", cacheNames = "events" )
    public Event createOrMinimalUpdate( @Nonnull Event event )
    {
        return resourceRepositoryTemplate.save( event );
    }

    protected Event _createOrMinimalUpdate( @Nonnull Event event )
    {
        return event.isNewResource() ? create( event ) : minimalUpdate( event );
    }

    @Override
    public void persistSave( @Nonnull Collection<Event> resources, boolean create, @Nullable Consumer<LocalDhisRepositoryPersistResult> resultConsumer )
    {
        if ( resources.isEmpty() )
        {
            return;
        }

        final List<Event> events = resources.stream().sorted( DhisResourceComparator.INSTANCE ).collect( Collectors.toList() );
        final ResponseEntity<ImportSummariesWebMessage> response =
            restTemplate.postForEntity( create ? CREATES_URI : UPDATES_URI, new DhisEvents( events ), ImportSummariesWebMessage.class );

        final ImportSummariesWebMessage result = Objects.requireNonNull( response.getBody() );
        final int size = events.size();

        if ( result.getStatus() != Status.OK || result.getResponse() == null || result.getResponse().getImportSummaries().size() != size )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful import of events." );
        }

        for ( int i = 0; i < size; i++ )
        {
            final ImportSummary importSummary = result.getResponse().getImportSummaries().get( i );
            final Event event = events.get( i );
            final LocalDhisRepositoryPersistResult persistResult;

            if ( importSummary.getStatus() == ImportStatus.ERROR )
            {
                persistResult = new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.ERROR, event.getId(),
                    StringUtils.defaultIfBlank( importSummary.getDescription(), "Failed to persist event." ) );
            }
            else
            {
                event.resetNewResource();
                event.setLocal( false );

                persistResult = new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.SUCCESS, event.getId() );
            }

            if ( resultConsumer != null )
            {
                resultConsumer.accept( persistResult );
            }
        }
    }

    @Override
    @Nonnull
    public Event persistSave( @Nonnull Event resource )
    {
        return _createOrMinimalUpdate( resource );
    }

    @HystrixCommand( ignoreExceptions = UnauthorizedException.class )
    @Nonnull
    @Override
    @CachePut( key = "{'find', #a0, #a1, #a2, #a3}", condition = "#a4 == false", cacheManager = "dhisCacheManager", cacheNames = "events" )
    public Collection<Event> findRefreshed( @Nonnull String programId, @Nonnull String programStageId,
        @Nonnull String enrollmentId, @Nonnull String trackedEntityInstanceId, boolean localOnly )
    {
        return resourceRepositoryTemplate.find( trackedEntityInstanceId,
            e -> programId.equals( e.getProgramId() ) && programStageId.equals( e.getProgramStageId() ) && enrollmentId.equals( e.getEnrollmentId() ),
            () -> _findRefreshed( programId, programStageId, enrollmentId, trackedEntityInstanceId ),
            localOnly, "findRefreshed", programId, programStageId, enrollmentId, trackedEntityInstanceId );
    }

    @Nonnull
    protected Collection<Event> _findRefreshed( @Nonnull String programId, @Nonnull String programStageId,
        @Nonnull String enrollmentId, @Nonnull String trackedEntityInstanceId )
    {
        final ResponseEntity<DhisEvents> result = restTemplate.getForEntity( FIND_URI, DhisEvents.class, programId, trackedEntityInstanceId );

        return Objects.requireNonNull( result.getBody() ).getEvents().stream().filter( e -> enrollmentId.equals( e.getEnrollmentId() ) &&
            programStageId.equals( e.getProgramStageId() ) ).collect( Collectors.toList() );
    }

    @HystrixCommand( ignoreExceptions = UnauthorizedException.class )
    @Nonnull
    @Override
    @Cacheable( key = "{'find', #a0, #a1, #a2, #a3}", condition = "#a4 == false", cacheManager = "dhisCacheManager", cacheNames = "events" )
    public Collection<Event> find( @Nonnull String programId, @Nonnull String programStageId,
        @Nonnull String enrollmentId, @Nonnull String trackedEntityInstanceId, boolean localOnly )
    {
        return findRefreshed( programId, programStageId, enrollmentId, trackedEntityInstanceId, localOnly );
    }

    @HystrixCommand( ignoreExceptions = UnauthorizedException.class )
    @Nonnull
    @Override
    public Optional<Event> findOneById( @Nonnull String eventId )
    {
        return resourceRepositoryTemplate.findOneById( eventId, this::_findOneById );
    }

    protected Event _findOneById( @Nonnull String eventId )
    {
        Event instance;

        try
        {
            instance = Objects.requireNonNull( restTemplate.getForObject( FIND_ID_URI, Event.class, eventId ) );
        }
        catch ( HttpClientErrorException e )
        {
            if ( RestTemplateUtils.isNotFound( e ) )
            {
                return null;
            }

            throw e;
        }

        return instance;
    }

    @HystrixCommand( ignoreExceptions = UnauthorizedException.class )
    @Nonnull
    @Override
    public Optional<Event> findOneDeletedById( @Nonnull String eventId )
    {
        final ResponseEntity<DhisEvents> result = restTemplate.getForEntity( FIND_DELETED_ID_URI, DhisEvents.class, eventId );
        return Objects.requireNonNull( result.getBody() ).getEvents().stream().findFirst();
    }

    @Nonnull
    @Override
    public Instant poll( @Nonnull DhisSyncGroup group, @Nonnull Instant lastUpdated, int toleranceMillis, int maxSearchCount, @Nonnull Set<String> excludedStoredBy, @Nonnull Consumer<Collection<ProcessedItemInfo>> consumer )
    {
        final EventPolledItemRetriever eventPolledItemRetriever = new EventPolledItemRetriever( restTemplate, toleranceMillis, maxSearchCount, zoneId );
        Instant result = Instant.now();
        for ( final String programId : polledProgramRetriever.findAllPolledProgramIds() )
        {
            final Instant currentResult = eventPolledItemRetriever.poll( lastUpdated, excludedStoredBy, consumer, Collections.singletonList( programId ) );
            result = ObjectUtils.min( result, currentResult );
        }
        return result;
    }

    @Nonnull
    protected Event create( @Nonnull Event event )
    {
        final ResponseEntity<ImportSummariesWebMessage> response;

        if ( event.getId() == null )
        {
            event.setId( CodeGenerator.generateUid() );
        }

        try
        {
            response = restTemplate.exchange( CREATE_URI, HttpMethod.POST, new HttpEntity<>( event ), ImportSummariesWebMessage.class );
        }
        catch ( HttpClientErrorException e )
        {
            if ( HttpStatus.CONFLICT.equals( e.getStatusCode() ) )
            {
                throw new DhisConflictException( "Event could not be created: " + e.getResponseBodyAsString(), e );
            }

            throw e;
        }

        final ImportSummariesWebMessage result = Objects.requireNonNull( response.getBody() );

        if ( result.isNotSuccessful() )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful event import." );
        }

        event.setId( result.getResponse().getImportSummaries().get( 0 ).getReference() );
        event.resetNewResource();
        event.setLocal( false );

        return event;
    }

    @Nonnull
    protected Event update( @Nonnull Event event )
    {
        final ResponseEntity<ImportSummariesWebMessage> response;

        try
        {
            response = restTemplate.exchange( UPDATE_URI, HttpMethod.PUT, new HttpEntity<>( event ),
                ImportSummariesWebMessage.class, event.getId() );
        }
        catch ( HttpClientErrorException e )
        {
            if ( HttpStatus.CONFLICT.equals( e.getStatusCode() ) )
            {
                throw new DhisConflictException( "Event could not be updated: " + e.getResponseBodyAsString(), e );
            }

            throw e;
        }

        final ImportSummariesWebMessage result = Objects.requireNonNull( response.getBody() );

        if ( result.getStatus() != Status.OK )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful import: " +
                result.getStatus() );
        }

        return event;
    }

    @HystrixCommand( ignoreExceptions = { UnauthorizedException.class, DhisFindException.class } )
    @Nonnull
    @Override
    public DhisResourceResult<Event> find( @Nullable String programId, @Nullable String programStageId, @Nonnull UriFilterApplier uriFilterApplier, int from, int max )
    {
        final DhisPagingQuery pagingQuery = DhisPagingUtils.createPagingQuery( from, max );
        final List<String> variables = new ArrayList<>();

        UriComponentsBuilder builder = uriFilterApplier.add( UriComponentsBuilder.newInstance(), variables ).path( "/events.json" )
            .queryParam( "skipPaging", "false" ).queryParam( "page", pagingQuery.getPage() ).queryParam( "pageSize", pagingQuery.getPageSize() )
            .queryParam( "ouMode", uriFilterApplier.containsQueryParam( "orgUnit" ) ? "SELECTED" : "ACCESSIBLE" )
            .queryParam( "fields", FIELDS );

        if ( programId != null )
        {
            builder = builder.queryParam( "program", programId );
        }

        if ( programStageId != null )
        {
            builder = builder.queryParam( "programStage", programStageId );
        }

        final ResponseEntity<DhisEvents> result;
        try
        {
            result = restTemplate.getForEntity( builder.build().toString(), DhisEvents.class, variables.toArray() );
        }
        catch ( HttpClientErrorException e )
        {
            if ( e.getStatusCode() == HttpStatus.BAD_REQUEST || e.getStatusCode() == HttpStatus.CONFLICT )
            {
                throw new DhisFindException( e.getMessage(), e );
            }

            throw e;
        }

        final DhisEvents events = Objects.requireNonNull( result.getBody() );

        return new DhisResourceResult<>( (events.getEvents().size() > pagingQuery.getResultOffset()) ?
            events.getEvents().subList( pagingQuery.getResultOffset(), events.getEvents().size() ) : Collections.emptyList(),
            (events.getEvents().size() >= pagingQuery.getPageSize()) );
    }

    protected void update( @Nonnull MinimalEvent event )
    {
        final ResponseEntity<ImportSummariesWebMessage> response;
        try
        {
            response = restTemplate.exchange( UPDATE_DATA_VALUE_URI, HttpMethod.PUT, new HttpEntity<>( event ),
                ImportSummariesWebMessage.class, event.getId(), event.getDataElementId() );
        }
        catch ( HttpClientErrorException e )
        {
            if ( HttpStatus.CONFLICT.equals( e.getStatusCode() ) )
            {
                throw new DhisConflictException( "Event could not be updated: " + e.getResponseBodyAsString(), e );
            }
            throw e;
        }
        final ImportSummariesWebMessage result = Objects.requireNonNull( response.getBody() );

        if ( result.getStatus() != Status.OK )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful event import: " +
                result.getStatus() );
        }
    }

    @Nonnull
    protected Event minimalUpdate( @Nonnull Event event )
    {
        if ( event.isModified() || event.getDataValues().stream().anyMatch( DataValue::isNewResource ) )
        {
            return update( event );
        }

        event.getDataValues().stream().filter( DataValue::isModified ).forEach( dv ->
            update( new MinimalEvent( event, dv ) ) );

        return event;
    }
}
