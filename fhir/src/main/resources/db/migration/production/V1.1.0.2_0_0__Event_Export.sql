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

INSERT INTO fhir_script_variable_enum VALUES('TEI_FHIR_RESOURCE');

ALTER TABLE fhir_system ADD fhir_display_name VARCHAR(100);
COMMENT ON COLUMN fhir_system.fhir_display_name IS 'Display name of the system and its assigned identifiers and codes that is used when storing data on FHIR servers.';

UPDATE fhir_system SET fhir_display_name='CVX' WHERE system_uri='http://hl7.org/fhir/sid/cvx';
UPDATE fhir_system SET fhir_display_name='LOINC' WHERE system_uri='http://loinc.org';
UPDATE fhir_system SET fhir_display_name='SNOMED CT' WHERE system_uri='http://snomed.info/sct';
UPDATE fhir_system SET fhir_display_name='FHIR Personal Relationship Role Type V2' WHERE system_uri='http://hl7.org/fhir/v2/0131';
UPDATE fhir_system SET fhir_display_name='FHIR Personal Relationship Role Type V3' WHERE system_uri='http://hl7.org/fhir/v3/RoleCode';
UPDATE fhir_system SET fhir_display_name='DHIS2 FHIR ID' WHERE system_uri='http://www.dhis2.org/dhis2-fhir-adapter/systems/identifier';

UPDATE fhir_system SET system_uri='http://www.dhis2.org/dhis2-fhir-adapter/systems/DHIS2-FHIR-Identifier' WHERE system_uri='http://www.dhis2.org/dhis2-fhir-adapter/systems/identifier';

INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('aa665bc7-631a-4b6e-865d-115e18f8bf1b', 0, '53fdf1da-6145-4e25-9d25-fc41b9baf09f',
'facilityTypeDisplayName', 'STRING', FALSE, 'Facility', 'The optional display name for the facility type.');
UPDATE fhir_script_source SET source_text =
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
true' WHERE id='50544af8-cf52-4e1e-9a5d-d44c736bc8d8';

UPDATE fhir_script_source SET source_text =
'function canOverrideAddress(address)
{
  return !address.hasLine() && !address.hasCity() && !address.hasDistrict() && !address.hasState() && !address.hasPostalCode() && !address.hasCountry();
}
if (output.getName().size() < 2)
{
  var lastName = input.getValue(args[''lastNameAttribute'']);
  var firstName = input.getValue(args[''firstNameAttribute'']);
  if ((lastName != null) || (firstName != null) || args[''resetFhirValue''])
  {
    output.getName().clear();
    if ((lastName != null) || args[''resetFhirValue''])
    {
      output.getNameFirstRep().setFamily(lastName);
    }
    if ((firstName != null) || args[''resetFhirValue''])
    {
      humanNameUtils.updateGiven(output.getNameFirstRep(), firstName);
    }
  }
}
if (args[''birthDateAttribute''] != null)
{
  var birthDate = input.getValue(args[''birthDateAttribute'']);
  if ((birthDate != null) || args[''resetFhirValue''])
  {
    output.setBirthDateElement(dateTimeUtils.getPreciseDateElement(birthDate));
  }
}
if (args[''genderAttribute''] != null)
{
  var gender = input.getValue(args[''genderAttribute'']);
  if ((gender != null) || args[''resetFhirValue''])
  {
    output.setGender(genderUtils.getAdministrativeGender(gender));
  }
}
if ((args[''addressTextAttribute''] != null) && (output.getAddress().size() < 2))
{
  var addressText = input.getValue(args[''addressTextAttribute'']);
  if (((addressText != null) || args[''resetFhirValue'']) && (args[''resetAddressText''] || !output.hasAddress() || canOverrideAddress(output.getAddressFirstRep())))
  {
    output.getAddress().clear();
    output.addAddress().setText(addressText);
  }
  if (output.getAddressFirstRep().isEmpty())
  {
    output.setAddress(null);
  }
}
true' WHERE id = 'f0a48e63-cc1d-4d02-85fa-80c7e79a5d9e';

