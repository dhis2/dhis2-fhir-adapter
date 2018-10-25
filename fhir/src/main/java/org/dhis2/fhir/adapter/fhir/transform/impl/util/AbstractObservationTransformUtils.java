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
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.hl7.fhir.instance.model.api.IBaseBackboneElement;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.instance.model.api.IIdType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Scriptable
public abstract class AbstractObservationTransformUtils extends AbstractFhirToDhisTransformerUtils
{
    private static final String SCRIPT_ATTR_NAME = "observationUtils";

    public static final String COMPONENT_SEPARATOR = "\n";

    private final AbstractFhirClientTransformUtils clientTransformUtils;

    private final AbstractCodeFhirToDhisTransformerUtils codeTransformerUtils;

    protected AbstractObservationTransformUtils( @Nonnull ScriptExecutionContext scriptExecutionContext,
        @Nonnull AbstractFhirClientTransformUtils clientTransformUtils, @Nonnull AbstractCodeFhirToDhisTransformerUtils codeTransformerUtils )
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
    public abstract String getResourceName();

    @Nullable
    public abstract ICompositeType getCodes( @Nullable IBaseResource resource );

    @Nullable
    public abstract String getComponentText( @Nullable IBaseResource resource );

    @Nullable
    public abstract IBaseBackboneElement getBackboneElement( @Nullable List<? extends IBaseBackboneElement> backboneElements,
        @Nonnull String system, @Nonnull String code );

    @Nullable
    public IBaseResource queryLatestPrioritizedByMappedCodes(
        @Nonnull String referencedResourceParameter, @Nonnull String referencedResourceType, @Nonnull IIdType referencedResourceId,
        @Nullable Object mappedCodes, @Nullable Integer maxCount, String... filter )
    {
        return clientTransformUtils.queryLatestPrioritizedByMappedCodes( getResourceName(),
            referencedResourceParameter, referencedResourceType, referencedResourceId, "code",
            mappedCodes, r -> codeTransformerUtils.getSystemCodeValues( getCodes( r ) ), maxCount, filter );
    }
}
