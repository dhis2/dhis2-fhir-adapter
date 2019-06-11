package org.dhis2.fhir.adapter.fhir.server.provider;

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

import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Abstract implementation of patient resource provider.
 *
 * @param <T> the concrete type of the versioned patient resource.
 * @author volsch
 */
public abstract class AbstractPatientResourceProvider<T extends IBaseResource> extends AbstractReadWriteResourceProvider<T>
{
    protected static final Map<FhirResourceType, String> PATIENT_SEARCH_PARAMS;

    static
    {
        final Map<FhirResourceType, String> patientSearchParams = new LinkedHashMap<>();

        patientSearchParams.put( FhirResourceType.OBSERVATION, "patient" );
        patientSearchParams.put( FhirResourceType.CONDITION, "patient" );
        patientSearchParams.put( FhirResourceType.IMMUNIZATION, "patient" );
        patientSearchParams.put( FhirResourceType.ENCOUNTER, "patient" );
        patientSearchParams.put( FhirResourceType.DIAGNOSTIC_REPORT, "patient" );
        patientSearchParams.put( FhirResourceType.MEDICATION_REQUEST, "patient" );

        PATIENT_SEARCH_PARAMS = Collections.unmodifiableMap( patientSearchParams );
    }

    protected AbstractPatientResourceProvider( @Nonnull Class<T> resourceClass, @Nonnull FhirClientResourceRepository fhirClientResourceRepository, @Nonnull FhirClientSystemRepository fhirClientSystemRepository,
        @Nonnull FhirRepository fhirRepository, @Nonnull DhisRepository dhisRepository )
    {
        super( resourceClass, fhirClientResourceRepository, fhirClientSystemRepository, fhirRepository, dhisRepository );
    }

    protected void patientInstanceEverything( @Nonnull IIdType patientId, @Nonnull BiConsumer<FhirResourceType, List<IBaseResource>> resourceConsumer )
    {
        final List<String> patientFilterValues = Collections.singletonList( "Patient/" + patientId.getIdPart() );

        executeInSecurityContext( () -> {
            PATIENT_SEARCH_PARAMS.forEach( ( ( fhirResourceType, patientSearchParam ) -> {
                final Map<String, List<String>> filter = Collections.singletonMap( patientSearchParam, patientFilterValues );

                resourceConsumer.accept( fhirResourceType, getDhisRepository().search( getFhirClientResource( fhirResourceType ).getFhirClient(), fhirResourceType,
                    null, true, null, filter, null ).getResources( 0, Integer.MAX_VALUE ) );
            } ) );
            return null;
        } );
    }

    @Nonnull
    protected String createFullUrl( @Nonnull RequestDetails requestDetail, @Nonnull FhirResourceType fhirResourceType, @Nonnull IBaseResource resource )
    {
        return requestDetail.getServerBaseForRequest() + "/" + fhirResourceType.getResourceTypeName() + "/" + resource.getIdElement().getIdPart();
    }
}
