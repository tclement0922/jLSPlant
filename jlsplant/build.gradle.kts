/*
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

import java.net.URL

val androidBuildToolsVersion: String by rootProject.extra
val androidCmakeVersion: String by rootProject.extra
val androidCompileSdkVersion: Int by rootProject.extra
val androidMinSdkVersion: Int by rootProject.extra
val androidNdkVersion: String by rootProject.extra
val androidTargetSdkVersion: Int by rootProject.extra
val libArtifactId: String by rootProject.extra
val libGroupId: String by rootProject.extra
val libNamespace: String by rootProject.extra
val libVersionName: String by rootProject.extra

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.dokka")
    id("maven-publish")
}

android {
    compileSdk = androidCompileSdkVersion
    buildToolsVersion = androidBuildToolsVersion

    defaultConfig {
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion
        namespace = libNamespace

        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                arguments("-DANDROID_STL=c++_shared")
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
        buildConfig = false
        androidResources = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    ndkVersion = androidNdkVersion
}

dependencies {
    implementation(project(":dobby"))
    implementation(project(":lsplant"))
    dokkaPlugin("org.jetbrains.dokka:dokka-base:_")
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:_")
}

tasks.dokkaHtml.configure {
    outputDirectory.set(rootDir.resolve("docs"))
    pluginsMapConfiguration.put(
        "org.jetbrains.dokka.base.DokkaBase",
        """{
            "customStyleSheets" : ["${file("${rootDir}/docs/custom/logo-styles.css")}"],
            "customAssets" : [],
            "separateInheritedMembers": true
        }"""
    )
    dokkaSourceSets {
        named("main") {
            sourceLink {
                localDirectory.set(file("src/main/java"))
                remoteUrl.set(URL("https://github.com/tclement0922/jLSPlant/tree/main/jlsplant/src/main/java"))
                remoteLineSuffix.set("#L")
            }
            jdkVersion.set(11)
        }
    }
}

tasks.register("sourceJar", Jar::class) {
    from(android.sourceSets.getByName("main").java.srcDirs)
    archiveClassifier.set("source")
}

publishing {
    publications {
        repositories {
            maven {
                name = "LocalMaven"
                url = uri("file://${rootDir}/maven")
            }
        }
        register<MavenPublication>("release") {
            groupId = libGroupId
            artifactId = libArtifactId
            version = libVersionName
            artifact("$buildDir/outputs/aar/jlsplant-release.aar")
            artifact(tasks.getByName("sourceJar"))
        }
    }
}
