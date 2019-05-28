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

UPDATE fhir_script SET base_script_id=NULL WHERE id='6c0f8f8c-59d8-4829-b930-39780d11277f' AND base_script_id='f18acd12-bc85-4f79-935d-353904eadc0b';

-- Script that sets if an immunization has been given or not been given and may also be used to set the reason.
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('54d7569e-8994-4bfd-9791-c872473166ac', 0, 'TRANSFORM_IMMUNIZATION_REASON_D2F', 'Transforms immunization with reason from DHIS2 to FHIR', 'Transforms immunization with reason from DHIS2 to FHIR.',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_IMMUNIZATION', 'DHIS_EVENT', '6c0f8f8c-59d8-4829-b930-39780d11277f');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('54d7569e-8994-4bfd-9791-c872473166ac', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('54d7569e-8994-4bfd-9791-c872473166ac', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('54d7569e-8994-4bfd-9791-c872473166ac', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('2120f93f-742b-4d84-9537-6cdb96c447e6', 0, '54d7569e-8994-4bfd-9791-c872473166ac',
'doseSequence', 'INTEGER', TRUE, NULL, 'The dose sequence that is transformed.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('892db24d-b78e-46f5-a088-10759e60375e', 0, '54d7569e-8994-4bfd-9791-c872473166ac',
'var doseGiven = input.getBooleanValue(args[''dataElement'']);
output.setVaccineCode(codeUtils.getRuleCodeableConcept());
if (output.getVaccineCode().isEmpty())
{
  output.getVaccineCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
}
output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', ''completed''));
output.setNotGiven(!doseGiven);
output.setPrimarySource(input.isProvidedElsewhere(args[''dataElement'']) == false);
output.setVaccinationProtocol(null);
output.addVaccinationProtocol().setDoseSequence(args[''doseSequence'']);
output.setExplanation(null);
if (!doseGiven && args[''dataElement2''] != null && args[''reasonCodeSetCode''] != null)
{
  output.getExplanation().addReasonNotGiven(codeUtils.getMappedValueSetCode(args[''reasonCodeSetCode''], input.getStringValue(args[''dataElement2''])));
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('892db24d-b78e-46f5-a088-10759e60375e', 'DSTU3');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('9a6c58c4-fb57-4111-ab60-fa3f48721177', 0, '54d7569e-8994-4bfd-9791-c872473166ac',
'var doseGiven = input.getBooleanValue(args[''dataElement'']);
output.setVaccineCode(codeUtils.getRuleCodeableConcept());
if (output.getVaccineCode().isEmpty())
{
  output.getVaccineCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
}
output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', doseGiven ? ''completed'' : ''not-done''));
output.setPrimarySource(input.isProvidedElsewhere(args[''dataElement'']) == false);
output.setProtocolApplied(null);
output.addProtocolApplied().setDoseNumber(fhirResourceUtils.createType(''positiveInt'').setValue(args[''doseSequence'']));
output.setStatusReason(null);
if (!doseGiven && args[''dataElement2''] != null && args[''reasonCodeSetCode''] != null)
{
  output.setStatusReason(codeUtils.getMappedValueSetCode(args[''reasonCodeSetCode''], input.getStringValue(args[''dataElement2''])));
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('9a6c58c4-fb57-4111-ab60-fa3f48721177', 'R4');

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
else if (typeof output.occurrenceDateTimeType !== ''undefined'')
{
  output.setOccurrence(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
updated' WHERE id='4a0b6fde-c0d6-4ad2-89da-992f4a47a115' AND version=0;

UPDATE fhir_script_source SET source_text=
'var result;
if (typeof input.occurrenceDateTimeType !== ''undefined'')
{
  result = dateTimeUtils.getPreciseDate(input.hasOccurrenceDateTimeType() ? input.getOccurrenceDateTimeType() : null);
}
else
{
  result = dateTimeUtils.getPreciseDate(input.hasDateElement() ? input.getDateElement() : null);
}
result' WHERE id='71056dc1-6bd3-491d-908c-c1494090ed65' AND version=0;

UPDATE fhir_script_source SET source_text=
'var providedElsewhere = input.hasPrimarySource() ? !input.isPrimarySource() : null;
var notGiven = input.notGiven;
output.setValue(args[''dataElement''], !notGiven, providedElsewhere);
if (args[''dataElement2''] != null && args[''reasonCodeSetCode''] != null)
{
  if (notGiven && input.statusReason !== ''undefined'')
  {
    output.setValue(args[''dataElement2''], codeUtils.getMappedValueSetCode(args[''reasonCodeSetCode''], input.getExplanation().getReasonNotGivenFirstRep()), providedElsewhere);
  }
  else
  {
    output.setValue(args[''dataElement2''], null, providedElsewhere);
  }
}
true' WHERE id='12efce0c-ecaa-4142-832b-636ddb4bd98b' AND version=0;

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('ef4e38d2-be17-4c15-a71b-e3d7e0b54e0a', 0, '54d7569e-8994-4bfd-9791-c872473166ac', 'Evaluates the immunization dose sequence 1 for DHIS2 to FHIR', 'TRANSFORM_IMMUNIZATION_REASON_D2F_1',
        'Evaluates the immunization dose sequence 1 for DHIS2 to FHIR.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('cddc2987-01cd-4054-a95f-8dc23fb02cbc', 'ef4e38d2-be17-4c15-a71b-e3d7e0b54e0a', '2120f93f-742b-4d84-9537-6cdb96c447e6', '1');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('321f0853-533e-4ec6-83d4-0936d881946e', 0, '54d7569e-8994-4bfd-9791-c872473166ac', 'Evaluates the immunization dose sequence 2 for DHIS2 to FHIR', 'TRANSFORM_IMMUNIZATION_REASON_D2F_2',
        'Evaluates the immunization dose sequence 2 for DHIS2 to FHIR.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('e6b6ef7f-0ab8-4b09-923a-dab2ded2f137', '321f0853-533e-4ec6-83d4-0936d881946e', '2120f93f-742b-4d84-9537-6cdb96c447e6', '2');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('02614e57-269f-458d-85fe-39a2d8cb2dd9', 0, '54d7569e-8994-4bfd-9791-c872473166ac', 'Evaluates the immunization dose sequence 3 for DHIS2 to FHIR', 'TRANSFORM_IMMUNIZATION_REASON_D2F_3',
        'Evaluates the immunization dose sequence 3 for DHIS2 to FHIR.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('4be27ae9-af34-4df4-b96a-9bb824dec426', '02614e57-269f-458d-85fe-39a2d8cb2dd9', '2120f93f-742b-4d84-9537-6cdb96c447e6', '3');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('1d0c1dc2-c2fd-4986-bcd8-b6760f9febd1', 0, '54d7569e-8994-4bfd-9791-c872473166ac', 'Evaluates the immunization dose sequence 4 for DHIS2 to FHIR', 'TRANSFORM_IMMUNIZATION_REASON_D2F_4',
        'Evaluates the immunization dose sequence 4 for DHIS2 to FHIR.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('112e3ce2-d528-427d-a794-0d1d2ef121c8', '1d0c1dc2-c2fd-4986-bcd8-b6760f9febd1', '2120f93f-742b-4d84-9537-6cdb96c447e6', '4');

INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('8c61c5b7-2b40-4dd4-85c1-07ce32046177', 0, '54d7569e-8994-4bfd-9791-c872473166ac', 'Evaluates the immunization dose sequence 5 for DHIS2 to FHIR', 'TRANSFORM_IMMUNIZATION_REASON_D2F_5',
        'Evaluates the immunization dose sequence 5 for DHIS2 to FHIR.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('b014e1f5-ef59-48b4-97e9-f2d8cc3b7650', '8c61c5b7-2b40-4dd4-85c1-07ce32046177', '2120f93f-742b-4d84-9537-6cdb96c447e6', '5');
