import {Communicator} from "./communicator";

export class CommunicatorBuilder {
    private _keyPath: string;
    private _certPath: string;
    private _peerTlsPath: string;
    private _peerEndpoint: string;
    private _hostAlias: string;
    private _mspId: string;

    constructor() {
        this._keyPath = '';
        this._certPath = '';
        this._peerTlsPath = '';
        this._peerEndpoint = '';
        this._hostAlias = '';
        this._mspId = '';
    }

    /**
     * Set the key path of the msp
     * @param keyPath
     */
    keyPath(keyPath: string): CommunicatorBuilder {
        this._keyPath = keyPath;
        return this;
    }

    /**
     * Set the certificate path of the msp
     * @param certPath
     */
    certPath(certPath: string): CommunicatorBuilder {
        this._certPath = certPath;
        return this;
    }

    /**
     * Set the peer tls path
     * @param peerTlsPath
     */
    peerTlsPath(peerTlsPath: string): CommunicatorBuilder {
        this._peerTlsPath = peerTlsPath;
        return this;
    }

    /**
     * Set the gateway peer endpoint that will interact with the blockchain
     * @param peerEndpoint
     */
    peerEndpoint(peerEndpoint: string): CommunicatorBuilder {
        this._peerEndpoint = peerEndpoint;
        return this;
    }

    /**
     * Set the host alias of the peer
     * @param hostAlias
     */
    hostAlias(hostAlias: string): CommunicatorBuilder {
        this._hostAlias = hostAlias;
        return this;
    }

    /**
     * Set the id of the msp
     * @param mspId
     */
    mspId(mspId: string): CommunicatorBuilder {
        this._mspId = mspId;
        return this;
    }

    build(): Communicator {
        return new Communicator(
            this._keyPath,
            this._certPath,
            this._peerTlsPath,
            this._peerEndpoint,
            this._hostAlias,
            this._mspId
        );
    }
}