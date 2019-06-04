package org.dhis2.fhir.adapter.fhir.metadata.controller;

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

import com.fasterxml.jackson.databind.JsonNode;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataExportParams;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataExportService;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataImportParams;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataImportResult;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataImportService;
import org.dhis2.fhir.adapter.fhir.metadata.service.impl.MetadataImportException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

/**
 * Controller for metadata export and import.
 *
 * @author volsch
 */
@RestController
@RequestMapping( "/api/metadata" )
@PreAuthorize( "hasRole('CODE_MAPPING') and hasRole('DATA_MAPPING')" )
public class MetadataRestController
{
    private final MetadataExportService metadataExportService;

    private final MetadataImportService metadataImportService;

    public MetadataRestController( @Nonnull MetadataExportService metadataExportService, @Nonnull MetadataImportService metadataImportService )
    {
        this.metadataExportService = metadataExportService;
        this.metadataImportService = metadataImportService;
    }

    @Nonnull
    @RequestMapping( method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<JsonNode> exp( @RequestParam( name = "trackerProgramId", required = false ) List<UUID> trackerProgramIds,
        @RequestParam( name = "excludedSystemUri", required = false ) List<String> excludedSystemUris,
        @RequestParam( name = "includeTrackedEntities", required = false, defaultValue = "false" ) boolean includeTrackedEntities,
        @RequestParam( name = "includeResourceMappings", required = false, defaultValue = "false" ) boolean includeResourceMappings,
        @RequestParam( name = "download", required = false, defaultValue = "false" ) boolean download )
    {
        final MetadataExportParams params = new MetadataExportParams();
        params.setIncludeTrackedEntities( includeTrackedEntities );
        params.setIncludeResourceMappings( includeResourceMappings );

        if ( trackerProgramIds != null )
        {
            params.getTrackerProgramIds().addAll( trackerProgramIds );
        }

        if ( excludedSystemUris != null )
        {
            params.getExcludedSystemUris().addAll( excludedSystemUris );
        }

        final JsonNode node = metadataExportService.exp( params );
        final HttpHeaders headers = new HttpHeaders();

        if ( download )
        {
            headers.set( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"metadata.json\"" );
        }

        return new ResponseEntity<>( node, headers, HttpStatus.OK );
    }

    @Nonnull
    @RequestMapping( method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE )
    public ResponseEntity<MetadataImportResult> imp( @RequestBody @Nonnull JsonNode jsonNode,
        @RequestParam( name = "includeResourceMappings", required = false, defaultValue = "false" ) boolean includeResourceMappings )
    {
        final MetadataImportParams params = new MetadataImportParams();
        params.setIncludeResourceMappings( includeResourceMappings );

        MetadataImportResult result;
        try
        {
            result = metadataImportService.imp( jsonNode, params );
        }
        catch ( MetadataImportException e )
        {
            result = e.getResult();
        }

        return new ResponseEntity<>( result, result.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST );
    }
}
