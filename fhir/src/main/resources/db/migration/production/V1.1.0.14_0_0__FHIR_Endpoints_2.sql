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

INSERT INTO fhir_resource_type_enum VALUES ('CONDITION');
ALTER TABLE fhir_client ADD COLUMN use_remote BOOLEAN DEFAULT TRUE NOT NULL;
COMMENT ON COLUMN fhir_client.use_json_format IS 'Specifies if JSON format should be used when communicating with the FHIR REST interfaces of the FHIR system.';
COMMENT ON COLUMN fhir_client.sort_supported IS 'Specifies if the FHIR interfaces of the FHIR system support sorting when reading data.';
COMMENT ON COLUMN fhir_client.use_remote IS 'Specifies if the remote FHIR system can be used for retrieving data (i.e. a connection can be established to it).';

INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('c8ca1fbd-4d95-4911-b16e-a2310af3533f', 0, 'DHIS2 FHIR Adapter Patient Identifier', 'SYSTEM_DHIS2_FHIR_PATIENT_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/patient-identifier',
        'DHIS2 FHIR Adapter Patient Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('cc46b8ac-aced-4bd2-b104-03a7d34da438', 0, 'DHIS2 FHIR Adapter Location Identifier', 'SYSTEM_DHIS2_FHIR_LOCATION_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/location-identifier',
        'DHIS2 FHIR Adapter Location Identifier.');
INSERT INTO fhir_system (id, version, name, code, system_uri, description)
VALUES ('db955a8a-ca58-4263-bc56-faa99085df93', 0, 'DHIS2 FHIR Adapter Organization Identifier', 'SYSTEM_DHIS2_FHIR_ORGANIZATION_IDENTIFIER', 'http://www.dhis2.org/dhis2-fhir-adapter/systems/organization-identifier',
        'DHIS2 FHIR Adapter Organization Identifier.');

