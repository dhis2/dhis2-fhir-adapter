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

UPDATE fhir_script_source SET source_text=
'output.setVerificationStatus(fhirResourceUtils.resolveEnumValue(output, ''verificationStatus'', ''entered-in-error'')); true'
WHERE id='495fd0d6-92c5-421a-812d-b4d4ba12469f' and version=0;
DELETE FROM fhir_script_source_version WHERE script_source_id='495fd0d6-92c5-421a-812d-b4d4ba12469f' AND fhir_version='R4';

UPDATE fhir_script_source SET source_text=
'output.setVerificationStatus(fhirResourceUtils.resolveEnumValue(output, ''verificationStatus'', (input.getStatus() == ''COMPLETED'' ? null : ''provisional''))); true'
WHERE id='1c673242-e309-43c4-a690-cb42da41fbb7' and version=0;
DELETE FROM fhir_script_source_version WHERE script_source_id='1c673242-e309-43c4-a690-cb42da41fbb7' AND fhir_version='R4';

INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('e3dad9c6-df15-4879-b1be-05f9fc78dc6e', 0, 'a3dba79b-bbc8-4335-874d-d7eb23f6c204',
'output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', ''entered-in-error'')); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('e3dad9c6-df15-4879-b1be-05f9fc78dc6e', 'R4');

INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('7264de56-5680-4b59-8ef0-189becc5fc90', 0, '372f8459-aa9a-44b9-bb8e-71a827265e52',
'output.setVerificationStatus(null); if (input.getStatus() != ''COMPLETED'') output.getVerificationStatus().addCoding().setSystem(''http://terminology.hl7.org/CodeSystem/condition-ver-status'').setCode(''provisional''); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('7264de56-5680-4b59-8ef0-189becc5fc90', 'R4');

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
else if (typeof output.assertedDateElement !== ''undefined'')
{
  output.setAssertedDateElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
updated' WHERE id='4a0b6fde-c0d6-4ad2-89da-992f4a47a115' and version=0;
