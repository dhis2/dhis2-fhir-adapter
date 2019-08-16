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
import org.dhis2.fhir.adapter.fhir.extension.IncidentDateExtensionUtils;
import org.dhis2.fhir.adapter.fhir.extension.LocationExtensionUtils;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.server.AbstractProgramStageFhirRestAppTest;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CarePlan;
import org.hl7.fhir.r4.model.IdType;
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
 * R4 tests for rest interfaces that access
 * DHIS2 enrollments. <b>Some methods are executed twice in order to
 * verify that no cached data is used without authentication.</b>
 *
 * @author volsch
 */
public class R4CarePlanEnrollmentFhirRestAppTest extends AbstractProgramStageFhirRestAppTest
{
    @Nonnull
    @Override
    protected FhirVersion getFhirVersion()
    {
        return FhirVersion.R4;
    }

    @Test( expected = AuthenticationException.class )
    public void getCarePlanWithoutAuthorization()
    {
        final IGenericClient client = createGenericClient();
        client.read().resource( CarePlan.class ).withId( "ieR4nl4muff" ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void getCarePlanWithInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments/ieR4nl4muff.json" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.read().resource( CarePlan.class ).withId( "ieR4nl4muff" ).execute();
    }

    @Test
    public void getCarePlanRepeated() throws Exception
    {
        getCarePlan();
        getCarePlan();
    }

    private void getCarePlan() throws Exception
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments/ieR4nl4muff.json" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-enrollment-90-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/ldXIdLNUNEp.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-org-unit-OU_4567.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        CarePlan carePlan = client.read().resource( CarePlan.class ).withId( "ieR4nl4muff" ).execute();
        Assert.assertEquals( 0, carePlan.getIdentifier().size() );
        Assert.assertEquals( "Patient/JeR2Ul4mZfx", carePlan.getSubject().getReference() );
        Assert.assertEquals( "Location/ldXIdLNUNEn",
            Objects.requireNonNull( LocationExtensionUtils.getValue( carePlan ) ).getReferenceElement().getValue() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test( expected = AuthenticationException.class )
    public void searchCarePlanWithoutAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        final IGenericClient client = createGenericClient();
        client.search().forResource( CarePlan.class ).returnBundle( Bundle.class ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void searchCarePlanInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments.json?skipPaging=false&page=1&pageSize=10&ouMode=ACCESSIBLE&fields=:all" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.search().forResource( CarePlan.class ).returnBundle( Bundle.class ).execute();
    }

    @Test
    public void searchCarePlanRepeated() throws Exception
    {
        searchCarePlan();
        searchCarePlan();
    }

    private void searchCarePlan() throws Exception
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments.json?skipPaging=false&page=1&pageSize=10&ouMode=ACCESSIBLE&fields=:all" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-90-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( CarePlan.class ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 1, bundle.getEntry().size() );
        CarePlan carePlan = (CarePlan) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( 0, carePlan.getIdentifier().size() );
        Assert.assertEquals( "Patient/JeR2Ul4mZfx", carePlan.getSubject().getReference() );
        Assert.assertEquals( "Location/ldXIdLNUNEn",
            Objects.requireNonNull( LocationExtensionUtils.getValue( carePlan ) ).getReferenceElement().getValue() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test
    public void searchCarePlanPatient() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments.json?trackedEntityInstance=JeR2Ul4mZfx&skipPaging=false&page=1&pageSize=10&ouMode=ACCESSIBLE&fields=:all" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-90-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( CarePlan.class ).where( CarePlan.PATIENT.hasId( new IdType( "Patient", "JeR2Ul4mZfx" ) ) ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 1, bundle.getEntry().size() );
        CarePlan carePlan = (CarePlan) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( 0, carePlan.getIdentifier().size() );
        Assert.assertEquals( "Patient/JeR2Ul4mZfx", carePlan.getSubject().getReference() );
        Assert.assertEquals( "Location/ldXIdLNUNEn",
            Objects.requireNonNull( LocationExtensionUtils.getValue( carePlan ) ).getReferenceElement().getValue() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test( expected = AuthenticationException.class )
    public void createCarePlanWithoutAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();

        CarePlan carePlan = (CarePlan) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-care-plan-90.json", StandardCharsets.UTF_8 ) );
        carePlan.setId( (IdType) null );
        carePlan.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );

        final IGenericClient client = createGenericClient();
        client.create().resource( carePlan ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void createCarePlanInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments.json?strategy=CREATE" ) ).andExpect( method( HttpMethod.POST ) )
            .andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-90-create.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        CarePlan carePlan = (CarePlan) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-care-plan-90.json", StandardCharsets.UTF_8 ) );
        carePlan.setId( (IdType) null );
        carePlan.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );
        LocationExtensionUtils.setValue( carePlan, new Reference( "Location/ldXIdLNUNEn" ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.create().resource( carePlan ).execute();
    }

    @Test
    public void createCarePlan() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments.json?strategy=CREATE" ) ).andExpect( method( HttpMethod.POST ) )
            .andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-90-create.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-90-create-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        CarePlan carePlan = (CarePlan) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-care-plan-90.json", StandardCharsets.UTF_8 ) );
        carePlan.setId( (IdType) null );
        carePlan.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );
        LocationExtensionUtils.setValue( carePlan, new Reference( "Location/ldXIdLNUNEn" ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        MethodOutcome methodOutcome = client.create().resource( carePlan ).execute();
        Assert.assertEquals( Boolean.TRUE, methodOutcome.getCreated() );
        Assert.assertEquals( "http://localhost:" + localPort + "/fhir/r4/default/CarePlan/ieR4nl4muff", methodOutcome.getId().toString() );
    }

    @Test( expected = AuthenticationException.class )
    public void updateCarePlanWithoutAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx.json?" +
                "fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-tei-15-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        CarePlan carePlan = (CarePlan) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-care-plan-90.json", StandardCharsets.UTF_8 ) );
        carePlan.setId( "ieR4nl4muff" );
        carePlan.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );

        final IGenericClient client = createGenericClient();
        client.update().resource( carePlan ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void updateCarePlanInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.between( 1, 2 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments/ieR4nl4muff.json" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        CarePlan carePlan = (CarePlan) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-care-plan-90.json", StandardCharsets.UTF_8 ) );
        carePlan.setId( "ieR4nl4muff" );
        carePlan.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.update().resource( carePlan ).execute();
    }

