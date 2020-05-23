import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import com.android.build.VariantOutput
import org.apache.tools.ant.taskdefs.condition.Os
import java.util.Locale

plugins {
    id("com.android.application")
    kotlin("android")
}

val flavorRegex = "(assemble|generate)\\w*(Release|Debug)".toRegex()
val currentFlavor get() = gradle.startParameter.taskRequests.toString().let { task ->
    flavorRegex.find(task)?.groupValues?.get(2)?.toLowerCase(Locale.ROOT) ?: "debug".also {
        println("Warning: No match found for $task")
    }
}

val minSdk = 21

android {
    val javaVersion = JavaVersion.VERSION_1_8
    compileSdkVersion(29)
    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }
    kotlinOptions.jvmTarget = javaVersion.toString()
    defaultConfig {
        applicationId = "com.github.shadowsocks.plugin.v2ray"
        minSdkVersion(minSdk)
        targetSdkVersion(29)
        versionCode = 1030100
        versionName = "1.3.1"
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
    sourceSets.getByName("main") {
        jniLibs.setSrcDirs(jniLibs.srcDirs + files("$projectDir/build/go"))
    }
}

tasks.register<Exec>("goBuild") {
    if (Os.isFamily(Os.FAMILY_WINDOWS)) {
        println("Warning: Building on Windows is not supported")
    } else {
        executable("/bin/bash")
        args("go-build.bash", minSdk)
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
    implementation(kotlin("stdlib-jdk8", rootProject.extra.get("kotlinVersion").toString()))
    implementation("androidx.preference:preference:1.1.1")
    implementation("com.github.shadowsocks:plugin:1.3.4")
    implementation("com.takisoft.preferencex:preferencex-simplemenu:1.1.0")
    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}

val abiCodes = mapOf("armeabi-v7a" to 1, "arm64-v8a" to 2, "x86" to 3, "x86_64" to 4)
if (currentFlavor == "release") android.applicationVariants.all {
    for (output in outputs) {
        abiCodes[(output as ApkVariantOutputImpl).getFilter(VariantOutput.ABI)]?.let { offset ->
            output.versionCodeOverride = versionCode + offset
        }
    }
}
