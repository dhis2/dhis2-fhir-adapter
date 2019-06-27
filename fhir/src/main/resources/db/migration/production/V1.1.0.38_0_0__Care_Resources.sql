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

CREATE TABLE IF NOT EXISTS fhir_enrollment_rule (
  id                              UUID         NOT NULL,
  program_ref_lookup_script_id        UUID NOT NULL,
  CONSTRAINT fhir_enrollment_rule_pk PRIMARY KEY (id),
  CONSTRAINT fhir_enrollment_rule_fk1 FOREIGN KEY (id) REFERENCES fhir_rule (id) ON DELETE CASCADE,
  CONSTRAINT fhir_enrollment_rule_fk3 FOREIGN KEY (program_ref_lookup_script_id) REFERENCES fhir_executable_script(id)
);
CREATE INDEX IF NOT EXISTS fhir_enrollment_rule_i1
  ON fhir_enrollment_rule (program_ref_lookup_script_id);
COMMENT ON TABLE fhir_enrollment_rule IS 'Contains rules for DHIS2 Enrollment Resource Types.';
COMMENT ON COLUMN fhir_enrollment_rule.id IS 'References the rule to which this Enrollment rule belongs to.';
COMMENT ON COLUMN fhir_enrollment_rule.program_ref_lookup_script_id IS 'References the executable script that returns the reference to a Tracker Program.';

INSERT INTO fhir_resource_type_enum VALUES('CARE_PLAN');
INSERT INTO fhir_resource_type_enum VALUES('QUESTIONNAIRE_RESPONSE');
INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_CARE_PLAN');
INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_QUESTIONNAIRE_RESPONSE');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type)
VALUES ('f638ba4c-8b53-11e9-89e4-e70cb21cbc8c', 0, 'CARE_PLAN_DATE_LOOKUP', 'Care Plan Date Lookup',
'Lookup of the exact date of the FHIR Care Plan.', 'EVALUATE', 'DATE_TIME', 'FHIR_CARE_PLAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f638ba4c-8b53-11e9-89e4-e70cb21cbc8c', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f638ba4c-8b53-11e9-89e4-e70cb21cbc8c', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('22469726-8b54-11e9-b95a-93563f739422', 0, 'f638ba4c-8b53-11e9-89e4-e70cb21cbc8c',
'input.getPeriod().getStart()', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('22469726-8b54-11e9-b95a-93563f739422', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('0f79f2c8-8b54-11e9-81f7-ebd621029975', 0, 'f638ba4c-8b53-11e9-89e4-e70cb21cbc8c', 'Care Plan TEI Lookup', 'CARE_PLAN_DATE_LOOKUP',
        'Lookup of the exact date of the FHIR Care Plan');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type)
VALUES ('44d63146-7500-4db7-b8bc-666c4898523b', 0, 'CARE_PLAN_PROGRAM_REF_LOOKUP', 'Care Plan Tracker Program Lookup',
'Lookup of the Tracker Program of the FHIR Care Plan.', 'EVALUATE', 'PROGRAM_REF', 'FHIR_CARE_PLAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('44d63146-7500-4db7-b8bc-666c4898523b', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('44d63146-7500-4db7-b8bc-666c4898523b', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('e13eeaef-7a6d-4083-a233-190bc8c1c975', 0, '44d63146-7500-4db7-b8bc-666c4898523b',
'var programRef = null; if (!input.getInstantiatesUri().isEmpty()) programRef = context.createReference(input.getInstantiatesUri().get(0).getValue(), ''id''); programRef', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('e13eeaef-7a6d-4083-a233-190bc8c1c975', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('79d29706-5c1c-47c5-8245-6069036072f8', 0, '44d63146-7500-4db7-b8bc-666c4898523b', 'Care Plan Tracker Program Lookup', 'CARE_PLAN_PROGRAM_REF_LOOKUP',
        'Lookup of the Tracker Program of the FHIR Care Plan.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('e885f646-6269-40c5-a812-6c2aa081e239', 0, 'DEFAULT_CARE_PLAN_F2D', 'Default FHIR Care Plan to DHIS2 Enrollment Transformation',
'Transforms FHIR Care Plan to DHIS2 Enrollment.', 'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_CARE_PLAN', 'DHIS_ENROLLMENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('e885f646-6269-40c5-a812-6c2aa081e239', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('e885f646-6269-40c5-a812-6c2aa081e239', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('e885f646-6269-40c5-a812-6c2aa081e239', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('383a5fd3-4890-41b5-93f3-6e00c14c2d43', 0, 'e885f646-6269-40c5-a812-6c2aa081e239',
'function getOutputStatus(input)
{
  var outputStatus = ''CANCELLED'';
  var inputStatus = input.getStatus() == null ? null : input.getStatus().toCode();
  if (inputStatus === ''draft'')
  {
    outputStatus = ''ACTIVE'';
  }
  else if (inputStatus === ''active'')
  {
    outputStatus = ''ACTIVE'';
  }
  else if (inputStatus === ''on-hold'')
  {
    outputStatus = ''ACTIVE'';
  }
  else if (inputStatus === ''revoked'')
  {
    outputStatus = ''CANCELLED'';
  }
  else if (inputStatus === ''completed'')
  {
    outputStatus = ''COMPLETED'';
  }
  else if (inputStatus === ''entered-in-error'')
  {
    outputStatus = ''CANCELLED'';
  }
  return outputStatus;
}
output.setStatus(getOutputStatus(input));
output.setIncidentDate(input.created);
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('383a5fd3-4890-41b5-93f3-6e00c14c2d43', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('aa956fdb-0107-4cb7-b5c1-ab6567162d9d', 0, 'e885f646-6269-40c5-a812-6c2aa081e239', 'Default FHIR Care Plan to DHIS2 Enrollment Transformation', 'DEFAULT_CARE_PLAN_F2D',
        'Transforms FHIR Care Plan to DHIS2 Enrollment.');

INSERT INTO fhir_resource_mapping(id, version, fhir_resource_type, tracked_entity_fhir_resource_type, imp_tei_lookup_script_id, imp_enrollment_org_lookup_script_id, imp_enrollment_date_lookup_script_id)
VALUES('b6650f00-8462-419b-b992-6775ca0a26cb', 0, 'CARE_PLAN', 'PATIENT', '762b4137-a98b-4b10-a0f5-629d93e23461', '25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', '0f79f2c8-8b54-11e9-81f7-ebd621029975');

INSERT INTO fhir_rule(id, version, fhir_resource_type, dhis_resource_type, name, description, enabled, imp_enabled, exp_enabled, contained_allowed, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled, grouping, evaluation_order, transform_imp_script_id)
VALUES('c4e17e7d-880e-45b5-9bc5-568da8c79742', 0, 'CARE_PLAN', 'ENROLLMENT', 'Default FHIR Care Plan to DHIS2 Enrollment', 'Default rule that transforms a FHIR Care Plan to a DHIS2 Enrollment.',
true, true, false, false, true, true, true, false, -2147483648, 'aa956fdb-0107-4cb7-b5c1-ab6567162d9d');
INSERT INTO fhir_enrollment_rule(id, program_ref_lookup_script_id) VALUES ('c4e17e7d-880e-45b5-9bc5-568da8c79742', '79d29706-5c1c-47c5-8245-6069036072f8');

UPDATE fhir_rule SET evaluation_order = 100 WHERE fhir_resource_type = 'PATIENT' AND evaluation_order = 0;
