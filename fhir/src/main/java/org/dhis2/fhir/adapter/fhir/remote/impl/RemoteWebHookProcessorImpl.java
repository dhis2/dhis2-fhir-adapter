package org.dhis2.fhir.adapter.fhir.remote.impl;

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

import org.dhis2.fhir.adapter.fhir.data.model.ProcessedRemoteFhirResource;
import org.dhis2.fhir.adapter.fhir.data.repository.ProcessedRemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.data.repository.QueuedRemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.data.repository.QueuedRemoteSubscriptionRequestRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionResourceRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.remote.RemoteWebHookProcessor;
import org.dhis2.fhir.adapter.fhir.remote.RemoteWebHookProcessorException;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementation of {@link RemoteWebHookProcessor}.
 *
 * @author volsch
 */
@Service
public class RemoteWebHookProcessorImpl implements RemoteWebHookProcessor
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final RemoteProcessorConfig processorConfig;

    private final QueuedRemoteSubscriptionRequestRepository queuedRemoteSubscriptionRequestRepository;

    private final RemoteSubscriptionResourceRepository remoteSubscriptionResourceRepository;

    private final ProcessedRemoteFhirResourceRepository processedRemoteFhirResourceRepository;

    private final QueuedRemoteFhirResourceRepository queuedRemoteFhirResourceRepository;

    private final Map<FhirVersion, AbstractSubscriptionResourceBundleRetriever> bundleRetrievers = new HashMap<>();

    private final JmsTemplate webHookRequestQueueJmsTemplate;

    private final JmsTemplate fhirResourceQueueJmsTemplate;

    private final String requestIdBase = UUID.randomUUID().toString();

    private final AtomicLong requestId = new AtomicLong();

    public RemoteWebHookProcessorImpl( @Nonnull RemoteProcessorConfig processorConfig,
        @Nonnull QueuedRemoteSubscriptionRequestRepository queuedRemoteSubscriptionRequestRepository,
        @Nonnull RemoteSubscriptionResourceRepository remoteSubscriptionResourceRepository,
        @Nonnull ProcessedRemoteFhirResourceRepository processedRemoteFhirResourceRepository,
        @Nonnull QueuedRemoteFhirResourceRepository queuedRemoteFhirResourceRepository,
        @Nonnull ObjectProvider<List<AbstractSubscriptionResourceBundleRetriever>> bundleRetrievers,
        @Nonnull @Qualifier( "webHookRequestQueueJmsTemplate" ) JmsTemplate webHookRequestQueueJmsTemplate,
        @Nonnull @Qualifier( "fhirResourceQueueJmsTemplate" ) JmsTemplate fhirResourceQueueJmsTemplate )
    {
        this.processorConfig = processorConfig;
        this.queuedRemoteSubscriptionRequestRepository = queuedRemoteSubscriptionRequestRepository;
        this.remoteSubscriptionResourceRepository = remoteSubscriptionResourceRepository;
        this.processedRemoteFhirResourceRepository = processedRemoteFhirResourceRepository;
        this.queuedRemoteFhirResourceRepository = queuedRemoteFhirResourceRepository;
        this.webHookRequestQueueJmsTemplate = webHookRequestQueueJmsTemplate;
        this.fhirResourceQueueJmsTemplate = fhirResourceQueueJmsTemplate;

        bundleRetrievers.getIfAvailable( Collections::emptyList ).forEach( br -> {
            for ( final FhirVersion version : br.getFhirVersions() )
            {
                RemoteWebHookProcessorImpl.this.bundleRetrievers.put( version, br );
            }
        } );
    }

    @Transactional
    @Override
    public void received( @Nonnull UUID remoteSubscriptionResourceId, @Nonnull String requestId )
    {
        logger.debug( "Checking for a queued entry of remote subscription resource {}.", remoteSubscriptionResourceId );
        if ( !queuedRemoteSubscriptionRequestRepository.enqueue( remoteSubscriptionResourceId, requestId ) )
        {
            logger.debug( "There is already a queued entry for remote subscription resource {}.", remoteSubscriptionResourceId );
            return;
        }

        logger.debug( "Enqueuing entry for remote subscription resource {}.", remoteSubscriptionResourceId );
        webHookRequestQueueJmsTemplate.convertAndSend( new RemoteWebHookRequest( remoteSubscriptionResourceId, ZonedDateTime.now() ), message -> {
            // only one message for a remote subscription resource must be processed at a specific time (grouping)
            message.setStringProperty( "JMSXGroupID", remoteSubscriptionResourceId.toString() );
            return message;
        } );
        logger.info( "Enqueued entry for remote subscription resource {}.", remoteSubscriptionResourceId );
    }

    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    @JmsListener( destination = "#{@fhirRemoteConfig.webHookRequestQueue.queueName}",
        concurrency = "#{@fhirRemoteConfig.webHookRequestQueue.listener.concurrency}" )
    public void receive( @Nonnull RemoteWebHookRequest remoteWebHookRequest )
    {
        logger.debug( "Processing queued web hook request {}.", remoteWebHookRequest.getRemoteSubscriptionResourceId() );
        queuedRemoteSubscriptionRequestRepository.dequeued( remoteWebHookRequest.getRemoteSubscriptionResourceId() );

        final RemoteSubscriptionResource remoteSubscriptionResource =
            remoteSubscriptionResourceRepository.findById( remoteWebHookRequest.getRemoteSubscriptionResourceId() ).orElse( null );
        if ( remoteSubscriptionResource == null )
        {
            logger.warn( "Remote subscription resource {} is no longer available. Skipping processing of updated resources.",
                remoteWebHookRequest.getRemoteSubscriptionResourceId() );
            return;
        }

        final FhirVersion fhirVersion = remoteSubscriptionResource.getRemoteSubscription().getFhirVersion();
        final AbstractSubscriptionResourceBundleRetriever bundleRetriever = bundleRetrievers.get( fhirVersion );
        if ( bundleRetriever == null )
        {
            throw new RemoteWebHookProcessorException( "Remote subscription resource requires FHIR version " + fhirVersion +
                ", but no bundle retriever is available for that version." );
        }

        final ZonedDateTime lastUpdated = bundleRetriever.poll( remoteSubscriptionResource, processorConfig.getMaxSearchCount(), resources -> {
            final String requestId = getCurrentRequestId();
            final ZonedDateTime zonedProcessedAt = ZonedDateTime.now();
            final Set<String> processedVersionedIds = processedRemoteFhirResourceRepository.findByVersionedIds( remoteSubscriptionResource,
                resources.stream().map( sr -> sr.toVersionString( zonedProcessedAt ) ).collect( Collectors.toList() ) );

            final LocalDateTime processedAt = zonedProcessedAt.toLocalDateTime();
            resources.forEach( sr -> {
                final String versionedId = sr.toVersionString( zonedProcessedAt );
                if ( !processedVersionedIds.contains( versionedId ) )
                {
                    // persist processed remote FHIR resource and
                    processedRemoteFhirResourceRepository.process( new ProcessedRemoteFhirResource( remoteSubscriptionResource, versionedId, processedAt ), p -> {
                        if ( queuedRemoteFhirResourceRepository.enqueue( remoteSubscriptionResource.getId(), sr.getId(), requestId ) )
                        {
                            fhirResourceQueueJmsTemplate.convertAndSend(
                                new RemoteFhirResource( remoteSubscriptionResource.getId(), sr.getId(), sr.getVersion(), sr.getLastUpdated() ) );
                            logger.debug( "FHIR Resource {} of remote subscription resource {} has been enqueued.",
                                sr.getId(), remoteSubscriptionResource.getId() );
                        }
                        else
                        {
                            logger.debug( "FHIR Resource {} of remote subscription resource {} is still queued.",
                                sr.getId(), remoteSubscriptionResource.getId() );
                        }
                    } );
                }
            } );
        } );
        remoteSubscriptionResourceRepository.updateRemoteLastUpdated( remoteSubscriptionResource, lastUpdated.toLocalDateTime() );

        // Purging old data must not be done before and also must not be done asynchronously. The remote last updated
        // timestamp may be older than the purged data. And before purging the old data, the remote last updated
        // timestamp of the remote subscription resource must be updated by processing the complete FHIR resources that
        // belong to the remote subscription resource.
        purgeOldestProcessed( remoteSubscriptionResource );
        logger.debug( "Processed queued web hook request {}.", remoteWebHookRequest.getRemoteSubscriptionResourceId() );
    }

    @Nonnull
    protected String getCurrentRequestId()
    {
        return requestIdBase + "#" + Long.toString( requestId.getAndIncrement(), 36 );
    }

    protected void purgeOldestProcessed( @Nonnull RemoteSubscriptionResource remoteSubscriptionResource )
    {
        final LocalDateTime from = LocalDateTime.now().minusMinutes( processorConfig.getMaxProcessedAgeMinutes() );
        logger.debug( "Purging oldest processed remote subscription FHIR resources before {} for remote subscription resource {}.",
            from, remoteSubscriptionResource.getId() );
        final int count = processedRemoteFhirResourceRepository.deleteOldest( remoteSubscriptionResource, from );
        logger.debug( "Purged {} oldest processed remote subscription FHIR resources before {} for remote subscription resource {}.",
            count, from, remoteSubscriptionResource.getId() );
    }
}
