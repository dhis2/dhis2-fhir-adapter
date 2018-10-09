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

-- Definition of HAPI FHIR JPA Server on which a subscription exists
INSERT INTO fhir_remote_subscription (id, version, name, code, description, fhir_version, web_hook_authorization_header, dhis_authorization_header, remote_base_url, tolerance_minutes, logging, verbose_logging)
VALUES ('73cd99c5-0ca8-42ad-a53b-1891fccce08f', 0, 'HAPI FHIR JPA Server', 'SAMPLE', 'HAPI FHIR JPA Server with sample data.', 'DSTU3',
'', 'Basic YWRtaW46ZGlzdHJpY3Q=', 'http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3', 1, TRUE, TRUE);
INSERT INTO fhir_remote_subscription_header (remote_subscription_id, name, value, secured)
VALUES ('73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'Authorization', 'Bearer jshru38jsHdsdfy38sh38H3d', TRUE);

-- Definition of subscribed resources on HAPI FHIR JPA Server
INSERT INTO fhir_remote_subscription_resource (id, version, remote_subscription_id, fhir_resource_type, fhir_criteria_parameters, description)
VALUES ('667bfa41-867c-4796-86b6-eb9f9ed4dc94', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'PATIENT', '_format=json', 'Subscription for all Patients.');
INSERT INTO fhir_remote_subscription_resource (id, version, remote_subscription_id, fhir_resource_type, fhir_criteria_parameters, description)
VALUES ('a756ef2a-1bf4-43f4-a991-fbb48ad358ac', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'IMMUNIZATION', '_format=json', 'Subscription for all Immunizations.');
INSERT INTO fhir_remote_subscription_resource (id, version, remote_subscription_id, fhir_resource_type, fhir_criteria_parameters, description)
VALUES ('b32b4098-f8e1-426a-8dad-c5c4d8e0fab6', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'OBSERVATION', '_format=json', 'Subscription for all Observations.');

-- Definition of system URIs that are used for unique business identifiers for FHIR resources.
INSERT INTO fhir_system (id, version, name, code, system_uri)
VALUES ('c4e9ac6a-cc8f-4c73-aab6-0fa6775c0ca3', 0, 'Philippines Organization', 'SYSTEM_PH_ORGANIZATION', 'http://example.ph/organizations');
INSERT INTO fhir_system (id, version, name, code, system_uri)
VALUES ('ff842c76-a529-4563-972d-216b887a3573', 0, 'Philippines Patient', 'SYSTEM_PH_PATIENT', 'http://example.ph/national-patient-id');

-- Assignment of system URIs of unique business identifiers for FHIR resources to the subscription.
INSERT INTO fhir_remote_subscription_system (id, version, remote_subscription_id, fhir_resource_type, system_id)
VALUES ('ea9804a3-9e82-4d0d-9cd2-e417b32b1c0c', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'ORGANIZATION', 'c4e9ac6a-cc8f-4c73-aab6-0fa6775c0ca3');
INSERT INTO fhir_remote_subscription_system (id, version, remote_subscription_id, fhir_resource_type, system_id)
VALUES ('ef7d37ae-6a02-46de-bf15-3dc522a464ed', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'PATIENT', 'ff842c76-a529-4563-972d-216b887a3573');

-- Executable script arguments that defines all attributes of tracked entity type Person
INSERT INTO fhir_executable_script_argument(id, version, executable_script_id, script_argument_id, override_value)
VALUES ('9b832b2c-0a57-4441-8411-47b5dc65ec91', 0, '72451c8f-7492-4707-90b8-a3e0796de19e', '90b3c110-38e4-4291-934c-e2569e8af1ba', 'NAME:Birth date');
INSERT INTO fhir_executable_script_argument(id, version, executable_script_id, script_argument_id, override_value)
VALUES ('5ce705ce-415c-4fb3-baa7-d3ae67823ac9', 0, '72451c8f-7492-4707-90b8-a3e0796de19e', '8e3efdc7-6ce4-4899-bb20-faed7d5e3279', 'NAME:Gender');
INSERT INTO fhir_executable_script_argument(id, version, executable_script_id, script_argument_id, override_value)
VALUES ('6f35479f-d594-4286-9269-1fdfc5dcc2cd', 0, '72451c8f-7492-4707-90b8-a3e0796de19e', '40a28a9c-82e3-46e8-9eb9-44aaf2f5eacc', 'NAME:Address line');
INSERT INTO fhir_executable_script_argument(id, version, executable_script_id, script_argument_id, override_value)
VALUES ('871dde31-8da8-4345-b38a-e065236a7ffa', 0, '72451c8f-7492-4707-90b8-a3e0796de19e', 'ae13ceca-86d7-4f60-8d54-25587d53a5bd', 'NAME:City');
INSERT INTO fhir_executable_script_argument(id, version, executable_script_id, script_argument_id, override_value)
VALUES ('b27348e5-7eff-461f-9516-7ab72289a94d', 0, '72451c8f-7492-4707-90b8-a3e0796de19e', '6fb6bfe4-5b44-42a1-812f-be1dc8413d6e', 'NAME:State of country');
INSERT INTO fhir_executable_script_argument(id, version, executable_script_id, script_argument_id, override_value)
VALUES ('5aedebb4-b62b-4b4d-b47e-3b067ed74db2', 0, '72451c8f-7492-4707-90b8-a3e0796de19e', 'a77ef245-e65e-4a87-9c96-5047911f9830', 'NAME:Country');
