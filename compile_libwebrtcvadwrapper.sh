#!/usr/bin/env bash

mkdir -p cmake-build

cd cmake-build
cmake ../
make
cp libwebrtcvadwrapper.so ../lib/native/linux-64/.
cd ..
