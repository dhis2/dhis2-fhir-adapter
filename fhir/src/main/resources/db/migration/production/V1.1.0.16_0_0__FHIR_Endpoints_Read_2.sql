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

INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('dbc68848-3713-46f7-8c5b-5d0f12f091b9', 0, 'DHIS2 FHIR Adapter Condition Identifier', 'SYSTEM_DHIS2_FHIR_CONDITION_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/condition-identifier',
        'DHIS2 FHIR Adapter Condition Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('06bc5bb6-116a-4a75-9b67-7c64a29f99b8', 0, 'DHIS2 FHIR Adapter Diagnostic Report Identifier', 'SYSTEM_DHIS2_FHIR_DIAGNOSTIC_REPORT_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/diagnostic-report-identifier',
        'DHIS2 FHIR Adapter Diagnostic Report Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('84d4ee66-6bd2-4c31-a0ea-d0cb1fcc0837', 0, 'DHIS2 FHIR Adapter Encounter Identifier', 'SYSTEM_DHIS2_FHIR_ENCOUNTER_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/encounter-identifier',
        'DHIS2 FHIR Adapter Organization Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('ce63a6d6-a005-4d64-8d0d-d91c93c994fd', 0, 'DHIS2 FHIR Adapter Immunization Identifier', 'SYSTEM_DHIS2_FHIR_IMMUNIZATION_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/immunization-identifier',
        'DHIS2 FHIR Adapter Immunization Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('36bfe529-5e5f-4f81-86d1-586e6ae76372', 0, 'DHIS2 FHIR Adapter Medication Request Identifier', 'SYSTEM_DHIS2_FHIR_MEDICATION_REQUEST_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/medication-request-identifier',
        'DHIS2 FHIR Adapter Medication Request Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('e10dca6e-1171-4da1-bc2d-781ef2764aa8', 0, 'DHIS2 FHIR Adapter Observation Identifier', 'SYSTEM_DHIS2_FHIR_OBSERVATION_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/observation-identifier',
        'DHIS2 FHIR Adapter Observation Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('64beefd5-3240-4bcb-a40d-f1b61a62d27b', 0, 'DHIS2 FHIR Adapter Related Person Identifier', 'SYSTEM_DHIS2_FHIR_RELATED_PERSON_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/related-person-identifier',
        'DHIS2 FHIR Adapter Related Person Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('77fd2a92-4761-420b-9db0-5b3999d8ef30', 0, 'DHIS2 FHIR Adapter Practitioner Identifier', 'SYSTEM_DHIS2_FHIR_PRACTITIONER_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/practitioner-identifier',
        'DHIS2 FHIR Adapter Practitioner Identifier.');

INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('f720eb06-8645-4586-9510-b01f8ce281a0', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'CONDITION', 'dbc68848-3713-46f7-8c5b-5d0f12f091b9');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('8f3bede5-ca92-4bcd-ae36-03c52f9aa2da', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'DIAGNOSTIC_REPORT', '06bc5bb6-116a-4a75-9b67-7c64a29f99b8');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('b19b210e-98d1-412d-b983-af75366c2796', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'ENCOUNTER', '84d4ee66-6bd2-4c31-a0ea-d0cb1fcc0837');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('a52778e9-a00d-44a7-a629-3cefd373ce11', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'IMMUNIZATION', 'ce63a6d6-a005-4d64-8d0d-d91c93c994fd');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('92dd859b-5933-4123-8550-8455cd329394', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'MEDICATION_REQUEST', '36bfe529-5e5f-4f81-86d1-586e6ae76372');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('77d278dc-01e1-437e-99c0-e2b2e9bd78cd', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'OBSERVATION', 'e10dca6e-1171-4da1-bc2d-781ef2764aa8');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('c99b20ff-9854-443b-bbd2-97072e1f9453', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'RELATED_PERSON', '64beefd5-3240-4bcb-a40d-f1b61a62d27b');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('965fdc22-3b2e-4a81-9762-b8f8871ff0ee', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'PRACTITIONER', '77fd2a92-4761-420b-9db0-5b3999d8ef30');

INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('9271ae78-a2c7-4630-be2a-2e141dbc4e7f', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'CONDITION', 'dbc68848-3713-46f7-8c5b-5d0f12f091b9');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('d2c407a9-d03e-4140-b459-3909500372f4', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'DIAGNOSTIC_REPORT', '06bc5bb6-116a-4a75-9b67-7c64a29f99b8');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('2dfbc117-c31c-4b41-819d-e38624d8c262', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'ENCOUNTER', '84d4ee66-6bd2-4c31-a0ea-d0cb1fcc0837');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('c26387c4-82ac-4a80-b20a-b8882e107590', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'IMMUNIZATION', 'ce63a6d6-a005-4d64-8d0d-d91c93c994fd');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('235aaaaa-0f6d-454a-9de5-32b975329997', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'MEDICATION_REQUEST', '36bfe529-5e5f-4f81-86d1-586e6ae76372');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('feceaac4-5b00-4f42-8e1b-dd629f721cc9', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'OBSERVATION', 'e10dca6e-1171-4da1-bc2d-781ef2764aa8');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('5ae99120-6dd2-483a-8e64-17858803f4e1', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'RELATED_PERSON', '64beefd5-3240-4bcb-a40d-f1b61a62d27b');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('ccd13c8f-2d79-4965-b7be-36d59561451d', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'PRACTITIONER', '77fd2a92-4761-420b-9db0-5b3999d8ef30');

ALTER TABLE fhir_resource_mapping
  ADD COLUMN tracked_entity_fhir_resource_type VARCHAR(30) DEFAULT 'PATIENT' NOT NULL,
  ADD CONSTRAINT fhir_resource_mapping_fk17 FOREIGN KEY (tracked_entity_fhir_resource_type) REFERENCES fhir_resource_type_enum(value);
COMMENT ON COLUMN fhir_resource_mapping.tracked_entity_fhir_resource_type IS 'The FHIR resource type of the DHIS tracked entity type.';
ALTER TABLE fhir_resource_mapping DROP CONSTRAINT fhir_resource_mapping_uk_fhir;
ALTER TABLE fhir_resource_mapping ADD CONSTRAINT fhir_resource_mapping_uk_fhir UNIQUE (fhir_resource_type, tracked_entity_fhir_resource_type);
