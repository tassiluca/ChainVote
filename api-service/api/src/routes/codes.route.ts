import { Router } from "express";
import {generateCodeFor, isValid, verifyCodeOwner} from "../controllers/codes";
import RedisLimiterStorage from "../configs/redis.config";
import {apiLimiter, ApiLimiterEntry} from "core-components";
import {authenticationHandler} from "core-components";
import {validationHandler} from "core-components"
import {body} from "express-validator";

const codesRoute =  Router();

const limitStorage = new RedisLimiterStorage();
const API_LIMITER_RULES: ApiLimiterEntry = {
    "/generate": {
        "POST": {
            time: 20,
            limit: 100
        }
    },
    "/check": {
        "POST": {
            time: 20,
            limit: 100,
        }
    },

    "/verify-owner": {
        "POST": {
            time: 20,
            limit: 100
        }
    }
}
codesRoute.use(apiLimiter(API_LIMITER_RULES, limitStorage));

/**
 * @openapi
 *
 * paths:
 *   /code/generate:
 *      post:
 *          summary: Generate a new voting code
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              userId:
 *                                  type: string
 *                                  description: The id of a user.
 *          responses:
 *              '201':
 *                  description: The election is created successfully.
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
codesRoute.post(
    "/generate",
    authenticationHandler,
    validationHandler([
        body("electionId").exists().isNumeric(),
    ]),
    generateCodeFor);

/**
 * @openapi
 *
 * paths:
 *   /code/check:
 *      post:
 *          summary: Check if a given code is valid
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              electionId:
 *                                  type: string
 *                                  description: The id of an election.
 *                              code:
 *                                  type: string
 *                                  description: The code to check
 *          responses:
 *              '200':
 *                  description: The request was handled successfully
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
codesRoute.post(
    "/check",
    authenticationHandler,
    validationHandler([
        body("electionId").exists().isNumeric(),
        body("code").exists().isAlphanumeric()
    ]),
    isValid
);

/**
 * @openapi
 *
 * paths:
 *   /code/verify-owner:
 *      post:
 *          summary: Check if the code belongs to the specified user
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              electionId:
 *                                  type: string
 *                                  description: The id of an election.
 *                              code:
 *                                  type: string
 *                                  description: The code to check
 *          responses:
 *              '200':
 *                  description: The request was handled successfully
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
codesRoute.post("/verify-owner",
    authenticationHandler,
    validationHandler([
        body("electionId").exists().isNumeric(),
        body("code").exists().isAlphanumeric()
    ]),
    verifyCodeOwner);


export default codesRoute;