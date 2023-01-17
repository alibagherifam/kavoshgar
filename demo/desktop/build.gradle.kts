import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.composeMultiplatform)
}

kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        named("jvmMain") {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":logger"))
                implementation(project(":demo:common"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.alibagherifam.kavoshgar.demo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "KavoshgarDemo"
            packageVersion = "1.0.0"

            // Metadata
            version = "1.0.0"
            description = "Demonstrates features of Kavoshgar library"
            copyright = "© 2023 Ali Bagherifam. All rights reserved."
            licenseFile.set(project.file("LICENSE"))

            windows {
                menuGroup = packageName
                perUserInstall = true
                dirChooser = true

                val iconPath = "src/jvmMain/resources/images/ic_launcher_windows"
                iconFile.set(project.file(iconPath))

                // a unique ID, which enables users to update an app via installer, when an
                // updated version is newer, than an installed version. The value must
                // remain constant for a single application. See the links for more details:
                // https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "AF792DA6-2EA3-495A-95E5-C3C6CBCB9948"
            }
        }
    }
}
