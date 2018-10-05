__Source code under development and under restructuring!__

# DHIS2 FHIR Adapter
## Running with Sample Data
### HAPI FHIR JPA Server
#### Creating Subscriptions
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
                                    