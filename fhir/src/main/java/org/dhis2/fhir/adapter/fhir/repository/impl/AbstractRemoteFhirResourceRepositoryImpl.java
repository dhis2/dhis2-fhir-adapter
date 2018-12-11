package org.dhis2.fhir.adapter.fhir.repository.impl;

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
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionFhirEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.event.AutoCreatedRemoteSubscriptionResourceEvent;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.repository.FhirClientUtils;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirResourceRepository;
import org.dhis2.fhir.adapter.rest.RestBadRequestException;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link RemoteFhirResourceRepository}.
 *
 * @author volsch
 */
@CacheConfig( cacheNames = "fhirResources", cacheManager = "fhirCacheManager" )
public abstract class AbstractRemoteFhirResourceRepositoryImpl implements RemoteFhirResourceRepository
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final RemoteSubscriptionRepository repository;

    private final Map<FhirVersion, FhirContext> fhirContexts;

    public AbstractRemoteFhirResourceRepositoryImpl( @Nonnull RemoteSubscriptionRepository repository, @Nonnull ObjectProvider<List<FhirContext>> fhirContexts )
    {
        this.repository = repository;
        this.fhirContexts = fhirContexts.getIfAvailable( Collections::emptyList ).stream().filter( fc -> (FhirVersion.get( fc.getVersion().getVersion() ) != null) )
            .collect( Collectors.toMap( fc -> FhirVersion.get( fc.getVersion().getVersion() ), fc -> fc ) );
    }

    @Nonnull
    protected abstract IAnyResource createFhirSubscription( @Nonnull RemoteSubscriptionResource subscriptionResource );

    @Nonnull
    @Override
    public Optional<FhirContext> findFhirContext( @Nonnull FhirVersion fhirVersion )
    {
        return Optional.ofNullable( fhirContexts.get( fhirVersion ) );
    }

    @HystrixCommand
    @CachePut( key = "{#remoteSubscriptionId, #fhirVersion, #resourceType, #resourceId}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findRefreshed( @Nonnull UUID remoteSubscriptionId, @Nonnull FhirVersion fhirVersion, @Nonnull SubscriptionFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull String resourceId )
    {
        final FhirContext fhirContext = fhirContexts.get( fhirVersion );
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, fhirEndpoint );

        logger.debug( "Reading {}/{} from FHIR endpoints {}.", resourceType, resourceId, fhirEndpoint.getBaseUrl() );
        IBaseResource resource;
        try
        {
            resource = client.read().resource( resourceType ).withId( resourceId ).cacheControl( new CacheControlDirective().setNoCache( true ) ).execute();
        }
        catch ( ResourceNotFoundException e )
        {
            resource = null;
        }
        logger.debug( "Read {}/{} from FHIR endpoints {} (found={}).", resourceType, resourceId, fhirEndpoint.getBaseUrl(), (resource != null) );
        return Optional.ofNullable( resource );
    }

    @HystrixCommand
    @Cacheable( key = "{#remoteSubscriptionId, #fhirVersion, #resourceType, #resourceId}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> find( @Nonnull UUID remoteSubscriptionId, @Nonnull FhirVersion fhirVersion, @Nonnull SubscriptionFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull String resourceId )
    {
        return findRefreshed( remoteSubscriptionId, fhirVersion, fhirEndpoint, resourceType, resourceId );
    }

    @HystrixCommand
    @CachePut( key = "{#root.methodName, #remoteSubscriptionId, #fhirVersion, #resourceType, #identifier}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findRefreshedByIdentifier( @Nonnull UUID remoteSubscriptionId, @Nonnull FhirVersion fhirVersion, @Nonnull SubscriptionFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull SystemCodeValue identifier )
    {
        final FhirContext fhirContext = fhirContexts.get( fhirVersion );
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, fhirEndpoint );

        logger.debug( "Reading {}?identifier={} from FHIR endpoints {}.", resourceType, identifier, fhirEndpoint.getBaseUrl() );
        final IBaseBundle bundle = client.search().forResource( resourceType ).returnBundle( getBundleClass() )
            .where( new TokenClientParam( "identifier" ).exactly().systemAndIdentifier( identifier.getSystem(), identifier.getCode() ) )
            .cacheControl( new CacheControlDirective().setNoCache( true ) ).execute();
        final IBaseResource resource = getFirstResource( bundle );
        logger.debug( "Read {}?identifier={} from FHIR endpoints {} (found={}).", resourceType, identifier, fhirEndpoint.getBaseUrl(), (resource != null) );
        return Optional.ofNullable( resource );
    }

    @HystrixCommand
    @Cacheable( key = "{#root.methodName, #remoteSubscriptionId, #fhirVersion, #resourceType, #identifier}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findByIdentifier( @Nonnull UUID remoteSubscriptionId, @Nonnull FhirVersion fhirVersion, @Nonnull SubscriptionFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull SystemCodeValue identifier )
    {
        return findRefreshedByIdentifier( remoteSubscriptionId, fhirVersion, fhirEndpoint, resourceType, identifier );
    }

    @HystrixCommand
    @Cacheable( key = "{#subscriptionResource.remoteSubscription.id, #subscriptionResource.remoteSubscription.fhirVersion, " +
        "T(org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType).getByResource(#resource).getResourceTypeName(), #resource.getIdElement().getIdPart()}", unless = "#result==null" )
    @Nonnull
    @Override
    public IBaseResource save( @Nonnull RemoteSubscriptionResource subscriptionResource, @Nonnull IBaseResource resource )
    {
        throw new UnsupportedOperationException( "Must be implemented!" );
    }

    @TransactionalEventListener( phase = TransactionPhase.BEFORE_COMMIT, classes = AutoCreatedRemoteSubscriptionResourceEvent.class )
    public void autoCreatedSubscriptionResource( @Nonnull AutoCreatedRemoteSubscriptionResourceEvent event )
    {
        final FhirContext fhirContext = fhirContexts.get( event.getRemoteSubscriptionResource().getRemoteSubscription().getFhirVersion() );
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, event.getRemoteSubscriptionResource().getRemoteSubscription().getFhirEndpoint() );

        final MethodOutcome methodOutcome;
        try
        {
            methodOutcome = client.create().resource( createFhirSubscription( event.getRemoteSubscriptionResource() ) ).execute();
        }
        catch ( BaseServerResponseException e )
        {
            throw new RestBadRequestException( "The subscription could not be created on " +
                event.getRemoteSubscriptionResource().getRemoteSubscription().getFhirEndpoint().getBaseUrl() + ": " + e.getMessage(), e );
        }
        final String id = methodOutcome.getId().toUnqualifiedVersionless().getIdPart();
        logger.info( "Created FHIR subscription {} for remote subscription {}.", id, event.getRemoteSubscriptionResource().getRemoteSubscription().getId() );
        event.getRemoteSubscriptionResource().setFhirSubscriptionId( id );
    }

    @Nonnull
    protected String createWebHookUrl( @Nonnull RemoteSubscriptionResource subscriptionResource )
    {
        final StringBuilder url = new StringBuilder( subscriptionResource.getRemoteSubscription().getAdapterEndpoint().getBaseUrl() );
        if ( url.charAt( url.length() - 1 ) != '/' )
        {
            url.append( '/' );
        }
        url.append( "remote-fhir-rest-hook/" );
        url.append( subscriptionResource.getRemoteSubscription().getId() );
        url.append( '/' );
        url.append( subscriptionResource.getId() );
        return url.toString();
    }

    @Nullable
    protected abstract IBaseResource getFirstResource( @Nonnull IBaseBundle bundle );

    @Nonnull
    protected abstract Class<? extends IBaseBundle> getBundleClass();
}
