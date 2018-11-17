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

import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.scriptable.ScriptMethod;
import org.dhis2.fhir.adapter.scriptable.ScriptMethodArg;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.ICompositeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * FHIR to DHIS2 transformer utility methods for addresses.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "AddressUtils", var = AbstractAddressFhirToDhisTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities to handle FHIR to DHIS2 transformations of addresses." )
public abstract class AbstractAddressFhirToDhisTransformerUtils extends AbstractFhirToDhisTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "addressUtils";

    protected static final String DEFAULT_LINE_DELIMITER = " ";

    protected static final String DEFAULT_TEXT_DELIMITER = " / ";

    protected AbstractAddressFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
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
    @ScriptMethod( description = "Returns a single address line for the adress lines includes in the specified FHIR address. A space is used to separate the lines.",
        args = @ScriptMethodArg( value = "address", description = "The FHIR address from which the address lines should be used." ),
        returnDescription = "A single line for the included address lines." )
    public String getSingleLine( @Nullable ICompositeType address )
    {
        return getSingleLine( address, DEFAULT_LINE_DELIMITER );
    }

    @Nullable
    @ScriptMethod( description = "Returns a constructed text of the address that contains the address lines, the postal code, the city and the state (if available). ' / ' is used to separate the address components.",
        args = @ScriptMethodArg( value = "address", description = "The FHIR address from which the address components should be used." ),
        returnDescription = "The constructed address text." )
    public String getConstructedText( @Nullable ICompositeType address )
    {
        return getConstructedText( address, DEFAULT_TEXT_DELIMITER );
    }

    @Nullable
    public abstract ICompositeType getPrimaryAddress( @Nonnull List<? extends ICompositeType> addresses );

    @Nullable
    @ScriptMethod( description = "Returns a single address line for the adress lines includes in the specified FHIR address.",
        args = {
            @ScriptMethodArg( value = "address", description = "The FHIR address from which the address lines should be used." ),
            @ScriptMethodArg( value = "delimiter", description = "The delimiter that should be used to separated the lines (e.g. space character)." )
        },
        returnDescription = "A single line for the included address lines." )
    public abstract String getSingleLine( @Nullable ICompositeType address, @Nonnull String delimiter );

    @Nullable
    @ScriptMethod( description = "Returns a constructed text of the address that contains the address lines, the postal code, the city and the state (if available).",
        args = {
            @ScriptMethodArg( value = "address", description = "The FHIR address from which the address components should be used." ),
            @ScriptMethodArg( value = "delimiter", description = "The delimiter that should be used to separated the lines (e.g. space character)." )
        },
        returnDescription = "The constructed address text." )
    public abstract String getConstructedText( @Nullable ICompositeType address, @Nonnull String delimiter );

    @Nullable
    @ScriptMethod( description = "Returns the text representation (if included) of the address.",
        args = @ScriptMethodArg( value = "address", description = "The FHIR address from which the text should be returned." ) )
    public abstract String getText( @Nullable ICompositeType address );
}
