import java.net.URI

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    alias(libs.plugins.java.qa)
    application
}

group = "it.unibo.ds"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

application {
    mainClass.set("org.hyperledger.fabric.contract.ContractRouter")
}

repositories {
    mavenCentral()
    maven { url = URI("https://jitpack.io") }
}

dependencies {
    api(project(":presentation"))
    implementation(libs.fabric.chaincode.shim)
    implementation(libs.genson)
    implementation(libs.json)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
}

tasks.test {
    useJUnitPlatform()
}
