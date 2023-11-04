import request from "supertest";
import ExpressConfig from "../src/configs/express.config";
import {StatusCodes} from "http-status-codes";
import {createElectionInfo} from "./common/utils";


const MAX_TIMEOUT = 20_000;
let app;

beforeAll(async () => {
    app = ExpressConfig();
});

describe("GET /election/info", () => {
    test("Can get all the election infos", async () => {
        const numElections = 3;
        for (let i = 0; i < numElections; i++) {
            await createElectionInfo(app);
        }
        const response = await request(app).get("/election/info/all")
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);

        expect(response.body.success).toBe(true);
        expect(response.body.data.length).toBeGreaterThanOrEqual(numElections);

        const oneResult = response.body.data[0];

        expect(oneResult.electionId).toBeDefined();
        expect(oneResult.voters).toBeDefined();
        expect(oneResult.goal).toBeDefined();
        expect(oneResult.startDate).toBeDefined();
        expect(oneResult.endDate).toBeDefined();
        expect(oneResult.choices).toBeDefined();

    }, MAX_TIMEOUT);

    test("Can get a specific election info", async () => {
        const electionId = await createElectionInfo(app);
        const response = await request(app).get("/election/info/detail/" + electionId)
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);

        expect(response.body.data.electionId).toEqual(electionId);
    }, MAX_TIMEOUT);
});

describe("POST /election/info", () => {
   test("Can create a new election info", async () => createElectionInfo(app), MAX_TIMEOUT);
});

describe("DELETE /election/info", () => {
    test("Can delete a specific election info", async () => {
        const electionId = await createElectionInfo(app);
        await request(app).delete("/election/info/")
            .send({electionId: electionId})
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);
    }, MAX_TIMEOUT);
});