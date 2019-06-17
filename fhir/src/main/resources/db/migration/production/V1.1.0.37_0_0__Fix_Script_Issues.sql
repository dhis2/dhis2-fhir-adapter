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

UPDATE fhir_script SET script_type='TRANSFORM_TO_DHIS',
 input_type='FHIR_OBSERVATION', output_type='DHIS_EVENT'
 WHERE ID='344b66b9-ffdf-40e3-8a2a-421fc11ef8eb';

UPDATE fhir_script_source SET source_text=
'var providedElsewhere = input.hasPrimarySource() ? !input.getPrimarySource() : null;
var notGiven = (input.getStatus() == fhirResourceUtils.resolveEnumValue(input, ''status'', ''not-done''));
output.setValue(args[''dataElement''], !notGiven, providedElsewhere);
if (args[''dataElement2''] != null && args[''reasonCodeSetCode''] != null)
{
  if (notGiven && input.statusReason !== ''undefined'')
  {
    output.setValue(args[''dataElement2''], codeUtils.getMappedValueSetCode(args[''reasonCodeSetCode''], input.statusReason), providedElsewhere);
  }
  else
  {
    output.setValue(args[''dataElement2''], null, providedElsewhere);
  }
}
true' WHERE id='cb2c0544-c0c0-4e5c-9a36-5f9e8eb1059c';

UPDATE fhir_script_source SET source_text=
'var ok = false;
var code = null;
if (output.location)
{
  if (context.getDhisRequest().isDhisFhirId())
  {
    var ref = context.getDhisFhirResourceReference(organizationUnit, ''Location'');
    if (ref != null)
    {
      if (typeof output.addLocation === ''undefined'')
      {
        output.setLocation(ref);
      }
      else
      {
        output.addLocation().setLocation(ref);
      }
      ok = true;
    }
  }
  else if (!output.getLocation().isEmpty() && !args[''overrideExisting''])
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
if (!ok && (output.managingOrganization || output.performer || output.serviceProvider || output.requester))
{
  if (context.getDhisRequest().isDhisFhirId())
  {
    var ref = context.getDhisFhirResourceReference(organizationUnit, ''Organization'');
    if (ref != null)
    {
      if (output.managingOrganization)
      {
        output.setManagingOrganization(ref);
        ok = true;
      }
      else if (output.performer)
      {
        output.setPerformer(null);
        var performer;
        if (typeof output.addPerformer === ''undefined'')
        {
          performer = output.getPerformer();
        }
        else
        {
          performer = output.addPerformer();
        }
        if (typeof performer.actor === ''undefined'')
        {
          performer.setReferenceElement(ref.getReferenceElement());
        }
        else
        {
          performer.setActor(ref);
        }
        ok = true;
      }
      else if (output.serviceProvider)
      {
        output.setServiceProvider(ref);
        ok = true;
      }
      else if (output.requester && output.requester.agent)
      {
        output.setRequester(null);
        output.getRequester().setAgent(ref);
        ok = true;
      }
    }
  }
  else if (((output.managingOrganization && !output.getManagingOrganization().isEmpty()) || (output.performer && !output.getPerformer().isEmpty()) || (output.serviceProvider && !output.getServiceProvider().isEmpty())) && !args[''overrideExisting''])
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
    else if (output.requester && output.requester.agent)
    {
      output.setRequester(null);
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
      var performer;
      if (typeof output.addPerformer === ''undefined'')
      {
        performer = output.getPerformer();
      }
      else
      {
        performer = output.addPerformer();
      }
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
    else if (output.requester && output.requester.agent)
    {
      output.setRequester(null);
      output.getRequester().getAgent().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
      ok = true;
    }
  }
}
ok' WHERE id = '78c2b73c-469c-4ab4-8244-07e817b72d4a';

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
else if (input.performer && typeof input.getPerformerFirstRep() !== ''undefined'' && !input.getPerformerFirstRep().isEmpty() && (input.getPerformerFirstRep().getReferenceElement().getResourceType() == null || input.getPerformerFirstRep().getReferenceElement().getResourceType() == ''Organization''))
{
  fhirOrgUnitRef = input.getPerformerFirstRep();
  fhirOrgUnitType = input.getPerformerFirstRep().getReferenceElement().getResourceType();
  if (fhirOrgUnitType == null)
  {
    fhirOrgUnitType = ''Organization'';
  }
}
else if (input.performer && typeof input.getPerformerFirstRep === ''undefined'' && !input.getPerformer().isEmpty() && (input.getPerformer().getReferenceElement().getResourceType() == null || input.getPerformer().getReferenceElement().getResourceType() == ''Organization''))
{
  fhirOrgUnitRef = input.getPerformer();
  fhirOrgUnitType = input.getPerformer().getReferenceElement().getResourceType();
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
else if (input.requester && input.requester.agent)
{
  fhirOrgUnitRef = input.getRequester().getAgent();
  fhirOrgUnitType = input.getRequester().getAgent().getReferenceElement().getResourceType();
  if (fhirOrgUnitType == null)
  {
    fhirOrgUnitType = ''Organization'';
  }
  ok = true;
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
