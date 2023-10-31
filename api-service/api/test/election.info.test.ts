import request from "supertest";
import ExpressConfig from "../src/configs/express.config";
import {StatusCodes} from "http-status-codes";
import {DateTime} from "luxon";

let app;

beforeAll(async () => {
    app = ExpressConfig();
});


async function createElectionInfo() {
    const startDate = DateTime.now()
        .set({millisecond: 0})
        .toISO({includeOffset: false, suppressMilliseconds: true});
    const endDate = DateTime.now()
        .set({millisecond: 0})
        .plus({days: 1})
        .toISO({includeOffset: false, suppressMilliseconds: true});

    const goal = "Test goal " + DateTime.now().toISODate();
    const voters: string = "100";
    const choices = ["Choice 1", "Choice 2", "Choice 3"];

    const createResponse = await request(app).post("/election/info")
        .send({
            goal: goal,
            voters: voters,
            startDate: startDate,
            endDate: endDate,
            choices: choices
        })
        .set("Accept", "application/json")
        .expect("Content-Type", "text/html; charset=utf-8")
        .expect(StatusCodes.OK);

    return createResponse.text;
}

describe("GET /elections/info", () => {
    test("Can get all the election infos", async () => {
        await request(app).get("/election/info/all")
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.OK);
    });

    test("Can get a specific election info", async () => {
        const electionId = await createElectionInfo();
        const response = await request(app).get("/election/info/" + electionId)
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.OK);
        expect(response.body.goal).toEqual("Test goal " + DateTime.now().toISODate());
    });

    
});
