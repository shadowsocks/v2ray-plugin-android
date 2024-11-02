import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    val javaVersion = JavaVersion.VERSION_1_8
    compileSdk = 35
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    kotlinOptions.jvmTarget = javaVersion.toString()
    namespace = "com.github.shadowsocks.plugin.v2ray"
    defaultConfig {
        minSdk = 23
        targetSdk = 35
        versionCode = 1030300
        versionName = "1.3.3"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    splits {
        abi {
            isEnable = true
            isUniversalApk = true
        }
    }
    sourceSets.getByName("main").jniLibs.srcDirs(files("$projectDir/build/go"))
    ndkVersion = "27.2.12479018"
    packagingOptions.jniLibs.useLegacyPackaging = true
}

tasks.register<Exec>("goBuild") {
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
        println("Warning: Building on Windows is not supported")
    } else {
        executable("/bin/bash")
        args("go-build.bash", android.defaultConfig.minSdk)
        environment("ANDROID_HOME", android.sdkDirectory)
        environment("ANDROID_NDK_HOME", android.ndkDirectory)
    }
}

tasks.whenTaskAdded {
    when (name) {
        "mergeDebugJniLibFolders", "mergeReleaseJniLibFolders" -> dependsOn("goBuild")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("androidx.preference:preference:1.2.1")
    implementation("com.github.shadowsocks:plugin:2.0.1")
    implementation("com.takisoft.preferencex:preferencex-simplemenu:1.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
