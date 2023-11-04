import ExpressConfig from "../src/configs/express.config";
import {createCodeForElection, createElection} from "./common/utils";
import {StatusCodes} from "http-status-codes";
import request from "supertest";
import {User} from "core-components";

let app, user;

const MAX_TIMEOUT = 20_000;
beforeAll(async () => {
    app = ExpressConfig();
    user = await new User({
        email: "fake.email2@email.it",
        password: "Password1!",
        firstName: "Test",
        secondName: "User"
    }).save();
});


afterAll(async () => {
    await User.deleteMany({});
});

describe("POST /election", () => {
    test("Can create a new election", async () => createElection(app), MAX_TIMEOUT);
});


describe("GET /election/", () => {
    test("Can get a specific election", async () => {
        const electionId = await createElection(app);
        const response = await request(app).get("/election/detail/" + electionId)
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
        const electionId = await createElection(app);
        const code = await createCodeForElection(app, user.id, electionId);

        const response = await request(app).put(`/election/${electionId}/vote`)
            .send({choice: "Choice 1", code: code, userId: user.id})
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);

        expect(response.body.success).toBe(true);
        expect(response.body.data).toBe(true);
    }, MAX_TIMEOUT);
});

describe("DELETE /election", () => {
   test("Can delete a specific election", async () => {
      const electionId = await createElection(app);
        await request(app).delete("/election")
            .send({electionId: electionId})
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);
   }, MAX_TIMEOUT);
});

