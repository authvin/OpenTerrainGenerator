plugins {
    id("parent-logic")
}

defaultTasks = arrayListOf("build", "publishToMavenLocal")

allprojects {
    group = "com.pg85.otg"
    version = "0.2.0"
    description = "Open Terrain Generator: Generate anything!"
}

subprojects {
    apply(plugin = "base-conventions")

    repositories {
        mavenCentral()
    }
}

val universalJar = tasks.register<Jar>("universalJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))
    archiveFileName.set("OpenTerrainGenerator-Universal-${rootProject.extra["minecraftVersion"]}-" + project.version + ".jar")
}

tasks.build {
    dependsOn(universalJar)
}

listOf(
    // project(":platforms:paper"),
    // project(":platforms:forge"),
    project(":platforms:fabric"),
).forEach { proj ->
    proj.afterEvaluate {
        universalJar {
            val tree = zipTree(proj.the<OTGPlatformExtension>().productionJar)
            from(tree)
            val manifestFile = tree.elements.map { files ->
                files.find { it.asFile.path.endsWith("META-INF/MANIFEST.MF") }
            }
            manifest.from(manifestFile)
        }
    }
}