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
import org.dhis2.fhir.adapter.fhir.metadata.model.AbstractRule;
import org.dhis2.fhir.adapter.fhir.metadata.model.ExecutableScript;
import org.dhis2.fhir.adapter.fhir.metadata.model.MappedTrackerProgramStage;
import org.dhis2.fhir.adapter.fhir.metadata.model.ProgramStageRule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
 * Tests for {@link RuleRepository}.
 *
 * @author volsch
 */
public class ProgramStageRuleRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private MappedTrackerProgramStageRepository mappedTrackerProgramStageRepository;

    @Autowired
    private ExecutableScriptRepository executableScriptRepository;

    @Test
    public void createProgramStageRule() throws Exception
    {
        final MappedTrackerProgramStage mappedProgramStage = loadTrackerProgramStage( UUID.fromString( "4c074c85-be49-4b9d-8973-9e16b9615dad" ) );
        final ConstrainedFields fields = new ConstrainedFields( ProgramStageRule.class, constraintDescriptionResolver );
        final String request = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createProgramStageRule.json", StandardCharsets.UTF_8 )
            .replace( "$apiBaseUri", API_BASE_URI );
        final String location = docMockMvc.perform( post( "/api/rules" ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( request ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for program stage rule creation" ) ),
                fields.withPath( "name" ).description( "The unique name of the rule." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description that describes for which purpose the rule is used." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "dhisResourceType" ).description( "The type of the rule and the type of the data that is stored in DHIS2." ).type( JsonFieldType.STRING ),
                fields.withPath( "fhirResourceType" ).description( "The FHIR resource type of the incoming resource." ).type( JsonFieldType.STRING ),
                fields.withPath( "enabled" ).description( "Specifies if this rule is enabled." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "impEnabled" ).description( "Specifies if transformation of a FHIR to a DHIS2 resource has been enabled." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "expEnabled" ).description( "Specifies if transformation of a DHIS2 to a FHIR resource has been enabled." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "fhirCreateEnabled" ).description( "Specifies if the creation of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule (by default true)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirUpdateEnabled" ).description( "Specifies if the update of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule (by default false)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirDeleteEnabled" ).description( "Specifies if the deletion of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule (by default false)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "stop" ).description( "Specifies if this rule is the last applied rule. When the transformation should not stop further rules are applied as well." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "grouping" ).description( "Specifies if the FHIR resources groups references of other FHIR resources (e.g. FHIR Encounter)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "simpleFhirId" ).description( "Specifies if the FHIR ID is just the DHIS2 ID without the rule ID. This feature must only be enabled if there is a distinct rule for the combination of FHIR and DHIS2 resource type." )
                    .type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "evaluationOrder" ).description( "Specifies the precedence of this rule when several rules match. Higher values define a higher precedence." ).type( JsonFieldType.NUMBER ),
                fields.withPath( "containedAllowed" ).description( "Specified if this rule can process contained resources." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "enrollmentCreationEnabled" ).description( "Specifies if the creation of an enrollment is allowed when processing the rule and the enrollment does not exist." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "eventCreationEnabled" ).description( "Specifies if the creation of an event is allowed when processing the rule and the event does not exist." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "updateEventDate" ).description( "Specifies if the event date can be updated with the content of the transformed FHIR resource." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "beforePeriodDayType" ).description( "Specifies to which date the the amount of the specified before period days are relative." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "beforePeriodDays" ).description( "The day (relative according to the specified setting) on and after which FHIR resources (according to their effective date) are regarded as being applicable for this program stage." )
                    .type( JsonFieldType.NUMBER ).optional(),
                fields.withPath( "afterPeriodDayType" ).description( "Specifies to which date the the amount of the specified after period days are relative." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "afterPeriodDays" ).description( "The day (relative according to the specified setting) on and before which FHIR resources (according to their effective date) are regarded as being applicable for this program stage." )
                    .type( JsonFieldType.NUMBER ).optional(),
                fields.withPath( "applicableEnrollmentStatus" ).description( "Specifies for which status of the corresponding enrollment the transformation can be performed." ).type( JsonFieldType.OBJECT ),
                fields.withPath( "applicableEnrollmentStatus.active" ).description( "Specifies that the transformation can be performed when the status of the enrollment is active." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "applicableEnrollmentStatus.completed" ).description( "Specifies that the transformation can be performed when the status of the enrollment is completed." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "applicableEnrollmentStatus.cancelled" ).description( "Specifies that the transformation can be performed when the status of the enrollment is cancelled." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "applicableEventStatus" ).description( "Specifies for which status of the corresponding event the transformation can be performed." ).type( JsonFieldType.OBJECT ),
                fields.withPath( "applicableEventStatus.active" ).description( "Specifies that the transformation can be performed when the status of the event is active." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "applicableEventStatus.overdue" ).description( "Specifies that the transformation can be performed when the status of the event is overdue." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "applicableEventStatus.schedule" ).description( "Specifies that the transformation can be performed when the status of the event is schedule." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "applicableEventStatus.visited" ).description( "Specifies that the transformation can be performed when the status of the event is visited." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "applicableEventStatus.skipped" ).description( "Specifies that the transformation can be performed when the status of the event is skipped." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "applicableEventStatus.completed" ).description( "Specifies that the transformation can be performed when the status of the event is completed." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "eventStatusUpdate" ).description( "Specifies if the status of the event should set to active when it is not active." ).type( JsonFieldType.OBJECT ),
                fields.withPath( "eventStatusUpdate.overdueToActive" ).description( "Specifies that the event status should be set to active when the event status is overdue." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "eventStatusUpdate.scheduleToActive" ).description( "Specifies that the event status should be set to active when the event status is schedule." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "eventStatusUpdate.completedToActive" ).description( "Specifies that the event status should be set to active when the event status is completed." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "dhisDataReferences" ).description( "Contains the references to DHIS2 data elements in which the data that is processed by the assigned rule is stored." ).type( JsonFieldType.ARRAY ).optional(),
                fields.withPath( "dhisDataReferences[].description" ).description( "The description of the purpose of the data reference." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "dhisDataReferences[].scriptArgName" ).description( "The name of the script argument to which the data reference is passed when performing the transformation." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "dhisDataReferences[].required" ).description( "Specifies if the data element is required. If the data element is required and has no value, the value is regarded as absent and absence handling " +
                    "is performed when transforming." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "dhisDataReferences[].dataReference" ).description( "Specifies the reference to the data element." ).type( JsonFieldType.OBJECT ),
                fields.withPath( "dhisDataReferences[].dataReference.value" ).description( "The unique ID/code/name of the data element." ).type( JsonFieldType.STRING ),
                fields.withPath( "dhisDataReferences[].dataReference.type" ).description( "The type of reference value of the data element." ).type( JsonFieldType.STRING ),
                fields.withPath( "applicableCodeSet" ).description( "Link to the code set reference that is used to check if the incoming request is applicable for this rule." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "applicableImpScript" ).description( "Link to the executable script reference that is used to check if the incoming request is applicable for this rule. The script must be an evaluation script that returns a boolean value." )
                    .type( JsonFieldType.STRING ).optional(),
                fields.withPath( "transformImpScript" ).description( "Link to the executable script reference that is used to transform the FHIR resource input to the DHIS2 resource." )
                    .type( JsonFieldType.STRING ).optional(),
                fields.withPath( "applicableExpScript" ).description( "Link to the executable script reference that is used to check if the incoming request is applicable for this rule when transforming a DHIS2 to FHIR resource. " +
                    "The script must be an evaluation script that returns a boolean value." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "transformExpScript" ).description( "Link to the executable script reference that is used to transform the DHIS2 resource input to the FHIR resource." )
                    .type( JsonFieldType.STRING ).optional(),
                fields.withPath( "programStage" ).description( "Link to the tracked entity resource that describes the tracked entity of the transformation." ).type( JsonFieldType.STRING ),
                fields.withPath( "expDeleteEvaluateScript" ).description( "Link to the executable evaluation script that evaluates if the transformation should result in a deletion of the corresponding FHIR resource." )
                    .type( JsonFieldType.STRING ).optional(),
                fields.withPath( "filterScript" ).description( "Link to the executable script that prepares the search filter." ).type( JsonFieldType.STRING ).optional() )
            ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "name", is( "Child Programme: Estimated Infant Weight" ) ) )
            .andExpect( jsonPath( "enabled", is( true ) ) )
            .andExpect( jsonPath( "impEnabled", is( true ) ) )
            .andExpect( jsonPath( "expEnabled", is( false ) ) )
            .andExpect( jsonPath( "fhirCreateEnabled", is( true ) ) )
            .andExpect( jsonPath( "fhirUpdateEnabled", is( false ) ) )
            .andExpect( jsonPath( "stop", is( false ) ) )
            .andExpect( jsonPath( "evaluationOrder", is( 10 ) ) )
            .andExpect( jsonPath( "dhisResourceType", is( "PROGRAM_STAGE_EVENT" ) ) )
            .andExpect( jsonPath( "fhirResourceType", is( "OBSERVATION" ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readProgramStageRule() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( ProgramStageRule.class, constraintDescriptionResolver );
        final String ruleId = loadProgramStageRule( UUID.fromString( "a6636c83-f236-48cd-bb2b-592147db9a34" ) ).getId().toString();
        docMockMvc.perform( get( "/api/rules/{ruleId}", ruleId ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "programStageRule" ).description( "Link to this resource itself." ),
                linkWithRel( "filterScript" ).description( "Link to the executable script that prepares the search filter." ).optional(),
                linkWithRel( "applicableCodeSet" ).description( "Link to the code set reference that is used to check if the incoming request is applicable for this rule." ).optional(),
                linkWithRel( "applicableImpScript" ).description( "Link to the executable script reference that is used to check if the incoming request is applicable for this rule. The script must be an evaluation script that returns a boolean value." ).optional(),
                linkWithRel( "transformImpScript" ).description( "Link to the executable script reference that is used to transform the FHIR resource input to the DHIS2 resource." ).optional(),
                linkWithRel( "applicableExpScript" ).description( "Link to the executable script reference that is used to check if the incoming request is applicable for this rule when transforming a DHIS2 to FHIR resource. " +
                    "The script must be an evaluation script that returns a boolean value." ).optional(),
                linkWithRel( "transformExpScript" ).description( "Link to the executable script reference that is used to transform the DHIS2 resource input to the FHIR resource." ).optional(),
                linkWithRel( "programStage" ).description( "Link to the tracked entity resource that describes the tracked entity of the transformation." ),
                linkWithRel( "expDeleteEvaluateScript" ).description( "Link to the executable evaluation script that evaluates if the transformation should result in a deletion of the corresponding FHIR resource." ).optional() ),
                responseFields(
                    attributes( key( "title" ).value( "Fields for rule reading" ) ),
                    fields.withPath( "createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                    fields.withPath( "lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                    fields.withPath( "name" ).description( "The unique name of the rule." ).type( JsonFieldType.STRING ),
                    fields.withPath( "description" ).description( "The detailed description that describes for which purpose the rule is used." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "dhisResourceType" ).description( "The type of the rule and the type of the data that is stored in DHIS2." ).type( JsonFieldType.STRING ),
                    fields.withPath( "fhirResourceType" ).description( "The FHIR resource type of the incoming resource." ).type( JsonFieldType.STRING ),
                    fields.withPath( "enabled" ).description( "Specifies if this rule is enabled." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "impEnabled" ).description( "Specifies if transformation of a FHIR to a DHIS2 resource has been enabled." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "expEnabled" ).description( "Specifies if transformation of a DHIS2 to a FHIR resource has been enabled." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "fhirCreateEnabled" ).description( "Specifies if the creation of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule (by default true)." ).type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "fhirUpdateEnabled" ).description( "Specifies if the update of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule (by default false)." ).type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "fhirDeleteEnabled" ).description( "Specifies if the deletion of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule (by default false)." ).type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "stop" ).description( "Specifies if this rule is the last applied rule. When the transformation should not stop further rules are applied as well." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "grouping" ).description( "Specifies if the FHIR resources groups references of other FHIR resources (e.g. FHIR Encounter)." ).type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "simpleFhirId" ).description( "Specifies if the FHIR ID is just the DHIS2 ID without the rule ID. This feature must only be enabled if there is a distinct rule for the combination of FHIR and DHIS2 resource type." )
                        .type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "evaluationOrder" ).description( "Specifies the precedence of this rule when several rules match. Higher values define a higher precedence." ).type( JsonFieldType.NUMBER ),
                    fields.withPath( "containedAllowed" ).description( "Specified if this rule can process contained resources." ).type( JsonFieldType.BOOLEAN ).optional(),
                    fields.withPath( "enrollmentCreationEnabled" ).description( "Specifies if the creation of an enrollment is allowed when processing the rule and the enrollment does not exist." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "eventCreationEnabled" ).description( "Specifies if the creation of an event is allowed when processing the rule and the event does not exist." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "updateEventDate" ).description( "Specifies if the event date can be updated with the content of the transformed FHIR resource." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "beforePeriodDayType" ).description( "Specifies to which date the the amount of the specified before period days are relative." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "beforePeriodDays" ).description( "The day (relative according to the specified setting) on and after which FHIR resources (according to their effective date) are regarded as being applicable for this program stage." )
                        .type( JsonFieldType.NUMBER ).optional(),
                    fields.withPath( "afterPeriodDayType" ).description( "Specifies to which date the the amount of the specified after period days are relative." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "afterPeriodDays" ).description( "The day (relative according to the specified setting) on and before which FHIR resources (according to their effective date) are regarded as being applicable for this program stage." )
                        .type( JsonFieldType.NUMBER ).optional(),
                    fields.withPath( "applicableEnrollmentStatus" ).description( "Specifies for which status of the corresponding enrollment the transformation can be performed." ).type( JsonFieldType.OBJECT ),
                    fields.withPath( "applicableEnrollmentStatus.active" ).description( "Specifies that the transformation can be performed when the status of the enrollment is active." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "applicableEnrollmentStatus.completed" ).description( "Specifies that the transformation can be performed when the status of the enrollment is completed." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "applicableEnrollmentStatus.cancelled" ).description( "Specifies that the transformation can be performed when the status of the enrollment is cancelled." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "applicableEventStatus" ).description( "Specifies for which status of the corresponding event the transformation can be performed." ).type( JsonFieldType.OBJECT ),
                    fields.withPath( "applicableEventStatus.active" ).description( "Specifies that the transformation can be performed when the status of the event is active." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "applicableEventStatus.overdue" ).description( "Specifies that the transformation can be performed when the status of the event is overdue." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "applicableEventStatus.schedule" ).description( "Specifies that the transformation can be performed when the status of the event is schedule." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "applicableEventStatus.visited" ).description( "Specifies that the transformation can be performed when the status of the event is visited." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "applicableEventStatus.skipped" ).description( "Specifies that the transformation can be performed when the status of the event is skipped." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "applicableEventStatus.completed" ).description( "Specifies that the transformation can be performed when the status of the event is completed." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "eventStatusUpdate" ).description( "Specifies if the status of the event should set to active when it is not active." ).type( JsonFieldType.OBJECT ),
                    fields.withPath( "eventStatusUpdate.overdueToActive" ).description( "Specifies that the event status should be set to active when the event status is overdue." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "eventStatusUpdate.scheduleToActive" ).description( "Specifies that the event status should be set to active when the event status is schedule." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "eventStatusUpdate.completedToActive" ).description( "Specifies that the event status should be set to active when the event status is completed." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "dhisDataReferences" ).description( "Contains the references to DHIS2 data elements in which the data that is processed by the assigned rule is stored." ).type( JsonFieldType.ARRAY ).optional(),
                    fields.withPath( "dhisDataReferences[].createdAt" ).description( "The timestamp when the resource has been created." ).type( JsonFieldType.STRING ),
                    fields.withPath( "dhisDataReferences[].lastUpdatedBy" ).description( "The ID of the user that has updated the user the last time or null if the data has been imported to the database directly." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "dhisDataReferences[].lastUpdatedAt" ).description( "The timestamp when the resource has been updated the last time." ).type( JsonFieldType.STRING ),
                    fields.withPath( "dhisDataReferences[].description" ).description( "The description of the purpose of the data reference." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "dhisDataReferences[].scriptArgName" ).description( "The name of the script argument to which the data reference is passed when performing the transformation." ).type( JsonFieldType.STRING ).optional(),
                    fields.withPath( "dhisDataReferences[].required" ).description( "Specifies if the data element is required. If the data element is required and has no value, the value is regarded as absent and absence handling " +
                        "is performed when transforming." ).type( JsonFieldType.BOOLEAN ),
                    fields.withPath( "dhisDataReferences[].dataReference" ).description( "Specifies the reference to the data element." ).type( JsonFieldType.OBJECT ),
                    fields.withPath( "dhisDataReferences[].dataReference.value" ).description( "The unique ID/code/name of the data element." ).type( JsonFieldType.STRING ),
                    fields.withPath( "dhisDataReferences[].dataReference.type" ).description( "The type of reference value of the data element." ).type( JsonFieldType.STRING ),
                    subsectionWithPath( "dhisDataReferences[]._links" ).ignored(),
                    subsectionWithPath( "_links" ).description( "Links to other resources" )
                ) ) );
    }

    @Nonnull
    protected MappedTrackerProgramStage loadTrackerProgramStage( @Nonnull UUID id )
    {
        return mappedTrackerProgramStageRepository.findById( id )
            .orElseThrow( () -> new AssertionError( "Mapped tracker program stage does not exist: " + id ) );
    }

    @Nonnull
    protected AbstractRule loadProgramStageRule( @Nonnull UUID id )
    {
        return ruleRepository.findById( id )
            .orElseThrow( () -> new AssertionError( "Rule does not exist: " + id ) );
    }

    @Nonnull
    protected ExecutableScript loadScript( @Nonnull String code )
    {
        final ExecutableScript example = new ExecutableScript();
        example.setCode( code );
        return executableScriptRepository.findOne( Example.of( example ) )
            .orElseThrow( () -> new AssertionError( "Executable script does not exist: " + code ) );
    }
}