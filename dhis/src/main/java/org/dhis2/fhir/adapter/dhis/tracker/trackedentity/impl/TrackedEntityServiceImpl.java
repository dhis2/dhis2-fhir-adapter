package org.dhis2.fhir.adapter.dhis.tracker.trackedentity.impl;

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
import org.dhis2.fhir.adapter.auth.UnauthorizedException;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.dhis.DhisConflictException;
import org.dhis2.fhir.adapter.dhis.DhisFindException;
import org.dhis2.fhir.adapter.dhis.DhisImportUnsuccessfulException;
import org.dhis2.fhir.adapter.dhis.DhisResourceException;
import org.dhis2.fhir.adapter.dhis.metadata.model.DhisSyncGroup;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceResult;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.ImportSummaryWebMessage;
import org.dhis2.fhir.adapter.dhis.model.Status;
import org.dhis2.fhir.adapter.dhis.model.UriFilterApplier;
import org.dhis2.fhir.adapter.dhis.sync.DhisLastUpdated;
import org.dhis2.fhir.adapter.dhis.sync.StoredDhisResourceService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.RequiredValueType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.dhis.util.DhisPagingQuery;
import org.dhis2.fhir.adapter.dhis.util.DhisPagingUtils;
import org.dhis2.fhir.adapter.rest.RestTemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Implementation of {@link TrackedEntityService}.
 *
 * @author volsch
 */
