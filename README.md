# DHIS2 FHIR Adapter Prototype for Demo
The adapter prototype handles data for a demo and can be seen as a proof of concept that rules and transformations can be used to bring data from FHIR into DHIS Tracker.

The adapter provides the following functions:

- Accepting subscription web hook notifications from a remote FHIR server.
- Importing data from systems that use different FHIR resource IDs than the adapter itself.
- Creating and updating (based on a national identifier, not on the resource ID) Tracked Entity Type "Person" from FHIR resource "Patient".
- Enrolling a "Person" into the child immunization program when it is younger than one year and receives one of the vaccines that is handled by the program.
- Enrolling a female "Person" into the WHO RMNCH Tracker program when it is older than fifteen years and has a last menstrual period observation and any supported data in the 1st ANC program stage is delivered.
- Creating and updating events with vaccination, observation information of the enrolled "Person".

The mapping process uses a flexible and configurable rule based data transformation. 

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

Furthermore, a single country may use different coding schemas for coding the same vaccine. In order to support this and to have a central vaccine administration, the adapter may offer the definition of vaccines, where one vaccine may also be assigned to 
multiple coding schemas. Such a feature would allow a simplification and a reusability of scripts. It has not yet been implemented but it has been decided that such a feature will be integrated.

    !input.notGiven && codeUtils.containsCode(input.vaccineCode, 'VACCINE_BCG')
    
The script above could check multiple vaccine codes at the same time with the table below.

| CODE_CATEGORY | ADAPTER_CODE  | SCHEMA                            | CODE  |
|---------------|---------------|-----------------------------------|-------|
| VACCINE       | VACCINE_BCG   | http://example.ph/vaccine-codes   | BCG   |
| VACCINE       | VACCINE_BCG   | http://hospital.org/vaccine-codes | T-BCG | 
| VACCINE       | VACCINE_BCG   | http://hl7.org/fhir/sid/cvx       | CVX   |

To make scripts even more reusable, passing arguments to scripts would increase the reusability immensely. It has not yet been implemented but it has been decided that such a feature will be integrated.

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

    output.setValue( args['dataElement'], true ); 
    true;

The script above could use arguments. This would make the the script more reusable. The script could be name to "Set Data Element Yes" and could have one arguments that refers to a the code and name of a data element. This argument could be set when defining the concrete rule.

## Resource ID Handling
The implementation for the demo does not generate resource IDs when it receives requests. For the purpose of the demo it is not relevant. In general the adapter must generate an identifier that contains the rule that processed the input data and the identifier of the affected DHIS2 resource. This is absolutely required when enabling FHIR read access. To handle read access was not in the scope of the demo. But there must still be a possibility to provide it.  

    8ab213cfa023bcd4-EVa3kGcGDCuk6
    
The above contains an example of such a generated resource ID. It contains the ID 8ab213cfa023bcd4 of the mapping rule, the type EV (event) of the DHIS2 resource and the ID a3kGcGDCuk6 of the DHIS2 event.   

## Implementation Notes
- The prototype may only contain technical and functional implementations that are absolutely required for the demo (even if in most cases more than required for the demo).
- The prototype may contain an in-memory database that also contains the mappings.
- The prototype may not be optimized (e.g. may not use caching).
- The prototype may not be unit tested.
- The prototype may not be able to handle multiple FHIR versions at the same time.
- The prototype may not support extensible scripts. The concepts to get extensible scripts are outlined in this document.
- The prototype does not yet support distributed processing of FHIR subscriptions.

