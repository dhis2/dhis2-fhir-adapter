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

ALTER TABLE fhir_rule RENAME COLUMN applicable_in_script_id TO applicable_imp_script_id;
ALTER TABLE fhir_rule RENAME COLUMN transform_in_script_id TO transform_imp_script_id;

ALTER TABLE fhir_queued_remote_subscription_request
  DROP COLUMN IF EXISTS request_id;
ALTER TABLE fhir_queued_remote_resource
  DROP COLUMN IF EXISTS request_id;

ALTER TABLE fhir_remote_subscription_resource_update
  ADD COLUMN last_requested TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC');
COMMENT ON COLUMN fhir_remote_subscription_resource_update.last_requested IS 'Contains the timestamp when the update has been requested last time. This timestamp is not used for remote FHIR resources.';

CREATE TABLE fhir_dhis_sync_group (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_dhis_subscription_resource_pk PRIMARY KEY (id)
);
COMMENT ON TABLE fhir_dhis_sync_group IS 'Contains the DHIS2 synchronization groups. All dhis subscriptions that are combined in a DHIS2 synchronization group are synchronized together. At the moment only one synchronization group is supported and concept exists only for future extensibility.';
COMMENT ON COLUMN fhir_dhis_sync_group.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_dhis_sync_group.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_dhis_sync_group.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_dhis_sync_group.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_dhis_sync_group.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';

CREATE TABLE fhir_dhis_sync_group_update (
  id             UUID                           NOT NULL,
  last_requested TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated   TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_dhis_sync_group_update_pk PRIMARY KEY (id),
  CONSTRAINT fhir_dhis_sync_group_update_fk1 FOREIGN KEY (id) REFERENCES fhir_dhis_sync_group (id) ON DELETE CASCADE
);
COMMENT ON TABLE fhir_dhis_sync_group_update IS 'Contains the timestamp that includes when the DHIS2 synchronization group has been updated last time.';
COMMENT ON COLUMN fhir_dhis_sync_group_update.last_requested IS 'Contains the timestamp when the update has been requested last time.';
COMMENT ON COLUMN fhir_dhis_sync_group_update.last_updated IS 'The timestamp of the last begin of fetching data from the dhis FHIR Service for the subscribed resource.';

INSERT INTO fhir_dhis_sync_group(id, version)
VALUES ('22204dd4-05d9-4cdd-96a8-ed742087d469', 0);
INSERT INTO fhir_dhis_sync_group_update(id)
VALUES ('22204dd4-05d9-4cdd-96a8-ed742087d469');

CREATE TABLE fhir_queued_dhis_sync_request (
  id        UUID                           NOT NULL,
  queued_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_queued_dhis_sync_request_pk PRIMARY KEY (id),
  CONSTRAINT fhir_queued_dhis_sync_request_fk1 FOREIGN KEY (id) REFERENCES fhir_dhis_sync_group (id) ON DELETE CASCADE
);
COMMENT ON TABLE fhir_queued_dhis_sync_request IS 'Contains queued DHIS2 sync group requests.';
COMMENT ON COLUMN fhir_queued_dhis_sync_request.id IS 'References the DHIS2 sync group.';
COMMENT ON COLUMN fhir_queued_dhis_sync_request.queued_at IS 'The timestamp when the data has been queued last time.';

CREATE TABLE fhir_processed_dhis_resource (
  dhis_sync_group_id UUID         NOT NULL,
  processed_id       VARCHAR(120) NOT NULL,
  processed_at       TIMESTAMP(3) NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_processed_dhis_resource_pk PRIMARY KEY (dhis_sync_group_id, processed_id),
  CONSTRAINT fhir_processed_dhis_resource_fk1 FOREIGN KEY (dhis_sync_group_id) REFERENCES fhir_dhis_sync_group (id) ON DELETE CASCADE
);
CREATE INDEX fhir_processed_dhis_resource_i1
  ON fhir_processed_dhis_resource (dhis_sync_group_id, processed_at);
