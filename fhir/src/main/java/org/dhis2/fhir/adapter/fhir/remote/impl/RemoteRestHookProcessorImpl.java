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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.data.model.UuidDataGroupId;
import org.dhis2.fhir.adapter.data.processor.DataItemQueueItem;
import org.dhis2.fhir.adapter.data.processor.DataProcessorItemRetriever;
import org.dhis2.fhir.adapter.data.processor.QueuedDataProcessorException;
import org.dhis2.fhir.adapter.data.processor.impl.AbstractQueuedDataProcessorImpl;
import org.dhis2.fhir.adapter.data.processor.impl.DataGroupQueueItem;
import org.dhis2.fhir.adapter.fhir.data.model.ProcessedRemoteFhirResource;
import org.dhis2.fhir.adapter.fhir.data.model.ProcessedRemoteFhirResourceId;
import org.dhis2.fhir.adapter.fhir.data.model.QueuedRemoteFhirResourceId;
import org.dhis2.fhir.adapter.fhir.data.model.QueuedRemoteSubscriptionRequestId;
import org.dhis2.fhir.adapter.fhir.data.model.StoredRemoteFhirResource;
import org.dhis2.fhir.adapter.fhir.data.model.StoredRemoteFhirResourceId;
import org.dhis2.fhir.adapter.fhir.data.repository.ProcessedRemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.data.repository.QueuedRemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.data.repository.QueuedRemoteSubscriptionRequestRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionResourceUpdateRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.remote.RemoteRestHookProcessor;
import org.dhis2.fhir.adapter.fhir.remote.StoredRemoteFhirResourceService;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirResource;
import org.dhis2.fhir.adapter.security.SystemAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

/**
 * Implementation of {@link RemoteRestHookProcessor}.
 *
 * @author volsch
 */
@Service
public class RemoteRestHookProcessorImpl extends
    AbstractQueuedDataProcessorImpl<ProcessedRemoteFhirResource, ProcessedRemoteFhirResourceId, StoredRemoteFhirResource, StoredRemoteFhirResourceId, QueuedRemoteSubscriptionRequestId, QueuedRemoteFhirResourceId, RemoteSubscriptionResource, UuidDataGroupId>
    implements RemoteRestHookProcessor
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final RemoteProcessorConfig processorConfig;

    private final RemoteSubscriptionResourceRepository remoteSubscriptionResourceRepository;

    private final Map<FhirVersion, AbstractSubscriptionResourceItemRetriever> itemRetrievers = new HashMap<>();

    public RemoteRestHookProcessorImpl(
        @Nonnull QueuedRemoteSubscriptionRequestRepository queuedGroupRepository,
        @Nonnull @Qualifier( "fhirRestHookRequestQueueJmsTemplate" ) JmsTemplate groupQueueJmsTemplate,
        @Nonnull RemoteSubscriptionResourceUpdateRepository dataGroupUpdateRepository,
        @Nonnull StoredRemoteFhirResourceService storedItemService,
        @Nonnull ProcessedRemoteFhirResourceRepository processedItemRepository,
        @Nonnull QueuedRemoteFhirResourceRepository queuedItemRepository,
        @Nonnull @Qualifier( "fhirResourceQueueJmsTemplate" ) JmsTemplate itemQueueJmsTemplate,
        @Nonnull PlatformTransactionManager platformTransactionManager,
        @Nonnull RemoteProcessorConfig processorConfig,
        @Nonnull SystemAuthenticationToken systemAuthenticationToken,
        @Nonnull RemoteSubscriptionResourceRepository remoteSubscriptionResourceRepository,
        @Nonnull ObjectProvider<List<AbstractSubscriptionResourceItemRetriever>> itemRetrievers )
    {
        super( queuedGroupRepository, groupQueueJmsTemplate, dataGroupUpdateRepository, storedItemService, processedItemRepository, queuedItemRepository, itemQueueJmsTemplate,
            platformTransactionManager, systemAuthenticationToken, new ForkJoinPool( processorConfig.getParallelCount() ) );
        this.processorConfig = processorConfig;
        this.remoteSubscriptionResourceRepository = remoteSubscriptionResourceRepository;

        itemRetrievers.getIfAvailable( Collections::emptyList ).forEach( br -> {
            for ( final FhirVersion version : br.getFhirVersions() )
            {
                RemoteRestHookProcessorImpl.this.itemRetrievers.put( version, br );
            }
        } );
    }

    @HystrixCommand
    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    @JmsListener( destination = "#{@fhirRemoteConfig.webHookRequestQueue.queueName}",
        concurrency = "#{@fhirRemoteConfig.webHookRequestQueue.listener.concurrency}" )
    public void receive( @Nonnull RemoteRestHookRequest remoteRestHookRequest )
    {
        super.receive( remoteRestHookRequest );
    }

    @Override
    protected QueuedRemoteSubscriptionRequestId createQueuedGroupId( @Nonnull RemoteSubscriptionResource group )
    {
        return new QueuedRemoteSubscriptionRequestId( group );
    }

    @Nonnull
    @Override
    protected DataGroupQueueItem<UuidDataGroupId> createDataGroupQueueItem( @Nonnull RemoteSubscriptionResource group )
    {
        return new RemoteRestHookRequest( group.getGroupId(), ZonedDateTime.now() );
    }

    @Nullable
    @Override
    protected RemoteSubscriptionResource findGroupByGroupId( @Nonnull UuidDataGroupId groupId )
    {
        return remoteSubscriptionResourceRepository.findByIdCached( groupId.getId() ).orElse( null );
    }

    @Override
    protected int getMaxProcessedAgeMinutes()
    {
        return processorConfig.getMaxProcessedAgeMinutes();
    }

    @Override
    protected int getMaxSearchCount()
    {
        return processorConfig.getMaxSearchCount();
    }

    @Nonnull
    @Override
    protected DataProcessorItemRetriever<RemoteSubscriptionResource> getDataProcessorItemRetriever( @Nonnull RemoteSubscriptionResource group )
    {
        final FhirVersion fhirVersion = group.getRemoteSubscription().getFhirVersion();
        final AbstractSubscriptionResourceItemRetriever itemRetriever = itemRetrievers.get( fhirVersion );
        if ( itemRetriever == null )
        {
            throw new QueuedDataProcessorException( "Remote subscription resource requires FHIR version " + fhirVersion +
                ", but no item retriever is available for that version." );
        }
        return itemRetriever;
    }

    @Nonnull
    @Override
    protected ProcessedRemoteFhirResource createProcessedItem( @Nonnull RemoteSubscriptionResource group, @Nonnull String id, @Nonnull Instant processedAt )
    {
        return new ProcessedRemoteFhirResource( new ProcessedRemoteFhirResourceId( group, id ), processedAt );
    }

    @Nonnull
    @Override
    protected QueuedRemoteFhirResourceId createQueuedItemId( @Nonnull RemoteSubscriptionResource group, @Nonnull ProcessedItemInfo processedItemInfo )
    {
        return new QueuedRemoteFhirResourceId( group, processedItemInfo.getId() );
    }

    @Nonnull
    @Override
    protected DataItemQueueItem<UuidDataGroupId> createDataItemQueueItem( @Nonnull RemoteSubscriptionResource group, @Nonnull ProcessedItemInfo processedItemInfo )
    {
        return new RemoteFhirResource( group.getGroupId(), processedItemInfo );
    }
}
