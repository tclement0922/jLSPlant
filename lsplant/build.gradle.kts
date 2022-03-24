/**
 * This file is part of Java-LSPlant.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>
 *
 * Copyright (C) 2022 T. Clément <https://github.com/tclement0922>
 */

val androidBuildToolsVersion: String by rootProject.extra
val androidCmakeVersion: String by rootProject.extra
val androidCompileSdkVersion: Int by rootProject.extra
val androidMinSdkVersion: Int by rootProject.extra
val androidNdkVersion: String by rootProject.extra
val androidTargetSdkVersion: Int by rootProject.extra
val lsplantSrcPrefix = "src/main/upstream/lsplant"

plugins {
    id("com.android.library")
}

android {
    compileSdk = androidCompileSdkVersion
    buildToolsVersion = androidBuildToolsVersion

    defaultConfig {
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion

        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                abiFilters("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                val flags = arrayOf(
                    "-Wall",
                    "-Werror",
                    "-Qunused-arguments",
                    "-Wno-gnu-string-literal-operator-template",
                    "-fno-rtti",
                    "-fvisibility=hidden",
                    "-fvisibility-inlines-hidden",
                    "-fno-exceptions",
                    "-fno-stack-protector",
                    "-fomit-frame-pointer",
                    "-Wno-builtin-macro-redefined",
                    "-ffunction-sections",
                    "-fdata-sections",
                    "-Wno-unused-value",
                    "-Wl,--gc-sections",
                    "-D__FILE__=__FILE_NAME__",
                    "-Wl,--exclude-libs,ALL",
                )
                cppFlags("-std=c++20", *flags)
                cFlags("-std=c18", *flags)
                val configFlags = arrayOf(
                    "-Oz",
                    "-DNDEBUG"
                ).joinToString(" ")
                arguments(
                    "-DANDROID_STL=c++_shared",
                    "-DCMAKE_CXX_FLAGS_RELEASE=$configFlags",
                    "-DCMAKE_CXX_FLAGS_RELWITHDEBINFO=$configFlags",
                    "-DCMAKE_C_FLAGS_RELEASE=$configFlags",
                    "-DCMAKE_C_FLAGS_RELWITHDEBINFO=$configFlags",
                    "-DDEBUG_SYMBOLS_PATH=${project.buildDir.absolutePath}/symbols/$name",
                )
            }
        }
    }

    sourceSets {
        getByName("main") {
            manifest.srcFile("$lsplantSrcPrefix/src/main/AndroidManifest.xml")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    externalNativeBuild {
        cmake {
            path = file("$lsplantSrcPrefix/src/main/jni/CMakeLists.txt")
            version = androidCmakeVersion
        }
    }

    buildFeatures {
        buildConfig = false
        prefabPublishing = true
        androidResources = false
    }

    packagingOptions {
        jniLibs {
            excludes += "**.so"
        }
    }

    prefab {
        register("lsplant") {
            headers = "$lsplantSrcPrefix/src/main/jni/include"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = false
    }

    ndkVersion = androidNdkVersion
}
