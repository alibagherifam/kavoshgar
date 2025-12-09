@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google {
            maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") {
                mavenContent {
                    includeGroupAndSubgroups("org.jetbrains.compose")
                    includeGroupAndSubgroups("org.jetbrains.androidx")
                }
            }
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") {
            mavenContent {
                includeGroupAndSubgroups("org.jetbrains.compose")
                includeGroupAndSubgroups("org.jetbrains.androidx")
            }
        }
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// Root project name and a subproject name can not being the same
// See https://github.com/gradle/gradle/issues/16608
rootProject.name = "kavoshgar-project"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":demo")
include(":kavoshgar")
