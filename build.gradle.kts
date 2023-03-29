@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
    java
    alias(libs.plugins.shadowJar)
    alias(libs.plugins.bukkitYml)
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    // Spigot
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")

    // inventory-framework & command-framework
    maven("https://jitpack.io")

    // MiniMessage
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")

    // PlaceholderAPI
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly(libs.fawe.bukkit)
    compileOnly(libs.fawe.core) { isTransitive = false }
    compileOnly(libs.lombok)
    compileOnly(libs.minimessage)
    compileOnly(libs.spigotApi)
    compileOnly(libs.placeholderApi)

    implementation(libs.commandFramework)
    implementation(libs.fastboard)
    implementation(libs.inventoryFramework)
    implementation(libs.mongodb)

    annotationProcessor(libs.lombok)
}

bukkit {
    name = "Dodgeball"
    author = "devwckd"
    version = "0.1"
    main = "me.devwckd.dodgeball.DodgeballPlugin"
    apiVersion = "1.19"
    depend = listOf("FastAsyncWorldEdit")
    softDepend = listOf("PlaceholderAPI")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}