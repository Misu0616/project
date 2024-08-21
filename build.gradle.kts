buildscript {
    repositories {
        google() // Google의 Maven 저장소
        mavenCentral() // Maven Central 저장소
    }
    dependencies {
        classpath("com.google.gms:google-services:4.4.2")
        classpath ("com.android.tools.build:gradle:7.0.2") // Gradle 버전에 따라 다를 수 있습니다.
        classpath ("com.google.gms:google-services:4.3.10") // Google 서비스 플러그인
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}