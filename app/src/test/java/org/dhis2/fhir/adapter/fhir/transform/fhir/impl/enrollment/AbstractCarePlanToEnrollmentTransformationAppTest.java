package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.enrollment;

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

import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.commons.io.IOUtils;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;

import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests the transformation of a FHIR CarePlan to a DHIS2 Program Instance.
 *
 * @author volsch
 */
public abstract class AbstractCarePlanToEnrollmentTransformationAppTest
    extends AbstractEnrollmentTransformationAppTest
{
    @Test
    public void createEnrollment() throws Exception
    {
        expectMetadataRequests();
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Patient/15" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-patient-15.json", StandardCharsets.UTF_8 ) ) ) );
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Organization/19" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-organization-19.json", StandardCharsets.UTF_8 ) ) ) );
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Organization/18" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-organization-18.json", StandardCharsets.UTF_8 ) ) ) );

        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?trackedEntityType=MCPQUTHX1Ze&ouMode=ACCESSIBLE&filter=jD1NGmSntCt:EQ:PT_88589&pageSize=2&fields=" +
            "deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-15-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/ldXIdLNUNEn.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments.json?strategy=CREATE" ) ).andExpect( method( HttpMethod.POST ) )
            .andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-90-create.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-90-create-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        notifyResource( FhirResourceType.CARE_PLAN, null,
            "90", IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-care-plan-90.json", StandardCharsets.UTF_8 ), true );

        waitForEmptyResourceQueue();
        userDhis2Server.verify();
        systemDhis2Server.verify();
    }

    @Test
    public void updateEnrollment() throws Exception
    {
        expectMetadataRequests();
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Patient/15" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-patient-15.json", StandardCharsets.UTF_8 ) ) ) );
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Organization/19" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-organization-19.json", StandardCharsets.UTF_8 ) ) ) );
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Organization/18" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-organization-18.json", StandardCharsets.UTF_8 ) ) ) );

        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?trackedEntityType=MCPQUTHX1Ze&ouMode=ACCESSIBLE&filter=jD1NGmSntCt:EQ:PT_88589&pageSize=2&fields=" +
            "deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-15-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/ldXIdLNUNEn.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments.json?strategy=CREATE" ) ).andExpect( method( HttpMethod.POST ) )
            .andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-90-create.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-90-create-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        userDhis2Server.expect( ExpectedCount.between( 1, 3 ), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments/ieR4nl4muff.json" ) ).andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization",
            testConfiguration.getDhis2UserAuthorization() ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-enrollment-90-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/enrollments/ieR4nl4muff.json?mergeMode=MERGE" ) ).andExpect( method( HttpMethod.PUT ) )
            .andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-90-update.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-enrollment-90-update-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        notifyResource( FhirResourceType.CARE_PLAN, null,
            "90", IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-care-plan-90.json", StandardCharsets.UTF_8 ), true );

        waitForEmptyResourceQueue();

        notifyResource( FhirResourceType.CARE_PLAN, null,
            "90", IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-care-plan-90-update.json", StandardCharsets.UTF_8 ), true );

        waitForEmptyResourceQueue();
        userDhis2Server.verify();
        systemDhis2Server.verify();
    }
}
