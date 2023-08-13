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
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
}

tasks.register("upAndDeploy") {
    group = "Blockchain"
    description = "Up the blockchain network and deploy this smart contract on it"

    fun executeScript(scriptDir: String, scriptName: String, vararg args: String) =
        exec {
            workingDir = File(scriptDir)
            commandLine("./$scriptName", *args)
        }

    doLast {
        val blockchainDir = "../blockchain"
        println(":: Downing network ::")
        executeScript(blockchainDir, "network.sh", "down")
        println(":: Upping network ::")
        executeScript(blockchainDir, "network.sh", "up")
        println(":: Deploy SC::")
        executeScript(blockchainDir, "deploy-chaincode.sh", "../chaincode-org2", "org2")
    }
}
