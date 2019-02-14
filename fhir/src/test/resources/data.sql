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
VALUES ('960d2e6c247948a2b04eb14879e71d14', 0, '8b5ab5f1363d4ccb8e63d6ecf25b3017', 'referenceUtils.getResource(input.subject, ''PATIENT'')', 'JAVASCRIPT');
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

