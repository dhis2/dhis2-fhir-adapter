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

CREATE TABLE fhir_data_type_enum (
  value VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_data_type_enum_pk PRIMARY KEY(value)
);
COMMENT ON TABLE fhir_data_type_enum IS 'Enumeration values with supported data types for argument and return types.';
INSERT INTO fhir_data_type_enum VALUES('BOOLEAN');
INSERT INTO fhir_data_type_enum VALUES('STRING');
INSERT INTO fhir_data_type_enum VALUES('INTEGER');
INSERT INTO fhir_data_type_enum VALUES('DOUBLE');
INSERT INTO fhir_data_type_enum VALUES('DATE_TIME');
INSERT INTO fhir_data_type_enum VALUES('DATE_UNIT');
INSERT INTO fhir_data_type_enum VALUES('WEIGHT_UNIT');
INSERT INTO fhir_data_type_enum VALUES('CONSTANT');
INSERT INTO fhir_data_type_enum VALUES('CODE');
INSERT INTO fhir_data_type_enum VALUES('LOCATION');
INSERT INTO fhir_data_type_enum VALUES('PATTERN');
INSERT INTO fhir_data_type_enum VALUES('ORG_UNIT_REF');
INSERT INTO fhir_data_type_enum VALUES('TRACKED_ENTITY_REF');
INSERT INTO fhir_data_type_enum VALUES('TRACKED_ENTITY_ATTRIBUTE_REF');
INSERT INTO fhir_data_type_enum VALUES('DATA_ELEMENT_REF');
INSERT INTO fhir_data_type_enum VALUES('PROGRAM_REF');
INSERT INTO fhir_data_type_enum VALUES('PROGRAM_STAGE_REF');
INSERT INTO fhir_data_type_enum VALUES('FHIR_RESOURCE');
INSERT INTO fhir_data_type_enum VALUES('EVENT_DECISION_TYPE');

CREATE TABLE fhir_transform_data_type_enum (
  value VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_transform_data_type_enum_pk PRIMARY KEY(value)
);
COMMENT ON TABLE fhir_transform_data_type_enum IS 'Enumeration values with supported transformation data types.';
INSERT INTO fhir_transform_data_type_enum VALUES('DHIS_TRACKED_ENTITY_INSTANCE');
INSERT INTO fhir_transform_data_type_enum VALUES('DHIS_ENROLLMENT');
INSERT INTO fhir_transform_data_type_enum VALUES('DHIS_EVENT');
INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_PATIENT');
INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_IMMUNIZATION');
INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_OBSERVATION');
INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_DIAGNOSTIC_REPORT');

CREATE TABLE fhir_resource_type_enum (
  value VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_resource_type_enum_pk PRIMARY KEY(value)
);
COMMENT ON TABLE fhir_resource_type_enum IS 'Enumeration values with supported FHIR Resources.';
INSERT INTO fhir_resource_type_enum VALUES('DIAGNOSTIC_REPORT');
INSERT INTO fhir_resource_type_enum VALUES('IMMUNIZATION');
INSERT INTO fhir_resource_type_enum VALUES('LOCATION');
INSERT INTO fhir_resource_type_enum VALUES('MEDICATION_REQUEST');
INSERT INTO fhir_resource_type_enum VALUES('OBSERVATION');
INSERT INTO fhir_resource_type_enum VALUES('ORGANIZATION');
INSERT INTO fhir_resource_type_enum VALUES('PATIENT');

CREATE TABLE fhir_dhis_resource_type_enum (
  value VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_dhis_resource_type_enum_pk PRIMARY KEY(value)
);
COMMENT ON TABLE fhir_dhis_resource_type_enum IS 'Enumeration values with supported DHIS2 Resources.';
INSERT INTO fhir_dhis_resource_type_enum VALUES('TRACKED_ENTITY');
INSERT INTO fhir_dhis_resource_type_enum VALUES('ENROLLMENT');
INSERT INTO fhir_dhis_resource_type_enum VALUES('PROGRAM_STAGE_EVENT');

CREATE TABLE fhir_version_enum (
  value VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_version_enum_pk PRIMARY KEY(value)
);
COMMENT ON TABLE fhir_version_enum IS 'Enumeration values with supported FHIR versions.';
INSERT INTO fhir_version_enum VALUES('DSTU3');

CREATE TABLE fhir_subscription_type_enum (
  value VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_subscription_type_enum_pk PRIMARY KEY(value)
);
COMMENT ON TABLE fhir_subscription_type_enum IS 'Enumeration values with supported FHIR subscription types.';
INSERT INTO fhir_subscription_type_enum VALUES('REST_HOOK');
INSERT INTO fhir_subscription_type_enum VALUES('REST_HOOK_WITH_JSON_PAYLOAD');

CREATE TABLE fhir_event_status_enum (
  value VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_event_status_enum_pk PRIMARY KEY(value)
);
COMMENT ON TABLE fhir_event_status_enum IS 'Enumeration values with available event status.';
INSERT INTO fhir_event_status_enum VALUES('OVERDUE');
INSERT INTO fhir_event_status_enum VALUES('ACTIVE');
INSERT INTO fhir_event_status_enum VALUES('SCHEDULE');
INSERT INTO fhir_event_status_enum VALUES('VISITED');
INSERT INTO fhir_event_status_enum VALUES('COMPLETED');
INSERT INTO fhir_event_status_enum VALUES('SKIPPED');

CREATE TABLE fhir_script_source_type_enum (
  value VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_script_source_type_enum_pk PRIMARY KEY(value)
);
COMMENT ON TABLE fhir_script_source_type_enum IS 'Enumeration values with supported script source types (i.e. script labguages).';
INSERT INTO fhir_script_source_type_enum VALUES('JAVASCRIPT');

CREATE TABLE fhir_script_type_enum (
  value VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_script_type_enum_pk PRIMARY KEY(value)
);
COMMENT ON TABLE fhir_script_type_enum IS 'Enumeration values with supported script types.';
INSERT INTO fhir_script_type_enum VALUES('TRANSFORM_TO_DHIS');
INSERT INTO fhir_script_type_enum VALUES('EVALUATE');

CREATE TABLE fhir_event_period_day_type_enum (
  value VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_event_period_day_type_enum_pk PRIMARY KEY(value)
);
COMMENT ON TABLE fhir_event_period_day_type_enum IS 'Enumeration values with supported event period day types.';
INSERT INTO fhir_event_period_day_type_enum VALUES('EVENT_DATE');
INSERT INTO fhir_event_period_day_type_enum VALUES('DUE_DATE');
INSERT INTO fhir_event_period_day_type_enum VALUES('ORIG_DUE_DATE');
INSERT INTO fhir_event_period_day_type_enum VALUES('EVENT_UPDATED_DATE');
INSERT INTO fhir_event_period_day_type_enum VALUES('VALUE_UPDATED_DATE');

CREATE TABLE fhir_script_variable_enum (
  value VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_script_variable_enum_pk PRIMARY KEY(value)
);
COMMENT ON TABLE fhir_script_variable_enum IS 'Enumeration values with supported script variables.';
INSERT INTO fhir_script_variable_enum VALUES('CONTEXT');
INSERT INTO fhir_script_variable_enum VALUES('INPUT');
INSERT INTO fhir_script_variable_enum VALUES('OUTPUT');
INSERT INTO fhir_script_variable_enum VALUES('TRACKED_ENTITY_ATTRIBUTES');
INSERT INTO fhir_script_variable_enum VALUES('TRACKED_ENTITY_TYPE');
INSERT INTO fhir_script_variable_enum VALUES('TRACKED_ENTITY_INSTANCE');
INSERT INTO fhir_script_variable_enum VALUES('PROGRAM');
INSERT INTO fhir_script_variable_enum VALUES('PROGRAM_STAGE');
INSERT INTO fhir_script_variable_enum VALUES('ENROLLMENT');
INSERT INTO fhir_script_variable_enum VALUES('EVENT');
INSERT INTO fhir_script_variable_enum VALUES('DATE_TIME');
INSERT INTO fhir_script_variable_enum VALUES('ORGANIZATION_UNIT_ID');
INSERT INTO fhir_script_variable_enum VALUES('PROGRAM_STAGE_EVENT');

CREATE TABLE fhir_authentication_method_enum (
  value VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_authentication_method_enum_pk PRIMARY KEY(value)
);
COMMENT ON TABLE fhir_authentication_method_enum IS 'Enumeration values with supported authentication methods.';
INSERT INTO fhir_authentication_method_enum VALUES('BASIC');

CREATE TABLE fhir_constant (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  name            VARCHAR(230)                   NOT NULL,
  description     TEXT,
  category        VARCHAR(30)                    NOT NULL,
  code            VARCHAR(50)                    NOT NULL,
  data_type       VARCHAR(30)                    NOT NULL,
  value           VARCHAR(250),
  CONSTRAINT fhir_constant_pk PRIMARY KEY (id),
  CONSTRAINT fhir_constant_uk_name UNIQUE (name),
  CONSTRAINT fhir_constant_uk_code UNIQUE (code),
  CONSTRAINT fhir_constant_fk1 FOREIGN KEY(data_type) REFERENCES fhir_data_type_enum(value)
);
COMMENT ON TABLE fhir_constant IS 'Contains mappings between a code and a value. The code may be GENDER_FEMALE and the value may be the option set value for female that has been configured in DHIS2. This can be used to map between the FHIR enumerations and frequently used DHIS2 option set values.';
COMMENT ON COLUMN fhir_constant.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_constant.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_constant.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_constant.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_constant.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_constant.name IS 'The unique name of the constant.';
COMMENT ON COLUMN fhir_constant.code IS 'The unique code of the constant. This is used in mappings to reference the constant.';
COMMENT ON COLUMN fhir_constant.description IS 'An optional description text of the constant.';
COMMENT ON COLUMN fhir_constant.category IS 'The category of the constant (enumeration value that is supported by the application).';
COMMENT ON COLUMN fhir_constant.data_type IS 'The data type of the value of the constant (enumeration value that is supported by the application).';
COMMENT ON COLUMN fhir_constant.value IS 'The value of the constant. The conversion of the value to the specified data type must be possible.';

CREATE TABLE fhir_script (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  name            VARCHAR(230)                   NOT NULL,
  code            VARCHAR(100)                   NOT NULL,
  script_type     VARCHAR(30)                    NOT NULL,
  return_type     VARCHAR(30)                    NOT NULL,
  input_type      VARCHAR(30),
  output_type     VARCHAR(30),
  description     TEXT,
  CONSTRAINT fhir_script_pk PRIMARY KEY (id),
  CONSTRAINT fhir_script_u1 UNIQUE (code),
  CONSTRAINT fhir_script_fk1 FOREIGN KEY(return_type) REFERENCES fhir_data_type_enum(value),
  CONSTRAINT fhir_script_fk2 FOREIGN KEY(input_type) REFERENCES fhir_transform_data_type_enum(value),
  CONSTRAINT fhir_script_fk3 FOREIGN KEY(output_type) REFERENCES fhir_transform_data_type_enum(value),
  CONSTRAINT fhir_script_fk4 FOREIGN KEY(script_type) REFERENCES fhir_script_type_enum(value)
);
COMMENT ON TABLE fhir_script IS 'Contains scripts that are used for evaluations an d transformations. The script itself cannot be executed. To execute a script and executable script that references this script must be created.';
COMMENT ON COLUMN fhir_script.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_script.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_script.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_script.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_script.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_script.name IS 'The unique name of the script.';
COMMENT ON COLUMN fhir_script.code IS 'The unique code of the script.';
COMMENT ON COLUMN fhir_script.script_type IS 'The concrete type of the script, which describes its purpose (enumeration value that is supported by the application).';
COMMENT ON COLUMN fhir_script.description IS 'An optional description text of the script. It should describe the detailed purpose of the script.';
COMMENT ON COLUMN fhir_script.return_type IS 'The expected return or result value type of the script (enumeration value that is supported by the application).';
COMMENT ON COLUMN fhir_script.input_type IS 'The type of the input data when the script is used for transformations (enumeration value that is supported by the application).';
COMMENT ON COLUMN fhir_script.output_type IS 'The type of the output data when the script is used for transformations (enumeration value that is supported by the application).';

CREATE TABLE fhir_script_source (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  script_id       UUID                           NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  source_text     TEXT                           NOT NULL,
  source_type     VARCHAR(30)                    NOT NULL,
  CONSTRAINT fhir_script_source_pk PRIMARY KEY (id),
  CONSTRAINT fhir_script_source_fk1 FOREIGN KEY (script_id) REFERENCES fhir_script (id) ON DELETE CASCADE,
  CONSTRAINT fhir_script_source_fk2 FOREIGN KEY (source_type) REFERENCES fhir_script_source_type_enum (value)
);
CREATE INDEX fhir_script_source_i1
  ON fhir_script_source (script_id);
COMMENT ON TABLE fhir_script_source IS 'Contains the source code of a script. The source code of the script has the specified source type (programming language) and may be restricted to specific FHIR versions.';
COMMENT ON COLUMN fhir_script_source.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_script_source.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_script_source.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_script_source.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_script_source.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_script_source.source_text IS 'The script source code (may also be a reference to an existing Java method that can be invoked).';
COMMENT ON COLUMN fhir_script_source.source_type IS 'The source type, i.e. programming language of the script (enumeration value that is supported by the application).';
COMMENT ON COLUMN fhir_script_source.script_id IS 'The reference to the script to which this source belongs. A script can have more than one sources (maximum one for each FHIR Version).';

CREATE TABLE fhir_script_source_version (
  script_source_id UUID        NOT NULL,
  fhir_version     VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_script_source_version_pk PRIMARY KEY (script_source_id, fhir_version),
  CONSTRAINT fhir_script_source_version_fk1 FOREIGN KEY (script_source_id) REFERENCES fhir_script_source (id) ON DELETE CASCADE,
  CONSTRAINT fhir_script_source_version_fk2 FOREIGN KEY(fhir_version) REFERENCES fhir_version_enum(value)
);
COMMENT ON TABLE fhir_script_source_version IS 'Contains the reference to the script source table to which this entity belongs to. Multiple version can be assigned to as single script source.';
COMMENT ON COLUMN fhir_script_source_version.script_source_id IS 'Contains the reference to the script source table to which this entity belongs to.';
COMMENT ON COLUMN fhir_script_source_version.fhir_version IS 'Contains the FHIR version for which the referenced script source can be used ((enumeration value that is supported by the application).';

CREATE TABLE fhir_script_variable (
  script_id UUID        NOT NULL,
  variable  VARCHAR(30) NOT NULL,
  CONSTRAINT fhir_script_variable_pk PRIMARY KEY (script_id, variable),
  CONSTRAINT fhir_script_variable_fk1 FOREIGN KEY (script_id) REFERENCES fhir_script (id) ON DELETE CASCADE,
  CONSTRAINT fhir_script_variable_fk2 FOREIGN KEY (variable) REFERENCES fhir_script_variable_enum(value)
);
COMMENT ON TABLE fhir_script_variable IS 'Contains the variables that are required by a script.';
COMMENT ON COLUMN fhir_script_variable.script_id IS 'The reference to the script that requires the variables.';
COMMENT ON COLUMN fhir_script_variable.variable IS 'The variable that is required by the script (enumeration value that is supported by the application).';

CREATE TABLE fhir_script_argument (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  script_id       UUID                           NOT NULL,
  name            VARCHAR(30)                    NOT NULL,
  data_type       VARCHAR(30)                    NOT NULL,
  mandatory       BOOLEAN                        NOT NULL DEFAULT TRUE,
  array_value     BOOLEAN                        NOT NULL DEFAULT FALSE,
  default_value   VARCHAR(1000),
  description     TEXT,
  CONSTRAINT fhir_script_argument_pk PRIMARY KEY (id),
  CONSTRAINT fhir_script_argument_fk1 FOREIGN KEY (script_id) REFERENCES fhir_script (id) ON DELETE CASCADE,
  CONSTRAINT fhir_script_argument_fk2 FOREIGN KEY (data_type) REFERENCES fhir_data_type_enum(value),
  CONSTRAINT fhir_script_argument_uk1 UNIQUE (script_id, name)
);
CREATE INDEX fhir_script_argument_i1
  ON fhir_script_argument (script_id);
COMMENT ON TABLE fhir_script_argument IS 'Contains a single input argument of a script.';
COMMENT ON COLUMN fhir_script_argument.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_script_argument.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_script_argument.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_script_argument.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_script_argument.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_script_argument.name IS 'The name of the argument that is also used inside the script to access the argument value (e.g. args[''programRef'']).';
COMMENT ON COLUMN fhir_script_argument.data_type IS 'The data type of the argument (enumeration value that is supported by the application).';
COMMENT ON COLUMN fhir_script_argument.mandatory IS 'Specifies if the value of this argument is mandatory when executing a script. If an argument is mandatory and no value has been supplied when executing the script, the script cannot be executed.';
COMMENT ON COLUMN fhir_script_argument.array_value IS 'Specifies if the argument is a single dimensional array. If an argument is an array, its values must be separated by a pipe character when specifying these.';
COMMENT ON COLUMN fhir_script_argument.default_value IS 'Specifies the default value of this argument. This can be overridden when creating an executable script. If the executable script does not override this value, this value is used. The value must match the data type of this argument.';
COMMENT ON COLUMN fhir_script_argument.description IS 'The detailed description of the purpose of this script argument.';
COMMENT ON COLUMN fhir_script_argument.script_id IS 'The reference to the script to which this argument belongs.';

CREATE TABLE fhir_executable_script (
  id              UUID NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  script_id       UUID NOT NULL,
  name            VARCHAR(230) NOT NULL,
  code            VARCHAR(100) NOT NULL,
  description     TEXT,
  CONSTRAINT fhir_executable_script_pk PRIMARY KEY (id),
  CONSTRAINT fhir_executable_script_uk_name UNIQUE (name),
  CONSTRAINT fhir_executable_script_uk_code UNIQUE (code),
  CONSTRAINT fhir_executable_script_fk1 FOREIGN KEY (script_id) REFERENCES fhir_script (id)
);
CREATE INDEX fhir_executable_script_i1
  ON fhir_executable_script (script_id);
COMMENT ON TABLE fhir_executable_script IS 'Contains the executable script of a script. The executable script must provide all missing mandatory input argument values and may also override input argument values that has been defined by the script.';
COMMENT ON COLUMN fhir_executable_script.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_executable_script.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_executable_script.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_executable_script.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_executable_script.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_executable_script.script_id IS 'References the executable script on which this executable script is based on.';
COMMENT ON COLUMN fhir_executable_script.name IS 'The unique name of the executable script.';
COMMENT ON COLUMN fhir_executable_script.code IS 'The unique code of the executable script.';
COMMENT ON COLUMN fhir_executable_script.description IS 'The detailed description of the purpose of the executable script.';

