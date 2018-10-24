package org.dhis2.fhir.adapter.fhir.repository.impl;

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

import com.google.common.collect.ArrayListMultimap;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.dhis2.fhir.adapter.auth.Authorization;
import org.dhis2.fhir.adapter.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionSystem;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionSystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerService;
import org.dhis2.fhir.adapter.fhir.transform.model.FhirRequestMethod;
import org.dhis2.fhir.adapter.fhir.transform.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.model.WritableFhirRequest;
import org.dhis2.fhir.adapter.util.ExceptionUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FhirRepositoryImpl implements FhirRepository
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    public static final int MAX_CONFLICT_RETRIES = 2;

    private final AuthorizationContext authorizationContext;

    private final RemoteSubscriptionSystemRepository remoteSubscriptionSystemRepository;

    private final FhirToDhisTransformerService fhirToDhisTransformerService;

    private final TrackedEntityService trackedEntityService;

    private final EnrollmentService enrollmentService;

    private final EventService eventService;

    private final ZoneId zoneId = ZoneId.systemDefault();

    public FhirRepositoryImpl( @Nonnull AuthorizationContext authorizationContext, @Nonnull RemoteSubscriptionSystemRepository remoteSubscriptionSystemRepository,
        @Nonnull FhirToDhisTransformerService fhirToDhisTransformerService, @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull EnrollmentService enrollmentService, @Nonnull EventService eventService )
    {
        this.authorizationContext = authorizationContext;
        this.remoteSubscriptionSystemRepository = remoteSubscriptionSystemRepository;
        this.fhirToDhisTransformerService = fhirToDhisTransformerService;
        this.trackedEntityService = trackedEntityService;
        this.enrollmentService = enrollmentService;
        this.eventService = eventService;
    }

    @Override
    public void save( @Nonnull RemoteSubscriptionResource subscriptionResource, @Nonnull IBaseResource resource )
    {
        authorizationContext.setAuthorization( new Authorization( subscriptionResource.getRemoteSubscription().getDhisAuthorizationHeader() ) );
        try
        {
            saveRetried( subscriptionResource, resource );
        }
        finally
        {
            authorizationContext.resetAuthorization();
        }
    }

    protected void saveRetried( @Nonnull RemoteSubscriptionResource subscriptionResource, @Nonnull IBaseResource resource )
    {
        RuntimeException lastException = null;
        for ( int i = 0; i < MAX_CONFLICT_RETRIES + 1; i++ )
        {
            try
            {
                saveInternally( subscriptionResource, resource );
                return;
            }
            catch ( RuntimeException e )
            {
                // case will be handled with non runtime exceptions in non-demo adapter (error handling will be enhanced)
                final HttpClientErrorException clientErrorException = (HttpClientErrorException) ExceptionUtils.findCause( e, HttpClientErrorException.class );
                if ( (clientErrorException == null) || !HttpStatus.CONFLICT.equals( clientErrorException.getStatusCode() ) )
                {
                    throw e;
                }
                logger.info( "DHIS2 Conflict reported. Retrying action if possible: " + clientErrorException.getResponseBodyAsString(), e );
                lastException = e;
            }
        }
        throw new RuntimeException( "Could not resolve DHIS2 reported Conflict after " + MAX_CONFLICT_RETRIES + " retries.", lastException );
    }

    @SuppressWarnings( "unchecked" )
    protected void saveInternally( @Nonnull RemoteSubscriptionResource subscriptionResource, @Nonnull IBaseResource resource )
    {
        final Collection<RemoteSubscriptionSystem> systems = remoteSubscriptionSystemRepository.findByRemoteSubscription( subscriptionResource.getRemoteSubscription() );

        final WritableFhirRequest fhirRequest = new WritableFhirRequest();
        fhirRequest.setDhisUsername( subscriptionResource.getRemoteSubscription().getDhisUsername() );
        fhirRequest.setRequestMethod( FhirRequestMethod.PUT );
        fhirRequest.setResourceType( FhirResourceType.getByResource( resource ) );
        fhirRequest.setLastUpdated( getLastUpdated( resource ) );
        fhirRequest.setResourceVersionId( (resource.getMeta() == null) ? null : resource.getMeta().getVersionId() );
        fhirRequest.setRemoteSubscriptionResourceId( subscriptionResource.getId() );
        fhirRequest.setVersion( subscriptionResource.getRemoteSubscription().getFhirVersion() );
        fhirRequest.setParameters( ArrayListMultimap.create() );
        fhirRequest.setResourceSystemsByType( systems.stream()
            .map( s -> new ResourceSystem( s.getFhirResourceType(), s.getSystem().getSystemUri() ) )
            .collect( Collectors.toMap( ResourceSystem::getFhirResourceType, rs -> rs ) ) );

        final FhirToDhisTransformOutcome<? extends DhisResource> outcome;
        final HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try
        {
            outcome = fhirToDhisTransformerService.transform( fhirToDhisTransformerService.createContext( fhirRequest ), resource );
        }
        finally
        {
            context.shutdown();
        }

        if ( outcome != null )
        {
            final DhisResource dhisResource;
            switch ( outcome.getResource().getResourceType() )
            {
                case TRACKED_ENTITY:
                    dhisResource = persistTrackedEntityOutcome( (FhirToDhisTransformOutcome<TrackedEntityInstance>) outcome );
                    break;
                case PROGRAM_STAGE_EVENT:
                    dhisResource = persistProgramStageEventOutcome( (FhirToDhisTransformOutcome<Event>) outcome );
                    break;
                default:
                    throw new AssertionError( "Unhandled DHIS resource type: " + outcome.getResource().getResourceType() );
            }
            resource.setId( dhisResource.getId() );
        }
    }

    @Nonnull
    private DhisResource persistTrackedEntityOutcome( @Nonnull FhirToDhisTransformOutcome<TrackedEntityInstance> outcome )
    {
        DhisResource dhisResource;
        logger.info( "Persisting tracked entity instance." );
        dhisResource = trackedEntityService.createOrUpdate( outcome.getResource() );
        logger.info( "Persisted tracked entity instance {}.", dhisResource.getId() );
        return dhisResource;
    }

    @Nonnull
    private DhisResource persistProgramStageEventOutcome( @Nonnull FhirToDhisTransformOutcome<Event> outcome )
    {
        DhisResource dhisResource = null;
        final Event event = outcome.getResource();
        if ( (event.getTrackedEntityInstance() != null) && event.getTrackedEntityInstance().isModified() )
        {
            logger.info( "Persisting tracked entity instance." );
            trackedEntityService.createOrUpdate( event.getTrackedEntityInstance() );
            logger.info( "Persisted tracked entity instance {}.", event.getTrackedEntityInstance().getId() );
        }
        if ( event.getEnrollment().isNewResource() )
        {
            logger.info( "Creating new enrollment." );
            event.getEnrollment().setEvents( Collections.singletonList( event ) );
            dhisResource = enrollmentService.create( event.getEnrollment() ).getEvents().get( 0 );
            logger.info( "Created new enrollment {} with new event.", event.getEnrollment().getId(), event.getId() );
        }
        else
        {
            final List<Event> events = event.getEnrollment().getEvents();
            if ( event.getEnrollment().isModified() )
            {
                logger.info( "Updating existing enrollment." );
                event.setEnrollment( enrollmentService.update( event.getEnrollment() ) );
                logger.info( "Updated existing enrollment {}.", event.getEnrollment().getId() );
            }
            logger.info( "Persisting event." );
            for ( final Event e : events )
            {
                if ( e.isModified() || e.isAnyDataValueModified() )
                {
                    logger.info( "Persisting event." );
                    dhisResource = eventService.createOrMinimalUpdate( event );
                    logger.info( "Persisted event {}.", dhisResource.getId() );
                }
            }
            if ( dhisResource == null )
            {
                logger.info( "Persisting event." );
                dhisResource = eventService.createOrMinimalUpdate( event );
                logger.info( "Persisted event {}.", dhisResource.getId() );
            }
        }
        return dhisResource;
    }

    @Nullable
    private ZonedDateTime getLastUpdated( @Nonnull IBaseResource resource )
    {
        final Date lastUpdated = (resource.getMeta() == null) ? null : resource.getMeta().getLastUpdated();
        return (lastUpdated == null) ? null : lastUpdated.toInstant().atZone( zoneId );
    }
}
