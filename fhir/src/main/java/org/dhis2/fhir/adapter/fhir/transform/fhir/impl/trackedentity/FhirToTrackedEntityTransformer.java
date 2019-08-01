package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.trackedentity;

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

import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.RequiredValueType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisDeleteTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.TrackedEntityInstanceNotFoundException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.AbstractFhirToDhisTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.lock.LockManager;
import org.dhis2.fhir.adapter.spring.StaticObjectProvider;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class FhirToTrackedEntityTransformer extends AbstractFhirToDhisTransformer<TrackedEntityInstance, TrackedEntityRule>
{
    private final LockManager lockManager;

    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final ScriptExecutionContext scriptExecutionContext;

    private final ValueConverter valueConverter;

    public FhirToTrackedEntityTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager,
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull OrganizationUnitService organizationUnitService, @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository,
        @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutor, organizationUnitService, new StaticObjectProvider<>( trackedEntityMetadataService ), new StaticObjectProvider<>( trackedEntityService ), fhirDhisAssignmentRepository, scriptExecutionContext, valueConverter );

        this.lockManager = lockManager;
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.scriptExecutionContext = scriptExecutionContext;
        this.valueConverter = valueConverter;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.ALL;
    }

    @Nonnull
    @Override
    public DhisResourceType getDhisResourceType()
    {
        return DhisResourceType.TRACKED_ENTITY;
    }

    @Nonnull
    @Override
    public Class<TrackedEntityInstance> getDhisResourceClass()
    {
        return TrackedEntityInstance.class;
    }

    @Nonnull
    @Override
    public Class<TrackedEntityRule> getRuleClass()
    {
        return TrackedEntityRule.class;
    }

    @Nullable
    @Override
    public FhirToDhisTransformOutcome<TrackedEntityInstance> transform( @Nonnull FhirClientResource fhirClientResource, @Nonnull FhirToDhisTransformerContext context, @Nonnull IBaseResource input,
        @Nonnull RuleInfo<TrackedEntityRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( !ruleInfo.getRule().getTrackedEntity().isEnabled() )
        {
            return null;
        }

        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        if ( !addScriptVariables( variables, ruleInfo ) )
        {
            return null;
        }

        final TrackedEntityAttributes trackedEntityAttributes = TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_ATTRIBUTES, TrackedEntityAttributes.class );
        final TrackedEntityType trackedEntityType = TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class );
        final TrackedEntityInstance trackedEntityInstance = getResource( fhirClientResource, context, ruleInfo, variables ).orElse( null );
        if ( trackedEntityInstance == null )
        {
            return null;
        }

        final WritableScriptedTrackedEntityInstance scriptedTrackedEntityInstance = new WritableScriptedTrackedEntityInstance(
            trackedEntityAttributes, trackedEntityType, trackedEntityInstance, scriptExecutionContext, valueConverter );
        variables.put( ScriptVariable.OUTPUT.getVariableName(), scriptedTrackedEntityInstance );

        final Optional<OrganizationUnit> organizationUnit;
        if ( ruleInfo.getRule().getOrgUnitLookupScript() == null )
        {
            if ( scriptedTrackedEntityInstance.getOrganizationUnitId() == null )
            {
                logger.info( "Rule does not define an organization unit lookup script and tracked entity instance does not yet include one." );
                return null;
            }
            organizationUnit = getOrgUnit( context,
                new Reference( scriptedTrackedEntityInstance.getOrganizationUnitId(), ReferenceType.ID ), variables );
        }
        else
        {
            organizationUnit = getOrgUnit( context, ruleInfo, ruleInfo.getRule().getOrgUnitLookupScript(), variables );
            organizationUnit.ifPresent( ou -> scriptedTrackedEntityInstance.setOrganizationUnitId( ou.getId() ) );
        }
        if ( !organizationUnit.isPresent() )
        {
            return null;
        }

        if ( ruleInfo.getRule().getLocationLookupScript() != null )
        {
            scriptedTrackedEntityInstance.setCoordinates( getCoordinate( context, ruleInfo, ruleInfo.getRule().getLocationLookupScript(), variables ) );
        }
        if ( !transform( context, ruleInfo, variables ) )
        {
            return null;
        }
        getTrackedEntityService().updateGeneratedValues( trackedEntityInstance, trackedEntityType,
            Collections.singletonMap( RequiredValueType.ORG_UNIT_CODE, organizationUnit.get().getCode() ) );
        scriptedTrackedEntityInstance.validate();

        return new FhirToDhisTransformOutcome<>( ruleInfo.getRule(), trackedEntityInstance, trackedEntityInstance.isNewResource() );
    }

    @Nullable
    @Override
    public FhirToDhisDeleteTransformOutcome<TrackedEntityInstance> transformDeletion( @Nonnull FhirClientResource fhirClientResource, @Nonnull RuleInfo<TrackedEntityRule> ruleInfo, @Nonnull DhisFhirResourceId dhisFhirResourceId ) throws TransformerException
    {
        return new FhirToDhisDeleteTransformOutcome<>(
            ruleInfo.getRule(), new TrackedEntityInstance( dhisFhirResourceId.getId() ), true );
    }

    protected boolean addScriptVariables( @Nonnull Map<String, Object> variables, @Nonnull RuleInfo<TrackedEntityRule> ruleInfo ) throws TransformerException
    {
        final TrackedEntityAttributes trackedEntityAttributes = trackedEntityMetadataService.getAttributes();
        variables.put( ScriptVariable.TRACKED_ENTITY_ATTRIBUTES.getVariableName(), trackedEntityAttributes );
        final TrackedEntityType trackedEntityType = trackedEntityMetadataService.findTypeByReference(
            ruleInfo.getRule().getTrackedEntity().getTrackedEntityReference() )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity type in rule " + ruleInfo + " could not be found: " +
                ruleInfo.getRule().getTrackedEntity().getTrackedEntityReference() ) );
        variables.put( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName(), trackedEntityType );

        // is applicable for further processing
        return true;
    }

    @Override
    protected boolean isSyncRequired( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<TrackedEntityRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return context.getFhirRequest().isSync();
    }

    @Override
    protected boolean isAlwaysActiveResource( @Nonnull RuleInfo<TrackedEntityRule> ruleInfo )
    {
        return false;
    }

    @Nonnull
    @Override
    protected Optional<TrackedEntityInstance> getResourceById( @Nullable String id ) throws TransformerException
    {
        return ( id == null ) ? Optional.empty() : getTrackedEntityService().findOneByIdRefreshed( id );
    }

    @Nonnull
    @Override
    protected Optional<TrackedEntityInstance> getActiveResource( @Nonnull FhirToDhisTransformerContext context,
        @Nonnull RuleInfo<TrackedEntityRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables, boolean sync, boolean refreshed ) throws TransformerException
    {
        final IBaseResource baseResource = getTeiResource( context, ruleInfo, scriptVariables );
        if ( baseResource == null )
        {
            return Optional.empty();
        }
        if ( sync )
        {
            lockManager.getCurrentLockContext().orElseThrow( () -> new FatalTransformerException( "No lock context available." ) )
                .lock( "in-te-fhir-resource-id:" + baseResource.getIdElement().toUnqualifiedVersionless() );
        }
        return getTrackedEntityInstanceByIdentifier( context, ruleInfo, baseResource, scriptVariables, sync );
    }

    @Nonnull
    @Override
    protected Optional<TrackedEntityInstance> findResourceById( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<TrackedEntityRule> ruleInfo, @Nonnull String id, @Nonnull Map<String, Object> scriptVariables )
    {
        return getTrackedEntityService().findOneByIdRefreshed( id );
    }

    @Nullable
    @Override
    protected TrackedEntityInstance createResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<TrackedEntityRule> ruleInfo,
        @Nullable String id, @Nonnull Map<String, Object> scriptVariables, boolean sync, boolean refreshed ) throws TransformerException
    {
        if ( context.isCreationDisabled() )
        {
            return null;
        }

        final IBaseResource resource;
        if ( ruleInfo.getRule().getTeiLookupScript() == null )
        {
            resource = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, IBaseResource.class );
        }
        else
        {
            resource = executeScript( context, ruleInfo, ruleInfo.getRule().getTeiLookupScript(), scriptVariables, IBaseResource.class );
            if ( resource == null )
            {
                return null;
            }
            throw new TrackedEntityInstanceNotFoundException( "Tracked entity for resource " + resource.getIdElement().toUnqualifiedVersionless() + " could not be found.", resource );
        }

        String identifier = getIdentifier( context, ruleInfo, scriptVariables );
        if ( identifier == null )
        {
            return null;
        }
        identifier = createFullQualifiedTrackedEntityInstanceIdentifier( context, resource, identifier );

        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance(
            TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class ), id, true );
        trackedEntityInstance.setIdentifier( identifier );

        final TrackedEntityAttributes trackedEntityAttributes = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_ATTRIBUTES, TrackedEntityAttributes.class );
        final String attributeId = trackedEntityAttributes.getOptional(
            ruleInfo.getRule().getTrackedEntity().getTrackedEntityIdentifierReference() ).orElseThrow( () ->
            new TransformerMappingException( "Tracked entity identifier attribute does not exist: " +
                ruleInfo.getRule().getTrackedEntity().getTrackedEntityIdentifierReference() ) ).getId();
        trackedEntityInstance.getAttribute( attributeId ).setValue( identifier );

        return trackedEntityInstance;
    }

    @Nullable
    protected String getIdentifier( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<TrackedEntityRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables )
    {
        final IBaseResource baseResource = getTeiResource( context, ruleInfo, scriptVariables );
        if ( baseResource == null )
        {
            return null;
        }
        return getIdentifier( context, baseResource, scriptVariables );
    }

    @Nullable
    private IBaseResource getTeiResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<TrackedEntityRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables )
    {
        final IBaseResource baseResource;
        if ( ruleInfo.getRule().getTeiLookupScript() == null )
        {
            baseResource = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, IBaseResource.class );
        }
        else
        {
            baseResource = executeScript( context, ruleInfo, ruleInfo.getRule().getTeiLookupScript(), scriptVariables, IBaseResource.class );
        }
        return baseResource;
    }
}
