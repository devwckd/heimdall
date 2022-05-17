plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")

    implementation(project(":client"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
}

bukkit {
    name = "HeimdallSpigotPlugin"
    main = "me.devwckd.plugin.HeimdallSpigotPlugin"
    apiVersion = "1.18"
    author = "devwckd"
}