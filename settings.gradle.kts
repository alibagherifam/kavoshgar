pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

// Root project name and a subproject name can not being the same
// See https://github.com/gradle/gradle/issues/16608
rootProject.name = "kavoshgar-project"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":demo")
include(":kavoshgar")
include(":logger")
