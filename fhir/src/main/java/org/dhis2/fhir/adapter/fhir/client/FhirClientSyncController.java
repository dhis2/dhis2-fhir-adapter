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

import ca.uhn.fhir.context.FhirContext;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirClientResource;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.dhis2.fhir.adapter.fhir.metadata.repository.FhirClientResourceRepository;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.repository.FhirResourceRepository;
import org.dhis2.fhir.adapter.rest.RestResourceNotFoundException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
 * Accepts resources that are initiated by a remote FHIR client
 * synchronization (e.g. OpenMRS).
 *
 * @author volsch
 */
@RestController
@RequestMapping( "/remote-fhir-sync" )
@ConditionalOnProperty( name = "dhis2.fhir-adapter.import-enabled" )
public class FhirClientSyncController extends AbstractFhirClientController
{
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    public static final MediaType FHIR_JSON_MEDIA_TYPE = MediaType.parseMediaType( "application/fhir+json;charset=UTF-8" );

    private final FhirResourceRepository fhirResourceRepository;

    public FhirClientSyncController( @Nonnull FhirClientResourceRepository resourceRepository, @Nonnull FhirClientRestHookProcessor processor,
        @Nonnull FhirResourceRepository fhirResourceRepository )
    {
        super( resourceRepository, processor );
        this.fhirResourceRepository = fhirResourceRepository;
    }

    @RequestMapping( path = "/{fhirClientId}/**/{resourceType}/{resourceId}", method = RequestMethod.DELETE )
    public ResponseEntity<byte[]> delete(
        @PathVariable( "fhirClientId" ) UUID fhirClientId, @PathVariable( "resourceType" ) String resourceType, @PathVariable( "resourceId" ) String resourceId,
        @RequestHeader( value = "Authorization", required = false ) String authorization )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResourceTypeName( resourceType );
        if ( fhirResourceType == null )
        {
            return createBadRequestResponse( "Unknown resource type: " + resourceType );
        }

        lookupFhirClientResource( fhirClientId, fhirResourceType, authorization );
        // not yet supported
        return new ResponseEntity<>( HttpStatus.NO_CONTENT );
    }

    @RequestMapping( path = "/{fhirClientId}/**/{resourceType}/{resourceId}", method = RequestMethod.GET )
    public ResponseEntity<byte[]> getRemoteResource( @PathVariable( "fhirClientId" ) UUID fhirClientId,
        @PathVariable( "resourceType" ) String resourceType, @PathVariable( "resourceId" ) String resourceId,
        @RequestHeader( value = "Authorization", required = false ) String authorization )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResourceTypeName( resourceType );
        if ( fhirResourceType == null )
        {
            return createBadRequestResponse( "Unknown resource type: " + resourceType );
        }

        final FhirClientResource fhirClientResource = lookupFhirClientResource( fhirClientId, fhirResourceType, authorization );
        if ( !fhirClientResource.getFhirClient().isRemoteSyncEnabled() )
        {
            return new ResponseEntity<>( HttpStatus.FORBIDDEN );
        }

        // read resource from remote client, process payload as notification, return resource again
        final FhirVersion fhirVersion = fhirClientResource.getFhirClient().getFhirVersion();
        final Optional<IBaseResource> resource = fhirResourceRepository.findRefreshed( fhirClientResource.getFhirClient().getId(),
            fhirVersion, fhirClientResource.getFhirClient().getFhirEndpoint(), resourceType, resourceId, false );
        if ( !resource.isPresent() )
        {
            return new ResponseEntity<>( HttpStatus.NOT_FOUND );
        }

        final FhirContext fhirContext = fhirResourceRepository.findFhirContext( fhirVersion )
            .orElseThrow( () -> new IllegalStateException( "FHIR context for FHIR version " + fhirVersion + " has not been defined." ) );
        final String fhirResource = fhirContext.newJsonParser().encodeResourceToString( resource.get() );
        processPayload( fhirClientResource, FHIR_JSON_MEDIA_TYPE.toString(), resourceType, resourceId, fhirResource );

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType( FHIR_JSON_MEDIA_TYPE );
        headers.setCacheControl( CacheControl.noCache() );
        return new ResponseEntity<>( fhirResource.getBytes( Objects.requireNonNull( FHIR_JSON_MEDIA_TYPE.getCharset() ) ), headers, HttpStatus.OK );
    }

    @RequestMapping( path = "/{fhirClientId}/**/{resourceType}/{resourceId}", method = { RequestMethod.POST, RequestMethod.PUT } )
    public ResponseEntity<byte[]> receiveWithPayload(
        @PathVariable( "fhirClientId" ) UUID fhirClientId, @PathVariable( "resourceType" ) String resourceType, @PathVariable( "resourceId" ) String resourceId,
        @RequestHeader( value = "Authorization", required = false ) String authorization, @Nonnull HttpEntity<byte[]> requestEntity )
    {
        final FhirResourceType fhirResourceType = FhirResourceType.getByResourceTypeName( resourceType );
        if ( fhirResourceType == null )
        {
            return createBadRequestResponse( "Unknown resource type: " + resourceType );
        }

        final FhirClientResource fhirClientResource = lookupFhirClientResource( fhirClientId, fhirResourceType, authorization );
        return processPayload( fhirClientResource, resourceType, resourceId, requestEntity );
    }

    @Nonnull
    protected FhirClientResource lookupFhirClientResource( @Nonnull UUID fhirClientId, @Nonnull FhirResourceType fhirResourceType, @Nullable String authorization )
    {
        final FhirClientResource fhirClientResource = getResourceRepository().findFirstCached( fhirClientId, fhirResourceType )
            .orElseThrow( () -> new RestResourceNotFoundException( "FHIR client data for resource " + fhirResourceType + " of FHIR client " + fhirClientId + " cannot be found." ) );
        validateRequest( fhirClientId, fhirClientResource, authorization );
        return fhirClientResource;
    }
}
