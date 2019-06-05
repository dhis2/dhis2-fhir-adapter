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
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeCategory;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

/**
 * Spring Data REST validator for {@link CodeCategory}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveCodeCategoryValidator extends AbstractBeforeCreateSaveValidator<CodeCategory> implements MetadataValidator<CodeCategory>
{
    public BeforeCreateSaveCodeCategoryValidator( @Nonnull EntityManager entityManager )
    {
        super( CodeCategory.class, entityManager );
    }

    @Override
    public void doValidate( @Nonnull CodeCategory codeCategory, @Nonnull Errors errors )
    {
        if ( StringUtils.isBlank( codeCategory.getName() ) )
        {
            errors.rejectValue( "name", "CodeCategory.name.blank", "Name must not be blank." );
        }
        if ( StringUtils.length( codeCategory.getName() ) > CodeCategory.MAX_NAME_LENGTH )
        {
            errors.rejectValue( "name", "CodeCategory.name.length", new Object[]{ CodeCategory.MAX_NAME_LENGTH }, "Name must not be longer than {0} characters." );
        }
        if ( StringUtils.isBlank( codeCategory.getCode() ) )
        {
            errors.rejectValue( "code", "CodeCategory.code.blank", "Code must not be blank." );
        }
        if ( StringUtils.length( codeCategory.getCode() ) > CodeCategory.MAX_CODE_LENGTH )
        {
            errors.rejectValue( "code", "CodeCategory.code.length", new Object[]{ CodeCategory.MAX_CODE_LENGTH }, "Code must not be longer than {0} characters." );
        }
    }
}
