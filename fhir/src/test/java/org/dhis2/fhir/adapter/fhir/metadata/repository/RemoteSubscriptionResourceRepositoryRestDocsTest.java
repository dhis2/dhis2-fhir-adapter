package org.dhis2.fhir.adapter.fhir.metadata.repository;

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
import org.dhis2.fhir.adapter.fhir.AbstractJpaRepositoryRestDocsTest;
import org.dhis2.fhir.adapter.fhir.ConstrainedFields;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscription;
import org.dhis2.fhir.adapter.fhir.metadata.model.RemoteSubscriptionResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for {@link RemoteSubscriptionResourceRepository}.
 *
 * @author volsch
 */
public class RemoteSubscriptionResourceRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private SystemRepository systemRepository;

    @Autowired
    private RemoteSubscriptionRepository remoteSubscriptionRepository;

    @Autowired
    private RemoteSubscriptionResourceRepository remoteSubscriptionResourceRepository;

    @Test
    public void createRemoteSubscriptionResource() throws Exception
    {
        final String remoteSubscriptionId = loadRemoteSubscription( "DEFAULT_SUBSCRIPTION" ).getId().toString();
        final ConstrainedFields fields = new ConstrainedFields( RemoteSubscriptionResource.class, constraintDescriptionResolver );
        final String request = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createRemoteSubscriptionResource.json", StandardCharsets.UTF_8 )
            .replace( "$remoteSubscriptionId", API_BASE_URI + "/remoteSubscriptions/" + remoteSubscriptionId );
        final String location = docMockMvc.perform( post( "/api/remoteSubscriptionResources" ).header( AUTHORIZATION_HEADER_NAME, ADMINISTRATION_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( request ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for script source creation" ) ),
                fields.withPath( "remoteSubscription" ).description( "The reference to the remote subscription to which this resource belongs to." ).type( JsonFieldType.STRING ),
                fields.withPath( "fhirResourceType" ).description( "The type of the subscribed FHIR resource." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description of the purpose of the subscribed FHIR resource." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "fhirCriteriaParameters" ).description( "The prefix that should be added to the codes when mapping them to DHIS2." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "expOnly" ).description( "Specifies that this is only used for exporting FHIR resources. Subscription requests are not accepted." )
                    .type( JsonFieldType.BOOLEAN ).optional()
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, ADMINISTRATION_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "fhirResourceType", is( "IMMUNIZATION" ) ) )
            .andExpect( jsonPath( "description", is( "Subscription for all immunizations." ) ) )
            .andExpect( jsonPath( "fhirCriteriaParameters" ).doesNotExist() )
            .andExpect( jsonPath( "fhirSubscriptionId" ).doesNotExist() )
            .andExpect( jsonPath( "expOnly", is( false ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readRemoteSubscriptionResource() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( RemoteSubscriptionResource.class, constraintDescriptionResolver );
        final String remoteSubscriptionResourceId = loadRemoteSubscriptionResource( "667bfa41-867c-4796-86b6-eb9f9ed4dc94" ).getId().toString();
        docMockMvc.perform( get( "/api/remoteSubscriptionResources/{remoteSubscriptionResourceId}", remoteSubscriptionResourceId ).header( AUTHORIZATION_HEADER_NAME, ADMINISTRATION_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "remoteSubscriptionResource" ).description( "Link to this resource itself." ),
                linkWithRel( "remoteSubscription" ).description( "The reference to the remote subscription to which this resource belongs to." ) ), responseFields(
                attributes( key( "title" ).value( "Fields for remote subscription resource reading" ) ),
                fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                fields.withPath( "fhirResourceType" ).description( "The type of the subscribed FHIR resource." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description of the purpose of the subscribed FHIR resource." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "fhirCriteriaParameters" ).description( "The prefix that should be added to the codes when mapping them to DHIS2." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "expOnly" ).description( "Specifies that this is only used for exporting FHIR resources. Subscription requests are not accepted." )
                    .type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "virtual" ).description( "Specifies that there is no subscription for this FHIR resource since the FHIR service may not accept subscription for this resource type (just available as contained resources)." )
                    .type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "hirSubscriptionId" ).description( "The ID of the automatically created FHIR subscription on the FHIR service." ).type( JsonFieldType.STRING ).optional(),
                subsectionWithPath( "_links" ).description( "Links to other resources" )
            ) ) );
    }

    @Nonnull
    protected RemoteSubscription loadRemoteSubscription( @Nonnull String code )
    {
        final RemoteSubscription remoteSubscription = new RemoteSubscription();
        remoteSubscription.setCode( code );
        return remoteSubscriptionRepository.findOne( Example.of( remoteSubscription,
            ExampleMatcher.matching().withIgnorePaths( "toleranceMillis", "logging", "verboseLogging", "enabled", "locked" ) ) ).orElseThrow( () -> new AssertionError( "Remote subscription does not exist: " + code ) );
    }

    @Nonnull
    protected RemoteSubscriptionResource loadRemoteSubscriptionResource( @Nonnull String id )
    {
        return remoteSubscriptionResourceRepository.findById( UUID.fromString( id ) ).orElseThrow( () -> new AssertionError( "Remote subscription resource does not exist: " + id ) );
    }
}