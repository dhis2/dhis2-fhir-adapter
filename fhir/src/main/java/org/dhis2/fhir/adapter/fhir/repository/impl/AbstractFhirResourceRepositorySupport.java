package org.dhis2.fhir.adapter.fhir.repository.impl;

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

import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerResource;
import org.dhis2.fhir.adapter.fhir.model.FhirVersionRestricted;
import org.hl7.fhir.instance.model.api.IAnyResource;
import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Abstract FHIR version dependent base class for FHIR resource repository utils.
 *
 * @author volsch
 */
public abstract class AbstractFhirResourceRepositorySupport implements FhirVersionRestricted
{
    @Nonnull
    protected abstract AbstractFhirRepositoryResourceUtils createFhirRepositoryResourceUtils( @Nonnull UUID fhirServerId );

    @Nonnull
    protected abstract IAnyResource createFhirSubscription( @Nonnull FhirServerResource fhirServerResource );

    @Nullable
    protected abstract IBaseResource getFirstResource( @Nonnull IBaseBundle bundle );

    @Nonnull
    protected abstract Class<? extends IBaseBundle> getBundleClass();

    @Nonnull
    protected abstract IBaseBundle createBundle( @Nonnull List<? extends IBaseResource> resources );

    @Nonnull
    protected String createWebHookUrl( @Nonnull FhirServerResource fhirServerResource )
    {
        final StringBuilder url = new StringBuilder( fhirServerResource.getFhirServer().getAdapterEndpoint().getBaseUrl() );
        if ( url.charAt( url.length() - 1 ) != '/' )
        {
            url.append( '/' );
        }
        url.append( "remote-fhir-rest-hook/" );
        url.append( fhirServerResource.getFhirServer().getId() );
        url.append( '/' );
        url.append( fhirServerResource.getId() );
        return url.toString();
    }
}
