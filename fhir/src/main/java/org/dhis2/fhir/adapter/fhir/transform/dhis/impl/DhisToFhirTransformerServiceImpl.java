package org.dhis2.fhir.adapter.fhir.transform.dhis.impl;

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
import org.dhis2.fhir.adapter.cache.RequestCacheContext;
import org.dhis2.fhir.adapter.cache.RequestCacheService;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.AvailableFhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientSystem;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RuleRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.model.FhirVersionedValue;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirDataProvider;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerRequest;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerService;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractFhirResourceDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.DhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.DhisRequest;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.ImmutableDhisRequest;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.WritableDhisRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.scripted.FhirReferenceResolver;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ImmutableScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisResource;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.fhir.transform.util.DhisBeanTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockContext;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of {@link DhisToFhirTransformerService}.
 *
 * @author volsch
 */
@Service
public class DhisToFhirTransformerServiceImpl implements DhisToFhirTransformerService, FhirReferenceResolver
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    public static final String TRANSFORMER_REQUEST_CACHE_MANAGER_NAME = "transformerRequest";

    public static final String TRANSFORMER_REQUEST_CACHE_NAME = "dhisFhirTransformerRequest";

    private final LockManager lockManager;

    private final FhirClientResourceRepository fhirClientResourceRepository;

    private final FhirClientSystemRepository fhirClientSystemRepository;

    private final OrganizationUnitService organizationUnitService;

    private final RuleRepository ruleRepository;

    private final RequestCacheService requestCacheService;

    private final Map<DhisResourceType, DhisToFhirRequestResolver> requestResolvers = new HashMap<>();

    private final Map<FhirVersion, Map<DhisResourceType, DhisToFhirDataProvider<? extends AbstractRule>>> dataProviders = new HashMap<>();

    private final Map<FhirVersionedValue<DhisResourceType>, SortedSet<DhisToFhirTransformer<?, ?>>> transformers = new HashMap<>();

    private final Map<FhirVersion, Map<String, DhisToFhirTransformerUtils>> transformerUtils = new HashMap<>();

    private final ScriptExecutor scriptExecutor;

    private final ScriptExecutionContext scriptExecutionContext;

    public DhisToFhirTransformerServiceImpl( @Nonnull LockManager lockManager,
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull FhirClientSystemRepository fhirClientSystemRepository,
        @Nonnull OrganizationUnitService organizationUnitService,
        @Nonnull RuleRepository ruleRepository,
        @Nonnull RequestCacheService requestCacheService,
        @Nonnull ObjectProvider<List<DhisToFhirRequestResolver>> requestResolvers,
        @Nonnull ObjectProvider<List<DhisToFhirDataProvider<? extends AbstractRule>>> dataProviders,
        @Nonnull ObjectProvider<List<DhisToFhirTransformer<?, ?>>> transformersProvider,
        @Nonnull ObjectProvider<List<DhisToFhirTransformerUtils>> transformUtilsProvider,
        @Nonnull ScriptExecutor scriptExecutor, @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        this.lockManager = lockManager;
        this.fhirClientResourceRepository = fhirClientResourceRepository;
        this.fhirClientSystemRepository = fhirClientSystemRepository;
        this.organizationUnitService = organizationUnitService;
        this.ruleRepository = ruleRepository;
        this.requestCacheService = requestCacheService;
        this.scriptExecutor = scriptExecutor;
        this.scriptExecutionContext = scriptExecutionContext;

        requestResolvers.ifAvailable( resolvers -> resolvers.forEach( r -> this.requestResolvers.put( r.getDhisResourceType(), r ) ) );

        dataProviders.ifAvailable( providers ->
        {
            for ( final DhisToFhirDataProvider<?> dataProvider : providers )
            {
                for ( final FhirVersion fhirVersion : dataProvider.getFhirVersions() )
                {
                    this.dataProviders.computeIfAbsent( fhirVersion, key -> new HashMap<>() ).put( dataProvider.getDhisResourceType(), dataProvider );
                }
            }
        } );

        transformersProvider.ifAvailable( transformers ->
        {
            for ( final DhisToFhirTransformer<?, ?> transformer : transformers )
            {
                for ( final FhirVersion fhirVersion : transformer.getFhirVersions() )
                {
                    final FhirVersionedValue<DhisResourceType> fhirVersionedValue = new FhirVersionedValue<>( fhirVersion, transformer.getDhisResourceType() );
                    this.transformers.computeIfAbsent( new FhirVersionedValue<>( fhirVersion, transformer.getDhisResourceType() ),
                        fvv -> new TreeSet<>( Collections.reverseOrder() ) ).add( transformer );
                }
            }
        } );

        transformUtilsProvider.ifAvailable( dhisToFhirTransformerUtils ->
        {
            for ( final DhisToFhirTransformerUtils tu : dhisToFhirTransformerUtils )
            {
                for ( final FhirVersion fhirVersion : tu.getFhirVersions() )
                {
                    this.transformerUtils.computeIfAbsent( fhirVersion, key -> new HashMap<>() ).put( tu.getScriptAttrName(), tu );
                }
            }
        } );
    }

    @Nonnull
    @Override
    public DhisToFhirDataProvider<? extends AbstractRule> getDataProvider( @Nonnull FhirVersion fhirVersion, @Nonnull DhisResourceType dhisResourceType )
    {
        final DhisToFhirDataProvider<? extends AbstractRule> dataProvider =
            dataProviders.computeIfAbsent( fhirVersion, version -> new HashMap<>() ).get( dhisResourceType );

        if ( dataProvider == null )
        {
            throw new TransformerMappingException( "No data provider can be found for FHIR version " + fhirVersion + " and DHIS resource type " + dhisResourceType );
        }

        return dataProvider;
    }

    @Nonnull
    @Override
    public List<RuleInfo<? extends AbstractRule>> findAllRules( @Nonnull FhirClient fhirClient, @Nonnull FhirResourceType fhirResourceType, @Nullable Collection<SystemCodeValue> systemCodeValues )
    {
        return ruleRepository.findAllExpByInputData( fhirResourceType, systemCodeValues );
    }

    @Nonnull
    @Override
    public List<IBaseReference> resolveFhirReferences( @Nonnull FhirClient fhirClient, @Nonnull ScriptedDhisResource dhisResource, @Nullable Set<FhirResourceType> fhirResourceTypes, int max )
    {
        final WritableDhisRequest dhisRequest = new WritableDhisRequest( true, false, false );
        dhisRequest.setResourceType( dhisResource.getResourceType() );

        DhisToFhirTransformerRequest transformerRequest = createTransformerRequest(
            fhirClient, new ImmutableDhisRequest( dhisRequest ), dhisResource, fhirResourceTypes );

        if ( transformerRequest == null )
        {
            logger.debug( "No matching rule has been found for FHIR references." );
            return Collections.emptyList();
        }

        final List<IBaseReference> references = new ArrayList<>();

        if ( transformerRequest.isSimpleFhirIdRule() )
        {
            references.add( resolveSimpleFhirIdReference( transformerRequest ) );
        }
        else
        {
            // retrieving last updated may initialize resource event if not required for simple FHIR ID
            dhisRequest.setLastUpdated( dhisResource.getLastUpdated() );
            final AbstractFhirResourceDhisToFhirTransformerUtils fhirResourceTransformerUtils = getFhirResourceTransformerUtils( fhirClient.getFhirVersion() );

            do
            {
                final DhisToFhirTransformOutcome<? extends IBaseResource> outcome = transform( transformerRequest );

                if ( outcome == null )
                {
                    transformerRequest = null;
                }
                else
                {
                    if ( outcome.getResource() != null )
                    {
                        references.add( fhirResourceTransformerUtils.createReference( outcome.getResource() ) );
                    }

                    transformerRequest = outcome.getNextTransformerRequest();
                }
            }
            while ( transformerRequest != null && references.size() < max );
        }

        return references;
    }

    @Nonnull
    protected IBaseReference resolveSimpleFhirIdReference( @Nonnull DhisToFhirTransformerRequest transformerRequest )
    {
        final AbstractFhirResourceDhisToFhirTransformerUtils fhirResourceTransformerUtils = getFhirResourceTransformerUtils( transformerRequest.getContext().getFhirVersion() );
        final DhisToFhirTransformerRequestImpl transformerRequestImpl = (DhisToFhirTransformerRequestImpl) transformerRequest;
        final RuleInfo<? extends AbstractRule> ruleInfo = Objects.requireNonNull( transformerRequestImpl.nextRule() );

        final FhirResourceType fhirResourceType = ruleInfo.getRule().getFhirResourceType();
        final IBaseResource resource = fhirResourceTransformerUtils.createResource( fhirResourceType );
        resource.setId( new IdDt( fhirResourceType.getResourceTypeName(), transformerRequestImpl.getInput().getId() ) );

        return fhirResourceTransformerUtils.createReference( resource );
    }

    @Nullable
    protected DhisToFhirTransformerRequest createTransformerRequest( @Nonnull FhirClient fhirClient, @Nonnull DhisRequest dhisRequest, @Nonnull ScriptedDhisResource scriptedDhisResource, @Nullable Set<FhirResourceType> fhirResourceTypes )
    {
        return createTransformerRequest( dhisRequest, rr -> scriptedDhisResource, ri -> (fhirResourceTypes == null) || fhirResourceTypes.isEmpty() || fhirResourceTypes.contains( ri.getRule().getFhirResourceType() ),
            ( si, rr ) -> fhirClient );
    }

    @Nullable
    @Override
    public RuleInfo<? extends AbstractRule> findSingleRule( @Nonnull FhirClient fhirClient, @Nonnull FhirResourceType fhirResourceType )
    {
        return ruleRepository.findOneExpByDhisFhirInputData( fhirResourceType ).orElse( null );
    }

    @Nullable
    @Override
    public DhisToFhirTransformerRequest createTransformerRequest( @Nonnull FhirClient fhirClient, @Nonnull DhisRequest dhisRequest, @Nonnull RuleInfo<? extends AbstractRule> ruleInfo, @Nonnull DhisResource resource )
    {
        final DhisToFhirRequestResolver requestResolver = getRequestResolver( dhisRequest.getResourceType() );
        final ScriptedDhisResource scriptedResource =
            requestResolver.convert( Objects.requireNonNull( DhisBeanTransformerUtils.clone( resource ) ), dhisRequest );

        return createTransformerRequest( false, fhirClient, dhisRequest, scriptedResource, Collections.singletonList( ruleInfo ) );
    }

    @Nullable
    @Override
    public DhisToFhirTransformerRequest createTransformerRequest( @Nonnull FhirClient fhirClient, @Nonnull DhisRequest dhisRequest, @Nonnull DhisResource resource, @Nonnull List<RuleInfo<? extends AbstractRule>> rules )
    {
        final DhisToFhirRequestResolver requestResolver = getRequestResolver( dhisRequest.getResourceType() );

        return createTransformerRequest( dhisRequest, rr -> rr.convert( resource, dhisRequest ), requestResolver,
            ( rr, scriptedResource ) -> rr.filterRules( scriptedResource, rules ), ri -> true, ( si, rr ) -> fhirClient );
    }

    @Nullable
    @Override
    public DhisToFhirTransformerRequest createTransformerRequest( @Nonnull DhisRequest dhisRequest, @Nonnull DhisResource resource )
    {
        return createTransformerRequest( dhisRequest, rr -> rr.convert( Objects.requireNonNull( DhisBeanTransformerUtils.clone( resource ) ), dhisRequest ),
            ri -> true, ( si, rr ) -> rr.resolveFhirClient( si ).orElse( null ) );
    }

    @Nullable
    protected DhisToFhirTransformerRequest createTransformerRequest( @Nonnull DhisRequest dhisRequest, @Nonnull Function<DhisToFhirRequestResolver, ScriptedDhisResource> scriptedDhisResourceFunction,
        @Nonnull Predicate<RuleInfo<? extends AbstractRule>> ruleInfoPredicate, @Nonnull BiFunction<ScriptedDhisResource, DhisToFhirRequestResolver, FhirClient> fhirClientFunction )
    {
        final DhisToFhirRequestResolver requestResolver = getRequestResolver( dhisRequest.getResourceType() );

        return createTransformerRequest( dhisRequest, scriptedDhisResourceFunction, requestResolver, DhisToFhirRequestResolver::resolveRules, ruleInfoPredicate, fhirClientFunction );
    }

    @Nullable
    protected DhisToFhirTransformerRequest createTransformerRequest( @Nonnull DhisRequest dhisRequest, @Nonnull Function<DhisToFhirRequestResolver, ScriptedDhisResource> scriptedDhisResourceFunction,
        @Nonnull DhisToFhirRequestResolver requestResolver, @Nonnull BiFunction<DhisToFhirRequestResolver, ScriptedDhisResource, List<RuleInfo<? extends AbstractRule>>> ruleResolverFunction,
        @Nonnull Predicate<RuleInfo<? extends AbstractRule>> ruleInfoPredicate, @Nonnull BiFunction<ScriptedDhisResource, DhisToFhirRequestResolver, FhirClient> fhirClientFunction )
    {
        final ScriptedDhisResource scriptedResource = scriptedDhisResourceFunction.apply( requestResolver );
        final List<RuleInfo<? extends AbstractRule>> ruleInfos = ruleResolverFunction.apply( requestResolver, scriptedResource )
            .stream().filter( ruleInfoPredicate ).collect( Collectors.toList() );

        if ( ruleInfos.isEmpty() )
        {
            logger.info( "Could not find any rule to process DHIS resource." );
            return null;
        }

        final FhirClient fhirClient = fhirClientFunction.apply( scriptedResource, requestResolver );

        if ( fhirClient == null )
        {
            logger.info( "Could not determine FHIR client to process DHIS resource." );
            return null;
        }
        return createTransformerRequest( false, fhirClient, dhisRequest, scriptedResource, ruleInfos );
    }

    @Nullable
    @Override
    public DhisToFhirTransformerRequest createTransformerRequest( @Nonnull FhirClient fhirClient, @Nonnull DhisRequest dhisRequest, @Nonnull DhisResource dhisResource,
        @Nonnull FhirResourceType fhirResourceType, @Nonnull UUID ruleId )
    {
        final DhisToFhirRequestResolver requestResolver = getRequestResolver( dhisResource.getResourceType() );
        final ScriptedDhisResource scriptedDhisResource = requestResolver.convert(
            Objects.requireNonNull( DhisBeanTransformerUtils.clone( dhisResource ) ), dhisRequest );

        final List<RuleInfo<? extends AbstractRule>> ruleInfos = requestResolver.resolveRules( scriptedDhisResource );
        final RuleInfo<? extends AbstractRule> matchingRuleInfo = ruleInfos.stream().filter( ri -> ri.getRule().getId().equals( ruleId ) ).findFirst().orElse( null );

        if ( matchingRuleInfo == null || !fhirResourceType.equals( matchingRuleInfo.getRule().getFhirResourceType() ) )
        {
            logger.info( "Could not find any matching rule to process DHIS resource." );

            return null;
        }

        final List<RuleInfo<? extends AbstractRule>> groupingRuleInfos = ruleInfos.stream()
            .filter( ri -> ri.getRule().isGrouping() ).collect( Collectors.toList() );

        final List<RuleInfo<? extends AbstractRule>> resultingRuleInfos;

        if ( groupingRuleInfos.isEmpty() )
        {
            resultingRuleInfos = Collections.singletonList( matchingRuleInfo );
        }
        else if ( matchingRuleInfo.getRule().isGrouping() )
        {
            resultingRuleInfos = ruleInfos;
        }
        else
        {
            resultingRuleInfos = ruleInfos.stream()
                .filter( ri -> ri.getRule().isGrouping() || ri.getRule().getId().equals( ruleId ) ).collect( Collectors.toList() );
        }

        return createTransformerRequest( matchingRuleInfo.getRule().isGrouping(), fhirClient, dhisRequest, scriptedDhisResource, resultingRuleInfos );
    }

    @Nullable
    protected DhisToFhirTransformerRequest createTransformerRequest( boolean grouping, @Nonnull FhirClient fhirClient, @Nonnull DhisRequest dhisRequest, @Nullable ScriptedDhisResource scriptedInput, @Nonnull List<RuleInfo<? extends AbstractRule>> ruleInfos )
    {
        final Collection<AvailableFhirClientResource> availableResources = fhirClientResourceRepository.findAllAvailable( fhirClient );
        ruleInfos = filterAvailableResourceRules( ruleInfos, availableResources );

        final Collection<FhirClientSystem> systems = fhirClientSystemRepository.findByFhirClient( fhirClient );
        final Map<FhirResourceType, ResourceSystem> resourceSystemsByType = systems.stream()
            .map( s -> new ResourceSystem( s.getFhirResourceType(), s.getSystem().getSystemUri(), s.getCodePrefix(), s.getDefaultValue(), s.getSystem().getFhirDisplayName(), s.isFhirId() ) )
            .collect( Collectors.toMap( ResourceSystem::getFhirResourceType, rs -> rs ) );

        final Map<String, DhisToFhirTransformerUtils> transformerUtils = this.transformerUtils.get( fhirClient.getFhirVersion() );

        if ( transformerUtils == null )
        {
            throw new TransformerMappingException( "No transformer utils can be found for FHIR version " + fhirClient.getFhirVersion() );
        }

        return new DhisToFhirTransformerRequestImpl(
            new DhisToFhirTransformerContextImpl( grouping, dhisRequest, fhirClient, this, resourceSystemsByType, availableResources ),
            scriptedInput, fhirClient, ruleInfos, transformerUtils );
    }

    @Override
    @Nonnull
    public DhisToFhirTransformerRequest updateTransformerRequest( @Nonnull DhisRequest dhisRequest, @Nonnull DhisToFhirTransformerRequest transformerRequest, @Nonnull DhisResource dhisResource )
    {
        final DhisToFhirRequestResolver requestResolver = getRequestResolver( dhisResource.getResourceType() );
        final DhisResource input = Objects.requireNonNull( DhisBeanTransformerUtils.clone( dhisResource ) );
        final ScriptedDhisResource scriptedInput = requestResolver.convert( input, dhisRequest );

        final DhisToFhirTransformerRequestImpl transformerRequestImpl = (DhisToFhirTransformerRequestImpl) transformerRequest;
        transformerRequestImpl.setInput( scriptedInput );

        return transformerRequest;
    }

    @Nonnull
    private List<RuleInfo<? extends AbstractRule>> filterAvailableResourceRules( @Nonnull List<RuleInfo<? extends AbstractRule>> ruleInfos, @Nonnull Collection<AvailableFhirClientResource> availableResources )
    {
        final Map<FhirResourceType, AvailableFhirClientResource> availableResourcesByType = availableResources.stream()
            .collect( Collectors.toMap( AvailableFhirClientResource::getResourceType, a -> a ) );

        // not all resources may be available on FHIR client
        return ruleInfos.stream().filter( r -> {
            final AvailableFhirClientResource availableResource = availableResourcesByType.get( r.getRule().getFhirResourceType() );
            if ( availableResource == null )
            {
                return false;
            }
            return !availableResource.isVirtual() || r.getRule().isContainedAllowed();
        } ).collect( Collectors.toList() );
    }

    @Nonnull
    private DhisToFhirRequestResolver getRequestResolver( @Nonnull DhisResourceType resourceType )
    {
        final DhisToFhirRequestResolver requestResolver = requestResolvers.get( resourceType );

        if ( requestResolver == null )
        {
            throw new TransformerMappingException( "No request resolver can be found for DHIS resource type " + resourceType );
        }

        return requestResolver;
    }

    @Nullable
    @Override
    public DhisToFhirTransformOutcome<? extends IBaseResource> transform( @Nonnull DhisToFhirTransformerRequest transformerRequest ) throws TransformerException
    {
        final DhisToFhirTransformerRequestImpl transformerRequestImpl = (DhisToFhirTransformerRequestImpl) transformerRequest;

        final boolean firstRule = transformerRequestImpl.isFirstRule();
        RuleInfo<? extends AbstractRule> ruleInfo;

        while ( (ruleInfo = transformerRequestImpl.nextRule()) != null )
        {
            Cache transformerRequestCache = null;
            TransformerRequestKey transformerRequestKey = null;
            if ( transformerRequestImpl.isSingleRule() )
            {
                final RequestCacheContext cacheContext = requestCacheService.getCurrentRequestCacheContext();
                if ( cacheContext != null )
                {
                    transformerRequestCache = Objects.requireNonNull( cacheContext.getCacheManager( TRANSFORMER_REQUEST_CACHE_MANAGER_NAME ).getCache( TRANSFORMER_REQUEST_CACHE_NAME ) );
                    transformerRequestKey = new TransformerRequestKey( ruleInfo.getRule().getId(), Objects.requireNonNull( transformerRequestImpl.getInput().getResourceId() ) );
                    final IBaseResource cachedOutcomeResource = transformerRequestCache.get( transformerRequestKey, IBaseResource.class );
                    if ( cachedOutcomeResource != null )
                    {
                        logger.debug( "Re-used transformer outcome for key {}.", transformerRequestKey );
                        return new DhisToFhirTransformOutcome<>( ruleInfo.getRule(), cachedOutcomeResource );
                    }
                }
            }

            final SortedSet<DhisToFhirTransformer<?, ?>> transformers = this.transformers.get(
                new FhirVersionedValue<>( transformerRequestImpl.getContext().getVersion(), ruleInfo.getRule().getDhisResourceType() ) );

            if ( transformers == null )
            {
                throw new TransformerMappingException( "No transformer can be found for FHIR version " +
                    transformerRequestImpl.getContext().getVersion() + " mapping of DHIS resource type " + ruleInfo.getRule().getDhisResourceType() );
            }

            final Map<String, Object> scriptVariables = new HashMap<>( transformerRequestImpl.getTransformerUtils() );
            scriptVariables.put( ScriptVariable.CONTEXT.getVariableName(), transformerRequestImpl.getContext() );
            scriptVariables.put( ScriptVariable.INPUT.getVariableName(), transformerRequestImpl.getInput() );
            scriptVariables.put( ScriptVariable.ORGANIZATION_UNIT_ID.getVariableName(), transformerRequestImpl.getInput().getOrganizationUnitId() );
            scriptVariables.put( ScriptVariable.ORGANIZATION_UNIT.getVariableName(), getScriptedOrganizationUnit( transformerRequestImpl.getInput() ) );
            if ( isApplicable( transformerRequestImpl.getContext(), transformerRequestImpl.getInput(), ruleInfo, scriptVariables ) )
            {
                for ( final DhisToFhirTransformer<?, ?> transformer : transformers )
                {
                    final DhisToFhirTransformOutcome<? extends IBaseResource> outcome = transformer.transformCasted( transformerRequest.getFhirClient(),
                        transformerRequestImpl.getContext(), transformerRequestImpl.getInput(), ruleInfo, scriptVariables );

                    if ( outcome != null )
                    {
                        logger.debug( "Rule {} used successfully for transformation of {} (stop={}).",
                            ruleInfo, transformerRequestImpl.getInput().getResourceId(), ruleInfo.getRule().isStop() );

                        if ( ( transformerRequestKey != null ) && !outcome.isDelete() )
                        {
                            transformerRequestCache.put( transformerRequestKey, outcome.getResource() );
                        }

                        return new DhisToFhirTransformOutcome<>( outcome, transformerRequestImpl.isLastRule() ? null : transformerRequestImpl );
                    }

                    // if the previous transformation caused a lock of any resource this must be released since the transformation has been rolled back
                    lockManager.getCurrentLockContext().ifPresent( LockContext::unlockAll );
                }
            }
        }

        if ( firstRule )
        {
            logger.debug( "No matching rule for {}.", transformerRequestImpl.getInput().getResourceId() );
        }

        return null;
    }

    @Nullable
    private ScriptedOrganizationUnit getScriptedOrganizationUnit( @Nonnull ScriptedDhisResource dhisResource )
    {
        if ( (dhisResource.getOrganizationUnitId() == null) || (DhisResourceType.ORGANIZATION_UNIT == dhisResource.getResourceType()) )
        {
            return null;
        }
        final OrganizationUnit organizationUnit = organizationUnitService.findMetadataByReference( new Reference( dhisResource.getOrganizationUnitId(), ReferenceType.ID ) )
            .orElseThrow( () -> new TransformerDataException( "DHIS resource " + dhisResource.getResourceId() + " reference organization unit " + dhisResource.getOrganizationUnitId() + " that cannot be found." ) );
        return new ImmutableScriptedOrganizationUnit( new WritableScriptedOrganizationUnit( organizationUnit, scriptExecutionContext ) );
    }

    private boolean isApplicable( @Nonnull DhisToFhirTransformerContext context, @Nonnull ScriptedDhisResource input,
        @Nonnull RuleInfo<? extends AbstractRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( ruleInfo.getRule().getApplicableExpScript() == null )
        {
            return true;
        }
        return Boolean.TRUE.equals( TransformerUtils.executeScript( scriptExecutor, context, ruleInfo, ruleInfo.getRule().getApplicableExpScript(), scriptVariables, Boolean.class ) );
    }

    @Nonnull
    private AbstractFhirResourceDhisToFhirTransformerUtils getFhirResourceTransformerUtils( @Nonnull FhirVersion fhirVersion )
    {
        final AbstractFhirResourceDhisToFhirTransformerUtils utils = (AbstractFhirResourceDhisToFhirTransformerUtils) transformerUtils.get( fhirVersion ).get( AbstractFhirResourceDhisToFhirTransformerUtils.SCRIPT_ATTR_NAME );
        if ( utils == null )
        {
            throw new FatalTransformerException( "FHIR Resource transformer utils have not been provided for FHIR version " + fhirVersion );
        }
        return utils;
    }
}
