import { Router } from "express";
import {generateCodeFor, invalidate, isValid, verifyCodeOwner} from "../controllers/codes";
import RedisLimiterStorage from "../configs/redis.config";
import {apiLimiter, ApiLimiterEntry} from "core-components";
import {authenticationHandler} from "../middleware/authentication.middleware";

const codesRoute =  Router();

const limitStorage = new RedisLimiterStorage();
const API_LIMITER_RULES: ApiLimiterEntry = {
    "/generate": {
        "POST": {
            time: 20,
            limit: 5
        }
    },

    "/is-valid": {
        "POST": {
            time: 20,
            limit: 20,
        }
    },

    "/invalidate": {
        "PATCH": {
            time: 20,
            limit: 5
        }
    },

    "/verify-owner": {
        "POST": {
            time: 20,
            limit: 20
        }
    }
}
codesRoute.use(apiLimiter(API_LIMITER_RULES, limitStorage));


codesRoute.post("/generate", authenticationHandler, generateCodeFor);


codesRoute.post("/is-valid", authenticationHandler, isValid);


codesRoute.patch("/invalidate", authenticationHandler, invalidate);


codesRoute.post("/verify-owner", authenticationHandler, verifyCodeOwner);

export default codesRoute;