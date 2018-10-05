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
  CONSTRAINT fhir_tracked_entity_rule_pk PRIMARY KEY (id),
  CONSTRAINT fhir_tracked_entity_rule_fk1 FOREIGN KEY (id) REFERENCES fhir_rule (id) ON DELETE CASCADE
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
  CONSTRAINT fhir_resource_mapping_uk1 UNIQUE (fhir_resource_type)
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
