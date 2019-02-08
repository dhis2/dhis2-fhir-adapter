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

import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.EnumValueUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * DHIS2 to FHIR transformer utility methods for FHIR identifiers.
 *
 * @author volsch
 */
@Component
@Scriptable
@ScriptType( value = "FhirResourceUtils", transformType = ScriptTransformType.IMP, var = FhirResourceFhirToDhisTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities to handle FHIR to DHIS2 transformations of FHIR resources." )
public class FhirResourceFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "fhirResourceUtils";

    public FhirResourceFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public final String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.ALL;
    }

    @Nullable
    @ScriptMethod( description = "Resolves the enumeration value for a specific property path of an object.", returnDescription = "The resolved enumeration value.",
        args = {
            @ScriptMethodArg( value = "object", description = "The object that has the enumeration property at the specified property path." ),
            @ScriptMethodArg( value = "propertyPath", description = "The property path on the specified object that contains the enum property." ),
            @ScriptMethodArg( value = "enumValueName", description = "The name of the enumeration value for which the enumeration value should be resolved." )
        } )
    public <T extends Enum<T>> T resolveEnumValue( @Nonnull Object object, @Nonnull String propertyPath, @Nullable Object enumValueName )
    {
        return EnumValueUtils.resolveEnumValue( object, propertyPath, enumValueName );
    }
}
