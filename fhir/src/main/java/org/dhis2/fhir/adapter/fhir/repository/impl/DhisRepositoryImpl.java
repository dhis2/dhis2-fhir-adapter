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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.dhis2.fhir.adapter.auth.Authorization;
import org.dhis2.fhir.adapter.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.cache.RequestCacheContext;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.data.repository.IgnoredQueuedItemException;
import org.dhis2.fhir.adapter.dhis.data.model.QueuedDhisResourceId;
import org.dhis2.fhir.adapter.dhis.data.repository.QueuedDhisResourceRepository;
import org.dhis2.fhir.adapter.dhis.metadata.model.DhisSyncGroup;
import org.dhis2.fhir.adapter.dhis.metadata.repository.DhisSyncGroupRepository;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.sync.DhisResourceQueueItem;
import org.dhis2.fhir.adapter.dhis.sync.DhisResourceRepository;
import org.dhis2.fhir.adapter.dhis.sync.StoredDhisResourceService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.MissingDhisResourceException;
import org.dhis2.fhir.adapter.fhir.repository.OptimisticFhirResourceLockException;
import org.dhis2.fhir.adapter.fhir.security.AdapterSystemAuthenticationToken;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerRequest;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerService;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.WritableDhisRequest;
import org.dhis2.fhir.adapter.lock.LockContext;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.dhis2.fhir.adapter.queue.RetryQueueDeliveryException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of {@link DhisRepository}.
 *
 * @author volsch
 */
