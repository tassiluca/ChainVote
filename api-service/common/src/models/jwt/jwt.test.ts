import { setupConnection, destroyConnection, dropCollectionsInDb } from "../../utils/local.db"

import {User} from "../users/users";
import {Jwt} from "./jwt";
import {ConfigurationObject, JwtHandler} from "../../utils/jwt/jwt.handler";
import {resolve} from "path";
import {UnauthorizedError} from "../../errors/errors";

const configuration: ConfigurationObject = {
    ATPrivateKeyPath: resolve("./secrets/at_private.pem"),
    RTPrivateKeyPath: resolve("./secrets/rt_private.pem")
}

beforeAll(async () => {
    await setupConnection();
    JwtHandler.config(configuration);
});

afterAll(async () => {
    await destroyConnection();
});

afterEach(async () => {
    await dropCollectionsInDb();
});

describe("Token's insertion in database",  () => {
    test("Should manually save a token successfully in database", async () => {
        const user = await new User({
            email: "claudio.rossi@email.it",
            password: "PassworD1!",
            firstName: "Claudio",
            secondName: "Rossi"
        }).save();
        const refreshToken = JwtHandler.getInstance().signRefreshToken(user, "10m");
        const accessToken = JwtHandler.getInstance().signAccessToken(user, "5m");

        const tokensRecord = await new Jwt({
            refreshToken: refreshToken,
            accessToken: accessToken,
            email: user.email
        }).save();

        expect(tokensRecord._id).toBeDefined();
        expect(tokensRecord.refreshToken).toBe(refreshToken);
        expect(tokensRecord.accessToken).toBe(accessToken);
        expect(tokensRecord.email).toBe(user.email);
        expect(tokensRecord.enabled).toBe(true);
    });

    test("Should save a token successfully in database", async () => {
        const user = await new User({
            email: "claudio.rossi@email.it",
            password: "PassworD1!",
            firstName: "Claudio",
            secondName: "Rossi"
        }).save();

        const tokensRecord = await Jwt.createTokenPair(user);
        expect(tokensRecord).toBeDefined();
        expect(tokensRecord.email).toBe(user.email);
    });
});

describe("Security features", () => {
    test("Should block other tokens if I generate another one for a specific user", async () => {
        const user = await new User({
            email: "claudio.rossi@email.it",
            password: "PassworD1!",
            firstName: "Claudio",
            secondName: "Rossi"
        }).save();
        // Create two token for the user, the first one will be blocked
        const firstToken = await Jwt.createTokenPair(user);
        const secondToken = await Jwt.createTokenPair(user);

        const existsEnabled = await Jwt.exists({
            refreshToken: secondToken.refreshToken,
            email: user.email,
            enabled: true
        });
        const existsDisabled = await Jwt.exists({
            refreshToken: firstToken.refreshToken,
            email: user.email,
            enabled: false
        });

        expect(existsEnabled).not.toBeNull();
        expect(existsDisabled).not.toBeNull();
    });
});

describe("Token's validation", () => {

    test("Should validate a valid token", async () => {
        const user = await new User({
            email: "user.one@email.it",
            password: "PassworD1!",
            firstName: "User",
            secondName: "Uno"
        }).save();

        const jwt = await Jwt.createTokenPair(user);
        const validationResponse = await jwt.validateAccessToken();

        expect(validationResponse).toBeDefined();
        expect(validationResponse.sub.email).toBe(user.email);
        expect(validationResponse.sub.firstName).toBe(user.firstName);
        expect(validationResponse.sub.secondName).toBe(user.secondName);
    });

    test("Shouldn't save a token if the email's doesn't belong to any user",  async () => {
        try {
            await new Jwt({accessToken: "test", refreshToken: "test", email: "groppo.galoppo@bho.it"}).save();
        } catch (error) {
            expect(error).toBeDefined();
        }
    });

    test("Shouldn't validate a token if the record is disabled",  async () => {
        const user = await new User({
            email: "user.one@email.it",
            password: "PassworD1!",
            firstName: "User",
            secondName: "Uno"
        }).save();

        const jwt = await Jwt.createTokenPair(user);
        jwt.enabled = false;
        await jwt.save();

        try {
            const validationResponse = await jwt.validateAccessToken();
        } catch (error) {
            expect(error).toBeDefined();
            expect(error).toBeInstanceOf(UnauthorizedError);
        }

        try {
            const validationResponse = await jwt.validateRefreshToken();
        } catch (error) {
            expect(error).toBeDefined();
            expect(error).toBeInstanceOf(UnauthorizedError);
        }

    });
});

