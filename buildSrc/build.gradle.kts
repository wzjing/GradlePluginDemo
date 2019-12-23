buildscript {

    extra["kotlin_version"] = "1.3.50"
    extra["gradle_version"] = "3.5.3"

    repositories {
        google()
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath(kotlin("gradle-plugin", version = project.extra["kotlin_version"].toString()))
        classpath("org.gradle.kotlin:plugins:1.3.3")
    }

}

plugins {
    id("groovy")
    id("java")
    id("org.gradle.kotlin.kotlin-dsl").version("1.3.3")
}

repositories {
    google()
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation(kotlin("stdlib-jdk8", project.extra["kotlin_version"].toString()))
    implementation("com.android.tools.build:gradle:3.5.3")
}