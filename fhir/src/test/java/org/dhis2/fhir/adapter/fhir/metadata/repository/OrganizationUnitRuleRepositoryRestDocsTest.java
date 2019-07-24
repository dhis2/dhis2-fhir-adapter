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
import org.dhis2.fhir.adapter.fhir.metadata.model.OrganizationUnitRule;
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
 * Tests for {@link OrganizationUnitRuleRepository}.
 *
 * @author volsch
 */
public class OrganizationUnitRuleRepositoryRestDocsTest extends AbstractJpaRepositoryRestDocsTest
{
    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private ExecutableScriptRepository executableScriptRepository;

    @Test
    public void createOrganizationUnitRule() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( OrganizationUnitRule.class, constraintDescriptionResolver );
        final String request = IOUtils.resourceToString( "/org/dhis2/fhir/adapter/fhir/metadata/repository/createOrganizationUnitRule.json", StandardCharsets.UTF_8 )
            .replace( "$transformExpScriptId", API_BASE_URI + "/trackedEntities/" + "/executableScripts/" + loadScript( "TRANSFORM_DHIS_ORG_UNIT_FHIR_LOC" ).getId().toString() )
            .replace( "$locationIdentifierLookupScriptId", API_BASE_URI + "/trackedEntities/" + "/executableScripts/" + loadScript( "DHIS_ORG_UNIT_IDENTIFIER_LOC" ).getId().toString() )
            .replace( "$managingOrgIdentifierLookupScriptId", API_BASE_URI + "/executableScripts/" + loadScript( "DHIS_ORG_UNIT_IDENTIFIER" ).getId().toString() );
        final String location = docMockMvc.perform( post( "/api/rules" ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE )
            .contentType( MediaType.APPLICATION_JSON ).content( request ) )
            .andExpect( status().isCreated() )
            .andExpect( header().exists( "Location" ) )
            .andDo( documentationHandler.document( requestFields(
                attributes( key( "title" ).value( "Fields for organization unit creation" ) ),
                fields.withPath( "name" ).description( "The unique name of the rule." ).type( JsonFieldType.STRING ),
                fields.withPath( "description" ).description( "The detailed description that describes for which purpose the rule is used." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "dhisResourceType" ).description( "The type of the rule and the type of the data that is stored in DHIS2." ).type( JsonFieldType.STRING ),
                fields.withPath( "fhirResourceType" ).description( "The FHIR resource type of the incoming resource." ).type( JsonFieldType.STRING ),
                fields.withPath( "enabled" ).description( "Specifies if this rule is enabled." ).type( JsonFieldType.BOOLEAN ),
                fields.withPath( "impEnabled" ).description( "Specifies if transformation of a FHIR to a DHIS2 resource has been enabled (by default true)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "expEnabled" ).description( "Specifies if transformation of a DHIS2 to a FHIR resource has been enabled (by default false)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirCreateEnabled" ).description( "Specifies if the creation of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule (by default true)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirUpdateEnabled" ).description( "Specifies if the update of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule (by default false)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "fhirDeleteEnabled" ).description( "Specifies if the deletion of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule (by default false)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "containedAllowed" ).description( "Specified if this rule can process contained resources." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "stop" ).description( "Specifies if this rule is the last applied rule. When the transformation should not stop further rules are applied as well (by default false)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "grouping" ).description( "Specifies if the FHIR resources groups references of other FHIR resources (e.g. FHIR Encounter)." ).type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "simpleFhirId" ).description( "Specifies if the FHIR ID is just the DHIS2 ID without the rule ID. This feature must only be enabled if there is a distinct rule for the combination of FHIR and DHIS2 resource type." )
                    .type( JsonFieldType.BOOLEAN ).optional(),
                fields.withPath( "evaluationOrder" ).description( "Specifies the precedence of this rule when several rules match. Higher values define a higher precedence." ).type( JsonFieldType.NUMBER ),
                fields.withPath( "applicableCodeSet" ).description( "Link to the code set reference that is used to check if the incoming request is applicable for this rule." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "applicableImpScript" ).description( "Link to the executable script reference that is used to check if the incoming request is applicable for this rule when transforming from a FHIR to a DHIS2 resource. " +
                    "The script must be an evaluation script that returns a boolean value." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "transformImpScript" ).description( "Link to the executable script reference that is used to transform the FHIR resource input to the DHIS2 resource." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "applicableExpScript" ).description( "Link to the executable script reference that is used to check if the incoming request is applicable for this rule when transforming from a DHIS2 to a FHIR resource. " +
                    "The script must be an evaluation script that returns a boolean value." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "transformExpScript" ).description( "Link to the executable script reference that is used to transform the DHIS2 resource input to the FHIR resource." ).type( JsonFieldType.STRING ).optional(),
                fields.withPath( "identifierLookupScript" ).description(
                    "Link to the executable script reference that is used to extract the location identifier from the DHIS2 organization unit. The script must be an evaluation script that returns a STRING." )
                    .type( JsonFieldType.STRING ),
                fields.withPath( "managingOrgIdentifierLookupScript" ).description(
                    "Link to the executable script reference that is used to extract the managing organization unit identifier from the DHIS2 organization unit. The script must be an evaluation script that returns a STRING." )
                    .type( JsonFieldType.STRING ).optional(),
                fields.withPath( "filterScript" ).description( "Link to the executable script that prepares the search filter." ).type( JsonFieldType.STRING ).optional()
            ) ) ).andReturn().getResponse().getHeader( "Location" );

        mockMvc
            .perform( get( Objects.requireNonNull( location ) ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "lastUpdatedBy", is( "2h2maqu827d" ) ) )
            .andExpect( jsonPath( "name", is( "DHIS Organization Unit to FHIR Location" ) ) )
            .andExpect( jsonPath( "enabled", is( true ) ) )
            .andExpect( jsonPath( "impEnabled", is( false ) ) )
            .andExpect( jsonPath( "expEnabled", is( true ) ) )
            .andExpect( jsonPath( "fhirCreateEnabled", is( true ) ) )
            .andExpect( jsonPath( "fhirUpdateEnabled", is( false ) ) )
            .andExpect( jsonPath( "stop", is( false ) ) )
            .andExpect( jsonPath( "evaluationOrder", is( 1 ) ) )
            .andExpect( jsonPath( "dhisResourceType", is( "ORGANIZATION_UNIT" ) ) )
            .andExpect( jsonPath( "fhirResourceType", is( "LOCATION" ) ) )
            .andExpect( jsonPath( "_links.self.href", is( location ) ) );
    }

    @Test
    public void readOrganizationUnitRule() throws Exception
    {
        final ConstrainedFields fields = new ConstrainedFields( OrganizationUnitRule.class, constraintDescriptionResolver );
        final String ruleId = loadOrganizationUnitRule( "DHIS Organization Unit to FHIR Organization" ).getId().toString();
        docMockMvc.perform( get( "/api/rules/{ruleId}", ruleId ).header( AUTHORIZATION_HEADER_NAME, DATA_MAPPING_AUTHORIZATION_HEADER_VALUE ) )
            .andExpect( status().isOk() )
            .andDo( documentationHandler.document( links(
                linkWithRel( "self" ).description( "Link to this resource itself." ),
                linkWithRel( "organizationUnitRule" ).description( "Link to this resource itself." ),
                linkWithRel( "filterScript" ).description( "Link to the executable script that prepares the search filter." ).optional(),
                linkWithRel( "applicableCodeSet" ).description( "Link to the code set reference that is used to check if the incoming request is applicable for this rule." ).optional(),
                linkWithRel( "applicableImpScript" ).description( "Link to the executable script reference that is used to check if the incoming request is applicable for this rule. The script must be an evaluation script that returns a boolean value." )
                    .optional(),
                linkWithRel( "transformImpScript" ).description( "Link to the executable script reference that is used to transform the FHIR resource input to the DHIS2 resource." ).optional(),
                linkWithRel( "applicableExpScript" ).description( "Link to the executable script reference that is used to check if the incoming request is applicable for this rule when transforming a DHIS2 to FHIR resource. " +
                    "The script must be an evaluation script that returns a boolean value." ).optional(),
                linkWithRel( "transformExpScript" ).description( "Link to the executable script reference that is used to transform the DHIS2 resource input to the FHIR resource." ).optional(),
                linkWithRel( "identifierLookupScript" ).description( "Link to the executable script reference that is used to extract the location identifier from the DHIS2 organization unit. The script must be an evaluation script that returns a STRING." ),
                linkWithRel( "managingOrgIdentifierLookupScript" ).description( "Link to the executable script reference that is used to extract the managing organization unit identifier from the DHIS2 organization unit. " +
                    "The script must be an evaluation script that returns a STRING." ).optional() ),
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
                    fields.withPath( "dhisDataReferences" ).description( "Contains the references to DHIS2 data elements in which the data that is processed by the assigned rule is stored." ).type( JsonFieldType.ARRAY ).optional(),
                    subsectionWithPath( "_links" ).description( "Links to other resources" )
                ) ) );
    }

    @Nonnull
    protected AbstractRule loadOrganizationUnitRule( @Nonnull String name )
    {
        final AbstractRule example = new OrganizationUnitRule();
        example.setName( name );
        return ruleRepository.findOne( Example.of( example, ExampleMatcher.matching().withIgnorePaths( "enabled", "evaluationOrder",
            "impEnabled", "expEnabled", "fhirCreateEnabled", "fhirUpdateEnabled" ) ) )
            .orElseThrow( () -> new AssertionError( "Rule does not exist: " + name ) );
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