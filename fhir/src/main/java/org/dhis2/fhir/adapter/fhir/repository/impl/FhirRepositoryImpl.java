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
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.dhis2.fhir.adapter.auth.Authorization;
import org.dhis2.fhir.adapter.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.data.repository.IgnoredQueuedItemException;
import org.dhis2.fhir.adapter.dhis.DhisConflictException;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.tracker.program.EnrollmentService;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.fhir.data.model.QueuedRemoteFhirResourceId;
import org.dhis2.fhir.adapter.fhir.data.repository.QueuedRemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.AuthenticationMethod;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscription;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionSystem;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionSystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirRepository;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirResource;
import org.dhis2.fhir.adapter.fhir.security.AdapterSystemAuthenticationToken;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerService;
import org.dhis2.fhir.adapter.fhir.transform.TrackedEntityInstanceNotFoundException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.model.FhirRequestMethod;
import org.dhis2.fhir.adapter.fhir.transform.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.model.WritableFhirRequest;
import org.dhis2.fhir.adapter.lock.LockContext;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.dhis2.fhir.adapter.queue.RetryQueueDeliveryException;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FhirRepository}.
 *
 * @author volsch
 */
@Component
public class FhirRepositoryImpl implements FhirRepository
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    public static final int MAX_CONFLICT_RETRIES = 2;

    private final AuthorizationContext authorizationContext;

    private final LockManager lockManager;

    private final RemoteSubscriptionSystemRepository remoteSubscriptionSystemRepository;

    private final RemoteSubscriptionResourceRepository remoteSubscriptionResourceRepository;

    private final QueuedRemoteFhirResourceRepository queuedRemoteFhirResourceRepository;

    private final RemoteFhirRepository remoteFhirRepository;

    private final FhirToDhisTransformerService fhirToDhisTransformerService;

    private final TrackedEntityService trackedEntityService;

    private final EnrollmentService enrollmentService;

    private final EventService eventService;

    private final ZoneId zoneId = ZoneId.systemDefault();

    public FhirRepositoryImpl( @Nonnull AuthorizationContext authorizationContext, @Nonnull LockManager lockManager,
        @Nonnull RemoteSubscriptionSystemRepository remoteSubscriptionSystemRepository,
        @Nonnull RemoteSubscriptionResourceRepository remoteSubscriptionResourceRepository,
        @Nonnull QueuedRemoteFhirResourceRepository queuedRemoteFhirResourceRepository,
        @Nonnull RemoteFhirRepository remoteFhirRepository, @Nonnull FhirToDhisTransformerService fhirToDhisTransformerService,
        @Nonnull TrackedEntityService trackedEntityService, @Nonnull EnrollmentService enrollmentService, @Nonnull EventService eventService )
    {
        this.authorizationContext = authorizationContext;
        this.lockManager = lockManager;
        this.remoteSubscriptionSystemRepository = remoteSubscriptionSystemRepository;
        this.remoteSubscriptionResourceRepository = remoteSubscriptionResourceRepository;
        this.queuedRemoteFhirResourceRepository = queuedRemoteFhirResourceRepository;
        this.remoteFhirRepository = remoteFhirRepository;
        this.fhirToDhisTransformerService = fhirToDhisTransformerService;
        this.trackedEntityService = trackedEntityService;
        this.enrollmentService = enrollmentService;
        this.eventService = eventService;
    }

    @Override
    public void save( @Nonnull RemoteSubscriptionResource subscriptionResource, @Nonnull IBaseResource resource )
    {
        authorizationContext.setAuthorization( createAuthorization( subscriptionResource.getRemoteSubscription() ) );
        try
        {
            saveRetriedWithoutTrackedEntityInstance( subscriptionResource, resource );
        }
        finally
        {
            authorizationContext.resetAuthorization();
        }
    }

    @Nonnull
    protected Authorization createAuthorization( @Nonnull RemoteSubscription remoteSubscription )
    {
        if ( remoteSubscription.getDhisEndpoint().getAuthenticationMethod() != AuthenticationMethod.BASIC )
        {
            throw new FatalTransformerException( "Unhandled DHIS2 authentication method: " + remoteSubscription.getDhisEndpoint().getAuthenticationMethod() );
        }
        return new Authorization( "Basic " + Base64.getEncoder().encodeToString(
            (remoteSubscription.getDhisEndpoint().getUsername() + ":" + remoteSubscription.getDhisEndpoint().getPassword()).getBytes( StandardCharsets.UTF_8 ) ) );
    }

    @HystrixCommand( ignoreExceptions = RetryQueueDeliveryException.class )
    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    @JmsListener( destination = "#{@fhirRepositoryConfig.fhirResourceQueue.queueName}",
        concurrency = "#{@fhirRepositoryConfig.fhirResourceQueue.listener.concurrency}" )
    public void receive( @Nonnull RemoteFhirResource remoteFhirResource )
    {
        SecurityContextHolder.getContext().setAuthentication( new AdapterSystemAuthenticationToken() );
        try
        {
            receiveAuthenticated( remoteFhirResource );
        }
        finally
        {
            SecurityContextHolder.clearContext();
        }
    }

    protected void receiveAuthenticated( @Nonnull RemoteFhirResource remoteFhirResource )
    {
        logger.info( "Processing FHIR resource {} for remote subscription resource {}.",
            remoteFhirResource.getId(), remoteFhirResource.getRemoteSubscriptionResourceId() );
        final RemoteSubscriptionResource remoteSubscriptionResource =
            remoteSubscriptionResourceRepository.findByIdCached( remoteFhirResource.getRemoteSubscriptionResourceId() ).orElse( null );
        if ( remoteSubscriptionResource == null )
        {
            logger.warn( "Remote subscription resource {} is no longer available. Skipping processing of updated FHIR resource {}.",
                remoteFhirResource.getRemoteSubscriptionResourceId(), remoteFhirResource.getId() );
            return;
        }

        try
        {
            queuedRemoteFhirResourceRepository.dequeued( new QueuedRemoteFhirResourceId( remoteSubscriptionResource, remoteFhirResource.getId() ) );
        }
        catch ( IgnoredQueuedItemException e )
        {
            // has already been logger with sufficient details
            return;
        }

        final RemoteSubscription remoteSubscription = remoteSubscriptionResource.getRemoteSubscription();
        final Optional<IBaseResource> resource = remoteFhirRepository.findRefreshed(
            remoteSubscription.getId(), remoteSubscription.getFhirVersion(), remoteSubscription.getFhirEndpoint(),
            remoteSubscriptionResource.getFhirResourceType().getResourceTypeName(), remoteFhirResource.getId() );
        if ( resource.isPresent() )
        {
            try ( final MDC.MDCCloseable c = MDC.putCloseable( "fhirId", remoteSubscriptionResource.getId() + ":" + resource.get().getIdElement().toUnqualifiedVersionless() ) )
            {
                logger.info( "Processing FHIR resource {} of remote subscription resource {}.",
                    resource.get().getIdElement().toUnqualifiedVersionless(), remoteSubscriptionResource.getId() );
                try
                {
                    save( remoteSubscriptionResource, resource.get() );
                }
                catch ( DhisConflictException e )
                {
                    logger.warn( "Processing of data of FHIR resource caused a conflict on DHIS2. Skipping FHIR resource because of the occurred conflict: {}", e.getMessage() );
                }
                catch ( TransformerDataException | TransformerMappingException e )
                {
                    logger.warn( "Processing of data of FHIR resource caused a transformation error. Retrying processing later because of resolvable issue: {}", e.getMessage() );
                    throw new RetryQueueDeliveryException( e );
                }
                logger.info( "Processed FHIR resource {} for remote subscription resource {}.",
                    resource.get().getIdElement().toUnqualifiedVersionless(), remoteSubscriptionResource.getId() );
            }
        }
        else
        {
            logger.info( "FHIR resource {}/{} for remote subscription resource {} is no longer available. Skipping processing of updated FHIR resource.",
                remoteSubscriptionResource.getFhirResourceType().getResourceTypeName(), remoteFhirResource.getId(), remoteSubscriptionResource.getId() );
        }
    }

    protected boolean saveRetriedWithoutTrackedEntityInstance( @Nonnull RemoteSubscriptionResource subscriptionResource, @Nonnull IBaseResource resource )
    {
        try
        {
            if ( !saveRetried( subscriptionResource, resource, false ) )
            {
                return false;
            }
        }
        catch ( TrackedEntityInstanceNotFoundException e )
        {
            logger.info( "Tracked entity instance {} could not be found for {}. Trying to create tracked entity instance.",
                e.getResource().getIdElement().toUnqualifiedVersionless(), resource.getIdElement().toUnqualifiedVersionless() );
            if ( !createTrackedEntityInstance( subscriptionResource, e.getResource() ) )
            {
                logger.info( "Tracked entity instance {} could not be created for {}. Ignoring FHIR resource.",
                    e.getResource().getIdElement().toUnqualifiedVersionless(), resource.getIdElement().toUnqualifiedVersionless() );
                return false;
            }

            try
            {
                if ( !saveRetried( subscriptionResource, resource, false ) )
                    return false;
            }
            catch ( TrackedEntityInstanceNotFoundException e2 )
            {
                logger.info( "Tracked entity instance {} could not be found for {}. Ignoring FHIR resource.",
                    e2.getResource().getIdElement().toUnqualifiedVersionless(), resource.getIdElement().toUnqualifiedVersionless() );
                return false;
            }
        }

        for ( final IAnyResource containedResource : ((IDomainResource) resource).getContained() )
        {
            logger.info( "Processing contained FHIR resource {} with ID {}.",
                containedResource.getClass().getSimpleName(), containedResource.getIdElement().toUnqualifiedVersionless() );
            try
            {
                saveContainedResource( subscriptionResource, containedResource );
            }
            catch ( TrackedEntityInstanceNotFoundException e )
            {
                logger.error( "Processing of contained FHIR resource {} with ID {} returned that tracked entity could not be found: {}",
                    containedResource.getClass().getSimpleName(), containedResource.getIdElement().toUnqualifiedVersionless(), e.getMessage() );
            }
        }

        return true;
    }

    protected boolean saveContainedResource( @Nonnull RemoteSubscriptionResource subscriptionResource, @Nonnull IBaseResource resource )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResource( resource );
        if ( fhirResourceType == null )
        {
            logger.info( "FHIR Resource type for {} is not available. Contained FHIR Resource cannot be processed." );
            return false;
        }

        final RemoteSubscriptionResource containedRemoteSubscriptionResource =
            remoteSubscriptionResourceRepository.findFirstCached( subscriptionResource.getRemoteSubscription(), fhirResourceType ).orElse( null );
        if ( containedRemoteSubscriptionResource == null )
        {
            logger.info( "Remote subscription {} does not define a resource {}. Contained FHIR Resource cannot be processed.",
                subscriptionResource.getRemoteSubscription().getId(), fhirResourceType );
            return false;
        }

        return saveRetried( containedRemoteSubscriptionResource, resource, true );
    }

    protected boolean createTrackedEntityInstance( @Nonnull RemoteSubscriptionResource subscriptionResource, @Nonnull IBaseResource resource )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResource( resource );
        if ( fhirResourceType == null )
        {
            logger.info( "FHIR Resource type for {} is not available. Tracked entity instance for FHIR Resource {} cannot be created.",
                resource.getClass().getSimpleName(), resource.getIdElement().toVersionless() );
            return false;
        }

        final RemoteSubscriptionResource trackedEntityRemoteSubscriptionResource =
            remoteSubscriptionResourceRepository.findFirstCached( subscriptionResource.getRemoteSubscription(), fhirResourceType ).orElse( null );
        if ( trackedEntityRemoteSubscriptionResource == null )
        {
            logger.info( "Remote subscription {} does not define a resource {}. Tracked entity instance for FHIR Resource {} cannot be created.",
                subscriptionResource.getRemoteSubscription().getId(), fhirResourceType, resource.getIdElement() );
            return false;
        }

        final RemoteSubscription remoteSubscription = trackedEntityRemoteSubscriptionResource.getRemoteSubscription();
        final Optional<IBaseResource> optionalRefreshedResource = remoteFhirRepository.findRefreshed(
            remoteSubscription.getId(), remoteSubscription.getFhirVersion(), remoteSubscription.getFhirEndpoint(),
            fhirResourceType.getResourceTypeName(), resource.getIdElement().getIdPart() );
        if ( !optionalRefreshedResource.isPresent() )
        {
            logger.info( "Resource {} that should be used as tracked entity resource does no longer exist for remote subscription {}.",
                resource.getIdElement(), trackedEntityRemoteSubscriptionResource.getRemoteSubscription().getId() );
            return false;
        }

        saveRetried( trackedEntityRemoteSubscriptionResource, optionalRefreshedResource.get(), false );
        return true;
    }

    protected boolean saveRetried( @Nonnull RemoteSubscriptionResource subscriptionResource, @Nonnull IBaseResource resource, boolean contained )
    {
        DhisConflictException lastException = null;
        for ( int i = 0; i < MAX_CONFLICT_RETRIES + 1; i++ )
        {
            try
            {
                return saveInternally( subscriptionResource, resource, contained );
            }
            catch ( DhisConflictException e )
            {
                logger.info( "DHIS2 Conflict reported. Retrying action if possible: " + e.getMessage() );
                lastException = e;
            }
        }
        throw lastException;
    }

    @SuppressWarnings( "unchecked" )
    protected boolean saveInternally( @Nonnull RemoteSubscriptionResource subscriptionResource, @Nonnull IBaseResource resource, boolean contained )
    {
        final Collection<RemoteSubscriptionSystem> systems = remoteSubscriptionSystemRepository.findByRemoteSubscription( subscriptionResource.getRemoteSubscription() );

        final WritableFhirRequest fhirRequest = new WritableFhirRequest();
        fhirRequest.setDhisUsername( subscriptionResource.getRemoteSubscription().getDhisEndpoint().getUsername() );
        fhirRequest.setRequestMethod( FhirRequestMethod.PUT );
        fhirRequest.setResourceType( FhirResourceType.getByResource( resource ) );
        fhirRequest.setLastUpdated( getLastUpdated( resource ) );
        fhirRequest.setResourceVersionId( (resource.getMeta() == null) ? null : resource.getMeta().getVersionId() );
        fhirRequest.setRemoteSubscriptionResourceId( subscriptionResource.getId() );
        fhirRequest.setVersion( subscriptionResource.getRemoteSubscription().getFhirVersion() );
        fhirRequest.setParameters( ArrayListMultimap.create() );
        fhirRequest.setRemoteSubscriptionCode( subscriptionResource.getRemoteSubscription().getCode() );
        fhirRequest.setResourceSystemsByType( systems.stream()
            .map( s -> new ResourceSystem( s.getFhirResourceType(), s.getSystem().getSystemUri(), s.getCodePrefix() ) )
            .collect( Collectors.toMap( ResourceSystem::getFhirResourceType, rs -> rs ) ) );

        final FhirToDhisTransformOutcome<? extends DhisResource> outcome;
        try ( final LockContext lockContext = lockManager.begin() )
        {
            final HystrixRequestContext context = HystrixRequestContext.initializeContext();
            try
            {
                outcome = fhirToDhisTransformerService.transform( fhirToDhisTransformerService.createContext( fhirRequest ), resource, contained );
            }
            finally
            {
                context.shutdown();
            }

            if ( outcome != null )
            {
                switch ( outcome.getResource().getResourceType() )
                {
                    case TRACKED_ENTITY:
                        persistTrackedEntityOutcome( (FhirToDhisTransformOutcome<TrackedEntityInstance>) outcome );
                        break;
                    case PROGRAM_STAGE_EVENT:
                        persistProgramStageEventOutcome( (FhirToDhisTransformOutcome<Event>) outcome );
                        break;
                    default:
                        throw new AssertionError( "Unhandled DHIS resource type: " + outcome.getResource().getResourceType() );
                }
            }
        }
        return (outcome != null);
    }

    @Nonnull
    private DhisResource persistTrackedEntityOutcome( @Nonnull FhirToDhisTransformOutcome<TrackedEntityInstance> outcome )
    {
        final DhisResource dhisResource;
        logger.debug( "Persisting tracked entity instance." );
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
            logger.debug( "Persisting tracked entity instance." );
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
            for ( final Event e : events )
            {
                if ( e.isModified() || e.isAnyDataValueModified() )
                {
                    logger.debug( "Persisting event." );
                    dhisResource = eventService.createOrMinimalUpdate( event );
                    logger.info( "Persisted event {}.", dhisResource.getId() );
                }
            }
            if ( dhisResource == null )
            {
                logger.debug( "Persisting event." );
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
