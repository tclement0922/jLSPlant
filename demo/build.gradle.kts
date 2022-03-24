plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val libVersionName: String by rootProject.extra
val libVersionCode: Int by rootProject.extra

android {
    compileSdk = 32

    defaultConfig {
        applicationId = "dev.tclement.jlsplant.demo"
        minSdk = 21
        targetSdk = 32
        versionCode = libVersionCode
        versionName = libVersionName
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    implementation(project(":java-lsplant"))
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.appCompat)
    implementation(Google.android.material)
    implementation(AndroidX.constraintLayout)
}