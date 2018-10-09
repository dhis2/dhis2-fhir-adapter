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
  code            VARCHAR(100)                   NOT NULL,
  script_type     VARCHAR(30)                    NOT NULL,
  return_type     VARCHAR(30)                    NOT NULL,
  input_type      VARCHAR(30),
  output_type     VARCHAR(30),
  description     TEXT,
  CONSTRAINT fhir_script_pk PRIMARY KEY (id),
  CONSTRAINT fhir_script_u1 UNIQUE (code)
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
  array_value     BOOLEAN                        NOT NULL DEFAULT FALSE,
  default_value   VARCHAR(1000),
  description     TEXT,
  CONSTRAINT fhir_script_argument_pk PRIMARY KEY (id),
  CONSTRAINT fhir_script_argument_fk1 FOREIGN KEY (script_id) REFERENCES fhir_script (id) ON DELETE CASCADE,
  CONSTRAINT fhir_script_argument_uk1 UNIQUE (script_id, name)
);
CREATE INDEX fhir_script_argument_i1
  ON fhir_script_argument (script_id);

CREATE TABLE fhir_executable_script (
  id          UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
  script_id   UUID NOT NULL,
  name        VARCHAR(230) NOT NULL,
  code        VARCHAR(100) NOT NULL,
  description TEXT,
  CONSTRAINT fhir_executable_script_pk PRIMARY KEY (id),
  CONSTRAINT fhir_executable_script_uk1 UNIQUE (name),
  CONSTRAINT fhir_executable_script_uk2 UNIQUE (code),
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
  override_value       VARCHAR(1000),
  enabled              BOOLEAN                        NOT NULL DEFAULT TRUE,
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

CREATE TABLE fhir_tracked_entity_rule (
  id                            UUID         NOT NULL,
  tracked_entity_ref            VARCHAR(230) NOT NULL,
  org_lookup_script_id          UUID         NOT NULL,
  tracked_entity_identifier_ref VARCHAR(230) NOT NULL,
  tracked_entity_identifier_fq  BOOLEAN      NOT NULL DEFAULT FALSE,
  CONSTRAINT fhir_tracked_entity_rule_pk PRIMARY KEY (id),
  CONSTRAINT fhir_tracked_entity_rule_fk1 FOREIGN KEY (id) REFERENCES fhir_rule (id) ON DELETE CASCADE,
  CONSTRAINT fhir_tracked_entity_rule_fk2 FOREIGN KEY (org_lookup_script_id) REFERENCES fhir_executable_script (id)
);
CREATE INDEX fhir_tracked_entity_rule_i1
  ON fhir_tracked_entity_rule (org_lookup_script_id);

CREATE TABLE fhir_tracker_program (
  id                      UUID                           NOT NULL,
  version                 BIGINT                         NOT NULL,
  created_at              TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by         VARCHAR(11),
  last_updated_at         TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  name                    VARCHAR(230)                   NOT NULL,
  description             TEXT,
  program_ref             VARCHAR(230)                   NOT NULL,
  enabled                 BOOLEAN                        NOT NULL DEFAULT TRUE,
  tracked_entity_rule_id  UUID                           NOT NULL,
  CONSTRAINT fhir_tracker_program_pk PRIMARY KEY (id),
  CONSTRAINT fhir_tracker_program_uk1 UNIQUE (name),
  CONSTRAINT fhir_tracker_program_uk2 UNIQUE (program_ref),
  CONSTRAINT fhir_tracker_program_fk1 FOREIGN KEY (tracked_entity_rule_id) REFERENCES fhir_tracked_entity_rule(id)
);
CREATE INDEX fhir_tracker_program_i1
  ON fhir_tracker_program (tracked_entity_rule_id);

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
  creation_script_id            UUID,
  final_script_id               UUID,
  CONSTRAINT fhir_enrollment_pk PRIMARY KEY (id),
  CONSTRAINT fhir_enrollment_fk1 FOREIGN KEY (program_id) REFERENCES fhir_tracker_program (id) ON DELETE CASCADE,
  CONSTRAINT fhir_enrollment_fk2 FOREIGN KEY (creation_applicable_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_enrollment_fk3 FOREIGN KEY (creation_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_enrollment_fk4 FOREIGN KEY (final_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_enrollment_uk1 UNIQUE (name)
);
CREATE INDEX fhir_enrollment_i1
  ON fhir_enrollment (program_id);
CREATE INDEX fhir_enrollment_i2
  ON fhir_enrollment (creation_applicable_script_id);
CREATE INDEX fhir_enrollment_i3
  ON fhir_enrollment (creation_script_id);
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
  creation_script_id            UUID,
  final_script_id               UUID,
  CONSTRAINT fhir_program_stage_rule_pk PRIMARY KEY (id),
  CONSTRAINT fhir_program_stage_rule_fk1 FOREIGN KEY (id) REFERENCES fhir_rule (id) ON DELETE CASCADE,
  CONSTRAINT fhir_program_stage_rule_fk2 FOREIGN KEY (program_id) REFERENCES fhir_tracker_program (id) ON DELETE CASCADE,
  CONSTRAINT fhir_program_stage_rule_fk3 FOREIGN KEY (enrollment_id) REFERENCES fhir_enrollment (id),
  CONSTRAINT fhir_program_stage_rule_fk4 FOREIGN KEY (creation_applicable_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_program_stage_rule_fk5 FOREIGN KEY (creation_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_program_stage_rule_fk6 FOREIGN KEY (final_script_id) REFERENCES fhir_executable_script (id)
);
CREATE INDEX fhir_program_stage_rule_i1
  ON fhir_program_stage_rule (program_id);
CREATE INDEX fhir_program_stage_rule_i2
  ON fhir_program_stage_rule (enrollment_id);
CREATE INDEX fhir_program_stage_rule_i3
  ON fhir_program_stage_rule (creation_applicable_script_id);
CREATE INDEX fhir_program_stage_rule_i4
  ON fhir_program_stage_rule (creation_script_id);
CREATE INDEX fhir_program_stage_rule_i5
  ON fhir_program_stage_rule (final_script_id);

CREATE TABLE fhir_resource_mapping (
  id                              UUID                           NOT NULL,
  version                         BIGINT                         NOT NULL,
  fhir_resource_type              VARCHAR(30)                    NOT NULL,
  created_at                      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by                 VARCHAR(11),
  last_updated_at                 TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  tei_lookup_script_id            UUID                           NOT NULL,
  enrollment_org_lookup_script_id UUID                           NOT NULL,
  event_org_lookup_script_id      UUID                           NOT NULL,
  event_date_lookup_script_id     UUID                           NOT NULL,
  CONSTRAINT fhir_resource_mapping_pk PRIMARY KEY (id),
  CONSTRAINT fhir_resource_mapping_uk1 UNIQUE (fhir_resource_type),
  CONSTRAINT fhir_resource_mapping_fk1 FOREIGN KEY (tei_lookup_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_resource_mapping_fk2 FOREIGN KEY (enrollment_org_lookup_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_resource_mapping_fk3 FOREIGN KEY (event_org_lookup_script_id ) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_resource_mapping_fk4 FOREIGN KEY (event_date_lookup_script_id ) REFERENCES fhir_executable_script (id)
);
CREATE INDEX fhir_resource_mapping_i1
  ON fhir_resource_mapping (tei_lookup_script_id);
CREATE INDEX fhir_resource_mapping_i2
  ON fhir_resource_mapping (enrollment_org_lookup_script_id);
CREATE INDEX fhir_resource_mapping_i3
  ON fhir_resource_mapping (event_org_lookup_script_id);
CREATE INDEX fhir_resource_mapping_i4
  ON fhir_resource_mapping (event_date_lookup_script_id);

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
  tolerance_minutes             INTEGER                        NOT NULL DEFAULT 0,
  logging                       BOOLEAN                        NOT NULL DEFAULT FALSE,
  verbose_logging               BOOLEAN                        NOT NULL DEFAULT FALSE,
  CONSTRAINT fhir_remote_subscription_pk PRIMARY KEY (id),
  CONSTRAINT fhir_remote_subscription_uk1 UNIQUE (name),
  CONSTRAINT fhir_remote_subscription_uk2 UNIQUE (code)
);

CREATE TABLE fhir_remote_subscription_header (
  remote_subscription_id UUID         NOT NULL,
  name                   VARCHAR(50)  NOT NULL,
  value                  VARCHAR(200) NOT NULL,
  secured                BOOLEAN      NOT NULL DEFAULT FALSE,
  CONSTRAINT fhir_remote_subscription_header_pk PRIMARY KEY (remote_subscription_id, name, value),
  CONSTRAINT fhir_remote_subscription_header_fk1 FOREIGN KEY (remote_subscription_id) REFERENCES fhir_remote_subscription (id) ON DELETE CASCADE
);

CREATE TABLE fhir_remote_subscription_system (
  id                            UUID                           NOT NULL,
  version                       BIGINT                         NOT NULL,
  created_at                    TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  last_updated_by               VARCHAR(11),
  last_updated_at               TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
  remote_subscription_id        UUID                           NOT NULL,
  fhir_resource_type            VARCHAR(30)                    NOT NULL,
  system_id                     UUID NOT NULL,
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

-- Gender Constants (Adapter Gender Code to DHIS2 code as value)
INSERT INTO fhir_constant (id, version, category, name, code, data_type, value)
VALUES ('fa4a3a0e-ca46-40e4-b832-3aec96bed55e', 0, 'GENDER', 'Gender Female', 'GENDER_FEMALE', 'STRING', 'Female');
INSERT INTO fhir_constant (id, version, category, name, code, data_type, value)
VALUES ('1ded2081-8836-43dd-a5e1-7cb9562c93ef', 0, 'GENDER', 'Gender Male', 'GENDER_MALE', 'STRING', 'Male');

-- Systems (the value is the system URI)
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('2601edcb-f7bc-4710-ab64-0f4edd9a2378', 0, 'CVX (Vaccine Administered)', 'SYSTEM_CVX', 'http://hl7.org/fhir/sid/cvx',
'Available at http://www2a.cdc.gov/vaccines/iis/iisstandards/vaccines.asp?rpt=cvx. Developed by The CDC''s National Center of Immunization and Respiratory Diseases (NCIRD).');

-- Definition of vaccines
INSERT INTO fhir_code_category (id, version, name, code, description)
VALUES ('7090561e-f45b-411e-99c0-65fa1d145018', 0, 'Vaccine', 'VACCINE', 'Available vaccines.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('28dae133-da81-4780-b68e-c8ac3e3fe9df', 0, '7090561e-f45b-411e-99c0-65fa1d145018',
'MMR', 'VACCINE_03', 'measles, mumps and rubella virus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('d1e2022a-cb31-4e34-8f59-7db430983881', 0, '7090561e-f45b-411e-99c0-65fa1d145018',
'M/R', 'VACCINE_04', 'measles and rubella virus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('85d88ea4-4d3b-4313-bda3-cef803837c57', 0, '7090561e-f45b-411e-99c0-65fa1d145018',
'measles', 'VACCINE_05', 'measles virus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('34fc9c25-5e7d-46a4-9b88-1473d33b0415', 0, '7090561e-f45b-411e-99c0-65fa1d145018',
'MMRV', 'VACCINE_94', 'measles, mumps, rubella, and varicella virus vaccine');

-- Definitions of vaccines by system
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('10f0a407-f696-42cb-8ad3-54f352be3cf6', 0, '28dae133-da81-4780-b68e-c8ac3e3fe9df', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '03');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('59e4129e-6325-4472-8b92-eaf11f2a1b50', 0, 'd1e2022a-cb31-4e34-8f59-7db430983881', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '04');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('4c018501-0d82-4675-8f50-804676cfcd5b', 0, '85d88ea4-4d3b-4313-bda3-cef803837c57', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '05');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('9f9ffd23-f792-42f4-a9eb-9795cc4fdcbd', 0, '34fc9c25-5e7d-46a4-9b88-1473d33b0415', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '94');

-- Script that returns boolean value true every time
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('5b37861d-9442-4e13-ac9f-88a893e91ce9', 0, 'True', 'TRUE', 'Returns Boolean True.', 'EVALUATE', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_source (id , version, script_id, source_text, source_type)
VALUES ('edcb402e-94b4-4953-8846-3a4d1c0dad6e', 0, '5b37861d-9442-4e13-ac9f-88a893e91ce9', 'true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('edcb402e-94b4-4953-8846-3a4d1c0dad6e', 'DSTU3');
INSERT INTO fhir_executable_script (id, script_id, name, code, description)
VALUES ('9299b82e-b90a-4542-8b78-200cadff3d7d', '5b37861d-9442-4e13-ac9f-88a893e91ce9', 'True', 'TRUE', 'Returns Boolean True.');

-- Script that extracts Organisation Unit Reference from Patient
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('a250e109-a135-42b2-8bdb-1c050c1d384c', 0, 'Org Unit Code from Patient Org', 'EXTRACT_FHIR_PATIENT_DHIS_ORG_UNIT_CODE',
'Extracts the organization unit code reference from the business identifier that is specified by the FHIR Organization of the FHIR Patient.',
'EVALUATE', 'ORG_UNIT_REF', 'FHIR_PATIENT', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a250e109-a135-42b2-8bdb-1c050c1d384c', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a250e109-a135-42b2-8bdb-1c050c1d384c', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('7b94feba-bcf6-4635-929a-01311b25d975', 0, 'a250e109-a135-42b2-8bdb-1c050c1d384c',
'context.createReference(identifierUtils.getReferenceIdentifier(input.managingOrganization, ''ORGANIZATION''), ''CODE'')', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('7b94feba-bcf6-4635-929a-01311b25d975', 'DSTU3');
INSERT INTO fhir_executable_script (id, script_id, name, code, description)
VALUES ('25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', 'a250e109-a135-42b2-8bdb-1c050c1d384c', 'Org Unit Code from Patient Org', 'EXTRACT_FHIR_PATIENT_DHIS_ORG_UNIT_CODE',
'Extracts the organization unit code reference from the business identifier that is specified by the FHIR Organization of the FHIR Patient.');

-- Script that extracts Organisation Unit Reference from Tracked Entity Instance
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('0e780e50-9a7e-4d4a-a9fd-1b8607d17fbb', 0, 'Org Unit Reference Code from Patient Organization', 'EXTRACT_TEI_DHIS_ORG_UNIT_ID',
'Extracts the organization unit ID reference from the tracked entity instance.', 'EVALUATE', 'ORG_UNIT_REF', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('0e780e50-9a7e-4d4a-a9fd-1b8607d17fbb', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('0e780e50-9a7e-4d4a-a9fd-1b8607d17fbb', 'TRACKED_ENTITY_INSTANCE');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('0cd71988-e116-4c11-bed4-98ff608dbfb6', 0, '0e780e50-9a7e-4d4a-a9fd-1b8607d17fbb',
'context.createReference(trackedEntityInstance.organizationUnitId, ''ID'')', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('0cd71988-e116-4c11-bed4-98ff608dbfb6', 'DSTU3');
INSERT INTO fhir_executable_script (id, script_id, name, code, description)
VALUES ('a52945b5-94b9-48d4-9c49-f67b43d9dfbc', '0e780e50-9a7e-4d4a-a9fd-1b8607d17fbb', 'Org Unit Reference Code from Patient Organization', 'EXTRACT_TEI_DHIS_ORG_UNIT_ID',
'Extracts the organization unit ID reference from the tracked entity instance.');

-- Script that transforms Patient to Person
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('ea887943-5e94-4e31-9441-c7661fe1063e', 0, 'Transforms FHIR Patient to DHIS Person', 'TRANSFORM_FHIR_PATIENT_DHIS_PERSON', 'Transforms FHIR Patient to DHIS Person.', 'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_PATIENT', 'DHIS_TRACKED_ENTITY_INSTANCE');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ea887943-5e94-4e31-9441-c7661fe1063e', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ea887943-5e94-4e31-9441-c7661fe1063e', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ea887943-5e94-4e31-9441-c7661fe1063e', 'OUTPUT');
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
'output.setValue(args[''lastNameAttribute''], humanNameUtils.getPrimaryName(input.name).family);
output.setValue(args[''firstNameAttribute''], humanNameUtils.getSingleGiven(humanNameUtils.getPrimaryName(input.name)));
output.setOptionalValue(args[''birthDateAttribute''], dateTimeUtils.getPreciseDate(input.birthDateElement));
output.setOptionalValue(args[''genderAttribute''], input.gender);
output.setOptionalValue(args[''addressLineAttribute''], addressUtils.getSingleLine(addressUtils.getPrimaryAddress(input.address)));
output.setOptionalValue(args[''cityAttribute''], addressUtils.getPrimaryAddress(input.address).city);
output.setOptionalValue(args[''stateOfCountryAttribute''], addressUtils.getPrimaryAddress(input.address).state);
output.setOptionalValue(args[''countryAttribute''], addressUtils.getPrimaryAddress(input.address).country);
output.coordinates = geoUtils.getLocation(addressUtils.getPrimaryAddress(input.address));', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('b2cfaf30-6ede-41f2-bd6c-448e76c429a1', 'DSTU3');
INSERT INTO fhir_executable_script (id, script_id, name, code, description)
VALUES ('72451c8f-7492-4707-90b8-a3e0796de19e', 'ea887943-5e94-4e31-9441-c7661fe1063e',
'Transforms FHIR Patient to DHIS Person', 'TRANSFORM_FHIR_PATIENT_DHIS_PERSON', 'Transforms FHIR Patient to DHIS Person.');

-- Script that performs the lookup of TEI FHIR Resource from FHIR Immunization
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('d4e2822a-4422-46a3-badc-cf5604c6e11f', 0, 'Immunization TEI Lookup', 'IMMUNIZATION_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Immunization.', 'EVALUATE', 'FHIR_RESOURCE', 'FHIR_IMMUNIZATION', NULL);
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('85b3c460-6c2a-4f50-af46-ff09bf2e69df', 0, 'd4e2822a-4422-46a3-badc-cf5604c6e11f', 'input.patient.getResource()', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('85b3c460-6c2a-4f50-af46-ff09bf2e69df', 'DSTU3');
INSERT INTO fhir_executable_script (id,script_id, name, code, description)
VALUES ('a08caa8a-1cc9-4f51-b6b8-814af781a442', 'd4e2822a-4422-46a3-badc-cf5604c6e11f',
'Immunization TEI Lookup', 'IMMUNIZATION_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Immunization.');

-- FHIR resource mapping for FHIR Immunization
INSERT INTO fhir_resource_mapping (id,version,fhir_resource_type,tei_lookup_script_id,enrollment_org_lookup_script_id,event_org_lookup_script_id,event_date_lookup_script_id)
VALUES ('44a6c99c-c83c-4061-acd2-39e4101de147', 0, 'IMMUNIZATION', 'a08caa8a-1cc9-4f51-b6b8-814af781a442', 'a08caa8a-1cc9-4f51-b6b8-814af781a442', 'a08caa8a-1cc9-4f51-b6b8-814af781a442', 'a08caa8a-1cc9-4f51-b6b8-814af781a442');

-- Script that checks if given FHIR Immunization is applicable
INSERT INTO fhir_script (id,version,name,code,description,script_type,return_type,input_type,output_type)
VALUES ('ddaebe0a-8a88-4cb7-a5ca-632e86216be0', 0, 'FHIR Immunization Measles Applicable', 'FHIR_IMMUNIZATION_APPLICABLE', 'Checks if given FHIR Immunization is applicable for a data element.', 'EVALUATE', 'BOOLEAN', 'FHIR_IMMUNIZATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ddaebe0a-8a88-4cb7-a5ca-632e86216be0', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ddaebe0a-8a88-4cb7-a5ca-632e86216be0', 'INPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('bd1b72d1-a07a-4513-a0aa-6544a04fde6d', 0, 'ddaebe0a-8a88-4cb7-a5ca-632e86216be0',
'mappedVaccineCodes', 'CODE', TRUE, TRUE, NULL, 'Mapped vaccine codes that define if the FHIR Immunization is applicable for processing.');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('2f04a7c3-7041-4c12-aa75-748862271818', 0, 'ddaebe0a-8a88-4cb7-a5ca-632e86216be0', '!input.notGiven && codeUtils.containsMappedCode(input.vaccineCode, args[''mappedVaccineCodes''])', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('2f04a7c3-7041-4c12-aa75-748862271818', 'DSTU3');

-- Script that sets for a data element if a FHIR Immunization has been given
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('3487e94d-0525-44c5-a2df-ebb00a398e94', 0, 'TRANSFORM_FHIR_IMMUNIZATION_YN', 'Transforms FHIR Immunization to Y/N data element', 'Transforms FHIR Immunization to Y/N data element.',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_IMMUNIZATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('3487e94d-0525-44c5-a2df-ebb00a398e94', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('3487e94d-0525-44c5-a2df-ebb00a398e94', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('3487e94d-0525-44c5-a2df-ebb00a398e94', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('b413837d-e3eb-45f0-8fe7-bc301f06ef31', 0, '3487e94d-0525-44c5-a2df-ebb00a398e94',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element with given vaccine on which Y/N must be set.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('fe03c526-a589-4b33-a095-3ee3ad7ddb9d', 0, '3487e94d-0525-44c5-a2df-ebb00a398e94', 'output.setValue(args[''dataElement''], true, (input.hasPrimarySource()?!input.getPrimarySource():null))', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('fe03c526-a589-4b33-a095-3ee3ad7ddb9d', 'DSTU3');

-- Executable script that checks if the FHIR Immunization contains a measles vaccine and is therefore applicable for processing
INSERT INTO fhir_executable_script (id, script_id, name, code, description)
VALUES ('1f4e3e5e-1287-48fa-b4cd-e936f59cc169', 'ddaebe0a-8a88-4cb7-a5ca-632e86216be0', 'FHIR Immunization Measles Applicable', 'FHIR_IMMUNIZATION_MEASLES_APPLICABLE', 'Checks if given FHIR Immunization is a measles vaccine and is applicable.');
INSERT INTO fhir_executable_script_argument(id, version, executable_script_id, script_argument_id, override_value)
VALUES ('cf6c3462-1396-435b-ab07-f515d92a1ab2', 0, '1f4e3e5e-1287-48fa-b4cd-e936f59cc169', 'bd1b72d1-a07a-4513-a0aa-6544a04fde6d', 'VACCINE_03|VACCINE_04|VACCINE_05|VACCINE_94');

-- Rule FHIR Patient to tracked entity type Person
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, applicable_in_script_id, transform_in_script_id)
VALUES ('5f9ebdc9-852e-4c83-87ca-795946aabc35', 0, 'FHIR Patient to Person', NULL, TRUE, 0, 'PATIENT', 'TRACKED_ENTITY', '9299b82e-b90a-4542-8b78-200cadff3d7d', '72451c8f-7492-4707-90b8-a3e0796de19e');
INSERT INTO fhir_tracked_entity_rule (id, tracked_entity_ref, org_lookup_script_id, tracked_entity_identifier_ref, tracked_entity_identifier_fq)
VALUES ('5f9ebdc9-852e-4c83-87ca-795946aabc35', 'NAME:Person', '25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', 'NAME:National identifier', FALSE);

-- Tracker Program Child Programme
INSERT INTO fhir_tracker_program (id,version,name,program_ref,tracked_entity_rule_id,enabled)
VALUES ('45e61665-754d-4861-891e-a2064fc0ae7d', 0, 'Child Programme', 'NAME:Child Programme', '5f9ebdc9-852e-4c83-87ca-795946aabc35', TRUE);

-- Tracker Program Child Programme: Measles given
INSERT INTO fhir_executable_script (id, script_id, code, name)
VALUES ('51ae5077-ddd7-45dc-b8ed-29000b9faaab', '3487e94d-0525-44c5-a2df-ebb00a398e94', 'CP: Measles given', 'CP_MEASLES_GIVEN');
INSERT INTO fhir_executable_script_argument(id, version, executable_script_id, script_argument_id, override_value)
VALUES ('86759369-63d9-4a2c-84f8-e9897f69166a', 0, '51ae5077-ddd7-45dc-b8ed-29000b9faaab', 'b413837d-e3eb-45f0-8fe7-bc301f06ef31', 'NAME:CP - MCH Measles dose');

-- Rule Tracker Program Child Programme: Measles
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, applicable_in_script_id, transform_in_script_id)
VALUES ('8019cebe-da61-4aff-a2fd-579a538c8671', 0, 'Child Programme: Measles', NULL, TRUE, 0, 'IMMUNIZATION', 'PROGRAM_STAGE_EVENT', '1f4e3e5e-1287-48fa-b4cd-e936f59cc169', '51ae5077-ddd7-45dc-b8ed-29000b9faaab');
INSERT INTO fhir_program_stage_rule (id, program_id, program_stage_ref, enrollment_enabled, enrollment_id, creation_enabled, creation_applicable_script_id, creation_script_id, final_script_id)
VALUES ('8019cebe-da61-4aff-a2fd-579a538c8671','45e61665-754d-4861-891e-a2064fc0ae7d', 'NAME:Child Programme', TRUE, NULL, TRUE, NULL, NULL, NULL);
