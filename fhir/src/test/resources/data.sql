--
-- Copyright (c) 2004-2018, University of Oslo
-- All rights reserved.
--
-- Redistribution and use in source and binary forms, with or without
-- modification, are permitted provided that the following conditions are met:
-- Redistributions of source code must retain the above copyright notice, this
-- list of conditions and the following disclaimer.
--
-- Redistributions in binary form must reproduce the above copyright notice,
-- this list of conditions and the following disclaimer in the documentation
-- and/or other materials provided with the distribution.
-- Neither the name of the HISP project nor the names of its contributors may
-- be used to endorse or promote products derived from this software without
-- specific prior written permission.
--
-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
-- ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
-- WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
-- DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
-- ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
-- (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
-- LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
-- ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
-- (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
-- SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
--

INSERT INTO fhir_code_category(id, version, created_at, last_updated_at, last_updated_by, name, code, description)
VALUES ('8673a315dd274e4cbb8b1212808d4ca1', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'Organization Unit', 'ORGANIZATION_UNIT', 'Includes the mapping for organization units.');

INSERT INTO fhir_code(id, version, created_at, last_updated_at, last_updated_by, name, code, mapped_code, description, code_category_id)
VALUES ('348d9391c77048538cdff3c5c6830485', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'Central Hospital Freetown', 'OU_FT_CH', 'OU_6125', 'Organization unit Central Hospital in Freetown.', '8673a315dd274e4cbb8b1212808d4ca1');

INSERT INTO fhir_constant (id, version, created_at, last_updated_at, last_updated_by, category, name, code, data_type, value)
VALUES ('fa4a3a0eca4640e4b8323aec96bed55e', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'GENDER', 'Gender Male', 'GENDER_MALE', 'STRING', 'Male');

INSERT INTO fhir_script (id, version, created_at, last_updated_at, last_updated_by, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('f18acd12bc854f79935d353904eadc0b', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'TRANSFORM_FHIR_IMMUNIZATION_OS', 'Transforms FHIR Immunization to option set data element', 'Transforms FHIR Immunization to an option set data element.',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_IMMUNIZATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f18acd12bc854f79935d353904eadc0b', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f18acd12bc854f79935d353904eadc0b', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f18acd12bc854f79935d353904eadc0b', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, created_at, last_updated_at, last_updated_by, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('44134ba8d77f4c4d90c6b434ffbe7958', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f18acd12bc854f79935d353904eadc0b',
'dataElement', 'DATA_ELEMENT_REF', TRUE, FALSE, NULL, 'Data element with given vaccine on which option set value must be set.');
INSERT INTO fhir_script_argument(id, version, created_at, last_updated_at, last_updated_by, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('404ae6f6618749148f4b80a72764c1d8', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f18acd12bc854f79935d353904eadc0b',
'optionValuePattern', 'PATTERN', FALSE, FALSE, NULL, 'Regular expression pattern to extract subsequent integer option value from option code. If the pattern is not specified the whole code will be used as an integer value.');
INSERT INTO fhir_script_source (id, version, created_at, last_updated_at, last_updated_by, script_id, source_text, source_type)
VALUES ('081c4642bb8344abb90faa206ad347aa', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f18acd12bc854f79935d353904eadc0b',
'output.setIntegerOptionValue(args[''dataElement''], immunizationUtils.getMaxDoseSequence(input), 1, false, args[''optionValuePattern''], (input.hasPrimarySource()?!input.getPrimarySource():null))', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('081c4642bb8344abb90faa206ad347aa', 'DSTU3');

INSERT INTO fhir_executable_script (id, version, created_at, last_updated_at, last_updated_by, script_id, name, code, description)
VALUES ('1a2950cf08424dd39453284fb08789d3', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f18acd12bc854f79935d353904eadc0b', 'CP: OPV Dose', 'CP_OPV_DOSE', 'Transforms FHIR Immunization for OPV vaccines.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value, enabled)
VALUES ('4a8ba21510e946f2921fda3973836119', '1a2950cf08424dd39453284fb08789d3', '44134ba8d77f4c4d90c6b434ffbe7958', 'CODE:DE_2006104', TRUE);

INSERT INTO fhir_script (id, version, created_at, last_updated_at, last_updated_by, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('f1da6937e2fe47a4b0f38bbff7818ee1', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'TRANSFORM_FHIR_OB_BODY_WEIGHT', 'Transforms FHIR Observation Body Weight', 'Transforms FHIR Observation Body Weight to a data element and performs weight unit conversion.',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_OBSERVATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f1da6937e2fe47a4b0f38bbff7818ee1', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f1da6937e2fe47a4b0f38bbff7818ee1', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f1da6937e2fe47a4b0f38bbff7818ee1', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, created_at, last_updated_at, last_updated_by, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('0767919959ae45309411ac5814102372', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f1da6937e2fe47a4b0f38bbff7818ee1',
'dataElement', 'DATA_ELEMENT_REF', TRUE, FALSE, NULL, 'Data element on which the body weight must be set.');
INSERT INTO fhir_script_argument(id, version, created_at, last_updated_at, last_updated_by, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('1ef4f760de9a4c29a321a8eee5c52313', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f1da6937e2fe47a4b0f38bbff7818ee1',
'override', 'BOOLEAN', TRUE, FALSE, 'true', 'Specifies if an existing value should be overridden.');
INSERT INTO fhir_script_argument(id, version, created_at, last_updated_at, last_updated_by, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('d8cd0e7d778045d18094b448b480e6b8', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f1da6937e2fe47a4b0f38bbff7818ee1',
'round', 'BOOLEAN', TRUE, FALSE, 'true', 'Specifies if the resulting value should be rounded.');

INSERT INTO fhir_system (id, version, created_at, last_updated_at, last_updated_by, name, code, system_uri, description_protected, enabled)
VALUES ('2dd51309331940d29a1fbe2a102df4a7', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'Sierra Leone Location', 'SYSTEM_SL_LOCATION', 'http://example.sl/locations', FALSE, TRUE);
INSERT INTO fhir_system (id, version, created_at, last_updated_at, last_updated_by, name, code, system_uri, description_protected, enabled)
VALUES ('c4e9ac6acc8f4c73aab60fa6775c0ca3', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'Sierra Leone Organization', 'SYSTEM_SL_ORGANIZATION', 'http://example.sl/organizations', FALSE, TRUE);

INSERT INTO fhir_system_code(id, version, created_at, last_updated_at, last_updated_by, code_id, system_id, system_code, system_code_value)
VALUES ('c513935c9cd24357a67960f0c79bfacb', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', '348d9391c77048538cdff3c5c6830485', 'c4e9ac6acc8f4c73aab60fa6775c0ca3', '982737', 'http://example.sl/organizations|982737');

INSERT INTO fhir_remote_subscription(id, version, created_at, last_updated_at, last_updated_by, name, code, description, fhir_version, web_hook_authorization_header,
dhis_authentication_method, dhis_username, dhis_password, remote_base_url, tolerance_millis, logging, verbose_logging, adapter_base_url, subscription_type, enabled, locked)
VALUES ('73cd99c50ca842ada53b1891fccce08f', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'HAPI FHIR JPA Server', 'DEFAULT_SUBSCRIPTION', 'HAPI FHIR JPA Server.', 'DSTU3',
'Bearer jhsj832jDShf8ehShdu7ejhDhsilwmdsgs', 'BASIC', 'admin', 'district', 'http://localhost:8082/hapifhirjpaserverexample/baseDstu3', 60000, FALSE, FALSE,
'http://localhost:8081', 'REST_HOOK_WITH_JSON_PAYLOAD', TRUE, FALSE);
INSERT INTO fhir_remote_subscription_header (remote_subscription_id, name, value, secure)
VALUES ('73cd99c50ca842ada53b1891fccce08f', 'Authorization', 'Bearer jshru38jsHdsdfy38sh38H3d', TRUE);

INSERT INTO fhir_remote_subscription_resource (id, version, created_at, last_updated_at, last_updated_by, remote_subscription_id, fhir_resource_type, fhir_criteria_parameters, description)
VALUES ('667bfa41867c479686b6eb9f9ed4dc94', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', '73cd99c50ca842ada53b1891fccce08f', 'PATIENT', '_format=json', 'Subscription for all Patients.');
INSERT INTO fhir_remote_subscription_system (id, version, created_at, last_updated_at, last_updated_by, remote_subscription_id, fhir_resource_type, system_id)
VALUES ('ea9804a39e824d0d9cd2e417b32b1c0c', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', '73cd99c50ca842ada53b1891fccce08f', 'ORGANIZATION', 'c4e9ac6acc8f4c73aab60fa6775c0ca3');
