#!/usr/bin/env bash
set -e

mkdir -p tmpBuild

javac -d tmpBuild src/main/java/org/jitsi/webrtcvadwrapper/WebRTCVad.java src/main/java/org/jitsi/webrtcvadwrapper/Exceptions/*.java

cd tmpBuild/
javah -d ../src/native/ org.jitsi.webrtcvadwrapper.WebRTCVad

rm org/jitsi/webrtcvadwrapper/*.class
rm org/jitsi/webrtcvadwrapper/Exceptions/*.class
rmdir org/jitsi/webrtcvadwrapper/Exceptions
rmdir org/jitsi/webrtcvadwrapper
rmdir org/jitsi
rmdir org
cd ..
rmdir tmpBuild
