import java.net.URI

group = "metchevn-codejam"
version = "1.0-SNAPSHOT"

buildscript {

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.2.71")
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.2.0")
    }
}

plugins {
    java
    kotlin("jvm") version "1.2.71"
}

apply {
    plugin("org.junit.platform.gradle.plugin")
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform {
        includeEngines("spek", "spek2")
    }

    testLogging {
        events("PASSED", "FAILED", "SKIPPED")
    }
}

repositories {
    maven { url = URI("http://dl.bintray.com/jetbrains/spek") }
    jcenter()
    mavenCentral()
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.2.71")
    compile("org.jetbrains.kotlin:kotlin-reflect:1.2.71")
    compile("com.google.guava:guava:27.0-jre")
    testCompile("org.jetbrains.kotlin:kotlin-test:1.2.71")
    testCompile("org.hamcrest:hamcrest-all:1.3")
    testCompile("org.spekframework.spek2:spek-dsl-jvm:2.0.0-rc.1")
    testRuntime("org.spekframework.spek2:spek-runner-junit5:2.0.0-rc.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.1.0")
}