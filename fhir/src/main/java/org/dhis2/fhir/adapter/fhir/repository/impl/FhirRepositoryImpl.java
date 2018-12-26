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
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.data.repository.IgnoredQueuedItemException;
import org.dhis2.fhir.adapter.dhis.DhisConflictException;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.sync.DhisResourceRepository;
import org.dhis2.fhir.adapter.fhir.data.model.QueuedRemoteFhirResourceId;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.data.repository.QueuedRemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.AuthenticationMethod;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscription;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionSystem;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionSystemRepository;
import org.dhis2.fhir.adapter.fhir.remote.ProcessedFhirItemInfoUtils;
import org.dhis2.fhir.adapter.fhir.remote.StoredRemoteFhirResourceService;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirResource;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.security.AdapterSystemAuthenticationToken;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerService;
import org.dhis2.fhir.adapter.fhir.transform.fhir.TrackedEntityInstanceNotFoundException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequestMethod;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.WritableFhirRequest;
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
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
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

    private final StoredRemoteFhirResourceService storedItemService;

    private final RemoteFhirResourceRepository remoteFhirResourceRepository;

    private final FhirToDhisTransformerService fhirToDhisTransformerService;

    private final DhisResourceRepository dhisResourceRepository;

    private final FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    private final ZoneId zoneId = ZoneId.systemDefault();

    public FhirRepositoryImpl( @Nonnull AuthorizationContext authorizationContext, @Nonnull LockManager lockManager,
        @Nonnull RemoteSubscriptionSystemRepository remoteSubscriptionSystemRepository,
        @Nonnull RemoteSubscriptionResourceRepository remoteSubscriptionResourceRepository,
        @Nonnull QueuedRemoteFhirResourceRepository queuedRemoteFhirResourceRepository,
        @Nonnull StoredRemoteFhirResourceService storedItemService,
        @Nonnull RemoteFhirResourceRepository remoteFhirResourceRepository, @Nonnull FhirToDhisTransformerService fhirToDhisTransformerService,
        @Nonnull DhisResourceRepository dhisResourceRepository, @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository )
    {
        this.authorizationContext = authorizationContext;
        this.lockManager = lockManager;
        this.remoteSubscriptionSystemRepository = remoteSubscriptionSystemRepository;
        this.remoteSubscriptionResourceRepository = remoteSubscriptionResourceRepository;
        this.queuedRemoteFhirResourceRepository = queuedRemoteFhirResourceRepository;
        this.storedItemService = storedItemService;
        this.remoteFhirResourceRepository = remoteFhirResourceRepository;
        this.fhirToDhisTransformerService = fhirToDhisTransformerService;
        this.dhisResourceRepository = dhisResourceRepository;
        this.fhirDhisAssignmentRepository = fhirDhisAssignmentRepository;
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
            remoteSubscriptionResourceRepository.findOneByIdCached( remoteFhirResource.getRemoteSubscriptionResourceId() ).orElse( null );
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
        final Optional<IBaseResource> resource = remoteFhirResourceRepository.findRefreshed(
            remoteSubscription.getId(), remoteSubscription.getFhirVersion(), remoteSubscription.getFhirEndpoint(),
            remoteSubscriptionResource.getFhirResourceType().getResourceTypeName(), remoteFhirResource.getId() );
        if ( resource.isPresent() )
        {
            final ProcessedItemInfo processedItemInfo = ProcessedFhirItemInfoUtils.create( resource.get() );
            if ( storedItemService.contains( remoteSubscription, processedItemInfo.toIdString( Instant.now() ) ) )
            {
                logger.info( "FHIR resource {} of remote subscription resource {} has already been stored.",
                    resource.get().getIdElement().toUnqualified(), remoteSubscriptionResource.getId() );
            }
            else
            {
                try ( final MDC.MDCCloseable c = MDC.putCloseable( "fhirId", remoteSubscriptionResource.getId() + ":" + resource.get().getIdElement().toUnqualifiedVersionless() ) )
                {
                    logger.info( "Processing FHIR resource {} of remote subscription resource {}.",
                        resource.get().getIdElement().toUnqualified(), remoteSubscriptionResource.getId() );
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
                storedItemService.stored( remoteSubscription, processedItemInfo.toIdString( Instant.now() ) );
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
                {
                    return false;
                }
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
        final Optional<IBaseResource> optionalRefreshedResource = remoteFhirResourceRepository.findRefreshed(
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
            .map( s -> new ResourceSystem( s.getFhirResourceType(), s.getSystem().getSystemUri(), s.getCodePrefix(), s.getSystem().getFhirDisplayName() ) )
            .collect( Collectors.toMap( ResourceSystem::getFhirResourceType, rs -> rs ) ) );

        final ProcessedItemInfo processedItemInfo = ProcessedFhirItemInfoUtils.create( resource );
        boolean saved = false;
        FhirToDhisTransformerRequest transformerRequest = fhirToDhisTransformerService.createTransformerRequest( fhirRequest, subscriptionResource, resource, contained );
        do
        {
            FhirToDhisTransformOutcome<? extends DhisResource> outcome;
            try ( final LockContext lockContext = lockManager.begin() )
            {
                final HystrixRequestContext context = HystrixRequestContext.initializeContext();
                try
                {
                    outcome = fhirToDhisTransformerService.transform( transformerRequest );
                }
                finally
                {
                    context.shutdown();
                }
                if ( outcome == null )
                {
                    transformerRequest = null;
                }
                else
                {
                    dhisResourceRepository.save( outcome.getResource() );
                    fhirDhisAssignmentRepository.saveDhisResourceId(
                        outcome.getRule(), subscriptionResource.getRemoteSubscription(),
                        resource.getIdElement(), outcome.getResource().getResourceId() );
                    saved = true;
                    transformerRequest = outcome.getNextTransformerRequest();
                }
            }
        }
        while ( transformerRequest != null );
        return saved;
    }

    @Nullable
    private ZonedDateTime getLastUpdated( @Nonnull IBaseResource resource )
    {
        final Date lastUpdated = (resource.getMeta() == null) ? null : resource.getMeta().getLastUpdated();
        return (lastUpdated == null) ? null : lastUpdated.toInstant().atZone( zoneId );
    }
}
