plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.novastats.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.novastats.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
        buildConfig = true
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

    // ── Jetpack Compose ──────────────────────
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ── Room ─────────────────────────────────
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ── Hilt ─────────────────────────────────
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // ── Coroutines ───────────────────────────
    implementation(libs.kotlinx.coroutines.android)

    // ── Retrofit + OkHttp ────────────────────
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // ── Coil (images) ────────────────────────
    implementation(libs.coil.compose)

    // ── Gson ─────────────────────────────────
    implementation(libs.gson)

    // ── DataStore ────────────────────────────
    implementation(libs.androidx.datastore.preferences)

    // ── Palette ──────────────────────────────
    implementation(libs.androidx.palette.ktx)

    // ── Splash Screen ────────────────────────
    implementation(libs.androidx.core.splashscreen)

    // ── WorkManager ──────────────────────────
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)

    debugImplementation(libs.androidx.ui.tooling)
}