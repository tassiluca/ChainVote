tasks.register("upAndDeploySmartContract") {
    group = "Blockchain"
    description = "Up the blockchain network and deploy this smart contract on it"

    fun executeScript(scriptDir: String, scriptName: String, vararg args: String) =
        exec {
            workingDir = File(scriptDir)
            commandLine("./$scriptName", *args)
        }

    doLast {
        val blockchainDir = "./blockchain"
        val chaincodeName = "chaincode"
        println(":: Downing network ::")
        executeScript(blockchainDir, "network.sh", "down")
        println(":: Upping network ::")
        executeScript(blockchainDir, "network.sh", "up")
        println(":: Build smart contract ::")
        executeScript(".", "gradlew", "chaincode:installDist")
        println(":: Deploy smart contract on the network ::")
        executeScript(blockchainDir, "deploy-chaincode.sh", "org1", "../chaincode/build/install/$chaincodeName")
    }
}
