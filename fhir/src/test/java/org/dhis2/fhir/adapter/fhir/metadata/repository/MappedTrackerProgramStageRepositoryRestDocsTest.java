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
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgramStage;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Tests for {@link MappedTrackerProgramStageRepository}.
 *
 * @author volsch
 */
public class MappedTrackerProgramStageRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private MappedTrackerProgramStageRepository mappedTrackerProgramStageRepository;

    @Test
    public void createProgramStage() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( MappedTrackerProgramStage.class, constraintDescriptionResolver );
        final String request = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createProgramStage.json", StandardCharsets.UTF_8 )
            .replace( "$apiBaseUri", API_BASE_URI );
        final String location = docMockMvc.perform( post( "/api/trackerProgramStages" ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( request ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for tracker program stage creation" ) ),
                fields.withPath( "name" ).description( "The unique name of the program stage." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description that describes for which purpose the program stage is used." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "enabled" ).description( "Specifies if this rule is enabled." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "expEnabled" ).description( "Specifies if output transformation from DHIS to FHIR for this tracked entity type is enabled." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "programStageReference" ).description( "The reference to the DHIS2 Program Stage." ).type( JsonFieldType.OBJECT ),
                fields.withPath( "programStageReference.value" ).description( "The unique ID/code/name of the Program Stage." ).type( JsonFieldType.STRING ),
                fields.withPath( "programStageReference.type" ).description( "The type of reference value of the Program Stage." ).type( JsonFieldType.STRING ),
                fields.withPath( "fhirCreateEnabled" ).description( "Specifies if the creation of a FHIR resource is enabled for output transformations from DHIS to FHIR for this tracked entity type (by default true)." )
                    .type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirUpdateEnabled" ).description( "Specifies if the update of a FHIR resource is enabled for output transformations from DHIS to FHIR for this tracked entity type. (by default false)." )
                    .type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirDeleteEnabled" ).description( "Specifies if the deletion of a FHIR resource is enabled for output transformations from DHIS to FHIR for this tracked entity type. (by default false)." )
                    .type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "creationEnabled" ).description( "Specifies if the automatic creation of this program stage has been enabled (by default false)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "creationStatus" ).description( "The status of the event when it is created automatically." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "eventDateIsIncident" ).description( "Specifies if the event date should be set based on the incident date." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "beforePeriodDayType" ).description( "Specifies to which date the the amount of the specified before period days are relative." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "beforePeriodDays" ).description( "The day (relative according to the specified setting) on and after which FHIR resources (according to their effective date) are regarded as being applicable for this program stage." )
                    .type( JsonFieldType.NUMBER ).optional(),
                fields.withPath( "afterPeriodDayType" ).description( "Specifies to which date the the amount of the specified after period days are relative." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "afterPeriodDays" ).description( "The day (relative according to the specified setting) on and before which FHIR resources (according to their effective date) are regarded as being applicable for this program stage." )
                    .type( JsonFieldType.NUMBER ).optional(),
                fields.withPath( "program" ).description( "Link to the tracker program to which this stage belongs to." ).type( JsonFieldType.STRING ),
                fields.withPath( "creationApplicableScript" ).description( "Link to the script that evaluates if the creation of the program stage is applicable (when creation is enabled)." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "creationScript" ).description( "Link to the evaluation of transformation script that is executed when the program stage event is created (when creation is enabled)." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "beforeScript" ).description( "Link to the evaluation of transformation script that is executed before the transformed FHIR resource is processed." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "afterScript" ).description( "Link to the evaluation of transformation script that is executed after the transformed FHIR resource has been processed." ).type( JsonFieldType.STRING ).optional()
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "name", is( "Other Baby Postnatal" ) ) )
            .andExpect( jsonPath( "enabled", is( true ) ) )
            .andExpect( jsonPath( "expEnabled", is( false ) ) )
            .andExpect( jsonPath( "fhirCreateEnabled", is( true ) ) )
            .andExpect( jsonPath( "fhirUpdateEnabled", is( false ) ) )
            .andExpect( jsonPath( "programStageReference.value", is( "Baby Postnatal" ) ) )
            .andExpect( jsonPath( "programStageReference.type", is( "NAME" ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readProgramStage() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( MappedTrackerProgramStage.class, constraintDescriptionResolver );
        final String programStageId = loadProgramStage( UUID.fromString( "4c074c85-be49-4b9d-8973-9e16b9615dad" ) ).getId().toString();
        docMockMvc.perform( get( "/api/trackerProgramStages/{programStageId}", programStageId ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "trackerProgramStage" ).description( "Link to this resource itself." ),
                linkWithRel( "program" ).description( "Link to the tracker program to which this stage belongs to." ),
                linkWithRel( "creationApplicableScript" ).description( "Link to the script that evaluates if the creation of the program stage is applicable (when creation is enabled)." ).optional(),
                linkWithRel( "creationScript" ).description( "Link to the evaluation of transformation script that is executed when the program stage event is created (when creation is enabled)." ).optional(),
                linkWithRel( "beforeScript" ).description( "Link to the evaluation of transformation script that is executed before the transformed FHIR resource is processed." ).optional(),
                linkWithRel( "afterScript" ).description( "Link to the evaluation of transformation script that is executed after the transformed FHIR resource has been processed." ).optional() ),
                responseFields(
                    attributes( key( "title" ).value( "Fields for tracker program stage reading" ) ),
                    fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                    fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                    fields.withPath( "name" ).description( "The unique name of the program stage." ).type( JsonFieldType.STRING ),
                    fields.withPath( "description" ).description( "The detailed description that describes for which purpose the program stage is used." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "enabled" ).description( "Specifies if this rule is enabled." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "expEnabled" ).description( "Specifies if output transformation from DHIS to FHIR for this tracked entity type is enabled." ).type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "programStageReference" ).description( "The reference to the DHIS2 Program Stage." ).type( JsonFieldType.OBJECT ),
                    fields.withPath( "programStageReference.value" ).description( "The unique ID/code/name of the Program Stage." ).type( JsonFieldType.STRING ),
                    fields.withPath( "programStageReference.type" ).description( "The type of reference value of the Program Stage." ).type( JsonFieldType.STRING ),
                    fields.withPath( "fhirCreateEnabled" ).description( "Specifies if the creation of a FHIR resource is enabled for output transformations from DHIS to FHIR for this tracked entity type (by default true)." )
                        .type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "fhirUpdateEnabled" ).description( "Specifies if the update of a FHIR resource is enabled for output transformations from DHIS to FHIR for this tracked entity type. (by default false)." )
                        .type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "fhirDeleteEnabled" ).description( "Specifies if the deletion of a FHIR resource is enabled for output transformations from DHIS to FHIR for this tracked entity type. (by default false)." )
                        .type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "creationEnabled" ).description( "Specifies if the automatic creation of this program stage has been enabled (by default false)." ).type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "creationStatus" ).description( "The status of the event when it is created automatically." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "eventDateIsIncident" ).description( "Specifies if the event date should be set based on the incident date." ).type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "beforePeriodDayType" ).description( "Specifies to which date the the amount of the specified before period days are relative." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "beforePeriodDays" ).description( "The day (relative according to the specified setting) on and after which FHIR resources (according to their effective date) are regarded as being applicable for this program stage." )
                        .type( JsonFieldType.NUMBER ).optional(),
                    fields.withPath( "afterPeriodDayType" ).description( "Specifies to which date the the amount of the specified after period days are relative." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "afterPeriodDays" ).description( "The day (relative according to the specified setting) on and before which FHIR resources (according to their effective date) are regarded as being applicable for this program stage." )
                        .type( JsonFieldType.NUMBER ).optional(),
                    subsectionWithPath( "_links" ).description( "Links to other resources" )
                ) ) );
    }

    @Nonnull
    protected MappedTrackerProgramStage loadProgramStage( @Nonnull UUID id )
    {
        return mappedTrackerProgramStageRepository.findById( id )
            .orElseThrow( () -> new AssertionError( "Mapped tracker program stage does not exist: " + id ) );
    }
}