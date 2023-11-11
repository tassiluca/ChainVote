import {CommunicatorInterface} from "./communicator";
import {CommunicatorBuilder} from "./communicator.builder";
import * as path from "path";

const certs = process.env.CERTS_DIR || path.join(
    __dirname,
    '..',
    '..',
    '..',
    '..',
    '..',
    '.chainvote',
    'blockchain');

const ORG1_CRYPTO_PATH = path.resolve(certs, 'org1');
const ORG1_MSP_ID = 'org1MSP';

const ORG2_CRYPTO_PATH = path.resolve(certs, 'org2');
const ORG2_MSP_ID = 'org2MSP';
export class CommunicatorFactory {
    static org1WithEndpoint(tlsRootFolder: string, peerEndpoint: string, hostAlias: string): CommunicatorInterface {
        return new CommunicatorBuilder()
            .keyPath(path.resolve(ORG1_CRYPTO_PATH, 'admin', 'msp', 'keystore'))
            .certPath(path.resolve(ORG1_CRYPTO_PATH, 'admin', 'msp', 'signcerts', 'cert.pem'))
            .peerTlsPath(path.resolve(ORG1_CRYPTO_PATH, tlsRootFolder, 'assets', 'tls-ca', 'tls-ca-cert.pem'))
            .peerEndpoint(peerEndpoint)
            .hostAlias(hostAlias)
            .mspId(ORG1_MSP_ID)
            .build();
    }

    static org2WithEndpoint(peerFolderName: string, peerEndpoint: string, hostAlias: string): CommunicatorInterface {
        return new CommunicatorBuilder()
            .keyPath(path.resolve(ORG2_CRYPTO_PATH, 'admin', 'msp', 'keystore'))
            .certPath(path.resolve(ORG2_CRYPTO_PATH, 'admin', 'msp', 'signcerts', 'cert.pem'))
            .peerTlsPath(path.resolve(ORG2_CRYPTO_PATH, peerFolderName, 'tls-msp', 'tlscacerts', 'tls-0-0-0-0-7052.pem'))
            .peerEndpoint(peerEndpoint)
            .hostAlias(hostAlias)
            .mspId(ORG2_MSP_ID)
            .build();
    }
}