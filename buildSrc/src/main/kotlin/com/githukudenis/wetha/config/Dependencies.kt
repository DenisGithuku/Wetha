object versions {
    val coroutines = "1.3.9"
    val lifecycle = "2.4.0"
    val timber = "5.0.1"
    val ktor = "2.3.0"
    val koin = "3.4.0"
    val compose_nav = "2.5.3"
    val datastore = "1.0.0"
    val room = "2.5.1"
    val material = "1.0.1"
    val gson = "2.9.0"
    val compose_ui = "1.4.2"
    val accompanist = "0.28.0"
    val ktorfit = "1.1.0"
}

object libs {
    val flow = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${versions.coroutines}"
    val flowViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${versions.lifecycle}"
    val flowLifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${versions.lifecycle}"
    val timber = "com.jakewharton.timber:timber:${versions.timber}"
    val ktorLogger = "io.ktor:ktor-client-logging:${versions.ktor}"
    val koin = "io.insert-koin:koin-android:${versions.koin}"
    val compose_nav = "androidx.navigation:navigation-compose:${versions.compose_nav}"

    val appCompat = "androidx.appcompat:appcompat:1.4.1"
    val espressoAndroidTest = "androidx.test.espresso:espresso-core:3.5.1"
    val ktorfit = "de.jensklingenberg.ktorfit:ktorfit-ksp:${versions.ktorfit}"
  
    // optional - Kotlin Extensions and Coroutines support for Room
    val testRunner = "androidx.test:runner:1.5.2"
    val roomKtx = "androidx.room:room-ktx:${versions.room}"
    val jupiterUnitTest = "org.junit.jupiter:junit-jupiter:5.8.1"
    val viewModelSavedState = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${versions.lifecycle}"
    val composeUi = "androidx.compose.ui:ui:${versions.compose_ui}"
    val androidCore = "androidx.core:core-ktx:1.9.0"
    val composeTooling = "androidx.compose.ui:ui-tooling-preview:${versions.compose_ui}"
    val datastorePrefs = "androidx.datastore:datastore-preferences:${versions.datastore}"
    val composeMaterial = "androidx.compose.material3:material3:${versions.material}"
    val materialExtendedIcons =
        "androidx.compose.material:material-icons-extended:${versions.material}"
    val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${versions.lifecycle}"
    val lifecycleRuntimeCompose =
        "androidx.lifecycle:lifecycle-runtime-compose:${versions.lifecycle}"
    val lifecycleViewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${versions.lifecycle}"
    val activityCompose = "androidx.activity:activity-compose:1.6.1"
    val lifecycleViewModelCompose =
        "androidx.lifecycle:lifecycle-viewmodel-compose:${versions.lifecycle}"
    val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:1.0.0"
    val gson = "com.squareup.retrofit2:converter-gson:${versions.gson}"
    val navigationCompose = "androidx.navigation:navigation-compose:2.5.3"
    val systemUiController =
        "com.google.accompanist:accompanist-systemuicontroller:${versions.accompanist}"
    val roomRuntime = "androidx.room:room-runtime:${versions.room}"
    val glide = "com.github.bumptech.glide:compose:1.0.0-alpha.1"
    val roomCompiler = "androidx.room:room-compiler:${versions.room}"
    val accompanistImage =
        "com.google.accompanist:accompanist-drawablepainter:${versions.accompanist}"
    val junitTest = "junit:junit:4.13.2"
    val lifefycleRuntimeTesting =
        "androidx.lifecycle:lifecycle-runtime-testing:${versions.lifecycle}"
    val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4"
    val truthUnitTest = "androidx.test.ext:truth:1.5.0"
    val coreAndroidTest = "androidx.arch.core:core-testing:2.2.0"
    val truthAndroidTest = "androidx.test.ext:truth:1.5.0"
    val junitAndroidTest = "androidx.test.ext:junit:1.1.5"
    val hiltAndroidTest = "com.google.dagger:hilt-android-testing:2.28-alpha"
    val hiltAndroidCompilerTest = "com.google.dagger:hilt-android-compiler:2.38.1"
    val composeJunitTest = "androidx.compose.ui:ui-test-junit4:${versions.compose_ui}"
    val composeUiDebugTooling = "androidx.compose.ui:ui-tooling:${versions.compose_ui}"
    val composeUiTestManifest = "androidx.compose.ui:ui-test-manifest:${versions.compose_ui}"
}