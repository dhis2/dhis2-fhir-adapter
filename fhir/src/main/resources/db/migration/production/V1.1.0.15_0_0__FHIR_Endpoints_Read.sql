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

ALTER TABLE fhir_rule ADD grouping BOOLEAN DEFAULT FALSE NOT NULL;
COMMENT ON COLUMN fhir_rule.grouping IS 'Specifies if the FHIR resources groups references of other FHIR resources (e.g. FHIR Encounter).';

UPDATE fhir_rule SET grouping=TRUE WHERE id IN ('1f2da6ec-41b0-4b64-99d9-e98fca864b0f', '9d342f13-aec1-4629-9d65-4f03fd0e848c');

UPDATE fhir_script_source SET source_text= 'var result;
var doseGiven = input.getIntegerOptionValue(args[''dataElement''], 1, args[''optionValuePattern'']);
if (doseGiven == null)
{
  output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', ''not-done''));
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
  output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', ''completed''));
  output.setNotGiven(false);
  output.setVaccinationProtocol(null);
  output.addVaccinationProtocol().setDoseSequence(doseGiven);
  result = true;
}
result' WHERE id='78b4af9b-c247-4290-abee-44983938b265' AND version=0;

UPDATE fhir_script_source SET source_text=
'output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', ''not-done'')); output.setNotGiven(true); true' WHERE id='235ffb1a-133e-4046-bdd3-498cf0ff771d' AND version=0;

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
if (!ok && (output.managingOrganization || output.performer || output.serviceProvider))
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
        var performer = output.addPerformer();
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

UPDATE fhir_script_source SET source_text =
'output.setPartOf(null);
output.getType().clear();
if ((input.getLevel() > args[''facilityLevel'']) && (input.getParentId() != null))
{
  if (context.getDhisRequest().isIncludeReferences())
  {
    var parentOrganizationUnit = organizationUnitResolver.getMandatoryById(input.getParentId());
    var parentOrganizationRef = null;
    if (context.getDhisRequest().isDhisFhirId())
    {
      parentOrganizationRef = context.getDhisFhirResourceReference(parentOrganizationUnit, ''Organization'');
    }
    else
    {
      var parentOrganization = organizationUnitResolver.getFhirResource(parentOrganizationUnit);
      if (parentOrganization == null)
      {
        context.missingDhisResource(parentOrganizationUnit.getResourceId());
      }
      parentOrganizationRef = fhirResourceUtils.createReference(parentOrganization);
    }
    output.setPartOf(parentOrganizationRef);
  }
}
else
{
  output.addType().setText(args[''facilityTypeDisplayName'']).addCoding().setSystem(''http://hl7.org/fhir/organization-type'').setCode(''prov'');
}
output.setActive(input.getClosedDate() == null);
output.setName(input.getName());
output.getAlias().clear();
if ((input.getShortName() != null) && !input.getShortName().equals(input.getName()))
{
  output.addAlias(input.getShortName());
}
if ((input.getDisplayName() != null) && !input.getDisplayName().equals(input.getName()) && !fhirResourceUtils.containsString(output.getAlias(), input.getDisplayName()))
{
  output.addAlias(input.getDisplayName());
}
true' WHERE id='50544af8-cf52-4e1e-9a5d-d44c736bc8d8' AND version=0;

UPDATE fhir_script_source SET source_text=
'output.setManagingOrganization(null);
output.setOperationalStatus(null);
output.setPartOf(null);
output.setPhysicalType(null);
if (input.getParentId() != null)
{
  if (context.getDhisRequest().isIncludeReferences())
  {
    var parentOrganizationUnit = organizationUnitResolver.getMandatoryById(input.getParentId());
    var parentLocationRef = null;
    if (context.getDhisRequest().isDhisFhirId())
    {
      parentLocationRef = context.getDhisFhirResourceReference(parentOrganizationUnit, ''Location'');
    }
    else
    {
      var parentLocation = organizationUnitResolver.getFhirResource(parentOrganizationUnit);
      if (parentLocation == null)
      {
        context.missingDhisResource(parentOrganizationUnit.getResourceId());
      }
      parentLocationRef = fhirResourceUtils.createReference(parentLocation);
    }
    output.setPartOf(parentLocationRef);
  }
}
if (input.getLevel() === args[''facilityLevel''])
{
  if (context.getDhisRequest().isIncludeReferences())
  {
    var organizationRef = null;
    if (context.getDhisRequest().isIncludeReferences())
    {
      organizationRef = context.getDhisFhirResourceReference(input, ''Organization'');
    }
    else
    {
      var organization = organizationUnitResolver.getFhirResource(input, ''ORGANIZATION'');
      if (organization == null)
      {
        context.missingDhisResource(input.getResourceId());
      }
      organizationRef = fhirResourceUtils.createReference(organization);
    }
    output.setManagingOrganization(organizationRef);
  }
  output.getPhysicalType().addCoding().setSystem(''http://hl7.org/fhir/location-physical-type'').setCode(''si'').setDisplay(''Site'');
}
if (input.getLevel() < args[''facilityLevel''])
{
  output.getPhysicalType().addCoding().setSystem(''http://hl7.org/fhir/location-physical-type'').setCode(''jdn'').setDisplay(''Jurisdiction'');
}
if (input.getClosedDate() == null)
{
  output.setStatus(locationUtils.getLocationStatus(''active''));
}
else
{
  output.setStatus(locationUtils.getLocationStatus(''inactive''));
}
output.setPosition(geoUtils.createLocationPosition(input.getCoordinates()));
output.setName(input.getName());
output.getAlias().clear();
if ((input.getShortName() != null) && !input.getShortName().equals(input.getName()))
{
  output.addAlias(input.getShortName());
}
if ((input.getDisplayName() != null) && !input.getDisplayName().equals(input.getName()) && !fhirResourceUtils.containsString(output.getAlias(), input.getDisplayName()))
{
  output.addAlias(input.getDisplayName());
}
true' WHERE id='4bb9745e-43c0-455f-beee-98e95c83df3d' AND version=0;
