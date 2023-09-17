import java.io.ByteArrayOutputStream
import java.io.OutputStream

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

data class Chaincode(val name: String, val org: String)
val chaincodeOrg1 = Chaincode("chaincode-org1", "org1")
val chaincodeOrg2 = Chaincode("chaincode-org2", "org2")
data class Peer(val org: String, val name: String, val address: String)
val peersOrg1 = setOf(
    Peer("org1", "peer1", "localhost:7051"),
    Peer("org1", "peer2", "localhost:8051"),
)
val peersOrg2 = setOf(
    Peer("org2", "peer1", "localhost:9051"),
    Peer("org2", "peer2", "localhost:10051"),
)
val allPeers = peersOrg1.plus(peersOrg2)
val blockchainGroup = "blockchain"
val blockchainDirectory = File("${projectDir.absolutePath}/blockchain")

fun executeCommand(
    command: String,
    directory: File = blockchainDirectory,
    environments: Map<String, String>? = null,
    stdout: OutputStream? = null,
) = exec {
    workingDir = directory
    stdout?.let { standardOutput = it }
    environments?.let { environment(it) }
    commandLine(command.split(" "))
}

tasks.register("downNetwork") {
    group = blockchainGroup
    executeCommand("./network.sh down")
}

tasks.register("upNetwork") {
    group = blockchainGroup
    executeCommand("./network.sh up")
}

fun Chaincode.compile() = executeCommand("./gradlew $name:installDist", projectDir)

fun commonEnvironmentsFor(organization: String) = mapOf(
    "CORE_PEER_TLS_ENABLED" to "true",
    "FABRIC_CFG_PATH" to "${projectDir.absolutePath}/blockchain/channels_config/$organization",
)

fun environmentsFor(organization: String, peer: Peer) = mapOf(
    "CORE_PEER_LOCALMSPID" to "${peer.org}MSP",
    "CORE_PEER_MSPCONFIGPATH" to "/tmp/hyperledger/${peer.org}/admin/msp",
    "CORE_PEER_TLS_ROOTCERT_FILE" to "/tmp/hyperledger/${peer.org}/${peer.name}/assets/tls-ca/tls-ca-cert.pem",
    "CORE_PEER_ADDRESS" to peer.address,
).plus(commonEnvironmentsFor(organization))

fun Chaincode.`package`() = executeCommand(
    "./bin/peer lifecycle chaincode package $name.tar.gz " +
        "--path ${projectDir.absolutePath}/$name/build/install/$name " +
        "--lang java " +
        "--label ${name}_1.0",
    environments = commonEnvironmentsFor(org),
)

tasks.register("packageChaincodeOrg1") {
    group = blockchainGroup
    chaincodeOrg1.compile()
    chaincodeOrg1.`package`()
}

tasks.register("packageChaincodeOrg2") {
    group = blockchainGroup
    chaincodeOrg2.compile()
    chaincodeOrg2.`package`()
}

tasks.register<Delete>("cleanAllPackages") {
    group = blockchainGroup
    blockchainDirectory.listFiles { _, fileName -> fileName.endsWith(".tar.gz") }?.forEach { delete(it) }
}

infix fun Chaincode.installOn(peers: Set<Peer>) = peers.forEach {
    println(">> Installing on $it")
    executeCommand(
        "./bin/peer lifecycle chaincode install $name.tar.gz",
        environments = environmentsFor(org, it),
    )
}

fun findPackageId(chaincode: String, organization: String): String? {
    val outputStream = ByteArrayOutputStream()
    executeCommand(
        "./bin/peer lifecycle chaincode queryinstalled",
        environments = commonEnvironmentsFor(organization),
        stdout = outputStream,
    )
    return Regex("$chaincode[^,]+").find(outputStream.toString())?.value
}

fun approve(chaincode: String, organization: String, peers: Set<Peer>, collectionsConfig: File? = null) {
    val packageId = findPackageId(chaincode, organization)
    peers.map { it.org }.distinct().forEach { org ->
        println(">> Approval for $org")
        val channels = setOf("ch${org.last()}", "ch${organization.last()}").distinct()
        val approvalPeer = peers.find { it.org == org }!!
        val caFile = "/tmp/hyperledger/$org/${approvalPeer.name}/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem"
        val collectionsArgs = collectionsConfig?.let { "--collections-config ${collectionsConfig.absolutePath}" } ?: ""
        channels.forEach {
            executeCommand(
                "./bin/peer lifecycle chaincode approveformyorg --orderer localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --name $chaincode --channelID $it --version 1.0 --package-id $packageId --sequence 1 --cafile $caFile $collectionsArgs --tls",
                environments = environmentsFor(org, approvalPeer),
            )
        }
    }
}

fun commit(chaincode: String, organization: String, peers: Set<Peer>, collectionsConfig: File? = null) {
    println(">> Committing")
    val approvalPeer = peers.find { it.org == organization } ?: error("No peers found")
    val channel = "ch${organization.last()}"
    val caFile = "/tmp/hyperledger/$organization/${approvalPeer.name}/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem"
    val collectionsArgs = collectionsConfig?.let { "--collections-config ${collectionsConfig.absolutePath}" } ?: ""
    val peerAddresses = peers.asSequence().filter { it.org == organization }
        .map { "--peerAddresses ${it.address} --tlsRootCertFiles /tmp/hyperledger/${it.org}/${it.name}/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem" }
        .joinToString(separator = " ")
    executeCommand(
        "./bin/peer lifecycle chaincode commit -o localhost:7050 --ordererTLSHostnameOverride orderer1-org0 --channelID $channel --name $chaincode --version 1.0 --sequence 1 --tls --cafile $caFile $peerAddresses $collectionsArgs --tls",
        environments = environmentsFor(organization, approvalPeer),
    )
}

tasks.register("upAndDeploy") {
    group = blockchainGroup
    finalizedBy("cleanAllPackages")
    dependsOn("downNetwork", "upNetwork", "packageChaincodeOrg1", "packageChaincodeOrg2")
    doLast {
        with(chaincodeOrg2) {
            val collectionsConfig = File("${projectDir.absolutePath}/$name/src/main/resources/collections-config.json")
            this installOn peersOrg2
            approve(name, org, peersOrg2, collectionsConfig)
            commit(name, org, peersOrg2, collectionsConfig)
        }
        with(chaincodeOrg1) {
            this installOn allPeers
            approve(name, org, allPeers)
            commit(name, org, allPeers)
        }
    }
}
