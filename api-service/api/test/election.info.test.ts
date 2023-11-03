import request from "supertest";
import ExpressConfig from "../src/configs/express.config";
import {StatusCodes} from "http-status-codes";
import {createElectionInfo} from "./common/utils";

let app;

beforeAll(async () => {
    app = ExpressConfig();
});

describe("GET /election/info", () => {

    test("Can get all the election infos", async () => {
        for (let i = 0; i < 3; i++) {
            await createElectionInfo(app);
        }
        const response = await request(app).get("/election/info/all")
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);

        expect(response.body.result).toBeDefined();
        expect(response.body.result.length).toBeGreaterThanOrEqual(3);

        const oneResult = response.body.result[0];

        expect(oneResult.electionId).toBeDefined();
        expect(oneResult.voters).toBeDefined();
        expect(oneResult.goal).toBeDefined();
        expect(oneResult.startDate).toBeDefined();
        expect(oneResult.endDate).toBeDefined();
        expect(oneResult.choices).toBeDefined();

    }, 20000);

    test("Can get a specific election info", async () => {
        const electionId = await createElectionInfo(app);
        const response = await request(app).get("/election/info/" + electionId)
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.OK);

        expect(response.body.result.electionId).toEqual(electionId);
    });
});

describe("POST /election/info", () => {
   test("Can create a new election info", async () => createElectionInfo(app), 20000);
});

describe("DELETE /election/info", () => {
    test("Can delete a specific election info", async () => {
        const electionId = await createElectionInfo(app);
        await request(app).delete("/election/info/")
            .send({electionId: electionId})
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);
    }, 20000);
});