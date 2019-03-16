package org.dhis2.fhir.adapter.fhir.metadata.repository.validator;

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
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScriptArg;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptArg;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Spring Data REST validator for {@link ExecutableScriptArg}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveExecutableScriptArgValidator implements Validator
{
    @Override
    public boolean supports( @Nonnull Class<?> clazz )
    {
        return ExecutableScriptArg.class.isAssignableFrom( clazz );
    }

    @Override
    public void validate( Object target, @Nonnull Errors errors )
    {
        final ExecutableScriptArg executableScriptArg = (ExecutableScriptArg) target;

        if ( executableScriptArg.getScript() == null )
        {
            errors.rejectValue( "script", "ExecutableScriptArg.script.null", "Script is mandatory." );
        }
        else if ( ( executableScriptArg.getArgument() != null ) && !Objects.equals( executableScriptArg.getArgument().getScript().getId(), executableScriptArg.getScript().getScript().getId() ) )
        {
            errors.rejectValue( "argument", "ExecutableScriptArg.argument.script", "Argument does not belong to script of executable script." );
        }
        if ( executableScriptArg.getArgument() == null )
        {
            errors.rejectValue( "argument", "ExecutableScriptArg.argument.null", "Argument is mandatory." );
        }
        if ( (executableScriptArg.getOverrideValue() == null) && (executableScriptArg.getArgument().isMandatory()) )
        {
            errors.rejectValue( "overrideValue", "ExecutableScriptArg.overrideValue.null", "Argument is mandatory and override value must not be null." );
        }
        if ( StringUtils.length( executableScriptArg.getOverrideValue() ) > ScriptArg.MAX_DEFAULT_VALUE_LENGTH )
        {
            errors.rejectValue( "overrideValue", "ExecutableScript.overrideValue.length", new Object[]{ ScriptArg.MAX_DEFAULT_VALUE_LENGTH }, "Override value must not be longer than {0} characters." );
        }
        if ( executableScriptArg.getArgument() != null && executableScriptArg.getOverrideValue() != null )
        {
            try
            {
                if ( executableScriptArg.getArgument().isArray() )
                {
                    for ( final String value : ScriptArg.splitArrayValues( executableScriptArg.getOverrideValue() ) )
                    {
                        executableScriptArg.getArgument().getDataType().getFromStringConverter().convert( value );
                    }
                }
                else
                {
                    executableScriptArg.getArgument().getDataType().getFromStringConverter().convert( executableScriptArg.getOverrideValue() );
                }
            }
            catch ( ConversionException e )
            {
                errors.rejectValue( "overrideValue", "ExecutableScriptArg.overrideValue.dataType",
                    new Object[]{ executableScriptArg.getArgument().getDataType(), e.getMessage() }, "Override value does not match data type {0}: {1}" );
            }
        }
    }
}
