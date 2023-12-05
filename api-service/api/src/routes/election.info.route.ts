import { Router } from "express";
import {
    createElectionInfo,
    deleteElectionInfo,
    getAllElectionInfo,
    readElectionInfo
} from "../controllers/election.info";
import {apiLimiter, ApiLimiterEntry} from "core-components";
import RedisLimiterStorage from "../configs/redis.config";
import {authenticationHandler} from "core-components";
import {validationHandler} from "core-components";
import {body, param} from "express-validator";

const electionInfoRouter = Router();

const limitStorage = new RedisLimiterStorage();
const API_LIMITER_RULES: ApiLimiterEntry = {
    "/all": {
        "GET": {
            time: 10,
            limit: 100
        }
    },
    "/detail": {
        "GET": {
            time: 20,
            limit: 100
        }
    },
    "/": {
        "POST": {
            time: 20,
            limit: 100
        },
        "DELETE": {
            time: 20,
            limit: 100
        }
    }
}

electionInfoRouter.use(apiLimiter(API_LIMITER_RULES, limitStorage));

/**
 * @openapi
 *
 * paths:
 *   /election/info/all:
 *      get:
 *          summary: Return all the election info created
 *          responses:
 *              '200':
 *                  description: Ok
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/CommonResponse'
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
electionInfoRouter.get("/all", authenticationHandler, getAllElectionInfo);

/**
 * @openapi
 *
 * paths:
 *   /election/info/detail/{electionId}:
 *      get:
 *          summary: Return a specific election info created in the system
 *          parameters:
 *              - name: electionId
 *                in: path
 *                description: The id of the election to get the info from.
 *                required: true
 *                schema:
 *                  type: string
 *          responses:
 *              '200':
 *                  description: Ok
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/CommonResponse'
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
electionInfoRouter.get(
    "/detail/:electionId",
    authenticationHandler,
    validationHandler([
        param("electionId").exists()
    ]),
    readElectionInfo);

/**
 * @openapi
 *
 * paths:
 *   /election/info/:
 *      post:
 *          summary: Create a new election info
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              goal:
 *                                  type: string
 *                                  description: The goal to reach with this election.
 *                              voters:
 *                                  type: string
 *                                  description: The number of participants to the election.
 *                              startDate:
 *                                  type: string
 *                                  description: The start date of the election in ISO format.
 *                              endDate:
 *                                  type: string
 *                                  description: The end date of the election in ISO format.
 *                              choices:
 *                                  type: array
 *                                  description: An array of string representing the mutually exclusive choices of the election.
 *          responses:
 *              '201':
 *                  description: Created
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/CommonResponse'
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
electionInfoRouter.post(
    "/",
    authenticationHandler,
    validationHandler([
        body("goal").exists().isString(),
        body("voters").exists().isNumeric(),
        body("startDate").exists().isISO8601(),
        body("endDate").exists().isISO8601(),
        body("choices").exists().isArray()
    ]),
    createElectionInfo);

/**
 * @openapi
 *
 * paths:
 *   /election/info/:
 *      delete:
 *          summary: Delete an election info
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              electionId:
 *                                  type: string
 *                                  description: The election id to delete
 *          responses:
 *              '200':
 *                  description: Ok
 *                  content:
 *                      application/json:
 *                          schema:
 *                              allOf:
 *                                  - $ref: '#/components/schemas/CommonResponse'
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
electionInfoRouter.delete(
    "/",
    authenticationHandler,
    validationHandler([
        body("electionId").exists()
    ]),
    deleteElectionInfo);

export default electionInfoRouter;