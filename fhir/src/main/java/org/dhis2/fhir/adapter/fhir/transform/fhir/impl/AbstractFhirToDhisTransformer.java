package org.dhis2.fhir.adapter.fhir.transform.fhir.impl;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.DhisResource;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.IdentifiedTrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttribute;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.fhir.data.repository.FhirDhisAssignmentRepository;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RuleInfo;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.repository.DhisFhirResourceId;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionException;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisDeleteTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.TrackedEntityInstanceNotFoundException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractFhirResourceFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractIdentifierFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AssignmentFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.scripted.WritableScriptedTrackedEntityInstance;
import org.dhis2.fhir.adapter.fhir.transform.util.TransformerUtils;
import org.dhis2.fhir.adapter.geo.Location;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract base class for all transformers from FHIR to DHIS2 resources. The default priority of the transformer is <code>0</code>.
 *
 * @param <R> the concrete type of the FHIR resource.
 * @param <U> the concrete type of transformation rule that this transformer processes.
 * @author volsch
 */
public abstract class AbstractFhirToDhisTransformer<R extends DhisResource, U extends AbstractRule> implements FhirToDhisTransformer<R, U>
{
    protected final Logger logger = LoggerFactory.getLogger( getClass() );

    private final ScriptExecutor scriptExecutor;

    private final OrganizationUnitService organizationUnitService;

    private final TrackedEntityService trackedEntityService;

    private final TrackedEntityMetadataService trackedEntityMetadataService;

    private final FhirDhisAssignmentRepository fhirDhisAssignmentRepository;

    private final ScriptExecutionContext scriptExecutionContext;

    private final ValueConverter valueConverter;

