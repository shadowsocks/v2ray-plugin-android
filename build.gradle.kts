// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    id("com.github.ben-manes.versions") version "0.28.0"
}

buildscript {
    val kotlinVersion = "1.3.72"
    extra.set("kotlinVersion", kotlinVersion)

    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0-rc01")
        classpath("com.github.ben-manes:gradle-versions-plugin:0.28.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
