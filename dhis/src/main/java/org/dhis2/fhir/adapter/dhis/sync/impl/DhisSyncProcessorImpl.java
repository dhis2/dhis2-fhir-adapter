package org.dhis2.fhir.adapter.dhis.sync.impl;

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
import org.dhis2.fhir.adapter.data.processor.DataProcessorItemRetriever;
import org.dhis2.fhir.adapter.data.processor.impl.AbstractQueuedDataProcessorImpl;
import org.dhis2.fhir.adapter.data.processor.impl.DataGroupQueueItem;
import org.dhis2.fhir.adapter.dhis.data.model.ProcessedDhisResource;
import org.dhis2.fhir.adapter.dhis.data.model.ProcessedDhisResourceId;
import org.dhis2.fhir.adapter.dhis.data.model.QueuedDhisResourceId;
import org.dhis2.fhir.adapter.dhis.data.model.QueuedDhisSyncRequestId;
import org.dhis2.fhir.adapter.dhis.data.repository.ProcessedDhisResourceRepository;
import org.dhis2.fhir.adapter.dhis.data.repository.QueuedDhisResourceRepository;
import org.dhis2.fhir.adapter.dhis.data.repository.QueuedDhisSyncRequestRepository;
import org.dhis2.fhir.adapter.dhis.metadata.model.DhisSyncGroup;
import org.dhis2.fhir.adapter.dhis.metadata.repository.DhisSyncGroupRepository;
import org.dhis2.fhir.adapter.dhis.metadata.repository.DhisSyncGroupUpdateRepository;
import org.dhis2.fhir.adapter.dhis.sync.DhisResourceQueueItem;
import org.dhis2.fhir.adapter.dhis.sync.DhisSyncProcessor;
import org.dhis2.fhir.adapter.dhis.sync.DhisSyncProcessorException;
import org.dhis2.fhir.adapter.dhis.sync.DhisSyncRequestQueueItem;
import org.dhis2.fhir.adapter.security.SystemAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZonedDateTime;

/**
 * Implementation of {@link DhisSyncProcessor}.
 *
 * @author volsch
 */
@Service
public class DhisSyncProcessorImpl extends
    AbstractQueuedDataProcessorImpl<ProcessedDhisResource, ProcessedDhisResourceId, QueuedDhisSyncRequestId, QueuedDhisResourceId, DhisSyncGroup, UuidDataGroupId>
    implements DhisSyncProcessor
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final DhisSyncProcessorConfig processorConfig;

    private final DhisSyncGroupRepository dhisSyncGroupRepository;

    private final DataProcessorItemRetriever<DhisSyncGroup> dataProcessorItemRetriever;

    public DhisSyncProcessorImpl( @Nonnull QueuedDhisSyncRequestRepository queuedGroupRepository, @Nonnull @Qualifier( "dhisSyncRequestQueueJmsTemplate" ) JmsTemplate groupQueueJmsTemplate,
        @Nonnull DhisSyncGroupUpdateRepository dataGroupUpdateRepository, @Nonnull ProcessedDhisResourceRepository processedItemRepository,
        @Nonnull QueuedDhisResourceRepository queuedItemRepository, @Nonnull @Qualifier( "dhisResourceQueueJmsTemplate" ) JmsTemplate itemQueueJmsTemplate,
        @Nonnull PlatformTransactionManager platformTransactionManager, @Nonnull SystemAuthenticationToken systemAuthenticationToken,
        @Nonnull DhisSyncProcessorConfig processorConfig, @Nonnull DhisSyncGroupRepository dhisSyncGroupRepository,
        @Nonnull DataProcessorItemRetriever<DhisSyncGroup> dataProcessorItemRetriever )
    {
        super( queuedGroupRepository, groupQueueJmsTemplate, dataGroupUpdateRepository, processedItemRepository, queuedItemRepository, itemQueueJmsTemplate, platformTransactionManager, systemAuthenticationToken );
        this.processorConfig = processorConfig;
        this.dhisSyncGroupRepository = dhisSyncGroupRepository;
        this.dataProcessorItemRetriever = dataProcessorItemRetriever;
    }

    @Scheduled( initialDelayString = "#{@dhisSyncProcessorConfig.resultingRequestRateMillis}",
        fixedDelayString = "#{@dhisSyncProcessorConfig.resultingRequestRateMillis}" )
    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    public void process()
    {
        final DhisSyncGroup group = dhisSyncGroupRepository.findByIdCached( DhisSyncGroup.DEFAULT_ID )
            .orElseThrow( () -> new DhisSyncProcessorException( "DHIS2 Sync Group with default ID could not be found." ) );
        process( group );
    }

    @HystrixCommand
    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    @JmsListener( destination = "#{@dhisSyncConfig.syncRequestQueue.queueName}",
        concurrency = "#{@dhisSyncConfig.syncRequestQueue.listener.concurrency}" )
    public void receive( @Nonnull DhisSyncRequestQueueItem dhisSyncRequestQueueItem )
    {
        super.receive( dhisSyncRequestQueueItem );
    }

    @Override
    protected QueuedDhisSyncRequestId createQueuedGroupId( @Nonnull DhisSyncGroup group )
    {
        return new QueuedDhisSyncRequestId( group );
    }

    @Nonnull
    @Override
    protected DataGroupQueueItem<UuidDataGroupId> createDataGroupQueueItem( @Nonnull DhisSyncGroup group )
    {
        return new DhisSyncRequestQueueItem( group.getGroupId(), ZonedDateTime.now() );
    }

    @Nullable
    @Override
    protected DhisSyncGroup findGroupByGroupId( @Nonnull UuidDataGroupId groupId )
    {
        return dhisSyncGroupRepository.findByIdCached( groupId.getId() ).orElse( null );
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
    protected DataProcessorItemRetriever<DhisSyncGroup> getDataProcessorItemRetriever( @Nonnull DhisSyncGroup group )
    {
        return dataProcessorItemRetriever;
    }

    @Nonnull
    @Override
    protected ProcessedDhisResource createProcessedItem( @Nonnull DhisSyncGroup group, @Nonnull String id, @Nonnull Instant processedAt )
    {
        return new ProcessedDhisResource( new ProcessedDhisResourceId( group, id ), processedAt );
    }

    @Nonnull
    @Override
    protected QueuedDhisResourceId createQueuedItemId( @Nonnull DhisSyncGroup group, @Nonnull ProcessedItemInfo processedItemInfo )
    {
        return new QueuedDhisResourceId( group, processedItemInfo.getId() );
    }

    @Nonnull
    @Override
    protected DhisResourceQueueItem createDataItemQueueItem( @Nonnull DhisSyncGroup group, @Nonnull ProcessedItemInfo processedItemInfo )
    {
        return new DhisResourceQueueItem( group.getGroupId(), processedItemInfo );
    }
}
