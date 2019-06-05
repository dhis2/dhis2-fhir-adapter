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
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSet;
import org.dhis2.fhir.adapter.fhir.metadata.model.CodeSetValue;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Spring Data REST validator for {@link CodeSet}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveCodeSetValidator extends AbstractBeforeCreateSaveValidator<CodeSet> implements MetadataValidator<CodeSet>
{
    public BeforeCreateSaveCodeSetValidator( @Nonnull EntityManager entityManager )
    {
        super( CodeSet.class, entityManager );
    }

    @Override
    public void doValidate( @Nonnull CodeSet codeSet, @Nonnull Errors errors )
    {
        if ( StringUtils.isBlank( codeSet.getName() ) )
        {
            errors.rejectValue( "name", "CodeSet.name.blank", "Name must not be blank." );
        }
        if ( StringUtils.length( codeSet.getName() ) > CodeSet.MAX_NAME_LENGTH )
        {
            errors.rejectValue( "name", "CodeSet.name.length", new Object[]{ CodeSet.MAX_NAME_LENGTH }, "Name must not be longer than {0} characters." );
        }
        if ( StringUtils.isBlank( codeSet.getCode() ) )
        {
            errors.rejectValue( "code", "CodeSet.code.blank", "Code must not be blank." );
        }
        if ( StringUtils.length( codeSet.getCode() ) > CodeSet.MAX_CODE_LENGTH )
        {
            errors.rejectValue( "code", "CodeSet.code.length", new Object[]{ CodeSet.MAX_CODE_LENGTH }, "Code must not be longer than {0} characters." );
        }
        if ( codeSet.getCodeCategory() == null )
        {
            errors.rejectValue( "codeCategory", "CodeSet.codeCategory.null", "Code category is mandatory." );
        }
        if ( codeSet.getCodeSetValues() != null )
        {
            final Set<UUID> codeIds = new HashSet<>();
            int index = 0;
            for ( final CodeSetValue codeSetValue : codeSet.getCodeSetValues() )
            {
                errors.pushNestedPath( "codeSetValues[" + index + "]" );

                if ( codeSetValue.getCodeSet() == null )
                {
                    codeSetValue.setCodeSet( codeSet );
                }
                else if ( !Objects.equals( codeSetValue.getCodeSet().getId(), codeSet.getId() ) )
                {
                    errors.rejectValue( "codeSet", "CodeSetValue.codeSet.invalid", "Code set value does not belong to code set that includes the value." );
                }
                if ( codeSetValue.getCode() == null )
                {
                    errors.rejectValue( "code", "CodeSetValue.code.null", "Code is mandatory." );
                }
                else
                {
                    if ( !codeIds.add( codeSetValue.getCode().getId() ) )
                    {
                        errors.rejectValue( "code", "CodeSetValue.code.duplicate", "No duplicate codes must be used." );
                    }
                }

                errors.popNestedPath();
                index++;
            }
        }
    }
}
