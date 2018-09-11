# DHIS2 FHIR Adapter Prototype for Demo
The adapter prototype handles for a demo the data as described by https://github.com/jamesagnew/aehin2018-demo-data. 

__The source code is still under development and must still be extended to support all features that are required for the demo!__
## Mappings to DHIS2
FHIR structures may contain multiple values for one field (e.g. multiple names or addresses). This will not be displayed by the following sections. The resulting mapping configuration reduces multiple values to a single value.
### FHIR Organization
The organization resource is only included in the transaction bundle to include a reference to the national identifier of the organization (must be the organization code). Since the organization will not include the parent organization (FHIR Organization.partOf) it cannot be used to create or update an organization unit on DHIS2. This would also require that the complete hierarchy up to the root organization is included and updated in the transaction bundle (due to required references of national identifiers). The prototype adapter will therefore ignore the update request of the organization and return a status of 200.
### FHIR Patient
| FHIR Structure           | DHIS2 Tracked Entity Name | Tracked Entity Attribute Name | Required | Unique |
|--------------------------|---------------------------|-------------------------------|----------|--------|
| Patient.name.family      | Patient                   | First name                    | No       | No     |
| Patient.name.given       | Patient                   | Last name                     | No       | No     |
| Patient.birthDate        | Patient                   | Birth date                    | No       | No     |
| Patient.identifier.value | Patient                   | National identifier           | Yes      | Yes    |
| Patient.gender           | Patient                   | Gender                        | No       | No     |
| Patient.address.line     | Patient                   | Street                        | No       | No     |
| Patient.address.city     | Patient                   | City                          | No       | No     |
| Patient.address.state    | Patient                   | State of country              | No       | No     |
| Patient.address.country  | Patient                   | Country                       | No       | No     |

The geo coordinates of the address of the Patient will be assigned to an internal field when mapping. 
### FHIR Immunization
_The mapping needs to be defined when the DHIS2 program and program stages are available._
## Implementation Notes
- The prototype may only contain technical and functional implementations that are absolutely required for the demo.
- The prototype may contain a read-only metadata database that contains the mappings.
- The prototype may not be optimized (e.g. may not use caching).
- The prototype may not be unit tested.
- The prototype may not be able to handle multiple FHIR versions at the same time.
