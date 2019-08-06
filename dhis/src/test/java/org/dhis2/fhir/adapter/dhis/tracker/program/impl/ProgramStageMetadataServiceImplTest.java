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
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStage;
import org.dhis2.fhir.adapter.dhis.tracker.program.ProgramStageMetadataService;
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
 * Unit tests for {@link ProgramStageMetadataServiceImpl}.
 *
 * @author volsch
 */
public class ProgramStageMetadataServiceImplTest
{
    private RestTemplate systemRestTemplate;

    private RestTemplate userRestTemplate;

    private MockRestServiceServer mockServer;

    private ProgramStageMetadataService service;

    @Before
    public void setUp()
    {
        systemRestTemplate = new RestTemplateBuilder().rootUri( "http://localhost:8080/api" ).build();
        userRestTemplate = new RestTemplateBuilder().rootUri( "http://localhost:8080/api" ).build();
        mockServer = MockRestServiceServer.createServer( systemRestTemplate );
        service = new ProgramStageMetadataServiceImpl( systemRestTemplate, userRestTemplate );
    }

    @Test
    public void findMetadataByReferenceId() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/programStages/93783.json?fields=id,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid," +
            "compulsory," +
            "allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/programStage.json" ), MediaType.APPLICATION_JSON ) );

        Optional<? extends ProgramStage> ou = service.findMetadataByReference( new Reference( "93783", ReferenceType.ID ) );
        Assert.assertTrue( ou.isPresent() );
        assertProgramStage( ou.get() );
    }

    @Test
    public void findMetadataByReferenceIdNotFound()
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/programStages/93783.json?fields=id,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid," +
            "compulsory," +
            "allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( MockRestResponseCreators.withStatus( HttpStatus.NOT_FOUND ).body( "{}" ).contentType( MediaType.APPLICATION_JSON ) );

        Optional<? extends ProgramStage> ou = service.findMetadataByReference( new Reference( "93783", ReferenceType.ID ) );
        Assert.assertFalse( ou.isPresent() );
    }

    @Test( expected = HttpServerErrorException.class )
    public void findMetadataByReferenceIdServerError()
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/programStages/93783.json?fields=id,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid," +
            "compulsory," +
            "allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( MockRestResponseCreators.withServerError() );
        service.findMetadataByReference( new Reference( "93783", ReferenceType.ID ) );
    }

    @Test
    public void findMetadataByReferenceName() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/programStages.json?paging=false&fields=id,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid," +
            "compulsory," +
            "allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D&filter=name:eq:Birth" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/programStages.json" ), MediaType.APPLICATION_JSON ) );

        Optional<? extends ProgramStage> ou = service.findMetadataByReference( new Reference( "Birth", ReferenceType.NAME ) );
        Assert.assertTrue( ou.isPresent() );
        assertProgramStage( ou.get() );
    }

    @Test
    public void findMetadataByReferenceNameNotFound() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/programStages.json?paging=false&fields=id,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid," +
            "compulsory," +
            "allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D&filter=name:eq:xBirth" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/tracker/program/impl/emptyPrograms.json" ), MediaType.APPLICATION_JSON ) );

        Optional<? extends ProgramStage> ou = service.findMetadataByReference( new Reference( "xBirth", ReferenceType.NAME ) );
        Assert.assertFalse( ou.isPresent() );
    }

    private void assertProgramStage( ProgramStage programStage )
    {
        assertNotNull( programStage );
        assertEquals( "A03MvHHogjR", programStage.getId() );
        assertEquals( "IpHINAT79UW", programStage.getProgramId() );
        assertEquals( "Birth", programStage.getName() );
        assertEquals( 2, programStage.getMinDaysFromStart() );
        assertNotNull( programStage.getDataElements() );
        assertEquals( "a3kGcGDCuk6", programStage.getDataElements().get( 0 ).getElement().getId() );
        assertEquals( "MCH Apgar Score", programStage.getDataElements().get( 0 ).getElement().getName() );
        assertEquals( "DE_2006098", programStage.getDataElements().get( 0 ).getElement().getCode() );
        assertEquals( ValueType.NUMBER, programStage.getDataElements().get( 0 ).getElement().getValueType() );
        assertTrue( programStage.getDataElements().get( 0 ).isAllowProvidedElsewhere() );
        assertTrue( programStage.getDataElements().get( 0 ).isCompulsory() );
        assertNull( programStage.getDataElements().get( 0 ).getElement().getOptionSet() );
        assertTrue( programStage.isGeneratedByEnrollmentDate() );
        assertTrue( programStage.isCaptureCoordinates() );
        assertTrue( programStage.isRepeatable() );
    }
}