package org.dhis2.fhir.adapter.dhis.converter;

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
import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.converter.ConvertedValueTypes;
import org.dhis2.fhir.adapter.converter.TypedConverter;
import org.dhis2.fhir.adapter.model.ValueType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * Converts values using {@link ValueConverter}s. The converters are associated with a
 * {@linkplain ValueType DHIS2 value type}. All converters that are annotated with
 * {@link ConvertedValueTypes} annotation are taken to initialize this value converter.
 * If there are multiple converters for one DHIS2 value type the most appropriate for
 * the specified source type is used (sub-classes may have more information than
 * super classes).
 *
 * @author volsch
 */
@Component
public class ValueConverter
{
    private final ListMultimap<ValueType, TypedConverter<?, ?>> converters = ArrayListMultimap.create();

    public ValueConverter( @Nonnull ObjectProvider<List<TypedConverter<?, ?>>> converters )
    {
        converters.ifAvailable( typedConverters -> typedConverters.stream().filter( tc -> tc.getClass().isAnnotationPresent( ConvertedValueTypes.class ) )
            .forEach( converter -> Arrays.stream( converter.getClass().getAnnotation( ConvertedValueTypes.class ).types() ).forEach( vt -> addConverter( vt, converter ) ) ) );
    }

    protected void addConverter( @Nonnull ValueType valueType, @Nonnull TypedConverter<?, ?> converter )
    {
        final List<TypedConverter<?, ?>> typedConverters = converters.get( valueType );
        final ListIterator<TypedConverter<?, ?>> iterator = typedConverters.listIterator();
        while ( iterator.hasNext() )
        {
            final TypedConverter<?, ?> other = iterator.next();
            if ( other.getFromClass().isAssignableFrom( converter.getFromClass() ) )
            {
                iterator.previous();
                iterator.add( converter );
                break;
            }
        }
        if ( !typedConverters.contains( converter ) )
        {
            converters.put( valueType, converter );
        }
    }

    /**
     * Converts the specified value to the specified value type with the specified result class.
     *
     * @param value       the value to be converted.
     * @param valueType   the resulting value type.
     * @param resultClass the resulting value class.
     * @param <R>         the concrete type of the result.
     * @return the converted value.
     * @throws ConversionException thrown if a conversion error occurs.
     */
    @SuppressWarnings( "unchecked" )
    @Nullable
    public <R> R convert( @Nullable Object value, @Nonnull ValueType valueType, @Nonnull Class<R> resultClass ) throws ConversionException
    {
        if ( (value == null) || isResultValueType( value, resultClass ) )
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

    private <R> boolean isResultValueType( @Nonnull Object value, @Nonnull Class<R> resultClass )
    {
        return (resultClass == Object.class) || resultClass.isInstance( value );
    }

    private <R> boolean isResultClassType( @Nonnull Class<R> resultClass, @Nonnull TypedConverter<?, ?> converter )
    {
        return (resultClass == Object.class) || resultClass.isAssignableFrom( converter.getToClass() );
    }
}
