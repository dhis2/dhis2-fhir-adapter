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
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServer;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerSystem;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirServerResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirServerSystemRepository;
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

    private final FhirServerSystemRepository fhirServerSystemRepository;

    private final FhirServerResourceRepository fhirServerResourceRepository;

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
        @Nonnull FhirServerSystemRepository fhirServerSystemRepository,
        @Nonnull FhirServerResourceRepository fhirServerResourceRepository,
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
        this.fhirServerSystemRepository = fhirServerSystemRepository;
        this.fhirServerResourceRepository = fhirServerResourceRepository;
        this.queuedFhirResourceRepository = queuedFhirResourceRepository;
        this.subscriptionFhirResourceRepository = subscriptionFhirResourceRepository;
        this.storedItemService = storedItemService;
        this.fhirResourceRepository = fhirResourceRepository;
        this.fhirToDhisTransformerService = fhirToDhisTransformerService;
        this.dhisResourceRepository = dhisResourceRepository;
        this.fhirDhisAssignmentRepository = fhirDhisAssignmentRepository;
    }

    @Override
    public void save( @Nonnull FhirServerResource fhirServerResource, @Nonnull IBaseResource resource )
    {
        authorizationContext.setAuthorization( createAuthorization( fhirServerResource.getFhirServer() ) );
        try
        {
            saveRetriedWithoutTrackedEntityInstance( fhirServerResource, resource );
        }
        finally
        {
            authorizationContext.resetAuthorization();
        }
    }

    @Nonnull
    protected Authorization createAuthorization( @Nonnull FhirServer fhirServer )
    {
        if ( fhirServer.getDhisEndpoint().getAuthenticationMethod() != AuthenticationMethod.BASIC )
        {
            throw new FatalTransformerException( "Unhandled DHIS2 authentication method: " + fhirServer.getDhisEndpoint().getAuthenticationMethod() );
        }
        return new Authorization( "Basic " + Base64.getEncoder().encodeToString(
            (fhirServer.getDhisEndpoint().getUsername() + ":" + fhirServer.getDhisEndpoint().getPassword()).getBytes( StandardCharsets.UTF_8 ) ) );
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
        logger.info( "Processing FHIR resource {} for FHIR server resource {}.",
            fhirResource.getId(), fhirResource.getFhirServerResourceId() );
        final FhirServerResource fhirServerResource =
            fhirServerResourceRepository.findOneByIdCached( fhirResource.getFhirServerResourceId() ).orElse( null );
        if ( fhirServerResource == null )
        {
            logger.warn( "FHIR server resource {} is no longer available. Skipping processing of updated FHIR resource {}.",
                fhirResource.getFhirServerResourceId(), fhirResource.getId() );
            return;
        }

        try
        {
            queuedFhirResourceRepository.dequeued( new QueuedFhirResourceId( fhirServerResource, fhirResource.getId() ) );
        }
        catch ( IgnoredQueuedItemException e )
        {
            // has already been logger with sufficient details
            return;
        }

        final FhirServer fhirServer = fhirServerResource.getFhirServer();
        final SubscriptionFhirResource subscriptionFhirResource = subscriptionFhirResourceRepository.findResource( fhirServerResource, fhirResource.getIdPart() ).orElse( null );
        final Optional<IBaseResource> resource;
        if ( fhirResource.isPersistedDataItem() )
        {
            resource = getParsedFhirResource( fhirResource, fhirServerResource, subscriptionFhirResource );
        }
        else
        {
            resource = fhirResourceRepository.findRefreshed(
                fhirServer.getId(), fhirServer.getFhirVersion(), fhirServer.getFhirEndpoint(),
                fhirServerResource.getFhirResourceType().getResourceTypeName(), fhirResource.getId() );
        }

        if ( resource.isPresent() )
        {
            final ProcessedItemInfo processedItemInfo = ProcessedFhirItemInfoUtils.create( resource.get() );
            if ( storedItemService.contains( fhirServer, processedItemInfo.toIdString( Instant.now() ) ) )
            {
                logger.info( "FHIR resource {} of FHIR server resource {} has already been stored.",
                    resource.get().getIdElement().toUnqualified(), fhirServerResource.getId() );
            }
            else
            {
                try ( final MDC.MDCCloseable c = MDC.putCloseable( "fhirId", fhirServerResource.getId() + ":" + resource.get().getIdElement().toUnqualifiedVersionless() ) )
                {
                    logger.info( "Processing FHIR resource {} of FHIR server resource {} (persisted={}).",
                        resource.get().getIdElement().toUnqualified(), fhirServerResource.getId(), fhirResource.isPersistedDataItem() );
                    try
                    {
                        save( fhirServerResource, resource.get() );
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
                    logger.info( "Processed FHIR resource {} for FHIR server resource {}.",
                        resource.get().getIdElement().toUnqualifiedVersionless(), fhirServerResource.getId() );
                }
                storedItemService.stored( fhirServer, processedItemInfo.toIdString( Instant.now() ) );
            }
        }
        else
        {
            logger.info( "FHIR resource {}/{} for FHIR server resource {} is no longer available (persisted={}). Skipping processing of updated FHIR resource.",
                fhirServerResource.getFhirResourceType().getResourceTypeName(), fhirResource.getId(), fhirServerResource.getId(), fhirResource.isPersistedDataItem() );
        }

        // must not be deleted before since it is still required when a retry must be performed
        if ( subscriptionFhirResource != null )
        {
            subscriptionFhirResourceRepository.deleteEnqueued( subscriptionFhirResource );
        }
    }

    @Nonnull
    private Optional<IBaseResource> getParsedFhirResource( @Nonnull FhirResource fhirResource, @Nonnull FhirServerResource fhirServerResource, @Nullable SubscriptionFhirResource subscriptionFhirResource )
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
            resource = Optional.of( Objects.requireNonNull( fhirResourceRepository.transform( fhirServerResource.getFhirServer().getId(), subscriptionFhirResource.getFhirVersion(),
                FhirParserUtils.parse( fhirContext, subscriptionFhirResource.getFhirResource(), subscriptionFhirResource.getContentType() ) ) ) );
        }
        return resource;
    }

    protected boolean saveRetriedWithoutTrackedEntityInstance( @Nonnull FhirServerResource fhirServerResource, @Nonnull IBaseResource resource )
    {
        try
        {
            if ( !saveRetried( fhirServerResource, resource, false ) )
            {
                return false;
            }
        }
        catch ( TrackedEntityInstanceNotFoundException e )
        {
            logger.info( "Tracked entity instance {} could not be found for {}. Trying to create tracked entity instance.",
                e.getResource().getIdElement().toUnqualifiedVersionless(), resource.getIdElement().toUnqualifiedVersionless() );
            if ( !createTrackedEntityInstance( fhirServerResource, e.getResource() ) )
            {
                logger.info( "Tracked entity instance {} could not be created for {}. Ignoring FHIR resource.",
                    e.getResource().getIdElement().toUnqualifiedVersionless(), resource.getIdElement().toUnqualifiedVersionless() );
                return false;
            }

            try
            {
                if ( !saveRetried( fhirServerResource, resource, false ) )
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
                saveContainedResource( fhirServerResource, containedResource );
            }
            catch ( TrackedEntityInstanceNotFoundException e )
            {
                logger.error( "Processing of contained FHIR resource {} with ID {} returned that tracked entity could not be found: {}",
                    containedResource.getClass().getSimpleName(), containedResource.getIdElement().toUnqualifiedVersionless(), e.getMessage() );
            }
        }

        return true;
    }

    protected boolean saveContainedResource( @Nonnull FhirServerResource fhirServerResource, @Nonnull IBaseResource resource )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResource( resource );
        if ( fhirResourceType == null )
        {
            logger.info( "FHIR Resource type for {} is not available. Contained FHIR Resource cannot be processed." );
            return false;
        }

        final FhirServerResource containedFhirServerResource =
            fhirServerResourceRepository.findFirstCached( fhirServerResource.getFhirServer(), fhirResourceType ).orElse( null );
        if ( containedFhirServerResource == null )
        {
            logger.info( "FHIR server {} does not define a resource {}. Contained FHIR Resource cannot be processed.",
                fhirServerResource.getFhirServer().getId(), fhirResourceType );
            return false;
        }

        return saveRetried( containedFhirServerResource, resource, true );
    }

    protected boolean createTrackedEntityInstance( @Nonnull FhirServerResource fhirServerResource, @Nonnull IBaseResource resource )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResource( resource );
        if ( fhirResourceType == null )
        {
            logger.info( "FHIR Resource type for {} is not available. Tracked entity instance for FHIR Resource {} cannot be created.",
                resource.getClass().getSimpleName(), resource.getIdElement().toVersionless() );
            return false;
        }

        final FhirServerResource trackedEntityFhirServerResource =
            fhirServerResourceRepository.findFirstCached( fhirServerResource.getFhirServer(), fhirResourceType ).orElse( null );
        if ( trackedEntityFhirServerResource == null )
        {
            logger.info( "FHIR server {} does not define a resource {}. Tracked entity instance for FHIR Resource {} cannot be created.",
                fhirServerResource.getFhirServer().getId(), fhirResourceType, resource.getIdElement() );
            return false;
        }

        final FhirServer fhirServer = trackedEntityFhirServerResource.getFhirServer();
        final Optional<IBaseResource> optionalRefreshedResource = fhirResourceRepository.findRefreshed(
            fhirServer.getId(), fhirServer.getFhirVersion(), fhirServer.getFhirEndpoint(),
            fhirResourceType.getResourceTypeName(), resource.getIdElement().getIdPart() );
        if ( !optionalRefreshedResource.isPresent() )
        {
            logger.info( "Resource {} that should be used as tracked entity resource does no longer exist for FHIR server {}.",
                resource.getIdElement(), trackedEntityFhirServerResource.getFhirServer().getId() );
            return false;
        }

        saveRetried( trackedEntityFhirServerResource, optionalRefreshedResource.get(), false );
        return true;
    }

    protected boolean saveRetried( @Nonnull FhirServerResource fhirServerResource, @Nonnull IBaseResource resource, boolean contained )
    {
        DhisConflictException lastException = null;
        for ( int i = 0; i < MAX_CONFLICT_RETRIES + 1; i++ )
        {
            try
            {
                return saveInternally( fhirServerResource, resource, contained );
            }
            catch ( DhisConflictException e )
            {
                logger.info( "DHIS2 Conflict reported. Retrying action if possible: " + e.getMessage() );
                lastException = e;
            }
        }
        throw lastException;
    }

    protected boolean saveInternally( @Nonnull FhirServerResource fhirServerResource, @Nonnull IBaseResource resource, boolean contained )
    {
        final Collection<FhirServerSystem> systems = fhirServerSystemRepository.findByFhirServer( fhirServerResource.getFhirServer() );

        final WritableFhirRequest fhirRequest = new WritableFhirRequest();
        fhirRequest.setDhisUsername( fhirServerResource.getFhirServer().getDhisEndpoint().getUsername() );
        fhirRequest.setRequestMethod( FhirRequestMethod.PUT );
        fhirRequest.setResourceType( FhirResourceType.getByResource( resource ) );
        fhirRequest.setLastUpdated( getLastUpdated( resource ) );
        fhirRequest.setResourceVersionId( (resource.getMeta() == null) ? null : resource.getMeta().getVersionId() );
        fhirRequest.setFhirServerResourceId( fhirServerResource.getId() );
        fhirRequest.setVersion( fhirServerResource.getFhirServer().getFhirVersion() );
        fhirRequest.setParameters( ArrayListMultimap.create() );
        fhirRequest.setFhirServerCode( fhirServerResource.getFhirServer().getCode() );
        fhirRequest.setResourceSystemsByType( systems.stream()
            .map( s -> new ResourceSystem( s.getFhirResourceType(), s.getSystem().getSystemUri(), s.getCodePrefix(), s.getDefaultValue(), s.getSystem().getFhirDisplayName() ) )
            .collect( Collectors.toMap( ResourceSystem::getFhirResourceType, rs -> rs ) ) );

        final ProcessedItemInfo processedItemInfo = ProcessedFhirItemInfoUtils.create( resource );
        boolean saved = false;
        FhirToDhisTransformerRequest transformerRequest = fhirToDhisTransformerService.createTransformerRequest( fhirRequest, fhirServerResource, resource, contained );
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
                        outcome.getRule(), fhirServerResource.getFhirServer(),
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
