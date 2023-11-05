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
            limit: 10
        }
    },
    "/detail": {
        "GET": {
            time: 20,
            limit: 40
        }
    },
    "/vote": {
        "PUT": {
            time: 20,
            limit: 5
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

electionRouter.use(apiLimiter(API_LIMITER_RULES, limitStorage));

electionRouter.get("/all", authenticationHandler, getAllElection);

electionRouter.get("/detail/:electionId", authenticationHandler, readElection);

electionRouter.post("/", authenticationHandler, createElection);

electionRouter.put("/vote/:electionId", authenticationHandler, castVote);

electionRouter.delete("/", authenticationHandler, deleteElection);

export default electionRouter;