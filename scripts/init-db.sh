#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
mariadb -uroot -plsx35936030 -e "CREATE DATABASE IF NOT EXISTS reservation_demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mariadb -uroot -plsx35936030 reservation_demo < reservation_demo.sql
mariadb -uroot -plsx35936030 reservation_demo < smart_seat_module.sql || true
