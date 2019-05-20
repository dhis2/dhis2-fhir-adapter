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

UPDATE fhir_script_source SET source_text='var fhirResource = null;
if (context.getFhirRequest().isDhisFhirId())
{
  fhirResource = fhirResourceUtils.createResource(''Patient'');
  fhirResource.setId(input.getPatient().getResource() == null ?  input.getPatient().getReferenceElement() : input.getPatient().getResource().getId());
}
else
{
  fhirResource = referenceUtils.getResource(input.patient, ''Patient'');
}
fhirResource'
WHERE id='85b3c460-6c2a-4f50-af46-ff09bf2e69df' AND version=0;

UPDATE fhir_script_source SET source_text='var fhirResource = null;
if (context.getFhirRequest().isDhisFhirId())
{
  fhirResource = fhirResourceUtils.createResource(''Patient'');
  fhirResource.setId(input.getSubject().getResource() == null ? input.getSubject().getReferenceElement() : input.getSubject().getResource().getId());
}
else
{
  fhirResource = referenceUtils.getResource(input.subject, ''Patient'');
}
fhirResource'
WHERE id='960d2e6c-2479-48a2-b04e-b14879e71d14' AND version=0;
