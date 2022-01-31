val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val arrow_version: String by project
val arrow_meta_version: String by project
val arrow_analysis_version: String by project

plugins {
    application
    id("com.google.devtools.ksp") version "1.6.0-1.0.2"
    kotlin("jvm") version "1.6.0"
}

group = "io.arrow-kt.example"
version = "0.0.1"
application {
    mainClass.set("io.arrow.example.ApplicationKt")
}

repositories {
    mavenCentral()
}

buildscript {
    dependencies {
        classpath("io.arrow-kt.analysis.kotlin:io.arrow-kt.analysis.kotlin.gradle.plugin:2.0-RC3")
    }
}

apply(plugin = "io.arrow-kt.analysis.kotlin")

dependencies {
    ksp("io.arrow-kt:arrow-optics-ksp-plugin:$arrow_version")
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
