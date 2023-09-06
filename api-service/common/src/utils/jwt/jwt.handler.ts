import { readFileSync } from "fs";
import jwt, { SignOptions } from 'jsonwebtoken';
import {BadRequestError, User} from "../..";
import {Jwt} from "../../models/jwt/jwt";

export type ConfigurationObject = {
    ATPrivateKeyPath?: string;
    ATPublicKeyPath?: string;
    RTPrivateKeyPath?: string;
    RTPublicKeyPath?: string;
};

export class JwtHandler {

    private static INSTANCE: JwtHandler | null = null;
    private internalConfiguration: ConfigurationObject;

    private signOptions: SignOptions = {
        issuer:   "ChainVote",
        audience: "https://www.chainvote.com",
        algorithm: "RS256"
    };

    private constructor(config: ConfigurationObject | null | undefined = undefined) {
        if(!config) {
            this.internalConfiguration = {}
        } else {
            this.internalConfiguration = config;
        }
    }
    public static config(config: ConfigurationObject): void {
        this.INSTANCE = new JwtHandler(config);
    }

    public static getInstance(config: ConfigurationObject | {}  = {}): JwtHandler {
        if(!this.INSTANCE) {
            this.INSTANCE = new JwtHandler(config);
        }

        return this.INSTANCE;
    }

    public setConfig(key: string, value: any): void {
        this.internalConfiguration[key] = value;
    }

    public getConfig(key: string): any {
        return this.internalConfiguration[key];
    }

    public verifyAccessToken<T>(token: string): T | undefined {
        const publicKeyPath = this.internalConfiguration.ATPublicKeyPath || process.env.AT_PUBLIC_KEY_PATH;
        if(!publicKeyPath) {
            throw new Error("Access token public key path is not set");
        }
        const publicKey: string = readFileSync(publicKeyPath, 'utf8');
        return this.verifyJwt(token, publicKey) as T
    }

    public async verifyRefreshToken<T>(token: string): Promise<T | undefined> {
        const publicKeyPath = this.internalConfiguration.RTPublicKeyPath || process.env.RT_PUBLIC_KEY_PATH;
        if(!publicKeyPath) {
            throw new Error("Refresh token public key path is not set");
        }

        const publicKey: string = readFileSync(publicKeyPath, 'utf8');
        const verifiedResponse = this.verifyJwt(token, publicKey) as T
        // @ts-ignore
        const subscriber = verifiedResponse.sub;
        if(!subscriber) {
            throw new Error("Unable to verify the identity of the subscriber");
        }

        const jwtRecord = await Jwt.exists({token:token, email: subscriber.email, enabled: true})
            .sort({createdAt: 'descending'});
        if(jwtRecord === null) {
            const checkPrevious = await Jwt.findOne({token:token}).sort({createdAt: 'ascending'});
            if(checkPrevious) {
                await Jwt.deleteMany({email: subscriber.email});
                throw new Error(`An attempt was made to verify an old token belonging to the user: ${subscriber.email}. 
                For security reasons we have disabled access, a new login should be made in order to generate a new token`);
            }

            throw new Error("Unable to verifying the access token");
        }

        return verifiedResponse;
    }

    public signAccessToken(user, expiration: string | undefined = undefined) {
        const privateKeyPath = this.internalConfiguration.ATPrivateKeyPath || process.env.AT_PRIVATE_KEY_PATH;
        if(!privateKeyPath) {
            throw new Error("Access token private key path is not set");
        }
        if(!expiration) {
            expiration = "1m";
        }
        const privateKey: string = readFileSync(privateKeyPath, 'utf8');
        return this.signJwt({sub: user}, privateKey, {expiresIn: expiration});
    }

    public async signRefreshToken(user, expiration: string | undefined = undefined): Promise<string> {
        const privateKeyPath = this.internalConfiguration.RTPrivateKeyPath || process.env.RT_PRIVATE_KEY_PATH;

        if(!privateKeyPath) {
            throw new Error("Refresh token private key path is not set");
        }
        if(!expiration) {
            expiration = "10m";
        }
        const privateKey: string = readFileSync(privateKeyPath, 'utf8');
        const generatedToken = this.signJwt({sub: user}, privateKey, {expiresIn: expiration});

        await Jwt.updateMany({email: user.email}, { enabled: false });

        const jwt = new Jwt({
            email: user.email,
            token: generatedToken
        });

        try {
            await jwt.save();
        } catch (error) {
            throw new Error("Error while saving the token: " + error.message);
        }

        return generatedToken;
    }

    private signJwt(payload: Object, pKey: string, options: SignOptions) {
        if(!("sub" in payload)) {
            throw new Error("sub property should be present in the payload, assign the User class that is making the reuqest");
        }
        try {
            return jwt.sign(payload, pKey, {...this.signOptions, ...options});
        } catch(error) {
            throw new Error("Error while creating a new payload: " + error.message);
        }
    }

    private verifyJwt<T>(token: string, pKey: string): T | null {
        return jwt.verify(token, pKey, (error, decoded) => {
            if(error) {
                throw new BadRequestError(error.message);
            }
            return decoded;
        }) as T;
    }
}

