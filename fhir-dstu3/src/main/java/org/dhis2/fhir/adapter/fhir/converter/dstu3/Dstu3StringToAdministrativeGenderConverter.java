package org.dhis2.fhir.adapter.fhir.converter.dstu3;

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

import org.dhis2.fhir.adapter.converter.ConvertedValueTypes;
import org.dhis2.fhir.adapter.fhir.converter.AbstractStringToAdministrativeGenderConverter;
import org.dhis2.fhir.adapter.fhir.metadata.model.ConstantResolver;
import org.dhis2.fhir.adapter.model.ValueType;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * FHIR version DSTU3 implementation of {@link AbstractStringToAdministrativeGenderConverter}.
 *
 * @author volsch
 */
@Component
@ConvertedValueTypes( types = ValueType.TEXT )
public class Dstu3StringToAdministrativeGenderConverter extends AbstractStringToAdministrativeGenderConverter<AdministrativeGender>
{
    public Dstu3StringToAdministrativeGenderConverter( @Nonnull ConstantResolver constantResolver )
    {
        super( AdministrativeGender.class, constantResolver );
    }

    @Nullable
    @Override
    protected AdministrativeGender getAdministrativeGender( @Nullable Gender gender )
    {
        if ( gender == null )
        {
            return AdministrativeGender.NULL;
        }
        switch ( gender )
        {
            case FEMALE:
                return AdministrativeGender.FEMALE;
            case MALE:
                return AdministrativeGender.MALE;
            default:
                throw new AssertionError( "Unhandled gender: " + gender );
        }
    }
}