    public AbstractFhirToDhisTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull OrganizationUnitService organizationUnitService,
        @Nonnull ObjectProvider<TrackedEntityMetadataService> trackedEntityMetadataService, @Nonnull ObjectProvider<TrackedEntityService> trackedEntityService,
        @Nonnull FhirDhisAssignmentRepository fhirDhisAssignmentRepository, @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        this.scriptExecutor = scriptExecutor;
        this.organizationUnitService = organizationUnitService;
        this.trackedEntityMetadataService = trackedEntityMetadataService.getIfAvailable();
        this.trackedEntityService = trackedEntityService.getIfAvailable();
        this.fhirDhisAssignmentRepository = fhirDhisAssignmentRepository;
        this.scriptExecutionContext = scriptExecutionContext;
        this.valueConverter = valueConverter;
    }

    @Override
    public int getPriority()
    {
        return 0;
    }

    @Override
    public int compareTo( @Nonnull FhirToDhisTransformer<?, ?> o )
    {
        final int value = getPriority() - o.getPriority();

        if ( value != 0 )
        {
            return value;
        }

        return getClass().getSimpleName().compareTo( o.getClass().getSimpleName() );
    }

    @Nullable
    @Override
    public FhirToDhisTransformOutcome<R> transformCasted( @Nonnull FhirClientResource fhirClientResource, @Nonnull FhirToDhisTransformerContext context, @Nonnull IBaseResource input,
        @Nonnull RuleInfo<? extends AbstractRule> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return transform( fhirClientResource, context, input, new RuleInfo<>( getRuleClass().cast( ruleInfo.getRule() ), ruleInfo.getDhisDataReferences() ), scriptVariables );
    }

    @Nullable
    @Override
    public FhirToDhisDeleteTransformOutcome<R> transformDeletionCasted( @Nonnull FhirClientResource fhirClientResource, @Nonnull RuleInfo<? extends AbstractRule> ruleInfo, @Nonnull DhisFhirResourceId dhisFhirResourceId ) throws TransformerException
    {
        return transformDeletion( fhirClientResource, new RuleInfo<>( getRuleClass().cast( ruleInfo.getRule() ), ruleInfo.getDhisDataReferences() ), dhisFhirResourceId );
    }

    @Nonnull
    protected TrackedEntityService getTrackedEntityService() throws FatalTransformerException
    {
        if ( trackedEntityService == null )
        {
            throw new FatalTransformerException( "Tracked entity service has not been provided." );
        }

        return trackedEntityService;
    }

    @Nonnull
    protected TrackedEntityMetadataService getTrackedEntityMetadataService() throws FatalTransformerException
    {
        if ( trackedEntityMetadataService == null )
        {
            throw new FatalTransformerException( "Tracked entity metadata service has not been provided." );
        }

        return trackedEntityMetadataService;
    }

    protected abstract boolean isAlwaysActiveResource( @Nonnull RuleInfo<U> ruleInfo );

    @Nonnull
    protected Optional<R> getResource( @Nonnull FhirClientResource fhirClientResource, @Nonnull FhirToDhisTransformerContext context,
        @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        boolean activeResource = true;

        if ( context.getFhirRequest().isDhisFhirId() )
        {
            final String id = getDhisId( context, ruleInfo );

            if ( id == null )
            {
                activeResource = ( context.getFhirRequest().getRequestMethod() == null ) ||
                    !context.getFhirRequest().getRequestMethod().isCreateOnly();
            }
            else
            {
                R resource = getResourceById( id ).orElse( null );

                if ( resource != null )
                {
                    return Optional.of( resource );
                }

                activeResource = false;
            }
        }
        else
        {
            final R resource = getResourceByAssignment( fhirClientResource, context, ruleInfo, scriptVariables ).orElse( null );

            if ( resource != null )
            {
                return Optional.of( resource );
            }
        }

        if ( activeResource || isAlwaysActiveResource( ruleInfo ) )
        {
            final R resource = getActiveResource( context, ruleInfo, scriptVariables, false, true ).orElse( null );

            if ( resource != null )
            {
                return Optional.of( resource );
            }
        }

        R resource = null;
        if ( context.getFhirRequest().getRequestMethod() == null || context.getFhirRequest().getRequestMethod().isCreate() )
        {
            final String id = getDhisId( context, ruleInfo );
            resource = createResource( context, ruleInfo, id, scriptVariables, false, false );

            if ( resource != null && isSyncRequired( context, ruleInfo, scriptVariables ) )
            {
                // the active resource may have been created in the meantime
                resource = getActiveResource( context, ruleInfo, scriptVariables, true, true ).orElse( null );

                if ( resource != null )
                {
                    return Optional.of( resource );
                }

                resource = createResource( context, ruleInfo, id, scriptVariables, true, false );
            }
        }

        return Optional.ofNullable( resource );
    }

    protected abstract boolean isSyncRequired( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;

    @Nonnull
    protected abstract Optional<R> getResourceById( @Nullable String id ) throws TransformerException;

    @Nonnull
    protected abstract Optional<R> getActiveResource(
        @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo,
        @Nonnull Map<String, Object> scriptVariables, boolean sync, boolean refreshed ) throws TransformerException;

    @Nonnull
    protected Optional<R> getResourceByAssignment( @Nonnull FhirClientResource fhirClientResource,
        @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final IBaseResource fhirResource = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.INPUT, IBaseResource.class );
        final String dhisResourceId = fhirDhisAssignmentRepository.findFirstDhisResourceId( ruleInfo.getRule(), fhirClientResource.getFhirClient(),
            fhirResource.getIdElement() );

        if ( dhisResourceId == null )
        {
            return Optional.empty();
        }

        return findResourceById( context, ruleInfo, dhisResourceId, scriptVariables );
    }

    @Nonnull
    protected abstract Optional<R> findResourceById( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull String id, @Nonnull Map<String, Object> scriptVariables );

    @Nullable
    protected abstract R createResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo,
        @Nullable String id, @Nonnull Map<String, Object> scriptVariables, boolean sync, boolean refreshed ) throws TransformerException;

    @Nullable
    protected String getDhisId( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo )
    {
        return context.getFhirRequest().getDhisResourceId();
    }

    @Nullable
    protected String getAssignedDhisId( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables, @Nullable IIdType fhirId )
    {
        final AssignmentFhirToDhisTransformerUtils assignmentTransformerUtils = TransformerUtils.getScriptVariable(
            scriptVariables, ScriptVariable.ASSIGNMENT_UTILS, AssignmentFhirToDhisTransformerUtils.class );

        return assignmentTransformerUtils.getMappedDhisId( context, ruleInfo.getRule(), fhirId );
    }

    protected boolean transform( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        if ( ruleInfo.getRule().getTransformImpScript() == null )
        {
            return true;
        }

        return Boolean.TRUE.equals( executeScript( context, ruleInfo, ruleInfo.getRule().getTransformImpScript(), scriptVariables, Boolean.class ) );
    }

    @Nonnull
    protected Optional<OrganizationUnit> getOrgUnit( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull ExecutableScript lookupScript, @Nonnull Map<String, Object> scriptVariables )
    {
        final Reference orgUnitReference = executeScript( context, ruleInfo, lookupScript, scriptVariables, Reference.class );

        if ( orgUnitReference == null )
        {
            logger.info( "Could not extract organization unit reference." );

            return Optional.empty();
        }

        return getOrgUnit( context, orgUnitReference, scriptVariables );
    }

    @Nonnull
    protected Optional<String> getOrgUnitId( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull ExecutableScript lookupScript, @Nonnull Map<String, Object> scriptVariables )
    {
        final Reference orgUnitReference = executeScript( context, ruleInfo, lookupScript, scriptVariables, Reference.class );

        if ( orgUnitReference == null )
        {
            logger.info( "Could not extract organization unit reference." );

            return Optional.empty();
        }

        if ( orgUnitReference.getType() == ReferenceType.ID )
        {
            return Optional.of( orgUnitReference.getValue() );
        }

        return getOrgUnit( context, orgUnitReference, scriptVariables ).map( OrganizationUnit::getId );
    }

    @Nonnull
    protected Optional<OrganizationUnit> getOrgUnit( @Nonnull FhirToDhisTransformerContext context, @Nonnull Reference orgUnitReference, @Nonnull Map<String, Object> scriptVariables )
    {
        final Optional<OrganizationUnit> organisationUnit = organizationUnitService.findMetadataByReference( orgUnitReference );
        if ( !organisationUnit.isPresent() )
        {
            logger.info( "Organization unit of reference does not exist: " + orgUnitReference );
        }
        return organisationUnit;
    }

    @Nonnull
    protected Optional<IdentifiedTrackedEntityInstance> getTrackedEntityInstance( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<TrackedEntityRule> ruleInfo,
        @Nonnull FhirResourceMapping resourceMapping, @Nonnull Map<String, Object> scriptVariables, boolean sync )
    {
        final IBaseResource baseResource = getTrackedEntityInstanceResource( context, ruleInfo, resourceMapping, scriptVariables );

        if ( baseResource == null )
        {
            logger.info( "Tracked entity instance resource could not be extracted from input." );

            return Optional.empty();
        }

        final Reference reference = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.FHIR_RESOURCE_UTILS, AbstractFhirResourceFhirToDhisTransformerUtils.class )
            .getResourceAdapterReference( context, baseResource, ruleInfo.getRule().getFhirResourceType() );

        if ( reference == null )
        {
            logger.info( "Tracked entity instance resource could not be extracted from input." );

            return Optional.empty();
        }

        switch ( reference.getType() )
        {
            case ID:
                return Optional.of( new IdentifiedTrackedEntityInstance( reference.getValue() ) );
            case CODE:
                return Optional.of( new IdentifiedTrackedEntityInstance( getTrackedEntityInstanceByIdentifier( context, ruleInfo, baseResource, scriptVariables, sync )
                    .orElseThrow( () -> new TrackedEntityInstanceNotFoundException( "Tracked entity instance for FHIR Resource ID " + baseResource.getIdElement() + " could not be found.", baseResource ) ) ) );
            default:
                throw new TransformerMappingException( "Reference type " + reference.getType() + " is not supported to retrieve tracked entity." );
        }
    }

    protected void addTrackedEntityScriptVariables( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<? extends AbstractRule> ruleInfo, @Nonnull IdentifiedTrackedEntityInstance trackedEntityInstance, @Nonnull Map<String, Object> variables ) throws TransformerException
    {
        final WritableScriptedTrackedEntityInstance scriptedTrackedEntityInstance;

        if ( trackedEntityInstance.hasResource() )
        {
            scriptedTrackedEntityInstance = new WritableScriptedTrackedEntityInstance(
                TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_ATTRIBUTES, TrackedEntityAttributes.class ),
                TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class ),
                Objects.requireNonNull( trackedEntityInstance.getResource() ), scriptExecutionContext, valueConverter );
        }
        else
        {
            scriptedTrackedEntityInstance = new WritableScriptedTrackedEntityInstance( getTrackedEntityMetadataService(), getTrackedEntityService(),
                TransformerUtils.getScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_ATTRIBUTES, TrackedEntityAttributes.class ),
                TransformerUtils.getOptionalScriptVariable( variables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class ),
                Objects.requireNonNull( trackedEntityInstance.getId() ), scriptExecutionContext, valueConverter );
        }

        variables.put( ScriptVariable.TRACKED_ENTITY_INSTANCE.getVariableName(), scriptedTrackedEntityInstance );
    }

    @Nullable
    protected IBaseResource getTrackedEntityInstanceResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<TrackedEntityRule> ruleInfo, @Nonnull FhirResourceMapping resourceMapping,
        @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return executeScript( context, ruleInfo, resourceMapping.getImpTeiLookupScript(), scriptVariables, IBaseResource.class );
    }

    @Nonnull
    protected Optional<TrackedEntityInstance> getTrackedEntityInstanceByIdentifier( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<TrackedEntityRule> ruleInfo, @Nonnull IBaseResource baseResource,
        @Nonnull Map<String, Object> scriptVariables, boolean sync ) throws TransformerException
    {
        String identifier = getIdentifier( context, baseResource, scriptVariables );

        if ( identifier == null )
        {
            return Optional.empty();
        }

        identifier = createFullQualifiedTrackedEntityInstanceIdentifier( context, baseResource, identifier );

        final TrackedEntityAttributes trackedEntityAttributes = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_ATTRIBUTES, TrackedEntityAttributes.class );
        final TrackedEntityAttribute identifierAttribute = trackedEntityAttributes.getOptional( ruleInfo.getRule().getTrackedEntity().getTrackedEntityIdentifierReference() )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity identifier attribute does not exist: " + ruleInfo.getRule().getTrackedEntity().getTrackedEntityIdentifierReference() ) );

        final TrackedEntityType trackedEntityType = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class );
        final Collection<TrackedEntityInstance> result;

        if ( sync )
        {
            result = getTrackedEntityService().findByAttrValueRefreshed(
                trackedEntityType.getId(), identifierAttribute.getId(), Objects.requireNonNull( identifier ), 2 );
        }
        else
        {
            result = getTrackedEntityService().findByAttrValue(
                trackedEntityType.getId(), identifierAttribute.getId(), Objects.requireNonNull( identifier ), 2 );
        }

        if ( result.size() > 1 )
        {
            throw new TransformerMappingException( "Filtering with identifier of rule " + ruleInfo +
                " returned more than one tracked entity instance: " + identifier );
        }

        final String finalIdentifier = identifier;

        return result.stream().peek( tei -> tei.setIdentifier( finalIdentifier ) ).findFirst();
    }

    protected String createFullQualifiedTrackedEntityInstanceIdentifier( @Nonnull FhirToDhisTransformerContext context, IBaseResource baseResource, String identifier )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResource( baseResource );
        if ( fhirResourceType == null )
        {
            return identifier;
        }
        final ResourceSystem resourceSystem = context.getFhirRequest().getResourceSystem( fhirResourceType );
        if ( (resourceSystem != null) && StringUtils.isNotBlank( resourceSystem.getCodePrefix() ) )
        {
            identifier = resourceSystem.getCodePrefix() + identifier;
        }
        return identifier;
    }

    @Nullable
    protected String getIdentifier( @Nonnull FhirToDhisTransformerContext context, IBaseResource baseResource, @Nonnull Map<String, Object> scriptVariables )
    {
        if ( !(baseResource instanceof IDomainResource) )
        {
            throw new TransformerMappingException( "Resource " + baseResource.getClass().getSimpleName() +
                " is no domain resource and does not include the required identifier." );
        }

        final FhirResourceType resourceType = FhirResourceType.getByResource( baseResource );
        if ( resourceType == null )
        {
            throw new TransformerMappingException( "Could not map  " + baseResource.getClass().getSimpleName() + " to a FHIR resource." );
        }
        final ResourceSystem resourceSystem = context.getFhirRequest().getOptionalResourceSystem( resourceType )
            .orElseThrow( () -> new TransformerMappingException( "No system has been defined for resource type " + resourceType + "." ) );

        final String identifier = TransformerUtils.getScriptVariable( scriptVariables, ScriptVariable.IDENTIFIER_UTILS, AbstractIdentifierFhirToDhisTransformerUtils.class )
            .getResourceIdentifier( (IDomainResource) baseResource, resourceType, resourceSystem.getSystem() );
        if ( identifier == null )
        {
            logger.info( "Resource " + resourceType + " does not include the required identifier with system: " + resourceSystem.getSystem() );
            return null;
        }

        return identifier;
    }

    @Nullable
    protected Location getCoordinate( @Nonnull FhirToDhisTransformerContext context, @Nonnull RuleInfo<U> ruleInfo, @Nonnull ExecutableScript locationLookupScript, @Nonnull Map<String, Object> scriptVariable )
    {
        return executeScript( context, ruleInfo, locationLookupScript, scriptVariable, Location.class );
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
}
