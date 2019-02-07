#!/usr/bin/env bash
set -e

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"
TARGET="$SCRIPTPATH/target_jni_header/"

mkdir -p $TARGET

javac -d $TARGET  src/main/java/org/jitsi/webrtcvadwrapper/WebRTCVad.java src/main/java/org/jitsi/webrtcvadwrapper/Exceptions/*.java

cd $TARGET
javah -d ../src/native/ org.jitsi.webrtcvadwrapper.WebRTCVad

rm -r $TARGET