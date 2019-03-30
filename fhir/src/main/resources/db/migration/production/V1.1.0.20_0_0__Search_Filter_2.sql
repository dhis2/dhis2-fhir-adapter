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

UPDATE fhir_script_source SET source_text=
'searchFilter.add(''name'', ''string'', ''name'');
searchFilter.addReference(''partof'', ''location'', ''organization-unit'', ''parent.id'');
searchFilter.addToken(''status'', ''active'', ''closedDate'', ''null'', null);
searchFilter.addToken(''status'', ''inactive'', ''closedDate'', ''!null'', null);
true' WHERE id = '10200cc2-f060-4aa8-a907-0d12b199ff3b' AND version = 0;

UPDATE fhir_script_source SET source_text=
'searchFilter.add(''name'', ''string'', ''name'');
searchFilter.addReference(''partof'', ''organization'', ''organization-unit'', ''parent.id'');
searchFilter.addToken(''status'', ''true'', ''closedDate'', ''null'', null);
searchFilter.addToken(''status'', ''false'', ''closedDate'', ''!null'', null);
true' WHERE id = '1f1f0ce9-5b9b-4ce1-b882-4e40713314bf' AND version = 0;

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type)
VALUES ('ebf89e1e-ede9-408c-a523-c9912a72d376', 0, 'SEARCH_FILTER_ENCOUNTER', 'Prepares Encounter Search Filter', 'Prepares Encounter Search Filter.', 'SEARCH_FILTER', 'BOOLEAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ebf89e1e-ede9-408c-a523-c9912a72d376', 'SEARCH_FILTER');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('4ff5f488-2109-43d0-b44d-d9ab7241bc39', 0, 'ebf89e1e-ede9-408c-a523-c9912a72d376',
'searchFilter.addReference(''subject'', ''patient'', ''tracked-entity'', ''trackedEntityInstance'');
searchFilter.addReference(''location'', ''location'', ''organization-unit'', ''orgUnit'');
searchFilter.addToken(''status'', ''in-progress'', ''status'', ''eq'', ''ACTIVE'');
searchFilter.addToken(''status'', ''skipped'', ''status'', ''eq'', ''CANCELLED'');
searchFilter.addToken(''status'', ''finished'', ''status'', ''eq'', ''COMPLETED'');
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('4ff5f488-2109-43d0-b44d-d9ab7241bc39', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('4ff5f488-2109-43d0-b44d-d9ab7241bc39', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('bae38b4e-5d16-46e9-b579-a9a0900edb72', 0, 'ebf89e1e-ede9-408c-a523-c9912a72d376', 'Prepares Encounter Search Filter', 'SEARCH_FILTER_ENCOUNTER');
UPDATE fhir_rule SET filter_script_id='bae38b4e-5d16-46e9-b579-a9a0900edb72' WHERE fhir_resource_type='ENCOUNTER' and dhis_resource_type='PROGRAM_STAGE_EVENT' and filter_script_id IS NULL;

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type)
VALUES ('45767b71-2bb0-4a20-bbd7-9932ed0e6d83', 0, 'SEARCH_FILTER_OBSERVATION', 'Prepares Observation Search Filter', 'Prepares Observation Search Filter.', 'SEARCH_FILTER', 'BOOLEAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('45767b71-2bb0-4a20-bbd7-9932ed0e6d83', 'SEARCH_FILTER');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('a99c0dfc-0055-4aff-90e3-f313d0bb82a6', 0, '45767b71-2bb0-4a20-bbd7-9932ed0e6d83',
'searchFilter.addReference(''patient'', ''patient'', ''tracked-entity'', ''trackedEntityInstance'');
searchFilter.addReference(''subject'', ''patient'', ''tracked-entity'', ''trackedEntityInstance'');
searchFilter.addReference(''encounter'', ''encounter'', ''program-stage-event'', ''event'');
searchFilter.addToken(''status'', ''final'', ''status'', ''eq'', ''COMPLETED'');
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('a99c0dfc-0055-4aff-90e3-f313d0bb82a6', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('a99c0dfc-0055-4aff-90e3-f313d0bb82a6', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('a97e64a7-81d1-4f62-84f2-da9a003f9d0b', 0, '45767b71-2bb0-4a20-bbd7-9932ed0e6d83', 'Prepares Observation Search Filter', 'SEARCH_FILTER_OBSERVATION');
UPDATE fhir_rule SET filter_script_id='a97e64a7-81d1-4f62-84f2-da9a003f9d0b' WHERE fhir_resource_type='OBSERVATION' and dhis_resource_type='PROGRAM_STAGE_EVENT' and filter_script_id IS NULL;

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type)
VALUES ('8bf44a88-fbbe-4f2a-b42b-89d1da7751b6', 0, 'SEARCH_FILTER_IMMUNIZATION', 'Prepares Immunization Search Filter', 'Prepares Immunization Search Filter.', 'SEARCH_FILTER', 'BOOLEAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('8bf44a88-fbbe-4f2a-b42b-89d1da7751b6', 'SEARCH_FILTER');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('2da4f14b-3d09-4f12-bc2c-3b228d0a8f4e', 0, '8bf44a88-fbbe-4f2a-b42b-89d1da7751b6',
'searchFilter.addReference(''patient'', ''patient'', ''tracked-entity'', ''trackedEntityInstance'');
searchFilter.addReference(''encounter'', ''encounter'', ''program-stage-event'', ''event'');
searchFilter.addToken(''status'', ''final'', ''status'', ''eq'', ''COMPLETED'');
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('2da4f14b-3d09-4f12-bc2c-3b228d0a8f4e', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('2da4f14b-3d09-4f12-bc2c-3b228d0a8f4e', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('2e3c191f-8cf1-4a90-9876-4e9b0b6e4e8c', 0, '8bf44a88-fbbe-4f2a-b42b-89d1da7751b6', 'Prepares Immunization Search Filter', 'SEARCH_FILTER_IMMUNIZATION');
UPDATE fhir_rule SET filter_script_id='2e3c191f-8cf1-4a90-9876-4e9b0b6e4e8c' WHERE fhir_resource_type='IMMUNIZATION' and dhis_resource_type='PROGRAM_STAGE_EVENT' and filter_script_id IS NULL;

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, base_script_id)
VALUES ('6f1a456a-db09-4503-8bb6-9bba687f13b4', 0, 'SEARCH_FILTER_PATIENT', 'Prepares Patient Search Filter', 'Prepares Patient Search Filter.', 'SEARCH_FILTER', 'BOOLEAN', 'ea887943-5e94-4e31-9441-c7661fe1063e');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6f1a456a-db09-4503-8bb6-9bba687f13b4', 'SEARCH_FILTER');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('49def204-d98d-4b21-bd24-66aeda141592', 0, '6f1a456a-db09-4503-8bb6-9bba687f13b4',
'searchFilter.addReference(''organization'', ''organization'', ''organization-unit'', ''ou'');
searchFilter.add(''given'', ''string'', args[''firstNameAttribute'']);
searchFilter.add(''name'', ''string'', args[''lastNameAttribute'']);
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('49def204-d98d-4b21-bd24-66aeda141592', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('49def204-d98d-4b21-bd24-66aeda141592', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, base_executable_script_id)
VALUES ('a2b0c480-d4b1-4a30-abbc-44f720402d5a', 0, '6f1a456a-db09-4503-8bb6-9bba687f13b4', 'Prepares Patient Search Filter', 'SEARCH_FILTER_PATIENT', '72451c8f-7492-4707-90b8-a3e0796de19e');
UPDATE fhir_rule SET filter_script_id='a2b0c480-d4b1-4a30-abbc-44f720402d5a' WHERE fhir_resource_type='PATIENT' and dhis_resource_type='TRACKED_ENTITY'
and filter_script_id IS NULL and id = '5f9ebdc9-852e-4c83-87ca-795946aabc35';
