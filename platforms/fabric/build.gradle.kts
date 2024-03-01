plugins {
    id("platform-conventions")
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.4-SNAPSHOT"
}

loom {
    silentMojangMappingsLicense()
}
println("Java version: " + System.getProperty("java.version"))
dependencies {
    minecraft("com.mojang:minecraft:${rootProject.extra["minecraftVersion"]}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${rootProject.extra["fabricLoaderVersion"]}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:${rootProject.extra["fabricApiVersion"]}")

    implementation(project(":common:common-core"))
}

tasks {
    processResources {
        val replacements = mapOf(
            "version" to project.version
        )
        inputs.properties(replacements)

        filesMatching("fabric.mod.json") {
            expand(replacements)
        }
    }

    jar {
        archiveClassifier.set("deobf")
    }

    shadowJar {
        archiveClassifier.set("deobf-all")
    }

    remapJar {
        input.set(shadowJar.flatMap { it.archiveFile })
    }

    remapSourcesJar {
        fixRemapSourcesDependencies()
    }
}
tasks.register("printRootDir") {
    doLast {
        println(rootProject.projectDir)
    }
}

otgPlatform {
    productionJar.set(tasks.remapJar.flatMap { it.archiveFile })
}
