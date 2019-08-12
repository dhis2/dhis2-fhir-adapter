package org.dhis2.fhir.adapter.dhis.service.impl;

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
import org.dhis2.fhir.adapter.dhis.DhisFindException;
import org.dhis2.fhir.adapter.dhis.metadata.model.DhisSyncGroup;
import org.dhis2.fhir.adapter.dhis.model.DhisMetadata;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceResult;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.UriFilterApplier;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.service.DhisMetadataService;
import org.dhis2.fhir.adapter.dhis.util.DhisPagingQuery;
import org.dhis2.fhir.adapter.dhis.util.DhisPagingUtils;
import org.dhis2.fhir.adapter.rest.RestTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Implementation of {@link OrganizationUnitService}.
 *
 * @param <T> the concrete type of the metadata.
 * @author volsch
 */
@Service
public abstract class AbstractDhisMetadataServiceImpl<T extends DhisResource & DhisMetadata> implements DhisMetadataService<T>
{
    private final RestTemplate systemRestTemplate;

    private final RestTemplate userRestTemplate;

    private final ZoneId zoneId = ZoneId.systemDefault();

    @Autowired
    protected AbstractDhisMetadataServiceImpl( @Nonnull @Qualifier( "systemDhis2RestTemplate" ) RestTemplate systemRestTemplate, @Nonnull @Qualifier( "userDhis2RestTemplate" ) RestTemplate userRestTemplate )
    {
        this.systemRestTemplate = systemRestTemplate;
        this.userRestTemplate = userRestTemplate;
    }

    @Nonnull
    public abstract DhisResourceType getDhisResourceType();

    @Nonnull
    protected abstract Class<? extends T> getItemClass();

    @Nonnull
    protected abstract Class<? extends DhisMetadataItems<? extends T>> getItemsClass();

    @Nonnull
    protected abstract String getFieldNames();

    @Cacheable( cacheResolver = "dhisMetadataCacheResolver" )
    @HystrixCommand
    @Override
    @Nonnull
    public Optional<T> findMetadataByReference( @Nonnull Reference reference )
    {
        return findMetadataRefreshedByReference( reference );
    }

    @HystrixCommand
    @Override
    @Nonnull
    @CachePut( cacheResolver = "dhisMetadataCacheResolver", unless = "#result==null" )
    public Optional<T> findMetadataRefreshedByReference( @Nonnull Reference reference )
    {
        return findOneByReference( systemRestTemplate, reference );
    }

    @HystrixCommand( ignoreExceptions = UnauthorizedException.class )
    @Nonnull
    @Override
    public Optional<T> findOneByReference( @Nonnull Reference reference )
    {
        return findOneByReference( userRestTemplate, reference );
    }

    @HystrixCommand( ignoreExceptions = { UnauthorizedException.class, DhisFindException.class } )
    @Nonnull
    @Override
    public DhisResourceResult<T> find( @Nonnull UriFilterApplier uriFilterApplier, int from, int max )
    {
        final DhisPagingQuery pagingQuery = DhisPagingUtils.createPagingQuery( from, max );
        final List<String> variables = new ArrayList<>();
        final String uri = uriFilterApplier.add( UriComponentsBuilder.newInstance(), variables ).path( "/" + getDhisResourceType().getTypeName() + ".json" )
            .queryParam( "paging", "true" ).queryParam( "page", pagingQuery.getPage() ).queryParam( "pageSize", pagingQuery.getPageSize() )
            .queryParam( "order", "id" ).queryParam( "fields", getFieldNames() ).build( false ).toString();

        final ResponseEntity<? extends DhisMetadataItems<? extends T>> result;
        try
        {
            result = userRestTemplate.getForEntity( uri, getItemsClass(), variables.toArray() );
        }
        catch ( HttpClientErrorException e )
        {
            if ( e.getStatusCode() == HttpStatus.BAD_REQUEST || e.getStatusCode() == HttpStatus.CONFLICT )
            {
                throw new DhisFindException( e.getMessage(), e );
            }

            throw e;
        }

        final DhisMetadataItems<? extends T> items = Objects.requireNonNull( result.getBody() );

        return new DhisResourceResult<>( ( items.getItems().size() > pagingQuery.getResultOffset() ) ?
            items.getItems().stream().skip( pagingQuery.getResultOffset() ).map( item -> (T) item ).collect( Collectors.toList() ) : Collections.emptyList(),
            ( items.getPager().getNextPage() != null ) );
    }

    @Nonnull
    protected Optional<T> findOneByReference( @Nonnull RestTemplate restTemplate, @Nonnull Reference reference )
    {
        final ResponseEntity<? extends DhisMetadataItems<? extends T>> result;

        switch ( reference.getType() )
        {
            case CODE:
                result = restTemplate.getForEntity( "/" + getDhisResourceType().getTypeName() + ".json?paging=false&fields=" + getFieldNames() + "&filter=code:eq:{code}", getItemsClass(), reference.getValue() );
                break;
            case NAME:
                result = restTemplate.getForEntity( "/" + getDhisResourceType().getTypeName() + ".json?paging=false&fields=" + getFieldNames() + "&filter=name:eq:{name}", getItemsClass(), reference.getValue() );
                break;
            case ID:
                try
                {
                    return Optional.of( Objects.requireNonNull( restTemplate.getForEntity( "/" + getDhisResourceType().getTypeName() + "/{id}.json?fields=" + getFieldNames(), getItemClass(), reference.getValue() ).getBody() ) );
                }
                catch ( HttpClientErrorException e )
                {
                    if ( RestTemplateUtils.isNotFound( e ) )
                    {
                        return Optional.empty();
                    }

                    throw e;
                }
            default:
                throw new AssertionError( "Unhandled reference type: " + reference.getType() );
        }

        return Objects.requireNonNull( result.getBody() ).getItems().stream().map( item -> (T) item ).findFirst();
    }

    @Nonnull
    @Override
    public Instant poll( @Nonnull DhisSyncGroup group, @Nonnull Instant lastUpdated, int toleranceMillis, int maxSearchCount, @Nonnull Set<String> excludedStoredBy, @Nonnull Consumer<Collection<ProcessedItemInfo>> consumer )
    {
        final DhisMetadataPolledItemRetriever eventPolledItemRetriever = new DhisMetadataPolledItemRetriever( getDhisResourceType(), systemRestTemplate,
            toleranceMillis, maxSearchCount, zoneId );

        return eventPolledItemRetriever.poll( lastUpdated, excludedStoredBy, consumer, null );
    }
}
