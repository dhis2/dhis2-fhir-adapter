package org.dhis2.fhir.adapter.fhir.converter;

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

import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.converter.TypedConverter;
import org.dhis2.fhir.adapter.fhir.metadata.model.ConstantResolver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.dhis2.fhir.adapter.fhir.converter.AbstractAdministrativeGenderToStringConverter.GENDER_FEMALE_CONSTANT_CODE;
import static org.dhis2.fhir.adapter.fhir.converter.AbstractAdministrativeGenderToStringConverter.GENDER_MALE_CONSTANT_CODE;

/**
 * Abstract implementation of the converter that converts the DHIS2 option value
 * for the gender to the FHIR administrative gender.
 *
 * @param <A> the concrete FHIR version specific type of the administrative gender.
 * @author volsch
 */
public abstract class AbstractStringToAdministrativeGenderConverter<A extends Enum<A>> extends TypedConverter<String, A>
{
    private final ConstantResolver constantResolver;

    protected AbstractStringToAdministrativeGenderConverter( @Nonnull Class<A> fromClass, @Nonnull ConstantResolver constantResolver )
    {
        super( String.class, fromClass );
        this.constantResolver = constantResolver;
    }

    @Nullable
    @Override
    public A doConvert( @Nonnull String optionValue ) throws ConversionException
    {
        if ( StringUtils.isBlank( optionValue ) )
        {
            return getAdministrativeGender( null );
        }
        String constant = constantResolver.findOneByCode( GENDER_FEMALE_CONSTANT_CODE )
            .orElseThrow( () -> new ConversionException( "No constant with code " + GENDER_FEMALE_CONSTANT_CODE + " has been defined." ) ).getValue();
        if ( optionValue.equalsIgnoreCase( constant ) )
        {
            return getAdministrativeGender( Gender.FEMALE );
        }
        constant = constantResolver.findOneByCode( GENDER_MALE_CONSTANT_CODE )
            .orElseThrow( () -> new ConversionException( "No constant with code " + GENDER_MALE_CONSTANT_CODE + " has been defined." ) ).getValue();
        if ( optionValue.equalsIgnoreCase( constant ) )
        {
            return getAdministrativeGender( Gender.MALE );
        }
        throw new ConversionException( "Unsupported gender option value: " + optionValue );
    }

    @Nullable
    protected abstract A getAdministrativeGender( @Nullable Gender gender );

    protected enum Gender
    {
        MALE, FEMALE
    }
}