CREATE TABLE fhir_executable_script_argument (
  id                   UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
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
COMMENT ON TABLE fhir_executable_script_argument IS 'Contains the overridden input argument for an executable script. At least all mandatory input arguments of the script for which no default values have been provided must be overridden.';
COMMENT ON COLUMN fhir_executable_script_argument.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_executable_script_argument.executable_script_id IS 'The reference to the executable script to which this overridden input argument value belongs to.';
COMMENT ON COLUMN fhir_executable_script_argument.script_argument_id IS 'The reference to the overridden argument of the script.';
COMMENT ON COLUMN fhir_executable_script_argument.override_value IS 'The override value of the script argument. The value must match the data type of this argument.';
COMMENT ON COLUMN fhir_executable_script_argument.enabled IS 'Specifies if this overridden value should be used when executing the script. Otherwise the default input argument value is used.';

CREATE TABLE fhir_code_category (
  id              UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version         BIGINT                         NOT NULL,
  created_at      TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by VARCHAR(11),
  last_updated_at TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  name            VARCHAR(230)                   NOT NULL,
  code            VARCHAR(50)                    NOT NULL,
  description     TEXT,
  CONSTRAINT fhir_code_category_pk PRIMARY KEY (id),
  CONSTRAINT fhir_code_category_uk_name UNIQUE (name),
  CONSTRAINT fhir_code_category_uk_code UNIQUE (code)
);
COMMENT ON TABLE fhir_code_category IS 'Contains a category for defined codes. The category is only required for grouping codes in order to finding and using them manually.';
COMMENT ON COLUMN fhir_code_category.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_code_category.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_code_category.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_code_category.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_code_category.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_code_category.name IS 'The unique name of the code category.';
COMMENT ON COLUMN fhir_code_category.code IS 'The unique code of the code category.';
COMMENT ON COLUMN fhir_code_category.description IS 'The detailed description that describes for which purpose the code category is used.';

CREATE TABLE fhir_code (
  id               UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version          BIGINT                         NOT NULL,
  created_at       TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by  VARCHAR(11),
  last_updated_at  TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  code_category_id UUID                           NOT NULL,
  name             VARCHAR(230)                   NOT NULL,
  code             VARCHAR(50)                    NOT NULL,
  mapped_code      VARCHAR(50),
  description      TEXT,
  CONSTRAINT fhir_code_pk PRIMARY KEY (id),
  CONSTRAINT fhir_code_fk1 FOREIGN KEY (code_category_id) REFERENCES fhir_code_category (id),
  CONSTRAINT fhir_code_uk_name UNIQUE (name),
  CONSTRAINT fhir_code_uk_code UNIQUE (code)
);
CREATE INDEX fhir_code_i1
  ON fhir_code (code_category_id);
CREATE INDEX fhir_code_i2
  ON fhir_code (mapped_code);
COMMENT ON TABLE fhir_code IS 'Contains unique codes that can be used by rules, transformations and scripts to reference system dependent codes (e.g. vaccine CVX codes).';
COMMENT ON COLUMN fhir_code.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_code.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_code.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_code.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_code.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_code.code_category_id IS 'References the code category to which the code belongs to.';
COMMENT ON COLUMN fhir_code.name IS 'The unique name of the code.';
COMMENT ON COLUMN fhir_code.code IS 'The unique code that can be used by rules, transformations and scripts.';
COMMENT ON COLUMN fhir_code.mapped_code IS 'The optional mapped code (unless it is the same like the code) that is used by DHIS2 (e.g. DHIS2 organization unit code).';
COMMENT ON COLUMN fhir_code.description IS 'The detailed description that describes for which purpose the code is used. This may also contain details about the license that affects the code. In such a case the description must not be changed.';

CREATE TABLE fhir_code_set (
  id               UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version          BIGINT                         NOT NULL,
  created_at       TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by  VARCHAR(11),
  last_updated_at  TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  code_category_id UUID                           NOT NULL,
  name             VARCHAR(230)                   NOT NULL,
  code             VARCHAR(50)                    NOT NULL,
  description      TEXT,
  CONSTRAINT fhir_code_set_pk PRIMARY KEY (id),
  CONSTRAINT fhir_code_set_fk1 FOREIGN KEY (code_category_id) REFERENCES fhir_code_category (id),
  CONSTRAINT fhir_code_set_uk_name UNIQUE (name),
  CONSTRAINT fhir_code_set_uk_code UNIQUE (code)
);
CREATE INDEX fhir_code_set_i1
  ON fhir_code_set (code_category_id);
COMMENT ON TABLE fhir_code_set IS 'Defines a combination of codes (e.g. all vaccine codes that cover DTP/DTaP immunization).';
COMMENT ON COLUMN fhir_code_set.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_code_set.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_code_set.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_code_set.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_code_set.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_code_set.name IS 'The unique name of the code set.';
COMMENT ON COLUMN fhir_code_set.code IS 'The unique code of the code set.';
COMMENT ON COLUMN fhir_code_set.description IS 'The detailed description that describes for which purpose the code set is used.';
COMMENT ON COLUMN fhir_code_set.code_category_id IS 'References the code category to which this code set and its codes belongs to.';

CREATE TABLE fhir_code_set_value (
  code_set_id      UUID    NOT NULL,
  code_id          UUID    NOT NULL,
  enabled          BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT fhir_code_set_value_pk PRIMARY KEY (code_set_id, code_id),
  CONSTRAINT fhir_code_set_value_fk1 FOREIGN KEY (code_set_id) REFERENCES fhir_code_set (id) ON DELETE CASCADE,
  CONSTRAINT fhir_code_set_value_fk2 FOREIGN KEY (code_id) REFERENCES fhir_code (id) ON DELETE CASCADE
);
CREATE INDEX fhir_code_set_value_i1
  ON fhir_code_set_value (code_id);
COMMENT ON TABLE fhir_code_set_value IS 'Contains mapping between the code set and its assigned codes. The mapping for individual codes may be disabled.';
COMMENT ON COLUMN fhir_code_set_value.code_set_id IS 'Contains the reference to the code set that owns this mapping.';
COMMENT ON COLUMN fhir_code_set_value.code_id IS 'Contains the reference to the code that is assigned to the code set of this mapping..';
COMMENT ON COLUMN fhir_code_set_value.enabled IS 'Disabling a code of the code set avoids that the code has to be removed from the code set. The mapping can be switched off temporarily or can be kept for historically reasons.';

CREATE TABLE fhir_system (
  id                    UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version               BIGINT                         NOT NULL,
  created_at            TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by       VARCHAR(11),
  last_updated_at       TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  name                  VARCHAR(230)                   NOT NULL,
  code                  VARCHAR(50)                    NOT NULL,
  system_uri            VARCHAR(120)                   NOT NULL,
  enabled               BOOLEAN                        NOT NULL DEFAULT TRUE,
  description           TEXT,
  description_protected BOOLEAN                        NOT NULL DEFAULT FALSE,
  CONSTRAINT fhir_system_pk PRIMARY KEY (id),
  CONSTRAINT fhir_system_uk_name UNIQUE (name),
  CONSTRAINT fhir_system_uk_code UNIQUE (code),
  CONSTRAINT fhir_system_uk_uri UNIQUE (system_uri)
);
COMMENT ON TABLE fhir_system IS 'Contains the definition of all system URIs (e.g. system URI of vaccine CVS codes or also system URI for identifiers of patients) that are used by FHIR Resources.';
COMMENT ON COLUMN fhir_system.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_system.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_system.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_system.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_system.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_system.name IS 'The unique name of the system URI.';
COMMENT ON COLUMN fhir_system.code IS 'The unique code of the system URI.';
COMMENT ON COLUMN fhir_system.enabled IS 'Defines if codes of this system are enabled. If codes of this system are not enabled these will not be used for processing of any rule, transformation or mapping.';
COMMENT ON COLUMN fhir_system.description IS 'The detailed description that describes for which purpose the system URI is used. This may also contain details about the license that affects using codes of this system. In such a case the description must not be changed.';
COMMENT ON COLUMN fhir_system.description_protected IS 'Defines if the description cannot be changed (e.g. contains mandatory details about license restrictions).';
COMMENT ON COLUMN fhir_system.system_uri IS 'The system URI (e.g. http://hl7.org/fhir/sid/cvx) the system that is defined by this entity.';

CREATE TABLE fhir_system_code (
  id                UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version           BIGINT                         NOT NULL,
  created_at        TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by   VARCHAR(11),
  last_updated_at   TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  system_id         UUID                           NOT NULL,
  code_id           UUID                           NOT NULL,
  system_code       VARCHAR(120)                   NOT NULL,
  system_code_value VARCHAR(241),
  CONSTRAINT fhir_system_code_pk PRIMARY KEY (id),
  CONSTRAINT fhir_system_code_fk1 FOREIGN KEY (system_id) REFERENCES fhir_system (id),
  CONSTRAINT fhir_system_code_fk2 FOREIGN KEY (code_id) REFERENCES fhir_code (id) ON DELETE CASCADE
);
CREATE INDEX fhir_system_code_i1
  ON fhir_system_code (system_id, system_code);
CREATE INDEX fhir_system_code_i2
  ON fhir_system_code (code_id);
COMMENT ON TABLE fhir_system_code IS 'Contains the mapping between a code and a system specific code (e.g. the internal code for a vaccine that is used by the adapter to the national vaccine code that is used by a specific country).';
COMMENT ON COLUMN fhir_system_code.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_system_code.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_system_code.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_system_code.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_system_code.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_system_code.system_id IS 'References the system to which this code belongs to.';
COMMENT ON COLUMN fhir_system_code.system_code IS 'The system specific code (e.g. a CVX vaccine code or country or region specific vaccine code).';
COMMENT ON COLUMN fhir_system_code.code_id IS 'References the adapter internal code that is used by rules, transformations, mappings and in scripts.';
COMMENT ON COLUMN fhir_system_code.system_code_value IS 'Combination of system URI and code separated by a pipe character. This is mainly used for search purpose.';

CREATE TABLE fhir_rule (
  id                      UUID                           NOT NULL     DEFAULT UUID_GENERATE_V4(),
  version                 BIGINT                         NOT NULL,
  created_at              TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL     DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by         VARCHAR(11),
  last_updated_at         TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL     DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  name                    VARCHAR(230)                   NOT NULL,
  description             TEXT,
  enabled                 BOOLEAN                        NOT NULL     DEFAULT TRUE,
  evaluation_order        INTEGER                        NOT NULL     DEFAULT 0,
  fhir_resource_type      VARCHAR(30)                    NOT NULL,
  dhis_resource_type      VARCHAR(30)                    NOT NULL,
  applicable_in_script_id UUID,
  applicable_code_set_id  UUID,
  transform_in_script_id  UUID                           NOT NULL,
  CONSTRAINT fhir_rule_pk PRIMARY KEY (id),
  CONSTRAINT fhir_rule_uk_name UNIQUE (name),
  CONSTRAINT fhir_rule_fk1 FOREIGN KEY (applicable_in_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_rule_fk2 FOREIGN KEY (applicable_code_set_id) REFERENCES fhir_code_set (id),
  CONSTRAINT fhir_rule_fk3 FOREIGN KEY (transform_in_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_rule_fk4 FOREIGN KEY (fhir_resource_type) REFERENCES fhir_resource_type_enum(value),
  CONSTRAINT fhir_rule_fk5 FOREIGN KEY (dhis_resource_type) REFERENCES fhir_dhis_resource_type_enum(value)
);
CREATE INDEX fhir_rule_i1
  ON fhir_rule (applicable_in_script_id);
CREATE INDEX fhir_rule_i2
  ON fhir_rule (applicable_code_set_id);
CREATE INDEX fhir_rule_i3
  ON fhir_rule (transform_in_script_id);
COMMENT ON TABLE fhir_rule IS 'Contains the base information of a rule that defines the transformation from FHIR to DHIS2. Depending on the specified DHIS2 Resource Type also an additional record with the same ID must be created.';
COMMENT ON COLUMN fhir_rule.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_rule.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_rule.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_rule.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_rule.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_rule.name IS 'The unique name of the rule.';
COMMENT ON COLUMN fhir_rule.description IS 'The detailed description about the purpose of the rule.';
COMMENT ON COLUMN fhir_rule.enabled IS 'Specifies if the rule has been enabled and is used for transformations.';
COMMENT ON COLUMN fhir_rule.evaluation_order IS 'Defines the precedence of the evaluation. Higher numbers guarantees that a rule is being processed before other matching rules. If two matching rules have the same order, they may be executed in an undefined order (but remains deterministic for a configured adapter).';
COMMENT ON COLUMN fhir_rule.fhir_resource_type IS 'The FHIR Resource Type of the FHIR Resource to be processed. This rule will only be used when the resource type matches.';
COMMENT ON COLUMN fhir_rule.dhis_resource_type IS 'The resulting DHIS2 Resource Type of the transformation process. Depending on this value also other depending tables must be filled with data.';
COMMENT ON COLUMN fhir_rule.applicable_in_script_id IS 'References the evaluation script that is used to evaluate if the input is applicable to be processed by this rule. If no script has been specified, the rule is applicable for further processing. The script will not be executed if the code set with applicable codes do not match.';
COMMENT ON COLUMN fhir_rule.applicable_code_set_id IS 'References the code set that is used to evaluate if the input is applicable to be processed by this rule. If no code included in the input matches the code of the specified code set, the rule will not be applicable. If no code set is specified, the applicable set (if any) will be used to evaluate if the rule is applicable for further processing.';
COMMENT ON COLUMN fhir_rule.transform_in_script_id IS 'References the transformation script that is used to transform the input to the DHIS2 Resource.';

CREATE TABLE fhir_tracked_entity_rule (
  id                            UUID         NOT NULL,
  tracked_entity_ref            VARCHAR(230) NOT NULL,
  org_lookup_script_id          UUID         NOT NULL,
  loc_lookup_script_id          UUID         NOT NULL,
  tracked_entity_identifier_ref VARCHAR(230) NOT NULL,
  CONSTRAINT fhir_tracked_entity_rule_pk PRIMARY KEY (id),
  CONSTRAINT fhir_tracked_entity_rule_fk1 FOREIGN KEY (id) REFERENCES fhir_rule (id) ON DELETE CASCADE,
  CONSTRAINT fhir_tracked_entity_rule_fk2 FOREIGN KEY (org_lookup_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_tracked_entity_rule_fk3 FOREIGN KEY (loc_lookup_script_id) REFERENCES fhir_executable_script (id)
);
CREATE INDEX fhir_tracked_entity_rule_i1
  ON fhir_tracked_entity_rule (org_lookup_script_id);
CREATE INDEX fhir_tracked_entity_rule_i2
  ON fhir_tracked_entity_rule (loc_lookup_script_id);
COMMENT ON TABLE fhir_tracked_entity_rule IS 'Contains rules for DHIS2 Tracked Entity Resource Types.';
COMMENT ON COLUMN fhir_tracked_entity_rule.id IS 'References the rule to which this tracked entity rule belongs to.';
COMMENT ON COLUMN fhir_tracked_entity_rule.tracked_entity_ref IS 'The reference of the DHIS2 Tracked Entity Type (e.g. "NAME:Person").';
COMMENT ON COLUMN fhir_tracked_entity_rule.org_lookup_script_id IS 'References the executable lookup script for DHIS2 Organization Units.';
COMMENT ON COLUMN fhir_tracked_entity_rule.loc_lookup_script_id IS 'References the executable lookup script for DHIS2 coordinates (longitude and latitude).';
COMMENT ON COLUMN fhir_tracked_entity_rule.tracked_entity_identifier_ref IS 'The reference of the DHIS2 Tracked Entity Attribute that includes the identifier value that is also used by FHIR resources (e.g. NAME:National identifier).';

CREATE TABLE fhir_tracker_program (
  id                            UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version                       BIGINT                         NOT NULL,
  created_at                    TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by               VARCHAR(11),
  last_updated_at               TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  name                          VARCHAR(230)                   NOT NULL,
  description                   TEXT,
  program_ref                   VARCHAR(230)                   NOT NULL,
  enabled                       BOOLEAN                        NOT NULL DEFAULT TRUE,
  tracked_entity_rule_id        UUID                           NOT NULL,
  creation_enabled              BOOLEAN                        NOT NULL DEFAULT FALSE,
  creation_applicable_script_id UUID,
  creation_script_id            UUID,
  enrollment_date_is_incident   BOOLEAN                        NOT NULL DEFAULT FALSE,
  before_script_id              UUID,
  after_script_id               UUID,
  CONSTRAINT fhir_tracker_program_pk PRIMARY KEY (id),
  CONSTRAINT fhir_tracker_program_uk_name UNIQUE (name),
  CONSTRAINT fhir_tracker_program_uk_program UNIQUE (program_ref),
  CONSTRAINT fhir_tracker_program_fk1 FOREIGN KEY (tracked_entity_rule_id) REFERENCES fhir_tracked_entity_rule(id),
  CONSTRAINT fhir_tracker_program_fk2 FOREIGN KEY (creation_applicable_script_id) REFERENCES fhir_executable_script(id),
  CONSTRAINT fhir_tracker_program_fk3 FOREIGN KEY (creation_script_id) REFERENCES fhir_executable_script(id),
  CONSTRAINT fhir_tracker_program_fk4 FOREIGN KEY (before_script_id) REFERENCES fhir_executable_script(id),
  CONSTRAINT fhir_tracker_program_fk5 FOREIGN KEY (after_script_id) REFERENCES fhir_executable_script(id)
);
CREATE INDEX fhir_tracker_program_i1
  ON fhir_tracker_program (tracked_entity_rule_id);
CREATE INDEX fhir_tracker_program_i2
  ON fhir_tracker_program (creation_applicable_script_id);
CREATE INDEX fhir_tracker_program_i3
  ON fhir_tracker_program (creation_script_id);
CREATE INDEX fhir_tracker_program_i4
  ON fhir_tracker_program (before_script_id);
CREATE INDEX fhir_tracker_program_i5
  ON fhir_tracker_program (after_script_id);
COMMENT ON TABLE fhir_tracker_program IS 'Contains mappings of DHIS2 Tracker Programs.';
COMMENT ON COLUMN fhir_tracker_program.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_tracker_program.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_tracker_program.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_tracker_program.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_tracker_program.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_tracker_program.name IS 'The unique name of the tracker program (just used by the adapter).';
COMMENT ON COLUMN fhir_tracker_program.description IS 'The detailed description about the purpose of the tracker program and its mapping in the adapter.';
COMMENT ON COLUMN fhir_tracker_program.program_ref IS 'The reference of the DHIS2 Tracker Program (e.g. "NAME:Child Programme")';
COMMENT ON COLUMN fhir_tracker_program.enabled IS 'Defines if this DHIS2 Tracker Program has been enabled for processing. If this tracker program has not been enabled for processing, rules belonging to this tracker program will not be regarded as applicable.';
COMMENT ON COLUMN fhir_tracker_program.creation_enabled IS 'Specifies if an enrollment of a tracked entity into this program can be made by the adapter.';
COMMENT ON COLUMN fhir_tracker_program.creation_applicable_script_id IS 'References an evaluation script that evaluates if the enrollment is applicable. If the enrollment is not applicable, the executed rule is also not applicable for processing the input data. If no script has been specified, the creation is applicable.';
COMMENT ON COLUMN fhir_tracker_program.creation_script_id IS 'References the creation script of an enrollment. If no creation script has been specified the default values for the data required by the enrollment is used.';
COMMENT ON COLUMN fhir_tracker_program.before_script_id IS 'References the script of an enrollment that is executed before processing the received data (only when enrollment has not been created for the incoming data).';
COMMENT ON COLUMN fhir_tracker_program.after_script_id IS 'References the script of an enrollment that is executed after processing the received data.';
COMMENT ON COLUMN fhir_tracker_program.enrollment_date_is_incident IS 'Specifies if the enrollment date should be used as the incident date. Otherwise the enrollment date that is calculated for the input FHIR Resource is used.';
COMMENT ON COLUMN fhir_tracker_program.tracked_entity_rule_id IS 'References the tracked entity rule that is associated with the tracked entity that is handled by this program.';

CREATE TABLE fhir_tracker_program_stage (
  id                            UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version                       BIGINT                         NOT NULL,
  created_at                    TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by               VARCHAR(11),
  last_updated_at               TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  name                          VARCHAR(230)                   NOT NULL,
  description                   TEXT,
  program_id                    UUID                           NOT NULL,
  program_stage_ref             VARCHAR(230)                   NOT NULL,
  enabled                       BOOLEAN                        NOT NULL DEFAULT TRUE,
  creation_enabled              BOOLEAN                        NOT NULL DEFAULT FALSE,
  creation_applicable_script_id UUID,
  creation_script_id            UUID,
  creation_status               VARCHAR(20),
  before_script_id              UUID,
  after_script_id               UUID,
  event_date_is_incident        BOOLEAN                        NOT NULL DEFAULT FALSE,
  before_period_day_type        VARCHAR(20),
  before_period_days            INTEGER                        NOT NULL DEFAULT 0,
  after_period_day_type         VARCHAR(20),
  after_period_days             INTEGER                        NOT NULL DEFAULT 0,
  CONSTRAINT fhir_tracker_program_stage_pk PRIMARY KEY (id),
  CONSTRAINT fhir_tracker_program_stage_uk_name UNIQUE (name),
  CONSTRAINT fhir_tracker_program_stage_uk_program_stage UNIQUE (program_id, program_stage_ref),
  CONSTRAINT fhir_tracker_program_stage_fk1 FOREIGN KEY (program_id) REFERENCES fhir_tracker_program(id),
  CONSTRAINT fhir_tracker_program_stage_fk2 FOREIGN KEY (creation_applicable_script_id) REFERENCES fhir_executable_script(id),
  CONSTRAINT fhir_tracker_program_stage_fk3 FOREIGN KEY (creation_script_id) REFERENCES fhir_executable_script(id),
  CONSTRAINT fhir_tracker_program_stage_fk4 FOREIGN KEY (creation_status) REFERENCES fhir_event_status_enum(value),
  CONSTRAINT fhir_tracker_program_stage_fk5 FOREIGN KEY (before_period_day_type) REFERENCES fhir_event_period_day_type_enum(value),
  CONSTRAINT fhir_tracker_program_stage_fk6 FOREIGN KEY (after_period_day_type) REFERENCES fhir_event_period_day_type_enum(value),
  CONSTRAINT fhir_tracker_program_stage_fk7 FOREIGN KEY (before_script_id) REFERENCES fhir_executable_script(id),
  CONSTRAINT fhir_tracker_program_stage_fk8 FOREIGN KEY (after_script_id) REFERENCES fhir_executable_script(id)
);
CREATE INDEX fhir_tracker_program_stage_i1
  ON fhir_tracker_program_stage (program_id);
CREATE INDEX fhir_tracker_program_stage_i2
  ON fhir_tracker_program_stage (creation_applicable_script_id);
CREATE INDEX fhir_tracker_program_stage_i3
  ON fhir_tracker_program_stage (creation_script_id);
CREATE INDEX fhir_tracker_program_stage_i4
  ON fhir_tracker_program_stage (before_script_id);
CREATE INDEX fhir_tracker_program_stage_i5
  ON fhir_tracker_program_stage (after_script_id);
COMMENT ON TABLE fhir_tracker_program_stage IS 'Contains mappings of DHIS2 Tracker Program Stages.';
COMMENT ON COLUMN fhir_tracker_program_stage.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_tracker_program_stage.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_tracker_program_stage.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_tracker_program_stage.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_tracker_program_stage.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_tracker_program_stage.name IS 'The unique name of the tracker program stage (just used by the adapter).';
COMMENT ON COLUMN fhir_tracker_program_stage.description IS 'The detailed description about the purpose of the tracker program stage and its mapping in the adapter.';
COMMENT ON COLUMN fhir_tracker_program_stage.program_id IS 'References the tracker program to which this program stage belongs to.';
COMMENT ON COLUMN fhir_tracker_program_stage.program_stage_ref IS 'The reference of the DHIS2 Tracker Program Stage (e.g. "NAME:Baby Postnatal")';
COMMENT ON COLUMN fhir_tracker_program_stage.enabled IS 'Defines if this DHIS2 Tracker Program Stage has been enabled for processing. If this tracker program stage has not been enabled for processing, rules belonging to this tracker program stage will not be regarded as applicable.';
COMMENT ON COLUMN fhir_tracker_program_stage.creation_enabled IS 'Specifies if the creation of an event of this program stage can be made by the adapter.';
COMMENT ON COLUMN fhir_tracker_program_stage.creation_applicable_script_id IS 'References an evaluation script that evaluates if the creation is applicable. If the creation is not applicable, the execute rule is also not applicable for processing the input data. If no script has been specified, the creation is applicable.';
COMMENT ON COLUMN fhir_tracker_program_stage.creation_status IS 'The status of the created event. The status can also be set by the creation script.';
COMMENT ON COLUMN fhir_tracker_program_stage.event_date_is_incident IS 'Specifies if the incident date of the enrollment should be used for calculating the event date (based on minimum start days configured for the program stage in DHIS2). Otherwise the event date that is calculated for the input FHIR Resource is used.';
COMMENT ON COLUMN fhir_tracker_program_stage.before_period_day_type IS 'Specifies the type of date that is used to evaluate the maximum number of days that the effective date of the incoming input data can be before this day in order that the input data can still be regarded as applicable for processing of this program stage. These number of days can be overridden by a program stage rule.';
COMMENT ON COLUMN fhir_tracker_program_stage.before_period_days IS 'Specifies the maximum number of days that the effective date of the incoming input data can be before the specified period day in order that the input data can still be regarded as applicable for processing of this program stage. These number of days can be overridden by a program stage rule.';
COMMENT ON COLUMN fhir_tracker_program_stage.after_period_day_type IS 'Specifies the type of date that is used to evaluate the maximum number of days that the effective date of the incoming input data can be after this day in order that the input data can still be regarded as applicable for processing of this program stage. These number of days can be overridden by a program stage rule.';
COMMENT ON COLUMN fhir_tracker_program_stage.after_period_days IS 'Specifies the maximum number of days that the effective date of the incoming input data can be after the specified period day in order that the input data can still be regarded as applicable for processing of this program stage. These number of days can be overridden by a program stage rule.';
COMMENT ON COLUMN fhir_tracker_program_stage.creation_script_id IS 'References a script that initializes the event on creation.';
COMMENT ON COLUMN fhir_tracker_program_stage.before_script_id IS 'References the script of an event that is executed before processing the received data (only when the event has not been created for the incoming data). The script must return either CONTINUE (continue execution), NEW_EVENT (create a new event in case of repeatable program stages) or BREAK (the current rule is not appropriate to process the incoming data). If a repeatable program stage is processed, the script gets as input variable also "organisationUnitId" that contains the selected organisation unit.';
COMMENT ON COLUMN fhir_tracker_program_stage.after_script_id IS 'References the script of an event that is executed after processing the received data.';

CREATE TABLE fhir_program_stage_rule (
  id                              UUID         NOT NULL,
  program_stage_id                UUID         NOT NULL,
  update_event_date               BOOLEAN      NOT NULL DEFAULT FALSE,
  enrollment_creation_enabled     BOOLEAN      NOT NULL DEFAULT FALSE,
  event_creation_enabled          BOOLEAN      NOT NULL DEFAULT FALSE,
  before_period_day_type          VARCHAR(20),
  before_period_days              INTEGER,
  after_period_day_type           VARCHAR(20),
  after_period_days               INTEGER,
  enrollment_active_applicable    BOOLEAN      NOT NULL DEFAULT TRUE,
  enrollment_completed_applicable BOOLEAN      NOT NULL DEFAULT FALSE,
  enrollment_cancelled_applicable BOOLEAN      NOT NULL DEFAULT FALSE,
  overdue_applicable              BOOLEAN      NOT NULL DEFAULT TRUE,
  active_applicable               BOOLEAN      NOT NULL DEFAULT TRUE,
  schedule_applicable             BOOLEAN      NOT NULL DEFAULT TRUE,
  visited_applicable              BOOLEAN      NOT NULL DEFAULT TRUE,
  completed_applicable            BOOLEAN      NOT NULL DEFAULT FALSE,
  skipped_applicable              BOOLEAN      NOT NULL DEFAULT FALSE,
  overdue_to_active_update        BOOLEAN      NOT NULL DEFAULT FALSE,
  schedule_to_active_update       BOOLEAN      NOT NULL DEFAULT FALSE,
  completed_to_active_update      BOOLEAN      NOT NULL DEFAULT FALSE,
  CONSTRAINT fhir_program_stage_rule_pk PRIMARY KEY (id),
  CONSTRAINT fhir_program_stage_rule_fk1 FOREIGN KEY (id) REFERENCES fhir_rule (id) ON DELETE CASCADE,
  CONSTRAINT fhir_program_stage_rule_fk2 FOREIGN KEY (program_stage_id) REFERENCES fhir_tracker_program_stage (id) ON DELETE CASCADE,
  CONSTRAINT fhir_program_stage_rule_fk3 FOREIGN KEY (before_period_day_type) REFERENCES fhir_event_period_day_type_enum(value),
  CONSTRAINT fhir_program_stage_rule_fk4 FOREIGN KEY (after_period_day_type) REFERENCES fhir_event_period_day_type_enum(value)
);
CREATE INDEX fhir_program_stage_rule_i1
  ON fhir_program_stage_rule (program_stage_id);
COMMENT ON TABLE fhir_program_stage_rule IS 'Contains rules for DHIS2 Program Stage Resource Types.';
COMMENT ON COLUMN fhir_program_stage_rule.id IS 'References the rule to which this program stage rule belongs to.';
COMMENT ON COLUMN fhir_program_stage_rule.program_stage_id IS 'References the tracker program stage to which this program stage belongs to.';
COMMENT ON COLUMN fhir_program_stage_rule.update_event_date IS 'Specifies if the event date should be updated to the effective date of the input data when processing this rule.';
COMMENT ON COLUMN fhir_program_stage_rule.enrollment_creation_enabled IS 'Specifies if the creation of an enrollment of this program can be made by the adapter.';
COMMENT ON COLUMN fhir_program_stage_rule.event_creation_enabled IS 'Specifies if the creation of an event of this program stage can be made by the adapter.';
COMMENT ON COLUMN fhir_program_stage_rule.before_period_day_type IS 'Specifies the type of date that is used to evaluate the maximum number of days that the effective date of the incoming input data can be before this day in order that the input data can still be regarded as applicable for processing of this program stage. Otherwise the day type defined by the tracker program stage is used.';
COMMENT ON COLUMN fhir_program_stage_rule.before_period_days IS 'Specifies the maximum number of days that the effective date of the incoming input data can be before the specified period day in order that the input data can still be regarded as applicable for processing of this program stage. Otherwise the days defined by the tracker program stage is used.';
COMMENT ON COLUMN fhir_program_stage_rule.after_period_day_type IS 'Specifies the type of date that is used to evaluate the maximum number of days that the effective date of the incoming input data can be after this day in order that the input data can still be regarded as applicable for processing of this program stage. Otherwise the day type defined by the tracker program stage is used.';
COMMENT ON COLUMN fhir_program_stage_rule.after_period_days IS 'Specifies the maximum number of days that the effective date of the incoming input data can be after the specified period day in order that the input data can still be regarded as applicable for processing of this program stage. Otherwise the days defined by the tracker program stage is used.';
COMMENT ON COLUMN fhir_program_stage_rule.enrollment_active_applicable IS 'Specifies if the rule can be processed if the enrollment is active.';
COMMENT ON COLUMN fhir_program_stage_rule.enrollment_completed_applicable IS 'Specifies if the rule can be processed if the enrollment is completed.';
COMMENT ON COLUMN fhir_program_stage_rule.enrollment_cancelled_applicable IS 'Specifies if the rule can be processed if the enrollment is cancelled.';
COMMENT ON COLUMN fhir_program_stage_rule.overdue_applicable IS 'Specifies if the rule can be processed if the event is in status overdue.';
COMMENT ON COLUMN fhir_program_stage_rule.active_applicable IS 'Specifies if the rule can be processed if the event is in status active.';
COMMENT ON COLUMN fhir_program_stage_rule.schedule_applicable IS 'Specifies if the rule can be processed if the event is in status schedule.';
COMMENT ON COLUMN fhir_program_stage_rule.visited_applicable IS 'Specifies if the rule can be processed if the event is in status visited.';
COMMENT ON COLUMN fhir_program_stage_rule.completed_applicable IS 'Specifies if the rule can be processed if the event is in status completed.';
COMMENT ON COLUMN fhir_program_stage_rule.skipped_applicable IS 'Specifies if the rule can be processed if the event is in status skipped.';
COMMENT ON COLUMN fhir_program_stage_rule.overdue_to_active_update IS 'Specifies if the event should be updated from overdue to active when processing the rule.';
COMMENT ON COLUMN fhir_program_stage_rule.schedule_to_active_update IS 'Specifies if the event should be updated from schedule to active when processing the rule.';
COMMENT ON COLUMN fhir_program_stage_rule.completed_to_active_update IS 'Specifies if the event should be updated from completed to active when processing the rule.';

CREATE TABLE fhir_resource_mapping (
  id                                UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version                           BIGINT                         NOT NULL,
  fhir_resource_type                VARCHAR(30)                    NOT NULL,
  created_at                        TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by                   VARCHAR(11),
  last_updated_at                   TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  tei_lookup_script_id              UUID,
  enrollment_org_lookup_script_id   UUID,
  event_org_lookup_script_id        UUID,
  enrollment_date_lookup_script_id  UUID,
  event_date_lookup_script_id       UUID,
  enrollment_loc_lookup_script_id   UUID,
  event_loc_lookup_script_id        UUID,
  effective_date_lookup_script_id   UUID,
  CONSTRAINT fhir_resource_mapping_pk PRIMARY KEY (id),
  CONSTRAINT fhir_resource_mapping_uk_fhir UNIQUE (fhir_resource_type),
  CONSTRAINT fhir_resource_mapping_fk1 FOREIGN KEY (tei_lookup_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_resource_mapping_fk2 FOREIGN KEY (enrollment_org_lookup_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_resource_mapping_fk3 FOREIGN KEY (event_org_lookup_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_resource_mapping_fk4 FOREIGN KEY (enrollment_date_lookup_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_resource_mapping_fk5 FOREIGN KEY (event_date_lookup_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_resource_mapping_fk6 FOREIGN KEY (enrollment_loc_lookup_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_resource_mapping_fk7 FOREIGN KEY (event_loc_lookup_script_id) REFERENCES fhir_executable_script (id),
  CONSTRAINT fhir_resource_mapping_fk8 FOREIGN KEY (effective_date_lookup_script_id) REFERENCES fhir_executable_script (id)
);
CREATE INDEX fhir_resource_mapping_i1
  ON fhir_resource_mapping (tei_lookup_script_id);
CREATE INDEX fhir_resource_mapping_i2
  ON fhir_resource_mapping (enrollment_org_lookup_script_id);
CREATE INDEX fhir_resource_mapping_i3
  ON fhir_resource_mapping (event_org_lookup_script_id);
CREATE INDEX fhir_resource_mapping_i4
  ON fhir_resource_mapping (enrollment_date_lookup_script_id);
CREATE INDEX fhir_resource_mapping_i5
  ON fhir_resource_mapping (event_date_lookup_script_id);
CREATE INDEX fhir_resource_mapping_i6
  ON fhir_resource_mapping (enrollment_loc_lookup_script_id);
CREATE INDEX fhir_resource_mapping_i7
  ON fhir_resource_mapping (event_loc_lookup_script_id);
CREATE INDEX fhir_resource_mapping_i8
  ON fhir_resource_mapping (effective_date_lookup_script_id);
COMMENT ON TABLE fhir_resource_mapping IS 'Contains mappings of DHIS2 Tracker Program Stages.';
COMMENT ON COLUMN fhir_resource_mapping.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_resource_mapping.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_resource_mapping.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_resource_mapping.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_resource_mapping.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_resource_mapping.fhir_resource_type IS 'The FHIR Resource Type for which this mapping has been specified. For each FHIR Resource Type only one mapping can be created.';
COMMENT ON COLUMN fhir_resource_mapping.tei_lookup_script_id IS 'References the evaluation script that looks up the DHIS2 Tracked Entity Instance from the processed input data.';
COMMENT ON COLUMN fhir_resource_mapping.enrollment_org_lookup_script_id IS 'References the evaluation script that looks up the DHIS2 Organisation Unit from the processed input data when an enrollment into a DHIS2 Tracker Program should be made.';
COMMENT ON COLUMN fhir_resource_mapping.event_org_lookup_script_id IS 'References the evaluation script that looks up the DHIS2 Organisation Unit from the processed input data when an event of a DHIS2 Tracker Program Stage should be created.';
COMMENT ON COLUMN fhir_resource_mapping.enrollment_loc_lookup_script_id IS 'References the evaluation script that looks up the coordinates (longitude/latitude) from the processed input data when an enrollment into a DHIS2 Tracker Program should be made.';
COMMENT ON COLUMN fhir_resource_mapping.event_loc_lookup_script_id IS 'References the evaluation script that looks up the coordinates (longitude/latitude) from the processed input data when an event of a DHIS2 Tracker Program Stage should be created.';
COMMENT ON COLUMN fhir_resource_mapping.enrollment_date_lookup_script_id IS 'References the evaluation script that looks up the enrollment date from the processed input data when an enrollment into a DHIS2 Tracker Program should be made.';
COMMENT ON COLUMN fhir_resource_mapping.event_date_lookup_script_id IS 'References the evaluation script that looks up the event date from the processed input data when an event of a DHIS2 Tracker Program Stage should be created.';
COMMENT ON COLUMN fhir_resource_mapping.effective_date_lookup_script_id IS 'References the evaluation script that looks up the effective date from the processed input data. The effective date defines the day when the values of the input data were effective. This may be after the day when the values have been recorded.';

CREATE TABLE fhir_remote_subscription (
  id                            UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version                       BIGINT                         NOT NULL,
  created_at                    TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by               VARCHAR(11),
  last_updated_at               TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  name                          VARCHAR(50)                    NOT NULL,
  code                          VARCHAR(20)                    NOT NULL,
  enabled                       BOOLEAN                        NOT NULL DEFAULT TRUE,
  locked                        BOOLEAN                        NOT NULL DEFAULT FALSE,
  description                   TEXT,
  fhir_version                  VARCHAR(30)                    NOT NULL,
  adapter_base_url              VARCHAR(200)                   NOT NULL,
  web_hook_authorization_header VARCHAR(200)                   NOT NULL,
  dhis_authentication_method    VARCHAR(30)                    NOT NULL,
  dhis_username                 VARCHAR(200)                   NOT NULL,
  dhis_password                 VARCHAR(200)                   NOT NULL,
  remote_base_url               VARCHAR(200)                   NOT NULL,
  tolerance_millis              INTEGER                        NOT NULL DEFAULT 0,
  logging                       BOOLEAN                        NOT NULL DEFAULT FALSE,
  verbose_logging               BOOLEAN                        NOT NULL DEFAULT FALSE,
  subscription_type             VARCHAR(30)                    NOT NULL,
  auto_configuration            BOOLEAN                        NOT NULL DEFAULT FALSE,
  CONSTRAINT fhir_remote_subscription_pk PRIMARY KEY (id),
  CONSTRAINT fhir_remote_subscription_uk_name UNIQUE (name),
  CONSTRAINT fhir_remote_subscription_uk_code UNIQUE (code),
  CONSTRAINT fhir_remote_subscription_fk1 FOREIGN KEY (fhir_version) REFERENCES fhir_version_enum(value),
  CONSTRAINT fhir_remote_subscription_fk2 FOREIGN KEY (dhis_authentication_method) REFERENCES fhir_authentication_method_enum(value),
  CONSTRAINT fhir_remote_subscription_fk3 FOREIGN KEY (subscription_type) REFERENCES fhir_subscription_type_enum(value)
);
COMMENT ON TABLE fhir_remote_subscription IS 'Contains FHIR Services on which the adapter has one or more FHIR Subscriptions.';
COMMENT ON COLUMN fhir_remote_subscription.id IS 'Unique ID of entity (also used as first part of web hook URI).';
COMMENT ON COLUMN fhir_remote_subscription.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_remote_subscription.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_remote_subscription.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_remote_subscription.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_remote_subscription.name IS 'The unique name of the FHIR Service on which this adapter has FHIR Subscriptions.';
COMMENT ON COLUMN fhir_remote_subscription.code IS 'The unique code of the FHIR Service on which this adapter has FHIR Subscriptions.';
COMMENT ON COLUMN fhir_remote_subscription.enabled IS 'Specifies if the subscriptions of this remote FHIR Service are enabled for processing.';
COMMENT ON COLUMN fhir_remote_subscription.locked IS 'Specifies if this remote FHIR Service has been locked and subscription web hook requests cannot be processed currently.';
COMMENT ON COLUMN fhir_remote_subscription.description IS 'The detailed description about the purpose of the subscriptions on this FHIR Service.';
COMMENT ON COLUMN fhir_remote_subscription.fhir_version IS 'The FHIR version that is used by the FHIR Service.';
COMMENT ON COLUMN fhir_remote_subscription.web_hook_authorization_header IS 'The authorization header value that is expected in a web hook request from the FHIR Service. If the value differs the request is rejected.';
COMMENT ON COLUMN fhir_remote_subscription.dhis_authentication_method IS 'The authentication method of the DHIS2 user that is used to create, read and update data on DHIS2 when processing data of this FHIR Service.';
COMMENT ON COLUMN fhir_remote_subscription.dhis_username IS 'The username of the DHIS2 user that is used to create, read and update data on DHIS2 when processing data of this FHIR Service.';
COMMENT ON COLUMN fhir_remote_subscription.dhis_password IS 'The password of the DHIS2 user that is used to create, read and update data on DHIS2 when processing data of this FHIR Service. ';
COMMENT ON COLUMN fhir_remote_subscription.remote_base_url IS 'The base URL of the remote FHIR Service that provides access to the FHIR Endpoints.';
COMMENT ON COLUMN fhir_remote_subscription.tolerance_millis IS 'The number of millis that is subtracted from the remote last updated timestamp when fetching the next data of a subscribed resource. This is useful in order to handle non-synchronized clocks on servers.';
COMMENT ON COLUMN fhir_remote_subscription.logging IS 'Specifies if the FHIR client should log accesses to the remote FHIR Service. Logging personal related health data to a log file may be a legal issue.';
COMMENT ON COLUMN fhir_remote_subscription.verbose_logging IS 'Specifies if the FHIR client should do verbose logging (all details) when accessing to the remote FHIR Service. Logging personal related health data to a log file may be a legal issue.';
COMMENT ON COLUMN fhir_remote_subscription.adapter_base_url IS 'The base URL of all endpoints of the adapter. This is used when creating subscriptions for calculating the web hook endpoints.';
COMMENT ON COLUMN fhir_remote_subscription.subscription_type IS 'The type of the subscription that has been configured on the remote FHIR service.';
COMMENT ON COLUMN fhir_remote_subscription.auto_configuration IS 'Specifies if the subscription registration has been done automatically (not manually).';

CREATE TABLE fhir_remote_subscription_header (
  remote_subscription_id UUID         NOT NULL,
  name                   VARCHAR(50)  NOT NULL,
  value                  VARCHAR(200) NOT NULL,
  secure                 BOOLEAN      NOT NULL DEFAULT FALSE,
  CONSTRAINT fhir_remote_subscription_header_pk PRIMARY KEY (remote_subscription_id, name, value),
  CONSTRAINT fhir_remote_subscription_header_fk1 FOREIGN KEY (remote_subscription_id) REFERENCES fhir_remote_subscription (id) ON DELETE CASCADE
);
COMMENT ON TABLE fhir_remote_subscription_header IS 'Contains the header values that are sent to the remote FHIR Service when accessing its FHIR Endpoints.';
COMMENT ON COLUMN fhir_remote_subscription_header.remote_subscription_id IS 'References the remote subscription to which this header belongs to.';
COMMENT ON COLUMN fhir_remote_subscription_header.name IS 'The name of the header.';
COMMENT ON COLUMN fhir_remote_subscription_header.value IS 'The value of the header.';
COMMENT ON COLUMN fhir_remote_subscription_header.secure IS 'Specifies if the value contains secure content (e.g. authentication data) that must be protected.';

CREATE TABLE fhir_remote_subscription_system (
  id                            UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version                       BIGINT                         NOT NULL,
  created_at                    TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by               VARCHAR(11),
  last_updated_at               TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  remote_subscription_id        UUID                           NOT NULL,
  fhir_resource_type            VARCHAR(30)                    NOT NULL,
  system_id                     UUID                           NOT NULL,
  code_prefix                   VARCHAR(20),
  CONSTRAINT fhir_remote_subscription_system_pk PRIMARY KEY (id),
  CONSTRAINT fhir_remote_subscription_system_uk_fhir UNIQUE (remote_subscription_id , fhir_resource_type ),
  CONSTRAINT fhir_remote_subscription_system_fk1 FOREIGN KEY (remote_subscription_id) REFERENCES fhir_remote_subscription (id) ON DELETE CASCADE,
  CONSTRAINT fhir_remote_subscription_system_fk2 FOREIGN KEY (system_id) REFERENCES fhir_system (id),
  CONSTRAINT fhir_remote_subscription_system_fk3 FOREIGN KEY (fhir_resource_type) REFERENCES fhir_resource_type_enum (value)
);
CREATE INDEX fhir_remote_subscription_system_i1 ON fhir_remote_subscription_system(system_id);
COMMENT ON TABLE fhir_remote_subscription_system IS 'Contains the system URIs that are used by the remote FHIR Service for specific FHIR Resources (e.g. system URI for national patient identifier).';
COMMENT ON COLUMN fhir_remote_subscription_system.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_remote_subscription_system.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_remote_subscription_system.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_remote_subscription_system.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_remote_subscription_system.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_remote_subscription_system.remote_subscription_id IS 'References the remote subscription to which this system URI belongs to.';
COMMENT ON COLUMN fhir_remote_subscription_system.fhir_resource_type IS 'The FHIR Resource Type for which this system URI is used.';
COMMENT ON COLUMN fhir_remote_subscription_system.system_id IS 'References the system of which the system URI should be used.';
COMMENT ON COLUMN fhir_remote_subscription_system.code_prefix IS 'Prefix that is used to prefix the code when using this system.';

CREATE TABLE fhir_remote_subscription_resource (
  id                       UUID                           NOT NULL DEFAULT UUID_GENERATE_V4(),
  version                  BIGINT                         NOT NULL,
  created_at               TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  last_updated_by          VARCHAR(11),
  last_updated_at          TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  remote_subscription_id   UUID                           NOT NULL,
  fhir_resource_type       VARCHAR(30)                    NOT NULL,
  fhir_criteria_parameters VARCHAR(200),
  description              TEXT,
  fhir_subscription_id     VARCHAR(100),
  CONSTRAINT fhir_remote_subscription_resource_pk PRIMARY KEY (id),
  -- do not enable cascading dequeued since remote last update date may get lost by mistake
  CONSTRAINT fhir_remote_subscription_resource_fk1 FOREIGN KEY (remote_subscription_id) REFERENCES fhir_remote_subscription (id),
  CONSTRAINT fhir_remote_subscription_resource_fk2 FOREIGN KEY (fhir_resource_type) REFERENCES fhir_resource_type_enum(value)
);
CREATE INDEX fhir_remote_subscription_resource_i1
  ON fhir_remote_subscription_resource (remote_subscription_id);
COMMENT ON TABLE fhir_remote_subscription_resource IS 'Contains the subscription that has been configured on the remote FHIR Service for a specific resource with a specific filter.';
COMMENT ON COLUMN fhir_remote_subscription_resource.id IS 'Unique ID of entity.';
COMMENT ON COLUMN fhir_remote_subscription_resource.version IS 'The version of the entity used for optimistic locking. When changing the entity this value must be incremented.';
COMMENT ON COLUMN fhir_remote_subscription_resource.created_at IS 'The timestamp when the entity has been created.';
COMMENT ON COLUMN fhir_remote_subscription_resource.last_updated_by IS 'The ID of the user that has updated the entity the last time or NULL if the user is not known or the entity has been created with initial database setup.';
COMMENT ON COLUMN fhir_remote_subscription_resource.last_updated_at IS 'The timestamp when the entity has been updated the last time. When changing the entity this value must be updated to the current timestamp.';
COMMENT ON COLUMN fhir_remote_subscription_resource.remote_subscription_id IS 'References the remote subscription to which this subscribed resource belongs to.';
COMMENT ON COLUMN fhir_remote_subscription_resource.fhir_resource_type IS 'The FHIR Resource Type that has been described by this resource subscription.';
COMMENT ON COLUMN fhir_remote_subscription_resource.fhir_criteria_parameters IS 'The parameters that have been appended to the resource type when creation the subscription on the remote FHIR Service.';
COMMENT ON COLUMN fhir_remote_subscription_resource.description IS 'The detailed purpose of subscribing this resource with the specified criteria parameters.';
COMMENT ON COLUMN fhir_remote_subscription_resource.fhir_subscription_id IS 'The ID of the subscription that has been set up on the remote FHIR service or unset if no subscription has been set up automatically.';

CREATE TABLE fhir_remote_subscription_resource_update (
  id                       UUID                           NOT NULL,
  remote_last_updated      TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_remote_subscription_resource_update_pk PRIMARY KEY (id),
  CONSTRAINT fhir_remote_subscription_resource_update_fk1 FOREIGN KEY (id) REFERENCES fhir_remote_subscription_resource (id) ON DELETE CASCADE
);
COMMENT ON TABLE fhir_remote_subscription_resource_update IS 'Contains the timestamp that includes when the remote FHIR resources have last been updated from the FHIR Service.';
COMMENT ON COLUMN fhir_remote_subscription_resource_update.remote_last_updated IS 'The timestamp of the last begin of fetching data from the remote FHIR Service for the subscribed resource.';

CREATE TABLE fhir_queued_remote_subscription_request (
  id         UUID                           NOT NULL,
  request_id VARCHAR(50)                    NOT NULL,
  queued_at  TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_queued_remote_subscription_request_pk PRIMARY KEY(id),
  CONSTRAINT fhir_queued_remote_subscription_request_fk1 FOREIGN KEY(id) REFERENCES fhir_remote_subscription_resource(id) ON DELETE CASCADE
);
COMMENT ON TABLE fhir_queued_remote_subscription_request IS 'Contains queued remote subscription requests.';
COMMENT ON COLUMN fhir_queued_remote_subscription_request.id IS 'References the remote subscription resource request.';
COMMENT ON COLUMN fhir_queued_remote_subscription_request.request_id IS 'ID of the web hook request on which this request has been added to the queue.';
COMMENT ON COLUMN fhir_queued_remote_subscription_request.queued_at IS 'The timestamp when the data has been queued last time.';

CREATE TABLE fhir_processed_remote_resource (
  remote_subscription_resource_id   UUID         NOT NULL,
  versioned_fhir_resource_id        VARCHAR(120) NOT NULL,
  processed_at                      TIMESTAMP(3) NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_processed_remote_resource_pk PRIMARY KEY (remote_subscription_resource_id, versioned_fhir_resource_id),
  CONSTRAINT fhir_processed_remote_resource_fk1 FOREIGN KEY(remote_subscription_resource_id) REFERENCES fhir_remote_subscription_resource(id) ON DELETE CASCADE
);
CREATE INDEX fhir_processed_remote_resource_i1
  ON fhir_processed_remote_resource(remote_subscription_resource_id, processed_at);
COMMENT ON TABLE fhir_processed_remote_resource IS 'Contains the versioned FHIR resource IDs that have been processed in the last few hours.';
COMMENT ON COLUMN fhir_processed_remote_resource.remote_subscription_resource_id IS 'References the remote subscription resource to which the subscription belongs to.';
COMMENT ON COLUMN fhir_processed_remote_resource.versioned_fhir_resource_id IS 'The unique string that identifies a distinct version of a remote FHIR resource.';
COMMENT ON COLUMN fhir_processed_remote_resource.processed_at IS 'Timestamp when the resource has been processed. Used for deleting the data after some hours mainly.';

CREATE TABLE fhir_queued_remote_resource (
  remote_subscription_resource_id   UUID                           NOT NULL,
  fhir_resource_id                  VARCHAR(80)                    NOT NULL,
  request_id                        VARCHAR(50)                    NOT NULL,
  queued_at                         TIMESTAMP(3) WITHOUT TIME ZONE NOT NULL DEFAULT (CURRENT_TIMESTAMP AT TIME ZONE 'UTC'),
  CONSTRAINT fhir_queued_remote_resource_pk PRIMARY KEY(remote_subscription_resource_id, fhir_resource_id ),
  CONSTRAINT fhir_queued_remote_resource_fk1 FOREIGN KEY(remote_subscription_resource_id) REFERENCES fhir_remote_subscription_resource(id) ON DELETE CASCADE
);
COMMENT ON TABLE fhir_queued_remote_resource IS 'Contains queued remote FHIR resources that should be processed.';
COMMENT ON COLUMN fhir_queued_remote_resource.remote_subscription_resource_id IS 'References the remote subscription resource to which the subscription belongs to.';
COMMENT ON COLUMN fhir_queued_remote_resource.fhir_resource_id IS 'The ID (only the ID part without resource name and version) of the FHIR Resource that has been queued.';
COMMENT ON COLUMN fhir_queued_remote_resource.queued_at IS 'The timestamp when the data has been queued last time.';
COMMENT ON COLUMN fhir_queued_remote_resource.request_id IS 'ID of the enqueue request on which this resource has been added to the queue.';

-- Gender Constants (Adapter Gender Code to DHIS2 code as value)
INSERT INTO fhir_constant (id, version, category, name, code, data_type, value)
VALUES ('fa4a3a0e-ca46-40e4-b832-3aec96bed55e', 0, 'GENDER', 'Gender Female', 'GENDER_FEMALE', 'STRING', 'Female');
INSERT INTO fhir_constant (id, version, category, name, code, data_type, value)
VALUES ('1ded2081-8836-43dd-a5e1-7cb9562c93ef', 0, 'GENDER', 'Gender Male', 'GENDER_MALE', 'STRING', 'Male');

-- Systems (the value is the systemAuthentication URI)
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('2601edcb-f7bc-4710-ab64-0f4edd9a2378', 0, 'CVX (Vaccine Administered)', 'SYSTEM_CVX', 'http://hl7.org/fhir/sid/cvx',
'Available at http://www2a.cdc.gov/vaccines/iis/iisstandards/vaccines.asp?rpt=cvx. Developed by The CDC''s National Center of Immunization and Respiratory Diseases (NCIRD).');
-- Attention: Copyright notice according to http://www.hl7.org/fhir/loinc.html
INSERT INTO fhir_system (id, version, name, code, system_uri, description_protected, description)
VALUES ('f6e720a2-e9ff-43a8-a2fd-d5636106297b', 0, 'LOINC', 'SYSTEM_LOINC', 'http://loinc.org', TRUE,
'Available at http://loinc.org/, the terms of use apply to all the codes and filterings/mappings (i.e. rules and transformations) that are associated with this system. This copyright notice must neither be changed nor removed.
This content LOINC® is copyright © 1995 Regenstrief Institute, Inc. and the LOINC Committee, and available at no cost under the license at http://loinc.org/terms-of-use.');
-- Attention: Please refer to the license before using this system. See http://www.hl7.org/fhir/snomedct.html for further information.
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('20f6d869-a767-461e-8c68-aa46a76ec5c4', 0, 'SNOMED CT', 'SYSTEM_SCT', 'http://snomed.info/sct',
'SNOMED CT can be used to represent clinically relevant information consistently, reliably and comprehensively as an integral part of producing electronic health information.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('02d5719e-8859-445a-a815-5980d418b4e6', 0, 'RxNorm', 'SYSTEM_RXNORM', ' http://www.nlm.nih.gov/research/umls/rxnorm',
'RxNorm is made available by the US National Library of Medicine at http://www.nlm.nih.gov/research/umls/rxnorm.');

INSERT INTO fhir_code_category (id, version, name, code, description)
VALUES ('9e1c64d8-82df-40e5-927b-a34c115f14e9', 0, 'Organization Unit', 'ORGANIZATION_UNIT', 'Organization units that exists on DHIS2.');
INSERT INTO fhir_code_category (id, version, name, code, description)
VALUES ('1197b27e-3956-43dd-a75c-bfc6808dc49d', 0, 'Vital Sign', 'VITAL_SIGN', 'Vital signs.');
INSERT INTO fhir_code_category (id, version, name, code, description)
VALUES ('616e470e-fe84-46ac-a523-b23bbc526ae2', 0, 'Survey', 'SURVEY', 'Survey.');

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- BEGIN LOINC Code Descriptions
-- The following copyright applies to the following section that contains description of LOINC codes:
-- This content LOINC® is copyright © 1995 Regenstrief Institute, Inc. and the LOINC Committee, and available at no cost under the license at http://loinc.org/terms-of-use.
--
-- All descriptions must include: LOINC_NUM, COMPONENT, PROPERTY, TIME_ASPCT, SYSTEM, SCALE_TYP, METHOD_TYP, STATUS and SHORTNAME.
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Definition of vital signs - body weight
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('0d78f5e3-c9fd-4859-9768-fb7c898a4142', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Weight Measured', 'LOINC_3141-9', '3141-9 / Body weight / Mass / Pt / ^Patient / Qn / Measured / ACTIVE / Weight Measured / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('a748a35e-00e6-4926-abc6-8cde3bb30ee4', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Weight', 'LOINC_29463-7', '29463-7 / Body weight / Mass / Pt / ^Patient / Qn /  / ACTIVE / Weight / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('26bb2fe1-8c80-4f01-bfa2-ccca24ffabf7', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Weight usual Reported', 'LOINC_75292-3', '75292-3 / Body weight^usual / Mass / Pt / ^Patient / Qn / Reported / ACTIVE / Weight usual Reported / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('34ab9dc2-4e28-4e2d-8dcb-99c78d24b0fa', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Weight W clothes Measured', 'LOINC_8350-1', '8350-1 / Body weight^with clothes / Mass / Pt / ^Patient / Qn / Measured / ACTIVE / Weight W clothes Measured / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('4b3d1fb0-2a66-4da4-a8e5-4e3ddcf8cf8c', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Weight WO clothes Measured', 'LOINC_8351-9', '8351-9 / Body weight^without clothes / Mass / Pt / ^Patient / Qn / Measured / ACTIVE / Weight WO clothes Measured / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('c03c0878-1896-4d05-8a6c-593892bdd5ba', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Weight Est', 'LOINC_8335-2', '8335-2 / Body weight / Mass / Pt / ^Patient / Qn / Estimated / ACTIVE / Weight Est / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('0139aaee-c988-4ba8-838c-bc26331a9c96', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Weight Stated', 'LOINC_3142-7', '3142-7 / Body weight / Mass / Pt / ^Patient / Qn / Stated / ACTIVE / Weight Stated / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('124b0bb5-d7cc-483b-a555-356fd3651b4f', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Birth weight GNWCH', 'LOINC_56092-0', '56092-0 / Body weight^at birth / Mass / Pt / ^Patient / Qn / GNWCH / ACTIVE / Birth weight GNWCH / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('bcdc2c18-30ee-4860-9b79-2f810cbcb0f1', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Birth weight Measured', 'LOINC_8339-4', '8339-4 / Body weight^at birth / Mass / Pt / ^Patient / Qn / Measured / ACTIVE / Birth weight Measured / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('160ab849-ec01-42d3-81dc-056f28faf14d', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Birth weight NVSS', 'LOINC_56093-8', '56093-8 / Body weight^at birth / Mass / Pt / ^Patient / Qn / NVSS / ACTIVE / Birth weight NVSS / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('644b6a02-160d-4021-b379-dcd8d8044684', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Birth weight Reported', 'LOINC_56056-5', '56056-5 / Body weight^at birth / Mass / Pt / ^Patient / Qn / Reported / ACTIVE / Birth weight Reported / LOINC®');

-- Definition of survey - apgar score
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('5662bc32-974e-4d18-bddc-b10f510439e6', 0, '616e470e-fe84-46ac-a523-b23bbc526ae2', '1M Apgar Score', 'LOINC_9272-6', '9272-6 / Score^1M post birth / Fcn / Pt / ^Patient / Qn / Apgar / ACTIVE / 1M Apgar Score / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('41578332-0130-486a-bd61-82e85e521a79', 0, '616e470e-fe84-46ac-a523-b23bbc526ae2', 'Apgar Scor', 'LOINC_9273-4', '9273-4 / Score^2M post birth / Fcn / Pt / ^Patient / Qn / Apgar / ACTIVE / Apgar Score / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('7b87fde7-7a3a-4124-aa63-06dbe26551b4', 0, '616e470e-fe84-46ac-a523-b23bbc526ae2', '5M Apgar Score', 'LOINC_9274-2', '9274-2 / Score^5M post birth / Fcn / Pt / ^Patient / Qn / Apgar / ACTIVE / 5M Apgar Score / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('9e64ae16-17c5-4fb0-97aa-eaca42c70c12', 0, '616e470e-fe84-46ac-a523-b23bbc526ae2', '10M Apgar Score', 'LOINC_9271-8', '9271-8 / Score^10M post birth / Fcn / Pt / ^Patient / Qn / Apgar / ACTIVE / 10M Apgar Score / LOINC®');

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- END LOINC Code Descriptions
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Definition of vaccines
INSERT INTO fhir_code_category (id, version, name, code, description)
VALUES ('7090561e-f45b-411e-99c0-65fa1d145018', 0, 'Vaccine', 'VACCINE', 'Available vaccines.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('5d40c358-0111-4472-b3fb-350fdd060b23', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'adenovirus, type 4', 'VACCINE_54', 'adenovirus vaccine, type 4, live, oral');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('8e170216-deda-47a2-985b-7d2fcf5e40d0', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'adenovirus, type 7', 'VACCINE_55', 'adenovirus vaccine, type 7, live, oral');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('a6f3cccc-8f2c-49e7-b451-80c79ccfc83a', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'adenovirus, unspecified formulation', 'VACCINE_82', 'adenovirus vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('d0d0b13b-9870-4490-8c6c-c159a698e165', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'anthrax', 'VACCINE_24', 'anthrax vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('c00aaec0-b02d-480e-9fd6-58578e224e1d', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'BCG', 'VACCINE_19', 'Bacillus Calmette-Guerin vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('531826ce-036a-4646-91a6-4fef313d004f', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'botulinum antitoxin', 'VACCINE_27', 'botulinum antitoxin');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('e8737467-46d5-47ed-b07f-8a8e324e7662', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'cholera, unspecified formulation', 'VACCINE_26', 'cholera vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('a2966812-3dae-4715-b4c3-dc97783b86d4', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'CMVIG', 'VACCINE_29', 'cytomegalovirus immune globulin, intravenous');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('57c4a571-3125-4c9c-ae85-0bec62cf0179', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'dengue fever', 'VACCINE_56', 'dengue fever vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('7244c0c6-1687-4364-b10f-fe49297924d4', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'diphtheria antitoxin', 'VACCINE_12', 'diphtheria antitoxin');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('5f89a6a1-5d84-4d9c-a753-7dfa5d7c1298', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'DT (pediatric)', 'VACCINE_28', 'diphtheria and tetanus toxoids, adsorbed for pediatric use');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f9462e8c-653b-4c6a-a502-8470a1ab2187', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'DTaP', 'VACCINE_20', 'diphtheria, tetanus toxoids and acellular pertussis vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('02422ddd-b606-4bb6-8d0f-cca090182b5d', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'DTaP, 5 pertussis antigens', 'VACCINE_106', 'diphtheria, tetanus toxoids and acellular pertussis vaccine, 5 pertussis antigens');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('e5b53f11-8926-42fe-9e47-f0e6d354f1fc', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'DTaP, unspecified formulation', 'VACCINE_107', 'diphtheria, tetanus toxoids and acellular pertussis vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('358c9a4b-a0fe-46f2-b407-92571a70da07', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'DTaP-Hep B-IPV', 'VACCINE_110', 'DTaP-hepatitis B and poliovirus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('32ad08cc-8f31-47dc-aafb-2ecb7dffbdfd', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'DTaP-Hib', 'VACCINE_50', 'DTaP-Haemophilus influenzae type b conjugate vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('2dadecf0-bc85-4467-8270-5453550d54df', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'DTaP-Hib-IPV', 'VACCINE_120', 'diphtheria, tetanus toxoids and acellular pertussis vaccine, Haemophilus influenzae type b conjugate, and poliovirus vaccine, inactivated (DTaP-Hib-IPV)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('11f6fec4-8efc-47fe-8a0b-2f82016fcff2', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'DTaP-IPV', 'VACCINE_130', 'Diphtheria, tetanus toxoids and acellular pertussis vaccine, and poliovirus vaccine, inactivated');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('7c8465f1-f45a-49f3-b183-f90ffe20c79b', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'DTP', 'VACCINE_01', 'diphtheria, tetanus toxoids and pertussis vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('d0d399ad-0fe6-4e6c-bc3d-8d06d9cb9db7', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'DTP-Hib', 'VACCINE_22', 'DTP-Haemophilus influenzae type b conjugate vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('9bf370f8-a318-4dab-b0dc-4c4c5d9a199e', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'DTP-Hib-Hep B', 'VACCINE_102', 'DTP- Haemophilus influenzae type b conjugate and hepatitis b vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('e259f1a1-b571-41e8-a761-92545e234fa6', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'hantavirus', 'VACCINE_57', 'hantavirus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('7c8917ef-8e09-49a2-bd80-e5a6f1b5ab6c', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep A, adult', 'VACCINE_52', 'hepatitis A vaccine, adult dosage');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('8ed3cb39-d123-40e6-a265-7231c929a2a7', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep A, ped/adol, 2 dose', 'VACCINE_83', 'hepatitis A vaccine, pediatric/adolescent dosage, 2 dose schedule');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('8eb0435a-faf3-43e8-ace1-85ac898d4b53', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep A, ped/adol, 3 dose', 'VACCINE_84', 'hepatitis A vaccine, pediatric/adolescent dosage, 3 dose schedule');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('e6a6202d-31a3-4a59-b736-3c22cf09adc7', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep A, pediatric, unspecified formulation', 'VACCINE_31', 'hepatitis A vaccine, pediatric dosage, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('461cfc19-aaa1-40e2-a2b2-462155cd6e20', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep A, unspecified formulation', 'VACCINE_85', 'hepatitis A vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('fb90a9d6-7565-42ca-a578-34f2de10b5bf', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep A-Hep B', 'VACCINE_104', 'hepatitis A and hepatitis B vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('84f05f76-fe87-4156-acb5-a6f30343d539', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'HBIG', 'VACCINE_30', 'hepatitis B immune globulin');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f69331e5-b12a-4595-9c4a-17275bd7a709', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep B, adolescent or pediatric', 'VACCINE_08', 'hepatitis B vaccine, pediatric or pediatric/adolescent dosage');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('3f3232cf-2db8-41a1-9f60-1213515d4100', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep B, adolescent/high risk infant', 'VACCINE_42', 'hepatitis B vaccine, adolescent/high risk infant dosage');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('85cb6ed9-3c84-431b-a631-bc78797bc7ed', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep B, adult', 'VACCINE_43', 'hepatitis B vaccine, adult dosage');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('e5d8c7b5-c69d-416e-b428-359b4ccb43e8', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep B, dialysis', 'VACCINE_44', 'hepatitis B vaccine, dialysis patient dosage');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('4de7737c-841e-4a86-8bf0-ab6e8a007120', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep B, unspecified formulation', 'VACCINE_45', 'hepatitis B vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('d133d6ce-3b4c-4838-9acf-3c9d22c9dd13', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep C', 'VACCINE_58', 'hepatitis C vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('bf27d89b-f678-40c5-b90f-b5e085898bc0', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep E', 'VACCINE_59', 'hepatitis E vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f7284329-0b50-4353-bb37-2dfbd7c9e631', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'herpes simplex 2', 'VACCINE_60', 'herpes simplex virus, type 2 vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('2929e2c7-7184-4925-ac08-fea5cdbe783c', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hib (PRP-D)', 'VACCINE_46', 'Haemophilus influenzae type b vaccine, PRP-D conjugate');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('90c8bd94-fc1b-4433-84e1-343c524fc090', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hib (HbOC)', 'VACCINE_47', 'Haemophilus influenzae type b vaccine, HbOC conjugate');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('7de129d7-89e0-41ac-8727-c8d0feff0e73', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hib (PRP-T)', 'VACCINE_48', 'Haemophilus influenzae type b vaccine, PRP-T conjugate');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('12972cce-c6b6-4882-815a-880814e94e35', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hib (PRP-OMP)', 'VACCINE_49', 'Haemophilus influenzae type b vaccine, PRP-OMP conjugate');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('9b034d5e-bd4d-4bf9-b52a-0651ae8b4240', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hib, unspecified formulation', 'VACCINE_17', 'Haemophilus influenzae type b vaccine, conjugate unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('ddf7345e-70b1-457d-a003-1c3d5707ad6a', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hib-Hep B', 'VACCINE_51', 'Haemophilus influenzae type b conjugate and Hepatitis B vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('da7579e9-ee76-4f81-97be-db9dde4ccc4d', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'HIV', 'VACCINE_61', 'human immunodeficiency virus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('715f3925-fae7-4047-a39b-07fe9e39eaeb', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'HPV, bivalent', 'VACCINE_118', 'human papilloma virus vaccine, bivalent');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('cd24fb33-a4ed-4d04-b7a4-f66bbd60ed67', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'HPV, quadrivalent', 'VACCINE_62', 'human papilloma virus vaccine, quadrivalent');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('96275924-06f8-45f8-87e0-7dc613809f84', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'IG', 'VACCINE_86', 'immune globulin, intramuscular');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('45732a27-6362-4ae5-9d65-2bdc2c36e348', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'IGIV', 'VACCINE_87', 'immune globulin, intravenous');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('3f6cffc5-0213-417f-8da9-7771dd060fd1', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'IG, unspecified formulation', 'VACCINE_14', 'immune globulin, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('ac5eca17-bc5d-4d07-bc59-2a751d1d7055', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'influenza, live, intranasal', 'VACCINE_111', 'influenza virus vaccine, live, attenuated, for intranasal use');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('cecac352-a277-4f96-a0ed-6931ad7ce108', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'influenza, split (incl. purified surface antigen)', 'VACCINE_15', 'influenza virus vaccine, split virus (incl. purified surface antigen)-retired CODE');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('c18cf9f4-af9b-4543-bb64-a4217772ae81', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'influenza, whole', 'VACCINE_16', 'influenza virus vaccine, whole virus');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('45f3f9e0-a76c-488b-930a-1b8f49a0775a', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'influenza, unspecified formulation', 'VACCINE_88', 'influenza virus vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('89e5c34e-e28c-4522-b56a-55a10239fd26', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'influenza, H5N1-1203', 'VACCINE_123', 'influenza virus vaccine, H5N1, A/Vietnam/1203/2004 (national stockpile)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f0806632-0e9c-4a84-9a7b-b087ef6f7de5', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'IPV', 'VACCINE_10', 'poliovirus vaccine, inactivated');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f2f15a43-6c57-4d57-a32d-d12b468cef7e', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'OPV', 'VACCINE_02', 'trivalent poliovirus vaccine, live, oral');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('1c2df036-336f-4968-85be-c05124257584', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'polio, unspecified formulation', 'VACCINE_89', 'poliovirus vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('aded726c-9dd9-4e89-adfe-fa5d255ec711', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Japanese encephalitis SC', 'VACCINE_39', 'Japanese Encephalitis Vaccine SC');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('26b0380f-0244-4473-a107-7856667888e0', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Junin virus', 'VACCINE_63', 'Junin virus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('571f8019-c7f7-4bdf-8ad7-76bff9822984', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'leishmaniasis', 'VACCINE_64', 'leishmaniasis vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f968ddbb-90dd-46df-9ce5-3ec0c559ae01', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'leprosy', 'VACCINE_65', 'leprosy vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('e54d3073-bc88-41b4-b9d1-5340c4953dbe', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Lyme disease', 'VACCINE_66', 'Lyme disease vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('71f5536a-2587-45b9-88ac-9aba362a424a', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'MMR', 'VACCINE_03', 'measles, mumps and rubella virus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('eac12b34-ddeb-47af-a1de-59ee2dac488f', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'M/R', 'VACCINE_04', 'measles and rubella virus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('055c6647-94ad-4b22-8fd3-ec7d4fbcfb35', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'MMRV', 'VACCINE_94', 'measles, mumps, rubella, and varicella virus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('90680325-0e3b-4cad-8d8e-42628b6a75dd', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'malaria', 'VACCINE_67', 'malaria vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('fa8fb2b5-4945-492b-83a1-50954bc51b25', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'measles', 'VACCINE_05', 'measles virus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('13295577-60bf-4aeb-b7bd-d39d4bef4a3e', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'melanoma', 'VACCINE_68', 'melanoma vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('ff1bc794-0792-499f-b184-440068ed3eb3', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'meningococcal MPSV4', 'VACCINE_32', 'meningococcal polysaccharide vaccine (MPSV4)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('573bfd6d-6b52-448b-b849-c98b5a352e53', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'meningococcal C conjugate', 'VACCINE_103', 'meningococcal C conjugate vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('e6e68c68-59c6-4e5e-8e35-486ad04aa2b3', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'meningococcal MCV4P', 'VACCINE_114', 'meningococcal polysaccharide (groups A, C, Y and W-135) diphtheria toxoid conjugate vaccine (MCV4P)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('10af2a05-7990-4c7f-b896-8ddb671c8420', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'meningococcal ACWY, unspecified formulation', 'VACCINE_108', 'meningococcal ACWY vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('b8918777-91bb-44c5-8ec6-63bf5507fe9f', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'mumps', 'VACCINE_07', 'mumps virus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('82d3f0fc-f331-48ec-a4e5-16e9638717a8', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'parainfluenza-3', 'VACCINE_69', 'parainfluenza-3 virus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('c8dca913-886d-468f-8c82-53903341513a', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'pertussis', 'VACCINE_11', 'pertussis vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('4d93f2a3-8ee7-460d-9570-3e3ccdd0b888', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'plague', 'VACCINE_23', 'plague vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('d700897f-0fe1-4415-bb5d-6fc12c3993d3', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'pneumococcal polysaccharide PPV23', 'VACCINE_33', 'pneumococcal polysaccharide vaccine, 23 valent');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('cc832000-fac4-4c8e-84bb-a5d2d8984e03', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'pneumococcal conjugate PCV 7', 'VACCINE_100', 'pneumococcal conjugate vaccine, 7 valent');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('72ba47ff-45c6-47cb-9dc0-3c8589047dea', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'pneumococcal, unspecified formulation', 'VACCINE_109', 'pneumococcal vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('ccfcb5ec-c369-42fa-a79e-f4dd8a6b121a', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Q fever', 'VACCINE_70', 'Q fever vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('be9da403-2c43-4066-af49-5ec553f01979', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'rabies, intramuscular injection', 'VACCINE_18', 'rabies vaccine, for intramuscular injection RETIRED CODE');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('e86fd74d-ebe5-4109-a434-44a2b6cc5adf', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'rabies, intradermal injection', 'VACCINE_40', 'rabies vaccine, for intradermal injection');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('877fb437-9591-409f-9dbb-73e4a06ba664', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'rabies, unspecified formulation', 'VACCINE_90', 'rabies vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('b1f5b9c5-94d7-47dd-a82b-215daed22c48', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'rheumatic fever', 'VACCINE_72', 'rheumatic fever vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('b8f4ed73-1353-46c5-83f2-2c2eabaccd25', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Rift Valley fever', 'VACCINE_73', 'Rift Valley fever vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('482a68f0-fb47-42c0-948e-69315f26e734', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'RIG', 'VACCINE_34', 'rabies immune globulin');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('3eba29be-af88-4d76-a61c-5573622f86bc', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'rotavirus, monovalent', 'VACCINE_119', 'rotavirus, live, monovalent vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('68e0186f-9a2b-461a-b443-e6ae11f1f47b', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'rotavirus, unspecified formulation', 'VACCINE_122', 'rotavirus vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('c4af73c5-01d7-4d3e-8f3f-89cabebe41a0', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'rotavirus, pentavalent', 'VACCINE_116', 'rotavirus, live, pentavalent vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('9b4dc374-66b9-4da7-bcfe-1a280e7d02bd', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'rotavirus, tetravalent', 'VACCINE_74', 'rotavirus, live, tetravalent vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('b009189c-22f8-492f-9057-605e173e6500', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'RSV-IGIV', 'VACCINE_71', 'respiratory syncytial virus immune globulin, intravenous');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('4fc6efb8-9cbb-4959-aba2-b146f1f750ea', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'RSV-MAb', 'VACCINE_93', 'respiratory syncytial virus monoclonal antibody (palivizumab), intramuscular');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('32fa90b9-c0bd-4f37-9973-1d2333fa4deb', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'rubella', 'VACCINE_06', 'rubella virus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('73bb77b3-4284-46cb-a6e0-07f8a4e19608', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'rubella/mumps', 'VACCINE_38', 'rubella and mumps virus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('4f4d0030-41a5-4d14-a3b4-f84fcea43d2f', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Staphylococcus bacterio lysate', 'VACCINE_76', 'Staphylococcus bacteriophage lysate');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('26c26217-2334-47bd-85f6-243d4351f3fb', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Td (adult), 5 Lf tetanus toxoid, preservative free, adsorbed', 'VACCINE_113', 'tetanus and diphtheria toxoids, adsorbed, preservative free, for adult use (5 Lf of tetanus toxoid and 2 Lf of diphtheria toxoid)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('1aab50c7-8d4b-46b5-93df-6571015869cc', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Td (adult), 2 Lf tetanus toxoid, preservative free, adsorbed', 'VACCINE_09', 'tetanus and diphtheria toxoids, adsorbed, preservative free, for adult use (2 Lf of tetanus toxoid and 2 Lf of diphtheria toxoid)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f2d9edab-52e2-4bf0-bd83-b33e232e0cf0', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Tdap', 'VACCINE_115', 'tetanus toxoid, reduced diphtheria toxoid, and acellular pertussis vaccine, adsorbed');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('04502f9c-5e31-48c6-991e-97bfe403283c', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'tetanus toxoid, adsorbed', 'VACCINE_35', 'tetanus toxoid, adsorbed');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('21800bed-dd89-40a2-a6ff-e79de896ac04', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'tetanus toxoid, unspecified formulation', 'VACCINE_112', 'tetanus toxoid, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('60f72218-0472-4de1-8a07-620393eda4cc', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'tick-borne encephalitis', 'VACCINE_77', 'tick-borne encephalitis vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('454048ad-5a86-4892-bc3e-ecdae17f69aa', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'TIG', 'VACCINE_13', 'tetanus immune globulin');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('af1d01f4-37f9-4a5f-ac8e-3ab41d076f01', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'TST-OT tine test', 'VACCINE_95', 'tuberculin skin test; old tuberculin, multipuncture device');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('8d0f7467-38d7-4b67-a962-956a5e963026', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'TST-PPD intradermal', 'VACCINE_96', 'tuberculin skin test; purified protein derivative solution, intradermal');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f413a77a-4928-4ff8-b944-dc457774c978', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'TST-PPD tine test', 'VACCINE_97', 'tuberculin skin test; purified protein derivative, multipuncture device');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('c8ee3ca2-8210-4d64-bf4a-6dbe61e52a93', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'TST, unspecified formulation', 'VACCINE_98', 'tuberculin skin test; unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('b55851f9-6fdd-43f9-b346-e16ec95aaa45', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'tularemia vaccine', 'VACCINE_78', 'tularemia vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f114ebab-1eb9-4675-b08e-d96f44320626', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'typhoid, unspecified formulation', 'VACCINE_91', 'typhoid vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('226936e0-f120-4e91-8665-c10bc559453d', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'typhoid, oral', 'VACCINE_25', 'typhoid vaccine, live, oral');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('6d84a940-d330-4eca-95b9-66c0283868a5', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'typhoid, parenteral', 'VACCINE_41', 'typhoid vaccine, parenteral, other than acetone-killed, dried');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('332a496f-486e-457e-b392-6debe786e616', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'typhoid, parenteral, AKD (U.S. military)', 'VACCINE_53', 'typhoid vaccine, parenteral, acetone-killed, dried (U.S. military)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('2916ea6e-199e-4c15-b4e9-1a738ca42ddb', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'typhoid, ViCPs', 'VACCINE_101', 'typhoid Vi capsular polysaccharide vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('a430d50c-3375-41b9-b8c3-74d5aa930e6a', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'vaccinia (smallpox)', 'VACCINE_75', 'vaccinia (smallpox) vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('42766894-016b-400f-b2f5-76ce7a349626', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'vaccinia (smallpox) diluted', 'VACCINE_105', 'vaccinia (smallpox) vaccine, diluted');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('b91ea0bf-506b-4cf4-820d-3b28e60b44f9', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'vaccinia immune globulin', 'VACCINE_79', 'vaccinia immune globulin');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f4739648-584d-4595-a79c-a6360eb25e67', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'varicella', 'VACCINE_21', 'varicella virus vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('5679ec68-9393-4466-bdeb-fd034c098722', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'VEE, inactivated', 'VACCINE_81', 'Venezuelan equine encephalitis, inactivated');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('9a778d82-75f2-43e2-bfb7-31bc137cf52f', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'VEE, live', 'VACCINE_80', 'Venezuelan equine encephalitis, live, attenuated');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('6d1ff982-d197-4bbc-bd5a-b6527151de98', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'VEE, unspecified formulation', 'VACCINE_92', 'Venezuelan equine encephalitis vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('85f9e72e-2369-4c67-8983-312ac4d172a3', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'VZIG', 'VACCINE_36', 'varicella zoster immune globulin');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('c5abb597-0e70-4a7d-80fb-715050cf6f83', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'VZIG (IND)', 'VACCINE_117', 'varicella zoster immune globulin (Investigational New Drug)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('a22bd4d3-9ada-48ce-a05d-fcdcc199ed22', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'yellow fever', 'VACCINE_37', 'yellow fever vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('e038f96b-fac0-401c-b51d-85e16cab9e43', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'zoster live', 'VACCINE_121', 'zoster vaccine, live');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('23cc2cf5-aedd-44be-9ec8-e0b0c3af2c0b', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'no vaccine administered', 'VACCINE_998', 'no vaccine administered');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('4b989b37-1197-44e8-84ae-72af75ee89e3', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'unknown', 'VACCINE_999', 'unknown vaccine or immune globulin');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('d0188a49-e2bd-4984-be19-156a740b2ba3', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'RESERVED - do not use', 'VACCINE_99', 'RESERVED - do not use');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('6435e8dc-0acf-4f92-810e-b51a53b5e894', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Pneumococcal conjugate PCV 13', 'VACCINE_133', 'pneumococcal conjugate vaccine, 13 valent');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('0cf9b34d-90d6-4d3a-b990-499acadd0fd7', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Japanese Encephalitis IM', 'VACCINE_134', 'Japanese Encephalitis vaccine for intramuscular administration');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('a8433843-daa3-41c9-b42f-54fa4fba3278', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'HPV, unspecified formulation', 'VACCINE_137', 'HPV, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('5d619143-4907-4325-b5d5-5ce4b372329b', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Meningococcal MCV4O', 'VACCINE_136', 'meningococcal oligosaccharide (groups A, C, Y and W-135) diphtheria toxoid conjugate vaccine (MCV4O)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('29c717e4-7e61-421a-8dba-8a6353b9e4a7', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Influenza, high dose seasonal', 'VACCINE_135', 'influenza, high dose seasonal, preservative-free');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('3a1603d3-b035-427c-89a9-c4e71adfaf14', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'typhus, historical', 'VACCINE_131', 'Historical record of a typhus vaccination');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('2ae523ed-76a9-4b14-acb9-0ff6f20a04bf', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'DTaP-IPV-HIB-HEP B, historical', 'VACCINE_132', 'Historical diphtheria and tetanus toxoids and acellular pertussis, poliovirus, Haemophilus b conjugate and hepatitis B (recombinant) vaccine.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('01f8818d-d28e-45a1-8044-c85fe39e253d', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Novel Influenza-H1N1-09, all formulations', 'VACCINE_128', 'Novel influenza-H1N1-09, all formulations');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('423fd398-1d52-4dff-a354-586c6b8da579', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Novel Influenza-H1N1-09, nasal', 'VACCINE_125', 'Novel Influenza-H1N1-09, live virus for nasal administration');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('23f81d52-12ef-49f2-bb8d-ba877886b300', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Novel influenza-H1N1-09, preservative-free', 'VACCINE_126', 'Novel influenza-H1N1-09, preservative-free, injectable');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('7d626ec7-fc19-4211-8f84-983d9a500e30', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Novel influenza-H1N1-09', 'VACCINE_127', 'Novel influenza-H1N1-09, injectable');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('a91d9d07-e695-4bba-8a74-d3a27cebdc8a', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Td (adult)', 'VACCINE_138', 'tetanus and diphtheria toxoids, not adsorbed, for adult use');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('526e7c54-88b6-4406-b4b2-ec9a5702bf7a', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Td(adult) unspecified formulation', 'VACCINE_139', 'Td(adult) unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('d232c9f5-bc78-4d60-8ce2-f8825383c954', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Influenza, seasonal, injectable, preservative free', 'VACCINE_140', 'Influenza, seasonal, injectable, preservative free');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('7447a86d-cb3b-4f43-ba42-2706260a3a11', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Japanese Encephalitis, unspecified formulation', 'VACCINE_129', 'Japanese Encephalitis vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('87358650-5ef0-49a8-a462-93322fdf8c65', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Influenza, seasonal, injectable', 'VACCINE_141', 'Influenza, seasonal, injectable');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f1574119-80f4-4fc4-aa4f-edf3e2681630', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'tetanus toxoid, not adsorbed', 'VACCINE_142', 'tetanus toxoid, not adsorbed');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('324d7189-8205-4604-b42b-349fc8480718', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Adenovirus types 4 and 7', 'VACCINE_143', 'Adenovirus, type 4 and type 7, live, oral');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('aaccc259-392f-4a80-a174-9a92528ffe47', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'influenza, seasonal, intradermal, preservative free', 'VACCINE_144', 'seasonal influenza, intradermal, preservative free');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('f4f02a9a-6835-409a-9d07-fc5c75fd99a4', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'RSV-MAb (new)', 'VACCINE_145', 'respiratory syncytial virus monoclonal antibody (motavizumab), intramuscular');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('200cc473-6878-48ce-ba48-f0185fd58de7', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'DTaP,IPV,Hib,HepB', 'VACCINE_146', 'Diphtheria and Tetanus Toxoids and Acellular Pertussis Adsorbed, Inactivated Poliovirus, Haemophilus b Conjugate (Meningococcal Protein Conjugate), and Hepatitis B (Recombinant) Vaccine.');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('6edb0791-4186-4d62-98ab-feff3278a50f', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'meningococcal MCV4, unspecified formulation', 'VACCINE_147', 'Meningococcal, MCV4, unspecified conjugate formulation(groups A, C, Y and W-135)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('d8c9aab7-b106-4c1a-810c-4b83143e443a', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Meningococcal C/Y-HIB PRP', 'VACCINE_148', 'Meningococcal Groups C and Y and Haemophilus b Tetanus Toxoid Conjugate Vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('db4c7ce9-91b5-454b-8ff6-2c3510ca56de', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'influenza, live, intranasal, quadrivalent', 'VACCINE_149', 'influenza, live, intranasal, quadrivalent');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('53568296-0ed3-4012-8117-6680238c9181', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'influenza, injectable, quadrivalent, preservative free', 'VACCINE_150', 'Influenza, injectable, quadrivalent, preservative free');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('4039a4b3-0a19-4a4a-bee8-70a297cf8c24', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'influenza nasal, unspecified formulation', 'VACCINE_151', 'influenza nasal, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('e6bf4d56-6f4f-4053-9736-d1a8c911bf49', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Pneumococcal Conjugate, unspecified formulation', 'VACCINE_152', 'Pneumococcal Conjugate, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('9a38b247-39a6-42f4-83c3-af2e31fe68da', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Influenza, injectable, MDCK, preservative free', 'VACCINE_153', 'Influenza, injectable, Madin Darby Canine Kidney, preservative free');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('5b4c4f20-897f-4ff0-b202-220231e65900', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep A, IG', 'VACCINE_154', 'Hepatitis A immune globulin');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('510c906b-5988-4790-8099-e2a015eb513a', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'influenza, recombinant, injectable, preservative free', 'VACCINE_155', 'Seasonal, trivalent, recombinant, injectable influenza vaccine, preservative free');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('6f85fa7e-a693-4832-afae-fe15a94e3e13', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Rho(D)-IG', 'VACCINE_156', 'Rho(D) Immune globulin- IV or IM');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('6d89aae0-7f41-4729-a5ae-bb23d64639aa', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Rho(D) -IG IM', 'VACCINE_157', 'Rho(D) Immune globulin - IM');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('27895d51-3a37-470f-afbf-e0038ab8aaaf', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'influenza, injectable, quadrivalent', 'VACCINE_158', 'influenza, injectable, quadrivalent, contains preservative');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('4ab1c248-e149-475d-b4bd-40915b9d7328', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Rho(D) - Unspecified formulation', 'VACCINE_159', 'Rho(D) Unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('c0968750-edcb-45f2-9391-6ce43dc84994', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Influenza A monovalent (H5N1), ADJUVANTED-2013', 'VACCINE_160', 'Influenza A monovalent (H5N1), adjuvanted, National stockpile 2013');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('4a4ead0c-3ebb-46fa-83b3-c14d46b678e9', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'AS03 Adjuvant', 'VACCINE_801', 'AS03 Adjuvant');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('55bd9e9a-d038-4706-ad53-6223e9a761ab', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Influenza, injectable,quadrivalent, preservative free, pediatric', 'VACCINE_161', 'Influenza, injectable,quadrivalent, preservative free, pediatric');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('b04debc1-9252-485c-b677-ddfdbd3535ca', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'meningococcal B, recombinant', 'VACCINE_162', 'meningococcal B vaccine, fully recombinant');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('55118fc8-e989-4c43-84df-8c2caf8c67d3', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'meningococcal B, OMV', 'VACCINE_163', 'meningococcal B vaccine, recombinant, OMV, adjuvanted');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('912ca93c-10d8-4a56-b751-fb77badd72d9', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'meningococcal B, unspecified', 'VACCINE_164', 'meningococcal B, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('246cc388-a66c-420a-a135-681f9b3742cf', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'HPV9', 'VACCINE_165', 'Human Papillomavirus 9-valent vaccine');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('3ed9adb7-6793-470d-82f1-8d023136a915', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'influenza, intradermal, quadrivalent, preservative free', 'VACCINE_166', 'influenza, intradermal, quadrivalent, preservative free, injectable');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('017e8f38-4c96-4a69-b014-4384d29958ea', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'meningococcal, unknown serogroups', 'VACCINE_167', 'meningococcal vaccine of unknown formulation and unknown serogroups');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('ac8c5bf8-26dd-4211-ad0a-f188dd9ed255', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'influenza, trivalent, adjuvanted', 'VACCINE_168', 'Seasonal trivalent influenza vaccine, adjuvanted, preservative free');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('8a3acc76-f388-4bf8-96d6-bc563c397a01', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep A, live attenuated', 'VACCINE_169', 'Hep A, live attenuated-IM');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('e595076f-63b4-4d23-9ece-bd8ebcc5106a', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'DTAP/IPV/HIB - non-US', 'VACCINE_170', 'non-US diphtheria, tetanus toxoids and acellular pertussis vaccine, Haemophilus influenzae type b conjugate, and poliovirus vaccine, inactivated (DTaP-Hib-IPV)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('754d5a97-a5b5-46a2-a5bd-c247a62dffa2', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Influenza, injectable, MDCK, preservative free, quadrivalent', 'VACCINE_171', 'Influenza, injectable, Madin Darby Canine Kidney, preservative free, quadrivalent');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('1e4aed74-4d82-4418-9cf5-4b80a3639ffd', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'cholera, WC-rBS', 'VACCINE_172', 'cholera, WC-rBS');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('8fad3743-c9a3-4e65-a021-ca280e88544d', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'cholera, BivWC', 'VACCINE_173', 'cholera, BivWC');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('aae99d0a-ca8c-4971-8364-61065c4ce598', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'cholera, live attenuated', 'VACCINE_174', 'cholera, live attenuated');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('cfcda7ec-68a0-4b82-a642-66b6f3bfed88', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Rabies - IM Diploid cell culture', 'VACCINE_175', 'Human Rabies vaccine from human diploid cell culture');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('a2a1d18f-783c-463c-8f88-a8952ea007ec', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Rabies - IM fibroblast culture', 'VACCINE_176', 'Human rabies vaccine from Chicken fibroblast culture');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('41c09845-b22b-4932-b4fb-4cde4b05f823', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'PCV10', 'VACCINE_177', 'pneumococcal conjugate vaccine, 10 valent');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('4deef885-57be-4777-ab9d-df2a91a53a0e', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'OPV bivalent', 'VACCINE_178', 'Non-US bivalent oral polio vaccine (types 1 and 3)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('c8d1af73-1579-42c8-bafd-740f457ae967', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'OPV ,monovalent, unspecified', 'VACCINE_179', 'Non-US monovalent oral polio vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('6358e25a-4758-418d-925d-d555ef003d5e', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'tetanus immune globulin', 'VACCINE_180', 'tetanus immune globulin');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('eba88e59-9077-4188-9e9a-6395cbcfbbf0', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'anthrax immune globulin', 'VACCINE_181', 'anthrax immune globulin');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('888976b0-7296-4ea6-8093-32b88db14143', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'OPV, Unspecified', 'VACCINE_182', 'Oral Polio Vaccine, Unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('8d8c1d31-2ade-41a2-a981-f032ab013d6e', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Yellow fever vaccine - alt', 'VACCINE_183', 'Yellow fever vaccine alternative formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('c6130601-2177-47b9-a4d1-ae1664699152', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Yellow fever, unspecified formulation', 'VACCINE_184', 'Yellow fever vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('bc23ec44-3d34-4adc-9ae4-08da0b1592be', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'influenza, recombinant, quadrivalent,injectable, preservative free', 'VACCINE_185', 'Seasonal, quadrivalent, recombinant, injectable influenza vaccine, preservative free');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('4ab0b157-e27a-4a9e-b9c0-6c37b9fc78e9', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Influenza, injectable, MDCK, quadrivalent, preservative', 'VACCINE_186', 'Influenza, injectable, Madin Darby Canine Kidney,  quadrivalent with preservative');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('04626d48-c625-4d4e-b9de-655b4accd14d', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'zoster recombinant', 'VACCINE_187', 'zoster vaccine recombinant');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('dd38ff02-9d7d-441a-8d3c-6f52fe828622', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'zoster, unspecified formulation', 'VACCINE_188', 'zoster vaccine, unspecified formulation');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('1aa744b0-8cd4-4e89-b326-42fba23ab608', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'HepB-CpG', 'VACCINE_189', 'Hepatitis B vaccine (recombinant), CpG adjuvanted');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('23b8929f-7d5a-40c0-8e14-79d502488d50', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Typhoid conjugate vaccine (TCV)', 'VACCINE_190', 'Typhoid conjugate vaccine (non-US)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('8fd5083f-986d-43b6-b732-04d4b5edbd7b', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'meningococcal A polysaccharide (non-US)', 'VACCINE_191', 'meningococcal A polysaccharide vaccine (non-US)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('c645352b-b84a-47a2-963c-32a22fe18926', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'meningococcal AC polysaccharide (non-US)', 'VACCINE_192', 'meningococcal AC polysaccharide vaccine (non-US)');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description) VALUES ('1ad0b939-6a40-4ba2-82d5-55ef913ee8fe', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'Hep A-Hep B, pediatric/adolescent', 'VACCINE_193', 'hepatitis A and hepatitis B vaccine, pediatric/adolescent (non-US)');

-- Definitions of vaccines by system
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('c513935c-9cd2-4357-a679-60f0c79bfacb', 0, '5d40c358-0111-4472-b3fb-350fdd060b23', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '54');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('ee68d40d-734b-4369-95c4-6dd67b571bbc', 0, '8e170216-deda-47a2-985b-7d2fcf5e40d0', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '55');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('3161188c-7f4a-4763-b74d-0b874b439b75', 0, 'a6f3cccc-8f2c-49e7-b451-80c79ccfc83a', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '82');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('1dc7d989-a6e8-4103-bea8-84cd266532a5', 0, 'd0d0b13b-9870-4490-8c6c-c159a698e165', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '24');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('622a1eef-3418-4515-88a5-9a16a6554340', 0, 'c00aaec0-b02d-480e-9fd6-58578e224e1d', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '19');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('009a0e66-5bcd-486c-a5d8-a133fea8b183', 0, '531826ce-036a-4646-91a6-4fef313d004f', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '27');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('08747415-4014-4110-af37-63d736333d48', 0, 'e8737467-46d5-47ed-b07f-8a8e324e7662', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '26');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('872a3ad7-2d67-495a-9aad-16138a05152c', 0, 'a2966812-3dae-4715-b4c3-dc97783b86d4', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '29');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('d29cc44c-1a52-4cc2-8640-0e675db1ab53', 0, '57c4a571-3125-4c9c-ae85-0bec62cf0179', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '56');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('907982e1-cdc5-4cfe-8d80-5448c1bc00ee', 0, '7244c0c6-1687-4364-b10f-fe49297924d4', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '12');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('697349d0-db0c-4ae2-88d4-22270e62dc7c', 0, '5f89a6a1-5d84-4d9c-a753-7dfa5d7c1298', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '28');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('1accbfcd-e6c5-4598-ad10-dc13bb09bb19', 0, 'f9462e8c-653b-4c6a-a502-8470a1ab2187', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '20');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('47e891bd-07a0-4c4a-a05e-0fd0ed7f364a', 0, '02422ddd-b606-4bb6-8d0f-cca090182b5d', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '106');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('3cbcd3c5-d6a3-48e4-8341-955702e3d3e3', 0, 'e5b53f11-8926-42fe-9e47-f0e6d354f1fc', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '107');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('ad0ff9c2-680c-48d9-b93a-205ad53c7c51', 0, '358c9a4b-a0fe-46f2-b407-92571a70da07', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '110');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('f3be6afe-25e0-4f23-85d9-abb7a2278e0f', 0, '32ad08cc-8f31-47dc-aafb-2ecb7dffbdfd', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '50');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('b251e68a-676b-4edc-9917-a470f271f75c', 0, '2dadecf0-bc85-4467-8270-5453550d54df', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '120');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('4585b866-b4f1-4d90-b233-fa1cbe773217', 0, '11f6fec4-8efc-47fe-8a0b-2f82016fcff2', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '130');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('58a300d0-d667-46e0-91f0-92ea69b32d2f', 0, '7c8465f1-f45a-49f3-b183-f90ffe20c79b', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '01');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('790e19bd-cf16-4d7f-8568-0bc3c1b5fa33', 0, 'd0d399ad-0fe6-4e6c-bc3d-8d06d9cb9db7', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '22');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('c842aa75-9b7c-44aa-b85e-73c5e3155535', 0, '9bf370f8-a318-4dab-b0dc-4c4c5d9a199e', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '102');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('ebc62d70-61ff-40bc-86fa-2082fbe15cb5', 0, 'e259f1a1-b571-41e8-a761-92545e234fa6', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '57');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('041d9dcc-e017-4791-b596-b057cfd74855', 0, '7c8917ef-8e09-49a2-bd80-e5a6f1b5ab6c', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '52');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('fbe7961a-268b-44b6-b06c-612ef955274b', 0, '8ed3cb39-d123-40e6-a265-7231c929a2a7', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '83');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('bc2e6758-0b84-41cf-b40a-ce705c51792f', 0, '8eb0435a-faf3-43e8-ace1-85ac898d4b53', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '84');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('00556b54-d466-4e5b-8e0b-3d2c934cba33', 0, 'e6a6202d-31a3-4a59-b736-3c22cf09adc7', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '31');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('fa257681-98a2-4047-b73d-2027a5549e6f', 0, '461cfc19-aaa1-40e2-a2b2-462155cd6e20', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '85');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('35a14086-02a3-4098-88e4-6b5c8836527c', 0, 'fb90a9d6-7565-42ca-a578-34f2de10b5bf', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '104');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('d73bc496-97c6-40d5-a2ff-a157d12c919c', 0, '84f05f76-fe87-4156-acb5-a6f30343d539', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '30');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('7668d8da-95d0-4a31-a066-ab1501fdc60f', 0, 'f69331e5-b12a-4595-9c4a-17275bd7a709', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '08');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('e377f73b-63ad-48a5-a12f-38fd3220ff47', 0, '3f3232cf-2db8-41a1-9f60-1213515d4100', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '42');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('0c7b4577-f3a0-471c-a2f5-c9ca28cab1a2', 0, '85cb6ed9-3c84-431b-a631-bc78797bc7ed', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '43');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('3db38e2b-787a-44e3-85a4-b0128ffb9fbb', 0, 'e5d8c7b5-c69d-416e-b428-359b4ccb43e8', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '44');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('b68b4aa9-bac3-4319-b8c4-6703dbb30883', 0, '4de7737c-841e-4a86-8bf0-ab6e8a007120', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '45');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('2a066d8a-0ee6-4794-8004-d631a0688d8f', 0, 'd133d6ce-3b4c-4838-9acf-3c9d22c9dd13', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '58');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('e16924f0-6e82-46fc-8de8-fcf2091d1475', 0, 'bf27d89b-f678-40c5-b90f-b5e085898bc0', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '59');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('c64dc29d-a468-4017-a946-ea170842b48f', 0, 'f7284329-0b50-4353-bb37-2dfbd7c9e631', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '60');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('4dce85e5-8f4c-4f04-bea2-07a6928afa58', 0, '2929e2c7-7184-4925-ac08-fea5cdbe783c', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '46');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('f783663c-2e85-4c8c-8d00-e21a38b976db', 0, '90c8bd94-fc1b-4433-84e1-343c524fc090', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '47');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('4e807bec-c99c-485d-8dd3-660b9565a033', 0, '7de129d7-89e0-41ac-8727-c8d0feff0e73', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '48');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('549479aa-8fcf-4239-866d-a4b0a2dbfd28', 0, '12972cce-c6b6-4882-815a-880814e94e35', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '49');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('a552202a-6803-4528-877b-0dc390ecc3e5', 0, '9b034d5e-bd4d-4bf9-b52a-0651ae8b4240', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '17');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('b2bd116f-c4a1-40ad-81dc-991b7c37c825', 0, 'ddf7345e-70b1-457d-a003-1c3d5707ad6a', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '51');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('72323d32-eba9-4bf9-991c-da3f20a800e2', 0, 'da7579e9-ee76-4f81-97be-db9dde4ccc4d', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '61');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('5b50bf92-f211-4907-b19e-6833eef61136', 0, '715f3925-fae7-4047-a39b-07fe9e39eaeb', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '118');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('c4539d21-ed6b-4af6-a44b-c88ae3dff263', 0, 'cd24fb33-a4ed-4d04-b7a4-f66bbd60ed67', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '62');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('b935ccd2-ce5e-4678-bbb3-0b5bf753bd1d', 0, '96275924-06f8-45f8-87e0-7dc613809f84', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '86');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('dbfc3394-ad9b-4c82-b13b-e1d34a524311', 0, '45732a27-6362-4ae5-9d65-2bdc2c36e348', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '87');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('61fc1b10-3ca6-4e95-b7f4-0366a1d3c963', 0, '3f6cffc5-0213-417f-8da9-7771dd060fd1', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '14');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('4a9f2e9f-eecc-4423-a6ae-fd7829b5de33', 0, 'ac5eca17-bc5d-4d07-bc59-2a751d1d7055', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '111');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('1652ebec-a018-465b-b082-ff306fe0df74', 0, 'cecac352-a277-4f96-a0ed-6931ad7ce108', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '15');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('33dfe234-fa0b-4596-9243-61949730249c', 0, 'c18cf9f4-af9b-4543-bb64-a4217772ae81', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '16');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('56a5316a-1d43-4c5b-93eb-8181a38ac081', 0, '45f3f9e0-a76c-488b-930a-1b8f49a0775a', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '88');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('97a70720-1651-4aef-8cd2-c6db81337b44', 0, '89e5c34e-e28c-4522-b56a-55a10239fd26', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '123');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('0056d260-fcca-4783-9e2d-597b52ac0177', 0, 'f0806632-0e9c-4a84-9a7b-b087ef6f7de5', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '10');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('bca3b458-4bb6-4faf-a7e8-d736adcafe82', 0, 'f2f15a43-6c57-4d57-a32d-d12b468cef7e', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '02');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('1b484a68-da52-4a1e-887d-6fbd6b5c6387', 0, '1c2df036-336f-4968-85be-c05124257584', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '89');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('4057dba7-39ef-4a8c-b5fd-0310c4d31ebb', 0, 'aded726c-9dd9-4e89-adfe-fa5d255ec711', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '39');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('ddb9269b-dcb6-4628-aa08-2f85a0bb1337', 0, '26b0380f-0244-4473-a107-7856667888e0', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '63');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('e8dcfe6d-52b6-40d2-95d4-cbbc4b88b4c0', 0, '571f8019-c7f7-4bdf-8ad7-76bff9822984', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '64');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('5b518693-a14e-4c1b-ae46-bc49211db1f2', 0, 'f968ddbb-90dd-46df-9ce5-3ec0c559ae01', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '65');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('014c9347-73ce-4b16-a1e8-b45df854ce57', 0, 'e54d3073-bc88-41b4-b9d1-5340c4953dbe', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '66');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('51f65035-c70b-4b39-b0f6-29a20a74ab45', 0, '71f5536a-2587-45b9-88ac-9aba362a424a', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '03');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('df2db407-84e2-467c-9438-3c0db6b69270', 0, 'eac12b34-ddeb-47af-a1de-59ee2dac488f', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '04');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('db607dad-aa3e-4bc3-951c-428ea0af81aa', 0, '055c6647-94ad-4b22-8fd3-ec7d4fbcfb35', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '94');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('b00174bb-dbd0-4c80-aaee-2a4b0ac19c8c', 0, '90680325-0e3b-4cad-8d8e-42628b6a75dd', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '67');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('8bc076f3-72ae-4cc8-9821-778c7632da2d', 0, 'fa8fb2b5-4945-492b-83a1-50954bc51b25', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '05');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('262cb372-ad75-4c28-bd50-87bf7526ef6e', 0, '13295577-60bf-4aeb-b7bd-d39d4bef4a3e', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '68');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('312a4cdb-c778-45cd-96b3-ae77c3ff91b0', 0, 'ff1bc794-0792-499f-b184-440068ed3eb3', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '32');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('4a48ad5c-62cd-45e6-9851-4065da19adc0', 0, '573bfd6d-6b52-448b-b849-c98b5a352e53', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '103');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('44d771e1-9c19-43bc-8337-6ea1c798fe28', 0, 'e6e68c68-59c6-4e5e-8e35-486ad04aa2b3', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '114');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('6586d174-2ac9-443d-851f-8f340625bedf', 0, '10af2a05-7990-4c7f-b896-8ddb671c8420', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '108');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('5202490a-2388-45f9-b5ef-a81a7740892d', 0, 'b8918777-91bb-44c5-8ec6-63bf5507fe9f', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '07');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('3cd31bdd-f5da-4ebc-ae2b-d1c7d5545045', 0, '82d3f0fc-f331-48ec-a4e5-16e9638717a8', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '69');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('eea8cfcd-04de-477a-99f0-2765c1afcabe', 0, 'c8dca913-886d-468f-8c82-53903341513a', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '11');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('a3709774-d547-443f-a754-fe18d5a063b1', 0, '4d93f2a3-8ee7-460d-9570-3e3ccdd0b888', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '23');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('19cbd9fa-56ad-4848-933a-764d5ad224ba', 0, 'd700897f-0fe1-4415-bb5d-6fc12c3993d3', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '33');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('fd739da7-c070-429c-9ed3-c71ed087a820', 0, 'cc832000-fac4-4c8e-84bb-a5d2d8984e03', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '100');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('a0918a36-8680-491d-a2aa-30588b2bb697', 0, '72ba47ff-45c6-47cb-9dc0-3c8589047dea', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '109');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('22f0d60b-0351-45d4-9a48-23abe44ac33f', 0, 'ccfcb5ec-c369-42fa-a79e-f4dd8a6b121a', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '70');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('d570b563-0ee1-44cd-a486-b3ead605a01b', 0, 'be9da403-2c43-4066-af49-5ec553f01979', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '18');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('b5266140-2ba5-4e85-8351-98bc530450ae', 0, 'e86fd74d-ebe5-4109-a434-44a2b6cc5adf', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '40');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('f60c7e79-f916-4b9f-9ed1-9f9315dfcfa9', 0, '877fb437-9591-409f-9dbb-73e4a06ba664', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '90');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('ab28b7d0-8a14-4c2f-9215-e6a33b9cdb9f', 0, 'b1f5b9c5-94d7-47dd-a82b-215daed22c48', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '72');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('a34f5d9c-0b46-404b-a3f4-7783238d2283', 0, 'b8f4ed73-1353-46c5-83f2-2c2eabaccd25', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '73');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('a3939d3f-c628-481b-b36b-00eec4cf6ff9', 0, '482a68f0-fb47-42c0-948e-69315f26e734', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '34');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('d3b591a2-9c58-4da4-a399-cbc229edd850', 0, '3eba29be-af88-4d76-a61c-5573622f86bc', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '119');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('073fe889-4159-4ee9-8d64-3da71bb90513', 0, '68e0186f-9a2b-461a-b443-e6ae11f1f47b', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '122');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('971d2064-83aa-4722-ba42-d01dde8fecd6', 0, 'c4af73c5-01d7-4d3e-8f3f-89cabebe41a0', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '116');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('785563ea-83c9-4070-80b0-97307f607e8d', 0, '9b4dc374-66b9-4da7-bcfe-1a280e7d02bd', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '74');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('d3d48a8a-968e-4c15-a189-2914d4681435', 0, 'b009189c-22f8-492f-9057-605e173e6500', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '71');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('2396ec13-fcbc-4f09-95ab-7e8cba1223b5', 0, '4fc6efb8-9cbb-4959-aba2-b146f1f750ea', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '93');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('19bc54b5-8f6f-4b83-a04c-30afa79a5d44', 0, '32fa90b9-c0bd-4f37-9973-1d2333fa4deb', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '06');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('6b0ad02d-5248-41e9-b89f-b07eb85b4cf2', 0, '73bb77b3-4284-46cb-a6e0-07f8a4e19608', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '38');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('13daf2ce-bb2b-4a94-a0a8-2c34c60348e4', 0, '4f4d0030-41a5-4d14-a3b4-f84fcea43d2f', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '76');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('4ce7c4d4-4dc1-4b8c-981d-422ae3c69f27', 0, '26c26217-2334-47bd-85f6-243d4351f3fb', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '113');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('ca2f663c-8680-4cf9-99d2-3b511dffd000', 0, '1aab50c7-8d4b-46b5-93df-6571015869cc', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '09');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('a149d58e-03fd-4105-9aec-cce1cf6d6588', 0, 'f2d9edab-52e2-4bf0-bd83-b33e232e0cf0', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '115');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('c278b345-9f09-4881-b3b2-343211ecadb0', 0, '04502f9c-5e31-48c6-991e-97bfe403283c', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '35');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('9e8abd0a-0eb4-46c7-ba5a-f176541217e3', 0, '21800bed-dd89-40a2-a6ff-e79de896ac04', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '112');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('b763d6e5-a1b1-4246-9025-a6efef25b829', 0, '60f72218-0472-4de1-8a07-620393eda4cc', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '77');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('34d212db-c999-408d-a014-3add79dce6dd', 0, '454048ad-5a86-4892-bc3e-ecdae17f69aa', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '13');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('5edea219-2670-487e-aa46-bbbc50e90a8f', 0, 'af1d01f4-37f9-4a5f-ac8e-3ab41d076f01', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '95');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('bd7b4251-5a54-4c27-afc4-efd63f03d0ff', 0, '8d0f7467-38d7-4b67-a962-956a5e963026', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '96');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('70030ed4-f6fc-48ab-81f2-57486c2ea35b', 0, 'f413a77a-4928-4ff8-b944-dc457774c978', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '97');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('a85e3551-dbe0-4fd5-b119-e7fa75d5f576', 0, 'c8ee3ca2-8210-4d64-bf4a-6dbe61e52a93', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '98');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('aaacac60-1cf3-42f6-a0f1-395cfea42fb5', 0, 'b55851f9-6fdd-43f9-b346-e16ec95aaa45', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '78');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('78236933-8756-4ff3-b7b0-679c64daf20d', 0, 'f114ebab-1eb9-4675-b08e-d96f44320626', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '91');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('b6d5f647-3b74-429c-b21e-442883e5ed3e', 0, '226936e0-f120-4e91-8665-c10bc559453d', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '25');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('5e983029-8019-4252-a67a-c033e87d54c9', 0, '6d84a940-d330-4eca-95b9-66c0283868a5', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '41');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('f40b31f3-b0a6-4e35-9cf9-78f6c0eeeaa1', 0, '332a496f-486e-457e-b392-6debe786e616', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '53');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('48234d00-dd9e-4c43-b487-14df9cf3ce7f', 0, '2916ea6e-199e-4c15-b4e9-1a738ca42ddb', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '101');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('2cc307a9-e5d7-4a00-8d6d-10b01c85f776', 0, 'a430d50c-3375-41b9-b8c3-74d5aa930e6a', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '75');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('877d4c61-58d2-4c42-9ede-b557289c27d3', 0, '42766894-016b-400f-b2f5-76ce7a349626', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '105');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('33538b0e-38dc-4706-9a6a-ce48f694672f', 0, 'b91ea0bf-506b-4cf4-820d-3b28e60b44f9', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '79');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('7fec0f15-b268-409b-9ebf-ccd484208597', 0, 'f4739648-584d-4595-a79c-a6360eb25e67', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '21');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('29f176ac-0bad-4402-a35c-de897b058b06', 0, '5679ec68-9393-4466-bdeb-fd034c098722', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '81');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('b4089d2b-9e51-4480-9fec-efcd9fee765d', 0, '9a778d82-75f2-43e2-bfb7-31bc137cf52f', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '80');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('b73041c4-b67f-4f03-a03e-898636a58119', 0, '6d1ff982-d197-4bbc-bd5a-b6527151de98', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '92');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('2bc06248-52b2-43d7-b838-3eb643dc22e1', 0, '85f9e72e-2369-4c67-8983-312ac4d172a3', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '36');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('c16781fa-868d-4d23-83f2-8816290870db', 0, 'c5abb597-0e70-4a7d-80fb-715050cf6f83', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '117');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('c718fbac-f063-4a51-addd-078462ce362f', 0, 'a22bd4d3-9ada-48ce-a05d-fcdcc199ed22', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '37');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('031663ac-e128-4319-8a80-c8994bd586bf', 0, 'e038f96b-fac0-401c-b51d-85e16cab9e43', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '121');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('4d162eaa-0f6f-422e-a95e-d8be49daac13', 0, '23cc2cf5-aedd-44be-9ec8-e0b0c3af2c0b', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '998');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('8c0b1510-2699-45cd-8850-ba0be0d0037c', 0, '4b989b37-1197-44e8-84ae-72af75ee89e3', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '999');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('5d889e9b-9bd8-40e6-8a57-1e82f147460c', 0, 'd0188a49-e2bd-4984-be19-156a740b2ba3', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '99');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('0ac3faef-9b2b-405e-b4c7-c4ef74d66b5c', 0, '6435e8dc-0acf-4f92-810e-b51a53b5e894', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '133');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('a146c031-9c11-46f0-bfdd-4af4dcbbdb68', 0, '0cf9b34d-90d6-4d3a-b990-499acadd0fd7', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '134');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('d85b530c-180c-46bb-97db-bbb70bd88b0b', 0, 'a8433843-daa3-41c9-b42f-54fa4fba3278', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '137');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('5889ef78-997e-473b-a28a-b3f018aa09cf', 0, '5d619143-4907-4325-b5d5-5ce4b372329b', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '136');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('af778144-da17-4c94-8468-3c7a42dae109', 0, '29c717e4-7e61-421a-8dba-8a6353b9e4a7', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '135');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('75cc9fdc-8cb3-449c-8fbd-e14623b852ce', 0, '3a1603d3-b035-427c-89a9-c4e71adfaf14', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '131');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('ed64dc13-fa9d-4608-ac37-cf364006205c', 0, '2ae523ed-76a9-4b14-acb9-0ff6f20a04bf', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '132');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('86ce21aa-745b-424b-9c72-5ae4b3df1d6b', 0, '01f8818d-d28e-45a1-8044-c85fe39e253d', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '128');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('918962c7-11ca-4ea8-82cb-3a2e659fb1d4', 0, '423fd398-1d52-4dff-a354-586c6b8da579', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '125');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('c40cbe70-ef18-4bca-be3a-21a77ab5fc29', 0, '23f81d52-12ef-49f2-bb8d-ba877886b300', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '126');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('cd87d338-f13e-4f63-bc5c-f85b83c52942', 0, '7d626ec7-fc19-4211-8f84-983d9a500e30', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '127');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('345a12bb-39d1-48cf-96d1-305bb023b92b', 0, 'a91d9d07-e695-4bba-8a74-d3a27cebdc8a', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '138');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('8d52fc42-3b50-490b-991f-0341528efcf8', 0, '526e7c54-88b6-4406-b4b2-ec9a5702bf7a', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '139');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('b5d2a55d-1bc9-4f47-b6c4-7ad6823b05d6', 0, 'd232c9f5-bc78-4d60-8ce2-f8825383c954', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '140');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('b4ec0f01-4389-4846-9d64-85fa9eb40e1a', 0, '7447a86d-cb3b-4f43-ba42-2706260a3a11', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '129');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('1de5c88a-d048-4ab9-a326-4aa9a2bb87f1', 0, '87358650-5ef0-49a8-a462-93322fdf8c65', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '141');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('20d8bb82-6fe4-4f34-b58f-0660928d86f3', 0, 'f1574119-80f4-4fc4-aa4f-edf3e2681630', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '142');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('73891cf4-2482-47f9-8e9f-a8f1fcea23ea', 0, '324d7189-8205-4604-b42b-349fc8480718', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '143');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('279adb55-f12d-46a0-a473-7d94098a2f53', 0, 'aaccc259-392f-4a80-a174-9a92528ffe47', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '144');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('b6eb499f-193c-446a-bda1-d4c0302228a8', 0, 'f4f02a9a-6835-409a-9d07-fc5c75fd99a4', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '145');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('aa00d416-1200-4c90-b2a8-01a8781b38e1', 0, '200cc473-6878-48ce-ba48-f0185fd58de7', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '146');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('886d9f5a-5e83-4ed2-93e1-4033f0d3aadd', 0, '6edb0791-4186-4d62-98ab-feff3278a50f', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '147');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('5c0891a4-e324-4e9e-bd18-6e2415b5ab25', 0, 'd8c9aab7-b106-4c1a-810c-4b83143e443a', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '148');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('ae28ca23-61c1-4f99-a3be-d4070554cc8b', 0, 'db4c7ce9-91b5-454b-8ff6-2c3510ca56de', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '149');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('bbf939c6-6647-4844-9aea-a0fea568b0fe', 0, '53568296-0ed3-4012-8117-6680238c9181', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '150');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('6b262700-b47f-4f6f-a937-0ec79adecdb9', 0, '4039a4b3-0a19-4a4a-bee8-70a297cf8c24', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '151');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('a097062c-874c-4b98-a60b-83da2d3e39b7', 0, 'e6bf4d56-6f4f-4053-9736-d1a8c911bf49', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '152');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('24289b55-52a7-43df-bedb-40bc1dd01052', 0, '9a38b247-39a6-42f4-83c3-af2e31fe68da', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '153');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('9b4c0ae1-39f5-423c-9ddd-e2e777831e00', 0, '5b4c4f20-897f-4ff0-b202-220231e65900', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '154');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('64d78032-2c68-4012-a2d3-d56311118f7d', 0, '510c906b-5988-4790-8099-e2a015eb513a', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '155');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('5b83f679-d217-4c33-a7c6-66e4ded43d05', 0, '6f85fa7e-a693-4832-afae-fe15a94e3e13', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '156');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('459ccf59-3a95-48f8-a693-d4439c312738', 0, '6d89aae0-7f41-4729-a5ae-bb23d64639aa', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '157');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('af6ff093-65f2-4e21-8405-746865eb46ea', 0, '27895d51-3a37-470f-afbf-e0038ab8aaaf', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '158');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('f1023310-b544-482f-8541-a9bff40ccae3', 0, '4ab1c248-e149-475d-b4bd-40915b9d7328', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '159');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('821f4468-3be2-427f-8c1f-1d1f38cc1c66', 0, 'c0968750-edcb-45f2-9391-6ce43dc84994', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '160');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('136246e8-a42a-494e-9c01-a8b3619bbeec', 0, '4a4ead0c-3ebb-46fa-83b3-c14d46b678e9', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '801');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('c7434d18-2854-4b75-bc73-ae14bb6ac761', 0, '55bd9e9a-d038-4706-ad53-6223e9a761ab', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '161');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('8eed2872-8dee-44ab-b426-32376fb88df0', 0, 'b04debc1-9252-485c-b677-ddfdbd3535ca', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '162');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('d2d683b6-a3c3-494e-b811-0f9611af85dd', 0, '55118fc8-e989-4c43-84df-8c2caf8c67d3', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '163');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('9ef92e29-d979-42e0-8094-bcfade20fea2', 0, '912ca93c-10d8-4a56-b751-fb77badd72d9', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '164');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('0252120e-d014-4287-b07f-11191c4393e1', 0, '246cc388-a66c-420a-a135-681f9b3742cf', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '165');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('3fff2df7-6553-458b-8a53-90d6de0d46bd', 0, '3ed9adb7-6793-470d-82f1-8d023136a915', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '166');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('23ebd451-27a0-44ba-98d3-7761f01e1b44', 0, '017e8f38-4c96-4a69-b014-4384d29958ea', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '167');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('5deaa166-8969-4947-acc4-52aa7cfde4ab', 0, 'ac8c5bf8-26dd-4211-ad0a-f188dd9ed255', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '168');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('99a3c16e-58dc-4a90-9e67-d26285a0c45c', 0, '8a3acc76-f388-4bf8-96d6-bc563c397a01', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '169');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('1bb07a9f-e128-4ff6-bcf3-13c8b652aea6', 0, 'e595076f-63b4-4d23-9ece-bd8ebcc5106a', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '170');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('787a193f-097c-4601-8c2b-3d271c974e85', 0, '754d5a97-a5b5-46a2-a5bd-c247a62dffa2', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '171');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('02bd418b-ea07-43d0-9ce6-f068af0f641a', 0, '1e4aed74-4d82-4418-9cf5-4b80a3639ffd', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '172');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('e521c8e1-5db7-4eb3-869a-940fd750042f', 0, '8fad3743-c9a3-4e65-a021-ca280e88544d', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '173');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('01e59593-62e2-43da-bf14-e1637c92c4d8', 0, 'aae99d0a-ca8c-4971-8364-61065c4ce598', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '174');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('968bcec0-3d13-4ac1-8430-d491d3983711', 0, 'cfcda7ec-68a0-4b82-a642-66b6f3bfed88', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '175');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('c2918b66-8c21-4dd8-bd1c-82d60217fc2b', 0, 'a2a1d18f-783c-463c-8f88-a8952ea007ec', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '176');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('b12fbeaa-3b89-4b90-8673-a4d7efc13484', 0, '41c09845-b22b-4932-b4fb-4cde4b05f823', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '177');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('c8c8274e-1d21-42ea-ac2b-ae6d1c7c1efd', 0, '4deef885-57be-4777-ab9d-df2a91a53a0e', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '178');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('1aa12db0-643c-4110-be7e-32a5d3b95f6e', 0, 'c8d1af73-1579-42c8-bafd-740f457ae967', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '179');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('5bbfd3a3-df01-4217-8ee5-a6239ecc0909', 0, '6358e25a-4758-418d-925d-d555ef003d5e', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '180');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('8bc8b270-3eb5-4058-a888-3aa46d169df2', 0, 'eba88e59-9077-4188-9e9a-6395cbcfbbf0', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '181');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('05a12bee-777c-4838-bec0-442a490cbf08', 0, '888976b0-7296-4ea6-8093-32b88db14143', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '182');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('bb2dcf9f-721b-416a-80ab-205c1aa6f6a4', 0, '8d8c1d31-2ade-41a2-a981-f032ab013d6e', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '183');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('f8e5b8c1-b6ef-4929-8643-88206d80a7bc', 0, 'c6130601-2177-47b9-a4d1-ae1664699152', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '184');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('bbc51f32-ede4-4b04-9afd-e266aa8e34ba', 0, 'bc23ec44-3d34-4adc-9ae4-08da0b1592be', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '185');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('be0234ad-a57a-49b7-a061-b544211e0876', 0, '4ab0b157-e27a-4a9e-b9c0-6c37b9fc78e9', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '186');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('ebdb03de-315a-419b-adf8-077a22189a98', 0, '04626d48-c625-4d4e-b9de-655b4accd14d', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '187');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('c09bb4c5-dc22-4963-b7a4-b59cbee238e0', 0, 'dd38ff02-9d7d-441a-8d3c-6f52fe828622', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '188');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('4525b2b2-fefd-440d-90ed-ab9a26108254', 0, '1aa744b0-8cd4-4e89-b326-42fba23ab608', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '189');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('efc560ff-6f9d-48e8-a2be-5234cc56de24', 0, '23b8929f-7d5a-40c0-8e14-79d502488d50', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '190');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('9a18ea6e-c950-40cf-9f0c-2b91722405c8', 0, '8fd5083f-986d-43b6-b732-04d4b5edbd7b', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '191');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('f15fd0c3-97f3-488b-a736-140bd6d10111', 0, 'c645352b-b84a-47a2-963c-32a22fe18926', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '192');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code) VALUES ('97faac0d-c0a6-4b14-a5f9-51558e0439c4', 0, '1ad0b939-6a40-4ba2-82d5-55ef913ee8fe', '2601edcb-f7bc-4710-ab64-0f4edd9a2378', '193');

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- BEGIN LOINC Codes
-- The following copyright applies to the following section that contains LOINC Codes:
-- This content LOINC® is copyright © 1995 Regenstrief Institute, Inc. and the LOINC Committee, and available at no cost under the license at http://loinc.org/terms-of-use.
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('cbb25e78-9a9e-4e62-a0ea-1bae99d489ea', 0, '0d78f5e3-c9fd-4859-9768-fb7c898a4142', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '3141-9');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('d88ff0fe-6f76-456e-9785-94859bd63069', 0, 'a748a35e-00e6-4926-abc6-8cde3bb30ee4', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '29463-7');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('a80b6643-580c-468f-8a76-a11a16974cea', 0, '26bb2fe1-8c80-4f01-bfa2-ccca24ffabf7', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '75292-3');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('8344f1aa-b0fe-4fb1-97da-27804eec89b2', 0, '34ab9dc2-4e28-4e2d-8dcb-99c78d24b0fa', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '8350-1');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('779cab23-b987-4546-a9b3-b2faa631e1ca', 0, '4b3d1fb0-2a66-4da4-a8e5-4e3ddcf8cf8c', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '8351-9');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('efe09e62-0925-4342-910b-b321f442915c', 0, 'c03c0878-1896-4d05-8a6c-593892bdd5ba', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '8335-2');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('1041167e-01a6-4816-816a-a8bf727ed0ba', 0, '0139aaee-c988-4ba8-838c-bc26331a9c96', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '3142-7');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('6f397ca3-af11-46c4-8dcf-1777f27ba37b', 0, '124b0bb5-d7cc-483b-a555-356fd3651b4f', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '56092-0');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('c75b0386-6b7d-4df0-a32d-ceb474bacb40', 0, 'bcdc2c18-30ee-4860-9b79-2f810cbcb0f1', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '8339-4');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('208ea7e0-0a54-4c6a-a7a8-38020f832706', 0, '160ab849-ec01-42d3-81dc-056f28faf14d', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '56093-8');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('b5f8e621-7b10-4a75-b7ef-94f39b2da575', 0, '644b6a02-160d-4021-b379-dcd8d8044684', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '56056-5');

INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('7e41f1d3-8b47-490b-8288-c2da5a3c6e66', 0, '5662bc32-974e-4d18-bddc-b10f510439e6', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '9272-6');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('f02c011e-804e-4986-8c9f-160cb7f35fb8', 0, '41578332-0130-486a-bd61-82e85e521a79', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '9273-4');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('3360df97-4c4b-447a-9cc4-7f877a21bb15', 0, '7b87fde7-7a3a-4124-aa63-06dbe26551b4', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '9274-2');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code)
VALUES ('ea64a79f-4ad6-4f02-89e9-179549241425', 0, '9e64ae16-17c5-4fb0-97aa-eaca42c70c12', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '9271-8');

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- END LOINC Codes
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Code set with all BCG vaccines
INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('7348c790-136f-4b4b-a974-f241fb5dbb55', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'All BCG', 'ALL_BCG', 'All BCG vaccines.');
INSERT INTO fhir_code_set_value(code_set_id, code_id)
  SELECT '7348c790-136f-4b4b-a974-f241fb5dbb55', id FROM fhir_code WHERE code IN ('VACCINE_19');

-- Code set with all DTP/DTaP vaccines
INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('bb66ee91-8e86-422c-bb00-5a90ac95a558', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'All DTP/DTaP', 'ALL_DTP_DTAP', 'All DTP/DTaP vaccines.');
INSERT INTO fhir_code_set_value(code_set_id, code_id)
  SELECT 'bb66ee91-8e86-422c-bb00-5a90ac95a558', id FROM fhir_code WHERE code IN ('VACCINE_01', 'VACCINE_102', 'VACCINE_106', 'VACCINE_107', 'VACCINE_110', 'VACCINE_120', 'VACCINE_130', 'VACCINE_132', 'VACCINE_146', 'VACCINE_170', 'VACCINE_20', 'VACCINE_22', 'VACCINE_50');

-- Code set with all OPV vaccines
INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('bf62319c-d93c-444d-a47c-b91133b3f99a', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'All OPV', 'ALL_OPV', 'All OPV vaccines.');
INSERT INTO fhir_code_set_value(code_set_id, code_id)
  SELECT 'bf62319c-d93c-444d-a47c-b91133b3f99a', id FROM fhir_code WHERE code IN ('VACCINE_02', 'VACCINE_179', 'VACCINE_178', 'VACCINE_182');

-- Code set with all Measles vaccines
INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('31c6b008-eb0d-48a3-970d-70725b92bd24', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'All Measles', 'ALL_MEASLES', 'All Measles vaccines.');
INSERT INTO fhir_code_set_value(code_set_id, code_id)
  SELECT '31c6b008-eb0d-48a3-970d-70725b92bd24', id FROM fhir_code WHERE code IN ('VACCINE_03', 'VACCINE_04', 'VACCINE_05', 'VACCINE_94');

-- Code set with all Yellow Fever vaccines
INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('f3769ff6-e994-4182-8d56-572a23b48312', 0, '7090561e-f45b-411e-99c0-65fa1d145018', 'All Yellow Fever', 'ALL_YELLOW_FEVER', 'All Yellow Fever vaccines.');
INSERT INTO fhir_code_set_value(code_set_id, code_id)
  SELECT 'f3769ff6-e994-4182-8d56-572a23b48312', id FROM fhir_code WHERE code IN ('VACCINE_37', 'VACCINE_183', 'VACCINE_184');

-- Code set with all Birth Weight Observations
INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('d4aa72e9-b57e-4d5c-8856-860ebdf460af', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'All Birth Weight Observations', 'ALL_OB_BIRTH_WEIGHT', 'All Birth Weight Observations.');
INSERT INTO fhir_code_set_value(code_set_id, code_id)
  SELECT 'd4aa72e9-b57e-4d5c-8856-860ebdf460af', id FROM fhir_code WHERE code IN ('LOINC_56092-0', 'LOINC_8339-4', 'LOINC_56093-8', 'LOINC_56056-5');

-- Code set with all Apgar Score Observations
INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('39457ebd-308c-4a44-9302-6fa47aa57b3b', 0, '616e470e-fe84-46ac-a523-b23bbc526ae2', 'All Apgar Score Observations', 'ALL_OB_APGAR_SCORE', 'All Apgar Score Observations.');
INSERT INTO fhir_code_set_value(code_set_id, code_id)
  SELECT '39457ebd-308c-4a44-9302-6fa47aa57b3b', id FROM fhir_code WHERE code IN ('LOINC_9272-6', 'LOINC_9273-4', 'LOINC_9274-2', 'LOINC_9271-8');

-- Code set with all Body Weight Observations
INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('d37dfecb-ce88-4fa4-9a78-44ffe874c140', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'All Body Weight Observations', 'ALL_OB_BODY_WEIGHT', 'All Body Weight Observations.');
INSERT INTO fhir_code_set_value(code_set_id, code_id)
  SELECT 'd37dfecb-ce88-4fa4-9a78-44ffe874c140', id FROM fhir_code WHERE code IN
  ('LOINC_3141-9', 'LOINC_29463-7', 'LOINC_75292-3', 'LOINC_8350-1', 'LOINC_8351-9', 'LOINC_8335-2', 'LOINC_3142-7');

-- Script that returns boolean value true every time
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('5b37861d-9442-4e13-ac9f-88a893e91ce9', 0, 'True', 'TRUE', 'Returns Boolean True.', 'EVALUATE', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_source (id , version, script_id, source_text, source_type)
VALUES ('edcb402e-94b4-4953-8846-3a4d1c0dad6e', 0, '5b37861d-9442-4e13-ac9f-88a893e91ce9', 'true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('edcb402e-94b4-4953-8846-3a4d1c0dad6e', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('9299b82e-b90a-4542-8b78-200cadff3d7d', 0, '5b37861d-9442-4e13-ac9f-88a893e91ce9', 'True', 'TRUE', 'Returns Boolean True.');

-- Script that extracts Organisation Unit Reference from Patient
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('a250e109-a135-42b2-8bdb-1c050c1d384c', 0, 'Org Unit Code from FHIR Resource', 'EXTRACT_FHIR_RESOURCE_DHIS_ORG_UNIT_CODE',
'Extracts the organization unit code reference from the input FHIR resource.',
'EVALUATE', 'ORG_UNIT_REF', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a250e109-a135-42b2-8bdb-1c050c1d384c', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a250e109-a135-42b2-8bdb-1c050c1d384c', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('7b94feba-bcf6-4635-929a-01311b25d975', 0, 'a250e109-a135-42b2-8bdb-1c050c1d384c',
'var ref = null;
var organizationReference = null;
if (input.managingOrganization)
{
  organizationReference = input.managingOrganization;
}
if (((organizationReference == null) || organizationReference.isEmpty()) && args[''useLocations''] && input.location && !input.getLocation().isEmpty())
{
  var location = referenceUtils.getResource(input.location);
  if ((location != null) && location.managingOrganization && !location.getManagingOrganization.isEmpty())
  {
    organizationReference = location.managingOrganization;
  }
}
if (organizationReference != null)
{
  var mappedCode = null;
  var hierarchy = organizationUtils.findHierarchy(organizationReference);
  if (hierarchy != null)
  {
    for (var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++)
	  {
      var code = identifierUtils.getResourceIdentifier(hierarchy.get(i), ''ORGANIZATION'');
      if (code != null)
      {
        mappedCode = codeUtils.getMappedCode(code, ''ORGANIZATION'');
        if ((mappedCode == null) && args[''useIdentifierCode''])
        {
          mappedCode = organizationUtils.existsWithPrefix(code);
        }
      }
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
}
if ((ref == null) && args[''useTei''] && (typeof trackedEntityInstance !== ''undefined''))
{
  ref = context.createReference(trackedEntityInstance.organizationUnitId, ''ID'');
}
ref', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('7b94feba-bcf6-4635-929a-01311b25d975', 'DSTU3');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('c0175733-83fc-4de2-9cd0-a2ae6b92e991', 0, 'a250e109-a135-42b2-8bdb-1c050c1d384c',
'useLocations', 'BOOLEAN', TRUE, 'true',
'Specifies if alternatively the managing organization of an included location should be used when the input itself does not contain a managing organization.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('33e66e7a-32cc-4a2e-8224-519e790c8ad2', 0, 'a250e109-a135-42b2-8bdb-1c050c1d384c',
'useIdentifierCode', 'BOOLEAN', TRUE, 'true',
'Specifies if the identifier code itself with the default code prefix for organizations should be used as fallback when no code mapping for the identifier code exists.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('2db146ac-1895-48e0-9d24-e81c7f8a7033', 0, 'a250e109-a135-42b2-8bdb-1c050c1d384c',
'defaultCode', 'CODE', FALSE, null,
'Specifies the default DHIS2 organization unit code that should be used when no other matching DHIS2 organization unit cannot be found.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('ef387255-d1df-48a3-955d-2300bedf1f99', 0, 'a250e109-a135-42b2-8bdb-1c050c1d384c',
'useTei', 'BOOLEAN', TRUE, 'true',
'Specifies if the organization unit of the tracked entity instance (if any) should be used as last fallback when no other organization unit can be found.');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', 0, 'a250e109-a135-42b2-8bdb-1c050c1d384c', 'Org Unit Code from FHIR Resource', 'EXTRACT_FHIR_RESOURCE_DHIS_ORG_UNIT_CODE',
'Extracts the organization unit code reference from the input FHIR resource.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('9de91cc0-979b-43b0-99d5-fc3ee76fd74d', '25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', '33e66e7a-32cc-4a2e-8224-519e790c8ad2', 'true');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('b19e4cc2-0ded-4410-b5d8-5e971e48fd93', '25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', '2db146ac-1895-48e0-9d24-e81c7f8a7033', NULL);

-- Script that extracts GEO location from an Address
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('2263b296-9d96-4698-bc1d-17930005eef3', 0, 'GEO Location from Patient', 'EXTRACT_ADDRESS_GEO_LOCATION',
'Extracts the GEO location form an address that is included in the input.',
'EVALUATE', 'LOCATION', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('2263b296-9d96-4698-bc1d-17930005eef3', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('2263b296-9d96-4698-bc1d-17930005eef3', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('039ac2e6-50f2-4e4a-9e4a-dc0515560273', 0, '2263b296-9d96-4698-bc1d-17930005eef3',
'var location = null;
if (input.address)
{
  location = geoUtils.getLocation(addressUtils.getPrimaryAddress(input.address));
}
location', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('039ac2e6-50f2-4e4a-9e4a-dc0515560273', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('ef90531f-4438-48bd-83b3-6370dd65875a', 0, '2263b296-9d96-4698-bc1d-17930005eef3',  'GEO Location from Address', 'EXTRACT_ADDRESS_GEO_LOCATION',
'Extracts the GEO location form an address that is included in the input.');

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
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a52945b5-94b9-48d4-9c49-f67b43d9dfbc', 0, '0e780e50-9a7e-4d4a-a9fd-1b8607d17fbb', 'Org Unit Reference Code from Patient Organization', 'EXTRACT_TEI_DHIS_ORG_UNIT_ID',
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
'firstNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, 'CODE:MMD_PER_NAM',
'The reference of the tracked entity attribute that contains the first name of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('90b3c110-38e4-4291-934c-e2569e8af1ba', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'birthDateAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the birth date of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('8e3efdc7-6ce4-4899-bb20-faed7d5e3279', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'genderAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the gender of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('40a28a9c-82e3-46e8-9eb9-44aaf2f5eacc', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'addressLineAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the address line (e.g. street, house number) of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('ae13ceca-86d7-4f60-8d54-25587d53a5bd', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'cityAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the city of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('6fb6bfe4-5b44-42a1-812f-be1dc8413d6e', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'stateOfCountryAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the state (i.e. state of country) of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('a77ef245-e65e-4a87-9c96-5047911f9830', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'countryAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the country of the Person.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('b2cfaf30-6ede-41f2-bd6c-448e76c429a1', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'output.setValue(args[''lastNameAttribute''], humanNameUtils.getPrimaryName(input.name).family, context.getFhirRequest().getLastUpdated());
output.setValue(args[''firstNameAttribute''], humanNameUtils.getSingleGiven(humanNameUtils.getPrimaryName(input.name)), context.getFhirRequest().getLastUpdated());
output.setOptionalValue(args[''birthDateAttribute''], dateTimeUtils.getPreciseDate(input.birthDateElement), context.getFhirRequest().getLastUpdated());
output.setOptionalValue(args[''genderAttribute''], input.gender, context.getFhirRequest().getLastUpdated());
output.setOptionalValue(args[''addressLineAttribute''], addressUtils.getSingleLine(addressUtils.getPrimaryAddress(input.address)), context.getFhirRequest().getLastUpdated());
output.setOptionalValue(args[''cityAttribute''], addressUtils.getPrimaryAddress(input.address).city, context.getFhirRequest().getLastUpdated());
output.setOptionalValue(args[''stateOfCountryAttribute''], addressUtils.getPrimaryAddress(input.address).state, context.getFhirRequest().getLastUpdated());
output.setOptionalValue(args[''countryAttribute''], addressUtils.getPrimaryAddress(input.address).country, context.getFhirRequest().getLastUpdated());
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('b2cfaf30-6ede-41f2-bd6c-448e76c429a1', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('72451c8f-7492-4707-90b8-a3e0796de19e', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'Transforms FHIR Patient to DHIS Person', 'TRANSFORM_FHIR_PATIENT_DHIS_PERSON', 'Transforms FHIR Patient to DHIS Person.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('9b832b2c-0a57-4441-8411-47b5dc65ec91', '72451c8f-7492-4707-90b8-a3e0796de19e', '90b3c110-38e4-4291-934c-e2569e8af1ba', 'CODE:MMD_PER_DOB');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('5ce705ce-415c-4fb3-baa7-d3ae67823ac9', '72451c8f-7492-4707-90b8-a3e0796de19e', '8e3efdc7-6ce4-4899-bb20-faed7d5e3279', 'NAME:Gender');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('871dde31-8da8-4345-b38a-e065236a7ffa', '72451c8f-7492-4707-90b8-a3e0796de19e', 'ae13ceca-86d7-4f60-8d54-25587d53a5bd', 'CODE:City');

-- Script that performs the lookup of TEI FHIR Resource from FHIR Observation
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('8b5ab5f1-363d-4ccb-8e63-d6ecf25b3017', 0, 'Observation TEI Lookup', 'OBSERVATION_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Observation.', 'EVALUATE', 'FHIR_RESOURCE', 'FHIR_OBSERVATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('8b5ab5f1-363d-4ccb-8e63-d6ecf25b3017', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('8b5ab5f1-363d-4ccb-8e63-d6ecf25b3017', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('960d2e6c-2479-48a2-b04e-b14879e71d14', 0, '8b5ab5f1-363d-4ccb-8e63-d6ecf25b3017', 'referenceUtils.getResource(input.subject)', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('960d2e6c-2479-48a2-b04e-b14879e71d14', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('1b6a2f75-cb4a-47b1-8e90-3dfb4db07d36', 0, '8b5ab5f1-363d-4ccb-8e63-d6ecf25b3017',
'Observation TEI Lookup', 'OBSERVATION_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Observation.');

-- Script that extracts GEO location from Observation
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('efb7fa7a-df45-4c6a-9096-0bc38f08c067', 0, 'GEO Location from Observation', 'EXTRACT_FHIR_OBSERVATION_GEO_LOCATION',
'Extracts the GEO location form FHIR Observation.',
'EVALUATE', 'LOCATION', 'FHIR_OBSERVATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('efb7fa7a-df45-4c6a-9096-0bc38f08c067', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('efb7fa7a-df45-4c6a-9096-0bc38f08c067', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('2e0565a0-ecde-4ce2-b313-b0f786105385', 0, 'efb7fa7a-df45-4c6a-9096-0bc38f08c067',
'null', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('2e0565a0-ecde-4ce2-b313-b0f786105385', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('bb070631-46b3-42ec-83b2-00ea219bcf50', 0, 'efb7fa7a-df45-4c6a-9096-0bc38f08c067',  'GEO Location from Observation', 'EXTRACT_FHIR_OBSERVATION_GEO_LOCATION',
'Extracts the GEO location form FHIR Observation.');

-- Script that gets the exact date from FHIR Observation
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('49d35701-2979-4b36-a9ba-4269c1572cfd', 0, 'Observation Date Lookup', 'OBSERVATION_DATE_LOOKUP', 'Lookup of the exact date of the FHIR Observation.', 'EVALUATE', 'DATE_TIME', 'FHIR_OBSERVATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('49d35701-2979-4b36-a9ba-4269c1572cfd', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('49d35701-2979-4b36-a9ba-4269c1572cfd', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('630cd838-3506-410b-9b66-8785682d5e0c', 0, '49d35701-2979-4b36-a9ba-4269c1572cfd',
'var date = null;
if (input.hasEffectiveDateTimeType())
  date = dateTimeUtils.getPreciseDate(input.getEffectiveDateTimeType());
else if (input.hasEffectivePeriod())
  date = dateTimeUtils.getPreciseDate(input.getEffectivePeriod().hasStart() ? input.getEffectivePeriod().getStartElement() : null);
else if (input.hasIssued())
  date = dateTimeUtils.getPreciseDate(input.getIssuedElement());
date', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('630cd838-3506-410b-9b66-8785682d5e0c', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a7b60436-9fa7-4fe4-8bf7-f5e22123a980', 0, '49d35701-2979-4b36-a9ba-4269c1572cfd',
'Observation Date Lookup', 'OBSERVATION_DATE_LOOKUP', 'Lookup of the exact date of the FHIR Observation.');

-- FHIR resource mapping for FHIR Observation
INSERT INTO fhir_resource_mapping (id,version,fhir_resource_type,tei_lookup_script_id,enrollment_org_lookup_script_id,event_org_lookup_script_id,
enrollment_date_lookup_script_id,event_date_lookup_script_id,enrollment_loc_lookup_script_id,event_loc_lookup_script_id,effective_date_lookup_script_id)
VALUES ('f1cbd84f-a3db-4aa7-a9f2-a5e547e60bed', 0, 'OBSERVATION', '1b6a2f75-cb4a-47b1-8e90-3dfb4db07d36',
'25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', '25a97bb4-7b39-4ed4-8677-db4bcaa28ccf',
'a7b60436-9fa7-4fe4-8bf7-f5e22123a980', 'a7b60436-9fa7-4fe4-8bf7-f5e22123a980',
'bb070631-46b3-42ec-83b2-00ea219bcf50', 'bb070631-46b3-42ec-83b2-00ea219bcf50',
'a7b60436-9fa7-4fe4-8bf7-f5e22123a980');

-- Script that checks if given FHIR Observation is applicable
INSERT INTO fhir_script (id,version,name,code,description,script_type,return_type,input_type,output_type)
VALUES ('73175d8e-faad-458f-b38f-14ff87032720', 0, 'FHIR Observation Applicable', 'FHIR_OBSERVATION_APPLICABLE', 'Checks if given FHIR Observation is applicable.', 'EVALUATE', 'BOOLEAN', 'FHIR_OBSERVATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('73175d8e-faad-458f-b38f-14ff87032720', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('73175d8e-faad-458f-b38f-14ff87032720', 'INPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('f08bc5b3-0aef-4707-8959-65d7cf690134', 0, '73175d8e-faad-458f-b38f-14ff87032720',
'mappedObservationCodes', 'CODE', TRUE, TRUE, NULL, 'Mapped observation codes that define if the FHIR Observation is applicable for processing.');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('376386c8-e306-49c7-9395-0d334aab60fc', 0, '73175d8e-faad-458f-b38f-14ff87032720', 'codeUtils.containsMappedCode(input.code, args[''mappedObservationCodes''])', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('376386c8-e306-49c7-9395-0d334aab60fc', 'DSTU3');

-- Script that sets for a data element the Apgar Score
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('d69681b8-2e37-42d7-b229-9ed21ce76cf3', 0, 'TRANSFORM_FHIR_OB_APGAR_SCORE', 'Transforms FHIR Observation Apgar Score', 'Transforms FHIR Observation Apgar Score to a data element.',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_OBSERVATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d69681b8-2e37-42d7-b229-9ed21ce76cf3', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d69681b8-2e37-42d7-b229-9ed21ce76cf3', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d69681b8-2e37-42d7-b229-9ed21ce76cf3', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('3fdad25a-63ce-4754-9a37-602b08bd8396', 0, 'd69681b8-2e37-42d7-b229-9ed21ce76cf3',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element on which the apgar score must be set.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('e9587a64-9abb-484c-b054-d76ddf2e83d1', 0, 'd69681b8-2e37-42d7-b229-9ed21ce76cf3',
'commentDataElement', 'DATA_ELEMENT_REF', FALSE, NULL, 'Data element on which the apgar score comment must be set.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, array_value, mandatory, default_value, description)
VALUES ('65cefdfe-eaaa-4aa1-82d3-bbba005dc327', 0, 'd69681b8-2e37-42d7-b229-9ed21ce76cf3',
'mappedApgarScoreCodes', 'CODE', TRUE, TRUE, 'LOINC_9272-6|LOINC_9273-4|LOINC_9274-2|LOINC_9271-8',
'Mapped Apgar Score codes in order of their relevance. The first Apgar Score code is more relevant than the last one.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('2e378bc2-9ed6-42e5-8337-0ee8bbf22fbf', 0, 'd69681b8-2e37-42d7-b229-9ed21ce76cf3',
'maxCommentApgarScore', 'INTEGER', FALSE, NULL, 'The maximum Apgar Score for which a comment is recorded.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('628348ae-9a40-4f0d-af78-5c6e0bfa1c6d', 0, 'd69681b8-2e37-42d7-b229-9ed21ce76cf3',
'var updated = false; var latestApgarScore = observationUtils.queryLatestPrioritizedByMappedCodes(''subject'', ''Patient'', input.getSubject().getReferenceElement(), args[''mappedApgarScoreCodes''], null);
if ((latestApgarScore != null) && latestApgarScore.hasValueQuantity())
{
  var apgarScore = latestApgarScore.getValueQuantity().getValue();
  updated = output.setValue(args[''dataElement''], apgarScore, null, context.getFhirRequest().getLastUpdated());
  if (updated && (args[''commentDataElement''] != null))
  {
    if ((args[''maxCommentApgarScore''] == null) || (apgarScore <= args[''maxCommentApgarScore'']))
    {
      output.setValue(args[''commentDataElement''], observationUtils.getComponentText(latestApgarScore));
    }
    else
    {
      output.setValue(args[''commentDataElement''], null);
    }
  }
}
updated', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('628348ae-9a40-4f0d-af78-5c6e0bfa1c6d', 'DSTU3');

-- Script that sets for a data element the birth weight with a specific weight unit
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('50a60c9b-d7f2-4cea-bbc7-b633d276a2f7', 0, 'TRANSFORM_FHIR_OB_BIRTH_WEIGHT', 'Transforms FHIR Observation Birth Weight', 'Transforms FHIR Observation Birth Weight to a data element and performs weight unit conversion.',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_OBSERVATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('50a60c9b-d7f2-4cea-bbc7-b633d276a2f7', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('50a60c9b-d7f2-4cea-bbc7-b633d276a2f7', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('50a60c9b-d7f2-4cea-bbc7-b633d276a2f7', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('600187f8-eba5-4599-9061-b6fe8bc1c518', 0, '50a60c9b-d7f2-4cea-bbc7-b633d276a2f7',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element on which the birth weight must be set.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('ead06d42-8e40-47f5-8a89-74fbcf237b2b', 0, '50a60c9b-d7f2-4cea-bbc7-b633d276a2f7',
'weightUnit', 'WEIGHT_UNIT', TRUE, 'KILO_GRAM', 'The resulting weight unit in which the value will be set on the data element.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('5e272692-0214-44fe-a16c-0c79f2f61917', 0, '50a60c9b-d7f2-4cea-bbc7-b633d276a2f7',
'round', 'BOOLEAN', TRUE, 'true', 'Specifies if the resulting value should be rounded.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('05485e11-fb1f-4fc1-8518-f9322dfb7dbb', 0, '50a60c9b-d7f2-4cea-bbc7-b633d276a2f7',
'output.setValue(args[''dataElement''], vitalSignUtils.getWeight(input.value, args[''weightUnit''], args[''round'']), null, context.getFhirRequest().getLastUpdated())', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('05485e11-fb1f-4fc1-8518-f9322dfb7dbb', 'DSTU3');

-- Script that sets for a data element the body weight with a specific weight unit
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('f1da6937-e2fe-47a4-b0f3-8bbff7818ee1', 0, 'TRANSFORM_FHIR_OB_BODY_WEIGHT', 'Transforms FHIR Observation Body Weight', 'Transforms FHIR Observation Body Weight to a data element and performs weight unit conversion.',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_OBSERVATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f1da6937-e2fe-47a4-b0f3-8bbff7818ee1', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f1da6937-e2fe-47a4-b0f3-8bbff7818ee1', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f1da6937-e2fe-47a4-b0f3-8bbff7818ee1', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('07679199-59ae-4530-9411-ac5814102372', 0, 'f1da6937-e2fe-47a4-b0f3-8bbff7818ee1',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element on which the body weight must be set.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('1ef4f760-de9a-4c29-a321-a8eee5c52313', 0, 'f1da6937-e2fe-47a4-b0f3-8bbff7818ee1',
'override', 'BOOLEAN', TRUE, 'true', 'Specifies if an existing value should be overridden.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('3d15bf81-343c-45bc-9c28-1e87a8da6fa5', 0, 'f1da6937-e2fe-47a4-b0f3-8bbff7818ee1',
'weightUnit', 'WEIGHT_UNIT', TRUE, 'KILO_GRAM', 'The resulting weight unit in which the value will be set on the data element.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('d8cd0e7d-7780-45d1-8094-b448b480e6b8', 0, 'f1da6937-e2fe-47a4-b0f3-8bbff7818ee1',
'round', 'BOOLEAN', TRUE, 'true', 'Specifies if the resulting value should be rounded.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('8d26fe6a-d9ac-40ab-abc8-5a87ab340762', 0, 'f1da6937-e2fe-47a4-b0f3-8bbff7818ee1',
'output.setValue(args[''dataElement''], vitalSignUtils.getWeight(input.value, args[''weightUnit''], args[''round'']), null, args[''override''], context.getFhirRequest().getLastUpdated())', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('8d26fe6a-d9ac-40ab-abc8-5a87ab340762', 'DSTU3');

-- Script that performs the lookup of TEI FHIR Resource from FHIR Immunization
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('d4e2822a-4422-46a3-badc-cf5604c6e11f', 0, 'Immunization TEI Lookup', 'IMMUNIZATION_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Immunization.', 'EVALUATE', 'FHIR_RESOURCE', 'FHIR_IMMUNIZATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d4e2822a-4422-46a3-badc-cf5604c6e11f', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d4e2822a-4422-46a3-badc-cf5604c6e11f', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('85b3c460-6c2a-4f50-af46-ff09bf2e69df', 0, 'd4e2822a-4422-46a3-badc-cf5604c6e11f', 'referenceUtils.getResource(input.patient)', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('85b3c460-6c2a-4f50-af46-ff09bf2e69df', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a08caa8a-1cc9-4f51-b6b8-814af781a442', 0, 'd4e2822a-4422-46a3-badc-cf5604c6e11f',
'Immunization TEI Lookup', 'IMMUNIZATION_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Immunization.');

-- Script that extracts GEO location from Immunization
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('a5079830-f04c-4575-af5d-1d6fa0bf844b', 0, 'GEO Location from Immunization', 'EXTRACT_FHIR_IMMUNIZATION_GEO_LOCATION',
'Extracts the GEO location form FHIR Immunization.',
'EVALUATE', 'LOCATION', 'FHIR_IMMUNIZATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a5079830-f04c-4575-af5d-1d6fa0bf844b', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a5079830-f04c-4575-af5d-1d6fa0bf844b', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('6427e6de-8426-4ab1-b66a-edd5b0e5f410', 0, 'a5079830-f04c-4575-af5d-1d6fa0bf844b',
'var location = null;
var locationResource = referenceUtils.getResource(input.getLocation());
if ((locationResource != null) && locationResource.hasPosition())
{
  var position = locationResource.getPosition();
  location = geoUtils.create(position.getLongitude(), position.getLatitude());
}
location', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('6427e6de-8426-4ab1-b66a-edd5b0e5f410', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('298b4a72-94ce-4f4c-83ad-c8d73436a402', 0, 'a5079830-f04c-4575-af5d-1d6fa0bf844b',  'GEO Location from Immunization', 'EXTRACT_FHIR_IMMUNIZATION_GEO_LOCATION',
'Extracts the GEO location form FHIR Immunization.');

-- Script that gets the exact date from FHIR Immunization
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('15c2a8b8-b8f0-443a-adda-cfb87a1a4378', 0, 'Immunization Date Lookup', 'IMMUNIZATION_DATE_LOOKUP', 'Lookup of the exact date of the FHIR Immunization.', 'EVALUATE', 'DATE_TIME', 'FHIR_IMMUNIZATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('15c2a8b8-b8f0-443a-adda-cfb87a1a4378', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('15c2a8b8-b8f0-443a-adda-cfb87a1a4378', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('71056dc1-6bd3-491d-908c-c1494090ed65', 0, '15c2a8b8-b8f0-443a-adda-cfb87a1a4378', 'dateTimeUtils.getPreciseDate(input.hasDateElement() ? input.getDateElement() : null)', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('71056dc1-6bd3-491d-908c-c1494090ed65', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('c0e2c559-ff88-4376-8063-031f971072dc', 0, '15c2a8b8-b8f0-443a-adda-cfb87a1a4378',
'Immunization Date Lookup', 'IMMUNIZATION_DATE_LOOKUP', 'Lookup of the exact date of the FHIR Immunization.');

-- FHIR resource mapping for FHIR Immunization
INSERT INTO fhir_resource_mapping (id,version,fhir_resource_type,tei_lookup_script_id,enrollment_org_lookup_script_id,event_org_lookup_script_id,
enrollment_date_lookup_script_id,event_date_lookup_script_id,enrollment_loc_lookup_script_id,event_loc_lookup_script_id,effective_date_lookup_script_id)
VALUES ('44a6c99c-c83c-4061-acd2-39e4101de147', 0, 'IMMUNIZATION', 'a08caa8a-1cc9-4f51-b6b8-814af781a442',
'a52945b5-94b9-48d4-9c49-f67b43d9dfbc', 'a52945b5-94b9-48d4-9c49-f67b43d9dfbc',
'c0e2c559-ff88-4376-8063-031f971072dc', 'c0e2c559-ff88-4376-8063-031f971072dc',
'298b4a72-94ce-4f4c-83ad-c8d73436a402', '298b4a72-94ce-4f4c-83ad-c8d73436a402',
'c0e2c559-ff88-4376-8063-031f971072dc');

-- Script that checks if given FHIR Immunization is applicable
INSERT INTO fhir_script (id,version,name,code,description,script_type,return_type,input_type,output_type)
VALUES ('ddaebe0a-8a88-4cb7-a5ca-632e86216be0', 0, 'FHIR Immunization Applicable', 'FHIR_IMMUNIZATION_APPLICABLE', 'Checks if given FHIR Immunization is applicable since it has been given.', 'EVALUATE', 'BOOLEAN', 'FHIR_IMMUNIZATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ddaebe0a-8a88-4cb7-a5ca-632e86216be0', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ddaebe0a-8a88-4cb7-a5ca-632e86216be0', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('2f04a7c3-7041-4c12-aa75-748862271818', 0, 'ddaebe0a-8a88-4cb7-a5ca-632e86216be0', '!input.notGiven', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('2f04a7c3-7041-4c12-aa75-748862271818', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('a88b2c6a-508f-4d02-bcfd-bba0a804b340', 0, 'ddaebe0a-8a88-4cb7-a5ca-632e86216be0', 'FHIR Immunization Applicable', 'FHIR_IMMUNIZATION_APPLICABLE');

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

-- Script that sets for a data element the dose sequence of the FHIR Immunization where the data element contains an option set
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('f18acd12-bc85-4f79-935d-353904eadc0b', 0, 'TRANSFORM_FHIR_IMMUNIZATION_OS', 'Transforms FHIR Immunization to option set data element', 'Transforms FHIR Immunization to an option set data element.',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_IMMUNIZATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f18acd12-bc85-4f79-935d-353904eadc0b', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f18acd12-bc85-4f79-935d-353904eadc0b', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f18acd12-bc85-4f79-935d-353904eadc0b', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('44134ba8-d77f-4c4d-90c6-b434ffbe7958', 0, 'f18acd12-bc85-4f79-935d-353904eadc0b',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element with given vaccine on which option set value must be set.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('404ae6f6-6187-4914-8f4b-80a72764c1d8', 0, 'f18acd12-bc85-4f79-935d-353904eadc0b',
'optionValuePattern', 'PATTERN', FALSE, NULL, 'Regular expression pattern to extract subsequent integer option value from option code. If the pattern is not specified the whole code will be used as an integer value.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('081c4642-bb83-44ab-b90f-aa206ad347aa', 0, 'f18acd12-bc85-4f79-935d-353904eadc0b',
'output.setIntegerOptionValue(args[''dataElement''], immunizationUtils.getMaxDoseSequence(input), 1, false, args[''optionValuePattern''], (input.hasPrimarySource()?!input.getPrimarySource():null))', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('081c4642-bb83-44ab-b90f-aa206ad347aa', 'DSTU3');

-- Rule FHIR Patient to tracked entity type Person
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, applicable_in_script_id, transform_in_script_id)
VALUES ('5f9ebdc9-852e-4c83-87ca-795946aabc35', 0, 'FHIR Patient to Person', NULL, TRUE, 0, 'PATIENT', 'TRACKED_ENTITY', '9299b82e-b90a-4542-8b78-200cadff3d7d', '72451c8f-7492-4707-90b8-a3e0796de19e');
INSERT INTO fhir_tracked_entity_rule (id, tracked_entity_ref, org_lookup_script_id, loc_lookup_script_id, tracked_entity_identifier_ref)
VALUES ('5f9ebdc9-852e-4c83-87ca-795946aabc35', 'NAME:Person', '25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', 'ef90531f-4438-48bd-83b3-6370dd65875a', 'CODE:National identifier');

UPDATE fhir_system_code sc SET system_code_value = (SELECT system_uri FROM fhir_system s WHERE s.id=sc.system_id) || '|' || system_code;
ALTER TABLE fhir_system_code ALTER COLUMN system_code_value SET NOT NULL;
CREATE INDEX fhir_system_code_i3 ON fhir_system_code (system_code_value);
