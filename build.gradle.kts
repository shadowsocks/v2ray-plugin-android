plugins {
    id("com.github.ben-manes.versions") version "0.51.0"
}

buildscript {
    val kotlinVersion = "2.0.10"
    extra.set("kotlinVersion", kotlinVersion)

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}
