import * as grpc from '@grpc/grpc-js';
import * as crypto from 'crypto';
import { connect, Identity, signers } from '@hyperledger/fabric-gateway';
import { promises as fs } from 'fs';
import { TextDecoder } from 'util';
import path from "path";

const utf8Decoder = new TextDecoder();

async function main(): Promise<void> {

    const org1Dir = path.resolve(
        "/tmp",
        "hyperledger",
        "org1"
    );
    

    const credentials = await fs.readFile('path/to/certificate.pem');
    const identity: Identity = { mspId: 'myorg', credentials };

    const privateKeyPem = await fs.readFile('path/to/privateKey.pem');
    const privateKey = crypto.createPrivateKey(privateKeyPem);
    const signer = signers.newPrivateKeySigner(privateKey);

    const client = new grpc.Client('gateway.example.org:1337', grpc.credentials.createInsecure());

    const gateway = connect({ identity, signer, client });
    try {
        const network = gateway.getNetwork('channelName');
        const contract = network.getContract('chaincodeName');

        const putResult = await contract.submitTransaction('put', 'time', new Date().toISOString());
        console.log('Put result:', utf8Decoder.decode(putResult));

        const getResult = await contract.evaluateTransaction('get', 'time');
        console.log('Get result:', utf8Decoder.decode(getResult));
    } finally {
        gateway.close();
        client.close()
    }
}

main().catch(console.error);