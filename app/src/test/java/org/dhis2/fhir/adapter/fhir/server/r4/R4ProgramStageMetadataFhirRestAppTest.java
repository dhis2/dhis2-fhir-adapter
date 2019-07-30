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
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Questionnaire;
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
 * DHIS2 program stage metadata.
 *
 * @author volsch
 */
public class R4ProgramStageMetadataFhirRestAppTest extends AbstractAppTest
{
    @Nonnull
    @Override
    protected FhirVersion getFhirVersion()
    {
        return FhirVersion.R4;
    }

    @Test( expected = AuthenticationException.class )
    public void getQuestionnaireWithoutAuthorization()
    {
        final IGenericClient client = createGenericClient();
        client.read().resource( Questionnaire.class ).withId( "MsWxkiY6tMS" ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void getQuestionnaireWithInvalidAuthorization()
    {
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/programStages/MsWxkiY6tMS.json?fields=id,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart," +
                "programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.read().resource( Questionnaire.class ).withId( "MsWxkiY6tMS" ).execute();
    }

    @Test
    public void getQuestionnaireRepeated() throws Exception
    {
        getQuestionnaire();
        getQuestionnaire();
    }

    private void getQuestionnaire() throws Exception
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/programStages/MsWxkiY6tMS.json?fields=id,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart," +
                "programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-program-stage.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Questionnaire planDefinition = client.read().resource( Questionnaire.class ).withId( "MsWxkiY6tMS" ).execute();
        Assert.assertEquals( "Birth", planDefinition.getTitle() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test( expected = ResourceNotFoundException.class )
    public void getQuestionnaireNotExistsRepeated()
    {
        try
        {
            getQuestionnaireNotExists();
            Assert.fail( "Exception expected also an first invocation." );
        }
        catch ( ResourceNotFoundException e )
        {
            getQuestionnaireNotExists();
        }
    }

    private void getQuestionnaireNotExists()
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/programStages/0dXIdLNUNEn.json?fields=id,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates,generatedByEnrollmentDate,minDaysFromStart," +
                "programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D" ) )
            .andRespond( withStatus( HttpStatus.NOT_FOUND ).contentType( MediaType.APPLICATION_JSON ).body( "{}" ) );

        try
        {
            final IGenericClient client = createGenericClient();
            client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
            client.read().resource( Questionnaire.class ).withId( "0dXIdLNUNEn" ).execute();
        }
        catch ( ResourceNotFoundException e )
        {
            systemDhis2Server.verify();
            userDhis2Server.verify();
            throw e;
        }
    }

    @Test( expected = ResourceNotFoundException.class )
    public void getQuestionnaireRuleNotFoundRepeated()
    {
        try
        {
            getQuestionnaireNotExists();
            Assert.fail( "Exception expected also an first invocation." );
        }
        catch ( ResourceNotFoundException e )
        {
            getQuestionnaireNotExists();
        }
    }

    private void getQuestionnaireRuleNotFound()
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        try
        {
            final IGenericClient client = createGenericClient();
            client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
            client.read().resource( Questionnaire.class ).withId( "ldXIdLNUNEn" ).execute();
        }
        catch ( ResourceNotFoundException e )
        {
            systemDhis2Server.verify();
            userDhis2Server.verify();
            throw e;
        }
    }

    @Test( expected = AuthenticationException.class )
    public void searchQuestionnaireWithoutAuthorization()
    {
        final IGenericClient client = createGenericClient();
        client.search().forResource( Questionnaire.class ).where( Questionnaire.TITLE.matches().value( "Test" ) ).returnBundle( Bundle.class ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void searchQuestionnaireInvalidAuthorization()
    {
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/programStages.json?filter=name:$ilike:Test&paging=true&page=1&pageSize=10&order=id&fields=id,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates," +
                "generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.search().forResource( Questionnaire.class ).where( Questionnaire.TITLE.matches().value( "Test" ) ).returnBundle( Bundle.class ).execute();
    }

    @Test
    public void searchQuestionnaire() throws Exception
    {
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/programStages.json?filter=name:$ilike:Birth&paging=true&page=1&pageSize=10&order=id&fields=id,program%5Bid%5D,lastUpdated,name,description,repeatable,captureCoordinates," +
                "generatedByEnrollmentDate,minDaysFromStart,programStageDataElements%5Bid,compulsory,allowProvidedElsewhere,dataElement%5Bid,name,code,formName,valueType,optionSetValue,optionSet%5Bid,name,options%5Bcode,name%5D%5D%5D%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-program-stage.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( Questionnaire.class ).where( Questionnaire.TITLE.matches().value( "Birth" ) ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 1, bundle.getEntry().size() );
        Questionnaire planDefinition = (Questionnaire) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( "Birth", planDefinition.getTitle() );
    }
}
