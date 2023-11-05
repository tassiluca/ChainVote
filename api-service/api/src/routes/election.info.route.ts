import { Router } from "express";
import {
    createElectionInfo,
    deleteElectionInfo,
    getAllElectionInfo,
    readElectionInfo
} from "../controllers/election.info";
import {apiLimiter, ApiLimiterEntry} from "core-components";
import RedisLimiterStorage from "../configs/redis.config";
import {authenticationHandler} from "../middleware/authentication.middleware";

const electionInfoRouter = Router();

const limitStorage = new RedisLimiterStorage();
const API_LIMITER_RULES: ApiLimiterEntry = {
    "/all": {
        "GET": {
            time: 10,
            limit: 10
        }
    },
    "/detail": {
        "GET": {
            time: 20,
            limit: 40
        }
    },
    "/": {
        "POST": {
            time: 20,
            limit: 5
        },
        "DELETE": {
            time: 20,
            limit: 5
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
 *                  description: Request accepted successfully.
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
electionInfoRouter.get("/all", authenticationHandler, getAllElectionInfo);

/**
 * @openapi
 *
 * paths:
 *   /election/info/detail/{electionId}:
 *      get:
 *          summary: Return a specific election info created
 *          parameters:
 *              - name: electionId
 *                in: path
 *                description: The id of the election to get the info from.
 *                required: true
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
electionInfoRouter.get("/detail/:electionId",   authenticationHandler, readElectionInfo);

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
 *                  description: The resource was created successfully.
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
electionInfoRouter.post("/", authenticationHandler, createElectionInfo);

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
 *                  description: The resource was deleted successfully.
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
electionInfoRouter.delete("/", authenticationHandler, deleteElectionInfo);

export default electionInfoRouter;