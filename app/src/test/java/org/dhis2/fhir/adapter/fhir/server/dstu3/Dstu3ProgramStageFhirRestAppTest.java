package org.dhis2.fhir.adapter.fhir.server.dstu3;

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
import org.dhis2.fhir.adapter.fhir.metadata.model.System;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.server.AbstractProgramStageFhirRestAppTest;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * DSTU3 tests for rest interfaces that access
 * DHIS2 program stages. <b>Some methods are executed twice in order to
 * verify that no cached data is used without authentication.</b>
 *
 * @author volsch
 */
public class Dstu3ProgramStageFhirRestAppTest extends AbstractProgramStageFhirRestAppTest
{
    @Nonnull
    @Override
    protected FhirVersion getFhirVersion()
    {
        return FhirVersion.DSTU3;
    }

    @Test( expected = AuthenticationException.class )
    public void getObservationWithoutAuthorization()
    {
        final IGenericClient client = createGenericClient();
        client.read().resource( Observation.class ).withId( "ps-deR4kl4mnf7-097d9ee0bdb344aeb9613b4584bad1db" ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void getObservationWithInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/deR4kl4mnf7.json?" +
                "fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.read().resource( Observation.class ).withId( "ps-deR4kl4mnf7-097d9ee0bdb344aeb9613b4584bad1db" ).execute();
    }

    @Test
    public void getObservationRepeated() throws Exception
    {
        getObservation();
        getObservation();
    }

    private void getObservation() throws Exception
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/deR4kl4mnf7.json?" +
                "fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-event-70-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/ldXIdLNUNEp.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-org-unit-OU_4567.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Observation observation = client.read().resource( Observation.class ).withId( "ps-deR4kl4mnf7-097d9ee0bdb344aeb9613b4584bad1db" ).execute();
        Assert.assertEquals( 0, observation.getIdentifier().size() );
        Assert.assertEquals( "Patient/JeR2Ul4mZfx", observation.getSubject().getReference() );
        Assert.assertEquals( "Organization/ldXIdLNUNEp", observation.getPerformerFirstRep().getReference() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test( expected = AuthenticationException.class )
    public void searchObservationWithoutAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        final IGenericClient client = createGenericClient();
        client.search().forResource( Observation.class ).returnBundle( Bundle.class ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void searchObservationInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events.json?skipPaging=false&page=1&pageSize=10&ouMode=ACCESSIBLE&" +
                "fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" +
                "&program=EPDyQuoRnXk&programStage=qowTSevVSkd" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.search().forResource( Observation.class ).returnBundle( Bundle.class ).execute();
    }

    @Test
    public void searchObservationRepeated() throws Exception
    {
        searchObservation();
        searchObservation();
    }

    private void searchObservation() throws Exception
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events.json?skipPaging=false&page=1&pageSize=10&ouMode=ACCESSIBLE&fields=deleted,event,orgUnit,program,enrollment," +
                "trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D&program=EPDyQuoRnXk&programStage=qowTSevVSkd" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-70-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events.json?skipPaging=false&page=1&pageSize=9&ouMode=ACCESSIBLE&fields=deleted,event,orgUnit,program,enrollment," +
                "trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D&program=EPDyQuoRnXk&programStage=MsWxkiY6tMS" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-71-only-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/ldXIdLNUNEp.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-org-unit-OU_4567.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( Observation.class ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 2, bundle.getEntry().size() );
        Observation observation = (Observation) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( 0, observation.getIdentifier().size() );
        Assert.assertEquals( "Patient/JeR2Ul4mZfx", observation.getSubject().getReference() );
        Assert.assertEquals( "Organization/ldXIdLNUNEp", observation.getPerformerFirstRep().getReference() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test
    public void searchObservationPatient() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events.json?trackedEntityInstance=JeR2Ul4mZfx&skipPaging=false&page=1&pageSize=10&ouMode=ACCESSIBLE&fields=deleted,event,orgUnit," +
                "program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D&program=EPDyQuoRnXk&programStage=qowTSevVSkd" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-70-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events.json?trackedEntityInstance=JeR2Ul4mZfx&skipPaging=false&page=1&pageSize=9&ouMode=ACCESSIBLE&fields=deleted,event,orgUnit," +
                "program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D&program=EPDyQuoRnXk&programStage=MsWxkiY6tMS" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-71-only-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/ldXIdLNUNEp.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-org-unit-OU_4567.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( Observation.class ).where( Observation.PATIENT.hasId( new IdType( "Patient", "JeR2Ul4mZfx" ) ) ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 2, bundle.getEntry().size() );
        Observation observation = (Observation) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( 0, observation.getIdentifier().size() );
        Assert.assertEquals( "Patient/JeR2Ul4mZfx", observation.getSubject().getReference() );
        Assert.assertEquals( "Organization/ldXIdLNUNEp", observation.getPerformerFirstRep().getReference() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test( expected = AuthenticationException.class )
    public void createObservationWithoutAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();

        Observation observation = (Observation) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-observation-70.json", StandardCharsets.UTF_8 ) );
        observation.setId( (IdType) null );
        observation.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );

        final IGenericClient client = createGenericClient();
        client.create().resource( observation ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void createObservationInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments.json?program=EPDyQuoRnXk&programStatus=ACTIVE&trackedEntityInstance=JeR2Ul4mZfx&ouMode=ACCESSIBLE&fields=:all&order=lastUpdated:desc&pageSize=1" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        Observation observation = (Observation) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-observation-70.json", StandardCharsets.UTF_8 ) );
        observation.setId( (IdType) null );
        observation.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.create().resource( observation ).execute();
    }

    @Test
    public void createObservation() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.between( 2, 4 ), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments.json?program=EPDyQuoRnXk&programStatus=ACTIVE&trackedEntityInstance=JeR2Ul4mZfx&ouMode=ACCESSIBLE&fields=:all&" +
            "order=lastUpdated:desc&pageSize=1" ) ).andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-empty.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/ldXIdLNUNEp.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-org-unit-OU_4567.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 1, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx.json?" +
                "fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-tei-15-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments.json?strategy=CREATE" ) ).andExpect( method( HttpMethod.POST ) )
            .andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-70-create.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-70-create-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        Observation observation = (Observation) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-observation-70.json", StandardCharsets.UTF_8 ) );
        observation.setId( (IdType) null );
        observation.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );
        observation.getPerformerFirstRep().setReference( "Organization/ldXIdLNUNEp" );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        MethodOutcome methodOutcome = client.create().resource( observation ).execute();
        Assert.assertEquals( Boolean.TRUE, methodOutcome.getCreated() );
        Assert.assertEquals( "http://localhost:" + localPort + "/fhir/dstu3/default/Observation/ps-deR4kl4mnf7-097d9ee0bdb344aeb9613b4584bad1db", methodOutcome.getId().toString() );
    }

    @Test
    public void createObservationByCodeSetCode() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.between( 2, 4 ), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments.json?program=EPDyQuoRnXk&programStatus=ACTIVE&trackedEntityInstance=JeR2Ul4mZfx&ouMode=ACCESSIBLE&fields=:all&" +
            "order=lastUpdated:desc&pageSize=1" ) ).andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-empty.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/ldXIdLNUNEp.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-org-unit-OU_4567.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 1, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx.json?" +
                "fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-tei-15-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments.json?strategy=CREATE" ) ).andExpect( method( HttpMethod.POST ) )
            .andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-70-create.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-70-create-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        Observation observation = (Observation) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-observation-70-codeset.json", StandardCharsets.UTF_8 ) );
        observation.setId( (IdType) null );
        observation.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );
        observation.getPerformerFirstRep().setReference( "Organization/ldXIdLNUNEp" );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        MethodOutcome methodOutcome = client.create().resource( observation ).execute();
        Assert.assertEquals( Boolean.TRUE, methodOutcome.getCreated() );
        Assert.assertEquals( "http://localhost:" + localPort + "/fhir/dstu3/default/Observation/ps-deR4kl4mnf7-097d9ee0bdb344aeb9613b4584bad1db", methodOutcome.getId().toString() );
    }

    @Test
    public void createObservationIdentifierReference() throws Exception
    {
        expectProgramStageMetadataRequests();

        userDhis2Server.expect( ExpectedCount.between( 2, 4 ), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments.json?program=EPDyQuoRnXk&programStatus=ACTIVE&trackedEntityInstance=JeR2Ul4mZfx&ouMode=ACCESSIBLE&fields=:all&" +
            "order=lastUpdated:desc&pageSize=1" ) ).andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-empty.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/ldXIdLNUNEp.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-org-unit-OU_4567.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 1, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx.json?" +
                "fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-tei-15-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments.json?strategy=CREATE" ) ).andExpect( method( HttpMethod.POST ) )
            .andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-70-create.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-70-create-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        Observation observation = (Observation) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-observation-70.json", StandardCharsets.UTF_8 ) );
        observation.setId( (IdType) null );
        observation.setSubject( new Reference().setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "JeR2Ul4mZfx" ) ) );
        observation.getPerformerFirstRep().setIdentifier( new Identifier().setSystem( System.DHIS2_FHIR_IDENTIFIER_URI ).setValue( "ldXIdLNUNEp" ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        MethodOutcome methodOutcome = client.create().resource( observation ).execute();
        Assert.assertEquals( Boolean.TRUE, methodOutcome.getCreated() );
        Assert.assertEquals( "http://localhost:" + localPort + "/fhir/dstu3/default/Observation/ps-deR4kl4mnf7-097d9ee0bdb344aeb9613b4584bad1db", methodOutcome.getId().toString() );
    }

    @Test( expected = AuthenticationException.class )
    public void updateObservationWithoutAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx.json?" +
                "fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-tei-15-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        Observation observation = (Observation) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-observation-70.json", StandardCharsets.UTF_8 ) );
        observation.setId( "ps-deR4kl4mnf7-097d9ee0bdb344aeb9613b4584bad1db" );
        observation.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );

        final IGenericClient client = createGenericClient();
        client.update().resource( observation ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void updateObservationInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.between( 1, 2 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/deR4kl4mnf7.json?fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated," +
                "dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        Observation observation = (Observation) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-observation-70.json", StandardCharsets.UTF_8 ) );
        observation.setId( "ps-deR4kl4mnf7-097d9ee0bdb344aeb9613b4584bad1db" );
        observation.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.update().resource( observation ).execute();
    }

    @Test
    public void updateObservation() throws Exception
    {
        expectProgramStageMetadataRequests();

        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_4567" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_4567.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/ldXIdLNUNEn.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        userDhis2Server.expect( ExpectedCount.between( 1, 3 ), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/deR4kl4mnf7.json?" +
            "fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-event-70-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments/ieR4nl4mufa.json" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-enrollment-70-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/aeR4bl7iufL/UnN0Jdrfr4o.json?mergeMode=MERGE" ) ).andExpect( method( HttpMethod.PUT ) )
            .andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-71-update.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-71-update-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        Observation observation = (Observation) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-observation-70.json", StandardCharsets.UTF_8 ) );
        observation.setId( "ps-deR4kl4mnf7-097d9ee0bdb344aeb9613b4584bad1db" );
        observation.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );
        observation.getPerformerFirstRep().setReference( "Organization/ldXIdLNUNEp" );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        MethodOutcome methodOutcome = client.update().resource( observation ).execute();
        Assert.assertNotEquals( Boolean.TRUE, methodOutcome.getCreated() );
        Assert.assertEquals( "http://localhost:" + localPort + "/fhir/dstu3/default/Observation/ps-deR4kl4mnf7-097d9ee0bdb344aeb9613b4584bad1db", methodOutcome.getId().toString() );
    }

    @Test( expected = AuthenticationException.class )
    public void deleteObservationWithoutAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();

        final IGenericClient client = createGenericClient();
        client.delete().resourceById( "Observation", "ps-deR4kl4mnf7-097d9ee0bdb344aeb9613b4584bad1db" ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void deleteObservationInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/deR4kl4mnf7.json?" +
            "fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.delete().resourceById( "Observation", "ps-deR4kl4mnf7-097d9ee0bdb344aeb9613b4584bad1db" ).execute();

        userDhis2Server.verify();
    }

    @Test
    public void deleteObservation() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/deR4kl4mnf7.json?" +
            "fields=deleted,event,orgUnit,program,enrollment,trackedEntityInstance,programStage,status,eventDate,dueDate,coordinate,lastUpdated,dataValues%5BdataElement,value,providedElsewhere,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-event-70-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments/ieR4nl4mufa.json" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-enrollment-70-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/events/deR4kl4mnf7/BnplxU2jGvX.json?mergeMode=MERGE" ) ).andExpect( method( HttpMethod.PUT ) )
            .andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-70-update-delete.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-event-70-update-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        client.delete().resourceById( "Observation", "ps-deR4kl4mnf7-097d9ee0bdb344aeb9613b4584bad1db" ).execute();

        userDhis2Server.verify();
    }
}
