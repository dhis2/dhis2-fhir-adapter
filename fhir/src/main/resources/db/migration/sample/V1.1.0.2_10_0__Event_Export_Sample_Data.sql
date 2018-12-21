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

UPDATE fhir_system SET fhir_display_name='National Location ID' WHERE system_uri='http://example.sl/locations';
UPDATE fhir_system SET fhir_display_name='National Organization ID' WHERE system_uri='http://example.sl/organizations';
UPDATE fhir_system SET fhir_display_name='National Patient ID' WHERE system_uri='http://example.sl/patients';

UPDATE fhir_tracker_program_stage SET exp_enabled = true;
UPDATE fhir_tracker_program_stage SET fhir_update_enabled = true;
UPDATE fhir_tracker_program SET exp_enabled = true;
UPDATE fhir_tracker_program SET fhir_update_enabled = true;

UPDATE fhir_rule SET exp_enabled = true, fhir_update_enabled = true WHERE id = 'b9546b02-4adc-4868-a4cd-d5d7789f0df0';

-- virtual subscription for FHIR locations
INSERT INTO fhir_remote_subscription_resource (id, version, remote_subscription_id, fhir_resource_type, exp_only, fhir_criteria_parameters, description)
VALUES ('527d1f3d-623e-4c23-af8f-8f817d902b34', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'LOCATION', TRUE, NULL, 'Virtual subscription for all Locations.');
INSERT INTO fhir_remote_subscription_resource_update(id)
VALUES ('527d1f3d-623e-4c23-af8f-8f817d902b34');

INSERT INTO fhir_remote_subscription_system (id, version, remote_subscription_id, fhir_resource_type, system_id)
VALUES ('480f0223-9d7e-4cfc-9215-c528186df149', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'LOCATION', '2dd51309-3319-40d2-9a1f-be2a102df4a7');
