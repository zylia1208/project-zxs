#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mvn -q -Dmaven.test.skip=true package
exec java -jar target/reserve_demo-0.0.1-SNAPSHOT.jar
