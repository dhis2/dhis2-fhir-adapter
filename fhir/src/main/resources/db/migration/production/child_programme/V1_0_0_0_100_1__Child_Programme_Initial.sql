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

-- Script that is executed to check if enrollment into Child Programme is applicable
INSERT INTO fhir_script (id,version,name,code,description,script_type,return_type,input_type,output_type)
VALUES ('3ddfc83e-0655-46db-8111-0a3370970125', 0, 'Child Programme Creation Applicable', 'CHILD_PROGRAMME_CREATION_APPLICABLE',
'Checks if the enrollment into child programme is applicable. The enrollment is applicable if the person is younger than a defined amount time.',
'EVALUATE', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('3ddfc83e-0655-46db-8111-0a3370970125', 'TRACKED_ENTITY_INSTANCE');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('3ddfc83e-0655-46db-8111-0a3370970125', 'DATE_TIME');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('b06c9036-006b-4043-9c6f-57dd7286098b', 0, '3ddfc83e-0655-46db-8111-0a3370970125',
'birthDateAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, FALSE, 'NAME:Birth date', 'The reference of the tracked entity attribute that contains the birth date of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('8cddd857-d510-472c-835a-d9454ffe1d39', 0, '3ddfc83e-0655-46db-8111-0a3370970125',
'age', 'INTEGER', TRUE, FALSE, '1', 'The person must be younger than the this amount of time (in specified units).');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('331db7c9-edd8-4ff5-9e77-40c391aa789e', 0, '3ddfc83e-0655-46db-8111-0a3370970125',
'ageUnit', 'DATE_UNIT', TRUE, FALSE, 'YEARS', 'The unit in which the age is specified.');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('1f9dc84d-29a5-4460-9192-eb03cea156d9', 0, '3ddfc83e-0655-46db-8111-0a3370970125', 'dateTimeUtils.isYoungerThan(dateTime, trackedEntityInstance.getValue(args[''birthDateAttribute'']), args[''age''], args[''ageUnit''])', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('1f9dc84d-29a5-4460-9192-eb03cea156d9', 'DSTU3');
INSERT INTO fhir_executable_script (id,script_id, name, code, description)
VALUES ('dfb7f13c-ae21-4cfb-8815-6f1416ca8388', '3ddfc83e-0655-46db-8111-0a3370970125',
'Child Programme Creation Applicable', 'CHILD_PROGRAMME_CREATION_APPLICABLE', 'Checks if the enrollment of a person is applicable.');

-- Script that is executed on creation of a program instance of Child Programme
INSERT INTO fhir_script (id,version,name,code,description,script_type,return_type,input_type,output_type)
VALUES ('60e83e18-5f66-40a1-9c9d-1d993e5ccdb3', 0, 'Child Programme Creation', 'CHILD_PROGRAMME_CREATION', 'Child programme creation.', 'EVALUATE', 'BOOLEAN', NULL, 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('60e83e18-5f66-40a1-9c9d-1d993e5ccdb3', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('60e83e18-5f66-40a1-9c9d-1d993e5ccdb3', 'ENROLLMENT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('363ed415-be4a-4311-a7bf-780a304d6f8c', 0, '60e83e18-5f66-40a1-9c9d-1d993e5ccdb3',
'birthDateAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, FALSE, 'NAME:Birth date', 'The reference of the tracked entity attribute that contains the birth date of the Person.');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('f45aed60-5208-430e-869e-f0b87f5a6321', 0, '60e83e18-5f66-40a1-9c9d-1d993e5ccdb3', 'enrollment.setIncidentDate(trackedEntityInstance.getValue(args[''birthDateAttribute'']))', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('f45aed60-5208-430e-869e-f0b87f5a6321', 'DSTU3');
INSERT INTO fhir_executable_script (id,script_id, name, code, description)
VALUES ('6836cbd1-93ba-4ab8-a4cb-69348ad9d099', '60e83e18-5f66-40a1-9c9d-1d993e5ccdb3',
'Child Programme Creation', 'CHILD_PROGRAMME_CREATION', 'Child programme creation.');

-- Tracker Program Child Programme
INSERT INTO fhir_tracker_program (id,version,name,program_ref,tracked_entity_rule_id,enabled,creation_enabled,creation_applicable_script_id,creation_script_id)
VALUES ('45e61665-754d-4861-891e-a2064fc0ae7d', 0, 'Child Programme', 'NAME:Child Programme', '5f9ebdc9-852e-4c83-87ca-795946aabc35', TRUE, TRUE,
'dfb7f13c-ae21-4cfb-8815-6f1416ca8388', '6836cbd1-93ba-4ab8-a4cb-69348ad9d099');
-- Tracker Program Child Programme, Stage Birth
INSERT INTO fhir_tracker_program_stage (id,version,name,program_stage_ref,program_id,enabled,creation_enabled)
VALUES ('4c074c85-be49-4b9d-8973-9e16b9615dad', 0, 'Birth', 'NAME:Birth', '45e61665-754d-4861-891e-a2064fc0ae7d', TRUE, TRUE);
-- Tracker Program Child Programme, Stage Baby Postnatal
INSERT INTO fhir_tracker_program_stage (id,version,name,program_stage_ref,program_id,enabled,creation_enabled)
VALUES ('526b4e01-7747-47ef-a25d-f32ccd739e87', 0, 'Baby Postnatal', 'NAME:Baby Postnatal', '45e61665-754d-4861-891e-a2064fc0ae7d', TRUE, TRUE);

-- Tracker Program Child Programme, Birth: OPV Dose
INSERT INTO fhir_executable_script (id, script_id, code, name)
VALUES ('1a2950cf-0842-4dd3-9453-284fb08789d3', 'f18acd12-bc85-4f79-935d-353904eadc0b', 'CP: OPV Dose', 'CP_OPV_DOSE');
INSERT INTO fhir_executable_script_argument(id, version, executable_script_id, script_argument_id, override_value)
VALUES ('4a8ba215-10e9-46f2-921f-da3973836119', 0, '1a2950cf-0842-4dd3-9453-284fb08789d3', '44134ba8-d77f-4c4d-90c6-b434ffbe7958', 'CODE:DE_2006104');

-- Rule Tracker Program Child Programme, Birth: OPV Dose
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, applicable_in_script_id, transform_in_script_id)
VALUES ('91ae6f5f-db07-4391-97cd-407a77794a1b', 0, 'Child Programme: OPV', NULL, TRUE, 0, 'IMMUNIZATION', 'PROGRAM_STAGE_EVENT', 'd6a9cf3e-10a3-4d1c-951c-c677905f89ed', '1a2950cf-0842-4dd3-9453-284fb08789d3');
INSERT INTO fhir_program_stage_rule (id, program_stage_id)
VALUES ('91ae6f5f-db07-4391-97cd-407a77794a1b','4c074c85-be49-4b9d-8973-9e16b9615dad');

-- Tracker Program Child Programme, Birth: BCG Dose
INSERT INTO fhir_executable_script (id, script_id, code, name)
VALUES ('748b12fa-37e4-4276-9ff5-d775f3560dcd', '3487e94d-0525-44c5-a2df-ebb00a398e94', 'CP: BCG Dose', 'CP_BCG_DOSE');
INSERT INTO fhir_executable_script_argument(id, version, executable_script_id, script_argument_id, override_value)
VALUES ('19df546e-7eb9-4384-87d1-162346d322fc', 0, '748b12fa-37e4-4276-9ff5-d775f3560dcd', 'b413837d-e3eb-45f0-8fe7-bc301f06ef31', 'CODE:DE_2006101');

-- Rule Tracker Program Child Programme, Birth: BCG Dose
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, applicable_in_script_id, transform_in_script_id)
VALUES ('9843c898-6a41-4388-ab6e-30503708ac91', 0, 'Child Programme: BCG', NULL, TRUE, 0, 'IMMUNIZATION', 'PROGRAM_STAGE_EVENT', 'c91a85ae-437b-455c-931d-0d64e0e4d29b', '748b12fa-37e4-4276-9ff5-d775f3560dcd');
INSERT INTO fhir_program_stage_rule (id, program_stage_id)
VALUES ('9843c898-6a41-4388-ab6e-30503708ac91','4c074c85-be49-4b9d-8973-9e16b9615dad');

-- Tracker Program Child Programme, Baby Postnatal: DPT Dose
INSERT INTO fhir_executable_script (id, script_id, code, name)
VALUES ('68dbdc96-da7f-40a9-a8d1-b4925d564292', 'f18acd12-bc85-4f79-935d-353904eadc0b', 'CP: DPT Dose', 'CP_DPT_DOSE');
INSERT INTO fhir_executable_script_argument(id, version, executable_script_id, script_argument_id, override_value)
VALUES ('528db27f-1927-4ae0-8326-b4e693349c52', 0, '68dbdc96-da7f-40a9-a8d1-b4925d564292', '44134ba8-d77f-4c4d-90c6-b434ffbe7958', 'CODE:DE_2006105');

-- Rule Tracker Program Child Programme, Baby Postnatal: DPT Dose
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, applicable_in_script_id, transform_in_script_id)
VALUES ('2f70e895-e238-40d3-ae0b-9e18e69792d6', 0, 'Child Programme: DPT', NULL, TRUE, 0, 'IMMUNIZATION', 'PROGRAM_STAGE_EVENT', 'ac0257b3-105d-4a63-85cc-7a2cc85b1130', '68dbdc96-da7f-40a9-a8d1-b4925d564292');
INSERT INTO fhir_program_stage_rule (id, program_stage_id)
VALUES ('2f70e895-e238-40d3-ae0b-9e18e69792d6','526b4e01-7747-47ef-a25d-f32ccd739e87');

-- Tracker Program Child Programme, Baby Postnatal: Measles given
INSERT INTO fhir_executable_script (id, script_id, code, name)
VALUES ('51ae5077-ddd7-45dc-b8ed-29000b9faaab', '3487e94d-0525-44c5-a2df-ebb00a398e94', 'CP: Measles given', 'CP_MEASLES_GIVEN');
INSERT INTO fhir_executable_script_argument(id, version, executable_script_id, script_argument_id, override_value)
VALUES ('86759369-63d9-4a2c-84f8-e9897f69166a', 0, '51ae5077-ddd7-45dc-b8ed-29000b9faaab', 'b413837d-e3eb-45f0-8fe7-bc301f06ef31', 'CODE:DE_2006125');

-- Rule Tracker Program Child Programme, Baby Postnatal: Measles
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, applicable_in_script_id, transform_in_script_id)
VALUES ('8019cebe-da61-4aff-a2fd-579a538c8671', 0, 'Child Programme: Measles', NULL, TRUE, 0, 'IMMUNIZATION', 'PROGRAM_STAGE_EVENT', '1f4e3e5e-1287-48fa-b4cd-e936f59cc169', '51ae5077-ddd7-45dc-b8ed-29000b9faaab');
INSERT INTO fhir_program_stage_rule (id, program_stage_id)
VALUES ('8019cebe-da61-4aff-a2fd-579a538c8671','526b4e01-7747-47ef-a25d-f32ccd739e87');

-- Tracker Program Child Programme, Baby Postnatal: Yellow Fever given
INSERT INTO fhir_executable_script (id, script_id, code, name)
VALUES ('4bcb1c80-d692-4141-b910-cb96f7853977', '3487e94d-0525-44c5-a2df-ebb00a398e94', 'CP: Yellow Fever given', 'CP_YELLOW_FEVER_GIVEN');
INSERT INTO fhir_executable_script_argument(id, version, executable_script_id, script_argument_id, override_value)
VALUES ('8bb6914e-5014-428b-a3fc-19e18e13df33', 0, '4bcb1c80-d692-4141-b910-cb96f7853977', 'b413837d-e3eb-45f0-8fe7-bc301f06ef31', 'CODE:DE_2006126');

-- Rule Tracker Program Child Programme, Baby Postnatal: Yellow Fever
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, applicable_in_script_id, transform_in_script_id)
VALUES ('9dd587e7-c7ca-4365-93f3-7263aa3cfb82', 0, 'Child Programme: Yellow Fever', NULL, TRUE, 0, 'IMMUNIZATION', 'PROGRAM_STAGE_EVENT', '6f3f556e-3bdb-4705-a962-5fc7d4e3cf66', '4bcb1c80-d692-4141-b910-cb96f7853977');
INSERT INTO fhir_program_stage_rule (id, program_stage_id)
VALUES ('9dd587e7-c7ca-4365-93f3-7263aa3cfb82','526b4e01-7747-47ef-a25d-f32ccd739e87');
