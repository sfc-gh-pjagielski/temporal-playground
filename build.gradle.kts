import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
    application
}

val temporalVersion = "1.20.0"

group = "com.snowflake"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.temporal:temporal-sdk:$temporalVersion")
    implementation("io.temporal:temporal-opentracing:$temporalVersion")
    implementation("ch.qos.logback:logback-classic:1.4.7")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test"))
    testImplementation("io.temporal:temporal-testing:$temporalVersion")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}
