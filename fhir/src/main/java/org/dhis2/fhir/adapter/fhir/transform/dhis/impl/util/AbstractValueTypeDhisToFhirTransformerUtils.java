package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util;

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

import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.OptionSet;
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
 * Abstract base class for converting DHIS2 value typed values to FHIR types.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "ValueTypeUtils", transformType = ScriptTransformType.EXP, var = AbstractValueTypeDhisToFhirTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities for handling conversion of DHIS2 values with a DHIS2 value type." )
public abstract class AbstractValueTypeDhisToFhirTransformerUtils extends AbstractTransformerUtils implements DhisToFhirTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "valueTypeUtils";

    private final ValueConverter valueConverter;

    protected AbstractValueTypeDhisToFhirTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutionContext );

        this.valueConverter = valueConverter;
    }

    @Nonnull
    @Override
    public String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @ScriptMethod( description = "Returns if the specified DHIS2 value type is supported.",
        args = @ScriptMethodArg( value = "valueType", description = "The DHIS2 value type that should be checked." ),
        returnDescription = "A boolean value that indicates if the value is supported." )
    public boolean isSupportedValueType( @Nonnull Object valueType )
    {
        final ValueType convertedValueType = NameUtils.toEnumValue( ValueType.class, valueType );

        switch ( convertedValueType )
        {
            case TEXT:
            case EMAIL:
            case LETTER:
            case ORGANISATION_UNIT:
            case PHONE_NUMBER:
            case TRACKER_ASSOCIATE:
            case URL:
            case USERNAME:
            case LONG_TEXT:
            case INTEGER:
            case INTEGER_POSITIVE:
            case INTEGER_NEGATIVE:
            case INTEGER_ZERO_OR_POSITIVE:
            case NUMBER:
            case PERCENTAGE:
            case UNIT_INTERVAL:
            case DATETIME:
            case AGE:
            case DATE:
            case TIME:
            case BOOLEAN:
            case TRUE_ONLY:
                return true;
        }

        return false;
    }

    @ScriptMethod( description = "Returns the DHIS2 value as FHIR value. The value is converted according to the specified value type.",
        args = {
            @ScriptMethodArg( value = "dhisValue", description = "The DHIS2 typed value." ),
            @ScriptMethodArg( value = "valueType", description = "The DHIS2 value from which the FHIR value should be converted." ),
            @ScriptMethodArg( value = "optionSet", description = "The DHIS2 option when the code belongs to a DHIS2 option set." )
        },
        returnDescription = "The DHIS2 value as FHIR value converted to a FHIR datatype." )
    @Nullable
    public IBaseDatatype convert( @Nullable Object dhisValue, @Nonnull Object valueType, @Nullable OptionSet optionSet )
    {
        final ValueType convertedValueType = NameUtils.toEnumValue( ValueType.class, valueType );

        return convert( valueConverter.convert( dhisValue, convertedValueType, String.class ), convertedValueType, optionSet );
    }

    @Nullable
    protected abstract IBaseDatatype convert( @Nullable String dhisValue, @Nonnull ValueType valueType, @Nullable OptionSet optionSet );
}
