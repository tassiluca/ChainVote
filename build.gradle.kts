import java.io.ByteArrayOutputStream

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

// ---------------------------- chaincode deployment ------------------------------

val blockchainGroup = "blockchain"

typealias Chaincode = String
data class Peer(val org: String, val name: String, val address: String)

val peersOrg1 = setOf(Peer("org1", "peer1", "localhost:7051"), Peer("org1", "peer2", "localhost:8051"))
val peersOrg2 = setOf(Peer("org2", "peer1", "localhost:9051"), Peer("org2", "peer2", "localhost:10051"))
val allPeers = peersOrg1.plus(peersOrg2)

val blockchainDirectory = File("${projectDir.absolutePath}/blockchain")

fun String.toArgs() = this.split(" ")

tasks.register<Exec>("downNetwork") {
    group = blockchainGroup
    workingDir = blockchainDirectory
    commandLine("./network.sh down".toArgs())
}

tasks.register<Exec>("upNetwork") {
    group = blockchainGroup
    workingDir = blockchainDirectory
    commandLine("./network.sh up".toArgs())
}

fun compile(chaincode: Chaincode) = exec {
    workingDir = projectDir
    commandLine("./gradlew $chaincode:installDist".toArgs())
}

fun `package`(chaincode: String, organization: String) = exec {
    workingDir = blockchainDirectory
    environment(
        mapOf(
            "CORE_PEER_TLS_ENABLED" to "true",
            "FABRIC_CFG_PATH" to "${projectDir.absolutePath}/blockchain/channels_config/$organization",
        ),
    )
    commandLine("./bin/peer lifecycle chaincode package $chaincode.tar.gz --path ${projectDir.absolutePath}/$chaincode/build/install/$chaincode --lang java --label ${chaincode}_1.0".toArgs())
}

tasks.register("packageChaincodeOrg1") {
    group = blockchainGroup
    compile("chaincode-org1")
    `package`("chaincode-org1", "org1")
}

tasks.register("packageChaincodeOrg2") {
    group = blockchainGroup
    compile("chaincode-org2")
    `package`("chaincode-org2", "org2")
}

tasks.register<Delete>("cleanAllPackages") {
    group = blockchainGroup
    blockchainDirectory.listFiles { _, fileName -> fileName.endsWith(".tar.gz") }?.forEach { delete(it) }
}

fun install(chaincode: String, organization: String, peers: Set<Peer>) {
    for (peer in peers) {
        println(">> Installing on $peer")
        exec {
            environment(
                mapOf(
                    "CORE_PEER_TLS_ENABLED" to "true",
                    "FABRIC_CFG_PATH" to "${projectDir.absolutePath}/blockchain/channels_config/$organization",
                    "CORE_PEER_LOCALMSPID" to "${peer.org}MSP",
                    "CORE_PEER_MSPCONFIGPATH" to "/tmp/hyperledger/${peer.org}/admin/msp",
                    "CORE_PEER_TLS_ROOTCERT_FILE" to "/tmp/hyperledger/${peer.org}/${peer.name}/assets/tls-ca/tls-ca-cert.pem",
                    "CORE_PEER_ADDRESS" to peer.address,
                ),
            )
            workingDir = blockchainDirectory
            commandLine("./bin/peer lifecycle chaincode install $chaincode.tar.gz".toArgs())
        }
    }
}

fun findPackageId(chaincode: String, organization: String): String? {
    val outputStream = ByteArrayOutputStream()
    exec {
        standardOutput = outputStream
        workingDir = blockchainDirectory
        environment(
            mapOf(
                "CORE_PEER_TLS_ENABLED" to "true",
                "FABRIC_CFG_PATH" to "${projectDir.absolutePath}/blockchain/channels_config/$organization",
            ),
        )
        commandLine("./bin/peer lifecycle chaincode queryinstalled".toArgs())
    }
    return Regex("$chaincode[^,]+").find(outputStream.toString())?.value
}

