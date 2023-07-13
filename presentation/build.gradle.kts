@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    alias(libs.plugins.java.qa)
}

group = "it.unibo.ds"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":core"))
    implementation(libs.genson)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
}

tasks.test {
    useJUnitPlatform()
}
