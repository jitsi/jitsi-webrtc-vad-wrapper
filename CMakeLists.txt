cmake_minimum_required(VERSION 3.10)
project(webrtcvadwrapper)

set(CMAKE_CXX_STANDARD 14)

find_package(JNI REQUIRED)

include_directories(${JNI_INCLUDE_DIRS})

set(SOURCE_FILES
    src/native/org_jitsi_webrtcvadwrapper_WebRTCVad.cpp
    src/native/org_jitsi_webrtcvadwrapper_WebRTCVad.h)

add_library(webrtcvadwrapper SHARED ${SOURCE_FILES})

find_library(FVAD_LIB fvad)
target_link_libraries(webrtcvadwrapper ${FVAD_LIB})
