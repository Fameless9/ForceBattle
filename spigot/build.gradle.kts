plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.1"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    compileOnly(libs.spigot)
    compileOnly(libs.annotations)
    implementation(project(":forcebattle-core"))
    implementation(libs.guice)
    implementation(libs.bstats)
    implementation(libs.adventureTextSerializerLegacy)
    implementation(libs.adventureTextMinimessage)
    implementation(libs.adventureBukkit)
}

group = "net.fameless"
version = "1.0.11"
description = "ForceBattle Spigot implementation"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveBaseName.set("ForceBattle-Spigot")
        archiveClassifier.set("")
        archiveVersion.set("1.0.11")

        relocate("org.bstats", "net.fameless.libs.bstats")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Javadoc> {
        options.encoding = "UTF-8"
    }
}
