plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.dreamsecurity.magicvkeypad"
    compileSdk = 35

    defaultConfig {
        minSdk = 14
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {
    val libsDir = fileTree(mapOf("dir" to "libs"))

    api(libsDir.matching { include("MagicVKeypad_*.jar") })

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.6.1")
}