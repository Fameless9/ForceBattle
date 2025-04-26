plugins {
    id("java-library")
}

group = "net.fameless"
version = "1.0.7"
description = "Core features implementing the basic logic of ForceBattle"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.annotations)
    implementation(libs.gson)
    implementation(libs.guice)
    implementation(libs.adventureTextMinimessage)
    implementation(libs.adventureTextSerializerLegacy)
    api(libs.slf4j)
    api(libs.logback)
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Javadoc> {
        options.encoding = "UTF-8"
    }
}
