plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("com.wzjing.preconfig")
}

preconfig {
    configFile.set(rootProject.file("flavors/me.yidui/config.json"))
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.2")

    defaultConfig {
        applicationId = "com.wzjing.gradleplugin"
        minSdkVersion(18)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                file("proguard-rules.pro")
            )
        }

    }
    applicationVariants.forEach { variant ->
        println("variant : ${variant.name}")
        variant.outputs.forEach { variantOutput ->
            (variantOutput as com.android.build.gradle.internal.api.BaseVariantOutputImpl).apply {
                outputFileName =
                    "$outputFileName-${variant.buildType}-${variant.flavorName}-${variant.versionName}.apk"
            }
        }
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation(kotlin("stdlib-jdk7", rootProject.extra["kotlin_version"].toString()))
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}
