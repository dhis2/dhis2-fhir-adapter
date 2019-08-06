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

UPDATE fhir_client SET fhir_version='R4', remote_base_url='http://localhost:8082/hapi-fhir-jpaserver/fhir'
WHERE id='73cd99c5-0ca8-42ad-a53b-1891fccce08f';

-- virtual subscription for FHIR organizations
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, exp_only, fhir_criteria_parameters, description)
VALUES ('2520acc5-86b4-4716-ae0f-ea0531eb885a', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'PLAN_DEFINITION', TRUE, NULL, 'Virtual subscription for all Plan Definitions.');
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, exp_only, fhir_criteria_parameters, description)
VALUES ('0918c4f3-995c-4130-b607-b28023a1a3a0', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'QUESTIONNAIRE', TRUE, NULL, 'Virtual subscription for all Questionnaires.');
INSERT INTO fhir_client_resource_update(id)
VALUES ('2520acc5-86b4-4716-ae0f-ea0531eb885a');
