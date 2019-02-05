#!/usr/bin/env bash
set -e

if  [ ! -d "libfvad" ]; then
    git clone git@github.com:dpirch/libfvad.git
fi

cd libfvad
autoreconf -i
./configure PREFER=../lib/native/
make
sudo make install

cd ..
cp /usr/local/lib/libfvad.so.0.0.0 lib/native/linux-64
mv lib/native/linux-64/libfvad.so.0.0.0 lib/native/linux-64/libfvad.so