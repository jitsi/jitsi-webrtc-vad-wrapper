#!/usr/bin/env bash
set -e

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"
TARGET="$SCRIPTPATH/target_libfvad/"

cd $SCRIPTPATH

if  [ ! -d "libfvad" ]; then
    git clone https://github.com/dpirch/libfvad.git
fi

cd libfvad
autoreconf -i
./configure prefix=$TARGET
make
make install

cd ..
mkdir -p lib/native/linux-64
cp "$TARGET/lib/libfvad.so.0.0.0" lib/native/linux-64
mv lib/native/linux-64/libfvad.so.0.0.0 lib/native/linux-64/libfvad.so

rm -r $TARGET
