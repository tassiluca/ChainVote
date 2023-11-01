import {DateTime} from "luxon";
import {StatusCodes} from "http-status-codes";
import request from "supertest";
import {Application} from "express";

export async function createElectionInfo(app: Application) {
    const startDate = DateTime.now()
        .set({millisecond: 0})
        .toISO({includeOffset: false, suppressMilliseconds: true});
    const endDate = DateTime.now()
        .set({millisecond: 0})
        .plus({days: 1})
        .toISO({includeOffset: false, suppressMilliseconds: true});

    const goal = "Test goal " + DateTime.now().toISO();
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


export async function createElection(app: Application) {
    const electionId = await createElectionInfo(app);
    await request(app).post("/election")
        .send({ electionId: electionId })
        .set("Accept", "application/json; charset=utf-8")
        .expect(StatusCodes.OK);

    expect(electionId).toBeDefined();
    return electionId;
}

export async function createCodeForElection(app: Application, userId, electionId) {
    const response = await request(app).post("/code/generate")
        .send({ userId: userId, electionId: electionId })
        .set("Accept", "application/json")
        .expect("Content-Type", "application/json; charset=utf-8")
        .expect(StatusCodes.OK);

    expect(response.body.result).toBeDefined();
    expect(response.body.success).toBe(true);
    return response.body.result;
}