// build.gradle.kts (project-level)
plugins {
    id("com.android.application") version "8.7.0" apply false
    alias(libs.plugins.kotlin.android) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.0") {
            exclude(group = "com.intellij", module = "annotations")
        }
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")
    }
}
