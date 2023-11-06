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

/**
 * @openapi
 *
 * paths:
 *   /users/{userEmail}:
 *      get:
 *          summary: Return a specific user
 *          parameters:
 *              - name: userEmail
 *                in: path
 *                description: The email of the user to get the info from.
 *                required: false
 *                schema:
 *                  type: string
 *          responses:
 *              '200':
 *                  description: Request accepted successfully.
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
userRouter.get("/:email", authenticationHandler, getProfile);

userRouter.get("/", authenticationHandler, getProfile);

/**
 * @openapi
 *
 * paths:
 *   /users/{userEmail}:
 *      put:
 *          summary: Update a specific user
 *          parameters:
 *              - name: userEmail
 *                in: path
 *                description: The email of the user to update.
 *                required: false
 *                schema:
 *                  type: string
 *          responses:
 *              '200':
 *                  description: Request accepted successfully.
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
userRouter.put("/:email", authenticationHandler, editProfile);

userRouter.put("/", authenticationHandler, editProfile);

/**
 * @openapi
 *
 * paths:
 *   /users/{userEmail}:
 *      delete:
 *          summary: Delete a specific user
 *          parameters:
 *              - name: userEmail
 *                in: path
 *                description: The email of the user to delete.
 *                required: false
 *                schema:
 *                  type: string
 *          responses:
 *              '200':
 *                  description: Request accepted successfully.
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
userRouter.delete("/:email", authenticationHandler, deleteProfile);

userRouter.delete("/", authenticationHandler, deleteProfile);

/**
 * @openapi
 *
 * paths:
 *   /users:
 *      post:
 *          summary: Create a new user
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              email:
 *                                  type: string
 *                                  description: The email of the user
 *                              password:
 *                                  type: string
 *                                  description: The password of the user, this will be encrypted.
 *                              firstName:
 *                                  type: string
 *                                  description: The first name of the user.
 *                              secondName:
 *                                  type: string
 *                                  description: The second name of the user.
 *          responses:
 *              '201':
 *                  description: The new user is created.
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
userRouter.post("/", createUser);

export default userRouter;