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

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- BEGIN LOINC Code Descriptions
-- The following copyright applies to the following section that contains description of LOINC codes:
-- This content LOINC® is copyright © 1995 Regenstrief Institute, Inc. and the LOINC Committee, and available at no cost under the license at http://loinc.org/terms-of-use.
--
-- All descriptions must include: LOINC_NUM, COMPONENT, PROPERTY, TIME_ASPCT, SYSTEM, SCALE_TYP, METHOD_TYP, STATUS and SHORTNAME.
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Definition of vital signs - body height
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('d308a6ac-ad84-453d-9fb6-e04f6a468469', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Body height', 'LOINC_8302-2', '8302-2 / Body height / Len / Pt / ^Patient / Qn /  / ACTIVE / Body height / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('e919b1dd-733d-42d3-89f0-565e79bcf404', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Body height Est', 'LOINC_8301-4', '8301-4 / Body height / Len / Pt / ^Patient / Qn / Estimated / ACTIVE / Body height Est / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('3b82c160-758b-43d3-910f-294f784d530b', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Body height Stated', 'LOINC_3138-5', '3138-5 / Body height / Len / Pt / ^Patient / Qn / Stated / ACTIVE / Body height Stated / LOINC®');
INSERT INTO fhir_code(id, version, code_category_id, name, code, description)
VALUES ('b639e9ac-2ac8-4b06-9f21-77d157b43121', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'Body height Measured', 'LOINC_3137-7', '3137-7 / Body height / Len / Pt / ^Patient / Qn / Measured / ACTIVE / Body height Measured / LOINC®');

INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code, system_code_value, display_name)
VALUES ('ce93f668-8483-4c29-9e00-068c54010d89', 0, 'd308a6ac-ad84-453d-9fb6-e04f6a468469', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '8302-2', 'http://loinc.org|8302-2', 'Body beight');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code, system_code_value, display_name)
VALUES ('bf31b2bb-bac6-4ebd-ad45-bab96a633818', 0, 'e919b1dd-733d-42d3-89f0-565e79bcf404', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '8301-4', 'http://loinc.org|8301-4', 'Body beight Est');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code, system_code_value, display_name)
VALUES ('036c4fec-975a-404f-b99d-2702f244d5a1', 0, '3b82c160-758b-43d3-910f-294f784d530b', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '3138-5', 'http://loinc.org|3138-5', 'Body beight Stated');
INSERT INTO fhir_system_code(id, version, code_id, system_id, system_code, system_code_value, display_name)
VALUES ('afc6ff57-5d57-439b-ae33-30368519f965', 0, 'b639e9ac-2ac8-4b06-9f21-77d157b43121', 'f6e720a2-e9ff-43a8-a2fd-d5636106297b', '3137-7', 'http://loinc.org|3137-7', 'Body beight Measured');

----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
-- END LOINC Code Descriptions
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

-- Code set with all Height Observations
INSERT INTO fhir_code_set(id, version, code_category_id, name, code, description)
VALUES ('ac0af564-c3ee-4979-9ac3-d36b04ddb73d', 0, '1197b27e-3956-43dd-a75c-bfc6808dc49d', 'All Body Height Observations', 'ALL_OB_BODY_HEIGHT', 'All Body Height Observations.');
INSERT INTO fhir_code_set_value(code_set_id, code_id)
  SELECT 'ac0af564-c3ee-4979-9ac3-d36b04ddb73d', id FROM fhir_code WHERE code IN
  ('LOINC_8302-2', 'LOINC_8301-4', 'LOINC_3138-5', 'LOINC_3137-7');
UPDATE fhir_code_set_value SET preferred_export=true WHERE code_id='d308a6ac-ad84-453d-9fb6-e04f6a468469';

INSERT INTO fhir_data_type_enum VALUES('HEIGHT_UNIT');
INSERT INTO fhir_script_variable_enum VALUES ('PROGRAM_STAGE_EVENTS');

INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('07febf71-a102-408e-8c59-26d2a6536a39', 0, 'f1da6937-e2fe-47a4-b0f3-8bbff7818ee1', 'Transforms FHIR Observation Body Weight', 'TRANSFORM_FHIR_OB_BODY_WEIGHT');
INSERT INTO fhir_executable_script_argument(id, executable_script_id, script_argument_id, override_value)
VALUES ('85653f4c-f099-4072-8be8-e2713d0c865a', '07febf71-a102-408e-8c59-26d2a6536a39', '5e272692-0214-44fe-a16c-0c79f2f61917', 'false');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('ddc7c612-aabb-4ec4-bd63-6d5220cf666f', 0, '83a84bee-eadc-4c8a-b78f-8c8b9269883d', 'Transforms Body Weight FHIR Observation', 'TRANSFORM_BODY_WEIGHT_FHIR_OB');

