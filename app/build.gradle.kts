plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "leematod.pulse"
    compileSdk = 35

    defaultConfig {
        applicationId = "leematod.pulse"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_16
        targetCompatibility = JavaVersion.VERSION_16
    }
}

dependencies {

    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.palette)

    implementation(libs.gson)
    implementation(libs.picasso)
    implementation(libs.okhttp)
}