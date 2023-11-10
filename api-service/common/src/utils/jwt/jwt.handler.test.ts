
import {setupConnection, destroyConnection, dropCollectionsInDb} from "../local.db"
import { User } from "../../models/users/users";
import { resolve } from "path";
import { ConfigurationObject, JwtHandler} from "./jwt.handler";
import {Jwt} from "../../models/jwt/jwt";
import {BadRequestError} from "../../errors/errors";

const configuration: ConfigurationObject = {
    ATPrivateKeyPath: resolve("./secrets/at_private.pem"),
    RTPrivateKeyPath: resolve("./secrets/rt_private.pem")
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

    JwtHandler.config(configuration);
    user = await user.save();
});

afterAll(async () => {
    await destroyConnection();
});

describe("Sign of jwt tokens", () => {
    test("Should create an access token successfully",  () => {
        const accessToken = JwtHandler.getInstance().signAccessToken({sub: user});
        expect(accessToken).not.toBe({});
        expect(typeof accessToken).toBe("string");
    });


    test("Should create a refresh token successfully",  async () => {
        const refreshToken = await JwtHandler.getInstance().signRefreshToken(user);
        expect(refreshToken).not.toBe({});
        expect(typeof refreshToken).toBe("string");
    });
});

describe("Verification of a jwt token", () => {

    test("Should verify an access token", async () => {
        const accessToken = JwtHandler.getInstance().signAccessToken(user);
        const verifiedTokenResponse: any = await JwtHandler.getInstance().verifyAccessToken(accessToken);
        expect(verifiedTokenResponse).toBeDefined();

        const subscriber = verifiedTokenResponse.sub;
        expect(subscriber.email).toBe("claudio.rossi@email.it");
        expect(subscriber.firstName).toBe("Claudio");
        expect(subscriber.secondName).toBe("Rossi");
        expect(subscriber.role).toBe("user");
    });

    test("Should verify a refresh token", async () => {
        const refreshToken= await JwtHandler.getInstance().signRefreshToken(user);
        expect(await Jwt.exists({refreshToken: refreshToken, email: user.email, enabled: true})).toBeDefined();

        const verifiedTokenResponse: any = await JwtHandler.getInstance().verifyRefreshToken(refreshToken);
        expect(verifiedTokenResponse).toBeDefined();

        const subscriber = verifiedTokenResponse.sub;
        expect(subscriber.email).toBe("claudio.rossi@email.it");
        expect(subscriber.firstName).toBe("Claudio");
        expect(subscriber.secondName).toBe("Rossi");
        expect(subscriber.role).toBe("user");
    });

    test("Should refuse to validate an expired token", async () => {
        const refreshToken = await JwtHandler.getInstance().signRefreshToken(user, "0s");
        let err;
        try {
            await JwtHandler.getInstance().verifyRefreshToken(refreshToken)
        } catch (error) {
            err = error;
        }
        expect(err).toBeDefined();
        expect(err).toBeInstanceOf(BadRequestError);
    });
});