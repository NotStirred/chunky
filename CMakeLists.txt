cmake_minimum_required(VERSION 3.18)
project(chunky)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_BUILD_TYPE Debug)

include_directories(/usr/lib/jvm/java-11-openjdk-amd64/include)
include_directories(/usr/lib/jvm/java-11-openjdk-amd64/include/linux)
include_directories(chunky/src/java/se/llbit/native)

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY_DEBUG "${CMAKE_CURRENT_SOURCE_DIR}")
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY_RELEASE "${CMAKE_CURRENT_SOURCE_DIR}")

add_library(chunky SHARED
        chunky/src/java/se/llbit/native/se_llbit_math_bvh_BinaryBVH.cpp chunky/src/java/se/llbit/native/se_llbit_math_bvh_BinaryBVH.h
        chunky/src/java/se/llbit/native/util/util.h
)