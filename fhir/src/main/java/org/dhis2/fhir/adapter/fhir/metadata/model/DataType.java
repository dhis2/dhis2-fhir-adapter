package org.dhis2.fhir.adapter.fhir.metadata.model;

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

import org.dhis2.fhir.adapter.converter.ObjectConverter;
import org.dhis2.fhir.adapter.converter.StringToBooleanConverter;
import org.dhis2.fhir.adapter.converter.StringToDoubleConverter;
import org.dhis2.fhir.adapter.converter.StringToEnumConverter;
import org.dhis2.fhir.adapter.converter.StringToIntegerConverter;
import org.dhis2.fhir.adapter.converter.StringToPatternConverter;
import org.dhis2.fhir.adapter.converter.StringToZonedDateTimeConverter;
import org.dhis2.fhir.adapter.dhis.converter.StringToExceptionConverter;
import org.dhis2.fhir.adapter.dhis.converter.StringToReferenceConverter;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.fhir.model.EventDecisionType;
import org.dhis2.fhir.adapter.geo.Location;
import org.dhis2.fhir.adapter.geo.StringToLocationConverter;
import org.dhis2.fhir.adapter.model.DateUnit;
import org.dhis2.fhir.adapter.model.Gender;
import org.dhis2.fhir.adapter.model.WeightUnit;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.core.convert.converter.Converter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Data types of constants and script arguments. They are used to perform value conversions and support the validation
 * of references to entities on DHIS2.
 *
 * @author volsch
 */
public enum DataType
{
    BOOLEAN( Boolean.class, new StringToBooleanConverter() ),
    STRING( String.class, new ObjectConverter<>( String.class ) ),
    INTEGER( Integer.class, new StringToIntegerConverter() ),
    DOUBLE( Double.class, new StringToDoubleConverter() ),
    DATE_TIME( ZonedDateTime.class, new StringToZonedDateTimeConverter() ),
    DATE_UNIT( DateUnit.class, new StringToEnumConverter<>( DateUnit.class ) ),
    WEIGHT_UNIT( WeightUnit.class, new StringToEnumConverter<>( WeightUnit.class ) ),
    GENDER( Gender.class, new StringToEnumConverter<>( Gender.class ) ),
    CONSTANT( String.class, new ObjectConverter<>( String.class ) ),
    CODE( String.class, new ObjectConverter<>( String.class ) ),
    LOCATION( Location.class, new StringToLocationConverter() ),
    PATTERN( Pattern.class, new StringToPatternConverter() ),
    ORG_UNIT_REF( Reference.class, new StringToReferenceConverter() ),
    TRACKED_ENTITY_REF( Reference.class, new StringToReferenceConverter() ),
    TRACKED_ENTITY_ATTRIBUTE_REF( Reference.class, new StringToReferenceConverter() ),
    DATA_ELEMENT_REF( Reference.class, new StringToReferenceConverter() ),
    PROGRAM_REF( Reference.class, new StringToReferenceConverter() ),
    PROGRAM_STAGE_REF( Reference.class, new StringToReferenceConverter() ),
    FHIR_RESOURCE( IBaseResource.class, new StringToExceptionConverter<>( IBaseResource.class ) ),
    FHIR_RESOURCE_LIST( List.class, new StringToExceptionConverter<>( List.class ) ),
    EVENT_DECISION_TYPE( EventDecisionType.class, new StringToEnumConverter<>( EventDecisionType.class ) );

    private final Class<?> javaType;

    private final Converter<String, ?> fromStringConverter;

    <T> DataType( Class<T> javaType, Converter<String, T> fromStringConverter )
    {
        this.javaType = javaType;
        this.fromStringConverter = fromStringConverter;
    }

    public Class<?> getJavaType()
    {
        return javaType;
    }

    public Converter<String, ?> getFromStringConverter()
    {
        return fromStringConverter;
    }
}
