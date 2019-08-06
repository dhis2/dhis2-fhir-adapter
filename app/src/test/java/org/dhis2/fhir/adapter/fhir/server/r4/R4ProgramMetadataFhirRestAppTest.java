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

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.apache.commons.io.IOUtils;
import org.dhis2.fhir.adapter.AbstractAppTest;
import org.dhis2.fhir.adapter.fhir.extension.ResourceTypeExtensionUtils;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.PlanDefinition;
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
 * R4 tests for rest interfaces that access
 * DHIS2 program metadata.
 *
 * @author volsch
 */
public class R4ProgramMetadataFhirRestAppTest extends AbstractAppTest
{
    @Nonnull
    @Override
    protected FhirVersion getFhirVersion()
    {
        return FhirVersion.R4;
    }

    @Test( expected = AuthenticationException.class )
    public void getPlanDefinitionWithoutAuthorization()
    {
        final IGenericClient client = createGenericClient();
        client.read().resource( PlanDefinition.class ).withId( "ldXIdLNUNEn" ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void getPlanDefinitionWithInvalidAuthorization()
    {
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/programs/EPDyQuoRnXk.json?" +
                "fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration,withoutRegistration,captureCoordinates,trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid," +
                "name,valueType," +
                "mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart," +
                "programStageDataElements%5Bid,compulsory," +
                "allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D%5D" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.read().resource( PlanDefinition.class ).withId( "EPDyQuoRnXk" ).execute();
    }

    @Test
    public void getPlanDefinitionRepeated() throws Exception
    {
        getPlanDefinition();
        getPlanDefinition();
    }

    private void getPlanDefinition() throws Exception
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityTypes/MCPQUTHX1Ze.json?" +
                "fields=id,name,trackedEntityTypeAttributes%5Bid,name,valueType,mandatory,trackedEntityAttribute%5Bid,name,code,valueType,generated,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-tracked-entity-type.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/programs/EPDyQuoRnXk.json?fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration," +
                "withoutRegistration,captureCoordinates,trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid," +
                "program%5Bid%5D,lastUpdated,name," +
                "description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name," +
                "options%5Bcode,name%5D%5D%5D%5D%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-program.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/programs.json?paging=false&fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration," +
                "withoutRegistration,captureCoordinates,trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid," +
                "program%5Bid%5D,lastUpdated,name," +
                "description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name," +
                "options%5Bcode,name%5D%5D%5D%5D%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/all-programs.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        PlanDefinition planDefinition = client.read().resource( PlanDefinition.class ).withId( "EPDyQuoRnXk" ).execute();
        Assert.assertEquals( "Child Programme", planDefinition.getTitle() );
        Assert.assertNotNull( planDefinition.getExtensionByUrl( ResourceTypeExtensionUtils.URL ) );
        Assert.assertEquals( "Patient", planDefinition.getExtensionByUrl( ResourceTypeExtensionUtils.URL ).getValue().primitiveValue() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test( expected = ResourceNotFoundException.class )
    public void getPlanDefinitionNotExistsRepeated()
    {
        try
        {
            getPlanDefinitionNotExists();
            Assert.fail( "Exception expected also an first invocation." );
        }
        catch ( ResourceNotFoundException e )
        {
            getPlanDefinitionNotExists();
        }
    }

    private void getPlanDefinitionNotExists()
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/programs/0dXIdLNUNEn.json?fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration," +
                "withoutRegistration,captureCoordinates,trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid," +
                "program%5Bid%5D,lastUpdated,name," +
                "description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name," +
                "options%5Bcode,name%5D%5D%5D%5D%5D" ) )
            .andRespond( withStatus( HttpStatus.NOT_FOUND ).contentType( MediaType.APPLICATION_JSON ).body( "{}" ) );

        try
        {
            final IGenericClient client = createGenericClient();
            client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
            client.read().resource( PlanDefinition.class ).withId( "0dXIdLNUNEn" ).execute();
        }
        catch ( ResourceNotFoundException e )
        {
            systemDhis2Server.verify();
            userDhis2Server.verify();
            throw e;
        }
    }

    @Test( expected = ResourceNotFoundException.class )
    public void getPlanDefinitionRuleNotFoundRepeated()
    {
        try
        {
            getPlanDefinitionNotExists();
            Assert.fail( "Exception expected also an first invocation." );
        }
        catch ( ResourceNotFoundException e )
        {
            getPlanDefinitionNotExists();
        }
    }

