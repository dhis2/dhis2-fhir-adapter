package org.dhis2.fhir.adapter.fhir.server;

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
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirServerResource;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirServerResourceRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.rest.RestResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Accepts resources that are initiated by a remote FHIR server
 * synchronization (e.g. OpenMRS).
 *
 * @author volsch
 */
@RestController
@RequestMapping( "/remote-fhir-sync" )
public class FhirServerSyncController extends AbstractFhirServerController
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    public static final MediaType FHIR_JSON_MEDIA_TYPE = MediaType.parseMediaType( "application/fhir+json;charset=UTF-8" );

    private final FhirResourceRepository fhirResourceRepository;

    public FhirServerSyncController( @Nonnull FhirServerResourceRepository resourceRepository, @Nonnull FhirServerRestHookProcessor processor,
        @Nonnull FhirResourceRepository fhirResourceRepository )
    {
        super( resourceRepository, processor );
        this.fhirResourceRepository = fhirResourceRepository;
    }

    @RequestMapping( path = "/{fhirServerId}/{resourceType}/{resourceId}", method = RequestMethod.DELETE )
    public ResponseEntity<byte[]> delete(
        @PathVariable( "fhirServerId" ) UUID fhirServerId, @PathVariable( "resourceType" ) String resourceType, @PathVariable( "resourceId" ) String resourceId,
        @RequestHeader( value = "Authorization", required = false ) String authorization )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResourceTypeName( resourceType );
        if ( fhirResourceType == null )
        {
            return createBadRequestResponse( "Unknown resource type: " + resourceType );
        }

        lookupFhirServerResource( fhirServerId, fhirResourceType, authorization );
        // not yet supported
        return new ResponseEntity<>( HttpStatus.NO_CONTENT );
    }

    @RequestMapping( path = "/{fhirServerId}/{resourceType}/{resourceId}", method = RequestMethod.GET )
    public ResponseEntity<byte[]> getRemoteResource( @PathVariable( "fhirServerId" ) UUID fhirServerId,
        @PathVariable( "resourceType" ) String resourceType, @PathVariable( "resourceId" ) String resourceId,
        @RequestHeader( value = "Authorization", required = false ) String authorization )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResourceTypeName( resourceType );
        if ( fhirResourceType == null )
        {
            return createBadRequestResponse( "Unknown resource type: " + resourceType );
        }

        final FhirServerResource fhirServerResource = lookupFhirServerResource( fhirServerId, fhirResourceType, authorization );
        if ( !fhirServerResource.getFhirServer().isRemoteSyncEnabled() )
        {
            return new ResponseEntity<>( HttpStatus.FORBIDDEN );
        }

        // read resource from remote server, process payload as notification, return resource again
        final FhirVersion fhirVersion = fhirServerResource.getFhirServer().getFhirVersion();
        final Optional<IBaseResource> resource = fhirResourceRepository.findRefreshed( fhirServerResource.getFhirServer().getId(),
            fhirVersion, fhirServerResource.getFhirServer().getFhirEndpoint(), resourceType, resourceId );
        if ( !resource.isPresent() )
        {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }

        final FhirContext fhirContext = fhirResourceRepository.findFhirContext( fhirVersion )
            .orElseThrow( () -> new IllegalStateException( "FHIR context for FHIR version " + fhirVersion + " has not been defined." ) );
        final String fhirResource = fhirContext.newJsonParser().encodeResourceToString( resource.get() );
        processPayload( fhirServerResource, FHIR_JSON_MEDIA_TYPE.toString(), resourceType, resourceId, fhirResource );

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType( FHIR_JSON_MEDIA_TYPE );
        headers.setCacheControl( CacheControl.noCache() );
        return new ResponseEntity<>( fhirResource.getBytes( Objects.requireNonNull( FHIR_JSON_MEDIA_TYPE.getCharset() ) ), headers, HttpStatus.OK );
    }

    @RequestMapping( path = "/{fhirServerId}/{resourceType}/{resourceId}", method = { RequestMethod.POST, RequestMethod.PUT } )
    public ResponseEntity<byte[]> receiveWithPayload(
        @PathVariable( "fhirServerId" ) UUID fhirServerId, @PathVariable( "resourceType" ) String resourceType, @PathVariable( "resourceId" ) String resourceId,
        @RequestHeader( value = "Authorization", required = false ) String authorization, @Nonnull HttpEntity<byte[]> requestEntity )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResourceTypeName( resourceType );
        if ( fhirResourceType == null )
        {
            return createBadRequestResponse( "Unknown resource type: " + resourceType );
        }

        final FhirServerResource fhirServerResource = lookupFhirServerResource( fhirServerId, fhirResourceType, authorization );
        return processPayload( fhirServerResource, resourceType, resourceId, requestEntity );
    }

    @Nonnull
    protected FhirServerResource lookupFhirServerResource( @Nonnull UUID fhirServerId, @Nonnull FhirResourceType fhirResourceType, @Nullable String authorization )
    {
        final FhirServerResource fhirServerResource = getResourceRepository().findFirstCached( fhirServerId, fhirResourceType )
            .orElseThrow( () -> new RestResourceNotFoundException( "FHIR server data for resource " + fhirResourceType + " of FHIR server " + fhirServerId + " cannot be found." ) );
        validateRequest( fhirServerId, fhirServerResource, authorization );
        return fhirServerResource;
    }
}
