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
 * Get all the election data
 */
electionInfoRouter.get("/all", authenticationHandler, getAllElectionInfo);

/**
 * Return a specific election data
 */
electionInfoRouter.get("/detail/:electionId",   authenticationHandler, readElectionInfo);

/**
 * Create a new election data
 */
electionInfoRouter.post("/", authenticationHandler, createElectionInfo);

/**
 * Delete a specific election data
 */
electionInfoRouter.delete("/", authenticationHandler, deleteElectionInfo);

/**
 * Check if a specific election data exists
 */
// electionRouter.get("/data/:electionId/exists", electionInfoExists);

export default electionInfoRouter;