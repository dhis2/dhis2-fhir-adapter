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

-- Should be invoked manually with the appropriate permissions:
-- REVOKE CREATE ON SCHEMA public FROM PUBLIC;
-- CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE fhir_constant (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  name            VARCHAR(230)                   NOT NULL,
  description     TEXT,
  category        VARCHAR(30)                    NOT NULL,
  code            VARCHAR(50)                    NOT NULL,
  data_type       VARCHAR(30)                    NOT NULL,
  value           VARCHAR(250),
  CONSTRAINT fhir_constant_pk PRIMARY KEY (id),
  CONSTRAINT fhir_constant_uk1 UNIQUE (name),
  CONSTRAINT fhir_constant_uk2 UNIQUE (code)
);

CREATE TABLE fhir_script (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  name            VARCHAR(230)                   NOT NULL,
  description     TEXT,
  code            VARCHAR(50)                    NOT NULL,
  script_type     VARCHAR(30)                    NOT NULL,
  return_type     VARCHAR(30)                    NOT NULL,
  input_type      VARCHAR(30),
  output_type     VARCHAR(30),
  CONSTRAINT fhir_script_pk PRIMARY KEY (id)
);

CREATE TABLE fhir_script_source (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  script_id       UUID                           NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  source_text     TEXT                           NOT NULL,
  source_type     VARCHAR(30)                    NOT NULL,
  CONSTRAINT fhir_script_source_pk PRIMARY KEY (id),
  CONSTRAINT fhir_script_source_fk1 FOREIGN KEY (script_id) REFERENCES fhir_script (id) ON DELETE CASCADE
);
CREATE INDEX fhir_script_source_i1
  ON fhir_script_source (script_id);

CREATE TABLE fhir_script_source_version (
  script_source_id UUID        NOT NULL,
  fhir_version     VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_script_source_version_pk PRIMARY KEY (script_source_id, fhir_version),
  CONSTRAINT fhir_script_source_version_fk1 FOREIGN KEY (script_source_id) REFERENCES fhir_script_source (id) ON DELETE CASCADE
);

CREATE TABLE fhir_script_variable (
  script_id UUID        NOT NULL,
  variable  VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_script_variable_pk PRIMARY KEY (script_id, variable),
  CONSTRAINT fhir_script_variable_fk1 FOREIGN KEY (script_id) REFERENCES fhir_script (id) ON DELETE CASCADE
);

CREATE TABLE fhir_script_argument (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  script_id       UUID                           NOT NULL,
  name            VARCHAR(30)                    NOT NULL,
  data_type       VARCHAR(30)                    NOT NULL,
  mandatory       BOOLEAN                        NOT NULL DEFAULT TRUE,
  default_value   VARCHAR(230),
  description     TEXT,
  CONSTRAINT fhir_script_argument_pk PRIMARY KEY (id),
  CONSTRAINT fhir_script_argument_fk1 FOREIGN KEY (script_id) REFERENCES fhir_script (id) ON DELETE CASCADE,
  CONSTRAINT fhir_script_argument_uk1 UNIQUE (script_id, name)
);
CREATE INDEX fhir_script_argument_i1
  ON fhir_script_argument (script_id);

CREATE TABLE fhir_executable_script (
  id        UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
  script_id UUID NOT NULL,
  CONSTRAINT fhir_executable_script_pk PRIMARY KEY (id),
  CONSTRAINT fhir_executable_script_fk1 FOREIGN KEY (script_id) REFERENCES fhir_script (id)
);
CREATE INDEX fhir_executable_script_i1
  ON fhir_executable_script (script_id);

CREATE TABLE fhir_executable_script_argument (
  id                   UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version              BIGINT                         NOT NULL,
  created_at           TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by      VARCHAR(11),
  last_updated_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  executable_script_id UUID                           NOT NULL,
  script_argument_id   UUID                           NOT NULL,
  override_value       VARCHAR(230),
  CONSTRAINT fhir_executable_script_argument_pk PRIMARY KEY (id),
  CONSTRAINT fhir_executable_script_argument_fk1 FOREIGN KEY (executable_script_id) REFERENCES fhir_executable_script (id) ON DELETE CASCADE,
  CONSTRAINT fhir_executable_script_argument_fk2 FOREIGN KEY (script_argument_id) REFERENCES fhir_script_argument (id) ON DELETE CASCADE,
  CONSTRAINT fhir_executable_script_argument_uk1 UNIQUE (executable_script_id, script_argument_id)
);
CREATE INDEX fhir_executable_script_argument_i1
  ON fhir_executable_script_argument (executable_script_id);
