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
ALTER TABLE fhir_resource_mapping RENAME COLUMN enrollment_loc_lookup_script_id TO imp_enrollment_loc_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN event_loc_lookup_script_id TO imp_event_loc_lookup_script_id;
ALTER TABLE fhir_resource_mapping RENAME COLUMN effective_date_lookup_script_id TO imp_effective_date_lookup_script_id;

ALTER TABLE fhir_tracker_program
  ADD COLUMN tracked_entity_fhir_resource_type VARCHAR(30) DEFAULT 'PATIENT' NOT NULL,
  ADD CONSTRAINT fhir_tracker_program_fk6 FOREIGN KEY(tracked_entity_fhir_resource_type) REFERENCES fhir_resource_type_enum(value);

ALTER TABLE fhir_resource_mapping
  ADD COLUMN exp_ou_transform_script_id UUID,
  ADD CONSTRAINT fhir_resource_mapping_fk9 FOREIGN KEY (exp_ou_transform_script_id) REFERENCES fhir_executable_script(id),
  ADD COLUMN exp_loc_transform_script_id UUID,
  ADD CONSTRAINT fhir_resource_mapping_fk10 FOREIGN KEY (exp_loc_transform_script_id) REFERENCES fhir_executable_script(id),
  ADD COLUMN exp_date_transform_script_id UUID,
  ADD CONSTRAINT fhir_resource_mapping_fk11 FOREIGN KEY (exp_date_transform_script_id) REFERENCES fhir_executable_script(id);

-- Script that sets for a data element if a FHIR Immunization has been given
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('c8a937b5-665b-485c-bbc9-7a83e21a4e47', 0, 'TRANSFORM_DHIS_IMMUNIZATION_YN', 'Transforms DHIS Immunization Y/N data element to FHIR', 'Transforms DHIS Immunization Y/N data element to FHIR.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_IMMUNIZATION', '3487e94d-0525-44c5-a2df-ebb00a398e94');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('c8a937b5-665b-485c-bbc9-7a83e21a4e47', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('c8a937b5-665b-485c-bbc9-7a83e21a4e47', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('c8a937b5-665b-485c-bbc9-7a83e21a4e47', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('6ede1e58-17c1-49bd-a7e4-36ef9c87521f', 0, 'c8a937b5-665b-485c-bbc9-7a83e21a4e47',
'true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('6ede1e58-17c1-49bd-a7e4-36ef9c87521f', 'DSTU3');
