package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.trackedentity;

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
 * Tests the transformation of a FHIR patient to a DHIS2 Tracked Entity.
 *
 * @author volsch
 */
public abstract class AbstractPatientToTrackedEntityInstanceTransformationAppTest
    extends AbstractTrackedEntityInstanceTransformationAppTest
{
    @Test
    public void createPatient() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Organization/19" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-organization-19.json", StandardCharsets.UTF_8 ) ) ) );
        systemDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_U_7777" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-empty.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Organization/18" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-organization-18.json", StandardCharsets.UTF_8 ) ) ) );
        systemDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_1234" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        userDhis2Server.expect( ExpectedCount.twice(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?trackedEntityType=MCPQUTHX1Ze&ouMode=ACCESSIBLE&filter=jD1NGmSntCt:EQ:PT_88589&pageSize=2&fields=" +
            "deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-empty.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?strategy=CREATE" ) )
            .andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) ).andExpect( method( HttpMethod.POST ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-15-create.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-15-create-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?trackedEntityType=MCPQUTHX1Ze&ouMode=ACCESSIBLE&filter=jD1NGmSntCt:EQ:PT_88589&pageSize=2&fields=" +
            "deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-15-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        systemDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/ldXIdLNUNEn.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx.json?mergeMode=MERGE" ) )
            .andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) ).andExpect( method( HttpMethod.PUT ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-15-update.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-15-create-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );
        userDhis2Server.expect( ExpectedCount.never(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2Ul4mZfx.json?fields=lastUpdated" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-15-update-last-updated.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        notifyResource( FhirResourceType.PATIENT,
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/search-patient-15.json", StandardCharsets.UTF_8 ),
            "15", IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-patient-15.json", StandardCharsets.UTF_8 ), false );

        waitForEmptyResourceQueue();
        userDhis2Server.verify();
        systemDhis2Server.verify();
    }

    @Test
    public void createRelatedPersonFirst() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Patient/16" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-patient-16.json", StandardCharsets.UTF_8 ) ) ) );
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Organization/19" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-organization-19.json", StandardCharsets.UTF_8 ) ) ) );
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Organization/18" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-organization-18.json", StandardCharsets.UTF_8 ) ) ) );
        systemDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_1234" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        userDhis2Server.expect( ExpectedCount.times( 3 ), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?trackedEntityType=MCPQUTHX1Ze&ouMode=ACCESSIBLE&filter=jD1NGmSntCt:EQ:PT_88588&pageSize=2&fields=" +
            "deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-empty.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?strategy=CREATE" ) )
            .andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) ).andExpect( method( HttpMethod.POST ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-16-create.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-16-create-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );

        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?trackedEntityType=MCPQUTHX1Ze&ouMode=ACCESSIBLE&filter=jD1NGmSntCt:EQ:PT_88588&pageSize=2&fields=" +
            "deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-16-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        systemDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/ldXIdLNUNEn.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2xl4mZfx.json?mergeMode=MERGE" ) )
            .andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) ).andExpect( method( HttpMethod.PUT ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-16-update.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-16-create-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );
        userDhis2Server.expect( ExpectedCount.never(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2xl4mZfx.json?fields=lastUpdated" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-16-update-last-updated.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ).headers( createDefaultHeaders() ) );

        notifyResource( FhirResourceType.RELATED_PERSON,
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/search-related-person-20.json", StandardCharsets.UTF_8 ),
            "20", IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-related-person-20.json", StandardCharsets.UTF_8 ), false );

        waitForEmptyResourceQueue();
        userDhis2Server.verify();
        systemDhis2Server.verify();
    }

    @Test
    public void updatePersonWithFallbackOrg() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Patient/17" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-patient-17.json", StandardCharsets.UTF_8 ) ) ) );
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Organization/20" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-organization-20.json", StandardCharsets.UTF_8 ) ) ) );
        systemDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_1357" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1357.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?trackedEntityType=MCPQUTHX1Ze&ouMode=ACCESSIBLE&filter=jD1NGmSntCt:EQ:PT_81587&pageSize=2&fields=" +
            "deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-17-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2xl4mZfz.json?mergeMode=MERGE" ) )
            .andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) ).andExpect( method( HttpMethod.PUT ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-17-update.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-17-update-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );
        userDhis2Server.expect( ExpectedCount.never(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/JeR2xl4mZfz.json?fields=lastUpdated" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-17-update-last-updated.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        notifyResource( FhirResourceType.PATIENT,
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/search-patient-17.json", StandardCharsets.UTF_8 ),
            "17", IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-patient-17.json", StandardCharsets.UTF_8 ), true );

        waitForEmptyResourceQueue();
        userDhis2Server.verify();
        systemDhis2Server.verify();
    }

    @Test
    public void updatePersonWithDefaultOrg() throws Exception
    {
        expectTrackedEntityMetadataRequests();
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Patient/18" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-patient-18.json", StandardCharsets.UTF_8 ) ) ) );
        fhirMockServer.stubFor( WireMock.get( urlPathEqualTo( getBaseFhirContext() + "/Organization/21" ) ).willReturn( aResponse()
            .withHeader( "Content-Type", "application/fhir+json" )
            .withBody( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-organization-21.json", StandardCharsets.UTF_8 ) ) ) );
        systemDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_2468" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-empty.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        systemDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_4567" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_4567.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances.json?trackedEntityType=MCPQUTHX1Ze&ouMode=ACCESSIBLE&filter=jD1NGmSntCt:EQ:PT_81586&pageSize=2&fields=" +
            "deleted,trackedEntityInstance,trackedEntityType,orgUnit,coordinates,lastUpdated,attributes%5Battribute,value,lastUpdated,storedBy%5D" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-18-get.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/keR2xl4mZfz.json?mergeMode=MERGE" ) )
            .andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) ).andExpect( method( HttpMethod.PUT ) )
            .andExpect( content().contentTypeCompatibleWith( MediaType.APPLICATION_JSON ) )
            .andExpect( content().json( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-18-update.json", StandardCharsets.UTF_8 ) ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-18-update-response.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON )
                .headers( createDefaultHeaders() ) );
        userDhis2Server.expect( ExpectedCount.never(), requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityInstances/keR2xl4mZfz.json?fields=lastUpdated" ) )
            .andExpect( method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2UserAuthorization() ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tei-18-update-last-updated.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        notifyResource( FhirResourceType.PATIENT,
            IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/search-patient-18.json", StandardCharsets.UTF_8 ),
            "18", IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/test/" + getResourceDir() + "/get-patient-18.json", StandardCharsets.UTF_8 ), false );

        waitForEmptyResourceQueue();
        userDhis2Server.verify();
        systemDhis2Server.verify();
    }
}
