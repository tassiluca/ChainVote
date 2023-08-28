import { readFileSync } from "fs";
import * as path from "path";
import jwt, { SignOptions } from 'jsonwebtoken';

export function signAccessToken(payload: Object) {
    const privateKey: string = readFileSync(path.resolve(__dirname, '../../secrets/at_private.pem'), 'utf8');
    return signJwt(payload, privateKey, {expiresIn: "20m"});
}

export function signRefreshToken(payload: Object) {
    const privateKey: string = readFileSync(path.resolve(__dirname, '../../secrets/rt_private.pem'), 'utf8');
    return signJwt(payload, privateKey, {expiresIn: "60m"});
}

export function signJwt(payload: Object, pKey: string, options: SignOptions) {
    var signOptions: SignOptions = {
        issuer:   "ChainVote",
        audience: "https://www.chainvote.com",
        algorithm: "RS256"
    };
    return jwt.sign(payload, pKey, {...signOptions, ...options});
}