UPDATE fhir_script_source SET source_text=
'if (output.hasAddress() && (output.getAddress().size() < 2))
{
  geoUtils.updateAddress(output.getAddressFirstRep(), input.getCoordinates());
}
true' WHERE id = '650e2d25-82d7-450a-bac0-3a20f31817b2';

ALTER TABLE fhir_tracker_program_stage
  ADD COLUMN exp_enabled BOOLEAN DEFAULT FALSE NOT NULL,
  ADD COLUMN fhir_create_enabled BOOLEAN DEFAULT TRUE NOT NULL,
  ADD COLUMN fhir_update_enabled BOOLEAN DEFAULT FALSE NOT NULL;
COMMENT ON COLUMN fhir_tracker_program_stage.exp_enabled IS 'Specifies if transformation from DHIS to FHIR resource is enabled.';
COMMENT ON COLUMN fhir_tracker_program_stage.fhir_create_enabled IS 'Specifies if the creation of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule.';
COMMENT ON COLUMN fhir_tracker_program_stage.fhir_update_enabled IS 'Specifies if the update of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule.';

ALTER TABLE fhir_tracker_program
  ADD COLUMN exp_enabled BOOLEAN DEFAULT FALSE NOT NULL,
  ADD COLUMN fhir_create_enabled BOOLEAN DEFAULT TRUE NOT NULL,
  ADD COLUMN fhir_update_enabled BOOLEAN DEFAULT FALSE NOT NULL;
COMMENT ON COLUMN fhir_tracker_program.exp_enabled IS 'Specifies if transformation from DHIS to FHIR resource is enabled.';
COMMENT ON COLUMN fhir_tracker_program.fhir_create_enabled IS 'Specifies if the creation of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule.';
COMMENT ON COLUMN fhir_tracker_program.fhir_update_enabled IS 'Specifies if the update of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule.';

CREATE TABLE fhir_rule_dhis_data_ref (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  rule_id         UUID                           NOT NULL,
  data_ref        VARCHAR(230)                   NOT NULL,
  script_arg_name VARCHAR(30),
  required        BOOLEAN                        NOT NULL DEFAULT TRUE,
  description     TEXT,
  CONSTRAINT fhir_rule_dhis_data_ref_pk PRIMARY KEY (id),
  CONSTRAINT fhir_rule_dhis_data_ref_uk1 UNIQUE (rule_id, data_ref),
  CONSTRAINT fhir_rule_dhis_data_ref_fk1 FOREIGN KEY (rule_id) REFERENCES fhir_rule(id) ON DELETE CASCADE
);
CREATE INDEX fhir_rule_dhis_data_i1 ON fhir_rule_dhis_data_ref (rule_id);
COMMENT ON TABLE fhir_rule_dhis_data_ref IS 'Contains the references to DHIS2 data elements in which the data that is processed by the assigned rule is stored.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.rule_id IS 'The reference to the rule to which the data references belong to.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.data_ref IS 'The reference to the DHIS2 data element (or depending on the context also to the DHIS2 attribute).';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.script_arg_name IS 'The name of the script argument to which the data reference (as reference argument type) should be passed.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.required IS 'Specifies if a value for the data element is required in order to perform the transformation. It is also used to remove existing FHIR resources when values are reset.';
COMMENT ON COLUMN fhir_rule_dhis_data_ref.description IS 'The detailed description of the purpose of the reference to the data element.';

ALTER TABLE fhir_resource_mapping RENAME COLUMN tei_lookup_script_id TO imp_tei_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN enrollment_org_lookup_script_id TO imp_enrollment_org_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN event_org_lookup_script_id TO imp_event_org_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN enrollment_date_lookup_script_id TO imp_enrollment_date_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN event_date_lookup_script_id TO imp_event_date_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN enrollment_loc_lookup_script_id TO imp_enrollment_geo_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN event_loc_lookup_script_id TO imp_event_geo_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN effective_date_lookup_script_id TO imp_effective_date_lookup_script_id;

