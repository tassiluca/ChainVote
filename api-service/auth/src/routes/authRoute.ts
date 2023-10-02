import { Router } from "express";
import { login, refreshToken } from "../controllers/auth";
import { apiLimiter } from "core-components";
import { ApiLimiterEntry } from "core-components";
import RedisLimiterStorage from "../configs/redis.config";


const API_LIMITER_RULES: ApiLimiterEntry = {
    "/login": {
        "POST": {
            time: 20,
            limit: 5
        }
    },
    "/refresh": {
        "POST": {
            time: 20,
            limit: 5
        }
    }
}

const limitStorage = new RedisLimiterStorage();

const authRouter = Router();

authRouter.use(apiLimiter(API_LIMITER_RULES, limitStorage));

authRouter.post("/login", login);

authRouter.post("/refresh", refreshToken);

export default authRouter;