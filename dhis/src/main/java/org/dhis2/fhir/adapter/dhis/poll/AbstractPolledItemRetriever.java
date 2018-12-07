package org.dhis2.fhir.adapter.dhis.poll;

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

import com.google.common.collect.Lists;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Polled item retriever that polls created or updated data from DHIS2.
 *
 * @param <P> the concrete type of the polled items.
 * @param <I> the concrete type of the polled item.
 * @author volsch
 */
public abstract class AbstractPolledItemRetriever<P extends PolledItems<I>, I extends PolledItem>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final DhisResourceType resourceType;

    private final RestTemplate restTemplate;

    private final String queryUri;

    private final int toleranceMillis;

    private final int maxSearchCount;

    private final Class<P> polledItemsClass;

    private final ZoneId zoneId = ZoneId.systemDefault();

    private int maxConsumedSize = 1000;

    protected AbstractPolledItemRetriever( @Nonnull DhisResourceType resourceType, @Nonnull RestTemplate restTemplate, @Nonnull String queryUri, int toleranceMillis, int maxSearchCount, @Nonnull Class<P> polledItemsClass )
    {
        this.resourceType = resourceType;
        this.restTemplate = restTemplate;
        this.queryUri = queryUri;
        this.toleranceMillis = toleranceMillis;
        this.maxSearchCount = maxSearchCount;
        this.polledItemsClass = polledItemsClass;
    }

    public int getMaxConsumedSize()
    {
        return maxConsumedSize;
    }

    public void setMaxConsumedSize( int maxConsumedSize )
    {
        this.maxConsumedSize = maxConsumedSize;
    }

    @Nonnull
    public Instant poll( @Nonnull final Instant lastUpdated, @Nonnull final Set<String> excludedStoredBy, @Nonnull Consumer<Collection<ProcessedItemInfo>> consumer )
    {
        final Instant fromLastUpdated = lastUpdated.minus( toleranceMillis, ChronoUnit.MILLIS );
        final Set<ProcessedItemInfo> allResources = new HashSet<>();
        final List<ProcessedItemInfo> orderedAllResources = new ArrayList<>();
        Set<PolledItem> previousPolledItems = null;
        Instant processedLastUpdated = null;
        Instant currentToLastUpdated = null;
        boolean moreAvailable;
        do
        {
            logger.debug( "Loading next from {} to {} with maximum count {}.",
                fromLastUpdated, currentToLastUpdated, maxSearchCount );
            if ( processedLastUpdated == null )
            {
                // last updated must only bet set on the first search invocation
                processedLastUpdated = Instant.now();
            }

            final P polledItems = getPolledItems( fromLastUpdated, currentToLastUpdated );
            final List<ProcessedItemInfo> resources =
                polledItems.getItems().stream().filter( pi -> !excludedStoredBy.contains( pi.getStoredBy() ) )
                    .map( pi -> new ProcessedItemInfo( DhisResourceId.toString( resourceType, pi.getId() ), pi.getLastUpdated().atZone( zoneId ).toInstant() ) )
                    .collect( Collectors.toList() );
            resources.forEach( r -> {
                if ( allResources.add( r ) )
                {
                    // list must contain only unique items
                    orderedAllResources.add( r );
                }
            } );

            moreAvailable = false;
            if ( !polledItems.getItems().isEmpty() )
            {
                final Instant nextToLastUpdated = Objects.requireNonNull( polledItems.getFromLastUpdated() ).atZone( zoneId ).toInstant();
                if ( (currentToLastUpdated != null) && !nextToLastUpdated.isBefore( currentToLastUpdated ) && hasMorePolledItems( fromLastUpdated, currentToLastUpdated ) )
                {
                    throw new PolledItemRetrieverException( "DHIS2 resource " + resourceType + " returned minimum last updated timestamp that is not before current last updated timestamp (" +
                        currentToLastUpdated + "). Result window of " + maxSearchCount + " seems not to be big enough." );
                }
                if ( (previousPolledItems == null) || !previousPolledItems.containsAll( polledItems.getItems() ) || (previousPolledItems.size() < polledItems.getItems().size()) )
                {
                    currentToLastUpdated = nextToLastUpdated;
                    previousPolledItems = new HashSet<>( polledItems.getItems() );
                    moreAvailable = true;
                }
            }
        }
        while ( moreAvailable );

        if ( !orderedAllResources.isEmpty() )
        {
            Collections.reverse( orderedAllResources );
            Lists.partition( orderedAllResources, maxConsumedSize ).forEach( consumer );
        }
        return processedLastUpdated;
    }

    private boolean hasMorePolledItems( @Nonnull Instant fromLastUpdated, @Nullable Instant currentToLastUpdated )
    {
        return !getPolledItems( fromLastUpdated, currentToLastUpdated, 2 ).getItems().isEmpty();
    }

    @Nonnull
    private P getPolledItems( @Nonnull Instant fromLastUpdated, @Nullable Instant currentToLastUpdated )
    {
        return getPolledItems( fromLastUpdated, currentToLastUpdated, 1 );
    }

    @Nonnull
    private P getPolledItems( @Nonnull Instant fromLastUpdated, @Nullable Instant currentToLastUpdated, int page )
    {
        final StringBuilder queryParams = new StringBuilder();
        if ( queryUri.indexOf( '?' ) < 0 )
        {
            queryParams.append( '?' );
        }
        else
        {
            queryParams.append( '&' );
        }
        queryParams.append( "lastUpdatedStartDate=" ).append( formatLastUpdated( fromLastUpdated ) );
        if ( currentToLastUpdated != null )
        {
            queryParams.append( "&lastUpdatedEndDate=" )
                .append( formatLastUpdated( currentToLastUpdated.plus( 1, ChronoUnit.MILLIS ) ) );
        }
        queryParams.append( "&pageSize=" ).append( maxSearchCount );
        queryParams.append( "&page=" ).append( page );

        final ResponseEntity<P> entity = restTemplate.getForEntity( queryUri + queryParams, polledItemsClass );
        return Objects.requireNonNull( entity.getBody() );
    }

    @Nonnull
    private String formatLastUpdated( @Nonnull Instant lastUpdated )
    {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( LocalDateTime.ofInstant( lastUpdated, zoneId ) );
    }
}
