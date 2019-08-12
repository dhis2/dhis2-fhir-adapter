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
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.auth.UnauthorizedException;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.dhis.DhisConflictException;
import org.dhis2.fhir.adapter.dhis.DhisFindException;
import org.dhis2.fhir.adapter.dhis.DhisImportUnsuccessfulException;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisRepositoryPersistCallback;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisRepositoryPersistResult;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisRepositoryPersistStatus;
import org.dhis2.fhir.adapter.dhis.local.LocalDhisResourceRepositoryTemplate;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceComparator;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceResult;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.ImportStatus;
import org.dhis2.fhir.adapter.dhis.model.ImportSummaries;
import org.dhis2.fhir.adapter.dhis.model.ImportSummariesWebMessage;
import org.dhis2.fhir.adapter.dhis.model.ImportSummary;
import org.dhis2.fhir.adapter.dhis.model.Status;
import org.dhis2.fhir.adapter.dhis.model.UriFilterApplier;
import org.dhis2.fhir.adapter.dhis.tracker.program.Enrollment;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentStatus;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Implementation of {@link EnrollmentService}.
 *
 * @author volsch
 * @author Charles Chigoriwa (ITINORDIC)
 */
@Service
public class EnrollmentServiceImpl implements EnrollmentService, LocalDhisRepositoryPersistCallback<Enrollment>
{
    protected static final String ENROLLMENTS_URI = "/enrollments.json";

    protected static final String ENROLLMENT_ID_URI = "/enrollments/{id}.json";

    protected static final String ENROLLMENT_CREATE_URI = "/enrollments.json?strategy=CREATE";

    protected static final String ENROLLMENT_CREATES_URI = "/enrollments.json?strategy=CREATE";

    protected static final String ENROLLMENT_UPDATE_URI = "/enrollments/{id}.json?mergeMode=MERGE";

    protected static final String ENROLLMENT_UPDATES_URI = "/enrollments.json?strategy=UPDATE&mergeMode=MERGE";

    protected static final String ENROLLMENT_DELETES_URI = "/enrollments.json?strategy=DELETE";

    protected static final String LATEST_ACTIVE_URI = "/enrollments.json?" +
        "program={programId}&programStatus=ACTIVE&trackedEntityInstance={trackedEntityInstanceId}&" +
        "ouMode=ACCESSIBLE&fields=:all&order=lastUpdated:desc&pageSize=1";

    private final RestTemplate restTemplate;

    private final EventService eventService;

    private final LocalDhisResourceRepositoryTemplate<Enrollment> resourceRepositoryTemplate;

    @Autowired
    public EnrollmentServiceImpl( @Nonnull @Qualifier( "userDhis2RestTemplate" ) RestTemplate restTemplate, @Nonnull EventService eventService, @Nonnull RequestCacheService requestCacheService )
    {
        this.restTemplate = restTemplate;
        this.eventService = eventService;

        this.resourceRepositoryTemplate = new LocalDhisResourceRepositoryTemplate<>( Enrollment.class, requestCacheService, this );
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.ENROLLMENT;
    }

