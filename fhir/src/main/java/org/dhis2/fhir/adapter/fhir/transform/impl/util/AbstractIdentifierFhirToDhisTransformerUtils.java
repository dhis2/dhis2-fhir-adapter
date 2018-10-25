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

import org.dhis2.fhir.adapter.Scriptable;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionRequired;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.impl.TransformerScriptException;
import org.dhis2.fhir.adapter.fhir.transform.model.ResourceSystem;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Scriptable
public abstract class AbstractIdentifierFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    private static final String SCRIPT_ATTR_NAME = "identifierUtils";

    private volatile Map<Class<? extends IDomainResource>, Method> identifierMethods = new HashMap<>();

    private final ReferenceTransformUtils referenceTransformUtils;

    protected AbstractIdentifierFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ReferenceTransformUtils referenceTransformUtils )
    {
        super( scriptExecutionContext );
        this.referenceTransformUtils = referenceTransformUtils;
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nullable
    protected abstract String getIdentifierValue( @Nonnull IDomainResource domainResource, @Nonnull Method identifierMethod, @Nullable String system );

    @Nullable
    @ScriptExecutionRequired
    public String getReferenceIdentifier( @Nullable IBaseReference reference, @Nonnull Object fhirResourceType ) throws TransformerException
    {
        if ( reference == null )
        {
            return null;
        }
        referenceTransformUtils.initReference( reference );
        if ( reference.getResource() instanceof IDomainResource )
        {
            return getResourceIdentifier( (IDomainResource) reference.getResource(), fhirResourceType );
        }
        return null;
    }


    @Nullable
    public String getReferenceIdentifier( @Nullable IBaseReference reference, @Nonnull Object fhirResourceType, @Nullable String system ) throws TransformerException
    {
        if ( reference == null )
        {
            return null;
        }
        referenceTransformUtils.initReference( reference );
        if ( reference.getResource() instanceof IDomainResource )
        {
            return getResourceIdentifier( (IDomainResource) reference.getResource(), fhirResourceType, system );
        }
        return null;
    }

    @Nullable
    @ScriptExecutionRequired
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
    public String getResourceIdentifier( @Nullable IDomainResource domainResource, @Nonnull Object fhirResourceType, @Nullable String system ) throws TransformerException
    {
        if ( domainResource == null )
        {
            throw new TransformerMappingException( "Cannot get identifier of undefined domain resource." );
        }

        if ( !Objects.equals( String.valueOf( FhirResourceType.getByResource( domainResource ) ), String.valueOf( fhirResourceType ) ) )
        {
            return null;
        }

        final Method method = getIdentifierMethod( domainResource );
        if ( method != null )
        {
            return getIdentifierValue( domainResource, method, system );
        }

        return null;
    }

    @Nullable
    private Method getIdentifierMethod( @Nonnull IDomainResource domainResource )
    {
        final Class<? extends IDomainResource> domainResourceClass = domainResource.getClass();
        final Map<Class<? extends IDomainResource>, Method> identifierMethods = this.identifierMethods;
        if ( identifierMethods.containsKey( domainResourceClass ) )
        {
            return identifierMethods.get( domainResourceClass );
        }

        final Method method = ReflectionUtils.findMethod( domainResource.getClass(), "getIdentifier" );
        final Map<Class<? extends IDomainResource>, Method> copiedIdentifierMethods = new HashMap<>( identifierMethods );
        copiedIdentifierMethods.put( domainResourceClass, method );
        this.identifierMethods = copiedIdentifierMethods;

        return method;
    }
}
