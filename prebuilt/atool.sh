#!/usr/bin/env sh

base=/data/local/tmp

adb push atool atool.jar $base > /dev/null

shift
adb shell $base/atool "$@"
