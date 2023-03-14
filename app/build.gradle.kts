@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dev.rikka.tools.refine") version "4.3.0"
}

val androidCompileSdkVersion: Int by rootProject.extra
val androidMinSdkVersion: Int by rootProject.extra
val androidTargetSdkVersion: Int by rootProject.extra
val androidSourceCompatibility: JavaVersion by rootProject.extra
val androidTargetCompatibility: JavaVersion by rootProject.extra
val kotlinJvmTarget: String by rootProject.extra

android {
    namespace = "xyz.mufanc.zsu"
    compileSdk = androidCompileSdkVersion

    defaultConfig {
        applicationId = "xyz.mufanc.zsu"
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion

        ndk {
            abiFilters.addAll(arrayOf("arm64-v8a", "x86_64"))
        }
    }

    buildFeatures {
        prefab = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = androidSourceCompatibility
        targetCompatibility = androidTargetCompatibility
    }

    kotlinOptions {
        jvmTarget = kotlinJvmTarget
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    compileOnly(project(":api-stub"))
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("dev.rikka.tools.refine:runtime:4.3.0")
    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:4.3")
}
