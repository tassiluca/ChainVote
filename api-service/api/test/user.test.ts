import {StatusCodes} from "http-status-codes";
import request from "supertest";
import {Jwt, JwtHandler, User} from "core-components";
import ExpressConfig from "../src/configs/express.config";
import {resolve} from "path";

let app;
let jwtUser, jwtAdmin
let user, otherUser, admin;

JwtHandler.config({
    ATPrivateKeyPath: resolve("./secrets/at_private.pem"),
    RTPrivateKeyPath: resolve("./secrets/rt_private.pem")
});

beforeAll(async () => {
    app = ExpressConfig();

    user = await new User({
        email: "fake.email@email.it",
        password: "Password1!",
        firstName: "Test",
        secondName: "User"
    }).save();

    otherUser = await new User({
        email: "other.user@email.it",
        password: "Password1!",
        firstName: "Test",
        secondName: "User"
    }).save();

   admin = await new User({
        email: "admin.email@email.it",
        password: "Password1!",
        firstName: "Test",
        secondName: "User",
        role: "admin"
    }).save();

    jwtUser = await Jwt.createTokenPair(user, {accessToken: "10m", refreshToken: "20m"});
    jwtAdmin = await Jwt.createTokenPair(admin, );

});

afterAll(async () => {
    await User.deleteMany({});
    await Jwt.deleteMany({});
});

