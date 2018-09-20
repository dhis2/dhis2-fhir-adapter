package org.dhis2.fhir.adapter.fhir.converter;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.converter.ConvertedValueTypes;
import org.dhis2.fhir.adapter.converter.TypedConverter;
import org.dhis2.fhir.adapter.fhir.metadata.model.ConstantResolver;
import org.dhis2.fhir.adapter.model.ValueType;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Component
@ConvertedValueTypes( types = ValueType.TEXT )
public class AdministrativeGenderToStringConverter extends TypedConverter<AdministrativeGender, String>
{
    public static final String GENDER_FEMALE_CONSTANT_CODE = "GENDER_FEMALE";

    public static final String GENDER_MALE_CONSTANT_CODE = "GENDER_MALE";

    private final ConstantResolver constantResolver;

    public AdministrativeGenderToStringConverter( @Nonnull ConstantResolver constantResolver )
    {
        super( AdministrativeGender.class, String.class );
        this.constantResolver = constantResolver;
    }

    @Nullable
    @Override
    public String doConvert( @Nonnull AdministrativeGender source ) throws ConversionException
    {
        switch ( source )
        {
            case FEMALE:
                return constantResolver.getByCode( GENDER_FEMALE_CONSTANT_CODE )
                    .orElseThrow( () -> new ConversionException( "No constant with code " + GENDER_FEMALE_CONSTANT_CODE + " has been defined." ) ).getValue();
            case MALE:
                return constantResolver.getByCode( GENDER_MALE_CONSTANT_CODE )
                    .orElseThrow( () -> new ConversionException( "No constant with code " + GENDER_MALE_CONSTANT_CODE + " has been defined." ) ).getValue();
            case NULL:
            case OTHER:
                return null;
            default:
                throw new ConversionException( "Unsupported gender: " + source.name() );
        }
    }
}
