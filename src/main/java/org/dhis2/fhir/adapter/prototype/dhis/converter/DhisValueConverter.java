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
import org.dhis2.fhir.adapter.prototype.converter.IsoStringToLocalDateConverter;
import org.dhis2.fhir.adapter.prototype.converter.LocalDateToIsoStringConverter;
import org.dhis2.fhir.adapter.prototype.converter.TypedConverter;
import org.dhis2.fhir.adapter.prototype.dhis.model.ValueType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class DhisValueConverter
{
    private final ListMultimap<ValueType, TypedConverter<?, ?>> toDhisConverters = ArrayListMultimap.create();

    private final ListMultimap<ValueType, TypedConverter<?, ?>> fromDhisConverters = ArrayListMultimap.create();

    public DhisValueConverter()
    {
        toDhisConverters.put( ValueType.DATE, new LocalDateToIsoStringConverter() );

        fromDhisConverters.put( ValueType.DATE, new IsoStringToLocalDateConverter() );
    }

    public @Nullable Object convertToDhis( @Nullable Object value, @Nonnull ValueType valueType )
    {
        if ( (value == null) || isUnconvertedPrimitive( value, valueType ) )
        {
            return value;
        }

        final List<TypedConverter<?, ?>> converters = toDhisConverters.get( valueType );
        for ( final TypedConverter<?, ?> converter : converters )
        {
            if ( converter.getFromClass().isInstance( value ) )
            {
                return converter.convertCasted( value );
            }
        }
        throw new ConversionException( ("No suitable converter for value type " + valueType + " and object type " + value.getClass().getSimpleName()) );
    }

    private static boolean isUnconvertedPrimitive( @Nonnull Object value, @Nonnull ValueType valueType )
    {
        return valueType.getJavaClass().equals( value.getClass() ) && ((value instanceof String) || (value instanceof Number) || (value instanceof Boolean));
    }
}
