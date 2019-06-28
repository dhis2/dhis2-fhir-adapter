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
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import reactor.util.annotation.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;

/**
 * Spring Data REST validator for {@link FhirResourceMapping}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveFhirResourceMappingValidator extends AbstractBeforeCreateSaveValidator<FhirResourceMapping> implements MetadataValidator<FhirResourceMapping>
{
    public BeforeCreateSaveFhirResourceMappingValidator( @Nonnull EntityManager entityManager )
    {
        super( FhirResourceMapping.class, entityManager );
    }

    @Override
    public void doValidate( @Nonnull FhirResourceMapping fhirResourceMapping, @Nonnull Errors errors )
    {
        if ( fhirResourceMapping.getFhirResourceType() == null )
        {
            errors.rejectValue( "fhirResourceType", "FhirResourceMapping.fhirResourceType.null", "FHIR Resource Type is mandatory." );
        }
        if ( fhirResourceMapping.getTrackedEntityFhirResourceType() == null )
        {
            errors.rejectValue( "trackedEntityFhirResourceType", "FhirResourceMapping.trackedEntityFhirResourceType.null", "Tracked Entity FHIR Resource Type is mandatory." );
        }
        if ( fhirResourceMapping.getFhirResourceType() != null )
        {
            checkValidTeiLookupScript( errors, "impTeiLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpTeiLookupScript() );
            checkValidOrgLookupScript( errors, "FhirResourceMapping.", "impEnrollmentOrgLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpEnrollmentOrgLookupScript() );
            checkValidOrgLookupScript( errors, "FhirResourceMapping.", "impEventOrgLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpEventOrgLookupScript() );
            checkValidLocationLookupScript( errors, "FhirResourceMapping.", "impEnrollmentGeoLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpEnrollmentGeoLookupScript() );
            checkValidLocationLookupScript( errors, "FhirResourceMapping.", "impEventGeoLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpEventGeoLookupScript() );
            checkValidDateLookupScript( errors, "impEnrollmentDateLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpEnrollmentDateLookupScript() );
            checkValidDateLookupScript( errors, "impEventDateLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpEventDateLookupScript() );
            checkValidDateLookupScript( errors, "impEffectiveDateLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpEffectiveDateLookupScript() );
            checkValidScript( errors, "FhirResourceMapping", "impProgramStageRefLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpProgramStageRefLookupScript(), ScriptType.EVALUATE, DataType.PROGRAM_STAGE_REF );
        }
    }

    public static void checkValidOrgLookupScript( @NonNull Errors errors, @Nonnull String codePrefix, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nullable ExecutableScript executableScript )
    {
        checkValidScript( errors, codePrefix, field, fhirResourceType, executableScript, ScriptType.EVALUATE, DataType.ORG_UNIT_REF );
    }

    public static void checkValidLocationLookupScript( @NonNull Errors errors, @Nonnull String codePrefix, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nullable ExecutableScript executableScript )
    {
        checkValidScript( errors, codePrefix, field, fhirResourceType, executableScript, ScriptType.EVALUATE, DataType.LOCATION );
    }

    protected static void checkValidDateLookupScript( @NonNull Errors errors, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nullable ExecutableScript executableScript )
    {
        checkValidScript( errors, "FhirResourceMapping", field, fhirResourceType, executableScript, ScriptType.EVALUATE, DataType.DATE_TIME );
    }

    protected static void checkValidTeiLookupScript( @NonNull Errors errors, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nullable ExecutableScript executableScript )
    {
        checkValidScript( errors, "FhirResourceMapping", field, fhirResourceType, executableScript, ScriptType.EVALUATE, DataType.FHIR_RESOURCE );
    }
}
