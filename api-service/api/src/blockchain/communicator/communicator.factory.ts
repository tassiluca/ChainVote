import {CommunicatorInterface} from "./communicator";
import {CommunicatorBuilder} from "./communicator.builder";
import path from "path";

export class CommunicatorFactory {
    static createCommunicatorForOrg1(peerFolderName: string, peerEndpoint: string, hostAlias: string): CommunicatorInterface {

        const cryptoPath = path.resolve('/tmp', 'hyperledger', 'org1');
        const mspId = 'org1MSP';

        return new CommunicatorBuilder()
            .keyPath(path.resolve(cryptoPath, 'admin', 'msp', 'keystore'))
            .certPath(path.resolve(cryptoPath, 'admin', 'msp', 'signcerts', 'cert.pem'))
            .peerTlsPath(path.resolve(cryptoPath, peerFolderName, 'tls-msp', 'tlscacerts', 'tls-0-0-0-0-7052.pem'))
            .peerEndpoint(peerEndpoint)
            .hostAlias(hostAlias)
            .mspId(mspId)
            .build();
    }

    static createCommunicatorForOrg2(peerFolderName: string, peerEndpoint: string, hostAlias: string): CommunicatorInterface {

        const cryptoPath = path.resolve('/tmp', 'hyperledger', 'org2');
        const mspId = 'org2MSP';

        return new CommunicatorBuilder()
            .keyPath(path.resolve(cryptoPath, 'admin', 'msp', 'keystore'))
            .certPath(path.resolve(cryptoPath, 'admin', 'msp', 'signcerts', 'cert.pem'))
            .peerTlsPath(path.resolve(cryptoPath, peerFolderName, 'tls-msp', 'tlscacerts', 'tls-0-0-0-0-7052.pem'))
            .peerEndpoint(peerEndpoint)
            .hostAlias(hostAlias)
            .mspId(mspId)
            .build();
    }
}