COMMENT ON TABLE fhir_processed_dhis_resource IS 'Contains the versioned DHIS2 IDs that have been processed in the last few hours.';
COMMENT ON COLUMN fhir_processed_dhis_resource.dhis_sync_group_id IS 'References the DHIS2 sync group to which the processed data belongs to.';
COMMENT ON COLUMN fhir_processed_dhis_resource.processed_id IS 'The unique string that identifies a distinct version of a DHIS2 resource.';
COMMENT ON COLUMN fhir_processed_dhis_resource.processed_at IS 'Timestamp when the resource has been processed. Used for deleting the data after some hours mainly.';

CREATE TABLE fhir_stored_dhis_resource (
  dhis_sync_group_id UUID         NOT NULL,
  stored_id          VARCHAR(120) NOT NULL,
  stored_at          TIMESTAMP(3) NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_stored_dhis_resource_pk PRIMARY KEY (dhis_sync_group_id, stored_id),
  CONSTRAINT fhir_stored_dhis_resource_fk1 FOREIGN KEY (dhis_sync_group_id) REFERENCES fhir_dhis_sync_group (id) ON DELETE CASCADE
);
CREATE INDEX fhir_stored_dhis_resource_i1
  ON fhir_stored_dhis_resource (dhis_sync_group_id, stored_at);
COMMENT ON TABLE fhir_stored_dhis_resource IS 'Contains the versioned DHIS2 IDs that have been stored in the last few hours.';
COMMENT ON COLUMN fhir_stored_dhis_resource.dhis_sync_group_id IS 'References the DHIS2 sync group to which the stored data belongs to.';
COMMENT ON COLUMN fhir_stored_dhis_resource.stored_id IS 'The unique string that identifies a distinct version of a DHIS2 resource.';
COMMENT ON COLUMN fhir_stored_dhis_resource.stored_at IS 'Timestamp when the resource has been stored. Used for deleting the data after some hours mainly.';

CREATE TABLE fhir_stored_remote_resource (
  remote_subscription_id UUID         NOT NULL,
  stored_id              VARCHAR(120) NOT NULL,
  stored_at              TIMESTAMP(3) NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_stored_remote_resource_pk PRIMARY KEY (remote_subscription_id, stored_id),
  CONSTRAINT fhir_stored_remote_resource_fk1 FOREIGN KEY (remote_subscription_id) REFERENCES fhir_remote_subscription (id) ON DELETE CASCADE
);
CREATE INDEX fhir_stored_remote_resource_i1
  ON fhir_stored_remote_resource (remote_subscription_id, stored_at);
COMMENT ON TABLE fhir_stored_remote_resource IS 'Contains the versioned FHIR resource IDs that have been stored in the last few hours.';
COMMENT ON COLUMN fhir_stored_remote_resource.remote_subscription_id IS 'References the remote subscription to which the subscription belongs to.';
COMMENT ON COLUMN fhir_stored_remote_resource.stored_id IS 'The unique string that identifies a distinct version of a remote FHIR resource.';
COMMENT ON COLUMN fhir_stored_remote_resource.stored_at IS 'Timestamp when the resource has been stored. Used for deleting the data after some hours mainly.';

CREATE TABLE fhir_queued_dhis_resource (
  dhis_sync_group_id UUID                           NOT NULL,
  dhis_resource_id   VARCHAR(80)                    NOT NULL,
  queued_at          TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_queued_dhis_resource_pk PRIMARY KEY (dhis_sync_group_id, dhis_resource_id),
  CONSTRAINT fhir_queued_dhis_resource_fk1 FOREIGN KEY (dhis_sync_group_id) REFERENCES fhir_dhis_sync_group (id) ON DELETE CASCADE
);
COMMENT ON TABLE fhir_queued_dhis_resource IS 'Contains queued DHIS2 resources that should be processed.';
COMMENT ON COLUMN fhir_queued_dhis_resource.dhis_sync_group_id IS 'References the DHIS2 sync group to which this data belongs to.';
COMMENT ON COLUMN fhir_queued_dhis_resource.dhis_resource_id IS 'The ID (with resource name) of the DHIS2 Resource that has been queued.';
COMMENT ON COLUMN fhir_queued_dhis_resource.queued_at IS 'The timestamp when the data has been queued last time.';

