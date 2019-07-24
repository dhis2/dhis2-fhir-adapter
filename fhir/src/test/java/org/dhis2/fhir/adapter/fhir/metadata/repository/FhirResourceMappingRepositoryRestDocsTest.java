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
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceMapping;
import org.dhis2.fhir.adapter.fhir.metadata.model.FhirResourceType;
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
 * Tests for {@link FhirResourceMappingRepository}.
 *
 * @author volsch
 */
public class FhirResourceMappingRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private FhirResourceMappingRepository fhirResourceMappingRepository;

    @Test
    public void createFhirResourceMapping() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( FhirResourceMapping.class, constraintDescriptionResolver );
        final String request = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createFhirResourceMapping.json", StandardCharsets.UTF_8 )
            .replace( "$executableScriptBaseUri", API_BASE_URI + "/executableScripts" );
        final String location = docMockMvc.perform( post( "/api/fhirResourceMappings" ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( request ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for FHIR resource mapping creation" ) ),
                fields.withPath( "fhirResourceType" ).description( "The unique FHIR resource type for which the definition is made." ).type( JsonFieldType.STRING ),
                fields.withPath( "trackedEntityFhirResourceType" ).description( "The FHIR resource type to which the DHIS tracked entity is mapped." ).type( JsonFieldType.STRING ),
                fields.withPath( "deleteWhenAbsent" ).description( "Specifies if the FHIR resource should be deleted when the corresponding required DHIS2 data elements are absent." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "impTeiLookupScript" ).description( "Link to the executable script that returns the FHIR resource for the TEI that is assigned to the evaluated mapped FHIR resource. The return type of the script must be FHIR_RESOURCE." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "impEventOrgLookupScript" ).description( "Link to the executable evaluation script that returns a reference to a DHIS2 organization unit of an event. The return type of the script must be ORG_UNIT_REF." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "impEventGeoLookupScript" ).description( "Link to the executable evaluation script that returns a location (longitude and latitude) of an event. The returns type of the script must be LOCATION." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "impEventDateLookupScript" ).description( "Link to the executable evaluation script that returns the event date. The return type of the script must be DATE_TIME." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "impEnrollmentOrgLookupScript" ).description( "Link to the executable evaluation script that returns a reference to a DHIS2 organization unit of an enrollment. The return type of the script must be ORG_UNIT_REF." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "impEnrollmentGeoLookupScript" ).description( "Link to the executable evaluation script that returns a location (longitude and latitude) of an event. The returns type of the script must be LOCATION." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "impEnrollmentDateLookupScript" ).description( "Link to the executable evaluation script that returns the event date. The return type of the script must be DATE_TIME." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "impEffectiveDateLookupScript" ).description( "Link to the executable evaluation script that extract the effective date when the data has been collected. The return type of the script must be DATE_TIME." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "impProgramStageRefLookupScript" ).description( "Link to the executable evaluation script that extract the program stage reference (if any). The return type of the script must be PROGRAM_STAGE_REF." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "expAbsentTransformScript" ).description( "Link to the executable transformation script that sets in an existing exported FHIR resource a status that indicates that corresponding required DHIS2 data elements are missing. "
                    + "The return type must be BOOLEAN and indicates if the script was successful." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "expTeiTransformScript" ).description( "Link to the executable transformation script that sets the DHIS2 tracked entity related FHIR resource in the transformed FHIR resource. "
                    + "The return type must be BOOLEAN and indicates if the script was successful." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "expOrgUnitTransformScript" ).description( "Link to the executable transformation script that sets the DHIS2 organization unit related FHIR resource in the transformed FHIR resource. "
                    + "The return type must be BOOLEAN and indicates if the script was successful." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "expGeoTransformScript" ).description( "Link to the executable transformation script that sets the DHIS2 GEO coordinate information in the transformed FHIR resource. "
                    + "The return type must be BOOLEAN and indicates if the script was successful." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "expStatusTransformScript" ).description( "Link to the executable transformation script that sets the DHIS2 status (e.g. event status) in the transformed FHIR resource. "
                    + "The return type must be BOOLEAN and indicates if the script was successful." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "expDateTransformScript" ).description( "Link to the executable transformation script that sets the DHIS2 effective date (e.g. event date) in the transformed FHIR resource. "
                    + "The return type must be BOOLEAN and indicates if the script was successful." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "expGroupTransformScript" ).description( "Link to the executable transformation script that sets and collects DHIS2 grouping information (e.g. FHIR encounter as DHIS2 event) in the transformed FHIR resource. "
                    + "The return type must be BOOLEAN and indicates if the script was successful." ).type( JsonFieldType.STRING ).optional()
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "fhirResourceType", is( "IMMUNIZATION" ) ) )
            .andExpect( jsonPath( "trackedEntityFhirResourceType", is( "PRACTITIONER" ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readFhirResourceMapping() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( FhirResourceMapping.class, constraintDescriptionResolver );
        final String fhirResourceMappingId = loadFhirResourceMapping( FhirResourceType.OBSERVATION ).getId().toString();
        docMockMvc.perform( get( "/api/fhirResourceMappings/{fhirResourceMappingId}", fhirResourceMappingId ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "fhirResourceMapping" ).description( "Link to this resource itself." ),
                linkWithRel( "impTeiLookupScript" ).description( "Link to the executable script that returns the FHIR resource for the TEI that is assigned to the evaluated mapped FHIR resource. The return type of the script must be FHIR_RESOURCE." ).optional(),
                linkWithRel( "impEventOrgLookupScript" ).description( "Link to the executable evaluation script that returns a reference to a DHIS2 organization unit of an event. The return type of the script must be ORG_UNIT_REF." ).optional(),
                linkWithRel( "impEventGeoLookupScript" ).description( "Link to the executable evaluation script that returns a location (longitude and latitude) of an event. The returns type of the script must be LOCATION." ).optional(),
                linkWithRel( "impEventDateLookupScript" ).description( "Link to the executable evaluation script that returns the event date. The return type of the script must be DATE_TIME." ).optional(),
                linkWithRel( "impEnrollmentOrgLookupScript" ).description( "Link to the executable evaluation script that returns a reference to a DHIS2 organization unit of an enrollment. The return type of the script must be ORG_UNIT_REF." ).optional(),
                linkWithRel( "impEnrollmentGeoLookupScript" ).description( "Link to the executable evaluation script that returns a location (longitude and latitude) of an event. The returns type of the script must be LOCATION." ).optional(),
                linkWithRel( "impEnrollmentDateLookupScript" ).description( "Link to the executable evaluation script that returns the event date. The return type of the script must be DATE_TIME." ).optional(),
                linkWithRel( "impEffectiveDateLookupScript" ).description( "Link to the executable evaluation script that extract the effective date when the data has been collected. The return type of the script must be DATE_TIME." ).optional(),
                linkWithRel( "impProgramStageRefLookupScript" ).description( "Link to the executable evaluation script that extract the program stage reference (if any). The return type of the script must be PROGRAM_STAGE_REF." ).optional(),
                linkWithRel( "expAbsentTransformScript" ).description( "Link to the executable transformation script that sets in an existing exported FHIR resource a status that indicates that corresponding required DHIS2 data elements are missing. " +
                    "The return type must be BOOLEAN and indicates if the script was successful." ).optional(),
                linkWithRel( "expTeiTransformScript" ).description( "Link to the executable transformation script that sets the DHIS2 tracked entity related FHIR resource in the transformed FHIR resource. "
                    + "The return type must be BOOLEAN and indicates if the script was successful." ).optional(),
                linkWithRel( "expOrgUnitTransformScript" ).description( "Link to the executable transformation script that sets the DHIS2 organization unit related FHIR resource in the transformed FHIR resource. "
                    + "The return type must be BOOLEAN and indicates if the script was successful." ).optional(),
                linkWithRel( "expGeoTransformScript" ).description( "Link to the executable transformation script that sets the DHIS2 GEO coordinate information in the transformed FHIR resource. "
                    + "The return type must be BOOLEAN and indicates if the script was successful." ).optional(),
                linkWithRel( "expStatusTransformScript" ).description( "Link to the executable transformation script that sets the DHIS2 status (e.g. event status) in the transformed FHIR resource. "
                    + "The return type must be BOOLEAN and indicates if the script was successful." ).optional(),
                linkWithRel( "expDateTransformScript" ).description( "Link to the executable transformation script that sets the DHIS2 effective date (e.g. event date) in the transformed FHIR resource. "
                    + "The return type must be BOOLEAN and indicates if the script was successful." ).optional(),
                linkWithRel( "expGroupTransformScript" ).description( "Link to the executable transformation script that sets and collects DHIS2 grouping information (e.g. FHIR encounter as DHIS2 event) in the transformed FHIR resource. "
                    + "The return type must be BOOLEAN and indicates if the script was successful." ).optional() ),
                responseFields(
                    attributes( key( "title" ).value( "Fields for tracked entity reading" ) ),
                    fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                    fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                    fields.withPath( "fhirResourceType" ).description( "The unique FHIR resource type for which the definition is made." ).type( JsonFieldType.STRING ),
                    fields.withPath( "trackedEntityFhirResourceType" ).description( "The FHIR resource type to which the DHIS tracked entity is mapped." ).type( JsonFieldType.STRING ),
                    fields.withPath( "deleteWhenAbsent" ).description( "Specifies if the FHIR resource should be deleted when the corresponding required DHIS2 data elements are absent." ).type( JsonFieldType.BOOLEAN ).optional(),
                    subsectionWithPath( "_links" ).description( "Links to other resources" )
                ) ) );
    }

    @Nonnull
    protected FhirResourceMapping loadFhirResourceMapping( @Nonnull FhirResourceType fhirResourceType )
    {
        final FhirResourceMapping example = new FhirResourceMapping();
        example.setFhirResourceType( fhirResourceType );
        return fhirResourceMappingRepository.findOne( Example.of( example, ExampleMatcher.matching().withIgnorePaths( "deleteWhenAbsent" ) ) )
            .orElseThrow( () -> new AssertionError( "FHIR resource mapping does not exist: " + fhirResourceType ) );
    }
}