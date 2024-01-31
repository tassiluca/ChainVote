beforeAll(async () => {
    app = ExpressConfig();
});

beforeEach(async () => {
    /**
     * INITIALIZATION CODE
     */
});

afterEach(async () => {
    /**
     * CLEANUP CODE
     */
});

describe("POST /code/", () => {

    test("Can check if a code is valid", async () => {
        const electionId = await createElection(app, adminJwtToken.accessToken);
        const code = await createCodeForElection(app, electionId, userJwtToken.accessToken);

        console.log(code, user.id, electionId);
        const response = await request(app).post("/code/check")
            .send({code: code, electionId: electionId })
            .set("Authorization", `Bearer ${userJwtToken.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);

        expect(response.body.success).toBe(true);
        expect(response.body.data).toBe(true);
    }, MAX_TIMEOUT);


    test("Can verify code owner", async () => {
        const electionId = await createElection(app, adminJwtToken.accessToken);
        const code = await createCodeForElection(app, electionId, userJwtToken.accessToken);
        const response = await request(app).post("/code/verify-owner")
            .send({code: code, userId: user.id, electionId: electionId})
            .set("Authorization", `Bearer ${userJwtToken.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", "application/json; charset=utf-8")
            .expect(StatusCodes.OK);

        expect(response.body.data).toBe(true);
    }, MAX_TIMEOUT);
});
