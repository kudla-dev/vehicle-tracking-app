#!/bin/sh
set -eu
case "$MOTOPARK_ENVIRONMENT" in
  DEV|MOCK) ;;
  STAGE|PROD)
    if [ "$MOTOPARK_API_SCHEME" != https ] || [ -z "$MOTOPARK_API_HOST" ] || [ -z "$MOTOPARK_API_PORT" ]; then
      echo "error: Supply explicit HTTPS MOTOPARK_API_SCHEME/HOST/PORT for $MOTOPARK_ENVIRONMENT" >&2
      exit 1
    fi ;;
  *) echo "error: Missing/invalid MOTOPARK_ENVIRONMENT" >&2; exit 1 ;;
esac
