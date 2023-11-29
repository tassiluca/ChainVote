import java.net.URI

repositories {
    maven { url = URI("https://jitpack.io") }
}

dependencies {
    api(project(":presentation"))
    implementation(libs.fabric.chaincode.shim)
    implementation(libs.genson)
    implementation(libs.json)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
}