    @HystrixCommand( ignoreExceptions = { UnauthorizedException.class, DhisFindException.class } )
    @Nonnull
    @Override
    public DhisResourceResult<Enrollment> find( @Nonnull UriFilterApplier uriFilterApplier, int from, int max )
    {
        final DhisPagingQuery pagingQuery = DhisPagingUtils.createPagingQuery( from, max );
        final List<String> variables = new ArrayList<>();
        final String uri = uriFilterApplier.add( UriComponentsBuilder.newInstance(), variables ).path( "/enrollments.json" )
            .queryParam( "skipPaging", "false" ).queryParam( "page", pagingQuery.getPage() ).queryParam( "pageSize", pagingQuery.getPageSize() )
            .queryParam( "ouMode", uriFilterApplier.containsQueryParam( "ou" ) ? "SELECTED" : "ACCESSIBLE" )
            .queryParam( "fields", ":all" ).build().toString();
        final ResponseEntity<DhisEnrollments> result;
        try
        {
            result = restTemplate.getForEntity( uri, DhisEnrollments.class, variables.toArray() );
        }
        catch ( HttpClientErrorException e )
        {
            if ( e.getStatusCode() == HttpStatus.BAD_REQUEST || e.getStatusCode() == HttpStatus.CONFLICT )
            {
                throw new DhisFindException( e.getMessage(), e );
            }
            throw e;
        }
        final DhisEnrollments enrollments = Objects.requireNonNull( result.getBody() );

        return new DhisResourceResult<>( ( enrollments.getEnrollments().size() > pagingQuery.getResultOffset() ) ?
            enrollments.getEnrollments().subList( pagingQuery.getResultOffset(), enrollments.getEnrollments().size() ) : Collections.emptyList(),
            ( enrollments.getEnrollments().size() >= pagingQuery.getPageSize() ) );
    }

    @HystrixCommand( ignoreExceptions = UnauthorizedException.class )
    @Nonnull
    @Override
    @CachePut( key = "{'findLatestActive', #a0, #a1}", condition = "#a2 == false", cacheManager = "dhisCacheManager", cacheNames = "enrollments" )
    public Optional<Enrollment> findLatestActiveRefreshed( @Nonnull String programId, @Nonnull String trackedEntityInstanceId, boolean localOnly )
    {
        final Collection<Enrollment> enrollments = resourceRepositoryTemplate.find( trackedEntityInstanceId,
            e -> programId.equals( e.getProgramId() ) && e.getStatus() == EnrollmentStatus.ACTIVE,
            () -> createCollection( _findLatestActiveRefreshed( programId, trackedEntityInstanceId ) ),
            localOnly, "findLatestActiveRefreshed", programId, trackedEntityInstanceId );

        return enrollments.stream().min( Comparator.comparing( Enrollment::getLastUpdated, Comparator.nullsLast( Comparator.reverseOrder() ) ) );
    }

    @Nonnull
    private Collection<Enrollment> createCollection( @Nullable Enrollment enrollment )
    {
        return enrollment == null ? Collections.emptyList() : Collections.singletonList( enrollment );
    }

    @Nullable
    protected Enrollment _findLatestActiveRefreshed( @Nonnull String programId, @Nonnull String trackedEntityInstanceId )
    {
        final ResponseEntity<DhisEnrollments> result = restTemplate.getForEntity(
            LATEST_ACTIVE_URI, DhisEnrollments.class, programId, trackedEntityInstanceId );

        return Objects.requireNonNull( result.getBody() ).getEnrollments().stream().findFirst().orElse( null );
    }

    @HystrixCommand( ignoreExceptions = UnauthorizedException.class )
    @Nonnull
    @Override
    @Cacheable( key = "{'findLatestActive', #a0, #a1}", condition = "#a2 == false", cacheManager = "dhisCacheManager", cacheNames = "enrollments" )
    public Optional<Enrollment> findLatestActive( @Nonnull String programId, @Nonnull String trackedEntityInstanceId, boolean localOnly )
    {
        return findLatestActiveRefreshed( programId, trackedEntityInstanceId, localOnly );
    }

    @HystrixCommand( ignoreExceptions = UnauthorizedException.class )
    @Nonnull
    @Override
    public Optional<Enrollment> findOneById( @Nonnull String id )
    {
        return resourceRepositoryTemplate.findOneById( id, this::_findOneById );
    }

