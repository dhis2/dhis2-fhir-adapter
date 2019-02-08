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

UPDATE fhir_script_source
  SET source_text='(input.getStatus() == null) || (input.getStatus() == fhirResourceUtils.resolveEnumValue(input, ''status'', ''completed''))'
WHERE id='b038a06d-664a-4b97-bdd9-7d6adccd8c71';

UPDATE fhir_script_source SET source_text=
'var ok = false;
var code = null;
if (output.location)
{
  if (!output.getLocation().isEmpty() && !args[''overrideExisting''])
  {
    ok = true;
  }
  else if ((organizationUnit == null) || (organizationUnit.getCode() == null) || organizationUnit.getCode().isEmpty())
  {
    output.setLocation(null);
    ok = true;
  }
  else
  {
    code = codeUtils.getByMappedCode(organizationUnit.getCode(), ''LOCATION'');
    if ((code == null) && args[''useLocationIdentifierCode''])
    {
      code = codeUtils.getCodeWithoutPrefix(organizationUnit.getCode(), ''LOCATION'');
    }
  }
  if (code != null)
  {
    var resource = fhirClientUtils.findBySystemIdentifier(''LOCATION'', code);
    if (resource == null)
    {
      context.missingDhisResource(organizationUnit.getResourceId());
    }
    output.setLocation(null);
    if (typeof output.addLocation !== ''undefined'')
    {
      output.addLocation().getLocation().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
    }
    else
    {
      output.getLocation().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
    }
    ok = true;
  }
}
if (output.managingOrganization || output.performer || output.serviceProvider)
{
  if (((output.managingOrganization && !output.getManagingOrganization().isEmpty()) || (output.performer && !output.getPerformer().isEmpty()) || (output.serviceProvider && !output.getServiceProvider().isEmpty())) && !args[''overrideExisting''])
  {
    ok = true;
  }
  else if ((organizationUnit == null) || (organizationUnit.getCode() == null) || organizationUnit.getCode().isEmpty())
  {
    if (output.managingOrganization)
    {
      output.setManagingOrganization(null);
      ok = true;
    }
    else if (output.performer)
    {
      output.setPerformer(null);
      ok = true;
    }
    else if (output.serviceProvider)
    {
      output.setServiceProvider(null);
      ok = true;
    }
  }
  else
  {
    code = codeUtils.getByMappedCode(organizationUnit.getCode(), ''ORGANIZATION'');
    if ((code == null) && args[''useIdentifierCode''])
    {
      code = codeUtils.getCodeWithoutPrefix(organizationUnit.getCode(), ''ORGANIZATION'');
    }
  }
  if (code != null)
  {
    var resource = fhirClientUtils.findBySystemIdentifier(''ORGANIZATION'', code);
    if (resource == null)
    {
      context.missingDhisResource(organizationUnit.getResourceId());
    }
    if (output.managingOrganization)
    {
      output.setManagingOrganization(null);
      output.getManagingOrganization().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      ok = true;
    }
    else if (output.performer)
    {
      output.setPerformer(null);
      var performer = output.addPerformer();
      if (typeof performer.actor === ''undefined'')
      {
        performer.setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      }
      else
      {
        performer.getActor().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      }
      ok = true;
    }
    else if (output.serviceProvider)
    {
      output.setServiceProvider(null);
      output.getServiceProvider().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      ok = true;
    }
  }
}
ok' WHERE id = '78c2b73c-469c-4ab4-8244-07e817b72d4a';

UPDATE fhir_script_source SET source_text = 'var result;
var given = input.getBooleanValue(args[''dataElement'']);
if (given)
{
  output.setVaccineCode(codeUtils.getRuleCodeableConcept());
  if (output.getVaccineCode().isEmpty())
  {
    output.getVaccineCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
  }
  output.setPrimarySource(input.isProvidedElsewhere(args[''dataElement'']) == false);
  output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', ''completed''));
  result = true;
}
else
{
  output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', ''not-done''));
  result = !output.getIdElement().isEmpty();
}
result' WHERE id = '178b9811-ce7d-4d21-8580-e3baf52068e4';

INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('9014b962-bdad-4c79-8dcd-2cd74bf2f299', 0, '30760578-0c97-43be-ad28-324ccbbad249',
'output.setCode(codeUtils.getRuleCodeableConcept());
if (output.getCode().isEmpty())
{
  output.getCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
}
output.setCategory(null);
output.addCategory().addCoding().setSystem(''http://hl7.org/fhir/observation-category'').setCode(''survey'').setDisplay(''Survey'');
var score = input.getIntegerValue(args[''dataElement'']);
output.addChild(''valueQuantity'').setValue(score).setCode(''{score}'');
var comment = input.getStringValue(args[''commentDataElement'']);
output.setNote(null);
if ((comment != null) && (comment.length() > 0))
{
  output.addNote().setText(comment);
}
true', 'JAVASCRIPT');
DELETE FROM fhir_script_source_version WHERE script_source_id='2fc991ef-4ece-4a5a-a095-b7c976b6ef00' AND fhir_version='R4';
INSERT INTO fhir_script_source_version(script_source_id, fhir_version) VALUES ('9014b962-bdad-4c79-8dcd-2cd74bf2f299', 'R4');


