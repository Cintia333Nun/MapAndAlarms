// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.kotlinKapt) apply false
    alias(libs.plugins.maps) apply false
    alias(libs.plugins.safeargs) apply false
    alias(libs.plugins.kspGradle) apply false
    alias(libs.plugins.dagger) apply false
}