import ExpressConfig from "../src/configs/express.config";
import {createElection} from "./common/utils";
import {StatusCodes} from "http-status-codes";
import request from "supertest";

let app;

const MAX_TIMEOUT = 20_000;
beforeAll(async () => {app = ExpressConfig()});

describe("POST /election", () => {
    test("Can create a new election", async () => createElection(app), MAX_TIMEOUT);
});

describe("DELETE /election", () => {
   test("Can delete a specific election", async () => {
      const electionId = await createElection(app);
        await request(app).delete("/election")
            .send({electionId: electionId})
            .set("Accept", "application/json")
            .expect("Content-Type", "text/html; charset=utf-8")
            .expect(StatusCodes.OK);
   }, MAX_TIMEOUT);
});

