import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.Properties
import kotlin.io.path.Path

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `java-library`
}

subprojects {

    apply(plugin = "java-library")

    group = "it.unibo.ds.chainvote"

    java {
        // Hyperledger Fabric chaincode libs works only with Java 11!
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

    tasks.javadoc {
        val javadocDir = file(Path(rootProject.projectDir.absolutePath, "build", "javadoc", project.name))
        javadocDir.mkdirs()
        setDestinationDir(javadocDir)
    }
}

data class Chaincode(val name: String, val organization: String)
val chaincodeOrg1 = Chaincode("chaincode-org1", "org1")
val chaincodeOrg2 = Chaincode("chaincode-org2", "org2")

data class Peer(val organization: String, val name: String, val address: String)
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
val blockchainDirectory: File = Path(projectDir.parent, "blockchain").toFile()
val configurationFile = File(projectDir.parent, "config.env")
val artifactsPath: String = Properties()
    .apply { configurationFile.inputStream().use { load(it) } }
    .getProperty("ARTIFACTS_DIR")
val blockchainArtifactsPath = Path(projectDir.parent, artifactsPath)
println(blockchainArtifactsPath)

fun commonEnvironments() = mapOf(
    "CORE_PEER_TLS_ENABLED" to "true",
    "FABRIC_CFG_PATH" to "$blockchainDirectory/channels_config/generated",
)

fun environmentsFor(peer: Peer) = mapOf(
    "CORE_PEER_LOCALMSPID" to "${peer.organization}MSP",
    "CORE_PEER_MSPCONFIGPATH" to "$blockchainArtifactsPath/${peer.organization}/admin/msp",
    "CORE_PEER_TLS_ROOTCERT_FILE" to "$blockchainArtifactsPath/${peer.organization}/${peer.name}/assets/tls-ca/tls-ca-cert.pem",
    "CORE_PEER_ADDRESS" to peer.address,
).plus(commonEnvironments())

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
    description = "Bring down the blockchain network"
    doLast { executeCommand("./network.sh down") }
}

tasks.register("upNetwork") {
    group = blockchainGroup
    description = "Bring up the blockchain network"
    dependsOn("downNetwork")
    doLast { executeCommand("./network.sh up") }
}

fun Chaincode.`package`() = executeCommand(
    "./bin/peer lifecycle chaincode package $name.tar.gz " +
        "--path ${projectDir.absolutePath}/$name/build/install/$name " +
        "--lang java " +
        "--label ${name}_1.0",
    environments = commonEnvironments(),
)

tasks.register("packageChaincodes") {
    group = blockchainGroup
    description = "Build and generate chaincode packages"
    dependsOn(":${chaincodeOrg1.name}:installDist", ":${chaincodeOrg2.name}:installDist", "upNetwork")
    doLast {
        chaincodeOrg1.`package`()
        chaincodeOrg2.`package`()
    }
}

tasks.register<Delete>("cleanAllPackages") {
    group = blockchainGroup
    description = "Remove all generated packages"
    blockchainDirectory.listFiles { _, fileName -> fileName.endsWith(".tar.gz") }?.forEach { delete(it) }
}

infix fun Chaincode.installOn(peers: Set<Peer>) = peers.forEach {
    println(">> Installing $name on $it")
    val outputStream = ByteArrayOutputStream()
    executeCommand(
        "./bin/peer lifecycle chaincode install $name.tar.gz",
        environments = environmentsFor(it),
        stdout = outputStream,
    )
}

fun Chaincode.packageId(): String? {
    val outputStream = ByteArrayOutputStream()
    val representativePeer = allPeers.find { it.organization == organization } ?: error("No peer found for $organization")
    executeCommand(
        "./bin/peer lifecycle chaincode queryinstalled",
        environments = environmentsFor(representativePeer),
        stdout = outputStream,
    )
    return Regex("$name[^,]+").find(outputStream.toString())?.value
}

fun Chaincode.approveFor(peers: Set<Peer>, collectionsConfig: File? = null) {
    val packageId = packageId() ?: error("No package found for $name")
    peers.map { it.organization }.distinct().forEach { org ->
        println(">> Approval for $org")
        val channels = setOf("ch${org.last()}", "ch${organization.last()}").distinct()
        val approvalPeer = peers.find { it.organization == org }!!
        channels.forEach { channel ->
            executeCommand(
                "./bin/peer lifecycle chaincode approveformyorg " +
                    "--orderer localhost:7050 " +
                    "--ordererTLSHostnameOverride orderer1-org0 " +
                    "--name $name " +
                    "--channelID $channel " +
                    "--version 1.0 " +
                    "--package-id $packageId " +
                    "--sequence 1 " +
                    "--cafile $blockchainArtifactsPath/$org/${approvalPeer.name}/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem " +
                    (collectionsConfig?.let { "--collections-config ${it.absolutePath} " } ?: "") +
                    "--tls",
                environments = environmentsFor(approvalPeer),
            )
        }
    }
}

fun Chaincode.commit(peers: Set<Peer>, collectionsConfig: File? = null) {
    println(">> Committing")
    val approvalPeer = peers.find { it.organization == organization } ?: error("No peers found")
    val channel = "ch${organization.last()}"
    val peerAddresses = peers.filter { it.organization == organization }.joinToString(separator = " ") {
        "--peerAddresses ${it.address} " +
            "--tlsRootCertFiles $blockchainArtifactsPath/${it.organization}/${it.name}/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem"
    }
    executeCommand(
        "./bin/peer lifecycle chaincode commit " +
            "--orderer localhost:7050 " +
            "--ordererTLSHostnameOverride orderer1-org0 " +
            "--channelID $channel " +
            "--name $name " +
            "--version 1.0 " +
            "--sequence 1 " +
            "--cafile $blockchainArtifactsPath/$organization/${approvalPeer.name}/tls-msp/tlscacerts/tls-0-0-0-0-7052.pem " +
            "$peerAddresses " +
            (collectionsConfig?.let { "--collections-config ${it.absolutePath} " } ?: "") +
            "--tls",
        environments = environmentsFor(approvalPeer),
    )
}

fun Chaincode.deploy(peers: Set<Peer>, collectionsConfig: File? = null) {
    installOn(peers)
    approveFor(peers, collectionsConfig)
    commit(peers, collectionsConfig)
}

tasks.register("upAndDeploy") {
    group = blockchainGroup
    description = "Up the network and deploy both chaincodes"
    dependsOn("upNetwork", "packageChaincodes")
    doLast {
        chaincodeOrg1.deploy(allPeers)
        chaincodeOrg2.apply {
            deploy(peersOrg2, File("${projectDir.absolutePath}/$name/src/main/resources/collections-config.json"))
        }
        executeCommand("./gradlew cleanAllPackages", projectDir)
    }
}

tasks.register("installBinaries") {
    description = "Install Hyperledger Fabric binaries"
    doLast { executeCommand("${blockchainDirectory.path}/install-binaries.sh") }
}

tasks.filter { it.group == blockchainGroup }.forEach {
    it.dependsOn("installBinaries")
}
