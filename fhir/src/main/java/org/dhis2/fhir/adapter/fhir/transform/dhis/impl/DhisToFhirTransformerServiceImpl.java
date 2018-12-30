package org.dhis2.fhir.adapter.fhir.transform.dhis.impl;

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

import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.AvailableFhirServerResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServer;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerSystem;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirServerResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirServerSystemRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.model.FhirVersionedValue;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerRequest;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerService;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.DhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.dhis.model.DhisRequest;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ImmutableScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisResource;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedOrganizationUnit;
import org.dhis2.fhir.adapter.fhir.transform.util.DhisBeanTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockContext;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of {@link DhisToFhirTransformerService}.
 *
 * @author volsch
 */
@Service
public class DhisToFhirTransformerServiceImpl implements DhisToFhirTransformerService
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final LockManager lockManager;

    private final FhirServerResourceRepository fhirServerResourceRepository;

    private final FhirServerSystemRepository fhirServerSystemRepository;

    private final OrganizationUnitService organizationUnitService;

    private final Map<DhisResourceType, DhisToFhirRequestResolver> requestResolvers = new HashMap<>();

    private final Map<FhirVersionedValue<DhisResourceType>, DhisToFhirTransformer<?, ?>> transformers = new HashMap<>();

    private final Map<FhirVersion, Map<String, DhisToFhirTransformerUtils>> transformerUtils = new HashMap<>();

    private final ScriptExecutor scriptExecutor;

    public DhisToFhirTransformerServiceImpl( @Nonnull LockManager lockManager,
        @Nonnull FhirServerResourceRepository fhirServerResourceRepository,
        @Nonnull FhirServerSystemRepository fhirServerSystemRepository,
        @Nonnull OrganizationUnitService organizationUnitService,
        @Nonnull ObjectProvider<List<DhisToFhirRequestResolver>> requestResolvers,
        @Nonnull ObjectProvider<List<DhisToFhirTransformer<?, ?>>> transformersProvider,
        @Nonnull ObjectProvider<List<DhisToFhirTransformerUtils>> transformUtilsProvider,
        @Nonnull ScriptExecutor scriptExecutor )
    {
        this.lockManager = lockManager;
        this.fhirServerResourceRepository = fhirServerResourceRepository;
        this.fhirServerSystemRepository = fhirServerSystemRepository;
        this.organizationUnitService = organizationUnitService;
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

    @Nullable
    @Override
    public DhisToFhirTransformerRequest createTransformerRequest( @Nonnull DhisRequest dhisRequest, @Nonnull DhisResource originalInput )
    {
        final DhisResource input = Objects.requireNonNull( DhisBeanTransformerUtils.clone( originalInput ) );

        final DhisToFhirRequestResolver requestResolver = requestResolvers.get( dhisRequest.getResourceType() );
        if ( requestResolver == null )
        {
            throw new TransformerMappingException( "No request resolver can be found for DHIS resource type " + dhisRequest.getResourceType() );
        }

        final ScriptedDhisResource scriptedInput = requestResolver.convert( input );
        List<RuleInfo<? extends AbstractRule>> ruleInfos = requestResolver.resolveRules( scriptedInput );
        if ( ruleInfos.isEmpty() )
        {
            logger.info( "Could not find any rule to process DHIS resource." );
            return null;
        }

        final FhirServer fhirServer = requestResolver.resolveFhirServer( scriptedInput ).orElse( null );
        if ( fhirServer == null )
        {
            logger.info( "Could not determine FHIR server to process DHIS resource." );
            return null;
        }

        final Collection<AvailableFhirServerResource> availableResources = fhirServerResourceRepository.findAllAvailable( fhirServer );
        ruleInfos = filterAvailableResourceRules( ruleInfos, availableResources );

        final Collection<FhirServerSystem> systems = fhirServerSystemRepository.findByFhirServer( fhirServer );
        final Map<FhirResourceType, ResourceSystem> resourceSystemsByType = systems.stream()
            .map( s -> new ResourceSystem( s.getFhirResourceType(), s.getSystem().getSystemUri(), s.getCodePrefix(), s.getSystem().getFhirDisplayName() ) )
            .collect( Collectors.toMap( ResourceSystem::getFhirResourceType, rs -> rs ) );

        final Map<String, DhisToFhirTransformerUtils> transformerUtils = this.transformerUtils.get( fhirServer.getFhirVersion() );
        if ( transformerUtils == null )
        {
            throw new TransformerMappingException( "No transformer utils can be found for FHIR version " + fhirServer.getFhirVersion() );
        }

        return new DhisToFhirTransformerRequestImpl(
            new DhisToFhirTransformerContextImpl( dhisRequest, fhirServer, resourceSystemsByType, availableResources ),
            scriptedInput, fhirServer, ruleInfos, transformerUtils );
    }

    @Nonnull
    private List<RuleInfo<? extends AbstractRule>> filterAvailableResourceRules( @Nonnull List<RuleInfo<? extends AbstractRule>> ruleInfos, @Nonnull Collection<AvailableFhirServerResource> availableResources )
    {
        final Map<FhirResourceType, AvailableFhirServerResource> availableResourcesByType = availableResources.stream()
            .collect( Collectors.toMap( AvailableFhirServerResource::getResourceType, a -> a ) );
        // not all resources may be available on FHIR server
        return ruleInfos.stream().filter( r -> {
            final AvailableFhirServerResource availableResource = availableResourcesByType.get( r.getRule().getFhirResourceType() );
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
                final DhisToFhirTransformOutcome<? extends IBaseResource> outcome = transformer.transformCasted( transformerRequest.getFhirServer(),
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
}
