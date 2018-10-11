package org.dhis2.fhir.adapter.fhir.transform.scripted;

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
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganisationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganisationUnitService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityTypeAttribute;
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.TrackedEntityRule;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutor;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformOutcome;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerRequestException;
import org.dhis2.fhir.adapter.fhir.transform.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.scripted.util.AbstractIdentifierFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.geo.Location;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractFhirToDhisTransformer<R extends DhisResource, U extends AbstractRule> implements FhirToDhisTransformer<R, U>
{
    protected final Logger logger = LoggerFactory.getLogger( getClass() );

    private final ScriptExecutor scriptExecutor;

    private final OrganisationUnitService organisationUnitService;

    private final TrackedEntityService trackedEntityService;

    public AbstractFhirToDhisTransformer( @Nonnull ScriptExecutor scriptExecutor, @Nonnull OrganisationUnitService organisationUnitService, @Nonnull ObjectProvider<TrackedEntityService> trackedEntityService )
    {
        this.scriptExecutor = scriptExecutor;
        this.organisationUnitService = organisationUnitService;
        this.trackedEntityService = trackedEntityService.getIfAvailable();
    }

    @Nullable
    @Override
    public FhirToDhisTransformOutcome<R> transformCasted( @Nonnull FhirToDhisTransformerContext context, @Nonnull IBaseResource input,
        @Nonnull AbstractRule rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return transform( context, input, getRuleClass().cast( rule ), scriptVariables );
    }

    @Nonnull
    protected ScriptExecutor getScriptExecutor()
    {
        return scriptExecutor;
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
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.DSTU3_ONLY;
    }

    @Nonnull
    protected Optional<R> getResource( @Nonnull FhirToDhisTransformerContext context,
        @Nonnull U rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final String id = getDhisId( context, rule );
        return Optional.ofNullable( getResourceById( id ).orElseGet( () -> getResourceByIdentifier( context, rule, scriptVariables )
            .orElseGet( () -> getActiveResource( context, rule, scriptVariables ).orElseGet( () -> createResource( context, rule, id, scriptVariables ) ) ) ) );
    }

    @Nonnull
    protected abstract Optional<R> getResourceById( @Nullable String id ) throws TransformerException;

    @Nonnull
    protected abstract Optional<R> getResourceByIdentifier(
        @Nonnull FhirToDhisTransformerContext context, @Nonnull U rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;

    @Nonnull
    protected abstract Optional<R> getActiveResource(
        @Nonnull FhirToDhisTransformerContext context, @Nonnull U rule,
        @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;

    @Nullable
    protected abstract R createResource(
        @Nonnull FhirToDhisTransformerContext context, @Nonnull U rule,
        @Nullable String id, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException;

    @Nullable
    protected String getDhisId( @Nonnull FhirToDhisTransformerContext context, @Nonnull U rule )
    {
        final String resourceId = context.getFhirRequest().getResourceId();
        if ( (resourceId != null) && context.getFhirRequest().isRemoteSubscription() )
        {
            throw new TransformerRequestException( "Requests that contain a resource ID " +
                "while processing a remote subscription are not supported." );
        }
        return resourceId;
    }

    protected boolean transform( @Nonnull FhirToDhisTransformerContext context, @Nonnull U rule, @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return Boolean.TRUE.equals( scriptExecutor.execute( rule.getTransformInScript(), context.getFhirRequest().getVersion(), scriptVariables, Boolean.class ) );
    }

    @Nonnull
    protected Optional<OrganisationUnit> getOrgUnit( @Nonnull FhirToDhisTransformerContext context, @Nonnull ExecutableScript lookupScript, @Nonnull Map<String, Object> scriptVariables )
    {
        final Reference orgUnitReference = getScriptExecutor().execute( lookupScript, context.getFhirRequest().getVersion(), scriptVariables, Reference.class );
        if ( orgUnitReference == null )
        {
            logger.info( "Could not extract organization unit reference." );
            return Optional.empty();
        }

        final Optional<OrganisationUnit> organisationUnit = organisationUnitService.get( orgUnitReference );
        if ( !organisationUnit.isPresent() )
        {
            logger.info( "Organization unit of extracted reference does not exist: " + orgUnitReference );
        }
        return organisationUnit;
    }

    @Nonnull
    protected Optional<TrackedEntityInstance> getTrackedEntityInstance( @Nonnull FhirToDhisTransformerContext context, @Nonnull TrackedEntityRule rule,
        @Nonnull FhirResourceMapping resourceMapping, @Nonnull Map<String, Object> scriptVariables )
    {
        final IBaseResource baseResource = getTrackedEntityInstanceResource( context, resourceMapping, scriptVariables );
        if ( baseResource == null )
        {
            logger.info( "Tracked entity instance resource could not be extracted from input." );
            return Optional.empty();
        }
        final Optional<TrackedEntityInstance> trackedEntityInstance = getTrackedEntityInstanceByIdentifier( context, rule, baseResource, scriptVariables );
        if ( !trackedEntityInstance.isPresent() )
        {
            logger.info( "Tracked entity instance could not be extracted from {}.", baseResource.getClass().getSimpleName() );
        }
        return trackedEntityInstance;
    }

    @Nullable
    protected IBaseResource getTrackedEntityInstanceResource( @Nonnull FhirToDhisTransformerContext context, @Nonnull FhirResourceMapping resourceMapping,
        @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        return getScriptExecutor().execute( resourceMapping.getTeiLookupScript(), context.getFhirRequest().getVersion(), scriptVariables, IBaseResource.class );
    }

    @Nonnull
    protected Optional<TrackedEntityInstance> getTrackedEntityInstanceByIdentifier( @Nonnull FhirToDhisTransformerContext context, @Nonnull TrackedEntityRule rule, @Nonnull IBaseResource baseResource,
        @Nonnull Map<String, Object> scriptVariables ) throws TransformerException
    {
        final String identifier = getIdentifier( context, baseResource, rule.isTrackedEntityIdentifierFq(), scriptVariables );
        if ( identifier == null )
        {
            return Optional.empty();
        }

        final TrackedEntityType trackedEntityType = getScriptVariable( scriptVariables, ScriptVariable.TRACKED_ENTITY_TYPE, TrackedEntityType.class );
        final TrackedEntityTypeAttribute identifierAttribute = trackedEntityType.getOptionalTypeAttribute( rule.getTrackedEntityIdentifierReference() )
            .orElseThrow( () -> new TransformerMappingException( "Tracked entity type \"" + rule.getTrackedEntityReference() +
                "\" does not include identifier attribute \"" + rule.getTrackedEntityIdentifierReference() + "\"." ) );

        final Collection<TrackedEntityInstance> result = getTrackedEntityService().findByAttrValue(
            trackedEntityType.getId(), identifierAttribute.getAttributeId(), Objects.requireNonNull( identifier ), 2 );
        if ( result.size() > 1 )
        {
            throw new TransformerMappingException( "Filtering with identifier of rule " + rule +
                " returned more than one tracked entity instance: " + identifier );
        }
        return result.stream().findFirst();
    }

    @Nullable
    protected String getIdentifier( @Nonnull FhirToDhisTransformerContext context, IBaseResource baseResource, boolean identifierFullQualified, @Nonnull Map<String, Object> scriptVariables )
    {
        if ( !(baseResource instanceof IDomainResource) )
        {
            throw new TransformerDataException( "Resource " + baseResource.getClass().getSimpleName() +
                " is no domain resource and does not include the required identifier." );
        }

        final FhirResourceType resourceType = FhirResourceType.getByResource( baseResource );
        if ( resourceType == null )
        {
            throw new TransformerDataException( "Could not map  " + baseResource.getClass().getSimpleName() + " to a FHIR resource." );
        }
        final ResourceSystem resourceSystem = context.getFhirRequest().getOptionalResourceSystem( resourceType )
            .orElseThrow( () -> new TransformerDataException( "No system has been defined for resource type " + resourceType + "." ) );

        final String identifier = getScriptVariable( scriptVariables, ScriptVariable.IDENTIFIER_UTILS, AbstractIdentifierFhirToDhisTransformerUtils.class )
            .getResourceIdentifier( (IDomainResource) baseResource, resourceType, resourceSystem.getSystem() );
        if ( identifier == null )
        {
            logger.info( "Resource " + resourceType + " does not include the required identifier with system: " + resourceSystem.getSystem() );
            return null;
        }
        if ( identifierFullQualified )
        {
            throw new TransformerDataException( "Full qualified identifiers are required that are not yet supported." );
        }

        return identifier;
    }

    @Nullable
    protected Location getCoordinate( @Nonnull FhirToDhisTransformerContext context, @Nonnull ExecutableScript locationLookupScript, @Nonnull Map<String, Object> scriptVariable )
    {
        return getScriptExecutor().execute( locationLookupScript, context.getFhirRequest().getVersion(), scriptVariable, Location.class );
    }

    @Nonnull
    protected <T> T getScriptVariable( @Nonnull Map<String, Object> scriptVariables, @Nonnull ScriptVariable scriptVariable, @Nonnull Class<T> type ) throws FatalTransformerException
    {
        final T value = type.cast( scriptVariables.get( scriptVariable.getVariableName() ) );
        if ( value == null )
        {
            throw new FatalTransformerException( "Script variable is not included: " + scriptVariable );
        }
        return value;
    }
}
