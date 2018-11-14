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
import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptVariable;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.script.ScriptArgUtils;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionRequired;
import org.dhis2.fhir.adapter.fhir.transform.FhirToDhisTransformerContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.impl.TransformerScriptException;
import org.dhis2.fhir.adapter.fhir.transform.model.ResourceSystem;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Scriptable
public abstract class AbstractCodeFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    private static final String SCRIPT_ATTR_NAME = "codeUtils";

    private static final List<String> GET_CODE_METHOD_NAMES = Collections.unmodifiableList( Arrays.asList( "getCode", "getVaccineCode" ) );

    private final CodeRepository codeRepository;

    private final SystemCodeRepository systemCodeRepository;

    private volatile Map<Class<? extends IDomainResource>, Method> codeMethods = new HashMap<>();

    protected AbstractCodeFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull CodeRepository codeRepository, @Nonnull SystemCodeRepository systemCodeRepository )
    {
        super( scriptExecutionContext );
        this.codeRepository = codeRepository;
        this.systemCodeRepository = systemCodeRepository;
    }

    @Nonnull
    protected SystemCodeRepository getSystemCodeRepository()
    {
        return systemCodeRepository;
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nonnull
    public abstract List<SystemCodeValue> getSystemCodeValues( @Nullable ICompositeType codeableConcept );

    @Nullable
    public abstract String getCode( @Nullable ICompositeType codeableConcept, @Nullable String system );

    public abstract boolean containsMappingCode( @Nullable ICompositeType codeableConcept, @Nullable Object mappingCodes );

    public abstract boolean containsCode( @Nullable ICompositeType codeableConcept, @Nonnull String system, @Nonnull String code );

    @Nullable
    protected abstract List<SystemCodeValue> getSystemCodeValues( @Nonnull IDomainResource domainResource, @Nonnull Method identifierMethod );

    public boolean containsAnyCode( @Nullable ICompositeType codeableConcept, @Nullable Collection<SystemCodeValue> systemCodeValues )
    {
        if ( (codeableConcept == null) || (systemCodeValues == null) )
        {
            return false;
        }
        return systemCodeValues.stream().anyMatch( scv -> containsCode( codeableConcept, scv.getSystem(), scv.getCode() ) );
    }

    @Nonnull
    public Map<String, List<SystemCodeValue>> getSystemCodeValuesByCodes( @Nullable Object[] codes )
    {
        if ( (codes == null) || (codes.length == 0) )
        {
            return Collections.emptyMap();
        }

        final List<String> convertedCodes = ScriptArgUtils.extractStringArray( codes );
        if ( CollectionUtils.isEmpty( convertedCodes ) )
        {
            return Collections.emptyMap();
        }

        // map must be returned ordered
        final Map<String, List<SystemCodeValue>> result = new LinkedHashMap<>();
        convertedCodes.forEach( c -> result.put( c, new ArrayList<>() ) );

        final Collection<SystemCode> systemCodes = getSystemCodeRepository().findAllByCodes( convertedCodes );
        for ( final SystemCode systemCode : systemCodes )
        {
            result.computeIfPresent( systemCode.getCode().getCode(), ( k, v ) -> {
                v.add( systemCode.getCalculatedSystemCodeValue() );
                return v;
            } );
        }
        return result;
    }

    @Nullable
    public List<SystemCodeValue> getResourceCodes( @Nullable IBaseResource baseResource ) throws TransformerException
    {
        if ( baseResource == null )
        {
            throw new TransformerMappingException( "Cannot get codes of undefined domain resource." );
        }
        if ( !(baseResource instanceof IDomainResource) )
        {
            return null;
        }

        final IDomainResource domainResource = (IDomainResource) baseResource;
        final Method method = getCodeMethod( domainResource );
        if ( method != null )
        {
            return getSystemCodeValues( domainResource, method );
        }
        return null;
    }

    @ScriptExecutionRequired
    public String getMappedCode( @Nullable String code, @Nonnull Object fhirResourceType )
    {
        if ( code == null )
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

        final Code c = codeRepository.findAllBySystemCodes( Collections.singleton( new SystemCodeValue( resourceSystem.getSystem(), code ).toString() ) )
            .stream().findFirst().orElse( null );
        if ( c == null )
        {
            return null;
        }

        return (c.getMappedCode() == null) ? c.getCode() : c.getMappedCode();
    }

    @Nullable
    private Method getCodeMethod( @Nonnull IDomainResource domainResource )
    {
        final Class<? extends IDomainResource> domainResourceClass = domainResource.getClass();
        final Map<Class<? extends IDomainResource>, Method> codeMethods = this.codeMethods;
        if ( codeMethods.containsKey( domainResourceClass ) )
        {
            return codeMethods.get( domainResourceClass );
        }

        Method method = null;
        for ( final String methodName : GET_CODE_METHOD_NAMES )
        {
            method = ReflectionUtils.findMethod( domainResource.getClass(), methodName );
            if ( method != null )
            {
                break;
            }
        }
        final Map<Class<? extends IDomainResource>, Method> copiedCodeMethods = new HashMap<>( codeMethods );
        copiedCodeMethods.put( domainResourceClass, method );
        this.codeMethods = copiedCodeMethods;

        return method;
    }
}
