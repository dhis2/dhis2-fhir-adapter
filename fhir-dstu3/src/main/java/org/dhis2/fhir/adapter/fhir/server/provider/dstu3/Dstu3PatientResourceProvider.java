package org.dhis2.fhir.adapter.fhir.server.provider.dstu3;

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

import ca.uhn.fhir.rest.annotation.Count;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.RawParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientSystemRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.DhisRepository;
import org.dhis2.fhir.adapter.fhir.repository.FhirRepository;
import org.dhis2.fhir.adapter.fhir.server.provider.AbstractPatientResourceProvider;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * DSTU3 resource provider.
 *
 * @author volsch
 */
@Component
public class Dstu3PatientResourceProvider extends AbstractPatientResourceProvider<Patient>
{
    public Dstu3PatientResourceProvider( @Nonnull FhirClientResourceRepository fhirClientResourceRepository, @Nonnull FhirClientSystemRepository fhirClientSystemRepository,
        @Nonnull FhirRepository fhirRepository, @Nonnull DhisRepository dhisRepository )
    {
        super( Patient.class, fhirClientResourceRepository, fhirClientSystemRepository, fhirRepository, dhisRepository );
    }

    @Nonnull
    @Override
    public FhirVersion getFhirVersion()
    {
        return FhirVersion.DSTU3;
    }

    @Nonnull
    @Operation( name = "$everything", idempotent = true )
    public Bundle patientInstanceEverything( @Nonnull RequestDetails requestDetails, @IdParam IIdType patientId )
    {
        validateUseCase( requestDetails );

        final Bundle bundle = new Bundle();
        patientInstanceEverything( patientId, ( fhirResourceType, resources ) -> resources.forEach( r -> bundle.addEntry().setResource( (Resource) r )
            .setFullUrl( createFullUrl( requestDetails, fhirResourceType, r ) ) ) );

        return bundle;
    }

    @Search( allowUnknownParams = true )
    @Nonnull
    public IBundleProvider search( @Nonnull RequestDetails requestDetails, @Nullable @Count Integer count, @Nullable @OptionalParam( name = SP_LAST_UPDATED ) DateRangeParam lastUpdatedDateRange, @Nullable @RawParam Map<String, List<String>> filter )
    {
        return search( requestDetails, count, null, lastUpdatedDateRange, filter );
    }
}
