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

import org.dhis2.fhir.adapter.fhir.metadata.model.DataType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import reactor.util.annotation.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Spring Data REST validator for {@link FhirResourceMapping}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveFhirResourceMappingValidator implements Validator
{
    @Override
    public boolean supports( @Nonnull Class<?> clazz )
    {
        return FhirResourceMapping.class.isAssignableFrom( clazz );
    }

    @Override
    public void validate( Object target, @Nonnull Errors errors )
    {
        final FhirResourceMapping fhirResourceMapping = (FhirResourceMapping) target;

        if ( fhirResourceMapping.getFhirResourceType() == null )
        {
            errors.rejectValue( "fhirResourceType", "FhirResourceMapping.fhirResourceType.null", "FHIR Resource Type is mandatory." );
        }
        if ( fhirResourceMapping.getFhirResourceType() != null )
        {
            checkValidTeiLookupScript( errors, "teiLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpTeiLookupScript() );
            checkValidOrgLookupScript( errors, "FhirResourceMapping.", "enrollmentOrgLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpEnrollmentOrgLookupScript() );
            checkValidOrgLookupScript( errors, "FhirResourceMapping.", "eventOrgLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpEventOrgLookupScript() );
            checkValidLocationLookupScript( errors, "FhirResourceMapping.", "enrollmentLocationLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpEnrollmentGeoLookupScript() );
            checkValidLocationLookupScript( errors, "FhirResourceMapping.", "eventLocationLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpEventGeoLookupScript() );
            checkValidDateLookupScript( errors, "enrollmentDateLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpEnrollmentDateLookupScript() );
            checkValidDateLookupScript( errors, "eventDateLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpEventDateLookupScript() );
            checkValidDateLookupScript( errors, "effectiveDateLookupScript", fhirResourceMapping.getFhirResourceType(), fhirResourceMapping.getImpEffectiveDateLookupScript() );
        }
    }

    public static void checkValidOrgLookupScript( @NonNull Errors errors, @Nonnull String codePrefix, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nullable ExecutableScript executableScript )
    {
        if ( executableScript == null )
        {
            return;
        }
        if ( executableScript.getScript().getScriptType() != ScriptType.EVALUATE )
        {
            errors.rejectValue( field, codePrefix + field + ".scriptType", "Assigned script type for organization unit lookup must be EVALUATE." );
        }
        if ( executableScript.getScript().getReturnType() != DataType.ORG_UNIT_REF )
        {
            errors.rejectValue( field, codePrefix + field + ".returnType", "Assigned return type for organization unit lookup must be ORG_UNIT_REF." );
        }
        if ( (executableScript.getScript().getInputType() != null) && (executableScript.getScript().getInputType().getFhirResourceType() != fhirResourceType) )
        {
            errors.rejectValue( field, codePrefix + field + ".inputType", new Object[]{ fhirResourceType }, "Assigned input type for organization unit lookup must be the same as for the mapping {0}." );
        }
    }

    public static void checkValidLocationLookupScript( @NonNull Errors errors, @Nonnull String codePrefix, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nullable ExecutableScript executableScript )
    {
        if ( executableScript == null )
        {
            return;
        }
        if ( executableScript.getScript().getScriptType() != ScriptType.EVALUATE )
        {
            errors.rejectValue( field, codePrefix + field + ".scriptType", "Assigned script type for location lookup must be EVALUATE." );
        }
        if ( executableScript.getScript().getReturnType() != DataType.LOCATION )
        {
            errors.rejectValue( field, codePrefix + field + ".returnType", "Assigned return type for location lookup must be LOCATION." );
        }
        if ( (executableScript.getScript().getInputType() != null) && (executableScript.getScript().getInputType().getFhirResourceType() != fhirResourceType) )
        {
            errors.rejectValue( field, codePrefix + field + ".inputType", new Object[]{ fhirResourceType }, "Assigned input type for location lookup must be the same as for the mapping {0}." );
        }
    }

    protected static void checkValidDateLookupScript( @NonNull Errors errors, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nullable ExecutableScript executableScript )
    {
        if ( executableScript == null )
        {
            return;
        }
        if ( executableScript.getScript().getScriptType() != ScriptType.EVALUATE )
        {
            errors.rejectValue( field, "FhirResourceMapping." + field + ".scriptType", "Assigned script type for date lookup must be EVALUATE." );
        }
        if ( executableScript.getScript().getReturnType() != DataType.DATE_TIME )
        {
            errors.rejectValue( field, "FhirResourceMapping." + field + ".returnType", "Assigned return type for date lookup must be DATE_TIME." );
        }
        if ( (executableScript.getScript().getInputType() != null) && (executableScript.getScript().getInputType().getFhirResourceType() != fhirResourceType) )
        {
            errors.rejectValue( field, "FhirResourceMapping." + field + ".inputType", new Object[]{ fhirResourceType }, "Assigned input type for date lookup must be the same as for the mapping {0}." );
        }
    }

    protected static void checkValidTeiLookupScript( @NonNull Errors errors, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nullable ExecutableScript executableScript )
    {
        if ( executableScript == null )
        {
            return;
        }
        if ( executableScript.getScript().getScriptType() != ScriptType.EVALUATE )
        {
            errors.rejectValue( field, "FhirResourceMapping." + field + ".scriptType", "Assigned script type for tracked entity instance lookup must be EVALUATE." );
        }
        if ( executableScript.getScript().getReturnType() != DataType.FHIR_RESOURCE )
        {
            errors.rejectValue( field, "FhirResourceMapping." + field + ".returnType", "Assigned return type for tracked entity instance lookup must be FHIR_RESOURCE." );
        }
        if ( (executableScript.getScript().getInputType() != null) && (executableScript.getScript().getInputType().getFhirResourceType() != fhirResourceType) )
        {
            errors.rejectValue( field, "FhirResourceMapping." + field + ".inputType", new Object[]{ fhirResourceType }, "Assigned input type for tracked entity instance lookup must be the same as for the mapping {0}." );
        }
    }
}
