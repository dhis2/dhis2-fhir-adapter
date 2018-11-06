package org.dhis2.fhir.adapter.fhir.metadata.repository.validator;

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
import org.dhis2.fhir.adapter.converter.ConversionException;
import org.dhis2.fhir.adapter.fhir.metadata.model.Constant;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Nonnull;

/**
 * Spring Data REST validator for {@link Constant}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveConstantValidator implements Validator
{
    @Override
    public boolean supports( @Nonnull Class<?> clazz )
    {
        return Constant.class.isAssignableFrom( clazz );
    }

    @Override
    public void validate( Object target, @Nonnull Errors errors )
    {
        final Constant constant = (Constant) target;

        if ( StringUtils.isBlank( constant.getName() ) )
        {
            errors.rejectValue( "name", "Constant.name.blank", "Name must not be blank." );
        }
        if ( StringUtils.length( constant.getName() ) > Constant.MAX_NAME_LENGTH )
        {
            errors.rejectValue( "name", "Constant.name.length", new Object[]{ Constant.MAX_NAME_LENGTH }, "Name must not be longer than {0} characters." );
        }
        if ( StringUtils.isBlank( constant.getCode() ) )
        {
            errors.rejectValue( "code", "Constant.code.blank", "Code must not be blank." );
        }
        else if ( constant.getCode().contains( "," ) )
        {
            errors.rejectValue( "code", "Constant.code.comma", "Code must not contain commas." );
        }
        if ( StringUtils.length( constant.getCode() ) > Constant.MAX_CODE_LENGTH )
        {
            errors.rejectValue( "code", "Constant.code.length", new Object[]{ Constant.MAX_CODE_LENGTH }, "Code must not be longer than {0} characters." );
        }
        if ( StringUtils.length( constant.getValue() ) > Constant.MAX_VALUE_LENGTH )
        {
            errors.rejectValue( "value", "Constant.value.length", new Object[]{ Constant.MAX_VALUE_LENGTH }, "Value must not be longer than {0} characters." );
        }
        if ( constant.getDataType() == null )
        {
            errors.rejectValue( "dataType", "Constant.dataType.null", "Data type is mandatory." );
        }
        else if ( constant.getValue() != null )
        {
            try
            {
                constant.getDataType().getFromStringConverter().convert( constant.getValue() );
            }
            catch ( ConversionException e )
            {
                errors.rejectValue( "value", "Constant.value.dataType", new Object[]{ constant.getDataType(), e.getMessage() }, "Value does not match data type {0}: {1}" );
            }
        }
    }
}
