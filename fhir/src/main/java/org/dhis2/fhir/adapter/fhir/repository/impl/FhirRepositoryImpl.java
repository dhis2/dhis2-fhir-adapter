package org.dhis2.fhir.adapter.fhir.repository.impl;

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

import ca.uhn.fhir.context.FhirContext;
import com.google.common.collect.ArrayListMultimap;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.dhis2.fhir.adapter.auth.Authorization;
import org.dhis2.fhir.adapter.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.cache.RequestCacheContext;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.data.repository.IgnoredQueuedItemException;
import org.dhis2.fhir.adapter.dhis.DhisConflictException;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.sync.DhisResourceRepository;
import org.dhis2.fhir.adapter.fhir.data.model.QueuedFhirResourceId;
import org.dhis2.fhir.adapter.fhir.data.model.SubscriptionFhirResource;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.data.repository.QueuedFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.data.repository.SubscriptionFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.AuthenticationMethod;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientSystem;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResource;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.security.AdapterSystemAuthenticationToken;
import org.dhis2.fhir.adapter.fhir.server.ProcessedFhirItemInfoUtils;
import org.dhis2.fhir.adapter.fhir.server.StoredFhirResourceService;
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
import org.dhis2.fhir.adapter.fhir.util.FhirParserUtils;
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
import java.util.Objects;
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

    private final RequestCacheService requestCacheService;

    private final FhirClientSystemRepository fhirClientSystemRepository;

    private final FhirClientResourceRepository fhirClientResourceRepository;

    private final QueuedFhirResourceRepository queuedFhirResourceRepository;

    private final SubscriptionFhirResourceRepository subscriptionFhirResourceRepository;

    private final StoredFhirResourceService storedItemService;

    private final FhirResourceRepository fhirResourceRepository;

    private final FhirToDhisTransformerService fhirToDhisTransformerService;

    private final DhisResourceRepository dhisResourceRepository;

    private final FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    private final ZoneId zoneId = ZoneId.systemDefault();

    public FhirRepositoryImpl( @Nonnull AuthorizationContext authorizationContext,
        @Nonnull LockManager lockManager, @Nonnull RequestCacheService requestCacheService,
        @Nonnull FhirClientSystemRepository fhirClientSystemRepository,
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull QueuedFhirResourceRepository queuedFhirResourceRepository,
        @Nonnull SubscriptionFhirResourceRepository subscriptionFhirResourceRepository,
        @Nonnull StoredFhirResourceService storedItemService,
        @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirToDhisTransformerService fhirToDhisTransformerService,
        @Nonnull DhisResourceRepository dhisResourceRepository,
        @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository )
    {
        this.authorizationContext = authorizationContext;
        this.lockManager = lockManager;
        this.requestCacheService = requestCacheService;
        this.fhirClientSystemRepository = fhirClientSystemRepository;
        this.fhirClientResourceRepository = fhirClientResourceRepository;
        this.queuedFhirResourceRepository = queuedFhirResourceRepository;
        this.subscriptionFhirResourceRepository = subscriptionFhirResourceRepository;
        this.storedItemService = storedItemService;
        this.fhirResourceRepository = fhirResourceRepository;
        this.fhirToDhisTransformerService = fhirToDhisTransformerService;
        this.dhisResourceRepository = dhisResourceRepository;
        this.fhirDhisAssignmentRepository = fhirDhisAssignmentRepository;
    }

    @Override
    public void save( @Nonnull FhirClientResource fhirClientResource, @Nonnull IBaseResource resource )
    {
        authorizationContext.setAuthorization( createAuthorization( fhirClientResource.getFhirClient() ) );
        try
        {
            saveRetriedWithoutTrackedEntityInstance( fhirClientResource, resource );
        }
        finally
        {
            authorizationContext.resetAuthorization();
        }
    }

    @Nonnull
    protected Authorization createAuthorization( @Nonnull FhirClient fhirClient )
    {
        if ( fhirClient.getDhisEndpoint().getAuthenticationMethod() != AuthenticationMethod.BASIC )
        {
            throw new FatalTransformerException( "Unhandled DHIS2 authentication method: " + fhirClient.getDhisEndpoint().getAuthenticationMethod() );
        }
        return new Authorization( "Basic " + Base64.getEncoder().encodeToString(
            (fhirClient.getDhisEndpoint().getUsername() + ":" + fhirClient.getDhisEndpoint().getPassword()).getBytes( StandardCharsets.UTF_8 ) ) );
    }

    @HystrixCommand( ignoreExceptions = RetryQueueDeliveryException.class )
    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    @JmsListener( destination = "#{@fhirRepositoryConfig.fhirResourceQueue.queueName}",
        concurrency = "#{@fhirRepositoryConfig.fhirResourceQueue.listener.concurrency}" )
    public void receive( @Nonnull FhirResource fhirResource )
    {
        SecurityContextHolder.getContext().setAuthentication( new AdapterSystemAuthenticationToken() );
        try
        {
            receiveAuthenticated( fhirResource );
        }
        finally
        {
            SecurityContextHolder.clearContext();
        }
    }

    protected void receiveAuthenticated( @Nonnull FhirResource fhirResource )
    {
        logger.info( "Processing FHIR resource {} for FHIR client resource {}.",
            fhirResource.getId(), fhirResource.getFhirClientResourceId() );
        final FhirClientResource fhirClientResource =
            fhirClientResourceRepository.findOneByIdCached( fhirResource.getFhirClientResourceId() ).orElse( null );
        if ( fhirClientResource == null )
        {
            logger.warn( "FHIR client resource {} is no longer available. Skipping processing of updated FHIR resource {}.",
                fhirResource.getFhirClientResourceId(), fhirResource.getId() );
            return;
        }

        try
        {
            queuedFhirResourceRepository.dequeued( new QueuedFhirResourceId( fhirClientResource, fhirResource.getId() ) );
        }
        catch ( IgnoredQueuedItemException e )
        {
            // has already been logger with sufficient details
            return;
        }

        final FhirClient fhirClient = fhirClientResource.getFhirClient();
        final SubscriptionFhirResource subscriptionFhirResource = subscriptionFhirResourceRepository.findResource( fhirClientResource, fhirResource.getIdPart() ).orElse( null );
        final Optional<IBaseResource> resource;
        if ( fhirResource.isPersistedDataItem() )
        {
            resource = getParsedFhirResource( fhirResource, fhirClientResource, subscriptionFhirResource );
        }
        else
        {
            resource = fhirResourceRepository.findRefreshed(
                fhirClient.getId(), fhirClient.getFhirVersion(), fhirClient.getFhirEndpoint(),
                fhirClientResource.getFhirResourceType().getResourceTypeName(), fhirResource.getId() );
        }

        if ( resource.isPresent() )
        {
            final ProcessedItemInfo processedItemInfo = ProcessedFhirItemInfoUtils.create( resource.get() );
            if ( storedItemService.contains( fhirClient, processedItemInfo.toIdString( Instant.now() ) ) )
            {
                logger.info( "FHIR resource {} of FHIR client resource {} has already been stored.",
                    resource.get().getIdElement().toUnqualified(), fhirClientResource.getId() );
            }
            else
            {
                try ( final MDC.MDCCloseable c = MDC.putCloseable( "fhirId", fhirClientResource.getId() + ":" + resource.get().getIdElement().toUnqualifiedVersionless() ) )
                {
                    logger.info( "Processing FHIR resource {} of FHIR client resource {} (persisted={}).",
                        resource.get().getIdElement().toUnqualified(), fhirClientResource.getId(), fhirResource.isPersistedDataItem() );
                    try
                    {
                        save( fhirClientResource, resource.get() );
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
                    logger.info( "Processed FHIR resource {} for FHIR client resource {}.",
                        resource.get().getIdElement().toUnqualifiedVersionless(), fhirClientResource.getId() );
                }
                storedItemService.stored( fhirClient, processedItemInfo.toIdString( Instant.now() ) );
            }
        }
        else
        {
            logger.info( "FHIR resource {}/{} for FHIR client resource {} is no longer available (persisted={}). Skipping processing of updated FHIR resource.",
                fhirClientResource.getFhirResourceType().getResourceTypeName(), fhirResource.getId(), fhirClientResource.getId(), fhirResource.isPersistedDataItem() );
        }

        // must not be deleted before since it is still required when a retry must be performed
        if ( subscriptionFhirResource != null )
        {
            subscriptionFhirResourceRepository.deleteEnqueued( subscriptionFhirResource );
        }
    }

    @Nonnull
    private Optional<IBaseResource> getParsedFhirResource( @Nonnull FhirResource fhirResource, @Nonnull FhirClientResource fhirClientResource, @Nullable SubscriptionFhirResource subscriptionFhirResource )
    {
        final Optional<IBaseResource> resource;
        if ( subscriptionFhirResource == null )
        {
            resource = Optional.empty();
        }
        else
        {
            final FhirContext fhirContext = fhirResourceRepository.findFhirContext( subscriptionFhirResource.getFhirVersion() )
                .orElseThrow( () -> new FatalTransformerException( "FHIR context for FHIR version " + subscriptionFhirResource.getFhirVersion() + " has not been configured." ) );
            resource = Optional.of( Objects.requireNonNull( fhirResourceRepository.transform( fhirClientResource.getFhirClient().getId(), subscriptionFhirResource.getFhirVersion(),
                FhirParserUtils.parse( fhirContext, subscriptionFhirResource.getFhirResource(), subscriptionFhirResource.getContentType() ) ) ) );
        }
        return resource;
    }

    protected boolean saveRetriedWithoutTrackedEntityInstance( @Nonnull FhirClientResource fhirClientResource, @Nonnull IBaseResource resource )
    {
        try
        {
            if ( !saveRetried( fhirClientResource, resource, false ) )
            {
                return false;
            }
        }
        catch ( TrackedEntityInstanceNotFoundException e )
        {
            logger.info( "Tracked entity instance {} could not be found for {}. Trying to create tracked entity instance.",
                e.getResource().getIdElement().toUnqualifiedVersionless(), resource.getIdElement().toUnqualifiedVersionless() );
            if ( !createTrackedEntityInstance( fhirClientResource, e.getResource() ) )
            {
                logger.info( "Tracked entity instance {} could not be created for {}. Ignoring FHIR resource.",
                    e.getResource().getIdElement().toUnqualifiedVersionless(), resource.getIdElement().toUnqualifiedVersionless() );
                return false;
            }

            try
            {
                if ( !saveRetried( fhirClientResource, resource, false ) )
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
                saveContainedResource( fhirClientResource, containedResource );
            }
            catch ( TrackedEntityInstanceNotFoundException e )
            {
                logger.error( "Processing of contained FHIR resource {} with ID {} returned that tracked entity could not be found: {}",
                    containedResource.getClass().getSimpleName(), containedResource.getIdElement().toUnqualifiedVersionless(), e.getMessage() );
            }
        }

        return true;
    }

    protected boolean saveContainedResource( @Nonnull FhirClientResource fhirClientResource, @Nonnull IBaseResource resource )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResource( resource );
        if ( fhirResourceType == null )
        {
            logger.info( "FHIR Resource type for {} is not available. Contained FHIR Resource cannot be processed." );
            return false;
        }

        final FhirClientResource containedFhirClientResource =
            fhirClientResourceRepository.findFirstCached( fhirClientResource.getFhirClient(), fhirResourceType ).orElse( null );
        if ( containedFhirClientResource == null )
        {
            logger.info( "FHIR client {} does not define a resource {}. Contained FHIR Resource cannot be processed.",
                fhirClientResource.getFhirClient().getId(), fhirResourceType );
            return false;
        }

        return saveRetried( containedFhirClientResource, resource, true );
    }

    protected boolean createTrackedEntityInstance( @Nonnull FhirClientResource fhirClientResource, @Nonnull IBaseResource resource )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResource( resource );
        if ( fhirResourceType == null )
        {
            logger.info( "FHIR Resource type for {} is not available. Tracked entity instance for FHIR Resource {} cannot be created.",
                resource.getClass().getSimpleName(), resource.getIdElement().toVersionless() );
            return false;
        }

        final FhirClientResource trackedEntityFhirClientResource =
            fhirClientResourceRepository.findFirstCached( fhirClientResource.getFhirClient(), fhirResourceType ).orElse( null );
        if ( trackedEntityFhirClientResource == null )
        {
            logger.info( "FHIR client {} does not define a resource {}. Tracked entity instance for FHIR Resource {} cannot be created.",
                fhirClientResource.getFhirClient().getId(), fhirResourceType, resource.getIdElement() );
            return false;
        }

        final FhirClient fhirClient = trackedEntityFhirClientResource.getFhirClient();
        final Optional<IBaseResource> optionalRefreshedResource = fhirResourceRepository.findRefreshed(
            fhirClient.getId(), fhirClient.getFhirVersion(), fhirClient.getFhirEndpoint(),
            fhirResourceType.getResourceTypeName(), resource.getIdElement().getIdPart() );
        if ( !optionalRefreshedResource.isPresent() )
        {
            logger.info( "Resource {} that should be used as tracked entity resource does no longer exist for FHIR client {}.",
                resource.getIdElement(), trackedEntityFhirClientResource.getFhirClient().getId() );
            return false;
        }

        saveRetried( trackedEntityFhirClientResource, optionalRefreshedResource.get(), false );
        return true;
    }

    protected boolean saveRetried( @Nonnull FhirClientResource fhirClientResource, @Nonnull IBaseResource resource, boolean contained )
    {
        DhisConflictException lastException = null;
        for ( int i = 0; i < MAX_CONFLICT_RETRIES + 1; i++ )
        {
            try
            {
                return saveInternally( fhirClientResource, resource, contained );
            }
            catch ( DhisConflictException e )
            {
                logger.info( "DHIS2 Conflict reported. Retrying action if possible: " + e.getMessage() );
                lastException = e;
            }
        }
        throw lastException;
    }

    protected boolean saveInternally( @Nonnull FhirClientResource fhirClientResource, @Nonnull IBaseResource resource, boolean contained )
    {
        final Collection<FhirClientSystem> systems = fhirClientSystemRepository.findByFhirClient( fhirClientResource.getFhirClient() );

        final WritableFhirRequest fhirRequest = new WritableFhirRequest();
        fhirRequest.setDhisUsername( fhirClientResource.getFhirClient().getDhisEndpoint().getUsername() );
        fhirRequest.setRequestMethod( FhirRequestMethod.PUT );
        fhirRequest.setResourceType( FhirResourceType.getByResource( resource ) );
        fhirRequest.setLastUpdated( getLastUpdated( resource ) );
        fhirRequest.setResourceVersionId( (resource.getMeta() == null) ? null : resource.getMeta().getVersionId() );
        fhirRequest.setFhirClientResourceId( fhirClientResource.getId() );
        fhirRequest.setVersion( fhirClientResource.getFhirClient().getFhirVersion() );
        fhirRequest.setParameters( ArrayListMultimap.create() );
        fhirRequest.setFhirClientCode( fhirClientResource.getFhirClient().getCode() );
        fhirRequest.setResourceSystemsByType( systems.stream()
            .map( s -> new ResourceSystem( s.getFhirResourceType(), s.getSystem().getSystemUri(), s.getCodePrefix(), s.getDefaultValue(), s.getSystem().getFhirDisplayName() ) )
            .collect( Collectors.toMap( ResourceSystem::getFhirResourceType, rs -> rs ) ) );

        final ProcessedItemInfo processedItemInfo = ProcessedFhirItemInfoUtils.create( resource );
        boolean saved = false;
        FhirToDhisTransformerRequest transformerRequest = fhirToDhisTransformerService.createTransformerRequest( fhirRequest, fhirClientResource, resource, contained );
        do
        {
            FhirToDhisTransformOutcome<? extends DhisResource> outcome;
            try ( final LockContext lockContext = lockManager.begin() )
            {
                try ( final RequestCacheContext requestCacheContext = requestCacheService.createRequestCacheContext() )
                {
                    outcome = fhirToDhisTransformerService.transform( transformerRequest );
                }
                if ( outcome == null )
                {
                    transformerRequest = null;
                }
                else
                {
                    dhisResourceRepository.save( outcome.getResource() );
                    fhirDhisAssignmentRepository.saveDhisResourceId(
                        outcome.getRule(), fhirClientResource.getFhirClient(),
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
