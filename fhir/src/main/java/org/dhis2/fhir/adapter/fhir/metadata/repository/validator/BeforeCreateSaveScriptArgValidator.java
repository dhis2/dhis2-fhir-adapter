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
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArg;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Nonnull;

/**
 * Spring Data REST validator for {@link ScriptArg}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveScriptArgValidator implements Validator
{
    @Override
    public boolean supports( @Nonnull Class<?> clazz )
    {
        return ScriptArg.class.isAssignableFrom( clazz );
    }

    @Override
    public void validate( Object target, @Nonnull Errors errors )
    {
        final ScriptArg scriptArg = (ScriptArg) target;

        if ( scriptArg.getScript() == null )
        {
            errors.rejectValue( "script", "ScriptArg.script.null", "Script is mandatory." );
        }
        if ( StringUtils.isBlank( scriptArg.getName() ) )
        {
            errors.rejectValue( "name", "ScriptArg.name.blank", "Name must not be blank." );
        }
        if ( StringUtils.length( scriptArg.getName() ) > ScriptArg.MAX_NAME_LENGTH )
        {
            errors.rejectValue( "name", "ScriptArg.name.length", new Object[]{ ScriptArg.MAX_NAME_LENGTH }, "Name must not be longer than {0} characters." );
        }
        if ( StringUtils.length( scriptArg.getDefaultValue() ) > ScriptArg.MAX_DEFAULT_VALUE_LENGTH )
        {
            errors.rejectValue( "defaultValue", "ScriptArg.defaultValue.length", new Object[]{ ScriptArg.MAX_DEFAULT_VALUE_LENGTH }, "Default value must not be longer than {0} characters." );
        }
        if ( scriptArg.getDataType() == null )
        {
            errors.rejectValue( "dataType", "ScriptArg.dataType.null", "Data type is mandatory." );
        }
        else if ( scriptArg.getDefaultValue() != null )
        {
            try
            {
                if ( scriptArg.isArray() )
                {
                    for ( final String value : ScriptArg.splitArrayValues( scriptArg.getDefaultValue() ) )
                    {
                        scriptArg.getDataType().getFromStringConverter().convert( value );
                    }
                }
                else
                {
                    scriptArg.getDataType().getFromStringConverter().convert( scriptArg.getDefaultValue() );
                }
            }
            catch ( ConversionException e )
            {
                errors.rejectValue( "defaultValue", "ScriptArg.defaultValue.dataType", new Object[]{ scriptArg.getDataType(), e.getMessage() }, "Default value does not match data type {0}: {1}" );
            }
        }
    }
}
