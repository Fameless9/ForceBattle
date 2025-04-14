plugins {
    `java-library`
    `maven-publish`
    id("com.diffplug.spotless") version "7.0.0.BETA1"
}

group = "net.fameless"
version = "1.0.5"
description = "ForceBattle"
java.sourceCompatibility = JavaVersion.VERSION_21

subprojects {
    apply(plugin = "com.diffplug.spotless")

    spotless {
        java {
            target("**/*.java")
            removeUnusedImports()
            toggleOffOn()
            trimTrailingWhitespace()
            endWithNewline()
            formatAnnotations()
            indentWithSpaces(4)
        }
    }

    tasks {
        build {
            dependsOn(spotlessApply)
        }
    }

}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}




tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
