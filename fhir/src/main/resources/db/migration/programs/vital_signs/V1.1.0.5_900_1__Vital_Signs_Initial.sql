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

-- @formatter:off

INSERT INTO fhir_script (id,version,name,code,description,script_type,return_type,input_type,output_type)
VALUES ('20e1ac3f-f49c-4fa8-93e2-b5e18c5f9665', 0, 'Vital Signs New Program Stage', 'VITAL_SIGN_NEW_PS', 'Decides about creation of a new program stage for vital signs.', 'EVALUATE', 'EVENT_DECISION_TYPE', NULL, 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('20e1ac3f-f49c-4fa8-93e2-b5e18c5f9665', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('20e1ac3f-f49c-4fa8-93e2-b5e18c5f9665', 'PROGRAM_STAGE_EVENTS');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('20e1ac3f-f49c-4fa8-93e2-b5e18c5f9665', 'DATE_TIME');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('09db6430-cfb2-4861-befb-af7408e35c2e', 0, '20e1ac3f-f49c-4fa8-93e2-b5e18c5f9665',
        'programStageUtils.containsEventDay(programStageEvents, dateTime) ? ''CONTINUE'' : ''NEW_EVENT''', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('09db6430-cfb2-4861-befb-af7408e35c2e', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('5f1b7d98-48ad-4df4-bdb2-23e7de8f0fc1', 0, '20e1ac3f-f49c-4fa8-93e2-b5e18c5f9665',
'Vital Signs New Program Stage', 'VITAL_SIGN_NEW_PS', 'Decides about creation of a new program stage for vital signs.');

INSERT INTO fhir_tracker_program (id,version,name,program_ref,tracked_entity_rule_id,enabled,creation_enabled,enrollment_date_is_incident,fhir_create_enabled,fhir_update_enabled,fhir_delete_enabled)
VALUES ('fe122800-6207-4071-9b1f-161e2844c72f', 0, 'Vital Signs', 'NAME:Vital Signs', '5f9ebdc9-852e-4c83-87ca-795946aabc35', TRUE, TRUE, TRUE, TRUE, TRUE, TRUE);
INSERT INTO fhir_tracker_program_stage (id,version,name,program_stage_ref,program_id,enabled,creation_enabled,event_date_is_incident,before_script_id,fhir_create_enabled,fhir_update_enabled,fhir_delete_enabled)
VALUES ('eca2b04a-54e8-4b75-95fa-8035f7477ed8', 0, 'Daily Vital Signs', 'NAME:Daily Vital Signs', 'fe122800-6207-4071-9b1f-161e2844c72f', TRUE, TRUE, FALSE, '5f1b7d98-48ad-4df4-bdb2-23e7de8f0fc1', TRUE, TRUE, TRUE);

INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, imp_enabled, transform_imp_script_id, transform_exp_script_id, fhir_create_enabled, fhir_update_enabled, exp_enabled, applicable_code_set_id)
VALUES ('75b9786f-8762-4c54-a5a5-bdae358a62e5', 0, 'Daily Vital Signs: Weight', NULL, TRUE, 1, 'OBSERVATION', 'PROGRAM_STAGE_EVENT', TRUE, '07febf71-a102-408e-8c59-26d2a6536a39', 'ddc7c612-aabb-4ec4-bd63-6d5220cf666f', TRUE, TRUE, TRUE, 'd37dfecb-ce88-4fa4-9a78-44ffe874c140');
INSERT INTO fhir_program_stage_rule (id, program_stage_id, event_creation_enabled, enrollment_creation_enabled)
VALUES ('75b9786f-8762-4c54-a5a5-bdae358a62e5', 'eca2b04a-54e8-4b75-95fa-8035f7477ed8', true, true);
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
VALUES('7edd49b3-e324-43cf-858b-e909d97bccf1', 0, '75b9786f-8762-4c54-a5a5-bdae358a62e5', 'CODE:VS_982701', 'dataElement', true);

INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, imp_enabled, transform_imp_script_id, transform_exp_script_id, fhir_create_enabled, fhir_update_enabled, exp_enabled,
                       applicable_code_set_id)
VALUES ('0035e8b1-99e0-4ced-acf7-726ab26d9483', 0, 'Daily Vital Signs: Height', NULL, TRUE, 1, 'OBSERVATION', 'PROGRAM_STAGE_EVENT', TRUE, '158d4ec6-d9cf-4381-9d2c-4ddb9691f8f2', 'e859a64f-349d-4e67-bba0-f2460e61fb6e', TRUE, TRUE, TRUE, 'ac0af564-c3ee-4979-9ac3-d36b04ddb73d');
INSERT INTO fhir_program_stage_rule (id, program_stage_id, event_creation_enabled, enrollment_creation_enabled)
VALUES ('0035e8b1-99e0-4ced-acf7-726ab26d9483', 'eca2b04a-54e8-4b75-95fa-8035f7477ed8', true, true);
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
VALUES('d2751686-cc9f-4bc6-9f44-068fe6d806f3', 0, '0035e8b1-99e0-4ced-acf7-726ab26d9483', 'CODE:VS_982702', 'dataElement', true);
