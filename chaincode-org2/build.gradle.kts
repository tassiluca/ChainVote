import java.net.URI

plugins {
    application
}

application {
    mainClass.set("org.hyperledger.fabric.contract.ContractRouter")
}

repositories {
    maven { url = URI("https://jitpack.io") }
}

dependencies {
    api(project(":presentation"))
    implementation(libs.fabric.chaincode.shim)
    implementation(libs.genson)
    implementation(libs.json)
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.3.0")
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
}
