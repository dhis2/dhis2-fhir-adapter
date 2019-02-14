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
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.dhis2.fhir.adapter.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.dhis.DhisConflictException;
import org.dhis2.fhir.adapter.dhis.sync.DhisResourceRepository;
import org.dhis2.fhir.adapter.fhir.client.ProcessedFhirItemInfoUtils;
import org.dhis2.fhir.adapter.fhir.client.StoredFhirResourceService;
import org.dhis2.fhir.adapter.fhir.data.model.SubscriptionFhirResource;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.data.repository.SubscriptionFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResource;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.security.AdapterSystemAuthenticationToken;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerService;
import org.dhis2.fhir.adapter.fhir.util.FhirParserUtils;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.dhis2.fhir.adapter.queue.RetryQueueDeliveryException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Listener that listens for FHIR resources that are received from the
 * FHIR resource queue.
 *
 * @author volsch
 */
@Component
@ConditionalOnProperty( name = "dhis2.fhir-adapter.import-enabled" )
public class FhirResourceQueueListener
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final FhirClientResourceRepository fhirClientResourceRepository;

    private final SubscriptionFhirResourceRepository subscriptionFhirResourceRepository;

    private final StoredFhirResourceService storedItemService;

    private final FhirResourceRepository fhirResourceRepository;

    private final FhirRepository fhirRepository;

    private final ZoneId zoneId = ZoneId.systemDefault();

    private final AtomicLong processedCount = new AtomicLong();

    public FhirResourceQueueListener( @Nonnull AuthorizationContext authorizationContext,
        @Nonnull LockManager lockManager, @Nonnull RequestCacheService requestCacheService,
        @Nonnull FhirClientSystemRepository fhirClientSystemRepository,
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull SubscriptionFhirResourceRepository subscriptionFhirResourceRepository,
        @Nonnull StoredFhirResourceService storedItemService,
        @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirRepository fhirRepository,
        @Nonnull FhirToDhisTransformerService fhirToDhisTransformerService,
        @Nonnull DhisResourceRepository dhisResourceRepository,
        @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository )
    {
        this.fhirClientResourceRepository = fhirClientResourceRepository;
        this.subscriptionFhirResourceRepository = subscriptionFhirResourceRepository;
        this.storedItemService = storedItemService;
        this.fhirResourceRepository = fhirResourceRepository;
        this.fhirRepository = fhirRepository;
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
        final long currentProcessedCount = processedCount.incrementAndGet();

        final FhirClientResource fhirClientResource =
            fhirClientResourceRepository.findOneByIdCached( fhirResource.getFhirClientResourceId() ).orElse( null );
        if ( fhirClientResource == null )
        {
            logger.warn( "FHIR client resource {} is no longer available. Skipping processing of updated FHIR resource {}.",
                fhirResource.getFhirClientResourceId(), fhirResource.getId() );
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
                    logger.info( "Processing FHIR resource {} of FHIR client resource {} (persisted={}, processed={}).",
                        resource.get().getIdElement().toUnqualified(), fhirClientResource.getId(), fhirResource.isPersistedDataItem(), currentProcessedCount );
                    try
                    {
                        fhirRepository.save( fhirClientResource, resource.get(), null );
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
        else if ( fhirResource.isPersistedDataItem() )
        {
            logger.debug( "Persisted FHIR resource {}/{} for FHIR client resource {} is no longer available. Skipping processing of updated FHIR resource.",
                fhirClientResource.getFhirResourceType().getResourceTypeName(), fhirResource.getId(), fhirClientResource.getId() );
        }
        else
        {
            logger.info( "FHIR resource {}/{} for FHIR client resource {} is no longer available. Skipping processing of updated FHIR resource.",
                fhirClientResource.getFhirResourceType().getResourceTypeName(), fhirResource.getId(), fhirClientResource.getId() );
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
}
