#!/usr/bin/env sh

base=/data/local/tmp

adb push atool $base > /dev/null
adb push atool.jar $base > /dev/null

adb shell $base/atool "$@"
