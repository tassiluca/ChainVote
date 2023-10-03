
import { promises as fs } from 'fs';
import * as grpc from '@grpc/grpc-js';
import { Identity, Signer, signers } from '@hyperledger/fabric-gateway';
import * as path from 'path';
import * as crypto from 'crypto';

export class Config {
    readonly _keyPath: string;
    readonly _certPath: string;
    readonly _peerTlsPath: string; 
    readonly _peerEndpoint: string;
    readonly _hostAlias: string;
    readonly _mspId: string;

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

    async createGrpcClient(): Promise<grpc.Client> {
        const tlsRootCert = await fs.readFile(this._peerTlsPath);
        console.log("TLS Root Cert ----->");
        console.log(tlsRootCert);
        const tlsCredentials = grpc.credentials.createSsl(tlsRootCert);
        return new grpc.Client(this._peerEndpoint, tlsCredentials, {
            'grpc.ssl_target_name_override': this._hostAlias,
        });
    }
    
    async createIdentity(): Promise<Identity> {
        const credentials: Buffer = await fs.readFile(this._certPath);
        console.log("Credentials Cert ----->");
        console.log(credentials);
        const mspId: string = this._mspId;
        return { mspId, credentials };
    }
    
    async createSigner(): Promise<Signer> {
        const files = await fs.readdir(this._keyPath);
        const keyPath = path.resolve(this._keyPath, files[0]);
        const privateKeyPem = await fs.readFile(keyPath);
        console.log("Private key pem ----->");
        console.log(privateKeyPem);
        const privateKey = crypto.createPrivateKey(privateKeyPem);
        return signers.newPrivateKeySigner(privateKey);
    }   
}
