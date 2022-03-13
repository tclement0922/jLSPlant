cmake_minimum_required(VERSION 3.18.1)

project(dobby)

find_package(cxx REQUIRED CONFIG)
link_libraries(cxx::cxx)

add_subdirectory(upstream)