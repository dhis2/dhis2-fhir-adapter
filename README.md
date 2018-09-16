# DHIS2 FHIR Adapter Prototype for Demo
The adapter prototype handles for a demo the data as described by https://github.com/jamesagnew/aehin2018-demo-data. 

The adapter provides the following functions:

- Importing data from systems that uses different FHIR resource IDs than the adapter itself (creation and updates based on filters).
- Creating and updating (based on a national identifier, not on the resource ID) Tracked Entity Type "Person" from FHIR resource "Patient".
- Creating and updating events with vaccination information of the enrolled "Person".

## Mappings to DHIS2
FHIR structures may contain multiple values for one field (e.g. multiple names or addresses). This will not be displayed by the following sections. The resulting mapping configuration reduces multiple values to a single value.

The adapter uses the following mapping table to decide which rule is applicable for the mapping of the received FHIR resource.

| Purpose           | FHIR Resource | DHIS2 Resource | Applicable Script                                                                                     |
|-------------------|---------------|----------------|-------------------------------------------------------------------------------------------------------|
| Import of Persons | PATIENT       | TRACKED_ENTITY | true                                                                                                  |
| Vaccine BCG       | IMMUNIZATION  | EVENT	         | !input.notGiven && codeUtils.getCode(input.vaccineCode, 'http://example.ph/vaccine-codes')=='BCG'     |
| Vaccine MMR       | IMMUNIZATION  | EVENT	         | !input.notGiven && codeUtils.getCode(input.vaccineCode, 'http://example.ph/vaccine-codes')=='MMR'     |
| Vaccine OPV       | IMMUNIZATION  | EVENT	         | !input.notGiven && codeUtils.getCode(input.vaccineCode, 'http://example.ph/vaccine-codes')=='OPV'     |

The same vaccine may be assigned to multiple rules. The applicable script may decide when it is applicable for that rule. Additionally, for events the state of the event and the existence of an active enrollment in a specific program may be relevant. Also an evaluation order may be defined. All of this is evaluated when deciding if the vaccine will be assigned to a specific rule.

Furthermore, a single country may use different coding schemas for coding the same vaccine. In order to support this and to have a central vaccine administration, the adapter may offer the definition of vaccines, where one vaccine may also be assigned to multiple coding schemas. Such a feature would allow a simplification and a reusability of scripts. It has not yet been implemented and it has not been decided if and when such a feature will be integrated.

    !input.notGiven && codeUtils.containsCode(input.vaccineCode, 'VACCINE_BCG')
    
The script above could check multiple vaccine codes at the same time.

To make scripts even more reusable, passing arguments to scripts would increase the reusability immensely. It has not yet been implemented and it has not been decided if and when such a feature will be integrated.

    !input.notGiven && codeUtils.containsCode(input.vaccineCode, args['immunizationCode'])

The script above could be stored once and could be referenced by the rules. The rules must specify the values of the arguments that are required by the script (in this case the value of argument "immunizationCode").

### FHIR Patient
| FHIR Structure           | DHIS2 Tracked Entity Name | Tracked Entity Attribute Name | Required | Unique |
|--------------------------|---------------------------|-------------------------------|----------|--------|
| Patient.name.family      | Person                    | First name                    | No       | No     |
| Patient.name.given       | Person                    | Last name                     | No       | No     |
| Patient.birthDate        | Person                    | Birth date                    | No       | No     |
| Patient.identifier.value | Person                    | National identifier           | No       | Yes    |
| Patient.gender           | Person                    | Gender                        | No       | No     |
| Patient.address.line     | Person                    | Address line                  | No       | No     |
| Patient.address.city     | Person                    | City                          | No       | No     |
| Patient.address.state    | Person                    | State of country              | No       | No     |
| Patient.address.country  | Person                    | Country                       | No       | No     |

