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
  if (input.getPatient().hasIdentifier() && fhirResource.identifier !== ''undefined'')
  {
    fhirResource.addIdentifier(input.getPatient().getIdentifier());
  }
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
  if (input.getSubject().hasIdentifier() && fhirResource.identifier !== ''undefined'')
  {
    fhirResource.addIdentifier(input.getSubject().getIdentifier());
  }
}
else
{
  fhirResource = referenceUtils.getResource(input.subject, ''Patient'');
}
fhirResource'
WHERE id='960d2e6c-2479-48a2-b04e-b14879e71d14' AND version=0;

UPDATE fhir_script_source SET source_text=
'function getOrganizationUnitMappedCode(organizationReference)
{
  var mappedCode = null;
  if (organizationReference != null)
  {
    var hierarchy = organizationUtils.findHierarchy(organizationReference);
    if (hierarchy != null)
    {
      for (var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++)
      {
        var code = identifierUtils.getResourceIdentifier(hierarchy.get(i), ''ORGANIZATION'');
        if (code != null)
        {
          mappedCode = codeUtils.getMappedCode(code, ''ORGANIZATION'');
          if ((mappedCode == null) && args[''useIdentifierCode''])
          {
            mappedCode = organizationUtils.existsWithPrefix(code);
          }
        }
      }
    }
  }
  return mappedCode;
}
function getLocationReference(location)
{
  if (typeof input.locationFirstRep === ''undefined'')
  {
    return location;
  }
  else if (typeof input.locationFirstRep.location !== ''undefined'')
  {
    return input.locationFirstRep.location;
  }
  return null;
}
function getMappedCode(resource)
{
  var  mappedCode = null ;
  if (resource.managingOrganization)
  {
    mappedCode = getOrganizationUnitMappedCode(resource.managingOrganization);
  }
  else if (resource.location)
  {
    var locationReference = getLocationReference(resource.location);
    if (locationReference != null)
    {
      var hierarchy = locationUtils.findHierarchy(locationReference);
      if (hierarchy != null)
      {
        for (var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++)
        {
          var code = identifierUtils.getResourceIdentifier(hierarchy.get(i), ''LOCATION'');
          if (code != null)
          {
            mappedCode = codeUtils.getMappedCode(code, ''LOCATION'');
            if ((mappedCode == null) && args[''useIdentifierCode''])
            {
              mappedCode = locationUtils.existsWithPrefix(code);
            }
          }
        }
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
  }
  else if (resource.performer && !resource.getPerformerFirstRep().isEmpty() && resource.getPerformerFirstRep().getReferenceElement().getResourceType() == ''Organization'')
  {
    mappedCode = getOrganizationUnitMappedCode(resource.getPerformerFirstRep());
  }
  else if (resource.serviceProvider)
  {
    mappedCode = getOrganizationUnitMappedCode(resource.serviceProvider);
  }
  return mappedCode;
}
var ref = null;
if (context.getFhirRequest().isDhisFhirId())
{
  var fhirOrgUnitRef = null;
  var fhirOrgUnitType = null;
  if (input.managingOrganization)
  {
    fhirOrgUnitRef = input.managingOrganization;
    fhirOrgUnitType = ''Organization'';
  }
  else if (input.location)
  {
    fhirOrgUnitRef = input.location;
    fhirOrgUnitType = ''Location'';
  }
  else if (input.performer && !input.getPerformerFirstRep().isEmpty() && input.getPerformerFirstRep().getReferenceElement().getResourceType() == ''Organization'')
  {
    fhirOrgUnitRef = input.getPerformerFirstRep();
    fhirOrgUnitType = input.getPerformerFirstRep().getReferenceElement().getResourceType();
  }
  else if (input.serviceProvider)
  {
    fhirOrgUnitRef = input.serviceProvider;
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
  var mappedCode;
  if (input.managingOrganization || input.location || (input.performer && !input.getPerformerFirstRep().isEmpty() && input.getPerformerFirstRep().getReferenceElement().getResourceType() == ''Organization'') || input.serviceProvider)
  {
    mappedCode = getMappedCode(input);
  }
  else if (input.encounter)
  {
    var encounter = referenceUtils.getResource(input.encounter, ''Encounter'');
    if (encounter != null)
    {
      mappedCode = getMappedCode(encounter);
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

ref' WHERE id='7b94feba-bcf6-4635-929a-01311b25d975' AND version=0;