INSERT INTO fhir_client (id, version, name, code, description, fhir_version, web_hook_authorization_header, dhis_authentication_method, dhis_username, dhis_password,
remote_base_url, use_remote, tolerance_millis, logging, verbose_logging, adapter_base_url, subscription_type, enabled, locked, use_adapter_identifier)
VALUES ('a5a6a642-15a2-4f27-9cee-55a26a86d062', 0, 'FHIR REST Interfaces DSTU3', 'FHIR_RI_DSTU3', 'Used by the FHIR REST Interfaces for DSTU3.', 'DSTU3',
'None ' || uuid_generate_v4(), 'BASIC', 'none', 'none', 'http://localhost:8088/fhir', FALSE, 0, FALSE, FALSE,
'http://localhost:8080/fhir-adapter', 'REST_HOOK_WITH_JSON_PAYLOAD', FALSE, TRUE, FALSE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('1de5bc4c-b55e-4be8-b96a-da239cfa2cf6', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'CONDITION', NULL, 'FHIR Condition Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('be9a7d61-d5cd-451a-89f9-3e36cf213299', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'DIAGNOSTIC_REPORT', NULL, 'FHIR Diagnostic Report Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('edfde6aa-8de9-440b-ab72-84a0f9be01e2', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'ENCOUNTER', NULL, 'FHIR Encounter Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('3e8719c1-aef1-4728-b6aa-b208dea629e2', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'IMMUNIZATION', NULL, 'FHIR Immunization Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('8c4dc1b9-d1da-4069-8e8c-ab8409a3daf5', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'LOCATION', NULL, 'FHIR Location Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('00716d5d-1c1e-4ab9-b860-fcaccd7dad1d', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'MEDICATION_REQUEST', NULL, 'FHIR Medication Request Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('fade2919-c46f-472a-b24d-e6e5bb99d5a6', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'OBSERVATION', NULL, 'FHIR Observation Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('7109e75f-9ece-4773-9ca5-6c8774827032', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'ORGANIZATION', NULL, 'FHIR Organization Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('080f368d-3566-49dd-bedf-6bb6026bdd28', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'PATIENT', NULL, 'FHIR Patient Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('3165533a-0df1-43db-8dd5-ac57da8fb516', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'RELATED_PERSON', NULL, 'FHIR Related Person Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('c9ae7ad7-ed40-4160-80ca-2964755b36ea', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'PRACTITIONER', NULL, 'FHIR Practitioner Resource.', TRUE);
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('99e36c20-e8ef-48d1-8d1f-e80572384281', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'PATIENT', 'c8ca1fbd-4d95-4911-b16e-a2310af3533f');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('bdf73819-1693-4177-bf6b-ca7a0c2c4ea9', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'LOCATION', 'cc46b8ac-aced-4bd2-b104-03a7d34da438');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('2e56fc2b-576a-46ca-aa6d-4485c75c6f5b', 0, 'a5a6a642-15a2-4f27-9cee-55a26a86d062', 'ORGANIZATION', 'db955a8a-ca58-4263-bc56-faa99085df93');

INSERT INTO fhir_client (id, version, name, code, description, fhir_version, web_hook_authorization_header, dhis_authentication_method, dhis_username, dhis_password,
remote_base_url, use_remote, tolerance_millis, logging, verbose_logging, adapter_base_url, subscription_type, enabled, locked, use_adapter_identifier)
VALUES ('46f0af46-3654-40b3-8d4c-7a633332c3b3', 0, 'FHIR REST Interfaces R4', 'FHIR_RI_R4', 'Used by the FHIR REST Interfaces for R4.', 'R4',
'None ' || uuid_generate_v4(), 'BASIC', 'none', 'none', 'http://localhost:8088/fhir', FALSE, 0, FALSE, FALSE,
'http://localhost:8080/fhir-adapter', 'REST_HOOK_WITH_JSON_PAYLOAD', FALSE, TRUE, FALSE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('fedf63ac-80f0-4f24-a8e9-595767f0983a', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'CONDITION', NULL, 'FHIR Condition Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('bf745a0b-81a4-4629-9434-c5715605d8e4', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'DIAGNOSTIC_REPORT', NULL, 'FHIR Diagnostic Report Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('ef225022-3762-4480-a2ee-702fa47283bc', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'ENCOUNTER', NULL, 'FHIR Encounter Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('3ac34328-e80e-465b-893b-df5afe7aa527', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'IMMUNIZATION', NULL, 'FHIR Immunization Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('2dc28bd3-802d-4cc1-8352-e3b9277fd280', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'LOCATION', NULL, 'FHIR Location Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('0e89f64e-04af-480e-9c41-e8423eb5c602', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'MEDICATION_REQUEST', NULL, 'FHIR Medication Request Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('b8eccfd3-4ded-45d7-ab84-dc8467541902', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'OBSERVATION', NULL, 'FHIR Observation Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('f3491bb6-9279-4fb7-a275-60c217f88a6a', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'ORGANIZATION', NULL, 'FHIR Organization Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('6236c52d-9961-4b99-88f5-d6e692021a7f', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'PATIENT', NULL, 'FHIR Patient Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('c8d4991f-c100-4b95-91c4-7b975ac636fd', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'RELATED_PERSON', NULL, 'FHIR Related Person Resource.', TRUE);
INSERT INTO fhir_client_resource (id, version, fhir_client_id, fhir_resource_type, fhir_criteria_parameters, description, preferred)
VALUES ('26526d56-224e-46ec-bc04-827711f2b345', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'PRACTITIONER', NULL, 'FHIR Practitioner Resource.', TRUE);
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('070a6e46-4dfb-452e-90c5-475021532dc0', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'PATIENT', 'c8ca1fbd-4d95-4911-b16e-a2310af3533f');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('47fd1b28-7558-429b-85ab-c5f5fcc3d00f', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'LOCATION', 'cc46b8ac-aced-4bd2-b104-03a7d34da438');
INSERT INTO fhir_client_system(id, version, fhir_client_id, fhir_resource_type, system_id)
VALUES('e2cdcfd6-09f0-44fe-9dd2-c3e97540ea22', 0, '46f0af46-3654-40b3-8d4c-7a633332c3b3', 'ORGANIZATION', 'db955a8a-ca58-4263-bc56-faa99085df93');

UPDATE fhir_script_source SET source_text=
'function getOrganizationUnitMappedCode(organizationReference)
{
  var mappedCode = null;
  if (organizationReference != null)
  {
    var hierarchy = organizationUtils.findHierarchy(organizationReference);
    if (hierarchy != null)
    {
      for (var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++)
      {
        var code = identifierUtils.getResourceIdentifier(hierarchy.get(i), ''ORGANIZATION'');
        if (code != null)
        {
          mappedCode = codeUtils.getMappedCode(code, ''ORGANIZATION'');
          if ((mappedCode == null) && args[''useIdentifierCode''])
          {
            mappedCode = organizationUtils.existsWithPrefix(code);
          }
        }
      }
    }
  }
  return mappedCode;
}
function getOrganizationUnitMappedCode(organizationReference)
{
  var mappedCode = null;
  if (organizationReference != null)
  {
    var hierarchy = organizationUtils.findHierarchy(organizationReference);
    if (hierarchy != null)
    {
      for (var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++)
      {
        var code = identifierUtils.getResourceIdentifier(hierarchy.get(i), ''ORGANIZATION'');
        if (code != null)
        {
          mappedCode = codeUtils.getMappedCode(code, ''ORGANIZATION'');
          if ((mappedCode == null) && args[''useIdentifierCode''])
          {
            mappedCode = organizationUtils.existsWithPrefix(code);
          }
        }
      }
    }
  }
  return mappedCode;
}
function getLocationReference(location)
{
  if (typeof input.locationFirstRep === ''undefined'')
  {
    return location;
  }
  else if (typeof input.locationFirstRep.location !== ''undefined'')
  {
    return input.locationFirstRep.location;
  }
  return null;
}
function getMappedCode(resource)
{
  var  mappedCode = null ;
  if (resource.managingOrganization)
  {
    mappedCode = getOrganizationUnitMappedCode(resource.managingOrganization);
  }
  else if (resource.location)
  {
    var locationReference = getLocationReference(resource.location);
    if (locationReference != null)
    {
      var hierarchy = locationUtils.findHierarchy(locationReference);
      if (hierarchy != null)
      {
        for (var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++)
        {
          var code = identifierUtils.getResourceIdentifier(hierarchy.get(i), ''LOCATION'');
          if (code != null)
          {
            mappedCode = codeUtils.getMappedCode(code, ''LOCATION'');
            if ((mappedCode == null) && args[''useIdentifierCode''])
            {
              mappedCode = locationUtils.existsWithPrefix(code);
            }
          }
        }
        for (var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++)
        {
          var organizationReference = hierarchy.get(i).getManagingOrganization();
          if (organizationReference != null)
          {
            mappedCode = getOrganizationUnitMappedCode(organizationReference);
          }
        }
      }
    }
  }
  return mappedCode;
}
var ref = null;
if (context.getFhirRequest().isDhisFhirId())
{
  var fhirOrgUnitRef = null;
  if (input.managingOrganization)
  {
    fhirOrgUnitRef = input.managingOrganization;
  }
  else if (input.location)
  {
    fhirOrgUnitRef = input.location;
  }
  else if (input.encounter)
  {
    var encounter = referenceUtils.getResource(input.encounter, ''Encounter'');
    if (encounter != null)
    {
      fhirOrgUnitRef = getLocationReference(encounter.location);
    }
  }
  if (fhirOrgUnitRef == null)
  {
    if (args[''useTei''] && (typeof trackedEntityInstance !== ''undefined''))
    {
      ref = context.createReference(trackedEntityInstance.organizationUnitId, ''ID'');
    }
    else if ((typeof enrollment !== ''undefined'') && (enrollment.organizationUnitId != null))
    {
      ref = context.createReference(enrollment.organizationUnitId, ''ID'');
    }
  }
  else
  {
    ref = context.createReference(context.extractDhisId(fhirOrgUnitRef.getReferenceElement()), ''id'');
  }
}
else
{
  var mappedCode;
  if (input.managingOrganization || input.location)
  {
    mappedCode = getMappedCode(input);
  }
  else if (input.encounter)
  {
    var encounter = referenceUtils.getResource(input.encounter, ''Encounter'');
    if (encounter != null)
    {
      mappedCode = getMappedCode(encounter);
    }
  }
  if (mappedCode == null)
  {
    mappedCode = args[''defaultCode''];
  }
  if (mappedCode != null)
  {
    ref = context.createReference(mappedCode, ''CODE'');
  }
  if ((ref == null) && args[''useTei''] && (typeof trackedEntityInstance !== ''undefined''))
  {
    ref = context.createReference(trackedEntityInstance.organizationUnitId, ''ID'');
  }
  else if ((ref == null) && (typeof enrollment !== ''undefined'') && (enrollment.organizationUnitId != null))
  {
    ref = context.createReference(enrollment.organizationUnitId, ''ID'');
  }
}
ref' WHERE id='7b94feba-bcf6-4635-929a-01311b25d975' AND version=0;

UPDATE fhir_script_source SET source_text='var fhirResource = null;
if (context.getFhirRequest().isDhisFhirId())
{
  fhirResource = fhirResourceUtils.createResource(''Patient'');
  fhirResource.setId(input.getPatient().getReferenceElement());
}
else
{
  fhirResource = referenceUtils.getResource(input.patient, ''Patient'');
}
fhirResource'
WHERE id='85b3c460-6c2a-4f50-af46-ff09bf2e69df' AND version=0;

UPDATE fhir_script_source SET source_text='var fhirResource = null;
if (context.getFhirRequest().isDhisFhirId())
{
  fhirResource = fhirResourceUtils.createResource(''Patient'');
  fhirResource.setId(input.getSubject().getReferenceElement());
}
else
{
  fhirResource = referenceUtils.getResource(input.subject, ''Patient'');
}
fhirResource'
WHERE id='960d2e6c-2479-48a2-b04e-b14879e71d14' AND version=0;
