plugins {
    val kotlinPluginVersion = "1.8.20"
    val paperPluginVersion = "1.5.6"
    val paperRuntime = "2.2.0"

    kotlin("jvm") version kotlinPluginVersion
    id("io.papermc.paperweight.userdev") version paperPluginVersion
    id("xyz.jpenilla.run-paper") version paperRuntime
}

group = "de.felix"
version = "1.0"


repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") // Use the paper-mc repository
    maven("https://repo.dmulloy2.net/repository/public/") // Protocol lib repository
}

dependencies {
    val paperVersion = "1.20.1-R0.1-SNAPSHOT"
    val protocolLibVersion = "5.1.0"
    // Compile the paper/bukkit api
    paperweight.paperDevBundle(paperVersion)
    // Include ProtocolLib to access the standard minecraft protocols
    compileOnly("com.comphenix.protocol:ProtocolLib:$protocolLibVersion")
    // Include KotlinBukkitLib from the file system
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    // Use standard kotlin tests
    testImplementation(kotlin("test"))
}

tasks {
    // Configure reobfJar to run when invoking the build task
    assemble {
        dependsOn(reobfJar)
    }

    test {
        useJUnitPlatform()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}


java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}