package org.dhis2.fhir.adapter.fhir.repository.impl;

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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.CacheControlDirective;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.PreferReturnEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.rest.server.exceptions.BaseServerResponseException;
import ca.uhn.fhir.rest.server.exceptions.PreconditionFailedException;
import ca.uhn.fhir.rest.server.exceptions.ResourceGoneException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServer;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.SubscriptionFhirEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirServerResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.event.AutoCreatedFhirServerResourceEvent;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.repository.FhirClientUtils;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceTransformationException;
import org.dhis2.fhir.adapter.fhir.repository.OptimisticFhirResourceLockException;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionException;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.server.ProcessedFhirItemInfoUtils;
import org.dhis2.fhir.adapter.fhir.server.StoredFhirResourceService;
import org.dhis2.fhir.adapter.rest.RestBadRequestException;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FhirResourceRepository}.
 *
 * @author volsch
 */
@CacheConfig( cacheNames = "fhirResources", cacheManager = "fhirCacheManager" )
public abstract class AbstractFhirResourceRepositoryImpl implements FhirResourceRepository
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final ScriptExecutor scriptExecutor;

    private final StoredFhirResourceService storedItemService;

    private final FhirServerResourceRepository fhirServerResourceRepository;

    private final Map<FhirVersion, FhirContext> fhirContexts;

    public AbstractFhirResourceRepositoryImpl( @Nonnull ScriptExecutor scriptExecutor, @Nonnull StoredFhirResourceService storedItemService, @Nonnull FhirServerResourceRepository fhirServerResourceRepository,
        @Nonnull ObjectProvider<List<FhirContext>> fhirContexts )
    {
        this.scriptExecutor = scriptExecutor;
        this.storedItemService = storedItemService;
        this.fhirServerResourceRepository = fhirServerResourceRepository;
        this.fhirContexts = fhirContexts.getIfAvailable( Collections::emptyList ).stream().filter( fc -> (FhirVersion.get( fc.getVersion().getVersion() ) != null) )
            .collect( Collectors.toMap( fc -> FhirVersion.get( fc.getVersion().getVersion() ), fc -> fc ) );
    }

    @Nonnull
    protected abstract AbstractFhirRepositoryResourceUtils createFhirRepositoryResourceUtils( @Nonnull UUID fhirServerId );

    @Nonnull
    protected abstract IAnyResource createFhirSubscription( @Nonnull FhirServerResource fhirServerResource );

    @Nonnull
    @Override
    public Optional<FhirContext> findFhirContext( @Nonnull FhirVersion fhirVersion )
    {
        return Optional.ofNullable( fhirContexts.get( fhirVersion ) );
    }

    @HystrixCommand( ignoreExceptions = FhirResourceTransformationException.class )
    @CachePut( key = "{#fhirServerId, #fhirVersion, #resourceType, #resourceId, #transform}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findRefreshed( @Nonnull UUID fhirServerId, @Nonnull FhirVersion fhirVersion, @Nonnull SubscriptionFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull String resourceId, boolean transform )
    {
        final FhirContext fhirContext = fhirContexts.get( fhirVersion );
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, fhirEndpoint );

        logger.debug( "Reading {}/{} from FHIR endpoints {}.", resourceType, resourceId, fhirEndpoint.getBaseUrl() );
        IBaseResource resource;
        try
        {
            resource = client.read().resource( NameUtils.toClassName( resourceType ) ).withId( resourceId ).cacheControl( new CacheControlDirective().setNoCache( true ) ).execute();
        }
        catch ( ResourceNotFoundException | ResourceGoneException e )
        {
            resource = null;
        }
        logger.debug( "Read {}/{} from FHIR endpoints {} (found={}).", resourceType, resourceId, fhirEndpoint.getBaseUrl(), (resource != null) );
        return Optional.ofNullable( transform ? transform( fhirServerId, fhirVersion, resource ) : resource );
    }

    @HystrixCommand( ignoreExceptions = FhirResourceTransformationException.class )
    @CachePut( key = "{#fhirServerId, #fhirVersion, #resourceType, #resourceId, true}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findRefreshed( @Nonnull UUID fhirServerId, @Nonnull FhirVersion fhirVersion, @Nonnull SubscriptionFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull String resourceId )
    {
        return findRefreshed( fhirServerId, fhirVersion, fhirEndpoint, resourceType, resourceId, true );
    }

    @HystrixCommand( ignoreExceptions = FhirResourceTransformationException.class )
    @Cacheable( key = "{#fhirServerId, #fhirVersion, #resourceType, #resourceId, true}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> find( @Nonnull UUID fhirServerId, @Nonnull FhirVersion fhirVersion, @Nonnull SubscriptionFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull String resourceId )
    {
        return findRefreshed( fhirServerId, fhirVersion, fhirEndpoint, resourceType, resourceId );
    }

    @HystrixCommand( ignoreExceptions = FhirResourceTransformationException.class )
    @CachePut( key = "{'findByIdentifier', #fhirServerId, #fhirVersion, #resourceType, #identifier.toString()}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findRefreshedByIdentifier( @Nonnull UUID fhirServerId, @Nonnull FhirVersion fhirVersion, @Nonnull SubscriptionFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull SystemCodeValue identifier )
    {
        return findByToken( fhirServerId, fhirVersion, fhirEndpoint, resourceType, "identifier", identifier );
    }

    @HystrixCommand( ignoreExceptions = FhirResourceTransformationException.class )
    @Cacheable( key = "{'findByIdentifier', #fhirServerId, #fhirVersion, #resourceType, #identifier.toString()}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findByIdentifier( @Nonnull UUID fhirServerId, @Nonnull FhirVersion fhirVersion, @Nonnull SubscriptionFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull SystemCodeValue identifier )
    {
        return findRefreshedByIdentifier( fhirServerId, fhirVersion, fhirEndpoint, resourceType, identifier );
    }

    @HystrixCommand( ignoreExceptions = FhirResourceTransformationException.class )
    @CachePut( key = "{'findByCode', #fhirServerId, #fhirVersion, #resourceType, #code.toString()}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findRefreshedByCode( @Nonnull UUID fhirServerId, @Nonnull FhirVersion fhirVersion, @Nonnull SubscriptionFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull SystemCodeValue code )
    {
        return findByToken( fhirServerId, fhirVersion, fhirEndpoint, resourceType, "code", code );
    }

    @HystrixCommand( ignoreExceptions = FhirResourceTransformationException.class )
    @Cacheable( key = "{'findByCode', #fhirServerId, #fhirVersion, #resourceType, #code.toString()}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findByCode( @Nonnull UUID fhirServerId, @Nonnull FhirVersion fhirVersion, @Nonnull SubscriptionFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull SystemCodeValue code )
    {
        return findRefreshedByCode( fhirServerId, fhirVersion, fhirEndpoint, resourceType, code );
    }

    @Nullable
    @Override
    public IBaseResource transform( @Nonnull UUID fhirServerId, @Nonnull FhirVersion fhirVersion, @Nullable IBaseResource resource )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResource( resource );
        if ( fhirResourceType == null )
        {
            return resource;
        }

        final Optional<FhirServerResource> fhirServerResource = fhirServerResourceRepository.findFirstCached( fhirServerId, fhirResourceType );
        if ( fhirServerResource.isPresent() && (fhirServerResource.get().getImpTransformScript() != null) )
        {
            final Map<String, Object> variables = new HashMap<>();
            variables.put( ScriptVariable.RESOURCE.getVariableName(), resource );
            variables.put( ScriptVariable.UTILS.getVariableName(), createFhirRepositoryResourceUtils( fhirServerId ) );

            try
            {
                if ( !Boolean.TRUE.equals( scriptExecutor.execute( fhirServerResource.get().getImpTransformScript(), fhirVersion, variables, Collections.emptyMap(), Boolean.class ) ) )
                {
                    throw new FhirResourceTransformationException( "Transformation of resource " + resource.getClass().getSimpleName() + " of FHIR server " + fhirServerId + " has failed." );
                }
            }
            catch ( ScriptExecutionException e )
            {
                throw new FhirResourceTransformationException( "Transformation of resource " + resource.getClass().getSimpleName() + " of FHIR server " + fhirServerId + " has failed.", e );
            }
        }

        return resource;
    }

    @HystrixCommand
    @CacheEvict( key = "{#fhirServer.id, #fhirServer.fhirVersion, " +
        "T(org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType).getByResource(#resource).getResourceTypeName(), #resource.getIdElement().getIdPart(), true}" )
    @Override
    public boolean delete( @Nonnull FhirServer fhirServer, @Nonnull IBaseResource resource )
    {
        final FhirContext fhirContext = fhirContexts.get( fhirServer.getFhirVersion() );
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, fhirServer.getFhirEndpoint() );

        try
        {
            client.delete().resource( resource ).execute();
        }
        catch ( PreconditionFailedException e )
        {
            throw new OptimisticFhirResourceLockException( "Could not delete FHIR resource " +
                resource.getIdElement() + " because of an optimistic locking failure.", e );
        }
        catch ( ResourceNotFoundException | ResourceGoneException e )
        {
            logger.debug( "Resource {} to be deleted could not be found.", resource.getIdElement() );
            return false;
        }
        return true;
    }

    @HystrixCommand
    @CacheEvict( key = "{#fhirServer.id, #fhirServer.fhirVersion, " +
        "T(org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType).getByResource(#resource).getResourceTypeName(), #resource.getIdElement().getIdPart(), true}" )
    @Nonnull
    @Override
    public IBaseResource save( @Nonnull FhirServer fhirServer, @Nonnull IBaseResource resource )
    {
        final FhirContext fhirContext = fhirContexts.get( fhirServer.getFhirVersion() );
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, fhirServer.getFhirEndpoint() );

        final MethodOutcome methodOutcome;
        if ( resource.getIdElement().hasIdPart() )
        {
            try
            {
                methodOutcome = client.update().resource( resource ).prefer( PreferReturnEnum.REPRESENTATION ).execute();
            }
            catch ( PreconditionFailedException e )
            {
                throw new OptimisticFhirResourceLockException( "Could not update FHIR resource " +
                    resource.getIdElement() + " because of an optimistic locking failure.", e );
            }
        }
        else
        {
            methodOutcome = client.create().resource( resource ).prefer( PreferReturnEnum.REPRESENTATION ).execute();
        }

        ProcessedItemInfo processedItemInfo = null;
        if ( (methodOutcome.getResource() != null) && (methodOutcome.getResource().getMeta() != null) )
        {
            // resource itself may contain old version ID (even if it should not)
            processedItemInfo = ProcessedFhirItemInfoUtils.create( methodOutcome.getResource(), methodOutcome.getId().getVersionIdPart() );
        }
        else if ( (methodOutcome.getId() != null) && methodOutcome.getId().hasVersionIdPart() )
        {
            processedItemInfo = ProcessedFhirItemInfoUtils.create( resource, methodOutcome.getId() );
        }

        if ( processedItemInfo == null )
        {
            logger.info( "FHIR server {} does neither return complete resource with update timestamp nor a version. " +
                "Duplicate detection for resource {} will not work.", fhirServer.getId(), methodOutcome.getId() );
        }
        else
        {
            storedItemService.stored( fhirServer, processedItemInfo.toIdString( Instant.now() ) );
        }

        final IBaseResource result;
        if ( methodOutcome.getResource() == null )
        {
            result = resource;
            if ( methodOutcome.getId() != null )
            {
                result.setId( methodOutcome.getId() );
            }
        }
        else
        {
            result = methodOutcome.getResource();
        }

        return result;
    }

    @TransactionalEventListener( phase = TransactionPhase.BEFORE_COMMIT, classes = AutoCreatedFhirServerResourceEvent.class )
    public void autoCreatedSubscriptionResource( @Nonnull AutoCreatedFhirServerResourceEvent event )
    {
        final FhirContext fhirContext = fhirContexts.get( event.getFhirServerResource().getFhirServer().getFhirVersion() );
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, event.getFhirServerResource().getFhirServer().getFhirEndpoint() );

        final MethodOutcome methodOutcome;
        try
        {
            methodOutcome = client.create().resource( createFhirSubscription( event.getFhirServerResource() ) ).execute();
        }
        catch ( BaseServerResponseException e )
        {
            throw new RestBadRequestException( "The subscription could not be created on " +
                event.getFhirServerResource().getFhirServer().getFhirEndpoint().getBaseUrl() + ": " + e.getMessage(), e );
        }
        final String id = methodOutcome.getId().toUnqualifiedVersionless().getIdPart();
        logger.info( "Created FHIR subscription {} for FHIR server {}.", id, event.getFhirServerResource().getFhirServer().getId() );
        event.getFhirServerResource().setFhirSubscriptionId( id );
    }

    @Nonnull
    protected String createWebHookUrl( @Nonnull FhirServerResource fhirServerResource )
    {
        final StringBuilder url = new StringBuilder( fhirServerResource.getFhirServer().getAdapterEndpoint().getBaseUrl() );
        if ( url.charAt( url.length() - 1 ) != '/' )
        {
            url.append( '/' );
        }
        url.append( "remote-fhir-rest-hook/" );
        url.append( fhirServerResource.getFhirServer().getId() );
        url.append( '/' );
        url.append( fhirServerResource.getId() );
        return url.toString();
    }

    @Nullable
    protected abstract IBaseResource getFirstResource( @Nonnull IBaseBundle bundle );

    @Nonnull
    protected abstract Class<? extends IBaseBundle> getBundleClass();

    @Nonnull
    protected Optional<IBaseResource> findByToken( @Nonnull UUID fhirServerId, @Nonnull FhirVersion fhirVersion, @Nonnull SubscriptionFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull String field, @Nonnull SystemCodeValue identifier )
    {
        final FhirContext fhirContext = fhirContexts.get( fhirVersion );
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, fhirEndpoint );

        logger.debug( "Reading {}?{}={} from FHIR endpoints {}.", resourceType, identifier, field, fhirEndpoint.getBaseUrl() );
        final IBaseBundle bundle = client.search().forResource( resourceType ).returnBundle( getBundleClass() )
            .where( new TokenClientParam( field ).exactly().systemAndIdentifier( identifier.getSystem(), identifier.getCode() ) )
            .cacheControl( new CacheControlDirective().setNoCache( true ) ).execute();
        final IBaseResource resource = getFirstResource( bundle );
        logger.debug( "Read {}?{}={} from FHIR endpoints {} (found={}).", resourceType, identifier, field, fhirEndpoint.getBaseUrl(), (resource != null) );
        return Optional.ofNullable( transform( fhirServerId, fhirVersion, resource ) );
    }
}
