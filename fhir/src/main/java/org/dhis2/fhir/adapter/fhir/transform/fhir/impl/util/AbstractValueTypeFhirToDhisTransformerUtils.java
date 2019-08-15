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
import org.dhis2.fhir.adapter.fhir.transform.util.AbstractTransformerUtils;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.NameUtils;
import org.hl7.fhir.instance.model.api.IBaseDatatype;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Abstract base class for converting FHIR types to DHIS2 value typed values.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "ValueTypeUtils", transformType = ScriptTransformType.IMP, var = AbstractValueTypeFhirToDhisTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities for handling conversion of DHIS2 values with a DHIS2 value type." )
public abstract class AbstractValueTypeFhirToDhisTransformerUtils extends AbstractTransformerUtils implements FhirToDhisTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "valueTypeUtils";

    protected AbstractValueTypeFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @ScriptMethod( description = "Returns the FHIR value as DHIS2 value. The value is converted according to the specified value type.",
        args = {
            @ScriptMethodArg( value = "fhirValue", description = "The FHIR typed value." ),
            @ScriptMethodArg( value = "valueType", description = "The DHIS2 value type to which the FHIR value should be converted." )
        },
        returnDescription = "The FHIR value as DHIS2 value converted to the DHIS2 value type." )
    @Nullable
    public Object convert( @Nullable IBaseDatatype fhirValue, @Nonnull Object valueType )
    {
        final ValueType convertedValueType = NameUtils.toEnumValue( ValueType.class, valueType );

        return convert( fhirValue, convertedValueType );
    }

    @Nullable
    protected abstract Object convert( @Nullable IBaseDatatype fhirValue, @Nonnull ValueType valueType );
}