CREATE INDEX fhir_executable_script_argument_i2
  ON fhir_executable_script_argument (script_argument_id);

CREATE TABLE fhir_code_category (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  name            VARCHAR(230)                   NOT NULL,
  code            VARCHAR(50)                    NOT NULL,
  description     TEXT,
  CONSTRAINT fhir_code_category_pk PRIMARY KEY (id),
  CONSTRAINT fhir_code_category_uk1 UNIQUE (name),
  CONSTRAINT fhir_code_category_uk2 UNIQUE (code)
);

CREATE TABLE fhir_code (
  id               UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version          BIGINT                         NOT NULL,
  created_at       TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by  VARCHAR(11),
  last_updated_at  TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  code_category_id UUID                           NOT NULL,
  name             VARCHAR(230)                   NOT NULL,
  code             VARCHAR(50)                    NOT NULL,
  description      TEXT,
  CONSTRAINT fhir_code_pk PRIMARY KEY (id),
  CONSTRAINT fhir_code_fk1 FOREIGN KEY (code_category_id) REFERENCES fhir_code_category (id)
);
CREATE INDEX fhir_code_i1
  ON fhir_code (code_category_id);

CREATE TABLE fhir_system (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  name            VARCHAR(230)                   NOT NULL,
  code            VARCHAR(50)                    NOT NULL,
  system_uri      VARCHAR(140)                   NOT NULL,
  description     TEXT,
  CONSTRAINT fhir_system_pk PRIMARY KEY (id),
  CONSTRAINT fhir_system_uk1 UNIQUE (name),
  CONSTRAINT fhir_system_uk2 UNIQUE (code),
  CONSTRAINT fhir_system_uk3 UNIQUE (system_uri)
);

CREATE TABLE fhir_system_code (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  system_id       UUID                           NOT NULL,
  code_id         UUID                           NOT NULL,
  system_code     VARCHAR(120)                   NOT NULL,
  CONSTRAINT fhir_system_code_pk PRIMARY KEY (id),
  CONSTRAINT fhir_system_code_fk1 FOREIGN KEY (system_id) REFERENCES fhir_system (id),
  CONSTRAINT fhir_system_code_fk2 FOREIGN KEY (code_id) REFERENCES fhir_code (id) ON DELETE CASCADE,
  CONSTRAINT fhir_system_code_uk1 UNIQUE (system_id, system_code)
);
CREATE INDEX fhir_system_code_i1
  ON fhir_system_code (system_id);
CREATE INDEX fhir_system_code_i2
  ON fhir_system_code (code_id);

