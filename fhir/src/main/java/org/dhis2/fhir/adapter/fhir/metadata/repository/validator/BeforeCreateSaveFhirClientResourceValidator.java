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
import org.dhis2.fhir.adapter.fhir.metadata.model.DataType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.ScriptType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import reactor.util.annotation.NonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;

/**
 * Spring Data REST validator for {@link FhirClientResource}.
 *
 * @author volsch
 */
@Component
public class BeforeCreateSaveFhirClientResourceValidator extends AbstractBeforeCreateSaveValidator<FhirClientResource> implements MetadataValidator<FhirClientResource>
{
    public BeforeCreateSaveFhirClientResourceValidator( @Nonnull EntityManager entityManager )
    {
        super( FhirClientResource.class, entityManager );
    }

    @Override
    public void doValidate( @Nonnull FhirClientResource fhirClientResource, @Nonnull Errors errors )
    {
        if ( fhirClientResource.getFhirClient() == null )
        {
            errors.rejectValue( "fhirClient", "FhirClientResource.fhirClient.null", "FHIR client is mandatory." );
        }
        else if ( FhirClient.FHIR_REST_INTERFACE_IDS.contains( fhirClientResource.getFhirClient().getId() ) )
        {
            errors.rejectValue( "fhirResourceType", "FhirClientResource.fhirResourceType.reserved", "Adapter specific FHIR client cannot be created or updated." );
        }
        if ( fhirClientResource.getFhirResourceType() == null )
        {
            errors.rejectValue( "fhirResourceType", "FhirClientResource.fhirResourceType.null", "FHIR resource type is mandatory." );
        }
        else
        {
            checkValidTransformInScript( errors, "impTransformScript", fhirClientResource.getFhirResourceType(), fhirClientResource.getImpTransformScript() );
        }
        if ( StringUtils.length( fhirClientResource.getFhirCriteriaParameters() ) > FhirClientResource.MAX_CRITERIA_PARAMETERS_LENGTH )
        {
            errors.rejectValue( "fhirCriteriaParameters", "FhirClientResource.fhirCriteriaParameters.length", new Object[]{ FhirClientResource.MAX_CRITERIA_PARAMETERS_LENGTH },
                "FHIR criteria parameters must not be longer than {0} characters." );
        }
    }

    protected static void checkValidTransformInScript( @NonNull Errors errors, @Nonnull String field, @Nonnull FhirResourceType fhirResourceType, @Nullable ExecutableScript executableScript )
    {
        if ( executableScript == null )
        {
            return;
        }

        checkValidScript( errors, "AbstractRule", field, fhirResourceType, executableScript, ScriptType.TRANSFORM_FHIR, DataType.BOOLEAN );

        if ( (executableScript.getScript().getOutputType() != null) && (executableScript.getScript().getOutputType().getFhirResourceType() != fhirResourceType) )
        {
            errors.rejectValue( field, "FhirClientResource." + field + ".inputType", new Object[]{ fhirResourceType }, "Assigned input type for outgoing transformation script must be the same as for the resource {0}." );
        }
    }
}
