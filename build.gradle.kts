plugins {
    id("java")
    id("java-library")
    id("com.gradleup.shadow") version "8.3.1"
    id("com.diffplug.spotless") version "7.0.0.BETA1"
    `maven-publish`
}

group = "net.fameless"
version = "1.0.11"
description = "ForceBattle"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    implementation(libs.gson)
    implementation(libs.guice)
    implementation(libs.adventureTextMinimessage)
    implementation(libs.adventureTextSerializerLegacy)
    implementation(libs.bstats)
    implementation(libs.adventureBukkit)

    api(libs.slf4j)
    api(libs.logback)

    compileOnly(libs.spigot)
    compileOnly(libs.annotations)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveBaseName.set("ForceBattle")
        archiveClassifier.set("")
        archiveVersion.set(version.toString())

        relocate("org.bstats", "net.fameless.libs.bstats")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Javadoc> {
        options.encoding = "UTF-8"
    }
}

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

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
