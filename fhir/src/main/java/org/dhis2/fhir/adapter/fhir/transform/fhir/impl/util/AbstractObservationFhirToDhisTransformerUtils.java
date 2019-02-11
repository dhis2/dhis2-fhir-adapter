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

import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.IBaseBackboneElement;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.instance.model.api.IIdType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * FHIR to DHIS2 transformer utility methods for observation resource.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "ObservationUtils", transformType = ScriptTransformType.IMP, var = AbstractObservationFhirToDhisTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities for handling FHIR observations." )
public abstract class AbstractObservationFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "observationUtils";

    public static final String COMPONENT_SEPARATOR = "\n";

    private final AbstractFhirClientFhirToDhisTransformerUtils clientTransformUtils;

    private final AbstractCodeFhirToDhisTransformerUtils codeTransformerUtils;

    protected AbstractObservationFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull AbstractFhirClientFhirToDhisTransformerUtils clientTransformUtils, @Nonnull AbstractCodeFhirToDhisTransformerUtils codeTransformerUtils )
    {
        super( scriptExecutionContext );
        this.clientTransformUtils = clientTransformUtils;
        this.codeTransformerUtils = codeTransformerUtils;
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nonnull
    protected AbstractCodeFhirToDhisTransformerUtils getCodeTransformerUtils()
    {
        return codeTransformerUtils;
    }

    @Nonnull
    @ScriptMethod( description = "Returns the name of the observation FHIR resource.",
        returnDescription = "The name of the observation FHIR resource." )
    public abstract String getResourceName();

    @Nullable
    @ScriptMethod( description = "Returns the composite type with the codes that are included in the FHIR observation resource.",
        args = @ScriptMethodArg( value = "resource", description = "The FHIR observation resource from which the codes should be returned." ),
        returnDescription = "The composite type with the codes." )
    public abstract ICompositeType getCodes( @Nullable IBaseResource resource );

    @Nullable
    @ScriptMethod( description = "Returns combined string value component text of the specified FHIR observation resource.",
        args = @ScriptMethodArg( value = "resource", description = "The FHIR observation resource from which the component text should be returned." ),
        returnDescription = "The combined string value component text." )
    public abstract String getComponentText( @Nullable IBaseResource resource );

    @Nullable
    @ScriptMethod( description = "Returns the backbone element with the specified system URI and code from the specified FHIR observation resource.",
        args = {
            @ScriptMethodArg( value = "backboneElements", description = "The list of backbone elements from the FHIR observation resource." ),
            @ScriptMethodArg( value = "system", description = "The system URI for which the backbone element should be returned." ),
            @ScriptMethodArg( value = "code", description = "The code (in combination with the system URI) for which the backbone element should be returned." ),
        },
        returnDescription = "The matching back bone element." )
    public abstract IBaseBackboneElement getBackboneElement( @Nullable List<? extends IBaseBackboneElement> backboneElements,
        @Nonnull String system, @Nonnull String code );

    @Nullable
    @ScriptMethod( description = "Returns the latest FHIR observation resource (based on last updated timestamp of resource) that match the specified criteria. The specified mapped codes are evaluated in the specified order. Even if a found FHIR observation" +
        " resource is older than another found resource, the order of the specified mapped codes is evaluated first. If the older resource matches the first mapped code and the latest resource matches the second mapped code, then the older resource is taken.",
        args = {
            @ScriptMethodArg( value = "referencedResourceParameter", description = "The FHIR resource search parameter that contains the referenced resource (e.g. subject)." ),
            @ScriptMethodArg( value = "referencedResourceType", description = "The FHIR resource type of the referenced resource (e.g. Patient)." ),
            @ScriptMethodArg( value = "referencedResourceId", description = "The FHIR resource ID (ID element) of the reference resource." ),
            @ScriptMethodArg( value = "mappedCodes", description = "The maximum latest FHIR observations with the specified criteria that are checked for the specified order of the mapped codes." ),
            @ScriptMethodArg( value = "maxCount", description = "The mapped codes (non-system dependent codes) on which observations are filtered." ),
            @ScriptMethodArg( value = "filter", description = "Optional further filter argument pairs (variable arguments). The first value is the filtered parameter and the second is the value." ),
        },
        returnDescription = "The latest FHIR resource that matches the specified criteria and the order of the mapped codes." )
    public IBaseResource queryLatestPrioritizedByMappedCodes(
        @Nonnull String referencedResourceParameter, @Nonnull String referencedResourceType, @Nonnull IIdType referencedResourceId,
        @Nullable Object mappedCodes, @Nullable Integer maxCount, String... filter )
    {
        return clientTransformUtils.queryLatestPrioritizedByMappedCodes( getResourceName(),
            referencedResourceParameter, referencedResourceType, referencedResourceId, "code",
            mappedCodes, r -> codeTransformerUtils.getSystemCodeValues( getCodes( r ) ), maxCount, filter );
    }
}
