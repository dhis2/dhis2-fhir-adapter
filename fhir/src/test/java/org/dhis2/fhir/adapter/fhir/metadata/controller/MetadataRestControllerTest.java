package org.dhis2.fhir.adapter.fhir.metadata.controller;

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataExportParams;
import org.dhis2.fhir.adapter.fhir.metadata.service.MetadataExportService;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.UUID;

/**
 * Unit tests for {@link MetadataRestController}.
 *
 * @author volsch
 */
public class MetadataRestControllerTest
{
    @Mock
    private MetadataExportService metadataExportService;

    @InjectMocks
    private MetadataRestController metadataRestController;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void getWithoutParameters()
    {
        Mockito.when( metadataExportService.export( Mockito.notNull() ) ).thenAnswer( (Answer<JsonNode>) invocation -> {
            final MetadataExportParams params = invocation.getArgument( 0 );
            Assert.assertNotNull( params );
            Assert.assertTrue( params.getTrackerProgramIds().isEmpty() );
            Assert.assertTrue( params.getExcludedSystemUris().isEmpty() );
            return JsonNodeFactory.instance.textNode( "Test1" );
        } );

        final ResponseEntity<JsonNode> responseEntity = metadataRestController.exp(
            null, null, false );

        Assert.assertEquals( HttpStatus.OK, responseEntity.getStatusCode() );
        Assert.assertTrue( responseEntity.getHeaders().isEmpty() );
        Assert.assertEquals( JsonNodeFactory.instance.textNode( "Test1" ), responseEntity.getBody() );

        Mockito.verify( metadataExportService ).export( Mockito.notNull() );
    }

    @Test
    public void getWithArrayParameters()
    {
        Mockito.when( metadataExportService.export( Mockito.notNull() ) ).thenAnswer( (Answer<JsonNode>) invocation -> {
            final MetadataExportParams params = invocation.getArgument( 0 );
            Assert.assertNotNull( params );
            Assert.assertThat( params.getTrackerProgramIds(),
                Matchers.containsInAnyOrder( UUID.fromString( "ef1561ae-c564-47b2-88f2-069f9637ae98" ), UUID.fromString( "515a0e80-3d02-4c41-83d9-6de1a6037b5e" ), UUID.fromString( "8912318e-9805-4be4-87b0-5306a98be334" ) ) );
            Assert.assertThat( params.getExcludedSystemUris(),
                Matchers.containsInAnyOrder( "http://loing.org", "http://snomedct.org" ) );
            return JsonNodeFactory.instance.textNode( "Test1" );
        } );

        final ResponseEntity<JsonNode> responseEntity = metadataRestController.exp(
            Arrays.asList( UUID.fromString( "ef1561ae-c564-47b2-88f2-069f9637ae98" ), UUID.fromString( "515a0e80-3d02-4c41-83d9-6de1a6037b5e" ), UUID.fromString( "8912318e-9805-4be4-87b0-5306a98be334" ) ),
            Arrays.asList( "http://loing.org", "http://snomedct.org" ), false );

        Assert.assertEquals( HttpStatus.OK, responseEntity.getStatusCode() );
        Assert.assertTrue( responseEntity.getHeaders().isEmpty() );
        Assert.assertEquals( JsonNodeFactory.instance.textNode( "Test1" ), responseEntity.getBody() );

        Mockito.verify( metadataExportService ).export( Mockito.notNull() );
    }

    @Test
    public void getParametersDownload()
    {
        Mockito.when( metadataExportService.export( Mockito.notNull() ) ).thenAnswer( (Answer<JsonNode>) invocation -> {
            final MetadataExportParams params = invocation.getArgument( 0 );
            Assert.assertNotNull( params );
            Assert.assertTrue( params.getTrackerProgramIds().isEmpty() );
            Assert.assertTrue( params.getExcludedSystemUris().isEmpty() );
            return JsonNodeFactory.instance.textNode( "Test1" );
        } );

        final ResponseEntity<JsonNode> responseEntity = metadataRestController.exp(
            null, null, true );

        Assert.assertEquals( HttpStatus.OK, responseEntity.getStatusCode() );
        Assert.assertEquals( 1, responseEntity.getHeaders().size() );
        Assert.assertEquals( "attachment; filename=\"metadata.json\"",
            responseEntity.getHeaders().getFirst( HttpHeaders.CONTENT_DISPOSITION ) );
        Assert.assertEquals( JsonNodeFactory.instance.textNode( "Test1" ), responseEntity.getBody() );

        Mockito.verify( metadataExportService ).export( Mockito.notNull() );
    }
}