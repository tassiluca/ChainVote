import { setupConnection, destroyConnection, dropCollectionsInDb } from "../../utils/local.db"

import {User} from "../users/users";
import {Jwt} from "./jwt";
import {ConfigurationObject, initJWTSystem, signAccessToken} from "../../utils/jwt/jwt.handler";
import {resolve} from "path";


const configuration: ConfigurationObject = {
    ATPrivateKeyPath: resolve("./secrets/at_private.pem"),
    RTPrivateKeyPath: resolve("./secrets/rt_private.pem"),
    ATPublicKeyPath: resolve("./secrets/at_public.pem"),
    RTPublicKeyPath: resolve("./secrets/rt_public.pem"),
}

beforeAll(async () => {
    await setupConnection();
    initJWTSystem(configuration);
});

afterAll(async () => {
    await destroyConnection();
});

afterEach(async () => {
    await dropCollectionsInDb();
});


describe("Token's insertion in database",  () => {

    test("Should save a token successfully in database", async () => {
        const user = await new User({
            email: "claudio.rossi@email.it",
            password: "PassworD1!",
            firstName: "Claudio",
            secondName: "Rossi"
        }).save();
        const accessToken = signAccessToken(user);
        const token = await new Jwt({token: accessToken, email: user.email}).save();
        expect(token._id).toBeDefined();
        expect(token.token).toBe(accessToken);
    });

});


describe("Token's validation", () => {

    test("Can't save a token if the email's doesn't belong to any user",  async () => {
        try {
            await new Jwt({token: "test", email: "groppo.galoppo@bho.it"}).save();
        } catch (error) {
            expect(error).toBeDefined();
        }
    });
});