ALTER TABLE fhir_tracker_program
  ADD COLUMN tracked_entity_fhir_resource_type VARCHAR(30) DEFAULT 'PATIENT' NOT NULL,
  ADD CONSTRAINT fhir_tracker_program_fk6 FOREIGN KEY(tracked_entity_fhir_resource_type) REFERENCES fhir_resource_type_enum(value);

ALTER TABLE fhir_resource_mapping
  ADD COLUMN exp_ou_transform_script_id UUID,
  ADD CONSTRAINT fhir_resource_mapping_fk9 FOREIGN KEY (exp_ou_transform_script_id) REFERENCES fhir_executable_script(id),
  ADD COLUMN exp_geo_transform_script_id UUID,
  ADD CONSTRAINT fhir_resource_mapping_fk10 FOREIGN KEY (exp_geo_transform_script_id) REFERENCES fhir_executable_script(id),
  ADD COLUMN exp_date_transform_script_id UUID,
  ADD CONSTRAINT fhir_resource_mapping_fk11 FOREIGN KEY (exp_date_transform_script_id) REFERENCES fhir_executable_script(id);

-- Script that sets for a data element if a FHIR Immunization has been given
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('c8a937b5-665b-485c-bbc9-7a83e21a4e47', 0, 'TRANSFORM_DHIS_IMMUNIZATION_YN', 'Transforms DHIS Immunization Y/N data element to FHIR', 'Transforms DHIS Immunization Y/N data element to FHIR.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_IMMUNIZATION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('c8a937b5-665b-485c-bbc9-7a83e21a4e47', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('c8a937b5-665b-485c-bbc9-7a83e21a4e47', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('c8a937b5-665b-485c-bbc9-7a83e21a4e47', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('36c2d0a9-d923-4476-8ab5-b94808bd5ade', 0, 'c8a937b5-665b-485c-bbc9-7a83e21a4e47',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element with given vaccine on which Y/N must be set.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('6ede1e58-17c1-49bd-a7e4-36ef9c87521f', 0, 'c8a937b5-665b-485c-bbc9-7a83e21a4e47',
'var result;
var given = input.getBooleanValue(args[''dataElement'']);
if (given == true)
{
  output.setVaccineCode(codeUtils.getRuleCodeableConcept());
  if (output.getVaccineCode().isEmpty())
  {
    output.getVaccineCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
  }
  output.setPrimarySource(input.isProvidedElsewhere(args[''dataElement'']) == false);
  output.setNotGiven(false);
  result = true;
}
else
{
  output.setNotGiven(true);
  result = !output.getIdElement().isEmpty();
}
result', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('6ede1e58-17c1-49bd-a7e4-36ef9c87521f', 'DSTU3');

ALTER TABLE fhir_code ADD enabled BOOLEAN DEFAULT TRUE NOT NULL;
COMMENT ON COLUMN fhir_code.enabled IS 'Specifies if the code should be used during evaluations and transformations.';
ALTER TABLE fhir_system_code ADD enabled BOOLEAN DEFAULT TRUE NOT NULL;
COMMENT ON COLUMN fhir_system_code.enabled IS 'Specifies if the code should be used during evaluations and transformations.';
ALTER TABLE fhir_code_set_value ADD preferred_export BOOLEAN DEFAULT FALSE NOT NULL;
COMMENT ON COLUMN fhir_code_set_value.preferred_export IS 'Specifies if the code should be used as preferred code when exporting data.';

UPDATE fhir_code_set SET name = 'Any ' || SUBSTR(name, 5) WHERE name LIKE 'All %';

ALTER TABLE fhir_system_code ADD display_name VARCHAR(230);
UPDATE fhir_system_code sc SET display_name = (SELECT c.name FROM fhir_code c WHERE c.id = sc.code_id);
UPDATE fhir_system_code SET display_name = SUBSTR(display_name, 4) WHERE display_name LIKE 'RC %';
ALTER TABLE fhir_system_code ALTER COLUMN display_name SET NOT NULL;

--- MMR is marked as preferred measles vaccine
UPDATE fhir_code_set_value SET preferred_export=TRUE
WHERE code_set_id='31c6b008-eb0d-48a3-970d-70725b92bd24' AND code_id='71f5536a-2587-45b9-88ac-9aba362a424a';

-- script may have been deleted in the meanwhile
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
SELECT '253bcef6-42c7-46b2-807d-9da823d59f24', 0, 'a250e109-a135-42b2-8bdb-1c050c1d384c', 'useLocationIdentifierCode', 'BOOLEAN', TRUE, 'true',
'Specifies if the identifier code itself with the default code prefix for locations should be used as fallback when no code mapping for the location identifier code exists.'
FROM fhir_script WHERE id = 'a250e109-a135-42b2-8bdb-1c050c1d384c';

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('655f55b5-3826-4fe7-b38e-1e29bcad09ec', 0, 'Returns FHIR Location Identifier for DHIS Org Unit', 'DHIS_ORG_UNIT_IDENTIFIER_LOC',
        'Returns the FHIR Location Identifier for the DHIS Org Unit.', 'EVALUATE', 'STRING', NULL, NULL);
UPDATE fhir_script SET base_script_id = (SELECT id FROM fhir_script WHERE id = 'a250e109-a135-42b2-8bdb-1c050c1d384c')
WHERE id='655f55b5-3826-4fe7-b38e-1e29bcad09ec';
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('655f55b5-3826-4fe7-b38e-1e29bcad09ec', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('655f55b5-3826-4fe7-b38e-1e29bcad09ec', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('1a2bf7fc-2908-4148-8a53-5edde593a4e9', 0, '655f55b5-3826-4fe7-b38e-1e29bcad09ec',
'var code = null;
if ((input.getCode() != null) && !input.getCode().isEmpty())
{
  code = codeUtils.getByMappedCode(input.getCode(), ''LOCATION'');
  if ((code == null) && args[''useIdentifierCode''])
  {
    code = codeUtils.getCodeWithoutPrefix(input.getCode(), ''LOCATION'');
  }
}
code', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('1a2bf7fc-2908-4148-8a53-5edde593a4e9', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('5090c485-a225-4c2e-a8f6-95fa74ce1618', 0, '655f55b5-3826-4fe7-b38e-1e29bcad09ec',
        'Returns FHIR Location Identifier for DHIS Org Unit', 'DHIS_ORG_UNIT_IDENTIFIER_LOC',
        'Returns FHIR Location Identifier for the DHIS Org Unit.');
-- Link to base script for transformation of Org Unit Code from FHIR Resource
-- (may have been deleted in the meanwhile)
UPDATE fhir_executable_script SET base_executable_script_id = (SELECT id FROM fhir_executable_script WHERE id = '25a97bb4-7b39-4ed4-8677-db4bcaa28ccf')
WHERE id='5090c485-a225-4c2e-a8f6-95fa74ce1618';

INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_LOCATION');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('b3568beb-5a5d-4059-9b4e-3afa0157adbc', 0, 'Transforms DHIS Org Unit to FHIR Location', 'TRANSFORM_DHIS_ORG_UNIT_FHIR_LOC',
        'Transforms DHIS Organization Unit to FHIR Organization.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_ORGANIZATION_UNIT', 'FHIR_LOCATION', 'f8faecad-964f-402c-8cb4-a8271085528d');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('b3568beb-5a5d-4059-9b4e-3afa0157adbc', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('b3568beb-5a5d-4059-9b4e-3afa0157adbc', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('4bb9745e-43c0-455f-beee-98e95c83df3d', 0, 'b3568beb-5a5d-4059-9b4e-3afa0157adbc',
'output.setManagingOrganization(null);
output.setOperationalStatus(null);
output.setPartOf(null);
output.setPhysicalType(null);
if (input.getParentId() != null)
{
  var parentOrganizationUnit = organizationUnitResolver.getMandatoryById(input.getParentId());
  var parentLocation = organizationUnitResolver.getFhirResource(parentOrganizationUnit);
  if (parentLocation == null)
  {
    context.missingDhisResource(parentOrganizationUnit.getResourceId());
  }
  output.getPartOf().setReferenceElement(parentLocation.getIdElement().toUnqualifiedVersionless());
}
if (input.getLevel() === args[''facilityLevel''])
{
  var organization = organizationUnitResolver.getFhirResource(input, ''ORGANIZATION'');
  if (organization == null)
  {
    context.missingDhisResource(input.getResourceId());
  }
  output.getManagingOrganization().setReferenceElement(organization.getIdElement().toUnqualifiedVersionless());
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
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('4bb9745e-43c0-455f-beee-98e95c83df3d', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description, base_executable_script_id)
VALUES ('b918b4cd-67fc-4d4d-9b74-91f98730acd7', 0, 'b3568beb-5a5d-4059-9b4e-3afa0157adbc',
        'Transforms DHIS Org Unit to FHIR Location', 'TRANSFORM_DHIS_ORG_UNIT_FHIR_LOC',
        'Transforms DHIS Organization Unit to FHIR Location.', 'a8485045-4d62-4b3a-9cfd-5c36760b8d45');

ALTER TABLE fhir_organization_unit_rule
  ADD COLUMN mo_identifier_lookup_script_id UUID,
  ADD CONSTRAINT fhir_organization_unit_rule_fk3 FOREIGN KEY (mo_identifier_lookup_script_id) REFERENCES fhir_executable_script(id);
CREATE INDEX fhir_organization_unit_rule_i2 ON fhir_organization_unit_rule(mo_identifier_lookup_script_id);

UPDATE fhir_rule SET evaluation_order=2 WHERE id='d0e1472a-05e6-47c9-b36b-ff1f06fec352';
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, imp_enabled, applicable_exp_script_id, transform_exp_script_id)
VALUES ('b9546b02-4adc-4868-a4cd-d5d7789f0df0', 0, 'DHIS Organization Unit to FHIR Location', NULL, TRUE, 1, 'LOCATION', 'ORGANIZATION_UNIT', FALSE, NULL, 'b918b4cd-67fc-4d4d-9b74-91f98730acd7');
INSERT INTO fhir_organization_unit_rule(id, identifier_lookup_script_id, mo_identifier_lookup_script_id) VALUES ('b9546b02-4adc-4868-a4cd-d5d7789f0df0', '5090c485-a225-4c2e-a8f6-95fa74ce1618', '66d12e44-471c-4318-827a-0b397f694b6a');

UPDATE fhir_script_source SET source_text=
'var ok = false;
var resourceType = null;
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
    resourceType = ''LOCATION'';
    code = codeUtils.getByMappedCode(organizationUnit.getCode(), ''LOCATION'');
    if ((code == null) && args[''useLocationIdentifierCode''])
    {
      code = codeUtils.getCodeWithoutPrefix(organizationUnit.getCode(), ''LOCATION'');
    }
  }
}
else if (output.managingOrganization)
{
  if (!output.getManagingOrganization().isEmpty() && !args[''overrideExisting''])
  {
    ok = true;
  }
  else if ((organizationUnit == null) || (organizationUnit.getCode() == null) || organizationUnit.getCode().isEmpty())
  {
    output.setManagingOrganization(null);
    ok = true;
  }
  else
  {
    resourceType = ''ORGANIZATION'';
    code = codeUtils.getByMappedCode(organizationUnit.getCode(), ''ORGANIZATION'');
    if ((code == null) && args[''useIdentifierCode''])
    {
      code = codeUtils.getCodeWithoutPrefix(organizationUnit.getCode(), ''ORGANIZATION'');
    }
  }
}
if (code != null)
{
  var resource = fhirClientUtils.findBySystemIdentifier(resourceType, code);
  if (resource == null)
  {
    context.missingDhisResource(organizationUnit.getResourceId());
  }
  if (output.location)
  {
    output.setLocation(null);
    output.getLocation().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
    ok = true;
  }
  else if (output.managingOrganization)
  {
    output.setManagingOrganization(null);
    output.getManagingOrganization().setReferenceElement(resource.getIdElement().toUnqualifiedVersionless());
    ok = true;
  }
}
ok' WHERE id = '78c2b73c-469c-4ab4-8244-07e817b72d4a';

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('ed2e0cde-fc19-4468-8d84-6d31841d55e6', 0, 'Transforms GEO Coordinates to FHIR Resource', 'TRANSFORM_DHIS_GEO_FHIR',
        'Transforms GEO coordinates to FHIR Resource.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('ed2e0cde-fc19-4468-8d84-6d31841d55e6', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('ed2e0cde-fc19-4468-8d84-6d31841d55e6', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('dcf956e8-23cc-4934-8e42-0fbc9a23eeb9', 0, 'ed2e0cde-fc19-4468-8d84-6d31841d55e6',
'if (input.coordinate && output.location)
{
  if (input.getCoordinate() != null)
  {
    var location = fhirResourceUtils.createResource(''Location'');
	  location.setPosition(geoUtils.createPosition(input.getCoordinate()));
	  location.setPartOf(output.getLocation());
	  output.setLocation(fhirResourceUtils.createReference(location));
  }
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('dcf956e8-23cc-4934-8e42-0fbc9a23eeb9', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('30ee57d1-062f-4847-85b9-f2262a678151', 0, 'ed2e0cde-fc19-4468-8d84-6d31841d55e6',
        'Transforms GEO Coordinates to FHIR Resource', 'TRANSFORM_DHIS_GEO_FHIR',
        'Transforms GEO coordinates to FHIR Resource.');

ALTER TABLE fhir_resource_mapping
  ADD exp_tei_transform_script_id  UUID,
  ADD CONSTRAINT fhir_resource_mapping_fk12 FOREIGN KEY (exp_tei_transform_script_id) REFERENCES fhir_executable_script(id);
COMMENT ON COLUMN fhir_resource_mapping.exp_tei_transform_script_id IS 'References the executable script that transforms the TEI FHIR resource into the exported FHIR resource.';
CREATE INDEX fhir_resource_mapping_i9 ON fhir_resource_mapping(exp_tei_transform_script_id);

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('190ae3fa-471e-458a-be1d-3f173f7d3c75', 0, 'Transforms TEI FHIR Patient to FHIR Resource', 'TRANSFORM_TEI_FHIR_PATIENT',
        'Transforms TEI FHIR Patient to FHIR Resource.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('190ae3fa-471e-458a-be1d-3f173f7d3c75', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('190ae3fa-471e-458a-be1d-3f173f7d3c75', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('190ae3fa-471e-458a-be1d-3f173f7d3c75', 'TEI_FHIR_RESOURCE');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('17931cd6-b2dc-4bb9-a9b0-bec16139ce07', 0, '190ae3fa-471e-458a-be1d-3f173f7d3c75',
'var updated = false;
if (output.patient)
{
  output.setPatient(fhirResourceUtils.createReference(teiFhirResource));
  updated = true;
}
else if (output.subject)
{
  output.setSubject(fhirResourceUtils.createReference(teiFhirResource));
  updated = true;
}
updated', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('17931cd6-b2dc-4bb9-a9b0-bec16139ce07', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('f7863a17-86da-42d2-89fd-7f6c3d214f1b', 0, '190ae3fa-471e-458a-be1d-3f173f7d3c75',
        'Transforms TEI FHIR Patient to FHIR Resource', 'TRANSFORM_TEI_FHIR_PATIENT',
        'Transforms TEI FHIR Patient to FHIR Resource.');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('094a3f6b-6f36-4a49-8308-5a05b0acc4ce', 0, 'Transforms DHIS event date to FHIR Resource', 'TRANSFORM_DHIS_EVENT_DATE_FHIR_PATIENT',
        'Transforms DHIS event date to FHIR Resource.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('094a3f6b-6f36-4a49-8308-5a05b0acc4ce', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('094a3f6b-6f36-4a49-8308-5a05b0acc4ce', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('4a0b6fde-c0d6-4ad2-89da-992f4a47a115', 0, '094a3f6b-6f36-4a49-8308-5a05b0acc4ce',
'var updated = false;
if (output.dateElement)
{
  output.setDateElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
else if (output.effective)
{
  output.setEffective(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
updated', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('4a0b6fde-c0d6-4ad2-89da-992f4a47a115', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('deb4fd13-d5b2-41df-9f30-0fb73b063c8b', 0, '094a3f6b-6f36-4a49-8308-5a05b0acc4ce',
        'Transforms DHIS event date to FHIR Resource', 'TRANSFORM_DHIS_EVENT_DATE_FHIR_PATIENT',
        'Transforms DHIS event date to FHIR Resource.');

UPDATE fhir_resource_mapping SET
  exp_ou_transform_script_id = '416decee-4604-473a-b650-1a997d731ff0',
  exp_geo_transform_script_id = '30ee57d1-062f-4847-85b9-f2262a678151',
  exp_tei_transform_script_id = 'f7863a17-86da-42d2-89fd-7f6c3d214f1b',
  exp_date_transform_script_id = 'deb4fd13-d5b2-41df-9f30-0fb73b063c8b'
WHERE fhir_resource_type='IMMUNIZATION';

UPDATE fhir_script_source SET source_text=
'var mappedCode = null;
if (input.managingOrganization)
{
  var organizationReference = input.managingOrganization;
  if ( organizationReference != null )
  {
    var hierarchy = organizationUtils.findHierarchy( organizationReference );
    if ( hierarchy != null )
    {
      for ( var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++ )
      {
        var code = identifierUtils.getResourceIdentifier( hierarchy.get( i ), ''ORGANIZATION'' );
        if ( code != null )
        {
          mappedCode = codeUtils.getMappedCode( code, ''ORGANIZATION'' );
          if ( (mappedCode == null) && args[''useIdentifierCode''] )
          {
            mappedCode = organizationUtils.existsWithPrefix( code );
          }
        }
      }
    }
  }
}
else if (input.location)
{
  var locationReference = input.location;
  if ( locationReference != null )
  {
    var hierarchy = locationUtils.findHierarchy( locationReference );
    if ( hierarchy != null )
    {
      for ( var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++ )
      {
        var code = identifierUtils.getResourceIdentifier( hierarchy.get( i ), ''LOCATION'' );
        if ( code != null )
        {
          mappedCode = codeUtils.getMappedCode( code, ''LOCATION'' );
          if ( (mappedCode == null) && args[''useIdentifierCode''] )
          {
            mappedCode = locationUtils.existsWithPrefix( code );
          }
        }
      }
    }
  }
}
if (mappedCode == null)
{
  mappedCode = args[''defaultCode''];
}
var ref = null;
if (mappedCode != null)
{
  ref = context.createReference(mappedCode, ''CODE'');
}
if ((ref == null) && args[''useTei''] && (typeof trackedEntityInstance !== ''undefined''))
{
  ref = context.createReference(trackedEntityInstance.organizationUnitId, ''ID'');
}
ref' WHERE id='7b94feba-bcf6-4635-929a-01311b25d975' AND version=0;
DELETE FROM fhir_script_argument WHERE id='c0175733-83fc-4de2-9cd0-a2ae6b92e991' AND version=0;
