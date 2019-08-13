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

import org.dhis2.fhir.adapter.fhir.metadata.model.ApplicableEnrollmentStatus;
import org.dhis2.fhir.adapter.fhir.metadata.model.ApplicableEventStatus;
import org.dhis2.fhir.adapter.fhir.metadata.model.EventStatusUpdate;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.TransformDataType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;

/**
 * Spring Data REST validator for {@link ProgramStageRule}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveProgramStageRuleValidator extends AbstractBeforeCreateSaveRuleValidator<ProgramStageRule> implements MetadataValidator<ProgramStageRule>
{
    public BeforeCreateSaveProgramStageRuleValidator( @Nonnull EntityManager entityManager )
    {
        super( ProgramStageRule.class, entityManager );
    }

    @Override
    public void doValidate( @Nonnull ProgramStageRule rule, @Nonnull Errors errors )
    {
        validate( rule, TransformDataType.DHIS_EVENT, errors );

        if ( rule.getApplicableEnrollmentStatus() == null )
        {
            rule.setApplicableEnrollmentStatus( new ApplicableEnrollmentStatus() );
        }
        if ( rule.getApplicableEventStatus() == null )
        {
            rule.setApplicableEventStatus( new ApplicableEventStatus() );
        }
        if ( rule.getEventStatusUpdate() == null )
        {
            rule.setEventStatusUpdate( new EventStatusUpdate() );
        }
    }
}
