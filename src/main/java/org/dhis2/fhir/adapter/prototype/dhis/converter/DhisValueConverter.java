package org.dhis2.fhir.adapter.prototype.dhis.converter;

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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.dhis2.fhir.adapter.prototype.converter.ConversionException;
import org.dhis2.fhir.adapter.prototype.converter.DateToIsoDateStringConverter;
import org.dhis2.fhir.adapter.prototype.converter.DateToZonedDateTimeConverter;
import org.dhis2.fhir.adapter.prototype.converter.DoubleToTextStringConverter;
import org.dhis2.fhir.adapter.prototype.converter.TypedConverter;
import org.dhis2.fhir.adapter.prototype.dhis.model.ValueType;
import org.dhis2.fhir.adapter.prototype.geo.LocationToStringConverter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class DhisValueConverter
{
    private final ListMultimap<ValueType, TypedConverter<?, ?>> converters = ArrayListMultimap.create();

    public DhisValueConverter()
    {
        addConverter( ValueType.BOOLEAN, new BooleanToStringConverter() );
        addConverter( ValueType.DATETIME, new DateToZonedDateTimeConverter() );
        addConverter( ValueType.DATE, new DateToIsoDateStringConverter() );
        addConverter( ValueType.COORDINATE, new LocationToStringConverter() );
        addConverter( ValueType.TEXT, new DoubleToTextStringConverter() );
    }

    protected void addConverter( @Nonnull ValueType valueType, @Nonnull TypedConverter<?, ?> converter )
    {
        converters.put( valueType, converter );
    }

    @SuppressWarnings( "unchecked" )
    public @Nullable <R> R convert( @Nullable Object value, @Nonnull ValueType valueType, @Nonnull Class<R> resultClass )
    {
        if ( (value == null) || (isResultValueType( value, resultClass ) && isUnconvertedPrimitive( value, valueType )) )
        {
            return (R) value;
        }

        final List<TypedConverter<?, ?>> converters = this.converters.get( valueType );
        for ( final TypedConverter<?, ?> converter : converters )
        {
            if ( converter.getFromClass().isInstance( value ) && isResultClassType( resultClass, converter ) )
            {
                return (R) converter.convertCasted( value );
            }
        }
        throw new ConversionException( ("No suitable converter for value type " + valueType + " and object type " + value.getClass().getSimpleName() + ".") );
    }

    public @Nullable Object convert( @Nullable Object value, @Nonnull ValueType valueType )
    {
        return convert( value, valueType, Object.class );
    }

    private static boolean isUnconvertedPrimitive( @Nonnull Object value, @Nonnull ValueType valueType )
    {
        return valueType.getJavaClass().equals( value.getClass() ) && ((value instanceof String) || (value instanceof Number) || (value instanceof Boolean));
    }

    private <R> boolean isResultValueType( @Nonnull Object value, @Nonnull Class<R> resultClass )
    {
        return (resultClass == Object.class) || resultClass.isInstance( value );
    }

    private <R> boolean isResultClassType( @Nonnull Class<R> resultClass, @Nonnull TypedConverter<?, ?> converter )
    {
        return (resultClass == Object.class) || resultClass.isAssignableFrom( converter.getToClass() );
    }
}
