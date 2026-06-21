#!/usr/bin/env bash
set -euo pipefail
URL="${1:-http://127.0.0.1:9099/}"
curl -fsSI --max-time 10 "$URL" | head -n 1
