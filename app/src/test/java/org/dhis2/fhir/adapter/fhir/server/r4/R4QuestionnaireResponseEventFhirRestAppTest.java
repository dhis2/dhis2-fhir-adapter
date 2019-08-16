package org.dhis2.fhir.adapter.fhir.server.r4;

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

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import org.apache.commons.io.IOUtils;
import org.dhis2.fhir.adapter.dhis.tracker.program.EventStatus;
import org.dhis2.fhir.adapter.fhir.extension.DueDateExtensionUtils;
import org.dhis2.fhir.adapter.fhir.extension.EventStatusExtensionUtils;
import org.dhis2.fhir.adapter.fhir.extension.LocationExtensionUtils;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.server.AbstractProgramStageFhirRestAppTest;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ResourceFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * R4 tests for rest interfaces that access DHIS2 events by using FHIR Questionnaire Responses.
 * <b>Some methods are executed twice in order to verify that no cached data is used without authentication.</b>
 *
 * @author volsch
 */
public class R4QuestionnaireResponseEventFhirRestAppTest extends AbstractProgramStageFhirRestAppTest
{
    @Nonnull
    @Override
    protected FhirVersion getFhirVersion()
    {
        return FhirVersion.R4;
    }

    @Test( expected = AuthenticationException.class )
    public void getQuestionnaireResponseWithoutAuthorization()
    {
        final IGenericClient client = createGenericClient();
        client.read().resource( QuestionnaireResponse.class ).withId( "deR4kl4mnf7" ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void getQuestionnaireResponseWithInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/deR4kl4mnf7.json?fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated," +
                "dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.read().resource( QuestionnaireResponse.class ).withId( "deR4kl4mnf7" ).execute();
    }

    @Test
    public void getQuestionnaireResponseRepeated() throws Exception
    {
        getQuestionnaireResponse();
        getQuestionnaireResponse();
    }

    private void getQuestionnaireResponse() throws Exception
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/deR4kl4mnf7.json?fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated," +
                "dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-event-91-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        QuestionnaireResponse questionnaireResponse = client.read().resource( QuestionnaireResponse.class ).withId( "deR4kl4mnf7" ).execute();
        Assert.assertEquals( "Patient/JeR2Ul4mZfx", questionnaireResponse.getSubject().getReference() );
        Assert.assertEquals( "Location/ldXIdLNUNEn",
            Objects.requireNonNull( LocationExtensionUtils.getValue( questionnaireResponse ) ).getReferenceElement().getValue() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test( expected = AuthenticationException.class )
    public void searchQuestionnaireResponseWithoutAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        final IGenericClient client = createGenericClient();
        client.search().forResource( QuestionnaireResponse.class ).returnBundle( Bundle.class ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void searchQuestionnaireResponseInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events.json?skipPaging=false&page=1&pageSize=10&ouMode=ACCESSIBLE&fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate," +
                "coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.search().forResource( QuestionnaireResponse.class ).returnBundle( Bundle.class ).execute();
    }

    @Test
    public void searchQuestionnaireResponseRepeated() throws Exception
    {
        searchQuestionnaireResponse();
        searchQuestionnaireResponse();
    }

    private void searchQuestionnaireResponse() throws Exception
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events.json?skipPaging=false&page=1&pageSize=10&ouMode=ACCESSIBLE&fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate," +
                "dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-91-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( QuestionnaireResponse.class ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 1, bundle.getEntry().size() );
        QuestionnaireResponse questionnaireResponse = (QuestionnaireResponse) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( "Patient/JeR2Ul4mZfx", questionnaireResponse.getSubject().getReference() );
        Assert.assertEquals( "Location/ldXIdLNUNEn",
            Objects.requireNonNull( LocationExtensionUtils.getValue( questionnaireResponse ) ).getReferenceElement().getValue() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test
    public void searchQuestionnaireResponsePatient() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events.json?trackedEntityInstance=JeR2Ul4mZfx&skipPaging=false&page=1&pageSize=10&ouMode=ACCESSIBLE&fields=deleted,event,orgUnit,program,enrollment," +
                "trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-91-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( QuestionnaireResponse.class ).where( QuestionnaireResponse.PATIENT.hasId( new IdType( "Patient", "JeR2Ul4mZfx" ) ) ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 1, bundle.getEntry().size() );
        QuestionnaireResponse questionnaireResponse = (QuestionnaireResponse) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( "Patient/JeR2Ul4mZfx", questionnaireResponse.getSubject().getReference() );
        Assert.assertEquals( "Location/ldXIdLNUNEn",
            Objects.requireNonNull( LocationExtensionUtils.getValue( questionnaireResponse ) ).getReferenceElement().getValue() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test( expected = AuthenticationException.class )
    public void createQuestionnaireResponseWithoutAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();

        QuestionnaireResponse questionnaireResponse = (QuestionnaireResponse) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-questionnaire-response-91.json", StandardCharsets.UTF_8 ) );
        questionnaireResponse.setId( (IdType) null );
        questionnaireResponse.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );

        final IGenericClient client = createGenericClient();
        client.create().resource( questionnaireResponse ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void createQuestionnaireResponseInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events.json?strategy=CREATE" ) ).andExpect( method( HttpMethod.POST ) )
            .andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-91-create.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        QuestionnaireResponse questionnaireResponse = (QuestionnaireResponse) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-questionnaire-response-91.json", StandardCharsets.UTF_8 ) );
        questionnaireResponse.setId( (IdType) null );
        questionnaireResponse.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );
        LocationExtensionUtils.setValue( questionnaireResponse, new Reference( "Location/ldXIdLNUNEn" ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.create().resource( questionnaireResponse ).execute();
    }

    @Test
    public void createQuestionnaireResponse() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events.json?strategy=CREATE" ) ).andExpect( method( HttpMethod.POST ) )
            .andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-91-create.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-91-create-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        QuestionnaireResponse questionnaireResponse = (QuestionnaireResponse) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-questionnaire-response-91.json", StandardCharsets.UTF_8 ) );
        questionnaireResponse.setId( (IdType) null );
        questionnaireResponse.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );
        LocationExtensionUtils.setValue( questionnaireResponse, new Reference( "Location/ldXIdLNUNEn" ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        MethodOutcome methodOutcome = client.create().resource( questionnaireResponse ).execute();
        Assert.assertEquals( Boolean.TRUE, methodOutcome.getCreated() );
        Assert.assertEquals( "http://localhost:" + localPort + "/fhir/r4/default/QuestionnaireResponse/deR4kl4mnf7", methodOutcome.getId().toString() );
    }

    @Test( expected = AuthenticationException.class )
    public void updateQuestionnaireResponseWithoutAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx.json?" +
                "fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-tei-15-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        QuestionnaireResponse questionnaireResponse = (QuestionnaireResponse) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-questionnaire-response-91.json", StandardCharsets.UTF_8 ) );
        questionnaireResponse.setId( "deR4kl4mnf7" );
        questionnaireResponse.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );

        final IGenericClient client = createGenericClient();
        client.update().resource( questionnaireResponse ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void updateQuestionnaireResponseInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.between( 1, 2 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/deR4kl4mnf7.json?fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated," +
                "dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        QuestionnaireResponse questionnaireResponse = (QuestionnaireResponse) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-questionnaire-response-91.json", StandardCharsets.UTF_8 ) );
        questionnaireResponse.setId( "deR4kl4mnf7" );
        questionnaireResponse.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.update().resource( questionnaireResponse ).execute();
    }

