package org.dhis2.fhir.adapter.fhir.transform.impl.util;

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

import ca.uhn.fhir.context.FhirContext;
import org.apache.commons.lang.StringUtils;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganisationUnitService;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscription;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.RemoteSubscriptionResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.RemoteFhirRepository;
import org.dhis2.fhir.adapter.fhir.repository.RemoteHierarchicallyFhirRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionRequired;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.model.ResourceSystem;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Transformer utilities for organization.
 *
 * @author volsch
 */
public abstract class AbstractOrganizationTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    private static final String SCRIPT_ATTR_NAME = "organizationUtils";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final OrganisationUnitService organisationUnitService;

    private final RemoteSubscriptionResourceRepository subscriptionResourceRepository;

    private final RemoteFhirRepository remoteFhirRepository;

    private final RemoteHierarchicallyFhirRepository remoteHierarchicallyFhirRepository;

    public AbstractOrganizationTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull OrganisationUnitService organisationUnitService,
        @Nonnull RemoteSubscriptionResourceRepository subscriptionResourceRepository,
        @Nonnull RemoteFhirRepository remoteFhirRepository,
        @Nonnull RemoteHierarchicallyFhirRepository remoteHierarchicallyFhirRepository )
    {
        super( scriptExecutionContext );
        this.organisationUnitService = organisationUnitService;
        this.subscriptionResourceRepository = subscriptionResourceRepository;
        this.remoteFhirRepository = remoteFhirRepository;
        this.remoteHierarchicallyFhirRepository = remoteHierarchicallyFhirRepository;
    }

    @Nonnull
    @Override
    public String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @ScriptExecutionRequired
    public boolean exists( @Nullable String code )
    {
        if ( code == null )
        {
            return false;
        }

        final FhirToDhisTransformerContext context = getScriptVariable( ScriptVariable.CONTEXT.getVariableName(), FhirToDhisTransformerContext.class );
        final ResourceSystem resourceSystem = context.getFhirRequest().getOptionalResourceSystem( FhirResourceType.ORGANIZATION )
            .orElseThrow( () -> new TransformerMappingException( "No system has been defined for resource type " + FhirResourceType.ORGANIZATION + "." ) );

        return organisationUnitService.findOneByReference( new Reference( code, ReferenceType.CODE ) ).isPresent();
    }

    @ScriptExecutionRequired
    @Nullable
    public String existsWithPrefix( @Nullable String code )
    {
        if ( code == null )
        {
            return null;
        }

        final FhirToDhisTransformerContext context = getScriptVariable( ScriptVariable.CONTEXT.getVariableName(), FhirToDhisTransformerContext.class );
        final ResourceSystem resourceSystem = context.getFhirRequest().getOptionalResourceSystem( FhirResourceType.ORGANIZATION )
            .orElseThrow( () -> new TransformerMappingException( "No system has been defined for resource type " + FhirResourceType.ORGANIZATION + "." ) );

        final String resultingCode = StringUtils.defaultString( resourceSystem.getCodePrefix() ) + code;
        if ( organisationUnitService.findOneByReference( new Reference( resultingCode, ReferenceType.CODE ) ).isPresent() )
        {
            return resultingCode;
        }
        return null;
    }

    @Nullable
    public List<? extends IBaseResource> findHierarchy( @Nullable IBaseReference childReference )
    {
        if ( (childReference == null) || childReference.isEmpty() )
        {
            return null;
        }

        if ( childReference.getReferenceElement().isLocal() )
        {
            throw new TransformerDataException( "Reference element refers to an embedded resource, but no resource is specified: " + childReference.getReferenceElement() );
        }
        if ( childReference.getReferenceElement().hasResourceType() && !FhirResourceType.ORGANIZATION.getResourceTypeName().equals( childReference.getReferenceElement().getResourceType() ) )
        {
            throw new TransformerDataException( "Reference element does not refer to a FHIR Organization: " + childReference.getReferenceElement() );
        }
        if ( !childReference.getReferenceElement().hasIdPart() )
        {
            throw new TransformerDataException( "Reference element does not include an ID part: " + childReference.getReferenceElement() );
        }

        final FhirToDhisTransformerContext context = getScriptVariable( ScriptVariable.CONTEXT.getVariableName(), FhirToDhisTransformerContext.class );
        final UUID resourceId = context.getFhirRequest().getRemoteSubscriptionResourceId();
        if ( resourceId == null )
        {
            throw new TransformerMappingException( "FHIR client cannot be created without having a remote request." );
        }
        final RemoteSubscriptionResource subscriptionResource = subscriptionResourceRepository.findById( resourceId )
            .orElseThrow( () -> new TransformerMappingException( "Could not find remote subscription resource with ID " + resourceId ) );

        final FhirContext fhirContext = remoteFhirRepository.findFhirContext( context.getFhirRequest().getVersion() )
            .orElseThrow( () -> new FatalTransformerException( "FHIR context for FHIR version " + context.getFhirRequest().getVersion() + " is not available." ) );
        final RemoteSubscription remoteSubscription = subscriptionResource.getRemoteSubscription();

        final IBaseBundle hierarchyBundle = remoteHierarchicallyFhirRepository.findWithParents( remoteSubscription.getId(), remoteSubscription.getFhirVersion(), remoteSubscription.getFhirEndpoint(),
            FhirResourceType.ORGANIZATION.getResourceTypeName(), childReference.getReferenceElement().getIdPart(), "organizationPartOf", this::getParentReference );
        final List<IBaseResource> hierarchy = extractResources( hierarchyBundle );
        return hierarchy.isEmpty() ? null : BeanTransformerUtils.clone( fhirContext, hierarchy );
    }

    @Nullable
    protected abstract IBaseReference getParentReference( @Nullable IBaseResource resource );

    @Nonnull
    protected abstract List<IBaseResource> extractResources( @Nullable IBaseBundle bundle );
}