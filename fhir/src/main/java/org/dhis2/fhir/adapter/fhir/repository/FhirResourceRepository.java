package org.dhis2.fhir.adapter.fhir.repository;

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

import ca.uhn.fhir.context.FhirContext;
import org.dhis2.fhir.adapter.fhir.metadata.model.ClientFhirEndpoint;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClient;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.model.SystemCodeValue;
import org.hl7.fhir.instance.model.api.IBaseResource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * Utility methods to access a client FHIR repository (a FHIR service/client).
 *
 * @author volsch
 */
public interface FhirResourceRepository
{
    @Nonnull
    Optional<FhirContext> findFhirContext( @Nonnull FhirVersion fhirVersion );

    @Nonnull
    Optional<IBaseResource> findRefreshed( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull String resourceId );

    @Nonnull
    Optional<IBaseResource> findRefreshed( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull String resourceId, boolean transform );

    @Nonnull
    Optional<IBaseResource> find( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull String resourceId );

    @Nonnull
    Optional<IBaseResource> findRefreshedByIdentifier( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull SystemCodeValue identifier );

    @Nonnull
    Optional<IBaseResource> findByIdentifier( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull SystemCodeValue identifier );

    @Nonnull
    Optional<IBaseResource> findRefreshedByCode( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull SystemCodeValue code );

    @Nonnull
    Optional<IBaseResource> findByCode( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nonnull ClientFhirEndpoint fhirEndpoint, @Nonnull String resourceType, @Nonnull SystemCodeValue code );

    @Nullable
    IBaseResource transform( @Nonnull UUID fhirClientId, @Nonnull FhirVersion fhirVersion, @Nullable IBaseResource resource );

    @Nonnull
    IBaseResource save( @Nonnull FhirClient fhirClient, @Nonnull IBaseResource resource, @Nullable String dhisResourceId );

    boolean delete( @Nonnull FhirClient fhirClient, @Nonnull IBaseResource resource );
}
