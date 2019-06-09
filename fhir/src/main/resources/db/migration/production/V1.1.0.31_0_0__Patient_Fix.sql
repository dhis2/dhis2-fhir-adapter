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
function updateContact(firstNameAttribute, lastNameAttribute, phoneAttribute, gender, nullGender, resetDhisValue)
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
updateContact(args[''motherFirstNameAttribute''], args[''motherLastNameAttribute''], args[''motherPhoneAttribute''], ''FEMALE'', true, args[''resetDhisValue'']);
updateContact(args[''fatherFirstNameAttribute''], args[''fatherLastNameAttribute''], args[''fatherPhoneAttribute''], ''MALE'', true, args[''resetDhisValue'']);
true' WHERE id = 'b2cfaf30-6ede-41f2-bd6c-448e76c429a1' AND version = 0;
