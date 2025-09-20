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
        dependsOn("checkCompatibility")
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

    register("checkCompatibility") {
        group = "compatibility-check"
        description = "Checks compatibility for all supported Spigot versions by compiling against them."
        dependsOn(mcVersions.map { "compileAgainstSpigot$it".replace(".", "_") })
    }
}

val mcVersions = listOf(
//        "1.8.8", not compatible
//        "1.9.4", not compatible
//        "1.10.2", not compatible
//        "1.11.2", not compatible
//        "1.12.2", not compatible
//        "1.13.2", not compatible
//        "1.14.4", not compatible
//        "1.15.2", not compatible
//        "1.16.5", not compatible
//        "1.17.1", not compatible
//        "1.18.2", not compatible
//        "1.19.4", not compatible
        "1.20.4",
        "1.20.6",
        "1.21.4"
)

mcVersions.forEach { version ->
    val taskName = "compileAgainstSpigot$version".replace(".", "_")

    val spigotApi = "org.spigotmc:spigot-api:$version-R0.1-SNAPSHOT"

    configurations.create(taskName + "CompileOnly")
    dependencies {
        add(taskName + "CompileOnly", spigotApi)
    }

    tasks.register<JavaCompile>(taskName) {
        group = "compatibility-check"
        description = "Compile against Spigot $version"
        classpath = configurations.getByName(taskName + "CompileOnly") + sourceSets["main"].compileClasspath
        source = sourceSets["main"].java
        destinationDirectory.set(file("build/compat/$version"))
    }
}
