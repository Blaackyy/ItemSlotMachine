plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
    idea
    id("io.github.goooler.shadow") version "8.1.8"
}

group = "dev.blacky"
version = "1.0.0"
paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://repo.unnamed.team/repository/unnamed-public/")
    maven("https://jitpack.io")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        //exclude bukkit spigot and paper apis
        exclude(group = "org.bukkit", module = "bukkit")
        exclude(group = "org.spigotmc", module = "spigot-api")
        exclude(group = "com.destroystokyo.paper", module = "paper-api")
    }

}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")

        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = Charsets.UTF_8.toString()
        filesMatching("*.yml") {
            expand(props)
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

idea {
    module {
        //download sources and javadocs
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}