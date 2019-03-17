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

INSERT INTO fhir_script_variable_enum VALUES ('SEARCH_FILTER');
INSERT INTO fhir_script_type_enum VALUES ('SEARCH_FILTER');

ALTER TABLE fhir_rule
  ADD filter_script_id UUID,
  ADD CONSTRAINT fhir_rule_fk8 FOREIGN KEY (filter_script_id) REFERENCES fhir_executable_script(id);
CREATE INDEX fhir_rule_i7 ON fhir_rule(filter_script_id);

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type)
VALUES ('cf3072ec-06ad-4d62-a8a0-75ad2ab330ba', 0, 'SEARCH_FILTER_LOCATION', 'Prepares Location Search Filter', 'Prepares Location Search Filter.', 'SEARCH_FILTER', 'BOOLEAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('cf3072ec-06ad-4d62-a8a0-75ad2ab330ba', 'SEARCH_FILTER');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('10200cc2-f060-4aa8-a907-0d12b199ff3b', 0, 'cf3072ec-06ad-4d62-a8a0-75ad2ab330ba',
'searchFilter.add(''name'', ''string'', ''name'');
searchFilter.addReference(''part-of'', ''location'', ''organization-unit'', ''parent'');
searchFilter.addToken(''status'', ''active'', ''closedDate'', ''null'', null);
searchFilter.addToken(''status'', ''inactive'', ''closedDate'', ''!null'', null);
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('10200cc2-f060-4aa8-a907-0d12b199ff3b', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('10200cc2-f060-4aa8-a907-0d12b199ff3b', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('27f50aeb-0f56-4256-ae16-6118e931524b', 0, 'cf3072ec-06ad-4d62-a8a0-75ad2ab330ba', 'Prepares Location Search Filter', 'SEARCH_FILTER_LOCATION');
UPDATE fhir_rule SET filter_script_id='27f50aeb-0f56-4256-ae16-6118e931524b' WHERE id='b9546b02-4adc-4868-a4cd-d5d7789f0df0';

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type)
VALUES ('e85a46b3-279a-4c1f-ab34-b73109a3addb', 0, 'SEARCH_FILTER_ORGANIZATION', 'Prepares Organization Search Filter', 'Prepares Organization Search Filter.', 'SEARCH_FILTER', 'BOOLEAN');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('e85a46b3-279a-4c1f-ab34-b73109a3addb', 'SEARCH_FILTER');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('1f1f0ce9-5b9b-4ce1-b882-4e40713314bf', 0, 'e85a46b3-279a-4c1f-ab34-b73109a3addb',
'searchFilter.add(''name'', ''string'', ''name'');
searchFilter.addReference(''part-of'', ''organization'', ''organization-unit'', ''parent'');
searchFilter.addToken(''status'', ''true'', ''closedDate'', ''null'', null);
searchFilter.addToken(''status'', ''false'', ''closedDate'', ''!null'', null);
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('1f1f0ce9-5b9b-4ce1-b882-4e40713314bf', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('1f1f0ce9-5b9b-4ce1-b882-4e40713314bf', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('21567ae5-610c-4883-bf2d-9b439040ef2d', 0, 'e85a46b3-279a-4c1f-ab34-b73109a3addb', 'Prepares Organization Search Filter', 'SEARCH_FILTER_ORGANIZATION');
UPDATE fhir_rule SET filter_script_id='21567ae5-610c-4883-bf2d-9b439040ef2d' WHERE id='d0e1472a-05e6-47c9-b36b-ff1f06fec352';

