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

import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.server.IPagingProvider;
import org.dhis2.fhir.adapter.fhir.transform.config.FhirRestInterfaceConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * A paging provider that do not support paging.
 *
 * @author volsch
 */
@Component
public class NonPagingProvider implements IPagingProvider
{
    private final FhirRestInterfaceConfig restInterfaceConfig;

    public NonPagingProvider( @Nonnull FhirRestInterfaceConfig restInterfaceConfig )
    {
        this.restInterfaceConfig = restInterfaceConfig;
    }

    @Override
    public int getDefaultPageSize()
    {
        return restInterfaceConfig.getDefaultSearchCount();
    }

    @Override
    public int getMaximumPageSize()
    {
        return restInterfaceConfig.getMaxSearchCount();
    }

    @Override
    public IBundleProvider retrieveResultList( String theSearchId )
    {
        // retrieving result list is not supported
        return null;
    }

    @Override
    public String storeResultList( IBundleProvider theList )
    {
        // storing result list is not supported
        return null;
    }
}
