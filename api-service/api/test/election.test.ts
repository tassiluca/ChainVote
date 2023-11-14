import ExpressConfig from "../src/configs/express.config";
import {createCodeForElection, createElection} from "./common/utils";
import {StatusCodes} from "http-status-codes";
import request from "supertest";
import {Jwt, JwtHandler, User} from "core-components";
import {resolve} from "path";

let app;
let user, userJwtToken;
let admin, adminJwtToken;

const MAX_TIMEOUT = 20_000;

JwtHandler.config({
    ATPrivateKeyPath: resolve("./secrets/at_private.pem"),
    RTPrivateKeyPath: resolve("./secrets/rt_private.pem")
});

beforeAll(async () => {
    app = ExpressConfig();

    user = await new User({
        email: "fake.email2@email.it",
        password: "Password1!",
        firstName: "Test",
        secondName: "User"
    }).save();

    admin = await new User({
        email: "admin.email1@email.it",
        password: "Password1!",
        firstName: "Test",
        secondName: "User",
        role: "admin"
    }).save();

    userJwtToken = await Jwt.createTokenPair(user, {accessToken: "10m", refreshToken: "20m"});
    adminJwtToken = await Jwt.createTokenPair(admin, {accessToken: "10m", refreshToken: "20m"});

});


afterAll(async () => {
    await User.deleteMany({});
    await Jwt.deleteMany({});
});

describe("POST /election", () => {
    test("Can create a new election", async () => await createElection(app, adminJwtToken.accessToken), MAX_TIMEOUT);
});


describe("GET /election/", () => {
    test("Can get a specific election", async () => {
        const electionId = await createElection(app, adminJwtToken.accessToken);
        const response = await request(app).get("/election/detail/" + electionId)
            .set("Authorization", `Bearer ${userJwtToken.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);

        expect(response.body.success).toBe(true);
        expect(response.body.data).toBeDefined();
        expect(response.body.data.id).toBe(electionId);

    }, MAX_TIMEOUT);
});


describe("PUT /election/", () => {
    test("Can cast a vote for a specific election", async () => {
        const electionId = await createElection(app, adminJwtToken.accessToken);
        const code = await createCodeForElection(app, user.id, electionId, userJwtToken.accessToken);

        const response = await request(app).put(`/election/vote/${electionId}`)
            .send({choice: "Choice 1", code: code, userId: user.id})
            .set("Authorization", `Bearer ${userJwtToken.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);

        expect(response.body.success).toBe(true);
        expect(response.body.data).toBe(true);
    }, MAX_TIMEOUT);
});

describe("DELETE /election", () => {
   test("Can delete a specific election", async () => {
      const electionId = await createElection(app, adminJwtToken.accessToken);
        await request(app).delete("/election")
            .send({electionId: electionId})
            .set("Authorization", `Bearer ${adminJwtToken.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);
   }, MAX_TIMEOUT);
});

