package org.dhis2.fhir.adapter.prototype.fhir.transform.scripted.util;

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

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.AdditionalRequestHeadersInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.dhis2.fhir.adapter.prototype.Scriptable;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Scriptable
public class FhirClientTransformUtils extends AbstractTransformUtils
{
    private static final String SCRIPT_ATTR_NAME = "fhirClientUtils";

    private final FhirContext fhirContext = FhirContext.forDstu3();

    @Value( "${dhis2.subscription.remote.baseUrl}" )
    private String remoteBaseUrl;

    @Value( "${dhis2.subscription.remote.authorizationHeader}" )
    private String remoteAuthorizationHeader;

    @Override
    public String getScriptAttrName()
    {
        return SCRIPT_ATTR_NAME;
    }

    public Resource queryLatest( @Nonnull String resourceName, String... filter )
    {
        final Map<String, List<String>> filterMap = new HashMap<>();
        for ( int i = 0; i < filter.length; i += 2 )
        {
            final String key = filter[i];
            final String value = (i + 1 >= filter.length) ? null : filter[i + 1];
            filterMap.computeIfAbsent( key, k -> new ArrayList<>() ).add( value );
        }

        final Bundle result = createFhirClient().search().forResource( resourceName ).count( 1 )
            .whereMap( filterMap ).sort().descending( "_lastUpdated" ).returnBundle( Bundle.class ).execute();
        return result.isEmpty() ? null : result.getEntryFirstRep().getResource();
    }

    protected IGenericClient createFhirClient()
    {
        final IGenericClient client = fhirContext.newRestfulGenericClient( remoteBaseUrl );
        client.registerInterceptor( new LoggingInterceptor( true ) );

        if ( StringUtils.isNotBlank( remoteAuthorizationHeader ) )
        {
            final AdditionalRequestHeadersInterceptor requestHeadersInterceptor = new AdditionalRequestHeadersInterceptor();
            requestHeadersInterceptor.addHeaderValue( "Authorization", remoteAuthorizationHeader );
            client.registerInterceptor( requestHeadersInterceptor );
        }
        return client;
    }
}
