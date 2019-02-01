package org.dhis2.fhir.adapter.util;

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

import org.apache.commons.beanutils.PropertyUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;

/**
 * Utility class to lookup enum values from properties (nested properties, indexed
 * properties). This can be used for scripting environments where no access to the
 * corresponding enum types is available.
 *
 * @author volsch
 */
public abstract class EnumValueUtils
{
    @Nullable
    public static <T extends Enum<T>> T resolveEnumValue( @Nonnull Object object, @Nonnull String propertyPath, @Nullable Object enumValueName )
    {
        final String stringEnumName = (enumValueName == null) ? null : enumValueName.toString();
        if ( stringEnumName == null )
        {
            return null;
        }

        final Class<?> propertyClass;
        try
        {
            propertyClass = PropertyUtils.getPropertyType( object, propertyPath );
        }
        catch ( IllegalAccessException | InvocationTargetException | NoSuchMethodException | IllegalArgumentException e )
        {
            throw new IllegalArgumentException( "Could not resolve property " + propertyPath + " of object " + object.getClass().getSimpleName(), e );
        }
        if ( !propertyClass.isEnum() )
        {
            throw new IllegalArgumentException( "Property " + propertyPath + " of object " + object.getClass().getSimpleName() + " is no enumeration class: " + propertyClass.getSimpleName() );
        }

        @SuppressWarnings( "unchecked" ) final Class<T> uncheckedEnumClass = (Class<T>) propertyClass;
        return NameUtils.toEnumValue( uncheckedEnumClass, stringEnumName );
    }

    private EnumValueUtils()
    {
        super();
    }
}
