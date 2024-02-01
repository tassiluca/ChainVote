import request from "supertest";
import ServerConfig from "../src/configs/server.config";
import {StatusCodes} from "http-status-codes";
import {createElectionInfo} from "./common/utils";
import {Jwt, JwtHandler, User} from "core-components";
import {resolve} from "path";

const MAX_TIMEOUT = 20_000;
let app= ServerConfig();

let admin, adminJwtToken;
let user, userJwtToken;

JwtHandler.config({
    ATPrivateKeyPath: resolve("./secrets/at_private.pem"),
    RTPrivateKeyPath: resolve("./secrets/rt_private.pem")
});

beforeAll(async () => {
    admin = await new User({
        email: "admin.email1@email.it",
        password: "Password1!",
        firstName: "Test",
        secondName: "User",
        role: "admin"
    }).save();

    user = await new User({
        email: "usr.email@email.it",
        password: "Password1!",
        firstName: "Test",
        secondName: "User",
    }).save();

    adminJwtToken = await Jwt.createTokenPair(admin, {accessToken: "10m", refreshToken: "20m"});
    userJwtToken = await Jwt.createTokenPair(user, {accessToken: "10m", refreshToken: "20m"});
});

afterAll(async () => {
    await User.deleteMany({});
    await Jwt.deleteMany({});
});

describe("GET /election/info", () => {
    test("Can get all the election infos", async () => {
        const numElections = 3;
        for (let i = 0; i < numElections; i++) {
            await createElectionInfo(app, adminJwtToken.accessToken);
        }
        const responseAdmin = await request(app).get("/election/info/all")
            .set("Authorization", `Bearer ${adminJwtToken.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);

        expect(responseAdmin.body.success).toBe(true);
        expect(responseAdmin.body.data.length).toBeGreaterThanOrEqual(numElections);

        const oneResult = responseAdmin.body.data[0];

        expect(oneResult.electionId).toBeDefined();
        expect(oneResult.voters).toBeDefined();
        expect(oneResult.goal).toBeDefined();
        expect(oneResult.startDate).toBeDefined();
        expect(oneResult.endDate).toBeDefined();
        expect(oneResult.choices).toBeDefined();


        const responseUser =  await request(app).get("/election/info/all")
            .set("Authorization", `Bearer ${userJwtToken.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);


        expect(responseUser.body.success).toBe(true);
        expect(responseUser.body.data.length).toBeGreaterThanOrEqual(numElections);

        const oneResultUser = responseAdmin.body.data[0];

        expect(oneResultUser.electionId).toBeDefined();
        expect(oneResultUser.voters).toBeDefined();
        expect(oneResultUser.goal).toBeDefined();
        expect(oneResultUser.startDate).toBeDefined();
        expect(oneResultUser.endDate).toBeDefined();
        expect(oneResultUser.choices).toBeDefined();

    }, MAX_TIMEOUT);

    test("Can get a specific election info", async () => {
        const electionId = await createElectionInfo(app, adminJwtToken.accessToken);
        const responseAdmin = await request(app).get("/election/info/detail/" + electionId)
            .set("Authorization", `Bearer ${adminJwtToken.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);
        expect(responseAdmin.body.data.electionId).toEqual(electionId);

        const responseUser = await request(app).get("/election/info/detail/" + electionId)
            .set("Authorization", `Bearer ${userJwtToken.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);

        expect(responseUser.body.data.electionId).toEqual(electionId);
    }, MAX_TIMEOUT);
});

describe("POST /election/info", () => {
   test("Can create a new election info", async () => createElectionInfo(app, adminJwtToken.accessToken), MAX_TIMEOUT);
});

describe("DELETE /election/info", () => {
    test("Can delete a specific election info", async () => {
        const electionId = await createElectionInfo(app, adminJwtToken.accessToken);
        await request(app).delete("/election/info/")
            .send({electionId: electionId})
            .set("Authorization", `Bearer ${adminJwtToken.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);
    }, MAX_TIMEOUT);
});