//import ExpressConfig from "../src/configs/express.config";
import {User} from "core-components";
import {StatusCodes} from "http-status-codes";
import ExpressConfig from "../src/configs/express.config";
import request from "supertest";
import {createCodeForElection, createElection} from "./common/utils";

let user;
let app;

beforeAll(async () => {
    app = ExpressConfig();
});

beforeEach(async () => {
    user = await new User({
        email: "fake.email@email.it",
        password: "Password1!",
        firstName: "Test",
        secondName: "User"
    }).save();
});

afterEach(async () => {
    await User.deleteMany({});
});

describe("POST /code/", () => {

    test("Can create a new code", async () => {
        const electionId = await createElection(app);
        createCodeForElection(app, user.id, electionId);
    }, 20000);

    test("Can check if a code is valid", async () => {
        const electionId = await createElection(app);
        const code = await createCodeForElection(app, user.id, electionId);
        const response = await request(app).post("/code/is-valid")
            .send({code: code, userId: user.id, electionId: electionId })
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);

        expect(response.body.success).toBe(true);
        expect(response.body.data).toBe(true);
    }, 20000);


    test("Can verify code owner", async () => {
        const electionId = await createElection(app);
        const code = await createCodeForElection(app, user.id, electionId);
        const response = await request(app).post("/code/verify-owner")
            .send({code: code, userId: user.id, electionId: electionId})
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);

        expect(response.body.data).toBe(true);
    }, 20000);
});


describe("PATCH /code/", () => {
    test("Can invalidate a code", async () => {
        const electionId = await createElection(app);
        const code = await createCodeForElection(app, user.id, electionId);
        const response = await request(app).patch("/code/invalidate")
            .send({code: code, userId: user.id, electionId: electionId})
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);
        expect(response.body.data).toBeDefined();
        expect(response.body.success).toBe(true);
    }, 20000);
});