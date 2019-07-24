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

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.apache.commons.io.IOUtils;
import org.dhis2.fhir.adapter.AbstractAppTest;
import org.dhis2.fhir.adapter.fhir.model.FhirVersion;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
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
 * DHIS2 organization units.
 *
 * @author volsch
 */
public class Dstu3OrgUnitFhirRestAppTest extends AbstractAppTest
{
    @Nonnull
    @Override
    protected FhirVersion getFhirVersion()
    {
        return FhirVersion.DSTU3;
    }

    @Test( expected = AuthenticationException.class )
    public void getLocationWithoutAuthorization()
    {
        final IGenericClient client = createGenericClient();
        client.read().resource( Location.class ).withId( "ldXIdLNUNEn" ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void getLocationWithInvalidAuthorization()
    {
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/ldXIdLNUNEn.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.read().resource( Location.class ).withId( "ldXIdLNUNEn" ).execute();
    }

    @Test( expected = ResourceNotFoundException.class )
    public void getLocationNotExistsRepeated()
    {
        try
        {
            getLocationNotExists();
            Assert.fail( "Exception expected also an first invocation." );
        }
        catch ( ResourceNotFoundException e )
        {
            getLocationNotExists();
        }
    }

    private void getLocationNotExists()
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/0dXIdLNUNEn.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withStatus( HttpStatus.NOT_FOUND ).contentType( MediaType.APPLICATION_JSON ).body( "{}" ) );

        try
        {
            final IGenericClient client = createGenericClient();
            client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
            client.read().resource( Location.class ).withId( "0dXIdLNUNEn" ).execute();
        }
        catch ( ResourceNotFoundException e )
        {
            systemDhis2Server.verify();
            userDhis2Server.verify();
            throw e;
        }
    }

    @Test( expected = ResourceNotFoundException.class )
    public void getLocationRuleNotFoundRepeated()
    {
        try
        {
            getLocationNotExists();
            Assert.fail( "Exception expected also an first invocation." );
        }
        catch ( ResourceNotFoundException e )
        {
            getLocationNotExists();
        }
    }