ALTER TABLE fhir_rule
  ADD COLUMN imp_enabled BOOLEAN DEFAULT TRUE NOT NULL,
  ADD COLUMN exp_enabled BOOLEAN DEFAULT FALSE NOT NULL,
  ADD COLUMN fhir_create_enabled BOOLEAN DEFAULT TRUE NOT NULL,
  ADD COLUMN fhir_update_enabled BOOLEAN DEFAULT FALSE NOT NULL;
COMMENT ON COLUMN fhir_rule.imp_enabled IS 'Specifies if transformation from FHIR to DHIS resource is enabled.';
COMMENT ON COLUMN fhir_rule.exp_enabled IS 'Specifies if transformation from DHIS to FHIR resource is enabled.';
COMMENT ON COLUMN fhir_rule.fhir_create_enabled IS 'Specifies if the creation of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule.';
COMMENT ON COLUMN fhir_rule.fhir_update_enabled IS 'Specifies if the update of a FHIR resource is enabled for output transformations from DHIS to FHIR for this rule.';

ALTER TABLE fhir_rule
  ADD COLUMN applicable_exp_script_id UUID,
  ADD CONSTRAINT fhir_rule_fk6 FOREIGN KEY (applicable_exp_script_id) REFERENCES fhir_executable_script (id);
CREATE INDEX fhir_rule_i4
  ON fhir_rule (applicable_exp_script_id);
COMMENT ON COLUMN fhir_rule.applicable_exp_script_id IS 'References the evaluation script that is used to evaluate if the DHIS 2 resource is applicable to be processed by this rule. If no script has been specified, the rule is applicable for further processing.';

ALTER TABLE fhir_rule
  ADD COLUMN stop BOOLEAN DEFAULT FALSE NOT NULL;
COMMENT ON COLUMN fhir_rule.stop IS 'Specifies if this rule is the last applied rule. When the transformation should not stop further rules are applied as well.';

ALTER TABLE fhir_rule
  ADD COLUMN transform_exp_script_id UUID,
  ADD CONSTRAINT fhir_rule_fk7 FOREIGN KEY (transform_exp_script_id) REFERENCES fhir_executable_script (id);
CREATE INDEX fhir_rule_i5
  ON fhir_rule (transform_exp_script_id);
COMMENT ON COLUMN fhir_rule.transform_exp_script_id IS 'References the transformation script that is used to transform the DHIS 2 resource to the FHIR resource.';

ALTER TABLE fhir_rule
  ALTER COLUMN transform_imp_script_id DROP NOT NULL;

ALTER TABLE fhir_tracked_entity
  ADD COLUMN exp_enabled BOOLEAN DEFAULT FALSE NOT NULL,
  ADD COLUMN fhir_create_enabled BOOLEAN DEFAULT TRUE NOT NULL,
  ADD COLUMN fhir_update_enabled BOOLEAN DEFAULT FALSE NOT NULL;
COMMENT ON COLUMN fhir_tracked_entity.exp_enabled IS 'Specifies if output transformation from DHIS to FHIR for this tracked entity type is enabled.';
COMMENT ON COLUMN fhir_tracked_entity.fhir_create_enabled IS 'Specifies if the creation of a FHIR resource is enabled for output transformations from DHIS to FHIR for this tracked entity type.';
COMMENT ON COLUMN fhir_tracked_entity.fhir_update_enabled IS 'Specifies if the update of a FHIR resource is enabled for output transformations from DHIS to FHIR for this tracked entity type.';

ALTER TABLE fhir_remote_subscription
  ADD COLUMN exp_enabled BOOLEAN DEFAULT FALSE NOT NULL,
  ADD COLUMN use_adapter_identifier BOOLEAN DEFAULT TRUE NOT NULL;
COMMENT ON COLUMN fhir_remote_subscription.exp_enabled IS 'Specifies if output transformation from DHIS to FHIR for this remote subscription is enabled.';
COMMENT ON COLUMN fhir_remote_subscription.use_adapter_identifier IS 'Specifies if the adapter should add an adapter specific identifier to created and updated resources. Using such identifiers makes it easier to map resources between FHIR and DHIS 2.';

