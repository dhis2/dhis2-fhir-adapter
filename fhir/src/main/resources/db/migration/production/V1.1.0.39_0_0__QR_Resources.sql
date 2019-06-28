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

ALTER TABLE fhir_resource_mapping ADD COLUMN imp_program_stage_ref_lookup_script_id UUID;
ALTER TABLE fhir_resource_mapping ADD CONSTRAINT fhir_resource_mapping_fk18
    FOREIGN KEY (imp_program_stage_ref_lookup_script_id) REFERENCES fhir_executable_script(id);
CREATE INDEX IF NOT EXISTS fhir_resource_mapping_i18
  ON fhir_resource_mapping(imp_program_stage_ref_lookup_script_id);
COMMENT ON COLUMN fhir_resource_mapping.imp_program_stage_ref_lookup_script_id IS 'References the executable script that extract a reference to the corresponding program stage on import.';

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type)
VALUES ('e52272fd-0e0c-4a74-b059-e324455721d0', 0, 'QR_DATE_LOOKUP', 'Questionnaire Response Date Lookup',
'Lookup of the exact date of the FHIR Questionnaire Response.', 'EVALUATE', 'DATE_TIME', 'FHIR_QUESTIONNAIRE_RESPONSE');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('e52272fd-0e0c-4a74-b059-e324455721d0', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('e52272fd-0e0c-4a74-b059-e324455721d0', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('bc694d06-6612-4cbe-aac0-aa526e25007c', 0, 'e52272fd-0e0c-4a74-b059-e324455721d0',
'input.getAuthored()', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('bc694d06-6612-4cbe-aac0-aa526e25007c', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('521b3a00-8ecc-487b-a7e3-fe12b68e388a', 0, 'e52272fd-0e0c-4a74-b059-e324455721d0', 'Questionnaire Response Date Lookup', 'QR_DATE_LOOKUP',
        'Lookup of the exact date of the FHIR Questionnaire Response.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type)
VALUES ('152434b0-81db-49bb-a3c5-3c09caf29208', 0, 'QR_PROGRAM_STAGE_REF_LOOKUP', 'Questionnaire Response Tracker Program Stage Lookup',
'Lookup of the Tracker Program Stage of the FHIR QuestionnaireResponse.', 'EVALUATE', 'PROGRAM_STAGE_REF', 'FHIR_QUESTIONNAIRE_RESPONSE');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('152434b0-81db-49bb-a3c5-3c09caf29208', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('152434b0-81db-49bb-a3c5-3c09caf29208', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('eb2ef8ad-93a0-42d5-b8da-2d2b0d3c6115', 0, '152434b0-81db-49bb-a3c5-3c09caf29208',
'var programStageRef = null; if (input.hasQuestionnaire()) programStageRef = context.createReference(input.getQuestionnaire(), ''id''); programStageRef', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('eb2ef8ad-93a0-42d5-b8da-2d2b0d3c6115', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('385e52d2-8674-403d-b425-86b5d4e9faf0', 0, '152434b0-81db-49bb-a3c5-3c09caf29208', 'Questionnaire Response Tracker Program Stage Lookup',
        'QR_PROGRAM_STAGE_REF_LOOKUP', 'Lookup of the Tracker Program Stage of the FHIR QuestionnaireResponse.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('930a7e03-b322-4fd9-ae0c-0213949ac60f', 0, 'DEFAULT_QR_F2D', 'Default FHIR Questionnaire Response to DHIS2 Event Transformation',
'Transforms FHIR Questionnaire Response to DHIS2 Event.', 'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_QUESTIONNAIRE_RESPONSE', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('930a7e03-b322-4fd9-ae0c-0213949ac60f', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('930a7e03-b322-4fd9-ae0c-0213949ac60f', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('930a7e03-b322-4fd9-ae0c-0213949ac60f', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('330214db-ec21-4cd5-b004-6592b8f1e2bf', 0, '930a7e03-b322-4fd9-ae0c-0213949ac60f',
'function getAnswerFirstValue(item)
{
  var answerValue = null;
  var answers = item.getAnswer();
  if (answers !== null && !answers.isEmpty())
  {
    var answer = answers.get(0);
    if (answer.hasValueDateType())
    {
      answerValue = answer.getValueDateType().getValue();
    }
    else if (answer.hasValueDecimalType())
    {
      answerValue = answer.getValueDecimalType().getValue();
    }
    else if (answer.hasValueBooleanType())
    {
      answerValue = answer.getValueBooleanType().getValue();
    }
    else if (answer.hasValueIntegerType())
    {
      answerValue = answer.getValueIntegerType().getValue();
    }
    else if (answer.hasValueStringType())
    {
      answerValue = answer.getValueStringType().getValue();
    }
    else if (answer.hasValueQuantity())
    {
      answerValue = answer.getValueQuantity().getValue();
    }
    else if (answer.hasValueDateTimeType())
    {
      answerValue = answer.getValueDateTimeType().getValue();
    }
    else if (answer.hasValueTimeType())
    {
      answerValue = answer.getValueTimeType().getValue();
    }
    else if (answer.hasValueTimeType())
    {
      answerValue = answer.getValueTimeType().getValue();
    }
  }
  return answerValue;
}
function getOutputStatus(input)
{
  var outputStatus = ''SKIPPED'';
  var inputStatus = input.getStatus().toCode();
  if (inputStatus === ''in-progress'')
  {
    outputStatus = ''ACTIVE'';
  }
  else if (inputStatus === ''completed'')
  {
    outputStatus = ''COMPLETED'';
  }
  else if (inputStatus === ''amended'')
  {
    outputStatus = ''ACTIVE'';
  }
  else if (inputStatus === ''entered-in-error'')
  {
    outputStatus = ''SKIPPED'';
  }
  else if (inputStatus === ''stopped'')
  {
    outputStatus = ''SKIPPED'';
  }
  return outputStatus;
}
output.setStatus(getOutputStatus(input));
var items = input.getItem();
for (var i = 0; i < items.size(); i++)
{
  var item = items.get(i);
  var linkId = item.getLinkId();
  var answerValue = getAnswerFirstValue(item);
  var linkRef = context.createReference(linkId, ''id'');
  if (programStage.getDataElement(linkRef) != null)
  {
    output.setValue(linkRef, answerValue);
  }
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('330214db-ec21-4cd5-b004-6592b8f1e2bf', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('2d5ec131-8fbc-44d3-95c5-2544f7ff284f', 0, '930a7e03-b322-4fd9-ae0c-0213949ac60f', 'Default FHIR Questionnaire Response to DHIS2 Event Transformation', 'DEFAULT_QR_F2D',
        'Transforms FHIR Questionnaire Response to DHIS2 Event.');

INSERT INTO fhir_resource_mapping(id, version, fhir_resource_type, tracked_entity_fhir_resource_type, imp_tei_lookup_script_id, imp_event_org_lookup_script_id, imp_event_date_lookup_script_id, imp_program_stage_ref_lookup_script_id,
imp_enrollment_org_lookup_script_id, imp_enrollment_date_lookup_script_id)
VALUES('417d0db3-76bc-48be-bc42-23e7a7e663b0', 0, 'QUESTIONNAIRE_RESPONSE', 'PATIENT', '762b4137-a98b-4b10-a0f5-629d93e23461', '25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', '521b3a00-8ecc-487b-a7e3-fe12b68e388a', '385e52d2-8674-403d-b425-86b5d4e9faf0',
'25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', '521b3a00-8ecc-487b-a7e3-fe12b68e388a');
