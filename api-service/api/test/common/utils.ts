import {DateTime} from "luxon";
import {StatusCodes} from "http-status-codes";
import request from "supertest";
import {Application} from "express";

export async function createElectionInfo(app: Application, accessToken: string) {
    const startDate = DateTime.now()
        .set({millisecond: 0})
        .minus({hour: 1})
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
        .set("Authorization", `Bearer ${accessToken}`)
        .set("Accept", "application/json")
        .expect("Content-Type", "application/json; charset=utf-8")
        .expect(StatusCodes.OK);

    expect(createResponse.body.success).toBe(true);
    expect(createResponse.body.data).toBeDefined();

    const dataReturned = createResponse.body.data;

    expect(dataReturned.goal).toBe(goal);
    expect(dataReturned.voters).toBe(parseInt(voters));
    expect(dataReturned.startDate).toBe(startDate);
    expect(dataReturned.endDate).toBe(endDate);

    return createResponse.body.data.electionId;
}


export async function createElection(app: Application, accessToken: string ) {
    const electionId = await createElectionInfo(app, accessToken);
    const createResponse = await request(app).post("/election")
        .set("Authorization", `Bearer ${accessToken}`)
        .send({ electionId: electionId })
        .set("Accept", "application/json; charset=utf-8")
        .expect(StatusCodes.OK);

    expect(createResponse.body.success).toBe(true);
    expect(createResponse.body.data).toBeDefined();
    return electionId;
}

export async function createCodeForElection(app: Application, userId, electionId, accessToken: string) {
    const response = await request(app).post("/code/generate")
        .send({ userId: userId, electionId: electionId })
        .set("Authorization", `Bearer ${accessToken}`)
        .set("Accept", "application/json")
        .expect("Content-Type", "application/json; charset=utf-8")
        .expect(StatusCodes.OK);

    expect(response.body.success).toBe(true);
    expect(response.body.data).toBeDefined();

    return response.body.data;
}