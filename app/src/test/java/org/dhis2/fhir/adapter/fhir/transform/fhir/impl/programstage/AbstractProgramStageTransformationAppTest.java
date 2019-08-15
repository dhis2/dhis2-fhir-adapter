package org.dhis2.fhir.adapter.fhir.transform.fhir.impl.programstage;

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
import org.dhis2.fhir.adapter.AbstractAppTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Abstract base class for tests that perform a transformation to a program stage
 * instance.
 *
 * @author volsch
 */
public abstract class AbstractProgramStageTransformationAppTest extends AbstractAppTest
{
    protected void expectMetadataRequests() throws Exception
    {
        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/programs.json?paging=false&fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration," +
                "withoutRegistration,captureCoordinates,trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid," +
                "program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType," +
                "optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D%5D&filter=name:eq:Child%20Programme" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/all-programs.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityTypes/MCPQUTHX1Ze.json?fields=id,name,trackedEntityTypeAttributes%5Bid,name,valueType,mandatory,trackedEntityAttribute%5Bid,name,code,valueType,generated," +
                "optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-tracked-entity-type.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityAttributes.json?paging=false&fields=id,name,code,valueType,generated,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tracked-entity-attributes.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityTypes.json?paging=false&filter=name:eq:Person&fields=id,name," +
                "trackedEntityTypeAttributes%5Bid,name,valueType,mandatory,trackedEntityAttribute%5Bid,name,code,valueType,generated,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-tracked-entity-type.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/programs.json?paging=false&" +
                "fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration,withoutRegistration,captureCoordinates,trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid," +
                "name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates,generatedByEnrollmentDate," +
                "minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,description,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode," +
                "name%5D%5D%5D%5D%5D&filter=name:eq:Child%20Programme" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-program.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
    }
}
