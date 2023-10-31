import request from "supertest";
import ExpressConfig from "../src/configs/express.config";
import {StatusCodes} from "http-status-codes";
import {DateTime} from "luxon";

let app;

beforeAll(async () => {
    app = ExpressConfig();
});


describe("GET /elections/info", () => {
    test("Can get all the election infos", async () => {
        await request(app).get("/election/info/all")
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.OK);
    });
    
});