@Service
public class TrackedEntityServiceImpl implements TrackedEntityService
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    protected static final String TEI_FIELDS =
        "deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated," +
            "attributes[attribute,value,lastUpdated,storedBy]";

    protected static final String GENERATE_URI = "/trackedEntityAttributes/{attributeId}/generate.json";

    protected static final String CREATE_URI = "/trackedEntityInstances.json?strategy=CREATE";

    protected static final String ID_URI = "/trackedEntityInstances/{id}.json?fields=" + TEI_FIELDS;

    protected static final String LAST_UPDATED_URI = "/trackedEntityInstances/{id}.json?fields=lastUpdated";

    protected static final String UPDATE_URI = "/trackedEntityInstances/{id}.json?mergeMode=MERGE";

    protected static final String FIND_BY_ATTR_VALUE_URI = "/trackedEntityInstances.json?" +
        "trackedEntityType={typeId}&ouMode=ACCESSIBLE&filter={attrId}:EQ:{attrValue}&pageSize={maxResult}&" +
        "fields=" + TEI_FIELDS;

    protected static final int MAX_RESERVE_RETRIES = 10;

    private final RestTemplate restTemplate;

    private final TrackedEntityMetadataService metadataService;

    private final StoredDhisResourceService storedItemService;

    private final Instant epochStartInstant = Instant.ofEpochMilli( 0 );

    private final ZoneId zoneId = ZoneId.systemDefault();

    @Autowired
    public TrackedEntityServiceImpl( @Nonnull @Qualifier( "userDhis2RestTemplate" ) RestTemplate restTemplate, @Nonnull TrackedEntityMetadataService metadataService, @Nonnull StoredDhisResourceService storedItemService )
    {
        this.restTemplate = restTemplate;
        this.metadataService = metadataService;
        this.storedItemService = storedItemService;
    }

    @HystrixCommand( ignoreExceptions = { DhisConflictException.class, UnauthorizedException.class } )
    @Override
    public void updateGeneratedValues( @Nonnull TrackedEntityInstance trackedEntityInstance, @Nonnull TrackedEntityType type, @Nonnull Map<RequiredValueType, String> requiredValues )
    {
        type.getAttributes().stream().filter( a -> a.getAttribute().isGenerated() && (trackedEntityInstance.getAttribute( a.getAttributeId() ).getValue() == null) ).forEach( a -> {
            final RequiredValues requiredValue = metadataService.getRequiredValues( a.getAttributeId() );
            final MultiValueMap<String, String> resultingRequiredValues =
                CollectionUtils.toMultiValueMap( requiredValues.entrySet().stream().filter( rq -> requiredValue.containsRequired( rq.getKey() ) )
                    .collect( Collectors.toMap( rq -> rq.getKey().name(), rq -> Collections.singletonList( rq.getValue() ) ) ) );

            // numeric generated values that start with 0 are not supported by most patterns
            boolean retry = true;
            for ( int i = 0; (i < MAX_RESERVE_RETRIES) && retry; i++ )
            {
                final String reservedValue = getReservedValue( a.getAttributeId(), resultingRequiredValues );
                retry = false;
                switch ( a.getValueType() )
                {
                    case INTEGER:
                    case INTEGER_NEGATIVE:
                    case INTEGER_POSITIVE:
                    case INTEGER_ZERO_OR_POSITIVE:
                    case NUMBER:
                        retry = reservedValue.startsWith( "0" );
                        break;
                }
                trackedEntityInstance.getAttribute( a.getAttributeId() ).setValue( reservedValue );
            }
        } );
    }

    @HystrixCommand( ignoreExceptions = UnauthorizedException.class )
    @Nonnull
    @Override
    @CachePut( key = "{'findOneById', #a0}", cacheManager = "dhisCacheManager", cacheNames = "trackedEntityInstances" )
    public Optional<TrackedEntityInstance> findOneByIdRefreshed( @Nonnull String id )
    {
        TrackedEntityInstance instance;
        try
        {
            instance = Objects.requireNonNull( restTemplate.getForObject( ID_URI, TrackedEntityInstance.class, id ) );
        }
        catch ( HttpClientErrorException e )
        {
            if ( RestTemplateUtils.isNotFound( e ) )
            {
                return Optional.empty();
            }
            throw e;
        }
        return Optional.of( instance );
    }

    @HystrixCommand( ignoreExceptions = UnauthorizedException.class )
    @Nonnull
    @Override
    @Cacheable( key = "{'findOneById', #a0}", cacheManager = "dhisCacheManager", cacheNames = "trackedEntityInstances" )
    public Optional<TrackedEntityInstance> findOneById( @Nonnull String id )
    {
        return findOneByIdRefreshed( id );
    }

    @HystrixCommand( ignoreExceptions = UnauthorizedException.class )
    @Nonnull
    @Override
    @CachePut( key = "{'findByAttrValue', #a0, #a1, #a2, #a3}", cacheManager = "dhisCacheManager", cacheNames = "trackedEntityInstances", unless = "#result.size() == 0" )
    public Collection<TrackedEntityInstance> findByAttrValueRefreshed( @Nonnull String typeId, @Nonnull String attributeId, @Nonnull String value, int maxResult )
    {
        // filtering by values with a colon inside is not supported by DHIS2 Web API
        if ( value.contains( ":" ) )
        {
            return Collections.emptyList();
        }
        return Objects.requireNonNull( restTemplate.getForEntity( FIND_BY_ATTR_VALUE_URI, TrackedEntityInstances.class, typeId, attributeId, value, maxResult )
            .getBody() ).getTrackedEntityInstances();
    }

    @HystrixCommand( ignoreExceptions = UnauthorizedException.class )
    @Nonnull
    @Override
    @Cacheable( key = "{'findByAttrValue', #a0, #a1, #a2, #a3}", cacheManager = "dhisCacheManager", cacheNames = "trackedEntityInstances", unless = "#result.size() == 0" )
    public Collection<TrackedEntityInstance> findByAttrValue( @Nonnull String typeId, @Nonnull String attributeId, @Nonnull String value, int maxResult )
    {
        return findByAttrValueRefreshed( typeId, attributeId, value, maxResult );
    }

    @Nonnull
    @Override
    public Instant poll( @Nonnull DhisSyncGroup group, @Nonnull Instant lastUpdated, int toleranceMillis, int maxSearchCount, @Nonnull Set<String> excludedStoredBy, @Nonnull Consumer<Collection<ProcessedItemInfo>> consumer )
    {
        return new TrackedEntityPolledItemRetriever( restTemplate, toleranceMillis, maxSearchCount, zoneId ).poll( lastUpdated, excludedStoredBy, consumer, null );
    }

    @HystrixCommand( ignoreExceptions = { DhisConflictException.class, UnauthorizedException.class } )
    @Nonnull
    @Override
    public TrackedEntityInstance createOrUpdate( @Nonnull TrackedEntityInstance trackedEntityInstance )
    {
        return trackedEntityInstance.isNewResource() ? create( trackedEntityInstance ) : update( trackedEntityInstance );
    }

    @HystrixCommand( ignoreExceptions = UnauthorizedException.class )
    @Nonnull
    @Override
    public DhisResourceResult<TrackedEntityInstance> find( @Nonnull String trackedEntityTypeId, @Nonnull UriFilterApplier uriFilterApplier, int from, int max )
    {
        final DhisPagingQuery pagingQuery = DhisPagingUtils.createPagingQuery( from, max );
        final List<String> variables = new ArrayList<>();
        final String uri = uriFilterApplier.add( UriComponentsBuilder.newInstance(), variables ).path( "/trackedEntityInstances.json" )
            .queryParam( "skipPaging", "false" ).queryParam( "page", pagingQuery.getPage() ).queryParam( "pageSize", pagingQuery.getPageSize() )
            .queryParam( "trackedEntityType", trackedEntityTypeId ).queryParam( "ouMode", "ACCESSIBLE" ).queryParam( "fields", TEI_FIELDS ).build().toString();
        final ResponseEntity<TrackedEntityInstances> result;
        try
        {
            result = restTemplate.getForEntity( uri, TrackedEntityInstances.class, variables.toArray() );
        }
        catch ( HttpClientErrorException e )
        {
            if ( e.getStatusCode() == HttpStatus.BAD_REQUEST || e.getStatusCode() == HttpStatus.CONFLICT )
            {
                throw new DhisFindException( e.getMessage(), e );
            }
            throw e;
        }
        final TrackedEntityInstances instances = Objects.requireNonNull( result.getBody() );

        return new DhisResourceResult<>( (instances.getTrackedEntityInstances().size() > pagingQuery.getResultOffset()) ?
            instances.getTrackedEntityInstances().subList( pagingQuery.getResultOffset(), instances.getTrackedEntityInstances().size() ) : Collections.emptyList(),
            (instances.getTrackedEntityInstances().size() >= pagingQuery.getPageSize()) );
    }

    @HystrixCommand( ignoreExceptions = UnauthorizedException.class )
    @Nonnull
    protected TrackedEntityInstance create( @Nonnull TrackedEntityInstance trackedEntityInstance )
    {
        final DhisSyncGroup syncGroup = storedItemService.findSyncGroupById( DhisSyncGroup.DEFAULT_ID )
            .orElseThrow( () -> new DhisResourceException( "Could not load default DHIS2 sync group." ) );

        final ResponseEntity<ImportSummaryWebMessage> response;
        try
        {
            clear( trackedEntityInstance );
            response = restTemplate.postForEntity( (trackedEntityInstance.getId() == null) ? CREATE_URI : ID_URI,
                trackedEntityInstance, ImportSummaryWebMessage.class, trackedEntityInstance.getId() );
        }
        catch ( HttpClientErrorException e )
        {
            if ( HttpStatus.CONFLICT.equals( e.getStatusCode() ) )
            {
                throw new DhisConflictException( "Tracked tracked entity instance could not be created: " + e.getResponseBodyAsString(), e );
            }
            throw e;
        }
        final ImportSummaryWebMessage result = Objects.requireNonNull( response.getBody() );
        if ( result.isNotSuccessful() )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful import of tracked entity instance." );
        }
        trackedEntityInstance.setNewResource( false );
        trackedEntityInstance.setId( result.getResponse().getImportSummaries().get( 0 ).getReference() );

        storeItem( syncGroup, trackedEntityInstance.getId(), response );
        return trackedEntityInstance;
    }

    @Nonnull
    protected TrackedEntityInstance update( @Nonnull TrackedEntityInstance trackedEntityInstance )
    {
        final DhisSyncGroup syncGroup = storedItemService.findSyncGroupById( DhisSyncGroup.DEFAULT_ID )
            .orElseThrow( () -> new DhisResourceException( "Could not load default DHIS2 sync group." ) );

        final ResponseEntity<ImportSummaryWebMessage> response;
        try
        {
            clear( trackedEntityInstance );
            response = restTemplate.exchange( UPDATE_URI, HttpMethod.PUT, new HttpEntity<>( trackedEntityInstance ),
                ImportSummaryWebMessage.class, trackedEntityInstance.getId() );
        }
        catch ( HttpClientErrorException e )
        {
            if ( HttpStatus.CONFLICT.equals( e.getStatusCode() ) )
            {
                throw new DhisConflictException( "Tracked tracked entity instance could not be updated: " + e.getResponseBodyAsString(), e );
            }
            throw e;
        }
        final ImportSummaryWebMessage result = Objects.requireNonNull( response.getBody() );
        if ( result.getStatus() != Status.OK )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful import of tracked entity instance: " + result.getStatus() );
        }

        storeItem( syncGroup, trackedEntityInstance.getId(), response );
        return trackedEntityInstance;
    }

    protected void storeItem( @Nonnull DhisSyncGroup syncGroup, @Nonnull String id, @Nonnull ResponseEntity<?> responseEntity )
    {
        if ( storedItemService.isEnabled() )
        {
            getProcessedItemInfo( id, responseEntity )
                .ifPresent( pii -> storedItemService.stored( syncGroup, pii.toIdString( epochStartInstant ) ) );
        }
    }

    @Nonnull
    protected Optional<ProcessedItemInfo> getProcessedItemInfo( @Nonnull String id, @Nonnull ResponseEntity<?> responseEntity )
    {
        final DhisLastUpdated lastUpdated;
        try
        {
            lastUpdated = Objects.requireNonNull( restTemplate.getForObject( LAST_UPDATED_URI, DhisLastUpdated.class, id ) );
        }
        catch ( HttpClientErrorException e )
        {
            if ( RestTemplateUtils.isNotFound( e ) )
            {
                logger.warn( "DHIS2 Tracked entity instance {} does no longer exist!", id );
                return Optional.empty();
            }
            throw e;
        }
        return Optional.of( new ProcessedItemInfo( DhisResourceId.toString( DhisResourceType.TRACKED_ENTITY, id ), Objects.requireNonNull( lastUpdated.getLastUpdated() ).toInstant(), false ) );
    }

    @Nonnull
    protected String getReservedValue( @Nonnull String attributeId, @Nonnull MultiValueMap<String, String> requiredValues )
    {
        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString( GENERATE_URI ).queryParams( requiredValues );
        final HttpEntity<ReservedValue> response = restTemplate.exchange( builder.buildAndExpand( attributeId ).toUriString(),
            HttpMethod.GET, null, ReservedValue.class );
        return Objects.requireNonNull( response.getBody() ).getValue();
    }

    private void clear( @Nonnull TrackedEntityInstance trackedEntityInstance )
    {
        trackedEntityInstance.setLastUpdated( null );
        if ( trackedEntityInstance.getAttributes() != null )
        {
            trackedEntityInstance.getAttributes().forEach( tei -> tei.setLastUpdated( null ) );
        }
    }
}
