package org.dhis2.fhir.adapter.fhir.metadata.model;

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

import javax.annotation.Nullable;

/**
 * The data type of the input or output variable of a transformation.
 *
 * @author volsch
 * @author Charles Chigoriwa (ITINORDIC)
 */
public enum TransformDataType
{
    DHIS_ORGANIZATION_UNIT( null ),
    DHIS_TRACKED_ENTITY_INSTANCE( null ),
    DHIS_ENROLLMENT( null ),
    DHIS_EVENT( null ),
    DHIS_DATA_VALUE_SET( null ),
    FHIR_ENCOUNTER( FhirResourceType.ENCOUNTER ),
    FHIR_LOCATION( FhirResourceType.LOCATION ),
    FHIR_ORGANIZATION( FhirResourceType.ORGANIZATION ),
    FHIR_PATIENT( FhirResourceType.PATIENT ),
    FHIR_IMMUNIZATION( FhirResourceType.IMMUNIZATION ),
    FHIR_OBSERVATION( FhirResourceType.OBSERVATION ),
    FHIR_DIAGNOSTIC_REPORT( FhirResourceType.DIAGNOSTIC_REPORT ),
    FHIR_RELATED_PERSON( FhirResourceType.RELATED_PERSON ),
    FHIR_CONDITION( FhirResourceType.CONDITION ),
    FHIR_MEDICATION_REQUEST( FhirResourceType.MEDICATION_REQUEST ),
    FHIR_PRACTITIONER( FhirResourceType.PRACTITIONER ),
    FHIR_PLAN_DEFINITION( FhirResourceType.PLAN_DEFINITION ),
    FHIR_QUESTIONNAIRE( FhirResourceType.QUESTIONNAIRE ),
    FHIR_CARE_PLAN( FhirResourceType.CARE_PLAN ),
    FHIR_QUESTIONNAIRE_RESPONSE( FhirResourceType.QUESTIONNAIRE_RESPONSE ),
    FHIR_MEASURE_REPORT( FhirResourceType.MEASURE_REPORT );

    private final FhirResourceType fhirResourceType;

    TransformDataType( @Nullable FhirResourceType fhirResourceType )
    {
        this.fhirResourceType = fhirResourceType;
    }

    @Nullable
    public FhirResourceType getFhirResourceType()
    {
        return fhirResourceType;
    }
}