const MAX_TIMEOUT = 20_000;
describe("GET /users/", () => {
    test("Get the informations of an user", async () => {
        const response = await request(app).get("/users/")
            .set("Authorization", `Bearer ${jwtUser.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            //.expect(StatusCodes.OK);

        console.log(response.body);

        expect(response.body.success).toBe(true);
        expect(response.body.data).toHaveProperty("email");
        expect(response.body.data).toHaveProperty("firstName");
        expect(response.body.data).toHaveProperty("secondName");
        expect(response.body.data.email).toBe(user.email);
        expect(response.body.data.firstName).toBe(user.firstName);
        expect(response.body.data.secondName).toBe(user.secondName);
    }, MAX_TIMEOUT);


    test("Can't get the informations of an user with an invalid token", async () => {
        const response = await request(app).get("/users/")
            .set("Authorization", `Bearer invalid`)
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.NOT_FOUND);

        expect(response.body).toHaveProperty("code");
        expect(response.body).toHaveProperty("error");

        expect(response.body.code).toBe(StatusCodes.NOT_FOUND);
        expect(response.body.error.message).toBe("The submitted jwt token doesn't exists");
        expect(response.body.error.name).toBe("Not Found");
    }, MAX_TIMEOUT);


    test("Can't get the informations of another user with an admin account", async () => {
        const response = await request(app).get(`/users/${otherUser.email}`)
            .set("Authorization", `Bearer ${jwtUser.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.UNAUTHORIZED);

        expect(response.body).toHaveProperty("error");
        expect(response.body.error).toHaveProperty("message");
        expect(response.body.error).toHaveProperty("name");

        expect(response.body.code).toBe(StatusCodes.UNAUTHORIZED);
        expect(response.body.error.message).toBe("Can't access to the resource");
        expect(response.body.error.name).toBe("Unauthorized");
    }, MAX_TIMEOUT);

    test("Get the informations of another user with an admin account", async () => {
        const response = await request(app).get(`/users/${otherUser.email}`)
            .set("Authorization", `Bearer ${jwtAdmin.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.OK);

        expect(response.body.data).toHaveProperty("email");
        expect(response.body.data).toHaveProperty("firstName");
        expect(response.body.data).toHaveProperty("secondName");

        expect(response.body.data.email).toBe(otherUser.email);
        expect(response.body.data.firstName).toBe(otherUser.firstName);
        expect(response.body.data.secondName).toBe(otherUser.secondName);
    }, MAX_TIMEOUT);
});


describe("POST /users/", () => {
    test("Create a new user", async () => {
        const response = await request(app).post("/users/")
            .send({
                email: "new.user@email.it",
                password: "Password1!",
                firstName: "New",
                secondName: "User"
            }).set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.CREATED);

        expect(response.body).toHaveProperty("code");
        expect(response.body).toHaveProperty("data");

        expect(response.body.code).toBe(StatusCodes.CREATED);
        expect(response.body.data.email).toBe("new.user@email.it");
        expect(response.body.data.firstName).toBe("New");
        expect(response.body.data.secondName).toBe("User");
    }, MAX_TIMEOUT);
});


describe("PUT /users/", () => {
    test("Edit the informations of an user", async () => {
        const response = await request(app).put("/users/")
            .send({
                data: {
                    firstName: "Claudio",
                    secondName: "Pozzi"
                }
            })
            .set("Authorization", `Bearer ${jwtUser.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.OK);

            expect(response.body.data).toBe(true);
    }, MAX_TIMEOUT);

    test("Can't edit the informations of another user", async () => {
        const response = await request(app).put(`/users/${otherUser.email}`)
            .send({
                data: {
                    firstName: "Claudio",
                    secondName: "Pozzi"
                }
            })
            .set("Authorization", `Bearer ${jwtUser.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.UNAUTHORIZED);

        expect(response.body).toHaveProperty("code");
        expect(response.body.error).toHaveProperty("message");
        expect(response.body.error).toHaveProperty("name");

        expect(response.body.code).toBe(StatusCodes.UNAUTHORIZED);
        expect(response.body.error.message).toBe("Can't access to the resource");
        expect(response.body.error.name).toBe("Unauthorized");
    }, MAX_TIMEOUT);

    test("Edit the informations of another user with an admin account", async () => {
        const response = await request(app).put(`/users/${otherUser.email}`)
            .send({
                data: {
                    firstName: "Claudio",
                    secondName: "Pozzi"
                }
            })
            .set("Authorization", `Bearer ${jwtAdmin.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.OK);

            expect(response.body).toHaveProperty("data");
            expect(response.body.data).toBe(true);
            expect(response.body.code).toBe(StatusCodes.OK);
    }, MAX_TIMEOUT);

});

describe("DELETE /users/", () => {

    let userToDelete;
    let jwtToDelete;

    beforeEach(async () => {
        userToDelete = await new User({
            email: "todelete.user@email.it",
            password: "Password1!",
            firstName: "ToDelete",
            secondName: "User"
        }).save();

        jwtToDelete = await Jwt.createTokenPair(userToDelete);
    });

    afterEach(async () => {
        await User.deleteMany({firstName: "ToDelete"});
        await Jwt.deleteMany({email: userToDelete.email});
    });


    test("Delete an user", async () => {

        const response = await request(app).delete("/users/")
            .set("Authorization", `Bearer ${jwtToDelete.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.OK);

        expect(response.body).toHaveProperty("data");
        expect(response.body.data).toBe(true);
        expect(response.body.code).toBe(StatusCodes.OK);
    }, MAX_TIMEOUT);

    test("Can't delete another user", async () => {
        const response = await request(app).delete(`/users/${userToDelete.email}`)
            .set("Authorization", `Bearer ${jwtUser.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.UNAUTHORIZED);

        expect(response.body).toHaveProperty("code");
        expect(response.body).toHaveProperty("error");

        expect(response.body.code).toBe(StatusCodes.UNAUTHORIZED);
        expect(response.body.error.message).toBe("Can't access to the resource");
        expect(response.body.error.name).toBe("Unauthorized");
    }, MAX_TIMEOUT);

    test("Delete another user with an admin account", async () => {
        const response = await request(app).delete(`/users/${userToDelete.email}`)
            .set("Authorization", `Bearer ${jwtAdmin.accessToken}`)
            .set("Accept", "application/json")
            .expect("Content-Type", /json/)
            .expect(StatusCodes.OK);

        expect(response.body).toHaveProperty("code");
        expect(response.body).toHaveProperty("data");
        expect(response.body.code).toBe(StatusCodes.OK);
        expect(response.body.data).toBe(true);
    }, MAX_TIMEOUT);
});