# DHIS2 FHIR Adapter
This repository contains the source code of the DHIS2 FHIR Adapter. The current scope of the Adapter is to import data into DHIS2 Tracker by using FHIR Subscriptions.

__The Adapter is still under development and the repository contains the development source code.__ The original prototype version that has been shown on a demo on AeHIN 2018 has been tagged with aehin2018-demo. 

## Running the Adapter in Development Environment
### Dependent Software Components
#### DHIS2
DHIS 2.29 must be installed and the DHIS2 demo DB with data for Sierra Leone can be used. No additional setup is required for the Adapter when using this data.

In order to use the Adapter without any configuration change, DHIS2 Web API Endpoints should be accessible on http://localhost:8080/api.

#### FHIR Service
A FHIR Service that provides the FHIR Endpoints and also supports FHIR Subscriptions is required. Also FHIR Includes must be supported currently. HAPI FHIR JPA Server Example 3.5.0 or later can be used. Instructions on how to setup the FHIR Service can be found at http://hapifhir.io/doc_jpa.html.

Three subscriptions must be setup on the FHIR Service. The adapter works with FHIR web hook subscriptions that do not need a payload. If the FHIR Service does not support this or there are any issues (like any kind of bugs or FHIR service does not behave as
 described by FHIR specification), a payload can be added optionally (payload will be ignored by the adapter). The values listed below do not need to be changed. The shell script command snippets below assume that FHIR Endpoints of HAPI FHIR JPA Server 
 Example can be reached on http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3 (HAPI FHIR JPA Server Example WAR has been placed in a Servlet Container that listens on port 8082 on the local machine) and the adapter can be reached on 
 http://localhost:8081/ (Adapter WAR has been placed in a Servlet Container that listens on port 8081 on the local machine).

Creating Subscription for FHIR Resource Patient:

    curl -XPOST http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3/Subscription -i -H 'Content-Type: application/json' -d \
        '{
              "resourceType": "Subscription",
              "criteria": "Patient?",
              "channel": {
                  "type": "rest-hook",
                  "endpoint": "http://localhost:8081/remote-fhir-web-hook/73cd99c5-0ca8-42ad-a53b-1891fccce08f/667bfa41-867c-4796-86b6-eb9f9ed4dc94",
                  "header": "Authorization: Bearer jhsj832jDShf8ehShdu7ejhDhsilwmdsgs",
                  "payload": "application/fhir+json"
              }, "status": "requested"}'

Creating Subscription for FHIR Resource Immunization:

    curl -XPOST http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3/Subscription -i -H 'Content-Type: application/json' -d \
        '{
                "resourceType": "Subscription",
                "criteria": "Immunization?",
                "channel": {
                    "type": "rest-hook",
                    "endpoint": "http://localhost:8081/remote-fhir-web-hook/73cd99c5-0ca8-42ad-a53b-1891fccce08f/a756ef2a-1bf4-43f4-a991-fbb48ad358ac",
                    "header": "Authorization: Bearer jhsj832jDShf8ehShdu7ejhDhsilwmdsgs",
                    "payload": "application/fhir+json"
                }, "status": "requested"}'
                
Creating Subscription for FHIR Resource Observation:

    curl -XPOST http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3/Subscription -i -H 'Content-Type: application/json' -d \
        '{
                "resourceType": "Subscription",
                "criteria": "Observation?",
                "channel": {
                    "type": "rest-hook",
                    "endpoint": "http://localhost:8081/remote-fhir-web-hook/73cd99c5-0ca8-42ad-a53b-1891fccce08f/b32b4098-f8e1-426a-8dad-c5c4d8e0fab6",
                    "header": "Authorization: Bearer jhsj832jDShf8ehShdu7ejhDhsilwmdsgs",
                    "payload": "application/fhir+json"
                }, "status": "requested"}'    
                
Creating Subscription for FHIR Resource Medication Requests:

    curl -XPOST http://localhost:8082/hapi-fhir-jpaserver-example/baseDstu3/Subscription -i -H 'Content-Type: application/json' -d \
        '{
                "resourceType": "Subscription",
                "criteria": "MedicationRequest?",
                "channel": {
                    "type": "rest-hook",
                    "endpoint": "http://localhost:8081/remote-fhir-web-hook/73cd99c5-0ca8-42ad-a53b-1891fccce08f/0b732310-1cca-4b0a-9510-432d4f93f582",
                    "header": "Authorization: Bearer jhsj832jDShf8ehShdu7ejhDhsilwmdsgs",
                    "payload": "application/fhir+json"
                }, "status": "requested"}'                
                               
#### PostgreSQL Database
The Adapter requires a PostgresSQL 10.0 database. 

Create a non-privileged user called dhis-fhir by invoking:

    sudo -u postgres createuser -SDRP dhis-fhir                  

For development purpose enter dhis-fhir as password at the prompt. Create a database called dhis2-fhir by invoking:

    sudo -u postgres createdb -O dhis-fhir dhis2-fhir
    
Add the uuid extension to the created database by invoking:

    sudo -u postgres psql dhis2-fhir
    
Enter the following command into the console:

    CREATE EXTENSION "uuid-ossp";    
    
Exit the console and return to your previous user with \q followed by exit.    

### Building
In order to build the adapter Java Development Kit 8 and Maven 3.2 or later is required. No additional repositories need to be configured in Maven configuration. The following command builds the artifact dhis2-fhir-adapter.war in sub-directory app/target 
and includes the configuration with sample data.

    mvn -Psample clean install

The project can also be imported into an IDE like IntelliJ IDEA ULTIMATE where it can be built automatically.

### Running
Since the database scripts are not yet versioned for every check-in into the source code repository, the application will not start when there is a database change and the database must be cleaned (just for development version). Cleaning of the database 
can be done by executing the following command in folder fhir in the console:

    mvn -Psample flyway:clean
    
The database will be reinitialized when starting the adapter.

The adapter WAR can be run with a servlet container 3.1 or later (like Apache Tomcat 8.5 or Jetty 9.3). In IntelliJ IDEA ULTIMATE also class org.dhis2.fhir.adapter.App can be used to start the Adapter as Spring Boot application without an external servlet 
container.

After successfully building the application also Maven can be used to run the application. Enter the following command in folder app in the console:

    mvn -Psample jetty:run
    
Since the created WAR file is an executable WAR file (can also be disabled when building), also the following command can be entered in folder app/target in the console:

    java -jar dhis2-fhir-adapter.war    

The project contains a sample test FHIR client that enrolls a new born child into Child Programme. The main class is named org.dhis2.fhir.adapter.DemoClient and expects three arguments. The first argument must be the code of an existing organization 
unit in which the enrollments should take place (tracker programs must have been assigned to these organization unit in maintenance section of DHIS2). The second argument is the new national identifier that will be assigned to the mother and the third 
argument is the new national identifier that will be assigned to the new born child.

The following command line arguments of the demo client can be used to create the tracked entities and enrollments into Connaught Hospital that is located in Freetown in Western Area.  

    OU_278320 93682 93683
