#!/bin/bash

if [ "$#" -ne 1 ]
then
  echo "Syntax: $0 FILE"
  exit 1
fi
FILE="$1"

cat "$FILE" | sed $':a;N;$!ba;s/\\n/\\\\000A/g;s/\'/\\\\0027/g' | sed $'s/^/U\&\'/g;s/$/\'/g'