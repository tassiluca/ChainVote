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

    keyPath(keyPath: string): CommunicatorBuilder {
        this._keyPath = keyPath;
        return this;
    }

    certPath(certPath: string): CommunicatorBuilder {
        this._certPath = certPath;
        return this;
    }

    peerTlsPath(peerTlsPath: string): CommunicatorBuilder {
        this._peerTlsPath = peerTlsPath;
        return this;
    }

    peerEndpoint(peerEndpoint: string): CommunicatorBuilder {
        this._peerEndpoint = peerEndpoint;
        return this;
    }

    hostAlias(hostAlias: string): CommunicatorBuilder {
        this._hostAlias = hostAlias;
        return this;
    }

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