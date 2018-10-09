#!/usr/bin/env bash

mkdir -p tmpBuild

javac -d tmpBuild src/main/java/webrtcvadwrapper/*.java

cd tmpBuild/
javah -d ../src/native/ webrtcvadwrapper.WebRTCVad

rm webrtcvadwrapper/*.class
rmdir webrtcvadwrapper
cd ..
rmdir tmpBuild
