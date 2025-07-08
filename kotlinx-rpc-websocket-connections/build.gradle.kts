plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.serialization") version "2.2.0"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.8.0"
}

group = "com.aftrolle"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Client API
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-client:0.8.0")
    // Server API
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-server:0.8.0")
    // Serialization module. Also, protobuf and cbor are provided
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:0.8.0")

    // Transport implementation for Ktor
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-client:0.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-server:0.8.0")

    // Ktor API
    implementation("io.ktor:ktor-client-cio-jvm:3.2.1")
    implementation("io.ktor:ktor-server-netty-jvm:3.2.1")

    implementation("ch.qos.logback:logback-classic:1.5.18")
}

kotlin {
    jvmToolchain(21)
}