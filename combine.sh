#!/bin/sh
# Joins all clips in output/<folder>/ into output/combined/<folder>_full.mp4 (default folder: arrays)
cd "$(dirname "$0")" || exit 1
mvn -q compile exec:java -Dexec.mainClass="com.lecviz.tools.CombineClips" -Dexec.args="${1:-arrays}"
