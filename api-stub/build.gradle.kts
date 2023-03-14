plugins {
    id("com.android.library")
}

val androidCompileSdkVersion: Int by rootProject.extra
val androidMinSdkVersion: Int by rootProject.extra
val androidTargetSdkVersion: Int by rootProject.extra

android {
    namespace = "hidden.api.stub"

    compileSdk = androidCompileSdkVersion
    defaultConfig {
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion
    }
}

dependencies {
    compileOnly("dev.rikka.tools.refine:annotation:4.3.0")
    annotationProcessor("dev.rikka.tools.refine:annotation-processor:4.3.0")
}