    private void getLocationRuleNotFound()
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        try
        {
            final IGenericClient client = createGenericClient();
            client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
            client.read().resource( Location.class ).withId( "ldXIdLNUNEn" ).execute();
        }
        catch ( ResourceNotFoundException e )
        {
            systemDhis2Server.verify();
            userDhis2Server.verify();
            throw e;
        }
    }

    @Test
    public void getLocationRepeated() throws Exception
    {
        getLocation();
        getLocation();
    }

    private void getLocation() throws Exception
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits/ldXIdLNUNEn.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/single-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_1234" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Location location = client.read().resource( Location.class ).withId( "ldXIdLNUNEn" ).execute();
        Assert.assertEquals( "Test Hospital", location.getName() );
        Assert.assertEquals( 1, location.getIdentifier().size() );
        Assert.assertEquals( "http://www.dhis2.org/dhis2fhiradapter/systems/location-identifier", location.getIdentifier().get( 0 ).getSystem() );
        Assert.assertEquals( "OU_1234", location.getIdentifier().get( 0 ).getValue() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test( expected = AuthenticationException.class )
    public void getLocationByIdentifierWithoutAuthorization()
    {
        final IGenericClient client = createGenericClient();
        client.search().forResource( Location.class ).where( Location.IDENTIFIER.exactly().systemAndIdentifier( "http://www.dhis2.org/dhis2fhiradapter/systems/location-identifier", "OU_1234" ) ).returnBundle( Bundle.class ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void getLocationByIdentifierInvalidAuthorization()
    {
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_1234" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.search().forResource( Location.class ).where( Location.IDENTIFIER.exactly().systemAndIdentifier( "http://www.dhis2.org/dhis2fhiradapter/systems/location-identifier", "OU_1234" ) ).returnBundle( Bundle.class ).execute();
    }

    @Test
    public void getLocationByIdentifierRepeated() throws Exception
    {
        getLocationByIdentifier();
        getLocationByIdentifier();
    }

    private void getLocationByIdentifier() throws Exception
    {
        systemDhis2Server.reset();
        userDhis2Server.reset();

        userDhis2Server.expect( ExpectedCount.between( 1, 2 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_1234" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( Location.class ).where( Location.IDENTIFIER.exactly().systemAndIdentifier( "http://www.dhis2.org/dhis2fhiradapter/systems/location-identifier", "OU_1234" ) ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 1, bundle.getEntry().size() );
        Location location = (Location) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( "Test Hospital", location.getName() );
        Assert.assertEquals( 1, location.getIdentifier().size() );
        Assert.assertEquals( "http://www.dhis2.org/dhis2fhiradapter/systems/location-identifier", location.getIdentifier().get( 0 ).getSystem() );
        Assert.assertEquals( "OU_1234", location.getIdentifier().get( 0 ).getValue() );

        systemDhis2Server.verify();
        userDhis2Server.verify();
    }

    @Test( expected = AuthenticationException.class )
    public void searchLocationWithoutAuthorization()
    {
        final IGenericClient client = createGenericClient();
        client.search().forResource( Location.class ).where( Location.NAME.matches().value( "Test" ) ).returnBundle( Bundle.class ).execute();
    }

    @Test( expected = AuthenticationException.class )
    public void searchLocationInvalidAuthorization()
    {
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6aW52YWxpZF8x" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?filter=name:$ilike:Test&paging=true&page=1&pageSize=10&order=id" +
                "&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withStatus( HttpStatus.UNAUTHORIZED ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "invalid_1" ) );
        client.search().forResource( Location.class ).where( Location.NAME.matches().value( "Test" ) ).returnBundle( Bundle.class ).execute();
    }

    @Test
    public void searchLocation() throws Exception
    {
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?filter=name:$ilike:Test&paging=true&page=1&pageSize=10&order=id" +
                "&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/multi-org-unit.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_1234" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_4567" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_4567.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( Location.class ).where( Location.NAME.matches().value( "Test" ) ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 2, bundle.getEntry().size() );
        Location location = (Location) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( "Test Hospital", location.getName() );
        Assert.assertEquals( 1, location.getIdentifier().size() );
        Assert.assertEquals( "http://www.dhis2.org/dhis2fhiradapter/systems/location-identifier", location.getIdentifier().get( 0 ).getSystem() );
        Assert.assertEquals( "OU_1234", location.getIdentifier().get( 0 ).getValue() );
        location = (Location) bundle.getEntry().get( 1 ).getResource();
        Assert.assertEquals( "Test Hospital 2", location.getName() );
        Assert.assertEquals( 1, location.getIdentifier().size() );
        Assert.assertEquals( "http://www.dhis2.org/dhis2fhiradapter/systems/location-identifier", location.getIdentifier().get( 0 ).getSystem() );
        Assert.assertEquals( "OU_4567", location.getIdentifier().get( 0 ).getValue() );
    }

    @Test
    public void searchParentLocation() throws Exception
    {
        userDhis2Server.expect( ExpectedCount.once(), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?filter=parent.id:eq:bdXIaLNUNEp&paging=true&page=1&pageSize=10&order=id" +
                "&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/multi-org-unit.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_1234" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_1234.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );
        userDhis2Server.expect( ExpectedCount.between( 0, 1 ), method( HttpMethod.GET ) ).andExpect( header( "Authorization", "Basic Zmhpcl9jbGllbnQ6Zmhpcl9jbGllbnRfMQ==" ) )
            .andExpect( requestTo( dhis2BaseUrl + "/api/" + dhis2ApiVersion + "/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,coordinates,leaf,parent%5Bid%5D&filter=code:eq:OU_4567" ) )
            .andRespond( withSuccess( IOUtils.resourceToString( "/org/dhis2/fhir/adapter/dhis/test/default-org-unit-OU_4567.json", StandardCharsets.UTF_8 ), MediaType.APPLICATION_JSON ) );

        final IGenericClient client = createGenericClient();
        client.registerInterceptor( new BasicAuthInterceptor( "fhir_client", "fhir_client_1" ) );
        Bundle bundle = client.search().forResource( Location.class ).where( Location.PARTOF.hasId( new IdType( "Location", "bdXIaLNUNEp" ) ) ).returnBundle( Bundle.class ).execute();
        Assert.assertEquals( 2, bundle.getEntry().size() );
        Location location = (Location) bundle.getEntry().get( 0 ).getResource();
        Assert.assertEquals( "Test Hospital", location.getName() );
        Assert.assertEquals( 1, location.getIdentifier().size() );
        Assert.assertEquals( "http://www.dhis2.org/dhis2fhiradapter/systems/location-identifier", location.getIdentifier().get( 0 ).getSystem() );
        Assert.assertEquals( "OU_1234", location.getIdentifier().get( 0 ).getValue() );
        location = (Location) bundle.getEntry().get( 1 ).getResource();
        Assert.assertEquals( "Test Hospital 2", location.getName() );
        Assert.assertEquals( 1, location.getIdentifier().size() );
        Assert.assertEquals( "http://www.dhis2.org/dhis2fhiradapter/systems/location-identifier", location.getIdentifier().get( 0 ).getSystem() );
        Assert.assertEquals( "OU_4567", location.getIdentifier().get( 0 ).getValue() );
    }
}