    @Test
    public void updateQuestionnaireResponse() throws Exception
    {
        expectProgramStageMetadataRequests();

        userDhis2Server.expect( ExpectedCount.between( 1, 3 ), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/deR4kl4mnf7.json?fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate," +
            "dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-event-91-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/deR4kl4mnf7.json?mergeMode=MERGE" ) ).andExpect( method( HttpMethod.PUT ) )
            .andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-91-update.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-91-update-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        QuestionnaireResponse questionnaireResponse = (QuestionnaireResponse) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-questionnaire-response-91.json", StandardCharsets.UTF_8 ) );
        questionnaireResponse.setId( "deR4kl4mnf7" );
        questionnaireResponse.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );
        questionnaireResponse.setStatus( QuestionnaireResponse.QuestionnaireResponseStatus.INPROGRESS );
        LocationExtensionUtils.setValue( questionnaireResponse, new Reference( "Location/ldXIdLNUNEn" ) );
        EventStatusExtensionUtils.setValue( questionnaireResponse, EventStatus.OVERDUE, ResourceFactory::createType );
        DueDateExtensionUtils.setValue( questionnaireResponse,
            Date.from( LocalDate.of( 2018, 11, 14 ).atStartOfDay( ZoneId.systemDefault() ).toInstant() ), ResourceFactory::createType );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        MethodOutcome methodOutcome = client.update().resource( questionnaireResponse ).execute();
        Assert.assertNotEquals( Boolean.TRUE, methodOutcome.getCreated() );
        Assert.assertEquals( "http://localhost:" + localPort + "/fhir/r4/default/QuestionnaireResponse/deR4kl4mnf7", methodOutcome.getId().toString() );
    }

    @Test( expected = AuthenticationException.class )
    public void deleteQuestionnaireResponseWithoutAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();

        final IGenericClient client = createGenericClient();
        client.delete().resourceById( "QuestionnaireResponse", "deR4kl4mnf7" ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void deleteQuestionnaireResponseInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/deR4kl4mnf7" ) )
            .andExpect( method( HttpMethod.DELETE ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.delete().resourceById( "QuestionnaireResponse", "deR4kl4mnf7" ).execute();

        userDhis2Server.verify();
    }

    @Test
    public void deleteQuestionnaireResponse() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/deR4kl4mnf7" ) )
            .andExpect( method( HttpMethod.DELETE ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-event-91-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        client.delete().resourceById( "QuestionnaireResponse", "deR4kl4mnf7" ).execute();

        userDhis2Server.verify();
    }
}
