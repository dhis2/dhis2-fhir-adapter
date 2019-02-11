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
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.ICompositeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * FHIR to DHIS2 transformer utility methods for handling vital sign observations.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "VitalSignUtils", transformType = ScriptTransformType.IMP, var = AbstractVitalSignFhirToDhisTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities for handling vital sign observations." )
public abstract class AbstractVitalSignFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "vitalSignUtils";

    protected AbstractVitalSignFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nullable
    @ScriptMethod( description = "Returns the weight with the specified weight unit from the specified composite value of a FHIR observation.",
        args = {
            @ScriptMethodArg( value = "value", description = "The composite value that includes the weight." ),
            @ScriptMethodArg( value = "weightUnit", description = "The weight unit in which the weight should be returned." ),
            @ScriptMethodArg( value = "round", description = "Specifies if the returned value should be rounded." ),
        },
        returnDescription = "Returns the included weight with the specified weight unit." )
    public abstract Double getWeight( @Nullable ICompositeType value, @Nullable Object weightUnit, boolean round ) throws TransformerDataException;

    @Nullable
    @ScriptMethod( description = "Returns the height with the specified height unit from the specified composite value of a FHIR observation.",
        args = {
            @ScriptMethodArg( value = "value", description = "The composite value that includes the height." ),
            @ScriptMethodArg( value = "heightUnit", description = "The height unit in which the height should be returned." ),
            @ScriptMethodArg( value = "round", description = "Specifies if the returned value should be rounded." ),
        },
        returnDescription = "Returns the included height with the specified height unit." )
    public abstract Double getHeight( @Nullable ICompositeType value, @Nullable Object heightUnit, boolean round ) throws TransformerDataException;
}
