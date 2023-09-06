
import { setupConnection, destroyConnection } from "../local.db"
import { User } from "../../models/users/users";
import { resolve } from "path";
import {
    ConfigurationObject,
    initJWTSystem,
    signAccessToken,
    signRefreshToken,
    verifyAccessToken,
    verifyRefreshToken
} from "./jwt.handler"
import { BadRequestError } from "../../errors/errors";

const configuration: ConfigurationObject = {
    ATPrivateKeyPath: resolve("./secrets/at_private.pem"),
    RTPrivateKeyPath: resolve("./secrets/rt_private.pem"),
    ATPublicKeyPath: resolve("./secrets/at_public.pem"),
    RTPublicKeyPath: resolve("./secrets/rt_public.pem"),
}

let user; 

beforeAll(async () => {
    await setupConnection();
    user = new User({
        email: "claudio.rossi@email.it",
        password: "PassworD1!",
        firstName: "Claudio",
        secondName: "Rossi"
    });
    user = await user.save();
    initJWTSystem(configuration);
});

afterAll(async () => {
    await destroyConnection();
});

describe("Sign of jwt tokens", () => {
    test("Should create an access token successfully",  () => {
        const accessToken = signAccessToken({sub: user});
        expect(accessToken).not.toBe({});
        expect(typeof accessToken).toBe("string");
    });
    
    test("Should create a refresh token successfully",  () => {
        const refreshToken = signRefreshToken({sub: user});
        expect(refreshToken).not.toBe({});
        expect(typeof refreshToken).toBe("string");
    });
}); 


describe("Verification of a jwt token", () => {

    test("Should verify an access token", () => {
        const accessToken = signAccessToken(user);
        const verifiedTokenResponse: any = verifyAccessToken(accessToken);
        expect(verifiedTokenResponse).toBeDefined();
        
        const subscriber = verifiedTokenResponse.sub;
        expect(subscriber.email).toBe("claudio.rossi@email.it");
        expect(subscriber.firstName).toBe("Claudio");
        expect(subscriber.secondName).toBe("Rossi");
        expect(subscriber.role).toBe("user");
    });

    test("Should verify a refresh token", async () => {
        const reshreshToken= await signRefreshToken(user);
        expect(user.tokens).not.toBe({});

        await signRefreshToken(user);
        const test = await User.findOne({email: user.email},null,{});
        if(test) {
            console.log(test.tokens[test.tokens.length - 1]);
        }

        const verifiedTokenResponse: any = verifyRefreshToken(reshreshToken);
        expect(verifiedTokenResponse).toBeDefined();
        
        //console.log(verifiedTokenResponse);


        const subscriber = verifiedTokenResponse.sub;
        expect(subscriber.email).toBe("claudio.rossi@email.it");
        expect(subscriber.firstName).toBe("Claudio");
        expect(subscriber.secondName).toBe("Rossi");
        expect(subscriber.role).toBe("user");
    });

    test("Should refuse to validate an expired token ", async () => {
        const now = new Date();
        const reshreshToken = await signRefreshToken({sub: user}, "0s");
        expect(() => verifyRefreshToken(reshreshToken)).toThrow(BadRequestError);
    });


}); 