
import { promises as fs } from 'fs';
import * as grpc from '@grpc/grpc-js';
import { Identity, Signer, signers } from '@hyperledger/fabric-gateway';
import * as path from 'path';
import * as crypto from 'crypto';

export interface CommunicatorInterface {
    createGrpcClient(): Promise<grpc.Client>
    createIdentity(): Promise<Identity>
    createSigner(): Promise<Signer>
}

export class Communicator implements CommunicatorInterface {

    private _keyPath: string;
    private _certPath: string;
    private _peerTlsPath: string;
    private _peerEndpoint: string;
    private _hostAlias: string;
    private _mspId: string;

    constructor (
        keyPath: string,
        certPath: string,
        peerTlsPath: string,
        peerEndpoint: string,
        hostAlias: string,
        mspId: string
    ) {
        this._keyPath = keyPath;
        this._certPath = certPath;
        this._peerTlsPath = peerTlsPath;
        this._peerEndpoint = peerEndpoint;
        this._hostAlias = hostAlias;
        this._mspId = mspId;
    }

    /**
    * Create a grpc client to interact with the blockchain
    */
    async createGrpcClient(): Promise<grpc.Client> {
        const tlsRootCert = await fs.readFile(this._peerTlsPath);
        const tlsCredentials = grpc.credentials.createSsl(tlsRootCert);
        return new grpc.Client(this._peerEndpoint, tlsCredentials, {
            'grpc.ssl_target_name_override': this._hostAlias,
        });
    }

    /**
    * Create the identity object that represent the identity that will interact with the blockchain.
    */
    async createIdentity(): Promise<Identity> {
        const credentials: Buffer = await fs.readFile(this._certPath);
        const mspId: string = this._mspId;
        return { mspId, credentials };
    }

    /**
    * Create the signer object that will endorse the transactions.
    */
    async createSigner(): Promise<Signer> {
        const files = await fs.readdir(this._keyPath);
        const keyPath = path.resolve(this._keyPath, files[0]);
        const privateKeyPem = await fs.readFile(keyPath);
        const privateKey = crypto.createPrivateKey(privateKeyPem);
        return signers.newPrivateKeySigner(privateKey);
    }
}
