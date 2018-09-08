# PoC for DHIS2 FHIR Adapter with HAPI FHIR
The adapter uses Spring Boot 1 and HAPI FHIR 3.4 to receive the creation request of a FHIR patient. The creation of the patient results in the creation of a tracked entity (named Patient, created by a metadata update script) on DHIS2. 

__The code of the PoC is no production code! If the code is used to create production code, error handling must be integrated and it must be generalized and optimized.__

Further information:
- The code includes an example on how a FHIR observation resource is mapped to a single event enrollment. This could be mapped in a more optional way. The example just verifies the mapping of the tracked entity instance. It may not be a realistic mapping.
