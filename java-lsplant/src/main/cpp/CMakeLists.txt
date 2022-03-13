#
# This file is part of Java-LSPlant.
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 3 of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public License
# along with this program; if not, see <https://www.gnu.org/licenses/>
#
# Copyright (C) 2022 T. Clément <https://github.com/tclement0922>
#

cmake_minimum_required(VERSION 3.18.1)

project("java_lsplant")

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_library(java_lsplant SHARED java_lsplant.cpp pine/elf_img.cpp)

find_package(dobby REQUIRED CONFIG)
find_package(lsplant REQUIRED CONFIG)

target_link_libraries(java_lsplant log dobby::dobby lsplant::lsplant)
