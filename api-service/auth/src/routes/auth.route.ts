import { Router } from "express";
import {login, refreshToken} from "../controllers/auth";
import {apiLimiter, validationHandler} from "core-components";
import { ApiLimiterEntry } from "core-components";
import RedisLimiterStorage from "../configs/redis.config";
import {body} from "express-validator";


const API_LIMITER_RULES: ApiLimiterEntry = {
    "/login": {
        "POST": {
            time: 20,
            limit: 100
        }
    },
    "/refresh": {
        "POST": {
            time: 20,
            limit: 100
        }
    }
}

const limitStorage = new RedisLimiterStorage();

const authRouter = Router();

authRouter.use(apiLimiter(API_LIMITER_RULES, limitStorage));

/**
 * @openapi
 *
 * paths:
 *   /auth/login:
 *      post:
 *          summary: Login
 *          description: Login in the application reiceving a valid access token and a refresh token.
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              email:
 *                                  type: string
 *                                  description: The email of the user.
 *                              password:
 *                                  type: string
 *                                  description: The password of the user.
 *
 *          responses:
 *              '200':
 *                  description: The request was handled successfully.
 *              '400':
 *                  description: The request was malformed. Probably some data is missing.
 *              '401':
 *                  description: The request was unauthorized. Login data is invalid.
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 */
authRouter.post(
    "/login",
    validationHandler([
        body("email").exists().isEmail(),
        body("password").exists().isString()
    ]),
    login
);

/**
 * @openapi
 *
 * paths:
 *   /auth/refresh:
 *      post:
 *          summary: Refresh tokens
 *          description: Return a new refresh and access token pair.
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              email:
 *                                  type: string
 *                                  description: The email of the user.
 *                              refreshToken:
 *                                  type: string
 *                                  description: The refresh token of the user.
 *          responses:
 *              '200':
 *                  description: The request was handled successfully.
 *              '400':
 *                  description: The request was malformed. Probably some data is missing.
 *              '404':
 *                  description: The user specified by the provided email doesn't exists.
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 */
authRouter.post(
    "/refresh",
    validationHandler([
        body("email").exists().isEmail(),
        body("refreshToken").exists().isJWT()
    ]),
    refreshToken
);

export default authRouter;
