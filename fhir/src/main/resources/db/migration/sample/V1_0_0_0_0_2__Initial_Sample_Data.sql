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
'Bearer jhsj832jDShf8ehShdu7ejhDhsilwmdsgs', 'Basic YWRtaW46ZGlzdHJpY3Q=', 'http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3', 1, TRUE, TRUE);
INSERT INTO fhir_remote_subscription_header (remote_subscription_id, name, value, secured)
VALUES ('73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'Authorization', 'Bearer jshru38jsHdsdfy38sh38H3d', TRUE);

-- Definition of subscribed resources on HAPI FHIR JPA Server
INSERT INTO fhir_remote_subscription_resource (id, version, remote_subscription_id, fhir_resource_type, fhir_criteria_parameters, description)
VALUES ('667bfa41-867c-4796-86b6-eb9f9ed4dc94', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'PATIENT', '_format=json', 'Subscription for all Patients.');
INSERT INTO fhir_remote_subscription_resource (id, version, remote_subscription_id, fhir_resource_type, fhir_criteria_parameters, description)
VALUES ('a756ef2a-1bf4-43f4-a991-fbb48ad358ac', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'IMMUNIZATION', '_format=json', 'Subscription for all Immunizations.');
INSERT INTO fhir_remote_subscription_resource (id, version, remote_subscription_id, fhir_resource_type, fhir_criteria_parameters, description)
VALUES ('b32b4098-f8e1-426a-8dad-c5c4d8e0fab6', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'OBSERVATION', '_format=json', 'Subscription for all Observations.');

-- Definition of systemAuthentication URIs that are used for unique business identifiers for FHIR resources.
INSERT INTO fhir_system (id, version, name, code, system_uri)
VALUES ('2dd51309-3319-40d2-9a1f-be2a102df4a7', 0, 'Sierra Leone Location', 'SYSTEM_SL_LOCATION', 'http://example.sl/locations');
INSERT INTO fhir_system (id, version, name, code, system_uri)
VALUES ('c4e9ac6a-cc8f-4c73-aab6-0fa6775c0ca3', 0, 'Sierra Leone Organization', 'SYSTEM_SL_ORGANIZATION', 'http://example.sl/organizations');
INSERT INTO fhir_system (id, version, name, code, system_uri)
VALUES ('ff842c76-a529-4563-972d-216b887a3573', 0, 'Sierra Leone Patient', 'SYSTEM_SL_PATIENT', 'http://example.sl/national-patient-id');

-- Assignment of systemAuthentication URIs of unique business identifiers for FHIR resources to the subscription.
INSERT INTO fhir_remote_subscription_system (id, version, remote_subscription_id, fhir_resource_type, system_id)
VALUES ('ea9804a3-9e82-4d0d-9cd2-e417b32b1c0c', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'ORGANIZATION', 'c4e9ac6a-cc8f-4c73-aab6-0fa6775c0ca3');
INSERT INTO fhir_remote_subscription_system (id, version, remote_subscription_id, fhir_resource_type, system_id)
VALUES ('ef7d37ae-6a02-46de-bf15-3dc522a464ed', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'PATIENT', 'ff842c76-a529-4563-972d-216b887a3573');
