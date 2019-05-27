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

INSERT INTO fhir_data_type_enum VALUES ('CODE_SET');

INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('c5a46ecc-477e-4a80-9499-b168e45568f5', 0, 'HL7 Act Reason V3', 'SYSTEM_HL7_V3_ACT_REASON', 'http://terminology.hl7.org/CodeSystem/v3-ActReason','HL7 Act Reason V3.');

-- Script that checks for a specific Immunization dose sequence
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('7db528c3-4e08-4f1b-ab20-4c02d499d41e', 0, 'EVAL_IMMUNIZATION_DOSE_SEQUENCE', 'Evaluates the immunization dose sequence for FHIR to DHIS2', 'Evaluates the immunization dose sequence for FHIR to DHIS2.',
'EVALUATE', 'BOOLEAN', NULL, 'FHIR_IMMUNIZATION', 'f18acd12-bc85-4f79-935d-353904eadc0b');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('7db528c3-4e08-4f1b-ab20-4c02d499d41e', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('7db528c3-4e08-4f1b-ab20-4c02d499d41e', 'INPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('11de0c23-d6c8-4398-a3c2-745468e1b9ea', 0, '7db528c3-4e08-4f1b-ab20-4c02d499d41e',
'doseSequence', 'INTEGER', TRUE, NULL, 'The dose sequence that should be checked.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('c90c7239-03cd-4755-bdfe-2a64f8db3f46', 0, '7db528c3-4e08-4f1b-ab20-4c02d499d41e',
'immunizationUtils.getMaxDoseSequence(input)==args[''doseSequence''] || (args[''doseSequence'']==1 && immunizationUtils.getMaxDoseSequence(input)==0 && input.isNotGiven())', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('c90c7239-03cd-4755-bdfe-2a64f8db3f46', 'DSTU3');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('3a94551e-195e-45db-8587-188d30350eb1', 0, '7db528c3-4e08-4f1b-ab20-4c02d499d41e',
'immunizationUtils.getMaxDoseSequence(input)==args[''doseSequence''] || (args[''doseSequence'']==1 && immunizationUtils.getMaxDoseSequence(input)==0 && ' ||
 'input.getStatus()==fhirResourceUtils.resolveEnumValue(input, ''status'',''not-done''))', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('3a94551e-195e-45db-8587-188d30350eb1', 'R4');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a57e0632-c4f9-4555-a5f1-8ad7c3b92efc', 0, '7db528c3-4e08-4f1b-ab20-4c02d499d41e', 'Evaluates the immunization dose sequence 1 for FHIR to DHIS2', 'EVAL_IMMUNIZATION_DOSE_SEQUENCE_1',
        'Evaluates the immunization dose sequence 1 for FHIR to DHIS2. In case the vaccine has not been given also no included dose sequence is used to evaluate to true.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('86dd8f6c-f206-4af0-9c48-42652ad3972e', 'a57e0632-c4f9-4555-a5f1-8ad7c3b92efc', '11de0c23-d6c8-4398-a3c2-745468e1b9ea', '1');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('85399bc1-3345-488c-8534-ab7ab5ccd7b0', 0, '7db528c3-4e08-4f1b-ab20-4c02d499d41e', 'Evaluates the immunization dose sequence 2 for FHIR to DHIS2', 'EVAL_IMMUNIZATION_DOSE_SEQUENCE_2',
        'Evaluates the immunization dose sequence 2 for FHIR to DHIS2.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('6e0089a7-7c27-46dc-8e9b-4dc7ac9b8a60', '85399bc1-3345-488c-8534-ab7ab5ccd7b0', '11de0c23-d6c8-4398-a3c2-745468e1b9ea', '2');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('2839d57b-078d-4a42-baf3-a92ac51c6c94', 0, '7db528c3-4e08-4f1b-ab20-4c02d499d41e', 'Evaluates the immunization dose sequence 3 for FHIR to DHIS2', 'EVAL_IMMUNIZATION_DOSE_SEQUENCE_3',
        'Evaluates the immunization dose sequence 3 for FHIR to DHIS2.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('28b40727-3cbe-4a00-a7b1-5d41bbcc933a', '2839d57b-078d-4a42-baf3-a92ac51c6c94', '11de0c23-d6c8-4398-a3c2-745468e1b9ea', '3');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a80cccdf-48f8-46de-b83b-a1941ea98d13', 0, '7db528c3-4e08-4f1b-ab20-4c02d499d41e', 'Evaluates the immunization dose sequence 4 for FHIR to DHIS2', 'EVAL_IMMUNIZATION_DOSE_SEQUENCE_4',
        'Evaluates the immunization dose sequence 4 for FHIR to DHIS2.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('153ac98c-1ff3-487c-8b49-c96e0348c43e', 'a80cccdf-48f8-46de-b83b-a1941ea98d13', '11de0c23-d6c8-4398-a3c2-745468e1b9ea', '4');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('658f95c8-9ac4-49ae-a3dc-5bba116473db', 0, '7db528c3-4e08-4f1b-ab20-4c02d499d41e', 'Evaluates the immunization dose sequence 5 for FHIR to DHIS2', 'EVAL_IMMUNIZATION_DOSE_SEQUENCE_5',
        'Evaluates the immunization dose sequence 5 for FHIR to DHIS2.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('0b221df2-ba66-4cf2-bb0b-f1b8443b45d7', '658f95c8-9ac4-49ae-a3dc-5bba116473db', '11de0c23-d6c8-4398-a3c2-745468e1b9ea', '5');

-- Script that sets for a data element if a FHIR Immunization has been given and reason when FHIR Immunization has not been given
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('6c0f8f8c-59d8-4829-b930-39780d11277f', 0, 'TRANSFORM_IMMUNIZATION_REASON_F2D', 'Transforms immunization with reason from FHIR to DHIS2', 'Transforms immunization with reason from FHIR to DHIS2.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_IMMUNIZATION', 'f18acd12-bc85-4f79-935d-353904eadc0b');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6c0f8f8c-59d8-4829-b930-39780d11277f', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6c0f8f8c-59d8-4829-b930-39780d11277f', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6c0f8f8c-59d8-4829-b930-39780d11277f', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('ba14085f-03c3-4a78-adf8-5cc7ef26571e', 0, '6c0f8f8c-59d8-4829-b930-39780d11277f',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element with Y/N for given/not given.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('9746a3e7-8030-401b-9e0c-aecd0f660c3e', 0, '6c0f8f8c-59d8-4829-b930-39780d11277f',
'dataElement2', 'DATA_ELEMENT_REF', FALSE, NULL, 'Data element with dropdown where reason can be specified why vaccine has not been given.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('a409c611-0a91-4a3a-a038-73dafe5505d8', 0, '6c0f8f8c-59d8-4829-b930-39780d11277f',
'reasonCodeSetCode', 'CODE_SET', FALSE, 'VACCINE_NOT_GIVEN_REASON', 'Code of code set that contains reasons why vaccine has not been given.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('12efce0c-ecaa-4142-832b-636ddb4bd98b', 0, '6c0f8f8c-59d8-4829-b930-39780d11277f',
'var providedElsewhere = input.hasPrimarySource() ? !input.isPrimarySource() : null;
var notGiven = input.notGiven;
output.setValue(args[''dataElement''], !notGiven, providedElsewhere);
if (args[''dataElement2''] != null && args[''reasonCodeSetCode''] != null)
{
  if (notGiven && input.statusReason !== ''undefined'')
  {
    output.setValue(args[''dataElement2''], codeUtils.getMappedValueSetCode(args[''reasonCodeSetCode''], input.statusReason), providedElsewhere);
  }
  else
  {
    output.setValue(args[''dataElement2''], null, providedElsewhere);
  }
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('12efce0c-ecaa-4142-832b-636ddb4bd98b', 'DSTU3');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('cb2c0544-c0c0-4e5c-9a36-5f9e8eb1059c', 0, '6c0f8f8c-59d8-4829-b930-39780d11277f',
'var providedElsewhere = input.hasPrimarySource() ? !input.isPrimarySource() : null;
var notGiven = (input.getStatus() == fhirResourceUtils.resolveEnumValue(input, ''status'', ''not-done''));
output.setValue(args[''dataElement''], !notGiven, providedElsewhere);
if (args[''dataElement2''] != null && args[''reasonCodeSetCode''] != null)
{
  if (notGiven && input.statusReason !== ''undefined'')
  {
    output.setValue(args[''dataElement2''], codeUtils.getMappedValueSetCode(args[''reasonCodeSetCode''], input.statusReason), providedElsewhere);
  }
  else
  {
    output.setValue(args[''dataElement2''], null, providedElsewhere);
  }
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('cb2c0544-c0c0-4e5c-9a36-5f9e8eb1059c', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('eeba194b-37e8-4686-8836-a90d41695d21', 0, '6c0f8f8c-59d8-4829-b930-39780d11277f', 'Transforms immunization with reason from FHIR to DHIS2', 'TRANSFORM_IMMUNIZATION_REASON_F2D',
        'Transforms immunization with reason from FHIR to DHIS2.');
