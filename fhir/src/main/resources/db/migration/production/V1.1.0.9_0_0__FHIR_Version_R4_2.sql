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

-- Script that checks if given FHIR Immunization is applicable
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('b038a06d-664a-4b97-bdd9-7d6adccd8c71', 0, 'ddaebe0a-8a88-4cb7-a5ca-632e86216be0', '(input.status == null) || (input.status != fhirResourceUtils.resolveEnumValue(input, ''status'', ''completed''))', 'JAVASCRIPT');
DELETE FROM fhir_script_source_version WHERE script_source_id='2f04a7c3-7041-4c12-aa75-748862271818' AND fhir_version='R4';
INSERT INTO fhir_script_source_version(script_source_id, fhir_version) VALUES ('b038a06d-664a-4b97-bdd9-7d6adccd8c71', 'R4');

INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('178b9811-ce7d-4d21-8580-e3baf52068e4', 0, 'c8a937b5-665b-485c-bbc9-7a83e21a4e47',
'var result;
var given = input.getBooleanValue(args[''dataElement'']);
if (given)
{
  output.setVaccineCode(codeUtils.getRuleCodeableConcept());
  if (output.getVaccineCode().isEmpty())
  {
    output.getVaccineCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
  }
  output.setPrimarySource(input.isProvidedElsewhere(args[''dataElement'']) == false);
  output.setNotGiven(false);
  result = true;
}
else
{
  output.setNotGiven(true);
  result = !output.getIdElement().isEmpty();
}
result', 'JAVASCRIPT');
DELETE FROM fhir_script_source_version WHERE script_source_id='6ede1e58-17c1-49bd-a7e4-36ef9c87521f' AND fhir_version='R4';
INSERT INTO fhir_script_source_version(script_source_id, fhir_version) VALUES ('178b9811-ce7d-4d21-8580-e3baf52068e4', 'R4');

INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('41bfbd0e-a98f-44e2-a87c-33b80e5f25d4', 0, '33952dc7-bbf9-474d-8eaa-ab866a926da3',
'var result;
var doseGiven = input.getIntegerOptionValue(args[''dataElement''], 1, args[''optionValuePattern'']);
if (doseGiven == null)
{
  output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', ''not-done''));
  result = !output.getIdElement().isEmpty();
}
else
{
  output.setVaccineCode(codeUtils.getRuleCodeableConcept());
  if (output.getVaccineCode().isEmpty())
  {
    output.getVaccineCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
  }
  output.setPrimarySource(input.isProvidedElsewhere(args[''dataElement'']) == false);
  output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', ''completed''));
  output.setProtocolApplied(null);
  output.addProtocolApplied().setDoseNumber(fhirResourceUtils.createType(''positiveInt'').setValue(doseGiven));
  result = true;
}
result', 'JAVASCRIPT');
DELETE FROM fhir_script_source_version WHERE script_source_id='43e49f59-67d6-4919-a808-6a35bc55e18c' AND fhir_version='R4';
INSERT INTO fhir_script_source_version(script_source_id, fhir_version) VALUES ('41bfbd0e-a98f-44e2-a87c-33b80e5f25d4', 'R4');

INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('0ab55f2c-0866-4b62-a727-566a97794edf', 0, '33952dc7-bbf9-474d-8eaa-ab866a926da3',
'var result;
var doseGiven = input.getIntegerOptionValue(args[''dataElement''], 1, args[''optionValuePattern'']);
if (doseGiven == null)
{
  output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', ''not-done''));
  result = !output.getIdElement().isEmpty();
}
else
{
  output.setVaccineCode(codeUtils.getRuleCodeableConcept());
  if (output.getVaccineCode().isEmpty())
  {
    output.getVaccineCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
  }
  output.setPrimarySource(input.isProvidedElsewhere(args[''dataElement'']) == false);
  output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', ''completed''));
  output.setProtocolApplied(null);
  output.addProtocolApplied().setDoseNumber(fhirResourceUtils.createType(''positiveInt'').setValue(doseGiven));
  result = true;
}
result', 'JAVASCRIPT');
DELETE FROM fhir_script_source_version WHERE script_source_id='78b4af9b-c247-4290-abee-44983938b265' AND fhir_version='R4';
INSERT INTO fhir_script_source_version(script_source_id, fhir_version) VALUES ('0ab55f2c-0866-4b62-a727-566a97794edf', 'R4');

INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('8d1c7038-3f3f-4278-9689-2718ae82f417', 0, '475e192c-09f2-4357-b545-c1069b9518b3',
'output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', ''entered-in-error'')); true', 'JAVASCRIPT');
DELETE FROM fhir_script_source_version WHERE script_source_id='235ffb1a-133e-4046-bdd3-498cf0ff771d' AND fhir_version='R4';
INSERT INTO fhir_script_source_version(script_source_id, fhir_version) VALUES ('8d1c7038-3f3f-4278-9689-2718ae82f417', 'R4');

INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('f3d6c097-61d7-48e0-894e-3ab30cad1299', 0, '15c2a8b8-b8f0-443a-adda-cfb87a1a4378', 'dateTimeUtils.getPreciseDate(input.hasOccurrence() ? input.getOccurrence() : null)', 'JAVASCRIPT');
DELETE FROM fhir_script_source_version WHERE script_source_id='71056dc1-6bd3-491d-908c-c1494090ed65' AND fhir_version='R4';
INSERT INTO fhir_script_source_version(script_source_id, fhir_version) VALUES ('f3d6c097-61d7-48e0-894e-3ab30cad1299', 'R4');

UPDATE fhir_script_source SET source_text = 'var updated = false;
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
else if (typeof output.occurrence !== ''undefined'')
{
  output.setOccurrence(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
updated' WHERE id='4a0b6fde-c0d6-4ad2-89da-992f4a47a115';