## Running the Demo Adapter
### Dependent Software Components
#### DHIS2
The adapter has been tested with DHIS2 2.29. There are no special requirements on the organization unit structure. A recent version of Tracker Programs "Child Programme" and "WHO RMNCH Tracker" is required. Names that are used by these programs must not 
have been changed. The tracked entity type Person must have the additional tracked entity attributes that are listed above in section [FHIR Patient](#fhir-patient). The project contains also a file metadata/dhis2-metadata-update.json that can be used to 
merge the required tracked entity attributes into the DHIS2 metadata.  

#### FHIR Service
A FHIR Service that provides the FHIR Endpoints and also supports FHIR Subscriptions is required. HAPI FHIR JPA Server Example 3.5.0 or later can be used. Instructions on how to setup the FHIR Service can be found at http://hapifhir.io/doc_jpa.html.  

Three subscriptions must be setup on the FHIR Service. The adapter works with FHIR web hook subscriptions that do not need a payload. If the FHIR Service does not support this or there are any issues (like any kind of bugs or FHIR service does not behave as
 described by FHIR specification), a payload can be added optionally (payload will be ignored by the adapter). Authorization has been disabled in the adapter configuration for the subscription. The values listed below do not need to be changed. The shell 
 script command snippets below assume that FHIR Endpoints of HAPI FHIR JPA Server Example can be reached on http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3 (HAPI FHIR JPA Server Example WAR has been placed in a Servlet Container that listens on 
 port 8082 on the local machine) and the adapter can be reached on http://localhost:8081/aehin2018-dhis2-fhir-adapter-prototype (Adapter WAR has been placed in a Servlet Container that listens on port 8081 on the local machine). 

    curl -XPOST http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3/Subscription -i -H 'Content-Type: application/json' -d \
        '{
            "resourceType": "Subscription",
            "criteria": "Patient?",
            "channel": {
                "type": "rest-hook",
                "endpoint": "http://localhost:8081/aehin2018-dhis2-fhir-adapter-prototype/remote-fhir-web-hook/73cd99c5-0ca8-42ad-a53b-1891fccce08f/667bfa41-867c-4796-86b6-eb9f9ed4dc94",
                "header": "Authorization: Bearer jhsj832jDShf8ehShdu7ejhDhsilwmdsgs",
                "payload": "application/fhir+json"
            }, 
            "status": "requested"
        }'
    			  			  
		curl -XPOST http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3/Subscription -i -H 'Content-Type: application/json' -d \
        '{
            "resourceType": "Subscription",
            "criteria": "Immunization?",
            "channel": {
                "type": "rest-hook",
                "endpoint": "http://localhost:8081/aehin2018-dhis2-fhir-adapter-prototype/remote-fhir-web-hook/73cd99c5-0ca8-42ad-a53b-1891fccce08f/a756ef2a-1bf4-43f4-a991-fbb48ad358ac",
                "header": "Authorization: Bearer jhsj832jDShf8ehShdu7ejhDhsilwmdsgs",
                "payload": "application/fhir+json"
            }, 
            "status": "requested"
        }'
    				
    curl -XPOST http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3/Subscription -i -H 'Content-Type: application/json' -d \
        '{
            "resourceType": "Subscription",
            "criteria": "Observation?",
            "channel": {
                "type": "rest-hook",
                "endpoint": "http://localhost:8081/aehin2018-dhis2-fhir-adapter-prototype/remote-fhir-web-hook/73cd99c5-0ca8-42ad-a53b-1891fccce08f/b32b4098-f8e1-426a-8dad-c5c4d8e0fab6",
                "header": "Authorization: Bearer jhsj832jDShf8ehShdu7ejhDhsilwmdsgs",
                "payload": "application/fhir+json"
            }, 
            "status": "requested"
        }'

### Building 
In order to build the adapter Java Development Kit 8 and Maven 3.2 or later is required. No additional repositories need to be configured in Maven configuration. The following command builds the artifact aehin2018-dhis2-fhir-adapter-prototype.war in sub-directory target.

`mvn clean package`

The project can also be imported into an IDE like IntelliJ IDEA ULTIMATE where it can be built automatically.

### Configuration
The adapter uses the following configuration with the specified standard values that may be overridden with environment specific values. The configuration values can be easily overridden by specifying system properties when starting the servlet container 
(like Apache Tomcat or Jetty). This way of configuring the adapter is just used for the demo. In upcoming versions of the adapter there will be configuration files in which these values can be overridden.

| Name                                          | Default Value                                               | Description                                                                                                                                   |
|-----------------------------------------------|-------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------|
| dhis2.endpoint.url                            | http://localhost:8080                                       | The base URL of the DHIS2 installation. The Web API Endpoints /api must be located below this URL.                                            |
| dhis2.endpoint.username                       | admin                                                       | The username that is used to authenticate with DHIS2.                                                                                         |
| dhis2.endpoint.password                       | district                                                    | The password that is used to authenticate with DHIS2.                                                                                         |
| dhis2.subscription.remote.baseUrl             | http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3 | The base URL of the FHIR Endpoints on the FHIR Service.                                                                                       |
| dhis2.subscription.remote.authorizationHeader | Bearer jshru38jsHdsdfy38sh38H3d                             | The authorization header value that is used to authenticate requests on FHIR endpoints. This will be ignored by HAPI FHIR JPA Server Example. |

### Running
The adapter WAR can be run with a servlet container 3.1 or later (like Apache Tomcat 8.5 or Jetty 9.3). In IntelliJ IDEA ULTIMATE also class org.dhis2.fhir.adapter.prototype.App can be used to start the adapter without an external servlet container.

The project contains a sample test FHIR client that enrolls a mother into WHO RMNCH Tracker program and a new born child into Child Programme. The main class is named org.dhis2.fhir.adapter.prototype.TestClient and expects three arguments. The first argument
 must be the code of an existing organization unit in which the enrollments should take place (tracker programs must have been assigned to these organization unit in maintenance section of DHIS2). The second argument is the new national identifier that will
 be assigned to the new born child and the third argument is the new national identifier that will be assigned to the mother.
 