package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.util.r4;

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

import ca.uhn.fhir.parser.DataFormatException;
import org.dhis2.fhir.adapter.dhis.converter.ValueConverter;
import org.dhis2.fhir.adapter.dhis.model.Option;
import org.dhis2.fhir.adapter.dhis.model.OptionSet;
import org.dhis2.fhir.adapter.dhis.model.WritableOption;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.script.ScriptExecutionContext;
import org.dhis2.fhir.adapter.fhir.transform.TransformerDataException;
import org.dhis2.fhir.adapter.fhir.transform.dhis.impl.util.AbstractValueTypeDhisToFhirTransformerUtils;
import org.dhis2.fhir.adapter.model.ValueType;
import org.dhis2.fhir.adapter.scriptable.Scriptable;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DecimalType;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.TimeType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * R4 specific implementation of {@link AbstractValueTypeDhisToFhirTransformerUtils}.
 *
 * @author volsch
 */
@Scriptable
@Component
public class R4ValueTypeDhisToFhirTransformerUtils extends AbstractValueTypeDhisToFhirTransformerUtils
{
    public R4ValueTypeDhisToFhirTransformerUtils( @Nonnull ScriptExecutionContext scriptExecutionContext, @Nonnull ValueConverter valueConverter )
    {
        super( scriptExecutionContext, valueConverter );
    }

    @Nonnull
    @Override
    public Set<FhirVersion> getFhirVersions()
    {
        return FhirVersion.R4_ONLY;
    }

    @Nullable
    @Override
    protected IBaseDatatype convert( @Nullable String dhisValue, @Nonnull ValueType valueType, @Nullable OptionSet optionSet )
    {
        if ( dhisValue == null )
        {
            return null;
        }

        if ( optionSet != null )
        {
            final Option option = optionSet.getOptionalOptionByCode( dhisValue ).orElseGet( () -> new WritableOption( dhisValue, dhisValue ) );

            return new Coding().setCode( option.getCode() ).setDisplay( option.getName() );
        }

        try
        {
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
                    return new StringType( dhisValue );
                case INTEGER:
                case INTEGER_POSITIVE:
                case INTEGER_NEGATIVE:
                case INTEGER_ZERO_OR_POSITIVE:
                    return new IntegerType( dhisValue );
                case NUMBER:
                case PERCENTAGE:
                case UNIT_INTERVAL:
                    return new DecimalType( dhisValue );
                case DATETIME:
                case DATE:
                case AGE:
                    return new DateTimeType( dhisValue );
                case TIME:
                    return new TimeType( dhisValue );
                case BOOLEAN:
                case TRUE_ONLY:
                    return new BooleanType( dhisValue );
                default:
                    throw new TransformerDataException( "Unsupported DHIS2 value type: " + valueType );
            }
        }
        catch ( DataFormatException | IllegalArgumentException e )
        {
            throw new TransformerDataException( "Value with value type " + valueType + " could not be parsed for setting corresponding FHIR value: " + dhisValue, e );
        }
    }
}