CREATE TABLE fhir_rule (
  id                      UUID                           NOT NULL     DEFAULT UUID_GENERATE_V4(),
  version                 BIGINT                         NOT NULL,
  created_at              TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL     DEFAULT CURRENT_TIMESTAMP,
  last_updated_by         VARCHAR(11),
  last_updated_at         TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL     DEFAULT CURRENT_TIMESTAMP,
  name                    VARCHAR(230)                   NOT NULL,
  description             TEXT,
  enabled                 BOOLEAN                        NOT NULL     DEFAULT TRUE,
  evaluation_order        INTEGER                        NOT NULL     DEFAULT 0,
  fhir_resource_type      VARCHAR(30)                    NOT NULL,
  dhis_resource_type      VARCHAR(30)                    NOT NULL,
  applicable_in_script_id UUID,
  transform_in_script_id  UUID                           NOT NULL,
  CONSTRAINT fhir_rule_pk PRIMARY KEY (id),
  CONSTRAINT fhir_rule_uk1 UNIQUE (name),
  CONSTRAINT fhir_rule_fk1 FOREIGN KEY (applicable_in_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_rule_fk2 FOREIGN KEY (transform_in_script_id) REFERENCES fhir_executable_script (id)
);
CREATE INDEX fhir_rule_i1
  ON fhir_rule (applicable_in_script_id);
CREATE INDEX fhir_rule_i2
  ON fhir_rule (transform_in_script_id);

CREATE TABLE fhir_tracker_program (
  id              UUID                           NOT NULL,
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  name            VARCHAR(230)                   NOT NULL,
  description     TEXT,
  program_ref     VARCHAR(230)                   NOT NULL,
  enabled         BOOLEAN                        NOT NULL DEFAULT TRUE,
  CONSTRAINT fhir_tracker_program_pk PRIMARY KEY (id),
  CONSTRAINT fhir_tracker_program_uk1 UNIQUE (name),
  CONSTRAINT fhir_tracker_program_uk2 UNIQUE (program_ref)
);

CREATE TABLE fhir_enrollment (
  id                            UUID                           NOT NULL,
  version                       BIGINT                         NOT NULL,
  created_at                    TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by               VARCHAR(11),
  last_updated_at               TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  program_id                    UUID                           NOT NULL,
  name                          VARCHAR(230)                   NOT NULL,
  description                   TEXT,
  creation_enabled              BOOLEAN                        NOT NULL DEFAULT FALSE,
  creation_applicable_script_id UUID,
  creation_transform_script_id  UUID,
  final_script_id               UUID,
  CONSTRAINT fhir_enrollment_pk PRIMARY KEY (id),
  CONSTRAINT fhir_enrollment_fk1 FOREIGN KEY (program_id) REFERENCES fhir_tracker_program (id) ON DELETE CASCADE,
  CONSTRAINT fhir_enrollment_fk2 FOREIGN KEY (creation_applicable_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_enrollment_fk3 FOREIGN KEY (creation_transform_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_enrollment_fk4 FOREIGN KEY (final_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_enrollment_uk1 UNIQUE (name)
);
CREATE INDEX fhir_enrollment_i1
  ON fhir_enrollment (program_id);
CREATE INDEX fhir_enrollment_i2
  ON fhir_enrollment (creation_applicable_script_id);
CREATE INDEX fhir_enrollment_i3
  ON fhir_enrollment (creation_transform_script_id);
CREATE INDEX fhir_enrollment_i4
  ON fhir_enrollment (final_script_id);

CREATE TABLE fhir_program_stage_rule (
  id                            UUID         NOT NULL,
  program_id                    UUID         NOT NULL,
  program_stage_ref             VARCHAR(230) NOT NULL,
  enrollment_enabled            BOOLEAN      NOT NULL DEFAULT FALSE,
  enrollment_id                 UUID,
  creation_enabled              BOOLEAN      NOT NULL DEFAULT FALSE,
  creation_applicable_script_id UUID,
  creation_transform_script_id  UUID,
  final_script_id               UUID,
  CONSTRAINT fhir_program_stage_rule_pk PRIMARY KEY (id),
  CONSTRAINT fhir_program_stage_rule_fk1 FOREIGN KEY (id) REFERENCES fhir_rule (id) ON DELETE CASCADE,
  CONSTRAINT fhir_program_stage_rule_fk2 FOREIGN KEY (program_id) REFERENCES fhir_tracker_program (id) ON DELETE CASCADE,
  CONSTRAINT fhir_program_stage_rule_fk3 FOREIGN KEY (enrollment_id) REFERENCES fhir_enrollment (id),
  CONSTRAINT fhir_program_stage_rule_fk4 FOREIGN KEY (creation_applicable_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_program_stage_rule_fk5 FOREIGN KEY (creation_transform_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_program_stage_rule_fk6 FOREIGN KEY (final_script_id) REFERENCES fhir_executable_script (id)
);
CREATE INDEX fhir_program_stage_rule_i1
  ON fhir_program_stage_rule (program_id);
CREATE INDEX fhir_program_stage_rule_i2
  ON fhir_program_stage_rule (enrollment_id);
CREATE INDEX fhir_program_stage_rule_i3
  ON fhir_program_stage_rule (creation_applicable_script_id);
CREATE INDEX fhir_program_stage_rule_i4
  ON fhir_program_stage_rule (creation_transform_script_id);
CREATE INDEX fhir_program_stage_rule_i5
  ON fhir_program_stage_rule (final_script_id);

CREATE TABLE fhir_tracked_entity_rule (
  id                 UUID         NOT NULL,
  tracked_entity_ref VARCHAR(230) NOT NULL,
  org_lookup_script_id UUID       NOT NULL,
  CONSTRAINT fhir_tracked_entity_rule_pk PRIMARY KEY (id),
  CONSTRAINT fhir_tracked_entity_rule_fk1 FOREIGN KEY (id) REFERENCES fhir_rule (id) ON DELETE CASCADE,
  CONSTRAINT fhir_tracked_entity_rule_fk2 FOREIGN KEY (org_lookup_script_id) REFERENCES fhir_executable_script (id)
);

CREATE TABLE fhir_resource_mapping (
  id                              UUID                           NOT NULL,
  version                         BIGINT                         NOT NULL,
  fhir_resource_type              VARCHAR(30)                    NOT NULL,
  created_at                      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by                 VARCHAR(11),
  last_updated_at                 TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tei_lookup_script_id         UUID                           NOT NULL,
  enrollment_org_lookup_script_id UUID                           NOT NULL,
  event_org_lookup_script_id      UUID                           NOT NULL,
  CONSTRAINT fhir_resource_mapping_pk PRIMARY KEY (id),
  CONSTRAINT fhir_resource_mapping_uk1 UNIQUE (fhir_resource_type),
  CONSTRAINT fhir_resource_mapping_fk1 FOREIGN KEY (tei_lookup_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_resource_mapping_fk2 FOREIGN KEY (enrollment_org_lookup_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_resource_mapping_fk3 FOREIGN KEY (event_org_lookup_script_id ) REFERENCES fhir_executable_script (id)
);

CREATE TABLE fhir_remote_subscription (
  id                            UUID                           NOT NULL,
  version                       BIGINT                         NOT NULL,
  created_at                    TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by               VARCHAR(11),
  last_updated_at               TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  name                          VARCHAR(50)                    NOT NULL,
  code                          VARCHAR(20)                    NOT NULL,
  enabled                       BOOLEAN                        NOT NULL DEFAULT TRUE,
  locked                        BOOLEAN                        NOT NULL DEFAULT FALSE,
  description                   TEXT,
  fhir_version                  VARCHAR(30)                    NOT NULL,
  web_hook_authorization_header VARCHAR(200)                   NOT NULL,
  dhis_authorization_header     VARCHAR(200)                   NOT NULL,
  remote_base_url               VARCHAR(200)                   NOT NULL,
  support_includes              BOOLEAN                        NOT NULL DEFAULT FALSE,
  tolerance_minutes              INTEGER DEFAULT 0 NOT NULL,
  logging BOOLEAN NOT NULL DEFAULT FALSE,
  verbose_logging BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fhir_remote_subscription_pk PRIMARY KEY (id),
  CONSTRAINT fhir_remote_subscription_uk1 UNIQUE (name),
  CONSTRAINT fhir_remote_subscription_uk2 UNIQUE (code)
);

CREATE TABLE fhir_remote_subscription_header (
  remote_subscription_id UUID         NOT NULL,
  name                 VARCHAR(50) NOT NULL,
  value                 VARCHAR(200) NOT NULL,
  secured            BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT fhir_remote_subscription_header_pk PRIMARY KEY (remote_subscription_id, name, value),
  CONSTRAINT fhir_remote_subscription_header_fk1 FOREIGN KEY (remote_subscription_id) REFERENCES fhir_remote_subscription (id) ON DELETE CASCADE
);

CREATE TABLE fhir_remote_subscription_system (
  id                            UUID                           NOT NULL,
  version                       BIGINT                         NOT NULL,
  created_at                    TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by               VARCHAR(11),
  last_updated_at               TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  remote_subscription_id        UUID NOT NULL,
  fhir_resource_type                 VARCHAR(30) NOT NULL,
  system_id    UUID NOT NULL,
  CONSTRAINT fhir_remote_subscription_system_pk PRIMARY KEY (id),
  CONSTRAINT fhir_remote_subscription_system_uk1 UNIQUE (remote_subscription_id , fhir_resource_type ),
  CONSTRAINT fhir_remote_subscription_system_fk1 FOREIGN KEY (remote_subscription_id) REFERENCES fhir_remote_subscription (id) ON DELETE CASCADE,
  CONSTRAINT fhir_remote_subscription_system_fk2 FOREIGN KEY (system_id) REFERENCES fhir_system (id)
);
CREATE INDEX fhir_remote_subscription_system_i1 ON fhir_remote_subscription_system(system_id);

CREATE TABLE fhir_remote_subscription_resource (
  id                       UUID                           NOT NULL,
  version                  BIGINT                         NOT NULL,
  created_at               TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by          VARCHAR(11),
  last_updated_at          TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  remote_subscription_id   UUID                           NOT NULL,
  fhir_resource_type       VARCHAR(30)                    NOT NULL,
  fhir_criteria_parameters VARCHAR(200),
  description              TEXT,
  remote_last_update       TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fhir_remote_subscription_resource_pk PRIMARY KEY (id),
  -- do not enable cascading delete since remote last update date may get lost by mistake
  CONSTRAINT fhir_remote_subscription_resource_fk1 FOREIGN KEY (remote_subscription_id) REFERENCES fhir_remote_subscription (id)
);
CREATE INDEX fhir_remote_subscription_resource_i1
  ON fhir_remote_subscription_resource (remote_subscription_id);

INSERT INTO fhir_code_category (id, version, name, code, description)
VALUES ('7090561e-f45b-411e-99c0-65fa1d145018', 0, 'Vaccine', 'VACCINE', 'Available vaccines.');

-- Gender Constants (Adapter Gender Code to DHIS2 code as value)
INSERT INTO fhir_constant (id, version, category, name, code, data_type, value)
VALUES ('fa4a3a0e-ca46-40e4-b832-3aec96bed55e', 0, 'GENDER', 'Gender Female', 'GENDER_FEMALE', 'STRING', 'Female');
INSERT INTO fhir_constant (id, version, category, name, code, data_type, value)
VALUES ('1ded2081-8836-43dd-a5e1-7cb9562c93ef', 0, 'GENDER', 'Gender Male', 'GENDER_MALE', 'STRING', 'Male');

-- Systems (the value is the system URI)
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('2601edcb-f7bc-4710-ab64-0f4edd9a2378', 0, 'CVX (Vaccine Administered)', 'SYSTEM_CVX', 'http://hl7.org/fhir/sid/cvx', 'Available at http://www2a.cdc.gov/vaccines/iis/iisstandards/vaccines.asp?rpt=cvx. ' ||
                                                                                                                              'Developed by The CDC''s National Center of Immunization and Respiratory Diseases (NCIRD).');
-- Script that returns boolean value true every time
INSERT INTO fhir_script (id, version, name, description, code, script_type, return_type, input_type, output_type)
VALUES ('5b37861d-9442-4e13-ac9f-88a893e91ce9', 0, 'True', 'Returns Boolean True.', 'TRUE', 'EVALUATE', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_source (id , version, script_id, source_text, source_type)
VALUES ('edcb402e-94b4-4953-8846-3a4d1c0dad6e', 0, '5b37861d-9442-4e13-ac9f-88a893e91ce9', 'true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('edcb402e-94b4-4953-8846-3a4d1c0dad6e', 'DSTU3');
INSERT INTO fhir_executable_script (id, script_id)
VALUES ('9299b82e-b90a-4542-8b78-200cadff3d7d', '5b37861d-9442-4e13-ac9f-88a893e91ce9');

-- Script that extracts Organisation Unit Reference from Patient
INSERT INTO fhir_script (id, version, name, description, code, script_type, return_type, input_type, output_type)
VALUES ('a250e109-a135-42b2-8bdb-1c050c1d384c', 0, 'Org Unit Reference Code from Patient Organization',
'Extracts the organization unit code reference from the business identifier that is specified by the FHIR Organization of the FHIR Patient.',
'EXTRACT_FHIR_PATIENT_DHIS_ORG_UNIT_CODE', 'EVALUATE', 'ORG_UNIT_REF', 'FHIR_PATIENT', 'DHIS_TRACKED_ENTITY_INSTANCE');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a250e109-a135-42b2-8bdb-1c050c1d384c', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a250e109-a135-42b2-8bdb-1c050c1d384c', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('7b94feba-bcf6-4635-929a-01311b25d975', 0, 'a250e109-a135-42b2-8bdb-1c050c1d384c',
'context.createReference(identifierUtils.getReferenceIdentifier(input.managingOrganization, ''ORGANIZATION''), ''CODE'')', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('7b94feba-bcf6-4635-929a-01311b25d975', 'DSTU3');
INSERT INTO fhir_executable_script (id, script_id)
VALUES ('25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', 'a250e109-a135-42b2-8bdb-1c050c1d384c');

-- Script that transforms Patient to Person
INSERT INTO fhir_script (id, version, name, description, code, script_type, return_type, input_type, output_type)
VALUES ('ea887943-5e94-4e31-9441-c7661fe1063e', 0, 'True', 'Transforms FHIR Patient to DHIS Person.', 'TRANSFORM_FHIR_PATIENT_DHIS_PERSON', 'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_PATIENT', 'DHIS_TRACKED_ENTITY_INSTANCE');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ea887943-5e94-4e31-9441-c7661fe1063e', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ea887943-5e94-4e31-9441-c7661fe1063e', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ea887943-5e94-4e31-9441-c7661fe1063e', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('d02a5fe8-e651-41ed-9f41-ad3e23199d48', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'nationalIdentifierAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, 'NAME:National identifier',
'The reference of the tracked entity attribute that contains the national identifier of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('0a7c26cb-7bd3-4394-9d47-a610ac231f8a', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'lastNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, 'NAME:Last name',
'The reference of the tracked entity attribute that contains the last name of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('b41dd571-a129-4fa6-a807-35ea5663e8e3', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'firstNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, 'NAME:First name',
'The reference of the tracked entity attribute that contains the first name of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('90b3c110-38e4-4291-934c-e2569e8af1ba', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'birthDateAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, NULL,
'The reference of the tracked entity attribute that contains the birth date of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('8e3efdc7-6ce4-4899-bb20-faed7d5e3279', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'genderAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, NULL,
'The reference of the tracked entity attribute that contains the gender of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('40a28a9c-82e3-46e8-9eb9-44aaf2f5eacc', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'addressLineAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, NULL,
'The reference of the tracked entity attribute that contains the address line (e.g. street, house number) of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('ae13ceca-86d7-4f60-8d54-25587d53a5bd', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'cityAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, NULL,
'The reference of the tracked entity attribute that contains the city of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('6fb6bfe4-5b44-42a1-812f-be1dc8413d6e', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'stateOfCountryAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, NULL,
'The reference of the tracked entity attribute that contains the state (i.e. state of country) of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('a77ef245-e65e-4a87-9c96-5047911f9830', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'countryAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, NULL,
'The reference of the tracked entity attribute that contains the country of the Person.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('b2cfaf30-6ede-41f2-bd6c-448e76c429a1', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'output.setValue(args[''nationalIdentifierAttribute''], identifierUtils.getResourceIdentifier(input, ''PATIENT''));
output.setValue(args[''lastNameAttribute''], humanNameUtils.getPrimaryName(input.name).family);
output.setValue(args[''firstNameAttribute''], humanNameUtils.getSingleGiven(humanNameUtils.getPrimaryName(input.name)));
output.setOptionalValue(args[''birthDateAttribute''], dateTimeUtils.getPreciseDate(input.birthDateElement));
output.setOptionalValue(args[''genderAttribute''], input.gender);
output.setOptionalValue(args[''addressLineAttribute''], addressUtils.getSingleLine(addressUtils.getPrimaryAddress(input.address)));
output.setOptionalValue(args[''cityAttribute''], addressUtils.getPrimaryAddress(input.address).city);
output.setOptionalValue(args[''stateOfCountryAttribute''], addressUtils.getPrimaryAddress(input.address).state);
output.setOptionalValue(args[''countryAttribute''], addressUtils.getPrimaryAddress(input.address).country);
output.coordinates = geoUtils.getLocation(addressUtils.getPrimaryAddress(input.address));
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('b2cfaf30-6ede-41f2-bd6c-448e76c429a1', 'DSTU3');
INSERT INTO fhir_executable_script (id, script_id)
VALUES ('72451c8f-7492-4707-90b8-a3e0796de19e', 'ea887943-5e94-4e31-9441-c7661fe1063e');
