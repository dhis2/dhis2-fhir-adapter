package org.dhis2.fhir.adapter.prototype.fhir.provider;

/*
 *  Copyright (c) 2004-2018, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import ca.uhn.fhir.rest.annotation.Transaction;
import ca.uhn.fhir.rest.annotation.TransactionParam;
import ca.uhn.fhir.rest.api.Constants;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.dhis2.fhir.adapter.prototype.fhir.model.FhirResourceType;
import org.dhis2.fhir.adapter.prototype.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.prototype.fhir.transform.FhirToDhisTransformerService;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryResponseComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
public class BundleResourceProvider implements IResourceProvider
{
    private final FhirToDhisTransformerService fhirToDhisTransformerService;

    public BundleResourceProvider( @Nonnull FhirToDhisTransformerService fhirToDhisTransformerService )
    {
        this.fhirToDhisTransformerService = fhirToDhisTransformerService;
    }

    @Override public Class<Bundle> getResourceType()
    {
        return Bundle.class;
    }

    @Transaction
    public Bundle transaction( @TransactionParam Bundle bundle )
    {
        final Bundle resultBundle = new Bundle().setType( BundleType.TRANSACTIONRESPONSE );
        for ( BundleEntryComponent entry : bundle.getEntry() )
        {
            fhirToDhisTransformerService.transform( fhirToDhisTransformerService.createContext( FhirResourceType.PATIENT, FhirVersion.DSTU3 ), entry.getResource() );

            resultBundle.addEntry().setResponse( new BundleEntryResponseComponent( new StringType( String.valueOf( Constants.STATUS_HTTP_200_OK ) ) ) );
        }
        return resultBundle;
    }
}
