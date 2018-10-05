/*
 *  Copyright (c) 2004-2018, University of Oslo
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

INSERT INTO fhir_system (id, version, name, code, system_uri)
VALUES ('c4e9ac6a-cc8f-4c73-aab6-0fa6775c0ca3', 0, 'Philippines Organization', 'SYSTEM_PH_ORGANIZATION', 'http://example.ph/organizations');
INSERT INTO fhir_system (id, version, name, code, system_uri)
VALUES ('ff842c76-a529-4563-972d-216b887a3573', 0, 'Philippines Patient', 'SYSTEM_PH_PATIENT', 'http://example.ph/national-patient-id');

INSERT INTO fhir_remote_subscription (id, version, name, code, description, fhir_version, web_hook_authorization_header, dhis_authorization_header, remote_base_url, tolerance_minutes, logging, verbose_logging)
VALUES ('73cd99c5-0ca8-42ad-a53b-1891fccce08f',
        0,
        'HAPI FHIR JPA Server',
        'SAMPLE',
        'HAPI FHIR JPA Server with sample data.',
        'DSTU3',
        '',
        'Basic YWRtaW46ZGlzdHJpY3Q=',
        'http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3', 1, TRUE, TRUE);

INSERT INTO fhir_remote_subscription_header (remote_subscription_id, name, value, secured)
VALUES ('73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'Authorization', 'Bearer jshru38jsHdsdfy38sh38H3d', TRUE);

INSERT INTO fhir_remote_subscription_resource (id, version, remote_subscription_id, fhir_resource_type, fhir_criteria_parameters, description)
VALUES ('667bfa41-867c-4796-86b6-eb9f9ed4dc94', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'PATIENT', '_format=json', 'Subscription for all Patients.');
INSERT INTO fhir_remote_subscription_resource (id, version, remote_subscription_id, fhir_resource_type, fhir_criteria_parameters, description)
VALUES ('a756ef2a-1bf4-43f4-a991-fbb48ad358ac', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'IMMUNIZATION', '_format=json', 'Subscription for all Immunizations.');
INSERT INTO fhir_remote_subscription_resource (id, version, remote_subscription_id, fhir_resource_type, fhir_criteria_parameters, description)
VALUES ('b32b4098-f8e1-426a-8dad-c5c4d8e0fab6', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'OBSERVATION', '_format=json', 'Subscription for all Observations.');

INSERT INTO fhir_remote_subscription_system (
  id           ,
  version                    ,
  remote_subscription_id       ,
  fhir_resource_type         ,
  system_id
) VALUES ('ea9804a3-9e82-4d0d-9cd2-e417b32b1c0c', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'ORGANIZATION', 'c4e9ac6a-cc8f-4c73-aab6-0fa6775c0ca3');

-- Script that returns boolean value true
INSERT INTO fhir_script (
  id      ,
  version,
  name            ,
  description     ,
  code           ,
  script_type   ,
  return_type  ,
  input_type  ,
  output_type
) VALUES ('5b37861d-9442-4e13-ac9f-88a893e91ce9', 0, 'True', 'Returns Boolean True.', 'TRUE', 'EVALUATE', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_source (
  id            ,
  version        ,
  script_id       ,
  source_text     ,
  source_type
) VALUES ('edcb402e-94b4-4953-8846-3a4d1c0dad6e', 0, '5b37861d-9442-4e13-ac9f-88a893e91ce9', 'true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (
  script_source_id ,
  fhir_version
) VALUES ('edcb402e-94b4-4953-8846-3a4d1c0dad6e', 'DSTU3');
INSERT INTO fhir_executable_script (
  id,
  script_id
) VALUES ('9299b82e-b90a-4542-8b78-200cadff3d7d', '5b37861d-9442-4e13-ac9f-88a893e91ce9');

-- Script that transforms Patient to Person
INSERT INTO fhir_script (
  id      ,
  version,
  name            ,
  description     ,
  code           ,
  script_type   ,
  return_type  ,
  input_type  ,
  output_type
) VALUES ('ea887943-5e94-4e31-9441-c7661fe1063e', 0, 'True', 'Transforms FHIR Patient to DHIS Person.', 'TRANSFORM_FHIR_PATIENT_DHIS_PERSON', 'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_PATIENT', 'DHIS_TRACKED_ENTITY_INSTANCE');
INSERT INTO fhir_script_source (
  id            ,
  version        ,
  script_id       ,
  source_text     ,
  source_type
) VALUES ('b2cfaf30-6ede-41f2-bd6c-448e76c429a1', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e', 'output.organizationUnitId = organizationUtils.getOrganizationUnitId( input.managingOrganization, ''http://example.ph/organizations'' ); output.setValueByName( ' ||
 '''National identifier'', identifierUtils.getIdentifier( input, ''http://example.ph/national-patient-id'' ) ); output.setValueByName( ''Last name'', humanNameUtils.getPrimaryName( input.name ).family ); output.setValueByName( ''First name'', humanNameUtils.getSingleGiven( humanNameUtils.getPrimaryName( input.name ) ) );  output.setValueByName( ''Birth date'', dateTimeUtils.getPreciseDate( input.birthDateElement ) );  output.setValueByName( ''Gender'', input.gender );  output.setValueByName( ''Address line'', addressUtils.getSingleLine( addressUtils.getPrimaryAddress( input.address ) ) );  output.setValueByName( ''City'', addressUtils.getPrimaryAddress( input.address ).city );  output.setValueByName( ''State of country'', addressUtils.getPrimaryAddress( input.address ).state );  output.setValueByName( ''Country'', addressUtils.getPrimaryAddress( input.address ).country );  output.coordinates = geoUtils.getLocation( addressUtils.getPrimaryAddress( input.address ) ); true;', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (
  script_source_id ,
  fhir_version
) VALUES ('b2cfaf30-6ede-41f2-bd6c-448e76c429a1', 'DSTU3');
INSERT INTO fhir_executable_script (
  id,
  script_id
) VALUES ('72451c8f-7492-4707-90b8-a3e0796de19e', 'ea887943-5e94-4e31-9441-c7661fe1063e');

-- Script that performs the lookup of TEI ID from FHIR Immunization
INSERT INTO fhir_script (
  id      ,
  version,
  name            ,
  description     ,
  code           ,
  script_type   ,
  return_type  ,
  input_type  ,
  output_type
) VALUES ('d4e2822a-4422-46a3-badc-cf5604c6e11f', 0, 'Immunization TEI Lookup', 'Lookup of the Tracked Entity Instance from FHIR Immunization.', 'IMMUNIZATION_TEI_LOOKUP', 'EVALUATE', 'STRING', NULL, NULL);
INSERT INTO fhir_script_source (
  id            ,
  version        ,
  script_id       ,
  source_text     ,
  source_type
) VALUES ('85b3c460-6c2a-4f50-af46-ff09bf2e69df', 0, 'd4e2822a-4422-46a3-badc-cf5604c6e11f', 'identifierUtils.getResourceId( input.patient, ''PATIENT'', ''http://example.ph/national-patient-id'' )', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (
  script_source_id ,
  fhir_version
) VALUES ('85b3c460-6c2a-4f50-af46-ff09bf2e69df', 'DSTU3');
INSERT INTO fhir_executable_script (
  id,
  script_id
) VALUES ('a08caa8a-1cc9-4f51-b6b8-814af781a442', 'd4e2822a-4422-46a3-badc-cf5604c6e11f');

INSERT INTO fhir_resource_mapping (
  id                             ,
  version                        ,
  fhir_resource_type             ,
  tei_lookup_script_id,
  enrollment_org_lookup_script_id,
  event_org_lookup_script_id
) VALUES ('44a6c99c-c83c-4061-acd2-39e4101de147', 0, 'IMMUNIZATION', 'a08caa8a-1cc9-4f51-b6b8-814af781a442', 'a08caa8a-1cc9-4f51-b6b8-814af781a442', 'a08caa8a-1cc9-4f51-b6b8-814af781a442');

-- Patient rule
INSERT INTO fhir_rule (
  id,
  version,
  name    ,
  description,
  enabled     ,
  evaluation_order,
  fhir_resource_type,
  dhis_resource_type ,
  applicable_in_script_id ,
  transform_in_script_id
) VALUES ('5f9ebdc9-852e-4c83-87ca-795946aabc35', 0, 'Patient to Person', NULL, TRUE, 0, 'PATIENT', 'TRACKED_ENTITY', '9299b82e-b90a-4542-8b78-200cadff3d7d', '72451c8f-7492-4707-90b8-a3e0796de19e');
INSERT INTO fhir_tracked_entity_rule (
  id                ,
  tracked_entity_ref
) VALUES ('5f9ebdc9-852e-4c83-87ca-795946aabc35', 'NAME:Person');

-- Child Programme
INSERT INTO fhir_tracker_program (
  id,
  version,
  name,
  program_ref,
  enabled)
VALUES ('45e61665-754d-4861-891e-a2064fc0ae7d', 0, 'Child Programme', 'NAME:Child Programme', TRUE);

-- Script that checks if FHIR Immunization is applicable for Child Programme Measles
INSERT INTO fhir_script (
  id      ,
  version,
  name            ,
  description     ,
  code           ,
  script_type   ,
  return_type  ,
  input_type  ,
  output_type
) VALUES ('ddaebe0a-8a88-4cb7-a5ca-632e86216be0', 0, 'Child Programme: Applicable Measles', 'Checks if FHIR Immunization is applicable for Measless in Child Programme', 'CHILD_PROGRAMME_APPLICABLE_MEASLES', 'EVALUATE', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_source (
  id            ,
  version        ,
  script_id       ,
  source_text     ,
  source_type
) VALUES ('2f04a7c3-7041-4c12-aa75-748862271818', 0, 'ddaebe0a-8a88-4cb7-a5ca-632e86216be0', '!input.notGiven && codeUtils.getCode( input.vaccineCode, ''http://example.ph/vaccine-codes'' ) == ''MMR''', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (
  script_source_id ,
  fhir_version
) VALUES ('2f04a7c3-7041-4c12-aa75-748862271818', 'DSTU3');
INSERT INTO fhir_executable_script (
  id,
  script_id
) VALUES ('1f4e3e5e-1287-48fa-b4cd-e936f59cc169', 'ddaebe0a-8a88-4cb7-a5ca-632e86216be0');

-- Script that transforms FHIR Immunization MMR to Child Programme Measles
INSERT INTO fhir_script (
  id      ,
  version,
  name            ,
  description     ,
  code           ,
  script_type   ,
  return_type  ,
  input_type  ,
  output_type
) VALUES ('3487e94d-0525-44c5-a2df-ebb00a398e94', 0, 'True', 'Transforms FHIR Immunization MMR to Child Programme Measles.', 'TRANSFORM_FHIR_IMMUNIZATION_MMR_CP_MEASLES', 'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_IMMUNIZATION', 'DHIS_EVENT');
INSERT INTO fhir_script_source (
  id            ,
  version        ,
  script_id       ,
  source_text     ,
  source_type
) VALUES ('fe03c526-a589-4b33-a095-3ee3ad7ddb9d', 0, '3487e94d-0525-44c5-a2df-ebb00a398e94', 'output.setValueByName( ''CP - MCH Measles dose'', true ); true;', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (
  script_source_id ,
  fhir_version
) VALUES ('fe03c526-a589-4b33-a095-3ee3ad7ddb9d', 'DSTU3');
INSERT INTO fhir_executable_script (
  id,
  script_id
) VALUES ('790fa636-0160-4109-bce3-1cd03a05368d', '3487e94d-0525-44c5-a2df-ebb00a398e94');

-- Immunization rule MMR
INSERT INTO fhir_rule (
  id,
  version,
  name    ,
  description,
  enabled     ,
  evaluation_order,
  fhir_resource_type,
  dhis_resource_type ,
  applicable_in_script_id ,
  transform_in_script_id
) VALUES ('8019cebe-da61-4aff-a2fd-579a538c8671', 0, 'Child Programme: Measles', NULL, TRUE, 0, 'IMMUNIZATION', 'PROGRAM_STAGE_EVENT', '1f4e3e5e-1287-48fa-b4cd-e936f59cc169', '790fa636-0160-4109-bce3-1cd03a05368d');
INSERT INTO fhir_program_stage_rule (
  id                ,
  program_id,
  program_stage_ref             ,
  enrollment_enabled           ,
  enrollment_id                ,
  creation_enabled             ,
  creation_applicable_script_id,
  creation_transform_script_id ,
  final_script_id
) VALUES ('8019cebe-da61-4aff-a2fd-579a538c8671','45e61665-754d-4861-891e-a2064fc0ae7d', 'NAME:Child Programme', TRUE, NULL, TRUE, NULL, NULL, NULL);
