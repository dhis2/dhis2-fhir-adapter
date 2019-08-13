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
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.FatalTransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.scripted.TransformerScriptException;
import org.dhis2.fhir.adapter.fhir.transform.util.FhirBeanTransformerUtils;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Utilities to retrieve the referenced FHIR Resource from a FHIR Reference.
 *
 * @author volsch
 */
@Component
@Scriptable
@ScriptType( value = "ReferenceUtils", transformType = ScriptTransformType.IMP, var = ReferenceFhirToDhisTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities to resolveRule FHIR Reference to FHIR Resources when handling FHIR to DHIS2 transformations." )
public class ReferenceFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "referenceUtils";

    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final FhirClientResourceRepository fhirClientResourceRepository;

    private final FhirResourceRepository fhirResourceRepository;

    public ReferenceFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull FhirClientResourceRepository fhirClientResourceRepository,
        @Nonnull FhirResourceRepository fhirResourceRepository )
    {
        super( scriptExecutionContext );

        this.fhirClientResourceRepository = fhirClientResourceRepository;
        this.fhirResourceRepository = fhirResourceRepository;
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.ALL;
    }

    public void initReference( @Nullable IBaseReference reference, @Nullable Object resourceType )
    {
        initReference( reference, resourceType, false );
    }

    public void initReference( @Nullable IBaseReference reference, @Nullable Object resourceType, boolean refreshed )
    {
        getResource( reference, resourceType, refreshed );
    }

    @Nullable
    @ScriptMethod( description = "Returns the FHIR resource with the specified resource type for the specified FHIR reference. The returned FHIR resource may be a cached version of the resource and may not contain the latest data.",
        args = {
            @ScriptMethodArg( value = "reference", description = "The FHIR reference for which the resource should be returned." ),
            @ScriptMethodArg( value = "resourceType", description = "The FHIR resource type of the resource (upper case with under scores, e.g. RELATED_PERSON). If this is not specified the resource type must be included in the reference. " +
                "If this is specified and the resource type is also included in the reference, then both must match." )
        },
        returnDescription = "The FHIR resource for the specified reference." )
    public IBaseResource getResource( @Nullable IBaseReference reference, @Nullable Object resourceType )
    {
        return getResource( reference, resourceType, false );
    }

    @Nullable
    @ScriptMethod( description = "Returns the FHIR resource with the specified resource type for the specified FHIR reference.",
        args = {
            @ScriptMethodArg( value = "reference", description = "The FHIR reference for which the resource should be returned." ),
            @ScriptMethodArg( value = "resourceType", description = "The FHIR resource type of the resource (upper case with under scores, e.g. RELATED_PERSON). If this is not specified the resource type must be included in the reference. " +
                "If this is specified and the resource type is also included in the reference, then both must match." ),
            @ScriptMethodArg( value = "refreshed", description = "Specifies if the latest version of the resource should be returned. If this is set to false, a cached version may be returned." )
        },
        returnDescription = "The FHIR resource for the specified reference." )
    public IBaseResource getResource( @Nullable IBaseReference reference, @Nullable Object resourceType, boolean refreshed )
    {
        if ( reference == null )
        {
            return null;
        }

        final FhirResourceType fhirResourceType;

        if ( resourceType == null )
        {
            fhirResourceType = null;
        }
        else
        {
            try
            {
                fhirResourceType = NameUtils.toEnumValue( FhirResourceType.class, resourceType );
            }
            catch ( IllegalArgumentException e )
            {
                throw new TransformerScriptException( "Invalid FHIR resource type: " + resourceType, e );
            }
        }

        if ( reference.getResource() != null )
        {
            if ( (fhirResourceType != null) && (FhirResourceType.getByResource( reference.getResource() ) != fhirResourceType) )
            {
                logger.debug( "The referenced resource ID contains resource type {}, but requested resource type is {}.",
                    FhirResourceType.getByResource( reference.getResource() ), fhirResourceType );

                return null;
            }

            // also handles the case of a local reference
            return reference.getResource();
        }

        if ( reference.isEmpty() )
        {
            return null;
        }
        if ( reference.getReferenceElement().isLocal() )
        {
            throw new TransformerDataException( "Reference element refers to a contained resource, but no resource is specified: " + reference.getReferenceElement() );
        }

        String finalResourceType = reference.getReferenceElement().getResourceType();
        if ( fhirResourceType != null )
        {
            if ( (finalResourceType != null) && !finalResourceType.equals( fhirResourceType.getResourceTypeName() ) )
            {
                logger.debug( "The referenced resource ID contains resource type {}, but requested resource type is {}.",
                    finalResourceType, fhirResourceType );
                return null;
            }
            finalResourceType = fhirResourceType.getResourceTypeName();
        }

        if ( finalResourceType == null )
        {
            throw new TransformerDataException( "Final resource type could not be determined for reference: " + reference.getReferenceElement() );
        }
        if ( !reference.getReferenceElement().hasIdPart() )
        {
            throw new TransformerDataException( "Reference element does not include an ID part: " + reference.getReferenceElement() );
        }

        final FhirToDhisTransformerContext context = getScriptVariable( ScriptVariable.CONTEXT.getVariableName(), FhirToDhisTransformerContext.class );
        final UUID resourceId = context.getFhirRequest().getFhirClientResourceId();
        if ( resourceId == null )
        {
            throw new TransformerMappingException( "FHIR client cannot be created without having a client request." );
        }
        final FhirClientResource fhirClientResource = fhirClientResourceRepository.findOneByIdCached( resourceId )
            .orElseThrow( () -> new TransformerMappingException( "Could not find FHIR client resource with ID " + resourceId ) );

        final FhirContext fhirContext = fhirResourceRepository.findFhirContext( context.getFhirRequest().getVersion() )
            .orElseThrow( () -> new FatalTransformerException( "FHIR context for FHIR version " + context.getFhirRequest().getVersion() + " is not available." ) );
        final FhirClient fhirClient = fhirClientResource.getFhirClient();
        final Optional<IBaseResource> optionalResource = refreshed ?
            fhirResourceRepository.findRefreshed( fhirClient.getId(), fhirClient.getFhirVersion(), fhirClient.getFhirEndpoint(),
                finalResourceType, reference.getReferenceElement().getIdPart() ) :
            fhirResourceRepository.find( fhirClient.getId(), fhirClient.getFhirVersion(), fhirClient.getFhirEndpoint(),
                finalResourceType, reference.getReferenceElement().getIdPart() );
        final IBaseResource resource = optionalResource.map( r -> FhirBeanTransformerUtils.clone( fhirContext, r ) )
            .orElseThrow( () -> new TransformerDataException( "Referenced FHIR resource " + reference.getReferenceElement() + " does not exist for FHIR client resource " + resourceId ) );
        reference.setResource( resource );
        return resource;
    }
}
