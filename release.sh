#!/bin/bash

release=$1
cp app/build/outputs/apk/release/app-armeabi-v7a-release.apk v2ray-armeabi-v7a-${release}.apk
cp app/build/outputs/apk/release/app-arm64-v8a-release.apk v2ray-arm64-v8a-${release}.apk
cp app/build/outputs/apk/release/app-x86-release.apk v2ray-x86-${release}.apk
cp app/build/outputs/apk/release/app-x86_64-release.apk v2ray-x86_64-${release}.apk
cp app/build/outputs/apk/release/app-universal-release.apk v2ray--universal-${release}.apk