    @Test
    public void updateCarePlan() throws Exception
    {
        expectProgramStageMetadataRequests();

        userDhis2Server.expect( ExpectedCount.between( 1, 3 ), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments/ieR4nl4muff.json" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-enrollment-90-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments/ieR4nl4muff.json?mergeMode=MERGE" ) ).andExpect( method( HttpMethod.PUT ) )
            .andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-90-update.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-90-update-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        CarePlan carePlan = (CarePlan) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-care-plan-90.json", StandardCharsets.UTF_8 ) );
        carePlan.setId( "ieR4nl4muff" );
        carePlan.setSubject( new Reference( "Patient/JeR2Ul4mZfx" ) );
        carePlan.setStatus( CarePlan.CarePlanStatus.COMPLETED );
        LocationExtensionUtils.setValue( carePlan, new Reference( "Location/ldXIdLNUNEn" ) );
        IncidentDateExtensionUtils.setValue( carePlan,
            Date.from( LocalDate.of( 2018, 11, 9 ).atStartOfDay( ZoneId.systemDefault() ).toInstant() ), ResourceFactory::createType );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        MethodOutcome methodOutcome = client.update().resource( carePlan ).execute();
        Assert.assertNotEquals( Boolean.TRUE, methodOutcome.getCreated() );
        Assert.assertEquals( "http://localhost:" + localPort + "/fhir/r4/default/CarePlan/ieR4nl4muff", methodOutcome.getId().toString() );
    }

    @Test( expected = AuthenticationException.class )
    public void deleteCarePlanWithoutAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();

        final IGenericClient client = createGenericClient();
        client.delete().resourceById( "CarePlan", "ieR4nl4muff" ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void deleteCarePlanInvalidAuthorization() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments/ieR4nl4muff" ) )
            .andExpect( method( HttpMethod.DELETE ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.delete().resourceById( "CarePlan", "ieR4nl4muff" ).execute();

        userDhis2Server.verify();
    }

    @Test
    public void deleteCarePlan() throws Exception
    {
        expectProgramStageMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments/ieR4nl4muff" ) )
            .andExpect( method( HttpMethod.DELETE ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-enrollment-90-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        client.delete().resourceById( "CarePlan", "ieR4nl4muff" ).execute();

        userDhis2Server.verify();
    }
}
