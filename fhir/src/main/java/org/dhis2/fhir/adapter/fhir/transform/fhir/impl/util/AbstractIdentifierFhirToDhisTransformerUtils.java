package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionRequired;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.scripted.TransformerScriptException;
import org.dhis2.fhir.adapter.fhir.transform.util.FhirIdentifierUtils;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IDomainResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * FHIR to DHIS2 transformer utility methods for FHIR identifiers.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "IdentifierUtils", transformType = ScriptTransformType.IMP, var = AbstractIdentifierFhirToDhisTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities to handle FHIR to DHIS2 transformations of FHIR identifiers." )
public abstract class AbstractIdentifierFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "identifierUtils";

    private volatile Map<Class<? extends IDomainResource>, Method> identifierMethods = new HashMap<>();

    private final FhirIdentifierUtils fhirIdentifierUtils;

    private final ReferenceFhirToDhisTransformerUtils referenceFhirToDhisTransformerUtils;

    protected AbstractIdentifierFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull FhirIdentifierUtils fhirIdentifierUtils,
        @Nonnull ReferenceFhirToDhisTransformerUtils referenceFhirToDhisTransformerUtils )
    {
        super( scriptExecutionContext );
        this.fhirIdentifierUtils = fhirIdentifierUtils;
        this.referenceFhirToDhisTransformerUtils = referenceFhirToDhisTransformerUtils;
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nullable
    @ScriptExecutionRequired
    @ScriptMethod( description = "Returns the business identifier from the specified referenced FHIR resource with the specified FHIR resource type. If the resource type does not match null is returned. " +
        "The system URI of the corresponding remote subscription system of the current transformation context is used (e.g. the system URI of a Patient of the remote subscription from which the resource has been retrieved).",
        args = {
            @ScriptMethodArg( value = "reference", description = "The FHIR reference of a domain resource from which the identifier should be extracted." ),
            @ScriptMethodArg( value = "fhirResourceType", description = "The FHIR resource type (upper case with underscores, e.g. RELATED_PERSON) of the specified domain resource." )
        },
        returnDescription = "The corresponding business identifier." )
    public String getReferenceIdentifier( @Nullable IBaseReference reference, @Nonnull Object fhirResourceType ) throws TransformerException
    {
        if ( (reference == null) || reference.isEmpty() )
        {
            return null;
        }
        referenceFhirToDhisTransformerUtils.initReference( reference, fhirResourceType );
        if ( reference.getResource() instanceof IDomainResource )
        {
            return getResourceIdentifier( (IDomainResource) reference.getResource(), fhirResourceType );
        }
        return null;
    }

    @Nullable
    @ScriptMethod( description = "Returns the business identifier with the specified system URI from the referenced FHIR resource with the specified FHIR resource type. If the resource type does not match null is returned.",
        args = {
            @ScriptMethodArg( value = "reference", description = "The FHIR reference of a domain resource from which the identifier should be extracted." ),
            @ScriptMethodArg( value = "fhirResourceType", description = "The FHIR resource type (upper case with underscores, e.g. RELATED_PERSON) of the specified domain resource." ),
            @ScriptMethodArg( value = "system", description = "The system URI for which the identifier is returned." )
        },
        returnDescription = "The corresponding business identifier." )
    public String getReferenceIdentifier( @Nullable IBaseReference reference, @Nonnull Object fhirResourceType, @Nullable String system ) throws TransformerException
    {
        if ( (reference == null) || reference.isEmpty() )
        {
            return null;
        }
        referenceFhirToDhisTransformerUtils.initReference( reference, fhirResourceType );
        if ( reference.getResource() instanceof IDomainResource )
        {
            return getResourceIdentifier( (IDomainResource) reference.getResource(), fhirResourceType, system );
        }
        return null;
    }

    @Nullable
    @ScriptExecutionRequired
    @ScriptMethod( description = "Returns the business identifier  from the specified domain resource with the specified FHIR resource type. If the resource type does not match null is returned. " +
        "The system URI of the corresponding remote subscription system of the current transformation context is used (e.g. the system URI of a Patient of the remote subscription from which the resource has been retrieved).",
        args = {
            @ScriptMethodArg( value = "resource", description = "The FHIR domain resource from which the identifier should be extracted." ),
            @ScriptMethodArg( value = "fhirResourceType", description = "The FHIR resource type (upper case with underscores, e.g. RELATED_PERSON) of the specified domain resource." )
        },
        returnDescription = "The corresponding business identifier." )
    public String getResourceIdentifier( @Nullable IDomainResource resource, @Nonnull Object fhirResourceType ) throws TransformerException
    {
        if ( resource == null )
        {
            return null;
        }
        final FhirResourceType resourceType;
        try
        {
            resourceType = FhirResourceType.valueOf( fhirResourceType.toString() );
        }
        catch ( IllegalArgumentException e )
        {
            throw new TransformerScriptException( "Invalid FHIR resource type: " + fhirResourceType, e );
        }
        final FhirToDhisTransformerContext context = getScriptVariable( ScriptVariable.CONTEXT.getVariableName(), FhirToDhisTransformerContext.class );
        final ResourceSystem resourceSystem = context.getFhirRequest().getOptionalResourceSystem( resourceType )
            .orElseThrow( () -> new TransformerMappingException( "No system has been defined for resource type " + resourceType + "." ) );
        return getResourceIdentifier( resource, fhirResourceType, resourceSystem.getSystem() );
    }

    @Nullable
    @ScriptMethod( description = "Returns the business identifier with the specified system URI from the specified domain resource with the specified FHIR resource type. If the resource type does not match null is returned.",
        args = {
            @ScriptMethodArg( value = "resource", description = "The FHIR domain resource from which the identifier should be extracted." ),
            @ScriptMethodArg( value = "fhirResourceType", description = "The FHIR resource type (upper case with underscores, e.g. RELATED_PERSON) of the specified domain resource." ),
            @ScriptMethodArg( value = "system", description = "The system URI for which the identifier is returned." )
        },
        returnDescription = "The corresponding business identifier." )
    public String getResourceIdentifier( @Nullable IDomainResource resource, @Nonnull Object fhirResourceType, @Nullable String system ) throws TransformerException
    {
        if ( resource == null )
        {
            throw new TransformerMappingException( "Cannot get identifier of undefined domain resource." );
        }

        if ( !Objects.equals( String.valueOf( FhirResourceType.getByResource( resource ) ), String.valueOf( fhirResourceType ) ) )
        {
            return null;
        }

        final Method method = fhirIdentifierUtils.getIdentifierMethod( resource );
        if ( method != null )
        {
            return getIdentifierValue( resource, method, system );
        }

        return null;
    }

    @Nullable
    protected abstract String getIdentifierValue( @Nonnull IDomainResource domainResource, @Nonnull Method identifierMethod, @Nullable String system );
}
