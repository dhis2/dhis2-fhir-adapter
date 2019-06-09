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

UPDATE fhir_executable_script_argument SET override_value= '{#}' WHERE id = 'a5afcf00-92cd-4f00-ba5d-86c4cdbf28ab';

-- Script that checks for a specific Immunization dose sequence
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('aadf5ed0-a4da-40bd-9232-e2208c9bf9bb', 0, 'EVAL_OBS_IMMUNIZATION_DOSE_SEQUENCE', 'Evaluates the observation immunization dose sequence for FHIR to DHIS2', 'Evaluates the observation immunization dose sequence for FHIR to DHIS2.',
'EVALUATE', 'BOOLEAN', NULL, 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('aadf5ed0-a4da-40bd-9232-e2208c9bf9bb', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('aadf5ed0-a4da-40bd-9232-e2208c9bf9bb', 'INPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('b58eb1bc-6b61-4f1f-ac48-e39a035d1202', 0, 'aadf5ed0-a4da-40bd-9232-e2208c9bf9bb',
'doseSequence', 'INTEGER', TRUE, NULL, 'The dose sequence that should be checked.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('3153bea6-2ca9-4b18-8596-8bcdbbcfb2f2', 0, 'aadf5ed0-a4da-40bd-9232-e2208c9bf9bb',
'input.hasValueQuantity() && (input.getValueQuantity().getValueElement().getValue() != null) && (input.getValueQuantity().getValueElement().getValue() == args[''doseSequence''])', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('3153bea6-2ca9-4b18-8596-8bcdbbcfb2f2', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('3153bea6-2ca9-4b18-8596-8bcdbbcfb2f2', 'R4');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('bf8d3341-df24-4890-b409-8f6bc6cc3f7f', 0, 'aadf5ed0-a4da-40bd-9232-e2208c9bf9bb', 'Evaluates the observation immunization dose sequence 0 for FHIR to DHIS2', 'EVAL_OBS_IMMUNIZATION_DOSE_SEQUENCE_0',
        'Evaluates the immunization dose sequence 0 for FHIR to DHIS2. In case the vaccine has not been given also no included dose sequence is used to evaluate to true.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('b728650a-72fa-406a-bb22-a042bfa9a28d', 'bf8d3341-df24-4890-b409-8f6bc6cc3f7f', '11de0c23-d6c8-4398-a3c2-745468e1b9ea', '0');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('e4067361-f63a-4061-a541-94d1e711e4ea', 0, 'aadf5ed0-a4da-40bd-9232-e2208c9bf9bb', 'Evaluates the observation immunization dose sequence 1 for FHIR to DHIS2', 'EVAL_OBS_IMMUNIZATION_DOSE_SEQUENCE_1',
        'Evaluates the immunization dose sequence 1 for FHIR to DHIS2. In case the vaccine has not been given also no included dose sequence is used to evaluate to true.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('104f899c-6267-468e-bbb7-27b776b82280', 'e4067361-f63a-4061-a541-94d1e711e4ea', '11de0c23-d6c8-4398-a3c2-745468e1b9ea', '1');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('7608a9bd-2c4e-4ad4-8dad-2a82d51166e0', 0, 'aadf5ed0-a4da-40bd-9232-e2208c9bf9bb', 'Evaluates the observation immunization dose sequence 2 for FHIR to DHIS2', 'EVAL_OBS_IMMUNIZATION_DOSE_SEQUENCE_2',
        'Evaluates the immunization dose sequence 2 for FHIR to DHIS2.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('5107cbeb-3ec4-4f65-820c-da5a32dbdb2f', '7608a9bd-2c4e-4ad4-8dad-2a82d51166e0', '11de0c23-d6c8-4398-a3c2-745468e1b9ea', '2');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('bdd03b94-93f0-465f-b672-96969d34e5dd', 0, 'aadf5ed0-a4da-40bd-9232-e2208c9bf9bb', 'Evaluates the observation immunization dose sequence 3 for FHIR to DHIS2', 'EVAL_OBS_IMMUNIZATION_DOSE_SEQUENCE_3',
        'Evaluates the immunization dose sequence 3 for FHIR to DHIS2.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('769410ca-d15c-4121-b0ba-bc64bfa9c2ea', 'bdd03b94-93f0-465f-b672-96969d34e5dd', '11de0c23-d6c8-4398-a3c2-745468e1b9ea', '3');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a0ccc3ea-c872-45c5-86c2-7775da71e4b9', 0, 'aadf5ed0-a4da-40bd-9232-e2208c9bf9bb', 'Evaluates the observation immunization dose sequence 4 for FHIR to DHIS2', 'EVAL_OBS_IMMUNIZATION_DOSE_SEQUENCE_4',
        'Evaluates the immunization dose sequence 4 for FHIR to DHIS2.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('aed77a39-f0b1-4164-89c4-b326833c2522', 'a0ccc3ea-c872-45c5-86c2-7775da71e4b9', '11de0c23-d6c8-4398-a3c2-745468e1b9ea', '4');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('f565e4cb-2185-47b2-8a16-f23bbf039473', 0, 'aadf5ed0-a4da-40bd-9232-e2208c9bf9bb', 'Evaluates the observation immunization dose sequence 5 for FHIR to DHIS2', 'EVAL_OBS_IMMUNIZATION_DOSE_SEQUENCE_5',
        'Evaluates the immunization dose sequence 5 for FHIR to DHIS2.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('be572ba7-d415-4ff0-88e2-27eecc2606c6', 'f565e4cb-2185-47b2-8a16-f23bbf039473', '11de0c23-d6c8-4398-a3c2-745468e1b9ea', '5');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('344b66b9-ffdf-40e3-8a2a-421fc11ef8eb', 0, 'TRANSFORM_OBS_IMMUNIZATION_REASON_F2D', 'Transforms observation immunization with reason from FHIR to DHIS2',
'Transforms observation immunization with reason from FHIR to DHIS2. The first data element contains the data element to be set to true. ' ||
 'The second data element contains the reason why the vaccine has not be given (will be reset). And the remaining data elements will be reset (other available dose sequences).',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('344b66b9-ffdf-40e3-8a2a-421fc11ef8eb', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('344b66b9-ffdf-40e3-8a2a-421fc11ef8eb', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('344b66b9-ffdf-40e3-8a2a-421fc11ef8eb', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('a421ba23-a0d0-4daa-ad04-4e8f74b836a0', 0, '344b66b9-ffdf-40e3-8a2a-421fc11ef8eb',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element with Y/N for given/not given of that dose sequence. This data element will be set to true.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('bc84af68-cb91-4497-bed5-6eca4e8deb41', 0, '344b66b9-ffdf-40e3-8a2a-421fc11ef8eb',
'dataElement2', 'DATA_ELEMENT_REF', FALSE, NULL, 'Data element with dropdown where reason can be specified why vaccine has not been given. This data element will be reset.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('3fa19468-ae73-4e26-a699-df5d9b27fc25', 0, '344b66b9-ffdf-40e3-8a2a-421fc11ef8eb',
'dataElement3', 'DATA_ELEMENT_REF', FALSE, NULL, 'Data element with Y/N for given/not given of other dose sequence. This data element will be reset.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('d9f0bf95-c5ce-48fd-8f8f-22fee6d2de41', 0, '344b66b9-ffdf-40e3-8a2a-421fc11ef8eb',
'dataElement4', 'DATA_ELEMENT_REF', FALSE, NULL, 'Data element with Y/N for given/not given of other dose sequence. This data element will be reset.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('b8ccced0-0204-4227-a68f-cc77b5bfa079', 0, '344b66b9-ffdf-40e3-8a2a-421fc11ef8eb',
'dataElement5', 'DATA_ELEMENT_REF', FALSE, NULL, 'Data element with Y/N for given/not given of other dose sequence. This data element will be reset.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('98d32790-b504-4531-976a-def37bd8986f', 0, '344b66b9-ffdf-40e3-8a2a-421fc11ef8eb',
'dataElement6', 'DATA_ELEMENT_REF', FALSE, NULL, 'Data element with Y/N for given/not given of other dose sequence. This data element will be reset.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('cc32329d-bba1-4b68-8a25-f4120450e7b9', 0, '344b66b9-ffdf-40e3-8a2a-421fc11ef8eb',
'dataElement7', 'DATA_ELEMENT_REF', FALSE, NULL, 'Data element with Y/N for given/not given of other dose sequence. This data element will be reset.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('1ef93968-379a-4d28-80ca-1425d82093c2', 0, '344b66b9-ffdf-40e3-8a2a-421fc11ef8eb',
'dataElement8', 'DATA_ELEMENT_REF', FALSE, NULL, 'Data element with Y/N for given/not given of other dose sequence. This data element will be reset.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('44ab65d4-3d36-426a-a7b5-10332979b14c', 0, '344b66b9-ffdf-40e3-8a2a-421fc11ef8eb',
'for (var i = 2; i <= 8; i++)
{
  if (args[''dataElement'' + i] != null)
  {
    output.setValue(args[''dataElement'' + i], null);
  }
}
output.setValue(args[''dataElement''], true);
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('44ab65d4-3d36-426a-a7b5-10332979b14c', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('44ab65d4-3d36-426a-a7b5-10332979b14c', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('59da85b3-5e5b-4f24-b53c-963df4cb9293', 0, '344b66b9-ffdf-40e3-8a2a-421fc11ef8eb', 'Transforms observation immunization with reason from FHIR to DHIS2',
'TRANSFORM_OBS_IMMUNIZATION_REASON_F2D', 'Transforms observation immunization with reason from FHIR to DHIS2.');

UPDATE fhir_script_source SET source_text = 'input.hasValueQuantity() && (input.getValueQuantity().getValueElement().getValue() != null) && (input.getValueQuantity().getValueElement().getValue() == args[''doseSequence''])'
WHERE id='3153bea6-2ca9-4b18-8596-8bcdbbcfb2f2';
