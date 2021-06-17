// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion: String by project
    val androidGradlePluginVersion: String by project

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:$androidGradlePluginVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.gms:google-services:4.3.8")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }

    allprojects {
        repositories {
            google()
            mavenCentral()
        }
    }

    tasks.register("clean", Delete::class) {
        delete(rootProject.buildDir)
    }
}