-- Script that sets for a data element the body height with a specific height unit
INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type)
VALUES ('cafbd012-3200-4756-a849-e505aae721f7', 0, 'TRANSFORM_FHIR_OB_BODY_HEIGHT', 'Transforms FHIR Observation Body Height', 'Transforms FHIR Observation Body Height to a data element and performs height unit conversion.',
'TRANSFORM_TO_DHIS', 'BOOLEAN', 'FHIR_OBSERVATION', 'DHIS_EVENT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('cafbd012-3200-4756-a849-e505aae721f7', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('cafbd012-3200-4756-a849-e505aae721f7', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('cafbd012-3200-4756-a849-e505aae721f7', 'OUTPUT');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('3918ef5c-45c3-4f9c-94c4-e6a1fd99d4bf', 0, 'cafbd012-3200-4756-a849-e505aae721f7',
'dataElement', 'DATA_ELEMENT_REF', TRUE, NULL, 'Data element on which the body height must be set.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('60c1f864-b1c3-459a-ae0b-8efeb74dd7fc', 0, 'cafbd012-3200-4756-a849-e505aae721f7',
'override', 'BOOLEAN', TRUE, 'true', 'Specifies if an existing value should be overridden.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('ab81fa50-e9db-4ceb-b283-0364c89edf90', 0, 'cafbd012-3200-4756-a849-e505aae721f7',
'heightUnit', 'HEIGHT_UNIT', TRUE, 'CENTI_METER', 'The resulting height unit in which the value will be set on the data element.');
INSERT INTO fhir_script_argument(id, version, script_id, name, data_type, mandatory, default_value, description)
VALUES ('beef9a9c-b6ae-48ef-bfda-25ef277fc29e', 0, 'cafbd012-3200-4756-a849-e505aae721f7',
'round', 'BOOLEAN', TRUE, 'true', 'Specifies if the resulting value should be rounded.');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('0aac359d-6d1b-4b4f-ab62-6d79290e01a7', 0, 'cafbd012-3200-4756-a849-e505aae721f7',
'output.setValue(args[''dataElement''], vitalSignUtils.getHeight(input.value, args[''heightUnit''], args[''round'']), null, args[''override''], context.getFhirRequest().getLastUpdated())', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('0aac359d-6d1b-4b4f-ab62-6d79290e01a7', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('158d4ec6-d9cf-4381-9d2c-4ddb9691f8f2', 0, 'cafbd012-3200-4756-a849-e505aae721f7', 'Transforms FHIR Observation Body Height', 'TRANSFORM_FHIR_OB_BODY_HEIGHT');

INSERT INTO fhir_script (id, version, code, name, description, script_type, return_type, input_type, output_type, base_script_id)
VALUES ('8a700412-354c-4c59-aef2-e996d52c36c3', 0, 'TRANSFORM_BODY_HEIGHT_FHIR_OB', 'Transforms Body Height FHIR Observation', 'Transforms Body Height to a FHIR Observation and performs height unit conversion.',
'TRANSFORM_TO_FHIR', 'BOOLEAN', 'DHIS_EVENT', 'FHIR_OBSERVATION', 'cafbd012-3200-4756-a849-e505aae721f7');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('8a700412-354c-4c59-aef2-e996d52c36c3', 'CONTEXT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('8a700412-354c-4c59-aef2-e996d52c36c3', 'INPUT');
INSERT INTO fhir_script_variable (script_id, variable) VALUES ('8a700412-354c-4c59-aef2-e996d52c36c3', 'OUTPUT');
INSERT INTO fhir_script_source (id, version, script_id, source_text, source_type)
VALUES ('b10bf5a2-34a2-4769-a55e-c7cffc895c93', 0, '8a700412-354c-4c59-aef2-e996d52c36c3',
'output.setCode(codeUtils.getRuleCodeableConcept());
if (output.getCode().isEmpty())
{
  output.getCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
}
output.setCategory(null);
output.addCategory().addCoding().setSystem(''http://hl7.org/fhir/observation-category'').setCode(''vital-signs'').setDisplay(''Vital Signs'');
var height = input.getIntegerValue(args[''dataElement'']);
var heightUnit = vitalSignUtils.getHeightUnit(args[''heightUnit'']);
output.addChild(''valueQuantity'').setValue(args[''round''] ? Math.round(height) : height).setCode(heightUnit.getUcumCode()).setSystem(''http://unitsofmeasure.org'').setUnit(heightUnit.getUnit());
true', 'JAVASCRIPT');
INSERT INTO fhir_script_source_version (script_source_id, fhir_version)
VALUES ('b10bf5a2-34a2-4769-a55e-c7cffc895c93', 'DSTU3');
INSERT INTO fhir_executable_script (id, version, script_id, name, code)
VALUES ('e859a64f-349d-4e67-bba0-f2460e61fb6e', 0, '8a700412-354c-4c59-aef2-e996d52c36c3', 'Transforms Body Height FHIR Observation', 'TRANSFORM_BODY_HEIGHT_FHIR_OB');

UPDATE fhir_script_source SET source_text='output.setCode(codeUtils.getRuleCodeableConcept());
if (output.getCode().isEmpty())
{
  output.getCode().setText(dataElementUtils.getDataElementName(args[''dataElement'']));
}
output.setCategory(null);
output.addCategory().addCoding().setSystem(''http://hl7.org/fhir/observation-category'').setCode(''vital-signs'').setDisplay(''Vital Signs'');
var weight = input.getIntegerValue(args[''dataElement'']);
var weightUnit = vitalSignUtils.getWeightUnit(args[''weightUnit'']);
output.addChild(''valueQuantity'').setValue(args[''round''] ? Math.round(weight) : weight).setCode(weightUnit.getUcumCode()).setSystem(''http://unitsofmeasure.org'').setUnit(weightUnit.getUnit());
true' WHERE id = 'cd274a35-6f1c-495a-94ed-f935572fcac3';

UPDATE fhir_script_source SET source_text=
'function getOrganizationUnitMappedCode( organizationReference )
{
  var mappedCode = null;
  if ( organizationReference != null )
  {
    var hierarchy = organizationUtils.findHierarchy( organizationReference );
    if ( hierarchy != null )
    {
      for ( var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++ )
      {
        var code = identifierUtils.getResourceIdentifier( hierarchy.get( i ), ''ORGANIZATION'' );
        if ( code != null )
        {
          mappedCode = codeUtils.getMappedCode( code, ''ORGANIZATION'' );
          if ( (mappedCode == null) && args[''useIdentifierCode''] )
          {
            mappedCode = organizationUtils.existsWithPrefix( code );
          }
        }
      }
    }
  }
  return mappedCode;
}
var mappedCode = null;
if (input.managingOrganization)
{
  mappedCode = getOrganizationUnitMappedCode( input.managingOrganization );
}
else if (input.location)
{
  var locationReference = input.location;
  if ( locationReference != null )
  {
    var hierarchy = locationUtils.findHierarchy( locationReference );
    if ( hierarchy != null )
    {
      for ( var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++ )
      {
        var code = identifierUtils.getResourceIdentifier( hierarchy.get( i ), ''LOCATION'' );
        if ( code != null )
        {
          mappedCode = codeUtils.getMappedCode( code, ''LOCATION'' );
          if ( (mappedCode == null) && args[''useIdentifierCode''] )
          {
            mappedCode = locationUtils.existsWithPrefix( code );
          }
        }
      }
      for ( var i = 0; (mappedCode == null) && (i < hierarchy.size()); i++ )
      {
        var organizationReference = hierarchy.get( i ).getManagingOrganization();
        if ( organizationReference != null )
        {
          mappedCode = getOrganizationUnitMappedCode( organizationReference );
        }
      }
    }
  }
}
if (mappedCode == null)
{
  mappedCode = args[''defaultCode''];
}
var ref = null;
if (mappedCode != null)
{
  ref = context.createReference(mappedCode, ''CODE'');
}
if ((ref == null) && args[''useTei''] && (typeof trackedEntityInstance !== ''undefined''))
{
  ref = context.createReference(trackedEntityInstance.organizationUnitId, ''ID'');
}
ref' WHERE id='7b94feba-bcf6-4635-929a-01311b25d975' AND version=0;
