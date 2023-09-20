import request from "supertest";
import ExpressConfig from "../src/configs/express.config";
import {Jwt, User} from "core-components";
import {StatusCodes} from "http-status-codes";

const app = ExpressConfig();
let user;
beforeAll(async () => {
    user = new User({
        email: "test.user@email.it",
        password: "Password1!",
        firstName: "Test",
        secondName: "User",
    });

    await user.save();
});

afterAll(async () => {
    await User.findOneAndDelete({email: user.email});
});


describe("POST /auth/login", () => {
    test("Login with user data", async () => {
        const response = await request(app).post("/auth/login")
            .send({
                email: user.email,
                password: "Password1!"
            })
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.OK);

        expect(response.body).toHaveProperty("accessToken");
        expect(response.body).toHaveProperty("refreshToken");
        expect(response.body).toHaveProperty("email");

        expect(response.body.email).toBe(user.email);
    });

    test("Login with wrong user data", async () => {
        const response = await request(app).post("/auth/login")
            .send({
                email: user.email,
                password: "password"
            })
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.UNAUTHORIZED);

        expect(response.body).toHaveProperty("code");
        expect(response.body).toHaveProperty("name");
        expect(response.body).toHaveProperty("message");

        expect(response.body.code).toBe(StatusCodes.UNAUTHORIZED);
        expect(response.body.name).toBe("Unauthorized");
        expect(response.body.message).toBe("Login error");
    });


    test("Login with wrong user data", async () => {
        const response = await request(app).post("/auth/login")
            .send({
                email: user.email,
            })
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.BAD_REQUEST);

        expect(response.body).toHaveProperty("code");
        expect(response.body).toHaveProperty("name");
        expect(response.body).toHaveProperty("message");

        expect(response.body.code).toBe(StatusCodes.BAD_REQUEST);
        expect(response.body.name).toBe("Bad Request");
        expect(response.body.message).toBe("Please provide email and password");

        const response2 = await request(app).post("/auth/login")
            .send({
                password: "test",
            })
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.BAD_REQUEST);

        expect(response2.body).toHaveProperty("code");
        expect(response2.body).toHaveProperty("name");
        expect(response2.body).toHaveProperty("message");

        expect(response2.body.code).toBe(StatusCodes.BAD_REQUEST);
        expect(response2.body.name).toBe("Bad Request");
        expect(response2.body.message).toBe("Please provide email and password");
    });
});

describe("POST /auth/refresh", () => {
    let jwtDefault;

    beforeEach(async () => {
        jwtDefault = await Jwt.createTokenPair(user, {"accessToken": "10m", "refreshToken": "20m"});
    });

    afterEach(async () => {
        await Jwt.findOneAndDelete({refreshToken: jwtDefault.refreshToken});
    });


    test("Refresh token with valid data", async () => {
        const response = await request(app).post("/auth/refresh")
            .send({
                email: user.email,
                refreshToken: jwtDefault.refreshToken
            })
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.OK);

        expect(response.body).toHaveProperty("email");
        expect(response.body).toHaveProperty("accessToken");
        expect(response.body).toHaveProperty("refreshToken");
    });

    test("Can't refresh a token if the mail doesn't exists", async () => {
        const response = await request(app).post("/auth/refresh")
            .send({
                email: "wrong.email@test.it",
                refreshToken: jwtDefault.refreshToken
            })
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.NOT_FOUND);

        expect(response.body).toHaveProperty("code");
        expect(response.body).toHaveProperty("name");
        expect(response.body).toHaveProperty("message");

        expect(response.body.code).toBe(StatusCodes.NOT_FOUND);
        expect(response.body.name).toBe("Not Found");
        expect(response.body.message).toBe("The specified email doesn't belong to any users");
    });

    test("Can't refresh a token if the token doesn't exists", async () => {
        const response = await request(app).post("/auth/refresh")
            .send({
                email: user.email,
                refreshToken: "wrong.token"
            })
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.NOT_FOUND);

        expect(response.body).toHaveProperty("code");
        expect(response.body).toHaveProperty("name");
        expect(response.body).toHaveProperty("message");

        expect(response.body.code).toBe(StatusCodes.NOT_FOUND);
        expect(response.body.name).toBe("Not Found");
        expect(response.body.message).toBe("Can't find the requested token");
    });

    test("Can't refresh a token if the token doesn't belongs to the user", async () => {

        const user2 = await new User({
            email: "test.user2@email.it",
            password: "Password1!",
            firstName: "Test",
            secondName: "UserDos",
        }).save();

        const jwtDefault2 = await Jwt.createTokenPair(user2,
            {"accessToken": "10m", "refreshToken": "20m"});

        const response = await request(app).post("/auth/refresh")
            .send({
                email: user.email,
                refreshToken: jwtDefault2.refreshToken
            })
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.UNAUTHORIZED);

        expect(response.body).toHaveProperty("code");
        expect(response.body).toHaveProperty("name");
        expect(response.body).toHaveProperty("message");

        expect(response.body.code).toBe(StatusCodes.UNAUTHORIZED);
        expect(response.body.name).toBe("Unauthorized");
        expect(response.body.message).toBe("The submitted token doesn't belong to the specified user");

        await User.findOneAndDelete({email: user2.email});
        await Jwt.findOneAndDelete({refreshToken: jwtDefault2.refreshToken});

    });

});
