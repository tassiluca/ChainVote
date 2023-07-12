@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("java")
    alias(libs.plugins.java.qa)
}

group = "it.unibo.ds"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
}

tasks.test {
    useJUnitPlatform()
}
