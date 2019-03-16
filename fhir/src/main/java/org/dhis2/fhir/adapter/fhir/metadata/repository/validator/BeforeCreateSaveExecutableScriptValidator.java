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
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScriptArg;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Spring Data REST validator for {@link ExecutableScript}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveExecutableScriptValidator implements Validator
{
    private final BeforeCreateSaveExecutableScriptArgValidator argValidator;

    public BeforeCreateSaveExecutableScriptValidator( @Nonnull BeforeCreateSaveExecutableScriptArgValidator argValidator )
    {
        this.argValidator = argValidator;
    }

    @Override
    public boolean supports( @Nonnull Class<?> clazz )
    {
        return ExecutableScript.class.isAssignableFrom( clazz );
    }

    @Override
    public void validate( Object target, @Nonnull Errors errors )
    {
        final ExecutableScript executableScript = (ExecutableScript) target;

        if ( executableScript.getScript() == null )
        {
            errors.rejectValue( "script", "ExecutableScript.script.null", "Script is mandatory." );
        }
        if ( StringUtils.isBlank( executableScript.getName() ) )
        {
            errors.rejectValue( "name", "ExecutableScript.name.blank", "Name must not be blank." );
        }
        if ( StringUtils.length( executableScript.getName() ) > ExecutableScript.MAX_NAME_LENGTH )
        {
            errors.rejectValue( "name", "ExecutableScript.name.length", new Object[]{ ExecutableScript.MAX_NAME_LENGTH }, "Name must not be longer than {0} characters." );
        }
        if ( StringUtils.isBlank( executableScript.getCode() ) )
        {
            errors.rejectValue( "code", "ExecutableScript.code.blank", "Code must not be blank." );
        }
        if ( StringUtils.length( executableScript.getCode() ) > ExecutableScript.MAX_CODE_LENGTH )
        {
            errors.rejectValue( "code", "ExecutableScript.code.length", new Object[]{ ExecutableScript.MAX_CODE_LENGTH }, "Code must not be longer than {0} characters." );
        }

        if ( executableScript.getScript() != null )
        {
            if ( executableScript.getOverrideArguments() != null )
            {
                final Set<UUID> argIds = new HashSet<>();
                int index = 0;
                for ( final ExecutableScriptArg arg : executableScript.getOverrideArguments() )
                {
                    errors.pushNestedPath( "overrideArguments[" + index + "]" );
                    if ( !Objects.equals( arg.getScript().getId(), executableScript.getId() ) )
                    {
                        errors.rejectValue( "script", "ExecutableScript.overrideArguments.script",
                            "Executable script argument does not reference executable script to which it belongs to." );
                    }
                    if ( arg.getArgument() != null && !argIds.add( arg.getArgument().getId() ) )
                    {
                        errors.rejectValue( null, "ExecutableScript.overrideArguments.duplicate",
                            new Object[]{ arg.getArgument().getId() }, "Duplicate override argument for argument {0}." );
                    }
                    argValidator.validate( arg, errors );
                    errors.popNestedPath();
                    index++;
                }
            }
        }
    }
}
