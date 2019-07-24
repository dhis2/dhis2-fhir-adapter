/*
 *  Copyright (c) 2004-2019, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO PROGRAM_STAGE_EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

-- @formatter:off

INSERT INTO fhir_dhis_resource_type_enum VALUES('PROGRAM_METADATA');
INSERT INTO fhir_dhis_resource_type_enum VALUES('PROGRAM_STAGE_METADATA');
INSERT INTO fhir_resource_type_enum VALUES('PLAN_DEFINITION');
INSERT INTO fhir_resource_type_enum VALUES('QUESTIONNAIRE');
INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_PLAN_DEFINITION');
INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_QUESTIONNAIRE');

INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('a55e8da7-d719-48dd-af74-d5f6919ce479', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'PLAN_DEFINITION', NULL, 'FHIR Plan Definition Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('70a57cdf-00af-4428-b7b3-114b408a736b', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'QUESTIONNAIRE', NULL, 'FHIR Questionnaire Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('edea9a1e-83c0-4ffe-801d-ed5da74d50de', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'CARE_PLAN', NULL, 'FHIR Care Plan Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('fdc10514-ab75-468a-8813-6ea41b7c671d', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'QUESTIONNAIRE_RESPONSE', NULL, 'FHIR Questionnaire Response Resource.', TRUE);

INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('f79b9892-2b43-48af-9a82-3ba03642bac4', 0, 'DHIS2 FHIR Adapter Plan Definition Identifier', 'SYSTEM_DHIS2_FHIR_PLAN_DEFINITION_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/plan-definition-identifier',
        'DHIS2 FHIR Adapter Plan Definition Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('296576d0-31b5-4427-8bf0-7cc168fb3a82', 0, 'DHIS2 FHIR Adapter Questionnaire Identifier', 'SYSTEM_DHIS2_FHIR_QUESTIONNAIRE_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/questionnaire-identifier',
        'DHIS2 FHIR Adapter Questionnaire Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('9c5584f0-8b06-44ee-a18f-6addf938969f', 0, 'DHIS2 FHIR Adapter Care Plan Identifier', 'SYSTEM_DHIS2_FHIR_CARE_PLAN_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/care-plan-identifier',
        'DHIS2 FHIR Adapter Care Plan Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('5bdff628-d94f-43dc-8367-8c84fe48bdd9', 0, 'DHIS2 FHIR Adapter Questionnaire Response Identifier', 'SYSTEM_DHIS2_FHIR_QUESTIONNAIRE_R_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/questionnaire-response-identifier',
        'DHIS2 FHIR Adapter Questionnaire Response Identifier.');

INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('69dfbae1-5873-4a94-862a-ba7344c383d7', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'PLAN_DEFINITION', 'f79b9892-2b43-48af-9a82-3ba03642bac4');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('7af9d58d-0393-411e-aa7f-e5891418f9f2', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'QUESTIONNAIRE', '296576d0-31b5-4427-8bf0-7cc168fb3a82');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('0340bf44-1eab-4c2a-98a2-aaf7ba35855c', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'CARE_PLAN', '9c5584f0-8b06-44ee-a18f-6addf938969f');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('789a91bd-6bcb-4f84-96d0-c6829b5fdeaa', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'QUESTIONNAIRE_RESPONSE', '5bdff628-d94f-43dc-8367-8c84fe48bdd9');

CREATE TABLE IF NOT EXISTS fhir_program_metadata_rule (
  id                              UUID         NOT NULL,
  CONSTRAINT fhir_program_metadata_rule_pk PRIMARY KEY (id),
  CONSTRAINT fhir_program_metadata_rule_fk1 FOREIGN KEY (id) REFERENCES fhir_rule (id) ON DELETE CASCADE
);
COMMENT ON TABLE fhir_program_metadata_rule IS 'Contains rules for DHIS2 Program Metadata Resource Types.';
COMMENT ON COLUMN fhir_program_metadata_rule.id IS 'References the rule to which this Program Metadata rule belongs to.';

CREATE TABLE IF NOT EXISTS fhir_program_stage_metadata_rule (
  id                              UUID         NOT NULL,
  CONSTRAINT fhir_program_stage_metadata_rule_pk PRIMARY KEY (id),
  CONSTRAINT fhir_program_stage_metadata_rule_fk1 FOREIGN KEY (id) REFERENCES fhir_rule (id) ON DELETE CASCADE
);
COMMENT ON TABLE fhir_program_stage_metadata_rule IS 'Contains rules for DHIS2 Program Stage Metadata Resource Types.';
COMMENT ON COLUMN fhir_program_stage_metadata_rule.id IS 'References the rule to which this Program Metadata Stage rule belongs to.';

INSERT INTO fhir_rule(id, version, fhir_resource_type, dhis_resource_type, name, description, enabled, imp_enabled, exp_enabled, contained_allowed, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled, grouping, evaluation_order, simple_fhir_id)
VALUES('4a9ac195-858b-455e-b34d-c560e1855787', 0, 'PLAN_DEFINITION', 'PROGRAM_METADATA', 'Default DHIS2 Program Metadata to FHIR Plan Definition', 'Default rule that transforms a DHIS2 Program Metadata to a FHIR Care Plan.',
true, false, true, false, true, true, true, false, -2147483648, TRUE);
INSERT INTO fhir_program_metadata_rule(id) VALUES ('4a9ac195-858b-455e-b34d-c560e1855787');

INSERT INTO fhir_rule(id, version, fhir_resource_type, dhis_resource_type, name, description, enabled, imp_enabled, exp_enabled, contained_allowed, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled, grouping, evaluation_order, simple_fhir_id)
VALUES('1f97f85a-e4ea-46f1-bcde-77ea178226f4', 0, 'QUESTIONNAIRE', 'PROGRAM_STAGE_METADATA', 'Default DHIS2 Program Stage Metadata to FHIR Questionnaire', 'Default rule that transforms a DHIS2 Program Stage Metadata to a FHIR Questionnaire.',
true, false, true, false, true, true, true, false, -2147483648, TRUE);
INSERT INTO fhir_program_stage_metadata_rule(id) VALUES ('1f97f85a-e4ea-46f1-bcde-77ea178226f4');
