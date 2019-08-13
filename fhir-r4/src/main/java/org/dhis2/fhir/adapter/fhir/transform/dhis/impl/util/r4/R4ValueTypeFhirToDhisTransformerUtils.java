package org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.r4;

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
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.AbstractValueTypeFhirToDhisTransformerUtils;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.instance.model.api.IPrimitiveType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.TimeType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * R4 specific implementation of {@link AbstractValueTypeFhirToDhisTransformerUtils}.
 *
 * @author volsch
 */
@Scriptable
@Component
public class R4ValueTypeFhirToDhisTransformerUtils extends AbstractValueTypeFhirToDhisTransformerUtils
{
    public R4ValueTypeFhirToDhisTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext )
    {
        super( scriptExecutionContext );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Nullable
    @Override
    protected Object convert( @Nullable IBaseDatatype fhirValue, @Nonnull ValueType valueType )
    {
        if ( fhirValue == null )
        {
            return null;
        }

        switch ( valueType )
        {
            case TEXT:
            case LONG_TEXT:
            case EMAIL:
            case LETTER:
            case ORGANISATION_UNIT:
            case PHONE_NUMBER:
            case TRACKER_ASSOCIATE:
            case URL:
            case USERNAME:
                if ( fhirValue instanceof IPrimitiveType )
                {
                    return ( (IPrimitiveType) fhirValue ).getValueAsString();
                }

                if ( fhirValue instanceof Coding )
                {
                    return getCode( (Coding) fhirValue );
                }

                break;
            case INTEGER:
            case INTEGER_POSITIVE:
            case INTEGER_NEGATIVE:
            case INTEGER_ZERO_OR_POSITIVE:
                if ( fhirValue instanceof IPrimitiveType && ( (IPrimitiveType) fhirValue ).getValue() instanceof Integer )
                {
                    return ( (IPrimitiveType) fhirValue ).getValueAsString();
                }

                if ( fhirValue instanceof Coding )
                {
                    return getCode( (Coding) fhirValue );
                }

                break;
            case NUMBER:
            case PERCENTAGE:
            case UNIT_INTERVAL:
                if ( fhirValue.isEmpty() )
                {
                    return null;
                }

                if ( fhirValue instanceof IPrimitiveType && ( (IPrimitiveType) fhirValue ).getValue() instanceof Number )
                {
                    return ( (IPrimitiveType) fhirValue ).getValueAsString();
                }

                break;
            case DATETIME:
            case DATE:
            case AGE:
                if ( fhirValue.isEmpty() )
                {
                    return null;
                }

                if ( fhirValue instanceof DateTimeType )
                {
                    return DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        .format( ( (DateTimeType) fhirValue ).getValue().toInstant().atZone( ZoneId.systemDefault() ).toLocalDateTime() );
                }

                break;
            case TIME:
                if ( fhirValue instanceof TimeType )
                {
                    return ( (TimeType) fhirValue ).getValueAsString();
                }

                break;
            case BOOLEAN:
            case TRUE_ONLY:
                if ( fhirValue.isEmpty() )
                {
                    return null;
                }

                if ( fhirValue instanceof IPrimitiveType && ( (IPrimitiveType) fhirValue ).getValue() instanceof Boolean )
                {
                    return ( (IPrimitiveType) fhirValue ).getValueAsString();
                }

                break;
            default:
                throw new TransformerDataException( "Unsupported DHIS2 value type: " + valueType );
        }

        throw new TransformerDataException( "Unsupported FHIR type " + fhirValue.getClass().getSimpleName() + " for DHIS2 value type " + valueType );
    }

    @Nullable
    protected String getCode( @Nonnull Coding coding )
    {
        return coding.getCode();
    }
}
