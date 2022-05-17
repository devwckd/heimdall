plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.3")
    api("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
}