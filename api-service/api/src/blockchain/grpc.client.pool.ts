import {connect, Gateway, Identity, Signer} from "@hyperledger/fabric-gateway";
import {
    getPeerBasename,
    getPeerHost,
    getPeerHostAlias,
    getPeerOrganization,
    Org1Peer,
    Org2Peer,
    Org3Peer
} from "./peer.enum";
import {CommunicatorFactory} from "./communicator/communicator.factory";
import {CommunicatorInterface} from "./communicator/communicator";
import * as grpc from "@grpc/grpc-js";

class GrpcClientPool {
    private static instance: GrpcClientPool;
    private clients: Map<string, Gateway> = new Map();

    private constructor() {}

    /**
     * Create a new client for connecting to the blockchain network specifying the peer
     * that will act as the gateway
     * @param peer
     * @private
     */
    private async createClient(peer: Org1Peer | Org2Peer | Org3Peer): Promise<Gateway> {
        let communicator: CommunicatorInterface;
        let client: grpc.Client, identity: Identity, signer: Signer;
        if(getPeerOrganization(peer) === 'org1') {
            communicator = CommunicatorFactory.org1WithEndpoint(
                getPeerBasename(peer),
                getPeerHost(peer),
                getPeerHostAlias(peer)
            )
        } else if (getPeerOrganization(peer) === 'org2') {
            communicator = CommunicatorFactory.org2WithEndpoint(
                getPeerBasename(peer),
                getPeerHost(peer),
                getPeerHostAlias(peer)
            )
        }
        else {
            communicator = CommunicatorFactory.org3WithEndpoint(
                getPeerBasename(peer),
                getPeerHost(peer),
                getPeerHostAlias(peer)
            )
        }

        client = await communicator.createGrpcClient();
        identity = await communicator.createIdentity();
        signer = await communicator.createSigner();

        return connect({
            client,
            identity,
            signer,
            evaluateOptions: () => {
                return {deadline: Date.now() + 5000}; // 5 seconds
            },
            endorseOptions: () => {
                return {deadline: Date.now() + 15000}; // 15 seconds
            },
            submitOptions: () => {
                return {deadline: Date.now() + 5000}; // 5 seconds
            },
            commitStatusOptions: () => {
                return {deadline: Date.now() + 60000}; // 1 minute
            },
        });
    }

    /**
     * Return the singleton instance of the GrpcClientPool
     */
    public static getInstance(): GrpcClientPool {
        if (!this.instance) {
            this.instance = new GrpcClientPool();
        }
        return this.instance;
    }

    /**
     * Return the gateway client for the specified peer. If the client is already present in the pool it will be
     * returned, otherwise a new client will be created.
     * @param peer
     */
    public async getClientForPeer(peer: Org1Peer | Org2Peer | Org3Peer): Promise<Gateway> {
        if (!this.clients.has(peer)) {
            this.clients.set(peer, await this.createClient(peer));
        }

        return this.clients.get(peer) as Gateway;
    }
}

export default GrpcClientPool;