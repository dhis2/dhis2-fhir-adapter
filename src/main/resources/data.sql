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

INSERT INTO fhir_remote_subscription (id, version, name, code, description, fhir_version, web_hook_authorization_header, tolerance_minutes)
VALUES ('73cd99c5-0ca8-42ad-a53b-1891fccce08f',
        0,
        'HAPI FHIR JPA Server',
        'SAMPLE',
        'HAPI FHIR JPA Server with sample data.',
        'DSTU3',
        '',
        1);
INSERT INTO fhir_remote_subscription_resource (id, version, remote_subscription_id, fhir_resource_type, fhir_criteria_parameters, description, resource_system)
VALUES ('667bfa41-867c-4796-86b6-eb9f9ed4dc94', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'PATIENT', '_format=json', 'Subscription for all Patients.', 'http://example.ph/national-patient-id');
INSERT INTO fhir_remote_subscription_resource (id, version, remote_subscription_id, fhir_resource_type, fhir_criteria_parameters, description, resource_system)
VALUES ('a756ef2a-1bf4-43f4-a991-fbb48ad358ac', 0, '73cd99c5-0ca8-42ad-a53b-1891fccce08f', 'IMMUNIZATION', '_format=json', 'Subscription for all Immunizations.', NULL);

INSERT INTO FHIR_AUTOMATED_ENROLLMENT VALUES('e48eb514-aa60-4541-841a-16119507e525',1,'var age = dateTimeUtils.getAge( trackedEntityInstance.getValueByName(''Birth date'') ); (age != null) && (age < 1)','output.organizationUnitId = trackedEntityInstance' ||
 '.organizationUnitId; output.enrollmentDate = new Date(); output.incidentDate = trackedEntityInstance.getValueByName( ''Birth date'' ); true;');
INSERT INTO FHIR_DHIS_MAP VALUES('13aeb380-0695-4e36-b255-36e18aa8a5c2',1,'IMMUNIZATION','DSTU3','EVENT',TRUE,1,'!input.notGiven && codeUtils.getCode( input.vaccineCode, ''http://example.ph/vaccine-codes'' ) == ''BCG''','output.setValueByName( ''CP - MCH ' ||
 'BCG dose'', true ); true;',NULL,NULL,NULL,FALSE);
INSERT INTO FHIR_DHIS_MAP VALUES('5b4bae24-ec73-46fc-89ef-accfcd81e14d',1,'IMMUNIZATION','DSTU3','EVENT',TRUE,1,'!input.notGiven && codeUtils.getCode( input.vaccineCode, ''http://example.ph/vaccine-codes'' ) == ''MMR''','output.setValueByName( ''CP - MCH ' ||
 'Measles dose'', true ); true;',NULL,NULL,NULL,FALSE);
INSERT INTO FHIR_DHIS_MAP VALUES('a5623570-446a-4267-944b-025a2c35c18c',1,'PATIENT','DSTU3','TRACKED_ENTITY',TRUE,1,'true','output.organizationUnitId = organizationUtils.getOrganizationUnitId( input.managingOrganization, ''http://example.ph/organizations'' ' ||
 '); output.setValueByName( ''National identifier'', identifierUtils.getIdentifier( input, ''http://example.ph/national-patient-id'' ) ); output.setValueByName( ''Last name'', humanNameUtils.getPrimaryName( input.name ).family ); output.setValueByName( ''First name'', humanNameUtils.getSingleGiven( humanNameUtils.getPrimaryName( input.name ) ) ); output.setValueByName( ''Birth date'', dateTimeUtils.getPreciseDate( input.birthDateElement ) ); output.setValueByName( ''Gender'', input.gender ); output.setValueByName( ''Address line'', addressUtils.getSingleLine( addressUtils.getPrimaryAddress( input.address ) ) ); output.setValueByName( ''City'', addressUtils.getPrimaryAddress( input.address ).city ); output.setValueByName( ''State of country'', addressUtils.getPrimaryAddress( input.address ).state ); output.setValueByName( ''Country'', addressUtils.getPrimaryAddress( input.address ).country ); output.coordinates = geoUtils.getLocation( addressUtils.getPrimaryAddress( input.address ) );  true;','National identifier','NAME','http://example.ph/national-patient-id',FALSE);
INSERT INTO FHIR_DHIS_MAP VALUES('f4e164a7-ed91-4f76-83b8-eda0daa0b406',1,'IMMUNIZATION','DSTU3','EVENT',TRUE,1,'!input.notGiven && codeUtils.getCode( input.vaccineCode, ''http://example.ph/vaccine-codes'' ) == ''OPV''','output.setValueByName( ''CP - MCH ' ||
 'OPV dose'', Math.max(0, Math.min(3, immunizationUtils.getMaxDoseSequence(input)-1)) ); true;',NULL,NULL,NULL,FALSE);
INSERT INTO FHIR_DHIS_MAP VALUES('f4e164a7-ed91-4f76-83b8-eda0daa0b408',1,'IMMUNIZATION','DSTU3','EVENT',TRUE,1,'!input.notGiven && codeUtils.getCode( input.vaccineCode, ''http://example.ph/vaccine-codes'' ) == ''DTaP''','output.setValueByName( ''CP - MCH ' ||
 'DPT dose'', Math.max(1, Math.min(3, immunizationUtils.getMaxDoseSequence(input))) ); true;',NULL,NULL,NULL,FALSE);
INSERT INTO FHIR_TRACKED_ENTITY_MAP VALUES('a5623570-446a-4267-944b-025a2c35c18c','Person');
INSERT INTO FHIR_EVENT_MAP VALUES('13aeb380-0695-4e36-b255-36e18aa8a5c2','Child Programme','Birth',TRUE,'e48eb514-aa60-4541-841a-16119507e525');
INSERT INTO FHIR_EVENT_MAP VALUES('5b4bae24-ec73-46fc-89ef-accfcd81e14d','Child Programme','Baby Postnatal',TRUE,'e48eb514-aa60-4541-841a-16119507e525');
INSERT INTO FHIR_EVENT_MAP VALUES('f4e164a7-ed91-4f76-83b8-eda0daa0b406','Child Programme','Birth',TRUE,'e48eb514-aa60-4541-841a-16119507e525');
INSERT INTO FHIR_EVENT_MAP VALUES('f4e164a7-ed91-4f76-83b8-eda0daa0b408','Child Programme','Baby Postnatal',TRUE,'e48eb514-aa60-4541-841a-16119507e525');
INSERT INTO FHIR_RESOURCE_MAP VALUES('f4e164a7-ed91-4f76-83b8-eda0daa0b407',1,'IMMUNIZATION','trackedEntityUtils.getTrackedEntityInstance( trackedEntityType, ''National identifier'', input.patient, ''http://example.ph/national-patient-id'' )','dateTimeUtils.getPrecisePastDate( input.dateElement )',
'enrollment.organizationUnitId');
