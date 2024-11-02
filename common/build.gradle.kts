subprojects {
    apply(plugin = "base-conventions")

    repositories {
        mavenCentral()
    }

    dependencies {
        // Shaded dependencies
        api("com.fasterxml.jackson.core:jackson-annotations:2.17.1")
        api("com.fasterxml.jackson.core:jackson-core:2.17.1")
        api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.1")
        api("com.fasterxml.jackson.core:jackson-databind:2.17.1")
        api("com.fasterxml.jackson.module:jackson-module-jsonSchema:2.17.1")
        api("org.yaml:snakeyaml:2.2")
        compileOnly("org.projectlombok:lombok:1.18.32")
        annotationProcessor("org.projectlombok:lombok:1.18.32")
    }
}