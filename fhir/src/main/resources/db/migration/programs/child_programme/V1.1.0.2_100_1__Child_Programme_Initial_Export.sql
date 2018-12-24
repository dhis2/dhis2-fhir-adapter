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

-- Tracker Program Child Programme, Birth: BCG dose
UPDATE fhir_rule
SET transform_exp_script_id = '864e47fe-a186-4340-9b4c-d728150fb45b'
WHERE id = '9843c898-6a41-4388-ab6e-30503708ac91';
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT '68411080-52b6-4abd-8eb9-d27d8d6f389b', 0, id, 'CODE:DE_2006101', 'dataElement', true
FROM fhir_rule
WHERE id = '9843c898-6a41-4388-ab6e-30503708ac91';
DELETE
FROM fhir_executable_script_argument
WHERE id = '19df546e-7eb9-4384-87d1-162346d322fc';

-- Tracker Program Child Programme, Baby Postnatal: Measles given
UPDATE fhir_rule
SET transform_exp_script_id = '864e47fe-a186-4340-9b4c-d728150fb45b'
WHERE id = '8019cebe-da61-4aff-a2fd-579a538c8671';
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT '651406fe-4612-48d9-92cb-9c4d162371d0', 0, id, 'CODE:DE_2006125', 'dataElement', true
FROM fhir_rule
WHERE id = '8019cebe-da61-4aff-a2fd-579a538c8671';
DELETE
FROM fhir_executable_script_argument
WHERE id = '86759369-63d9-4a2c-84f8-e9897f69166a';

-- Tracker Program Child Programme, Baby Postnatal: Yellow fever given
UPDATE fhir_rule
SET transform_exp_script_id = '864e47fe-a186-4340-9b4c-d728150fb45b'
WHERE id = '9dd587e7-c7ca-4365-93f3-7263aa3cfb82';
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT '8931b1f9-47a1-4248-9348-5376472c45a0', 0, id, 'CODE:DE_2006126', 'dataElement', true
FROM fhir_rule
WHERE id = '9dd587e7-c7ca-4365-93f3-7263aa3cfb82';
DELETE
FROM fhir_executable_script_argument
WHERE id = '8bb6914e-5014-428b-a3fc-19e18e13df33';

-- Tracker Program Child Programme, Birth: OPV Dose
UPDATE fhir_rule
SET transform_exp_script_id = 'a5b648c0-3d7d-4a19-8bda-3ec37f9d31a2'
WHERE id = '91ae6f5f-db07-4391-97cd-407a77794a1b';
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT 'e66efbcb-7142-445a-8e1a-d1ef524977c1', 0, id, 'CODE:DE_2006104', 'dataElement', true
FROM fhir_rule
WHERE id = '91ae6f5f-db07-4391-97cd-407a77794a1b';
DELETE
FROM fhir_executable_script_argument
WHERE id = '4a8ba215-10e9-46f2-921f-da3973836119';

-- Tracker Program Child Programme, Baby Postnatal: DPT Dose
UPDATE fhir_rule
SET transform_exp_script_id = 'a5b648c0-3d7d-4a19-8bda-3ec37f9d31a2'
WHERE id = '2f70e895-e238-40d3-ae0b-9e18e69792d6';
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT '7c1bcaf5-d49c-406c-9e8c-ea76e1e71186', 0, id, 'CODE:DE_2006105', 'dataElement', true
FROM fhir_rule
WHERE id = '2f70e895-e238-40d3-ae0b-9e18e69792d6';
DELETE
FROM fhir_executable_script_argument
WHERE id = '528db27f-1927-4ae0-8326-b4e693349c52';
