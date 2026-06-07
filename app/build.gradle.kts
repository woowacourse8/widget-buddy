@file:Suppress("DEPRECATION")

import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
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
        debug {
            manifestPlaceholders["ADMOB_APP_ID"] = "ca-app-pub-3940256099942544~3347511713"
            buildConfigField("String", "ADMOB_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/5224354917\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            // local.properties에서 실제 ID 로드 (보안 및 전문적인 관리 방식)
            val properties = Properties()
            val localPropertiesFile = project.rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                properties.load(localPropertiesFile.inputStream())
            }

            val appId = properties.getProperty("RELEASE_ADMOB_APP_ID") ?: "ca-app-pub-4729200165720419~3118203992"
            val adUnitId = properties.getProperty("RELEASE_ADMOB_AD_UNIT_ID") ?: "ca-app-pub-4729200165720419/7331412876"

            manifestPlaceholders["ADMOB_APP_ID"] = appId
            buildConfigField("String", "ADMOB_AD_UNIT_ID", "\"$adUnitId\"")

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
        buildConfig = true
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
    implementation(libs.androidx.appcompat)
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
