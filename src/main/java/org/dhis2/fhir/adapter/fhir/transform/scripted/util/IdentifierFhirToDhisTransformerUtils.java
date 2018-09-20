package org.dhis2.fhir.adapter.fhir.transform.scripted.util;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.dhis2.fhir.adapter.Scriptable;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.transform.model.ResourceSystem;
import org.dhis2.fhir.adapter.fhir.transform.scripted.TransformerScriptException;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@Scriptable
public class IdentifierFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    private static final String SCRIPT_ATTR_NAME = "identifierUtils";

    private volatile Map<Class<? extends DomainResource>, Method> identifierMethods = new HashMap<>();

    public IdentifierFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nullable
    public String getReferenceIdentifier( @Nullable IBaseReference reference, @Nonnull Object fhirResourceType ) throws TransformerException
    {
        if ( reference == null )
        {
            return null;
        }
        if ( reference.getResource() instanceof DomainResource )
        {
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
            final ResourceSystem resourceSystem = context.getFhirRequest().getOptionalRemoteResourceSystem( resourceType )
                .orElseThrow( () -> new TransformerDataException( "No system has been defined for resource type " + resourceType + "." ) );
            return getResourceIdentifier( (DomainResource) reference.getResource(), fhirResourceType, resourceSystem.getSystem() );
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
        if ( reference.getResource() instanceof DomainResource )
        {
            return getResourceIdentifier( (DomainResource) reference.getResource(), fhirResourceType, system );
        }
        return null;
    }

    @Nullable
    public String getResourceIdentifier( @Nullable DomainResource domainResource, @Nonnull Object fhirResourceType, @Nullable String system ) throws TransformerException
    {
        if ( domainResource == null )
        {
            throw new TransformerMappingException( "Cannot get identifier of undefined domain resource." );
        }

        if ( !Objects.equals( String.valueOf( FhirResourceType.getByPath( domainResource.getResourceType().getPath() ) ), String.valueOf( fhirResourceType ) ) )
        {
            return null;
        }

        final Method method = getIdentifierMethod( domainResource );
        if ( method != null )
        {
            @SuppressWarnings( "unchecked" ) final List<Identifier> identifiers = (List<Identifier>) ReflectionUtils.invokeMethod( method, domainResource );
            if ( identifiers != null )
            {
                return identifiers.stream().filter( i -> Objects.equals( system, i.getSystem() ) ).findFirst().orElse( new Identifier() ).getValue();
            }
        }

        return null;
    }

    @Nullable
    private Method getIdentifierMethod( @Nonnull DomainResource domainResource )
    {
        final Class<? extends DomainResource> domainResourceClass = domainResource.getClass();
        final Map<Class<? extends DomainResource>, Method> identifierMethods = this.identifierMethods;
        if ( identifierMethods.containsKey( domainResourceClass ) )
        {
            return identifierMethods.get( domainResourceClass );
        }

        final Method method = ReflectionUtils.findMethod( domainResource.getClass(), "getIdentifier" );
        final Map<Class<? extends DomainResource>, Method> copiedIdentifierMethods = new HashMap<>( identifierMethods );
        copiedIdentifierMethods.put( domainResourceClass, method );
        this.identifierMethods = copiedIdentifierMethods;

        return method;
    }
}
