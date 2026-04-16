@file:Suppress("DEPRECATION")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
}

android {
    namespace = "com.starterkim.widgetbuddy"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.starterkim.widgetbuddy"
        minSdk = 26
        targetSdk = 36
        versionCode = 3
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.play.services.ads.api)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Jetpack Glance (위젯 UI)
    implementation(libs.androidx.glance.appwidget)
    debugImplementation(libs.androidx.glance.appwidget.preview)
    // Jetpack WorkManager (백그라운드 작업)
    implementation(libs.androidx.work.runtime.ktx)
    // Jetpack DataStore (데이터 저장)
    implementation(libs.androidx.datastore.preferences)
    // Material3 테마 적용
    implementation(libs.androidx.glance.material3)
    // AdMob SDK
    implementation(libs.google.play.services.ads)
    implementation(libs.androidx.material.icons.extended.v178)
    implementation(libs.androidx.core.splashscreen)
    debugImplementation(libs.androidx.glance.preview)
}
