@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
    alias(libs.plugins.java.qa)
}

subprojects {

    apply(plugin = "java-library")
    apply(plugin = rootProject.libs.plugins.java.qa.get().pluginId)

    group = "it.unibo.ds.chainvote"

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        with(rootProject.libs) {
            implementation(log4j.api)
            implementation(log4j.core)
            implementation(log4j.slf4j.impl)
            compileOnly(spotbugs.annotations)
            testCompileOnly(spotbugs.annotations)
            testImplementation(junit.api)
            testImplementation(junit.engine)
        }
    }

    tasks.test {
        useJUnitPlatform()
    }
}

val blockchainGroup = "blockchain"

tasks.register("upAndDeploy") {
    group = blockchainGroup
    description = "Up the blockchain network and deploy smart contracts on it"

    val blockchainDir = "./blockchain"

    fun executeScript(script: String, vararg args: String, scriptDir: String = ".") =
        exec {
            workingDir = File(scriptDir)
            commandLine(script, *args)
        }

    fun deployChaincode(chaincodeName: String, organizationName: String) {
        println(":: Deploy $chaincodeName ::")
        executeScript("./gradlew", "$chaincodeName:clean")
        executeScript("./gradlew", "$chaincodeName:installDist")
        if (File("./$chaincodeName/META-INF").isDirectory) {
            executeScript("cp", "-r", "META-INF", "build/install/$chaincodeName", scriptDir = chaincodeName)
        }
        executeScript("./deploy-chaincode.sh", "../$chaincodeName", organizationName, scriptDir = blockchainDir)
    }

    fun restartNetwork() {
        println(":: Downing network ::")
        executeScript("./network.sh", "down", scriptDir = blockchainDir)
        println(":: Upping network ::")
        executeScript("./network.sh", "up", scriptDir = blockchainDir)
    }

    doLast {
        restartNetwork()
        deployChaincode("chaincode-org1", "org1")
        deployChaincode("chaincode-org2", "org2")
    }
}
