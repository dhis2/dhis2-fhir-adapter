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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.primitive.IdDt;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemRepository;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionException;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.dhis.DhisToFhirTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractAssignmentDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractFhirResourceDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractIdentifierDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedDhisResource;
import org.dhis2.fhir.adapter.fhir.transform.scripted.ScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.util.FhirBeanTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Transforms a DHIS2 resource to a FHIR resource. The default priority of the transformer is <code>0</code>.
 *
 * @param <R> the concrete type of the DHIS2 resource that is processed by this transformer.
 * @param <U> the concrete type of the transformer rule that is processed by this transformer.
 * @author volsch
 */
public abstract class AbstractDhisToFhirTransformer<R extends ScriptedDhisResource, U extends AbstractRule>
    implements DhisToFhirTransformer<R, U>
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final ScriptExecutor scriptExecutor;

    private final LockManager lockManager;

    private final SystemRepository systemRepository;

    private final FhirResourceRepository fhirResourceRepository;

    private final FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    public AbstractDhisToFhirTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager, @Nonnull SystemRepository systemRepository,
        @Nonnull FhirResourceRepository fhirResourceRepository, @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository )
    {
        this.scriptExecutor = scriptExecutor;
        this.lockManager = lockManager;
        this.systemRepository = systemRepository;
        this.fhirResourceRepository = fhirResourceRepository;
        this.fhirDhisAssignmentRepository = fhirDhisAssignmentRepository;
    }

    @Override
    public int getPriority()
    {
        return 0;
    }

    @Override
    public int compareTo( @Nonnull DhisToFhirTransformer<?, ?> o )
    {
        final int value = getPriority() - o.getPriority();

        if ( value != 0 )
        {
            return value;
        }

        return getClass().getSimpleName().compareTo( o.getClass().getSimpleName() );
    }

    @Nonnull
    protected LockManager getLockManager()
    {
        return lockManager;
    }

    @Nonnull
    protected FhirResourceRepository getFhirResourceRepository()
    {
        return fhirResourceRepository;
    }

    @Nullable
    @Override
    public DhisToFhirTransformOutcome<? extends IBaseResource> transformCasted( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull ScriptedDhisResource input,
        @Nonnull RuleInfo<? extends AbstractRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return transform( fhirClient, context, getDhisResourceClass().cast( input ),
            new RuleInfo<>( getRuleClass().cast( ruleInfo.getRule() ), ruleInfo.getDhisDataReferences() ), scriptVariables );
    }

    @Nonnull
    protected Optional<? extends IBaseResource> getResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return getResource( fhirClient, context, ruleInfo, scriptVariables, false );
    }

    @Nonnull
    protected Optional<? extends IBaseResource> getExistingResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( context.getDhisRequest().isDhisFhirId() )
        {
            return Optional.empty();
        }

        if ( context.isUseAdapterIdentifier() )
        {
            final IBaseResource resource = getResourceByAdapterIdentifier( fhirClient, context, ruleInfo, scriptVariables ).orElse( null );

            if ( resource != null )
            {
                if ( !ruleInfo.getRule().isEffectiveFhirUpdateEnable() )
                {
                    logger.info( "Existing FHIR resource could be found by adapter identifier, but rule {} does not allow updating FHIR resources.", ruleInfo );

                    return Optional.empty();
                }

                return Optional.of( resource );
            }
        }

        IBaseResource resource = getResourceBySystemIdentifier( fhirClient, context, ruleInfo, scriptVariables ).orElse( null );

        if ( resource != null )
        {
            if ( !ruleInfo.getRule().isEffectiveFhirUpdateEnable() )
            {
                logger.info( "Existing FHIR resource could be found by system identifier, but rule {} does not allow updating FHIR resources.", ruleInfo );

                return Optional.empty();
            }

            return Optional.of( resource );
        }

        resource = getResourceByAssignment( fhirClient, context, ruleInfo, scriptVariables ).orElse( null );

        if ( resource != null )
        {
            if ( !ruleInfo.getRule().isEffectiveFhirUpdateEnable() )
            {
                logger.info( "Existing FHIR resource could be found by assigned identifier, but rule {} does not allow updating FHIR resources.", ruleInfo );

                return Optional.empty();
            }

            return Optional.of( resource );
        }

        resource = getActiveResource( fhirClient, context, ruleInfo, scriptVariables ).orElse( null );

        if ( resource != null )
        {
            if ( !ruleInfo.getRule().isEffectiveFhirUpdateEnable() )
            {
                logger.info( "Existing active FHIR resource could be found, but rule {} does not allow updating FHIR resources.", ruleInfo );

                return Optional.empty();
            }

            return Optional.of( resource );
        }

        return Optional.empty();
    }

    @Nonnull
    protected Optional<? extends IBaseResource> getResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables, boolean sync ) throws TransformerException
    {
        if ( context.getDhisRequest().isDhisFhirId() )
        {
            final IBaseResource baseResource = createResource( fhirClient, context, ruleInfo, scriptVariables, false );

            if ( baseResource == null )
            {
                return Optional.empty();
            }

            final ScriptedDhisResource scriptedDhisResource = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, ScriptedDhisResource.class );
            baseResource.setId( new IdDt( ruleInfo.getRule().getFhirResourceType().getResourceTypeName(), createDhisFhirResourceId( ruleInfo, scriptedDhisResource ) ) );

            if ( context.getDhisRequest().getLastUpdated() != null )
            {
                baseResource.getMeta().setLastUpdated( Date.from( context.getDhisRequest().getLastUpdated().toInstant() ) );
            }

            // FHIR resource will be filled with read data
            return Optional.of( baseResource );
        }

        IBaseResource resource = getExistingResource( fhirClient, context, ruleInfo, scriptVariables ).orElse( null );

        if ( resource != null )
        {
            return Optional.of( resource );
        }

        if ( ruleInfo.getRule().isEffectiveFhirCreateEnable() )
        {
            resource = createResource( fhirClient, context, ruleInfo, scriptVariables, sync );

            if ( (resource != null) && !sync )
            {
                lockResource( fhirClient, context, ruleInfo, scriptVariables );
                resource = getResource( fhirClient, context, ruleInfo, scriptVariables, true ).orElse( null );
            }
        }

        return Optional.ofNullable( resource );
    }

    @Nonnull
    protected String createDhisFhirResourceId( @Nonnull RuleInfo<? extends AbstractRule> ruleInfo, @Nonnull ScriptedDhisResource dhisResource )
    {
        if ( ruleInfo.getRule().isSimpleFhirId() )
        {
            return DhisFhirResourceId.toString( null, Objects.requireNonNull( dhisResource.getId() ), null );
        }
        else
        {
            return DhisFhirResourceId.toString( dhisResource.getResourceType(), Objects.requireNonNull( dhisResource.getId() ), ruleInfo.getRule().getId() );
        }
    }

    @Nonnull
    protected Optional<? extends IBaseResource> getResourceByAdapterIdentifier(
        @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final R scriptedDhisResource = getDhisResourceClass().cast( TransformerUtils.getScriptVariable(
            scriptVariables, ScriptVariable.INPUT, ScriptedDhisResource.class ) );

        return getResourceByAdapterIdentifierInternal( fhirClient, context, ruleInfo, scriptedDhisResource, scriptVariables );
    }

    @Nonnull
    private <UX extends AbstractRule, RX extends ScriptedDhisResource> Optional<? extends IBaseResource> getResourceByAdapterIdentifierInternal(
        @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull RuleInfo<UX> ruleInfo, @Nonnull RX scriptedDhisResource, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( !context.isUseAdapterIdentifier() )
        {
            return Optional.empty();
        }

        final SystemCodeValue identifier = new SystemCodeValue( getAdapterIdentifierSystem().getSystemUri(), createAdapterIdentifierValue( ruleInfo, scriptedDhisResource ) );

        return getFhirResourceRepository().findRefreshedByIdentifier( fhirClient.getId(), fhirClient.getFhirVersion(), fhirClient.getFhirEndpoint(), ruleInfo.getRule().getFhirResourceType().getResourceTypeName(), identifier )
            .map( r -> clone( context, r ) );
    }

    @Nonnull
    protected Optional<? extends IBaseResource> getResourceBySystemIdentifier( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final R scriptedDhisResource = getDhisResourceClass().cast(
            TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, ScriptedDhisResource.class ) );

        return getResourceBySystemIdentifierInternal( fhirClient, context, ruleInfo,
            scriptedDhisResource, scriptVariables, new DefaultIdentifierValueProvider() );
    }

    @Nonnull
    protected Optional<? extends IBaseResource> getResourceByAssignment( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final R scriptedDhisResource = getDhisResourceClass().cast(
            TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, ScriptedDhisResource.class ) );
        final String fhirResourceId = fhirDhisAssignmentRepository.findFirstFhirResourceId( ruleInfo.getRule(), fhirClient,
            Objects.requireNonNull( scriptedDhisResource.getResourceId() ) );

        if ( fhirResourceId == null )
        {
            return Optional.empty();
        }

        return getFhirResourceRepository().findRefreshed( fhirClient.getId(), fhirClient.getFhirVersion(), fhirClient.getFhirEndpoint(), ruleInfo.getRule().getFhirResourceType().getResourceTypeName(), fhirResourceId )
            .map( r -> clone( context, r ) );
    }

    @Nonnull
    private <UX extends AbstractRule, RX extends ScriptedDhisResource> Optional<? extends IBaseResource> getResourceBySystemIdentifierInternal(
        @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<UX> ruleInfo,
        @Nonnull RX scriptedDhisResource, @Nonnull Map<String, Object> scriptVariables, @Nonnull IdentifierValueProvider<UX, RX> identifierValueProvider ) throws TransformerException
    {
        final ResourceSystem resourceSystem = context.getResourceSystem( ruleInfo.getRule().getFhirResourceType() );

        if ( resourceSystem == null )
        {
            return Optional.empty();
        }

        final String identifierValue = identifierValueProvider.getIdentifierValue( context, ruleInfo, null, scriptedDhisResource, scriptVariables );

        if ( identifierValue == null )
        {
            logger.info( "FHIR resource type {} defines resource system, but resource does not include an identifier.", ruleInfo.getRule().getFhirResourceType() );
            return Optional.empty();
        }

        if ( StringUtils.isNotBlank( resourceSystem.getCodePrefix() ) && (!identifierValue.startsWith( resourceSystem.getCodePrefix() ) || identifierValue.equals( resourceSystem.getCodePrefix() )) )
        {
            logger.info( "Resource identifier \"{}\" does not start with required prefix \"{}\" for resource type {}.",
                identifierValue, resourceSystem.getCodePrefix(), ruleInfo.getRule().getFhirResourceType() );
            return Optional.empty();
        }

        final SystemCodeValue identifier = new SystemCodeValue( resourceSystem.getSystem(), identifierValue.substring( StringUtils.length( resourceSystem.getCodePrefix() ) ) );

        return getFhirResourceRepository().findRefreshedByIdentifier( fhirClient.getId(), fhirClient.getFhirVersion(), fhirClient.getFhirEndpoint(), ruleInfo.getRule().getFhirResourceType().getResourceTypeName(), identifier )
            .map( r -> clone( context, r ) );
    }

    @Nonnull
    protected abstract Optional<? extends IBaseResource> getActiveResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> ruleInfo,
        @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;

    @Nullable
    protected IBaseResource createResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables, boolean sync ) throws TransformerException
    {
        final AbstractFhirResourceDhisToFhirTransformerUtils fhirResourceUtils =
            TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.FHIR_RESOURCE_UTILS, AbstractFhirResourceDhisToFhirTransformerUtils.class );
        final IBaseResource resource = fhirResourceUtils.createResource( ruleInfo.getRule().getFhirResourceType() );

        if ( !updateIdentifiers( context, ruleInfo, resource, scriptVariables, sync ) )
        {
            return null;
        }

        return resource;
    }

    protected boolean updateIdentifiers( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, IBaseResource resource, @Nonnull Map<String, Object> scriptVariables, boolean sync )
    {
        final R scriptedDhisResource =
            getDhisResourceClass().cast( TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, ScriptedDhisResource.class ) );
        final AbstractIdentifierDhisToFhirTransformerUtils identifierUtils =
            TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.IDENTIFIER_UTILS, AbstractIdentifierDhisToFhirTransformerUtils.class );
        final ResourceSystem resourceSystem = context.getResourceSystem( ruleInfo.getRule().getFhirResourceType() );

        if ( resourceSystem != null )
        {
            final String identifierValue = getIdentifierValue( context, ruleInfo, null, scriptedDhisResource, scriptVariables );

            if ( identifierValue == null )
            {
                logger.debug( "FHIR resource type {} defines resource system, but tracked entity does not include an identifier.", ruleInfo.getRule().getFhirResourceType() );
            }
            else
            {
                if ( StringUtils.isNotBlank( resourceSystem.getCodePrefix() ) && (!identifierValue.startsWith( resourceSystem.getCodePrefix() ) || identifierValue.equals( resourceSystem.getCodePrefix() )) )
                {
                    logger.info( "Tracked entity identifier \"{}\" does not start with required prefix \"{}\" for resource type {}.",
                        identifierValue, resourceSystem.getCodePrefix(), ruleInfo.getRule().getFhirResourceType() );

                    return false;
                }

                final SystemCodeValue identifier = new SystemCodeValue( resourceSystem.getSystem(), identifierValue.substring( StringUtils.length( resourceSystem.getCodePrefix() ) ) );

                if ( sync )
                {
                    lockFhirIdentifier( context, identifier );
                }

                identifierUtils.addOrUpdateIdentifier( resource, identifier, resourceSystem.getFhirDisplayName() );
            }
        }

        if ( context.isUseAdapterIdentifier() )
        {
            final System adapterIdentifierSystem = getAdapterIdentifierSystem();
            final SystemCodeValue identifier = new SystemCodeValue( adapterIdentifierSystem.getSystemUri(), createAdapterIdentifierValue( ruleInfo, scriptedDhisResource ) );

            if ( sync )
            {
                lockFhirIdentifier( context, identifier );
            }

            identifierUtils.addOrUpdateIdentifier( resource, identifier, adapterIdentifierSystem.getFhirDisplayName(), true );
        }

        return true;
    }

    protected abstract void lockResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> rule,
        @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;

    @Nullable
    protected abstract String getIdentifierValue( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> ruleInfo,
        @Nullable ExecutableScript identifierLookupScript, @Nonnull R scriptedDhisResource, @Nonnull Map<String, Object> scriptVariables );

    protected void lockFhirIdentifier( @Nonnull DhisToFhirTransformerContext context, @Nonnull SystemCodeValue systemCodeValue )
    {
        if ( !context.getDhisRequest().isDhisFhirId() )
        {
            getLockManager().getCurrentLockContext().orElseThrow( () -> new FatalTransformerException( "No lock context available." ) )
                .lock( "fhir-identifier:" + systemCodeValue.toString() );
        }
    }

    protected boolean transformFhirResourceType( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> variables, @Nonnull FhirResourceMapping resourceMapping,
        @Nonnull ScriptedDhisResource input, @Nonnull IBaseResource output )
    {
        if ( context.getDhisRequest().isCompleteTransformation() )
        {
            if ( ( resourceMapping.getExpStatusTransformScript() != null ) &&
                !Boolean.TRUE.equals( executeScript( context, ruleInfo, resourceMapping.getExpStatusTransformScript(), variables, Boolean.class ) ) )
            {
                logger.info( "Resulting DHIS status could not be transformed into FHIR resource {} with type {}.",
                    output.getIdElement().toUnqualifiedVersionless(), resourceMapping.getFhirResourceType() );

                return false;
            }

            if ( ( resourceMapping.getExpTeiTransformScript() != null ) &&
                !Boolean.TRUE.equals( executeScript( context, ruleInfo, resourceMapping.getExpTeiTransformScript(), variables, Boolean.class ) ) )
            {
                logger.info( "Resulting DHIS TEI could not be transformed into FHIR resource {}.",
                    resourceMapping.getFhirResourceType() );

                return false;
            }

            if ( ( resourceMapping.getExpOrgUnitTransformScript() != null ) &&
                !Boolean.TRUE.equals( executeScript( context, ruleInfo, resourceMapping.getExpOrgUnitTransformScript(), variables, Boolean.class ) ) )
            {
                logger.info( "DHIS Organization Unit {} could not be transformed into FHIR resource {}.",
                    input.getOrganizationUnitId(), resourceMapping.getFhirResourceType() );

                return false;
            }

            if ( ( resourceMapping.getExpGeoTransformScript() != null ) &&
                !Boolean.TRUE.equals( executeScript( context, ruleInfo, resourceMapping.getExpGeoTransformScript(), variables, Boolean.class ) ) )
            {
                return false;
            }

            if ( ( resourceMapping.getExpDateTransformScript() != null ) &&
                !Boolean.TRUE.equals( executeScript( context, ruleInfo, resourceMapping.getExpDateTransformScript(), variables, Boolean.class ) ) )
            {
                logger.info( "Date could not be transformed into FHIR resource {}.",
                    resourceMapping.getFhirResourceType() );
                return false;
            }
        }

        return true;
    }

    protected Optional<? extends IBaseResource> getTrackedEntityFhirResource( @Nonnull FhirClient fhirClient, @Nonnull DhisToFhirTransformerContext context,
        @Nonnull RuleInfo<TrackedEntityRule> ruleInfo, @Nonnull FhirResourceType trackedEntityResourceType,
        @Nonnull ScriptedTrackedEntityInstance scriptedTrackedEntityInstance, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        IBaseResource resource;

        if ( context.getDhisRequest().isDhisFhirId() )
        {
            final IBaseReference reference = context.getDhisFhirResourceReference( scriptedTrackedEntityInstance, trackedEntityResourceType );

            if ( reference == null )
            {
                return Optional.empty();
            }

            final AbstractFhirResourceDhisToFhirTransformerUtils fhirResourceUtils =
                TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.FHIR_RESOURCE_UTILS, AbstractFhirResourceDhisToFhirTransformerUtils.class );
            resource = fhirResourceUtils.createResource( reference.getReferenceElement().getResourceType() );
            resource.setId( reference.getReferenceElement() );
        }
        else
        {
            resource = getResourceBySystemIdentifierInternal( fhirClient, context, ruleInfo, scriptedTrackedEntityInstance, scriptVariables, new TrackedEntityIdentifierValueProvider() ).orElse( null );

            if ( resource != null )
            {
                return Optional.of( resource );
            }
            if ( context.isUseAdapterIdentifier() )
            {
                resource = getResourceByAdapterIdentifierInternal( fhirClient, context, ruleInfo, scriptedTrackedEntityInstance, scriptVariables ).orElse( null );
            }
        }

        return Optional.ofNullable( resource );
    }

    @Nonnull
    protected System getAdapterIdentifierSystem()
    {
        return systemRepository.findAllByCode( System.DHIS2_FHIR_IDENTIFIER_CODE )
            .orElseThrow( () -> new FatalTransformerException( "Adapter identifier system has not been defined." ) );
    }

    @Nonnull
    protected FhirContext getFhirContext( @Nonnull DhisToFhirTransformerContext context )
    {
        return fhirResourceRepository.findFhirContext( context.getVersion() )
            .orElseThrow( () -> new FatalTransformerException( "No FHIR context for FHIR version " + context.getVersion() ) );
    }

    @Nullable
    protected <T extends IBaseResource> T cloneToModified( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, T resource, @Nonnull Map<String, Object> scriptVariables )
    {
        if ( context.getDhisRequest().isDhisFhirId() )
        {
            return resource;
        }

        final T modifiedResource = clone( context, resource );

        if ( !updateIdentifiers( context, ruleInfo, modifiedResource, scriptVariables, false ) )
        {
            return null;
        }

        return modifiedResource;
    }

    @Nonnull
    protected <T extends IBaseResource> T clone( @Nonnull DhisToFhirTransformerContext context, @Nonnull T resource )
    {
        return Objects.requireNonNull( FhirBeanTransformerUtils.clone( getFhirContext( context ), resource ) );
    }

    protected boolean evaluateNotModified( @Nonnull DhisToFhirTransformerContext context, @Nonnull Map<String, Object> scriptVariables, @Nonnull IBase base1, @Nonnull IBase base2 )
    {
        if ( base1 == base2 )
        {
            // It cannot be detected if resource has been changed.
            // Since this happens on a raw resource that is filled with data,
            // the resource has been changed in 100 percentage of the cases.
            return false;
        }

        final AbstractFhirResourceDhisToFhirTransformerUtils fhirResourceUtils =
            TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.FHIR_RESOURCE_UTILS, AbstractFhirResourceDhisToFhirTransformerUtils.class );

        return fhirResourceUtils.equalsDeep( base1, base2 );
    }

    @Nonnull
    protected String createAdapterIdentifierValue( @Nonnull RuleInfo<? extends AbstractRule> ruleInfo, @Nonnull ScriptedDhisResource dhisResource )
    {
        return createDhisFhirResourceId( ruleInfo, dhisResource );
    }

    @Nullable
    protected String getTrackedEntityIdentifierValue( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<TrackedEntityRule> ruleInfo, @Nonnull ScriptedTrackedEntityInstance scriptedTrackedEntityInstance,
        @Nonnull Map<String, Object> scriptVariables )
    {
        return scriptedTrackedEntityInstance.getStringValue( ruleInfo.getRule().getTrackedEntity().getTrackedEntityIdentifierReference() );
    }

    protected boolean transform( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( ruleInfo.getRule().getTransformExpScript() == null )
        {
            return true;
        }

        return Boolean.TRUE.equals( executeScript( context, ruleInfo, ruleInfo.getRule().getTransformExpScript(), scriptVariables, Boolean.class ) );
    }

    @Nonnull
    protected <T extends IBaseReference> List<T> createAssignedFhirReferences( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull DhisResourceType dhisResourceType, @Nullable String dhisId, @Nonnull FhirResourceType fhirResourceType )
    {
        @SuppressWarnings( "unchecked" ) final T reference = (T) createAssignedFhirReference( context, ruleInfo, scriptVariables, dhisResourceType, dhisId, fhirResourceType );

        return reference == null ? new ArrayList<>() : new ArrayList<>( Collections.singletonList( reference ) );
    }

    @Nullable
    protected IBaseReference createAssignedFhirReference( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables,
        @Nonnull DhisResourceType dhisResourceType, @Nullable String dhisId, @Nonnull FhirResourceType fhirResourceType )
    {
        final AbstractAssignmentDhisToFhirTransformerUtils assignmentTransformerUtils = TransformerUtils.getScriptVariable(
            scriptVariables, ScriptVariable.ASSIGNMENT_UTILS, AbstractAssignmentDhisToFhirTransformerUtils.class );

        return assignmentTransformerUtils.getMappedFhirId( context, ruleInfo.getRule(), dhisResourceType, dhisId, fhirResourceType );
    }

    /**
     * Executes an executable script with the specified variables . If the mandatory data for executing
     * the script has not been provided, the script will not be executed at all.
     *
     * @param context          the transformer context of the transformation.
     * @param ruleInfo         the rule of the transformation.
     * @param executableScript the script that should be executed.
     * @param variables        the variables that the script requires.
     * @param resultClass      the type of the result the script returns.
     * @param <T>              the concrete class of the return type.
     * @return the result of the script or <code>null</code> if specified executable script is <code>null</code>.
     * @throws ScriptExecutionException thrown if the
     */
    @Nullable
    protected <T> T executeScript( @Nonnull TransformerContext context, @Nonnull RuleInfo<? extends AbstractRule> ruleInfo, @Nullable ExecutableScript executableScript, @Nonnull Map<String, Object> variables, @Nonnull Class<T> resultClass )
    {
        return TransformerUtils.executeScript( scriptExecutor, context, ruleInfo, executableScript, variables, resultClass );
    }

    public class DefaultIdentifierValueProvider implements IdentifierValueProvider<U, R>
    {
        @Nullable
        @Override
        public String getIdentifierValue( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nullable ExecutableScript identifierLookupScript, @Nonnull R scriptedDhisResource, @Nonnull Map<String, Object> scriptVariables )
        {
            return AbstractDhisToFhirTransformer.this.getIdentifierValue( context, ruleInfo, identifierLookupScript, scriptedDhisResource, scriptVariables );
        }
    }

    private class TrackedEntityIdentifierValueProvider implements IdentifierValueProvider<TrackedEntityRule, ScriptedTrackedEntityInstance>
    {
        @Nullable
        @Override
        public String getIdentifierValue( @Nonnull DhisToFhirTransformerContext context, @Nonnull RuleInfo<TrackedEntityRule> ruleInfo, @Nullable ExecutableScript identifierLookupScript, @Nonnull ScriptedTrackedEntityInstance scriptedDhisResource,
            @Nonnull Map<String, Object> scriptVariables )
        {
            if ( identifierLookupScript != null )
            {
                throw new FatalTransformerException( "Identifier value of a tracked entity instance cannot be looked up with an alternative lookup script." );
            }

            return AbstractDhisToFhirTransformer.this.getTrackedEntityIdentifierValue( context, ruleInfo, scriptedDhisResource, scriptVariables );
        }
    }
}
