import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.8.20-1.0.11"
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.21"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    id("de.jensklingenberg.ktorfit") version "1.0.0"
}


configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
    version = versions.ktorfit
}

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())
val appId = properties.getProperty("appId")

android {
    namespace = "com.githukudenis.feature_weather_info"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        targetSdk = 33
        buildConfigField("String", "appId", appId)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.6"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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


dependencies {
    implementation(libs.accompanistImage)
    implementation(libs.activityCompose)
    implementation(libs.androidCore)
    implementation(libs.appCompat)

    implementation(libs.datastorePrefs)
    implementation(libs.flow)
    implementation(libs.flowLifecycle)
    implementation(libs.flowViewModel)

    implementation(libs.retroFit)
    implementation(libs.retrofitGson)
    implementation(libs.okhttpLogging)

    implementation(libs.locationProvider)
    implementation(libs.lifecycleRuntime)
    implementation(libs.lifecycleRuntimeCompose)
    implementation(libs.lifecycleViewModelCompose)
    implementation(libs.lifecycleViewModelCompose)
    implementation(libs.lifecycleViewModelKtx)
    implementation(libs.viewModelSavedState)

    ksp(libs.ktorfitKsp)
    implementation(libs.ktorFit)

    implementation(libs.ktorLogger)
    implementation(libs.ktorContentNegotiation)
    implementation(libs.ktorSerialization)
    implementation(libs.ktorSerializationKotlinx)
    implementation(libs.ktorClientCore)
    implementation(libs.ktorClientCIO)
    implementation(libs.ktorSerializationGson)

    implementation(libs.koin)
    implementation(libs.koinAndroid)
    implementation(libs.koinAnnotations)
    implementation(libs.koinCompose)
    ksp(libs.koinKsp)

    implementation(libs.compose_nav)
    implementation(libs.composeTooling)
    implementation(libs.composeUi)
    implementation(libs.composeMaterial)

    implementation(libs.timber)

    testImplementation(libs.composeJunitTest)
    testImplementation(libs.junitTest)
    testImplementation(libs.truthUnitTest)
    testImplementation(libs.jupiterUnitTest)
    testImplementation(libs.lifefycleRuntimeTesting)
    testImplementation(libs.koinJunitTest)
    testImplementation(libs.testRunner)


    androidTestImplementation(libs.coreAndroidTest)
    androidTestImplementation(libs.espressoAndroidTest)
    androidTestImplementation(libs.junitAndroidTest)
    androidTestImplementation(libs.truthAndroidTest)

    debugImplementation(libs.composeUiTestManifest)
    debugImplementation(libs.composeUiDebugTooling)
}