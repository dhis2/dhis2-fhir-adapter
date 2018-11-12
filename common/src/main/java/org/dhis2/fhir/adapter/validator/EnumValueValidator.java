package org.dhis2.fhir.adapter.validator;

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

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Validates that value is enumeration value. This is primarily used for documentation
 * purpose (Spring REST Docs).
 *
 * @author volsch
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object>
{
    private Set<Enum<?>> supported;

    @SuppressWarnings( "unchecked" )
    @Override
    public void initialize( EnumValue enumValue )
    {
        supported = new HashSet<>( getSupported( enumValue.value(), enumValue.unsupported(), enumValue.supported() ) );
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public boolean isValid( Object value, ConstraintValidatorContext context )
    {
        if ( value == null )
        {
            return true;
        }

        final Collection<Enum<?>> values;
        if ( value instanceof Collection )
        {
            values = (Collection<Enum<?>>) value;
        }
        else
        {
            values = Collections.singleton( (Enum) value );
        }

        for ( final Enum<?> v : values )
        {
            if ( (v != null) && !supported.contains( v ) )
            {
                {
                    context.buildConstraintViolationWithTemplate( "Supported values " + StringUtils.join( supported, ", " ) );
                    return false;
                }
            }
        }
        return true;
    }

    @SuppressWarnings( "unchecked" )
    @Nonnull
    public static SortedSet<Enum<?>> getSupported( @Nonnull Class<? extends Enum<?>> enumClass, @Nonnull String[] unsupportedValues, @Nonnull String[] supportedValues )
    {
        final Set<Enum<?>> unsupported = new HashSet<>();
        for ( final String value : unsupportedValues )
        {
            unsupported.add( Enum.valueOf( (Class<? extends Enum>) enumClass, value ) );
        }
        final SortedSet<Enum<?>> supported = new TreeSet<>( Comparator.comparing( Enum::name ) );
        if ( supportedValues.length > 0 )
        {
            for ( final String value : supportedValues )
            {
                supported.add( Enum.valueOf( (Class<? extends Enum>) enumClass, value ) );
            }
        }
        else
        {
            supported.addAll( Arrays.asList( enumClass.getEnumConstants() ) );
        }
        supported.removeAll( unsupported );
        return supported;
    }
}
