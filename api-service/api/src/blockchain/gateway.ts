
import path from "path";
import { promises as fs } from 'fs';
import * as grpc from '@grpc/grpc-js';
import {connect, Identity, Signer, signers} from "@hyperledger/fabric-gateway";
import * as crypto from 'crypto';

const utf8Decoder = new TextDecoder();

async function main(): Promise<void> {

    const org1Dir = path.resolve(
        "/tmp",
        "hyperledger",
        "org1"
    );

    const keyDirectoryPathOrg1 = path.resolve(
        org1Dir,
        'client',
        'msp',
        'keystore'
    );

    const certPathOrg1 = path.resolve(
        org1Dir,
        'client',
        'msp',
        'signcerts',
        'cert.pem'
    );

    const tlsCertPathOrg1 = path.resolve(
        org1Dir,
        'client',
        'tls-msp',
        'tlscacerts',
        'tls-0-0-0-0-7052.pem'
    );

    const peerEndpointOrg1 = 'localhost:7051';
    const peerNameOrg1 = 'peer1-org1';
    const channelName = 'ch1';
    const chaincodeName = 'chaincode-org1';
    const mspIdOrg1 = 'org1MSP';

    async function newGrpcConnection(
        tlsCertPath: string,
        peerEndpoint: string,
        peerName: string
    ): Promise<grpc.Client> {
        const tlsRootCert = await fs.readFile(tlsCertPath);
        const tlsCredentials = grpc.credentials.createSsl(tlsRootCert);
        return new grpc.Client(peerEndpoint, tlsCredentials, {
            'grpc.ssl_target_name_override': peerName,
        });
    }

    async function newIdentity(
        certPath: string,
        mspId: string
    ): Promise<Identity> {
        const credentials = await fs.readFile(certPath);
        return { mspId, credentials };
    }

    async function newSigner(keyDirectoryPath: string): Promise<Signer> {
        const files = await fs.readdir(keyDirectoryPath);
        const keyPath = path.resolve(keyDirectoryPath, files[0]);
        const privateKeyPem = await fs.readFile(keyPath);
        const privateKey = crypto.createPrivateKey(privateKeyPem);
        return signers.newPrivateKeySigner(privateKey);
    }


    const clientOrg1 = await newGrpcConnection(
        tlsCertPathOrg1,
        peerEndpointOrg1,
        peerNameOrg1
    );

    const gatewayOrg1 = connect({
        client: clientOrg1,
        identity: await newIdentity(certPathOrg1, mspIdOrg1),
        signer: await newSigner(keyDirectoryPathOrg1),
    });


    try {
        const contractOrg1 = gatewayOrg1
            .getNetwork(channelName)
            .getContract(chaincodeName);

        const resultBytes = await contractOrg1.evaluateTransaction('getAllAssets');

        const resultString = utf8Decoder.decode(resultBytes);
        const result = JSON.parse(resultString);
        console.log('*** Result:', result);

    } finally {
        gatewayOrg1.close();
        clientOrg1.close();
    }
}
main().then(() => console.log('done')).catch((e) => console.log(e));