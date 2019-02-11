package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.dstu3;

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

import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractObservationFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.instance.model.api.IBaseBackboneElement;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

/**
 * FHIR version DSTU3 implementation of {@link AbstractObservationFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
@Component
@Scriptable
public class Dstu3ObservationFhirToDhisTransformerUtils extends AbstractObservationFhirToDhisTransformerUtils
{
    public Dstu3ObservationFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull Dstu3FhirClientFhirToDhisTransformerUtils clientTransformUtils,
        @Nonnull Dstu3CodeFhirToDhisTransformerUtils codeTransformerUtils )
    {
        super( scriptExecutionContext, clientTransformUtils, codeTransformerUtils );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.DSTU3_ONLY;
    }

    @Nonnull
    @Override
    public String getResourceName()
    {
        return ResourceType.Observation.name();
    }

    @Nullable
    @Override
    public ICompositeType getCodes( @Nullable IBaseResource resource )
    {
        return (resource == null) ? null : ((Observation) resource).getCode();
    }

    @Nullable
    @Override
    public String getComponentText( @Nullable IBaseResource resource )
    {
        final Observation observation = (Observation) resource;
        if ( (observation == null) || !observation.hasComponent() )
        {
            return null;
        }

        final StringBuilder text = new StringBuilder();
        for ( final ObservationComponentComponent component : observation.getComponent() )
        {
            if ( component.hasValueStringType() )
            {
                if ( text.length() > 0 )
                {
                    text.append( COMPONENT_SEPARATOR );
                }
                text.append( component.getValue().toString() );
            }
        }
        return (text.length() == 0) ? null : text.toString();
    }

    @Override
    @Nullable
    public IBaseBackboneElement getBackboneElement( @Nullable List<? extends IBaseBackboneElement> backboneElements, @Nonnull String system, @Nonnull String code )
    {
        if ( backboneElements == null )
        {
            return null;
        }
        return backboneElements.stream().map( ObservationComponentComponent.class::cast )
            .filter( c -> getCodeTransformerUtils().containsCode( c.getCode(), system, code ) )
            .findFirst().orElse( new ObservationComponentComponent() );
    }
}
