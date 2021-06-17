plugins {
    id ("com.android.application")
    id ("kotlin-android")
//    id ("org.jetbrains.kotlin.plugin.serialization") version kotlin_version
    id ("com.google.gms.google-services")
    id ("kotlin-kapt")
}

apply ("passwords.gradle")

val googleMapsKey: String by project
val kotlinVersion: String by project
val roomVersion: String by project
val lifecycleVersion: String by project
val fragmentVersion: String by project
val timberVersion: String by project
val coroutinesVersion: String by project
val navigation: String by project

android {
    signingConfigs {
        create("release") {
        }
    }
    compileSdkVersion (30)
    buildToolsVersion ("30.0.3")

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    defaultConfig {
        applicationId ("com.psvoid.whappens")
        minSdkVersion (21)
        targetSdkVersion (30)
        versionCode (1)
        versionName ("1.0")
        multiDexEnabled = true

        testInstrumentationRunner ("androidx.test.runner.AndroidJUnitRunner")

        manifestPlaceholders  (mapOf("google_maps_key" to googleMapsKey))
//        buildConfigField ("String", "EVENTFUL_KEY", "\"$eventful_key\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {

        }
    }

    compileOptions {
        sourceCompatibility (JavaVersion.VERSION_1_8)
        targetCompatibility (JavaVersion.VERSION_1_8)
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    // Tests
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.2")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.3.0")

    implementation ("com.android.support:multidex:1.0.3")

    // Data binding
    kapt ("com.android.databinding:compiler:3.2.0")

    // Material
    implementation ("com.google.android.material:material:1.3.0")

    implementation ("androidx.appcompat:appcompat:1.3.0")
    implementation ("androidx.core:core-ktx:1.5.0")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")

    implementation ("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation ("androidx.fragment:fragment-ktx:$fragmentVersion")

    // Navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:$navigation")
    implementation ("androidx.navigation:navigation-ui-ktx:$navigation")

    // Room
    implementation ("androidx.room:room-runtime:$roomVersion")
    implementation ("androidx.room:room-ktx:$roomVersion")
    kapt ("androidx.room:room-compiler:$roomVersion")

    // Maps
    implementation ("com.google.maps.android:android-maps-utils:2.2.3")
    implementation ("com.google.android.gms:play-services-maps:17.0.1")
    //implementation 'com.google.android.gms:play-services-location:17.0.0'

    // Firebase
//    implementation 'com.google.firebase:firebase-analytics:17.4.0'
//    implementation 'com.google.firebase:firebase-storage:19.1.1'
//    implementation 'com.google.firebase:firebase-messaging:20.1.6'
    implementation ("com.google.firebase:firebase-database-ktx:20.0.0")
//    implementation 'com.google.firebase:firebase-auth:19.3.1'
//    implementation 'com.google.firebase:firebase-config:19.1.4'
//    implementation 'com.firebaseui:firebase-ui-auth:6.2.1'

    // Retrofit Coroutines Support
//    implementation "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2"
    implementation ("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
//    implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1"

    implementation ("com.jakewharton.timber:timber:$timberVersion")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

}
