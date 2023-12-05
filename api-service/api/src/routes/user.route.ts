import { Router } from "express";
import { createUser, getProfile, editProfile, deleteProfile } from "../controllers/users";
import { authenticationHandler } from "core-components";
import { validationHandler } from "core-components";
import {body, param} from "express-validator";
import {ApiLimiterEntry,apiLimiter} from "core-components";
import RedisLimiterStorage from "../configs/redis.config";

const userRouter = Router();


const API_LIMITER_RULES: ApiLimiterEntry = {
    "/": {
        "GET": {
            time: 20,
            limit: 100
        },
        "PUT": {
            time: 20,
            limit: 100
        },
        "DELETE": {
            time: 20,
            limit: 100
        },
        "POST": {
            time: 10,
            limit: 100
        }
    },
}

const limitStorage = new RedisLimiterStorage();

userRouter.use(apiLimiter(API_LIMITER_RULES, limitStorage));

userRouter.get(
    "/:email",
    authenticationHandler,
    validationHandler([
        param("email").exists().isEmail()
    ]),
    getProfile);

userRouter.get("/", authenticationHandler, getProfile);

userRouter.put(
    "/:email",
    authenticationHandler,
    validationHandler([
        // Param validation
        param("email").exists().isEmail(),

        // Body validation
        body("data").isObject().notEmpty(),
        body("data.firstName").optional().isAlpha(),
        body("data.secondName").optional().isAlpha(),

        // Check that the user is not trying to change the email or the password
        body("data.password").not().exists(),
        body("data.email").not().exists(),
    ]),
    editProfile);

userRouter.put(
    "/",
    authenticationHandler,
    editProfile
);

userRouter.delete(
    "/:email",
    authenticationHandler,
    validationHandler([
        param("email").exists().isEmail()
    ]),
    deleteProfile);

userRouter.delete("/", authenticationHandler, deleteProfile);

userRouter.post(
    "/",
    validationHandler([
        body("email").exists().isEmail(),
        body("password").exists().isStrongPassword(),
        body("firstName").exists().isAlpha(),
        body("secondName").exists().isAlpha(),
    ]),
    createUser);

export default userRouter;