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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
        release {
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
    implementation(AndroidX.appCompat)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.core.ktx)
    implementation(Google.android.material)
    androidTestImplementation(AndroidX.test.espresso.core)
    androidTestImplementation(AndroidX.test.ext.junitKtx)
    androidTestImplementation(AndroidX.test.runner)
}