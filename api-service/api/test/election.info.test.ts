import request from "supertest";
import ExpressConfig from "../src/configs/express.config";
import {StatusCodes} from "http-status-codes";
import {DateTime} from "luxon";
import {createElectionInfo} from "./common/utils";

let app;

beforeAll(async () => {
    app = ExpressConfig();
});

describe("GET /election/info", () => {
    test("Can get all the election infos", async () => {
        await request(app).get("/election/info/all")
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.OK);
    });

    test("Can get a specific election info", async () => {
        const electionId = await createElectionInfo(app);
        const response = await request(app).get("/election/info/" + electionId)
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.OK);
        expect(response.body.goal).toEqual("Test goal " + DateTime.now().toISODate());
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