fun approve(chaincode: String, organization: String, peers: Set<Peer>, collectionsConfig: File? = null) {
    val packageId = findPackageId(chaincode, organization)
    peers.map { it.org }.distinct().forEach { org ->
        println(">> Approval for $org")
        val channel = "ch${org.last()}"
        val approvalPeer = peers.find { it.org == org }!!
        val caFile = "/tmp/hyperledger/$org/${approvalPeer.name}/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem"
        val envs = mapOf(
            "CORE_PEER_TLS_ENABLED" to "true",
            "FABRIC_CFG_PATH" to "${projectDir.absolutePath}/blockchain/channels_config/$organization",
            "CORE_PEER_LOCALMSPID" to "${org}MSP",
            "CORE_PEER_MSPCONFIGPATH" to "/tmp/hyperledger/$org/admin/msp",
            "CORE_PEER_TLS_ROOTCERT_FILE" to "/tmp/hyperledger/$org/${approvalPeer.name}/assets/tls-ca/tls-ca-cert.pem",
            "CORE_PEER_ADDRESS" to approvalPeer.address,
        )
        val collectionsConfigArgs = collectionsConfig?.let { "--collections-config ${collectionsConfig.absolutePath}" } ?: ""
        exec {
            workingDir = blockchainDirectory
            environment(envs)
            commandLine("./bin/peer lifecycle chaincode approveformyorg --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --name $chaincode --channelID $channel --version 1.0 --package-id $packageId --sequence 1 --cafile $caFile $collectionsConfigArgs --tls".toArgs())
        }
        if (org != organization) {
            val channel1 = "ch${organization.last()}"
            exec {
                workingDir = blockchainDirectory
                environment(envs)
                commandLine("./bin/peer lifecycle chaincode approveformyorg --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --name $chaincode --channelID $channel1 --version 1.0 --package-id $packageId --sequence 1 --cafile $caFile --tls".toArgs())
            }
        }
    }
}

fun commit(chaincode: String, organization: String, peers: Set<Peer>, collectionsConfig: File? = null) {
    val approvalPeer = peers.find { it.org == organization } ?: error("No peers found")
    val channel = "ch${organization.last()}"
    val caFile = "/tmp/hyperledger/$organization/${approvalPeer.name}/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem"
    val envs = mapOf(
        "CORE_PEER_TLS_ENABLED" to "true",
        "FABRIC_CFG_PATH" to "${projectDir.absolutePath}/blockchain/channels_config/$organization",
        "CORE_PEER_LOCALMSPID" to "${organization}MSP",
        "CORE_PEER_MSPCONFIGPATH" to "/tmp/hyperledger/$organization/admin/msp",
        "CORE_PEER_TLS_ROOTCERT_FILE" to "/tmp/hyperledger/$organization/${approvalPeer.name}/assets/tls-ca/tls-ca-cert.pem",
        "CORE_PEER_ADDRESS" to approvalPeer.address,
    )
    val collectionsConfigArgs = collectionsConfig?.let { "--collections-config ${collectionsConfig.absolutePath}" } ?: ""
    val peerAddresses = peers.asSequence().filter { it.org == organization }
        .map { "--peerAddresses ${it.address} --tlsRootCertFiles /tmp/hyperledger/${it.org}/${it.name}/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem" }
        .joinToString(separator = " ")
    exec {
        workingDir = blockchainDirectory
        environment(envs)
        commandLine("./bin/peer lifecycle chaincode commit -o localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --channelID $channel --name $chaincode --version 1.0 --sequence 1 --tls --cafile $caFile $peerAddresses $collectionsConfigArgs --tls".toArgs())
    }
}

tasks.register("deployChaincodes") {
    group = blockchainGroup
    finalizedBy("cleanAllPackages")
    dependsOn("downNetwork", "upNetwork", "packageChaincodeOrg1", "packageChaincodeOrg2")
    doLast {
        with(Pair("chaincode-org2", "org2")) {
            val collectionsConfig = File("${projectDir.absolutePath}/$first/src/main/resources/collections-config.json")
            install(first, second, peersOrg2)
            approve(first, second, peersOrg2, collectionsConfig)
            commit(first, second, peersOrg2, collectionsConfig)
        }
        with(Pair("chaincode-org1", "org1")) {
            install(first, second, allPeers)
            approve(first, second, allPeers)
            commit(first, second, allPeers)
        }
    }
}
