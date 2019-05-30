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

-- Script that can transform simple observation values from FHIR to DHIS23
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('d934d281-79f5-4ba9-846c-a9540b7031d6', 0, 'TRANSFORM_OBSERVATION_SIMPLE_F2D', 'Transforms simple observation value from FHIR to DHIS2',
'Transforms simple (boolean, integer, string, date time) observation value from FHIR to DHIS2. If the value is a quantity the unit will not be checked.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d934d281-79f5-4ba9-846c-a9540b7031d6', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d934d281-79f5-4ba9-846c-a9540b7031d6', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d934d281-79f5-4ba9-846c-a9540b7031d6', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('6c9fc9f1-0f13-4337-a311-90cfc2bd3b70', 0, 'd934d281-79f5-4ba9-846c-a9540b7031d6',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element to which the value should be transformed.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('602b0b73-13ae-4644-86db-a02204375a22', 0, 'd934d281-79f5-4ba9-846c-a9540b7031d6',
'var result = false;
if (input.hasValue())
{
  var value = input.getValue();
  if (input.hasValueQuantity())
  {
    output.setValue(args[''dataElement''], value.getValueElement().getValue());
    result = true;
  }
  else if (typeof value.primitive !== ''undefined'')
  {
    if (value.isPrimitive() && typeof value.value !== ''undefined'')
    {
      output.setValue(args[''dataElement''], value.getValue());
      result = true;
    }
  }
}
else
{
  output.setValue(args[''dataElement''], null);
  result = true;
}
result', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('602b0b73-13ae-4644-86db-a02204375a22', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('602b0b73-13ae-4644-86db-a02204375a22', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('3248aca5-52e5-4ad8-8b96-b2fc0b086410', 0, 'd934d281-79f5-4ba9-846c-a9540b7031d6', 'Transforms simple observation value from FHIR to DHIS2', 'TRANSFORM_OBSERVATION_SIMPLE_F2D',
        'Transforms simple (boolean, integer, string, date time) observation value from FHIR to DHIS2. If the value is a quantity the unit will not be checked.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('1667671a-b471-4f16-8250-8bd8bb2039ad', 0, 'TRANSFORM_OBSERVATION_INTEGER_D2F', 'Transforms a DHIS2 integer to a FHIR observation value',
'Transforms a DHIS2 integer value to a FHIR observation value.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('1667671a-b471-4f16-8250-8bd8bb2039ad', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('1667671a-b471-4f16-8250-8bd8bb2039ad', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('1667671a-b471-4f16-8250-8bd8bb2039ad', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('0d460824-f413-4cc7-8263-692940b64994', 0, '1667671a-b471-4f16-8250-8bd8bb2039ad',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element to which the value should be transformed.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('960a2be6-23f0-41bb-9bd8-d4f27a4af5b8', 0, '1667671a-b471-4f16-8250-8bd8bb2039ad',
'var value = input.getIntegerValue(args[''dataElement'']);
output.setValue(value == null ? null : fhirResourceUtils.createType(''integer'').setValue(value));
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('960a2be6-23f0-41bb-9bd8-d4f27a4af5b8', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('960a2be6-23f0-41bb-9bd8-d4f27a4af5b8', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('5dc0821a-c6f2-4c3d-a38d-f639b0e6b5e3', 0, '1667671a-b471-4f16-8250-8bd8bb2039ad', 'Transforms a DHIS2 integer to a FHIR observation value', 'TRANSFORM_OBSERVATION_INTEGER_D2F',
        'Transforms a DHIS2 integer to a FHIR observation value.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('552d5978-fa57-48ae-92d7-c350ca398004', 0, 'TRANSFORM_OBSERVATION_DATE_D2F', 'Transforms a DHIS2 date to a FHIR observation value',
'Transforms a DHIS2 date value to a FHIR observation value.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('552d5978-fa57-48ae-92d7-c350ca398004', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('552d5978-fa57-48ae-92d7-c350ca398004', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('552d5978-fa57-48ae-92d7-c350ca398004', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('a3eb360a-fe52-4851-8630-b9f98717804e', 0, '552d5978-fa57-48ae-92d7-c350ca398004',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element to which the value should be transformed.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('8073d8a1-b74c-4f23-ab45-58075a3d0b33', 0, '552d5978-fa57-48ae-92d7-c350ca398004',
'var value = input.getDateValue(args[''dataElement'']);
output.setValue(value == null ? null : dateTimeUtils.getDayDateTimeElement(value));
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('8073d8a1-b74c-4f23-ab45-58075a3d0b33', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('8073d8a1-b74c-4f23-ab45-58075a3d0b33', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('7505ba56-51b7-4033-a06e-ccde1a9ecab4', 0, '552d5978-fa57-48ae-92d7-c350ca398004', 'Transforms a DHIS2 date to a FHIR observation value', 'TRANSFORM_OBSERVATION_DATE_D2F',
        'Transforms a DHIS2 date to a FHIR observation value.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('6968dbce-d9c1-47bc-8db3-4af084b4f07d', 0, 'TRANSFORM_OBSERVATION_STRING_D2F', 'Transforms a DHIS2 string value to a FHIR observation value',
'Transforms a DHIS2 string value to a FHIR observation value.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6968dbce-d9c1-47bc-8db3-4af084b4f07d', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6968dbce-d9c1-47bc-8db3-4af084b4f07d', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6968dbce-d9c1-47bc-8db3-4af084b4f07d', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('608e66d2-dbaf-4068-9e13-c18aca952126', 0, '6968dbce-d9c1-47bc-8db3-4af084b4f07d',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element to which the value should be transformed.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('77d00b71-745f-4dc3-850c-f6a9ae292dc8', 0, '6968dbce-d9c1-47bc-8db3-4af084b4f07d',
'var value = input.getStringValue(args[''dataElement'']);
output.setValue(value == null ? null : fhirResourceUtils.createType(''string'').setValue(value));
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('77d00b71-745f-4dc3-850c-f6a9ae292dc8', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('77d00b71-745f-4dc3-850c-f6a9ae292dc8', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('6192c06f-b540-459c-ae19-1f4cdf828640', 0, '6968dbce-d9c1-47bc-8db3-4af084b4f07d', 'Transforms a DHIS2 string to a FHIR observation value', 'TRANSFORM_OBSERVATION_STRING_D2F',
        'Transforms a DHIS2 string to a FHIR observation value.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('89cd3163-681c-4015-ad1f-4ce53959dd65', 0, 'TRANSFORM_OBSERVATION_BOOLEAN_D2F', 'Transforms a DHIS2 boolean value to a FHIR observation value',
'Transforms a DHIS2 boolean value to a FHIR observation value.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('89cd3163-681c-4015-ad1f-4ce53959dd65', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('89cd3163-681c-4015-ad1f-4ce53959dd65', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('89cd3163-681c-4015-ad1f-4ce53959dd65', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('ab0095bd-5d4a-4d40-8beb-cd9f48ac76b6', 0, '89cd3163-681c-4015-ad1f-4ce53959dd65',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element to which the value should be transformed.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('6cffd450-0647-4dd4-85b0-2d6c4e48d6ad', 0, '89cd3163-681c-4015-ad1f-4ce53959dd65',
'var value = input.getBooleanValue(args[''dataElement'']);
output.setValue(value == null ? null : fhirResourceUtils.createType(''boolean'').setValue(value));
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('6cffd450-0647-4dd4-85b0-2d6c4e48d6ad', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('6cffd450-0647-4dd4-85b0-2d6c4e48d6ad', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('209c2388-336b-41dd-b61d-3aa55f3b8892', 0, '89cd3163-681c-4015-ad1f-4ce53959dd65', 'Transforms a DHIS2 boolean to a FHIR observation value', 'TRANSFORM_OBSERVATION_BOOLEAN_D2F',
        'Transforms a DHIS2 boolean to a FHIR observation value.');

INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('44410bec-d191-4c99-99a4-73385789dd49', 0, 'f1da6937-e2fe-47a4-b0f3-8bbff7818ee1', 'Transforms weight in grams from FHIR to DHIS2', 'TRANSFORM_WEIGHT_GRAM_F2D');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('8e23e35a-69a1-4416-b753-6dd29b07633a', '44410bec-d191-4c99-99a4-73385789dd49', 'ead06d42-8e40-47f5-8a89-74fbcf237b2b', 'GRAM');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, base_executable_script_id)
VALUES ('ea35e12c-0fda-453f-be94-af68171b6dc6', 0, 'f1da6937-e2fe-47a4-b0f3-8bbff7818ee1', 'Transforms weight unit in grams from DHIS2 to FHIR', 'TRANSFORM_WEIGHT_GRAM_D2F', '44410bec-d191-4c99-99a4-73385789dd49');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('4004ff76-fe20-494f-abb8-679d983b80e0', 0, 'TRANSFORM_OBSERVATION_QUANTITY_D2F', 'Transforms a DHIS2 quantity value to a FHIR observation value',
'Transforms a DHIS2 number value to a FHIR observation quantity.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('4004ff76-fe20-494f-abb8-679d983b80e0', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('4004ff76-fe20-494f-abb8-679d983b80e0', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('4004ff76-fe20-494f-abb8-679d983b80e0', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('426c482a-cc27-4de4-a859-82ad4b6d5954', 0, '4004ff76-fe20-494f-abb8-679d983b80e0',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element to which the value should be transformed.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('a4b890e8-1acc-4296-96ad-6402b49cf3e0', 0, '4004ff76-fe20-494f-abb8-679d983b80e0',
'unit', 'STRING', FALSE, NULL, 'The displayable unit.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('df475ccf-0800-4331-a787-4f78e2d30098', 0, '4004ff76-fe20-494f-abb8-679d983b80e0',
'system', 'STRING', FALSE, NULL, 'The system URI of the unit.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('13ec44ca-bf9a-4095-88e8-c352ce5da66a', 0, '4004ff76-fe20-494f-abb8-679d983b80e0',
'code', 'STRING', FALSE, NULL, 'The unit code.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('76b1d246-4950-451f-b7ed-299c0770123d', 0, '4004ff76-fe20-494f-abb8-679d983b80e0',
'var value = input.getBigDecimalValue(args[''dataElement'']);
output.setValue(value == null ? null : fhirResourceUtils.createType(''Quantity'').setValue(value).setSystem(args[''system'']).setCode(args[''code'']).setUnit(args[''unit'']));
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('76b1d246-4950-451f-b7ed-299c0770123d', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('76b1d246-4950-451f-b7ed-299c0770123d', 'R4');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a72361f0-c078-4bc7-a01a-fed69df96f16', 0, '4004ff76-fe20-494f-abb8-679d983b80e0', 'Transforms years from DHIS2 to FHIR', 'TRANSFORM_OBSERVATION_YEARS_D2F',
        'Transforms years from DHIS2 to FHIR');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('9ab1c21d-4903-4e20-8ce7-31a941e432b2', 'a72361f0-c078-4bc7-a01a-fed69df96f16', 'a4b890e8-1acc-4296-96ad-6402b49cf3e0', 'year');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('6755971f-c2c4-450d-b3a4-01236f15d8f7', 'a72361f0-c078-4bc7-a01a-fed69df96f16', 'df475ccf-0800-4331-a787-4f78e2d30098', 'http://unitsofmeasure.org');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('77064b28-0a5d-4af2-818c-e57d17d8483e', 'a72361f0-c078-4bc7-a01a-fed69df96f16', '13ec44ca-bf9a-4095-88e8-c352ce5da66a', 'a');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('22ceb0d0-6985-44ae-8c8c-9899d470a833', 0, '4004ff76-fe20-494f-abb8-679d983b80e0', 'Transforms weeks from DHIS2 to FHIR', 'TRANSFORM_OBSERVATION_WEEKS_D2F',
        'Transforms weeks from DHIS2 to FHIR');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('5e9760a9-5e3a-499e-a1b4-a53383797411', '22ceb0d0-6985-44ae-8c8c-9899d470a833', 'a4b890e8-1acc-4296-96ad-6402b49cf3e0', 'week');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('1dbce102-5312-45c8-bbe6-a24a47bfddad', '22ceb0d0-6985-44ae-8c8c-9899d470a833', 'df475ccf-0800-4331-a787-4f78e2d30098', 'http://unitsofmeasure.org');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('25594a28-df18-4b4b-90d8-bb96f3205d2c', '22ceb0d0-6985-44ae-8c8c-9899d470a833', '13ec44ca-bf9a-4095-88e8-c352ce5da66a', 'wk');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('c1af3918-e3e6-450a-9dae-ceb40b87c787', 0, '4004ff76-fe20-494f-abb8-679d983b80e0', 'Transforms time quantities from DHIS2 to FHIR', 'TRANSFORM_OBSERVATION_TIME QUANTITIES_D2F',
        'Transforms time quantities from DHIS2 to FHIR');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('8f6ac0e5-39f0-4584-8a84-e54eae91894a', 'c1af3918-e3e6-450a-9dae-ceb40b87c787', 'a4b890e8-1acc-4296-96ad-6402b49cf3e0', '#');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('73721540-a058-4b1a-98e4-0ba9f08916a1', 'c1af3918-e3e6-450a-9dae-ceb40b87c787', 'df475ccf-0800-4331-a787-4f78e2d30098', 'http://unitsofmeasure.org');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('a5afcf00-92cd-4f00-ba5d-86c4cdbf28ab', 'c1af3918-e3e6-450a-9dae-ceb40b87c787', '13ec44ca-bf9a-4095-88e8-c352ce5da66a', '[#]');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('1d5b1e4f-b116-4d0a-9a08-a53416ebcee9', 0, 'TRANSFORM_CODE_SET_VAL_F2D', 'Transforms code set observation value from FHIR to DHIS2',
'Transforms code set observation value from DHIS2 to FHIR',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_OBSERVATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('1d5b1e4f-b116-4d0a-9a08-a53416ebcee9', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('1d5b1e4f-b116-4d0a-9a08-a53416ebcee9', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('1d5b1e4f-b116-4d0a-9a08-a53416ebcee9', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('be68eb86-1950-42b8-9109-181b4ac32bb4', 0, '1d5b1e4f-b116-4d0a-9a08-a53416ebcee9',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element to which the value should be transformed.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('8ab4ec2f-5ac5-438d-b286-66cb19e8a335', 0, '1d5b1e4f-b116-4d0a-9a08-a53416ebcee9',
'codeSetCode', 'CODE_SET', TRUE, NULL, 'The code of the code set that should be used as value set of the codeable concept.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('29df4b98-a355-43dd-b903-fe0df8c13b19', 0, '1d5b1e4f-b116-4d0a-9a08-a53416ebcee9',
'output.setValue(args[''dataElement''], codeUtils.getMappedValueSetCode(args[''codeSetCode''], input.hasValueCodeableConcept() ? input.getValueCodeableConcept() : null)); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('29df4b98-a355-43dd-b903-fe0df8c13b19', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('29df4b98-a355-43dd-b903-fe0df8c13b19', 'R4');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('d3cd20be-ea53-42ea-b9c8-d34ea7058c82', 0, 'TRANSFORM_CODE_SET_VAL_D2F', 'Transforms code set observation value from DHIS2 to FHIR',
'Transforms code set observation value from DHIS2 to FHIR.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d3cd20be-ea53-42ea-b9c8-d34ea7058c82', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d3cd20be-ea53-42ea-b9c8-d34ea7058c82', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d3cd20be-ea53-42ea-b9c8-d34ea7058c82', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('54c94e77-7c15-4a04-a8f0-dbfb9d64fc8a', 0, 'd3cd20be-ea53-42ea-b9c8-d34ea7058c82',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element to which the value should be transformed.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('6742e4f8-2834-4afa-b7cc-5d22c3ed9ed0', 0, 'd3cd20be-ea53-42ea-b9c8-d34ea7058c82',
'codeSetCode', 'CODE_SET', TRUE, NULL, 'The code of the code set that should be used as value set of the codeable concept.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('82fd29a3-cc4a-46e0-8fef-297f16a989f9', 0, 'd3cd20be-ea53-42ea-b9c8-d34ea7058c82',
'output.setValue(codeUtils.getMappedValueSetCode(args[''codeSetCode''], input.getStringValue(args[''dataElement'']))); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('82fd29a3-cc4a-46e0-8fef-297f16a989f9', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('82fd29a3-cc4a-46e0-8fef-297f16a989f9', 'R4');
