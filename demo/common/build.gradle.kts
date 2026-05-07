import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose.compiler)
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

    android {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }

        namespace = "dev.alibagherifam.kavoshgar.demo.common"
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
        minSdk = libs.versions.androidMinSdk.get().toInt()
    }

    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.lifecycle.core)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.materialIcons)
            implementation(libs.compose.preview)
            implementation(libs.compose.resources)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.logger)
            implementation(projects.kavoshgar)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
        }

        named("desktopMain").dependencies {
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}
