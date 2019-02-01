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

-- FHIR Adapter Subscription User: openmrs Password: openmrs
UPDATE fhir_server SET remote_base_url='http://localhost:8090/openmrs/ws/fhir', web_hook_authorization_header='Basic b3Blbm1yczpvcGVubXJz', remote_sync_enabled=TRUE WHERE id='73cd99c5-0ca8-42ad-a53b-1891fccce08f';
-- OpenMRS User: admin Password: Admin123
UPDATE fhir_server_header SET value='Basic YWRtaW46QWRtaW4xMjM=' WHERE name='Authorization' AND fhir_server_id ='73cd99c5-0ca8-42ad-a53b-1891fccce08f';

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('b281aed3-a397-4731-b704-cff5ef8e194c', 0, 'OPENMRS_TRANSFORM', 'OpenMRS: Transforms Resources', 'OpenMRS FHIR Resource transformation.',
'TRANSFORM_FHIR', 'BOOLEAN', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('b281aed3-a397-4731-b704-cff5ef8e194c', 'UTILS');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('b281aed3-a397-4731-b704-cff5ef8e194c', 'RESOURCE');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('4fdff9cb-431b-46e3-bfb3-b439e605f9ba', 0, 'b281aed3-a397-4731-b704-cff5ef8e194c',
'var organizationSystem = utils.getResourceSystem(''Organization'');
if ((organizationSystem != null) && (organizationSystem.getDefaultValue() != null))
{
  var organization = utils.createResource(''Organization'');
  organization.addIdentifier().setSystem(organizationSystem.getSystem()).setValue(organizationSystem.getDefaultValue());
  resource.setManagingOrganization(null);
  resource.getManagingOrganization().setResource(organization);
}
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('4fdff9cb-431b-46e3-bfb3-b439e605f9ba', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('ea9400a8-d1f0-4150-9d17-6f00fe9b86b7', 0, 'b281aed3-a397-4731-b704-cff5ef8e194c', 'OpenMRS: Transforms the Resource', 'OPENMRS_TRANSFORM');

UPDATE fhir_server SET exp_enabled=false WHERE id='73cd99c5-0ca8-42ad-a53b-1891fccce08f';
UPDATE fhir_system SET name='OpenMRS ID',system_uri='OpenMRS ID' WHERE id='ff842c76-a529-4563-972d-216b887a3573';
UPDATE fhir_server_system SET code_prefix='OMRS01-' WHERE id='ef7d37ae-6a02-46de-bf15-3dc522a464ed';
UPDATE fhir_server_system SET default_value='OU_278320' WHERE id='ea9804a3-9e82-4d0d-9cd2-e417b32b1c0c';

UPDATE fhir_server_resource SET imp_transform_script_id='ea9400a8-d1f0-4150-9d17-6f00fe9b86b7' WHERE id='667bfa41-867c-4796-86b6-eb9f9ed4dc94';
UPDATE fhir_server_resource SET imp_transform_script_id='ea9400a8-d1f0-4150-9d17-6f00fe9b86b7'  WHERE id='527d1f3d-623e-4c23-af8f-8f817d902b34';
