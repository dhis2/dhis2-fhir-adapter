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
    if (context.getDhisRequest().isDhisFhirId())
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
