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

import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArgUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.repository.CodeRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionRequired;
import org.dhis2.fhir.adapter.fhir.transform.TransformerException;
import org.dhis2.fhir.adapter.fhir.transform.TransformerMappingException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.model.ResourceSystem;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
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
import java.util.stream.Collectors;

/**
 * FHIR to DHIS2 transformer utility methods for code mappings and FHIR codeable concepts.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "CodeUtils", transformType = ScriptTransformType.IMP, var = AbstractCodeFhirToDhisTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities to handle FHIR to DHIS2 transformations for codes (either code mappings or extracting of codes from FHIR codeable concepts)." )
public abstract class AbstractCodeFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "codeUtils";

    private static final List<String> GET_CODE_METHOD_NAMES = Collections.unmodifiableList( Arrays.asList( "getCode", "getVaccineCode", "getMedicationCodeableConcept" ) );

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
    @ScriptMethod( description = "Returns all system code values that are included in the specified FHIR codeable concept.",
        args = @ScriptMethodArg( value = "codeableConcept", description = "The FHIR codeable concept from which the system code values should be extracted." ),
        returnDescription = "Returns a list that contains all corresponding system codes values (type SystemCodeValue)." )
    public abstract List<SystemCodeValue> getSystemCodeValues( @Nullable ICompositeType codeableConcept );

    @Nullable
    @ScriptMethod( description = "Returns the code that is included in the specified codeable concept for the specified system URI.",
        args = {
            @ScriptMethodArg( value = "codeableConcept", description = "The codeable concept from which the code should be extracted." ),
            @ScriptMethodArg( value = "system", description = "The system URI to which the returned code must be assigned to." )
        },
        returnDescription = "Returns the corresponding code."
    )
    public abstract String getCode( @Nullable ICompositeType codeableConcept, @Nullable String system );

    @ScriptMethod( description = "Returns if the specified FHIR codeable concept contains any enabled mapping for the specified mapping codes defined for the adapter.",
        args = {
            @ScriptMethodArg( value = "codeableConcept", description = "The codeable concept that should be checked." ),
            @ScriptMethodArg( value = "mappingCodes", description = "Array of mapping codes that have been defined for the adapter (field code of adapter resource codes)." )
        },
        returnDescription = "Returns if the mapping code is included."
    )
    public abstract boolean containsMappingCode( @Nullable ICompositeType codeableConcept, @Nullable Object mappingCodes );

    @ScriptMethod( description = "Returns if the specified codeable concept contains any combination of the specified system URI and the specified code.",
        args = {
            @ScriptMethodArg( value = "codeableConcept", description = "The codeable concept that should be checked." ),
            @ScriptMethodArg( value = "system", description = "The system URI that must be included in combination with the specified code." ),
            @ScriptMethodArg( value = "code", description = "The code that must be included in combination with the specified system URI." ),
        },
        returnDescription = "Returns if the code is included." )
    public abstract boolean containsCode( @Nullable ICompositeType codeableConcept, @Nullable String system, @Nonnull String code );

    @SuppressWarnings( "unchecked" )
    @ScriptMethod( description = "Returns if the specified codeable concept contains any combination of the specified list of system code values (type SystemCodeValue).",
        args = {
            @ScriptMethodArg( value = "codeableConcept", description = "The codeable concept that should be checked." ),
            @ScriptMethodArg( value = "systemCodeValues", description = "List of system code values that should be used for the check (type SystemCodeValue)." ),
        },
        returnDescription = "Returns if any system code value is included." )
    public boolean containsAnyCode( @Nullable Object codeableConcept, @Nullable Collection<SystemCodeValue> systemCodeValues )
    {
        if ( (codeableConcept == null) || (systemCodeValues == null) )
        {
            return false;
        }
        final Collection<? extends ICompositeType> codeableConcepts;
        if ( codeableConcept instanceof ICompositeType )
        {
            codeableConcepts = Collections.singletonList( (ICompositeType) codeableConcept );
        }
        else if ( codeableConcept instanceof Collection )
        {
            codeableConcepts = (Collection<? extends ICompositeType>) codeableConcept;
        }
        else
        {
            throw new IllegalArgumentException( "Expected composite type: " + codeableConcept.getClass() );
        }
        return systemCodeValues.stream().anyMatch( scv -> codeableConcepts.stream().anyMatch( cc -> containsCode( cc, scv.getSystem(), scv.getCode() ) ) );
    }

    @ScriptMethod( description = "Returns a map where the key is each specified mapping code and the value is a list of system code values (type SystemCodeValue). " +
        "The mapping codes are included in field code of adapter resource codes. All specified mapping codes are returned (even if there is no corresponding system code value available).",
        args = @ScriptMethodArg( value = "mappingCodes", description = "The mapping codes for which the system code values should be returned." ),
        returnDescription = "Returns a map where the key is each specified mapping code and the value is a list of system code values (type SystemCodeValue)." )
    @Nonnull
    public Map<String, List<SystemCodeValue>> getSystemCodeValuesByMappingCodes( @Nullable Object mappingCodes )
    {
        if ( mappingCodes == null )
        {
            return Collections.emptyMap();
        }
        final List<String> convertedCodes = ScriptArgUtils.extractStringArray( mappingCodes );
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

    @ScriptMethod( description = "Extracts all system code values (type SystemCodeValue) for the codes that identify the purpose of the specified FHIR resource (e.g. observation or vaccine codes).",
        args = {
            @ScriptMethodArg( value = "resource", description = "The FHIR resource from which the codes should be returned." ),
        },
        returnDescription = "Returns a list of system code values (type SystemCodeValue)." )
    @Nullable
    public List<SystemCodeValue> getResourceCodes( @Nullable IBaseResource resource ) throws TransformerException
    {
        if ( resource == null )
        {
            throw new TransformerMappingException( "Cannot get codes of undefined domain resource." );
        }
        if ( !(resource instanceof IDomainResource) )
        {
            return null;
        }

        final IDomainResource domainResource = (IDomainResource) resource;
        final Method method = getCodeMethod( domainResource );
        if ( method != null )
        {
            return getSystemCodeValues( domainResource, method );
        }
        return null;
    }

    @ScriptExecutionRequired
    @ScriptMethod( description = "Returns the mapped code (field code or if available field mappedCode of adapter resource code) for the specified FHIR resource type dependent code. " +
        "As system URI the value associated with the FHIR resource type of the FHIR client system (adapter resource fhirClientSystems) of the current transformation context is used.",
        args = {
            @ScriptMethodArg( value = "code", description = "The code in context of the system URI for the specified FHIR resource type." ),
            @ScriptMethodArg( value = "fhirResourceType", description = "The FHIR resource type (upper case letters, separated by underscores) from which the associated system URI should be taken." ),
        },
        returnDescription = "The associated mapped code." )
    @Nullable
    public String getMappedCode( @Nullable String code, @Nonnull Object fhirResourceType )
    {
        if ( code == null )
        {
            return null;
        }

        final ResourceSystem resourceSystem = getMandatoryResourceSystem( convertFhirResourceType( fhirResourceType ) );
        final Code c = codeRepository.findAllBySystemCodes( Collections.singleton( new SystemCodeValue( resourceSystem.getSystem(), code ).toString() ) )
            .stream().findFirst().orElse( null );
        if ( c == null )
        {
            return null;
        }
        return (c.getMappedCode() == null) ? c.getCode() : c.getMappedCode();
    }

    @ScriptMethod( description = "Returns the mapped value set code for the specified codeable concept.",
        args = {
            @ScriptMethodArg( value = "codeSetCode", description = "The code of the code set to which the codeable concept must belong to." ),
            @ScriptMethodArg( value = "codeableConcept", description = "The codeable concept to which the code is mapped." ),
        },
        returnDescription = "The associated mapped code (may be null)." )
    @Nullable
    public String getMappedValueSetCode( @Nonnull String codeSetCode, @Nullable ICompositeType codeableConcept )
    {
        if ( codeableConcept == null )
        {
            return null;
        }

        final List<SystemCodeValue> systemCodeValues = getSystemCodeValues( codeableConcept );

        if ( systemCodeValues.isEmpty() )
        {
            return null;
        }

        final Code c = codeRepository.findFirstMappedByCodeSetAndSystemCodes( codeSetCode,
            systemCodeValues.stream().map( SystemCodeValue::toString ).collect( Collectors.toList() ) ).orElse( null );

        return c == null ? null : c.getMappedCode();
    }

    @Nullable
    protected abstract List<SystemCodeValue> getSystemCodeValues( @Nonnull IDomainResource domainResource, @Nonnull Method identifierMethod );

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
