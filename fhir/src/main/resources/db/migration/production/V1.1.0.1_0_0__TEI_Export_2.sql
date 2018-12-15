/*
 *  Copyright (c) 2004-2018, University of Oslo
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

INSERT INTO fhir_dhis_resource_type_enum VALUES('ORGANIZATION_UNIT');
INSERT INTO fhir_transform_data_type_enum VALUES('DHIS_ORGANIZATION_UNIT');
INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_ORGANIZATION');

ALTER TABLE fhir_organization_unit_rule
  ADD COLUMN identifier_lookup_script_id UUID NOT NULL,
  ADD CONSTRAINT fhir_organization_unit_rule_fk2
    FOREIGN KEY(identifier_lookup_script_id) REFERENCES fhir_executable_script(ID);
CREATE INDEX fhir_organization_unit_rule_i1 ON fhir_organization_unit_rule(identifier_lookup_script_id);
COMMENT ON COLUMN fhir_organization_unit_rule.identifier_lookup_script_id IS 'References the executable script that performs the lookup of the organization identifier.';

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('7c350b43-3779-42d6-a9c9-74e567f6c066', 0, 'Returns FHIR Identifier for DHIS Org Unit', 'DHIS_ORG_UNIT_IDENTIFIER',
        'Returns the FHIR Identifier for the DHIS Org Unit.', 'EVALUATE', 'STRING', NULL, NULL);
UPDATE fhir_script SET base_script_id = (SELECT id FROM fhir_script WHERE id = 'a250e109-a135-42b2-8bdb-1c050c1d384c')
WHERE id='7c350b43-3779-42d6-a9c9-74e567f6c066';
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('7c350b43-3779-42d6-a9c9-74e567f6c066', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('7c350b43-3779-42d6-a9c9-74e567f6c066', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('eca2e74d-ac80-49bd-bebf-6b9129557567', 0, '7c350b43-3779-42d6-a9c9-74e567f6c066',
'var code = null;
if ((input.getCode() != null) && !input.getCode().isEmpty())
{
  code = codeUtils.getByMappedCode(organizationUnit.getCode(), ''ORGANIZATION'');
  if ((code == null) && args[''useIdentifierCode''])
  {
    code = codeUtils.getCodeWithoutPrefix(organizationUnit.getCode(), ''ORGANIZATION'');
  }
}
code', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('eca2e74d-ac80-49bd-bebf-6b9129557567', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('66d12e44-471c-4318-827a-0b397f694b6a', 0, '7c350b43-3779-42d6-a9c9-74e567f6c066',
        'Returns FHIR Identifier for DHIS Org Unit', 'DHIS_ORG_UNIT_IDENTIFIER',
        'Returns FHIR Identifier for the DHIS Org Unit.');
-- Link to base script for transformation of Org Unit Code from FHIR Resource
-- (may have been deleted in the meanwhile)
UPDATE fhir_executable_script SET base_executable_script_id = (SELECT id FROM fhir_executable_script WHERE id = '25a97bb4-7b39-4ed4-8677-db4bcaa28ccf')
WHERE id='66d12e44-471c-4318-827a-0b397f694b6a';

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('f8faecad-964f-402c-8cb4-a8271085528d', 0, 'Returns if DHIS Org Unit is applicable for export', 'EXP_DHIS_ORG_UNIT_APPLICABLE',
        'Returns if the DHIS Org Unit is applicable for export.', 'EVALUATE', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('f8faecad-964f-402c-8cb4-a8271085528d', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('f8faecad-964f-402c-8cb4-a8271085528d', 'INPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('6aaa5d0b-5dd1-423f-bb3f-dffc9f42f03f', 0, 'f8faecad-964f-402c-8cb4-a8271085528d',
'facilityLevel', 'INTEGER', TRUE, '4', 'Specifies the organization unit level in  which facilities are located.');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('ca71ef86-a93e-47d6-ab2d-645a977165a9', 0, 'f8faecad-964f-402c-8cb4-a8271085528d',
'input.getLevel() >= args[''facilityLevel'']', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('ca71ef86-a93e-47d6-ab2d-645a977165a9', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a8485045-4d62-4b3a-9cfd-5c36760b8d45', 0, 'f8faecad-964f-402c-8cb4-a8271085528d',
        'Returns if DHIS Org Unit is applicable for export', 'EXP_DHIS_ORG_UNIT_APPLICABLE',
        'Returns if the DHIS Org Unit is applicable for export.');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('53fdf1da-6145-4e25-9d25-fc41b9baf09f', 0, 'Transforms DHIS Org Unit to FHIR Organization', 'TRANSFORM_DHIS_ORG_UNIT_FHIR_ORG',
        'Transforms DHIS Organization Unit to FHIR Organization.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_ORGANIZATION_UNIT', 'FHIR_ORGANIZATION', 'f8faecad-964f-402c-8cb4-a8271085528d');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('53fdf1da-6145-4e25-9d25-fc41b9baf09f', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('53fdf1da-6145-4e25-9d25-fc41b9baf09f', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('50544af8-cf52-4e1e-9a5d-d44c736bc8d8', 0, '53fdf1da-6145-4e25-9d25-fc41b9baf09f',
'output.setPartOf(null);
output.getType().clear();
if ((input.getLevel() > args[''facilityLevel'']) && (input.getParentId() != null))
{
  var parentOrganizationUnit = organizationUnitResolver.getMandatoryById(input.getParentId());
  var parentOrganization = organizationUnitResolver.getFhirResource(parentOrganizationUnit);
  if (parentOrganization == null)
  {
    context.missingDhisResource(parentOrganizationUnit.getResourceId());
  }
  output.getPartOf().setReferenceElement(parentOrganization.getIdElement().toUnqualifiedVersionless());
}
else
{
  output.getType().addCoding().setSystem("http://hl7.org/fhir/organization-type").setCode("prov");
}
output.setActive(input.getClosedDate() == null);
output.setName(input.getName());
output.getAlias().clear();
if (input.getShortName() != null)
{
  output.addAlias(input.getShortName());
}
if (input.getDisplayName() != null)
{
  output.addAlias(input.getDisplayName());
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('50544af8-cf52-4e1e-9a5d-d44c736bc8d8', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description, base_executable_script_id)
VALUES ('50544af8-cf52-4e1e-9a5d-d44c736bc8d8', 0, '53fdf1da-6145-4e25-9d25-fc41b9baf09f',
        'Transforms DHIS Org Unit to FHIR Organization', 'TRANSFORM_DHIS_ORG_UNIT_FHIR_ORG',
        'Transforms DHIS Organization Unit to FHIR Organization.', 'a8485045-4d62-4b3a-9cfd-5c36760b8d45');

INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, imp_enabled, applicable_exp_script_id, transform_exp_script_id)
VALUES ('d0e1472a-05e6-47c9-b36b-ff1f06fec352', 0, 'DHIS Organization Unit to FHIR Organization', NULL, TRUE, 0, 'ORGANIZATION', 'ORGANIZATION_UNIT', FALSE, 'a8485045-4d62-4b3a-9cfd-5c36760b8d45', '50544af8-cf52-4e1e-9a5d-d44c736bc8d8');
INSERT INTO fhir_organization_unit_rule(id, identifier_lookup_script_id) VALUES ('d0e1472a-05e6-47c9-b36b-ff1f06fec352', '66d12e44-471c-4318-827a-0b397f694b6a');
