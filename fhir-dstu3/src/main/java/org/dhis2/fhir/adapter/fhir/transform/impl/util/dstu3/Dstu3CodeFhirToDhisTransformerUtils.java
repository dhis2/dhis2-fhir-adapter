package org.dhis2.fhir.adapter.fhir.transform.impl.util.dstu3;

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

import com.google.common.collect.Sets;
import org.dhis2.fhir.adapter.Scriptable;
import org.dhis2.fhir.adapter.fhir.metadata.model.SystemCode;
import org.dhis2.fhir.adapter.fhir.metadata.repository.SystemCodeRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.dhis2.fhir.adapter.fhir.script.ScriptArgUtils;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.impl.util.AbstractCodeFhirToDhisTransformerUtils;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.instance.model.api.IDomainResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Scriptable
public class Dstu3CodeFhirToDhisTransformerUtils extends AbstractCodeFhirToDhisTransformerUtils
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final SystemCodeRepository systemCodeRepository;

    public Dstu3CodeFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull SystemCodeRepository systemCodeRepository )
    {
        super( scriptExecutionContext );
        this.systemCodeRepository = systemCodeRepository;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.DSTU3_ONLY;
    }

    @Nonnull
    @Override
    public List<SystemCodeValue> getSystemCodeValues( @Nullable ICompositeType codeableConcept )
    {
        if ( codeableConcept == null )
        {
            return Collections.emptyList();
        }
        final List<SystemCodeValue> result = new ArrayList<>();
        for ( final Coding coding : ((CodeableConcept) codeableConcept).getCoding() )
        {
            result.add( new SystemCodeValue( coding.getSystem(), coding.getCode() ) );
        }
        return result;
    }

    @Override
    @Nullable
    public String getCode( @Nullable ICompositeType codeableConcept, @Nullable String system )
    {
        if ( system == null )
        {
            throw new IllegalArgumentException( "System must be specified." );
        }

        if ( codeableConcept == null )
        {
            return null;
        }
        for ( final Coding coding : ((CodeableConcept) codeableConcept).getCoding() )
        {
            if ( system.equals( coding.getSystem() ) )
            {
                return coding.getCode();
            }
        }
        return null;
    }

    @Override
    public boolean containsMappedCode( @Nullable ICompositeType codeableConcept, @Nullable Object mappedCodes )
    {
        if ( mappedCodes == null )
        {
            throw new IllegalArgumentException( "Mapped codes must be specified." );
        }

        if ( codeableConcept == null )
        {
            return false;
        }
        final List<String> convertedMappedCodes = ScriptArgUtils.extractStringArray( mappedCodes );
        if ( CollectionUtils.isEmpty( convertedMappedCodes ) )
        {
            return false;
        }

        final Set<String> checkedCodes = new HashSet<>();
        final Collection<SystemCode> systemCodes = systemCodeRepository.findAllByCodes( convertedMappedCodes );
        for ( final SystemCode systemCode : systemCodes )
        {
            if ( containsCode( codeableConcept, systemCode.getSystem().getSystemUri(), systemCode.getSystemCode() ) )
            {
                return true;
            }
            checkedCodes.add( systemCode.getCode().getCode() );
        }
        if ( logger.isDebugEnabled() && checkedCodes.size() < convertedMappedCodes.size() )
        {
            logger.info( "Mapped codes have not been defined: " + Sets.difference( new HashSet<>( convertedMappedCodes ), checkedCodes ) );
        }
        return false;
    }

    @Override
    public boolean containsCode( @Nullable ICompositeType codeableConcept, @Nonnull String system, @Nonnull String code )
    {
        if ( codeableConcept == null )
        {
            return false;
        }
        return ((CodeableConcept) codeableConcept).getCoding().stream().anyMatch(
            coding -> system.equals( coding.getSystem() ) && code.equals( coding.getCode() ) );
    }

    @Nullable
    @Override
    protected List<SystemCodeValue> getSystemCodeValues( @Nonnull IDomainResource domainResource, @Nonnull Method identifierMethod )
    {
        final ICompositeType codeableConcept = (ICompositeType) ReflectionUtils.invokeMethod( identifierMethod, domainResource );
        if ( codeableConcept != null )
        {
            return getSystemCodeValues( codeableConcept );
        }
        return null;
    }
}