    @Nullable
    protected Enrollment _findOneById( @Nonnull String id )
    {
        Enrollment instance;

        try
        {
            instance = Objects.requireNonNull( restTemplate.getForObject( ENROLLMENT_ID_URI, Enrollment.class, id ) );
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

    @HystrixCommand( ignoreExceptions = { DhisConflictException.class, UnauthorizedException.class } )
    @Nonnull
    @Override
    @CacheEvict( key = "{'findLatestActive', #a0.programId, #a0.trackedEntityInstanceId}", cacheManager = "dhisCacheManager", cacheNames = "enrollments" )
    public Enrollment createOrUpdate( @Nonnull Enrollment enrollment )
    {
        return resourceRepositoryTemplate.save( enrollment, e -> {
            if ( e.getEvents() != null )
            {
                // local IDs must be created for each included event
                e.getEvents().forEach( event -> {
                    // force update of included enrollment ID
                    event.setEnrollment( e );
                    eventService.createOrMinimalUpdate( event );
                } );
            }
        } );
    }

    @Nonnull
    protected Enrollment _create( @Nonnull Enrollment enrollment )
    {
        final ResponseEntity<ImportSummariesWebMessage> response;

        if ( enrollment.getId() == null )
        {
            enrollment.setId( CodeGenerator.generateUid() );
        }

        if ( enrollment.getEvents() != null )
        {
            enrollment.getEvents().forEach( e -> {
                if ( e.getId() == null )
                {
                    e.setId( CodeGenerator.generateUid() );
                }

                // force setting of new enrollment ID
                e.setEnrollment( enrollment );
            } );
        }

        try
        {
            response = restTemplate.exchange( ENROLLMENT_CREATE_URI, HttpMethod.POST, new HttpEntity<>( enrollment ), ImportSummariesWebMessage.class );
        }
        catch ( HttpClientErrorException e )
        {
            if ( HttpStatus.CONFLICT.equals( e.getStatusCode() ) )
            {
                throw new DhisConflictException( "Enrollment could not be created: " + e.getResponseBodyAsString(), e );
            }

            throw e;
        }

        final ImportSummariesWebMessage result = Objects.requireNonNull( response.getBody() );

        if ( result.isNotSuccessful() )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful enrollment import." );
        }

        enrollment.setId( result.getResponse().getImportSummaries().get( 0 ).getReference() );
        enrollment.resetNewResource();
        enrollment.setLocal( false );

        if ( (enrollment.getEvents() != null) && !enrollment.getEvents().isEmpty() )
        {
            final ImportSummaries eventImportSummaries = result.getResponse().getImportSummaries().get( 0 ).getEvents();

            if ( eventImportSummaries == null )
            {
                throw new DhisImportUnsuccessfulException( "Enrollment contains events but response did not." );
            }

            final int size = enrollment.getEvents().size();

            for ( int i = 0; i < size; i++ )
            {
                final Event event = enrollment.getEvents().get( i );

                event.setId( eventImportSummaries.getImportSummaries().get( i ).getReference() );
                event.resetNewResource();
                event.setLocal( false );
            }
        }

        return enrollment;
    }

    @Nonnull
    protected Enrollment _update( @Nonnull Enrollment enrollment )
    {
        // update of included events is not supported
        enrollment.setEvents( Collections.emptyList() );

        final ResponseEntity<ImportSummariesWebMessage> response;
        try
        {
            response = restTemplate.exchange( ENROLLMENT_UPDATE_URI, HttpMethod.PUT, new HttpEntity<>( enrollment ),
                ImportSummariesWebMessage.class, enrollment.getId() );
        }
        catch ( HttpClientErrorException e )
        {
            if ( HttpStatus.CONFLICT.equals( e.getStatusCode() ) )
            {
                throw new DhisConflictException( "Enrollment could not be updated: " + e.getResponseBodyAsString(), e );
            }

            throw e;
        }

        final ImportSummariesWebMessage result = Objects.requireNonNull( response.getBody() );

        if ( result.getStatus() != Status.OK )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful enrollment import: " +
                result.getStatus() );
        }

