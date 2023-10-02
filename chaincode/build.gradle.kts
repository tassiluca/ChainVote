import java.net.URI

plugins {
    application
}

repositories {
    maven { url = URI("https://jitpack.io") }
}

dependencies {
    api(project(":presentation"))
    implementation(libs.fabric.chaincode.shim)
    implementation(libs.genson)
    implementation(libs.json)
    implementation("com.fasterxml.jackson.jr:jackson-jr-annotation-support:2.15.1")
    implementation("com.fasterxml.jackson.jr:jackson-jr-annotation-support:2.15.1")
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
}
