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

ALTER TABLE fhir_rule ADD COLUMN simple_fhir_id BOOLEAN DEFAULT FALSE NOT NULL;
COMMENT ON COLUMN fhir_rule.simple_fhir_id IS 'Specifies if the FHIR ID is just the DHIS2 ID without the rule ID. This feature must only be enabled if there is a distinct rule for the combination of FHIR and DHIS2 resource type.';


UPDATE fhir_rule SET simple_fhir_id=true WHERE id='52227dd9-c79c-478b-92af-9aa1f33c76fd';
UPDATE fhir_rule SET simple_fhir_id=true WHERE id='c4e17e7d-880e-45b5-9bc5-568da8c79742';
UPDATE fhir_rule SET simple_fhir_id=true WHERE id='b9546b02-4adc-4868-a4cd-d5d7789f0df0';
UPDATE fhir_rule SET simple_fhir_id=true WHERE id='d0e1472a-05e6-47c9-b36b-ff1f06fec352';
UPDATE fhir_rule SET simple_fhir_id=true WHERE id='5f9ebdc9-852e-4c83-87ca-795946aabc35';