For mapping the following mapping script is used. Data type conversions are made by the transformation engine that is included in the adapter.

    output.organizationUnitId = organizationUtils.getOrganizationUnitId( input.managingOrganization, 'http://example.ph/organizations' );
    output.setValueByName( 'National identifier', identifierUtils.getIdentifier( input, 'http://example.ph/national-patient-id' ) );
    output.setValueByName( 'Last name', humanNameUtils.getPrimaryName( input.name ).family );
    output.setValueByName( 'First name', humanNameUtils.getSingleGiven( humanNameUtils.getPrimaryName( input.name ) ) );
    output.setValueByName( 'Birth date', input.birthDate );
    output.setValueByName( 'Gender', input.gender );
    output.setValueByName( 'Address line', addressUtils.getSingleLine( addressUtils.getPrimaryAddress( input.address ) ) );
    output.setValueByName( 'City', addressUtils.getPrimaryAddress( input.address ).city );
    output.setValueByName( 'State of country', addressUtils.getPrimaryAddress( input.address ).state );
    output.setValueByName( 'Country', addressUtils.getPrimaryAddress( input.address ).country );
    output.coordinates = geoUtils.getLocation( addressUtils.getPrimaryAddress( input.address ) );
    true;

In order to increase the readability of the mapping, attribute names are used to map values. DHIS2 also allows to define codes for attributes optionally. If a code has been setup for an attribute, this code can be used by the mapping as well. Using codes for mappings increases the readability of the mapping, too. 

The mapping still contains custom system identifiers. The adapter may support in the future a central system identifier configuration that allows more reusable mappings. In such a case the system 'http://example.ph/organizations' could be replaced by a reference to a code (e.g. ORGANIZATION).

The birth date that is included in the FHIR resource Patient has a maximum precision of the exact day and a minimum precision of a year. The following listing contains only some examples how to deal with the variable precision (also a combination of them may be viable). How this must be handled depends on the use case.
- It is known that the connected systems and applications only provide a birth date with the precision of a day. In this case the mapping provides the exact birth date of a Patient in DHIS2.
- That the birth date has a variable precision and may not be accurate is not relevant for the use case.
- The birth date is not set in DHIS2 if the precision is less than a day (can be checked and handled by the mapping). Such a behavior could also be configured as a general behavior of the adapter.
- The precision (day, month or year) of the birth date is stored in an additional attribute in DHIS2.
- The birth date (regardless of its precision) is stored as it is in an additional text attribute in DHIS2.

### FHIR Immunization
For the mapping of the value of "CP - MCH BCG dose" (included in Program Stage "Birth" of Program "Child Programme") the following script is used.

    output.setValueByName( 'CP - MCH BCG dose', true ); 
    true;

In order to increase the readability of the mapping, names are used to map values. 

### FHIR Organization
The organization resource is only included in the transaction bundle to include a reference to the national identifier of the organization (must be the organization code). Since the organization will not include the parent organization (FHIR Organization.partOf) it cannot be used to create or update an organization unit on DHIS2. This would also require that the complete hierarchy up to the root organization is included and updated in the transaction bundle (due to required references of national identifiers). The prototype adapter will therefore ignore the update request of the organization and return a status of 200.

## Resource ID Handling
The implementation for the demo does not generate resource IDs when it receives requests. For the purpose of the demo it is not relevant. In general the adapter must generate an identifier that contains the rule that processed the input data and the identifier of the affected DHIS2 resource. This is absolutely required when enabling FHIR read access. 

    8ab213cfa023bcd4-EVa3kGcGDCuk6
    
The above contains an example of such a generated resource ID. It contains the ID 8ab213cfa023bcd4 of the mapping rule, the type EV (event) of the DHIS2 resource and the ID a3kGcGDCuk6 of the DHIS2 event.   

## Implementation Notes
- The prototype may only contain technical and functional implementations that are absolutely required for the demo (even if in most cases more than required for the demo).
- The prototype may contain a read-only metadata database that contains the mappings.
- The prototype may not be optimized (e.g. may not use caching).
- The prototype may not be unit tested.
- The prototype may not be able to handle multiple FHIR versions at the same time.
