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
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.dhis2.fhir.adapter.fhir.server.AbstractTrackedEntityInstanceFhirRestAppTest;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * R4 tests for rest interfaces that access
 * DHIS2 tracked entity instances. <b>Some methods are executed twice in order to
 * verify that no cached data is used without authentication.</b>
 *
 * @author volsch
 */
public class R4TrackedEntityInstanceFhirRestAppTest extends AbstractTrackedEntityInstanceFhirRestAppTest
{
    @Nonnull
    @Override
    protected FhirVersion getFhirVersion()
    {
        return FhirVersion.R4;
    }

    @Test( expected = AuthenticationException.class )
    public void getPatientWithoutAuthorization()
    {
        final IGenericClient client = createGenericClient();
        client.read().resource( Patient.class ).withId( "JeR2Ul4mZfx" ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void getPatientWithInvalidAuthorization() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx.json?" +
                "fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.read().resource( Patient.class ).withId( "JeR2Ul4mZfx" ).execute();
    }

    @Test
    public void getPatientRepeated() throws Exception
    {
        getPatient();
        getPatient();
    }

    private void getPatient() throws Exception
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        expectTrackedEntityMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx.json?" +
                "fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-tei-15-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_1234" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Patient patient = client.read().resource( Patient.class ).withId( "JeR2Ul4mZfx" ).execute();
        Assert.assertEquals( "West", patient.getNameFirstRep().getFamily() );
        Assert.assertEquals( 1, patient.getIdentifier().size() );
        Assert.assertEquals( "http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier", patient.getIdentifier().get( 0 ).getSystem() );
        Assert.assertEquals( "PT_81589", patient.getIdentifier().get( 0 ).getValue() );
        Assert.assertEquals( "Organization/ldXIdLNUNEn", patient.getManagingOrganization().getReference() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test( expected = AuthenticationException.class )
    public void getPatientByIdentifierWithoutAuthorization() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        final IGenericClient client = createGenericClient();
        client.search().forResource( Patient.class ).where( Patient.IDENTIFIER.exactly().systemAndIdentifier( "http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier", "OU_1234" ) ).returnBundle( Bundle.class ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void getPatientByIdentifierInvalidAuthorization() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?trackedEntityType=MCPQUTHX1Ze&ouMode=ACCESSIBLE&filter=jD1NGmSntCt:EQ:OU_1234&pageSize=1" +
                "&fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.search().forResource( Patient.class ).where( Patient.IDENTIFIER.exactly().systemAndIdentifier( "http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier", "OU_1234" ) ).returnBundle( Bundle.class ).execute();
    }

    @Test
    public void getPatientByIdentifierRepeated() throws Exception
    {
        getPatientByIdentifier();
        getPatientByIdentifier();
    }

    private void getPatientByIdentifier() throws Exception
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        expectTrackedEntityMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?trackedEntityType=MCPQUTHX1Ze&ouMode=ACCESSIBLE&filter=jD1NGmSntCt:EQ:OU_1234&pageSize=1" +
                "&fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-15-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_1234" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( Patient.class ).where( Patient.IDENTIFIER.exactly().systemAndIdentifier( "http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier", "OU_1234" ) ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 1, bundle.getEntry().size() );
        Patient patient = (Patient) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( "West", patient.getNameFirstRep().getFamily() );
        Assert.assertEquals( 1, patient.getIdentifier().size() );
        Assert.assertEquals( "http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier", patient.getIdentifier().get( 0 ).getSystem() );
        Assert.assertEquals( "PT_81589", patient.getIdentifier().get( 0 ).getValue() );
        Assert.assertEquals( "Organization/ldXIdLNUNEn", patient.getManagingOrganization().getReference() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test( expected = AuthenticationException.class )
    public void searchPatientWithoutAuthorization() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        final IGenericClient client = createGenericClient();
        client.search().forResource( Patient.class ).returnBundle( Bundle.class ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void searchPatientInvalidAuthorization() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?skipPaging=false&page=1&pageSize=10&trackedEntityType=MCPQUTHX1Ze&ouMode=ACCESSIBLE" +
                "&fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.search().forResource( Patient.class ).returnBundle( Bundle.class ).execute();
    }

    @Test
    public void searchPatientRepeated() throws Exception
    {
        searchPatient();
        searchPatient();
    }

    private void searchPatient() throws Exception
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        expectTrackedEntityMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?skipPaging=false&page=1&pageSize=10&trackedEntityType=MCPQUTHX1Ze&ouMode=ACCESSIBLE" +
                "&fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/multi-tei-15-16-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_1234" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( Patient.class ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 2, bundle.getEntry().size() );
        Patient patient = (Patient) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( "West", patient.getNameFirstRep().getFamily() );
        Assert.assertEquals( 1, patient.getIdentifier().size() );
        Assert.assertEquals( "http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier", patient.getIdentifier().get( 0 ).getSystem() );
        Assert.assertEquals( "PT_81589", patient.getIdentifier().get( 0 ).getValue() );
        Assert.assertEquals( "Organization/ldXIdLNUNEn", patient.getManagingOrganization().getReference() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test
    public void searchPatientOrganization() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?ou=ldXIdLNUNEn&skipPaging=false&page=1&pageSize=10&trackedEntityType=MCPQUTHX1Ze&ouMode=SELECTED" +
                "&fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/multi-tei-15-16-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_1234" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( Patient.class ).where( Patient.ORGANIZATION.hasId( new IdType( "Organization", "ldXIdLNUNEn" ) ) ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 2, bundle.getEntry().size() );
        Patient patient = (Patient) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( "West", patient.getNameFirstRep().getFamily() );
        Assert.assertEquals( 1, patient.getIdentifier().size() );
        Assert.assertEquals( "http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier", patient.getIdentifier().get( 0 ).getSystem() );
        Assert.assertEquals( "PT_81589", patient.getIdentifier().get( 0 ).getValue() );
        Assert.assertEquals( "Organization/ldXIdLNUNEn", patient.getManagingOrganization().getReference() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test
    public void searchPatientGiven() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?filter=TfdH5KvFmMy:like:ete&skipPaging=false&page=1&pageSize=10&trackedEntityType=MCPQUTHX1Ze&ouMode=ACCESSIBLE" +
                "&fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/multi-tei-15-16-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_1234" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( Patient.class ).where( Patient.GIVEN.contains().value( "ete" ) ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 2, bundle.getEntry().size() );
        Patient patient = (Patient) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( "West", patient.getNameFirstRep().getFamily() );
        Assert.assertEquals( 1, patient.getIdentifier().size() );
        Assert.assertEquals( "http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier", patient.getIdentifier().get( 0 ).getSystem() );
        Assert.assertEquals( "PT_81589", patient.getIdentifier().get( 0 ).getValue() );
        Assert.assertEquals( "Organization/ldXIdLNUNEn", patient.getManagingOrganization().getReference() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test( expected = AuthenticationException.class )
    public void createPatientWithoutAuthorization() throws Exception
    {
        expectTrackedEntityMetadataRequests();

        Patient patient = (Patient) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-patient-15.json", StandardCharsets.UTF_8 ) );
        patient.getIdentifierFirstRep().setSystem( "http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier" ).setValue( "PT_88589" );
        patient.setId( (IdType) null );
        patient.setManagingOrganization( new Reference( "Organization/ldXIdLNUNEn" ) );

        final IGenericClient client = createGenericClient();
        client.create().resource( patient ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void createPatientInvalidAuthorization() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?strategy=CREATE" ) ).andExpect( method( HttpMethod.POST ) )
            .andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) ).andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-15-create.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        Patient patient = (Patient) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-patient-15.json", StandardCharsets.UTF_8 ) );
        patient.getIdentifierFirstRep().setSystem( "http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier" ).setValue( "PT_88589" );
        patient.setId( (IdType) null );
        patient.setManagingOrganization( new Reference( "Organization/ldXIdLNUNEn" ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        MethodOutcome methodOutcome = client.create().resource( patient ).execute();
    }

    @Test
    public void createPatient() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?strategy=CREATE" ) ).andExpect( method( HttpMethod.POST ) )
            .andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) ).andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-15-create.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-15-create-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        Patient patient = (Patient) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-patient-15.json", StandardCharsets.UTF_8 ) );
        patient.getIdentifierFirstRep().setSystem( "http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier" ).setValue( "PT_88589" );
        patient.setId( (IdType) null );
        patient.setManagingOrganization( new Reference( "Organization/ldXIdLNUNEn" ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        MethodOutcome methodOutcome = client.create().resource( patient ).execute();
        Assert.assertEquals( Boolean.TRUE, methodOutcome.getCreated() );
        Assert.assertEquals( "http://localhost:" + localPort + "/fhir/r4/default/Patient/JeR2Ul4mZfx", methodOutcome.getId().toString() );
    }

    @Test( expected = AuthenticationException.class )
    public void updatePatientWithoutAuthorization() throws Exception
    {
        expectTrackedEntityMetadataRequests();

        Patient patient = (Patient) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-patient-15.json", StandardCharsets.UTF_8 ) );
        patient.getIdentifierFirstRep().setSystem( "http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier" ).setValue( "PT_88589" );
        patient.setId( "JeR2Ul4mZfx" );
        patient.setManagingOrganization( new Reference( "Organization/ldXIdLNUNEn" ) );
        patient.getNameFirstRep().setGiven( new ArrayList<>() );
        patient.getNameFirstRep().addGiven( "Joe" ).addGiven( "Scott" );

        final IGenericClient client = createGenericClient();
        client.update().resource( patient ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void updatePatientInvalidAuthorization() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx.json?" +
            "fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        Patient patient = (Patient) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-patient-15.json", StandardCharsets.UTF_8 ) );
        patient.getIdentifierFirstRep().setSystem( "http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier" ).setValue( "PT_88589" );
        patient.setId( "JeR2Ul4mZfx" );
        patient.setManagingOrganization( new Reference( "Organization/ldXIdLNUNEn" ) );
        patient.getNameFirstRep().setGiven( new ArrayList<>() );
        patient.getNameFirstRep().addGiven( "Joe" ).addGiven( "Scott" );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.update().resource( patient ).execute();
    }

    @Test
    public void updatePatient() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx.json?" +
            "fields=deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-tei-15-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx.json?mergeMode=MERGE" ) ).andExpect( method( HttpMethod.PUT ) )
            .andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) ).andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-15-update-simple.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-15-create-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        Patient patient = (Patient) getFhirContext().newJsonParser().parseResource(
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-patient-15.json", StandardCharsets.UTF_8 ) );
        patient.getIdentifierFirstRep().setSystem( "http://www.dhis2.org/dhis2fhiradapter/systems/patient-identifier" ).setValue( "PT_88589" );
        patient.setId( "JeR2Ul4mZfx" );
        patient.setManagingOrganization( new Reference( "Organization/ldXIdLNUNEn" ) );
        patient.getNameFirstRep().setGiven( new ArrayList<>() );
        patient.getNameFirstRep().addGiven( "Joe" ).addGiven( "Scott" );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        MethodOutcome methodOutcome = client.update().resource( patient ).execute();
        Assert.assertNotEquals( Boolean.TRUE, methodOutcome.getCreated() );
        Assert.assertEquals( "http://localhost:" + localPort + "/fhir/r4/default/Patient/JeR2Ul4mZfx", methodOutcome.getId().toString() );
    }

    @Test( expected = AuthenticationException.class )
    public void deletePatientWithoutAuthorization() throws Exception
    {
        expectTrackedEntityMetadataRequests();

        final IGenericClient client = createGenericClient();
        client.delete().resourceById( "Patient", "JeR2Ul4mZfx" ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void deletePatientInvalidAuthorization() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx" ) )
            .andExpect( method( HttpMethod.DELETE ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.delete().resourceById( "Patient", "JeR2Ul4mZfx" ).execute();

        userDhis2Server.verify();
    }

    @Test
    public void deletePatient() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx" ) )
            .andExpect( method( HttpMethod.DELETE ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-tei-15-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        client.delete().resourceById( "Patient", "JeR2Ul4mZfx" ).execute();

        userDhis2Server.verify();
    }
}
