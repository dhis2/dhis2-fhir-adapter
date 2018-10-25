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
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.param.DateRangeParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.model.FhirVersionRestricted;
import org.dhis2.fhir.adapter.fhir.remote.RemoteWebHookProcessorException;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
public abstract class AbstractSubscriptionResourceBundleRetriever implements FhirVersionRestricted
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final FhirContext fhirContext;

    private final ZoneId zoneId = ZoneId.systemDefault();

    protected AbstractSubscriptionResourceBundleRetriever( @Nonnull FhirContext fhirContext )
    {
        this.fhirContext = fhirContext;
    }

    @Nonnull
    public ZonedDateTime poll( @Nonnull RemoteSubscriptionResource subscriptionResource, int maxSearchCount, @Nonnull Consumer<SubscriptionResourceInfoBundle> consumer )
    {
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, subscriptionResource.getRemoteSubscription() );
        ZonedDateTime lastUpdated;
        LocalDateTime fromLastUpdated = subscriptionResource.getRemoteLastUpdated()
            .minusMinutes( subscriptionResource.getRemoteSubscription().getToleranceMinutes() );

        Set<SubscriptionResourceInfo> previousResources = null;
        boolean moreAvailable;
        final String resourceName = subscriptionResource.getFhirResourceType().getResourceTypeName();
        do
        {
            logger.debug( "Loading next since {} for remote subscription resource with maximum count {}.", fromLastUpdated, subscriptionResource.getId(), maxSearchCount );
            lastUpdated = ZonedDateTime.now();
            IBaseBundle bundle = client.search().forResource( resourceName ).cacheControl( new CacheControlDirective().setNoCache( true ) )
                .whereMap( getQuery( subscriptionResource ) ).count( maxSearchCount ).lastUpdated( new DateRangeParam( Date.from( fromLastUpdated.atZone( zoneId ).toInstant() ), null ) )
                .elementsSubset( "meta", "id" ).returnBundle( getBundleClass() ).sort().ascending( "_lastUpdated" ).execute();
            do
            {
                final List<SubscriptionResourceInfo> resources = new ArrayList<>();
                for ( final IAnyResource resource : getResourceEntries( bundle ) )
                {
                    resources.add( new SubscriptionResourceInfo( resource.getIdElement().getIdPart(),
                        (resource.getMeta().getLastUpdated() == null) ? null : ZonedDateTime.ofInstant( resource.getMeta().getLastUpdated().toInstant(), zoneId ),
                        resource.getIdElement().getVersionIdPart() ) );
                }

                final Long totalCount = getBundleTotalCount( bundle );
                if ( (previousResources != null) && !resources.isEmpty() && previousResources.containsAll( resources ) && (previousResources.size() >= resources.size()) &&
                    ((totalCount == null) || (resources.size() < totalCount)) )
                {
                    throw new RemoteWebHookProcessorException( "Remote subscription resource " + subscriptionResource.getId() + " returned same result for last updated  " +
                        fromLastUpdated + " (count " + resources.size() + " of maximum " + maxSearchCount + ")." );
                }
                previousResources = new HashSet<>( resources );

                final SubscriptionResourceInfoBundle infoBundle = new SubscriptionResourceInfoBundle( resources );
                consumer.accept( infoBundle );

                moreAvailable = false;
                if ( !resources.isEmpty() )
                {
                    bundle = loadNextPage( client, bundle );
                    if ( bundle == null )
                    {
                        if ( totalCount == null )
                        {
                            logger.warn( "Remote subscription resource {} does not support total count in search result.", subscriptionResource.getId() );
                        }
                        else if ( resources.size() < totalCount )
                        {
                            logger.debug( "Returned {} of {} for remote subscription resource {} with maximum requested {}.",
                                resources.size(), totalCount, subscriptionResource.getId(), maxSearchCount );
                            if ( infoBundle.getMaxLastUpdated() == null )
                            {
                                logger.warn( "Remote subscription resource {} does not support last updated timestamps.", subscriptionResource.getId() );
                            }
                            else
                            {
                                final LocalDateTime newFromLastUpdated = infoBundle.getMaxLastUpdated().toLocalDateTime();
                                if ( newFromLastUpdated.equals( fromLastUpdated ) )
                                {
                                    throw new RemoteWebHookProcessorException( "Remote subscription resource " + subscriptionResource.getId() + " last updated timestamp " +
                                        fromLastUpdated + " has not been changed after processing " + resources.size() + " resources (total " + totalCount + ")." );
                                }
                                else
                                {
                                    fromLastUpdated = newFromLastUpdated;
                                    moreAvailable = true;
                                }
                            }
                        }
                    }
                }
            }
            while ( bundle != null );
        }
        while ( moreAvailable );

        return lastUpdated;
    }

    @Nonnull
    protected abstract Class<? extends IBaseBundle> getBundleClass();

    @Nonnull
    protected abstract List<? extends IAnyResource> getResourceEntries( @Nonnull IBaseBundle bundle );

    @Nullable
    protected abstract Long getBundleTotalCount( @Nonnull IBaseBundle bundle );

    @Nullable
    protected abstract IBaseBundle loadNextPage( @Nonnull IGenericClient client, @Nonnull IBaseBundle bundle );

    @Nonnull
    protected Map<String, List<String>> getQuery( @Nonnull RemoteSubscriptionResource subscriptionResource )
    {
        if ( StringUtils.isBlank( subscriptionResource.getFhirCriteriaParameters() ) || subscriptionResource.getFhirCriteriaParameters().equals( "?" ) )
        {
            return Collections.emptyMap();
        }

        final String parameters = subscriptionResource.getFhirCriteriaParameters().trim();
        final StringBuilder url = new StringBuilder( "test" );
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
            throw new RemoteWebHookProcessorException( "FHIR criteria parameters of remote subscription resource " + subscriptionResource.getId() + " are no valid query string.", e );
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