@Component
public class DhisRepositoryImpl implements DhisRepository
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final AuthorizationContext authorizationContext;

    private final Authorization systemDhis2Authorization;

    private final LockManager lockManager;

    private final RequestCacheService requestCacheService;

    private final FhirClientSystemRepository fhirClientSystemRepository;

    private final DhisSyncGroupRepository dhisSyncGroupRepository;

    private final QueuedDhisResourceRepository queuedDhisResourceRepository;

    private final StoredDhisResourceService storedItemService;

    private final DhisResourceRepository dhisResourceRepository;

    private final DhisToFhirTransformerService dhisToFhirTransformerService;

    private final FhirResourceRepository fhirResourceRepository;

    private final FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    public DhisRepositoryImpl(
        @Nonnull AuthorizationContext authorizationContext,
        @Nonnull Authorization systemDhis2Authorization,
        @Nonnull LockManager lockManager,
        @Nonnull RequestCacheService requestCacheService,
        @Nonnull FhirClientSystemRepository fhirClientSystemRepository,
        @Nonnull DhisSyncGroupRepository dhisSyncGroupRepository,
        @Nonnull QueuedDhisResourceRepository queuedDhisResourceRepository,
        @Nonnull StoredDhisResourceService storedItemService,
        @Nonnull DhisResourceRepository dhisResourceRepository,
        @Nonnull DhisToFhirTransformerService dhisToFhirTransformerService,
        @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository )
    {
        this.authorizationContext = authorizationContext;
        this.systemDhis2Authorization = systemDhis2Authorization;
        this.lockManager = lockManager;
        this.requestCacheService = requestCacheService;
        this.fhirClientSystemRepository = fhirClientSystemRepository;
        this.dhisSyncGroupRepository = dhisSyncGroupRepository;
        this.queuedDhisResourceRepository = queuedDhisResourceRepository;
        this.storedItemService = storedItemService;
        this.dhisResourceRepository = dhisResourceRepository;
        this.dhisToFhirTransformerService = dhisToFhirTransformerService;
        this.fhirResourceRepository = fhirResourceRepository;
        this.fhirDhisAssignmentRepository = fhirDhisAssignmentRepository;
    }

    @Override
    public void save( @Nonnull DhisSyncGroup syncGroup, @Nonnull DhisResource resource )
    {
        authorizationContext.setAuthorization( systemDhis2Authorization );
        try
        {
            saveInternallyWithMissingDhisResources( syncGroup, resource, new HashSet<>(), true );
        }
        finally
        {
            authorizationContext.resetAuthorization();
        }
    }

    @HystrixCommand( ignoreExceptions = RetryQueueDeliveryException.class )
    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    @JmsListener( destination = "#{@dhisSyncConfig.dhisResourceQueue.queueName}",
        concurrency = "#{@dhisSyncConfig.dhisResourceQueue.listener.concurrency}" )
    public void receive( @Nonnull DhisResourceQueueItem queueItem )
    {
        SecurityContextHolder.getContext().setAuthentication( new AdapterSystemAuthenticationToken() );
        try
        {
            receiveAuthenticated( queueItem );
        }
        finally
        {
            SecurityContextHolder.clearContext();
        }
    }

    protected void receiveAuthenticated( @Nonnull DhisResourceQueueItem queueItem )
    {
        logger.info( "Processing DHIS resource {} of sync group {}.", queueItem.getId(), queueItem.getDataGroupId() );
        final DhisSyncGroup syncGroup =
            dhisSyncGroupRepository.findByIdCached( queueItem.getDataGroupId().getId() ).orElse( null );
        if ( syncGroup == null )
        {
            logger.warn( "Sync group {} is no longer available. Skipping processing of updated DHIS resource {}.",
                queueItem.getDataGroupId(), queueItem.getId() );
            return;
        }

        try
        {
            queuedDhisResourceRepository.dequeued( new QueuedDhisResourceId( syncGroup, queueItem.getId() ) );
        }
        catch ( IgnoredQueuedItemException e )
        {
            // has already been logged with sufficient details
            return;
        }

        final Optional<? extends DhisResource> resource;
        authorizationContext.setAuthorization( systemDhis2Authorization );
        try
        {
            final DhisResourceId resourceId = Objects.requireNonNull( DhisResourceId.parse( queueItem.getId() ) );
            if ( queueItem.isDeleted() )
            {
                resource = dhisResourceRepository.findRefreshedDeleted( resourceId );
            }
            else
            {
                resource = dhisResourceRepository.findRefreshed( resourceId );
            }
        }
        finally
        {
            authorizationContext.resetAuthorization();
        }

        if ( resource.isPresent() )
        {
            final ProcessedItemInfo processedItemInfo = getProcessedItemInfo( resource.get() );
            if ( storedItemService.contains( syncGroup, processedItemInfo.toIdString( Instant.now() ) ) )
            {
                logger.info( "DHIS resource {} of sync group {} has already been stored.",
                    resource.get().getResourceId(), syncGroup.getId() );
            }
            else
            {
                try ( final MDC.MDCCloseable c = MDC.putCloseable( "dhisId", syncGroup.getId() + ":" + resource.get().getResourceId() ) )
                {
                    logger.info( "Processing DHIS resource {} of sync group {}.", resource.get().getResourceId(), syncGroup.getId() );
                    try
                    {
                        save( syncGroup, resource.get() );
                    }
                    catch ( MissingDhisResourceException e )
                    {
                        // retrying this issue will result in the same issue most likely
                        logger.warn( "Processing of data of DHIS resource caused a transformation error because of a missing DHIS resource {} that could not be created. Transformation will not be retried.", e.getDhisResourceId() );
                    }
                    catch ( TransformerDataException | TransformerMappingException e )
                    {
                        logger.warn( "Processing of data of DHIS resource caused a transformation error. Retrying processing later because of resolvable issue: {}", e.getMessage() );
                        throw new RetryQueueDeliveryException( e );
                    }
                    catch ( OptimisticFhirResourceLockException e )
                    {
                        logger.debug( e.getMessage(), e );
                        logger.info( "Processing of data of DHIS resource caused an optimistic locking error. Retrying processing later because of resolvable issue." );
                        throw new RetryQueueDeliveryException( e );
                    }
                    logger.info( "Processed DHIS resource {} for sync group {}.", resource.get().getResourceId(), syncGroup.getId() );
                }
                storedItemService.stored( syncGroup, processedItemInfo.toIdString( Instant.now() ) );
            }
        }
        else
        {
            logger.info( "DHIS resource {} for sync group {} is no longer available. Skipping processing of updated DHIS resource.",
                queueItem.getId(), syncGroup.getId() );
        }
    }

    @Nonnull
    private ProcessedItemInfo getProcessedItemInfo( @Nonnull DhisResource resource )
    {
        return new ProcessedItemInfo( resource.getResourceId().toString(),
            Objects.requireNonNull( resource.getLastUpdated() ).toInstant(), resource.isDeleted() );
    }

    protected boolean saveInternallyWithMissingDhisResources( @Nonnull DhisSyncGroup syncGroup, @Nonnull DhisResource resource, @Nonnull Set<DhisResourceId> missingDhisResourceIds, boolean initial )
    {
        boolean result = false;
        boolean saved = false;
        do
        {
            try
            {
                result = saveInternally( syncGroup, resource );
                saved = true;
            }
            catch ( MissingDhisResourceException e )
            {
                logger.info( "Saving DHIS resource {} failed because of missing DHIS resource {}.",
                    resource.getResourceId(), e.getDhisResourceId() );
                // endless loop must be avoided
                if ( !missingDhisResourceIds.add( e.getDhisResourceId() ) )
                {
                    throw e;
                }

                final DhisResource missingResource = dhisResourceRepository.findRefreshed( e.getDhisResourceId() )
                    .orElseThrow( () ->
                    {
                        logger.warn( "Missing DHIS resource {} could not be found.", e.getDhisResourceId() );
                        return new MissingDhisResourceException( e.getDhisResourceId() );
                    } );

                logger.info( "Saving missing DHIS resource {}.", e.getDhisResourceId() );
                if ( !saveInternallyWithMissingDhisResources( syncGroup, missingResource, missingDhisResourceIds, false ) )
                {
                    logger.info( "Missing DHIS resource {} could not be saved.", e.getDhisResourceId() );
                    throw e;
                }
                logger.info( "Saved missing DHIS resource {}.", e.getDhisResourceId() );
            }
        }
        while ( !saved );

        if ( !initial && storedItemService.isEnabled() )
        {
            final ProcessedItemInfo processedItemInfo = getProcessedItemInfo( resource );
            storedItemService.stored( syncGroup, processedItemInfo.toIdString( Instant.now() ) );
        }
        return result;
    }

    protected boolean saveInternally( @Nonnull DhisSyncGroup syncGroup, @Nonnull DhisResource resource )
    {
        final WritableDhisRequest dhisRequest = new WritableDhisRequest();
        dhisRequest.setResourceType( resource.getResourceType() );
        dhisRequest.setLastUpdated( resource.getLastUpdated() );

        DhisToFhirTransformerRequest transformerRequest = dhisToFhirTransformerService.createTransformerRequest( dhisRequest, resource );
        if ( transformerRequest == null )
        {
            return false;
        }

        boolean saved = false;
        do
        {
            DhisToFhirTransformOutcome<? extends IBaseResource> outcome;
            try ( final LockContext lockContext = lockManager.begin() )
            {
                try ( final RequestCacheContext requestCacheContext = requestCacheService.createRequestCacheContext() )
                {
                    outcome = dhisToFhirTransformerService.transform( transformerRequest );
                }
                if ( outcome == null )
                {
                    transformerRequest = null;
                }
                else
                {
                    if ( outcome.getResource() != null )
                    {
                        if ( outcome.isDelete() )
                        {
                            final boolean deleted = fhirResourceRepository.delete( transformerRequest.getFhirClient(), outcome.getResource() );
                            fhirDhisAssignmentRepository.deleteFhirResourceId( outcome.getRule(), transformerRequest.getFhirClient(),
                                outcome.getResource().getIdElement() );
                            logger.info( "Deleted (found={}) resource {} for FHIR client {}.", deleted,
                                outcome.getResource().getIdElement().toUnqualifiedVersionless(), transformerRequest.getFhirClient().getId() );
                        }
                        else
                        {
                            final IBaseResource resultingResource = fhirResourceRepository.save( transformerRequest.getFhirClient(), outcome.getResource() );
                            // resource may have been set as attribute in transformer context (e.g. shared encounter)
                            outcome.getResource().setId( resultingResource.getIdElement() );
                            fhirDhisAssignmentRepository.saveFhirResourceId( outcome.getRule(), transformerRequest.getFhirClient(),
                                resource.getResourceId(), resultingResource.getIdElement() );
                            logger.info( "Saved FHIR resource {} for FHIR client {}.",
                                resultingResource.getIdElement().toUnqualified(), transformerRequest.getFhirClient().getId() );
                        }
                    }
                    saved = true;
                    transformerRequest = outcome.getNextTransformerRequest();
                }
            }
        }
        while ( transformerRequest != null );
        return saved;
    }
}