    private void getPlanDefinitionRuleNotFound()
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        try
        {
            final IGenericClient client = createGenericClient();
            client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
            client.read().resource( PlanDefinition.class ).withId( "ldXIdLNUNEn" ).execute();
        }
        catch ( ResourceNotFoundException e )
        {
            systemDhis2Server.verify();
            userDhis2Server.verify();
            throw e;
        }
    }

    @Test( expected = AuthenticationException.class )
    public void getPlanDefinitionByIdentifierWithoutAuthorization()
    {
        final IGenericClient client = createGenericClient();
        client.search().forResource( PlanDefinition.class ).where( PlanDefinition.IDENTIFIER.exactly().systemAndIdentifier( "http://www.dhis2.org/dhis2-fhir-adapter/systems/plan-definition-identifier", "XX_1234" ) ).returnBundle( Bundle.class ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void getPlanDefinitionByIdentifierInvalidAuthorization()
    {
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion +
                "/programs.json?paging=false&fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration,withoutRegistration," +
                "captureCoordinates,trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid,program%5Bid%5D,lastUpdated,name," +
                "description,repeatable," +
                "captureCoordinates,generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode," +
                "name%5D%5D%5D%5D%5D&filter=code:eq:OU_1234" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.search().forResource( PlanDefinition.class ).where( PlanDefinition.IDENTIFIER.exactly().systemAndIdentifier( "http://www.dhis2.org/dhis2-fhir-adapter/systems/plan-definition-identifier", "OU_1234" ) ).returnBundle( Bundle.class ).execute();
    }

    @Test
    public void getPlanDefinitionByIdentifierRepeated() throws Exception
    {
        getPlanDefinitionByIdentifier();
        getPlanDefinitionByIdentifier();
    }

    private void getPlanDefinitionByIdentifier() throws Exception
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityTypes/MCPQUTHX1Ze.json?" +
                "fields=id,name,trackedEntityTypeAttributes%5Bid,name,valueType,mandatory,trackedEntityAttribute%5Bid,name,code,valueType,generated,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-tracked-entity-type.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 1, 2 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/programs.json?paging=false&fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture,selectEnrollmentDatesInFuture,displayIncidentDate,registration," +
                "withoutRegistration," +
                "captureCoordinates,trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D,programStages%5Bid,program%5Bid%5D,lastUpdated,name," +
                "description,repeatable," +
                "captureCoordinates,generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode," +
                "name%5D%5D%5D%5D%5D&filter=code:eq:PD_1234" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-program.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle =
            client.search().forResource( PlanDefinition.class ).where( PlanDefinition.IDENTIFIER.exactly().systemAndIdentifier( "http://www.dhis2.org/dhis2-fhir-adapter/systems/plan-definition-identifier", "PD_1234" ) ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 1, bundle.getEntry().size() );
        PlanDefinition planDefinition = (PlanDefinition) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( "Child Programme", planDefinition.getTitle() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test( expected = AuthenticationException.class )
    public void searchPlanDefinitionWithoutAuthorization()
    {
        final IGenericClient client = createGenericClient();
        client.search().forResource( PlanDefinition.class ).where( PlanDefinition.NAME.matches().value( "Test" ) ).returnBundle( Bundle.class ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void searchPlanDefinitionInvalidAuthorization()
    {
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/programs.json?filter=name:$ilike:Test&paging=true&page=1&pageSize=10&order=id&fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture," +
                "selectEnrollmentDatesInFuture," +
                "displayIncidentDate,registration,withoutRegistration,captureCoordinates,trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name,code,valueType,generated%5D%5D," +
                "programStages%5Bid,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName," +
                "valueType,optionSetValue," +
                "optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D%5D" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.search().forResource( PlanDefinition.class ).where( PlanDefinition.TITLE.matches().value( "Test" ) ).returnBundle( Bundle.class ).execute();
    }

    @Test
    public void searchPlanDefinition() throws Exception
    {
        systemDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", testConfiguration.getDhis2SystemAuthorization() ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/trackedEntityTypes/MCPQUTHX1Ze.json?" +
                "fields=id,name,trackedEntityTypeAttributes%5Bid,name,valueType,mandatory,trackedEntityAttribute%5Bid,name,code,valueType,generated,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-tracked-entity-type.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/programs.json?filter=name:$ilike:Child%20Programme&paging=true&page=1&pageSize=10&order=id&fields=id,name,code,description,lastUpdated,selectIncidentDatesInFuture," +
                "selectEnrollmentDatesInFuture,displayIncidentDate,registration,withoutRegistration,captureCoordinates,trackedEntityType%5Bid%5D,programTrackedEntityAttributes%5Bid,name,valueType,mandatory,allowFutureDate,trackedEntityAttribute%5Bid,name," +
                "code,valueType,generated%5D%5D,programStages%5Bid,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere," +
                "dataElement%5Bid,name,code,formName," +
                "valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-program.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( PlanDefinition.class ).where( PlanDefinition.TITLE.matches().value( "Child Programme" ) ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 1, bundle.getEntry().size() );
        PlanDefinition planDefinition = (PlanDefinition) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( "Child Programme", planDefinition.getTitle() );
    }
}
