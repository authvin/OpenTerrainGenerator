plugins {
    `java-library`
    `maven-publish`
}
val javaVersion = project.property("java_version").toString().toInt()
configure<JavaPluginExtension> {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
    withSourcesJar()
}

configure<BasePluginExtension> {
    version = project.property("otg_version").toString()
    group = project.property("otg_group").toString()
}

configure<PublishingExtension> {
    publications.create<MavenPublication>("maven") {
        artifactId = "otg-${project.name}"
        from(components["java"])
    }
}

tasks {
    withType<JavaCompile> {
        options.release.set(javaVersion)
        options.encoding = Charsets.UTF_8.name()
    }

    withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }

    withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
}
