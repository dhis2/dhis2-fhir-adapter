package org.dhis2.fhir.adapter.data.processor.impl;

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
import org.dhis2.fhir.adapter.data.model.DataGroup;
import org.dhis2.fhir.adapter.data.model.DataGroupId;
import org.dhis2.fhir.adapter.data.model.DataGroupUpdate;
import org.dhis2.fhir.adapter.data.model.ProcessedItem;
import org.dhis2.fhir.adapter.data.model.ProcessedItemId;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.data.model.QueuedItemId;
import org.dhis2.fhir.adapter.data.model.StoredItem;
import org.dhis2.fhir.adapter.data.model.StoredItemId;
import org.dhis2.fhir.adapter.data.processor.DataItemQueueItem;
import org.dhis2.fhir.adapter.data.processor.DataProcessorItemRetriever;
import org.dhis2.fhir.adapter.data.processor.QueuedDataProcessor;
import org.dhis2.fhir.adapter.data.processor.QueuedDataProcessorException;
import org.dhis2.fhir.adapter.data.processor.StoredItemService;
import org.dhis2.fhir.adapter.data.repository.DataGroupUpdateRepository;
import org.dhis2.fhir.adapter.data.repository.ProcessedItemRepository;
import org.dhis2.fhir.adapter.security.SystemAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Abstract implementation of {@link QueuedDataProcessor}.
 *
 * @param <P>  the concrete type of the processed item.
 * @param <PI> the concrete type of the ID of the processed item.
 * @param <S>  the concrete type of the stored item.
 * @param <SI> the concrete type of the ID of the stored item.
 * @param <SG> the concrete type of the group of the stored item <code>S</code>.
 * @param <QG> the concrete type of the queued item that is used for queuing the data group.
 * @param <QI> the concrete type of the queued item that is used for queuing the item.
 * @param <G>  the concrete type of the group of the ID that is constant for a specific use case.
 * @param <GI> the concrete type of the group ID of the group <code>G</code>.
 * @author volsch
 */
