import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hotReload)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-Xreturn-value-checker=full",
                "-Xexplicit-backing-fields"
            )
        )
    }

    jvm("desktop")

    sourceSets {
        named("desktopMain").dependencies {
            implementation(compose.desktop.currentOs)
            implementation(projects.demo.common)
        }
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
