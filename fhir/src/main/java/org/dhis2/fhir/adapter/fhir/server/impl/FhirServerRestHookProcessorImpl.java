package org.dhis2.fhir.adapter.fhir.server.impl;

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
import org.dhis2.fhir.adapter.fhir.data.model.ProcessedFhirResource;
import org.dhis2.fhir.adapter.fhir.data.model.ProcessedFhirResourceId;
import org.dhis2.fhir.adapter.fhir.data.model.QueuedFhirResourceId;
import org.dhis2.fhir.adapter.fhir.data.model.QueuedFhirServerRequestId;
import org.dhis2.fhir.adapter.fhir.data.model.StoredFhirResource;
import org.dhis2.fhir.adapter.fhir.data.model.StoredFhirResourceId;
import org.dhis2.fhir.adapter.fhir.data.repository.ProcessedFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.data.repository.QueuedFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.data.repository.QueuedFhirServerRequestRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServer;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerResource;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirServerResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirServerResourceUpdateRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResource;
import org.dhis2.fhir.adapter.fhir.server.FhirServerRestHookProcessor;
import org.dhis2.fhir.adapter.fhir.server.StoredFhirResourceService;
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
 * Implementation of {@link FhirServerRestHookProcessor}.
 *
 * @author volsch
 */
@Service
public class FhirServerRestHookProcessorImpl extends
    AbstractQueuedDataProcessorImpl<ProcessedFhirResource, ProcessedFhirResourceId, StoredFhirResource, StoredFhirResourceId, FhirServer, QueuedFhirServerRequestId, QueuedFhirResourceId,
        FhirServerResource, UuidDataGroupId>
    implements FhirServerRestHookProcessor
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final FhirServerProcessorConfig processorConfig;

    private final FhirServerResourceRepository fhirServerResourceRepository;

    private final Map<FhirVersion, AbstractSubscriptionResourceItemRetriever> itemRetrievers = new HashMap<>();

    public FhirServerRestHookProcessorImpl(
        @Nonnull QueuedFhirServerRequestRepository queuedGroupRepository,
        @Nonnull @Qualifier( "fhirRestHookRequestQueueJmsTemplate" ) JmsTemplate groupQueueJmsTemplate,
        @Nonnull FhirServerResourceUpdateRepository dataGroupUpdateRepository,
        @Nonnull StoredFhirResourceService storedItemService,
        @Nonnull ProcessedFhirResourceRepository processedItemRepository,
        @Nonnull QueuedFhirResourceRepository queuedItemRepository,
        @Nonnull @Qualifier( "fhirResourceQueueJmsTemplate" ) JmsTemplate itemQueueJmsTemplate,
        @Nonnull PlatformTransactionManager platformTransactionManager,
        @Nonnull FhirServerProcessorConfig processorConfig,
        @Nonnull SystemAuthenticationToken systemAuthenticationToken,
        @Nonnull FhirServerResourceRepository fhirServerResourceRepository,
        @Nonnull ObjectProvider<List<AbstractSubscriptionResourceItemRetriever>> itemRetrievers )
    {
        super( queuedGroupRepository, groupQueueJmsTemplate, dataGroupUpdateRepository, storedItemService, processedItemRepository, queuedItemRepository, itemQueueJmsTemplate,
            platformTransactionManager, systemAuthenticationToken, new ForkJoinPool( processorConfig.getParallelCount() ) );
        this.processorConfig = processorConfig;
        this.fhirServerResourceRepository = fhirServerResourceRepository;

        itemRetrievers.getIfAvailable( Collections::emptyList ).forEach( br -> {
            for ( final FhirVersion version : br.getFhirVersions() )
            {
                FhirServerRestHookProcessorImpl.this.itemRetrievers.put( version, br );
            }
        } );
    }

    @HystrixCommand
    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    @JmsListener( destination = "#{@fhirServerConfig.restHookRequestQueue.queueName}",
        concurrency = "#{@fhirServerConfig.restHookRequestQueue.listener.concurrency}" )
    public void receive( @Nonnull FhirServerRestHookRequest fhirServerRestHookRequest )
    {
        super.receive( fhirServerRestHookRequest );
    }

    @Override
    protected QueuedFhirServerRequestId createQueuedGroupId( @Nonnull FhirServerResource group )
    {
        return new QueuedFhirServerRequestId( group );
    }

    @Nonnull
    @Override
    protected DataGroupQueueItem<UuidDataGroupId> createDataGroupQueueItem( @Nonnull FhirServerResource group )
    {
        return new FhirServerRestHookRequest( group.getGroupId(), ZonedDateTime.now() );
    }

    @Nullable
    @Override
    protected FhirServerResource findGroupByGroupId( @Nonnull UuidDataGroupId groupId )
    {
        return fhirServerResourceRepository.findOneByIdCached( groupId.getId() ).orElse( null );
    }

    @Nonnull
    @Override
    protected FhirServer getStoredItemGroup( @Nonnull FhirServerResource group )
    {
        return group.getFhirServer();
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
    protected DataProcessorItemRetriever<FhirServerResource> getDataProcessorItemRetriever( @Nonnull FhirServerResource group )
    {
        final FhirVersion fhirVersion = group.getFhirServer().getFhirVersion();
        final AbstractSubscriptionResourceItemRetriever itemRetriever = itemRetrievers.get( fhirVersion );
        if ( itemRetriever == null )
        {
            throw new QueuedDataProcessorException( "FHIR server resource requires FHIR version " + fhirVersion +
                ", but no item retriever is available for that version." );
        }
        return itemRetriever;
    }

    @Nonnull
    @Override
    protected ProcessedFhirResource createProcessedItem( @Nonnull FhirServerResource group, @Nonnull String id, @Nonnull Instant processedAt )
    {
        return new ProcessedFhirResource( new ProcessedFhirResourceId( group, id ), processedAt );
    }

    @Nonnull
    @Override
    protected QueuedFhirResourceId createQueuedItemId( @Nonnull FhirServerResource group, @Nonnull ProcessedItemInfo processedItemInfo )
    {
        return new QueuedFhirResourceId( group, processedItemInfo.getId() );
    }

    @Nonnull
    @Override
    protected DataItemQueueItem<UuidDataGroupId> createDataItemQueueItem( @Nonnull FhirServerResource group, @Nonnull ProcessedItemInfo processedItemInfo )
    {
        return new FhirResource( group.getGroupId(), processedItemInfo );
    }
}
