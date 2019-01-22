package org.dhis2.fhir.adapter.fhir.metadata.repository;

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
import org.dhis2.fhir.adapter.fhir.AbstractJpaRepositoryRestDocsTest;
import org.dhis2.fhir.adapter.fhir.ConstrainedFields;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackedEntity;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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
 * Tests for {@link MappedTrackedEntityRepository}.
 *
 * @author volsch
 */
public class TrackedEntityRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private MappedTrackedEntityRepository mappedTrackedEntityRepository;

    @Test
    public void createTrackedEntity() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( MappedTrackedEntity.class, constraintDescriptionResolver );
        final String request = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createTrackedEntity.json", StandardCharsets.UTF_8 );
        final String location = docMockMvc.perform( post( "/api/trackedEntities" ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( request ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for tracked entity creation" ) ),
                fields.withPath( "name" ).description( "The unique name of the rule." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description that describes for which purpose the rule is used." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "enabled" ).description( "Specifies if this rule is enabled." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "outEnabled" ).description( "Specifies if output transformation from DHIS to FHIR for this tracked entity type is enabled." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirCreateEnabled" ).description( "Specifies if the creation of a FHIR resource is enabled for output transformations from DHIS to FHIR for this tracked entity type (by default true)." )
                    .type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirUpdateEnabled" ).description( "Specifies if the update of a FHIR resource is enabled for output transformations from DHIS to FHIR for this tracked entity type. (by default false)." )
                    .type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "trackedEntityReference" ).description( "The reference to the DHIS2 Tracked Entity Type." ).type( JsonFieldType.OBJECT ),
                fields.withPath( "trackedEntityReference.value" ).description( "The unique ID/code/name of the Tracked Entity Type." ).type( JsonFieldType.STRING ),
                fields.withPath( "trackedEntityReference.type" ).description( "The type of reference value of the Tracked Entity Type." ).type( JsonFieldType.STRING ),
                fields.withPath( "trackedEntityIdentifierReference" ).description( "The reference to the DHIS2 Tracked Entity Attribute that is used as national identifier." ).type( JsonFieldType.OBJECT ),
                fields.withPath( "trackedEntityIdentifierReference.value" ).description( "The unique ID/code/name of the Tracked Entity Attribute." ).type( JsonFieldType.STRING ),
                fields.withPath( "trackedEntityIdentifierReference.type" ).description( "The type of reference value of the Tracked Entity Attribute." ).type( JsonFieldType.STRING )
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "name", is( "Person Tracked Entity (disabled)" ) ) )
            .andExpect( jsonPath( "enabled", is( false ) ) )
            .andExpect( jsonPath( "expEnabled", is( false ) ) )
            .andExpect( jsonPath( "fhirCreateEnabled", is( true ) ) )
            .andExpect( jsonPath( "fhirUpdateEnabled", is( false ) ) )
            .andExpect( jsonPath( "trackedEntityReference.value", is( "Obsolete Person" ) ) )
            .andExpect( jsonPath( "trackedEntityReference.type", is( "NAME" ) ) )
            .andExpect( jsonPath( "trackedEntityIdentifierReference.value", is( "National identifier" ) ) )
            .andExpect( jsonPath( "trackedEntityIdentifierReference.type", is( "CODE" ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readTrackedEntity() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( MappedTrackedEntity.class, constraintDescriptionResolver );
        final String trackedEntityId = loadTrackedEntity( "Person" ).getId().toString();
        docMockMvc.perform( get( "/api/trackedEntities/{trackedEntityId}", trackedEntityId ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "trackedEntity" ).description( "Link to this resource itself." ) ),
                responseFields(
                    attributes( key( "title" ).value( "Fields for tracked entity reading" ) ),
                    fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                    fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                    fields.withPath( "name" ).description( "The unique name of the rule." ).type( JsonFieldType.STRING ),
                    fields.withPath( "description" ).description( "The detailed description that describes for which purpose the rule is used." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "enabled" ).description( "Specifies if this rule is enabled." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "expEnabled" ).description( "Specifies if output transformation from DHIS to FHIR for this tracked entity type is enabled." ).type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "fhirCreateEnabled" ).description( "Specifies if the creation of a FHIR resource is enabled for output transformations from DHIS to FHIR for this tracked entity type (by default true)." )
                        .type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "fhirUpdateEnabled" ).description( "Specifies if the update of a FHIR resource is enabled for output transformations from DHIS to FHIR for this tracked entity type. (by default false)." )
                        .type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "trackedEntityReference" ).description( "The reference to the DHIS2 Tracked Entity Type." ).type( JsonFieldType.OBJECT ),
                    fields.withPath( "trackedEntityReference.value" ).description( "The unique ID/code/name of the Tracked Entity Type." ).type( JsonFieldType.STRING ),
                    fields.withPath( "trackedEntityReference.type" ).description( "The type of reference value of the Tracked Entity Type." ).type( JsonFieldType.STRING ),
                    fields.withPath( "trackedEntityIdentifierReference" ).description( "The reference to the DHIS2 Tracked Entity Attribute that is used as national identifier." ).type( JsonFieldType.OBJECT ),
                    fields.withPath( "trackedEntityIdentifierReference.value" ).description( "The unique ID/code/name of the Tracked Entity Attribute." ).type( JsonFieldType.STRING ),
                    fields.withPath( "trackedEntityIdentifierReference.type" ).description( "The type of reference value of the Tracked Entity Attribute." ).type( JsonFieldType.STRING ),
                    subsectionWithPath( "_links" ).description( "Links to other resources" )
                ) ) );
    }

    @Nonnull
    protected MappedTrackedEntity loadTrackedEntity( @Nonnull String name )
    {
        final MappedTrackedEntity example = new MappedTrackedEntity();
        example.setName( name );
        return mappedTrackedEntityRepository.findOne( Example.of( example, ExampleMatcher.matching().withIgnorePaths( "enabled" ) ) )
            .orElseThrow( () -> new AssertionError( "Mapped tracked entity does not exist: " + name ) );
    }
}