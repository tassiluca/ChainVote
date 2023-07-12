import java.net.URI

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("java")
    alias(libs.plugins.java.qa)
    application
}

group = "it.unibo.ds"

application {
    mainClass.set("org.hyperledger.fabric.contract.ContractRouter")
}

repositories {
    mavenCentral()
    maven { url = URI("https://jitpack.io") }
}

dependencies {
    implementation(libs.fabric.chaincode.shim)
    implementation(libs.genson)
    implementation(libs.json)
}

tasks.test {
    useJUnitPlatform()
}
