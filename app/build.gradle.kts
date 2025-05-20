import com.sqwerty.res_guard.configureResGuard

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("sq.res-guard")
}

configureResGuard {
    enabled = true
}

android {
    namespace = "com.sqwerty"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sqwerty"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}