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

ALTER TABLE fhir_code_set_value DROP CONSTRAINT fhir_code_set_value_uk_code;
ALTER TABLE fhir_code_set_value ADD CONSTRAINT fhir_code_set_value_uk_code UNIQUE (code_set_id, code_id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE fhir_executable_script_argument DROP CONSTRAINT fhir_executable_script_argument_uk1;
ALTER TABLE fhir_executable_script_argument ADD CONSTRAINT fhir_executable_script_argument_uk1 UNIQUE (executable_script_id, script_argument_id) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE fhir_code DROP CONSTRAINT fhir_code_uk_name;
ALTER TABLE fhir_code ADD CONSTRAINT fhir_code_uk_name UNIQUE (name)  DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE fhir_code DROP CONSTRAINT fhir_code_uk_code;
ALTER TABLE fhir_code ADD CONSTRAINT fhir_code_uk_code UNIQUE (code)  DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE fhir_code_set DROP CONSTRAINT fhir_code_set_uk_name;
ALTER TABLE fhir_code_set ADD CONSTRAINT fhir_code_set_uk_name UNIQUE (name)  DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE fhir_code_set DROP CONSTRAINT fhir_code_set_uk_code;
ALTER TABLE fhir_code_set ADD CONSTRAINT fhir_code_set_uk_code UNIQUE (code)  DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE fhir_script DROP CONSTRAINT fhir_script_u1;
ALTER TABLE fhir_script ADD CONSTRAINT fhir_script_u1 UNIQUE (code)  DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE fhir_executable_script DROP CONSTRAINT fhir_executable_script_uk_name;
ALTER TABLE fhir_executable_script ADD CONSTRAINT fhir_executable_script_uk_name UNIQUE (name)  DEFERRABLE INITIALLY DEFERRED;
ALTER TABLE fhir_executable_script DROP CONSTRAINT fhir_executable_script_uk_code;
ALTER TABLE fhir_executable_script ADD CONSTRAINT fhir_executable_script_uk_code UNIQUE (code)  DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE fhir_tracker_program DROP CONSTRAINT fhir_tracker_program_uk_name;
ALTER TABLE fhir_tracker_program ADD CONSTRAINT fhir_tracker_program_uk_name UNIQUE (name)  DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE fhir_tracker_program_stage DROP CONSTRAINT fhir_tracker_program_stage_uk_name;
ALTER TABLE fhir_tracker_program_stage ADD CONSTRAINT fhir_tracker_program_stage_uk_name UNIQUE (name)  DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE fhir_tracker_program DROP CONSTRAINT fhir_tracker_program_uk_program;
ALTER TABLE fhir_tracker_program ADD CONSTRAINT fhir_tracker_program_uk_program UNIQUE (program_ref)  DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE fhir_rule_dhis_data_ref DROP CONSTRAINT fhir_rule_dhis_data_ref_uk1;
ALTER TABLE fhir_rule_dhis_data_ref ADD CONSTRAINT fhir_rule_dhis_data_ref_uk1 UNIQUE (rule_id, data_ref) DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE fhir_rule DROP CONSTRAINT fhir_rule_uk_name;
ALTER TABLE fhir_rule ADD CONSTRAINT fhir_rule_uk_name UNIQUE (name)  DEFERRABLE INITIALLY DEFERRED;

UPDATE fhir_script SET output_type=NULL WHERE id='7db528c3-4e08-4f1b-ab20-4c02d499d41e';

UPDATE fhir_script SET input_type='FHIR_IMMUNIZATION', output_type='DHIS_EVENT', script_type='TRANSFORM_TO_DHIS' WHERE id='6c0f8f8c-59d8-4829-b930-39780d11277f';
UPDATE fhir_script SET input_type='FHIR_OBSERVATION', output_type='DHIS_EVENT', script_type='TRANSFORM_TO_DHIS' WHERE id='d934d281-79f5-4ba9-846c-a9540b7031d6';

UPDATE fhir_executable_script_argument SET script_argument_id='3d15bf81-343c-45bc-9c28-1e87a8da6fa5' WHERE id='8e23e35a-69a1-4416-b753-6dd29b07633a';
