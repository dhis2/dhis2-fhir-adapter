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
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
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

    private final LockManager lockManager;

    private final FhirClientResourceRepository fhirClientResourceRepository;

    private final FhirClientSystemRepository fhirClientSystemRepository;

    private final OrganizationUnitService organizationUnitService;

    private final RuleRepository ruleRepository;

    private final Map<DhisResourceType, DhisToFhirRequestResolver> requestResolvers = new HashMap<>();

    private final Map<FhirVersionedValue<DhisResourceType>, DhisToFhirTransformer<?, ?>> transformers = new HashMap<>();

    private final Map<FhirVersion, Map<String, DhisToFhirTransformerUtils>> transformerUtils = new HashMap<>();

    private final ScriptExecutor scriptExecutor;

    public DhisToFhirTransformerServiceImpl( @Nonnull LockManager lockManager,
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull FhirClientSystemRepository fhirClientSystemRepository,
        @Nonnull OrganizationUnitService organizationUnitService,
        @Nonnull RuleRepository ruleRepository,
        @Nonnull ObjectProvider<List<DhisToFhirRequestResolver>> requestResolvers,
        @Nonnull ObjectProvider<List<DhisToFhirTransformer<?, ?>>> transformersProvider,
        @Nonnull ObjectProvider<List<DhisToFhirTransformerUtils>> transformUtilsProvider,
        @Nonnull ScriptExecutor scriptExecutor )
    {
        this.lockManager = lockManager;
        this.fhirClientResourceRepository = fhirClientResourceRepository;
        this.fhirClientSystemRepository = fhirClientSystemRepository;
        this.organizationUnitService = organizationUnitService;
        this.ruleRepository = ruleRepository;
        this.scriptExecutor = scriptExecutor;

        requestResolvers.ifAvailable( resolvers ->
            resolvers.forEach( r -> this.requestResolvers.put( r.getDhisResourceType(), r ) ) );
        transformersProvider.ifAvailable( transformers ->
        {
            for ( final DhisToFhirTransformer<?, ?> transformer : transformers )
            {
                for ( final FhirVersion fhirVersion : transformer.getFhirVersions() )
                {
                    this.transformers.put( new FhirVersionedValue<>( fhirVersion, transformer.getDhisResourceType() ), transformer );
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
    public List<IBaseReference> resolveFhirReferences( @Nonnull FhirClient fhirClient, @Nonnull ScriptedDhisResource dhisResource, @Nullable Set<FhirResourceType> fhirResourceTypes, int max )
    {
        final WritableDhisRequest dhisRequest = new WritableDhisRequest( true, false, false );
        dhisRequest.setLastUpdated( dhisResource.getLastUpdated() );
        dhisRequest.setResourceType( dhisResource.getResourceType() );

        DhisToFhirTransformerRequest transformerRequest = createTransformerRequest(
            fhirClient, new ImmutableDhisRequest( dhisRequest ), dhisResource, fhirResourceTypes );
        if ( transformerRequest == null )
        {
            logger.debug( "No matching rule has been found for FHIR references." );
            return Collections.emptyList();
        }

        final AbstractFhirResourceDhisToFhirTransformerUtils fhirResourceTransformerUtils = getFhirResourceTransformerUtils( fhirClient.getFhirVersion() );
        final List<IBaseReference> references = new ArrayList<>();
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
        while ( (transformerRequest != null) && (references.size() < max) );
        return references;
    }

    @Nullable
    protected DhisToFhirTransformerRequest createTransformerRequest( @Nonnull FhirClient fhirClient, @Nonnull DhisRequest dhisRequest, @Nonnull ScriptedDhisResource scriptedDhisResource, @Nullable Set<FhirResourceType> fhirResourceTypes )
    {
        return createTransformerRequest( dhisRequest, rr -> scriptedDhisResource, ri -> (fhirResourceTypes == null) || fhirResourceTypes.isEmpty() || fhirResourceTypes.contains( ri.getRule().getFhirResourceType() ),
            ( si, rr ) -> fhirClient );
    }

    @Nullable
    @Override
    public DhisToFhirTransformerRequest createTransformerRequest( @Nonnull DhisRequest dhisRequest, @Nonnull DhisResource originalInput )
    {
        return createTransformerRequest( dhisRequest, rr -> rr.convert( Objects.requireNonNull( DhisBeanTransformerUtils.clone( originalInput ) ) ),
            ri -> true, ( si, rr ) -> rr.resolveFhirClient( si ).orElse( null ) );
    }

    @Nullable
    protected DhisToFhirTransformerRequest createTransformerRequest( @Nonnull DhisRequest dhisRequest, @Nonnull Function<DhisToFhirRequestResolver, ScriptedDhisResource> scriptedDhisResourceFunction,
        @Nonnull Predicate<RuleInfo<? extends AbstractRule>> ruleInfoPredicate, @Nonnull BiFunction<ScriptedDhisResource, DhisToFhirRequestResolver, FhirClient> fhirClientFunction )
    {
        final DhisToFhirRequestResolver requestResolver = requestResolvers.get( dhisRequest.getResourceType() );
        if ( requestResolver == null )
        {
            throw new TransformerMappingException( "No request resolver can be found for DHIS resource type " + dhisRequest.getResourceType() );
        }

        final ScriptedDhisResource scriptedInput = scriptedDhisResourceFunction.apply( requestResolver );
        final List<RuleInfo<? extends AbstractRule>> ruleInfos = requestResolver.resolveRules( scriptedInput )
            .stream().filter( ruleInfoPredicate ).collect( Collectors.toList() );
        if ( ruleInfos.isEmpty() )
        {
            logger.info( "Could not find any rule to process DHIS resource." );
            return null;
        }

        final FhirClient fhirClient = fhirClientFunction.apply( scriptedInput, requestResolver );
        if ( fhirClient == null )
        {
            logger.info( "Could not determine FHIR client to process DHIS resource." );
            return null;
        }
        return createTransformerRequest( false, fhirClient, dhisRequest, scriptedInput, ruleInfos );
    }

    @Nullable
    @Override
    public DhisToFhirTransformerRequest createTransformerRequest( @Nonnull FhirClient fhirClient, @Nonnull DhisRequest dhisRequest, @Nonnull DhisResource dhisResource,
        @Nonnull FhirResourceType fhirResourceType, @Nonnull UUID ruleId )
    {
        final DhisToFhirRequestResolver requestResolver = requestResolvers.get( dhisResource.getResourceType() );
        if ( requestResolver == null )
        {
            throw new TransformerMappingException( "No request resolver can be found for DHIS resource type " + dhisResource.getResourceType() );
        }
        final ScriptedDhisResource scriptedDhisResource = requestResolver.convert(
            Objects.requireNonNull( DhisBeanTransformerUtils.clone( dhisResource ) ) );

        final List<RuleInfo<? extends AbstractRule>> ruleInfos = requestResolver.resolveRules( scriptedDhisResource );
        final RuleInfo<? extends AbstractRule> matchingRuleInfo = ruleInfos.stream().filter( ri -> ri.getRule().getId().equals( ruleId ) ).findFirst().orElse( null );
        if ( (matchingRuleInfo == null) || !fhirResourceType.equals( matchingRuleInfo.getRule().getFhirResourceType() ) )
        {
            logger.info( "Could not find any matching grouping rule to process DHIS resource." );
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
            .map( s -> new ResourceSystem( s.getFhirResourceType(), s.getSystem().getSystemUri(), s.getCodePrefix(), s.getDefaultValue(), s.getSystem().getFhirDisplayName() ) )
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
    public DhisToFhirTransformerRequest updateTransformerRequest( @Nonnull DhisToFhirTransformerRequest transformerRequest, @Nonnull DhisResource dhisResource )
    {
        final DhisToFhirRequestResolver requestResolver = requestResolvers.get( dhisResource.getResourceType() );
        if ( requestResolver == null )
        {
            throw new TransformerMappingException( "No request resolver can be found for DHIS resource type " + dhisResource.getResourceType() );
        }
        final DhisResource input = Objects.requireNonNull( DhisBeanTransformerUtils.clone( dhisResource ) );
        final ScriptedDhisResource scriptedInput = requestResolver.convert( input );

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

    @Nullable
    @Override
    public DhisToFhirTransformOutcome<? extends IBaseResource> transform( @Nonnull DhisToFhirTransformerRequest transformerRequest ) throws TransformerException
    {
        final DhisToFhirTransformerRequestImpl transformerRequestImpl = (DhisToFhirTransformerRequestImpl) transformerRequest;

        final boolean firstRule = transformerRequestImpl.isFirstRule();
        RuleInfo<? extends AbstractRule> ruleInfo;
        while ( (ruleInfo = transformerRequestImpl.nextRule()) != null )
        {
            final DhisToFhirTransformer<?, ?> transformer = this.transformers.get(
                new FhirVersionedValue<>( transformerRequestImpl.getContext().getVersion(), ruleInfo.getRule().getDhisResourceType() ) );
            if ( transformer == null )
            {
                throw new TransformerMappingException( "No transformer can be found for FHIR version " +
                    transformerRequestImpl.getContext().getVersion() +
                    " mapping of DHIS resource type " + ruleInfo.getRule().getDhisResourceType() );
            }

            final Map<String, Object> scriptVariables = new HashMap<>( transformerRequestImpl.getTransformerUtils() );
            scriptVariables.put( ScriptVariable.CONTEXT.getVariableName(), transformerRequestImpl.getContext() );
            scriptVariables.put( ScriptVariable.INPUT.getVariableName(), transformerRequestImpl.getInput() );
            scriptVariables.put( ScriptVariable.ORGANIZATION_UNIT_ID.getVariableName(), transformerRequestImpl.getInput().getOrganizationUnitId() );
            scriptVariables.put( ScriptVariable.ORGANIZATION_UNIT.getVariableName(), getScriptedOrganizationUnit( transformerRequestImpl.getInput() ) );
            if ( isApplicable( transformerRequestImpl.getContext(), transformerRequestImpl.getInput(), ruleInfo, scriptVariables ) )
            {
                final DhisToFhirTransformOutcome<? extends IBaseResource> outcome = transformer.transformCasted( transformerRequest.getFhirClient(),
                    transformerRequestImpl.getContext(), transformerRequestImpl.getInput(), ruleInfo, scriptVariables );
                if ( outcome != null )
                {
                    logger.info( "Rule {} used successfully for transformation of {} (stop={}).",
                        ruleInfo, transformerRequestImpl.getInput().getResourceId(), ruleInfo.getRule().isStop() );
                    return new DhisToFhirTransformOutcome<>( outcome, transformerRequestImpl.isLastRule() ? null : transformerRequestImpl );
                }
                // if the previous transformation caused a lock of any resource this must be released since the transformation has been rolled back
                lockManager.getCurrentLockContext().ifPresent( LockContext::unlockAll );
            }
        }
        if ( firstRule )
        {
            logger.info( "No matching rule for {}.", transformerRequestImpl.getInput().getResourceId() );
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
        final OrganizationUnit organizationUnit = organizationUnitService.findOneByReference( new Reference( dhisResource.getOrganizationUnitId(), ReferenceType.ID ) )
            .orElseThrow( () -> new TransformerDataException( "DHIS resource " + dhisResource.getResourceId() + " reference organization unit " + dhisResource.getOrganizationUnitId() + " that cannot be found." ) );
        return new ImmutableScriptedOrganizationUnit( new WritableScriptedOrganizationUnit( organizationUnit ) );
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