public abstract class AbstractQueuedDataProcessorImpl<P extends ProcessedItem<PI, G>, PI extends ProcessedItemId<G>, S extends StoredItem<SI, SG>, SI extends StoredItemId<SG>, SG extends DataGroup, QG extends QueuedItemId<G>, QI extends QueuedItemId<G>,
    G extends DataGroup, GI extends DataGroupId> implements QueuedDataProcessor<G>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final JmsTemplate groupQueueJmsTemplate;

    private final DataGroupUpdateRepository<DataGroupUpdate<G>, G> dataGroupUpdateRepository;

    private final StoredItemService<S, SI, SG> storedItemService;

    private final ProcessedItemRepository<P, PI, G> processedItemRepository;

    private final JmsTemplate itemQueueJmsTemplate;

    private final PlatformTransactionManager platformTransactionManager;

    private final SystemAuthenticationToken systemAuthenticationToken;

    private final ForkJoinPool itemProcessorForkJoinPool;

    private boolean periodicInfoLogging = true;

    public AbstractQueuedDataProcessorImpl(
        @Nonnull JmsTemplate groupQueueJmsTemplate,
        @Nonnull DataGroupUpdateRepository<DataGroupUpdate<G>, G> dataGroupUpdateRepository,
        @Nonnull StoredItemService<S, SI, SG> storedItemService,
        @Nonnull ProcessedItemRepository<P, PI, G> processedItemRepository,
        @Nonnull JmsTemplate itemQueueJmsTemplate,
        @Nonnull PlatformTransactionManager platformTransactionManager,
        @Nonnull SystemAuthenticationToken systemAuthenticationToken,
        @Nonnull ForkJoinPool itemProcessorForkJoinPool )
    {
        this.groupQueueJmsTemplate = groupQueueJmsTemplate;
        this.dataGroupUpdateRepository = dataGroupUpdateRepository;
        this.storedItemService = storedItemService;
        this.processedItemRepository = processedItemRepository;
        this.itemQueueJmsTemplate = itemQueueJmsTemplate;
        this.platformTransactionManager = platformTransactionManager;
        this.systemAuthenticationToken = systemAuthenticationToken;
        this.itemProcessorForkJoinPool = itemProcessorForkJoinPool;
    }

    protected boolean isPeriodicInfoLogging()
    {
        return periodicInfoLogging;
    }

    public void setPeriodicInfoLogging( boolean periodicInfoLogging )
    {
        this.periodicInfoLogging = periodicInfoLogging;
    }

    @HystrixCommand
    @Override
    public void process( @Nonnull G group )
    {
        process( group, null );
    }

    protected void process( @Nonnull G group, @Nullable Integer rateMillis )
    {
        if ( (rateMillis != null) && !dataGroupUpdateRepository.requested( group, rateMillis ) )
        {
            logger.debug( "Rate {} has not yet been reached.", rateMillis );
            return;
        }

        final QG queuedGroupId = createQueuedGroupId( group );
            logger.debug( "Enqueuing entry for group {}.", group.getGroupId() );
            groupQueueJmsTemplate.convertAndSend( createDataGroupQueueItem( group ), message -> {
                // only one message for a single group must be in the queue at a specific time (grouping)
                message.setStringProperty( "_AMQ_LVQ_NAME", queuedGroupId.toKey() );
                // only one message for a single group must be processed at a specific time (grouping)
                message.setStringProperty( "JMSXGroupID", queuedGroupId.toKey() );
                return message;
            } );
            if ( isPeriodicInfoLogging() )
            {
                logger.info( "Enqueued entry for group {}.", group.getGroupId() );
            }
            else
            {
                logger.debug( "Enqueued entry for group {}.", group.getGroupId() );
            }
    }

    protected void receive( @Nonnull DataGroupQueueItem<GI> dataGroupQueueItem )
    {
        SecurityContextHolder.getContext().setAuthentication( createAuthentication() );
        try
        {
            receiveAuthenticated( dataGroupQueueItem );
        }
        finally
        {
            SecurityContextHolder.clearContext();
        }
    }

    protected void receiveAuthenticated( @Nonnull DataGroupQueueItem<GI> dataGroupQueueItem )
    {
        if ( isPeriodicInfoLogging() )
        {
            logger.info( "Processing queued group {}.", dataGroupQueueItem.getDataGroupId() );
        }
        else
        {
            logger.debug( "Processing queued group {}.", dataGroupQueueItem.getDataGroupId() );
        }
        final G group = findGroupByGroupId( dataGroupQueueItem.getDataGroupId() );
        if ( group == null )
        {
            logger.warn( "Group {} is no longer available. Skipping processing of updated group.",
                dataGroupQueueItem.getDataGroupId() );
            return;
        }
        final SG storedItemGroup = getStoredItemGroup( group );

        final Instant begin = Instant.now();
        final DataProcessorItemRetriever<G> itemRetriever = getDataProcessorItemRetriever( group );
        final Instant origLastUpdated = dataGroupUpdateRepository.getLastUpdated( group );
        final AtomicLong count = new AtomicLong();
        final Instant lastUpdated = itemRetriever.poll( group, origLastUpdated, getMaxSearchCount(), items -> {
            final Instant processedAt = Instant.now();
            final List<String> processableIds = items.stream().map( sr -> sr.toIdString( processedAt ) ).collect( Collectors.toList() );
            final Set<String> processedIds = processedItemRepository.find( group, processableIds );
            final Set<String> storedIds = storedItemService.findProcessedIds( storedItemGroup, processableIds );

            final ForkJoinTask<?> task = itemProcessorForkJoinPool.submit( () -> items.parallelStream().forEach( item -> {
                final String processedId = item.toIdString( processedAt );
                if ( !processedIds.contains( processedId ) && !storedIds.contains( processedId ) )
                {
                    // persist processed item
                    processedItemRepository.process( createProcessedItem( group, processedId, processedAt ), p -> {
                        if ( enqueueDataItem( group, item, false ) )
                        {
                            count.incrementAndGet();
                        }
                    } );
                }
            } ) );
            awaitTaskTermination( task );
        } );
        dataGroupUpdateRepository.updateLastUpdated( group, lastUpdated );
        final Instant end = Instant.now();

        // Purging old data must not be done before and also must not be done asynchronously. The ast updated
        // timestamp may be older than the purged data. And before purging the old data, the last updated
        // timestamp of the group must be updated by processing the complete items that belong to the group.
        purgeOldestProcessed( group, storedItemGroup );
        if ( count.longValue() > 0 )
        {
            logger.info( "Processed queued group {} with {} enqueued items in {} ms.",
                dataGroupQueueItem.getDataGroupId(), count.longValue(), Duration.between( begin, end ).toMillis() );
        }
        else if ( isPeriodicInfoLogging() )
        {
            logger.info( "Processed queued group {} with no enqueued items.", dataGroupQueueItem.getDataGroupId() );
        }
        else
        {
            logger.debug( "Processed queued group {} with no enqueued items.", dataGroupQueueItem.getDataGroupId() );
        }
    }

    protected boolean enqueueDataItem( @Nonnull G group, @Nonnull ProcessedItemInfo item, boolean persistedDataItem )
    {
        final QI queuedItemId = createQueuedItemId( group, item );
        itemQueueJmsTemplate.convertAndSend( createDataItemQueueItem( group, item, persistedDataItem ), message -> {
            // only one message for a single group must be in the queue at a specific time (grouping)
            message.setStringProperty( "_AMQ_LVQ_NAME", queuedItemId.toKey() );
            return message;
        } );
        logger.debug( "Item {} of group {} has been enqueued.", item.getId(), group.getGroupId() );
        return true;
    }

    private void awaitTaskTermination( @Nonnull ForkJoinTask<?> task )
    {
        try
        {
            task.get();
        }
        catch ( InterruptedException e )
        {
            throw new QueuedDataProcessorException( "Parallel execution of item queueing has been interrupted.", e );
        }
        catch ( ExecutionException e )
        {
            final Throwable cause = e.getCause();
            if ( cause == null )
            {
                throw new QueuedDataProcessorException( "Unexpected error when enqueuing items.", e );
            }
            if ( cause instanceof Error )
            {
                throw (Error) cause;
            }
            if ( cause instanceof RuntimeException )
            {
                throw (RuntimeException) cause;
            }
            throw new QueuedDataProcessorException( "Unexpected error when enqueuing items.", cause );
        }
    }

    protected void purgeOldestProcessed( @Nonnull G group, @Nonnull SG storedItemGroup )
    {
        final Instant from = Instant.now().minus( getMaxProcessedAgeMinutes(), ChronoUnit.MINUTES );

        logger.debug( "Purging oldest processed items before {} for group {}.", from, group.getGroupId() );
        int count = processedItemRepository.deleteOldest( group, from );
        if ( count > 0 )
        {
            logger.info( "Purged {} oldest processed items before {} for group {}.", count, from, group.getGroupId() );
        }

        logger.debug( "Purging oldest stored items before {} for group {}.", from, group.getGroupId() );
        count = storedItemService.deleteOldest( storedItemGroup, from );
        if ( count > 0 )
        {
            logger.info( "Purged {} oldest stored items before {} for group {}.", count, from, group.getGroupId() );
        }
    }

    @Nonnull
    protected Authentication createAuthentication()
    {
        return systemAuthenticationToken;
    }

    protected abstract QG createQueuedGroupId( @Nonnull G group );

    @Nonnull
    protected abstract DataGroupQueueItem<GI> createDataGroupQueueItem( @Nonnull G group );

    @Nullable
    protected abstract G findGroupByGroupId( @Nonnull GI groupId );

    protected abstract int getMaxProcessedAgeMinutes();

    protected abstract int getMaxSearchCount();

    @Nonnull
    protected abstract DataProcessorItemRetriever<G> getDataProcessorItemRetriever( @Nonnull G group );

    @Nonnull
    protected abstract P createProcessedItem( @Nonnull G group, @Nonnull String id, @Nonnull Instant processedAt );

    @Nonnull
    protected abstract QI createQueuedItemId( @Nonnull G group, @Nonnull ProcessedItemInfo processedItemInfo );

    @Nonnull
    protected abstract DataItemQueueItem<GI> createDataItemQueueItem( @Nonnull G group, @Nonnull ProcessedItemInfo processedItemInfo, boolean persistedDataItem );

    @Nonnull
    protected abstract SG getStoredItemGroup( @Nonnull G group );
}
