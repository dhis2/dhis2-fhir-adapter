/*
 *  Copyright (c) 2004-2019, University of Oslo
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

INSERT INTO fhir_code_category (id, version, name, code, description)
VALUES ('8f0363dc-632d-4ccf-ba07-0da5eff6be97', 0, 'Unspecified', 'UNSPECIFIED', 'Unspecified.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type)
VALUES ('0c40d4a7-e44e-4419-b6e1-6c0150596fa7', 0, 'SEARCH_FILTER_CONDITION', 'Prepares Condition Search Filter', 'Prepares Condition Search Filter.', 'SEARCH_FILTER', 'BOOLEAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('0c40d4a7-e44e-4419-b6e1-6c0150596fa7', 'SEARCH_FILTER');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('f0d13222-96ff-4fd9-ab39-68049a4eac8c', 0, '0c40d4a7-e44e-4419-b6e1-6c0150596fa7',
'searchFilter.addReference(''patient'', ''patient'', ''tracked-entity'', ''trackedEntityInstance'');
searchFilter.addReference(''subject'', ''patient'', ''tracked-entity'', ''trackedEntityInstance'');
searchFilter.addReference(''encounter'', ''encounter'', ''program-stage-event'', ''event'');
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('f0d13222-96ff-4fd9-ab39-68049a4eac8c', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('f0d13222-96ff-4fd9-ab39-68049a4eac8c', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('14bacf9c-2427-4d06-8d1b-7cea735f0089', 0, '0c40d4a7-e44e-4419-b6e1-6c0150596fa7', 'Prepares Condition Search Filter', 'SEARCH_FILTER_CONDITION');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type)
VALUES ('cb1a4e13-2571-4361-b76f-f363832314e8', 0, 'SEARCH_FILTER_MEDICATION_REQUEST', 'Prepares Medication Request Search Filter', 'Prepares Medication Request Search Filter.', 'SEARCH_FILTER', 'BOOLEAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('cb1a4e13-2571-4361-b76f-f363832314e8', 'SEARCH_FILTER');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('2e7aaf19-b749-4883-8bb2-0702ffade1b7', 0, 'cb1a4e13-2571-4361-b76f-f363832314e8',
'searchFilter.addReference(''patient'', ''patient'', ''tracked-entity'', ''trackedEntityInstance'');
searchFilter.addReference(''subject'', ''patient'', ''tracked-entity'', ''trackedEntityInstance'');
searchFilter.addReference(''encounter'', ''encounter'', ''program-stage-event'', ''event'');
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('2e7aaf19-b749-4883-8bb2-0702ffade1b7', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('2e7aaf19-b749-4883-8bb2-0702ffade1b7', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('339bdfa6-0e73-4f26-a7f5-b53c7aa580e2', 0, 'cb1a4e13-2571-4361-b76f-f363832314e8', 'Prepares Medication Request Search Filter', 'SEARCH_FILTER_MEDICATION_REQUEST');
