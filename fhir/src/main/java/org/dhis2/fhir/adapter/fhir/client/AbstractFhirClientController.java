package org.dhis2.fhir.adapter.fhir.client;

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
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.rest.RestResourceNotFoundException;
import org.dhis2.fhir.adapter.rest.RestUnauthorizedException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Abstract base class for controllers that receive FHIR resources or notifications
 * about FHIR resources from FHIR clients.
 *
 * @author volsch
 */
public abstract class AbstractFhirClientController
{
    private final FhirClientResourceRepository resourceRepository;

    private final FhirClientRestHookProcessor processor;

    protected AbstractFhirClientController( @Nonnull FhirClientResourceRepository resourceRepository, @Nonnull FhirClientRestHookProcessor processor )
    {
        this.resourceRepository = resourceRepository;
        this.processor = processor;
    }

    @Nonnull
    public FhirClientResourceRepository getResourceRepository()
    {
        return resourceRepository;
    }

    @Nonnull
    protected FhirClientRestHookProcessor getProcessor()
    {
        return processor;
    }

    @Nonnull
    protected ResponseEntity<byte[]> processPayload( @Nonnull FhirClientResource fhirClientResource,
        @Nonnull String resourceType, @Nonnull String resourceId, HttpEntity<byte[]> requestEntity )
    {
        if ( (requestEntity.getBody() == null) || (requestEntity.getBody().length == 0) )
        {
            return createBadRequestResponse( "Payload expected." );
        }

        final MediaType mediaType = requestEntity.getHeaders().getContentType();
        final String fhirResource = new String( requestEntity.getBody(), getCharset( mediaType ) );
        processPayload( fhirClientResource, (mediaType == null) ? null : mediaType.toString(), resourceType, resourceId, fhirResource );
        return new ResponseEntity<>( HttpStatus.OK );
    }

    protected void processPayload( @Nonnull FhirClientResource fhirClientResource, @Nullable String contentType,
        @Nonnull String resourceType, @Nonnull String resourceId, @Nonnull String fhirResource )
    {
        processor.process( fhirClientResource, contentType, resourceType, resourceId, fhirResource );
    }

    @Nonnull
    protected Charset getCharset( @Nullable MediaType contentType )
    {
        Charset charset;
        if ( contentType == null )
        {
            charset = StandardCharsets.UTF_8;
        }
        else
        {
            charset = contentType.getCharset();
            if ( charset == null )
            {
                charset = StandardCharsets.UTF_8;
            }
        }
        return charset;
    }

    @Nonnull
    protected ResponseEntity<byte[]> createBadRequestResponse( @Nonnull String message )
    {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.TEXT_PLAIN );
        return new ResponseEntity<>( message.getBytes( StandardCharsets.UTF_8 ), headers, HttpStatus.BAD_REQUEST );
    }

    protected void validateRequest( @Nonnull UUID fhirClientId, FhirClientResource fhirClientResource, String authorization )
    {
        if ( !fhirClientResource.getFhirClient().getId().equals( fhirClientId ) )
        {
            // do not give detail if the resource or the subscription cannot be found
            throw new RestResourceNotFoundException( "FHIR client data for resource cannot be found: " + fhirClientResource );
        }
        if ( fhirClientResource.isExpOnly() )
        {
            throw new RestResourceNotFoundException( "FHIR client resource is intended for export only: " + fhirClientResource );
        }

        if ( StringUtils.isNotBlank( fhirClientResource.getFhirClient().getAdapterEndpoint().getAuthorizationHeader() ) &&
            !fhirClientResource.getFhirClient().getAdapterEndpoint().getAuthorizationHeader().equals( authorization ) )
        {
            throw new RestUnauthorizedException( "Authentication has failed." );
        }
    }
}
