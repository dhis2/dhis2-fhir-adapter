package org.dhis2.fhir.adapter.dhis.tracker.trackedentity.impl;

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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.ImmutableTrackedEntityType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityAttributes;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.TrackedEntityType;
import org.dhis2.fhir.adapter.dhis.tracker.trackedentity.WritableTrackedEntityType;
import org.dhis2.fhir.adapter.rest.RestTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

@Service
public class TrackedEntityMetadataServiceImpl implements TrackedEntityMetadataService
{
    protected static final String TRACKED_ENTITY_ATTRIBUTE_FIELDS = "id,name,code,valueType,generated,optionSetValue,optionSet[id,name,options[code,name]]";

    protected static final String TRACKED_ENTITY_TYPE_FIELDS = "id,name,trackedEntityTypeAttributes[id,name,valueType,mandatory,trackedEntityAttribute[" +
        TRACKED_ENTITY_ATTRIBUTE_FIELDS + "]]";

    protected static final String TRACKED_ENTITY_TYPE_BY_ID_URI = "/trackedEntityTypes/{id}.json?fields=" + TRACKED_ENTITY_TYPE_FIELDS;

    protected static final String TRACKED_ENTITY_TYPE_BY_NAME_URI = "/trackedEntityTypes.json?" +
        "paging=false&filter=name:eq:{name}&fields=" + TRACKED_ENTITY_TYPE_FIELDS;

    protected static final String TRACKED_ENTITY_ATTRIBUTES_URI = "/trackedEntityAttributes.json?paging=false&fields=" + TRACKED_ENTITY_ATTRIBUTE_FIELDS;

    protected static final String REQUIRED_VALUE_URI = "/trackedEntityAttributes/{attributeId}/requiredValues.json";

    private final RestTemplate restTemplate;

    @Autowired
    public TrackedEntityMetadataServiceImpl( @Nonnull @Qualifier( "systemDhis2RestTemplate" ) RestTemplate restTemplate )
    {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand
    @Nonnull
    @Override
    public Optional<? extends TrackedEntityType> findTypeByReferenceRefreshed( @Nonnull Reference reference )
    {
        switch ( reference.getType() )
        {
            case NAME:
                return Objects.requireNonNull( restTemplate.getForEntity( TRACKED_ENTITY_TYPE_BY_NAME_URI, TrackedEntityTypes.class, reference.getValue() ).getBody() )
                    .getTrackedEntityTypes().stream().map( ImmutableTrackedEntityType::new ).findFirst();
            case ID:
                try
                {
                    return Optional.of( Objects.requireNonNull( restTemplate.getForEntity(
                        TRACKED_ENTITY_TYPE_BY_ID_URI, WritableTrackedEntityType.class, reference.getValue() ).getBody() ) )
                        .map( ImmutableTrackedEntityType::new );
                }
                catch ( HttpClientErrorException e )
                {
                    if ( RestTemplateUtils.isNotFound( e ) )
                    {
                        return Optional.empty();
                    }
                    throw e;
                }
            case CODE:
                // tracked entity type does not have a code
                return Optional.empty();
            default:
                throw new AssertionError( "Unhandled reference type: " + reference.getType() );
        }
    }

    @HystrixCommand
    @Cacheable( value = "trackedEntityTypes", cacheManager = "dhisCacheManager" )
    @Nonnull
    @Override
    public Optional<? extends TrackedEntityType> findTypeByReference( @Nonnull Reference reference )
    {
        return findTypeByReferenceRefreshed( reference );
    }

    @HystrixCommand
    @Cacheable( value = "trackedEntityAttributes", cacheManager = "dhisCacheManager" )
    @Nonnull
    @Override
    public TrackedEntityAttributes getAttributes()
    {
        final ResponseEntity<TrackedEntityAttributes> result =
            restTemplate.getForEntity( TRACKED_ENTITY_ATTRIBUTES_URI, TrackedEntityAttributes.class );
        return Objects.requireNonNull( result.getBody() );
    }

    @HystrixCommand
    @Cacheable( value = "requiredValues", cacheManager = "dhisCacheManager" )
    @Override
    @Nonnull
    public RequiredValues getRequiredValues( @Nonnull String attributeId )
    {
        return Objects.requireNonNull( restTemplate.getForObject( REQUIRED_VALUE_URI, RequiredValues.class, attributeId ) );
    }
}
