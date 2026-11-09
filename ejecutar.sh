#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")"
mkdir -p out
javac -encoding UTF-8 -d out @fuentes.txt
java -Dfile.encoding=UTF-8 -cp out uniquindio.Main
