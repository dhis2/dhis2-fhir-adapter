package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.trackedentity;

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

import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResourceType;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganisationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganisationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.RequiredValueType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.TrackedEntityInstanceNotFoundException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.AbstractFhirToDhisTransformer;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedTrackedEntityInstance;
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

@Component
public class FhirToTrackedEntityTransformer extends AbstractFhirToDhisTransformer<TrackedEntityInstance, TrackedEntityRule>
{
    private final LockManager lockManager;

    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final ValueConverter valueConverter;

    public FhirToTrackedEntityTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull LockManager lockManager,
        @Nonnull TrackedEntityMetadataService trackedEntityMetadataService, @Nonnull TrackedEntityService trackedEntityService,
        @Nonnull OrganisationUnitService organisationUnitService, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutor, organisationUnitService, new StaticObjectProvider<>( trackedEntityService ) );
        this.lockManager = lockManager;
        this.trackedEntityMetadataService = trackedEntityMetadataService;
        this.valueConverter = valueConverter;
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
    public FhirToDhisTransformOutcome<TrackedEntityInstance> transform( @Nonnull FhirToDhisTransformerContext context, @Nonnull IBaseResource input,
        @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( !rule.getTrackedEntity().isEnabled() )
        {
            return null;
        }

        final Map<String, Object> variables = new HashMap<>( scriptVariables );
        if ( !addScriptVariables( variables, rule ) )
        {
            return null;
        }

        final TrackedEntityAttributes trackedEntityAttributes = getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_ATTRIBUTES, TrackedEntityAttributes.class );
        final TrackedEntityType trackedEntityType = getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class );
        final TrackedEntityInstance trackedEntityInstance = getResource( context, rule, variables ).orElse( null );
        if ( trackedEntityInstance == null )
        {
            return null;
        }

        final WritableScriptedTrackedEntityInstance scriptedTrackedEntityInstance = new WritableScriptedTrackedEntityInstance(
            context, trackedEntityAttributes, trackedEntityType, trackedEntityInstance, valueConverter );
        variables.put( ScriptVariable.OUTPUT.getVariableName(), scriptedTrackedEntityInstance );

        final Optional<OrganisationUnit> organisationUnit;
        if ( rule.getOrgUnitLookupScript() == null )
        {
            if ( scriptedTrackedEntityInstance.getOrganizationUnitId() == null )
            {
                logger.info( "Rule does not define an organization unit lookup script and tracked entity instance does not yet include one." );
                return null;
            }
            organisationUnit = getOrgUnit( context,
                new Reference( scriptedTrackedEntityInstance.getOrganizationUnitId(), ReferenceType.ID ), variables );
        }
        else
        {
            organisationUnit = getOrgUnit( context, rule.getOrgUnitLookupScript(), variables );
            organisationUnit.ifPresent( ou -> scriptedTrackedEntityInstance.setOrganizationUnitId( ou.getId() ) );
        }
        if ( !organisationUnit.isPresent() )
        {
            return null;
        }

        if ( rule.getLocationLookupScript() != null )
        {
            scriptedTrackedEntityInstance.setCoordinates( getCoordinate( context, rule.getLocationLookupScript(), variables ) );
        }
        if ( !transform( context, rule, variables ) )
        {
            return null;
        }
        getTrackedEntityService().updateGeneratedValues( trackedEntityInstance, trackedEntityType,
            Collections.singletonMap( RequiredValueType.ORG_UNIT_CODE, organisationUnit.get().getCode() ) );
        scriptedTrackedEntityInstance.validate();

        return new FhirToDhisTransformOutcome<>( trackedEntityInstance );
    }

    protected boolean addScriptVariables( @Nonnull Map<String, Object> arguments, @Nonnull TrackedEntityRule rule ) throws TransformerException
    {
        final TrackedEntityAttributes trackedEntityAttributes = trackedEntityMetadataService.getAttributes();
        arguments.put( ScriptVariable.TRACKED_ENTITY_ATTRIBUTES.getVariableName(), trackedEntityAttributes );
        final TrackedEntityType trackedEntityType = trackedEntityMetadataService.getType(
            rule.getTrackedEntity().getTrackedEntityReference() )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity type in rule " + rule + " could not be found: " +
                rule.getTrackedEntity().getTrackedEntityReference() ) );
        arguments.put( ScriptVariable.TRACKED_ENTITY_TYPE.getVariableName(), trackedEntityType );

        // is applicable for further processing
        return true;
    }

    @Override
    protected boolean isSyncRequired( @Nonnull FhirToDhisTransformerContext context, @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return true;
    }

    @Nonnull
    @Override
    protected Optional<TrackedEntityInstance> getResourceById( @Nullable String id ) throws TransformerException
    {
        return (id == null) ? Optional.empty() : getTrackedEntityService().findById( id );
    }

    @Nonnull
    @Override
    protected Optional<TrackedEntityInstance> getActiveResource( @Nonnull FhirToDhisTransformerContext context,
        @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables, boolean sync ) throws TransformerException
    {
        final IBaseResource baseResource;
        if ( rule.getTeiLookupScript() == null )
        {
            baseResource = getScriptVariable( scriptVariables, ScriptVariable.INPUT, IBaseResource.class );
        }
        else
        {
            baseResource = getScriptExecutor().execute( rule.getTeiLookupScript(), context.getFhirRequest().getVersion(), scriptVariables, IBaseResource.class );
        }
        if ( baseResource == null )
        {
            return Optional.empty();
        }
        if ( sync )
        {
            lockManager.getCurrentLockContext().orElseThrow( () -> new FatalTransformerException( "No lock context available." ) )
                .lock( "tei-fhir-resource-id:" + baseResource.getIdElement().toUnqualifiedVersionless() );
        }
        return getTrackedEntityInstanceByIdentifier( context, rule, baseResource, scriptVariables, sync );
    }

    @Nullable
    @Override
    protected TrackedEntityInstance createResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull TrackedEntityRule rule,
        @Nullable String id, @Nonnull Map<String, Object> scriptVariables, boolean sync ) throws TransformerException
    {
        if ( context.isCreationDisabled() )
        {
            return null;
        }

        final IBaseResource resource;
        if ( rule.getTeiLookupScript() == null )
        {
            resource = getScriptVariable( scriptVariables, ScriptVariable.INPUT, IBaseResource.class );
        }
        else
        {
            resource = getScriptExecutor().execute( rule.getTeiLookupScript(), context.getFhirRequest().getVersion(), scriptVariables, IBaseResource.class );
            if ( resource == null )
            {
                return null;
            }
            throw new TrackedEntityInstanceNotFoundException( "Tracked entity for resource " + resource.getIdElement().toUnqualifiedVersionless() + " could not be found.", resource );
        }

        String identifier = getIdentifier( context, rule, scriptVariables );
        if ( identifier == null )
        {
            return null;
        }
        identifier = createFullQualifiedTrackedEntityInstanceIdentifier( context, resource, identifier );

        final TrackedEntityInstance trackedEntityInstance = new TrackedEntityInstance(
            getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class ), id, true );
        trackedEntityInstance.setIdentifier( identifier );

        final TrackedEntityAttributes trackedEntityAttributes = getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_ATTRIBUTES, TrackedEntityAttributes.class );
        final String attributeId = trackedEntityAttributes.getOptional(
            rule.getTrackedEntity().getTrackedEntityIdentifierReference() ).orElseThrow( () ->
            new TransformerMappingException( "Tracked entity identifier attribute does not exist: " +
                rule.getTrackedEntity().getTrackedEntityIdentifierReference() ) ).getId();
        trackedEntityInstance.getAttribute( attributeId ).setValue( identifier );

        return trackedEntityInstance;
    }

    @Nullable
    protected String getIdentifier( @Nonnull FhirToDhisTransformerContext context, @Nonnull TrackedEntityRule rule, @Nonnull Map<String, Object> scriptVariables )
    {
        final IBaseResource baseResource;
        if ( rule.getTeiLookupScript() == null )
        {
            baseResource = getScriptVariable( scriptVariables, ScriptVariable.INPUT, IBaseResource.class );
        }
        else
        {
            baseResource = getScriptExecutor().execute( rule.getTeiLookupScript(), context.getFhirRequest().getVersion(), scriptVariables, IBaseResource.class );
        }
        if ( baseResource == null )
        {
            return null;
        }
        return getIdentifier( context, baseResource, scriptVariables );
    }
}
