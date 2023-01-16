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
                implementation(project(":kavoshgar"))
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.alibagherifam.kavoshgar.demo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi)
            packageName = "KavoshgarDesktop"
            packageVersion = "1.0.0"

            windows {
                menu = true
                // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "AF792DA6-2EA3-495A-95E5-C3C6CBCB9948"
            }
        }
    }
}
