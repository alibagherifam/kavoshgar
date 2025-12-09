import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hotReload)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    compilerOptions {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        optIn.addAll(
            listOf(
                "kotlinx.coroutines.ExperimentalCoroutinesApi",
                "androidx.compose.foundation.ExperimentalFoundationApi",
                "androidx.compose.material3.ExperimentalMaterial3Api",
                "kotlin.time.ExperimentalTime"
            )
        )
    }

    androidTarget {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    jvm("desktop")

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    dependencies {
        implementation(libs.androidx.lifecycle.core)
        implementation(libs.androidx.lifecycle.viewmodel)
        implementation(libs.compose.foundation)
        implementation(libs.compose.material3)
        implementation(libs.compose.preview)
        implementation(libs.compose.resources)
        implementation(libs.compose.runtime)
        implementation(libs.compose.ui)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.datetime)
        implementation(libs.logger)
        implementation(projects.kavoshgar)
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.ktx)
            implementation(libs.kotlinx.coroutines.android)
        }

        named("desktopMain").dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

android {
    namespace = "dev.alibagherifam.kavoshgar.demo"
    compileSdk = libs.versions.androidCompileSdk.get().toInt()

    defaultConfig {
        applicationId = "dev.alibagherifam.kavoshgar.demo"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        androidResources {
            @Suppress("UnstableApiUsage")
            localeFilters += setOf("en", "fa")
        }

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt")
            )
        }
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/LICENSE.md",
                "/META-INF/LICENSE-notice.md",
                "/META-INF/INDEX.LIST"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

compose.desktop {
    application {
        mainClass = "dev.alibagherifam.kavoshgar.demo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "KavoshgarDemo"
            packageVersion = "1.0.0"

            // Metadata
            version = "1.0.0"
            description = "Demonstrates features of Kavoshgar library"
            copyright = "© 2023 Ali Bagherifam. All rights reserved."
            licenseFile = project.file("LICENSE")

            windows {
                menuGroup = packageName
                perUserInstall = true
                dirChooser = true

                val iconPath = "src/jvmMain/resources/images/ic_launcher_windows"
                iconFile = project.file(iconPath)

                // a unique ID, which enables users to update an app via installer, when an
                // updated version is newer, than an installed version. The value must
                // remain constant for a single application. See the links for more details:
                // https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "AF792DA6-2EA3-495A-95E5-C3C6CBCB9948"
            }
        }
    }
}
