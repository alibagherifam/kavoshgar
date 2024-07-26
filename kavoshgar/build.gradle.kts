plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

group = "dev.alibagherifam.kavoshgar"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(projects.logger)
    implementation(libs.kotlinx.coroutines.core)
}
