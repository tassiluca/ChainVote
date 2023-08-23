import * as grpc from '@grpc/grpc-js';
import { connect, Contract, Gateway, Identity, Network, Signer} from '@hyperledger/fabric-gateway';

import * as path from 'path';
import { TextDecoder } from 'util';

import { Config } from './config';

/**
 * envOrDefault() will return the value of an environment variable, or a default value if the variable is undefined.
 */
function envOrDefault(key: string, defaultValue: string): string {
    return process.env[key] || defaultValue;
}

const channelName = envOrDefault('CHANNEL_NAME', 'ch1');
const chaincodeName = envOrDefault('CHAINCODE_NAME', 'basic');

// Path to crypto materials.
const cryptoPath = envOrDefault('CRYPTO_PATH', path.resolve('/tmp', 'hyperledger', 'org1'));

// Path to admin private key directory.
const keyDirectoryPath = envOrDefault('KEY_DIRECTORY_PATH', path.resolve(cryptoPath, 'admin', 'msp', 'keystore'));

// Path to admin certificate.
const certPath = envOrDefault('CERT_PATH', path.resolve(cryptoPath, 'admin', 'msp', 'signcerts', 'cert.pem'));

// Path to peer tls certificate.
const tlsCertPath = envOrDefault('TLS_CERT_PATH', path.resolve(cryptoPath, 'peer1', 'tls-msp', 'tlscacerts', 'tls-0-0-0-0-7052.pem'));

// Gateway peer endpoint.
const peerEndpoint = envOrDefault('PEER_ENDPOINT', 'peer1-org1:7051');

// Gateway peer SSL host name override.
const peerHostAlias = envOrDefault('PEER_HOST_ALIAS', 'peer1-org1');

// MSP of the organization
const mspId = envOrDefault('MSP_ID', 'org1MSP');

// The configuration object
const configurations: Config = new Config(
    keyDirectoryPath, 
    certPath, 
    tlsCertPath, 
    peerEndpoint, 
    peerHostAlias, 
    mspId
);


const utf8Decoder = new TextDecoder();

async function main(): Promise<void> {

    const client: grpc.Client = await configurations.createGrpcClient();
    const identity: Identity = await configurations.createIdentity();
    const signer: Signer = await configurations.createSigner();
    
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





