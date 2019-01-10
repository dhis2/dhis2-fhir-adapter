# Setting up a demo environment with FHIR apps

First of all the setup that is described by the main README.md is required. A single Apache Tomcat 8.5 instance can be used to install most of the applications. The configuration described by this document uses an Apache Tomcat that listens on port 8080. The 
following table contains the context roots. The token SERVERNAME that is used in this document refers to the external name or IP address of the server on which the Apache Tomcat is running.

| Application                  | Context Root    |
|------------------------------|-----------------|
| DHIS 2                       | /fhir-dhis      |
| FHIR Adapter                 | /fhir-adapter   |
| FHIR Server                  | /fhir-server    |

In order to enable all features with sample data build the Adapter with the following command:

    mvn -Psample clean install
    
The sample data fits to the DHIS 2 Sierra Leone demo database. When exporting data from FHIR server an additional DHIS 2 account is required. The admin account can be replicated for this purpose. The following Adapter configuration assumes that the admin 
account has been replicated with the new username `fhir` and the corresponding password.

    dhis2.fhir-adapter:
      endpoint:
        url: http://localhost:8080/fhir-dhis
        system-authentication:
          username: fhir
          password: Fhir_1234
      sync:
        processor:
          enabled: true
          request-rate-millis: 10000

To enable the tracked entity attribute Unique ID to be used as national identifier of a patient, execute the following SQL snippet in the Adapter database. This script also sets the correct DHIS 2 username and password and corrects the URLs to the context 
roots that are listed above.

    UPDATE fhir_executable_script_argument SET override_value = 'NAME:Date of birth' WHERE id = 'dc2ba48e-d3c7-4b10-98ba-2b958aff9bbb';
    UPDATE fhir_server SET adapter_base_url = 'http://localhost:8080/fhir-dhis', dhis_username = 'fhir', dhis_password = 'Fhir_1234', remote_base_url = 'http://localhost:8080/fhir-server/baseDstu3' WHERE id = '73cd99c5-0ca8-42ad-a53b-1891fccce08f';
    UPDATE fhir_tracked_entity SET tracked_entity_identifier_ref = 'CODE:MMD_PER_ID' WHERE id = '4203754d-2177-4a44-86aa-2de31ee4c8ee';

## App Patient Browser
Clone the repository of https://github.com/smart-on-fhir/patient-browser and invoke `npm start` in the root directory. This will build the app. 

- After building copy file `build/config/stu3-open-hapi.json5` to `build/config/default.json5`.
- Edit `build/config/default.json5` and set `server.url` to http://SERVERNAME:8080/fhir-server/baseDstu3
- Edit `build/config/default.json5` and set `fhirViewer.url` to http://SERVERNAME:8080/fhir-apps/fhir-viewer/index.html

Copy the content of directory `build` to directory `webapps/fhir-apps/patient-browser` of your Apache Tomcat installation.  

## App FHIR Viewer
Clone the repository of https://github.com/smart-on-fhir/fhir-viewer and invoke `npm start` in the root directory. This will build the app. 

Copy the content of the base directory to directory `webapps/fhir-apps/fhir-viewer` of your Apache Tomcat installation.  

## SMART App Launcher
SMART app launcher is required by other apps that must be launched in a SMART context. In order to use this app it is recommended to use a provided docker container. In order to install this, execute the following command:

    sudo docker pull smartonfhir/smart-launcher
    
Afterwards you can start the app by the following command. This app will listen on port 8081. It can only be started (can be changed with some effort) on the root context and must use therefore a different port.

    sudo docker run -dit --restart unless-stopped -e FHIR_SERVER_R3='http://SERVERNAME/fhir-server/baseDstu3' -e BASE_URL='http://SERVERNAME:8081' -p 8081:80 smartonfhir/smart-launcher

## Growth Chart App
This app must be executed in context of SMART app launcher. Clone the repository of https://github.com/smart-on-fhir/growth-chart-app and invoke `npm start` in the root directory. This will build the app. 

Copy the content of the base directory to directory `webapps/fhir-apps/growth-chart-app` of your Apache Tomcat installation.  

You can enter the app by:

- Open http://SERVERNAME:8081/ in your web browser.
- Uncheck "Simulate launch within the EHR user interface".
- Select FHIR Version "R3 (STU3)"
- Enter launch URL http://SERVERNAME:8080/fhir-apps/rowth-chart-app/launch.html
- Press launch button
