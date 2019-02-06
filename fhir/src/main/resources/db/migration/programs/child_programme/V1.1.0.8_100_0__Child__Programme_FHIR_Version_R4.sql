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

DELETE FROM fhir_script_source_version WHERE script_source_id='78b4af9b-c247-4290-abee-44983938b265' AND fhir_version='R4';
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('43e49f59-67d6-4919-a808-6a35bc55e18c', 0, '33952dc7-bbf9-474d-8eaa-ab866a926da3',
'var result;
var doseGiven = input.getIntegerOptionValue(args[''dataElement''], 1, args[''optionValuePattern'']);
if (doseGiven == null)
{
  output.setNotGiven(true);
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
  output.setNotGiven(false);
  output.setProtocolApplied(null);
  output.addProtocolApplied().setDoseNumber(fhirResourceUtils.createType(''positiveInt'').setValue(doseGiven));
  result = true;
}
result', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('43e49f59-67d6-4919-a808-6a35bc55e18c', 'R4');
