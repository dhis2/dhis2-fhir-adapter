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

-- Tracker Program Child Programme, Birth: Weight
INSERT INTO fhir_executable_script (id, version, script_id, name, code, base_executable_script_id)
VALUES ('f8fb0309-26c6-450d-9e80-9e2e765ccd00', 0, '83a84bee-eadc-4c8a-b78f-8c8b9269883d', 'CP: Export Birth Weight', 'CP_EXP_BIRTH_WEIGHT', '518b52d2-2d1e-4171-86ce-1e40ec269a1a');
UPDATE fhir_rule
SET transform_exp_script_id = 'f8fb0309-26c6-450d-9e80-9e2e765ccd00'
WHERE id = 'ff043b6e-40c9-4fa7-9721-ba2239b38360';
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT '87388c6e-449e-466f-8ef8-c2b44557a10f', 0, id, 'CODE:DE_2005736', 'dataElement', true
FROM fhir_rule
WHERE id = 'ff043b6e-40c9-4fa7-9721-ba2239b38360';
DELETE
FROM fhir_executable_script_argument
WHERE id = '08d3eedf-b73d-4487-9fa4-f2aeef3be35f';

-- Tracker Program Child Programme, Birth: Weight from Body Weight
INSERT INTO fhir_executable_script (id, version, script_id, name, code, base_executable_script_id)
VALUES ('2918efbf-b8f9-48f9-921a-405996bd6831', 0, '83a84bee-eadc-4c8a-b78f-8c8b9269883d', 'CP: Export Birth Weight from Body Weight', 'CP_EXP_BIRTH_WEIGHT_BODY_WEIGHT', '4a326b89-4961-4b69-8021-8d5285c2b0a7');
UPDATE fhir_rule
SET transform_exp_script_id = '2918efbf-b8f9-48f9-921a-405996bd6831',
    exp_enabled= false
WHERE id = '097d9ee0-bdb3-44ae-b961-3b4584bad1db';
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT 'a11ed48e-66e3-4988-a64d-7e0a69486155', 0, id, 'CODE:DE_2005736', 'dataElement', true
FROM fhir_rule
WHERE id = '097d9ee0-bdb3-44ae-b961-3b4584bad1db';
DELETE
FROM fhir_executable_script_argument
WHERE id = '70684c93-b22a-4fff-86bb-604683c27fdd';

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
UPDATE fhir_program_stage_rule
SET exp_delete_evaluate_script_id='2db4dba6-b445-4fce-a9a5-b3f24d0a12cc'
WHERE id = '9843c898-6a41-4388-ab6e-30503708ac91';

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
UPDATE fhir_program_stage_rule
SET exp_delete_evaluate_script_id='2db4dba6-b445-4fce-a9a5-b3f24d0a12cc'
WHERE id = '8019cebe-da61-4aff-a2fd-579a538c8671';

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
UPDATE fhir_program_stage_rule
SET exp_delete_evaluate_script_id='2db4dba6-b445-4fce-a9a5-b3f24d0a12cc'
WHERE id = '9dd587e7-c7ca-4365-93f3-7263aa3cfb82';

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

-- Tracker Program Child Programme, Baby Postnatal: Body Weight
INSERT INTO fhir_executable_script (id, version, script_id, name, code, base_executable_script_id)
VALUES ('06005c16-cbc8-4a50-abb0-7b16140c109c', 0, '83a84bee-eadc-4c8a-b78f-8c8b9269883d', 'CP: Export Infant Weight', 'CP_EXP_INFANT_WEIGHT', '0104ad19-ba82-48dc-bbd1-38dd5f0d8a2c');
UPDATE fhir_rule
SET transform_exp_script_id = '06005c16-cbc8-4a50-abb0-7b16140c109c'
WHERE id = 'a6636c83-f236-48cd-bb2b-592147db9a34';
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT '9589bff1-80b9-4f9d-bafb-8e679a219bf1', 0, id, 'CODE:DE_2006099', 'dataElement', true
FROM fhir_rule
WHERE id = 'a6636c83-f236-48cd-bb2b-592147db9a34';
DELETE
FROM fhir_executable_script_argument
WHERE id = '794c69f6-86b3-4c30-b4e1-fd162c6ab825';

-- Tracker Program Child Programme, Birth: Apgar Score
INSERT INTO fhir_executable_script (id, version, script_id, name, code, base_executable_script_id)
VALUES ('40f5a167-7e88-4a08-baf5-b9fd19b1756c', 0, '30760578-0c97-43be-ad28-324ccbbad249', 'CP: Export Apgar Score', 'CP_EXP_APGAR_SCORE', 'a6f14056-a046-40c9-839c-9320d09810f6');
UPDATE fhir_rule
SET transform_exp_script_id = '40f5a167-7e88-4a08-baf5-b9fd19b1756c'
WHERE id = '026eb6b1-cb25-4243-b02d-7ce1e1831842';
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT '2e88d387-424a-4178-b1e8-21f58b9cd6a0', 0, id, 'CODE:DE_2006098', 'dataElement', true
FROM fhir_rule
WHERE id = '026eb6b1-cb25-4243-b02d-7ce1e1831842';
INSERT INTO fhir_rule_dhis_data_ref(id, version, rule_id, data_ref, script_arg_name, required)
SELECT '0180ec99-a0eb-49bd-9bcf-2873a457b1d2', 0, id, 'CODE:DE_391382', 'commentDataElement', false
FROM fhir_rule
WHERE id = '026eb6b1-cb25-4243-b02d-7ce1e1831842';
DELETE
FROM fhir_executable_script_argument
WHERE id = '3fdad25a-63ce-4754-9a37-602b08bd8396';
DELETE
FROM fhir_executable_script_argument
WHERE id = 'ea619e25-dd65-4e8e-a9ea-6452e7cefc88';
