plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id ("org.jetbrains.kotlin.plugin.serialization") version "1.8.20"
    id ("de.jensklingenberg.ktorfit") version "1.0.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

android {
    namespace = "com.githukudenis.wetha"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.githukudenis.wetha"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    kotlin {
        sourceSets.main {
            kotlin.srcDir("build/generated/ksp/main/kotlin")
        }
        sourceSets.test {
            kotlin.srcDir("build/generated/ksp/test/kotlin")
        }
    }

}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    debug.set(true)
    verbose.set(true)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(true)
    enableExperimentalRules.set(true)
    disabledRules.set(setOf("final-newline", "no-wildcard-imports", "experimental:package-name", "annotation", "chain-wrapping"))
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.SARIF)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
}


configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
    version = versions.ktorfit
}

dependencies {
    implementation(project(":feature_weather_info"))
    implementation(libs.activityCompose)
    implementation(libs.appCompat)
    implementation(libs.composeMaterial)
    implementation(libs.composeTooling)
    implementation(libs.composeUi)
    implementation(libs.navigationCompose)
    implementation(libs.timber)
    implementation(libs.koin)
    implementation(libs.koinCompose)
    implementation(libs.koinAndroid)
    implementation(libs.koinAnnotations)
    ksp(libs.koinKsp)
}