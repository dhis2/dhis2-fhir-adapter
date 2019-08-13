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

import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValues;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionRequired;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.scripted.TransformerScriptException;
import org.dhis2.fhir.adapter.fhir.util.FhirUriUtils;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.EnumValueUtils;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.instance.model.api.IBaseExtension;
import org.hl7.fhir.instance.model.api.IBaseHasExtensions;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * DHIS2 to FHIR transformer utility methods for FHIR identifiers.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "FhirResourceUtils", transformType = ScriptTransformType.IMP, var = AbstractFhirResourceFhirToDhisTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities to handle FHIR to DHIS2 transformations of FHIR resources." )
public abstract class AbstractFhirResourceFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "fhirResourceUtils";

    protected static final String INTERNAL_REFERENCE_BEGIN = "urn:";

    private final ReferenceFhirToDhisTransformerUtils referenceUtils;

    private final SystemCodeRepository systemCodeRepository;

    protected AbstractFhirResourceFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ReferenceFhirToDhisTransformerUtils referenceUtils,
        @Nonnull SystemCodeRepository systemCodeRepository )
    {
        super( scriptExecutionContext );

        this.referenceUtils = referenceUtils;
        this.systemCodeRepository = systemCodeRepository;
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nonnull
    @ScriptMethod( description = "Creates the FHIR resource (HAPI FHIR) with the specified resource type name.", returnDescription = "The created FHIR resource.",
        args = @ScriptMethodArg( value = "resourceType", description = "The FHIR resource type name of the resource to be created (e.g. Patient)." ) )
    public abstract IBaseResource createResource( @Nonnull Object resourceType );

    @Nullable
    @ScriptExecutionRequired
    @ScriptMethod( description = "Returns the identified FHIR reference for the specified resource type for the specified FHIR reference. " +
        "The identified FHIR reference contains sufficient information to lookup the resource in the current context but may not include more information.",
        args = {
            @ScriptMethodArg( value = "reference", description = "The FHIR reference for which the identified should be returned." ),
            @ScriptMethodArg( value = "resourceType", description = "The FHIR resource type of the resource." )
        },
        returnDescription = "The identified FHIR resource for the specified reference." )
    public IBaseReference getIdentifiedReference( @Nullable IBaseReference reference, @Nonnull Object resourceType )
    {
        final FhirToDhisTransformerContext context = getScriptVariable(
            ScriptVariable.CONTEXT.getVariableName(), FhirToDhisTransformerContext.class );

        return getIdentifiedReference( context, reference, resourceType );
    }

    public IBaseReference getIdentifiedReference( @Nonnull FhirToDhisTransformerContext context, @Nullable IBaseReference reference, @Nonnull Object resourceType )
    {
        if ( reference == null )
        {
            return null;
        }

        final FhirResourceType fhirResourceType = FhirResourceType.getByResourceTypeName( resourceType.toString() );

        if ( fhirResourceType == null )
        {
            throw new TransformerMappingException( "FHIR resource type is unknown: " + resourceType );
        }

        final ResourceSystem resourceSystem = context.getFhirRequest().getResourceSystem( fhirResourceType );
        final IBaseReference fhirReference;

        SystemCodeValues identifiers = getIdentifiers( reference );
        boolean resetIdElement = false;
        boolean containsSufficientIdentifier = false;

        if ( identifiers.getSystemCodeValues().isEmpty() && reference.getResource() != null )
        {
            identifiers = getIdentifiers( reference.getResource() );
        }

        if ( resourceSystem != null && resourceSystem.isFhirId() && reference.getReferenceElement().hasIdPart() && !reference.getReferenceElement().isLocal() &&
            identifiers.getSystemCodeValues().stream().noneMatch( scv -> Objects.equals( resourceSystem.getSystem(), scv.getSystem() ) ) )
        {
            identifiers.getSystemCodeValues().add( new SystemCodeValue( resourceSystem.getSystem(), reference.getReferenceElement().getIdPart() ) );
            resetIdElement = true;
        }

        if ( !identifiers.getSystemCodeValues().isEmpty() )
        {
            containsSufficientIdentifier = containsSufficientIdentifier( identifiers, resourceSystem );

            if ( !containsSufficientIdentifier )
            {
                final List<String> internalSystemUris = new ArrayList<>( 2 );

                internalSystemUris.add( System.DHIS2_FHIR_IDENTIFIER_URI );

                if ( resourceSystem != null )
                {
                    internalSystemUris.add( resourceSystem.getSystem() );
                }

                identifiers = new SystemCodeValues( systemCodeRepository.findAllInternalBySystemCodeValues(
                    internalSystemUris, identifiers.getSystemCodeValues().stream().map( SystemCodeValue::toString ).collect( Collectors.toList() ) )
                    .stream().map( sc -> new SystemCodeValue( sc.getSystem().getSystemUri(), sc.getSystemCode() ) ).collect( Collectors.toList() ) );
                containsSufficientIdentifier = containsSufficientIdentifier( identifiers, resourceSystem );
            }
        }

        if ( containsSufficientIdentifier || fhirResourceType.isSyncDhisId() || context.getFhirRequest().isDhisFhirId() )
        {
            fhirReference = createReference( reference.getResource() == null ? reference.getReferenceElement() : reference.getResource().getIdElement() );

            if ( resetIdElement )
            {
                fhirReference.setReference( null );
            }

            identifiers.getSystemCodeValues().stream().filter( scv -> ( resourceSystem != null && resourceSystem.getSystem().equals( scv.getSystem() ) ) )
                .forEach( scv -> addIdentifier( fhirReference, scv ) );
            // may override the resource system identifier (has higher precedence)
            identifiers.getSystemCodeValues().stream().filter( scv -> System.DHIS2_FHIR_IDENTIFIER_URI.equals( scv.getSystem() ) )
                .forEach( scv -> addIdentifier( fhirReference, scv ) );
        }
        else
        {
            fhirReference = null;
        }

        return fhirReference;
    }

    private boolean containsSufficientIdentifier( @Nonnull SystemCodeValues identifiers, @Nullable ResourceSystem resourceSystem )
    {
        return identifiers.getSystemCodeValues().stream()
            .anyMatch( scv -> System.DHIS2_FHIR_IDENTIFIER_URI.equals( scv.getSystem() ) ||
                ( resourceSystem != null && resourceSystem.getSystem().equals( scv.getSystem() ) ) );
    }

    @Nullable
    @ScriptExecutionRequired
    @ScriptMethod( description = "Returns the identified FHIR resource with the specified resource type for the specified FHIR reference. " +
        "The identified FHIR resource contains sufficient information to lookup the resource in the current context but may not include more information.",
        args = {
            @ScriptMethodArg( value = "reference", description = "The FHIR reference for which the resource should be returned." ),
            @ScriptMethodArg( value = "resourceType", description = "The FHIR resource type of the resource." )
        },
        returnDescription = "The identified FHIR resource for the specified reference." )
    public IBaseResource getIdentifiedResource( @Nullable IBaseReference reference, @Nonnull Object resourceType )
    {
        final FhirToDhisTransformerContext context = getScriptVariable(
            ScriptVariable.CONTEXT.getVariableName(), FhirToDhisTransformerContext.class );
        final IBaseReference identifiedReference = getIdentifiedReference( reference, resourceType );
        IBaseResource fhirResource = null;

        if ( !context.getFhirRequest().isDhisFhirId() && identifiedReference == null )
        {
            fhirResource = referenceUtils.getResource( reference, resourceType );
        }
        else if ( identifiedReference != null )
        {
            fhirResource = createResource( resourceType );
            fhirResource.setId( identifiedReference.getReferenceElement() );

            addIdentifiers( fhirResource, getIdentifiers( identifiedReference ) );
        }

        return fhirResource;
    }

    @Nullable
    @ScriptExecutionRequired
    @ScriptMethod( description = "Returns the adapter reference by ID, code or name for the specified canonical FHIR reference (if this can be resolved).",
        args = {
            @ScriptMethodArg( value = "canonical", description = "The canonical FHIR reference for which the adapter reference should be returned." ),
            @ScriptMethodArg( value = "fhirResourceType", description = "The FHIR resource type of the resource." )
        },
        returnDescription = "The adapter reference for the specified reference or null if it cannot be resolved." )
    public Reference getCanonicalAdapterReference( @Nullable IBaseDatatype canonical, @Nonnull Object fhirResourceType )
    {
        final FhirToDhisTransformerContext context = getScriptVariable( ScriptVariable.CONTEXT.getVariableName(), FhirToDhisTransformerContext.class );

        return getCanonicalAdapterReference( context, canonical, fhirResourceType );
    }

    public Reference getCanonicalAdapterReference( @Nonnull FhirToDhisTransformerContext context, @Nullable IBaseDatatype canonical, @Nonnull Object fhirResourceType )
    {
        if ( canonical == null )
        {
            return null;
        }

        final FhirResourceType resourceType;
        try
        {
            resourceType = NameUtils.toEnumValue( FhirResourceType.class, fhirResourceType );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerScriptException( "Invalid FHIR resource type: " + fhirResourceType, e );
        }

        final String canonicalUri = getCanonicalString( canonical, resourceType );

        if ( canonicalUri == null )
        {
            return null;
        }

        final IIdType canonicalId;
        try
        {
            canonicalId = FhirUriUtils.createIdFromUri( canonicalUri, null );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerDataException( e.getMessage(), e );
        }

        return getAdapterReference( context, createReference( canonicalId.getResourceType(), canonicalId.getIdPart() ), fhirResourceType );
    }

    @Nullable
    @ScriptExecutionRequired
    @ScriptMethod( description = "Returns the adapter reference by ID, code or name for the specified FHIR reference (if this can be resolved).",
        args = {
            @ScriptMethodArg( value = "reference", description = "The FHIR reference for which the adapter reference should be returned." ),
            @ScriptMethodArg( value = "fhirResourceType", description = "The FHIR resource type of the resource." )
        },
        returnDescription = "The adapter reference for the specified reference or null if it cannot be resolved." )
    public Reference getAdapterReference( @Nullable IBaseReference reference, @Nonnull Object fhirResourceType )
    {
        final FhirToDhisTransformerContext context = getScriptVariable( ScriptVariable.CONTEXT.getVariableName(), FhirToDhisTransformerContext.class );

        return getAdapterReference( context, reference, fhirResourceType );
    }

    public Reference getAdapterReference( @Nonnull FhirToDhisTransformerContext context, @Nullable IBaseReference reference, @Nonnull Object fhirResourceType )
    {
        final IBaseReference identifiedReference = getIdentifiedReference( context, reference, fhirResourceType );
        Reference adapterReference = null;

        if ( identifiedReference != null )
        {
            final FhirResourceType resourceType;
            try
            {
                resourceType = NameUtils.toEnumValue( FhirResourceType.class, fhirResourceType );
            }
            catch ( IllegalArgumentException e )
            {
                throw new TransformerScriptException( "Invalid FHIR resource type: " + fhirResourceType, e );
            }

            if ( identifiedReference.getReferenceElement().hasIdPart() && !identifiedReference.getReferenceElement().isLocal() &&
                ( resourceType.isSyncDhisId() || context.getFhirRequest().isDhisFhirId() ) )
            {
                final String dhisId = context.extractDhisId( identifiedReference.getReferenceElement().getIdPart() );

                if ( dhisId != null )
                {
                    adapterReference = new Reference( dhisId, ReferenceType.ID );
                }
            }
            else
            {
                for ( final SystemCodeValue systemCodeValue : getIdentifiers( identifiedReference ).getSystemCodeValues() )
                {
                    if ( System.DHIS2_FHIR_IDENTIFIER_URI.equals( systemCodeValue.getSystem() ) )
                    {
                        adapterReference = new Reference( Objects.requireNonNull( context.extractDhisId( systemCodeValue.getCode() ) ), ReferenceType.ID );

                        // has highest precedence
                        break;
                    }
                    else
                    {
                        adapterReference = new Reference( systemCodeValue.getCode(), ReferenceType.CODE );
                    }
                }
            }
        }

        return adapterReference;
    }

    @Nullable
    @ScriptExecutionRequired
    @ScriptMethod( description = "Returns the adapter reference by ID, code or name for the specified FHIR reference (if this can be resolved).",
        args = {
            @ScriptMethodArg( value = "resource", description = "The FHIR resource for which the adapter reference should be returned." ),
            @ScriptMethodArg( value = "resourceType", description = "The FHIR resource type of the resource." )
        },
        returnDescription = "The adapter reference for the specified reference or null if it cannot be resolved." )
    public Reference getResourceAdapterReference( @Nullable IBaseResource resource, @Nonnull Object resourceType )
    {
        final FhirToDhisTransformerContext context = getScriptVariable(
            ScriptVariable.CONTEXT.getVariableName(), FhirToDhisTransformerContext.class );

        return getResourceAdapterReference( context, resource, resourceType );
    }

    public Reference getResourceAdapterReference( @Nonnull FhirToDhisTransformerContext context, @Nullable IBaseResource resource, @Nonnull Object resourceType )
    {
        if ( resource == null )
        {
            return null;
        }

        Reference adapterReference = null;

        if ( resource.getIdElement().hasIdPart() && !resource.getIdElement().isLocal() && context.getFhirRequest().isDhisFhirId() )
        {
            final String dhisId = context.extractDhisId( resource.getIdElement().getIdPart() );

            if ( dhisId != null )
            {
                adapterReference = new Reference( dhisId, ReferenceType.ID );
            }
        }
        else
        {
            for ( final SystemCodeValue systemCodeValue : getIdentifiers( resource ).getSystemCodeValues() )
            {
                if ( System.DHIS2_FHIR_IDENTIFIER_URI.equals( systemCodeValue.getSystem() ) )
                {
                    adapterReference = new Reference( Objects.requireNonNull( context.extractDhisId( systemCodeValue.getCode() ) ), ReferenceType.ID );

                    // has highest precedence
                    break;
                }
                else
                {
                    adapterReference = new Reference( systemCodeValue.getCode(), ReferenceType.CODE );
                }
            }
        }

        return adapterReference;
    }

    @Nullable
    @ScriptMethod( description = "Resolves the enumeration value for a specific property path of an object.", returnDescription = "The resolved enumeration value.",
        args = {
            @ScriptMethodArg( value = "object", description = "The object that has the enumeration property at the specified property path." ),
            @ScriptMethodArg( value = "propertyPath", description = "The property path on the specified object that contains the enum property." ),
            @ScriptMethodArg( value = "enumValueName", description = "The name of the enumeration value for which the enumeration value should be resolved." )
        } )
    public <T extends Enum<T>> T resolveEnumValue( @Nonnull Object object, @Nonnull String propertyPath, @Nullable Object enumValueName )
    {
        return EnumValueUtils.resolveEnumValue( object, propertyPath, enumValueName );
    }

    @ScriptMethod( description = "Returns if the specified resource contains the extension with the specified URL.", returnDescription = "If the specified resource contains the extension with the specified URL.",
        args = {
            @ScriptMethodArg( value = "resource", description = "The resource that should be checked." ),
            @ScriptMethodArg( value = "url", description = "The URL that should be checked." )
        } )
    public boolean hasExtension( @Nonnull IBaseHasExtensions resource, @Nonnull String url )
    {
        return resource.getExtension().stream().anyMatch( e -> url.equals( e.getUrl() ) );
    }

    @Nullable
    @ScriptMethod( description = "Returns the value of the extension with the specified URL from the specified resource.", returnDescription = "Returns the value of the extension or null if the resource has no such extension.",
        args = {
            @ScriptMethodArg( value = "resource", description = "The resource from which the value should be returned." ),
            @ScriptMethodArg( value = "url", description = "The URL for which the extension value should be returned." )
        } )
    public IBaseDatatype getExtensionValue( @Nonnull IBaseHasExtensions resource, @Nonnull String url )
    {
        return resource.getExtension().stream().filter( e -> url.equals( e.getUrl() ) ).findFirst().map( IBaseExtension::getValue ).orElse( null );
    }

    @Nonnull
    protected abstract IBaseReference createReference( @Nullable IIdType id );

    @Nonnull
    protected abstract SystemCodeValues getIdentifiers( @Nonnull IBaseReference baseReference );

    protected abstract boolean addIdentifier( @Nonnull IBaseReference reference, @Nonnull SystemCodeValue identifier );

    @Nonnull
    protected abstract SystemCodeValues getIdentifiers( @Nonnull IBaseResource baseResource );

    protected abstract boolean addIdentifiers( @Nonnull IBaseResource resource, @Nonnull SystemCodeValues identifiers );

    @Nullable
    protected abstract String getCanonicalString( @Nonnull IBaseDatatype canonicalReference, @Nonnull FhirResourceType defaultResourceType );

    @Nonnull
    protected abstract IBaseReference createReference( @Nonnull String type, @Nonnull String id );
}
