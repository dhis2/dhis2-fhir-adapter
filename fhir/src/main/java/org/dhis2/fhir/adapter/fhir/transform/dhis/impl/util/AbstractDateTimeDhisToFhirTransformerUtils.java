package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util;

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
import org.dhis2.fhir.adapter.scriptable.ScriptTransformType;
import org.dhis2.fhir.adapter.scriptable.ScriptType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.IPrimitiveType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

/**
 * DHIS2 to FHIR transformer utility methods for human names.
 *
 * @author volsch
 */
@Scriptable
@ScriptType( value = "DateTimeUtils", transformType = ScriptTransformType.EXP, var = AbstractDateTimeDhisToFhirTransformerUtils.SCRIPT_ATTR_NAME,
    description = "Utilities to handle DHIS2 to FHIR transformations of date time value." )
public abstract class AbstractDateTimeDhisToFhirTransformerUtils extends AbstractDhisToFhirTransformerUtils
{
    public static final String SCRIPT_ATTR_NAME = "dateTimeUtils";

    protected static final String DEFAULT_GIVEN_DELIMITER = " ";

    protected AbstractDateTimeDhisToFhirTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
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
    @ScriptMethod( description = "Returns a date/time element for the specified date/time value when it has at least day precision. A date/time element value may have only year or month precision.",
        args = @ScriptMethodArg( value = "dateTime", description = "The date/time value for which the evaluation should be performed." ),
        returnDescription = "Returns if the specified date/time value as date/time element or null when it has not at least day precision." )
    public abstract IPrimitiveType<Date> getPreciseDateElement( @Nullable Object dateTime );

    @Nullable
    @ScriptMethod( description = "Returns a date/time element for the specified date/time value.",
        args = @ScriptMethodArg( value = "dateTime", description = "The date/time value that should be converted." ),
        returnDescription = "Returns if the specified date/time value as date/time element." )
    public abstract IPrimitiveType<Date> getDateTimeElement( @Nullable Object dateTime );

    @Nullable
    @ScriptMethod( description = "Returns a date/time element for the specified date/time value with day precision.",
        args = @ScriptMethodArg( value = "dateTime", description = "The date/time value that should be converted." ),
        returnDescription = "Returns if the specified date/time value as date/time element with day precision." )
    public abstract IPrimitiveType<Date> getDayDateTimeElement( @Nullable Object dateTime );
}
