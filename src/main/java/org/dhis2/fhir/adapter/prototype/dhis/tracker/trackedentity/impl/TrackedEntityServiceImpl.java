package org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity.impl;

/*
 * Copyright (c) 2004-2018, University of Oslo
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

import org.dhis2.fhir.adapter.prototype.dhis.model.ImportStatus;
import org.dhis2.fhir.adapter.prototype.dhis.model.ImportSummaryWebMessage;
import org.dhis2.fhir.adapter.prototype.dhis.model.Status;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity.TrackedEntityInstance;
import org.dhis2.fhir.adapter.prototype.dhis.tracker.trackedentity.TrackedEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;

@Service
public class TrackedEntityServiceImpl implements TrackedEntityService
{
    protected static final String TRACKED_ENTITY_INSTANCES_URI = "/trackedEntityInstances.json";

    private final RestTemplate restTemplate;

    @Autowired public TrackedEntityServiceImpl( @Nonnull @Qualifier( "userDhis2RestTemplate" ) RestTemplate restTemplate )
    {
        this.restTemplate = restTemplate;
    }

    @Override public TrackedEntityInstance create( TrackedEntityInstance trackedEntityInstance )
    {
        final ResponseEntity<ImportSummaryWebMessage> response =
            restTemplate.postForEntity( TRACKED_ENTITY_INSTANCES_URI, trackedEntityInstance, ImportSummaryWebMessage.class );
        final ImportSummaryWebMessage result = response.getBody();
        if ( (result.getStatus() != Status.OK) ||
            (result.getResponse().getImportSummaries().size() != 1) ||
            (result.getResponse().getImportSummaries().get( 0 ).getStatus() != ImportStatus.SUCCESS) ||
            (result.getResponse().getImportSummaries().get( 0 ).getReference() == null) )
        {
            throw new RuntimeException( "Tracked trackedentity instance could not be created" );
        }
        trackedEntityInstance.setId( result.getResponse().getImportSummaries().get( 0 ).getReference() );
        return trackedEntityInstance;
    }
}
