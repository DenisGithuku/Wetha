plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.8.20-1.0.11"
    id("kotlin-parcelize")
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    id("de.jensklingenberg.ktorfit") version "1.0.0"
}


configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
    version = versions.ktorfit
}

android {
    namespace = "com.githukudenis.feature_weather_info"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}


dependencies {
    ksp("de.jensklingenberg.ktorfit:ktorfit-ksp:${versions.ktorfit}")
    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:${versions.ktorfit}")
}