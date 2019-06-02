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

INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('4a156bee-5935-4bcc-80b8-e944a08a2988', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'middleNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the middle name of the Person.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('8b4235c1-dcf7-4220-bc65-a95337253361', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'villageNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the village name where the Person lives.');

INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('fc8eea95-eb6a-4515-be14-6445983b0936', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'motherFirstNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the first name of the mother/caregiver.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('483c7d66-7b36-405d-b511-48450c616a0e', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'motherLastNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the last name of the mother/caregiver.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('2ea7dd03-d534-4d00-aea8-ecbb45f686a6', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'motherPhoneAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the phone number of the mother/caregiver.');

INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('64a02bc8-0a4a-4747-a5e2-f8b0e4d81fc1', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'fatherFirstNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the first name of the father/caregiver.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('b1b17e68-a0fb-4dbc-a308-24bc5460c3c0', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'fatherLastNameAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the last name of the father/caregiver.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('29980459-b0eb-4727-ba3f-39a503bb90cd', 0, 'ea887943-5e94-4e31-9441-c7661fe1063e',
'fatherPhoneAttribute', 'TRACKED_ENTITY_ATTRIBUTE_REF', FALSE, NULL,
'The reference of the tracked entity attribute that contains the phone number of the father/caregiver.');

UPDATE fhir_script_source SET source_text=
'function findContact(contacts, gender)
{
  for (var i = 0; i < contacts.length; i++)
  {
    var contact = contacts[i];
    if (dateTimeUtils.isValidNow(contact.getPeriod()) && ((gender==null && contact.getGender()==null) || (contact.getGender()!=null && contact.getGender().name()==gender)))
    {
      return contact;
    }
  }
  return null;
}
function updateContact(firstNameAttribute, lastNameAttribute, phoneAttribute, gender, nullGender)
{
  if (firstNameAttribute != null || lastNameAttribute != null || phoneAttribute != null)
  {
    var firstName = null;
    var lastName = null;
    var phone = null;
    var contact = findContact(input.contact, gender);
    if (contact == null && nullGender)
    {
      contact = findContact(input.contact, null);
    }
    if (contact != null)
    {
      firstName = humanNameUtils.getSingleGiven(contact.name);
      lastName = contact.name.family;
      phone = contactPointUtils.getPhoneContactPointValue(contact.telecom);
    }
    if ((firstName != null) || resetDhisValue)
    {
      output.setOptionalValue(firstNameAttribute, firstName, context.getFhirRequest().getLastUpdated());
    }
    if ((lastName != null) || resetDhisValue)
    {
      output.setOptionalValue(lastNameAttribute, lastName, context.getFhirRequest().getLastUpdated());
    }
    if ((phone != null) || resetDhisValue)
    {
      output.setOptionalValue(phoneAttribute, phone, context.getFhirRequest().getLastUpdated());
    }
  }
}
output.setOptionalValue(args[''uniqueIdAttribute''], output.getIdentifier());
output.setValue(args[''lastNameAttribute''], humanNameUtils.getPrimaryName(input.name).family, context.getFhirRequest().getLastUpdated());
if (args[''middleNameAttribute''] == null)
{
  output.setValue(args[''firstNameAttribute''], humanNameUtils.getSingleGiven(humanNameUtils.getPrimaryName(input.name)), context.getFhirRequest().getLastUpdated());
}
else
{
  output.setValue(args[''firstNameAttribute''], humanNameUtils.getFirstGiven(humanNameUtils.getPrimaryName(input.name)), context.getFhirRequest().getLastUpdated());
  output.setValue(args[''middleNameAttribute''], humanNameUtils.getSecondGiven(humanNameUtils.getPrimaryName(input.name)), context.getFhirRequest().getLastUpdated());
}
var birthDate = dateTimeUtils.getPreciseDate(input.birthDateElement);
if ((birthDate != null) || args[''resetDhisValue''])
{
  output.setOptionalValue(args[''birthDateAttribute''], birthDate, context.getFhirRequest().getLastUpdated());
}
if ((input.gender != null) || args[''resetDhisValue''])
{
  output.setOptionalValue(args[''genderAttribute''], input.gender, context.getFhirRequest().getLastUpdated());
}
var addressText = addressUtils.getConstructedText(addressUtils.getPrimaryAddress(input.address));
if ((addressText != null) || args[''resetDhisValue''])
{
  output.setOptionalValue(args[''addressTextAttribute''], addressText, context.getFhirRequest().getLastUpdated());
}
var villageName = addressUtils.getPrimaryAddress(input.address).getCity();
if ((villageName != null) || args[''resetDhisValue''])
{
  output.setOptionalValue(args[''villageNameAttribute''], villageName, context.getFhirRequest().getLastUpdated());
}
updateContact(args[''motherFirstNameAttribute''], args[''motherLastNameAttribute''], args[''motherPhoneAttribute''], ''FEMALE'', true);
updateContact(args[''fatherFirstNameAttribute''], args[''fatherLastNameAttribute''], args[''fatherPhoneAttribute''], ''MALE'', true);
true' WHERE id = 'b2cfaf30-6ede-41f2-bd6c-448e76c429a1' AND version = 0;

UPDATE fhir_script_source SET source_text=
'function addContact(patient, firstNameAttribute, lastNameAttribute, phoneAttribute, gender)
{
  var firstName = firstNameAttribute==null ? null : input.getValue(firstNameAttribute);
  var lastName = lastNameAttribute==null ? null : input.getValue(lastNameAttribute);
  var phone = phoneAttribute==null ? null : input.getValue(phoneAttribute);
  if (firstName!=null || lastName!=null || phone!=null)
  {
    var contact = patient.addContact();
    contact.setGender(genderUtils.getAdministrativeGender(gender));
    if (firstName!=null || lastName!=null)
    {
      contact.getName().setFamily(lastName);
      if (firstName!=null)
      {
        humanNameUtils.updateGiven(contact.getName(), firstName);
      }
    }
    if (phone!=null)
    {
      var contactPoint = contact.addTelecom().setValue(phone);
      contactPoint.setSystem(fhirResourceUtils.resolveEnumValue(contactPoint, ''system'', ''phone''));
    }
  }
}
function canOverrideAddress(address)
{
  return !address.hasLine() && !address.hasCity() && !address.hasDistrict() && !address.hasState() && !address.hasPostalCode() && !address.hasCountry();
}
if (output.getName().size() < 2)
{
  var lastName = input.getValue(args[''lastNameAttribute'']);
  var firstName = input.getValue(args[''firstNameAttribute'']);
  if ((lastName != null) || (firstName != null) || args[''resetFhirValue''])
  {
    output.getName().clear();
    if ((lastName != null) || args[''resetFhirValue''])
    {
      output.getNameFirstRep().setFamily(lastName);
    }
    if (args[''middleNameAttribute''] == null)
    {
      if ((firstName != null) || args[''resetFhirValue''])
      {
        humanNameUtils.updateGiven(output.getNameFirstRep(), firstName);
      }
    }
    else
    {
      var middleName = input.getValue(args[''middleNameAttribute'']);
      if (firstName!=null || lastName!= null || args[''resetFhirValue''])
      {
        output.getNameFirstRep().setGiven(null);
      }
      if (firstName != null)
      {
        output.getNameFirstRep().addGiven(firstName);
      }
      if (middleName != null)
      {
        output.getNameFirstRep().addGiven(middleName);
      }
    }
  }
}
if (args[''birthDateAttribute''] != null)
{
  var birthDate = input.getValue(args[''birthDateAttribute'']);
  if ((birthDate != null) || args[''resetFhirValue''])
  {
    output.setBirthDateElement(dateTimeUtils.getPreciseDateElement(birthDate));
  }
}
if (args[''genderAttribute''] != null)
{
  var gender = input.getValue(args[''genderAttribute'']);
  if ((gender != null) || args[''resetFhirValue''])
  {
    output.setGender(genderUtils.getAdministrativeGender(gender));
  }
}
if ((args[''addressTextAttribute''] != null) && (output.getAddress().size() < 2))
{
  var addressText = input.getValue(args[''addressTextAttribute'']);
  if (((addressText != null) || args[''resetFhirValue'']) && (args[''resetAddressText''] || !output.hasAddress() || canOverrideAddress(output.getAddressFirstRep())))
  {
    output.getAddress().clear();
    output.addAddress().setText(addressText);
  }
}
if (args[''motherFirstNameAttribute'']!=null || args[''motherLastNameAttribute'']!=null || args[''motherPhoneAttribute'']!=null || args[''fatherFirstNameAttribute'']!=null || args[''fatherLastNameAttribute'']!=null || args[''fatherPhoneAttribute'']!=null)
{
  output.setContact(null);
  addContact(output, args[''motherFirstNameAttribute''], args[''motherLastNameAttribute''], args[''motherPhoneAttribute''], ''FEMALE'');
  addContact(output, args[''fatherFirstNameAttribute''], args[''fatherLastNameAttribute''], args[''fatherPhoneAttribute''], ''MALE'')
}
true' WHERE id='f0a48e63-cc1d-4d02-85fa-80c7e79a5d9e' AND version=0;

INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_CONDITION');
INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_MEDICATION_REQUEST');
INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_PRACTITIONER');
INSERT INTO fhir_transform_data_type_enum VALUES('FHIR_MEASURE_REPORT');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('41d3bd48-5788-4761-8274-f8327f15fcbe', 0, 'Subject TEI Lookup', 'SUBJECT_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from subject in FHIR Resource.', 'EVALUATE', 'FHIR_RESOURCE', NULL, NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('41d3bd48-5788-4761-8274-f8327f15fcbe', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('41d3bd48-5788-4761-8274-f8327f15fcbe', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('67249e7b-4ba7-466c-a770-a78923fbf1c3', 0, '41d3bd48-5788-4761-8274-f8327f15fcbe', 'referenceUtils.getResource(input.subject, ''PATIENT'')', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('67249e7b-4ba7-466c-a770-a78923fbf1c3', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('67249e7b-4ba7-466c-a770-a78923fbf1c3', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('762b4137-a98b-4b10-a0f5-629d93e23461', 0, '41d3bd48-5788-4761-8274-f8327f15fcbe',
'Subject TEI Lookup', 'SUBJECT_TEI_LOOKUP', 'Lookup of the Tracked Entity Instance FHIR Resource from subject in FHIR Resource.');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('d48d2cab-5014-425c-830a-46936b902c27', 0, 'Condition Date Lookup', 'CONDITION_DATE_LOOKUP', 'Lookup of the exact date of the FHIR Condition.', 'EVALUATE', 'DATE_TIME', 'FHIR_CONDITION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d48d2cab-5014-425c-830a-46936b902c27', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('d48d2cab-5014-425c-830a-46936b902c27', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('0c96c2b2-3766-4b8a-8afa-1503de510eca', 0, 'd48d2cab-5014-425c-830a-46936b902c27',
'var date = null;
if (input.hasRecordedDate())
  date = dateTimeUtils.getPreciseDate(input.getRecordedDateElement());
date', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('0c96c2b2-3766-4b8a-8afa-1503de510eca', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('0c96c2b2-3766-4b8a-8afa-1503de510eca', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('43f3ee7a-b546-4dff-9aeb-a0abbd083f30', 0, 'd48d2cab-5014-425c-830a-46936b902c27',
'Condition Date Lookup', 'CONDITION_DATE_LOOKUP', 'Lookup of the exact date of the FHIR Condition.');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('19303d9f-58cd-49ad-a7db-059607b406ac', 0, 'Medication Request Date Lookup', 'MEDICATION_REQUEST_DATE_LOOKUP', 'Lookup of the exact date of the FHIR Medication Request.', 'EVALUATE', 'DATE_TIME', 'FHIR_MEDICATION_REQUEST', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('19303d9f-58cd-49ad-a7db-059607b406ac', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('19303d9f-58cd-49ad-a7db-059607b406ac', 'INPUT');
INSERT INTO fhir_script_source (id,version,script_id,source_text,source_type)
VALUES ('81ddf4f3-9be3-47dc-b823-1c23e8f999a4', 0, '19303d9f-58cd-49ad-a7db-059607b406ac',
'var date = null;
if (input.hasAuthoredOn(())
  date = dateTimeUtils.getPreciseDate(input.getAuthoredOnElement());
date', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('81ddf4f3-9be3-47dc-b823-1c23e8f999a4', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id,fhir_version)
VALUES ('81ddf4f3-9be3-47dc-b823-1c23e8f999a4', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('0af1516c-7b7c-4f23-8ade-148e4de3969b', 0, '19303d9f-58cd-49ad-a7db-059607b406ac',
'Medication Request Date Lookup', 'MEDICATION_REQUEST_DATE_LOOKUP', 'Lookup of the exact date of the FHIR Medication Request.');

INSERT INTO fhir_script (id, version, name, code, description, script_type, return_type, input_type, output_type)
VALUES ('c83f547f-f898-4647-9f47-e743809a245e', 0, 'Null GEO Location from FHIR Resource', 'EXTRACT_FHIR_RESOURCE_NULL_GEO_LOCATION',
'Extracts the GEO location null form any FHIR Resource.','EVALUATE', 'LOCATION', 'FHIR_OBSERVATION', NULL);
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('c83f547f-f898-4647-9f47-e743809a245e', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('c83f547f-f898-4647-9f47-e743809a245e', 'INPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type) VALUES ('92c15774-50cb-4dc2-8a6e-9fe7a82a8689', 0, 'c83f547f-f898-4647-9f47-e743809a245e',
'null', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('92c15774-50cb-4dc2-8a6e-9fe7a82a8689', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('92c15774-50cb-4dc2-8a6e-9fe7a82a8689', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('f6054bfc-f674-4684-aae2-83370e697483', 0, 'c83f547f-f898-4647-9f47-e743809a245e',  'Null GEO Location from FHIR Resource', 'EXTRACT_FHIR_RESOURCE_NULL_GEO_LOCATION',
'Extracts the GEO location null form any FHIR Resource.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('a3dba79b-bbc8-4335-874d-d7eb23f6c204', 0, 'TRANSFORM_ABSENT_FHIR_CONDITION', 'Transforms absence of data element to FHIR Condition', 'Transforms absence of data element to FHIR Condition.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_CONDITION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a3dba79b-bbc8-4335-874d-d7eb23f6c204', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a3dba79b-bbc8-4335-874d-d7eb23f6c204', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('a3dba79b-bbc8-4335-874d-d7eb23f6c204', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('cf0bbe54-10a1-4a33-9de7-178687f8c432', 0, 'a3dba79b-bbc8-4335-874d-d7eb23f6c204',
'output.setVerificationStatus(fhirResourceUtils.resolveEnumValue(output, ''verificationStatus'', ''entered-in-error'')); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('cf0bbe54-10a1-4a33-9de7-178687f8c432', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('cf0bbe54-10a1-4a33-9de7-178687f8c432', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('377358a0-dcd1-471a-9582-b03d4c650c79', 0, 'a3dba79b-bbc8-4335-874d-d7eb23f6c204',
        'Transforms absence of data element to FHIR Condition', 'TRANSFORM_ABSENT_FHIR_CONDITION',
        'Transforms absence of data element to FHIR Condition.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('fb8bd7f7-6a4a-46f2-867c-7109d2828dc4', 0, 'TRANSFORM_ABSENT_FHIR_MEDICATION_REQUEST', 'Transforms absence of data element to FHIR Medication request', 'Transforms absence of data element to FHIR Medication request.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_MEDICATION_REQUEST');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('fb8bd7f7-6a4a-46f2-867c-7109d2828dc4', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('fb8bd7f7-6a4a-46f2-867c-7109d2828dc4', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('fb8bd7f7-6a4a-46f2-867c-7109d2828dc4', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('495fd0d6-92c5-421a-812d-b4d4ba12469f', 0, 'fb8bd7f7-6a4a-46f2-867c-7109d2828dc4',
'output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', ''entered-in-error'')); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('495fd0d6-92c5-421a-812d-b4d4ba12469f', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('495fd0d6-92c5-421a-812d-b4d4ba12469f', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('9ffe53d8-dced-40af-8c0b-229b09919e6e', 0, 'fb8bd7f7-6a4a-46f2-867c-7109d2828dc4',
        'Transforms absence of data element to FHIR Medication request', 'TRANSFORM_ABSENT_FHIR_MEDICATION_REQUEST',
        'Transforms absence of data element to FHIR Medication request.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('372f8459-aa9a-44b9-bb8e-71a827265e52', 0, 'TRANSFORM_STATUS_FHIR_CONDITION', 'Transforms DHIS event status to FHIR Condition', 'Transforms DHIS event status to FHIR Condition.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_CONDITION');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('372f8459-aa9a-44b9-bb8e-71a827265e52', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('372f8459-aa9a-44b9-bb8e-71a827265e52', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('372f8459-aa9a-44b9-bb8e-71a827265e52', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('1c673242-e309-43c4-a690-cb42da41fbb7', 0, '372f8459-aa9a-44b9-bb8e-71a827265e52',
'output.setVerificationStatus(fhirResourceUtils.resolveEnumValue(output, ''verificationStatus'', (input.getStatus() == ''COMPLETED'' ? null : ''provisional''))); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('1c673242-e309-43c4-a690-cb42da41fbb7', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('1c673242-e309-43c4-a690-cb42da41fbb7', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('32d0c04d-3fcd-4fea-8be1-5d0c2d9ca6a5', 0, '372f8459-aa9a-44b9-bb8e-71a827265e52',
        'Transforms DHIS event status to FHIR Condition', 'TRANSFORM_STATUS_FHIR_CONDITION',
        'Transforms DHIS event status to FHIR Condition.');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('40eec24f-49bf-423f-85ac-f695a67db584', 0, 'TRANSFORM_STATUS_FHIR_MEDICATION_REQUEST', 'Transforms DHIS event status to FHIR Medication request', 'Transforms DHIS event status to FHIR Medication request.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_MEDICATION_REQUEST');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('40eec24f-49bf-423f-85ac-f695a67db584', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('40eec24f-49bf-423f-85ac-f695a67db584', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('40eec24f-49bf-423f-85ac-f695a67db584', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('3ea247a2-0b62-4eb5-b402-279e57cf9abb', 0, '40eec24f-49bf-423f-85ac-f695a67db584',
'output.setStatus(fhirResourceUtils.resolveEnumValue(output, ''status'', (input.getStatus() == ''COMPLETED'' ? null : ''draft''))); true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('3ea247a2-0b62-4eb5-b402-279e57cf9abb', 'DSTU3');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('3ea247a2-0b62-4eb5-b402-279e57cf9abb', 'R4');
INSERT INTO fhir_executable_script (id, version, script_id, name, code, description)
VALUES ('c49d4c8f-c40a-4119-8f66-2319ed1e4e5a', 0, '40eec24f-49bf-423f-85ac-f695a67db584',
        'Transforms DHIS event status to FHIR Medication request', 'TRANSFORM_STATUS_FHIR_MEDICATION_REQUEST',
        'Transforms DHIS event status to FHIR Medication request.');

UPDATE fhir_script_source SET source_text=
'var updated = false;
if (typeof output.dateElement !== ''undefined'')
{
  output.setDateElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
else if (typeof output.effective !== ''undefined'')
{
  output.setEffective(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
else if (typeof output.period !== ''undefined'')
{
  output.setPeriod(null);
  output.getPeriod().setStartElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
else if (typeof output.recordedDateElement !== ''undefined'')
{
  output.setRecordedDateElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
else if (typeof output.recordedDateElement !== ''authoredOn'')
{
  output.setAuthoredOnElement(dateTimeUtils.getDayDateTimeElement(input.getEventDate()));
  updated = true;
}
updated' WHERE id='4a0b6fde-c0d6-4ad2-89da-992f4a47a115' and version=0;

INSERT INTO fhir_resource_mapping (id, version, fhir_resource_type, imp_tei_lookup_script_id, imp_enrollment_org_lookup_script_id, imp_event_org_lookup_script_id, imp_enrollment_date_lookup_script_id, imp_event_date_lookup_script_id, imp_enrollment_geo_lookup_script_id, imp_event_geo_lookup_script_id, imp_effective_date_lookup_script_id, exp_ou_transform_script_id, exp_geo_transform_script_id, exp_date_transform_script_id, exp_absent_transform_script_id, exp_status_transform_script_id, exp_delete_when_absent, exp_tei_transform_script_id, exp_group_transform_script_id, tracked_entity_fhir_resource_type) VALUES
('4f703d2c-fa6a-49fb-8bda-26e9013d642b', 0, 'CONDITION', '762b4137-a98b-4b10-a0f5-629d93e23461', '25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', '25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', '43f3ee7a-b546-4dff-9aeb-a0abbd083f30','43f3ee7a-b546-4dff-9aeb-a0abbd083f30','f6054bfc-f674-4684-aae2-83370e697483', 'f6054bfc-f674-4684-aae2-83370e697483', 'a7b60436-9fa7-4fe4-8bf7-f5e22123a980', null, null, 'deb4fd13-d5b2-41df-9f30-0fb73b063c8b', '377358a0-dcd1-471a-9582-b03d4c650c79', '32d0c04d-3fcd-4fea-8be1-5d0c2d9ca6a5', true, 'f7863a17-86da-42d2-89fd-7f6c3d214f1b', '2347ba1f-2d5b-4276-8934-100cbdb0fcfa', 'PATIENT');

INSERT INTO fhir_resource_mapping (id, version, fhir_resource_type, imp_tei_lookup_script_id, imp_enrollment_org_lookup_script_id, imp_event_org_lookup_script_id, imp_enrollment_date_lookup_script_id, imp_event_date_lookup_script_id, imp_enrollment_geo_lookup_script_id, imp_event_geo_lookup_script_id, imp_effective_date_lookup_script_id, exp_ou_transform_script_id, exp_geo_transform_script_id, exp_date_transform_script_id, exp_absent_transform_script_id, exp_status_transform_script_id, exp_delete_when_absent, exp_tei_transform_script_id, exp_group_transform_script_id, tracked_entity_fhir_resource_type) VALUES
('db5d7a8c-ff73-430f-8e99-3005425946a9', 0, 'MEDICATION_REQUEST', '762b4137-a98b-4b10-a0f5-629d93e23461', '25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', '25a97bb4-7b39-4ed4-8677-db4bcaa28ccf', '0af1516c-7b7c-4f23-8ade-148e4de3969b','0af1516c-7b7c-4f23-8ade-148e4de3969b','f6054bfc-f674-4684-aae2-83370e697483', 'f6054bfc-f674-4684-aae2-83370e697483', 'a7b60436-9fa7-4fe4-8bf7-f5e22123a980', '416decee-4604-473a-b650-1a997d731ff0', null, 'deb4fd13-d5b2-41df-9f30-0fb73b063c8b', '9ffe53d8-dced-40af-8c0b-229b09919e6e', 'c49d4c8f-c40a-4119-8f66-2319ed1e4e5a', true, 'f7863a17-86da-42d2-89fd-7f6c3d214f1b', '2347ba1f-2d5b-4276-8934-100cbdb0fcfa', 'PATIENT');
