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
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.annotation.Nonnull;

/**
 * Spring Data REST validator for {@link ScriptSource}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveScriptSourceValidator implements Validator
{
    @Override
    public boolean supports( @Nonnull Class<?> clazz )
    {
        return ScriptSource.class.isAssignableFrom( clazz );
    }

    @Override
    public void validate( Object target, @Nonnull Errors errors )
    {
        final ScriptSource scriptSource = (ScriptSource) target;

        if ( scriptSource.getScript() == null )
        {
            errors.rejectValue( "script", "ScriptSource.script.null", "Script is mandatory." );
        }
        if ( StringUtils.isBlank( scriptSource.getSourceText() ) )
        {
            errors.rejectValue( "sourceText", "ScriptSource.sourceText.blank", "Source text must not be blank." );
        }
        if ( scriptSource.getSourceType() == null )
        {
            errors.rejectValue( "sourceType", "ScriptSource.sourceType.null", "Source type is mandatory." );
        }
        if ( (scriptSource.getFhirVersions() == null) || scriptSource.getFhirVersions().isEmpty() )
        {
            errors.rejectValue( "fhirVersions", "ScriptSource.fhirVersions.empty", "At least one FHIR version must be specified." );
        }
    }
}
