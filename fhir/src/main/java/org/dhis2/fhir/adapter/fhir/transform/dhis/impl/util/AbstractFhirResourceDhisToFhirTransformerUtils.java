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

import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.dhis2.fhir.adapter.util.EnumValueUtils;
import org.hl7.fhir.instance.model.api.IBase;
import org.hl7.fhir.instance.model.api.IBaseElement;
import org.hl7.fhir.instance.model.api.IBaseReference;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.ICompositeType;
import org.hl7.fhir.instance.model.api.IPrimitiveType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * DHIS2 to FHIR transformer utility methods for FHIR identifiers.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "FhirResourceUtils", transformType = ScriptTransformType.EXP, var = AbstractFhirResourceDhisToFhirTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities to handle DHIS2 to FHIR transformations of FHIR identifiers." )
public abstract class AbstractFhirResourceDhisToFhirTransformerUtils extends AbstractDhisToFhirTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "fhirResourceUtils";

    protected AbstractFhirResourceDhisToFhirTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
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
    @ScriptMethod( description = "Creates the FHIR resource (HAPI FHIR) with the specified resource type name.", returnDescription = "The created FHIR resource.",
        args = @ScriptMethodArg( value = "resourceType", description = "The FHIR resource type name of the resource to be created (e.g. Patient)." ) )
    public abstract IBaseResource createResource( @Nonnull String resourceType );

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

    @Nonnull
    @ScriptMethod( description = "Creates a FHIR codeable concept.", returnDescription = "The created FHIR codeable concept." )
    public abstract ICompositeType createCodeableConcept();

    @Nonnull
    @ScriptMethod( description = "Returns the created reference for the specified resource." )
    public abstract IBaseReference createReference( @Nonnull IBaseResource resource );

    @ScriptMethod( description = "Returns if the specified string value is included in the specified string type list.",
        args = {
            @ScriptMethodArg( value = "stringList", description = "The list of string type values." ),
            @ScriptMethodArg( value = "value", description = "The value that should be checked in the string type list." )
        } )
    public abstract boolean containsString( @Nonnull List<? extends IPrimitiveType<String>> stringList, @Nullable String value );

    @Nonnull
    public IBaseResource createResource( @Nonnull Object fhirResourceType )
    {
        final FhirResourceType resourceType = convertFhirResourceType( fhirResourceType );
        return createResource( resourceType.getResourceTypeName() );
    }

    @Nonnull
    public abstract IBaseElement createType( @Nonnull String fhirType );

    @Nonnull
    public IBaseResource createResource( @Nonnull FhirResourceType fhirResourceType )
    {
        return createResource( fhirResourceType.getResourceTypeName() );
    }

    public abstract boolean equalsDeep( @Nonnull IBase base1, @Nonnull IBase base2 );
}
