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

UPDATE fhir_rule SET exp_enabled = true;
UPDATE fhir_rule SET fhir_update_enabled = true;
UPDATE fhir_tracked_entity SET exp_enabled = true;
UPDATE fhir_tracked_entity SET fhir_update_enabled = true;
UPDATE fhir_remote_subscription SET exp_enabled = true;

-- virtual subscription for FHIR organizations
INSERT INTO fhir_remote_subscription_resource (id, version, remote_subscription_id, fhir_resource_type, virtual, fhir_criteria_parameters, description)
VALUES ('a9de5bdc-f6f8-49b0-ab0e-f7a04a90ae75', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'ORGANIZATION', TRUE, NULL, 'Virtual subscription for all Organizations.');
INSERT INTO fhir_remote_subscription_resource_update(id)
VALUES ('a9de5bdc-f6f8-49b0-ab0e-f7a04a90ae75');
