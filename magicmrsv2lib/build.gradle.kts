plugins {
    id("com.android.library")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.android") version "1.9.0"
    kotlin("plugin.serialization") version "2.0.0-RC3"
}

android {
    namespace = "com.dreamsecurity.magicmrs"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    val libsDir = fileTree(mapOf("dir" to "libs"))

    api(libsDir.matching { include("MagicMRSv2_*.jar") })
    // Use compileOnly to avoid duplicate classes in the app module which provides a newer version
    compileOnly(libsDir.matching { include("jcaos-*.jar") })

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.google.android.material:material:1.6.1")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.5.1")

    // qrscan
    // CameraX core library using the camera2 implementation
    val cameraxVersion = "1.5.0-alpha03"
    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    // If you want to additionally use the CameraX Lifecycle library
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    // If you want to additionally use the CameraX VideoCapture library
    implementation("androidx.camera:camera-video:${cameraxVersion}")
    // If you want to additionally use the CameraX View class
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation("androidx.camera:camera-mlkit-vision:${cameraxVersion}")
    // If you want to additionally use the CameraX Extensions library
    implementation("androidx.camera:camera-extensions:${cameraxVersion}")
    // mlkit
    implementation("com.google.mlkit:barcode-scanning:17.3.0")
}
