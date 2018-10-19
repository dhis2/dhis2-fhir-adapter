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
import org.dhis2.fhir.adapter.dhis.DhisConflictException;
import org.dhis2.fhir.adapter.dhis.DhisImportUnsuccessfulException;
import org.dhis2.fhir.adapter.dhis.model.DataValue;
import org.dhis2.fhir.adapter.dhis.model.ImportStatus;
import org.dhis2.fhir.adapter.dhis.model.ImportSummaryWebMessage;
import org.dhis2.fhir.adapter.dhis.model.Status;
import org.dhis2.fhir.adapter.dhis.tracker.program.Event;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of {@link EventService}.
 *
 * @author volsch
 */
@Service
public class EventServiceImpl implements EventService
{
    protected static final String CREATE_URI = "/events.json?strategy=CREATE";

    protected static final String ID_URI = "/events/{id}.json";

    protected static final String UPDATE_URI = ID_URI + "?mergeMode=MERGE";

    protected static final String UPDATE_DATA_VALUE_URI = "/events/{id}/{dataElementId}.json?mergeMode=MERGE";

    protected static final String FIND_URI = "/events.json?" +
        "program={programId}&trackedEntityInstance={trackedEntityInstanceId}&ouMode=ACCESSIBLE&" +
        "fields=event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated," +
        "dataValues[dataElement,value,providedElsewhere,lastUpdated,storedBy]&skipPaging=true";

    private final RestTemplate restTemplate;

    @Autowired
    public EventServiceImpl( @Nonnull @Qualifier( "userDhis2RestTemplate" ) RestTemplate restTemplate )
    {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand( ignoreExceptions = { DhisConflictException.class } )
    @Nonnull
    @Override
    public Event createOrMinimalUpdate( @Nonnull Event event )
    {
        return event.isNewResource() ? create( event ) : minimalUpdate( event );
    }

    @HystrixCommand
    @Nonnull
    @Override
    public List<Event> find( @Nonnull String programId, @Nonnull String programStageId,
        @Nonnull String enrollmentId, @Nonnull String trackedEntityInstanceId )
    {
        final ResponseEntity<DhisEvents> result = restTemplate.getForEntity( FIND_URI, DhisEvents.class, programId, trackedEntityInstanceId );
        return Objects.requireNonNull( result.getBody() ).getEvents().stream().filter( e -> enrollmentId.equals( e.getEnrollmentId() ) &&
            programStageId.equals( e.getProgramStageId() ) ).collect( Collectors.toList() );
    }

    @Nonnull
    protected Event create( @Nonnull Event event )
    {
        final ResponseEntity<ImportSummaryWebMessage> response;
        try
        {
            response = restTemplate.postForEntity( CREATE_URI, event, ImportSummaryWebMessage.class );
        }
        catch ( HttpClientErrorException e )
        {
            if ( HttpStatus.CONFLICT.equals( e.getStatusCode() ) )
            {
                throw new DhisConflictException( "Event could not be created: " + e.getResponseBodyAsString(), e );
            }
            throw e;
        }
        final ImportSummaryWebMessage result = Objects.requireNonNull( response.getBody() );
        if ( (result.getStatus() != Status.OK) ||
            (result.getResponse().getImportSummaries().size() != 1) ||
            (result.getResponse().getImportSummaries().get( 0 ).getStatus() != ImportStatus.SUCCESS) ||
            (result.getResponse().getImportSummaries().get( 0 ).getReference() == null) )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful import." );
        }
        event.setId( result.getResponse().getImportSummaries().get( 0 ).getReference() );
        return event;
    }

    @Nonnull
    protected Event update( @Nonnull Event event )
    {
        final ResponseEntity<ImportSummaryWebMessage> response;
        try
        {
            response = restTemplate.exchange( UPDATE_URI, HttpMethod.PUT, new HttpEntity<>( event ),
                ImportSummaryWebMessage.class, event.getId() );
        }
        catch ( HttpClientErrorException e )
        {
            if ( HttpStatus.CONFLICT.equals( e.getStatusCode() ) )
            {
                throw new DhisConflictException( "Event could not be updated: " + e.getResponseBodyAsString(), e );
            }
            throw e;
        }
        final ImportSummaryWebMessage result = Objects.requireNonNull( response.getBody() );
        if ( result.getStatus() != Status.OK )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful import: " +
                result.getStatus() );
        }
        return event;
    }

    protected void update( @Nonnull MinimalEvent event )
    {
        final ResponseEntity<ImportSummaryWebMessage> response;
        try
        {
            response = restTemplate.exchange( UPDATE_DATA_VALUE_URI, HttpMethod.PUT, new HttpEntity<>( event ),
                ImportSummaryWebMessage.class, event.getId(), event.getDataElementId() );
        }
        catch ( HttpClientErrorException e )
        {
            if ( HttpStatus.CONFLICT.equals( e.getStatusCode() ) )
            {
                throw new DhisConflictException( "Event could not be updated: " + e.getResponseBodyAsString(), e );
            }
            throw e;
        }
        final ImportSummaryWebMessage result = Objects.requireNonNull( response.getBody() );
        if ( result.getStatus() != Status.OK )
        {
            throw new DhisImportUnsuccessfulException( "Response indicates an unsuccessful event import: " +
                result.getStatus() );
        }
    }

    @Nonnull
    protected Event minimalUpdate( @Nonnull Event event )
    {
        if ( event.isModified() || event.getDataValues().stream().anyMatch( DataValue::isNewResource ) )
        {
            return update( event );
        }
        event.getDataValues().stream().filter( DataValue::isModified ).forEach( dv ->
            update( new MinimalEvent( event.getId(), dv ) ) );
        return event;
    }
}
