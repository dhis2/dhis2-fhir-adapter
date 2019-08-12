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

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.server.SimpleBundleProvider;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.dhis2.fhir.adapter.auth.Authorization;
import org.dhis2.fhir.adapter.auth.AuthorizationContext;
import org.dhis2.fhir.adapter.auth.ForbiddenException;
import org.dhis2.fhir.adapter.auth.UnauthorizedException;
import org.dhis2.fhir.adapter.cache.RequestCacheContext;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.data.model.ProcessedItemInfo;
import org.dhis2.fhir.adapter.dhis.metadata.model.DhisSyncGroup;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceId;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.sync.DhisResourceRepository;
import org.dhis2.fhir.adapter.dhis.sync.StoredDhisResourceService;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.MissingDhisResourceException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.config.FhirRestInterfaceConfig;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProvider;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProviderException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirSearchResult;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirSearchState;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerRequest;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerService;
import org.dhis2.fhir.adapter.fhir.transform.dhis.PreparedDhisToFhirSearch;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.DhisRequest;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.ImmutableDhisRequest;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.WritableDhisRequest;
import org.dhis2.fhir.adapter.lock.LockContext;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link DhisRepository}.
 *
 * @author volsch
 */
@Component
public class DhisRepositoryImpl implements DhisRepository
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final AuthorizationContext authorizationContext;

    private final Authorization systemDhis2Authorization;

    private final LockManager lockManager;

    private final RequestCacheService requestCacheService;

    private final StoredDhisResourceService storedItemService;

    private final DhisResourceRepository dhisResourceRepository;

    private final DhisToFhirTransformerService dhisToFhirTransformerService;

    private final FhirResourceRepository fhirResourceRepository;

    private final FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    private final FhirRestInterfaceConfig fhirRestInterfaceConfig;

    public DhisRepositoryImpl(
        @Nonnull AuthorizationContext authorizationContext,
        @Nonnull Authorization systemDhis2Authorization,
        @Nonnull LockManager lockManager,
        @Nonnull RequestCacheService requestCacheService,
        @Nonnull StoredDhisResourceService storedItemService,
        @Nonnull DhisResourceRepository dhisResourceRepository,
        @Nonnull DhisToFhirTransformerService dhisToFhirTransformerService,
        @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository,
        @Nonnull FhirRestInterfaceConfig fhirRestInterfaceConfig )
    {
        this.authorizationContext = authorizationContext;
        this.systemDhis2Authorization = systemDhis2Authorization;
        this.lockManager = lockManager;
        this.requestCacheService = requestCacheService;
        this.storedItemService = storedItemService;
        this.dhisResourceRepository = dhisResourceRepository;
        this.dhisToFhirTransformerService = dhisToFhirTransformerService;
        this.fhirResourceRepository = fhirResourceRepository;
        this.fhirDhisAssignmentRepository = fhirDhisAssignmentRepository;
        this.fhirRestInterfaceConfig = fhirRestInterfaceConfig;
    }

    @HystrixCommand( ignoreExceptions = { MissingDhisResourceException.class, TransformerDataException.class, TransformerMappingException.class, UnauthorizedException.class } )
    @Override
    @Transactional( propagation = Propagation.NOT_SUPPORTED )
    public void save( @Nonnull DhisSyncGroup syncGroup, @Nonnull DhisResource resource )
    {
        authorizationContext.setAuthorization( systemDhis2Authorization );
        try
        {
            saveInternallyWithMissingDhisResources( syncGroup, resource, new HashSet<>(), true );
        }
        finally
        {
            authorizationContext.resetAuthorization();
        }
    }

    @HystrixCommand( ignoreExceptions = { MissingDhisResourceException.class, TransformerDataException.class, TransformerMappingException.class, UnauthorizedException.class, ForbiddenException.class } )
    @Override
    @Nonnull
    public Optional<IBaseResource> read( @Nonnull FhirClient fhirClient, @Nonnull FhirResourceType fhirResourceType, @Nonnull DhisFhirResourceId dhisFhirResourceId )
    {
        final DhisResourceType dhisResourceType;
        final UUID ruleId;

        if ( dhisFhirResourceId.isQualified() )
        {
            dhisResourceType = dhisFhirResourceId.getType();
            ruleId = dhisFhirResourceId.getRuleId();
        }
        else
        {
            final RuleInfo<? extends AbstractRule> ruleInfo = dhisToFhirTransformerService.findSingleRule( fhirClient, fhirResourceType );

            if ( ruleInfo == null || !ruleInfo.getRule().isSimpleFhirId() )
            {
                return Optional.empty();
            }

            dhisResourceType = ruleInfo.getRule().getDhisResourceType();
            ruleId = ruleInfo.getRule().getId();
        }

        return new OneDhisResourceReader()
        {
            @Nonnull
            @Override
            protected UUID getRuleId()
            {
                return ruleId;
            }

            @Nullable
            @Override
            protected DhisToFhirTransformerRequest createTransformerRequest( @Nonnull FhirResourceType fhirResourceType, @Nonnull DhisRequest dhisRequest, @Nonnull DhisResource dhisResource )
            {
                return dhisToFhirTransformerService.createTransformerRequest( fhirClient, dhisRequest, dhisResource, fhirResourceType, ruleId );
            }

            @Nullable
            @Override
            protected DhisResource getDhisResource()
            {
                return dhisResourceRepository.findRefreshed( dhisFhirResourceId.toQualified( dhisResourceType, ruleId ).getDhisResourceId() ).orElse( null );
            }
        }.read( fhirClient, fhirResourceType );
    }

    @HystrixCommand( ignoreExceptions = { MissingDhisResourceException.class, TransformerDataException.class, TransformerMappingException.class, UnauthorizedException.class, ForbiddenException.class } )
    @Nonnull
    @Override
    public Optional<IBaseResource> readByIdentifier( @Nonnull FhirClient fhirClient, @Nonnull FhirResourceType fhirResourceType, @Nonnull String identifier )
    {
        final RuleInfo<? extends AbstractRule> ruleInfo = dhisToFhirTransformerService.findSingleRule( fhirClient, fhirResourceType );

        if ( ruleInfo == null )
        {
            return Optional.empty();
        }

        return new OneDhisResourceReader()
        {
            @Nonnull
            @Override
            protected UUID getRuleId()
            {
                return ruleInfo.getRule().getId();
            }

            @Nullable
            @Override
            protected DhisToFhirTransformerRequest createTransformerRequest( @Nonnull FhirResourceType fhirResourceType, @Nonnull DhisRequest dhisRequest, @Nonnull DhisResource dhisResource )
            {
                return dhisToFhirTransformerService.createTransformerRequest( fhirClient, dhisRequest, ruleInfo, dhisResource );
            }

            @Nullable
            @Override
            protected DhisResource getDhisResource()
            {
                return dhisToFhirTransformerService.getDataProvider( fhirClient.getFhirVersion(), ruleInfo.getRule().getDhisResourceType() )
                    .findByDhisFhirIdentifierCasted( fhirClient, ruleInfo, identifier );
            }
        }.read( fhirClient, fhirResourceType );
    }

    @HystrixCommand( ignoreExceptions = { MissingDhisResourceException.class, TransformerDataException.class, TransformerMappingException.class, DhisToFhirDataProviderException.class, UnauthorizedException.class, ForbiddenException.class,
        DhisToFhirDataProviderException.class } )
    @Nonnull
    @Override
    public IBundleProvider search( @Nonnull FhirClient fhirClient, @Nonnull FhirResourceType fhirResourceType, @Nullable Integer count, boolean unlimitedCount,
        @Nullable Set<SystemCodeValue> filteredCodes, @Nullable Map<String, List<String>> filter, @Nullable DateRangeParam lastUpdatedDateRange ) throws DhisToFhirDataProviderException
    {
        final int resultingCount;

        if ( unlimitedCount )
        {
            resultingCount = Integer.MAX_VALUE;
        }
        else if ( count == null )
        {
            resultingCount = fhirRestInterfaceConfig.getDefaultSearchCount();
        }
        else
        {
            resultingCount = Math.min( count, fhirRestInterfaceConfig.getMaxSearchCount() );
        }

        List<IBaseResource> result = Collections.emptyList();
        try ( final RequestCacheContext requestCacheContext = requestCacheService.createRequestCacheContext( true ) )
        {
            final List<RuleInfo<? extends AbstractRule>> rules = dhisToFhirTransformerService.findAllRules( fhirClient, fhirResourceType, filteredCodes );
            final Set<DhisResourceType> dhisResourceTypes = rules.stream().map( r -> r.getRule().getDhisResourceType() ).collect( Collectors.toSet() );
            if ( dhisResourceTypes.isEmpty() )
            {
                logger.debug( "No matching rules for FHIR resource {} and codes {}.", fhirResourceType, filteredCodes );
            }
            else if ( dhisResourceTypes.size() > 1 )
            {
                logger.debug( "More than one matching DHIS resource type ({}) for FHIR resource {} and codes {}. " +
                    "Search is not supported in this case.", dhisResourceTypes, fhirResourceType, filteredCodes );
            }
            else
            {
                result = search( fhirClient, fhirResourceType, filteredCodes, filter, lastUpdatedDateRange, dhisResourceTypes.stream().findFirst().get(), rules, resultingCount );
            }
        }
        return new SimpleBundleProvider( result ).setSize( (result.size() < resultingCount) ? result.size() : null );
    }

    @Nonnull
    protected List<IBaseResource> search( @Nonnull FhirClient fhirClient, @Nonnull FhirResourceType fhirResourceType, @Nullable Set<SystemCodeValue> filteredCodes, @Nullable Map<String, List<String>> filter, @Nullable DateRangeParam lastUpdatedDateRange,
        @Nonnull DhisResourceType dhisResourceType, @Nonnull List<RuleInfo<? extends AbstractRule>> rules, int count )
    {
        if ( count == 0 )
        {
            return Collections.emptyList();
        }

        final DhisToFhirDataProvider<? extends AbstractRule> dataProvider = dhisToFhirTransformerService.getDataProvider( fhirClient.getFhirVersion(), dhisResourceType );
        final PreparedDhisToFhirSearch preparedSearch = dataProvider.prepareSearchCasted( fhirClient.getFhirVersion(), rules, filter, lastUpdatedDateRange, count );

        final LinkedList<DhisResource> dhisResources = new LinkedList<>();
        final List<IBaseResource> result = new ArrayList<>();
        DhisToFhirSearchState searchState = null;
        do
        {
            if ( dhisResources.isEmpty() )
            {
                final DhisToFhirSearchResult<? extends DhisResource> searchResult = dataProvider.search( preparedSearch, searchState, count - result.size() );
                if ( searchResult == null )
                {
                    return result;
                }
                searchState = searchResult.getState();

                if ( searchResult.getResult().isEmpty() )
                {
                    continue;
                }

                dhisResources.addAll( searchResult.getResult() );
            }

            final DhisResource dhisResource = Objects.requireNonNull( dhisResources.poll() );
            final WritableDhisRequest dhisRequest = new WritableDhisRequest( true, true, true );
            dhisRequest.setResourceType( dhisResource.getResourceType() );
            dhisRequest.setLastUpdated( dhisResource.getLastUpdated() );

            DhisToFhirTransformerRequest transformerRequest =
                dhisToFhirTransformerService.createTransformerRequest( fhirClient, new ImmutableDhisRequest( dhisRequest ), dhisResource, rules );
            while ( (transformerRequest != null) && (result.size() < count) )
            {
                final DhisToFhirTransformOutcome<? extends IBaseResource> outcome = dhisToFhirTransformerService.transform( transformerRequest );
                if ( outcome == null )
                {
                    transformerRequest = null;
                }
                else
                {
                    if ( outcome.getResource() != null )
                    {
                        result.add( outcome.getResource() );
                    }
                    transformerRequest = outcome.getNextTransformerRequest();
                }
            }
        }
        while ( result.size() < count );
        return result;
    }

    protected boolean saveInternallyWithMissingDhisResources( @Nonnull DhisSyncGroup syncGroup, @Nonnull DhisResource resource, @Nonnull Set<DhisResourceId> missingDhisResourceIds, boolean initial )
    {
        boolean result = false;
        boolean saved = false;
        do
        {
            try
            {
                result = saveInternally( resource );
                saved = true;
            }
            catch ( MissingDhisResourceException e )
            {
                logger.info( "Saving DHIS resource {} failed because of missing DHIS resource {}.",
                    resource.getResourceId(), e.getDhisResourceId() );
                // endless loop must be avoided
                if ( !missingDhisResourceIds.add( e.getDhisResourceId() ) )
                {
                    throw e;
                }

                final DhisResource missingResource = dhisResourceRepository.findRefreshed( e.getDhisResourceId() )
                    .orElseThrow( () ->
                    {
                        logger.warn( "Missing DHIS resource {} could not be found.", e.getDhisResourceId() );
                        return new MissingDhisResourceException( e.getDhisResourceId() );
                    } );

                logger.info( "Saving missing DHIS resource {}.", e.getDhisResourceId() );
                if ( !saveInternallyWithMissingDhisResources( syncGroup, missingResource, missingDhisResourceIds, false ) )
                {
                    logger.info( "Missing DHIS resource {} could not be saved.", e.getDhisResourceId() );
                    throw e;
                }
                logger.info( "Saved missing DHIS resource {}.", e.getDhisResourceId() );
            }
        }
        while ( !saved );

        if ( !initial && storedItemService.isEnabled() )
        {
            final ProcessedItemInfo processedItemInfo = getProcessedItemInfo( resource );
            storedItemService.stored( syncGroup, processedItemInfo.toIdString( Instant.now() ) );
        }
        return result;
    }

    @Nonnull
    private ProcessedItemInfo getProcessedItemInfo( @Nonnull DhisResource resource )
    {
        return new ProcessedItemInfo( resource.getResourceId().toString(),
            Objects.requireNonNull( resource.getLastUpdated() ).toInstant(), resource.isDeleted() );
    }

    protected boolean saveInternally( @Nonnull DhisResource resource )
    {
        final WritableDhisRequest dhisRequest = new WritableDhisRequest( false, true, true );
        dhisRequest.setResourceType( resource.getResourceType() );
        dhisRequest.setLastUpdated( resource.getLastUpdated() );

        DhisToFhirTransformerRequest transformerRequest = dhisToFhirTransformerService.createTransformerRequest( new ImmutableDhisRequest( dhisRequest ), resource );
        if ( transformerRequest == null )
        {
            return false;
        }

        boolean saved = false;
        do
        {
            DhisToFhirTransformOutcome<? extends IBaseResource> outcome;
            try ( final LockContext lockContext = lockManager.begin() )
            {
                try ( final RequestCacheContext requestCacheContext = requestCacheService.createRequestCacheContext( true ) )
                {
                    outcome = dhisToFhirTransformerService.transform( transformerRequest );
                }
                if ( outcome == null )
                {
                    transformerRequest = null;
                }
                else
                {
                    if ( outcome.getResource() != null )
                    {
                        if ( outcome.isDelete() )
                        {
                            final boolean deleted = fhirResourceRepository.delete( transformerRequest.getFhirClient(), outcome.getResource() );
                            fhirDhisAssignmentRepository.deleteFhirResourceId( outcome.getRule(), transformerRequest.getFhirClient(),
                                outcome.getResource().getIdElement() );
                            logger.info( "Deleted (found={}) resource {} for FHIR client {}.", deleted,
                                outcome.getResource().getIdElement().toUnqualifiedVersionless(), transformerRequest.getFhirClient().getId() );
                        }
                        else
                        {
                            final IBaseResource resultingResource = fhirResourceRepository
                                .save( transformerRequest.getFhirClient(), outcome.getResource(), resource.getId() );
                            // resource may have been set as attribute in transformer context (e.g. shared encounter)
                            outcome.getResource().setId( resultingResource.getIdElement() );
                            fhirDhisAssignmentRepository.saveFhirResourceId( outcome.getRule(), transformerRequest.getFhirClient(),
                                resource.getResourceId(), resultingResource.getIdElement() );
                            logger.info( "Saved FHIR resource {} for FHIR client {}.",
                                resultingResource.getIdElement().toUnqualified(), transformerRequest.getFhirClient().getId() );
                        }
                    }
                    saved = true;
                    transformerRequest = outcome.getNextTransformerRequest();
                }
            }
        }
        while ( transformerRequest != null );
        return saved;
    }

    protected abstract class OneDhisResourceReader
    {
        @Nonnull
        public Optional<IBaseResource> read( @Nonnull FhirClient fhirClient, @Nonnull FhirResourceType fhirResourceType )
        {
            IBaseResource resource = null;
            try ( final RequestCacheContext requestCacheContext = requestCacheService.createRequestCacheContext( true ) )
            {
                final DhisResource dhisResource = getDhisResource();

                if ( dhisResource == null )
                {
                    logger.debug( "DHIS resource could not be found." );

                    return Optional.empty();
                }

                final WritableDhisRequest dhisRequest = new WritableDhisRequest( true, true, true );
                dhisRequest.setResourceType( dhisResource.getResourceType() );
                dhisRequest.setLastUpdated( dhisResource.getLastUpdated() );

                DhisToFhirTransformerRequest transformerRequest = createTransformerRequest( fhirResourceType, new ImmutableDhisRequest( dhisRequest ), dhisResource );
                if ( transformerRequest == null )
                {
                    logger.debug( "No matching rule has been found." );

                    return Optional.empty();
                }

                final UUID ruleId = getRuleId();
                do
                {
                    final boolean matchingRule = ( ruleId == null ) || ruleId.equals( transformerRequest.getRuleId() );
                    dhisRequest.setCompleteTransformation( matchingRule );
                    dhisRequest.setIncludeReferences( matchingRule );

                    final DhisToFhirTransformOutcome<? extends IBaseResource> outcome = dhisToFhirTransformerService.transform( transformerRequest );
                    if ( outcome == null )
                    {
                        transformerRequest = null;
                    }
                    else
                    {
                        if ( matchingRule && ( resource == null ) && ( outcome.getResource() != null ) )
                        {
                            resource = outcome.getResource();
                        }
                        transformerRequest = outcome.getNextTransformerRequest();
                    }
                }
                while ( transformerRequest != null );
            }
            return Optional.ofNullable( resource );
        }

        @Nullable
        protected abstract UUID getRuleId();

        @Nullable
        protected abstract DhisResource getDhisResource();

        @Nullable
        protected abstract DhisToFhirTransformerRequest createTransformerRequest( @Nonnull FhirResourceType fhirResourceType, @Nonnull DhisRequest dhisRequest, @Nonnull DhisResource dhisResource );
    }
}
