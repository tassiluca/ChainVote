import { Router } from "express";
import { createUser, getProfile, editProfile, deleteProfile } from "../controllers/users";
import { authenticationHandler } from "../middleware/authentication.middleware";
import {ApiLimiterEntry} from "core-components";
import { apiLimiter } from "core-components";
import RedisLimiterStorage from "../configs/redis.config";

const userRouter = Router();


const API_LIMITER_RULES: ApiLimiterEntry = {
    "/": {
        "GET": {
            time: 20,
            limit: 20
        },
        "PUT": {
            time: 20,
            limit: 5
        },
        "DELETE": {
            time: 20,
            limit: 5
        },
        "POST": {
            time: 80,
            limit: 1
        }
    },
}

const limitStorage = new RedisLimiterStorage();

userRouter.use(apiLimiter(API_LIMITER_RULES, limitStorage));

/**
 * Retrieve the information of a user
 */
userRouter.get("/:email", authenticationHandler, getProfile);

userRouter.get("/", authenticationHandler, getProfile);

/** 
 * Update the information of a user
 */
userRouter.put("/:email", authenticationHandler, editProfile);

userRouter.put("/", authenticationHandler, editProfile);

/**
 * Delete a user from the system
 */
userRouter.delete("/:email", authenticationHandler, deleteProfile);

userRouter.delete("/", authenticationHandler, deleteProfile);

/**
 * Create a new user.
 */
userRouter.post("/", createUser);

export default userRouter;