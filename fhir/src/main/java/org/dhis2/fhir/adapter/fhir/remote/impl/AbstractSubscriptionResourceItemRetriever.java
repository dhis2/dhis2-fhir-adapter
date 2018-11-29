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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.SummaryEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.param.DateRangeParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.data.processor.DataProcessorItemRetriever;
import org.dhis2.fhir.adapter.data.processor.QueuedDataProcessorException;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.model.FhirVersionRestricted;
import org.dhis2.fhir.adapter.fhir.repository.FhirClientUtils;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Retrieves the data for the specified subscription beginning from the last
 * updated timestamp that is associated with the remote subscription resource.
 * The retriever must be able to handle that the FHIR server does not support
 * paging.
 *
 * @author volsch
 */
public abstract class AbstractSubscriptionResourceItemRetriever implements DataProcessorItemRetriever<RemoteSubscriptionResource>, FhirVersionRestricted
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final FhirContext fhirContext;

    protected AbstractSubscriptionResourceItemRetriever( @Nonnull FhirContext fhirContext )
    {
        this.fhirContext = fhirContext;
    }

    @Override
    @Nonnull
    public Instant poll( @Nonnull RemoteSubscriptionResource group, @Nonnull Instant lastUpdated, int maxSearchCount, @Nonnull Consumer<Collection<ProcessedItemInfo>> consumer )
    {
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, group.getRemoteSubscription().getFhirEndpoint() );
        Instant processedLastUpdated = null;
        Instant fromLastUpdated = lastUpdated;

        final Set<ProcessedItemInfo> allResources = new HashSet<>();
        final List<ProcessedItemInfo> orderedAllResources = new ArrayList<>();
        Set<ProcessedItemInfo> previousResources = null;
        boolean paging = false;
        boolean backwardPaging = false;
        boolean moreAvailable;
        final String resourceName = group.getFhirResourceType().getResourceTypeName();
        do
        {
            logger.debug( "Loading next since {} for remote subscription resource with maximum count {}.", fromLastUpdated, group.getId(), maxSearchCount );
            if ( processedLastUpdated == null )
            {
                // last updated must only bet set on the first search invocation
                processedLastUpdated = Instant.now();
            }
            fromLastUpdated = fromLastUpdated
                .minus( group.getRemoteSubscription().getToleranceMillis(), ChronoUnit.MILLIS );
            IBaseBundle bundle = createBaseQuery( client, resourceName, group, fromLastUpdated ).count( maxSearchCount )
                .elementsSubset( "meta", "id" ).returnBundle( getBundleClass() ).sort().ascending( "_lastUpdated" ).execute();
            do
            {
                final List<ProcessedItemInfo> resources = new ArrayList<>();
                for ( final IAnyResource resource : getResourceEntries( bundle ) )
                {
                    resources.add( new ProcessedItemInfo( resource.getIdElement().getIdPart(),
                        (resource.getMeta().getLastUpdated() == null) ? null : resource.getMeta().getLastUpdated().toInstant(),
                        resource.getIdElement().getVersionIdPart() ) );
                }
                resources.forEach( r -> {
                    if ( allResources.add( r ) )
                    {
                        // list must contain only unique items
                        orderedAllResources.add( r );
                    }
                } );

                moreAvailable = false;
                if ( resources.isEmpty() )
                {
                    bundle = null;
                }
                else
                {
                    final IBaseBundle currentBundle = bundle;
                    bundle = backwardPaging ? loadPreviousPage( client, currentBundle ) : loadNextPage( client, currentBundle );
                    if ( isEmpty( bundle ) )
                    {
                        long totalCount;
                        bundle = null;
                        if ( paging )
                        {
                            if ( !backwardPaging )
                            {
                                // page backwards in order to prevent loss of data when paging is not stable
                                bundle = loadPreviousPage( client, currentBundle );
                                backwardPaging = true;
                                if ( isEmpty( bundle ) )
                                {
                                    bundle = null;
                                }
                            }
                        }
                        else if ( resources.size() < (totalCount = getTotalCount( client, resourceName, group, fromLastUpdated, currentBundle )) )
                        {
                            logger.debug( "Returned {} of {} for remote subscription resource {} with maximum requested {}.",
                                resources.size(), totalCount, group.getId(), maxSearchCount );
                            final Instant minLastUpdated = resources.stream().map( ProcessedItemInfo::getLastUpdated )
                                .filter( Objects::nonNull ).min( Comparator.naturalOrder() ).orElse( null );
                            if ( (minLastUpdated != null) && minLastUpdated.isBefore( fromLastUpdated ) )
                            {
                                logger.warn( "Remote subscription resource {} returned minimum last updated {} for lower bound {}.",
                                    group.getId(), minLastUpdated, fromLastUpdated );
                            }

                            if ( (previousResources != null) && previousResources.containsAll( resources ) && (previousResources.size() >= resources.size()) )
                            {
                                throw new QueuedDataProcessorException( "Remote subscription resource " + group.getId() + " returned same result for last updated  " +
                                    fromLastUpdated + " (count " + resources.size() + " of maximum " + maxSearchCount + ")." );
                            }
                            previousResources = new HashSet<>( resources );

                            final Instant maxLastUpdated = resources.stream().map( ProcessedItemInfo::getLastUpdated )
                                .filter( Objects::nonNull ).max( Comparator.naturalOrder() ).orElse( null );
                            if ( maxLastUpdated == null )
                            {
                                logger.warn( "Remote subscription resource {} does not support last updated timestamps.", group.getId() );
                            }
                            else
                            {
                                if ( maxLastUpdated.isAfter( fromLastUpdated ) )
                                {
                                    fromLastUpdated = maxLastUpdated;
                                    moreAvailable = true;
                                }
                                else
                                {
                                    throw new QueuedDataProcessorException( "Remote subscription resource " + group.getId() + " last updated timestamp " +
                                        fromLastUpdated + " has not been changed after processing " + resources.size() + " resources (total " + totalCount + ")." );
                                }
                            }
                        }
                    }
                    else
                    {
                        paging = true;
                    }
                }
            }
            while ( bundle != null );
        }
        while ( moreAvailable );

        // resources should not be consumed inside the loop above since paging may take longer
        if ( !orderedAllResources.isEmpty() )
        {
            consumer.accept( orderedAllResources );
        }
        return processedLastUpdated;
    }

    protected long getTotalCount( @Nonnull IGenericClient client, @Nonnull String resourceName, @Nonnull RemoteSubscriptionResource subscriptionResource, @Nonnull Instant fromLastUpdated, @Nonnull IBaseBundle bundle )
    {
        Long totalCount = getBundleTotalCount( bundle );
        if ( totalCount != null )
        {
            return totalCount;
        }

        final IBaseBundle newBundle = createBaseQuery( client, resourceName, subscriptionResource, fromLastUpdated )
            .summaryMode( SummaryEnum.COUNT ).returnBundle( getBundleClass() ).execute();
        totalCount = getBundleTotalCount( newBundle );
        if ( totalCount == null )
        {
            throw new QueuedDataProcessorException( "Remote subscription resource " + subscriptionResource.getId() + " did not return requested total count." );
        }
        return totalCount;
    }

    @Nonnull
    protected IQuery<? extends IBaseBundle> createBaseQuery( @Nonnull IGenericClient client, @Nonnull String resourceName, @Nonnull RemoteSubscriptionResource subscriptionResource, @Nonnull Instant fromLastUpdated )
    {
        return client.search().forResource( resourceName )
            .whereMap( getQuery( subscriptionResource ) ).lastUpdated( new DateRangeParam( Date.from( fromLastUpdated ), null ) )
            .cacheControl( new CacheControlDirective().setNoCache( true ) );
    }

    @Nonnull
    protected abstract Class<? extends IBaseBundle> getBundleClass();

    @Nonnull
    protected abstract List<? extends IAnyResource> getResourceEntries( @Nonnull IBaseBundle bundle );

    @Nullable
    protected abstract Long getBundleTotalCount( @Nonnull IBaseBundle bundle );

    @Nullable
    protected abstract IBaseBundle loadPreviousPage( @Nonnull IGenericClient client, @Nonnull IBaseBundle bundle );

    @Nullable
    protected abstract IBaseBundle loadNextPage( @Nonnull IGenericClient client, @Nonnull IBaseBundle bundle );

    protected abstract boolean isEmpty( @Nullable IBaseBundle bundle );

    @Nonnull
    protected Map<String, List<String>> getQuery( @Nonnull RemoteSubscriptionResource subscriptionResource )
    {
        if ( StringUtils.isBlank( subscriptionResource.getFhirCriteriaParameters() ) || subscriptionResource.getFhirCriteriaParameters().equals( "?" ) )
        {
            return Collections.emptyMap();
        }

        final String parameters = subscriptionResource.getFhirCriteriaParameters().trim();
        final StringBuilder url = new StringBuilder( "Resource" );
        if ( !parameters.startsWith( "?" ) )
        {
            url.append( "?" );
        }
        url.append( parameters );

        final List<NameValuePair> params;
        try
        {
            params = URLEncodedUtils.parse( new URI( url.toString() ), StandardCharsets.UTF_8 );
        }
        catch ( URISyntaxException e )
        {
            throw new QueuedDataProcessorException( "FHIR criteria parameters of remote subscription resource " + subscriptionResource.getId() + " are no valid query string.", e );
        }

        final Map<String, List<String>> result = new LinkedHashMap<>();
        for ( final NameValuePair param : params )
        {
            result.computeIfAbsent( param.getName(), key -> new ArrayList<>() ).add( param.getValue() );
        }

        // remove properties that must be set by polling only
        result.remove( "_count" );
        result.remove( "_elements" );
        result.remove( "_sort" );
        return result;
    }
}
