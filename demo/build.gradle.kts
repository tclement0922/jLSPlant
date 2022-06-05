plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val androidBuildToolsVersion: String by rootProject.extra
val androidCompileSdkVersion: Int by rootProject.extra
val androidMinSdkVersion: Int by rootProject.extra
val androidTargetSdkVersion: Int by rootProject.extra
val demoNamespace: String by rootProject.extra
val libVersionCode: Int by rootProject.extra
val libVersionName: String by rootProject.extra

android {
    compileSdk = androidCompileSdkVersion
    buildToolsVersion = androidBuildToolsVersion

    defaultConfig {
        applicationId = demoNamespace
        namespace = demoNamespace
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion
        versionCode = libVersionCode
        versionName = libVersionName
    }

    if (System.getenv("CI") != null) {
        signingConfigs {
            val keystoreFile = file("${System.getenv("RUNNER_TEMP")}/keystore/jlsplant.jks")
            maybeCreate("debug").apply {
                storeFile = keystoreFile
                storePassword = System.getenv("KEYSTORE_PWD")
                keyAlias = System.getenv("KEYSTORE_DEBUG_ALIAS")
                keyPassword = System.getenv("KEYSTORE_PWD")
            }
            maybeCreate("release").apply {
                storeFile = keystoreFile
                storePassword = System.getenv("KEYSTORE_PWD")
                keyAlias = System.getenv("KEYSTORE_RELEASE_ALIAS")
                keyPassword = System.getenv("KEYSTORE_PWD")
            }
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
            if (System.getenv("CI") != null) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":jlsplant"))
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.appCompat)
    implementation(Google.android.material)
    implementation(AndroidX.constraintLayout)
}