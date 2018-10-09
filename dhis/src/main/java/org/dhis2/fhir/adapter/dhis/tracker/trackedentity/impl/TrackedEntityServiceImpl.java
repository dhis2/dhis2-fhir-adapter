package org.dhis2.fhir.adapter.dhis.tracker.trackedentity.impl;

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

import org.dhis2.fhir.adapter.dhis.model.ImportStatus;
import org.dhis2.fhir.adapter.dhis.model.ImportSummaryWebMessage;
import org.dhis2.fhir.adapter.dhis.model.Status;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.RequiredValueType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrackedEntityServiceImpl implements TrackedEntityService
{
    protected static final String GENERATE_URI = "/trackedEntityAttributes/{attributeId}/generate.json";

    protected static final String CREATE_URI = "/trackedEntityInstances.json?strategy=CREATE";

    protected static final String ID_URI = "/trackedEntityInstances/{id}.json";

    protected static final String UPDATE_URI = ID_URI + "?mergeMode=MERGE";

    protected static final String FIND_BY_ATTR_VALUE_URI = "/trackedEntityInstances.json?" +
        "trackedEntityType={typeId}&ouMode=ACCESSIBLE&filter={attrId}:EQ:{attrValue}&pageSize={maxResult}";

    protected static final int MAX_RESERVE_RETRIES = 10;

    private final RestTemplate restTemplate;

    private final TrackedEntityMetadataService metadataService;

    @Autowired
    public TrackedEntityServiceImpl( @Nonnull @Qualifier( "userDhis2RestTemplate" ) RestTemplate restTemplate, @Nonnull TrackedEntityMetadataService metadataService )
    {
        this.restTemplate = restTemplate;
        this.metadataService = metadataService;
    }

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

    @Nonnull
    @Override
    public Optional<TrackedEntityInstance> getById( @Nonnull String id )
    {
        TrackedEntityInstance instance = null;
        try
        {
            instance = restTemplate.getForObject( ID_URI, TrackedEntityInstance.class, id );
        }
        catch ( HttpClientErrorException e )
        {
            if ( e.getStatusCode() != HttpStatus.NOT_FOUND )
            {
                throw e;
            }
        }
        return Optional.ofNullable( instance );
    }

    @Nonnull
    @Override
    public Collection<TrackedEntityInstance> findByAttrValue( @Nonnull String typeId, @Nonnull String attributeId, @Nonnull String value, int maxResult )
    {
        // filtering by values with a colon inside is not supported by DHIS2 Web API
        if ( value.contains( ":" ) )
        {
            return Collections.emptyList();
        }
        return restTemplate.getForEntity( FIND_BY_ATTR_VALUE_URI, TrackedEntityInstances.class, typeId, attributeId, value, maxResult )
            .getBody().getTrackedEntityInstances();
    }

    @Nonnull
    @Override
    public TrackedEntityInstance createOrUpdate( @Nonnull TrackedEntityInstance trackedEntityInstance )
    {
        return trackedEntityInstance.isNewResource() ? create( trackedEntityInstance ) : update( trackedEntityInstance );
    }

    @Nonnull
    protected TrackedEntityInstance create( @Nonnull TrackedEntityInstance trackedEntityInstance )
    {
        final ResponseEntity<ImportSummaryWebMessage> response;
        try
        {
            response = restTemplate.postForEntity( (trackedEntityInstance.getId() == null) ? CREATE_URI : ID_URI,
                trackedEntityInstance, ImportSummaryWebMessage.class, trackedEntityInstance.getId() );
        }
        catch ( HttpClientErrorException e )
        {
            throw new RuntimeException( "Tracked tracked entity instance could not be created: " + e.getResponseBodyAsString(), e );
        }
        final ImportSummaryWebMessage result = response.getBody();
        if ( (result.getStatus() != Status.OK) ||
            (result.getResponse().getImportSummaries().size() != 1) ||
            (result.getResponse().getImportSummaries().get( 0 ).getStatus() != ImportStatus.SUCCESS) ||
            (result.getResponse().getImportSummaries().get( 0 ).getReference() == null) )
        {
            throw new RuntimeException( "Tracked tracked entity instance could not be created." );
        }
        trackedEntityInstance.setNewResource( false );
        trackedEntityInstance.setId( result.getResponse().getImportSummaries().get( 0 ).getReference() );
        return trackedEntityInstance;
    }

    @Nonnull
    protected TrackedEntityInstance update( @Nonnull TrackedEntityInstance trackedEntityInstance )
    {
        final ResponseEntity<ImportSummaryWebMessage> response;
        try
        {
            response = restTemplate.exchange( UPDATE_URI, HttpMethod.PUT, new HttpEntity<>( trackedEntityInstance ),
                ImportSummaryWebMessage.class, trackedEntityInstance.getId() );
        }
        catch ( HttpClientErrorException e )
        {
            throw new RuntimeException( "Tracked tracked entity instance could not be updated: " + e.getResponseBodyAsString(), e );
        }
        final ImportSummaryWebMessage result = response.getBody();
        if ( result.getStatus() != Status.OK )
        {
            throw new RuntimeException( "Tracked tracked entity instance could not be created." );
        }
        return trackedEntityInstance;
    }

    @Nonnull
    protected String getReservedValue( @Nonnull String attributeId, @Nonnull MultiValueMap<String, String> requiredValues )
    {
        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString( GENERATE_URI ).queryParams( requiredValues );
        final HttpEntity<ReservedValue> response = restTemplate.exchange( builder.buildAndExpand( attributeId ).toUriString(),
            HttpMethod.GET, null, ReservedValue.class );
        return response.getBody().getValue();
    }
}
