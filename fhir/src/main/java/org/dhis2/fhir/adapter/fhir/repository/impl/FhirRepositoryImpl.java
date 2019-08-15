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

import ca.uhn.fhir.model.primitive.IdDt;
import com.google.common.collect.ArrayListMultimap;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.dhis2.fhir.adapter.auth.Authorization;
import org.dhis2.fhir.adapter.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.auth.UnauthorizedException;
import org.dhis2.fhir.adapter.cache.RequestCacheContext;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.dhis.DhisConflictException;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.sync.DhisResourceRepository;
import org.dhis2.fhir.adapter.fhir.client.ProcessedFhirItemInfoUtils;
import org.dhis2.fhir.adapter.fhir.client.StoredFhirResourceService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.data.repository.SubscriptionFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.AuthenticationMethod;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientSystem;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryOperation;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepositoryOperationOutcome;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.transform.DhisDataExistsException;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisDeleteTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerService;
import org.dhis2.fhir.adapter.fhir.transform.fhir.TrackedEntityInstanceNotFoundException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.FhirRequestMethod;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.WritableFhirRequest;
import org.dhis2.fhir.adapter.lock.LockContext;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FhirRepository}.
 *
 * @author volsch
 */
@Component
public class FhirRepositoryImpl implements FhirRepository
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    public static final int MAX_CONFLICT_RETRIES = 2;

    private final AuthorizationContext authorizationContext;

    private final LockManager lockManager;

    private final RequestCacheService requestCacheService;

    private final FhirClientSystemRepository fhirClientSystemRepository;

    private final FhirClientResourceRepository fhirClientResourceRepository;

    private final FhirResourceRepository fhirResourceRepository;

    private final FhirToDhisTransformerService fhirToDhisTransformerService;

    private final DhisResourceRepository dhisResourceRepository;

    private final FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    private final ZoneId zoneId = ZoneId.systemDefault();

    private final AtomicLong processedCount = new AtomicLong();

    public FhirRepositoryImpl( @Nonnull AuthorizationContext authorizationContext,
        @Nonnull LockManager lockManager, @Nonnull RequestCacheService requestCacheService,
        @Nonnull FhirClientSystemRepository fhirClientSystemRepository,
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull SubscriptionFhirResourceRepository subscriptionFhirResourceRepository,
        @Nonnull StoredFhirResourceService storedItemService,
        @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirToDhisTransformerService fhirToDhisTransformerService,
        @Nonnull DhisResourceRepository dhisResourceRepository,
        @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository )
    {
        this.authorizationContext = authorizationContext;
        this.lockManager = lockManager;
        this.requestCacheService = requestCacheService;
        this.fhirClientSystemRepository = fhirClientSystemRepository;
        this.fhirClientResourceRepository = fhirClientResourceRepository;
        this.fhirResourceRepository = fhirResourceRepository;
        this.fhirToDhisTransformerService = fhirToDhisTransformerService;
        this.dhisResourceRepository = dhisResourceRepository;
        this.fhirDhisAssignmentRepository = fhirDhisAssignmentRepository;
    }

    @Override
    @Nullable
    @HystrixCommand( ignoreExceptions = { DhisConflictException.class, TransformerDataException.class, TransformerMappingException.class, TrackedEntityInstanceNotFoundException.class, DhisDataExistsException.class, UnauthorizedException.class } )
    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    public FhirRepositoryOperationOutcome save( @Nonnull FhirClientResource fhirClientResource, @Nonnull IBaseResource resource, @Nullable FhirRepositoryOperation fhirRepositoryOperation )
    {
        // if no FHIR repository operation is specified, DHIS2 authorization has not yet been set
        if ( fhirRepositoryOperation == null )
        {
            authorizationContext.setAuthorization( createAuthorization( fhirClientResource.getFhirClient() ) );
            try
            {
                return saveRetriedWithoutTrackedEntityInstance( fhirClientResource, resource );
            }
            finally
            {
                authorizationContext.resetAuthorization();
            }
        }
        return saveInternally( fhirClientResource, resource, fhirRepositoryOperation, false );
    }

    @Override
    @HystrixCommand( ignoreExceptions = { DhisConflictException.class, TransformerDataException.class, TransformerMappingException.class, TrackedEntityInstanceNotFoundException.class, DhisDataExistsException.class, UnauthorizedException.class } )
    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    public boolean delete( @Nonnull FhirClientResource fhirClientResource, @Nonnull DhisFhirResourceId dhisFhirResourceId )
    {
        final FhirToDhisDeleteTransformOutcome<? extends DhisResource> outcome = fhirToDhisTransformerService.delete( fhirClientResource, dhisFhirResourceId );

        if ( outcome == null )
        {
            return false;
        }

        if ( outcome.isDelete() )
        {
            return dhisResourceRepository.delete( outcome.getResource() );
        }

        dhisResourceRepository.save( outcome.getResource() );

        return true;
    }

    @Nonnull
    protected Authorization createAuthorization( @Nonnull FhirClient fhirClient )
    {
        if ( fhirClient.getDhisEndpoint().getAuthenticationMethod() != AuthenticationMethod.BASIC )
        {
            throw new FatalTransformerException( "Unhandled DHIS2 authentication method: " + fhirClient.getDhisEndpoint().getAuthenticationMethod() );
        }

        return new Authorization( "Basic " + Base64.getEncoder().encodeToString(
            (fhirClient.getDhisEndpoint().getUsername() + ":" + fhirClient.getDhisEndpoint().getPassword()).getBytes( StandardCharsets.UTF_8 ) ) );
    }

    @Nullable
    protected FhirRepositoryOperationOutcome saveRetriedWithoutTrackedEntityInstance( @Nonnull FhirClientResource fhirClientResource, @Nonnull IBaseResource resource )
    {
        FhirRepositoryOperationOutcome outcome;
        try
        {
            outcome = saveRetried( fhirClientResource, resource, null, false );
            if ( outcome == null )
            {
                return null;
            }
        }
        catch ( TrackedEntityInstanceNotFoundException e )
        {
            logger.info( "Tracked entity instance {} could not be found for {}. Trying to create tracked entity instance.",
                e.getResource().getIdElement().toUnqualifiedVersionless(), resource.getIdElement().toUnqualifiedVersionless() );
            if ( !createTrackedEntityInstance( fhirClientResource, e.getResource() ) )
            {
                logger.info( "Tracked entity instance {} could not be created for {}. Ignoring FHIR resource.",
                    e.getResource().getIdElement().toUnqualifiedVersionless(), resource.getIdElement().toUnqualifiedVersionless() );
                return null;
            }

            try
            {
                outcome = saveRetried( fhirClientResource, resource, null, false );
                if ( outcome == null )
                {
                    return null;
                }
            }
            catch ( TrackedEntityInstanceNotFoundException e2 )
            {
                logger.info( "Tracked entity instance {} could not be found for {}. Ignoring FHIR resource.",
                    e2.getResource().getIdElement().toUnqualifiedVersionless(), resource.getIdElement().toUnqualifiedVersionless() );
                return null;
            }
        }

        for ( final IAnyResource containedResource : ((IDomainResource) resource).getContained() )
        {
            logger.info( "Processing contained FHIR resource {} with ID {}.",
                containedResource.getClass().getSimpleName(), containedResource.getIdElement().toUnqualifiedVersionless() );
            try
            {
                saveContainedResource( fhirClientResource, containedResource );
            }
            catch ( TrackedEntityInstanceNotFoundException e )
            {
                logger.error( "Processing of contained FHIR resource {} with ID {} returned that tracked entity could not be found: {}",
                    containedResource.getClass().getSimpleName(), containedResource.getIdElement().toUnqualifiedVersionless(), e.getMessage() );
            }
        }

        return outcome;
    }

    @Nullable
    protected FhirRepositoryOperationOutcome saveContainedResource( @Nonnull FhirClientResource fhirClientResource, @Nonnull IBaseResource resource )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResource( resource );
        if ( fhirResourceType == null )
        {
            logger.info( "FHIR resource type for {} is not available. Contained FHIR Resource cannot be processed.", resource.getClass().getSimpleName() );
            return null;
        }

        final FhirClientResource containedFhirClientResource =
            fhirClientResourceRepository.findFirstCached( fhirClientResource.getFhirClient(), fhirResourceType ).orElse( null );
        if ( containedFhirClientResource == null )
        {
            logger.info( "FHIR client {} does not define a resource {}. Contained FHIR Resource cannot be processed.",
                fhirClientResource.getFhirClient().getId(), fhirResourceType );
            return null;
        }

        return saveRetried( containedFhirClientResource, resource, null, true );
    }

    protected boolean createTrackedEntityInstance( @Nonnull FhirClientResource fhirClientResource, @Nonnull IBaseResource resource )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResource( resource );
        if ( fhirResourceType == null )
        {
            logger.info( "FHIR Resource type for {} is not available. Tracked entity instance for FHIR Resource {} cannot be created.",
                resource.getClass().getSimpleName(), resource.getIdElement().toVersionless() );
            return false;
        }

        final FhirClientResource trackedEntityFhirClientResource =
            fhirClientResourceRepository.findFirstCached( fhirClientResource.getFhirClient(), fhirResourceType ).orElse( null );
        if ( trackedEntityFhirClientResource == null )
        {
            logger.info( "FHIR client {} does not define a resource {}. Tracked entity instance for FHIR Resource {} cannot be created.",
                fhirClientResource.getFhirClient().getId(), fhirResourceType, resource.getIdElement() );
            return false;
        }

        final FhirClient fhirClient = trackedEntityFhirClientResource.getFhirClient();
        final Optional<IBaseResource> optionalRefreshedResource = fhirResourceRepository.findRefreshed(
            fhirClient.getId(), fhirClient.getFhirVersion(), fhirClient.getFhirEndpoint(),
            fhirResourceType.getResourceTypeName(), resource.getIdElement().getIdPart() );
        if ( !optionalRefreshedResource.isPresent() )
        {
            logger.info( "Resource {} that should be used as tracked entity resource does no longer exist for FHIR client {}.",
                resource.getIdElement(), trackedEntityFhirClientResource.getFhirClient().getId() );
            return false;
        }

        saveRetried( trackedEntityFhirClientResource, optionalRefreshedResource.get(), null, false );
        return true;
    }

    @Nullable
    protected FhirRepositoryOperationOutcome saveRetried( @Nonnull FhirClientResource fhirClientResource, @Nonnull IBaseResource resource, @Nullable FhirRepositoryOperation fhirRepositoryOperation, boolean contained )
    {
        DhisConflictException lastException = null;
        for ( int i = 0; i < MAX_CONFLICT_RETRIES + 1; i++ )
        {
            try
            {
                return saveInternally( fhirClientResource, resource, fhirRepositoryOperation, contained );
            }
            catch ( DhisConflictException e )
            {
                logger.info( "DHIS2 Conflict reported. Retrying action if possible: " + e.getMessage() );
                lastException = e;
            }
        }
        throw lastException;
    }

    @Nullable
    protected FhirRepositoryOperationOutcome saveInternally( @Nonnull FhirClientResource fhirClientResource, @Nonnull IBaseResource resource, @Nullable FhirRepositoryOperation fhirRepositoryOperation, boolean contained )
    {
        final Collection<FhirClientSystem> systems = fhirClientSystemRepository.findByFhirClient( fhirClientResource.getFhirClient() );

        final WritableFhirRequest fhirRequest = new WritableFhirRequest();
        fhirRequest.setResourceType( FhirResourceType.getByResource( resource ) );
        fhirRequest.setLastUpdated( getLastUpdated( resource ) );
        fhirRequest.setResourceId( resource.getMeta() == null ? null : resource.getIdElement().getIdPart() );
        fhirRequest.setResourceVersionId( resource.getMeta() == null ? null : resource.getMeta().getVersionId() );
        fhirRequest.setFhirClientId( fhirClientResource.getFhirClient().getId() );
        fhirRequest.setFhirClientResourceId( fhirClientResource.getId() );
        fhirRequest.setVersion( fhirClientResource.getFhirClient().getFhirVersion() );
        fhirRequest.setParameters( ArrayListMultimap.create() );
        fhirRequest.setFhirClientCode( fhirClientResource.getFhirClient().getCode() );
        fhirRequest.setResourceSystemsByType( systems.stream()
            .map( s -> new ResourceSystem( s.getFhirResourceType(), s.getSystem().getSystemUri(), s.getCodePrefix(), s.getDefaultValue(), s.getSystem().getFhirDisplayName(), s.isFhirId() ) )
            .collect( Collectors.toMap( ResourceSystem::getFhirResourceType, rs -> rs ) ) );

        if ( fhirRepositoryOperation == null )
        {
            // since automatic parallel processing of multiple resources take place synchronization must be performed
            fhirRequest.setSync( true );
            fhirRequest.setDhisUsername( fhirClientResource.getFhirClient().getDhisEndpoint().getUsername() );
        }
        else
        {
            fhirRequest.setRequestMethod( getRequestMethod( fhirRepositoryOperation ) );
            fhirRequest.setDhisFhirId( true );
            fhirRequest.setFirstRuleOnly( true );

            if ( fhirRepositoryOperation.getDhisFhirResourceId() != null )
            {
                fhirRequest.setDhisResourceType( fhirRepositoryOperation.getDhisFhirResourceId().getType() );
                fhirRequest.setDhisResourceId( fhirRepositoryOperation.getDhisFhirResourceId().getId() );
                fhirRequest.setRuleId( fhirRepositoryOperation.getDhisFhirResourceId().getRuleId() );
            }
        }

        final ProcessedItemInfo processedItemInfo = ProcessedFhirItemInfoUtils.create( resource );
        FhirRepositoryOperationOutcome operationOutcome = null;
        FhirToDhisTransformerRequest transformerRequest = fhirToDhisTransformerService.createTransformerRequest( fhirRequest, fhirClientResource, resource, contained );

        do
        {
            FhirToDhisTransformOutcome<? extends DhisResource> outcome;
            try ( final LockContext lockContext = lockManager.begin() )
            {
                try ( final RequestCacheContext requestCacheContext = requestCacheService.createRequestCacheContext( true ) )
                {
                    outcome = fhirToDhisTransformerService.transform( transformerRequest );
                }

                if ( outcome == null )
                {
                    transformerRequest = null;
                }
                else
                {
                    final boolean created = outcome.isCreated();
                    final DhisResource persistedDhisResource = dhisResourceRepository.save( outcome.getResource() );

                    if ( fhirRepositoryOperation == null )
                    {
                        fhirDhisAssignmentRepository.saveDhisResourceId(
                            outcome.getRule(), fhirClientResource.getFhirClient(),
                            resource.getIdElement(), outcome.getResource().getResourceId() );
                    }

                    if ( operationOutcome == null )
                    {
                        final String dhisFhirResourceId = createDhisFhirResourceId( outcome, persistedDhisResource );

                        if ( fhirRequest.isDhisFhirId() && ( resource.getIdElement().isEmpty() || resource.getIdElement().isLocal() || !DhisFhirResourceId.isValid( resource.getIdElement().getIdPart() ) ) )
                        {
                            resource.setId( new IdDt( Objects.requireNonNull( fhirRequest.getResourceType() ).getResourceTypeName(), dhisFhirResourceId ) );
                        }

                        operationOutcome = new FhirRepositoryOperationOutcome( dhisFhirResourceId, created );
                    }

                    transformerRequest = outcome.getNextTransformerRequest();
                }
            }
        }
        while ( (transformerRequest != null) && !fhirRequest.isFirstRuleOnly() );

        return operationOutcome;
    }

    @Nonnull
    protected String createDhisFhirResourceId( @Nonnull FhirToDhisTransformOutcome<? extends DhisResource> outcome, @Nonnull DhisResource persistedDhisResource )
    {
        if ( outcome.getRule().isSimpleFhirId() )
        {
            return DhisFhirResourceId.toString( null, persistedDhisResource.getId(), null );
        }
        else
        {
            return DhisFhirResourceId.toString( persistedDhisResource.getResourceType(), persistedDhisResource.getId(), outcome.getRule().getId() );
        }
    }

    @Nonnull
    protected FhirRequestMethod getRequestMethod( @Nonnull FhirRepositoryOperation fhirRepositoryOperation )
    {
        switch ( fhirRepositoryOperation.getOperationType() )
        {
            case CREATE:
                return FhirRequestMethod.CREATE;
            case UPDATE:
                return FhirRequestMethod.UPDATE;
            case CREATE_OR_UPDATE:
                return FhirRequestMethod.CREATE_OR_UPDATE;
            default:
                throw new AssertionError( "Unhandled operation type: " + fhirRepositoryOperation.getOperationType() );
        }
    }

    @Nullable
    private ZonedDateTime getLastUpdated( @Nonnull IBaseResource resource )
    {
        final Date lastUpdated = (resource.getMeta() == null) ? null : resource.getMeta().getLastUpdated();
        return (lastUpdated == null) ? null : lastUpdated.toInstant().atZone( zoneId );
    }
}