ALTER TABLE fhir_script
  ADD base_script_id UUID,
  ADD CONSTRAINT fhir_script_fk5 FOREIGN KEY (base_script_id) REFERENCES fhir_script(id);
CREATE INDEX fhir_script_i1 ON fhir_script(base_script_id);
COMMENT ON COLUMN fhir_script.base_script_id IS 'References another script from which arguments are inherited.';

ALTER TABLE fhir_executable_script
  ADD base_executable_script_id UUID,
  ADD CONSTRAINT fhir_executable_script_fk5 FOREIGN KEY (base_executable_script_id) REFERENCES fhir_executable_script(id);
CREATE INDEX fhir_executable_script_i2 ON fhir_executable_script(base_executable_script_id);
COMMENT ON COLUMN fhir_executable_script.base_executable_script_id IS 'References another executable script from which arguments are inherited.';

-- Systems (the value is the systemAuthentication URI)
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('fa133f34-280f-40d0-ac9a-523d8ea8f23b', 0, 'DHIS2 FHIR Adapter Identifier', 'SYSTEM_DHIS2_FHIR_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/identifier',
        'DHIS2 FHIR Adapter generated identifiers.');

INSERT INTO fhir_script_type_enum
VALUES ('TRANSFORM_TO_FHIR');

-- Script that transforms DHIS2 Person to FHIR Patient
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('2f3b01d6-41ce-41ea-a66c-cc7b1d98aea6', 0, 'Transforms DHIS Person to FHIR Patient', 'TRANSFORM_DHIS_PERSON_FHIR_PATIENT', 'Transforms FHIR Patient to DHIS Person.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_TRACKED_ENTITY_INSTANCE', 'FHIR_PATIENT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('2f3b01d6-41ce-41ea-a66c-cc7b1d98aea6', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('2f3b01d6-41ce-41ea-a66c-cc7b1d98aea6', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('2f3b01d6-41ce-41ea-a66c-cc7b1d98aea6', 'OUTPUT');
-- Link to base script for transformation of FHIR Patient to DHIS2 Person
-- (may have been deleted in the meanwhile)
UPDATE fhir_script SET base_script_id = (SELECT id FROM fhir_script WHERE id = 'ea887943-5e94-4e31-9441-c7661fe1063e')
WHERE id='2f3b01d6-41ce-41ea-a66c-cc7b1d98aea6';
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('d7e622c5-1914-4e5b-a590-039fc0c2a105', 0, '2f3b01d6-41ce-41ea-a66c-cc7b1d98aea6',
'resetFhirValue', 'BOOLEAN', TRUE, 'false', 'Specifies if existing values in FHIR can be reset by null values (except first and last name).');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('f0a48e63-cc1d-4d02-85fa-80c7e79a5d9e', 0, '2f3b01d6-41ce-41ea-a66c-cc7b1d98aea6',
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
  if (((addressText != null) || args[''resetFhirValue'']) && (!output.hasAddress() || canOverrideAddress(output.getAddressFirstRep())))
  {
    output.getAddress().clear();
    output.addAddress().setText(addressText);
  }
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('f0a48e63-cc1d-4d02-85fa-80c7e79a5d9e', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('6a014e53-6a7e-4bb6-8cfc-817e4756ed4d', 0, '2f3b01d6-41ce-41ea-a66c-cc7b1d98aea6',
        'Transforms DHIS Person to FHIR Patient', 'TRANSFORM_DHIS_PERSON_FHIR_PATIENT', 'Transforms DHIS Person to FHIR Patient.');
-- Link to base script for transformation of FHIR Patient to DHIS2 Person
-- (may have been deleted in the meanwhile)
UPDATE fhir_executable_script SET base_executable_script_id = (SELECT id FROM fhir_executable_script WHERE id = '72451c8f-7492-4707-90b8-a3e0796de19e')
WHERE id='6a014e53-6a7e-4bb6-8cfc-817e4756ed4d';

UPDATE fhir_rule
SET transform_exp_script_id = '6a014e53-6a7e-4bb6-8cfc-817e4756ed4d'
WHERE id = '5f9ebdc9-852e-4c83-87ca-795946aabc35';
