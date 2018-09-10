#!/bin/bash

if [ "$#" -ne 1 ]
then
  echo "Syntax: $0 DHIS2_BASE_URL"
  exit 1
fi
DHIS2_BASE_URL="$1"

exec curl -s -S -i -X POST -H "Content-Type: application/json" -d @dhis2-metadata-update.json "$DHIS2_BASE_URL/api/metadata?identifier=AUTO&mergeMode=MERGE&importStrategy=CREATE_AND_UPDATE"
