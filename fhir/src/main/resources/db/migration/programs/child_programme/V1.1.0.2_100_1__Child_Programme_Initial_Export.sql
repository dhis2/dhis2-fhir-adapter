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

-- Tracker Program Child Programme, Baby Postnatal: Measles given
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('87ac1eff-f011-4428-92bb-c4e2da2d3490', 0, 'c8a937b5-665b-485c-bbc9-7a83e21a4e47', 'CP: Measles given to FHIR', 'CP_MEASLES_GIVEN_EXP');
UPDATE fhir_rule
SET transform_exp_script_id = '87ac1eff-f011-4428-92bb-c4e2da2d3490'
WHERE id = '8019cebe-da61-4aff-a2fd-579a538c8671';
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT '651406fe-4612-48d9-92cb-9c4d162371d0', 0, id, 'CODE:DE_2006125', 'dataElement', true
FROM fhir_rule
WHERE id = '8019cebe-da61-4aff-a2fd-579a538c8671';
DELETE
FROM fhir_executable_script_argument
WHERE id = '86759369-63d9-4a2c-84f8-e9897f69166a';
