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

import org.dhis2.fhir.adapter.fhir.metadata.model.DataType;
import org.dhis2.fhir.adapter.fhir.metadata.model.EnrollmentRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptType;
import org.dhis2.fhir.adapter.fhir.metadata.model.TransformDataType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import reactor.util.annotation.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;

/**
 * Spring Data REST validator for {@link EnrollmentRule}.
 *
 * @author Charles Chigoriwa (ITINORDIC)
 */
@Component
public class BeforeCreateSaveEnrollmentRuleValidator extends AbstractBeforeCreateSaveRuleValidator<EnrollmentRule> implements MetadataValidator<EnrollmentRule>
{
    public BeforeCreateSaveEnrollmentRuleValidator( @Nonnull EntityManager entityManager )
    {
        super( EnrollmentRule.class, entityManager );
    }

    @Override
    public void doValidate( @Nonnull EnrollmentRule rule, @Nonnull Errors errors )
    {
        validate( rule, TransformDataType.DHIS_ENROLLMENT, errors );

        checkValidProgramRefLookupScript( errors, "programRefLookupScript", rule.getFhirResourceType(), rule.getProgramRefLookupScript() );
    }

    protected static void checkValidProgramRefLookupScript( @NonNull Errors errors, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nullable ExecutableScript executableScript )
    {
        checkValidScript( errors, "EnrollmentRule", field, fhirResourceType, executableScript, ScriptType.EVALUATE, DataType.PROGRAM_REF );
    }
}
