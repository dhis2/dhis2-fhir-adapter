package org.dhis2.fhir.adapter.dhis.tracker.program.impl;

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

import org.apache.commons.io.IOUtils;
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.tracker.program.Program;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramMetadataService;
import org.dhis2.fhir.adapter.model.ValueType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Unit tests for {@link ProgramMetadataServiceImpl}.
 *
 * @author volsch
 */
public class ProgramMetadataServiceImplTest
{
    private RestTemplate systemRestTemplate;

    private RestTemplate userRestTemplate;

    private MockRestServiceServer mockServer;

    private ProgramMetadataService service;

    @Before
    public void setUp()
    {
        systemRestTemplate = new RestTemplateBuilder().rootUri( "http://localhost:8080/api" ).build();
        userRestTemplate = new RestTemplateBuilder().rootUri( "http://localhost:8080/api" ).build();
        mockServer = MockRestServiceServer.createServer( systemRestTemplate );
        service = new ProgramMetadataServiceImpl( systemRestTemplate, userRestTemplate );
    }

    @Test
    public void findMetadataByReferenceId() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/programs/93783.json?fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration,withoutRegistration," +
            "captureCoordinates,trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid,program%5Bid%5D,lastUpdated,name," +
            "description," +
            "repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name," +
            "options%5Bcode,name%5D%5D%5D%5D%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/program.json" ), MediaType.APPLICATION_JSON ) );

        Optional<? extends Program> ou = service.findMetadataByReference( new Reference( "93783", ReferenceType.ID ) );
        Assert.assertTrue( ou.isPresent() );
        assertProgram( ou.get() );
    }

    @Test
    public void findMetadataByReferenceIdNotFound()
    {
        mockServer.expect( requestTo(
            "http://localhost:8080/api/programs/93783.json?fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration,withoutRegistration,captureCoordinates," +
                "trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid,program%5Bid%5D,lastUpdated,name,description," +
                "repeatable," +
                "captureCoordinates," +
            "generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D%5D" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( MockRestResponseCreators.withStatus( HttpStatus.NOT_FOUND ).body( "{}" ).contentType( MediaType.APPLICATION_JSON ) );

        Optional<? extends Program> ou = service.findMetadataByReference( new Reference( "93783", ReferenceType.ID ) );
        Assert.assertFalse( ou.isPresent() );
    }

    @Test( expected = HttpServerErrorException.class )
    public void findMetadataByReferenceIdServerError()
    {
        mockServer.expect( requestTo(
            "http://localhost:8080/api/programs/93783.json?fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration,withoutRegistration,captureCoordinates," +
                "trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid,program%5Bid%5D,lastUpdated,name,description," +
                "repeatable,captureCoordinates," +
            "generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D%5D" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( MockRestResponseCreators.withServerError() );
        service.findMetadataByReference( new Reference( "93783", ReferenceType.ID ) );
    }

    @Test
    public void findMetadataByReferenceCode() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/programs.json?paging=false&fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration,withoutRegistration," +
            "captureCoordinates," +
            "trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates," +
            "generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode," +
            "name%5D%5D%5D%5D%5D&filter=code:eq:OU_3783" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/programs.json" ), MediaType.APPLICATION_JSON ) );

        Optional<? extends Program> ou = service.findMetadataByReference( new Reference( "OU_3783", ReferenceType.CODE ) );
        Assert.assertTrue( ou.isPresent() );
        assertProgram( ou.get() );
    }

    @Test
    public void findMetadataByReferenceCodeNotFound() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/programs.json?paging=false&fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration,withoutRegistration," +
            "captureCoordinates," +
            "trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates," +
            "generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode," +
            "name%5D%5D%5D%5D%5D&filter=code:eq:OU_3783" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/emptyPrograms.json" ), MediaType.APPLICATION_JSON ) );

        Optional<? extends Program> ou = service.findMetadataByReference( new Reference( "OU_3783", ReferenceType.CODE ) );
        Assert.assertFalse( ou.isPresent() );
    }

    @Test
    public void findMetadataByReferenceName() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/programs.json?paging=false&fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration,withoutRegistration," +
            "captureCoordinates," +
            "trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates," +
            "generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode," +
            "name%5D%5D%5D%5D%5D&filter=name:eq:Freetown" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/programs.json" ), MediaType.APPLICATION_JSON ) );

        Optional<? extends Program> ou = service.findMetadataByReference( new Reference( "Freetown", ReferenceType.NAME ) );
        Assert.assertTrue( ou.isPresent() );
        assertProgram( ou.get() );
    }

    @Test
    public void findMetadataByReferenceNameNotFound() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/programs.json?paging=false&fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration,withoutRegistration," +
            "captureCoordinates," +
            "trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates," +
            "generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode," +
            "name%5D%5D%5D%5D%5D&filter=name:eq:Freetown" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/emptyPrograms.json" ), MediaType.APPLICATION_JSON ) );

        Optional<? extends Program> ou = service.findMetadataByReference( new Reference( "Freetown", ReferenceType.NAME ) );
        Assert.assertFalse( ou.isPresent() );
    }

    private void assertProgram( Program program )
    {
        assertNotNull( program );
        assertEquals( "Child Programme", program.getName() );
        assertEquals( "IpHINAT79UW", program.getId() );
        assertEquals( "nEenWmSyUEp", program.getTrackedEntityTypeId() );
        assertNotNull( program.getStages() );
        assertEquals( 2, program.getStages().size() );
        assertEquals( "A03MvHHogjR", program.getStages().get( 0 ).getId() );
        assertEquals( "Birth", program.getStages().get( 0 ).getName() );
        assertEquals( 2, program.getStages().get( 0 ).getMinDaysFromStart() );
        assertNotNull( program.getStages().get( 0 ).getDataElements() );
        assertEquals( "a3kGcGDCuk6", program.getStages().get( 0 ).getDataElements().get( 0 ).getElement().getId() );
        assertEquals( "MCH Apgar Score", program.getStages().get( 0 ).getDataElements().get( 0 ).getElement().getName() );
        assertEquals( "DE_2006098", program.getStages().get( 0 ).getDataElements().get( 0 ).getElement().getCode() );
        assertEquals( ValueType.NUMBER, program.getStages().get( 0 ).getDataElements().get( 0 ).getElement().getValueType() );
        assertTrue( program.getStages().get( 0 ).getDataElements().get( 0 ).isAllowProvidedElsewhere() );
        assertTrue( program.getStages().get( 0 ).getDataElements().get( 0 ).isCompulsory() );
        assertNull( program.getStages().get( 0 ).getDataElements().get( 0 ).getElement().getOptionSet() );
        assertNotNull( program.getStages().get( 0 ).getDataElements().get( 1 ).getElement().getOptionSet() );
        assertEquals( "kzgQRhOCadd", program.getStages().get( 0 ).getDataElements().get( 1 ).getElement().getOptionSet().getId() );
        assertEquals( "MNCH Polio doses (0-3)", program.getStages().get( 0 ).getDataElements().get( 1 ).getElement().getOptionSet().getName() );
        assertNotNull( program.getStages().get( 0 ).getDataElements().get( 1 ).getElement().getOptionSet().getOptions() );
        assertEquals( 2, program.getStages().get( 0 ).getDataElements().get( 1 ).getElement().getOptionSet().getOptions().size() );
        assertEquals( "0", program.getStages().get( 0 ).getDataElements().get( 1 ).getElement().getOptionSet().getOptions().get( 0 ).getCode() );
        assertEquals( "Dose 0", program.getStages().get( 0 ).getDataElements().get( 1 ).getElement().getOptionSet().getOptions().get( 0 ).getName() );
        assertTrue( program.getStages().get( 0 ).isGeneratedByEnrollmentDate() );
        assertTrue( program.getStages().get( 0 ).isCaptureCoordinates() );
        assertTrue( program.getStages().get( 0 ).isRepeatable() );
        assertNotNull( program.getTrackedEntityAttributes() );
        assertEquals( 2, program.getTrackedEntityAttributes().size() );
        assertTrue( program.isSelectEnrollmentDatesInFuture() );
        assertTrue( program.isSelectIncidentDatesInFuture() );
        assertTrue( program.isCaptureCoordinates() );
        assertTrue( program.isDisplayIncidentDate() );
        assertTrue( program.isRegistration() );
        assertTrue( program.isWithoutRegistration() );
        assertEquals( 2, program.getTrackedEntityAttributes().size() );
        assertEquals( "l2T72XzBCLd", program.getTrackedEntityAttributes().get( 0 ).getId() );
        assertEquals( "Child Programme First name", program.getTrackedEntityAttributes().get( 0 ).getName() );
        assertEquals( ValueType.TEXT, program.getTrackedEntityAttributes().get( 0 ).getValueType() );
        assertNotNull( program.getTrackedEntityAttributes().get( 0 ).getAttribute() );
        assertEquals( "w75KJ2mc4zz", program.getTrackedEntityAttributes().get( 0 ).getAttribute().getId() );
        assertEquals( "MMD_PER_NAM", program.getTrackedEntityAttributes().get( 0 ).getAttribute().getCode() );
        assertEquals( "First name", program.getTrackedEntityAttributes().get( 0 ).getAttribute().getName() );
        assertEquals( ValueType.TEXT, program.getTrackedEntityAttributes().get( 0 ).getAttribute().getValueType() );
        assertTrue( program.getTrackedEntityAttributes().get( 0 ).getAttribute().isGenerated() );
        assertTrue( program.getTrackedEntityAttributes().get( 0 ).isAllowFutureDate() );
        assertTrue( program.getTrackedEntityAttributes().get( 0 ).isMandatory() );
    }
}