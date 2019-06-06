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

ALTER TABLE fhir_code ALTER COLUMN mapped_code TYPE VARCHAR(230);

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('6783a443-8cb3-4b0a-8465-b5d02e676441', 0, 'TRANSFORM_OBSERVATION_TIME_D2F', 'Transforms a DHIS2 time value to a FHIR observation value',
'Transforms a DHIS2 time value to a FHIR observation value.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6783a443-8cb3-4b0a-8465-b5d02e676441', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6783a443-8cb3-4b0a-8465-b5d02e676441', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('6783a443-8cb3-4b0a-8465-b5d02e676441', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('2a68b01e-acee-4d9f-a6aa-2a73309b392e', 0, '6783a443-8cb3-4b0a-8465-b5d02e676441',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element to which the value should be transformed.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('9941bcb7-4a0f-4348-8cb4-1ada11469161', 0, '6783a443-8cb3-4b0a-8465-b5d02e676441',
'var value = input.getStringValue(args[''dataElement'']);
output.setValue(value == null ? null : fhirResourceUtils.createType(''time'').setValue(value));
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('9941bcb7-4a0f-4348-8cb4-1ada11469161', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('9941bcb7-4a0f-4348-8cb4-1ada11469161', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('46818a7b-421e-484e-abea-0e3af69a26d9', 0, '6968dbce-d9c1-47bc-8db3-4af084b4f07d', 'Transforms a DHIS2 time to a FHIR observation value', 'TRANSFORM_OBSERVATION_TIME_D2F',
        'Transforms a DHIS2 time to a FHIR observation value.');

UPDATE fhir_executable_script SET script_id='83a84bee-eadc-4c8a-b78f-8c8b9269883d' WHERE id='ea35e12c-0fda-453f-be94-af68171b6dc6';

UPDATE fhir_script_source SET source_text=
'var updated = false;
if (typeof output.dateElement !== ''undefined'')
{
  output.setDateElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
else if (typeof output.effective !== ''undefined'')
{
  output.setEffective(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
else if (typeof output.period !== ''undefined'')
{
  output.setPeriod(null);
  output.getPeriod().setStartElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
else if (typeof output.recordedElement !== ''undefined'')
{
  output.setRecordedElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
else if (typeof output.recordedDateElement !== ''undefined'')
{
  output.setRecordedDateElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
else if (typeof output.authoredOnElement !== ''undefined'')
{
  output.setAuthoredOnElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
updated' WHERE id='4a0b6fde-c0d6-4ad2-89da-992f4a47a115' and version=0;
