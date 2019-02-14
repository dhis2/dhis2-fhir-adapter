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

import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.rest.RestResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Accepts the web hook request from the client FHIR service and queues the request
 * into a queue. Before it checks if there is no such request included in the queue.
 * Not every web hook notification should result in a poll request if there is
 * already a queued poll request for the client FHIR service that handles all
 * relevant resources.
 *
 * @author volsch
 */
@RestController
@RequestMapping( "/remote-fhir-rest-hook" )
@ConditionalOnProperty( name = "dhis2.fhir-adapter.import-enabled" )
public class FhirClientRestHookController extends AbstractFhirClientController
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final AtomicLong receivedWithPayloadCount = new AtomicLong();

    private final AtomicLong receivedCount = new AtomicLong();

    public FhirClientRestHookController( @Nonnull FhirClientResourceRepository resourceRepository, @Nonnull FhirClientRestHookProcessor processor )
    {
        super( resourceRepository, processor );
    }

    @RequestMapping( path = "/{fhirClientId}/{fhirClientResourceId}/{resourceType}/{resourceId}/_history/{version}", method = { RequestMethod.POST, RequestMethod.PUT } )
    public ResponseEntity<byte[]> receiveWithPayloadAndHistory(
        @PathVariable( "fhirClientId" ) UUID fhirClientId, @PathVariable( "fhirClientResourceId" ) UUID fhirClientResourceId,
        @PathVariable( "resourceType" ) String resourceType, @PathVariable( "resourceId" ) String resourceId,
        @PathVariable( "version" ) String version, @RequestHeader( value = "Authorization", required = false ) String authorization,
        @Nonnull HttpEntity<byte[]> requestEntity )
    {
        final long count = receivedWithPayloadCount.incrementAndGet();
        logger.info( "Received rest hook {}/{} (version={}) for FHIR client resource ID {} (received with payload={}).",
            resourceType, resourceId, version, fhirClientResourceId, count );
        return processPayload( lookupFhirClientResource( fhirClientId, fhirClientResourceId, authorization ),
            resourceType, resourceId, requestEntity );
    }

    @RequestMapping( path = "/{fhirClientId}/{fhirClientResourceId}/{resourceType}/{resourceId}", method = { RequestMethod.POST, RequestMethod.PUT } )
    public ResponseEntity<byte[]> receiveWithPayload(
        @PathVariable( "fhirClientId" ) UUID fhirClientId, @PathVariable( "fhirClientResourceId" ) UUID fhirClientResourceId,
        @PathVariable( "resourceType" ) String resourceType, @PathVariable( "resourceId" ) String resourceId,
        @RequestHeader( value = "Authorization", required = false ) String authorization,
        @Nonnull HttpEntity<byte[]> requestEntity )
    {
        return receiveWithPayloadAndHistory( fhirClientId, fhirClientResourceId, resourceType, resourceId, null, authorization, requestEntity );
    }

    @PostMapping( path = "/{fhirClientId}/{fhirClientResourceId}" )
    public void receive( @PathVariable UUID fhirClientId, @PathVariable UUID fhirClientResourceId,
        @RequestHeader( value = "Authorization", required = false ) String authorization )
    {
        final long count = receivedCount.incrementAndGet();
        logger.info( "Received rest hook for FHIR client resource ID {} (received without payload={}).",
            fhirClientResourceId, count );
        final FhirClientResource fhirClientResource = lookupFhirClientResource( fhirClientId, fhirClientResourceId, authorization );
        getProcessor().process( fhirClientResource );
    }

    @Nonnull
    protected FhirClientResource lookupFhirClientResource( @Nonnull UUID fhirClientId, @Nonnull UUID fhirClientResourceId, String authorization )
    {
        final FhirClientResource fhirClientResource = getResourceRepository().findOneByIdCached( fhirClientResourceId )
            .orElseThrow( () -> new RestResourceNotFoundException( "FHIR client data for resource cannot be found: " + fhirClientResourceId ) );
        validateRequest( fhirClientId, fhirClientResource, authorization );
        return fhirClientResource;
    }
}
