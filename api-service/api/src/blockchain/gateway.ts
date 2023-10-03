import * as grpc from '@grpc/grpc-js';
import { connect, Contract, Gateway, Identity, Network, Signer} from '@hyperledger/fabric-gateway';

import { TextDecoder } from 'util';

import { CommunicatorInterface } from './communicator';
import {CommunicatorFactory} from "./communicator.factory";

/**
 * envOrDefault() will return the value of an environment variable, or a default value if the variable is undefined.
 */
function envOrDefault(key: string, defaultValue: string): string {
    return process.env[key] || defaultValue;
}

const channelName = envOrDefault('CHANNEL_NAME', 'ch1');
const chaincodeName = envOrDefault('CHAINCODE_NAME', 'basic');

// The configuration object
const communicator: CommunicatorInterface = CommunicatorFactory.createCommunicatorForOrg1(
    "peer1",
    "peer1-org1:7051",
    "peer1-org1"
);
const utf8Decoder = new TextDecoder();

async function main(): Promise<void> {
    const client: grpc.Client = await communicator.createGrpcClient();
    const identity: Identity = await communicator.createIdentity();
    const signer: Signer = await communicator.createSigner();
    
    const gateway: Gateway = connect({
        client,
        identity,
        signer,
        // Default timeouts for different gRPC calls
        evaluateOptions: () => {
            return { deadline: Date.now() + 5000 }; // 5 seconds
        },
        endorseOptions: () => {
            return { deadline: Date.now() + 15000 }; // 15 seconds
        },
        submitOptions: () => {
            return { deadline: Date.now() + 5000 }; // 5 seconds
        },
        commitStatusOptions: () => {
            return { deadline: Date.now() + 60000 }; // 1 minute
        },
    });

    try{
        let network: Network = gateway.getNetwork(channelName);
        let contract: Contract = network.getContract(chaincodeName);
        
        await initLedger(contract);
        await getAllAssets(contract);

    } finally {
        gateway.close();
        client.close();
    }
}

main(); 


/**
 * This type of transaction would typically only be run once by an application the first time it was started after its
 * initial deployment. A new version of the chaincode deployed later would likely not need to run an "init" function.
 */
async function initLedger(contract: Contract): Promise<void> {
    console.log('\n--> Submit Transaction: InitLedger, function creates the initial set of assets on the ledger');

    await contract.submitTransaction('InitLedger');

    console.log('*** Transaction committed successfully');
}

/**
 * Evaluate a transaction to query ledger state.
 */
async function getAllAssets(contract: Contract): Promise<void> {
    console.log('\n--> Evaluate Transaction: GetAllAssets, function returns all the current assets on the ledger');

    const resultBytes = await contract.evaluateTransaction('GetAllAssets');

    const resultJson = utf8Decoder.decode(resultBytes);
    const result = JSON.parse(resultJson);
    console.log('*** Result:', result);
}





