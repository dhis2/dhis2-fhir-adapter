package org.dhis2.fhir.adapter.dhis.tracker.program.impl;

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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.tracker.program.ImmutableProgram;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.dhis.tracker.program.WritableProgram;
import org.dhis2.fhir.adapter.rest.RestTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of a {@link ProgramMetadataService}.
 *
 * @author volsch
 */
@Service
@CacheConfig( cacheNames = "programMetadata" )
public class ProgramMetadataServiceImpl implements ProgramMetadataService
{
    protected static final String FIELDS =
        "id,name,code,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate," +
            "registration,withoutRegistration,captureCoordinates,trackedEntityType[id]," +
            "programTrackedEntityAttributes[id,name,valueType,mandatory,allowFutureDate," +
            "trackedEntityAttribute[id,name,code,valueType,generated]]," +
            "programStages[id,name,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart," +
            "programStageDataElements[id,compulsory,allowProvidedElsewhere," +
            "dataElement[id,name,code,valueType,optionSetValue,optionSet[id,name,options[code,name]]]]]";

    protected static final String PROGRAM_BY_CODE_URI = "/programs.json?paging=false&fields=" + FIELDS + "&filter=code:eq:{code}";

    protected static final String PROGRAM_BY_NAME_URI = "/programs.json?paging=false&fields=" + FIELDS + "&filter=name:eq:{name}";

    protected static final String PROGRAM_BY_ID_URI = "/programs/{id}.json&fields=" + FIELDS;

    private final RestTemplate restTemplate;

    @Autowired
    public ProgramMetadataServiceImpl( @Nonnull @Qualifier( "systemDhis2RestTemplate" ) RestTemplate restTemplate )
    {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand
    @Cacheable
    @Nonnull
    @Override
    public Optional<? extends Program> findOneByReference( @Nonnull Reference reference )
    {
        final ResponseEntity<DhisPrograms> result;
        switch ( reference.getType() )
        {
            case CODE:
                result = restTemplate.getForEntity( PROGRAM_BY_CODE_URI, DhisPrograms.class, reference.getValue() );
                break;
            case NAME:
                result = restTemplate.getForEntity( PROGRAM_BY_NAME_URI, DhisPrograms.class, reference.getValue() );
                break;
            case ID:
                try
                {
                    return Optional.of( Objects.requireNonNull( restTemplate.getForEntity(
                        PROGRAM_BY_ID_URI, WritableProgram.class, reference.getValue() ).getBody() ) )
                        .map( ImmutableProgram::new );
                }
                catch ( HttpClientErrorException e )
                {
                    if ( RestTemplateUtils.isNotFound( e ) )
                    {
                        return Optional.empty();
                    }
                    throw e;
                }
            default:
                throw new AssertionError( "Unhandled reference type: " + reference.getType() );
        }
        return Objects.requireNonNull( result.getBody() ).getPrograms().stream().map( ImmutableProgram::new ).findFirst();
    }
}
