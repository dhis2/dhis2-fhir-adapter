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

-- @formatter:off

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

INSERT INTO fhir_script (id, version, created_at, last_updated_at, last_updated_by, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('f18acd12bc854f79935d353904eadc0c', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'TEST', 'Test script', 'Test script.',
        'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_IMMUNIZATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('f18acd12bc854f79935d353904eadc0c', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('f18acd12bc854f79935d353904eadc0c', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('f18acd12bc854f79935d353904eadc0c', 'OUTPUT');

INSERT INTO fhir_executable_script (id, version, created_at, last_updated_at, last_updated_by, script_id, name, code, description)
VALUES ('1a2950cf08424dd39453284fb08789d3', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'f18acd12bc854f79935d353904eadc0b', 'CP: OPV Dose', 'CP_OPV_DOSE', 'Transforms FHIR Immunization for OPV vaccines.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value, enabled)
VALUES ('4a8ba21510e946f2921fda3973836119', '1a2950cf08424dd39453284fb08789d3', '44134ba8d77f4c4d90c6b434ffbe7958', 'CODE:DE_2006104', TRUE);

-- Script that returns boolean value true every time
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('5b37861d94424e13ac9f88a893e91ce9', 0, 'True', 'TRUE', 'Returns Boolean True.', 'EVALUATE', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('edcb402e94b4495388463a4d1c0dad6e', 0, '5b37861d94424e13ac9f88a893e91ce9', 'true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('edcb402e94b4495388463a4d1c0dad6e', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('9299b82eb90a45428b78200cadff3d7d', 0, '5b37861d94424e13ac9f88a893e91ce9', 'True', 'TRUE', 'Returns Boolean True.');

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
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('3d15bf81343c45bc9c281e87a8da6fa5', 0, 'f1da6937e2fe47a4b0f38bbff7818ee1',
'weightUnit', 'WEIGHT_UNIT', TRUE, 'KILO_GRAM', 'The resulting weight unit in which the value will be set on the data element.');

INSERT INTO fhir_system (id, version, created_at, last_updated_at, last_updated_by, name, code, system_uri, description_protected, enabled)
VALUES ('2dd51309331940d29a1fbe2a102df4a7', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'Sierra Leone Location', 'SYSTEM_SL_LOCATION', 'http://example.sl/locations', FALSE, TRUE);
INSERT INTO fhir_system (id, version, created_at, last_updated_at, last_updated_by, name, code, system_uri, description_protected, enabled)
VALUES ('c4e9ac6acc8f4c73aab60fa6775c0ca3', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'Sierra Leone Organization', 'SYSTEM_SL_ORGANIZATION', 'http://example.sl/organizations', FALSE, TRUE);

INSERT INTO fhir_system_code(id, version, created_at, last_updated_at, last_updated_by, code_id, system_id, system_code, system_code_value, display_name)
VALUES ('c513935c9cd24357a67960f0c79bfacb', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', '348d9391c77048538cdff3c5c6830485', 'c4e9ac6acc8f4c73aab60fa6775c0ca3', '982737', 'http://example.sl/organizations|982737', 'Organization 982737');

INSERT INTO fhir_client(id, version, created_at, last_updated_at, last_updated_by, name, code, description, fhir_version, web_hook_authorization_header,
                        dhis_authentication_method, dhis_username, dhis_password, remote_base_url, tolerance_millis, logging, verbose_logging, adapter_base_url, subscription_type, enabled, locked, sort_supported)
VALUES ('73cd99c50ca842ada53b1891fccce08f', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'HAPI FHIR JPA Server', 'DEFAULT_SUBSCRIPTION', 'HAPI FHIR JPA Server.', 'DSTU3',
'Bearer jhsj832jDShf8ehShdu7ejhDhsilwmdsgs', 'BASIC', 'admin', 'district', 'http://localhost:8082/hapifhirjpaserverexample/baseDstu3', 60000, FALSE, FALSE,
'http://localhost:8081', 'REST_HOOK_WITH_JSON_PAYLOAD', TRUE, FALSE, TRUE);
INSERT INTO fhir_client_header (fhir_client_id, name, value, secure)
VALUES ('73cd99c50ca842ada53b1891fccce08f', 'Authorization', 'Bearer jshru38jsHdsdfy38sh38H3d', TRUE);

INSERT INTO fhir_client_resource (id, version, created_at, last_updated_at, last_updated_by, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, virtual)
VALUES ('667bfa41867c479686b6eb9f9ed4dc94', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', '73cd99c50ca842ada53b1891fccce08f', 'PATIENT', '_format=json', 'Subscription for all Patients.', false);
INSERT INTO fhir_client_system (id, version, created_at, last_updated_at, last_updated_by, fhir_client_id, fhir_resource_type, system_id)
VALUES ('ea9804a39e824d0d9cd2e417b32b1c0c', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', '73cd99c50ca842ada53b1891fccce08f', 'ORGANIZATION', 'c4e9ac6acc8f4c73aab60fa6775c0ca3');

INSERT INTO fhir_code_category (id, version, created_at, last_updated_at, last_updated_by, name, code, description)
VALUES ('7090561ef45b411e99c065fa1d145018', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'Vaccine', 'VACCINE', 'Available vaccines.');
INSERT INTO fhir_code(id, version, created_at, last_updated_at, last_updated_by, code_category_id, name, code, description)
VALUES ('f9462e8c653b4c6aa5028470a1ab2187', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', '7090561ef45b411e99c065fa1d145018', 'DTaP', 'VACCINE_20',
'diphtheria, tetanus toxoids and acellular pertussis vaccine');
INSERT INTO fhir_code(id, version, created_at, last_updated_at, last_updated_by, code_category_id, name, code, description)
VALUES ('02422dddb6064bb68d0fcca090182b5d', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', '7090561ef45b411e99c065fa1d145018',
'DTaP, 5 pertussis antigens', 'VACCINE_106', 'diphtheria, tetanus toxoids and acellular pertussis vaccine, 5 pertussis antigens');
INSERT INTO fhir_code(id, version, created_at, last_updated_at, last_updated_by, code_category_id, name, code, description)
VALUES ('71f5536a258745b988ac9aba362a424a', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', '7090561ef45b411e99c065fa1d145018', 'MMR', 'VACCINE_03',
'measles, mumps and rubella virus vaccine');
INSERT INTO fhir_code(id, version, created_at, last_updated_at, last_updated_by, code_category_id, name, code, description)
VALUES ('eac12b34ddeb47afa1de59ee2dac488f', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', '7090561ef45b411e99c065fa1d145018', 'M/R', 'VACCINE_04',
'measles and rubella virus vaccine');
INSERT INTO fhir_code_set(id, version, created_at, last_updated_at, last_updated_by, code_category_id, name, code, description)
VALUES ('bb66ee918e86422cbb005a90ac95a558', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', '7090561ef45b411e99c065fa1d145018', 'All DTP/DTaP', 'ALL_DTP_DTAP', 'All DTP/DTaP vaccines.');
INSERT INTO fhir_code_set_value(id, code_set_id, code_id, enabled)
  SELECT RANDOM_UUID(), 'bb66ee918e86422cbb005a90ac95a558', id, TRUE FROM fhir_code WHERE code IN ('VACCINE_106', 'VACCINE_20');

-- Script that performs the lookup of TEI FHIR Resource from FHIR Patient (the patient itself)
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('8b5ab5f1363d4ccb8e63d6ecf25b3018', 0, 'Patient TEI Lookup', 'PATIENT_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Patient.', 'EVALUATE', 'FHIR_RESOURCE', 'FHIR_PATIENT', NULL);
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('8b5ab5f1363d4ccb8e63d6ecf25b3018', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('8b5ab5f1363d4ccb8e63d6ecf25b3018', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('960d2e6c247948a2b04eb14879e71d15', 0, '8b5ab5f1363d4ccb8e63d6ecf25b3018', 'input', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('960d2e6c247948a2b04eb14879e71d15', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('1b6a2f75cb4a47b18e903dfb4db07d37', 0, '8b5ab5f1363d4ccb8e63d6ecf25b3018',
        'Patient TEI Lookup', 'PATIENT_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Patient.');

INSERT INTO fhir_script (id, version, created_at, last_updated_at, last_updated_by,  name, code, description, script_type, return_type, input_type, output_type)
VALUES ('a250e109a13542b28bdb1c050c1d384c', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'Org Unit Code from Patient Org', 'EXTRACT_FHIR_PATIENT_DHIS_ORG_UNIT_CODE',
'Extracts the organization unit code reference from the business identifier that is specified by the FHIR Organization of the FHIR Patient.',
'EVALUATE', 'ORG_UNIT_REF', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a250e109a13542b28bdb1c050c1d384c', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a250e109a13542b28bdb1c050c1d384c', 'INPUT');
INSERT INTO fhir_script_source (id, version, created_at, last_updated_at, last_updated_by, script_id, source_text, source_type)
VALUES ('7b94febabcf64635929a01311b25d975', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'a250e109a13542b28bdb1c050c1d384c',
'context.createReference(identifierUtils.getReferenceIdentifier(input.managingOrganization, ''ORGANIZATION''), ''CODE'')', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('7b94febabcf64635929a01311b25d975', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, created_at, last_updated_at, last_updated_by, script_id, name, code, description)
VALUES ('25a97bb47b394ed48677db4bcaa28ccf', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'a250e109a13542b28bdb1c050c1d384c', 'Org Unit Code from Patient Org', 'EXTRACT_FHIR_PATIENT_DHIS_ORG_UNIT_CODE',
'Extracts the organization unit code reference from the business identifier that is specified by the FHIR Organization of the FHIR Patient.');

INSERT INTO fhir_script (id, version, created_at, last_updated_at, last_updated_by, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('2263b2969d964698bc1d17930005eef3', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'GEO Location from Patient', 'EXTRACT_FHIR_PATIENT_GEO_LOCATION',
'Extracts the GEO location form FHIR Patient.',
'EVALUATE', 'LOCATION', 'FHIR_PATIENT', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('2263b2969d964698bc1d17930005eef3', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('2263b2969d964698bc1d17930005eef3', 'INPUT');
INSERT INTO fhir_script_source (id, version, created_at, last_updated_at, last_updated_by, script_id, source_text, source_type)
VALUES ('039ac2e650f24e4a9e4adc0515560273', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', '2263b2969d964698bc1d17930005eef3',
'geoUtils.getLocation(addressUtils.getPrimaryAddress(input.address))', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('039ac2e650f24e4a9e4adc0515560273', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, created_at, last_updated_at, last_updated_by, script_id, name, code, description)
VALUES ('ef90531f443848bd83b36370dd65875a', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', '2263b2969d964698bc1d17930005eef3',  'GEO Location from Patient', 'EXTRACT_FHIR_PATIENT_GEO_LOCATION',
'Extracts the GEO location form FHIR Patient.');

INSERT INTO fhir_script (id, version, created_at, last_updated_at, last_updated_by, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('ea8879435e944e319441c7661fe1063e', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'Transforms FHIR Patient to DHIS Person',
'TRANSFORM_FHIR_PATIENT_DHIS_PERSON', 'Transforms FHIR Patient to DHIS Person.', 'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_PATIENT', 'DHIS_TRACKED_ENTITY_INSTANCE');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ea8879435e944e319441c7661fe1063e', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ea8879435e944e319441c7661fe1063e', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('ea8879435e944e319441c7661fe1063e', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, created_at, last_updated_at, last_updated_by, script_id, name, data_type, mandatory, default_value, description, array_value)
VALUES ('0a7c26cb7bd343949d47a610ac231f8a', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'ea8879435e944e319441c7661fe1063e',
'lastNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, 'NAME:Last name',
'The reference of the tracked entity attribute that contains the last name of the Person.', FALSE);
INSERT INTO fhir_script_argument(id, version, created_at, last_updated_at, last_updated_by, script_id, name, data_type, mandatory, default_value, description, array_value)
VALUES ('b41dd571a1294fa6a80735ea5663e8e3', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'ea8879435e944e319441c7661fe1063e',
'firstNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, 'CODE:MMD_PER_NAM',
'The reference of the tracked entity attribute that contains the first name of the Person.', FALSE);
INSERT INTO fhir_script_argument(id, version, created_at, last_updated_at, last_updated_by, script_id, name, data_type, mandatory, default_value, description, array_value)
VALUES ('90b3c11038e44291934ce2569e8af1ba', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'ea8879435e944e319441c7661fe1063e',
'birthDateAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the birth date of the Person.', FALSE);
INSERT INTO fhir_script_source (id, version, created_at, last_updated_at, last_updated_by, script_id, source_text, source_type)
VALUES ('b2cfaf306ede41f2bd6c448e76c429a1', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'ea8879435e944e319441c7661fe1063e',
'output.setValue(args[''lastNameAttribute''], humanNameUtils.getPrimaryName(input.name).family, context.getFhirRequest().getLastUpdated());
output.setValue(args[''firstNameAttribute''], humanNameUtils.getSingleGiven(humanNameUtils.getPrimaryName(input.name)), context.getFhirRequest().getLastUpdated());
output.setOptionalValue(args[''birthDateAttribute''], dateTimeUtils.getPreciseDate(input.birthDateElement), context.getFhirRequest().getLastUpdated());
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('b2cfaf306ede41f2bd6c448e76c429a1', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, created_at, last_updated_at, last_updated_by, script_id, name, code, description)
VALUES ('72451c8f7492470790b8a3e0796de19e', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'ea8879435e944e319441c7661fe1063e',
'Transforms FHIR Patient to DHIS Person', 'TRANSFORM_FHIR_PATIENT_DHIS_PERSON', 'Transforms FHIR Patient to DHIS Person.');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value, enabled)
VALUES ('9b832b2c0a574441841147b5dc65ec91', '72451c8f7492470790b8a3e0796de19e', '90b3c11038e44291934ce2569e8af1ba', 'CODE:MMD_PER_DOB', TRUE);

-- Tracked Entity Person
INSERT INTO fhir_tracked_entity(id, version, created_at, last_updated_at, last_updated_by, name, enabled, description, tracked_entity_ref, tracked_entity_identifier_ref)
VALUES ('4203754d21774a4486aa2de31ee4c8ee', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'Person', TRUE, 'Tracked entity for a patient.', 'NAME:Person', 'CODE:National identifier');

INSERT INTO fhir_rule (id, version, created_at, last_updated_at, last_updated_by, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, applicable_imp_script_id, transform_imp_script_id, contained_allowed)
VALUES ('5f9ebdc9852e4c8387ca795946aabc35', 0, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP(), '2h2maqu827d', 'FHIR Patient to Person', NULL, TRUE, 1, 'PATIENT', 'TRACKED_ENTITY', NULL, '72451c8f7492470790b8a3e0796de19e', FALSE);
INSERT INTO fhir_tracked_entity_rule (id, tracked_entity_id, org_lookup_script_id, loc_lookup_script_id)
VALUES ('5f9ebdc9852e4c8387ca795946aabc35', '4203754d21774a4486aa2de31ee4c8ee', '25a97bb47b394ed48677db4bcaa28ccf', 'ef90531f443848bd83b36370dd65875a');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('8b5ab5f1363d4ccb8e63d6ecf25b3017', 0, 'Observation TEI Lookup', 'OBSERVATION_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Observation.', 'EVALUATE', 'FHIR_RESOURCE', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('8b5ab5f1363d4ccb8e63d6ecf25b3017', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('8b5ab5f1363d4ccb8e63d6ecf25b3017', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('960d2e6c247948a2b04eb14879e71d14', 0, '8b5ab5f1363d4ccb8e63d6ecf25b3017', 'if (context.getFhirRequest().isDhisFhirId())
{
  fhirResource = fhirResourceUtils.createResource(''Patient'');
  fhirResource.setId(input.getSubject().getReferenceElement());
}
else
{
  fhirResource = referenceUtils.getResource(input.subject, ''Patient'');
}
fhirResource', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('960d2e6c247948a2b04eb14879e71d14', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('1b6a2f75cb4a47b18e903dfb4db07d36', 0, '8b5ab5f1363d4ccb8e63d6ecf25b3017',
        'Observation TEI Lookup', 'OBSERVATION_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from FHIR Observation.');

-- Script that extracts GEO location from Observation
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('efb7fa7adf454c6a90960bc38f08c067', 0, 'GEO Location from Observation', 'EXTRACT_FHIR_OBSERVATION_GEO_LOCATION',
'Extracts the GEO location form FHIR Observation.',
'EVALUATE', 'LOCATION', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('efb7fa7adf454c6a90960bc38f08c067', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('efb7fa7adf454c6a90960bc38f08c067', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('2e0565a0ecde4ce2b313b0f786105385', 0, 'efb7fa7adf454c6a90960bc38f08c067',
'null', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('2e0565a0ecde4ce2b313b0f786105385', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('bb07063146b342ec83b200ea219bcf50', 0, 'efb7fa7adf454c6a90960bc38f08c067',  'GEO Location from Observation', 'EXTRACT_FHIR_OBSERVATION_GEO_LOCATION',
'Extracts the GEO location form FHIR Observation.');

-- Script that gets the exact date from FHIR Observation
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('49d3570129794b36a9ba4269c1572cfd', 0, 'Observation Date Lookup', 'OBSERVATION_DATE_LOOKUP', 'Lookup of the exact date of the FHIR Observation.', 'EVALUATE', 'DATE_TIME', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('49d3570129794b36a9ba4269c1572cfd', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('49d3570129794b36a9ba4269c1572cfd', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('630cd8383506410b9b668785682d5e0c', 0, '49d3570129794b36a9ba4269c1572cfd',
'var date = null;
if (input.hasEffectiveDateTimeType())
  date = dateTimeUtils.getPreciseDate(input.getEffectiveDateTimeType());
else if (input.hasEffectivePeriod())
  date = dateTimeUtils.getPreciseDate(input.getEffectivePeriod().hasStart() ? input.getEffectivePeriod().getStartElement() : null);
else if (input.hasIssued())
  date = dateTimeUtils.getPreciseDate(input.getIssuedElement());
date', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('630cd8383506410b9b668785682d5e0c', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a7b604369fa74fe48bf7f5e22123a980', 0, '49d3570129794b36a9ba4269c1572cfd',
'Observation Date Lookup', 'OBSERVATION_DATE_LOOKUP', 'Lookup of the exact date of the FHIR Observation.');

-- Script that gets a null date
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('04d4dc1f7c0f443790eaf432928bfcf2', 0, 'Other Date Lookup', 'OTHER_DATE_LOOKUP', 'Returns date time null.', 'EVALUATE', 'DATE_TIME', 'FHIR_RELATED_PERSON', NULL);
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('04df8d72ace2482f954d424749af5461', 0, '04d4dc1f7c0f443790eaf432928bfcf2', 'null', 'JAVASCRIPT');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('b8625033eede4bf3bd341469d5952a2d', 0, '04d4dc1f7c0f443790eaf432928bfcf2',
'Other Date Lookup', 'OTHER_DATE_LOOKUP', 'Returns date time null.');

-- Script that gets a null organization unit reference
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('b6b7f1eb2eea4a6eb59f042ae3e07556', 0, 'Other Date Lookup', 'OTHER_ORG_UNIT_LOOKUP', 'Returns organization unit null.', 'EVALUATE', 'ORG_UNIT_REF', 'FHIR_RELATED_PERSON', NULL);
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('816a12da1fbb462981bd14764485acfd', 0, 'b6b7f1eb2eea4a6eb59f042ae3e07556', 'null', 'JAVASCRIPT');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('e1aa08bfa91b47f5a0cddae996e225b9', 0, 'b6b7f1eb2eea4a6eb59f042ae3e07556',
'Other organization unit Lookup', 'OTHER_ORG_UNIT_LOOKUP', 'Returns null.');

-- Script that gets a null GEO  reference
INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('fa8a9a4ec9c44c6e91a8b32ef477cf41', 0, 'Other Date Lookup', 'OTHER_GEO_LOOKUP', 'Returns GEO location null.', 'EVALUATE', 'LOCATION', 'FHIR_RELATED_PERSON', NULL);
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('8e96c41de0dd4d6380afe23cc09b6564', 0, 'fa8a9a4ec9c44c6e91a8b32ef477cf41', 'null', 'JAVASCRIPT');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('9da5690f9d5c4e6b974836d292c4f47a', 0, 'fa8a9a4ec9c44c6e91a8b32ef477cf41',
'Other Geo Lookup', 'OTHER_GEO_LOOKUP', 'Returns null.');

INSERT INTO fhir_resource_mapping(id, version, fhir_resource_type, created_at, last_updated_by, last_updated_at, imp_tei_lookup_script_id, imp_enrollment_org_lookup_script_id, imp_event_org_lookup_script_id, imp_enrollment_date_lookup_script_id, imp_event_date_lookup_script_id, imp_enrollment_geo_lookup_script_id, imp_event_geo_lookup_script_id, imp_effective_date_lookup_script_id) VALUES
('f1cbd84fa3db4aa7a9f2a5e547e60bed', 0, 'OBSERVATION', CURRENT_TIMESTAMP(), 'h2maqu827d', CURRENT_TIMESTAMP(), '1b6a2f75cb4a47b18e903dfb4db07d36', '25a97bb47b394ed48677db4bcaa28ccf', '25a97bb47b394ed48677db4bcaa28ccf',
 'a7b604369fa74fe48bf7f5e22123a980', 'a7b604369fa74fe48bf7f5e22123a980', 'bb07063146b342ec83b200ea219bcf50', 'bb07063146b342ec83b200ea219bcf50', 'a7b604369fa74fe48bf7f5e22123a980');

INSERT INTO fhir_code_category (id, version, name, code, description)
VALUES ('1197b27e395643dda75cbfc6808dc49d', 0, 'Vital Sign', 'VITAL_SIGN', 'Vital signs.');
INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('d4aa72e9b57e4d5c8856860ebdf460af', 0, '1197b27e395643dda75cbfc6808dc49d', 'All Birth Weight Observations', 'ALL_OB_BIRTH_WEIGHT', 'All Birth Weight Observations.');
-- Code set with all Body Weight Observations
INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('d37dfecbce884fa49a7844ffe874c140', 0, '1197b27e395643dda75cbfc6808dc49d', 'All Body Weight Observations', 'ALL_OB_BODY_WEIGHT', 'All Body Weight Observations.');

-- Script that sets for a data element the birth weight with a specific weight unit
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('50a60c9bd7f24ceabbc7b633d276a2f7', 0, 'TRANSFORM_FHIR_OB_BIRTH_WEIGHT', 'Transforms FHIR Observation Birth Weight', 'Transforms FHIR Observation Birth Weight to a data element and performs weight unit conversion.',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_OBSERVATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('50a60c9bd7f24ceabbc7b633d276a2f7', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('50a60c9bd7f24ceabbc7b633d276a2f7', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('50a60c9bd7f24ceabbc7b633d276a2f7', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('600187f8eba545999061b6fe8bc1c518', 0, '50a60c9bd7f24ceabbc7b633d276a2f7',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element on which the birth weight must be set.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('ead06d428e4047f58a8974fbcf237b2b', 0, '50a60c9bd7f24ceabbc7b633d276a2f7',
'weightUnit', 'WEIGHT_UNIT', TRUE, 'KILO_GRAM', 'The resulting weight unit in which the value will be set on the data element.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('5e272692021444fea16c0c79f2f61917', 0, '50a60c9bd7f24ceabbc7b633d276a2f7',
'round', 'BOOLEAN', TRUE, 'true', 'Specifies if the resulting value should be rounded.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('05485e11fb1f4fc18518f9322dfb7dbb', 0, '50a60c9bd7f24ceabbc7b633d276a2f7',
'output.setValue(args[''dataElement''], vitalSignUtils.getWeight(input.value, args[''weightUnit''], args[''round'']), null, context.getFhirRequest().getLastUpdated())', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('05485e11fb1f4fc18518f9322dfb7dbb', 'DSTU3');

-- Script that is executed to check if enrollment into Child Programme is applicable
INSERT INTO fhir_script (id,version,name,code,description,script_type,return_type,input_type,output_type)
VALUES ('3ddfc83e065546db81110a3370970125', 0, 'Child Programme Creation Applicable', 'CHILD_PROGRAMME_CREATION_APPLICABLE',
'Checks if the enrollment into child programme is applicable. The enrollment is applicable if the person is younger than a defined amount time.',
'EVALUATE', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('3ddfc83e065546db81110a3370970125', 'TRACKED_ENTITY_INSTANCE');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('3ddfc83e065546db81110a3370970125', 'DATE_TIME');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('b06c9036006b40439c6f57dd7286098b', 0, '3ddfc83e065546db81110a3370970125',
'birthDateAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, FALSE, 'CODE:MMD_PER_DOB', 'The reference of the tracked entity attribute that contains the birth date of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('8cddd857d510472c835ad9454ffe1d39', 0, '3ddfc83e065546db81110a3370970125',
'age', 'INTEGER', TRUE, FALSE, '1', 'The person must be younger than the this amount of time (in specified units).');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('331db7c9edd84ff59e7740c391aa789e', 0, '3ddfc83e065546db81110a3370970125',
'ageUnit', 'DATE_UNIT', TRUE, FALSE, 'YEARS', 'The unit in which the age is specified.');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('1f9dc84d29a544609192eb03cea156d9', 0, '3ddfc83e065546db81110a3370970125', 'dateTimeUtils.isYoungerThan(dateTime, trackedEntityInstance.getValue(args[''birthDateAttribute'']), args[''age''], args[''ageUnit''])', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('1f9dc84d29a544609192eb03cea156d9', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('dfb7f13cae214cfb88156f1416ca8388', 0, '3ddfc83e065546db81110a3370970125',
'Child Programme Creation Applicable', 'CHILD_PROGRAMME_CREATION_APPLICABLE', 'Checks if the enrollment of a person is applicable.');

-- Script that is executed on creation of a program instance of Child Programme
INSERT INTO fhir_script (id,version,name,code,description,script_type,return_type,input_type,output_type)
VALUES ('60e83e185f6640a19c9d1d993e5ccdb3', 0, 'Child Programme Creation', 'CHILD_PROGRAMME_CREATION', 'Child programme creation.', 'EVALUATE', 'BOOLEAN', NULL, 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('60e83e185f6640a19c9d1d993e5ccdb3', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('60e83e185f6640a19c9d1d993e5ccdb3', 'ENROLLMENT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, array_value, default_value, description)
VALUES ('363ed415be4a4311a7bf780a304d6f8c', 0, '60e83e185f6640a19c9d1d993e5ccdb3',
'birthDateAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', TRUE, FALSE, 'CODE:MMD_PER_DOB', 'The reference of the tracked entity attribute that contains the birth date of the Person.');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('f45aed605208430e869ef0b87f5a6321', 0, '60e83e185f6640a19c9d1d993e5ccdb3', 'enrollment.setIncidentDate(trackedEntityInstance.getValue(args[''birthDateAttribute'']))', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('f45aed605208430e869ef0b87f5a6321', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('6836cbd193ba4ab8a4cb69348ad9d099', 0, '60e83e185f6640a19c9d1d993e5ccdb3',
'Child Programme Creation', 'CHILD_PROGRAMME_CREATION', 'Child programme creation.');

-- Tracker Program Child Programme
INSERT INTO fhir_tracker_program (id,version,name,program_ref,tracked_entity_rule_id,enabled,creation_enabled,creation_applicable_script_id,creation_script_id,enrollment_date_is_incident,tracked_entity_fhir_resource_type)
VALUES ('45e61665754d4861891ea2064fc0ae7d', 0, 'Child Programme', 'NAME:Child Programme', '5f9ebdc9852e4c8387ca795946aabc35', TRUE, TRUE,
'dfb7f13cae214cfb88156f1416ca8388', '6836cbd193ba4ab8a4cb69348ad9d099', TRUE, 'PATIENT');
-- Tracker Program Child Programme, Stage Birth
INSERT INTO fhir_tracker_program_stage (id,version,name,program_stage_ref,program_id,enabled,creation_enabled,event_date_is_incident)
VALUES ('4c074c85be494b9d89739e16b9615dad', 0, 'Birth', 'NAME:Birth', '45e61665754d4861891ea2064fc0ae7d', TRUE, TRUE, TRUE);
-- Tracker Program Child Programme, Stage Baby Postnatal
INSERT INTO fhir_tracker_program_stage (id,version,name,program_stage_ref,program_id,enabled,creation_enabled,event_date_is_incident)
VALUES ('526b4e01774747efa25df32ccd739e87', 0, 'Baby Postnatal', 'NAME:Baby Postnatal', '45e61665754d4861891ea2064fc0ae7d', TRUE, TRUE, TRUE);

-- Tracker Program Child Programme, Birth: Weight from Birth Weight
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('518b52d22d1e417186ce1e40ec269a1a', 0, '50a60c9bd7f24ceabbc7b633d276a2f7', 'CP: Birth Weight', 'CP_BIRTH_WEIGHT');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('75c2d252e7c24321b20bba7d5f89af80', '518b52d22d1e417186ce1e40ec269a1a', 'ead06d428e4047f58a8974fbcf237b2b', 'GRAM');

-- Rule Tracker Program Child Programme, Birth: Weight from Birth Weight
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, applicable_code_set_id, transform_imp_script_id)
VALUES ('ff043b6e40c94fa79721ba2239b38360', 0, 'Child Programme: Birth Weight', NULL, TRUE, 0, 'OBSERVATION', 'PROGRAM_STAGE_EVENT', 'd4aa72e9b57e4d5c8856860ebdf460af', '518b52d22d1e417186ce1e40ec269a1a');
INSERT INTO fhir_program_stage_rule (id, program_stage_id,enrollment_creation_enabled,event_creation_enabled)
VALUES ('ff043b6e40c94fa79721ba2239b38360','4c074c85be494b9d89739e16b9615dad',TRUE,TRUE);

INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT '87388c6e449e466f8ef8c2b44557a10f', 0, id, 'CODE:DE_2005736', 'dataElement', true
FROM fhir_rule
WHERE id = 'ff043b6e40c94fa79721ba2239b38360';

-- Tracker Program Child Programme, Baby Postnatal: Infant Weight
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('0104ad19ba8248dcbbd138dd5f0d8a2c', 0, 'f1da6937e2fe47a4b0f38bbff7818ee1', 'CP: Infant Weight', 'CP_BODY_WEIGHT');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('5b729da43f0b49ba9059cc7fc74841d8', '0104ad19ba8248dcbbd138dd5f0d8a2c', '3d15bf81343c45bc9c281e87a8da6fa5', 'GRAM');

-- Rule Tracker Program Child Programme, Baby Postnatal: Infant Weight
INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, applicable_code_set_id, transform_imp_script_id)
VALUES ('a6636c83f23648cdbb2b592147db9a34', 0, 'Child Programme: Infant Weight', NULL, TRUE, 10, 'OBSERVATION', 'PROGRAM_STAGE_EVENT', 'd37dfecbce884fa49a7844ffe874c140', '0104ad19ba8248dcbbd138dd5f0d8a2c');
INSERT INTO fhir_program_stage_rule (id, program_stage_id, before_period_day_type, after_period_day_type, after_period_days,enrollment_creation_enabled,event_creation_enabled)
VALUES ('a6636c83f23648cdbb2b592147db9a34','526b4e01774747efa25df32ccd739e87', 'ORIG_DUE_DATE', 'ORIG_DUE_DATE', 1,TRUE,TRUE);

INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT '9589bff180b94f9dbafb8e679a219bf1', 0, id, 'CODE:DE_2006099', 'dataElement', true
FROM fhir_rule
WHERE id = 'a6636c83f23648cdbb2b592147db9a34';

INSERT INTO fhir_script (id,version,name,code,description,script_type,return_type,input_type,output_type)
VALUES ('20e1ac3ff49c4fa893e2b5e18c5f9665', 0, 'Vital Signs New Program Stage', 'VITAL_SIGN_NEW_PS', 'Decides about creation of a new program stage for vital signs.', 'EVALUATE', 'EVENT_DECISION_TYPE', NULL, 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('20e1ac3ff49c4fa893e2b5e18c5f9665', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('20e1ac3ff49c4fa893e2b5e18c5f9665', 'PROGRAM_STAGE_EVENTS');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('20e1ac3ff49c4fa893e2b5e18c5f9665', 'DATE_TIME');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('09db6430cfb24861befbaf7408e35c2e', 0, '20e1ac3ff49c4fa893e2b5e18c5f9665',
        'programStageUtils.containsEventDay(programStageEvents, dateTime) ? ''CONTINUE'' : ''NEW_EVENT''', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('09db6430cfb24861befbaf7408e35c2e', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('5f1b7d9848ad4df4bdb223e7de8f0fc1', 0, '20e1ac3ff49c4fa893e2b5e18c5f9665',
'Vital Signs New Program Stage', 'VITAL_SIGN_NEW_PS', 'Decides about creation of a new program stage for vital signs.');

INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('c8ca1fbd4d954911b16ea2310af3533f', 0, 'DHIS2 FHIR Adapter Patient Identifier', 'SYSTEM_DHIS2_FHIR_PATIENT_IDENTIFIER', 'http://www.dhis2.org/dhis2fhiradapter/systems/patientidentifier',
        'DHIS2 FHIR Adapter Patient Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('cc46b8acaced4bd2b10403a7d34da438', 0, 'DHIS2 FHIR Adapter Location Identifier', 'SYSTEM_DHIS2_FHIR_LOCATION_IDENTIFIER', 'http://www.dhis2.org/dhis2fhiradapter/systems/locationidentifier',
        'DHIS2 FHIR Adapter Location Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('db955a8aca584263bc56faa99085df93', 0, 'DHIS2 FHIR Adapter Organization Identifier', 'SYSTEM_DHIS2_FHIR_ORGANIZATION_IDENTIFIER', 'http://www.dhis2.org/dhis2fhiradapter/systems/organizationidentifier',
        'DHIS2 FHIR Adapter Organization Identifier.');

INSERT INTO fhir_client (id, version, name, code, description, fhir_version, web_hook_authorization_header, dhis_authentication_method, dhis_username, dhis_password,
remote_base_url, use_remote, tolerance_millis, logging, verbose_logging, adapter_base_url, subscription_type, enabled, locked)
VALUES ('a5a6a64215a24f279cee55a26a86d062', 0, 'FHIR REST Interfaces DSTU3', 'FHIR_RI_DSTU3', 'Used by the FHIR REST Interfaces for DSTU3.', 'DSTU3',
'None 1234', 'BASIC', 'none', 'none', 'http://localhost:8088/fhir', TRUE, 0, FALSE, FALSE,
'http://localhost:8080/fhiradapter', 'REST_HOOK_WITH_JSON_PAYLOAD', TRUE, FALSE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('1de5bc4cb55e4be8b96ada239cfa2cf6', 0, 'a5a6a64215a24f279cee55a26a86d062', 'CONDITION', NULL, 'FHIR Condition Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('be9a7d61d5cd451a89f93e36cf213299', 0, 'a5a6a64215a24f279cee55a26a86d062', 'DIAGNOSTIC_REPORT', NULL, 'FHIR Diagnostic Report Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('edfde6aa8de9440bab7284a0f9be01e2', 0, 'a5a6a64215a24f279cee55a26a86d062', 'ENCOUNTER', NULL, 'FHIR Encounter Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('3e8719c1aef14728b6aab208dea629e2', 0, 'a5a6a64215a24f279cee55a26a86d062', 'IMMUNIZATION', NULL, 'FHIR Immunization Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('8c4dc1b9d1da40698e8cab8409a3daf5', 0, 'a5a6a64215a24f279cee55a26a86d062', 'LOCATION', NULL, 'FHIR Location Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('00716d5d1c1e4ab9b860fcaccd7dad1d', 0, 'a5a6a64215a24f279cee55a26a86d062', 'MEDICATION_REQUEST', NULL, 'FHIR Medication Request Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('fade2919c46f472ab24de6e5bb99d5a6', 0, 'a5a6a64215a24f279cee55a26a86d062', 'OBSERVATION', NULL, 'FHIR Observation Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('7109e75f9ece47739ca56c8774827032', 0, 'a5a6a64215a24f279cee55a26a86d062', 'ORGANIZATION', NULL, 'FHIR Organization Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('080f368d356649ddbedf6bb6026bdd28', 0, 'a5a6a64215a24f279cee55a26a86d062', 'PATIENT', NULL, 'FHIR Patient Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('3165533a0df143db8dd5ac57da8fb516', 0, 'a5a6a64215a24f279cee55a26a86d062', 'RELATED_PERSON', NULL, 'FHIR Related Person Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('c9ae7ad7ed40416080ca2964755b36ea', 0, 'a5a6a64215a24f279cee55a26a86d062', 'PRACTITIONER', NULL, 'FHIR Practitioner Resource.', TRUE);
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('99e36c20e8ef48d18d1fe80572384281', 0, 'a5a6a64215a24f279cee55a26a86d062', 'PATIENT', 'c8ca1fbd4d954911b16ea2310af3533f');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('bdf7381916934177bf6bca7a0c2c4ea9', 0, 'a5a6a64215a24f279cee55a26a86d062', 'LOCATION', 'cc46b8acaced4bd2b10403a7d34da438');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('2e56fc2b576a46caaa6d4485c75c6f5b', 0, 'a5a6a64215a24f279cee55a26a86d062', 'ORGANIZATION', 'db955a8aca584263bc56faa99085df93');

INSERT INTO fhir_client (id, version, name, code, description, fhir_version, web_hook_authorization_header, dhis_authentication_method, dhis_username, dhis_password,
remote_base_url, use_remote, tolerance_millis, logging, verbose_logging, adapter_base_url, subscription_type, enabled, locked)
VALUES ('46f0af46365440b38d4c7a633332c3b3', 0, 'FHIR REST Interfaces R4', 'FHIR_RI_R4', 'Used by the FHIR REST Interfaces for R4.', 'R4',
'None 1234', 'BASIC', 'none', 'none', 'http://localhost:8088/fhir', TRUE, 0, FALSE, FALSE,
'http://localhost:8080/fhiradapter', 'REST_HOOK_WITH_JSON_PAYLOAD', FALSE, TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('fedf63ac80f04f24a8e9595767f0983a', 0, '46f0af46365440b38d4c7a633332c3b3', 'CONDITION', NULL, 'FHIR Condition Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('bf745a0b81a446299434c5715605d8e4', 0, '46f0af46365440b38d4c7a633332c3b3', 'DIAGNOSTIC_REPORT', NULL, 'FHIR Diagnostic Report Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('ef22502237624480a2ee702fa47283bc', 0, '46f0af46365440b38d4c7a633332c3b3', 'ENCOUNTER', NULL, 'FHIR Encounter Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('3ac34328e80e465b893bdf5afe7aa527', 0, '46f0af46365440b38d4c7a633332c3b3', 'IMMUNIZATION', NULL, 'FHIR Immunization Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('2dc28bd3802d4cc18352e3b9277fd280', 0, '46f0af46365440b38d4c7a633332c3b3', 'LOCATION', NULL, 'FHIR Location Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('0e89f64e04af480e9c41e8423eb5c602', 0, '46f0af46365440b38d4c7a633332c3b3', 'MEDICATION_REQUEST', NULL, 'FHIR Medication Request Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('b8eccfd34ded45d7ab84dc8467541902', 0, '46f0af46365440b38d4c7a633332c3b3', 'OBSERVATION', NULL, 'FHIR Observation Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('f3491bb692794fb7a27560c217f88a6a', 0, '46f0af46365440b38d4c7a633332c3b3', 'ORGANIZATION', NULL, 'FHIR Organization Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('6236c52d99614b9988f5d6e692021a7f', 0, '46f0af46365440b38d4c7a633332c3b3', 'PATIENT', NULL, 'FHIR Patient Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('c8d4991fc1004b9591c47b975ac636fd', 0, '46f0af46365440b38d4c7a633332c3b3', 'RELATED_PERSON', NULL, 'FHIR Related Person Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('26526d56224e46ecbc04827711f2b345', 0, '46f0af46365440b38d4c7a633332c3b3', 'PRACTITIONER', NULL, 'FHIR Practitioner Resource.', TRUE);
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('070a6e464dfb452e90c5475021532dc0', 0, '46f0af46365440b38d4c7a633332c3b3', 'PATIENT', 'c8ca1fbd4d954911b16ea2310af3533f');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('47fd1b287558429b85abc5f5fcc3d00f', 0, '46f0af46365440b38d4c7a633332c3b3', 'LOCATION', 'cc46b8acaced4bd2b10403a7d34da438');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('e2cdcfd609f044fe9dd2c3e97540ea22', 0, '46f0af46365440b38d4c7a633332c3b3', 'ORGANIZATION', 'db955a8aca584263bc56faa99085df93');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('655f55b538264fe7b38e1e29bcad09ec', 0, 'Returns FHIR Location Identifier for DHIS Org Unit', 'DHIS_ORG_UNIT_IDENTIFIER_LOC',
        'Returns the FHIR Location Identifier for the DHIS Org Unit.', 'EVALUATE', 'STRING', NULL, NULL);
UPDATE fhir_script SET base_script_id = (SELECT id FROM fhir_script WHERE id = 'a250e109a13542b28bdb1c050c1d384c')
WHERE id='655f55b538264fe7b38e1e29bcad09ec';
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('655f55b538264fe7b38e1e29bcad09ec', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('655f55b538264fe7b38e1e29bcad09ec', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('1a2bf7fc290841488a535edde593a4e9', 0, '655f55b538264fe7b38e1e29bcad09ec',
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
VALUES ('1a2bf7fc290841488a535edde593a4e9', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('5090c485a2254c2ea8f695fa74ce1618', 0, '655f55b538264fe7b38e1e29bcad09ec',
        'Returns FHIR Location Identifier for DHIS Org Unit', 'DHIS_ORG_UNIT_IDENTIFIER_LOC',
        'Returns FHIR Location Identifier for the DHIS Org Unit.');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('7c350b43377942d6a9c974e567f6c066', 0, 'Returns FHIR Identifier for DHIS Org Unit', 'DHIS_ORG_UNIT_IDENTIFIER',
        'Returns the FHIR Identifier for the DHIS Org Unit.', 'EVALUATE', 'STRING', NULL, NULL);
UPDATE fhir_script SET base_script_id = (SELECT id FROM fhir_script WHERE id = 'a250e109a13542b28bdb1c050c1d384c')
WHERE id='7c350b43377942d6a9c974e567f6c066';
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('7c350b43377942d6a9c974e567f6c066', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('7c350b43377942d6a9c974e567f6c066', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('eca2e74dac8049bdbebf6b9129557567', 0, '7c350b43377942d6a9c974e567f6c066',
'var code = null;
if ((input.getCode() != null) && !input.getCode().isEmpty())
{
  code = codeUtils.getByMappedCode(input.getCode(), ''ORGANIZATION'');
  if ((code == null) && args[''useIdentifierCode''])
  {
    code = codeUtils.getCodeWithoutPrefix(input.getCode(), ''ORGANIZATION'');
  }
}
code', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('eca2e74dac8049bdbebf6b9129557567', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('66d12e44471c4318827a0b397f694b6a', 0, '7c350b43377942d6a9c974e567f6c066',
        'Returns FHIR Identifier for DHIS Org Unit', 'DHIS_ORG_UNIT_IDENTIFIER',
        'Returns FHIR Identifier for the DHIS Org Unit.');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('b3568beb5a5d40599b4e3afa0157adbc', 0, 'Transforms DHIS Org Unit to FHIR Location', 'TRANSFORM_DHIS_ORG_UNIT_FHIR_LOC',
        'Transforms DHIS Organization Unit to FHIR Organization.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_ORGANIZATION_UNIT', 'FHIR_LOCATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('b3568beb5a5d40599b4e3afa0157adbc', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('b3568beb5a5d40599b4e3afa0157adbc', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('4bb9745e43c0455fbeee98e95c83df3d', 0, 'b3568beb5a5d40599b4e3afa0157adbc',
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
  output.getPhysicalType().addCoding().setSystem(''http://hl7.org/fhir/locationphysicaltype'').setCode(''si'').setDisplay(''Site'');
}
if (input.getLevel() < args[''facilityLevel''])
{
  output.getPhysicalType().addCoding().setSystem(''http://hl7.org/fhir/locationphysicaltype'').setCode(''jdn'').setDisplay(''Jurisdiction'');
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
VALUES ('4bb9745e43c0455fbeee98e95c83df3d', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('b918b4cd67fc4d4d9b7491f98730acd7', 0, 'b3568beb5a5d40599b4e3afa0157adbc',
        'Transforms DHIS Org Unit to FHIR Location', 'TRANSFORM_DHIS_ORG_UNIT_FHIR_LOC',
        'Transforms DHIS Organization Unit to FHIR Location.');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('53fdf1da61454e259d25fc41b9baf09f', 0, 'Transforms DHIS Org Unit to FHIR Organization', 'TRANSFORM_DHIS_ORG_UNIT_FHIR_ORG',
        'Transforms DHIS Organization Unit to FHIR Organization.', 'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_ORGANIZATION_UNIT', 'FHIR_ORGANIZATION');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('53fdf1da61454e259d25fc41b9baf09f', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('53fdf1da61454e259d25fc41b9baf09f', 'INPUT');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('50544af8cf524e1e9a5dd44c736bc8d8', 0, '53fdf1da61454e259d25fc41b9baf09f',
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
  output.addType().addCoding().setSystem(''http://hl7.org/fhir/organizationtype'').setCode(''prov'');
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
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('50544af8cf524e1e9a5dd44c736bc8d8', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('50544af8cf524e1e9a5dd44c736bc8d8', 0, '53fdf1da61454e259d25fc41b9baf09f',
        'Transforms DHIS Org Unit to FHIR Organization', 'TRANSFORM_DHIS_ORG_UNIT_FHIR_ORG',
        'Transforms DHIS Organization Unit to FHIR Organization.');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('f8faecad964f402c8cb4a8271085528d', 0, 'Returns if DHIS Org Unit is applicable for export', 'EXP_DHIS_ORG_UNIT_APPLICABLE',
        'Returns if the DHIS Org Unit is applicable for export.', 'EVALUATE', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('f8faecad964f402c8cb4a8271085528d', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable)
VALUES ('f8faecad964f402c8cb4a8271085528d', 'INPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('6aaa5d0b5dd1423fbb3fdffc9f42f03f', 0, 'f8faecad964f402c8cb4a8271085528d',
'facilityLevel', 'INTEGER', TRUE, '4', 'Specifies the organization unit level in  which facilities are located.');
INSERT INTO fhir_script_source(id, version, script_id, source_text, source_type)
VALUES ('ca71ef86a93e47d6ab2d645a977165a9', 0, 'f8faecad964f402c8cb4a8271085528d',
'input.getLevel() >= args[''facilityLevel'']', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('ca71ef86a93e47d6ab2d645a977165a9', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('a84850454d624b3a9cfd5c36760b8d45', 0, 'f8faecad964f402c8cb4a8271085528d',
        'Returns if DHIS Org Unit is applicable for export', 'EXP_DHIS_ORG_UNIT_APPLICABLE',
        'Returns if the DHIS Org Unit is applicable for export.');

INSERT INTO fhir_rule (id, version, name, description, enabled, evaluation_order, fhir_resource_type, dhis_resource_type, imp_enabled, applicable_exp_script_id, transform_exp_script_id)
VALUES ('d0e1472a05e647c9b36bff1f06fec352', 0, 'DHIS Organization Unit to FHIR Organization', NULL, TRUE, 0, 'ORGANIZATION', 'ORGANIZATION_UNIT', FALSE, 'a84850454d624b3a9cfd5c36760b8d45', '50544af8cf524e1e9a5dd44c736bc8d8');
INSERT INTO fhir_organization_unit_rule(id, identifier_lookup_script_id) VALUES ('d0e1472a05e647c9b36bff1f06fec352', '66d12e44471c4318827a0b397f694b6a');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type)
VALUES ('cf3072ec06ad4d62a8a075ad2ab330ba', 0, 'SEARCH_FILTER_LOCATION', 'Prepares Location Search Filter', 'Prepares Location Search Filter.', 'SEARCH_FILTER', 'BOOLEAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('cf3072ec06ad4d62a8a075ad2ab330ba', 'SEARCH_FILTER');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('10200cc2f0604aa8a9070d12b199ff3b', 0, 'cf3072ec06ad4d62a8a075ad2ab330ba',
'searchFilter.add(''name'', ''string'', ''name'');
searchFilter.addReference(''partof'', ''location'', ''organizationunit'', ''parent'');
searchFilter.addToken(''status'', ''active'', ''closedDate'', ''!null'', null);
searchFilter.addToken(''status'', ''inactive'', ''closedDate'', ''null'', null);
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('10200cc2f0604aa8a9070d12b199ff3b', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('10200cc2f0604aa8a9070d12b199ff3b', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('27f50aeb0f564256ae166118e931524b', 0, 'cf3072ec06ad4d62a8a075ad2ab330ba', 'Prepares Location Search Filter', 'SEARCH_FILTER_LOCATION');
UPDATE fhir_rule SET filter_script_id='27f50aeb0f564256ae166118e931524b' WHERE id='b9546b024adc4868a4cdd5d7789f0df0';

INSERT INTO fhir_code(id, version, code_category_id, code, name) VALUES ('d0a07a16547e4b5982697601e0dd68fd', 0, '8673a315dd274e4cbb8b1212808d4ca1', 'TEST_ORG_1', 'Test Org 1');
INSERT INTO fhir_code(id, version, code_category_id, code, name) VALUES ('d2053c951e8a4a358eb4baeb384ae8be', 0, '8673a315dd274e4cbb8b1212808d4ca1', 'TEST_ORG_2', 'Test Org 2');

INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code, system_code_value, display_name)
VALUES ( RANDOM_UUID(), 0, 'd0a07a16547e4b5982697601e0dd68fd', 'c4e9ac6acc8f4c73aab60fa6775c0ca3', '4711', 'http://example.sl/organizations|4711', 'My Org 1 1' );
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code, system_code_value, display_name)
VALUES ( RANDOM_UUID(), 0, 'd0a07a16547e4b5982697601e0dd68fd', 'db955a8aca584263bc56faa99085df93', '1234', 'http://www.dhis2.org/dhis2fhiradapter/systems/organizationidentifier|1234', 'My Org 1 2' );

INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code, system_code_value, display_name)
VALUES ( RANDOM_UUID(), 0, 'd2053c951e8a4a358eb4baeb384ae8be', 'c4e9ac6acc8f4c73aab60fa6775c0ca3', '4712', 'http://example.sl/organizations|4712', 'My Org 2 1' );
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code, system_code_value, display_name)
VALUES ( RANDOM_UUID(), 0, 'd2053c951e8a4a358eb4baeb384ae8be', 'db955a8aca584263bc56faa99085df93', '5678', 'http://www.dhis2.org/dhis2fhiradapter/systems/organizationidentifier|5678', 'My Org 2 2' );

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('41d3bd48578847618274f8327f15fcbe', 0, 'Subject TEI Lookup', 'SUBJECT_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from subject in FHIR Resource.', 'EVALUATE', 'FHIR_RESOURCE', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('41d3bd48578847618274f8327f15fcbe', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('41d3bd48578847618274f8327f15fcbe', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('67249e7b4ba7466ca770a78923fbf1c3', 0, '41d3bd48578847618274f8327f15fcbe', 'referenceUtils.getResource(input.subject, ''PATIENT'')', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('67249e7b4ba7466ca770a78923fbf1c3', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('67249e7b4ba7466ca770a78923fbf1c3', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('762b4137a98b4b10a0f5629d93e23461', 0, '41d3bd48578847618274f8327f15fcbe',
'Subject TEI Lookup', 'SUBJECT_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from subject in FHIR Resource.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type)
VALUES ('f638ba4c8b5311e989e4e70cb21cbc8c', 0, 'CARE_PLAN_DATE_LOOKUP', 'Care Plan Date Lookup',
'Lookup of the exact date of the FHIR Care Plan.', 'EVALUATE', 'DATE_TIME', 'FHIR_CARE_PLAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f638ba4c8b5311e989e4e70cb21cbc8c', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('f638ba4c8b5311e989e4e70cb21cbc8c', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('224697268b5411e9b95a93563f739422', 0, 'f638ba4c8b5311e989e4e70cb21cbc8c',
'input.getPeriod().getStart()', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('224697268b5411e9b95a93563f739422', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('0f79f2c88b5411e981f7ebd621029975', 0, 'f638ba4c8b5311e989e4e70cb21cbc8c', 'Care Plan TEI Lookup', 'CARE_PLAN_DATE_LOOKUP',
        'Lookup of the exact date of the FHIR Care Plan');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type)
VALUES ('44d6314675004db7b8bc666c4898523b', 0, 'CARE_PLAN_PROGRAM_REF_LOOKUP', 'Care Plan Tracker Program Lookup',
'Lookup of the Tracker Program of the FHIR Care Plan.', 'EVALUATE', 'PROGRAM_REF', 'FHIR_CARE_PLAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('44d6314675004db7b8bc666c4898523b', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('44d6314675004db7b8bc666c4898523b', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('e13eeaef7a6d4083a233190bc8c1c975', 0, '44d6314675004db7b8bc666c4898523b',
'var programRef = null; if (!input.getInstantiatesUri().isEmpty()) programRef = context.createReference(input.getInstantiatesUri().get(0).getValue(), ''id''); programRef', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('e13eeaef7a6d4083a233190bc8c1c975', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('79d297065c1c47c582456069036072f8', 0, '44d6314675004db7b8bc666c4898523b', 'Care Plan Tracker Program Lookup', 'CARE_PLAN_PROGRAM_REF_LOOKUP',
        'Lookup of the Tracker Program of the FHIR Care Plan.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('e885f646626940c5a8126c2aa081e239', 0, 'DEFAULT_CARE_PLAN_F2D', 'Default FHIR Care Plan to DHIS2 Enrollment Transformation',
'Transforms FHIR Care Plan to DHIS2 Enrollment.', 'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_CARE_PLAN', 'DHIS_ENROLLMENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('e885f646626940c5a8126c2aa081e239', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('e885f646626940c5a8126c2aa081e239', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('e885f646626940c5a8126c2aa081e239', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('383a5fd3489041b593f36e00c14c2d43', 0, 'e885f646626940c5a8126c2aa081e239',
'function getOutputStatus(input)
{
  var outputStatus = ''CANCELLED'';
  var inputStatus = input.getStatus() == null ? null : input.getStatus().toCode();
  if (inputStatus === ''draft'')
  {
    outputStatus = ''ACTIVE'';
  }
  else if (inputStatus === ''active'')
  {
    outputStatus = ''ACTIVE'';
  }
  else if (inputStatus === ''on-hold'')
  {
    outputStatus = ''ACTIVE'';
  }
  else if (inputStatus === ''revoked'')
  {
    outputStatus = ''CANCELLED'';
  }
  else if (inputStatus === ''completed'')
  {
    outputStatus = ''COMPLETED'';
  }
  else if (inputStatus === ''entered-in-error'')
  {
    outputStatus = ''CANCELLED'';
  }
  return outputStatus;
}
output.setStatus(getOutputStatus(input));
output.setIncidentDate(input.created);
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('383a5fd3489041b593f36e00c14c2d43', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('aa956fdb01074cb7b5c1ab6567162d9d', 0, 'e885f646626940c5a8126c2aa081e239', 'Default FHIR Care Plan to DHIS2 Enrollment Transformation', 'DEFAULT_CARE_PLAN_F2D',
        'Transforms FHIR Care Plan to DHIS2 Enrollment.');

INSERT INTO fhir_resource_mapping(id, version, fhir_resource_type, tracked_entity_fhir_resource_type, imp_tei_lookup_script_id, imp_enrollment_org_lookup_script_id, imp_enrollment_date_lookup_script_id)
VALUES('b6650f008462419bb9926775ca0a26cb', 0, 'CARE_PLAN', 'PATIENT', '762b4137a98b4b10a0f5629d93e23461', '25a97bb47b394ed48677db4bcaa28ccf', '0f79f2c88b5411e981f7ebd621029975');

INSERT INTO fhir_rule(id, version, fhir_resource_type, dhis_resource_type, name, description, enabled, imp_enabled, exp_enabled, contained_allowed, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled, grouping, evaluation_order, transform_imp_script_id)
VALUES('c4e17e7d880e45b59bc5568da8c79742', 0, 'CARE_PLAN', 'ENROLLMENT', 'Default FHIR Care Plan to DHIS2 Enrollment', 'Default rule that transforms a FHIR Care Plan to a DHIS2 Enrollment.',
true, true, false, false, true, true, true, false, -2147483648, 'aa956fdb01074cb7b5c1ab6567162d9d');
INSERT INTO fhir_enrollment_rule(id, program_ref_lookup_script_id) VALUES ('c4e17e7d880e45b59bc5568da8c79742', '79d297065c1c47c582456069036072f8');

UPDATE fhir_rule SET evaluation_order = 100 WHERE fhir_resource_type = 'PATIENT' AND evaluation_order = 0;

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type)
VALUES ('152434b081db49bba3c53c09caf29208', 0, 'QR_PROGRAM_STAGE_REF_LOOKUP', 'Questionnaire Response Tracker Program Stage Lookup',
'Lookup of the Tracker Program Stage of the FHIR QuestionnaireResponse.', 'EVALUATE', 'PROGRAM_STAGE_REF', 'FHIR_QUESTIONNAIRE_RESPONSE');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('152434b081db49bba3c53c09caf29208', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('152434b081db49bba3c53c09caf29208', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('eb2ef8ad93a042d5b8da2d2b0d3c6115', 0, '152434b081db49bba3c53c09caf29208',
'var programStageRef = null; if (input.hasQuestionnaire()) programStageRef = context.createReference(input.getQuestionnaire(), ''id''); programStageRef', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('eb2ef8ad93a042d5b8da2d2b0d3c6115', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('385e52d28674403db42586b5d4e9faf0', 0, '152434b081db49bba3c53c09caf29208', 'Questionnaire Response Tracker Program Stage Lookup',
        'QR_PROGRAM_STAGE_REF_LOOKUP', 'Lookup of the Tracker Program Stage of the FHIR QuestionnaireResponse.');

INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('a55e8da7d71948ddaf74d5f6919ce479', 0, '46f0af46365440b38d4c7a633332c3b3', 'PLAN_DEFINITION', NULL, 'FHIR Plan Definition Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('70a57cdf00af4428b7b3114b408a736b', 0, '46f0af46365440b38d4c7a633332c3b3', 'QUESTIONNAIRE', NULL, 'FHIR Questionnaire Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('edea9a1e83c04ffe801ded5da74d50de', 0, '46f0af46365440b38d4c7a633332c3b3', 'CARE_PLAN', NULL, 'FHIR Care Plan Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('fdc10514ab75468a88136ea41b7c671d', 0, '46f0af46365440b38d4c7a633332c3b3', 'QUESTIONNAIRE_RESPONSE', NULL, 'FHIR Questionnaire Response Resource.', TRUE);

INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('f79b98922b4348af9a823ba03642bac4', 0, 'DHIS2 FHIR Adapter Plan Definition Identifier', 'SYSTEM_DHIS2_FHIR_PLAN_DEFINITION_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/plan-definition-identifier',
        'DHIS2 FHIR Adapter Plan Definition Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('296576d031b544278bf07cc168fb3a82', 0, 'DHIS2 FHIR Adapter Questionnaire Identifier', 'SYSTEM_DHIS2_FHIR_QUESTIONNAIRE_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/questionnaire-identifier',
        'DHIS2 FHIR Adapter Questionnaire Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('9c5584f08b0644eea18f6addf938969f', 0, 'DHIS2 FHIR Adapter Care Plan Identifier', 'SYSTEM_DHIS2_FHIR_CARE_PLAN_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/care-plan-identifier',
        'DHIS2 FHIR Adapter Care Plan Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('5bdff628d94f43dc83678c84fe48bdd9', 0, 'DHIS2 FHIR Adapter Questionnaire Response Identifier', 'SYSTEM_DHIS2_FHIR_QUESTIONNAIRE_R_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/questionnaire-response-identifier',
        'DHIS2 FHIR Adapter Questionnaire Response Identifier.');

INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('69dfbae158734a94862aba7344c383d7', 0, '46f0af46365440b38d4c7a633332c3b3', 'PLAN_DEFINITION', 'f79b98922b4348af9a823ba03642bac4');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('7af9d58d0393411eaa7fe5891418f9f2', 0, '46f0af46365440b38d4c7a633332c3b3', 'QUESTIONNAIRE', '296576d031b544278bf07cc168fb3a82');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('0340bf441eab4c2a98a2aaf7ba35855c', 0, '46f0af46365440b38d4c7a633332c3b3', 'CARE_PLAN', '9c5584f08b0644eea18f6addf938969f');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('789a91bd6bcb4f8496d0c6829b5fdeaa', 0, '46f0af46365440b38d4c7a633332c3b3', 'QUESTIONNAIRE_RESPONSE', '5bdff628d94f43dc83678c84fe48bdd9');

INSERT INTO fhir_rule(id, version, fhir_resource_type, dhis_resource_type, name, description, enabled, imp_enabled, exp_enabled, contained_allowed, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled, grouping, evaluation_order, simple_fhir_id)
VALUES('4a9ac195858b455eb34dc560e1855787', 0, 'PLAN_DEFINITION', 'PROGRAM_METADATA', 'Default DHIS2 Program Metadata to FHIR Plan Definition', 'Default rule that transforms a DHIS2 Program Metadata to a FHIR Care Plan.',
true, false, true, false, true, true, true, false, -2147483648, TRUE);
INSERT INTO fhir_program_metadata_rule(id) VALUES ('4a9ac195858b455eb34dc560e1855787');

INSERT INTO fhir_rule(id, version, fhir_resource_type, dhis_resource_type, name, description, enabled, imp_enabled, exp_enabled, contained_allowed, fhir_create_enabled, fhir_update_enabled, fhir_delete_enabled, grouping, evaluation_order, simple_fhir_id)
VALUES('1f97f85ae4ea46f1bcde77ea178226f4', 0, 'QUESTIONNAIRE', 'PROGRAM_STAGE_METADATA', 'Default DHIS2 Program Stage Metadata to FHIR Questionnaire', 'Default rule that transforms a DHIS2 Program Stage Metadata to a FHIR Questionnaire.',
true, false, true, false, true, true, true, false, -2147483648, TRUE);
INSERT INTO fhir_program_stage_metadata_rule(id) VALUES ('1f97f85ae4ea46f1bcde77ea178226f4');
