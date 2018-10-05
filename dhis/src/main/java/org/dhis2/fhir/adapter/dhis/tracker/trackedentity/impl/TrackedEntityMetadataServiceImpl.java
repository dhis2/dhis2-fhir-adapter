package org.dhis2.fhir.adapter.dhis.tracker.trackedentity.impl;

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

import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.ImmutableTrackedEntityType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TrackedEntityMetadataServiceImpl implements TrackedEntityMetadataService
{
    protected static final String TRACKED_ENTITY_TYPE_URI = "/trackedEntityTypes.json?paging=false&" +
        "fields=id,name,trackedEntityTypeAttributes[id,name,valueType,mandatory,trackedEntityAttribute[id,name,code]]," +
        "attributeValues[lastUpdated,value,attribute[id,name,code]]";

    private final RestTemplate restTemplate;

    @Autowired
    public TrackedEntityMetadataServiceImpl( @Nonnull @Qualifier( "systemDhis2RestTemplate" ) RestTemplate restTemplate )
    {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<TrackedEntityType> getTypeById( @Nonnull String id )
    {
        return getTrackedEntityTypes().stream().filter( tet -> Objects.equals( tet.getId(), id ) )
            .map( tet -> (TrackedEntityType) new ImmutableTrackedEntityType( tet ) ).findFirst();
    }

    @Override
    public Optional<TrackedEntityType> getType( @Nonnull Reference reference )
    {
        // tracked entity type can only be retrieved by name
        if ( reference.getType() != ReferenceType.NAME )
        {
            return Optional.empty();
        }
        return getTrackedEntityTypes().stream().filter( tet -> Objects.equals( tet.getName(), reference.getValue() ) )
            .map( tet -> (TrackedEntityType) new ImmutableTrackedEntityType( tet ) ).findFirst();
    }

    public List<TrackedEntityType> getTrackedEntityTypes()
    {
        final ResponseEntity<DhisTrackedEntityTypes> result = restTemplate.getForEntity( TRACKED_ENTITY_TYPE_URI, DhisTrackedEntityTypes.class );
        return Optional.ofNullable( result.getBody() ).orElse( new DhisTrackedEntityTypes() ).toModel();
    }
}
