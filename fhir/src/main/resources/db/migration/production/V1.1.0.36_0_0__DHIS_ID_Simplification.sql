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
'function getCodeFromHierarchy(hierarchy, resourceType, resourceRef)
{
  var mappedCode = null;
  if (hierarchy != null)
  {
    for (var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++)
    {
      var code = identifierUtils.getResourceIdentifier(hierarchy.get(i), resourceType);
      if (code != null)
      {
        mappedCode = codeUtils.getMappedCode(code, resourceType);
        if ((mappedCode == null) && args[''useIdentifierCode''])
        {
          mappedCode = organizationUtils.existsWithPrefix(code);
        }
      }
    }
  }
  return mappedCode;
}
function getOrganizationUnitMappedCode(organizationReference)
{
  var mappedCode = null;
  if (organizationReference != null)
  {
    var hierarchy = organizationUtils.findHierarchy(organizationReference);
    mappedCode = getCodeFromHierarchy(hierarchy, ''Organization'', organizationReference);
  }
  return mappedCode;
}
function getLocationMappedCode(locationReference)
{
  var mappedCode = null;
  if (locationReference != null)
  {
    var hierarchy = locationUtils.findHierarchy(locationReference);
    if (hierarchy != null)
    {
      getCodeFromHierarchy(hierarchy, ''Location'', locationReference);
      for (var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++)
      {
        var organizationReference = hierarchy.get(i).getManagingOrganization();
        if (organizationReference != null)
        {
          mappedCode = getOrganizationUnitMappedCode(organizationReference);
        }
      }
    }
  }
  return mappedCode;
}
var fhirOrgUnitRef = null;
var fhirOrgUnitType = null;
if (input.managingOrganization)
{
  fhirOrgUnitRef = input.managingOrganization;
  fhirOrgUnitType = ''Organization'';
}
else if ( (typeof input.locationFirstRep !== ''undefined'') && (typeof input.locationFirstRep.location !== ''undefined'') )
{
  fhirOrgUnitRef = input.locationFirstRep.location;
  fhirOrgUnitType = ''Location'';
}
else if (input.location)
{
  fhirOrgUnitRef = input.location;
  fhirOrgUnitType = ''Location'';
}
else if (input.performer && !input.getPerformerFirstRep().isEmpty() && (input.getPerformerFirstRep().getReferenceElement().getResourceType() == null || input.getPerformerFirstRep().getReferenceElement().getResourceType() == ''Organization''))
{
  fhirOrgUnitRef = input.getPerformerFirstRep();
  fhirOrgUnitType = input.getPerformerFirstRep().getReferenceElement().getResourceType();
  if (fhirOrgUnitType == null)
  {
    fhirOrgUnitType = ''Organization'';
  }
}
else if (input.serviceProvider)
{
  fhirOrgUnitRef = input.serviceProvider;
  fhirOrgUnitType = ''Organization'';
}
else if (input.author)
{
  fhirOrgUnitRef = input.author;
  fhirOrgUnitType = ''Organization'';
}
else if (input.encounter)
{
  var encounter = referenceUtils.getResource(input.encounter, ''Encounter'');
  if (encounter != null)
  {
    fhirOrgUnitRef = getLocationReference(encounter.location);
    fhirOrgUnitType = ''Location'';
  }
}
var ref = null;
if (fhirOrgUnitRef != null)
{
  ref = fhirResourceUtils.getAdapterReference(fhirOrgUnitRef, fhirOrgUnitType);
}
if (ref == null)
{
  if (context.getFhirRequest().isDhisFhirId())
  {
    if (fhirOrgUnitRef == null)
    {
      if (args[''useTei''] && (typeof trackedEntityInstance !== ''undefined''))
      {
        ref = context.createReference(trackedEntityInstance.organizationUnitId, ''ID'');
      }
      else if ((typeof enrollment !== ''undefined'') && (enrollment.organizationUnitId != null))
      {
        ref = context.createReference(enrollment.organizationUnitId, ''ID'');
      }
    }
    else
    {
      if (fhirOrgUnitRef != null && fhirOrgUnitRef.identifier !== ''undefined'' && fhirOrgUnitRef.hasIdentifier())
      {
        ref = context.createReference(identifierUtils.getReferenceIdentifier(fhirOrgUnitRef, fhirOrgUnitType), ''code'');
      }
      if (ref == null)
      {
        ref = context.createReference(context.extractDhisId(fhirOrgUnitRef.getReferenceElement()), ''id'');
      }
    }
  }
  else
  {
    var mappedCode = null;
    if (fhirOrgUnitRef != null)
    {
      if (fhirOrgUnitType == ''Location'')
      {
        mappedCode = getLocationMappedCode(fhirOrgUnitRef);
      }
      else
      {
        mappedCode = getOrganizationUnitMappedCode(fhirOrgUnitRef);
      }
    }
    if (mappedCode == null)
    {
      mappedCode = args[''defaultCode''];
    }
    if (mappedCode != null)
    {
      ref = context.createReference(mappedCode, ''CODE'');
    }
    if ((ref == null) && args[''useTei''] && (typeof trackedEntityInstance !== ''undefined''))
    {
      ref = context.createReference(trackedEntityInstance.organizationUnitId, ''ID'');
    }
    else if ((ref == null) && (typeof enrollment !== ''undefined'') && (enrollment.organizationUnitId != null))
    {
      ref = context.createReference(enrollment.organizationUnitId, ''ID'');
    }
  }
}
ref' WHERE id='7b94feba-bcf6-4635-929a-01311b25d975' AND version=0;

UPDATE fhir_script_source SET source_text='fhirResourceUtils.getIdentifiedResource(input.subject, ''Patient'')'
WHERE id='960d2e6c-2479-48a2-b04e-b14879e71d14' AND version=0;

UPDATE fhir_script_source SET source_text='fhirResourceUtils.getIdentifiedResource(input.patient, ''Patient'')'
WHERE id='1f94dda8-28ec-480f-8c6b-d8d734612414' AND version=0;

UPDATE fhir_script_source SET source_text='fhirResourceUtils.getIdentifiedResource(input.patient, ''Patient'')'
WHERE id='85b3c460-6c2a-4f50-af46-ff09bf2e69df' AND version=0;

UPDATE fhir_script_source SET source_text='fhirResourceUtils.getIdentifiedResource(input.subject, ''Patient'')'
WHERE id='67249e7b-4ba7-466c-a770-a78923fbf1c3' AND version=0;
