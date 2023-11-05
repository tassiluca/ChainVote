import { Router } from "express";
import {
    createElectionInfo,
    deleteElectionInfo,
    getAllElectionInfo,
    readElectionInfo
} from "../controllers/election.info";
import {apiLimiter, ApiLimiterEntry} from "core-components";
import RedisLimiterStorage from "../configs/redis.config";

const electionInfoRouter = Router();

const limitStorage = new RedisLimiterStorage();
const API_LIMITER_RULES: ApiLimiterEntry = {
    "/all": {
        "GET": {
            time: 20,
            limit: 200
        }
    },
    "/detail": {
        "GET": {
            time: 20,
            limit: 200
        }
    },
}

electionInfoRouter.use(apiLimiter(API_LIMITER_RULES, limitStorage));

/**
 * Get all the election data
 */
electionInfoRouter.get("/all", getAllElectionInfo);

/**
 * Return a specific election data
 */
electionInfoRouter.get("/detail/:electionId", readElectionInfo);

/**
 * Create a new election data
 */
electionInfoRouter.post("/", createElectionInfo);

/**
 * Delete a specific election data
 */
electionInfoRouter.delete("/", deleteElectionInfo);

/**
 * Check if a specific election data exists
 */
// electionRouter.get("/data/:electionId/exists", electionInfoExists);

export default electionInfoRouter;