        return enrollment;
    }

    @Nonnull
    protected Enrollment _createOrUpdate( @Nonnull Enrollment enrollment )
    {
        return enrollment.isNewResource() ? _create( enrollment ) : _update( enrollment );
    }

    @Override
    public void persistSave( @Nonnull Collection<Enrollment> resources, boolean create, @Nullable Consumer<LocalDhisRepositoryPersistResult> resultConsumer )
    {
        if ( resources.isEmpty() )
        {
            return;
        }

        final List<Enrollment> enrollments = resources.stream().sorted( DhisResourceComparator.INSTANCE ).collect( Collectors.toList() );
        enrollments.forEach( e -> e.setEvents( Collections.emptyList() ) );

        final ResponseEntity<ImportSummariesWebMessage> response =
            restTemplate.postForEntity( create ? ENROLLMENT_CREATES_URI : ENROLLMENT_UPDATES_URI, new DhisEnrollments( enrollments ), ImportSummariesWebMessage.class );

        final ImportSummariesWebMessage result = Objects.requireNonNull( response.getBody() );
        final int size = enrollments.size();

        if ( result.getStatus() != Status.OK || result.getResponse() == null || result.getResponse().getImportSummaries().size() != size )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful import of events." );
        }

        for ( int i = 0; i < size; i++ )
        {
            final ImportSummary importSummary = result.getResponse().getImportSummaries().get( i );
            final Enrollment enrollment = enrollments.get( i );
            final LocalDhisRepositoryPersistResult persistResult;

            if ( importSummary.getStatus() == ImportStatus.ERROR )
            {
                persistResult = new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.ERROR, enrollment.getId(),
                    StringUtils.defaultIfBlank( importSummary.getDescription(), "Failed to persist enrollment." ) );
            }
            else
            {
                enrollment.resetNewResource();
                enrollment.setLocal( false );

                persistResult = new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.SUCCESS, enrollment.getId() );
            }

            if ( resultConsumer != null )
            {
                resultConsumer.accept( persistResult );
            }
        }
    }

    @Override
    @Nonnull
    public Enrollment persistSave( @Nonnull Enrollment resource )
    {
        return _createOrUpdate( resource );
    }

    @HystrixCommand( ignoreExceptions = { DhisConflictException.class, UnauthorizedException.class } )
    @Override
    public boolean delete( @Nonnull String enrollmentId )
    {
        return resourceRepositoryTemplate.deleteById( enrollmentId, Enrollment::new );
    }

    protected boolean _delete( @Nonnull String enrollmentId )
    {
        try
        {
            restTemplate.delete( "/enrollments/{id}", enrollmentId );
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

        final List<Enrollment> Enrollments = ids.stream().map( Enrollment::new ).sorted( DhisResourceComparator.INSTANCE ).collect( Collectors.toList() );
        final ResponseEntity<ImportSummariesWebMessage> response =
            restTemplate.postForEntity( ENROLLMENT_DELETES_URI, new DhisEnrollments( Enrollments ), ImportSummariesWebMessage.class );

        final ImportSummariesWebMessage result = Objects.requireNonNull( response.getBody() );
        final int size = Enrollments.size();

        if ( result.getStatus() != Status.OK || result.getResponse() == null || result.getResponse().getImportSummaries().size() != size )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful deletion of enrollment." );
        }

        if ( resultConsumer != null )
        {
            for ( int i = 0; i < size; i++ )
            {
                final ImportSummary importSummary = result.getResponse().getImportSummaries().get( i );
                final Enrollment enrollment = Enrollments.get( i );
                final LocalDhisRepositoryPersistResult persistResult;

                if ( importSummary.getStatus() == ImportStatus.ERROR )
                {
                    persistResult = new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.ERROR, enrollment.getId(),
                        org.apache.commons.lang.StringUtils.defaultIfBlank( importSummary.getDescription(), "Failed to delete enrollment." ) );
                }
                else if ( importSummary.getImportCount().getDeleted() == 0 )
                {
                    persistResult = new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.NOT_FOUND, enrollment.getId(),
                        org.apache.commons.lang.StringUtils.defaultIfBlank( importSummary.getDescription(), "Could not find enrollment." ) );
                }
                else
                {
                    persistResult = new LocalDhisRepositoryPersistResult( LocalDhisRepositoryPersistStatus.SUCCESS, enrollment.getId() );
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
}
