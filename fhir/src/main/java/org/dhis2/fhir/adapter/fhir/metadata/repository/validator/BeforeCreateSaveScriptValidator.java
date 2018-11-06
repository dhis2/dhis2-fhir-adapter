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
import org.dhis2.fhir.adapter.fhir.metadata.model.Script;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Nonnull;

/**
 * Spring Data REST validator for {@link Script}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveScriptValidator implements Validator
{
    @Override
    public boolean supports( @Nonnull Class<?> clazz )
    {
        return Script.class.isAssignableFrom( clazz );
    }

    @Override
    public void validate( Object target, @Nonnull Errors errors )
    {
        final Script script = (Script) target;

        if ( StringUtils.isBlank( script.getName() ) )
        {
            errors.rejectValue( "name", "Script.name.blank", "Name must not be blank." );
        }
        if ( StringUtils.length( script.getName() ) > Script.MAX_NAME_LENGTH )
        {
            errors.rejectValue( "name", "Script.name.length", new Object[]{ Script.MAX_NAME_LENGTH }, "Name must not be longer than {0} characters." );
        }
        if ( StringUtils.isBlank( script.getCode() ) )
        {
            errors.rejectValue( "code", "Script.code.blank", "Code must not be blank." );
        }
        if ( StringUtils.length( script.getCode() ) > Script.MAX_CODE_LENGTH )
        {
            errors.rejectValue( "code", "Script.code.length", new Object[]{ Script.MAX_CODE_LENGTH }, "Code must not be longer than {0} characters." );
        }
        if ( script.getScriptType() == null )
        {
            errors.rejectValue( "scriptType", "Script.scriptType.null", "Script type is mandatory." );
        }
        else if ( script.getScriptType() == ScriptType.TRANSFORM_TO_DHIS )
        {
            if ( script.getInputType() == null )
            {
                errors.rejectValue( "inputType", "Script.inputType.null", "Input type is mandatory when script is a transformation script." );
            }
            if ( script.getOutputType() == null )
            {
                errors.rejectValue( "outputType", "Script.outputType.null", "Output type is mandatory when script is a transformation script." );
            }
        }
        else if ( script.getScriptType() == ScriptType.EVALUATE )
        {
            if ( script.getReturnType() == null )
            {
                errors.rejectValue( "returnType", "Script.returnType.null", "Return type is mandatory when script is an evaluation script." );
            }
            if ( script.getOutputType() != null )
            {
                errors.rejectValue( "outputType", "Script.outputType.notNull", "Output type must not be specified when script is an evaluation script." );
            }
        }
    }
}
