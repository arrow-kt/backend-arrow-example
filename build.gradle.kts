val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val arrow_version: String by project
val arrow_meta_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.31"
    // kotlin("plugin.serialization") version "1.6.0"
}

group = "io.arrow-kt.example"
version = "0.0.1"
application {
    mainClass.set("io.arrow.example.ApplicationKt")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
}

buildscript {
    repositories {
        mavenLocal()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
    dependencies {
        classpath("io.arrow-kt.optics:io.arrow-kt.optics.gradle.plugin:1.5.31-SNAPSHOT")
        classpath("io.arrow-kt.analysis:io.arrow-kt.analysis.gradle.plugin:1.5.31-SNAPSHOT")
    }
}

apply(plugin = "io.arrow-kt.optics")
// apply(plugin = "io.arrow-kt.analysis")

dependencies {
    implementation("io.arrow-kt:arrow-core:$arrow_version")
    implementation("io.arrow-kt:arrow-fx-coroutines:$arrow_version")
    implementation("io.arrow-kt:arrow-optics:$arrow_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-gson:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
