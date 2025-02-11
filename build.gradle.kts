plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.ksp) apply false
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}