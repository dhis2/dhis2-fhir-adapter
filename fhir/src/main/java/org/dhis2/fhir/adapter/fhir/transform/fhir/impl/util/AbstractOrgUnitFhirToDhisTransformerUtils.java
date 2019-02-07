package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util;

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
import org.apache.commons.lang.StringUtils;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.repository.HierarchicallyFhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionRequired;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.util.FhirBeanTransformerUtils;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Transformer utilities for FHIR related organization units.
 *
 * @author volsch
 */
@Scriptable
public abstract class AbstractOrgUnitFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final OrganizationUnitService organizationUnitService;

    private final FhirClientResourceRepository fhirClientResourceRepository;

    private final FhirResourceRepository fhirResourceRepository;

    private final HierarchicallyFhirResourceRepository hierarchicallyFhirResourceRepository;

    public AbstractOrgUnitFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull OrganizationUnitService organizationUnitService,
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull FhirResourceRepository fhirResourceRepository,
        @Nonnull HierarchicallyFhirResourceRepository hierarchicallyFhirResourceRepository )
    {
        super( scriptExecutionContext );
        this.organizationUnitService = organizationUnitService;
        this.fhirClientResourceRepository = fhirClientResourceRepository;
        this.fhirResourceRepository = fhirResourceRepository;
        this.hierarchicallyFhirResourceRepository = hierarchicallyFhirResourceRepository;
    }

    @ScriptMethod( description = "Checks if the specified DHIS2 organization unit code exists on DHIS2.",
        args = @ScriptMethodArg( value = "code", description = "The DHIS2 organization unit code that should be checked." ),
        returnDescription = "If the specified DHIS2 organization unit code exists." )
    public boolean exists( @Nullable String code )
    {
        if ( code == null )
        {
            return false;
        }
        return organizationUnitService.findOneByReference( new Reference( code, ReferenceType.CODE ) ).isPresent();
    }

    @Nullable
    @ScriptExecutionRequired
    @ScriptMethod( description = "Checks if the specified DHIS2 organization unit code exists on DHIS2 with the code prefix that is defined for organizations of the FHIR client of the current transformation context.",
        args = @ScriptMethodArg( value = "code", description = "The DHIS2 organization unit code (without prefix) that should be checked." ),
        returnDescription = "The DHIS2 organization unit code (includin prefix, as it exists on DHIS2) or null if it does not exist." )
    public String existsWithPrefix( @Nullable String code )
    {
        if ( code == null )
        {
            return null;
        }

        final FhirToDhisTransformerContext context = getScriptVariable( ScriptVariable.CONTEXT.getVariableName(), FhirToDhisTransformerContext.class );
        final ResourceSystem resourceSystem = context.getFhirRequest().getOptionalResourceSystem( getFhirResourceType() )
            .orElseThrow( () -> new TransformerMappingException( "No system has been defined for resource type " + getFhirResourceType() + "." ) );

        final String resultingCode = StringUtils.defaultString( resourceSystem.getCodePrefix() ) + code;
        if ( organizationUnitService.findOneByReference( new Reference( resultingCode, ReferenceType.CODE ) ).isPresent() )
        {
            return resultingCode;
        }
        return null;
    }

    @Nullable
    protected List<? extends IBaseResource> findHierarchy( @Nullable IBaseReference childReference, @Nonnull Set<IBaseResource> processedResources )
    {
        if ( childReference == null )
        {
            return null;
        }

        if ( childReference.getResource() != null )
        {
            if ( !processedResources.add( childReference.getResource() ) )
            {
                // loop within hierarchy
                return null;
            }

            final List<IBaseResource> foundResources = new ArrayList<>();
            foundResources.add( childReference.getResource() );
            final List<? extends IBaseResource> remainingResources = findHierarchy(
                getParentReference( childReference.getResource() ), processedResources );
            if ( remainingResources != null )
            {
                foundResources.addAll( remainingResources );
            }
            return foundResources;
        }

        if ( childReference.isEmpty() )
        {
            return null;
        }
        if ( childReference.getReferenceElement().isLocal() )
        {
            throw new TransformerDataException( "Reference element refers to a contained resource, but no resource is specified: " + childReference.getReferenceElement() );
        }
        if ( childReference.getReferenceElement().hasResourceType() && !getFhirResourceType().getResourceTypeName().equals( childReference.getReferenceElement().getResourceType() ) )
        {
            throw new TransformerDataException( "Reference element does not refer to a FHIR " + getFhirResourceType() + ": " + childReference.getReferenceElement() );
        }
        if ( !childReference.getReferenceElement().hasIdPart() )
        {
            throw new TransformerDataException( "Reference element does not include an ID part: " + childReference.getReferenceElement() );
        }

        final FhirToDhisTransformerContext context = getScriptVariable( ScriptVariable.CONTEXT.getVariableName(), FhirToDhisTransformerContext.class );
        final UUID resourceId = context.getFhirRequest().getFhirClientResourceId();
        if ( resourceId == null )
        {
            throw new TransformerMappingException( "FHIR client cannot be created without having a server request." );
        }
        final FhirClientResource fhirClientResource = fhirClientResourceRepository.findOneByIdCached( resourceId )
            .orElseThrow( () -> new TransformerMappingException( "Could not find FHIR client resource with ID " + resourceId ) );

        final FhirContext fhirContext = fhirResourceRepository.findFhirContext( context.getFhirRequest().getVersion() )
            .orElseThrow( () -> new FatalTransformerException( "FHIR context for FHIR version " + context.getFhirRequest().getVersion() + " is not available." ) );
        final FhirClient fhirClient = fhirClientResource.getFhirClient();

        final IBaseBundle hierarchyBundle = hierarchicallyFhirResourceRepository.findWithParents( fhirClient.getId(), fhirClient.getFhirVersion(), fhirClient.getFhirEndpoint(),
            getFhirResourceType().getResourceTypeName(), childReference.getReferenceElement().getIdPart(), getFhirResourceType() + "PartOf", this::getParentReference );
        final List<IBaseResource> hierarchy = extractResources( hierarchyBundle );
        return hierarchy.isEmpty() ? null : FhirBeanTransformerUtils.clone( fhirContext, hierarchy );
    }

    @Nullable
    protected abstract IBaseReference getParentReference( @Nullable IBaseResource resource );

    @Nonnull
    protected abstract List<IBaseResource> extractResources( @Nullable IBaseBundle bundle );

    @Nonnull
    protected abstract FhirResourceType getFhirResourceType();
}