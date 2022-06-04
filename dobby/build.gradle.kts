/**
 * Inspired by https://github.com/vvb2060/dobby-android/blob/master/dobby/build.gradle
 *
 * This file is part of jLSPlant.
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

plugins {
    id("com.android.library")
}

android {
    compileSdk = androidCompileSdkVersion
    buildToolsVersion = androidBuildToolsVersion

    defaultConfig {
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion
        namespace = "dev.tclement.dobby"

        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                arguments(
                    "-DCMAKE_BUILD_TYPE=Release",
                    "-DDOBBY_GENERATE_SHARED=OFF",
                    "-DDOBBY_DEBUG=OFF",
                    "-DPlugin.Android.BionicLinkerRestriction=ON",
                    "-DANDROID_STL=none",
                    "-DVERSION_REVISION=${defaultConfig.versionCode}",
                    "-DCMAKE_C_FLAGS_RELEASE=-Oz",
                    "-DCMAKE_CXX_FLAGS_RELEASE=-Oz"
                )
                cFlags(
                    "-Wno-builtin-macro-redefined",
                    "-D__FILE__=__FILE_NAME__"
                )
            }
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
            path = file("src/main/cpp/CMakeLists.txt")
            version = androidCmakeVersion
        }
    }
    buildFeatures {
        prefab = true
        prefabPublishing = true
    }
    prefab {
        register("dobby") {
            headers = "${project.buildDir}/headers/"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    ndkVersion = androidNdkVersion
}

dependencies {
    implementation("dev.rikka.ndk.thirdparty:cxx:_")
}

tasks.create("copyHeaders", Sync::class) {
    into("${project.buildDir}/headers/")
    from("src/main/cpp/upstream/include") {
        include("*.h")
    }
    from("src/main/cpp/upstream/builtin-plugin/BionicLinkerRestriction") {
        include("*.h")
    }
    from("src/main/cpp/upstream/builtin-plugin/SymbolResolver") {
        include("*.h")
    }
}

tasks.getByName("preBuild") {
    dependsOn("copyHeaders")
}
