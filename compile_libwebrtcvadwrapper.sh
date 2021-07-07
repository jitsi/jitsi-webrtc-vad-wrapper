#!/usr/bin/env bash
set -e

mkdir -p cmake-build lib/native/linux-64

cd cmake-build
cmake ../
make
cp libwebrtcvadwrapper.so ../lib/native/linux-64/.
cd ..
