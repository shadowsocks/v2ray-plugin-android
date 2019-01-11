#!/bin/bash

function try () {
"$@" || exit -1
}

[ -z "$ANDROID_NDK_HOME" ] && ANDROID_NDK_HOME=$ANDROID_HOME/ndk-bundle
TOOLCHAIN=$(find $ANDROID_NDK_HOME/toolchains/llvm/prebuilt/* -maxdepth 1 -type d -print -quit)/bin

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
MIN_API=$1
TARGET=$DIR/bin

try mkdir -p $TARGET/armeabi-v7a $TARGET/x86 $TARGET/arm64-v8a $TARGET/x86_64

export GOPATH=$DIR

pushd $DIR/v2ray-plugin

if [ ! -f "$TARGET/armeabi-v7a/libv2ray.so" ] || [ ! -f "$TARGET/arm64-v8a/libv2ray.so" ] ||
   [ ! -f "$TARGET/x86/libv2ray.so" ] || [ ! -f "$TARGET/x86_64/libv2ray.so" ]; then

    echo "Get dependences for v2ray"
    go get -v

    echo "Cross compile v2ray for arm"
    if [ ! -f "$TARGET/armeabi-v7a/libv2ray.so" ]; then
        try env CGO_ENABLED=1 CC=$TOOLCHAIN/armv7a-linux-androideabi${MIN_API}-clang GOOS=android GOARCH=arm GOARM=7 \
            go build -ldflags="-s -w" -o client
        try $TOOLCHAIN/arm-linux-androideabi-strip client
        try mv client $TARGET/armeabi-v7a/libv2ray.so
    fi

    echo "Cross compile v2ray for arm64"
    if [ ! -f "$TARGET/arm64-v8a/libv2ray.so" ]; then
        try env CGO_ENABLED=1 CC=$TOOLCHAIN/aarch64-linux-android${MIN_API}-clang GOOS=android GOARCH=arm64 \
            go build -ldflags="-s -w" -o client
        try $TOOLCHAIN/aarch64-linux-android-strip client
        try mv client $TARGET/arm64-v8a/libv2ray.so
    fi

    echo "Cross compile v2ray for 386"
    if [ ! -f "$TARGET/x86/libv2ray.so" ]; then
        try env CGO_ENABLED=1 CC=$TOOLCHAIN/i686-linux-android${MIN_API}-clang GOOS=android GOARCH=386 \
            go build -ldflags="-s -w" -o client
        try $TOOLCHAIN/i686-linux-android-strip client
        try mv client $TARGET/x86/libv2ray.so
    fi

    echo "Cross compile v2ray for amd64"
    if [ ! -f "$TARGET/x86_64/libv2ray.so" ]; then
        try env CGO_ENABLED=1 CC=$TOOLCHAIN/x86_64-linux-android${MIN_API}-clang GOOS=android GOARCH=amd64 \
            go build -ldflags="-s -w" -o client
        try $TOOLCHAIN/x86_64-linux-android-strip client
        try mv client $TARGET/x86_64/libv2ray.so
    fi

    popd

fi

echo "Successfully build v2ray"
