import { Router } from "express";
import {castVote, createElection, deleteElection, getAllElection, readElection} from "../controllers/election";
import RedisLimiterStorage from "../configs/redis.config";
import {apiLimiter, ApiLimiterEntry} from "core-components";
import {authenticationHandler} from "../middleware/authentication.middleware";

const electionRouter = Router();

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
    "/vote": {
        "PUT": {
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

electionRouter.use(apiLimiter(API_LIMITER_RULES, limitStorage));

/**
 * @openapi
 *
 * paths:
 *   /election/all:
 *      get:
 *          summary: Return all the election created
 *          responses:
 *              '200':
 *                  description: Request accepted successfully.
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
electionRouter.get("/all", authenticationHandler, getAllElection);

/**
 * @openapi
 *
 * paths:
 *   /election/info/detail/{electionId}:
 *      get:
 *          summary: Return a specific election created
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
electionRouter.get("/detail/:electionId", authenticationHandler, readElection);

/**
 * @openapi
 *
 * paths:
 *   /election/:
 *      post:
 *          summary: Create a new election
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              electionId:
 *                                  type: string
 *                                  description: The id of an election info to use.
 *
 *          responses:
 *              '201':
 *                  description: The resource was created successfully.
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
electionRouter.post("/", authenticationHandler, createElection);

/**
 * @openapi
 *
 * paths:
 *   /election/vote/{electionId}:
 *      put:
 *          summary: Cast a vote for the election
 *          parameters:
 *              - name: electionId
 *                in: path
 *                description: The id of the election.
 *                required: true
 *                schema:
 *                  type: string
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              choice:
 *                                  type: string
 *                                  description: The expressed vote.
 *                              userId:
 *                                  type: string
 *                                  description: The id of the user that is casting the vote.
 *                              code:
 *                                  type: string
 *                                  description: The vote code.
 *          responses:
 *              '200':
 *                  description: The request was handled successfully.
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
electionRouter.put("/vote/:electionId", authenticationHandler, castVote);

/**
 * @openapi
 *
 * paths:
 *   /election/:
 *      delete:
 *          summary: Delete an election
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
electionRouter.delete("/", authenticationHandler, deleteElection);

export default electionRouter;