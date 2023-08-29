import { readFileSync } from "fs";
import jwt, { SignOptions } from 'jsonwebtoken';
import { BadRequestError } from "../..";

export type ConfigurationObject = {
    ATPrivateKeyPath?: string;
    ATPublicKeyPath?: string;
    RTPrivateKeyPath?: string;
    RTPublicKeyPath?: string;
};

const signOptions: SignOptions = {
    issuer:   "ChainVote",
    audience: "https://www.chainvote.com",
    algorithm: "RS256"
};

let localConfigurations: ConfigurationObject; 

/**
 * Setup the configurations relative to the public / private
 * key pair.
 * @param configurations 
 */
export function initJWTSystem(configurations) {
    localConfigurations = configurations;
}

/**
 * Verify an access token
 * @param token 
 * @returns 
 */
export function verifyAccessToken<T>(token: string): T | undefined {
    const publicKeyPath = localConfigurations.ATPublicKeyPath || process.env.AT_PUBLIC_KEY_PATH;
    if(!publicKeyPath) {
        throw new Error("Access token public key path is not set");
    }
    const publicKey: string = readFileSync(publicKeyPath, 'utf8');
    return verifyJwt(token, publicKey) as T
}

/**
 * Verify a refresh token
 * @param token 
 * @returns 
 */
export function verifyRefreshToken<T>(token: string): T | undefined {
    const publicKeyPath = localConfigurations.RTPublicKeyPath || process.env.RT_PUBLIC_KEY_PATH;
    if(!publicKeyPath) {
        throw new Error("Refresh token public key path is not set");
    }
    const publicKey: string = readFileSync(publicKeyPath, 'utf8');
    return verifyJwt(token, publicKey) as T
}

/**
 * Sign a new access token
 * @param payload 
 * @returns 
 */
export function signAccessToken(payload: Object, expiration: string | undefined = undefined) {
    const privateKeyPath = localConfigurations.ATPrivateKeyPath || process.env.AT_PRIVATE_KEY_PATH;
    if(!privateKeyPath) {
        throw new Error("Access token private key path is not set");
    }
    if(!expiration) {
        expiration = "1m";
    }
    const privateKey: string = readFileSync(privateKeyPath, 'utf8');
    return signJwt(payload, privateKey, {expiresIn: expiration});
}

/**
 * Sign a new refresh token
 * @param payload 
 * @returns 
 */
export function signRefreshToken(payload: Object, expiration: string | undefined = undefined) {
    const privateKeyPath = localConfigurations.RTPrivateKeyPath || process.env.RT_PRIVATE_KEY_PATH;
    
    if(!privateKeyPath) {
        throw new Error("Refresh token private key path is not set");
    }
    if(!expiration) {
        expiration = "10m";
    }
    const privateKey: string = readFileSync(privateKeyPath, 'utf8');
    return signJwt(payload, privateKey, {expiresIn: expiration});
}


function signJwt(payload: Object, pKey: string, options: SignOptions) {
    if(!("sub" in payload)) {
        throw new Error("sub property should be present in the payload, assign the User class that is making the reuqest"); 
    }
    try {
        return jwt.sign(payload, pKey, {...signOptions, ...options});
    } catch(error) {
        throw new Error("Error while creating a new payload: " + error.message);
    }
}

function verifyJwt<T>(token: string, pKey: string): T | null {
    return jwt.verify(token, pKey, (error, decoded) => {
        if(error) {
            throw new BadRequestError(error.message);
        } 
        return decoded;
    }) as T;
}
