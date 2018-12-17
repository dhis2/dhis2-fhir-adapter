package org.dhis2.fhir.adapter.dhis.orgunit.impl;

/*
 * Copyright (c) 2004-2018, University of Oslo
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
import org.dhis2.fhir.adapter.dhis.model.Reference;
import org.dhis2.fhir.adapter.dhis.model.ReferenceType;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnit;
import org.dhis2.fhir.adapter.dhis.orgunit.OrganizationUnitService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Unit test for {@link OrganizationUnitService}.
 */
public class OrganizationUnitServiceImplTest
{
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private OrganizationUnitService service;

    @Before
    public void setUp()
    {
        restTemplate = new RestTemplateBuilder().rootUri( "http://localhost:8080/api" ).build();
        mockServer = MockRestServiceServer.createServer( restTemplate );
        service = new OrganizationUnitServiceImpl( restTemplate );
    }

    @Test
    public void findOneByReferenceId() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/organisationUnits/93783.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,leaf,parent%5Bid%5D" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/orgunit/impl/organisationUnit.json" ), MediaType.APPLICATION_JSON ) );

        Optional<OrganizationUnit> ou = service.findOneByReference( new Reference( "93783", ReferenceType.ID ) );
        Assert.assertTrue( ou.isPresent() );
        Assert.assertEquals( "93783", ou.get().getId() );
        Assert.assertEquals( "XU_478347", ou.get().getCode() );
    }

    @Test
    public void findOneByReferenceIdNotFound()
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/organisationUnits/93783.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,leaf,parent%5Bid%5D" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( MockRestResponseCreators.withStatus( HttpStatus.NOT_FOUND ).body( "{}" ).contentType( MediaType.APPLICATION_JSON ) );

        Optional<OrganizationUnit> ou = service.findOneByReference( new Reference( "93783", ReferenceType.ID ) );
        Assert.assertFalse( ou.isPresent() );
    }

    @Test( expected = HttpServerErrorException.class )
    public void findOneByReferenceIdServerError()
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/organisationUnits/93783.json?fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,leaf,parent%5Bid%5D" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( MockRestResponseCreators.withServerError() );
        service.findOneByReference( new Reference( "93783", ReferenceType.ID ) );
    }

    @Test
    public void findOneByReferenceCode() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,leaf,parent%5Bid%5D&filter=code:eq:OU_3783" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/orgunit/impl/organisationUnits.json" ), MediaType.APPLICATION_JSON ) );

        Optional<OrganizationUnit> ou = service.findOneByReference( new Reference( "OU_3783", ReferenceType.CODE ) );
        Assert.assertTrue( ou.isPresent() );
        Assert.assertEquals( "93783", ou.get().getId() );
        Assert.assertEquals( "OU_3783", ou.get().getCode() );
    }

    @Test
    public void findOneByReferenceCodeNotFound() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,leaf,parent%5Bid%5D&filter=code:eq:OU_3783" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/orgunit/impl/emptyOrganisationUnits.json" ), MediaType.APPLICATION_JSON ) );

        Optional<OrganizationUnit> ou = service.findOneByReference( new Reference( "OU_3783", ReferenceType.CODE ) );
        Assert.assertFalse( ou.isPresent() );
    }

    @Test
    public void findOneByReferenceName() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,leaf,parent%5Bid%5D&filter=name:eq:Freetown" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/orgunit/impl/organisationUnits.json" ), MediaType.APPLICATION_JSON ) );

        Optional<OrganizationUnit> ou = service.findOneByReference( new Reference( "Freetown", ReferenceType.NAME ) );
        Assert.assertTrue( ou.isPresent() );
        Assert.assertEquals( "93783", ou.get().getId() );
        Assert.assertEquals( "OU_3783", ou.get().getCode() );
    }

    @Test
    public void findOneByReferenceNameNotFound() throws IOException
    {
        mockServer.expect( requestTo( "http://localhost:8080/api/organisationUnits.json?paging=false&fields=lastUpdated,id,code,name,shortName,displayName,level,openingDate,closedDate,leaf,parent%5Bid%5D&filter=name:eq:Freetown" ) ).andExpect( method( HttpMethod.GET ) )
            .andRespond( withSuccess( IOUtils.resourceToByteArray( "/org/dhis2/fhir/adapter/dhis/orgunit/impl/emptyOrganisationUnits.json" ), MediaType.APPLICATION_JSON ) );

        Optional<OrganizationUnit> ou = service.findOneByReference( new Reference( "Freetown", ReferenceType.NAME ) );
        Assert.assertFalse( ou.isPresent() );
    }
}