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
import org.dhis2.fhir.adapter.fhir.client.ProcessedFhirItemInfoUtils;
import org.dhis2.fhir.adapter.fhir.client.StoredFhirResourceService;
import org.dhis2.fhir.adapter.fhir.metadata.model.ClientFhirEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.event.AutoCreatedFhirClientResourceEvent;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.repository.FhirClientUtils;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceTransformationException;
import org.dhis2.fhir.adapter.fhir.repository.OptimisticFhirResourceLockException;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionException;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.rest.RestBadRequestException;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
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
@Component
@CacheConfig( cacheNames = "fhirResources", cacheManager = "fhirCacheManager" )
public class FhirResourceRepositoryImpl implements FhirResourceRepository
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final ScriptExecutor scriptExecutor;

    private final StoredFhirResourceService storedItemService;

    private final FhirClientResourceRepository fhirClientResourceRepository;

    private final Map<FhirVersion, FhirContext> fhirContexts;

    private final Map<FhirVersion, AbstractFhirResourceRepositorySupport> supports = new HashMap<>();

    public FhirResourceRepositoryImpl( @Nonnull ScriptExecutor scriptExecutor, @Nonnull StoredFhirResourceService storedItemService, @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull ObjectProvider<List<FhirContext>> fhirContexts, @Nonnull ObjectProvider<List<AbstractFhirResourceRepositorySupport>> supports )
    {
        this.scriptExecutor = scriptExecutor;
        this.storedItemService = storedItemService;
        this.fhirClientResourceRepository = fhirClientResourceRepository;
        this.fhirContexts = fhirContexts.getIfAvailable( Collections::emptyList ).stream().filter( fc -> (FhirVersion.get( fc.getVersion().getVersion() ) != null) )
            .collect( Collectors.toMap( fc -> FhirVersion.get( fc.getVersion().getVersion() ), fc -> fc ) );
        supports.getIfAvailable( Collections::emptyList ).forEach( s -> s.getFhirVersions().forEach( v -> FhirResourceRepositoryImpl.this.supports.put( v, s ) ) );
    }

    @Nonnull
    @Override
    public Optional<FhirContext> findFhirContext( @Nonnull FhirVersion fhirVersion )
    {
        return Optional.ofNullable( fhirContexts.get( fhirVersion ) );
    }

    @HystrixCommand( ignoreExceptions = FhirResourceTransformationException.class )
    @CachePut( key = "{#fhirClientId, #fhirVersion, #resourceType, #resourceId, #transform}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findRefreshed( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull String resourceId, boolean transform )
    {
        if ( !fhirEndpoint.isUseRemote() )
        {
            logger.debug( "Remote for FHIR client {} should not be used.", fhirClientId );
            return Optional.empty();
        }

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
        return Optional.ofNullable( transform ? transform( fhirClientId, fhirVersion, resource ) : resource );
    }

    @HystrixCommand( ignoreExceptions = FhirResourceTransformationException.class )
    @CachePut( key = "{#fhirClientId, #fhirVersion, #resourceType, #resourceId, true}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findRefreshed( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull String resourceId )
    {
        return findRefreshed( fhirClientId, fhirVersion, fhirEndpoint, resourceType, resourceId, true );
    }

    @HystrixCommand( ignoreExceptions = FhirResourceTransformationException.class )
    @Cacheable( key = "{#fhirClientId, #fhirVersion, #resourceType, #resourceId, true}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> find( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull String resourceId )
    {
        return findRefreshed( fhirClientId, fhirVersion, fhirEndpoint, resourceType, resourceId );
    }

    @HystrixCommand( ignoreExceptions = FhirResourceTransformationException.class )
    @CachePut( key = "{'findByIdentifier', #fhirClientId, #fhirVersion, #resourceType, #identifier.toString()}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findRefreshedByIdentifier( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull SystemCodeValue identifier )
    {
        return findByToken( fhirClientId, fhirVersion, fhirEndpoint, resourceType, "identifier", identifier );
    }

    @HystrixCommand( ignoreExceptions = FhirResourceTransformationException.class )
    @Cacheable( key = "{'findByIdentifier', #fhirClientId, #fhirVersion, #resourceType, #identifier.toString()}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findByIdentifier( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull SystemCodeValue identifier )
    {
        return findRefreshedByIdentifier( fhirClientId, fhirVersion, fhirEndpoint, resourceType, identifier );
    }

    @HystrixCommand( ignoreExceptions = FhirResourceTransformationException.class )
    @CachePut( key = "{'findByCode', #fhirClientId, #fhirVersion, #resourceType, #code.toString()}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findRefreshedByCode( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull SystemCodeValue code )
    {
        return findByToken( fhirClientId, fhirVersion, fhirEndpoint, resourceType, "code", code );
    }

    @HystrixCommand( ignoreExceptions = FhirResourceTransformationException.class )
    @Cacheable( key = "{'findByCode', #fhirClientId, #fhirVersion, #resourceType, #code.toString()}", unless = "#result==null" )
    @Nonnull
    @Override
    public Optional<IBaseResource> findByCode( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull SystemCodeValue code )
    {
        return findRefreshedByCode( fhirClientId, fhirVersion, fhirEndpoint, resourceType, code );
    }

    @Nullable
    @Override
    public IBaseResource transform( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nullable IBaseResource resource )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResource( resource );
        if ( fhirResourceType == null )
        {
            return resource;
        }

        final Optional<FhirClientResource> fhirClientResource = fhirClientResourceRepository.findFirstCached( fhirClientId, fhirResourceType );
        if ( fhirClientResource.isPresent() && (fhirClientResource.get().getImpTransformScript() != null) )
        {
            final AbstractFhirResourceRepositorySupport support = supports.get( fhirVersion );
            final Map<String, Object> variables = new HashMap<>();
            variables.put( ScriptVariable.RESOURCE.getVariableName(), resource );
            variables.put( ScriptVariable.UTILS.getVariableName(), support.createFhirRepositoryResourceUtils( fhirClientId ) );

            try
            {
                if ( !Boolean.TRUE.equals( scriptExecutor.execute( fhirClientResource.get().getImpTransformScript(), fhirVersion, variables, Collections.emptyMap(), Boolean.class ) ) )
                {
                    throw new FhirResourceTransformationException( "Transformation of resource " + resource.getClass().getSimpleName() + " of FHIR client " + fhirClientId + " has failed." );
                }
            }
            catch ( ScriptExecutionException e )
            {
                throw new FhirResourceTransformationException( "Transformation of resource " + resource.getClass().getSimpleName() + " of FHIR client " + fhirClientId + " has failed.", e );
            }
        }

        return resource;
    }

    @HystrixCommand
    @CacheEvict( key = "{#fhirClient.id, #fhirClient.fhirVersion, " +
        "T(org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType).getByResource(#resource).getResourceTypeName(), #resource.getIdElement().getIdPart(), true}" )
    @Override
    public boolean delete( @Nonnull FhirClient fhirClient, @Nonnull IBaseResource resource )
    {
        if ( !fhirClient.getFhirEndpoint().isUseRemote() )
        {
            logger.debug( "Remote for FHIR client {} should not be used. Deletion of resource will not be performed.", fhirClient.getId() );
            return false;
        }

        final FhirContext fhirContext = fhirContexts.get( fhirClient.getFhirVersion() );
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, fhirClient.getFhirEndpoint() );

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
    @CacheEvict( key = "{#fhirClient.id, #fhirClient.fhirVersion, " +
        "T(org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType).getByResource(#resource).getResourceTypeName(), #resource.getIdElement().getIdPart(), true}" )
    @Nonnull
    @Override
    public IBaseResource save( @Nonnull FhirClient fhirClient, @Nonnull IBaseResource resource, @Nullable String dhisResourceId )
    {
        if ( !fhirClient.getFhirEndpoint().isUseRemote() )
        {
            logger.debug( "Remote for FHIR client {} should not be used. Saving of resource will not be performed.", fhirClient.getId() );
            return resource;
        }

        final FhirContext fhirContext = fhirContexts.get( fhirClient.getFhirVersion() );
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, fhirClient.getFhirEndpoint() );

        final IBaseResource preparedResource = prepareResource( resource, dhisResourceId );
        final MethodOutcome methodOutcome;

        if ( resource.getIdElement().hasIdPart() )
        {
            try
            {
                methodOutcome = client.update().resource( preparedResource ).prefer( PreferReturnEnum.REPRESENTATION ).execute();
            }
            catch ( PreconditionFailedException e )
            {
                throw new OptimisticFhirResourceLockException( "Could not update FHIR resource " +
                    preparedResource.getIdElement() + " because of an optimistic locking failure.", e );
            }
        }
        else
        {
            methodOutcome = client.create().resource( preparedResource ).prefer( PreferReturnEnum.REPRESENTATION ).execute();
        }

        ProcessedItemInfo processedItemInfo = null;

        if ( (methodOutcome.getResource() != null) && (methodOutcome.getResource().getMeta() != null) )
        {
            // resource itself may contain old version ID (even if it should not)
            processedItemInfo = ProcessedFhirItemInfoUtils.create( methodOutcome.getResource(), methodOutcome.getId().getVersionIdPart() );
        }
        else if ( (methodOutcome.getId() != null) && methodOutcome.getId().hasVersionIdPart() )
        {
            processedItemInfo = ProcessedFhirItemInfoUtils.create( preparedResource, methodOutcome.getId() );
        }

        if ( processedItemInfo == null )
        {
            logger.info( "FHIR client {} does neither return complete resource with update timestamp nor a version. " +
                "Duplicate detection for resource {} will not work.", fhirClient.getId(), methodOutcome.getId() );
        }
        else
        {
            storedItemService.stored( fhirClient, processedItemInfo.toIdString( Instant.now() ) );
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

    @Nonnull
    protected <T extends IBaseResource> T prepareResource( @Nonnull T resource, @Nullable String dhisResourceId )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResource( resource );

        if ( fhirResourceType == null )
        {
            throw new FhirResourceTransformationException( "Could not determine FHIR resource type for " + resource.getClass().getSimpleName() );
        }

        if ( fhirResourceType.isSyncDhisId() && dhisResourceId != null && !resource.getIdElement().hasIdPart() )
        {
            resource.setId( dhisResourceId );
        }

        return resource;
    }

    @TransactionalEventListener( phase = TransactionPhase.BEFORE_COMMIT, classes = AutoCreatedFhirClientResourceEvent.class )
    public void autoCreatedSubscriptionResource( @Nonnull AutoCreatedFhirClientResourceEvent event )
    {
        final FhirVersion fhirVersion = event.getFhirClientResource().getFhirClient().getFhirVersion();
        final FhirContext fhirContext = fhirContexts.get( fhirVersion );
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, event.getFhirClientResource().getFhirClient().getFhirEndpoint() );

        final AbstractFhirResourceRepositorySupport support = supports.get( fhirVersion );
        final MethodOutcome methodOutcome;
        try
        {
            methodOutcome = client.create().resource( support.createFhirSubscription( event.getFhirClientResource() ) ).execute();
        }
        catch ( BaseServerResponseException e )
        {
            throw new RestBadRequestException( "The subscription could not be created on " +
                event.getFhirClientResource().getFhirClient().getFhirEndpoint().getBaseUrl() + ": " + e.getMessage(), e );
        }
        final String id = methodOutcome.getId().toUnqualifiedVersionless().getIdPart();
        logger.info( "Created FHIR subscription {} for FHIR client {}.", id, event.getFhirClientResource().getFhirClient().getId() );
        event.getFhirClientResource().setFhirSubscriptionId( id );
    }

    @Nonnull
    protected Optional<IBaseResource> findByToken( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull String field, @Nonnull SystemCodeValue identifier )
    {
        if ( !fhirEndpoint.isUseRemote() )
        {
            logger.debug( "Remote for FHIR client {} should not be used. Finding by token will not be performed.", fhirClientId );
            return Optional.empty();
        }

        final FhirContext fhirContext = fhirContexts.get( fhirVersion );
        final IGenericClient client = FhirClientUtils.createClient( fhirContext, fhirEndpoint );
        final AbstractFhirResourceRepositorySupport support = supports.get( fhirVersion );

        logger.debug( "Reading {}?{}={} from FHIR endpoints {}.", resourceType, identifier, field, fhirEndpoint.getBaseUrl() );
        final IBaseBundle bundle = client.search().forResource( resourceType ).returnBundle( support.getBundleClass() )
            .where( new TokenClientParam( field ).exactly().systemAndIdentifier( identifier.getSystem(), identifier.getCode() ) )
            .cacheControl( new CacheControlDirective().setNoCache( true ).setNoStore( true ).setMaxResults( 1 ) ).execute();
        final IBaseResource resource = support.getFirstResource( bundle );
        logger.debug( "Read {}?{}={} from FHIR endpoints {} (found={}).", resourceType, identifier, field, fhirEndpoint.getBaseUrl(), (resource != null) );
        return Optional.ofNullable( transform( fhirClientId, fhirVersion, resource ) );
    }
}
