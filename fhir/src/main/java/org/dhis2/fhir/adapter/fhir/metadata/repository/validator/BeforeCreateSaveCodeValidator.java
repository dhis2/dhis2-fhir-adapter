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
import org.dhis2.fhir.adapter.fhir.metadata.model.Code;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

/**
 * Spring Data REST validator for {@link Code}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveCodeValidator extends AbstractBeforeCreateSaveValidator<Code> implements MetadataValidator<Code>
{
    public BeforeCreateSaveCodeValidator( @Nonnull EntityManager entityManager )
    {
        super( Code.class, entityManager );
    }

    @Override
    public void doValidate( @Nonnull Code code, @Nonnull Errors errors )
    {
        if ( code.getCodeCategory() == null )
        {
            errors.rejectValue( "codeCategory", "Code.codeCategory", "Code category is mandatory." );
        }
        if ( StringUtils.isBlank( code.getName() ) )
        {
            errors.rejectValue( "name", "Code.name.blank", "Name must not be blank." );
        }
        if ( StringUtils.length( code.getName() ) > Code.MAX_NAME_LENGTH )
        {
            errors.rejectValue( "name", "Code.name.length", new Object[]{ Code.MAX_NAME_LENGTH }, "Name must not be longer than {0} characters." );
        }
        if ( StringUtils.isBlank( code.getCode() ) )
        {
            errors.rejectValue( "code", "Code.code.blank", "Code must not be blank." );
        }
        if ( StringUtils.length( code.getCode() ) > Code.MAX_CODE_LENGTH )
        {
            errors.rejectValue( "code", "Code.code.length", new Object[]{ Code.MAX_CODE_LENGTH }, "Code must not be longer than {0} characters." );
        }
        if ( (code.getMappedCode() != null) && StringUtils.isBlank( code.getMappedCode() ) )
        {
            errors.rejectValue( "mappedCode", "Code.mappedCode.blank", "Mapped code must not be blank." );
        }
        if ( StringUtils.length( code.getMappedCode() ) > Code.MAX_MAPPED_CODE_LENGTH )
        {
            errors.rejectValue( "mappedCode", "Code.mappedCode.length", new Object[]{ Code.MAX_MAPPED_CODE_LENGTH }, "Mapped code must not be longer than {0} characters." );
        }
    }
}
