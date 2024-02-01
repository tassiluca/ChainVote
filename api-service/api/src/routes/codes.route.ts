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
 *                              electionId:
 *                                  type: string
 *                                  description: The election id for which generates a one-time-code.
 *                      example:
 *                          electionId: 1728519885
 *          responses:
 *              '201':
 *                  description: Created
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/CommonResponse'
 *                                  - type: object
 *                                    properties:
 *                                      data:
 *                                          type: string
 *                                          description: The generated code
 *                                          example: 123456
 *              '400':
 *                  description: Bad Request
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/BadRequestError'
 *
 *              '429':
 *                  description: Too many requests
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/TooManyRequestError'
 *              '500':
 *                  description: Generic server error
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/InternalServerError'
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
 *                      example:
 *                          userId: 2050333334
 *                          code: 6e692a06ad
 *          responses:
 *              '200':
 *                  description: Ok
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/CommonResponse'
 *                                  - type: object
 *                                    properties:
 *                                      data:
 *                                          type: boolean
 *                                          description: Return true if the code is still valid, false otherwise
 *                                          example: true
 *              '400':
 *                  description: Bad Request
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/BadRequestError'
 *
 *              '429':
 *                  description: Too many requests
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/TooManyRequestError'
 *              '500':
 *                  description: Generic server error
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/InternalServerError'
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
 *          summary: Check if the code belongs to the user that is making the request (via JWT token)
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
 *                      example:
 *                          electionId: 1728519885
 *                          code: 6e692a06ad
 *          responses:
 *              '200':
 *                  description: Ok
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/CommonResponse'
 *                                  - type: object
 *                                    properties:
 *                                      data:
 *                                          type: boolean
 *                                          description: Return true if the code is still valid, false otherwise
 *                                          example: true
 *              '400':
 *                  description: Bad Request
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/BadRequestError'
 *
 *              '429':
 *                  description: Too many requests
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/TooManyRequestError'
 *              '500':
 *                  description: Generic server error
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/InternalServerError'
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